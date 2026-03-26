# Database Context

<!-- Add database schema, entities, relationships, and data flow -->
# 🚍 SafeRoute - Contexto de Base de Datos

---

## 1. 📊 Visión General

### 1.1 Sistema de Base de Datos

- **Motor:** PostgreSQL 15+
- **Extensión Espacial:** PostGIS 3.4+
- ** Esquema:** `public`
- **Hosting:** Por definir (local, cloud, RDS, etc.)

---

### 1.2 Diagrama de Entidades (Texto)

```
┌─────────────────┐       ┌─────────────────┐
│      Users      │       │     Drivers     │
├─────────────────┤       ├─────────────────┤
│ id (PK)         │◄──────│ user_id (FK)    │
│ email           │       │ name            │
│ password_hash   │       │ phone           │
│ name            │       │ vehicle_plate   │
│ role            │       └─────────────────┘
│ status          │               │
│ created_at      │               ▼
│ last_login_at   │       ┌─────────────────┐
└─────────────────┘       │     Routes      │
        │                ├─────────────────┤
        │                │ id (PK)         │
        │                │ name            │
        ▼                │ driver_id (FK)  │
┌─────────────────┐       │ status          │
│   Guardians     │       │ start_time      │
├─────────────────┤       │ end_time        │
│ id (PK)         │       └─────────────────┘
│ name            │               │
│ phone           │               ▼
│ email           │       ┌─────────────────┐
└─────────────────┘       │      Stops      │
        │                ├─────────────────┤
        ▼                │ id (PK)         │
┌─────────────────┐       │ route_id (FK)  │
│ Student_Guardians│     │ student_id(FK) │
├─────────────────┤       │ order           │
│ id (PK)         │       │ location (P)   │
│ student_id(FK) │       └─────────────────┘
│ guardian_id(FK) │               │
└─────────────────┘               ▼
        │                ┌─────────────────┐
        ▼                │  GPS_Positions  │
┌─────────────────┐       ├─────────────────┤
│    Students     │       │ id (PK)        │
├─────────────────┤       │ driver_id (FK) │
│ id (PK)         │       │ route_id (FK)  │
│ name            │       │ location (P)   │
│ address         │       │ timestamp      │
│ location (P)    │       │ speed          │
│ created_at      │       └─────────────────┘
└─────────────────┘

┌─────────────────┐       ┌─────────────────┐
│ Student_Events  │       │  Observations  │
├─────────────────┤       ├─────────────────┤
│ id (PK)         │       │ id (PK)        │
│ student_id(FK)  │       │ student_id(FK) │
│ driver_id (FK)  │       │ driver_id (FK) │
│ route_id (FK)   │       │ description    │
│ event_type      │       │ photo_url      │
│ location (P)    │       │ timestamp      │
│ timestamp       │       └─────────────────┘
└─────────────────┘
```

Leyenda:
- **(PK)** = Primary Key
- **(FK)** = Foreign Key
- **(P)** = Punto Geográfico (PostGIS)

---

## 2. 🏗️ Entidades

### 2.1 users

**Descripción:** Tabla principal de usuarios del sistema

| Campo | Tipo | Restricciones | Descripción |
|-------|------|---------------|-------------|
| id | UUID | PK, NOT NULL | Identificador único |
| email | VARCHAR(255) | UNIQUE, NOT NULL | Email del usuario |
| password_hash | VARCHAR(255) | NOT NULL | Hash de contraseña |
| name | VARCHAR(255) | NOT NULL | Nombre completo |
| role | VARCHAR(20) | NOT NULL, CHECK IN ('ADMIN', 'DRIVER', 'GUARDIAN') | Rol del usuario |
| status | VARCHAR(20) | NOT NULL, DEFAULT 'ACTIVE', CHECK IN ('ACTIVE', 'INACTIVE', 'DELETED') | Estado del usuario |
| created_at | TIMESTAMP | NOT NULL, DEFAULT NOW() | Fecha de creación |
| last_login_at | TIMESTAMP | NULL | Último login |

**Índices:**
```sql
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_role ON users(role);
CREATE INDEX idx_users_status ON users(status);
```

---

### 2.2 drivers

