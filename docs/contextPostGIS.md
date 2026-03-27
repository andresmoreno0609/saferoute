# PostGIS Context

<!-- Documentación de implementación de PostGIS para SafeRoute -->
<!--未尽之事：待完成 -->

---

## 1. 📋 Visión General

### 1.1 ¿Qué es PostGIS?

PostGIS es una extensión de PostgreSQL que permite almacenar, indexar y consultar datos geoespaciales (geométricos y geográficos). Es el estándar de facto para bases de datos espaciales en aplicaciones empresariales.

### 1.2 PostGIS en SafeRoute

SafeRoute usa PostGIS para gestionar la información geoespacial relacionada con el transporte escolar:

| Entidad | Tipo Geoespacial | Uso |
|---------|------------------|-----|
| `Student` | `Point` | Ubicación de casa y colegio |
| `Stop` | `Point` | Coordenadas de cada parada |
| `GpsPosition` | `Point` | Posición del vehículo en tiempo real |
| `StudentEvent` | `Point` | Ubicación donde ocurrió el evento |

---

## 2. 🏗️ Responsabilidades de PostGIS

### 2.1 ¿Qué hace PostGIS?

```
┌─────────────────────────────────────────────────────────────────┐
│                    RESPONSABILIDADES DE POSTGIS                  │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│  1. 📍 ALMACENAMIENTO                                           │
│     └─> Guardar coordenadas (latitud, longitud) como tipos     │
│         nativos de base de datos                                │
│                                                                  │
│  2. 🔍 INDEXACIÓN ESPACIAL                                      │
│     └─> GIST index para búsquedas rápidas por ubicación         │
│                                                                  │
│  3. 🧮 CÁLCULOS GEOMÉTRICOS                                    │
│     └─> Distancias, áreas, intersecciones, proximidad          │
│                                                                  │
│  4. 📐 TRANSFORMACIONES                                        │
│     └─> Convertir entre sistemas de coordenadas (SRID)          │
│                                                                  │
│  5. 🔎 CONSULTAS ESPACIALES                                     │
│     └─> "Encontrar todas las paradas dentro de 500m"            │
│                                                                  │
└─────────────────────────────────────────────────────────────────┘
```

### 2.2 ¿Qué NO hace PostGIS?

| ❌ No es responsable de | ✅ En su lugar |
|------------------------|----------------|
| Tracking en tiempo real | La aplicación genera los GPS positions |
| Notificaciones a usuarios | Servicio de notificaciones de la app |
| Lógica de negocio | Services/UseCases de Spring |
| Frontend/Mapa | Frontend (React/Mobile) |

---

## 3. 🛠️ Implementación Técnica

### 3.1 Configuración de Hibernate Spatial

```properties
# application.properties
spring.jpa.properties.hibernate.dialect=org.hibernate.spatial.dialect.postgis.PostgisDialect
```

### 3.2 Dependencias Requeridas

```xml
<!-- pom.xml -->
<dependency>
    <groupId>org.hibernate.orm</groupId>
    <artifactId>hibernate-spatial</artifactId>
</dependency>

<dependency>
    <groupId>org.locationtech.jts</groupId>
    <artifactId>jts-core</artifactId>
    <version>1.18.2</version>
</dependency>
```

### 3.3 Tipos Geométricos en Java

| Tipo JTS | Tipo PostGIS | Uso |
|----------|--------------|-----|
| `Point` | `GEOGRAPHY(POINT)` | Coordenadas individuales |
| `LineString` | `GEOGRAPHY(LINESTRING)` | Ruta recorrida |
| `Polygon` | `GEOGRAPHY(POLYGON)` | Zonas seguras |

### 3.4 Configuración de Entidades

```java
// Ejemplo: StudentEntity.java
@Entity
@Table(name = "students")
public class StudentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    // Tipo geográfico - POINT con SRID 4326 (WGS84)
    @Column(name = "location", columnDefinition = "GEOGRAPHY(POINT, 4326)")
    private Point location;

    @Column(name = "school_location", columnDefinition = "GEOGRAPHY(POINT, 4326)")
    private Point schoolLocation;

    // Getters y Setters
}
```

```java
// Ejemplo: GpsPositionEntity.java
@Entity
@Table(name = "gps_positions")
public class GpsPositionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "location", columnDefinition = "GEOGRAPHY(POINT, 4326)")
    private Point location;

    private Double speed;      // km/h
    private Double heading;     // grados (0-360)
    private Double accuracy;   // metros

    @Column(name = "timestamp")
    private LocalDateTime timestamp;
}
```

---

## 4. 📝 Operaciones CRUD con Datos Espaciales

