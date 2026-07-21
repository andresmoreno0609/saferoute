package com.saferoute.common.dto.dashboard;

import lombok.Builder;

@Builder
public record DashboardStatsResponse(
    long totalUsers,
    long activeRoutes,
    long pendingDrivers,
    long expiringDocuments
) {}
