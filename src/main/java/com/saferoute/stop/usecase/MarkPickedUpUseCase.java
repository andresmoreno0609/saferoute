package com.saferoute.stop.usecase;

import com.saferoute.common.dto.stop.StopResponse;
import com.saferoute.common.service.StopService;
import com.saferoute.common.usecase.UseCaseAdvance;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class MarkPickedUpUseCase extends UseCaseAdvance<UUID, StopResponse> {
    private final StopService service;

    @Override
    protected StopResponse core(UUID id) {
        log.debug("Marking stop as picked up: {}", id);
        return service.markPickedUp(id);
    }
}
