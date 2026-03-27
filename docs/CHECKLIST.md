# SafeRoute - Checklist de Implementación

## 📋 Resumen de Módulos

| # | Módulo | Estado | Prioridad |
|---|--------|--------|-----------|
| 1 | **Auth** (JWT, Login, Register, Refresh) | 🔴 Por hacer | 🔴 Alta |
| 2 | **Users** (CRUD) | 🟡 Parcial (falta Controller/Adapter) | 🔴 Alta |
| 3 | **Drivers** (CRUD) | 🟡 Parcial | 🔴 Alta |
| 4 | **Students** (CRUD + GPS) | 🟡 Parcial | 🔴 Alta |
| 5 | **Guardians** (CRUD) | 🟡 Parcial | 🔴 Alta |
| 6 | **StudentGuardian** (Relaciones) | 🔴 Por hacer | 🔴 Alta |
| 7 | **Routes** (CRUD + Gestión) | 🔴 Por hacer | 🔴 Alta |
| 8 | **Stops** (CRUD + GPS) | 🔴 Por hacer | 🔴 Alta |
| 9 | **GPS Tracking** (Posiciones) | 🔴 Por hacer | 🟡 Media |
| 10 | **StudentEvents** (BOARD, ARRIVAL, DROP) | 🔴 Por hacer | 🟡 Media |
| 11 | **Observations** (Reportes) | 🔴 Por hacer | 🟡 Media |
| 12 | **Notifications** (FCM) | 🔴 Por hacer | 🟡 Media |
| 13 | **Security** (Roles, Permisos) | 🔴 Por hacer | 🔴 Alta |
| 14 | **API Documentation** (Swagger) | 🔴 Por hacer | 🟢 Baja |

---

## 🎯 Módulo 1: AUTH (Autenticación)

### Endpoints
- [ ] POST `/api/v1/auth/login` - Iniciar sesión
- [ ] POST `/api/v1/auth/register` - Registrarse
- [ ] POST `/api/v1/auth/refresh` - Renovar token
- [ ] POST `/api/v1/auth/logout` - Cerrar sesión
- [ ] GET `/api/v1/auth/me` - Usuario actual

### Componentes Necesarios
- [ ] **JWT Configuration** - application.properties ✅ (hecho)
- [ ] **JwtService** - Generar/validar tokens
- [ ] **JwtAuthenticationFilter** - Interceptor de requests
- [ ] **SecurityConfig** - Configuración de seguridad
- [ ] **AuthController** - Endpoints REST
- [ ] **AuthAdapter** - Adaptador de requests
- [ ] **LoginUseCase** - Lógica de login
- [ ] **RegisterUseCase** - Lógica de registro
- [ ] **RefreshTokenUseCase** - Renovar token

### DTOs Necesarios
- [ ] AuthLoginRequest
- [ ] AuthRegisterRequest
- [ ] AuthResponse
- [ ] RefreshTokenRequest

---

## 👥 Módulo 2: USERS

### Endpoints
- [ ] GET `/api/v1/users` - Listar usuarios
- [ ] GET `/api/v1/users/{id}` - Obtener usuario
- [ ] POST `/api/v1/users` - Crear usuario
- [ ] PUT `/api/v1/users/{id}` - Actualizar usuario
- [ ] DELETE `/api/v1/users/{id}` - Eliminar usuario

### Componentes Necesarios
- [ ] **UserController** ❌ No existe
- [ ] **UserAdapter** ❌ No existe
- [ ] **UserService** ✅ Existe

---

## 🚗 Módulo 3: DRIVERS

### Endpoints
- [ ] GET `/api/v1/drivers` - Listar conductores
- [ ] GET `/api/v1/drivers/{id}` - Obtener conductor
- [ ] POST `/api/v1/drivers` - Crear conductor
- [ ] PUT `/api/v1/drivers/{id}` - Actualizar conductor
- [ ] DELETE `/api/v1/drivers/{id}` - Eliminar conductor

### Componentes Necesarios
- [ ] **DriverController** ❌ No existe
- [ ] **DriverAdapter** ❌ No existe
- [ ] **DriverService** ❌ No existe
- [ ] **DriverDtos** ❌ No existe

