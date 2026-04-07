# Design: Gestión de Vehículos y Documentos (Actualizado)

## Technical Approach

Implementar esquema unificado de documentos para conductor y vehículo. DriverLicense se integra en DriverDocument como tipo LICENCIA. Regla de negocio: solo un documento activo por tipo (al crear nuevo, anterior se inactiva automáticamente). Historial conservado.

## Architecture Decisions

### Decision: Integrar DriverLicense en DriverDocument

| Opción | Ventaja | Desventaja |
|--------|---------|------------|
| Entidad separada DriverLicenseEntity | Datos específicos de licencia agrupados | Duplicación código, dos tablas |
| DriverDocument con campos extras | Unificación, consultas simples | Documento genérico pierde identidad |
| **ELEGIDA: DriverDocument con tipo LICENCIA + campos licenseType, licenseNumber** | Unifica gestión, extensible, historial único | Requiere campos condicionales |

**Rationale**: Mantiene consistencia con VehicleDocument, permite consultar "todos los documentos activos del conductor" en una query, y facilita migración de datos existentes.

### Decision: Estrategia de Activación de Documentos

| Opción | Ventaja | Desventaja |
|--------|---------|------------|
| Soft delete (isActive) | Reversible, auditoría simple | Consulta siempre filtra por isActive |
| Campo activo + histórico separado | Queries rápidas, datos limpios | Duplicación estructura |
| **ELEGIDA: Campo isActive + fecha vigencia (startDate/endDate)** | Historial completo consultable, validación por fecha | Requiere índice compuesto |

**Rationale**: Permite consultar documentos históricos para auditoría, y validar vigencia por fecha actual. endDate NULL = sin vencimiento.

### Decision: Relación Conductor-Vehículo

| Opción | Ventaja | Desventaja |
|--------|---------|------------|
| 1 conductor → N vehículos | Flexibilidad cambio vehículo | Complejidad gestión |
| **ELEGIDA: 1 conductor → 1 vehículo** | Simple, requisitos actuales | Cambio futuro requiere migración |

**Rationale**: Según specs, "Un conductor = un vehículo (1:1)". Simplifica queries de disponibilidad.

## Data Flow

```
┌─────────────────────────────────────────────────────────────────┐
│                        RELACIONES                                │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│   DriverEntity (1) ────────── (1) VehicleEntity                  │
│         │                              │                          │
│         │ (1:N)                        │ (1:N)                   │
│         ▼                              ▼                         │
│   DriverDocumentEntity          VehicleDocumentEntity           │
│   - type: CEDULA, LICENCIA,     - type: SOAP, SEGURO,           │
│     PASAPORTE, OTRO               TECNOMECANICA,                │
│   - documentNumber (genérico)     TARJETA_PROPIEDAD             │
│   - licenseCategory (solo LICENCIA)                            │
│   - startDate, endDate            - startDate, endDate          │
│   - fileUrl                       - fileUrl                      │
│   - isActive                      - isActive                    │
│                                                                  │
│   Regla: Solo 1 documento activo por tipo                       │
│   endDate NULL = sin vencimiento                                │
└─────────────────────────────────────────────────────────────────┘
```

```
┌─────────────────┐     ┌──────────────────┐     ┌─────────────────┐
│  REST Controller │────▶│  UseCase/Service │────▶│  Repository     │
└─────────────────┘     └──────────────────┘     └─────────────────┘
                               │                        │
                               ▼                        ▼
                        ┌──────────────────┐     ┌─────────────────┐
                        │ DocumentService │     │ JPA Entities   │
                        │ (lógica activa) │     │ (lazy loaded)  │
                        └──────────────────┘     └─────────────────┘
```

## File Changes

