package com.saferoute.driverdocument.adapter;

import com.saferoute.common.dto.driverdocument.DriverDocumentRequest;
import com.saferoute.common.dto.driverdocument.DriverDocumentResponse;
import com.saferoute.common.service.DriverDocumentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

/**
 * DriverDocument Adapter - orchestrates use cases for driver document management.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DriverDocumentAdapter {

    private final DriverDocumentService driverDocumentService;

    public List<DriverDocumentResponse> getByDriver(UUID driverId) {
        return driverDocumentService.findByDriver(driverId);
    }

    public List<DriverDocumentResponse> getActiveByDriver(UUID driverId) {
        return driverDocumentService.findActiveByDriver(driverId);
    }

    public DriverDocumentResponse getById(UUID id) {
        return driverDocumentService.findById(id);
    }

    public DriverDocumentResponse create(UUID driverId, DriverDocumentRequest request) {
        return driverDocumentService.create(driverId, request);
    }

    public DriverDocumentResponse update(UUID documentId, DriverDocumentRequest request) {
        return driverDocumentService.update(documentId, request);
    }

    public void delete(UUID id) {
        driverDocumentService.delete(id);
    }

    public DriverDocumentResponse verify(UUID documentId, UUID verifiedBy) {
        return driverDocumentService.verifyDocument(documentId, verifiedBy);
    }

    public DriverDocumentResponse reject(UUID documentId, UUID verifiedBy, String reason) {
        return driverDocumentService.rejectDocument(documentId, verifiedBy, reason);
    }
}