package com.saferoute.vehicledocument.adapter;

import com.saferoute.common.dto.vehicledocument.VehicleDocumentRequest;
import com.saferoute.common.dto.vehicledocument.VehicleDocumentResponse;
import com.saferoute.common.service.VehicleDocumentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

/**
 * VehicleDocument Adapter - orchestrates use cases for vehicle document management.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class VehicleDocumentAdapter {

    private final VehicleDocumentService vehicleDocumentService;

    public List<VehicleDocumentResponse> getByVehicle(UUID vehicleId) {
        return vehicleDocumentService.findByVehicle(vehicleId);
    }

    public List<VehicleDocumentResponse> getActiveByVehicle(UUID vehicleId) {
        return vehicleDocumentService.findActiveByVehicle(vehicleId);
    }

    public VehicleDocumentResponse getById(UUID id) {
        return vehicleDocumentService.findById(id);
    }

    public VehicleDocumentResponse create(UUID vehicleId, VehicleDocumentRequest request) {
        return vehicleDocumentService.create(vehicleId, request);
    }

    public VehicleDocumentResponse update(UUID documentId, VehicleDocumentRequest request) {
        return vehicleDocumentService.update(documentId, request);
    }

    public void delete(UUID id) {
        vehicleDocumentService.delete(id);
    }

    public VehicleDocumentResponse verify(UUID documentId, UUID verifiedBy) {
        return vehicleDocumentService.verifyDocument(documentId, verifiedBy);
    }

    public VehicleDocumentResponse reject(UUID documentId, UUID verifiedBy, String reason) {
        return vehicleDocumentService.rejectDocument(documentId, verifiedBy, reason);
    }
}