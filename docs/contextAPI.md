# API Context

<!-- Add API endpoints, contracts, and integration details -->
# 🚍 SafeRoute - Contexto de API

---

## 1. 📡 Información General

### 1.1 URL Base

```
{{BASE_URL}}
```

Ejemplo: `https://api.saferoute.com` o `http://localhost:8080/api/v1`

---

### 1.2 Versión

```
{{API_VERSION}}
```

Versión actual: `v1`

---

### 1.3 Transport

- **Producción:** HTTPS obligatorio
- **Desarrollo:** HTTP permitido

---

## 2. 🔐 Autenticación

### 2.1 Tipo de Autenticación

La API usa **JWT (JSON Web Token)** para autenticación.

Todos los endpoints protegidos requieren el header:

```
Authorization: Bearer <TOKEN_JWT>
```

---

### 2.2 Obtener Token

#### POST /api/v1/auth/login

**Descripción:** Iniciar sesión y obtener token de acceso

**Autenticación:** No requerida

**Request Body:**

```json
{
  "email": "string",
  "password": "string"
}
```

**Response (200 OK):**

```json
{
  "accessToken": "string",
  "refreshToken": "string",
  "expiresIn": 3600,
  "user": {
    "id": "uuid",
    "email": "string",
    "name": "string",
    "role": "ADMIN | DRIVER | GUARDIAN"
  }
}
```

**Errores:**

| Código | Descripción |
|--------|-------------|
| 400 | Credenciales inválidas |
| 401 | Usuario inactivo |

---

#### POST /api/v1/auth/register

**Descripción:** Registrar nuevo usuario

**Autenticación:** No requerida (por definir)

**Request Body:**

```json
{
  "email": "string",
  "password": "string",
  "name": "string",
  "role": "ADMIN | DRIVER | GUARDIAN"
}
```

**Response (201 Created):**

```json
{
  "id": "uuid",
  "email": "string",
  "name": "string",
  "role": "ADMIN | DRIVER | GUARDIAN",
  "createdAt": "timestamp"
}
```

**Errores:**

| Código | Descripción |
|--------|-------------|
| 400 | Email ya registrado |
| 400 | Contraseña inválida |

---

#### POST /api/v1/auth/refresh

**Descripción:** Renovar token vencido

**Request Body:**

```json
{
  "refreshToken": "string"
}
```

**Response (200 OK):**

```json
{
  "accessToken": "string",
  "expiresIn": 3600
}
```

**Errores:**

| Código | Descripción |
|--------|-------------|
| 401 | Refresh token inválido o vencido |

---

#### POST /api/v1/auth/logout

**Descripción:** Invalidar token actual

**Autenticación:** Requiere token válido

**Response (200 OK):**

```json
{
  "message": "Logout exitoso"
}
```

---

## 3. 👥 Users (Administración)

### GET /api/v1/users

**Descripción:** Listar todos los usuarios

**Autenticación:** Requiere token + rol ADMIN

**Query Parameters:**

| Parámetro | Tipo | Requerido | Descripción |
|-----------|------|-----------|-------------|
| page | int | No | Número de página (default: 0) |
| size | int | No | Elementos por página (default: 20) |
| role | string | No | Filtrar por rol |
| status | string | No | Filtrar por estado |
| search | string | No | Buscar por nombre o email |

**Response (200 OK):**

```json
{
  "content": [
    {
      "id": "uuid",
      "email": "string",
      "name": "string",
      "role": "ADMIN | DRIVER | GUARDIAN",
      "status": "ACTIVE | INACTIVE | DELETED",
      "createdAt": "timestamp",
      "lastLoginDate": "timestamp | null"
    }
  ],
  "totalElements": 100,
  "totalPages": 5,
  "currentPage": 0
}
```

---

### POST /api/v1/users

**Descripción:** Crear nuevo usuario

**Autenticación:** Requiere token + rol ADMIN

**Request Body:**

```json
{
  "email": "string",
  "password": "string",
  "name": "string",
  "role": "ADMIN | DRIVER | GUARDIAN"
}
```

**Response (201 Created):**

