# Properties Context

<!-- Documentación de configuraciones de application.properties -->
<!-- safeRoute - Configuración de Propiedades -->

---

## 1. 📋 Visión General

Este documento describe todas las configuraciones de propiedades de la aplicación SafeRoute, organizadas por perfil (dev, prod) y propósito.

### 1.1 Archivos de Configuración

| Archivo | Propósito | Perfil Activo por Defecto |
|---------|-----------|---------------------------|
| `application.properties` | Configuración principal | - |
| `application-dev.properties` | Desarrollo local | `dev` |
| `application-prod.properties` | Producción | `prod` |

### 1.2 Perfiles de Spring

```bash
# Activar perfil específico
spring.profiles.active=dev    # Desarrollo
spring.profiles.active=prod   # Producción
```

---

## 2. ⚙️ Configuración Principal (application.properties)

### 2.1 Identificación de la Aplicación

```properties
spring.application.name=saferoute
```

| Propiedad | Descripción | Valor por Defecto |
|-----------|-------------|-------------------|
| `spring.application.name` | Nombre de la aplicación | `saferoute` |

---

### 2.2 Configuración de Base de Datos (PostgreSQL + PostGIS)

```properties
# ===================================================================
# 📊 Database Configuration (PostgreSQL + PostGIS)
# ===================================================================
spring.datasource.url=jdbc:postgresql://localhost:5432/saferoute
spring.datasource.username=postgres
spring.datasource.password=postgres
spring.datasource.driver-class-name=org.postgresql.Driver
```

| Propiedad | Descripción | Ejemplo |
|-----------|-------------|---------|
| `spring.datasource.url` | URL de conexión JDBC | `jdbc:postgresql://host:5432/dbname` |
| `spring.datasource.username` | Usuario de PostgreSQL | `postgres` |
| `spring.datasource.password` | Contraseña de PostgreSQL | `********` |
| `spring.datasource.driver-class-name` | Driver JDBC | `org.postgresql.Driver` |

#### Connection Pool (HikariCP)

```properties
spring.datasource.hikari.maximum-pool-size=10
spring.datasource.hikari.minimum-idle=5
spring.datasource.hikari.connection-timeout=30000
spring.datasource.hikari.idle-timeout=600000
spring.datasource.hikari.max-lifetime=1800000
```

| Propiedad | Descripción | Valor por Defecto |
|-----------|-------------|-------------------|
| `hikari.maximum-pool-size` | Máx conexiones en pool | `10` |
| `hikari.minimum-idle` | Mín conexiones inactivas | `5` |
| `hikari.connection-timeout` | Timeout conexión (ms) | `30000` |
| `hikari.idle-timeout` | Timeout inactividad (ms) | `600000` |
| `hikari.max-lifetime` | Vida máx conexión (ms) | `1800000` |

---

### 2.3 Configuración JPA/Hibernate

```properties
# ===================================================================
# JPA / Hibernate
# ===================================================================
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.dialect=org.hibernate.spatial.dialect.postgis.PostgisDialect
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.properties.hibernate.jdbc.batch_size=20
spring.jpa.properties.hibernate.order_inserts=true
spring.jpa.properties.hibernate.order_updates=true
```

| Propiedad | Descripción | Valores Posibles |
|-----------|-------------|------------------|
| `spring.jpa.hibernate.ddl-auto` | Acción DDL automática | `none`, `update`, `validate`, `create`, `create-drop` |
| `spring.jpa.show-sql` | Mostrar SQL en logs | `true`, `false` |
| `hibernate.dialect` | Dialecto de Hibernate | `PostgisDialect` para PostGIS |
| `hibernate.format_sql` | Formatear SQL | `true`, `false` |
| `hibernate.jdbc.batch_size` | Tamaño de batch JDBC | `20` |
| `hibernate.order_inserts` | Ordenar inserts en batch | `true` |
| `hibernate.order_updates` | Ordenar updates en batch | `true` |

#### Valores de `ddl-auto` por Perfil

| Perfil | Recomendación | Razón |
|--------|---------------|-------|
| **dev** | `update` |方便开发，自动同步entities |
| **test** | `create-drop` |每次重建数据库 |
| **prod** | `validate` | No modifica esquema, solo valida |

---

### 2.4 Configuración JWT

```properties
# ===================================================================
# 🔐 JWT Configuration
# ===================================================================
app.jwt.secret=YourSuperSecretKeyForJWTTokenGenerationMustBeAtLeast256BitsLong!
app.jwt.access-token-expiration=900000    # 15 minutos en milisegundos
app.jwt.refresh-token-expiration=604800000 # 7 días en milisegundos
```

