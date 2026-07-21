package com.saferoute.dashboard.adapter;

import com.saferoute.common.dto.dashboard.DashboardStatsResponse;
import com.saferoute.dashboard.usecase.GetDashboardStatsUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DashboardAdapter {

    private final GetDashboardStatsUseCase getStatsUseCase;

    public DashboardStatsResponse getStats() {
        return getStatsUseCase.execute(null);
    }
}
