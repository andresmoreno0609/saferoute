# Features Context

<!-- Add feature descriptions and business logic -->

---

## 1. 📋 Visión General de Features

Este documento describe las funcionalidades, reglas de negocio y flujos de cada módulo del sistema SafeRoute.

### 1.1 Módulos del Sistema

| Módulo | Descripción | Prioridad |
|--------|-------------|-----------|
| **auth** | Autenticación y autorización | 🔴 MVP |
| **users** | Gestión de usuarios | 🔴 MVP |
| **students** | Registro de estudiantes | 🔴 MVP |
| **guardians** | Acudientes y relaciones | 🔴 MVP |
| **drivers** | Información de conductores | 🔴 MVP |
| **vehicles** | Gestión de vehículos y documentos | 🔴 MVP |
| **routes** | Gestión de rutas escolares | 🔴 MVP |
| **tracking** | GPS en tiempo real | 🔴 MVP |
| **events** | Eventos del estudiante | 🔴 MVP |
| **nfc** | Sistema de identificación NFC | 🔴 MVP |
| **observations** | Novedades y fotos | 🟡 v2 |
| **notifications** | Push notifications FCM | 🟡 v2 |

---

## 2. 👥 Users Module

### 2.1 Funcionalidades

| Funcionalidad | Descripción | Método HTTP |
|---------------|-------------|-------------|
| Crear usuario | Registrar nuevo usuario en el sistema | POST |
| Actualizar usuario | Modificar datos del usuario | PUT |
| Eliminar usuario | Soft delete (cambiar status a DELETED) | DELETE |
| Buscar por ID | Obtener usuario por UUID | GET |
| Listar usuarios | Obtener todos los usuarios con paginación | GET |
| Buscar por email | Obtener usuario por email | GET |

### 2.2 Reglas de Negocio

- El **email debe ser único** en todo el sistema
- El **password se almacena encriptado** con BCrypt
- El **status** puede ser: `ACTIVE`, `INACTIVE`, `DELETED`
- El **rol** puede ser: `ADMIN`, `DRIVER`, `GUARDIAN`
- Un usuario **soft-deleted** no puede iniciar sesión
- El **email no se puede modificar** después de creado

### 2.3 DTOs

**UserRequest:**
```json
{
  "email": "usuario@ejemplo.com",
  "password": "password123",
  "name": "Juan Perez",
  "role": "ADMIN"
}
```

**UserResponse:**
```json
{
  "id": "uuid",
  "email": "usuario@ejemplo.com",
  "name": "Juan Perez",
  "role": "ADMIN",
  "status": "ACTIVE",
  "createdAt": "2024-01-01T00:00:00Z",
  "lastLoginAt": null
}
```

### 2.4 Endpoints

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST | `/api/v1/users` | Crear usuario |
| GET | `/api/v1/users/{id}` | Obtener por ID |
| GET | `/api/v1/users` | Listar con paginación |
| PUT | `/api/v1/users/{id}` | Actualizar usuario |
| DELETE | `/api/v1/users/{id}` | Eliminar usuario |

---

## 3. 🎓 Students Module

### 3.1 Funcionalidades

| Funcionalidad | Descripción | Método HTTP |
|---------------|-------------|-------------|
| Crear estudiante | Registrar nuevo estudiante | POST |
| Actualizar estudiante | Modificar datos del estudiante | PUT |
| Eliminar estudiante | Soft delete | DELETE |
| Buscar por ID | Obtener estudiante por UUID | GET |
| Listar estudiantes | Obtener todos con paginación | GET |
| Buscar por nombre | Búsqueda por nombre | GET |
| Consultar ubicación | Obtener coordenadas de casa/colegio | GET |

### 3.2 Reglas de Negocio

- La **ubicación de casa** es obligatoria (uso de PostGIS POINT)
- La **ubicación del colegio** es opcional
- Un estudiante **puede tener múltiples acudientes** (relación N:M)
- Al menos **un acudiente debe ser contacto de emergencia**
- El estudiante **no tiene usuario propio** - es gestionado por admin/guardian

### 3.3 Datos del Estudiante