---

## 🎓 Módulo 4: STUDENTS

### Endpoints
- [ ] GET `/api/v1/students` - Listar estudiantes
- [ ] GET `/api/v1/students/{id}` - Obtener estudiante
- [ ] POST `/api/v1/students` - Crear estudiante
- [ ] PUT `/api/v1/students/{id}` - Actualizar estudiante
- [ ] DELETE `/api/v1/students/{id}` - Eliminar estudiante

### Componentes Necesarios
- [ ] **StudentController** ❌ No existe
- [ ] **StudentAdapter** ❌ No existe
- [ ] **StudentService** ✅ Existe
- [ ] **StudentDtos** ✅ Existe

---

## 👨‍👩‍👧 Módulo 5: GUARDIANS

### Endpoints
- [ ] GET `/api/v1/guardians` - Listar acudientes
- [ ] GET `/api/v1/guardians/{id}` - Obtener acudiente
- [ ] POST `/api/v1/guardians` - Crear acudiente
- [ ] PUT `/api/v1/guardians/{id}` - Actualizar acudiente
- [ ] DELETE `/api/v1/guardians/{id}` - Eliminar acudiente

### Componentes Necesarios
- [ ] **GuardianController** ❌ No existe
- [ ] **GuardianAdapter** ❌ No existe
- [ ] **GuardianService** ✅ Existe
- [ ] **GuardianDtos** ✅ Existe

---

## 🔗 Módulo 6: STUDENT-GUARDIAN (Relaciones)

### Endpoints
- [ ] POST `/api/v1/students/{studentId}/guardians` - Agregar acudiente
- [ ] DELETE `/api/v1/students/{studentId}/guardians/{guardianId}` - Quitar acudiente
- [ ] GET `/api/v1/students/{studentId}/guardians` - Listar acudientes de estudiante
- [ ] GET `/api/v1/guardians/{guardianId}/students` - Listar estudiantes de acudiente

### Componentes Necesarios
- [ ] **StudentGuardianController** ❌ No existe
- [ ] **StudentGuardianService** ❌ No existe

---

## 🛣️ Módulo 7: ROUTES

### Endpoints
- [ ] GET `/api/v1/routes` - Listar rutas
- [ ] GET `/api/v1/routes/{id}` - Obtener ruta
- [ ] POST `/api/v1/routes` - Crear ruta
- [ ] PUT `/api/v1/routes/{id}` - Actualizar ruta
- [ ] DELETE `/api/v1/routes/{id}` - Eliminar ruta
- [ ] POST `/api/v1/routes/{id}/start` - Iniciar ruta
- [ ] POST `/api/v1/routes/{id}/complete` - Completar ruta
- [ ] POST `/api/v1/routes/{id}/cancel` - Cancelar ruta
- [ ] GET `/api/v1/routes/{id}/stops` - Listar paradas

### Componentes Necesarios
- [ ] **RouteController** ❌ No existe
- [ ] **RouteAdapter** ❌ No existe
- [ ] **RouteService** ❌ No existe
- [ ] **RouteDtos** ❌ No existe

---

## 🛑 Módulo 8: STOPS

### Endpoints
- [ ] GET `/api/v1/stops` - Listar paradas
- [ ] GET `/api/v1/stops/{id}` - Obtener parada
- [ ] POST `/api/v1/stops` - Crear parada
- [ ] PUT `/api/v1/stops/{id}` - Actualizar parada
- [ ] DELETE `/api/v1/stops/{id}` - Eliminar parada
- [ ] PUT `/api/v1/stops/{id}/picked-up` - Marcar recogido
- [ ] PUT `/api/v1/stops/{id}/dropped-off` - Marcar dejado

### Componentes Necesarios
- [ ] **StopController** ❌ No existe
- [ ] **StopAdapter** ❌ No existe
- [ ] **StopService** ❌ No existe
- [ ] **StopDtos** ❌ No existe

---

## 📍 Módulo 9: GPS TRACKING

### Endpoints
- [ ] POST `/api/v1/gps/position` - Enviar posición
- [ ] GET `/api/v1/routes/{routeId}/positions` - Historial de posiciones
- [ ] GET `/api/v1/routes/{routeId}/current-position` - Posición actual

