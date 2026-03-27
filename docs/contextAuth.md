# Authentication Context

<!-- Add authentication, authorization, and security details -->

---

## 1. 📋 Visión General del Sistema de Autenticación

El sistema de autenticación de SafeRoute usa **JWT (JSON Web Tokens)** para la gestión de sesiones stateless.

### 1.1 Características

| Característica | Valor |
|----------------|-------|
| Tipo de Token | JWT (Access + Refresh) |
| Algoritmo | HS256 (HMAC SHA-256) |
| Tiempo de expiración (Access) | 15 minutos |
| Tiempo de expiración (Refresh) | 7 días |
| Almacenamiento | Client-side (localStorage/cookies HttpOnly) |
| Roles soportados | ADMIN, DRIVER, GUARDIAN |

### 1.2 Flujo de Autenticación

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                           FLUJO DE LOGIN                                     │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  1. Usuario envía credentials                                                │
│     ┌──────────────────┐                                                    │
│     │ POST /auth/login │ { email, password }                                │
│     └────────┬─────────┘                                                    │
│              │                                                               │
│              ▼                                                               │
│  2. Controller → Adapter → UseCase                                         │
│              │                                                               │
│              ▼                                                               │
│  3. Validar email existe                                                    │
│     └── SI: continuar                                                       │
│     └── NO: throw UnauthorizedException                                     │
│              │                                                               │
│              ▼                                                               │
│  4. Validar password (BCrypt)                                               │
│     └── SI: continuar                                                       │
│     └── NO: throw UnauthorizedException                                     │
│              │                                                               │
│              ▼                                                               │
│  5. Verificar usuario activo (status = ACTIVE)                              │
│     └── SI: continuar                                                       │
│     └── NO: throw AccountDisabledException                                   │
│              │                                                               │
│              ▼                                                               │
│  6. Generar Access Token + Refresh Token                                   │
│     ├── Claims: userId, email, role                                         │
│     └── Generar пары: access + refresh                                      │
│              │                                                               │
│              ▼                                                               │
│  7. Actualizar lastLoginAt en usuario                                       │
│              │                                                               │
│              ▼                                                               │
│  8. Devolver AuthResponse { accessToken, refreshToken, user }             │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

---

## 2. 🔐 Endpoints de Autenticación

### 2.1 Tabla de Endpoints

| Método | Endpoint | Descripción | Público | Requiere Auth |
|--------|----------|-------------|---------|---------------|
| POST | `/api/v1/auth/login` | Iniciar sesión | ✅ | ❌ |
| POST | `/api/v1/auth/register` | Registrarse | ✅ | ❌ |
| POST | `/api/v1/auth/refresh` | Renovar access token | ✅ | ❌ |
| POST | `/api/v1/auth/logout` | Cerrar sesión | ❌ | ✅ |
| GET | `/api/v1/auth/me` | Obtener usuario actual | ❌ | ✅ |

### 2.2 Detalle de Endpoints

#### POST /auth/login
```http
POST /api/v1/auth/login
Content-Type: application/json

{
  "email": "usuario@ejemplo.com",
  "password": "password123"
}
```

**Response (200 OK):**
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "expiresIn": 900,
  "user": {
    "id": "uuid",
    "email": "usuario@ejemplo.com",
    "name": "Juan Perez",
    "role": "ADMIN"
  }
}
```

**Errores:**
- 401: Credenciales inválidas
- 403: Cuenta deshabilitada

---

#### POST /auth/register
```http
POST /api/v1/auth/register
Content-Type: application/json

{
  "email": "nuevo@ejemplo.com",
  "password": "password123",
  "name": "Juan Perez",
  "role": "GUARDIAN"
}
```

**Response (201 Created):**
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "expiresIn": 900,
  "user": {
    "id": "uuid",
    "email": "nuevo@ejemplo.com",
    "name": "Juan Perez",
    "role": "GUARDIAN"
  }
}
```

**Errores:**
- 400: Datos inválidos
- 409: Email ya existe

---

#### POST /auth/refresh
```http
POST /api/v1/auth/refresh
Content-Type: application/json

{
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

**Response (200 OK):**
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "expiresIn": 900
}
```

**Errores:**
- 401: Refresh token inválido o expirado

---

#### POST /auth/logout
```http
POST /api/v1/auth/logout
Authorization: Bearer {accessToken}
```

**Response (204 No Content)**

---

#### GET /auth/me
```http
GET /api/v1/auth/me
Authorization: Bearer {accessToken}
```

**Response (200 OK):**
```json
{
  "id": "uuid",
  "email": "usuario@ejemplo.com",
  "name": "Juan Perez",
  "role": "ADMIN",
  "status": "ACTIVE",
  "createdAt": "2024-01-01T00:00:00Z",
  "lastLoginAt": "2024-01-15T10:30:00Z"
}
```

