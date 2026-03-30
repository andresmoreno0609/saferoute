package com.saferoute.common.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Servicio de análisis de tracking usando MobilityDB.
 * Proporciona estadísticas avanzadas de las rutas y posiciones GPS.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TrackingAnalyticsService {

    private final JdbcTemplate jdbcTemplate;

    /**
     * Obtiene la trayectoria completa de una ruta.
     * 
     * @param routeId ID de la ruta
     * @return Map con la línea de trayectoria y metadata
     */
    public Map<String, Object> getRouteTrajectory(UUID routeId) {
        log.info("Obteniendo trayectoria para ruta: {}", routeId);
        
        Map<String, Object> trajectory = new HashMap<>();
        
        try {
            // Verificar si MobilityDB está disponible
            boolean hasMobilityDB = isMobilityDBAvailable();
            trajectory.put("usesMobilityDB", hasMobilityDB);
            
            if (hasMobilityDB) {
                // Usar MobilityDB para trayectoria
                return getTrajectoryWithMobilityDB(routeId);
            } else {
                // Fallback: usar PostGIS básico
                return getTrajectoryWithPostGIS(routeId);
            }
            
        } catch (Exception e) {
            log.error("Error al obtener trayectoria: {}", e.getMessage());
            trajectory.put("error", e.getMessage());
            return trajectory;
        }
    }

    /**
     * Obtiene trayectoria usando MobilityDB.
     */
    private Map<String, Object> getTrajectoryWithMobilityDB(UUID routeId) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // Crear trajectory desde las posiciones GPS
            String sql = """
                SELECT 
                    ST_MakeTrajectory(
                        'STPOINT'::leaftype,
                        ST_Sequence(
                            ARRAY[
                                ROW(
                                    ST_SetSRID(ST_MakePoint(longitude, latitude), 4326)::tgeompoint,
                                    timestamp
                                )
                            ]
                        )
                    ) as trajectory,
                    COUNT(*) as point_count
                FROM gps_positions
                WHERE route_id = ?
                AND timestamp IS NOT NULL
                """;
            
            var queryResult = jdbcTemplate.queryForList(sql, routeId.toString());
            
            if (!queryResult.isEmpty()) {
                result.put("trajectory", queryResult.get(0).get("trajectory"));
                result.put("pointCount", queryResult.get(0).get("point_count"));
            }
            
            return result;
            
        } catch (Exception e) {
            log.debug("Error con MobilityDB trajectory: {}", e.getMessage());
            return getTrajectoryWithPostGIS(routeId);
        }
    }

    /**
     * Obtiene trayectoria usando PostGIS básico.
     */
    private Map<String, Object> getTrajectoryWithPostGIS(UUID routeId) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            String sql = """
                SELECT 
                    ST_MakeLine(location::geometry ORDER BY timestamp) as trajectory_line,
                    ST_Length(ST_MakeLine(location::geometry ORDER BY timestamp)) as length_meters,
                    COUNT(*) as point_count,
                    MIN(timestamp) as start_time,
                    MAX(timestamp) as end_time
                FROM gps_positions
                WHERE route_id = ?
                AND location IS NOT NULL
                """;
            
            var queryResult = jdbcTemplate.queryForList(sql, routeId.toString());
            
            if (!queryResult.isEmpty()) {
                var row = queryResult.get(0);
                result.put("trajectoryLine", row.get("trajectory_line"));
                result.put("lengthMeters", row.get("length_meters"));
                result.put("pointCount", row.get("point_count"));
                result.put("startTime", row.get("start_time"));
                result.put("endTime", row.get("end_time"));
                
                // Calcular duración
                if (row.get("start_time") != null && row.get("end_time") != null) {
                    LocalDateTime start = (LocalDateTime) row.get("start_time");
                    LocalDateTime end = (LocalDateTime) row.get("end_time");
                    result.put("durationMinutes", Duration.between(start, end).toMinutes());
                }
            }
            
            return result;
            
        } catch (Exception e) {
            log.error("Error al obtener trayectoria PostGIS: {}", e.getMessage());
            return Map.of("error", e.getMessage());
        }
    }

    /**
     * Obtiene estadísticas de tracking de una ruta.
     * 
     * @param routeId ID de la ruta
     * @return Map con estadísticas
     */
    public Map<String, Object> getTrackingStatistics(UUID routeId) {
        log.info("Obteniendo estadísticas de tracking para ruta: {}", routeId);
        
        Map<String, Object> stats = new HashMap<>();
        
        try {
            // Número de posiciones GPS
            String countSql = "SELECT COUNT(*) FROM gps_positions WHERE route_id = ?";
            Integer count = jdbcTemplate.queryForObject(countSql, Integer.class, routeId.toString());
            stats.put("totalPositions", count != null ? count : 0);
            
            // Trayecto total
            String lengthSql = """
                SELECT ST_Length(ST_MakeLine(location::geometry)) as total_length
                FROM gps_positions
                WHERE route_id = ?
                AND location IS NOT NULL
                """;
            try {
                Double length = jdbcTemplate.queryForObject(lengthSql, Double.class, routeId.toString());
                stats.put("totalLengthMeters", length != null ? length : 0.0);
            } catch (Exception e) {
                stats.put("totalLengthMeters", 0.0);
            }
            
            // Tiempos
            String timeSql = """
                SELECT 
                    MIN(timestamp) as first_position,
                    MAX(timestamp) as last_position,
                    MAX(timestamp) - MIN(timestamp) as duration
                FROM gps_positions
                WHERE route_id = ?
                AND timestamp IS NOT NULL
                """;
            
            var timeResult = jdbcTemplate.queryForList(timeSql, routeId.toString());
            if (!timeResult.isEmpty()) {
                var row = timeResult.get(0);
                stats.put("firstPositionTime", row.get("first_position"));
                stats.put("lastPositionTime", row.get("last_position"));
                stats.put("duration", row.get("duration"));
            }
            
            // Velocidad promedio
            stats.put("estimatedAvgSpeedKmh", calculateAverageSpeed(routeId));
            
            // Detener tiempo (si hay datos)
            stats.put("hasStopData", true);
            
            return stats;
            
        } catch (Exception e) {
            log.error("Error al obtener estadísticas: {}", e.getMessage());
            return Map.of("error", e.getMessage());
        }
    }

    /**
     * Calcula la velocidad promedio de una ruta.
     */
    private Double calculateAverageSpeed(UUID routeId) {
        try {
            String sql = """
                WITH ordered_points AS (
                    SELECT 
                        location,
                        timestamp,
                        LAG(timestamp) OVER (ORDER BY timestamp) as prev_time
                    FROM gps_positions
                    WHERE route_id = ?
                    AND location IS NOT NULL
                    AND timestamp IS NOT NULL
                )
                SELECT 
                    AVG(
                        ST_Distance(
                            location::geography,
                            LAG(location) OVER (ORDER BY timestamp)::geography
                        ) / 
                        EXTRACT(EPOCH FROM (timestamp - LAG(timestamp) OVER (ORDER BY timestamp)))
                    ) * 3.6 as avg_speed
                FROM ordered_points
                WHERE prev_time IS NOT NULL
                """;
            
            Double avgSpeed = jdbcTemplate.queryForObject(sql, Double.class, routeId.toString());
            return avgSpeed != null ? avgSpeed : 30.0; // Default 30 km/h
            
        } catch (Exception e) {
            log.debug("Error al calcular velocidad promedio: {}", e.getMessage());
            return 30.0; // Default
        }
    }

    /**
     * Obtiene la posición actual del autobús en una ruta.
     * 
     * @param routeId ID de la ruta
     * @return Map con la última posición
     */
    public Map<String, Object> getCurrentPosition(UUID routeId) {
        try {
            String sql = """
                SELECT 
                    ST_X(location::geometry) as longitude,
                    ST_Y(location::geometry) as latitude,
                    timestamp,
                    speed,
                    heading
                FROM gps_positions
                WHERE route_id = ?
                ORDER BY timestamp DESC
                LIMIT 1
                """;
            
            var result = jdbcTemplate.queryForList(sql, routeId.toString());
            
            if (!result.isEmpty()) {
                return result.get(0);
            }
            
            return Map.of("message", "No hay posiciones GPS para esta ruta");
            
        } catch (Exception e) {
            log.error("Error al obtener posición actual: {}", e.getMessage());
            return Map.of("error", e.getMessage());
        }
    }

    /**
     * Obtiene las últimas N posiciones de una ruta.
     * 
     * @param routeId ID de la ruta
     * @param limit Número de posiciones
     * @return Lista de posiciones
     */
    public List<Map<String, Object>> getRecentPositions(UUID routeId, int limit) {
        try {
            String sql = """
                SELECT 
                    ST_X(location::geometry) as longitude,
                    ST_Y(location::geometry) as latitude,
                    timestamp,
                    speed,
                    heading
                FROM gps_positions
                WHERE route_id = ?
                ORDER BY timestamp DESC
                LIMIT ?
                """;
            
            return jdbcTemplate.queryForList(sql, routeId.toString(), limit);
            
        } catch (Exception e) {
            log.error("Error al obtener posiciones recientes: {}", e.getMessage());
            return List.of();
        }
    }

    /**
     * Verifica si MobilityDB está disponible.
     */
    public boolean isMobilityDBAvailable() {
        try {
            jdbcTemplate.queryForObject(
                "SELECT 1 FROM pg_extension WHERE extname = 'mobilitydb'",
                Integer.class
            );
            return true;
        } catch (Exception e) {
            log.debug("MobilityDB no disponible: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Obtiene información de las extensiones espaciales disponibles.
     */
    public Map<String, Boolean> getAvailableExtensions() {
        Map<String, Boolean> extensions = new HashMap<>();
        
        extensions.put("postgis", isExtensionAvailable("postgis"));
        extensions.put("pgrouting", isExtensionAvailable("pgrouting"));
        extensions.put("mobilitydb", isExtensionAvailable("mobilitydb"));
        extensions.put("postgis_tiger_geocoder", isExtensionAvailable("postgis_tiger_geocoder"));
        
        return extensions;
    }

    private boolean isExtensionAvailable(String extName) {
        try {
            jdbcTemplate.queryForObject(
                "SELECT 1 FROM pg_extension WHERE extname = ?",
                Integer.class,
                extName
            );
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