**Descripción:** Información extendida del conductor (relación 1:1 con users)

| Campo | Tipo | Restricciones | Descripción |
|-------|------|---------------|-------------|
| id | UUID | PK, NOT NULL | Identificador único |
| user_id | UUID | FK → users(id), UNIQUE, NOT NULL | Referencia al usuario |
| name | VARCHAR(255) | NOT NULL | Nombre del conductor |
| phone | VARCHAR(20) | NOT NULL | Teléfono de contacto |
| vehicle_plate | VARCHAR(20) | NOT NULL | Placa del vehículo |
| vehicle_model | VARCHAR(100) | NULL | Modelo del vehículo |
| vehicle_color | VARCHAR(50) | NULL | Color del vehículo |
| created_at | TIMESTAMP | NOT NULL, DEFAULT NOW() | Fecha de creación |
| updated_at | TIMESTAMP | NOT NULL, DEFAULT NOW() | Fecha de actualización |

**Índices:**
```sql
CREATE INDEX idx_drivers_user_id ON drivers(user_id);
CREATE INDEX idx_drivers_plate ON drivers(vehicle_plate);
```

---

### 2.3 students

**Descripción:** Estudiantes registrados en el sistema

| Campo | Tipo | Restricciones | Descripción |
|-------|------|---------------|-------------|
| id | UUID | PK, NOT NULL | Identificador único |
| name | VARCHAR(255) | NOT NULL | Nombre del estudiante |
| address | VARCHAR(500) | NOT NULL | Dirección de residencia |
| location | GEOGRAPHY(POINT) | NOT NULL | Ubicación geográfica (POINT) |
| school_name | VARCHAR(255) | NULL | Nombre del colegio |
| school_location | GEOGRAPHY(POINT) | NULL | Ubicación del colegio |
| created_at | TIMESTAMP | NOT NULL, DEFAULT NOW() | Fecha de creación |
| updated_at | TIMESTAMP | NOT NULL, DEFAULT NOW() | Fecha de actualización |

**Índices:**
```sql
CREATE INDEX idx_students_location ON students USING GIST(location);
```

---

### 2.4 guardians

**Descripción:** Acudientes/Padres de estudiantes

| Campo | Tipo | Restricciones | Descripción |
|-------|------|---------------|-------------|
| id | UUID | PK, NOT NULL | Identificador único |
| name | VARCHAR(255) | NOT NULL | Nombre del acudiente |
| phone | VARCHAR(20) | NOT NULL | Teléfono móvil |
| email | VARCHAR(255) | NULL | Email (opcional) |
| fcm_token | TEXT | NULL | Token de Firebase Cloud Messaging |
| created_at | TIMESTAMP | NOT NULL, DEFAULT NOW() | Fecha de creación |
| updated_at | TIMESTAMP | NOT NULL, DEFAULT NOW() | Fecha de actualización |

**Índices:**
```sql
CREATE INDEX idx_guardians_phone ON guardians(phone);
```

---

### 2.5 student_guardians

**Descripción:** Relación muchos a muchos entre estudiantes y acudientes

| Campo | Tipo | Restricciones | Descripción |
|-------|------|---------------|-------------|
| id | UUID | PK, NOT NULL | Identificador único |
| student_id | UUID | FK → students(id), NOT NULL | Estudiante |
| guardian_id | UUID | FK → guardians(id), NOT NULL | Acudiente |
| relationship | VARCHAR(50) | NOT NULL, CHECK IN ('father', 'mother', 'guardian', 'other') | Relación familiar |
| is_emergency_contact | BOOLEAN | NOT NULL, DEFAULT false | Contacto de emergencia |
| notify_events | BOOLEAN | NOT NULL, DEFAULT true | Recibe notificaciones |
| created_at | TIMESTAMP | NOT NULL, DEFAULT NOW() | Fecha de creación |

**Índices:**
```sql
CREATE INDEX idx_student_guardians_student ON student_guardians(student_id);
CREATE INDEX idx_student_guardians_guardian ON student_guardians(guardian_id);
```

---

### 2.6 routes

**Descripción:** Rutas escolares

