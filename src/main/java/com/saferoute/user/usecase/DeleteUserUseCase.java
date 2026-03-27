package com.saferoute.user.usecase;

import com.saferoute.common.entity.UserEntity.UserStatus;
import com.saferoute.common.repository.UserRepository;
import com.saferoute.common.usecase.UseCaseAdvance;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Use case for deleting (soft delete) a user.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DeleteUserUseCase extends UseCaseAdvance<UUID, Void> {

    private final UserRepository userRepository;

    @Override
    protected void preConditions(UUID id) {
        if (!userRepository.existsById(id)) {
            throw new EntityNotFoundException("User not found with id: " + id);
        }
    }

    @Override
    protected Void core(UUID id) {
        log.debug("Soft deleting user: {}", id);
        
        userRepository.findById(id).ifPresent(user -> {
            user.setStatus(UserStatus.DELETED);
            userRepository.save(user);
        });
        
        log.info("User soft deleted successfully: {}", id);
        return null;
    }
}