| Campo | Tipo | Requerido | Descripción |
|-------|------|-----------|-------------|
| name | String | ✅ | Nombre completo |
| address | String | ✅ | Dirección de residencia |
| location | Point (PostGIS) | ✅ | Coordenadas de casa |
| schoolName | String | ❌ | Nombre del colegio |
| schoolLocation | Point (PostGIS) | ❌ | Coordenadas del colegio |
| addressGeocoded | Boolean | ✅ | ¿Dirección geocodificada? |
| birthDate | LocalDate | ❌ | Fecha de nacimiento |
| grade | String | ❌ | Grado escolar |
| emergencyContact | String | ❌ | Contacto de emergencia |
| emergencyPhone | String | ❌ | Teléfono de emergencia |
| medicalInfo | String | ❌ | Info médica (alergias, medicamentos) |
| photoUrl | String | ❌ | URL de foto |
| studentCode | String | ❌ | Código interno del estudiante |

### 3.4 DTOs

**StudentRequest:**
```json
{
  "name": "Juan Perez",
  "address": "Calle 123 #45-67, Bogotá",
  "homeLatitude": 4.7110,
  "homeLongitude": -74.0721,
  "schoolName": "Colegio San Ignacio",
  "schoolLatitude": 4.7200,
  "schoolLongitude": -74.0800,
  "grade": "1°",
  "birthDate": "2015-05-15",
  "emergencyContact": "María Perez",
  "emergencyPhone": "+573001234567",
  "medicalInfo": "Alergia a frutos secos",
  "studentCode": "EST-001"
}
```

**StudentResponse:**
```json
{
  "id": "uuid",
  "name": "Juan Perez",
  "address": "Calle 123 #45-67, Bogotá",
  "homeLatitude": 4.7110,
  "homeLongitude": -74.0721,
  "schoolName": "Colegio San Ignacio",
  "schoolLatitude": 4.7200,
  "schoolLongitude": -74.0800,
  "addressGeocoded": true,
  "birthDate": "2015-05-15",
  "grade": "1°",
  "emergencyContact": "María Perez",
  "emergencyPhone": "+573001234567",
  "medicalInfo": "Alergia a frutos secos",
  "photoUrl": "https://storage.com/photos/student.jpg",
  "studentCode": "EST-001",
  "createdAt": "2024-01-01T00:00:00Z",
  "updatedAt": "2024-01-01T00:00:00Z"
}
```

### 3.5 Endpoints

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST | `/api/v1/students` | Crear estudiante |
| GET | `/api/v1/students/{id}` | Obtener por ID |
| GET | `/api/v1/students` | Listar con paginación |
| PUT | `/api/v1/students/{id}` | Actualizar estudiante |
| DELETE | `/api/v1/students/{id}` | Eliminar estudiante |

---

## 4. 👨‍👩‍👧 Guardians Module

### 4.1 Funcionalidades

| Funcionalidad | Descripción | Método HTTP |
|---------------|-------------|-------------|
| Crear acudiente | Registrar nuevo acudiente | POST |
| Actualizar acudiente | Modificar datos | PUT |
| Eliminar acudiente | Soft delete | DELETE |
| Buscar por ID | Obtener por UUID | GET |
| Listar acudientes | Obtener todos | GET |
| Asociar estudiante | Vincular estudiante con acudiente | POST |
| Desasociar estudiante | Desvincular relación | DELETE |
| Registrar FCM Token | Guardar token para notificaciones | POST |
| Obtener estudiantes | Lista de estudiantes asociados | GET |

### 4.2 Reglas de Negocio

- Un **acudiente puede tener múltiples estudiantes** asociados
- El **teléfono es único** por acudiente
- El **email es opcional** pero debe ser único si se proporciona
- El **FCM token** se usa para notificaciones push
- La **relación** puede ser: `father`, `mother`, `guardian`, `other`
- Un acudiente puede ser **contacto de emergencia** o no
- Un acudiente puede **recibir notificaciones** o no

### 4.3 Relación Estudiante-Acudiente

```
┌─────────────────┐       ┌──────────────────────┐       ┌─────────────────┐
│    Students     │◄──────│  Student_Guardians   │──────►│    Guardians    │
├─────────────────┤       ├──────────────────────┤       ├─────────────────┤
│ id (PK)         │       │ student_id (FK)     │       │ id (PK)         │
│ name            │       │ guardian_id (FK)    │       │ name            │
│ address         │       │ relationship        │       │ phone           │
│ location        │       │ is_emergency_contact│       │ email           │
└─────────────────┘       │ notify_events        │       │ fcm_token       │
                         └──────────────────────┘       └─────────────────┘
```

### 4.4 DTOs

**GuardianRequest:**
```json
{
  "name": "Maria Perez",
  "phone": "+573001234567",
  "email": "maria@ejemplo.com"
}
```

