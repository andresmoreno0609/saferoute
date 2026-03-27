package com.saferoute.user.usecase;

import com.saferoute.common.dto.user.UserResponse;
import com.saferoute.common.service.UserService;
import com.saferoute.common.usecase.UseCaseAdvance;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Use case for getting all users.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class GetAllUsersUseCase extends UseCaseAdvance<Void, List<UserResponse>> {

    private final UserService userService;

    @Override
    protected List<UserResponse> core(Void request) {
        log.debug("Fetching all users");
        return userService.findAll();
    }
}
