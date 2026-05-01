# 📡 Contratos de API - SafeRoute

---

## 1. Visión General

Este documento define los contratos principales de la API REST de SafeRoute, sirviendo como referencia tanto para el backend como para el frontend.

**Base URL:** `http://localhost:8080/api/v1`

**Autenticación:** JWT Bearer Token en header `Authorization: Bearer <token>`

---

## 2. Autenticación

### 2.1 Login

```
POST /auth/login

Request:
{
  "email": "user@example.com",
  "password": "password123"
}

Response (200):
{
  "accessToken": "eyJhbGc...",
  "refreshToken": "eyJhbGc...",
  "tokenType": "Bearer",
  "expiresIn": 3600,
  "user": {
    "id": "uuid",
    "email": "user@example.com",
    "name": "Juan Pérez",
    "roles": ["DRIVER"]
  }
}
```

### 2.2 Registro

```
POST /auth/register

Request:
{
  "email": "user@example.com",
  "password": "password123",
  "name": "Juan Pérez",
  "phone": "+573001234567"
}

Response (201):
{
  "accessToken": "eyJhbGc...",
  "refreshToken": "eyJhbGc...",
  "tokenType": "Bearer",
  "expiresIn": 3600,
  "user": {
    "id": "uuid",
    "email": "user@example.com",
    "name": "Juan Pérez",
    "roles": ["GUARDIAN"]
  }
}
```

### 2.3 Refresh Token

```
POST /auth/refresh

Request:
{
  "refreshToken": "eyJhbGc..."
}

Response (200):
{
  "accessToken": "eyJhbGc...",
  "refreshToken": "eyJhbGc...",
  "tokenType": "Bearer",
  "expiresIn": 3600
}
```

---

## 3. Estudiantes

### 3.1 Crear Estudiante

```
POST /students

Request:
{
  "name": "Juan Pérez",
  "address": "Carrera 50 #123-45",
  "latitude": 4.7110,
  "longitude": -74.0721,
  "schoolName": "Colegio San José",
  "schoolLatitude": 4.7200,
  "schoolLongitude": -74.0800,
  "grade": "5to Primaria",
  "birthDate": "2014-03-15",
  "emergencyContact": "María Pérez",
  "emergencyPhone": "+573001234567",
  "medicalInfo": "Alergia a frutas",
  "studentCode": "EST-001"
}

Response (201):
{
  "id": "uuid",
  "name": "Juan Pérez",
  "address": "Carrera 50 #123-45",
  "location": { "type": "Point", "coordinates": [-74.0721, 4.7110] },
  "schoolName": "Colegio San José",
  "schoolLocation": { "type": "Point", "coordinates": [-74.0800, 4.7200] },
  "grade": "5to Primaria",
  "birthDate": "2014-03-15",
  "emergencyContact": "María Pérez",
  "emergencyPhone": "+573001234567",
  "medicalInfo": "Alergia a frutas",
  "studentCode": "EST-001",
  "createdAt": "2026-04-13T10:00:00Z"
}
```

### 3.2 Listar Estudiantes

```
GET /students?page=0&size=10&sortBy=createdAt&direction=DESC

Response (200):
{
  "content": [...],
  "totalElements": 50,
  "totalPages": 5,
  "currentPage": 0,
  "pageSize": 10
}
```

---

## 4. Rutas

### 4.1 Crear Ruta

```
POST /routes

Request:
{
  "name": "Ruta Norte - Mañana",
  "driverId": "uuid",
  "scheduledDate": "2026-04-14",
  "stops": [
    { "studentId": "uuid", "order": 1 },
    { "studentId": "uuid", "order": 2 }
  ]
}

Response (201):
{
  "id": "uuid",
  "name": "Ruta Norte - Mañana",
  "driverId": "uuid",
  "status": "PENDING",
  "scheduledDate": "2026-04-14",
  "stops": [...],
  "createdAt": "2026-04-13T10:00:00Z"
}
```

### 4.2 Iniciar Ruta

```
POST /routes/{id}/start

Response (200):
{
  "id": "uuid",
  "status": "IN_PROGRESS",
  "startTime": "2026-04-14T06:30:00Z"
}
```

### 4.3 Finalizar Ruta

```
POST /routes/{id}/finish

Response (200):
{
  "id": "uuid",
  "status": "COMPLETED",
  "endTime": "2026-04-14T08:15:00Z"
}
```

---

## 5. GPS Tracking

### 5.1 Enviar Posición

```
POST /gps/position

Request:
{
  "routeId": "uuid",
  "latitude": 4.7110,
  "longitude": -74.0721,
  "speed": 35.5,
  "heading": 180.0,
  "accuracy": 5.0
}

Response (201):
{
  "id": "uuid",
  "timestamp": "2026-04-14T06:45:00Z"
}
```

### 5.2 Obtener Posición Actual

```
GET /routes/{id}/position

Response (200):
{
  "latitude": 4.7110,
  "longitude": -74.0721,
  "speed": 35.5,
  "heading": 180.0,
  "timestamp": "2026-04-14T06:45:00Z"
}
```

---

## 6. Eventos

### 6.1 Registrar BOARD (Estudiante sube)

```
POST /events/board

Request:
{
  "studentId": "uuid",
  "routeId": "uuid",
  "latitude": 4.7110,
  "longitude": -74.0721,
  "notes": "Estudiante subido sin incidentes"
}

Response (201):
{
  "id": "uuid",
  "eventType": "BOARD",
  "studentId": "uuid",
  "routeId": "uuid",
  "timestamp": "2026-04-14T06:50:00Z"
}
```

### 6.2 Registrar DROP (Estudiante baja)

```
POST /events/drop

Request:
{
  "studentId": "uuid",
  "routeId": "uuid",
  "latitude": 4.7500,
  "longitude": -74.0900,
  "notes": "Entregado a la madre"
}

Response (201):
{
  "id": "uuid",
  "eventType": "DROP",
  "studentId": "uuid",
  "routeId": "uuid",
  "timestamp": "2026-04-14T07:45:00Z"
}
```

---

## 7. NFC

### 7.1 Asignar NFC a Estudiante

```
POST /students/{id}/nfc

Request:
{
  "nfcUid": "04A1B2C3D4E5F6",
  "notes": "Tarjeta nueva"
}

Response (201):
{
  "id": "uuid",
  "studentId": "uuid",
  "nfcUid": "04A1B2C3D4E5F6",
  "isActive": true,
  "assignedAt": "2026-04-13T10:00:00Z"
}
```

### 7.2 Escanear NFC

```
POST /nfc/scan

Request:
{
  "nfcUid": "04A1B2C3D4E5F6"
}

Response (200):
{
  "studentId": "uuid",
  "studentName": "Juan Pérez",
  "schoolGrade": "5to Primaria"
}
```

---

## 8. Códigos de Error Comunes

| Código | Descripción |
|--------|-------------|
| 400 | Request inválido |
| 401 | No autenticado |
| 403 | No autorizado |
| 404 | Recurso no encontrado |
| 409 | Conflicto (ej: email ya existe) |
| 422 | Validación fallida |
| 500 | Error interno del servidor |

---

## 9. Véase También

- [App Overview](../01-general/app-overview.md)
- [Arquitectura Backend](../02-backend/architecture.md)
- [PostGIS](../02-backend/postgis.md)

---

*Documento generado: 2026-04-13*
*Versión: 1.0.0*