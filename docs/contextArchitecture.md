# Architecture Context

<!-- Add architecture decisions, patterns, and technical approach -->

---

## 1. 📦 Estructura General del Proyecto

```
src/main/java/com/saferoute/
├── common/                          ← Compartido por todos los módulos
│   ├── entity/                      ← JPA Entities
│   ├── repository/                  ← JpaRepository interfaces
│   ├── service/                     ← Services (incluye BaseCrudService)
│   ├── dto/                         ← DTOs por dominio
│   │   └── {modulo}/
│   │       ├── Request.java         ← DTO de entrada
│   │       └── Response.java        ← DTO de salida
│   └── usecase/
│       └── UseCaseAdvance.java      ← Template para UseCases
│
├── controller/                      ← Controllers modularizados
│   └── {modulo}/
│       └── {Modulo}Controller.java
│
├── usecase/                         ← UseCases modularizados
│   └── {modulo}/
│       ├── {Accion}UseCase.java
│       └── dto/                     ← Request/Response específicos del use case
│
├── adapter/                         ← Adapters modularizados
│   └── {modulo}/
│       ├── {Modulo}Adapter.java     ← Interfaz
│       └── {Modulo}AdapterImpl.java ← Implementación
│
└── config/                          ← Configuración de Spring
```

---

## 2. 🔄 Flujo de una Request (HTTP)

```
HTTP Request
    │
    ▼
┌─────────────────────────────────────┐
│         Controller                  │ ◄── 1. Recibe request
│  - Define endpoint                  │     2. Documenta (OpenAPI)
│  - Valida DTO con @Valid            │     3. Llama al Adapter
│  - Define @RequestMapping           │     
└──────────────┬──────────────────────┘
               │ DTO Request
               ▼
┌─────────────────────────────────────┐
│         Adapter                     │ ◄── 1. Traduce DTO → Request object
│  - Interfaz + Implementación         │     2. Decide: Service o UseCase
│  - Translation layer                │     3. Traduce Response → DTO
└──────────────┬──────────────────────┘
               │
       ┌───────┴───────┐
       ▼               ▼
┌──────────────┐  ┌─────────────────┐
│   Service    │  │    UseCase      │ ◄── UseCase: lógica compleja
│  (CRUD base) │  │ (UseCaseAdvance)│     Service: operaciones básicas
└──────┬───────┘  └────────┬────────┘
       │                    │
       ▼                    ▼
┌─────────────────────────────────────┐
│       Repository                    │ ◄── JpaRepository
│  - Acceso a datos                   │     + JpaSpecificationExecutor
└─────────────────────────────────────┘
               │
               ▼
┌─────────────────────────────────────┐
│         Entity                       │ ◄── JPA Entity (@Entity)
└─────────────────────────────────────┘
```

---

## 3. 📋 Convenciones por Capa

### 3.1 Entity (common/entity/)

```java
@Entity
@Table(name = "nombre_tabla")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EntityName {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String field;

    @Enumerated(EnumType.STRING)
    private EnumType enumField;

    @PrePersist
    protected void onCreate() {
        // Timestamps, defaults
    }
}
```

**Consideraciones:**
- Usar `UUID` como ID primario
- Usar `EnumType.STRING` para enums
- Definir enums internos en la entidad cuando aplican solo a esa tabla
- Usar `@Builder` para construcción flexible
- Incluir `@PrePersist` y `@PreUpdate` para timestamps

---

### 3.2 Repository (common/repository/)

```java
@Repository
public interface EntityRepository extends JpaRepository<Entity, UUID>, JpaSpecificationExecutor<Entity> {
    
    // Query methods
    Optional<Entity> findByField(String field);
    boolean existsByField(String field);
}
```

**Consideraciones:**
- **SIEMPRE** extender `JpaRepository<Entity, UUID>` y `JpaSpecificationExecutor<Entity>`
- Usar nombres de método de Spring Data para queries simples
- Para queries complejas, usar `@Query`

---

### 3.3 DTOs (common/dto/{modulo}/)

**Request DTO:**
```java
public record EntityRequest(
    String field,
    UUID relationId
) {}
```

