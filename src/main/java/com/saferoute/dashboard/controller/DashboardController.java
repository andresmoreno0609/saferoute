package com.saferoute.dashboard.controller;

import com.saferoute.common.dto.dashboard.DashboardStatsResponse;
import com.saferoute.dashboard.adapter.DashboardAdapter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Dashboard REST Controller.
 * Expone métricas agregadas para el panel de administración.
 * Acceso: ADMIN.
 */
@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "12. Dashboard", description = "Métricas agregadas para el panel de administración")
public class DashboardController {

    private final DashboardAdapter adapter;

    /**
     * GET /api/v1/dashboard/stats
     * Retorna las métricas agregadas del sistema para el dashboard.
     */
    @Operation(summary = "Obtener estadísticas del dashboard", description = "Retorna totales de usuarios, rutas activas, conductores pendientes y documentos por vencer. Solo ADMIN.")
    @GetMapping("/stats")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DashboardStatsResponse> getStats() {
        log.info("GET /api/v1/dashboard/stats");
        return ResponseEntity.ok(adapter.getStats());
    }
}
