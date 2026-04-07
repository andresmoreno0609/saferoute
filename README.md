# 🚍 SafeRoute

> Sistema de gestión y monitoreo de rutas escolares en tiempo real

---

## 🎯 ¿Qué es SafeRoute?

**SafeRoute** es una plataforma integral para la gestión del transporte escolar que permite:

- **Administrar rutas escolares** de manera eficiente
- **Monitorear la ubicación** de los vehículos en tiempo real (GPS)
- **Registrar eventos** de estudiantes (BOARD/DROP) de forma automatizada
- **Identificar estudiantes** mediante tarjetas NFC
- **Notificar a los padres** en tiempo real sobre el estado de sus hijos
- **Gestionar vehículos y conductores** con validación de documentación

El objetivo principal es **mejorar la seguridad** de los estudiantes durante el transporte escolar, proporcionando trazabilidad completa y comunicación efectiva entre conductores y padres de familia.

---

## 🏗️ Arquitectura Técnica

### Stack Tecnológico

| Componente | Tecnología |
|------------|------------|
| **Backend** | Java 17 + Spring Boot 3.x |
| **Base de Datos** | PostgreSQL 15 + PostGIS 3.4 |
| **API** | RESTful con JWT |
| **Notificaciones** | Firebase Cloud Messaging (FCM) |
| **Arquitectura** | Monolito modular (preparado para microservicios) |

### Estructura del Proyecto

```
src/main/java/com/saferoute/
├── common/                          ← Compartido por todos los módulos
│   ├── entity/                     ← JPA Entities
│   ├── repository/                 ← JpaRepository interfaces
│   ├── service/                    ← Services
│   ├── dto/                        ← DTOs por dominio
│   └── usecase/                    ← Template UseCaseAdvance
│
├── auth/                           ← Módulo de autenticación
├── users/                          ← Módulo de usuarios
├── students/                       ← Módulo de estudiantes
│   ├── controller/
│   ├── adapter/
│   └── usecase/
├── guardians/                      ← Módulo de padres/acudientes
├── drivers/                        ← Módulo de conductores
├── vehicles/                       ← Módulo de vehículos
├── routes/                         ← Módulo de rutas
├── tracking/                       ← Módulo de GPS
├── studentnfc/                    ← Módulo de NFC
└── ...
```

### Patrones de Diseño

- **DTOs** como records inmutables con `@Builder` de Lombok
- **UseCases** con patrón `UseCaseAdvance` (preConditions → core → postConditions)
- **Adapters** para orquestar use cases
- **Repository Pattern** con JPA
- **Service Layer** para lógica de negocio

---

## 👥 Actores del Sistema

| Rol | Descripción | Funciones |
|-----|-------------|-----------|
| **ADMIN** | Administrador | Gestiona usuarios, rutas, verifica conductores y documentos |
| **DRIVER** | Conductor | Ejecuta rutas, envía GPS, registra eventos, reporta observaciones |
| **GUARDIAN** | Acudiente/Padre | Consulta estado del estudiante, recibe notificaciones |

---

## 📱 Módulos del Sistema

### MVP (Implementados)

| Módulo | Descripción |
|--------|-------------|
| **auth** | Autenticación JWT, login, registro |
| **users** | Gestión de usuarios del sistema |
| **students** | Registro de estudiantes con ubicación geográfica |
| **guardians** | Acudientes y relación con estudiantes |
| **drivers** | Información de conductores con verificación |
| **vehicles** | Gestión de vehículos y documentos obligatorios |
| **routes** | Creación y gestión de rutas escolares |
| **tracking** | GPS en tiempo real |
| **events** | Eventos BOARD, ARRIVAL, DROP |
| **nfc** | Sistema de identificación por NFC |

### v2 (Pendientes)

| Módulo | Descripción |
|--------|-------------|
| **observations** | Registro de novedades con fotos |
| **notifications** | Push notifications avanzadas |

---

## 🔑 Características Principales

### 1. Gestión de Rutas
- Creación de rutas con paradas ordenadas
- Asignación de conductor y vehículo
- Estados: PENDING → IN_PROGRESS → COMPLETED

### 2. Tracking GPS
- Envío de posición cada 10 segundos
- Historial de posiciones por ruta
- Visualización en mapa en tiempo real

### 3. Eventos de Estudiantes
- **BOARD**: Estudiante sube al vehículo
- **ARRIVAL**: Llega al colegio
- **DROP**: Regresa a casa
- Notificaciones automáticas a los padres

