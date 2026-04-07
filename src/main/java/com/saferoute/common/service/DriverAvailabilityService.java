package com.saferoute.common.service;

import com.saferoute.common.entity.DriverEntity;
import com.saferoute.common.repository.DriverDocumentRepository;
import com.saferoute.common.repository.DriverRepository;
import com.saferoute.common.repository.VehicleDocumentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Service to determine if a driver can work based on their documents.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class DriverAvailabilityService {

    private final DriverRepository driverRepository;
    private final DriverDocumentRepository driverDocumentRepository;
    private final VehicleDocumentRepository vehicleDocumentRepository;

    /**
     * Check if a driver is available to work.
     * A driver is available if:
     * 1. Has a vehicle assigned
     * 2. Has a valid license (active and not expired)
     * 3. Vehicle has all required documents (SOAP, SEGURO, TECNOMECANICA, TARJETA_PROPIEDAD) active and not expired
     */
    public AvailabilityResult checkAvailability(UUID driverId) {
        List<String> documentsRequired = new ArrayList<>();
        List<String> documentsMissing = new ArrayList<>();
        List<String> documentsExpired = new ArrayList<>();
        
        // Get driver
        DriverEntity driver = driverRepository.findById(driverId).orElse(null);
        if (driver == null) {
            return AvailabilityResult.notAvailable("Driver not found", documentsRequired, documentsMissing, documentsExpired);
        }

        // Check vehicle assignment
        if (driver.getVehicle() == null) {
            return AvailabilityResult.notAvailable("Driver has no vehicle assigned", documentsRequired, documentsMissing, documentsExpired);
        }

        UUID vehicleId = driver.getVehicle().getId();

        // Required driver documents
        documentsRequired.add("LICENCIA");
        boolean hasValidLicense = checkValidLicense(driverId);
        if (!hasValidLicense) {
            documentsMissing.add("LICENCIA");
        }

        // Required vehicle documents
        List<VehicleDocumentService.VehicleDocumentType> requiredVehicleDocs = List.of(
                VehicleDocumentService.VehicleDocumentType.SOAP,
                VehicleDocumentService.VehicleDocumentType.SEGURO,
                VehicleDocumentService.VehicleDocumentType.TECNOMECANICA,
                VehicleDocumentService.VehicleDocumentType.TARJETA_PROPIEDAD
        );

        for (VehicleDocumentService.VehicleDocumentType docType : requiredVehicleDocs) {
            String docName = docType.name();
            documentsRequired.add(docName);
            
            var doc = vehicleDocumentRepository.findActiveByVehicleAndType(vehicleId, 
                    com.saferoute.common.entity.VehicleDocumentEntity.VehicleDocumentType.valueOf(docName));
            
            if (doc.isEmpty()) {
                documentsMissing.add(docName);
            } else {
                LocalDate endDate = doc.get().getEndDate();
                if (endDate != null && endDate.isBefore(LocalDate.now())) {
                    documentsExpired.add(docName);
                }
            }
        }

        boolean isAvailable = hasValidLicense && documentsMissing.isEmpty() && documentsExpired.isEmpty();

        if (isAvailable) {
            return AvailabilityResult.available(documentsRequired, documentsMissing, documentsExpired);
        } else {
            String reason = !documentsMissing.isEmpty() ? "Documentos faltantes" : "Documentos vencidos";
            return AvailabilityResult.notAvailable(reason, documentsRequired, documentsMissing, documentsExpired);
        }
    }

    private boolean checkValidLicense(UUID driverId) {
        var activeLicense = driverDocumentRepository.findActiveByDriverAndType(
                driverId, com.saferoute.common.entity.DriverDocumentEntity.DriverDocumentType.LICENCIA);
        
        if (activeLicense.isEmpty()) {
            return false;
        }
        
        LocalDate endDate = activeLicense.get().getEndDate();
        // null endDate means no expiration
        return endDate == null || !endDate.isBefore(LocalDate.now());
    }

    public record AvailabilityResult(
            boolean available,
            String reason,
            List<String> documentsRequired,
            List<String> documentsMissing,
            List<String> documentsExpired
    ) {
        public static AvailabilityResult available(List<String> documentsRequired, 
                List<String> documentsMissing, List<String> documentsExpired) {
            return new AvailabilityResult(true, null, documentsRequired, documentsMissing, documentsExpired);
        }

        public static AvailabilityResult notAvailable(String reason, List<String> documentsRequired,
                List<String> documentsMissing, List<String> documentsExpired) {
            return new AvailabilityResult(false, reason, documentsRequired, documentsMissing, documentsExpired);
        }
    }
}