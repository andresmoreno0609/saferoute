package com.saferoute.common.service;

import com.saferoute.common.dto.driverdocument.DriverDocumentRequest;
import com.saferoute.common.dto.driverdocument.DriverDocumentResponse;
import com.saferoute.common.entity.DriverDocumentEntity;
import com.saferoute.common.entity.DriverEntity;
import com.saferoute.common.repository.DriverDocumentRepository;
import com.saferoute.common.repository.DriverRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Driver Document service implementing CRUD with auto-inactivation logic.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class DriverDocumentService {

    private final DriverDocumentRepository driverDocumentRepository;
    private final DriverRepository driverRepository;

    public List<DriverDocumentResponse> findByDriver(UUID driverId) {
        return driverDocumentRepository.findByDriverId(driverId).stream()
                .map(this::toResponse)
                .toList();
    }

    public List<DriverDocumentResponse> findActiveByDriver(UUID driverId) {
        return driverDocumentRepository.findByDriverIdAndIsActiveTrue(driverId).stream()
                .map(this::toResponse)
                .toList();
    }

    public DriverDocumentResponse findById(UUID id) {
        DriverDocumentEntity entity = driverDocumentRepository.findById(id)
                .orElseThrow(() -> new DriverDocumentNotFoundException("Driver document not found with id: " + id));
        return toResponse(entity);
    }

    @Transactional
    public DriverDocumentResponse create(UUID driverId, DriverDocumentRequest request) {
        DriverEntity driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new DriverNotFoundException("Driver not found with id: " + driverId));

        // Check if there's an active document of the same type - if so, inactivate it
        driverDocumentRepository.findActiveByDriverAndType(driverId, request.documentType())
                .ifPresent(activeDoc -> {
                    activeDoc.setIsActive(false);
                    driverDocumentRepository.save(activeDoc);
                    log.info("Inactivated previous document of type {} for driver {}", request.documentType(), driverId);
                });

        DriverDocumentEntity entity = DriverDocumentEntity.builder()
                .driver(driver)
                .documentType(request.documentType())
                .documentNumber(request.documentNumber())
                .licenseCategory(request.licenseCategory())
                .fileUrl(request.fileUrl())
                .startDate(request.startDate())
                .endDate(request.endDate())
                .isActive(true)
                .isVerified(false) // Por defecto no verificado
                .build();

        DriverDocumentEntity saved = driverDocumentRepository.save(entity);
        log.info("Driver document created: {}", saved.getId());
        return toResponse(saved);
    }

    @Transactional
    public DriverDocumentResponse update(UUID documentId, DriverDocumentRequest request) {
        DriverDocumentEntity entity = driverDocumentRepository.findById(documentId)
                .orElseThrow(() -> new DriverDocumentNotFoundException("Driver document not found with id: " + documentId));

        // If changing document type, inactivate old one and create new
        if (request.documentType() != null && !request.documentType().equals(entity.getDocumentType())) {
            // Inactivate current
            entity.setIsActive(false);
            driverDocumentRepository.save(entity);

            // Check if new type already has active document
            driverDocumentRepository.findActiveByDriverAndType(entity.getDriver().getId(), request.documentType())
                    .ifPresent(activeDoc -> {
                        activeDoc.setIsActive(false);
                        driverDocumentRepository.save(activeDoc);
                    });

            // Create new document
            DriverDocumentEntity newDoc = DriverDocumentEntity.builder()
                    .driver(entity.getDriver())
                    .documentType(request.documentType())
                    .documentNumber(request.documentNumber())
                    .licenseCategory(request.licenseCategory())
                    .fileUrl(request.fileUrl())
                    .startDate(request.startDate())
                    .endDate(request.endDate())
                    .isActive(true)
                    .isVerified(false)
                    .build();

            DriverDocumentEntity saved = driverDocumentRepository.save(newDoc);
            log.info("Driver document updated (replaced): {}", saved.getId());
            return toResponse(saved);
        }

        // Simple update
        if (request.documentNumber() != null) {
            entity.setDocumentNumber(request.documentNumber());
        }
        if (request.licenseCategory() != null) {
            entity.setLicenseCategory(request.licenseCategory());
        }
        if (request.fileUrl() != null) {
            entity.setFileUrl(request.fileUrl());
        }
        if (request.startDate() != null) {
            entity.setStartDate(request.startDate());
        }
        if (request.endDate() != null) {
            entity.setEndDate(request.endDate());
        }

        DriverDocumentEntity saved = driverDocumentRepository.save(entity);
        log.info("Driver document updated: {}", documentId);
        return toResponse(saved);
    }

    @Transactional
    public void delete(UUID id) {
        DriverDocumentEntity entity = driverDocumentRepository.findById(id)
                .orElseThrow(() -> new DriverDocumentNotFoundException("Driver document not found with id: " + id));
        
        // Soft delete - just mark as inactive
        entity.setIsActive(false);
        driverDocumentRepository.save(entity);
        log.info("Driver document soft deleted: {}", id);
    }

    /**
     * Verificar documento del conductor (Admin)
     */
    @Transactional
    public DriverDocumentResponse verifyDocument(UUID documentId, UUID verifiedBy) {
        DriverDocumentEntity entity = driverDocumentRepository.findById(documentId)
                .orElseThrow(() -> new DriverDocumentNotFoundException("Driver document not found with id: " + documentId));

        entity.setIsVerified(true);
        entity.setVerifiedAt(LocalDateTime.now());
        entity.setVerifiedBy(verifiedBy);
        entity.setRejectionReason(null); // Limpiar motivo de rechazo si existía

        DriverDocumentEntity saved = driverDocumentRepository.save(entity);
        log.info("Driver document verified: {} by user: {}", documentId, verifiedBy);
        return toResponse(saved);
    }

    /**
     * Rechazar documento del conductor (Admin)
     */
    @Transactional
    public DriverDocumentResponse rejectDocument(UUID documentId, UUID verifiedBy, String reason) {
        DriverDocumentEntity entity = driverDocumentRepository.findById(documentId)
                .orElseThrow(() -> new DriverDocumentNotFoundException("Driver document not found with id: " + documentId));

        entity.setIsVerified(false);
        entity.setVerifiedAt(LocalDateTime.now());
        entity.setVerifiedBy(verifiedBy);
        entity.setRejectionReason(reason);

        DriverDocumentEntity saved = driverDocumentRepository.save(entity);
        log.info("Driver document rejected: {} by user: {}, reason: {}", documentId, verifiedBy, reason);
        return toResponse(saved);
    }

    /**
     * Check if driver has a valid license (active, not expired AND verified).
     */
    public boolean hasValidLicense(UUID driverId) {
        var activeLicense = driverDocumentRepository.findActiveByDriverAndType(
                driverId, DriverDocumentEntity.DriverDocumentType.LICENCIA);
        
        if (activeLicense.isEmpty()) {
            return false;
        }
        
        // Check verification
        if (!activeLicense.get().getIsVerified()) {
            return false;
        }
        
        LocalDate endDate = activeLicense.get().getEndDate();
        // null endDate means no expiration
        return endDate == null || !endDate.isBefore(LocalDate.now());
    }

    private DriverDocumentResponse toResponse(DriverDocumentEntity entity) {
        return new DriverDocumentResponse(
                entity.getId(),
                entity.getDriver().getId(),
                entity.getDocumentType(),
                entity.getDocumentNumber(),
                entity.getLicenseCategory(),
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
    public static class DriverDocumentNotFoundException extends RuntimeException {
        public DriverDocumentNotFoundException(String message) {
            super(message);
        }
    }

    public static class DriverNotFoundException extends RuntimeException {
        public DriverNotFoundException(String message) {
            super(message);
        }
    }
}