```json
{
  "id": "uuid",
  "email": "string",
  "name": "string",
  "role": "ADMIN | DRIVER | GUARDIAN",
  "status": "ACTIVE",
  "createdAt": "timestamp"
}
```

---

### GET /api/v1/users/{id}

**Descripción:** Obtener usuario por ID

**Autenticación:** Requiere token

**Response (200 OK):**

```json
{
  "id": "uuid",
  "email": "string",
  "name": "string",
  "role": "ADMIN | DRIVER | GUARDIAN",
  "status": "ACTIVE | INACTIVE | DELETED",
  "createdAt": "timestamp",
  "lastLoginDate": "timestamp | null"
}
```

**Errores:**

| Código | Descripción |
|--------|-------------|
| 404 | Usuario no encontrado |

---

### PUT /api/v1/users/{id}

**Descripción:** Actualizar usuario

**Autenticación:** Requiere token + rol ADMIN (o mismo usuario)

**Request Body:**

```json
{
  "name": "string",
  "status": "ACTIVE | INACTIVE | DELETED"
}
```

**Response (200 OK):**

```json
{
  "id": "uuid",
  "email": "string",
  "name": "string",
  "role": "ADMIN | DRIVER | GUARDIAN",
  "status": "ACTIVE",
  "updatedAt": "timestamp"
}
```

---

### DELETE /api/v1/users/{id}

**Descripción:** Eliminar usuario (soft delete)

**Autenticación:** Requiere token + rol ADMIN

**Response (200 OK):**

```json
{
  "id": "uuid",
  "status": "DELETED",
  "deletedAt": "timestamp"
}
```

---

## 4. 🚸 Students

### GET /api/v1/students

**Descripción:** Listar estudiantes

**Autenticación:** Requiere token + rol ADMIN

**Query Parameters:**

| Parámetro | Tipo | Descripción |
|-----------|------|-------------|
| page | int | Página |
| size | int | Elementos por página |
| search | string | Buscar por nombre |

**Response (200 OK):**

```json
{
  "content": [
    {
      "id": "uuid",
      "name": "string",
      "address": "string",
      "location": {
        "lat": 0.0,
        "lng": 0.0
      },
      "guardians": [
        {
          "id": "uuid",
          "name": "string",
          "phone": "string"
        }
      ],
      "createdAt": "timestamp"
    }
  ]
}
```

---

### POST /api/v1/students

**Descripción:** Crear estudiante

**Autenticación:** Requiere token + rol ADMIN

**Request Body:**

```json
{
  "name": "string",
  "address": "string",
  "latitude": 0.0,
  "longitude": 0.0
}
```

**Response (201 Created):**

```json
{
  "id": "uuid",
  "name": "string",
  "address": "string",
  "location": {
    "lat": 0.0,
    "lng": 0.0
  },
  "createdAt": "timestamp"
}
```

---

### GET /api/v1/students/{id}

**Descripción:** Obtener estudiante por ID

**Autenticación:** Requiere token

**Response (200 OK):**

```json
{
  "id": "uuid",
  "name": "string",
  "address": "string",
  "location": {
    "lat": 0.0,
    "lng": 0.0
  },
  "guardians": [...],
  "createdAt": "timestamp"
}
```

---

### PUT /api/v1/students/{id}

**Descripción:** Actualizar estudiante

**Autenticación:** Requiere token + rol ADMIN

**Request Body:**

```json
{
  "name": "string",
  "address": "string",
  "latitude": 0.0,
  "longitude": 0.0
}
```

---

### DELETE /api/v1/students/{id}

**Descripción:** Eliminar estudiante

**Autenticación:** Requiere token + rol ADMIN

---

### POST /api/v1/students/{id}/guardians

**Descripción:** Asociar acudiente a estudiante

**Autenticación:** Requiere token + rol ADMIN

**Request Body:**

```json
{
  "guardianId": "uuid"
}
```

---

## 5. 👨‍👩‍👧 Guardians

### GET /api/v1/guardians

**Descripción:** Listar acudientes

**Autenticación:** Requiere token + rol ADMIN

---

### POST /api/v1/guardians

**Descripción:** Crear acudiente

**Autenticación:** Requiere token + rol ADMIN

**Request Body:**

