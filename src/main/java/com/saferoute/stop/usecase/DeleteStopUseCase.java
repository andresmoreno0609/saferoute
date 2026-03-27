package com.saferoute.stop.usecase;

import com.saferoute.common.service.StopService;
import com.saferoute.common.usecase.UseCaseAdvance;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class DeleteStopUseCase extends UseCaseAdvance<UUID, Void> {
    private final StopService service;

    @Override
    protected Void core(UUID id) {
        log.debug("Deleting stop: {}", id);
        service.delete(id);
        return null;
    }
}