| Campo | Tipo | Restricciones | Descripción |
|-------|------|---------------|-------------|
| id | UUID | PK, NOT NULL | Identificador único |
| name | VARCHAR(255) | NOT NULL | Nombre de la ruta |
| driver_id | UUID | FK → drivers(id), NOT NULL | Conductor asignado |
| status | VARCHAR(20) | NOT NULL, DEFAULT 'PENDING', CHECK IN ('PENDING', 'IN_PROGRESS', 'COMPLETED', 'CANCELLED') | Estado de la ruta |
| start_time | TIMESTAMP | NULL | Hora de inicio real |
| end_time | TIMESTAMP | NULL | Hora de finalización real |
| scheduled_date | DATE | NOT NULL | Fecha programada de la ruta |
| notes | TEXT | NULL | Notas adicionales |
| created_at | TIMESTAMP | NOT NULL, DEFAULT NOW() | Fecha de creación |
| updated_at | TIMESTAMP | NOT NULL, DEFAULT NOW() | Fecha de actualización |

**Índices:**
```sql
CREATE INDEX idx_routes_driver ON routes(driver_id);
CREATE INDEX idx_routes_status ON routes(status);
CREATE INDEX idx_routes_scheduled_date ON routes(scheduled_date);
```

---

### 2.7 stops

**Descripción:** Paradas de una ruta (ordenadas por estudiante)

| Campo | Tipo | Restricciones | Descripción |
|-------|------|---------------|-------------|
| id | UUID | PK, NOT NULL | Identificador único |
| route_id | UUID | FK → routes(id), NOT NULL | Ruta asociada |
| student_id | UUID | FK → students(id), NOT NULL | Estudiante associated |
| order | INTEGER | NOT NULL, > 0 | Orden de la parada |
| location | GEOGRAPHY(POINT) | NOT NULL | Ubicación de la parada |
| arrival_time | TIMESTAMP | NULL | Hora de llegada real |
| picked_up | BOOLEAN | NOT NULL, DEFAULT false | Ya fue recogido |
| dropped_off | BOOLEAN | NOT NULL, DEFAULT false | Ya fue dejado |
| created_at | TIMESTAMP | NOT NULL, DEFAULT NOW() | Fecha de creación |

**Índices:**
```sql
CREATE INDEX idx_stops_route ON stops(route_id);
CREATE INDEX idx_stops_route_order ON stops(route_id, order);
CREATE INDEX idx_stops_location ON stops USING GIST(location);
```

**Restricciones:**
```sql
-- Una ruta no puede tener dos paradas con el mismo orden
CREATE UNIQUE INDEX idx_stops_unique_order ON stops(route_id, order);
-- Un estudiante no puede estar dos veces en la misma ruta
CREATE UNIQUE INDEX idx_stops_unique_student_route ON stops(route_id, student_id);
```

---

### 2.8 gps_positions

**Descripción:** Historial de posiciones GPS del vehículo

| Campo | Tipo | Restricciones | Descripción |
|-------|------|---------------|-------------|
| id | UUID | PK, NOT NULL | Identificador único |
| driver_id | UUID | FK → drivers(id), NOT NULL | Conductor |
| route_id | UUID | FK → routes(id), NOT NULL | Ruta activa |
| location | GEOGRAPHY(POINT) | NOT NULL | Coordenadas |
| timestamp | TIMESTAMP | NOT NULL | Timestamp del GPS |
| speed | DOUBLE PRECISION | NULL | Velocidad en km/h |
| heading | DOUBLE PRECISION | NULL | Dirección (0-360 grados) |
| accuracy | DOUBLE PRECISION | NULL | Precisión en metros |
| created_at | TIMESTAMP | NOT NULL, DEFAULT NOW() | Fecha de inserción |

**Índices:**
```sql
CREATE INDEX idx_gps_positions_route ON gps_positions(route_id);
CREATE INDEX idx_gps_positions_driver ON gps_positions(driver_id);
CREATE INDEX idx_gps_positions_timestamp ON gps_positions(timestamp);
CREATE INDEX idx_gps_positions_route_time ON gps_positions(route_id, timestamp);
CREATE INDEX idx_gps_positions_location ON gps_positions USING GIST(location);
```

---

### 2.9 student_events