| File | Action | Description |
|------|--------|-------------|
| `common/entity/VehicleEntity.java` | Create | Entidad vehículo con placa única |
| `common/entity/VehicleDocumentEntity.java` | Create | Documentos del vehículo (con startDate/endDate/isActive) |
| `common/entity/DriverDocumentEntity.java` | Create | Documentos del conductor (incluye licencia) |
| `common/dto/vehicle/VehicleRequest.java` | Create | DTO creación vehículo |
| `common/dto/vehicle/VehicleResponse.java` | Create | DTO respuesta vehículo |
| `common/dto/vehicle/VehicleDocumentRequest.java` | Create | DTO documentos vehículo |
| `common/dto/vehicle/VehicleDocumentResponse.java` | Create | DTO respuesta documentos |
| `common/dto/driver/DriverDocumentRequest.java` | Create | DTO documentos conductor |
| `common/dto/driver/DriverDocumentResponse.java` | Create | DTO respuesta documentos |
| `common/repository/VehicleRepository.java` | Create | Métodos: findByPlate, findByDriverId |
| `common/repository/VehicleDocumentRepository.java` | Create | findByVehicleAndType, findActiveByType |
| `common/repository/DriverDocumentRepository.java` | Create | findByDriverAndType, findActiveByType |
| `common/service/VehicleDocumentService.java` | Create | CRUD + lógica activo único |
| `common/service/DriverDocumentService.java` | Create | CRUD + lógica activo único |
| `common/service/DriverAvailabilityService.java` | Create | Lógica disponibilidad conductor |
| `driver/adapter/DriverAdapter.java` | Modify | Actualizar mappings a DriverDocument |
| `vehicle/adapter/VehicleAdapter.java` | Create | Adapter para endpoints vehículos |
| `driver/controller/DriverDocumentController.java` | Create | REST endpoints DriverDocuments |
| `vehicle/controller/VehicleDocumentController.java` | Create | REST endpoints VehicleDocuments |
| `route/usecase/CreateRouteUseCase.java` | Modify | Validar documentos vigentes |

## Interfaces / Contracts

### Enums

```java
// VehicleDocumentType
public enum VehicleDocumentType {
    SOAP, SEGURO, TECNOMECANICA, TARJETA_PROPIEDAD
}

// DriverDocumentType
public enum DriverDocumentType {
    CEDULA, LICENCIA, PASAPORTE, OTRO
}

// LicenseCategory (para DriverDocument cuando tipo = LICENCIA)
public enum LicenseCategory {
    A, B, C, A1, A2, B1, B2, C1, C2, C3
}
```

### Entidades

```java
// DriverDocumentEntity
@Entity
@Table(name = "driver_documents")
@Getter @Setter
public class DriverDocumentEntity {
    @Id @GeneratedValue UUID id;
    
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "driver_id", nullable = false)
    private DriverEntity driver;
    
    @Enumerated(STRING) @Column(nullable = false)
    private DriverDocumentType documentType;
    
    // Número genérico del documento (CEDULA: número cédula, LICENCIA: número licencia, PASAPORTE: número pasaporte)
    @Column(name = "document_number", length = 50)
    private String documentNumber;
    
    // Categoría de licencia - solo aplica cuando documentType = LICENCIA (ej: A, B, C, A1, A2, B1, B2)
    @Column(name = "license_category", length = 10)
    private String licenseCategory;
    
    @Column(name = "file_url", length = 500)
    private String fileUrl;
    
    // Fecha inicio de vigencia del documento
    @Column(name = "start_date")
    private LocalDate startDate;
    
    // Fecha fin de vigencia (NULL = sin vencimiento)
    @Column(name = "end_date")
    private LocalDate endDate;
    
    @Column(nullable = false)
    private Boolean isActive = true;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}

// VehicleDocumentEntity
@Entity
@Table(name = "vehicle_documents")
@Getter @Setter
public class VehicleDocumentEntity {
    @Id @GeneratedValue UUID id;
    
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "vehicle_id", nullable = false)
    private VehicleEntity vehicle;
    
    @Enumerated(STRING) @Column(nullable = false)
    private VehicleDocumentType documentType;
    
    @Column(name = "file_url", length = 500)
    private String fileUrl;
    
    @Column(name = "start_date")
    private LocalDate startDate;
    
    @Column(name = "end_date")
    private LocalDate endDate;
    
    @Column(nullable = false)
    private Boolean isActive = true;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}

// VehicleEntity
@Entity
@Table(name = "vehicles")
@Getter @Setter
public class VehicleEntity {
    @Id @GeneratedValue UUID id;
    
    @Column(unique = true, nullable = false, length = 20)
    private String plate;
    
    @Column(length = 100) private String model;
    @Column(length = 100) private String brand;
    @Column(length = 50) private String color;
    private Integer capacity;
    
    @OneToOne(fetch = LAZY)
    @JoinColumn(name = "driver_id", unique = true)
    private DriverEntity driver;
    
    @OneToMany(mappedBy = "vehicle", cascade = ALL, orphanRemoval = true)
    private List<VehicleDocumentEntity> documents;
    
    @Column(name = "created_at") private LocalDateTime createdAt;
    @Column(name = "updated_at") private LocalDateTime updatedAt;
}
```

