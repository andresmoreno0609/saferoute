package com.saferoute.guardian.usecase;

import com.saferoute.common.entity.GuardianEntity;
import com.saferoute.common.entity.UserEntity;
import com.saferoute.common.repository.GuardianRepository;
import com.saferoute.common.repository.UserRepository;
import com.saferoute.common.usecase.UseCaseAdvance;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Verifica que el guardian pertenezca al usuario autenticado.
 * Admin tiene acceso total.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class VerifyGuardianOwnershipUseCase extends UseCaseAdvance<UUID, GuardianEntity> {

    private final GuardianRepository guardianRepository;
    private final UserRepository userRepository;

    @Override
    protected GuardianEntity core(UUID guardianId) {
        // 1. Obtener usuario actual del token
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userIdStr = auth.getName();
        UUID userId = UUID.fromString(userIdStr);

        // 2. Buscar usuario
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalStateException("Usuario no encontrado"));

        // 3. Si es ADMIN, permitir todo
        if (user.getRoles().stream().anyMatch(r -> r == UserEntity.UserRole.ADMIN)) {
            return guardianRepository.findById(guardianId)
                    .orElseThrow(() -> new IllegalArgumentException("Guardian no encontrado: " + guardianId));
        }

        // 4. Verificar ownership
        GuardianEntity guardian = guardianRepository.findById(guardianId)
                .orElseThrow(() -> new IllegalArgumentException("Guardian no encontrado: " + guardianId));

        if (!guardian.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("No tienes acceso a este guardian");
        }

        log.info("Guardian ownership verified for guardianId: {}", guardianId);
        return guardian;
    }
}