**Descripción:** Eventos críticos del estudiante (BOARD, ARRIVAL, DROP)

| Campo | Tipo | Restricciones | Descripción |
|-------|------|---------------|-------------|
| id | UUID | PK, NOT NULL | Identificador único |
| student_id | UUID | FK → students(id), NOT NULL | Estudiante |
| driver_id | UUID | FK → drivers(id), NOT NULL | Conductor |
| route_id | UUID | FK → routes(id), NOT NULL | Ruta |
| event_type | VARCHAR(20) | NOT NULL, CHECK IN ('BOARD', 'ARRIVAL', 'DROP') | Tipo de evento |
| location | GEOGRAPHY(POINT) | NOT NULL | Ubicación donde ocurrió |
| timestamp | TIMESTAMP | NOT NULL | Hora del evento |
| notes | TEXT | NULL | Notas adicionales |
| created_at | TIMESTAMP | NOT NULL, DEFAULT NOW() | Fecha de creación |

**Nota:** Esta tabla es **inmutable** (INSERT ONLY). No hay UPDATE ni DELETE.

**Índices:**
```sql
CREATE INDEX idx_student_events_student ON student_events(student_id);
CREATE INDEX idx_student_events_route ON student_events(route_id);
CREATE INDEX idx_student_events_timestamp ON student_events(timestamp);
CREATE INDEX idx_student_events_type ON student_events(event_type);
CREATE INDEX idx_student_events_location ON student_events USING GIST(location);
```

---

### 2.10 observations

**Descripción:** Observaciones/novedades reportadas por el conductor

| Campo | Tipo | Restricciones | Descripción |
|-------|------|---------------|-------------|
| id | UUID | PK, NOT NULL | Identificador único |
| student_id | UUID | FK → students(id), NOT NULL | Estudiante |
| driver_id | UUID | FK → drivers(id), NOT NULL | Conductor |
| description | TEXT | NOT NULL | Descripción de la observación |
| photo_url | TEXT | NULL | URL de la foto |
| severity | VARCHAR(20) | NOT NULL, DEFAULT 'LOW', CHECK IN ('LOW', 'MEDIUM', 'HIGH') | Severidad |
| timestamp | TIMESTAMP | NOT NULL | Hora del registro |
| created_at | TIMESTAMP | NOT NULL, DEFAULT NOW() | Fecha de creación |

**Índices:**
```sql
CREATE INDEX idx_observations_student ON observations(student_id);
CREATE INDEX idx_observations_driver ON observations(driver_id);
CREATE INDEX idx_observations_timestamp ON observations(timestamp);
```

---

### 2.11 notifications

**Descripción:** Registro de notificaciones enviadas

| Campo | Tipo | Restricciones | Descripción |
|-------|------|---------------|-------------|
| id | UUID | PK, NOT NULL | Identificador único |
| guardian_id | UUID | FK → guardians(id), NOT NULL | Acudiente receptor |
| title | VARCHAR(255) | NOT NULL | Título de la notificación |
| body | TEXT | NOT NULL | Cuerpo del mensaje |
| type | VARCHAR(50) | NOT NULL | Tipo (BOARD, ARRIVAL, DROP, OBSERVATION) |
| reference_id | UUID | NULL | ID de referencia (evento, observación) |
| sent_at | TIMESTAMP | NOT NULL, DEFAULT NOW() | Fecha de envío |
| read_at | TIMESTAMP | NULL | Fecha de lectura |
| fcm_message_id | VARCHAR(255) | NULL | ID de Firebase |

**Índices:**
```sql
CREATE INDEX idx_notifications_guardian ON notifications(guardian_id);
CREATE INDEX idx_notifications_sent_at ON notifications(sent_at);
CREATE INDEX idx_notifications_read ON notifications(guardian_id, read_at);
```

---

## 3. 🔗 Relaciones

### 3.1 Resumen de Relaciones