### 4.1 Crear una Entidad con Punto Geográfico

```java
@Service
@RequiredArgsConstructor
public class StudentService {

    private final StudentRepository repository;

    public StudentResponse createStudent(StudentRequest request) {
        // Crear Point desde latitud y longitud usando JTS
        Point location = createPoint(request.latitude(), request.longitude());

        StudentEntity entity = StudentEntity.builder()
                .name(request.name())
                .address(request.address())
                .location(location)
                .schoolName(request.schoolName())
                .schoolLocation(createPoint(
                    request.schoolLatitude(),
                    request.schoolLongitude()
                ))
                .build();

        StudentEntity saved = repository.save(entity);
        return toResponse(saved);
    }

    // Factory method para crear Point con SRID 4326 (WGS84)
    private Point createPoint(Double longitude, Double latitude) {
        GeometryFactory geometryFactory = new GeometryFactory(
            new PrecisionModel(), 4326
        );
        return geometryFactory.createPoint(new Coordinate(longitude, latitude));
    }
}
```

### 4.2 Leer y Convertir a JSON

```java
// Al serializar a JSON, el Point se convierte automáticamente
// Pero necesitamos un formato legible:
// { "type": "Point", "coordinates": [-74.0721, 4.7110] }

// Custom converter o usar GeoJSON
public class PointSerializer extends JsonSerializer<Point> {
    @Override
    public void serialize(Point point, JsonGenerator gen, 
                         SerializerProvider serializers) throws IOException {
        gen.writeStartObject();
        gen.writeStringField("type", "Point");
        gen.writeArrayFieldStart("coordinates");
        gen.writeNumber(point.getX());  // Longitud
        gen.writeNumber(point.getY());  // Latitud
        gen.writeEndArray();
        gen.writeEndObject();
    }
}
```

### 4.3 Actualizar Ubicación

```java
public void updateStudentLocation(UUID studentId, Double lat, Double lng) {
    StudentEntity student = repository.findById(studentId)
            .orElseThrow(() -> new EntityNotFoundException("Student not found"));

    GeometryFactory gf = new GeometryFactory(new PrecisionModel(), 4326);
    student.setLocation(gf.createPoint(new Coordinate(lng, lat)));

    repository.save(student);
}
```

---

## 5. 🔍 Consultas Espaciales

### 5.1 Repository con Consultas Espaciales

```java
@Repository
public interface StopRepository extends JpaRepository<StopEntity, UUID>,
        JpaSpecificationExecutor<StopEntity> {

    // Encontrar paradas dentro de un radio (en metros)
    @Query(value = """
        SELECT * FROM stops s
        WHERE ST_DWithin(s.location, 
            ST_SetSRID(ST_MakePoint(:longitude, :latitude), 4326)::geography,
            :radiusInMeters)
        """, nativeQuery = true)
    List<StopEntity> findStopsWithinRadius(
        @Param("longitude") Double longitude,
        @Param("latitude") Double latitude,
        @Param("radiusInMeters") Double radiusInMeters
    );

    // Encontrar parada más cercana a una ubicación
    @Query(value = """
        SELECT * FROM stops s
        ORDER BY s.location <-> 
            ST_SetSRID(ST_MakePoint(:longitude, :latitude), 4326)::geography
        LIMIT 1
        """, nativeQuery = true)
    Optional<StopEntity> findNearestStop(
        @Param("longitude") Double longitude,
        @Param("latitude") Double latitude
    );
}
```

### 5.2 Service con Lógica de Negocios

```java
@Service
@RequiredArgsConstructor
public class RouteService {

    private final StopRepository stopRepository;
    private final GpsPositionRepository gpsRepository;

    // Encontrar estudiantes cerca del bus
    public List<StudentResponse> findStudentsNearBus(
            Double busLongitude, Double busLatitude, Double radiusMeters) {

        List<StopEntity> nearbyStops = stopRepository.findStopsWithinRadius(
                busLongitude, busLatitude, radiusMeters
        );

        return nearbyStops.stream()
                .map(stop -> toStudentResponse(stop.getStudent()))
                .toList();
    }

    // Calcular distancia recorrida en una ruta
    public Double calculateRouteDistance(UUID routeId) {
        List<GpsPositionEntity> positions = gpsRepository
                .findByRouteIdOrderByTimestampAsc(routeId);

        // Usar PostGIS para calcular distancia total
        // ST_Length de la línea formada por todos los puntos
        // Esta consulta se hace en la base de datos
        return gpsRepository.calculateTotalDistance(routeId);
    }

    // Obtener última posición conocida del vehículo
    public GpsPositionEntity getLastKnownPosition(UUID routeId) {
        return gpsRepository.findTopByRouteIdOrderByTimestampDesc(routeId);
    }
}
```