**GuardianResponse:**
```json
{
  "id": "uuid",
  "name": "Maria Perez",
  "phone": "+573001234567",
  "email": "maria@ejemplo.com",
  "createdAt": "2024-01-01T00:00:00Z"
}
```

**AssociateStudentRequest:**
```json
{
  "studentId": "uuid",
  "relationship": "mother",
  "isEmergencyContact": true,
  "notifyEvents": true
}
```

### 4.5 Endpoints

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST | `/api/v1/guardians` | Crear acudiente |
| GET | `/api/v1/guardians/{id}` | Obtener por ID |
| GET | `/api/v1/guardians` | Listar |
| PUT | `/api/v1/guardians/{id}` | Actualizar |
| DELETE | `/api/v1/guardians/{id}` | Eliminar |
| POST | `/api/v1/guardians/{id}/students` | Asociar estudiante |
| DELETE | `/api/v1/guardians/{id}/students/{studentId}` | Desasociar |
| PUT | `/api/v1/guardians/{id}/fcm-token` | Actualizar FCM token |

---

## 5. 🚗 Drivers Module

### 5.1 Funcionalidades

| Funcionalidad | Descripción | Método HTTP |
|---------------|-------------|-------------|
| Crear conductor | Registrar conductor con vehículo | POST |
| Actualizar conductor | Modificar datos del conductor | PUT |
| Eliminar conductor | Soft delete | DELETE |
| Buscar por ID | Obtener por UUID | GET |
| Listar conductors | Obtener todos | GET |
| Consultar disponibilidad | Verificar si conductor puede trabajar | GET |
| Verificar documentos | Revisar estado de documentos | GET |

### 5.2 Reglas de Negocio

- Un conductor **relaciona 1:1 con Users**
- Un conductor **debe estar verificado por ADMIN** (`isVerified`) para poder trabajar
- El conductor debe tener un **vehículo asignado**
- El conductor debe tener una **licencia válida** (documento activo y no vencido)
- El vehículo del conductor debe tener **todos los documentos obligatorios** (SOAP, SEGURO, TECNOMECANICA, TARJETA_PROPIEDAD)

### 5.3 Datos del Conductor

| Campo | Tipo | Requerido | Descripción |
|-------|------|-----------|-------------|
| name | String | ✅ | Nombre completo |
| phone | String | ✅ | Teléfono de contacto |
| documentNumber | String | ❌ | Número de identificación |
| birthDate | LocalDate | ❌ | Fecha de nacimiento |
| address | String | ❌ | Dirección de residencia |
| licenseNumber | String | ❌ | Número de licencia |
| licenseCategory | String | ❌ | Categoría (A, B, C, etc.) |
| licenseExpirationDate | LocalDate | ❌ | Vencimiento de licencia |
| emergencyContact | String | ❌ | Contacto de emergencia |
| emergencyPhone | String | ❌ | Teléfono de emergencia |
| yearsExperience | Integer | ❌ | Años de experiencia |
| photoUrl | String | ❌ | URL de foto |
| bankName | String | ❌ | Banco para pagos |
| bankAccount | String | ❌ | Número de cuenta |
| vehicleId | UUID | ❌ | Vehículo asignado |
| isVerified | Boolean | ✅ | Verificado por ADMIN |

### 5.4 DTOs

**DriverRequest:**
```json
{
  "userId": "uuid-del-usuario",
  "name": "Carlos Rodriguez",
  "phone": "+573009876543",
  "documentNumber": "12345678",
  "birthDate": "1985-03-15",
  "address": "Calle 123 #45-67, Bogotá",
  "licenseNumber": "Lic-123456",
  "licenseCategory": "B",
  "licenseExpirationDate": "2028-03-15",
  "emergencyContact": "Ana Rodriguez",
  "emergencyPhone": "+573009876999",
  "yearsExperience": 5,
  "bankName": "Banco de Bogotá",
  "bankAccount": "1234567890"
}
```

**DriverResponse:**
```json
{
  "id": "uuid",
  "userId": "uuid-del-usuario",
  "name": "Carlos Rodriguez",
  "phone": "+573009876543",
  "documentNumber": "12345678",
  "birthDate": "1985-03-15",
  "address": "Calle 123 #45-67, Bogotá",
  "licenseNumber": "Lic-123456",
  "licenseCategory": "B",
  "licenseExpirationDate": "2028-03-15",
  "emergencyContact": "Ana Rodriguez",
  "emergencyPhone": "+573009876999",
  "yearsExperience": 5,
  "photoUrl": "https://storage.com/photos/driver.jpg",
  "bankName": "Banco de Bogotá",
  "bankAccount": "1234567890",
  "vehicleId": "uuid-del-vehiculo",
  "isVerified": true,
  "createdAt": "2024-01-01T00:00:00Z"
}
```