### 4. Sistema NFC
- Asignación de tarjetas NFC a estudiantes
- Un solo NFC activo por estudiante
- Historial de asignaciones
- Escaneo para detectar estudiante

### 5. Validación de Documentos
- **Vehículo**: SOAP, SEGURO, TECNOMECANICA, TARJETA_PROPIEDAD
- **Conductor**: LICENCIA, CEDULA
- Verificación manual por ADMIN
- Disponibilidad del conductor basada en documentos vigentes

---

## 📡 API Endpoints Principales

### Autenticación
```
POST /api/v1/auth/login     → Iniciar sesión
POST /api/v1/auth/register  → Registrar usuario
```

### Estudiantes
```
POST   /api/v1/students           → Crear estudiante
GET    /api/v1/students           → Listar estudiantes
GET    /api/v1/students/{id}       → Obtener estudiante
PUT    /api/v1/students/{id}       → Actualizar estudiante
```

### Conductores
```
POST   /api/v1/drivers                    → Crear conductor
GET    /api/v1/drivers/{id}/availability   → Verificar disponibilidad
GET    /api/v1/drivers/{id}/documents      → Ver documentos
POST   /api/v1/drivers/{id}/verify          → Verificar conductor (ADMIN)
```

### Vehículos
```
POST   /api/v1/vehicles/{id}/documents              → Agregar documento
POST   /api/v1/vehicles/{id}/documents/{docId}/verify   → Verificar documento
```

### Rutas
```
POST   /api/v1/routes              → Crear ruta
POST   /api/v1/routes/{id}/start    → Iniciar ruta
POST   /api/v1/routes/{id}/finish   → Finalizar ruta
POST   /api/v1/routes/{id}/stops    → Agregar parada
```

### NFC
```
POST   /api/v1/students/{id}/nfc              → Asignar NFC
GET    /api/v1/students/{id}/nfc              → Obtener NFC activo
GET    /api/v1/students/{id}/nfc/history      → Ver historial NFC
POST   /api/v1/nfc/scan                       → Escanear NFC
```

### Eventos
```
POST   /api/v1/events/board     → Registrar estudiante subió
POST   /api/v1/events/drop      → Registrar estudiante bajó
```

---

## 🗄️ Base de Datos

### Entidades Principales

- **users**: Usuarios del sistema (ADMIN, DRIVER, GUARDIAN)
- **students**: Estudiantes con ubicación geográfica (PostGIS)
- **guardians**: Acudientes asociados a estudiantes
- **drivers**: Información extendida del conductor
- **vehicles**: Vehículos del sistema
- **vehicle_documents**: Documentos del vehículo
- **driver_documents**: Documentos del conductor
- **routes**: Rutas escolares
- **stops**: Paradas ordenadas
- **gps_positions**: Historial GPS
- **student_events**: Eventos de estudiantes
- **student_nfc**: Tarjetas NFC asignadas

### PostGIS
- Uso de `GEOGRAPHY(POINT)` para ubicaciones
- Consultas espaciales eficientes

---

## 🔐 Seguridad

- **Autenticación**: JWT (JSON Web Tokens)
- **Autorización**: Roles (ADMIN, DRIVER, GUARDIAN)
- **Contraseñas**: Hash con BCrypt
- **Soft Delete**: Los registros no se eliminan, se marcan como DELETED

---

## 📂 Documentación

| Documento | Descripción |
|-----------|-------------|
| [contextGeneral](./docs/contextGeneral.md) | Visión general del sistema |
| [contextFeatures](./docs/contextFeatures.md) | Funcionalidades por módulo |
| [contextArchitecture](./docs/contextArchitecture.md) | Arquitectura técnica |
| [contextDatabase](./docs/contextDatabase.md) | Esquema de base de datos |
| [contextAPI](./docs/contextAPI.md) | Documentación de APIs |
| [contextNFC](./docs/contextNFC.md) | Sistema NFC detallado |
| [contextTasksPending](./docs/contextTasksPending.md) | Tareas pendientes |

---

## 🚀 Estado del Proyecto

- ✅ **MVP en desarrollo**
- ✅ Entidades y APIs core implementadas
- ✅ Sistema NFC base implementado
- ⏳ Integración NFC con eventos de ruta (pendiente)
- ⏳ Observaciones y notificaciones avanzadas (v2)

---

## 📝 Licencia

Este proyecto es propiedad de SafeRoute.

---

*Documento generado: 2026-04-06*
*Versión: 1.0.0*