```json
{
  "name": "string",
  "phone": "string",
  "email": "string"
}
```

---

### GET /api/v1/guardians/{id}

**Descripción:** Obtener acudiente con sus estudiantes

**Autenticación:** Requiere token

**Response (200 OK):**

```json
{
  "id": "uuid",
  "name": "string",
  "phone": "string",
  "email": "string",
  "students": [
    {
      "id": "uuid",
      "name": "string"
    }
  ]
}
```

---

## 6. 🚗 Drivers

### GET /api/v1/drivers

**Descripción:** Listar conductores

**Autenticación:** Requiere token + rol ADMIN

---

### POST /api/v1/drivers

**Descripción:** Crear conductor

**Autenticación:** Requiere token + rol ADMIN

**Request Body:**

```json
{
  "userId": "uuid",
  "name": "string",
  "phone": "string",
  "vehiclePlate": "string"
}
```

---

### GET /api/v1/drivers/{id}

**Descripción:** Obtener conductor

**Autenticación:** Requiere token

---

### PUT /api/v1/drivers/{id}

**Descripción:** Actualizar conductor

**Autenticación:** Requiere token + rol ADMIN

**Request Body:**

```json
{
  "name": "string",
  "phone": "string",
  "vehiclePlate": "string"
}
```

---

## 7. 🛣️ Routes

### GET /api/v1/routes

**Descripción:** Listar rutas

**Autenticación:** Requiere token

**Query Parameters:**

| Parámetro | Tipo | Descripción |
|-----------|------|-------------|
| driverId | uuid | Filtrar por conductor |
| status | string | Filtrar por estado |

**Response (200 OK):**

```json
{
  "content": [
    {
      "id": "uuid",
      "name": "string",
      "driverId": "uuid",
      "driverName": "string",
      "status": "PENDING | IN_PROGRESS | COMPLETED | CANCELLED",
      "startTime": "timestamp | null",
      "endTime": "timestamp | null",
      "stops": [
        {
          "id": "uuid",
          "studentId": "uuid",
          "studentName": "string",
          "order": 1,
          "location": { "lat": 0.0, "lng": 0.0 }
        }
      ],
      "createdAt": "timestamp"
    }
  ]
}
```

---

### POST /api/v1/routes

**Descripción:** Crear ruta

**Autenticación:** Requiere token + rol ADMIN

**Request Body:**

```json
{
  "name": "string",
  "driverId": "uuid",
  "stopIds": ["uuid", "uuid"]
}
```

**Response (201 Created):**

```json
{
  "id": "uuid",
  "name": "string",
  "driverId": "uuid",
  "status": "PENDING",
  "createdAt": "timestamp"
}
```

---

### GET /api/v1/routes/{id}

**Descripción:** Obtener ruta con paradas

**Autenticación:** Requiere token

---

### PUT /api/v1/routes/{id}/start

**Descripción:** Iniciar ruta

**Autenticación:** Requiere token + rol DRIVER (asignado a la ruta)

**Response (200 OK):**

```json
{
  "id": "uuid",
  "status": "IN_PROGRESS",
  "startTime": "timestamp"
}
```

---

### PUT /api/v1/routes/{id}/finish

**Descripción:** Finalizar ruta

**Autenticación:** Requiere token + rol DRIVER

**Response (200 OK):**

```json
{
  "id": "uuid",
  "status": "COMPLETED",
  "endTime": "timestamp"
}
```

---

### PUT /api/v1/routes/{id}/cancel

**Descripción:** Cancelar ruta

**Autenticación:** Requiere token + rol ADMIN

**Request Body:**

```json
{
  "reason": "string"
}
```

---

## 8. 📍 GPS Tracking

### POST /api/v1/tracking/position

**Descripción:** Enviar posición GPS del vehículo

**Autenticación:** Requiere token + rol DRIVER

**Request Body:**

```json
{
  "routeId": "uuid",
  "latitude": 0.0,
  "longitude": 0.0,
  "timestamp": "ISO8601"
}
```

**Response (200 OK):**

```json
{
  "id": "uuid",
  "driverId": "uuid",
  "routeId": "uuid",
  "location": { "lat": 0.0, "lng": 0.0 },
  "timestamp": "timestamp"
}
```