### 5.4 Endpoints

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST | `/api/v1/drivers` | Crear conductor |
| GET | `/api/v1/drivers/{id}` | Obtener por ID |
| GET | `/api/v1/drivers` | Listar |
| PUT | `/api/v1/drivers/{id}` | Actualizar |
| DELETE | `/api/v1/drivers/{id}` | Eliminar |
| GET | `/api/v1/drivers/{id}/availability` | Consultar disponibilidad |
| GET | `/api/v1/drivers/{id}/documents` | Ver documentos del conductor |
| PUT | `/api/v1/drivers/{id}/verify` | Verificar conductor (Admin) |

---

## 6. 🚐 Vehicles Module

### 6.1 Funcionalidades

| Funcionalidad | Descripción | Método HTTP |
|---------------|-------------|-------------|
| Crear vehículo | Registrar nuevo vehículo | POST |
| Actualizar vehículo | Modificar datos del vehículo | PUT |
| Eliminar vehículo | Soft delete | DELETE |
| Buscar por ID | Obtener vehículo por UUID | GET |
| Listar vehículos | Obtener todos los vehículos | GET |
| Agregar documento | Agregar documento al vehículo | POST |
| Listar documentos | Ver documentos del vehículo | GET |
| Verificar documento | Aprobar documento (Admin) | POST |
| Rechazar documento | Rechazar documento (Admin) | POST |
| Eliminar documento | Eliminar documento (soft delete) | DELETE |

### 6.2 Documentos Obligatorios del Vehículo

| Documento | Tipo | Vigencia | Descripción |
|-----------|------|----------|-------------|
| SOAP | vehicle_documents | Fecha fin | Seguro Obligatorio de Accidentes de Tránsito |
| SEGURO | vehicle_documents | Fecha fin | Seguro de responsabilidad civil |
| TECNOMECANICA | vehicle_documents | Fecha fin | Revisión técnico-mecánica |
| TARJETA_PROPIEDAD | vehicle_documents | NULL | Sin vencimiento |

### 6.3 Reglas de Negocio

- La **placa del vehículo es única** en todo el sistema
- Solo un **documento activo por tipo** (al crear nuevo, anterior se inactiva)
- Los documentos pueden tener **fecha de fin NULL** (sin vencimiento)
- Para que un conductor pueda trabajar, el vehículo debe tener **todos los documentos activos y vigentes**
- Los documentos requieren **verificación manual por ADMIN**

### 6.4 Datos del Vehículo

| Campo | Tipo | Requerido | Descripción |
|-------|------|-----------|-------------|
| plate | String | ✅ | Placa única del vehículo |
| model | String | ❌ | Modelo del vehículo |
| brand | String | ❌ | Marca del vehículo |
| color | String | ❌ | Color del vehículo |
| capacity | Integer | ❌ | Capacidad de pasajeros |

### 6.5 DTOs

**VehicleRequest:**
```json
{
  "plate": "ABC-123",
  "model": "Toyota Hiace",
  "brand": "Toyota",
  "color": "Blanco",
  "capacity": 15
}
```

**VehicleResponse:**
```json
{
  "id": "uuid",
  "plate": "ABC-123",
  "model": "Toyota Hiace",
  "brand": "Toyota",
  "color": "Blanco",
  "capacity": 15,
  "createdAt": "2024-01-01T00:00:00Z"
}
```

**VehicleDocumentRequest:**
```json
{
  "documentType": "SOAP",
  "fileUrl": "https://storage.com/docs/soap.pdf",
  "startDate": "2026-01-01",
  "endDate": "2027-01-01"
}
```

### 6.6 Endpoints

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST | `/api/v1/vehicles` | Crear vehículo |
| GET | `/api/v1/vehicles/{id}` | Obtener por ID |
| GET | `/api/v1/vehicles` | Listar |
| PUT | `/api/v1/vehicles/{id}` | Actualizar |
| DELETE | `/api/v1/vehicles/{id}` | Eliminar |
| GET | `/api/v1/vehicles/{id}/documents` | Listar documentos |
| POST | `/api/v1/vehicles/{id}/documents` | Agregar documento |
| POST | `/api/v1/vehicles/{id}/documents/{docId}/verify` | Verificar documento |
| POST | `/api/v1/vehicles/{id}/documents/{docId}/reject` | Rechazar documento |
| DELETE | `/api/v1/vehicles/{id}/documents/{docId}` | Eliminar documento |