| Propiedad | Descripción | Ejemplo |
|-----------|-------------|---------|
| `app.jwt.secret` | Clave secreta para firmar tokens | cadena min 256 bits |
| `app.jwt.access-token-expiration` | Expiración access token | `900000` (15 min) |
| `app.jwt.refresh-token-expiration` | Expiración refresh token | `604800000` (7 días) |

#### Tiempos de Expiración Recomendados

| Token | Desarrollo | Producción |
|-------|------------|------------|
| Access Token | 15-30 minutos | 15 minutos |
| Refresh Token | 7-30 días | 7 días |

---

### 2.5 Configuración del Servidor

```properties
# ===================================================================
# 🌐 Server Configuration
# ===================================================================
server.port=8080
server.servlet.context-path=/
```

| Propiedad | Descripción | Valor por Defecto |
|-----------|-------------|-------------------|
| `server.port` | Puerto HTTP | `8080` |
| `server.servlet.context-path` | Path base | `/` |

---

### 2.6 Configuración de Logging

```properties
# ===================================================================
# 📝 Logging
# ===================================================================
logging.level.root=INFO
logging.level.com.saferoute=DEBUG
logging.level.org.springframework.web=INFO
logging.level.org.springframework.security=DEBUG
logging.level.org.hibernate.SQL=DEBUG
logging.level.org.hibernate.type.descriptor.sql.BasicBinder=TRACE
```

| Propiedad | Descripción |
|-----------|-------------|
| `logging.level.root` | Nivel raíz de logging |
| `logging.level.com.saferoute` | Nivel para paquetes de la app |
| `logging.level.org.springframework.*` | Nivel para Spring |
| `logging.level.org.hibernate.*` | Nivel para Hibernate |

#### Niveles de Log

```
TRACE < DEBUG < INFO < WARN < ERROR
```

| Nivel | Uso |
|-------|-----|
| `TRACE` | Detalle máximo (parámetros SQL) |
| `DEBUG` | Información de desarrollo |
| `INFO` | Información general |
| `WARN` | Advertencias |
| `ERROR` | Errores |

---

### 2.7 Configuración Jackson (JSON)

```properties
# ===================================================================
# 🔄 Jackson Configuration
# ===================================================================
spring.jackson.serialization.write-dates-as-timestamps=false
spring.jackson.time-zone=America/Bogota
spring.jackson.default-property-inclusion=non_null
```

| Propiedad | Descripción | Valor |
|-----------|-------------|-------|
| `write-dates-as-timestamps` | Formato de fechas | `false` (ISO-8601) |
| `time-zone` | Zona horaria | `America/Bogota` |
| `default-property-inclusion` | Inclusión de propiedades | `non_null` |

---

### 2.8 Perfiles y Beans

```properties
# ===================================================================
# 🏗️ Spring Profiles
# ===================================================================
spring.profiles.active=dev

# ===================================================================
# 📦 Bean Configuration
# ===================================================================
spring.main.allow-bean-definition-overriding=false
```

| Propiedad | Descripción |
|-----------|-------------|
| `spring.profiles.active` | Perfil activo |
| `spring.main.allow-bean-definition-overriding` | Permitir override de beans |

---

## 3. 🛠️ Perfil de Desarrollo (application-dev.properties)

```properties
# ===================================================================
# 🚍 SafeRoute - Development Profile Configuration
# ===================================================================

# Database (Local)
spring.datasource.url=jdbc:postgresql://localhost:5432/saferoute
spring.datasource.username=postgres
spring.datasource.password=postgres

# Show SQL in console (dev only)
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

# Logging
logging.level.com.saferoute=DEBUG
logging.level.org.hibernate.SQL=DEBUG
```

### Características del Perfil Dev

| Configuración | Valor | Propósito |
|---------------|-------|-----------|
| Base de datos | `localhost:5432` | Desarrollo local |
| DDL Auto | `update` | Sincronización automática |
| Show SQL | `true` | Depuración de queries |
| Formato SQL | `true` | SQL legible |
| Logging | `DEBUG` | Máxima información |

---

## 4. ☁️ Perfil de Producción (application-prod.properties)