**Response DTO:**
```java
public record EntityResponse(
    UUID id,
    String field,
    LocalDateTime createdAt
) {}
```

**Consideraciones:**
- Usar **records** para DTOs inmutables
- Nombrar como `{Entidad}Request` y `{Entidad}Response`
- Incluir todos los campos relevantes (no exponer password_hash)
- Para responses complejos, crear DTO específico

---

### 3.4 Service (common/service/)

```java
@Service
@Slf4j
@RequiredArgsConstructor
public class EntityService extends BaseCrudService<Entity, EntityRequest, EntityResponse, UUID> {

    private final PasswordEncoder passwordEncoder;  // si aplica

    // Obligatorio: retornar el repository
    @Override
    protected EntityRepository getRepository() {
        return entityRepository;
    }

    // Mapper: Request → Entity
    @Override
    protected Entity toEntity(EntityRequest request) {
        return Entity.builder()
                .field(request.field())
                .build();
    }

    // Mapper: Entity → Response
    @Override
    protected EntityResponse toResponse(Entity entity) {
        return new EntityResponse(
                entity.getId(),
                entity.getField()
        );
    }

    // Update: Request → Entity existente
    @Override
    protected void updateEntity(EntityRequest request, Entity entity) {
        if (request.field() != null) {
            entity.setField(request.field());
        }
    }
}
```

**Consideraciones:**
- Extender `BaseCrudService<Entity, REQ, RES, UUID>`
- Usar `@RequiredArgsConstructor` de Lombok
- Implementar los 4 métodos abstractos obligatorios
- Agregar métodos custom específicos del dominio
- No exponer entities directamente en responses

---

### 3.5 UseCase (usecase/{modulo}/)

```java
@Component
@Slf4j
public class AccionUseCase extends UseCaseAdvance<Request, Response> {

    private final EntityService entityService;

    // Validaciones previas
    @Override
    protected void preConditions(Request request) {
        if (request.field() == null) {
            throw new IllegalArgumentException("Field is required");
        }
    }

    // Lógica de negocio principal
    @Override
    protected Response core(Request request) {
        // Orchestración de servicios, transacciones, etc.
        return new Response(...);
    }

    // Side effects post-éxito
    @Override
    protected void postConditions(Response response) {
        // Notificaciones, logs, eventos
    }
}
```

**Consideraciones:**
- Usar `UseCaseAdvance<Request, Response>` como base
- `preConditions`: Validaciones, cargar contexto
- `core`: Lógica principal (OBLIGATORIO)
- `postConditions`: Efectos secundarios (opcional)
- Manejo de excepciones ya incluido en el template
- Para lógica simple, usar Service directamente

---

### 3.6 Adapter (adapter/{modulo}/)

**Interfaz:**
```java
public interface EntityAdapter {
    Response getById(UUID id);
    Response create(Request request);
    PageResponse<EntityResponse> search(PageRequest pageRequest);
}
```

**Implementación:**
```java
@Service
@Slf4j
@RequiredArgsConstructor
public class EntityAdapterImpl implements EntityAdapter {

    private final EntityService entityService;
    private final AccionUseCase accionUseCase;

    @Override
    public Response getById(UUID id) {
        return entityService.findById(id);
    }

    @Override
    public Response create(Request request) {
        // Decide: usar Service o UseCase
        if (esOperacionSimple(request)) {
            return entityService.create(request);
        } else {
            return accionUseCase.execute(request);
        }
    }
}
```

**Consideraciones:**
- Definir interfaz + implementación
- Traduce DTOs entre capas
- Decide cuándo usar Service vs UseCase
- Centraliza transformación de datos

---

### 3.7 Controller (controller/{modulo}/)