---

## 7. 🛣️ Routes Module

### 6.1 Funcionalidades

| Funcionalidad | Descripción | Método HTTP |
|---------------|-------------|-------------|
| Crear ruta | Nueva ruta con conductor y paradas | POST |
| Iniciar ruta | Cambiar status a IN_PROGRESS | POST |
| Finalizar ruta | Cambiar status a COMPLETED | POST |
| Cancelar ruta | Cambiar status a CANCELLED | POST |
| Actualizar ruta | Modificar datos | PUT |
| Eliminar ruta | Soft delete | DELETE |
| Buscar por ID | Obtener ruta | GET |
| Listar rutas | Obtener todas con filtros | GET |
| Agregar parada | Añadir estudiante a la ruta | POST |
| Reordenar paradas | Cambiar orden de paradas | PUT |
| Eliminar parada | Quitar estudiante de la ruta | DELETE |

### 6.2 Reglas de Negocio

- Una ruta **pertenece a un solo conductor**
- El **conductor solo puede tener UNA ruta activa** a la vez
- Las **paradas tienen un orden secuencial** (1, 2, 3...)
- Una **parada no puede repetirse** para el mismo estudiante en la misma ruta
- El **status** de la ruta puede ser: `PENDING`, `IN_PROGRESS`, `COMPLETED`, `CANCELLED`
- La **fecha programada** es obligatoria
- Los **horarios de inicio/fin** se registran automáticamente

### 6.3 Estados de la Ruta

```
PENDING ──► IN_PROGRESS ──► COMPLETED
    │           │
    └──────────► CANCELLED
```

### 6.4 DTOs

**RouteRequest:**
```json
{
  "name": "Ruta Mañana - Sector Norte",
  "driverId": "uuid",
  "scheduledDate": "2024-01-15",
  "notes": "Recoger en zona residencial"
}
```

**RouteResponse:**
```json
{
  "id": "uuid",
  "name": "Ruta Mañana - Sector Norte",
  "driverId": "uuid",
  "driverName": "Carlos Rodriguez",
  "status": "PENDING",
  "scheduledDate": "2024-01-15",
  "startTime": null,
  "endTime": null,
  "notes": "Recoger en zona residencial",
  "stops": [],
  "createdAt": "2024-01-01T00:00:00Z"
}
```

**StopRequest:**
```json
{
  "studentId": "uuid",
  "order": 1,
  "latitude": 4.7110,
  "longitude": -74.0721
}
```

### 6.5 Endpoints

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST | `/api/v1/routes` | Crear ruta |
| GET | `/api/v1/routes/{id}` | Obtener por ID |
| GET | `/api/v1/routes` | Listar con filtros |
| PUT | `/api/v1/routes/{id}` | Actualizar |
| DELETE | `/api/v1/routes/{id}` | Eliminar |
| POST | `/api/v1/routes/{id}/start` | Iniciar ruta |
| POST | `/api/v1/routes/{id}/finish` | Finalizar ruta |
| POST | `/api/v1/routes/{id}/cancel` | Cancelar ruta |
| POST | `/api/v1/routes/{id}/stops` | Agregar parada |
| PUT | `/api/v1/routes/{id}/stops/reorder` | Reordenar |
| DELETE | `/api/v1/routes/{id}/stops/{stopId}` | Eliminar parada |

---

## 7. 📍 Tracking Module (GPS)

### 7.1 Funcionalidades

| Funcionalidad | Descripción | Método HTTP |
|---------------|-------------|-------------|
| Enviar posición | Registrar coordenadas GPS | POST |
| Obtener posición actual | Última posición del vehículo | GET |
| Historial de posiciones | Lista de posiciones de una ruta | GET |

### 7.2 Reglas de Negocio

- Las posiciones GPS se registran **cada 10 segundos** (configurable)
- Cada posición debe tener: **latitud, longitud, timestamp**
- **Opcional**: velocidad, heading, precisión
- Las posiciones se asocian a una **ruta específica**
- Solo el **conductor asignado** puede enviar posiciones

### 7.3 Flujo de Tracking