| Relación | Tipo | Descripción |
|----------|------|-------------|
| users → drivers | 1:1 | Cada conductor tiene un usuario |
| users → guardians | 1:N | Un usuario puede crear varios guardianes |
| drivers → routes | 1:N | Un conductor puede tener varias rutas |
| routes → stops | 1:N | Una ruta tiene varias paradas |
| students → stops | 1:N | Un estudiante puede estar en varias rutas (diferentes días) |
| students → guardians | N:M | Relación a través de student_guardians |
| drivers → gps_positions | 1:N | Un conductor tiene muchas posiciones GPS |
| routes → student_events | 1:N | Una ruta genera muchos eventos |
| students → observations | 1:N | Un estudiante tiene muchas observaciones |

---

## 4. 🧪 Tipos de Datos PostGIS

### 4.1 Uso de Geography vs Geometry

| Tipo | Uso recomendado |
|------|-----------------|
| `GEOGRAPHY(POINT)` | Cálculos de distancia (meters) |
| `GEOMETRY(POINT)` | Índices más rápidos, menor almacenamiento |

**Recomendación:** Usar `GEOGRAPHY(POINT)` para `location` en:
- `students` (casa y colegio)
- `stops`
- `gps_positions`
- `student_events`

---

### 4.2 Funciones PostGIS Útiles

```sql
-- Distancia entre dos puntos en metros
SELECT ST_Distance(
    ST_SetSRID(ST_MakePoint(-74.0721, 4.7110), 4326)::geography,
    ST_SetSRID(ST_MakePoint(-74.0800, 4.7200), 4326)::geography
);

-- Encontrar paradas dentro de radio (500m)
SELECT * FROM stops
WHERE ST_DWithin(location, ST_SetSRID(ST_MakePoint(-74.0721, 4.7110), 4326)::geography, 500);

-- Obtener punto más cercano
SELECT * FROM stops
ORDER BY location <-> ST_SetSRID(ST_MakePoint(-74.0721, 4.7110), 4326)
LIMIT 1;
```

---

## 5. 📜 Scripts de Creación

### 5.1 Esquema Completo