---

## 3. 📦 Estructura de DTOs de Auth

### 3.1 DTOs del Módulo

```
src/main/java/com/saferoute/common/dto/auth/
├── LoginRequest.java          ← { email, password }
├── LoginResponse.java         ← { accessToken, refreshToken, expiresIn, user }
├── RegisterRequest.java       ← { email, password, name, role }
├── RefreshRequest.java        ← { refreshToken }
├── RefreshResponse.java       ← { accessToken, expiresIn }
└── UserInfoResponse.java     ← { id, email, name, role, status, createdAt, lastLoginAt }
```

### 3.2 Definiciones

**LoginRequest.java:**
```java
public record LoginRequest(
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    String email,
    
    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    String password
) {}
```

**RegisterRequest.java:**
```java
public record RegisterRequest(
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    String email,
    
    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    String password,
    
    @NotBlank(message = "Name is required")
    String name,
    
    @NotNull(message = "Role is required")
    UserRole role
) {}
```

**LoginResponse.java:**
```java
public record LoginResponse(
    String accessToken,
    String refreshToken,
    Integer expiresIn,
    UserInfoResponse user
) {}
```

---

## 4. 🎫 Estructura del JWT Token

### 4.1 Access Token

```json
{
  "header": {
    "alg": "HS256",
    "typ": "JWT"
  },
  "payload": {
    "sub": "uuid-del-usuario",
    "email": "usuario@ejemplo.com",
    "role": "ADMIN",
    "type": "access",
    "iat": 1705312800,
    "exp": 1705313700
  },
  "signature": "HMACSHA256(base64UrlEncode(header) + '.' + base64UrlEncode(payload), SECRET_KEY)"
}
```

### 4.2 Refresh Token

```json
{
  "payload": {
    "sub": "uuid-del-usuario",
    "type": "refresh",
    "iat": 1705312800,
    "exp": 1705917600
  }
}
```

### 4.3 Claims del Token

| Claim | Tipo | Descripción | Access | Refresh |
|-------|------|-------------|--------|---------|
| sub | String | User ID (UUID) | ✅ | ✅ |
| email | String | Email del usuario | ✅ | ❌ |
| role | String | Rol del usuario (ADMIN/DRIVER/GUARDIAN) | ✅ | ❌ |
| type | String | "access" o "refresh" | ✅ | ✅ |
| iat | Long | Issued At (timestamp) | ✅ | ✅ |
| exp | Long | Expiration (timestamp) | ✅ | ✅ |

---

## 5. 🏗️ Estructura de Capas (Auth)

### 5.1 Diagrama de Arquitectura

```
controller/auth/
└── AuthController.java           ← Endpoints

adapter/auth/
├── AuthAdapter.java              ← Interfaz
└── AuthAdapterImpl.java          ← Implementación

usecase/auth/
├── LoginUseCase.java             ← Lógica de login
├── RegisterUseCase.java          ← Lógica de registro
├── RefreshTokenUseCase.java      ← Renovación de token
└── LogoutUseCase.java            ← Cerrar sesión

service/
├── AuthService.java              ← Lógica de autenticación (BCrypt, JWT)
├── TokenService.java             ← Generación/validación de tokens
└── UserService.java              ← Gestión de usuarios (ya existe)

common/
├── entity/UserEntity.java        ← Entity de usuario
├── repository/UserRepository.java ← Repository
└── dto/auth/*.java              ← DTOs de auth
```

### 5.2 AuthService

```java
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    /**
     * Autentica usuario con email y password.
     * @throws UnauthorizedException si credenciales son inválidas
     * @throws AccountDisabledException si cuenta no está activa
     */
    public UserEntity authenticate(String email, String password) {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UnauthorizedException("Invalid credentials"));

        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            throw new UnauthorizedException("Invalid credentials");
        }

        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new AccountDisabledException("Account is disabled");
        }

        return user;
    }

    /**
     * Registra un nuevo usuario.
     * @throws EmailAlreadyExistsException si el email ya está registrado
     */
    public UserEntity register(String email, String password, String name, UserRole role) {
        if (userRepository.existsByEmail(email)) {
            throw new EmailAlreadyExistsException("Email already exists");
        }

        UserEntity user = UserEntity.builder()
                .email(email)
                .passwordHash(passwordEncoder.encode(password))
                .name(name)
                .role(role)
                .status(UserStatus.ACTIVE)
                .build();

        return userRepository.save(user);
    }
}
```

### 5.3 TokenService