```
1. Conductor inicia ruta
2. App móvil envía posición cada 10 segundos
   POST /api/v1/tracking/positions
   {
     "routeId": "uuid",
     "latitude": 4.7110,
     "longitude": -74.0721,
     "timestamp": "2024-01-15T07:30:00Z",
     "speed": 45.5,
     "heading": 180.0,
     "accuracy": 5.0
   }
3. Backend guarda posición
4. Frontend consulta posición actual
```

### 7.4 DTOs

**GPSPositionRequest:**
```json
{
  "routeId": "uuid",
  "latitude": 4.7110,
  "longitude": -74.0721,
  "timestamp": "2024-01-15T07:30:00Z",
  "speed": 45.5,
  "heading": 180.0,
  "accuracy": 5.0
}
```

**GPSPositionResponse:**
```json
{
  "id": "uuid",
  "routeId": "uuid",
  "latitude": 4.7110,
  "longitude": -74.0721,
  "timestamp": "2024-01-15T07:30:00Z",
  "speed": 45.5,
  "heading": 180.0
}
```

### 7.5 Endpoints

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST | `/api/v1/tracking/positions` | Enviar posición |
| GET | `/api/v1/tracking/routes/{routeId}/current` | Posición actual |
| GET | `/api/v1/tracking/routes/{routeId}/history` | Historial |

---

## 8. 🎫 Events Module (Student Events)

### 8.1 Funcionalidades

| Funcionalidad | Descripción | Método HTTP |
|---------------|-------------|-------------|
| Registrar evento | Crear evento del estudiante | POST |
| Consultar eventos | Lista de eventos por estudiante/ruta | GET |
| Último evento | Obtener el último evento de un estudiante | GET |

### 8.2 Tipos de Eventos

| Evento | Código | Descripción | Notificación |
|--------|--------|-------------|---------------|
| BOARD | 1 | Estudiante sube al vehículo | ✅ Acudiente |
| ARRIVAL | 2 | Llega al colegio | ✅ Acudiente |
| DROP | 3 | Regresa a casa | ✅ Acudiente |

### 8.3 Reglas de Negocio

- **La tabla es inmutable** (INSERT ONLY - no UPDATE/DELETE)
- Los eventos se registran en **orden cronológico**
- Un estudiante **no puede tener DROP antes de BOARD** en la misma ruta
- Cada evento incluye **ubicación** donde ocurrió
- Los eventos **generan notificaciones** al acudiente

### 8.4 Secuencia de Eventos

```
Ruta Iniciada
    │
    ▼
Estudiante 1: BOARD ──► ARRIVAL ──► DROP
    │
    ▼
Estudiante 2: BOARD ──► ARRIVAL ──► DROP
    │
    ▼
Ruta Finalizada
```

### 8.5 DTOs

**StudentEventRequest:**
```json
{
  "studentId": "uuid",
  "eventType": "BOARD",
  "latitude": 4.7110,
  "longitude": -74.0721,
  "notes": "El estudiante estaba esperando en la puerta"
}
```

**StudentEventResponse:**
```json
{
  "id": "uuid",
  "studentId": "uuid",
  "studentName": "Juan Perez",
  "eventType": "BOARD",
  "location": { "lat": 4.7110, "lng": -74.0721 },
  "timestamp": "2024-01-15T07:30:00Z",
  "notes": "El estudiante estaba esperando en la puerta"
}
```

### 8.6 Endpoints

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST | `/api/v1/events` | Registrar evento |
| GET | `/api/v1/events/student/{studentId}` | Eventos por estudiante |
| GET | `/api/v1/events/route/{routeId}` | Eventos de una ruta |
| GET | `/api/v1/events/student/{studentId}/route/{routeId}/latest` | Último evento |

---

## 8. 📱 NFC Module (Student NFC)

### 8.1 Funcionalidades

| Funcionalidad | Descripción | Método HTTP |
|---------------|-------------|-------------|
| Asignar NFC | Asignar tarjeta NFC a estudiante | POST |
| Obtener NFC activo | Ver NFC activo del estudiante | GET |
| Desactivar NFC | Desactivar NFC del estudiante | DELETE |
| Ver historial | Ver historial de NFCs del estudiante | GET |
| Escanear NFC | Detectar estudiante por NFC | POST |

### 8.2 Reglas de Negocio

- Solo **UN NFC activo** por estudiante a la vez
- Al asignar nuevo NFC, el **anterior se inactiva automáticamente**
- Se mantiene **histórico de todos los NFCs** (inactivos)
- El **NFC UID debe ser único** en todo el sistema
- Un NFC puede tener **fecha de desactivación** cuando se reemplaza