```sql
-- Habilitar extensión UUID y PostGIS
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS "postgis";

-- ============================================
-- TABLA: users
-- ============================================
CREATE TABLE IF NOT EXISTS users (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    email VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    name VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL CHECK (role IN ('ADMIN', 'DRIVER', 'GUARDIAN')),
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE' CHECK (status IN ('ACTIVE', 'INACTIVE', 'DELETED')),
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    last_login_at TIMESTAMP
);

CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_role ON users(role);
CREATE INDEX idx_users_status ON users(status);

-- ============================================
-- TABLA: drivers
-- ============================================
CREATE TABLE IF NOT EXISTS drivers (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID UNIQUE NOT NULL REFERENCES users(id),
    name VARCHAR(255) NOT NULL,
    phone VARCHAR(20) NOT NULL,
    vehicle_plate VARCHAR(20) NOT NULL,
    vehicle_model VARCHAR(100),
    vehicle_color VARCHAR(50),
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_drivers_user_id ON drivers(user_id);
CREATE INDEX idx_drivers_plate ON drivers(vehicle_plate);

-- ============================================
-- TABLA: students
-- ============================================
CREATE TABLE IF NOT EXISTS students (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name VARCHAR(255) NOT NULL,
    address VARCHAR(500) NOT NULL,
    location GEOGRAPHY(POINT, 4326) NOT NULL,
    school_name VARCHAR(255),
    school_location GEOGRAPHY(POINT, 4326),
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_students_location ON students USING GIST(location);

-- ============================================
-- TABLA: guardians
-- ============================================
CREATE TABLE IF NOT EXISTS guardians (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name VARCHAR(255) NOT NULL,
    phone VARCHAR(20) NOT NULL,
    email VARCHAR(255),
    fcm_token TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_guardians_phone ON guardians(phone);

-- ============================================
-- TABLA: student_guardians
-- ============================================
CREATE TABLE IF NOT EXISTS student_guardians (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    student_id UUID NOT NULL REFERENCES students(id),
    guardian_id UUID NOT NULL REFERENCES guardians(id),
    relationship VARCHAR(50) NOT NULL CHECK (relationship IN ('father', 'mother', 'guardian', 'other')),
    is_emergency_contact BOOLEAN NOT NULL DEFAULT false,
    notify_events BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    UNIQUE(student_id, guardian_id)
);

CREATE INDEX idx_student_guardians_student ON student_guardians(student_id);
CREATE INDEX idx_student_guardians_guardian ON student_guardians(guardian_id);

-- ============================================
-- TABLA: routes
-- ============================================
CREATE TABLE IF NOT EXISTS routes (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name VARCHAR(255) NOT NULL,
    driver_id UUID NOT NULL REFERENCES drivers(id),
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING' CHECK (status IN ('PENDING', 'IN_PROGRESS', 'COMPLETED', 'CANCELLED')),
    start_time TIMESTAMP,
    end_time TIMESTAMP,
    scheduled_date DATE NOT NULL,
    notes TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_routes_driver ON routes(driver_id);
CREATE INDEX idx_routes_status ON routes(status);
CREATE INDEX idx_routes_scheduled_date ON routes(scheduled_date);

-- ============================================
-- TABLA: stops
-- ============================================
CREATE TABLE IF NOT EXISTS stops (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    route_id UUID NOT NULL REFERENCES routes(id),
    student_id UUID NOT NULL REFERENCES students(id),
    order_num INTEGER NOT NULL CHECK (order_num > 0),
    location GEOGRAPHY(POINT, 4326) NOT NULL,
    arrival_time TIMESTAMP,
    picked_up BOOLEAN NOT NULL DEFAULT false,
    dropped_off BOOLEAN NOT NULL DEFAULT false,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    UNIQUE(route_id, order_num),
    UNIQUE(route_id, student_id)
);

CREATE INDEX idx_stops_route ON stops(route_id);
CREATE INDEX idx_stops_location ON stops USING GIST(location);

-- ============================================
-- TABLA: gps_positions
-- ============================================
CREATE TABLE IF NOT EXISTS gps_positions (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    driver_id UUID NOT NULL REFERENCES drivers(id),
    route_id UUID NOT NULL REFERENCES routes(id),
    location GEOGRAPHY(POINT, 4326) NOT NULL,
    timestamp TIMESTAMP NOT NULL,
    speed DOUBLE PRECISION,
    heading DOUBLE PRECISION,
    accuracy DOUBLE PRECISION,
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_gps_positions_route ON gps_positions(route_id);
CREATE INDEX idx_gps_positions_driver ON gps_positions(driver_id);
CREATE INDEX idx_gps_positions_timestamp ON gps_positions(timestamp);
CREATE INDEX idx_gps_positions_route_time ON gps_positions(route_id, timestamp);
CREATE INDEX idx_gps_positions_location ON gps_positions USING GIST(location);

-- ============================================
-- TABLA: student_events
-- ============================================
CREATE TABLE IF NOT EXISTS student_events (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    student_id UUID NOT NULL REFERENCES students(id),
    driver_id UUID NOT NULL REFERENCES drivers(id),
    route_id UUID NOT NULL REFERENCES routes(id),
    event_type VARCHAR(20) NOT NULL CHECK (event_type IN ('BOARD', 'ARRIVAL', 'DROP')),
    location GEOGRAPHY(POINT, 4326) NOT NULL,
    timestamp TIMESTAMP NOT NULL,
    notes TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_student_events_student ON student_events(student_id);
CREATE INDEX idx_student_events_route ON student_events(route_id);
CREATE INDEX idx_student_events_timestamp ON student_events(timestamp);
CREATE INDEX idx_student_events_type ON student_events(event_type);
CREATE INDEX idx_student_events_location ON student_events USING GIST(location);

-- ============================================
-- TABLA: observations
-- ============================================
CREATE TABLE IF NOT EXISTS observations (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    student_id UUID NOT NULL REFERENCES students(id),
    driver_id UUID NOT NULL REFERENCES drivers(id),
    description TEXT NOT NULL,
    photo_url TEXT,
    severity VARCHAR(20) NOT NULL DEFAULT 'LOW' CHECK (severity IN ('LOW', 'MEDIUM', 'HIGH')),
    timestamp TIMESTAMP NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_observations_student ON observations(student_id);
CREATE INDEX idx_observations_driver ON observations(driver_id);
CREATE INDEX idx_observations_timestamp ON observations(timestamp);

-- ============================================
-- TABLA: notifications
-- ============================================
CREATE TABLE IF NOT EXISTS notifications (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    guardian_id UUID NOT NULL REFERENCES guardians(id),
    title VARCHAR(255) NOT NULL,
    body TEXT NOT NULL,
    type VARCHAR(50) NOT NULL,
    reference_id UUID,
    sent_at TIMESTAMP NOT NULL DEFAULT NOW(),
    read_at TIMESTAMP,
    fcm_message_id VARCHAR(255)
);

CREATE INDEX idx_notifications_guardian ON notifications(guardian_id);
CREATE INDEX idx_notifications_sent_at ON notifications(sent_at);
```

