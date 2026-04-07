package com.saferoute.common.service;

import com.saferoute.common.dto.vehicledocument.VehicleDocumentRequest;
import com.saferoute.common.dto.vehicledocument.VehicleDocumentResponse;
import com.saferoute.common.entity.VehicleDocumentEntity;
import com.saferoute.common.entity.VehicleEntity;
import com.saferoute.common.repository.VehicleDocumentRepository;
import com.saferoute.common.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Vehicle Document service implementing CRUD with auto-inactivation logic.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class VehicleDocumentService {

    private final VehicleDocumentRepository vehicleDocumentRepository;
    private final VehicleRepository vehicleRepository;

    public List<VehicleDocumentResponse> findByVehicle(UUID vehicleId) {
        return vehicleDocumentRepository.findByVehicleId(vehicleId).stream()
                .map(this::toResponse)
                .toList();
    }

    public List<VehicleDocumentResponse> findActiveByVehicle(UUID vehicleId) {
        return vehicleDocumentRepository.findByVehicleIdAndIsActiveTrue(vehicleId).stream()
                .map(this::toResponse)
                .toList();
    }

    public VehicleDocumentResponse findById(UUID id) {
        VehicleDocumentEntity entity = vehicleDocumentRepository.findById(id)
                .orElseThrow(() -> new VehicleDocumentNotFoundException("Vehicle document not found with id: " + id));
        return toResponse(entity);
    }

    @Transactional
    public VehicleDocumentResponse create(UUID vehicleId, VehicleDocumentRequest request) {
        VehicleEntity vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new VehicleService.VehicleNotFoundException("Vehicle not found with id: " + vehicleId));

        // Check if there's an active document of the same type - if so, inactivate it
        vehicleDocumentRepository.findActiveByVehicleAndType(vehicleId, request.documentType())
                .ifPresent(activeDoc -> {
                    activeDoc.setIsActive(false);
                    vehicleDocumentRepository.save(activeDoc);
                    log.info("Inactivated previous document of type {} for vehicle {}", request.documentType(), vehicleId);
                });

        VehicleDocumentEntity entity = VehicleDocumentEntity.builder()
                .vehicle(vehicle)
                .documentType(request.documentType())
                .fileUrl(request.fileUrl())
                .startDate(request.startDate())
                .endDate(request.endDate())
                .isActive(true)
                .isVerified(false) // Por defecto no verificado
                .build();

        VehicleDocumentEntity saved = vehicleDocumentRepository.save(entity);
        log.info("Vehicle document created: {}", saved.getId());
        return toResponse(saved);
    }

    @Transactional
    public VehicleDocumentResponse update(UUID documentId, VehicleDocumentRequest request) {
        VehicleDocumentEntity entity = vehicleDocumentRepository.findById(documentId)
                .orElseThrow(() -> new VehicleDocumentNotFoundException("Vehicle document not found with id: " + documentId));

        // If changing document type, inactivate old one and create new
        if (request.documentType() != null && !request.documentType().equals(entity.getDocumentType())) {
            // Inactivate current
            entity.setIsActive(false);
            vehicleDocumentRepository.save(entity);

            // Check if new type already has active document
            vehicleDocumentRepository.findActiveByVehicleAndType(entity.getVehicle().getId(), request.documentType())
                    .ifPresent(activeDoc -> {
                        activeDoc.setIsActive(false);
                        vehicleDocumentRepository.save(activeDoc);
                    });

            // Create new document
            VehicleDocumentEntity newDoc = VehicleDocumentEntity.builder()
                    .vehicle(entity.getVehicle())
                    .documentType(request.documentType())
                    .fileUrl(request.fileUrl())
                    .startDate(request.startDate())
                    .endDate(request.endDate())
                    .isActive(true)
                    .isVerified(false)
                    .build();

            VehicleDocumentEntity saved = vehicleDocumentRepository.save(newDoc);
            log.info("Vehicle document updated (replaced): {}", saved.getId());
            return toResponse(saved);
        }

        // Simple update
        if (request.fileUrl() != null) {
            entity.setFileUrl(request.fileUrl());
        }
        if (request.startDate() != null) {
            entity.setStartDate(request.startDate());
        }
        if (request.endDate() != null) {
            entity.setEndDate(request.endDate());
        }

        VehicleDocumentEntity saved = vehicleDocumentRepository.save(entity);
        log.info("Vehicle document updated: {}", documentId);
        return toResponse(saved);
    }

    @Transactional
    public void delete(UUID id) {
        VehicleDocumentEntity entity = vehicleDocumentRepository.findById(id)
                .orElseThrow(() -> new VehicleDocumentNotFoundException("Vehicle document not found with id: " + id));
        
        // Soft delete - just mark as inactive
        entity.setIsActive(false);
        vehicleDocumentRepository.save(entity);
        log.info("Vehicle document soft deleted: {}", id);
    }

    /**
     * Verificar documento (Admin)
     */
    @Transactional
    public VehicleDocumentResponse verifyDocument(UUID documentId, UUID verifiedBy) {
        VehicleDocumentEntity entity = vehicleDocumentRepository.findById(documentId)
                .orElseThrow(() -> new VehicleDocumentNotFoundException("Vehicle document not found with id: " + documentId));

        entity.setIsVerified(true);
        entity.setVerifiedAt(LocalDateTime.now());
        entity.setVerifiedBy(verifiedBy);
        entity.setRejectionReason(null); // Limpiar motivo de rechazo si existía

        VehicleDocumentEntity saved = vehicleDocumentRepository.save(entity);
        log.info("Vehicle document verified: {} by user: {}", documentId, verifiedBy);
        return toResponse(saved);
    }

    /**
     * Rechazar documento (Admin)
     */
    @Transactional
    public VehicleDocumentResponse rejectDocument(UUID documentId, UUID verifiedBy, String reason) {
        VehicleDocumentEntity entity = vehicleDocumentRepository.findById(documentId)
                .orElseThrow(() -> new VehicleDocumentNotFoundException("Vehicle document not found with id: " + documentId));

        entity.setIsVerified(false);
        entity.setVerifiedAt(LocalDateTime.now());
        entity.setVerifiedBy(verifiedBy);
        entity.setRejectionReason(reason);

        VehicleDocumentEntity saved = vehicleDocumentRepository.save(entity);
        log.info("Vehicle document rejected: {} by user: {}, reason: {}", documentId, verifiedBy, reason);
        return toResponse(saved);
    }

    /**
     * Check if all required documents are active, valid (not expired) AND verified.
     */
    public boolean hasAllRequiredDocuments(UUID vehicleId) {
        List<VehicleDocumentEntity.VehicleDocumentType> requiredTypes = List.of(
                VehicleDocumentEntity.VehicleDocumentType.SOAP,
                VehicleDocumentEntity.VehicleDocumentType.SEGURO,
                VehicleDocumentEntity.VehicleDocumentType.TECNOMECANICA,
                VehicleDocumentEntity.VehicleDocumentType.TARJETA_PROPIEDAD
        );

        LocalDate today = LocalDate.now();

        for (VehicleDocumentEntity.VehicleDocumentType type : requiredTypes) {
            var activeDoc = vehicleDocumentRepository.findActiveByVehicleAndType(vehicleId, type);
            if (activeDoc.isEmpty()) {
                return false;
            }
            // Check expiration (null endDate means no expiration)
            LocalDate endDate = activeDoc.get().getEndDate();
            if (endDate != null && endDate.isBefore(today)) {
                return false;
            }
            // Check verification
            if (!activeDoc.get().getIsVerified()) {
                return false;
            }
        }
        return true;
    }

    private VehicleDocumentResponse toResponse(VehicleDocumentEntity entity) {
        return new VehicleDocumentResponse(
                entity.getId(),
                entity.getVehicle().getId(),
                entity.getDocumentType(),
                entity.getFileUrl(),
                entity.getStartDate(),
                entity.getEndDate(),
                entity.getIsActive(),
                entity.getIsVerified(),
                entity.getVerifiedAt(),
                entity.getVerifiedBy(),
                entity.getRejectionReason()
        );
    }

    // Custom exceptions
    public static class VehicleDocumentNotFoundException extends RuntimeException {
        public VehicleDocumentNotFoundException(String message) {
            super(message);
        }
    }
}