### 8.3 Datos del NFC

| Campo | Tipo | Descripción |
|-------|------|-------------|
| id | UUID | Identificador único |
| studentId | UUID | Estudiante asignado |
| nfcUid | String | UID único de la tarjeta NFC |
| isActive | Boolean | ¿NFC activo? |
| assignedAt | Timestamp | Fecha de asignación |
| deactivatedAt | Timestamp | Fecha de desactivación (nullable) |
| assignedBy | UUID | Usuario que asignó |
| notes | String | Notas adicionales |

### 8.4 DTOs

**AssignNfcRequest:**
```json
{
  "nfcUid": "ABC123456789",
  "notes": "Tarjeta asignada el 06/04/2026"
}
```

**NfcScanRequest:**
```json
{
  "nfcUid": "ABC123456789"
}
```

**StudentNfcResponse:**
```json
{
  "id": "uuid",
  "studentId": "uuid",
  "studentName": "Juan Perez",
  "nfcUid": "ABC123456789",
  "isActive": true,
  "assignedAt": "2026-04-06T10:00:00",
  "deactivatedAt": null,
  "assignedBy": "uuid",
  "notes": "Tarjeta asignada el 06/04/2026",
  "createdAt": "2026-04-06T10:00:00",
  "updatedAt": "2026-04-06T10:00:00"
}
```

### 8.5 Endpoints

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST | `/api/v1/students/{id}/nfc` | Asignar NFC |
| GET | `/api/v1/students/{id}/nfc` | Obtener NFC activo |
| DELETE | `/api/v1/students/{id}/nfc` | Desactivar NFC |
| GET | `/api/v1/students/{id}/nfc/history` | Ver historial |
| POST | `/api/v1/nfc/scan` | Escanear NFC |

---

## 9. 📝 Observations Module

### 9.1 Funcionalidades

| Funcionalidad | Descripción | Método HTTP |
|---------------|-------------|-------------|
| Crear observación | Registrar novedad con foto | POST |
| Listar observaciones | Por estudiante o ruta | GET |
| Consultar por ID | Obtener observación | GET |

### 9.2 Niveles de Severidad

| Severidad | Código | Descripción |
|-----------|--------|-------------|
| LOW | 1 | Información menor |
| MEDIUM | 2 | Requiere atención |
| HIGH | 3 | Urgente |

### 9.3 Reglas de Negocio

- La **descripción es obligatoria**
- La **foto es opcional** (URL)
- La severidad se asigna al crear
- Se asocia a un **estudiante específico**
- El conductor que reporta debe estar en una **ruta activa**

### 9.4 DTOs

**ObservationRequest:**
```json
{
  "studentId": "uuid",
  "description": "El estudiante no traía su uniforme",
  "photoUrl": "https://storage.com/obs/123.jpg",
  "severity": "LOW"
}
```

**ObservationResponse:**
```json
{
  "id": "uuid",
  "studentId": "uuid",
  "studentName": "Juan Perez",
  "driverId": "uuid",
  "driverName": "Carlos Rodriguez",
  "description": "El estudiante no traía su uniforme",
  "photoUrl": "https://storage.com/obs/123.jpg",
  "severity": "LOW",
  "timestamp": "2024-01-15T08:00:00Z"
}
```

### 9.5 Endpoints

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST | `/api/v1/observations` | Crear observación |
| GET | `/api/v1/observations/{id}` | Obtener por ID |
| GET | `/api/v1/observations/student/{studentId}` | Por estudiante |
| GET | `/api/v1/observations/route/{routeId}` | Por ruta |

---

## 10. 🔔 Notifications Module

### 10.1 Funcionalidades

| Funcionalidad | Descripción | Método HTTP |
|---------------|-------------|-------------|
| Enviar notificación | Push notification a FCM | POST (interno) |
| Listar notificaciones | Por acudiente | GET |
| Marcar como leída | Actualizar readAt | PUT |

### 10.2 Tipos de Notificación

| Tipo | Trigger | Mensaje |
|------|---------|---------|
| BOARD | Estudiante sube al vehículo | "Tu hijo ha abordado la ruta" |
| ARRIVAL | Llega al colegio | "Tu hijo ha llegado al colegio" |
| DROP | Regresa a casa | "Tu hijo ha llegado a casa" |
| OBSERVATION | Nueva observación | "Nueva observación sobre tu hijo" |