```properties
# ===================================================================
# 🚍 SafeRoute - Production Profile Configuration
# ===================================================================

# Database (Production - Set via environment variables)
spring.datasource.url=${DB_URL:jdbc:postgresql://localhost:5432/saferoute}
spring.datasource.username=${DB_USERNAME:postgres}
spring.datasource.password=${DB_PASSWORD:}

# Hibernate - validate only in production (don't auto-update)
spring.jpa.hibernate.ddl-auto=validate

# Hide SQL in production
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.format_sql=false

# Logging - less verbose
logging.level.root=WARN
logging.level.com.saferoute=INFO
logging.level.org.springframework.web=WARN

# Security
server.servlet.session.cookie.secure=true
server.servlet.session.cookie.http-only=true
```

### Características del Perfil Prod

| Configuración | Valor | Propósito |
|---------------|-------|-----------|
| Base de datos | Variables de entorno | Seguridad |
| DDL Auto | `validate` | No modifica esquema |
| Show SQL | `false` | Ocultar queries |
| Logging | `WARN/INFO` | Menor verbosidad |
| Cookies | `secure + http-only` | Seguridad |

### Variables de Entorno para Producción

| Variable | Descripción | Ejemplo |
|----------|-------------|---------|
| `DB_URL` | URL de PostgreSQL | `jdbc:postgresql://prod-db:5432/saferoute` |
| `DB_USERNAME` | Usuario | `saferoute_app` |
| `DB_PASSWORD` | Contraseña | `********` |
| `JWT_SECRET` | Clave JWT | `********` |

---

## 5. 🔧 Configuración de PostGIS

### 5.1 Requisitos Previos

```sql
-- En PostgreSQL, crear la base de datos con PostGIS
CREATE DATABASE saferoute;

\c saferoute;

CREATE EXTENSION IF NOT EXISTS postgis;
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
```

### 5.2 Configuración Hibernate Spatial

```properties
# Dialecto PostGIS
spring.jpa.properties.hibernate.dialect=org.hibernate.spatial.dialect.postgis.PostgisDialect
```

### 5.3 Tipos Geoespaciales Soportados

| Tipo Java | Tipo PostGIS | Uso |
|-----------|--------------|-----|
| `Point` | `GEOGRAPHY(POINT)` | Coordenadas GPS |
| `LineString` | `GEOGRAPHY(LINESTRING)` | Rutas |
| `Polygon` | `GEOGRAPHY(POLYGON)` | Zonas |

---

## 6. 📝 Ejemplo de Configuración Completa

### Desarrollo Local

```bash
# Variables de entorno (opcional para dev)
export DB_URL=jdbc:postgresql://localhost:5432/saferoute
export DB_USERNAME=postgres
export DB_PASSWORD=postgres

# Ejecutar
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

### Producción

```bash
# Variables de entorno obligatorias
export DB_URL=jdbc:postgresql://prod-server:5432/saferoute_prod
export DB_USERNAME=saferoute_app
export DB_PASSWORD=secure_password_here
export JWT_SECRET=your_256_bit_secret_key_here

# Ejecutar
mvn spring-boot:run -Dspring-boot.run.profiles=prod
```

---

## 7. ⚠️ Notas de Seguridad

### 7.1 Secrets en Producción

| Item | Acción Requerida |
|------|------------------|
| `app.jwt.secret` | Cambiar por clave segura (mín 256 bits) |
| `spring.datasource.password` | Usar variable de entorno |
| `spring.datasource.url` | Apuntar a DB segura |

### 7.2 Configuración No Recomendada

```properties
# ❌ NO HACER - Valores inseguros para producción
spring.jpa.hibernate.ddl-auto=create
spring.jpa.show-sql=true
logging.level.root=DEBUG
app.jwt.secret=secret
```

---

## 8. 📌 Checklist de Configuración

- [ ] Base de datos PostgreSQL creada
- [ ] PostGIS habilitado en la base de datos
- [ ] Credenciales de base de datos configuradas
- [ ] JWT secret cambiada (producción)
- [ ] Perfil activo seleccionado
- [ ] Logging configurado según entorno
- [ ] Connection pool tuneado para producción

---

## 9. 📚 Referencias

- [Spring Boot Reference - Externalized Configuration](https://docs.spring.io/spring-boot/docs/current/reference/html/spring-boot-features.html#boot-features-external-config)
- [HikariCP Configuration](https://github.com/brettwooldridge/HikariCP#configuration-knobs-baby)
- [Hibernate Spatial - PostGIS](https://docs.jboss.org/hibernate/orm/current/userguide/html_single/Hibernate_User_Guide.html#spatial)