---

## 6. 🎯 Funcionalidades de SafeRoute con PostGIS

### 6.1 Mapa en Tiempo Real

```
┌─────────────────────────────────────────────────────────────────┐
│                    FUNCIÓN: MAPA TIEMPO REAL                     │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│  FLUJO:                                                          │
│                                                                  │
│  1. GPS del conductor envía posición cada 30 segundos           │
│     └─> POST /api/v1/gps/position                              │
│                                                                  │
│  2. Backend guarda en tabla gps_positions (con Point)          │
│                                                                  │
│  3. Frontend consulta posición actual:                          │
│     └─> GET /api/v1/routes/{id}/position                      │
│                                                                  │
│  4. Backend retorna Point → Frontend dibuja en mapa            │
│                                                                  │
│  CONSULTAS POSTGIS USADAS:                                       │
│  - ST_MakePoint(long, lat) - Crear punto desde coordenadas    │
│  - ST_AsGeoJSON - Convertir a formato JSON para mapa           │
│                                                                  │
└─────────────────────────────────────────────────────────────────┘
```

### 6.2 Geofencing - Zonas Seguras

```
┌─────────────────────────────────────────────────────────────────┐
│                    FUNCIÓN: GEOFENCING                          │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│  DEFINICIÓN:                                                     │
│  - Radio de 200m alrededor de cada parada                       │
│  - Al entrar/salir del radio, se dispara evento                │
│                                                                  │
│  IMPLEMENTACIÓN:                                                 │
│                                                                  │
│  // Verificar si el bus está dentro del radio de la parada    │
│  @Query("""                                                    │
│      SELECT ST_DWithin(stop.location,                          │
          :busLocation, :radiusMeters) = true                    │
│      FROM StopEntity stop                                      │
│      WHERE stop.id = :stopId                                   │
│  """)                                                          │
│  boolean isBusWithinStopRadius(                               │
│      @Param("stopId") UUID stopId,                            │
│      @Param("busLocation") Point busLocation,                 │
│      @Param("radiusMeters") Double radius                     │
│  );                                                             │
│                                                                  │
│  CONSULTAS POSTGIS USADAS:                                       │
│  - ST_DWithin - ¿Está dentro del radio?                        │
│  - ST_Distance - Calcular distancia entre puntos              │
│                                                                  │
└─────────────────────────────────────────────────────────────────┘
```

### 6.3 Notificaciones por Ubicación

```
┌─────────────────────────────────────────────────────────────────┐
│              FUNCIÓN: NOTIFICACIONES POR UBICACIÓN              │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│  ESCENARIOS:                                                     │
│                                                                  │
│  1. BUS LLEGA A PARADA (150m)                                  │
│     └─> "El bus está cerca, prepárate para subir"              │
│                                                                  │
│  2. ESTUDIANTE SUBE AL BUS                                     │
│     └─> Notificar al acudiente: "Juan/subió al bus"           │
│                                                                  │
│  3. BUS LLEGA AL COLEGIO                                       │
│     └─> Notificar: "Juan llegó al colegio"                     │
│                                                                  │
│  4. BUS CERCA DE CASA (200m)                                   │
│     └─> Notificar: "El bus está por llegar"                   │
│                                                                  │
│  IMPLEMENTACIÓN:                                                │
│  - ST_DWithin para detectar proximidad                         │
│  - StudentEvent se guarda con ubicación (Point)                │
│                                                                  │
└─────────────────────────────────────────────────────────────────┘
```

### 6.4 Rutas y Optimización

```
┌─────────────────────────────────────────────────────────────────┐
│                    FUNCIÓN: RUTAS Y ÓPTIMAS                     │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│  A FUTURO (v2):                                                 │
│  - Optimización de ruta (TSP - Traveling Salesman Problem)    │
│  - Evitar tráfico en tiempo real                                │
│  - Calcular tiempo estimado de llegada (ETA)                   │
│                                                                  │
│  CONSULTAS POSTGIS:                                             │
│  - ST_Length - Longitud total de la ruta                       │
│  - ST_LineSubString - Dividir ruta en segmentos                │
│  - ST_ClosestPoint - Encontrar punto más cercano               │
│                                                                  │
└─────────────────────────────────────────────────────────────────┘
```

---

## 7. 📊 Consultas PostGIS Más Comunes

### 7.1 Chuleta de Consultas