### 10.3 Reglas de Negocio

- Se envía a **todos los acudientes** con `notify_events = true`
- Se puede marcar como **leída o no leída**
- El **FCM token** es necesario para enviar
- Se guarda **historial** de todas las notificaciones enviadas

### 10.4 DTOs

**NotificationResponse:**
```json
{
  "id": "uuid",
  "guardianId": "uuid",
  "title": "Estudiante abordó",
  "body": "Tu hijo Juan ha abordado la ruta",
  "type": "BOARD",
  "referenceId": "uuid-del-evento",
  "sentAt": "2024-01-15T07:30:00Z",
  "readAt": null
}
```

### 10.5 Endpoints

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/api/v1/notifications/guardian/{guardianId}` | Lista notificaciones |
| PUT | `/api/v1/notifications/{id}/read` | Marcar como leída |

---

## 11. 🔗 Relaciones Entre Módulos

```
┌─────────────┐
│    Users    │ ◄── Autenticación
└──────┬──────┘
       │ 1:1
       ▼
┌─────────────┐     ┌─────────────┐
│   Drivers   │────►│    Routes   │
└─────────────┘     └──────┬──────┘
                           │ 1:N
                           ▼
                    ┌─────────────┐
                    │    Stops    │
                    └──────┬──────┘
                           │ N:1
                           ▼
                    ┌─────────────┐
                    │  Students   │
                    └──────┬──────┘
                           │ N:M
                           ▼
                    ┌─────────────┐
                    │  Guardians  │
                    └─────────────┘
                           │
            ┌──────────────┴──────────────┐
            ▼                             ▼
     ┌─────────────┐              ┌─────────────┐
     │   Events    │              │ Observations│
     └─────────────┘              └─────────────┘
            │
            ▼
     ┌─────────────┐
     │Notifications│
     └─────────────┘

     ┌─────────────┐
     │   Routes    │
            │
            ▼
     ┌─────────────┐
     │ GPS_Positions│
     └─────────────┘
```

---

## 12. 📋 Resumen de Endpoints

### 12.1 Endpoints Públicos

| Método | Endpoint | Módulo |
|--------|----------|--------|
| POST | `/api/v1/auth/login` | Auth |
| POST | `/api/v1/auth/register` | Auth |
| POST | `/api/v1/auth/refresh` | Auth |

### 12.2 Endpoints Protegidos

| Método | Endpoint | Módulo |
|--------|----------|--------|
| POST | `/api/v1/auth/logout` | Auth |
| GET | `/api/v1/auth/me` | Auth |
| POST | `/api/v1/users` | Users |
| GET | `/api/v1/users` | Users |
| GET | `/api/v1/users/{id}` | Users |
| PUT | `/api/v1/users/{id}` | Users |
| DELETE | `/api/v1/users/{id}` | Users |
| POST | `/api/v1/students` | Students |
| GET | `/api/v1/students` | Students |
| GET | `/api/v1/students/{id}` | Students |
| PUT | `/api/v1/students/{id}` | Students |
| DELETE | `/api/v1/students/{id}` | Students |
| POST | `/api/v1/guardians` | Guardians |
| GET | `/api/v1/guardians` | Guardians |
| GET | `/api/v1/guardians/{id}` | Guardians |
| POST | `/api/v1/guardians/{id}/students` | Guardians |
| POST | `/api/v1/drivers` | Drivers |
| GET | `/api/v1/drivers` | Drivers |
| POST | `/api/v1/routes` | Routes |
| GET | `/api/v1/routes` | Routes |
| POST | `/api/v1/routes/{id}/start` | Routes |
| POST | `/api/v1/routes/{id}/finish` | Routes |
| POST | `/api/v1/routes/{id}/stops` | Routes |
| POST | `/api/v1/tracking/positions` | Tracking |
| GET | `/api/v1/tracking/routes/{routeId}/current` | Tracking |
| POST | `/api/v1/events` | Events |
| GET | `/api/v1/events/student/{studentId}` | Events |
| POST | `/api/v1/observations` | Observations |
| GET | `/api/v1/observations/student/{studentId}` | Observations |
| GET | `/api/v1/notifications/guardian/{guardianId}` | Notifications |

---

## 13. 🔗 Referencias

- [Architecture Context](./contextArchitecture.md)
- [Context General](./contextGeneral.md)
- [Context Database](./contextDatabase.md)
- [Context Auth](./contextAuth.md)
