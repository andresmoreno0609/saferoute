# 🚍 SafeRoute - Contexto General del Sistema

---

## 1. 📌 Visión General

**SafeRoute** es un sistema de gestión y monitoreo de rutas escolares que permite administrar, optimizar y supervisar el transporte de estudiantes en tiempo real.

El sistema está enfocado en mejorar la seguridad, trazabilidad y comunicación entre conductores y acudientes, proporcionando visibilidad completa del recorrido y estado de los estudiantes.

---

## 2. 🎯 Objetivo del MVP

El MVP tiene como objetivo principal:

* Gestionar rutas escolares de manera eficiente.
* Monitorear la ubicación de los vehículos en tiempo real.
* Registrar y notificar eventos del estudiante.
* Permitir comunicación básica mediante observaciones.

---

## 3. 👥 Actores del Sistema

### 3.1 Administrador (ADMIN)

* Gestiona usuarios del sistema
* Crea y administra rutas
* Asigna conductores
* Supervisa la operación general

---

### 3.2 Conductor (DRIVER)

* Ejecuta rutas asignadas
* Envía ubicación GPS en tiempo real
* Registra eventos del estudiante
* Registra observaciones

---

### 3.3 Acudiente (GUARDIAN)

* Consulta estado del estudiante
* Recibe notificaciones
* Visualiza ubicación del vehículo

---

## 4. 🧱 Arquitectura General

### 4.1 Enfoque arquitectónico

* Tipo: Monolito modular (preparado para microservicios)
* Backend: Java 17 + Spring Boot 3.x
* Base de datos: PostgreSQL 15 + PostGIS 3.4
* Comunicación: REST APIs
* Notificaciones: Firebase Cloud Messaging (FCM)

---

### 4.2 Principios de diseño

* Separación de responsabilidades
* Arquitectura modular
* Event-driven (basado en eventos)
* Escalabilidad progresiva
* Backend desacoplado

---

## 5. 🧩 Módulos del Sistema

### 5.1 authentication

* Login
* Registro
* Generación de JWT
* Control de acceso

---

### 5.2 users

* Gestión de usuarios
* Roles (ADMIN, DRIVER, GUARDIAN)
* Estados (ACTIVE, INACTIVE, DELETED)

---

### 5.3 students

* Registro de estudiantes
* Asociación con acudientes
* Ubicación geográfica

---

### 5.4 guardians

* Información de acudientes
* Relación con estudiantes

---

### 5.5 drivers

* Gestión de conductores
* Información del vehículo
* Documentos del conductor (licencia, cédula)
* Verificación por ADMIN
* Disponibilidad para rutas

---

### 5.6 vehicles

* Gestión de vehículos
* Documentos del vehículo (SOAP, Seguro, Tecno-mecánica, Tarjeta propiedad)
* Verificación de documentos por ADMIN

---

### 5.7 routes

* Creación de rutas
* Asignación de conductor
* Gestión de paradas

---

### 5.8 nfc

* Sistema de identificación de estudiantes por NFC
* Asignación de tarjetas NFC
* Historial deNFC por estudiante
* Escaneo de NFC para detectar estudiante

---

### 5.9 tracking

* Recepción de coordenadas GPS
* Almacenamiento de posiciones
* Consulta de ubicación actual

---

### 5.10 trips (eventos)

* Registro de eventos del estudiante:

    * BOARD (sube al vehículo)
    * ARRIVAL (llega al colegio)
    * DROP (regresa a casa)

---

### 5.9 observations

* Registro de novedades
* Texto + imagen
* Asociación a estudiante

---

### 5.10 notifications

* Envío de notificaciones push
* Integración con FCM

---

## 6. 🔄 Flujo Principal del Sistema

1. El conductor inicia la ruta
2. La aplicación envía ubicación GPS periódicamente
3. El estudiante aborda el vehículo (evento BOARD)
4. Se envía notificación al acudiente
5. El vehículo llega al colegio (evento ARRIVAL)
6. El estudiante regresa a casa (evento DROP)

---

## 7. 🗃️ Modelo de Datos (Resumen)

El modelo de datos completo se encuentra en [contextDatabase.md](./contextDatabase.md).

**Entidades principales:**

| Entidad | Descripción |
|---------|-------------|
| Users | Usuarios del sistema (ADMIN, DRIVER, GUARDIAN) |
| Students | Estudiantes registrados |
| Guardians | Acudientes asociados a estudiantes |
| Drivers | Información de conductores y vehículos |
| Routes | Rutas con conductor asignado |
| Stops | Paradas ordenadas por estudiante |
| GPS_Positions | Historial de posiciones del vehículo |
| Student_Events | Eventos críticos del estudiante (BOARD, ARRIVAL, DROP) |
| Observations | Novedades reportadas por el conductor |

---

## 8. 📡 Eventos del Sistema

Eventos principales:

* STUDENT_BOARDED
* STUDENT_ARRIVED
* STUDENT_DROPPED
* ROUTE_STARTED
* ROUTE_FINISHED
* OBSERVATION_CREATED

---

## 9. 🔔 Notificaciones

Cada evento importante genera una notificación para el acudiente.

Ejemplos:

* "Tu hijo ha abordado la ruta"
* "El vehículo está cerca de tu ubicación"
* "Tu hijo ha llegado al colegio"

---

## 10. 📍 Geolocalización

* Uso de PostGIS para consultas espaciales
* Envío de ubicación cada 10 segundos
* Representación en mapas en tiempo real

---

## 11. 🔐 Seguridad

* Autenticación basada en JWT
* Control de acceso por roles
* Protección de endpoints

---

## 12. ⚙️ Reglas de Negocio

* Un estudiante puede tener múltiples acudientes
* Un conductor puede tener múltiples rutas, pero solo una activa
* Un conductor debe estar verificado por ADMIN para trabajar
* Un conductor debe tener vehículo con todos los documentos vigentes
* Solo un documento activo por tipo (vehículo y conductor)
* Un estudiante puede tener múltiples NFCs (histórico), solo uno activo
* Una ruta tiene múltiples paradas
* Cada parada corresponde a un estudiante
* Los eventos no se editan (son inmutables)

---

## 13. 🚀 Escalabilidad futura

* Migración a microservicios
* Uso de mensajería (Kafka o RabbitMQ)
* Optimización automática de rutas
* Integración de pagos
* Integración NFC con eventos de ruta (BOARD/DROP automático)

---

## 14. 🧠 Principios clave del sistema

* Los eventos son la fuente de verdad del estado del estudiante
* La ubicación es dinámica y en tiempo real
* Las notificaciones son reactivas a eventos
* El sistema es modular y desacoplado

---

## 15. 📈 Métricas futuras

* Puntualidad de rutas
* Tiempo promedio de recorrido
* Número de incidencias
* Calidad del servicio

---

## 16. 📌 Convenciones

* Idioma del código: Inglés
* Base de datos: esquema `public`
* Nombres: snake_case en DB, camelCase en código
* APIs: RESTful
* Uso obligatorio de DTOs

---

## 17. 📍 Resumen

SafeRoute es un sistema centrado en:

* Eventos (estado del estudiante)
* Ubicación (posición del vehículo)
* Comunicación (notificaciones al acudiente)

El backend está diseñado para ser:

* Escalable
* Modular
* Seguro
* Preparado para crecimiento

---

## Véase también

- [Architecture](./contextArchitecture.md)
- [Database](./contextDatabase.md)
- [API](./contextAPI.md)
- [Auth](./contextAuth.md)
- [Features](./contextFeatures.md)