```java
@RestController
@RequestMapping("/api/v1/{recurso}")
@RequiredArgsConstructor
@Slf4j
public class EntityController {

    private final EntityAdapter entityAdapter;

    @PostMapping
    @Operation(summary = "Crear recurso", description = "Crea un nuevo recurso")
    @ApiResponse(responseCode = "201", description = "Recurso creado")
    @ApiResponse(responseCode = "400", description = "Request inválido")
    public ResponseEntity<Response> create(@Valid @RequestBody Request request) {
        Response response = entityAdapter.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener por ID")
    public ResponseEntity<Response> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(entityAdapter.getById(id));
    }

    @GetMapping
    @Operation(summary = "Buscar con paginación")
    public ResponseEntity<PageResponse> search(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String direction
    ) {
        return ResponseEntity.ok(entityAdapter.search(...));
    }
}
```

**Consideraciones:**
- Usar `@Operation` y `@ApiResponse` para documentación OpenAPI
- Delegar TODO al Adapter
- No tener lógica de negocio
- Usar `ResponseEntity` para respuestas HTTP
- Validar con `@Valid` en request body

---

## 4. 🎯 Cuándo Usar Service vs UseCase

| Escenario | Usar |
|-----------|------|
| CRUD básico (create, update, delete, findById) | **Service** |
| Consulta simple (findAll, existsById) | **Service** |
| Búsqueda con filtros/paginación | **Service** |
| Lógica de negocio compleja | **UseCase** |
| Múltiples operaciones transaccionales | **UseCase** |
| Orchestración de múltiples servicios | **UseCase** |
| Validaciones de negocio complejas | **UseCase** |
| Notificaciones post-operación | **UseCase** |

---

## 5. 📝 Convenciones de Código

### 5.1 Nombrado

| Elemento | Convención | Ejemplo |
|----------|------------|---------|
| Paquetes | lowercase | `com.saferoute.common.entity` |
| Clases | PascalCase | `UserService` |
| Métodos | camelCase | `findByEmail()` |
| Variables | camelCase | `userRepository` |
| Constantes | UPPER_SNAKE | `MAX_RETRY` |
| DTOs Records | PascalCase | `UserRequest` |
| Enums | PascalCase | `UserRole` |

### 5.2 Anotaciones Obligatorias

```java
// Entity
@Entity @Table @Getter @Setter @Builder

// Service
@Service @Slf4j @RequiredArgsConstructor

// Controller
@RestController @RequestMapping @RequiredArgsConstructor

// Repository
@Repository

// UseCase
@Component @Slf4j
```

### 5.3 Inyección de Dependencias

```java
// ✅ CORRECTO: Constructor con final fields
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
}

// ❌ INCORRECTO: @Autowired en campo
@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
}
```

---

## 6. ⚠️ Consideraciones Importantes

### 6.1 Seguridad

- **NUNCA** exponer passwords en responses
- Usar `PasswordEncoder` (BCrypt) para passwords
- No exponer `passwordHash` en entities
- Usar JWT para autenticación

### 6.2 Excepciones

- Usar `IllegalArgumentException` para errores de validación
- `BaseCrudService.EntityNotFoundException` para no encontrado
- No exponer stack traces en producción
- UseCase tiene manejo centralizado de excepciones

### 6.3 Validaciones

- Usar `@Valid` en Controller
- Usar validation annotations en DTOs (`@NotNull`, `@Size`, etc.)
- Validaciones de negocio en `preConditions` del UseCase

### 6.4 Transacciones

- `@Transactional` en Service o UseCase
- Por defecto, Propagation.REQUIRED
- No hacer commits manuales

### 6.5 Logs

- Usar SLF4J con `@Slf4j`
- Logs significativos (no "entró", "salió")
- Incluir contexto en errores

---

## 7. 📌 Resumen del Flujo Completo

```
1. Controller recibe HTTP Request
       ↓
2. Valida DTO con @Valid
       ↓
3. Llama al Adapter
       ↓
4. Adapter traduce y decide: Service o UseCase
       ↓
5. Service → Repository → Entity → DB
   UseCase → Orchestación → Services → Repos
       ↓
6. Respuesta fluye de vuelta: Entity → Service → Adapter → Controller
       ↓
7. Controller devuelve HTTP Response
```

---

## 8. 🔗 Referencias

- [Context General](./contextGeneral.md)
- [Context Database](./contextDatabase.md)
- [Context API](./contextAPI.md)
- [Context Auth](./contextAuth.md)
- [Context Features](./contextFeatures.md)