---

### GET /api/v1/tracking/positions/{routeId}

**Descripción:** Obtener historial de posiciones de una ruta

**Autenticación:** Requiere token

**Query Parameters:**

| Parámetro | Tipo | Descripción |
|-----------|------|-------------|
| startTime | timestamp | Inicio del rango |
| endTime | timestamp | Fin del rango |

**Response (200 OK):**

```json
{
  "positions": [
    {
      "id": "uuid",
      "location": { "lat": 0.0, "lng": 0.0 },
      "timestamp": "timestamp"
    }
  ]
}
```

---

### GET /api/v1/tracking/current/{routeId}

**Descripción:** Obtener posición actual del vehículo

**Autenticación:** Requiere token

**Response (200 OK):**

```json
{
  "location": { "lat": 0.0, "lng": 0.0 },
  "timestamp": "timestamp",
  "speed": 0.0
}
```

---

## 9. 🎫 Events (Student Events)

### POST /api/v1/events/board

**Descripción:** Registrar que estudiante subió al vehículo

**Autenticación:** Requiere token + rol DRIVER

**Request Body:**

```json
{
  "studentId": "uuid",
  "routeId": "uuid"
}
```

**Response (201 Created):**

```json
{
  "id": "uuid",
  "eventType": "BOARD",
  "studentId": "uuid",
  "driverId": "uuid",
  "routeId": "uuid",
  "location": { "lat": 0.0, "lng": 0.0 },
  "timestamp": "timestamp"
}
```

**Nota:** Envía notificación al acudiente automáticamente

---

### POST /api/v1/events/arrival

**Descripción:** Registrar llegada al colegio

**Autenticación:** Requiere token + rol DRIVER

**Request Body:**

```json
{
  "studentId": "uuid",
  "routeId": "uuid"
}
```

**Response (201 Created):**

```json
{
  "id": "uuid",
  "eventType": "ARRIVAL",
  "studentId": "uuid",
  "driverId": "uuid",
  "routeId": "uuid",
  "timestamp": "timestamp"
}
```

**Nota:** Envía notificación al acudiente automáticamente

---

### POST /api/v1/events/drop

**Descripción:** Registrar que estudiante bajó en su casa

**Autenticación:** Requiere token + rol DRIVER

**Request Body:**

```json
{
  "studentId": "uuid",
  "routeId": "uuid"
}
```

**Response (201 Created):**

```json
{
  "id": "uuid",
  "eventType": "DROP",
  "studentId": "uuid",
  "driverId": "uuid",
  "routeId": "uuid",
  "location": { "lat": 0.0, "lng": 0.0 },
  "timestamp": "timestamp"
}
```

**Nota:** Envía notificación al acudiente automáticamente

---

### GET /api/v1/events/student/{studentId}

**Descripción:** Obtener historial de eventos de un estudiante

**Autenticación:** Requiere token

**Query Parameters:**

| Parámetro | Tipo | Descripción |
|-----------|------|-------------|
| startDate | date | Filtrar desde |
| endDate | date | Filtrar hasta |
| limit | int | Límite de resultados |

**Response (200 OK):**

```json
{
  "events": [
    {
      "id": "uuid",
      "eventType": "BOARD | ARRIVAL | DROP",
      "routeId": "uuid",
      "timestamp": "timestamp"
    }
  ]
}
```

---

## 10. 📝 Observations

### POST /api/v1/observations

**Descripción:** Registrar observación/novedad

**Autenticación:** Requiere token + rol DRIVER

**Request Body:**

```json
{
  "studentId": "uuid",
  "description": "string",
  "photoUrl": "string | null"
}
```

**Response (201 Created):**

```json
{
  "id": "uuid",
  "studentId": "uuid",
  "driverId": "uuid",
  "description": "string",
  "photoUrl": "string | null",
  "timestamp": "timestamp"
}
```

**Nota:** Envía notificación al acudiente automáticamente

---

### GET /api/v1/observations/student/{studentId}

**Descripción:** Obtener observaciones de un estudiante

**Autenticación:** Requiere token

**Response (200 OK):**

