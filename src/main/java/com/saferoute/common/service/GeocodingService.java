package com.saferoute.common.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Servicio de geocodificación usando PostGIS TIGER geocoder.
 * Convierte direcciones en coordenadas GPS.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class GeocodingService {

    private final JdbcTemplate jdbcTemplate;
    private final GeometryFactory geometryFactory = new GeometryFactory(new org.locationtech.jts.geom.PrecisionModel(), 4326);

    /**
     * Geocodifica una dirección usando PostGIS TIGER geocoder.
     * 
     * @param address Dirección a geocodificar
     * @return Map con latitud, longitud y datos de confiabilidad
     */
    public Map<String, Object> geocodeAddress(String address) {
        log.info("Geocodificando dirección: {}", address);
        
        try {
            // Usar TIGER geocoder de PostGIS
            String sql = """
                SELECT 
                    ST_X(ST_Transform(geomout, 4326)) as longitude,
                    ST_Y(ST_Transform(geomout, 4326)) as latitude,
                    rating
                FROM postgis_tiger_geocoder.geocode(
                    postgis_tiger_geocoder.pagc_normalize_address(?),
                    1
                )
                WHERE geomout IS NOT NULL
                LIMIT 1
                """;
            
            var result = jdbcTemplate.queryForList(sql, address);
            
            if (result.isEmpty()) {
                log.warn("No se pudo geocodificar la dirección: {}", address);
                return null;
            }
            
            Map<String, Object> geocodeResult = result.get(0);
            log.info("Geocodificación exitosa: lat={}, lon={}", 
                    geocodeResult.get("latitude"), 
                    geocodeResult.get("longitude"));
            
            return geocodeResult;
            
        } catch (Exception e) {
            log.error("Error al geocodificar dirección: {} - {}", address, e.getMessage());
            return null;
        }
    }

    /**
     * Geocodifica una dirección y retorna un Point de PostGIS.
     * 
     * @param address Dirección a geocodificar
     * @return Point con las coordenadas o null si falla
     */
    public Point geocodeToPoint(String address) {
        Map<String, Object> result = geocodeAddress(address);
        
        if (result == null) {
            return null;
        }
        
        try {
            Double longitude = ((Number) result.get("longitude")).doubleValue();
            Double latitude = ((Number) result.get("latitude")).doubleValue();
            
            return geometryFactory.createPoint(new Coordinate(longitude, latitude));
            
        } catch (Exception e) {
            log.error("Error al crear Point: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Verifica si TIGER geocoder está disponible.
     * 
     * @return true si está disponible
     */
    public boolean isGeocoderAvailable() {
        try {
            jdbcTemplate.queryForObject(
                "SELECT 1 FROM pg_extension WHERE extname = 'postgis_tiger_geocoder'",
                Integer.class
            );
            return true;
        } catch (Exception e) {
            log.warn("TIGER geocoder no disponible: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Calcula la distancia entre dos puntos usando PostGIS.
     * 
     * @param lat1 Latitud punto 1
     * @param lon1 Longitud punto 1
     * @param lat2 Latitud punto 2
     * @param lon2 Longitud punto 2
     * @return Distancia en metros
     */
    public Double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        try {
            String sql = """
                SELECT ST_Distance(
                    ST_SetSRID(ST_MakePoint(?, ?), 4326)::geography,
                    ST_SetSRID(ST_MakePoint(?, ?), 4326)::geography
                ) as distance
                """;
            
            Double distance = jdbcTemplate.queryForObject(sql, Double.class, lon1, lat1, lon2, lat2);
            return distance != null ? distance : 0.0;
            
        } catch (Exception e) {
            log.error("Error al calcular distancia: {}", e.getMessage());
            return 0.0;
        }
    }

    /**
     * Calcula la distancia total de una ruta usando pgRouting.
     * 
     * @param routeId ID de la ruta
     * @return Distancia total en metros
     */
    public Double calculateRouteDistance(UUID routeId) {
        try {
            // Usar las posiciones GPS de la ruta
            String sql = """
                SELECT ST_MakeLine(location::geometry ORDER BY timestamp) as trajectory,
                       ST_Length(ST_MakeLine(location::geometry ORDER BY timestamp)) as distance
                FROM gps_positions
                WHERE route_id = ?
                AND location IS NOT NULL
                """;
            
            var result = jdbcTemplate.queryForList(sql, routeId.toString());
            
            if (result.isEmpty()) {
                return 0.0;
            }
            
            return ((Number) result.get(0).get("distance")).doubleValue();
            
        } catch (Exception e) {
            log.error("Error al calcular distancia de ruta: {}", e.getMessage());
            return 0.0;
        }
    }
}