### Servicio de Disponibilidad

```java
@Service
public class DriverAvailabilityService {
    
    /**
     * Verifica si un conductor puede trabajar:
     * - Debe tener un vehículo asignado
     * - Todos sus documentos activos deben tener vigencia válida (endDate >= hoy O endDate IS NULL)
     */
    public boolean isAvailableToWork(DriverEntity driver) {
        if (driver.getVehicle() == null) return false;
        
        // Verificar que TODOS los documentos activos estén vigentes
        List<DriverDocumentEntity> activeDocuments = driver.getDocuments().stream()
            .filter(DriverDocumentEntity::getIsActive)
            .toList();
        
        for (DriverDocumentEntity doc : activeDocuments) {
            if (!isVigente(doc.getStartDate(), doc.getEndDate())) {
                return false;
            }
        }
        
        // Verificar documentos obligatorios del vehículo
        List<VehicleDocumentType> requiredTypes = List.of(
            VehicleDocumentType.SOAP,
            VehicleDocumentType.SEGURO,
            VehicleDocumentType.TECNOMECANICA
        );
        
        for (VehicleDocumentType type : requiredTypes) {
            VehicleDocumentEntity doc = getActiveDocumentByType(driver.getVehicle(), type);
            if (doc == null || !isVigente(doc.getStartDate(), doc.getEndDate())) {
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * Documento vigente: startDate <= hoy Y (endDate >= hoy O endDate IS NULL)
     * endDate IS NULL = documento sin vencimiento (ej: ciertos documentos de identidad)
     */
    private boolean isVigente(LocalDate startDate, LocalDate endDate) {
        LocalDate now = LocalDate.now();
        
        // Verificar que no haya comenzado aún
        if (startDate != null && now.isBefore(startDate)) {
            return false;
        }
        
        // Verificar vigencia: endDate es NULL (sin vencimiento) O endDate >= hoy
        return endDate == null || !now.isAfter(endDate);
    }
}
```

### Lógica de Activación - Regla de Negocio Unificada

**Regla**: Solo UN documento activo por tipo. Al crear un nuevo documento del mismo tipo, el anterior pasa a `isActive = false`.

```java
@Service
public class DriverDocumentService {
    
    public DriverDocumentEntity create(DriverDocumentRequest request, UUID driverId) {
        // 1. Buscar documento activo del mismo tipo
        driverDocumentRepository.findByDriverIdAndDocumentTypeAndIsActiveTrue(
            driverId, request.documentType()
        ).ifPresent(activeDoc -> {
            // 2. Inactivar anterior (mantiene historial)
            activeDoc.setIsActive(false);
            driverDocumentRepository.save(activeDoc);
        });
        
        // 3. Crear nuevo documento activo
        DriverDocumentEntity doc = DriverDocumentEntity.builder()
            .driver(driver)
            .documentType(request.documentType())
            .documentNumber(request.documentNumber())  // genérico: número cédula, licencia, pasaporte
            .licenseCategory(request.licenseCategory()) // solo aplica si documentType = LICENCIA
            .fileUrl(request.fileUrl())
            .startDate(request.startDate())
            .endDate(request.endDate())  // NULL = sin vencimiento
            .isActive(true)
            .build();
        
        return driverDocumentRepository.save(doc);
    }
}

@Service
public class VehicleDocumentService {
    
    public VehicleDocumentEntity create(VehicleDocumentRequest request, UUID vehicleId) {
        // Misma lógica: inactivar anterior del mismo tipo antes de crear nuevo
        vehicleDocumentRepository.findByVehicleIdAndDocumentTypeAndIsActiveTrue(
            vehicleId, request.documentType()
        ).ifPresent(activeDoc -> {
            activeDoc.setIsActive(false);
            vehicleDocumentRepository.save(activeDoc);
        });
        
        VehicleDocumentEntity doc = VehicleDocumentEntity.builder()
            .vehicle(vehicle)
            .documentType(request.documentType())
            .fileUrl(request.fileUrl())
            .startDate(request.startDate())
            .endDate(request.endDate())
            .isActive(true)
            .build();
        
        return vehicleDocumentRepository.save(doc);
    }
}
```

