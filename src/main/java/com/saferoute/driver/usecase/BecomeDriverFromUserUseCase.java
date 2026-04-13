package com.saferoute.driver.usecase;

import com.saferoute.common.dto.driver.DriverRequest;
import com.saferoute.common.dto.driver.DriverResponse;
import com.saferoute.common.entity.UserEntity.UserRole;
import com.saferoute.common.repository.DriverRepository;
import com.saferoute.common.service.DriverService;
import com.saferoute.common.service.UserService;
import com.saferoute.common.usecase.UseCaseAdvance;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Use case for a user to become a driver.
 * This endpoint allows an existing user to add the DRIVER role and create a driver profile.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class BecomeDriverFromUserUseCase extends UseCaseAdvance<BecomeDriverFromUserRequest, DriverResponse> {

    private final UserService userService;
    private final DriverService driverService;
    private final DriverRepository driverRepository;

    @Override
    protected void preConditions(BecomeDriverFromUserRequest request) {
        // 1. Validate user exists
        if (!userService.existsById(request.userId())) {
            throw new EntityNotFoundException("User not found with id: " + request.userId());
        }

        // 2. Validate user does NOT have DRIVER role
        if (userService.hasRole(request.userId(), UserRole.DRIVER)) {
            throw new IllegalStateException("User already has DRIVER role");
        }

        // 3. Validate user does NOT already have a driver profile
        if (driverRepository.existsByUserId(request.userId())) {
            throw new IllegalStateException("User already has a driver profile");
        }
    }

    @Override
    @Transactional
    protected DriverResponse core(BecomeDriverFromUserRequest request) {
        log.debug("User {} becoming a driver", request.userId());

        // Step 1: Add DRIVER role to user
        userService.addRole(request.userId(), UserRole.DRIVER);
        log.info("Added DRIVER role to user: {}", request.userId());

        // Step 2: Create driver profile with userId
        DriverRequest driverRequest = new DriverRequest(
                request.name(),
                request.phone(),
                request.documentNumber(),
                request.birthDate(),
                request.address(),
                request.licenseNumber(),
                request.licenseCategory(),
                request.licenseExpirationDate(),
                request.emergencyContact(),
                request.emergencyPhone(),
                request.yearsExperience(),
                request.photoUrl(),
                request.bankName(),
                request.bankAccount(),
                request.vehicleId(),
                request.userId()  // Link to user
        );

        DriverResponse driver = driverService.create(driverRequest);
        log.info("Driver profile created for user: {}", request.userId());

        return driver;
    }
}