---

## 6. ⚙️ Reglas de Negocio a Nivel de Datos

### 6.1 Restricciones Importantes

1. **Un conductor solo puede tener UNA ruta activa a la vez**
   ```sql
   -- Validar en aplicación: antes de iniciar ruta, verificar que no haya otra en estado IN_PROGRESS
   ```

2. **Eventos inmutables** — Solo INSERT permitido en `student_events`

3. **Secuencia de eventos** — Un estudiante no puede tener DROP antes de BOARD en la misma ruta

4. **Una parada no se puede repetir** en la misma ruta para el mismo estudiante

5. **Un estudiante puede tener múltiples guardianes** pero al menos uno debe ser contacto de emergencia

---

## 7. 📈 Consultas Frecuentes (Optimización)

### 7.1 Posición actual del vehículo

```sql
SELECT * FROM gps_positions
WHERE route_id = :routeId
ORDER BY timestamp DESC
LIMIT 1;
```

### 7.2 Estudiantes de una ruta con información de recogida

```sql
SELECT 
    s.id,
    s.name,
    s.address,
    st.order_num,
    st.picked_up,
    st.dropped_off,
    st.arrival_time
FROM stops st
JOIN students s ON st.student_id = s.id
WHERE st.route_id = :routeId
ORDER BY st.order_num;
```

### 7.3 Último evento de un estudiante en una ruta

```sql
SELECT * FROM student_events
WHERE student_id = :studentId AND route_id = :routeId
ORDER BY timestamp DESC
LIMIT 1;
```

### 7.4 Historial de eventos de un estudiante

```sql
SELECT 
    se.event_type,
    se.timestamp,
    r.name as route_name
FROM student_events se
JOIN routes r ON se.route_id = r.id
WHERE se.student_id = :studentId
ORDER BY se.timestamp DESC;
```

### 7.5 Acudientes con notificaciones pendientes

```sql
SELECT 
    g.id,
    g.name,
    g.phone,
    COUNT(n.id) as unread_count
FROM guardians g
LEFT JOIN notifications n ON n.guardian_id = g.id AND n.read_at IS NULL
WHERE g.id IN (
    SELECT guardian_id FROM student_guardians 
    WHERE student_id = :studentId AND notify_events = true
)
GROUP BY g.id, g.name, g.phone;
```

---

## 8. 🧪 Migraciones y seeders

### 8.1 Pendiente

- [ ] Definir estrategia de migraciones (Flyway, Liquibase, manual)
- [ ] Scripts de seeding para datos de prueba
- [ ] Scripts de migración para versiones futuras

---

## 9. 📝 Convenciones

| Convención | Regla |
|------------|-------|
| **Tablas** | snake_case plural (users, student_events) |
| **Campos** | snake_case (created_at, vehicle_plate) |
| **PK** | UUID con valor por defecto `uuid_generate_v4()` |
| **Fechas** | `TIMESTAMP NOT NULL DEFAULT NOW()` |
| **Soft delete** | Campo `status` con valores 'ACTIVE', 'INACTIVE', 'DELETED' |
| **Enums** | VARCHAR con CHECK constraint |
| **Índices** | Nombrar `idx_tabla_campo` |
| **FK** | Nombrar `tabla_id` (driver_id, student_id) |

---

## 10. 📌 Pendiente por Definir

- [ ] Estrategia de backups
- [ ] Replication (si aplica)
- [ ] Particionamiento de tablas grandes (gps_positions por fecha)
- [ ] Políticas de retención de datos GPS
- [ ] Scripts de migrate/up/down
- [ ] Datos de prueba (seeders)
- [ ] Vistas materializadas para reportes
