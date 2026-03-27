package com.saferoute.guardian.usecase;

import com.saferoute.common.dto.guardian.GuardianResponse;
import com.saferoute.common.service.GuardianService;
import com.saferoute.common.usecase.UseCaseAdvance;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Use case for getting all guardians.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class GetAllGuardiansUseCase extends UseCaseAdvance<Void, List<GuardianResponse>> {

    private final GuardianService guardianService;

    @Override
    protected List<GuardianResponse> core(Void request) {
        log.debug("Fetching all guardians");
        return guardianService.findAll();
    }
}
