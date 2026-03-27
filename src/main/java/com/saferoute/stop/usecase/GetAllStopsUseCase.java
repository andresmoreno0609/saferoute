package com.saferoute.stop.usecase;

import com.saferoute.common.dto.stop.StopResponse;
import com.saferoute.common.service.StopService;
import com.saferoute.common.usecase.UseCaseAdvance;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class GetAllStopsUseCase extends UseCaseAdvance<Void, List<StopResponse>> {
    private final StopService service;

    @Override
    protected List<StopResponse> core(Void request) {
        return service.findAll();
    }
}