```json
{
  "observations": [
    {
      "id": "uuid",
      "driverName": "string",
      "description": "string",
      "photoUrl": "string | null",
      "timestamp": "timestamp"
    }
  ]
}
```

---

## 11. 🔔 Notifications

### GET /api/v1/notifications

**Descripción:** Listar notificaciones del usuario

**Autenticación:** Requiere token

**Query Parameters:**

| Parámetro | Tipo | Descripción |
|-----------|------|-------------|
| page | int | Página |
| unreadOnly | boolean | Solo no leídas |

**Response (200 OK):**

```json
{
  "content": [
    {
      "id": "uuid",
      "title": "string",
      "body": "string",
      "type": "BOARD | ARRIVAL | DROP | OBSERVATION",
      "read": false,
      "createdAt": "timestamp"
    }
  ],
  "unreadCount": 3
}
```

---

### PUT /api/v1/notifications/{id}/read

**Descripción:** Marcar notificación como leída

**Autenticación:** Requiere token

---

### PUT /api/v1/notifications/read-all

**Descripción:** Marcar todas las notificaciones como leídas

**Autenticación:** Requiere token

---

### POST /api/v1/notifications/register-device

**Descripción:** Registrar dispositivo para push notifications

**Autenticación:** Requiere token

**Request Body:**

```json
{
  "fcmToken": "string",
  "deviceType": "ANDROID | IOS"
}
```

---

## 12. 📊 Códigos de Error Comunes

| Código | Descripción |
|--------|-------------|
| 200 | OK - Request exitoso |
| 201 | Created - Recurso creado |
| 204 | No Content - Respuesta vacía exitosa |
| 400 | Bad Request - Datos inválidos |
| 401 | Unauthorized - Token inválido o ausente |
| 403 | Forbidden - No tienes permiso |
| 404 | Not Found - Recurso no existe |
| 409 | Conflict - Conflicto de datos |
| 422 | Unprocessable Entity - Validación fallida |
| 500 | Internal Server Error - Error del servidor |

---

## 13. 🎨 Formato de Fechas

Todas las fechas usan **ISO 8601**:

```
2024-01-15T14:30:00Z
```

- Timestamps en UTC
- Fechas locales usar formato: `2024-01-15T14:30:00-05:00`

---

## 14. 📏 Formato de Coordenadas

```json
{
  "location": {
    "latitude": 4.7110,
    "longitude": -74.0721
  }
}
```

O en PostgreSQL/PostGIS:
```json
{
  "location": {
    "type": "Point",
    "coordinates": [-74.0721, 4.7110]
  }
}
```

---

## 15. 📝 Convenciones

- **Nombres de endpoints:** kebab-case (`/api/v1/user-profiles`)
- **Parámetros de path:** camelCase (`/users/{userId}`)
- **Query parameters:** snake_case (`start_time`, `unread_only`)
- **Respuestas paginadas:** siempre incluir `content`, `totalElements`, `totalPages`, `currentPage`
- **Fechas:** ISO 8601, UTC preferible

---

## 16. ⚙️ Configuración por Rol

| Endpoint | ADMIN | DRIVER | GUARDIAN |
|----------|-------|--------|----------|
| /api/v1/users (GET) | ✅ | ❌ | ❌ |
| /api/v1/users (POST) | ✅ | ❌ | ❌ |
| /api/v1/routes (all) | ✅ | ✅ | 🔒 |
| /api/v1/events (POST) | ❌ | ✅ | ❌ |
| /api/v1/observations (POST) | ❌ | ✅ | ❌ |
| /api/v1/notifications (GET) | ✅ | ✅ | ✅ |
| /api/v1/tracking (POST) | ❌ | ✅ | ❌ |

> 🔒 = Solo puede ver relacionadas a sus estudiantes/hijos

---

## 17. 📌 Pendiente por Definir

- [ ] Rate limiting (límites de requests)
- [ ] Versionado de API (header `Accept: application/vnd.saferoute.v1+json`)
- [ ] Documentación interactiva (Swagger/OpenAPI)
- [ ] Endpoints de métricas
- [ ] Exportación de datos (CSV, Excel)
- [ ] Webhooks para eventos externos
