package com.saferoute.common.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

/**
 * Servicio de análisis de rutas usando pgRouting.
 * Proporciona funcionalidades para calcular distancias, tiempos y optimizar rutas.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RouteAnalyticsService {

    private final JdbcTemplate jdbcTemplate;

    /**
     * Calcula la distancia total de una ruta basándose en las paradas.
     * Usa la distancia euclidiana (línea recta) como fallback.
     * 
     * @param routeId ID de la ruta
     * @return Distancia total en metros
     */
    public BigDecimal calculateTotalDistance(UUID routeId) {
        log.info("Calculando distancia total para ruta: {}", routeId);
        
        try {
            // Primero intentar con pgRouting si hay datos de vías
            Double distance = calculateWithPgRouting(routeId);
            
            if (distance == null || distance == 0) {
                // Fallback: distancia euclidiana entre paradas
                distance = calculateEuclideanDistance(routeId);
            }
            
            return BigDecimal.valueOf(distance);
            
        } catch (Exception e) {
            log.error("Error al calcular distancia: {}", e.getMessage());
            return BigDecimal.ZERO;
        }
    }

    /**
     * Calcula la distancia usando pgRouting (requiere datos de calles cargados).
     */
    private Double calculateWithPgRouting(UUID routeId) {
        try {
            String sql = """
                WITH route_points AS (
                    SELECT s.location as point
                    FROM stops s
                    WHERE s.route_id = ?
                    ORDER BY s.stop_order
                )
                SELECT 
                    ST_LengthSpheroid(
                        ST_MakeLine(point::geometry), 
                        'SPHEROID["WGS 84",6378137,298.257223563]'
                    ) as distance
                FROM route_points
                """;
            
            List<Map<String, Object>> result = jdbcTemplate.queryForList(sql, routeId.toString());
            
            if (!result.isEmpty() && result.get(0).get("distance") != null) {
                return ((Number) result.get(0).get("distance")).doubleValue();
            }
            
            return null;
            
        } catch (Exception e) {
            log.debug("pgRouting no disponible o sin datos: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Calcula la distancia euclidiana entre paradas (línea recta).
     */
    private Double calculateEuclideanDistance(UUID routeId) {
        try {
            String sql = """
                SELECT 
                    ST_Length(
                        ST_MakeLine(s.location::geometry ORDER BY s.stop_order)
                    ) as distance
                FROM stops s
                WHERE s.route_id = ?
                AND s.location IS NOT NULL
                """;
            
            Double distance = jdbcTemplate.queryForObject(sql, Double.class, routeId.toString());
            return distance != null ? distance : 0.0;
            
        } catch (Exception e) {
            log.error("Error al calcular distancia euclidiana: {}", e.getMessage());
            return 0.0;
        }
    }

    /**
     * Estima la duración de la ruta con velocidad promedio por defecto (30 km/h).
     * 
     * @param routeId ID de la ruta
     * @return Duración estimada en minutos
     */
    public Integer estimateDuration(UUID routeId) {
        return estimateDuration(routeId, 30.0);
    }

    /**
     * Estima la duración de la ruta basándose en distancia y velocidad promedio.
     * 
     * @param routeId ID de la ruta
     * @param avgSpeedKmh Velocidad promedio en km/h
     * @return Duración estimada en minutos
     */
    public Integer estimateDuration(UUID routeId, double avgSpeedKmh) {
        BigDecimal distance = calculateTotalDistance(routeId);
        
        // Convertir metros a km
        double distanceKm = distance.doubleValue() / 1000.0;
        
        // Calcular tiempo (horas * 60 = minutos)
        double durationMinutes = (distanceKm / avgSpeedKmh) * 60;
        
        // Agregar tiempo base por paradas (2 minutos por parada)
        int stopCount = getStopCount(routeId);
        durationMinutes += stopCount * 2;
        
        return (int) Math.round(durationMinutes);
    }

    /**
     * Obtiene el número de paradas de una ruta.
     */
    private int getStopCount(UUID routeId) {
        try {
            Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM stops WHERE route_id = ?",
                Integer.class,
                routeId.toString()
            );
            return count != null ? count : 0;
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * Obtiene estadísticas de una ruta.
     * 
     * @param routeId ID de la ruta
     * @return Map con estadísticas
     */
    public Map<String, Object> getRouteStatistics(UUID routeId) {
        Map<String, Object> stats = new HashMap<>();
        
        try {
            // Distancia total
            stats.put("totalDistanceMeters", calculateTotalDistance(routeId));
            
            // Duración estimada
            stats.put("estimatedDurationMinutes", estimateDuration(routeId));
            
            // Número de paradas
            stats.put("stopCount", getStopCount(routeId));
            
            // Velocidad promedio estimada (30 km/h default + tiempo en paradas)
            stats.put("estimatedAvgSpeedKmh", 30.0);
            
            // Posiciones GPS registradas
            String gpsSql = "SELECT COUNT(*) FROM gps_positions WHERE route_id = ?";
            Integer gpsCount = jdbcTemplate.queryForObject(gpsSql, Integer.class, routeId.toString());
            stats.put("gpsPositionCount", gpsCount != null ? gpsCount : 0);
            
            return stats;
            
        } catch (Exception e) {
            log.error("Error al obtener estadísticas: {}", e.getMessage());
            return Map.of("error", e.getMessage());
        }
    }

    /**
     * Calcula la distancia entre dos coordenadas usando PostGIS.
     * 
     * @param lat1 Latitud punto 1
     * @param lon1 Longitud punto 1
     * @param lat2 Latitud punto 2
     * @param lon2 Longitud punto 2
     * @return Distancia en metros
     */
    public Double calculateDistanceBetweenPoints(double lat1, double lon1, double lat2, double lon2) {
        try {
            String sql = """
                SELECT ST_Distance(
                    ST_SetSRID(ST_MakePoint(?, ?), 4326)::geography,
                    ST_SetSRID(ST_MakePoint(?, ?), 4326)::geography
                ) as distance
                """;
            
            return jdbcTemplate.queryForObject(sql, Double.class, lon1, lat1, lon2, lat2);
            
        } catch (Exception e) {
            log.error("Error al calcular distancia entre puntos: {}", e.getMessage());
            return 0.0;
        }
    }

    /**
     * Verifica si pgRouting está disponible.
     * 
     * @return true si pgRouting está instalado
     */
    public boolean isPgRoutingAvailable() {
        try {
            jdbcTemplate.queryForObject(
                "SELECT 1 FROM pg_extension WHERE extname = 'pgrouting'",
                Integer.class
            );
            return true;
        } catch (Exception e) {
            log.warn("pgRouting no disponible: {}", e.getMessage());
            return false;
        }
    }
}
