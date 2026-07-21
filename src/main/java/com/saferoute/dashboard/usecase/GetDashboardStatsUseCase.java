package com.saferoute.dashboard.usecase;

import com.saferoute.common.dto.dashboard.DashboardStatsResponse;
import com.saferoute.common.entity.RouteEntity.RouteStatus;
import com.saferoute.common.repository.DriverDocumentRepository;
import com.saferoute.common.repository.DriverRepository;
import com.saferoute.common.repository.RouteRepository;
import com.saferoute.common.repository.UserRepository;
import com.saferoute.common.repository.VehicleDocumentRepository;
import com.saferoute.common.usecase.UseCaseAdvance;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
@Slf4j
public class GetDashboardStatsUseCase extends UseCaseAdvance<Void, DashboardStatsResponse> {

    private static final int EXPIRING_WINDOW_DAYS = 30;

    private final UserRepository userRepository;
    private final RouteRepository routeRepository;
    private final DriverRepository driverRepository;
    private final DriverDocumentRepository driverDocumentRepository;
    private final VehicleDocumentRepository vehicleDocumentRepository;

    @Override
    protected DashboardStatsResponse core(Void unused) {
        log.debug("Aggregating dashboard stats");

        var today = LocalDate.now();
        var horizon = today.plusDays(EXPIRING_WINDOW_DAYS);

        var totalUsers = userRepository.count();
        var activeRoutes = routeRepository.countByStatus(RouteStatus.IN_PROGRESS);
        var pendingDrivers = driverRepository.countByInfoValidate(false);
        var expiringDocuments =
            driverDocumentRepository.countByEndDateBetween(today, horizon)
                + vehicleDocumentRepository.countByEndDateBetween(today, horizon);

        return DashboardStatsResponse.builder()
            .totalUsers(totalUsers)
            .activeRoutes(activeRoutes)
            .pendingDrivers(pendingDrivers)
            .expiringDocuments(expiringDocuments)
            .build();
    }
}