| Operación | Función PostGIS | Ejemplo |
|-----------|-----------------|---------|
| Crear punto | `ST_MakePoint(lng, lat)` | `ST_MakePoint(-74.0721, 4.7110)` |
| Distancia (m) | `ST_Distance(p1, p2)` | Distancia entre bus y parada |
| Dentro de radio | `ST_DWithin(p1, p2, radio)` | ¿Está a menos de 200m? |
| Punto más cercano | `<->` (operador) | `ORDER BY location <-> bus LIMIT 1` |
| Convertir a GeoJSON | `ST_AsGeoJSON(point)` | `{ "type": "Point", ... }` |
| Longitud línea | `ST_Length(line)` | Distancia total recorrida |
| Centroide | `ST_Centroid(polygon)` | Centro de una zona |

### 7.2 Ejemplos en JPQL/HQL

```java
// Distancia entre dos puntos (en metros)
@Query("""
    SELECT ST_Distance(s.location, 
        ST_SetSRID(ST_MakePoint(:lng, :lat), 4326)::geography)
    FROM StudentEntity s WHERE s.id = :id
""")
Double distanceToPoint(@Param("id") UUID id, 
                       @Param("lng") Double lng, 
                       @Param("lat") Double lat);

// Verificar si está dentro del radio
@Query("""
    SELECT ST_DWithin(s.location,
        ST_SetSRID(ST_MakePoint(:lng, :lat), 4326)::geography,
        :radius) 
    FROM StudentEntity s WHERE s.id = :id
""")
Boolean isWithinRadius(@Param("id") UUID id,
                       @Param("lng") Double lng,
                       @Param("lat") Double lat,
                       @Param("radius") Double radius);
```

---

## 8. ⚠️ Consideraciones y Mejores Prácticas

### 8.1 SRID (Spatial Reference System Identifier)

| SRID | Sistema | Uso |
|------|---------|-----|
| `4326` | WGS84 (GPS) | ✅ Estándar para coordenadas GPS |
| `32618` | UTM Zone 18N | Colombia (opcional) |
| `0` | Sin referencia | ❌ No usar |

**Importante:** Siempre usar `4326` para coordenadas GPS.

```java
// ✅ Correcto - SRID 4326
new GeometryFactory(new PrecisionModel(), 4326)

// ❌ Incorrecto - Sin SRID
new GeometryFactory()
```

### 8.2 Geography vs Geometry

```
┌─────────────────────────────────────────────────────────────────┐
│                    GEOGRAPHY vs GEOMETRY                        │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│  GEOMETRY:                                                       │
│  - Planar (plano 2D)                                            │
│  - Cálculos más rápidos                                          │
│  - Para áreas pequeñas (< 400km²)                               │
│  - Usa metros para distancia solo con SRID proyectado          │
│                                                                  │
│  GEOGRAPHY:                                                      │
│  - Esférico (considera curvatura terrestre)                    │
│  - Cálculos más precisos para largas distancias                │
│  - Usa metros independientemente del SRID                       │
│  - ✅ RECOMENDADO para coordenadas GPS                         │
│                                                                  │
│  SafeRoute usa: GEOGRAPHY(POINT, 4326)                         │
│                                                                  │
└─────────────────────────────────────────────────────────────────┘
```

### 8.3 Índices Espaciales

```sql
-- Crear índice GIST para columnas geográficas
CREATE INDEX idx_students_location 
ON students USING GIST(location);

CREATE INDEX idx_stops_location 
ON stops USING GIST(location);

CREATE INDEX idx_gps_positions_location 
ON gps_positions USING GIST(location);
```

**Importante:** Sin índice GIST, las consultas espaciales son lentas.

### 8.4 Performance

| Recomendación | Reason |
|--------------|--------|
| Siempre usar índices GIST | Búsquedas O(log n) en vez de O(n) |
| Limitar resultados con `LIMIT` | Evitar scans completos |
| Usar `GEOGRAPHY` no `GEOMETRY` | Cálculos en metros exactos |
| Batch inserts para GPS | Insertar muchas posiciones a la vez |
| Particionar `gps_positions` por fecha | Tablas más pequeñas y rápidas |

---

## 9. 📋 Plan de Implementación

### Fase 1: Configuración Base (✅ Completado)
- [x] Agregar dependencias hibernate-spatial y jts-core
- [x] Configurar dialect en application.properties
- [x] Agregar extensión PostGIS en DB

### Fase 2: Entidades Espaciales (✅ Completado)
- [x] StudentEntity con Point (location, schoolLocation)
- [x] StopEntity con Point
- [x] GpsPositionEntity con Point
- [x] StudentEventEntity con Point