### Endpoints

```java
// DriverDocumentController
@RestController @RequestMapping("/api/drivers/{driverId}/documents")
public class DriverDocumentController {
    
    @GetMapping
    public List<DriverDocumentResponse> getAll(@PathVariable UUID driverId);
    
    @GetMapping("/active")
    public List<DriverDocumentResponse> getActive(@PathVariable UUID driverId);
    
    @PostMapping
    public DriverDocumentResponse create(
        @PathVariable UUID driverId,
        @Valid @RequestBody DriverDocumentRequest request);
    
    @PutMapping("/{documentId}")
    public DriverDocumentResponse update(
        @PathVariable UUID driverId,
        @PathVariable UUID documentId,
        @Valid @RequestBody DriverDocumentRequest request);
    
    @DeleteMapping("/{documentId}")
    public void delete(@PathVariable UUID driverId, @PathVariable UUID documentId);
}

// VehicleDocumentController
@RestController @RequestMapping("/api/vehicles/{vehicleId}/documents")
public class VehicleDocumentController {
    
    @GetMapping
    public List<VehicleDocumentResponse> getAll(@PathVariable UUID vehicleId);
    
    @GetMapping("/active")
    public List<VehicleDocumentResponse> getActive(@PathVariable UUID vehicleId);
    
    @PostMapping
    public VehicleDocumentResponse create(
        @PathVariable UUID vehicleId,
        @Valid @RequestBody VehicleDocumentRequest request);
    
    @PutMapping("/{documentId}")
    public VehicleDocumentResponse update(
        @PathVariable UUID vehicleId,
        @PathVariable UUID documentId,
        @Valid @RequestBody VehicleDocumentRequest request);
    
    @DeleteMapping("/{documentId}")
    public void delete(@PathVariable UUID vehicleId, @PathVariable UUID documentId);
}
```

## Testing Strategy

| Layer | What to Test | Approach |
|-------|-------------|----------|
| Unit | DriverAvailabilityService.isAvailableToWork() | Mock entities, fechas válidas/vencidas |
| Unit | VehicleDocumentService.create() inactiva anterior | Mock repository, verificar setIsActive(false) |
| Unit | DriverDocumentService.create() inactiva licencia anterior | Mock repository |
| Integration | POST /drivers/{id}/documents flujo completo | @SpringBootTest, H2 |
| Integration | POST /vehicles/{id}/documents activa/desactiva | Verificar estado anterior |
| E2E | Asignar ruta a conductor sin documentos vigentes | Valida rechazo |

## Migration / Rollout

**Migración requerida:**
1. Crear tablas: `vehicles`, `vehicle_documents`, `driver_documents`
2. Migrar datos existentes (vehiclePlate → vehicles, licencia → driver_documents)
3. Remover columnas `vehicle_plate`, `vehicle_model`, `vehicle_color` de `drivers`

**Plan:**
1. Agregar FKs opcionales en nueva estructura
2. Migrar datos como documentos vigentes (endDate: 2030-01-01 para documentos sin fecha)
3. Desplegar nueva versión app
4. Remover columnas legacy en siguiente release

**Rollback:** Scripts SQL reversibles, datos existentes preservados.

## Open Questions

- [x] ¿Un conductor = un vehículo? → Sí, 1:1 implementado
- [x] ¿Solo un documento activo por tipo? → Sí, lógica de inactivation implementada
- [x] ¿Historial guardado? → Sí, documentos inactivos consultables
- [x] ¿DriverLicense integrado? → Sí, como DriverDocument con tipo LICENCIA
- [x] ¿Campos de vigencia unificados? → Sí, DriverDocument ahora tiene startDate/endDate/isActive
- [x] ¿licenseNumber → documentNumber? → Sí, campo genérico para cualquier tipo de documento
- [x] ¿licenseType → licenseCategory? → Sí, renombrado y solo aplica para LICENCIA
- [x] ¿endDate NULL = sin vencimiento? → Sí, documentos de identidad sin fecha de expiración

None - todas las preguntas resueltas por specs del usuario.