### Componentes Necesarios
- [ ] **GpsController** ❌ No existe
- [ ] **GpsService** ❌ No existe
- [ ] **GpsDtos** ❌ No existe
- [ ] Consultas espaciales en repositorio ✅ Faltan

---

## 📝 Módulo 10: STUDENT EVENTS

### Endpoints
- [ ] POST `/api/v1/events/board` - Registrar subida
- [ ] POST `/api/v1/events/arrival` - Registrar llegada a escuela
- [ ] POST `/api/v1/events/drop` - Registrar dejada en casa
- [ ] GET `/api/v1/students/{studentId}/events` - Historial de eventos
- [ ] GET `/api/v1/routes/{routeId}/events` - Eventos de ruta

### Componentes Necesarios
- [ ] **StudentEventController** ❌ No existe
- [ ] **StudentEventService** ❌ No existe
- [ ] **StudentEventDtos** ❌ No existe

---

## 📸 Módulo 11: OBSERVATIONS

### Endpoints
- [ ] GET `/api/v1/observations` - Listar observaciones
- [ ] GET `/api/v1/observations/{id}` - Obtener observación
- [ ] POST `/api/v1/observations` - Crear observación
- [ ] GET `/api/v1/students/{studentId}/observations` - Observaciones de estudiante

### Componentes Necesarios
- [ ] **ObservationController** ❌ No existe
- [ ] **ObservationService** ❌ No existe
- [ ] **ObservationDtos** ❌ No existe

---

## 🔔 Módulo 12: NOTIFICATIONS

### Endpoints
- [ ] GET `/api/v1/notifications` - Listar notificaciones
- [ ] GET `/api/v1/notifications/{id}` - Obtener notificación
- [ ] PUT `/api/v1/notifications/{id}/read` - Marcar como leída
- [ ] PUT `/api/v1/notifications/read-all` - Marcar todas como leídas

### Componentes Necesarios
- [ ] **NotificationController** ❌ No existe
- [ ] **NotificationService** ❌ No existe
- [ ] **NotificationDtos** ❌ No existe
- [ ] **FcmService** ❌ No existe (Firebase)

---

## 🔒 Módulo 13: SECURITY

### Configuraciones
- [ ] **SecurityConfig** - Configuración de Spring Security
- [ ] **JwtAuthenticationFilter** - Filtro de JWT
- [ ] **JwtService** - Utilidades JWT
- [ ] **CustomUserDetailsService** - Cargar usuarios
- [ ] **AccessDeniedHandler** - Manejo de accesos denegados
- [ ] **AuthenticationEntryPoint** - Manejo de auth fallida

### Roles y Permisos
- [ ] ADMIN - Acceso total
- [ ] DRIVER - Rutas, GPS, eventos
- [ ] GUARDIAN - Solo lectura de sus estudiantes

---

## 📚 Módulo 14: API DOCUMENTATION

### Configuraciones
- [ ] **OpenAPI Config** - Configuración de Swagger
- [ ] **API Info** - Información de la API
- [ ] **Security Schemes** - Definir JWT en Swagger
- [ ] **Annotations** - Documentar endpoints

---

## 📊 Progreso General

```
Módulo              Hecho    Total    Porcentaje
─────────────────────────────────────────────────
✅ Entidades           11      11       100%
✅ Repositorios        11      11       100%
✅ DTOs (User)          2       2       100%
✅ DTOs (Student)       2       2       100%
✅ DTOs (Guardian)      2       2       100%
✅ Services             3      13        23%
❌ Controllers          0      13         0%
❌ Adapters             0      13         0%
❌ UseCases             0      13         0%
❌ Security             0       7         0%
─────────────────────────────────────────────────
```

---

## 🎯 Siguiente: Módulo AUTH

Arrancamos con el módulo de autenticación que incluye:
1. JWT Configuration ✅ (hecho en properties)
2. JwtService
3. JwtAuthenticationFilter
4. SecurityConfig
5. AuthController
6. AuthAdapter
7. Auth UseCases
8. Auth DTOs