### Fase 3: Repositorios y Consultas (🔄 En Progreso)
- [ ] Agregar consultas espaciales a repositorios
- [ ] Implementar findStopsWithinRadius
- [ ] Implementar findNearestStop

### Fase 4: Servicios
- [ ] StudentLocationService - actualizar ubicación
- [ ] GpsTrackingService - recibir y guardar posiciones
- [ ] ProximityService - notificaciones por proximidad

### Fase 5: Controladores
- [ ] POST /api/v1/gps/position
- [ ] GET /api/v1/routes/{id}/position
- [ ] GET /api/v1/students/{id}/distance-to-bus

---

## 10. 🔧 Herramientas de Desarrollo

### 10.1 Verificar que PostGIS está instalado

```sql
-- En psql o cualquier cliente PostgreSQL
SELECT postgis_full_version();
```

### 10.2 Visualizar datos espaciales

```sql
-- Ver coordinates como texto
SELECT id, name, ST_AsText(location) as location FROM students;

-- Ver como GeoJSON
SELECT id, name, ST_AsGeoJSON(location) as location FROM students;

-- VerSRID
SELECT id, name, ST_SRID(location) as srid FROM students;
```

### 10.3 Testing con datos espaciales

```java
@SpringBootTest
class StudentServiceTest {

    @Test
    void shouldCalculateDistance() {
        // Crear dos puntos
        Point bogota = createPoint(-74.0721, 4.7110);
        Point chia = createPoint(-74.0577, 4.8617);

        // Distancia debería ser aproximadamente 18km
        double distance = bogota.distance(chia); // En grados
        // O usar ST_Distance en query para metros
    }
}
```

---

## 11. 📚 Referencias

### Documentación Oficial
- [PostGIS Documentation](https://postgis.net/documentation/)
- [Hibernate Spatial](https://docs.jboss.org/hibernate/orm/current/userguide/html_single/Hibernate_User_Guide.html#spatial)
- [JTS Topology Suite](https://locationtech.github.io/jts/)

### Artículos y Tutoriales
- [PostGIS Spatial Queries](https://postgis.net/docs/using_postgis_dbmanagement.html#idm2246)
- [Geography vs Geometry](https://postgis.net/docs/using_postgis_dbmanagement.html#PostGIS_Geography vs Geometry)

### SRID Comunes
- 4326: WGS84 (GPS)
- 32618: UTM Zone 18N (Colombia)

---

## 12. ❓ Preguntas Frecuentes

### P: ¿Por qué no guardamos lat/lng como columnas separadas?
R: PostGIS optimiza consultas espaciales (radio, distancia, proximidad). Guardar como `Point` permite usar funciones como `ST_DWithin` nativamente.

### P: ¿Necesitamos actualizar la ubicación de todos los estudiantes en tiempo real?
R: No. Solo guardamos la ubicación de casa/colegio (estática). Las posiciones GPS son del vehículo.

### P: ¿Qué precisión tiene el GPS?
R: Depende del dispositivo. Típicamente 5-10 metros. Guardamos `accuracy` en metros.

### P: ¿Cuántas posiciones GPS se guardan por día?
R: Estimación: 1 posición cada 30 seg × 8 horas × 30 rutas = ~28,800 posiciones/día.

---

## 13. 📌 Resumen para el Equipo

```
┌─────────────────────────────────────────────────────────────────┐
│                    RESUMEN POSTGIS PARA SAFEROUTE              │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│  ✅ USAMOS:                                                     │
│     - PostgreSQL + PostGIS 3.4+                                 │
│     - Hibernate Spatial                                        │
│     - JTS (Java Topology Suite)                                │
│     - SRID 4326 (WGS84 - coordenadas GPS)                      │
│                                                                  │
│  📍 DATOS ESPACIALES:                                           │
│     - Student: location (casa), schoolLocation (colegio)      │
│     - Stop: location (coordenadas de parada)                   │
│     - GpsPosition: location (posición del bus)                │
│     - StudentEvent: location (donde ocurrió evento)            │
│                                                                  │
│  🔍 CONSULTAS CLAVE:                                            │
│     - ST_DWithin: "¿Está cerca?" (radio)                      │
│     - ST_Distance: "¿A qué distancia?"                          │
│     - <->: "Encontrar más cercano"                             │
│                                                                  │
│  ⚡ PERFORMANCE:                                                │
│     - SIEMPRE crear índice GIST                                 │
│     - Usar GEOGRAPHY no GEOMETRY                               │
│     - Limitar resultados con LIMIT                              │
│                                                                  │
└─────────────────────────────────────────────────────────────────┘
```