```java
@Service
@RequiredArgsConstructor
public class TokenService {

    private final JwtProperties jwtProperties;

    /**
     * Genera un access token JWT.
     */
    public String generateAccessToken(UserEntity user) {
        return Jwts.builder()
                .subject(user.getId().toString())
                .claim("email", user.getEmail())
                .claim("role", user.getRole().name())
                .claim("type", "access")
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + jwtProperties.getAccessExpiration()))
                .signWith(jwtProperties.getSecretKey())
                .compact();
    }

    /**
     * Genera un refresh token JWT.
     */
    public String generateRefreshToken(UserEntity user) {
        return Jwts.builder()
                .subject(user.getId().toString())
                .claim("type", "refresh")
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + jwtProperties.getRefreshExpiration()))
                .signWith(jwtProperties.getSecretKey())
                .compact();
    }

    /**
     * Valida un token JWT.
     * @throws JwtException si el token es inválido o expirado
     */
    public Claims validateToken(String token) {
        return Jwts.parser()
                .verifyWith(jwtProperties.getSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Extrae el user ID del token.
     */
    public UUID getUserIdFromToken(String token) {
        Claims claims = validateToken(token);
        return UUID.fromString(claims.getSubject());
    }

    /**
     * Verifica si el token es de tipo access.
     */
    public boolean isAccessToken(String token) {
        Claims claims = validateToken(token);
        return "access".equals(claims.get("type"));
    }
}
```

### 5.4 LoginUseCase

```java
@Component
@Slf4j
public class LoginUseCase extends UseCaseAdvance<LoginRequest, LoginResponse> {

    private final AuthService authService;
    private final TokenService tokenService;

    @Override
    protected void preConditions(LoginRequest request) {
        if (request.email() == null || request.email().isBlank()) {
            throw new IllegalArgumentException("Email is required");
        }
        if (request.password() == null || request.password().isBlank()) {
            throw new IllegalArgumentException("Password is required");
        }
    }

    @Override
    protected LoginResponse core(LoginRequest request) {
        // 1. Autenticar usuario
        UserEntity user = authService.authenticate(request.email(), request.password());

        // 2. Generar tokens
        String accessToken = tokenService.generateAccessToken(user);
        String refreshToken = tokenService.generateRefreshToken(user);

        // 3. Devolver respuesta
        return new LoginResponse(
                accessToken,
                refreshToken,
                900, // 15 minutos en segundos
                toUserInfoResponse(user)
        );
    }

    private UserInfoResponse toUserInfoResponse(UserEntity user) {
        return new UserInfoResponse(
                user.getId(),
                user.getEmail(),
                user.getName(),
                user.getRole(),
                user.getStatus(),
                user.getCreatedAt(),
                user.getLastLoginAt()
        );
    }
}
```

---

## 6. ⚙️ Configuración de Security

### 6.1 Dependencias Requeridas (pom.xml)

```xml
<dependencies>
    <!-- Spring Security -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-security</artifactId>
    </dependency>

    <!-- JWT -->
    <dependency>
        <groupId>io.jsonwebtoken</groupId>
        <artifactId>jjwt-api</artifactId>
        <version>0.12.3</version>
    </dependency>
    <dependency>
        <groupId>io.jsonwebtoken</groupId>
        <artifactId>jjwt-impl</artifactId>
        <version>0.12.3</version>
        <scope>runtime</scope>
    </dependency>
    <dependency>
        <groupId>io.jsonwebtoken</groupId>
        <artifactId>jjwt-jackson</artifactId>
        <version>0.12.3</version>
        <scope>runtime</scope>
    </dependency>
</dependencies>
```

### 6.2 application.yml

```yaml
jwt:
  secret: "${JWT_SECRET:super-secret-key-must-be-at-least-256-bits-long-for-hs256}"
  access-expiration: 900000    # 15 minutos en milisegundos
  refresh-expiration: 604800000 # 7 días en milisegundos
```

### 6.3 JwtProperties

```java
@Component
@ConfigurationProperties(prefix = "jwt")
@Getter
@Setter
public class JwtProperties {
    
    private String secret;
    private long accessExpiration;
    private long refreshExpiration;

    public Key getSecretKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }
}
```

### 6.4 SecurityConfig

```java
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtFilter;
    private final AuthenticationEntryPoint authenticationEntryPoint;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/v1/auth/**").permitAll()
                .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint(authenticationEntryPoint)
            );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
```

### 6.5 JwtAuthenticationFilter

```java
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final TokenService tokenService;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                    HttpServletResponse response, 
                                    FilterChain filterChain) 
            throws ServletException, IOException {
        
        // 1. Extraer token del header
        String authHeader = request.getHeader("Authorization");
        
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);

        try {
            // 2. Validar token
            if (tokenService.isAccessToken(token)) {
                UUID userId = tokenService.getUserIdFromToken(token);
                
                // 3. Cargar usuario
                UserDetails userDetails = userDetailsService.loadUserByUsername(userId.toString());
                
                // 4. Establecer autenticación
                UsernamePasswordAuthenticationToken authentication = 
                        new UsernamePasswordAuthenticationToken(
                                userDetails, 
                                null, 
                                userDetails.getAuthorities()
                        );
                
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception e) {
            // Token inválido - continuar sin autenticación
            log.warn("JWT validation failed: {}", e.getMessage());
        }

        filterChain.doFilter(request, response);
    }
}
```

---

## 7. 🏷️ Roles y Permisos

### 7.1 Roles Definidos

| Rol | Descripción | Endpoints Accesibles |
|-----|-------------|---------------------|
| ADMIN | Administrador del sistema | Todos |
| DRIVER | Conductor de ruta | Rutas asignadas, tracking, eventos |
| GUARDIAN | Acudiente/Padre | Info de estudiantes asociados, notificaciones |

### 7.2 Mapping de Roles en Security

```java
// En JwtAuthenticationFilter o UserDetailsService
public record UserPrincipal(
    UUID id,
    String email,
    String name,
    UserRole role,
    Set<SimpleGrantedAuthority> authorities
) implements UserDetails {

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    public static UserPrincipal create(UserEntity user) {
        Set<SimpleGrantedAuthority> authorities = Set.of(
                new SimpleGrantedAuthority("ROLE_" + user.getRole().name())
        );
        
        return new UserPrincipal(
                user.getId(),
                user.getEmail(),
                user.getName(),
                user.getRole(),
                authorities
        );
    }
}
```

---

## 8. ⚠️ Consideraciones de Seguridad

### 8.1 Contraseñas

- **NUNCA** almacenar passwords en texto plano
- Usar **BCrypt** con factor de costo 10-12
- Validar complejidad mínima (8 caracteres)
- No permitir passwords comunes o comprometidos

### 8.2 Tokens JWT

- **NUNCA** exponer información sensible en tokens
- Usar HTTPS exclusivamente en producción
- Implementar blacklist de tokens (para logout)
- Renovar tokens antes de expiración (refresh)

### 8.3 Sesiones

- Implementar rate limiting en `/auth/login`
- Bloquear cuenta después de N intentos fallidos
- Tiempo de expiración corto para access token
- Usar cookies HttpOnly para almacenar tokens

### 8.4 Excepciones Personalizadas

```java
// Custom exceptions
public class UnauthorizedException extends RuntimeException {
    public UnauthorizedException(String message) { super(message); }
}

public class AccountDisabledException extends RuntimeException {
    public AccountDisabledException(String message) { super(message); }
}

public class EmailAlreadyExistsException extends RuntimeException {
    public EmailAlreadyExistsException(String message) { super(message); }
}

public class InvalidTokenException extends RuntimeException {
    public InvalidTokenException(String message) { super(message); }
}
```

### 8.5 GlobalExceptionHandler

```java
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorized(UnauthorizedException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponse("UNAUTHORIZED", ex.getMessage()));
    }

    @ExceptionHandler(AccountDisabledException.class)
    public ResponseEntity<ErrorResponse> handleAccountDisabled(AccountDisabledException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new ErrorResponse("ACCOUNT_DISABLED", ex.getMessage()));
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleEmailExists(EmailAlreadyExistsException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ErrorResponse("EMAIL_EXISTS", ex.getMessage()));
    }
}
```

---

## 9. 📋 Resumen de Archivos a Crear

```
src/main/java/com/saferoute/
├── common/
│   ├── dto/auth/
│   │   ├── LoginRequest.java
│   │   ├── LoginResponse.java
│   │   ├── RegisterRequest.java
│   │   ├── RefreshRequest.java
│   │   ├── RefreshResponse.java
│   │   └── UserInfoResponse.java
│   ├── exception/
│   │   ├── UnauthorizedException.java
│   │   ├── AccountDisabledException.java
│   │   ├── EmailAlreadyExistsException.java
│   │   └── InvalidTokenException.java
│   └── config/
│       ├── JwtProperties.java
│       ├── SecurityConfig.java
│       └── JwtAuthenticationFilter.java
│
├── service/
│   ├── AuthService.java
│   └── TokenService.java
│
├── usecase/auth/
│   ├── LoginUseCase.java
│   ├── RegisterUseCase.java
│   ├── RefreshTokenUseCase.java
│   └── LogoutUseCase.java
│
├── adapter/auth/
│   ├── AuthAdapter.java
│   └── AuthAdapterImpl.java
│
└── controller/auth/
    └── AuthController.java
```

---

## 10. 🔗 Referencias

- [Architecture Context](./contextArchitecture.md)
- [Context General](./contextGeneral.md)
- [Context Database](./contextDatabase.md)
