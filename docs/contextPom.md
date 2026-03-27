# POM Context

<!-- Documentación de dependencias y configuración del proyecto Maven -->
<!-- SafeRoute - Project Object Model (POM) -->

---

## 1. 📋 Visión General

Este documento describe la estructura y dependencias del proyecto SafeRoute basadas en Maven.

### 1.1 Información del Proyecto

```xml
<groupId>com.saferoute</groupId>
<artifactId>saferoute</artifactId>
<version>0.0.1-SNAPSHOT</version>
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>4.0.5</version>
</parent>
```

| Campo | Valor | Descripción |
|-------|-------|-------------|
| `groupId` | `com.saferoute` | Identificador del grupo |
| `artifactId` | `saferoute` | Nombre del proyecto |
| `version` | `0.0.1-SNAPSHOT` | Versión actual |
| `parent` | Spring Boot `4.0.5` | Padre de Maven |

---

## 2. 🏗️ Dependencias Principal

### 2.1 Spring Boot Starters

| Starter | Dependencia | Propósito |
|---------|-------------|-----------|
| Web MVC | `spring-boot-starter-webmvc` | Desarrollo web REST |
| Data JPA | `spring-boot-starter-data-jpa` | Acceso a datos con Hibernate |
| Security | `spring-boot-starter-security` | Autenticación y autorización |
| Validation | `spring-boot-starter-validation` | Validación de beans |

```xml
<!-- Web MVC -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-webmvc</artifactId>
</dependency>

<!-- Data JPA -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>

<!-- Security -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>

<!-- Validation -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-validation</artifactId>
</dependency>
```

---

### 2.2 Base de Datos

#### PostgreSQL Driver

```xml
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
    <scope>runtime</scope>
</dependency>
```

| Propiedad | Valor |
|-----------|-------|
| Driver | `org.postgresql.Driver` |
| Versión |Gestionada por Spring Boot Parent |
| Scope | `runtime` (solo ejecución) |

#### Hibernate Spatial (PostGIS)

```xml
<dependency>
    <groupId>org.hibernate.orm</groupId>
    <artifactId>hibernate-spatial</artifactId>
</dependency>

<dependency>
    <groupId>org.locationtech.jts</groupId>
    <artifactId>jts-core</artifactId>
    <version>1.18.2</version>
</dependency>
```

| Dependencia | Propósito |
|------------|-----------|
| `hibernate-spatial` | Integración Hibernate + PostGIS |
| `jts-core` | Java Topology Suite - Tipos geométricos |

---

### 2.3 Seguridad y JWT

```xml
<!-- JWT API -->
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
    <version>0.12.5</version>
</dependency>

<!-- JWT Implementation -->
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-impl</artifactId>
    <version>0.12.5</version>
    <scope>runtime</scope>
</dependency>

<!-- JWT Jackson Support -->
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-jackson</artifactId>
    <version>0.12.5</version>
    <scope>runtime</scope>
</dependency>
```

| Artefacto | Propósito |
|-----------|-----------|
| `jjwt-api` | Interfaces y APIs públicas |
| `jjwt-impl` | Implementación de JWT (HS256) |
| `jjwt-jackson` | Serialización JSON para JWT |

---

### 2.4 Documentación API

```xml
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.5.0</version>
</dependency>
```

| Artefacto | Propósito |
|-----------|-----------|
| `springdoc-openapi-starter-webmvc-ui` | Swagger/OpenAPI UI |

**Endpoints:**
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

---

### 2.5 Utilidades

```xml
<!-- Lombok -->
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <optional>true</optional>
</dependency>

<!-- DevTools -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-devtools</artifactId>
    <scope>runtime</scope>
    <optional>true</optional>
</dependency>
```

| Dependencia | Propósito |
|------------|-----------|
| `lombok` | Reducción de boilerplate ( getters, setters, builders) |
| `spring-boot-devtools` | Recarga automática en desarrollo |

---

## 3. 🧪 Dependencias de Test

```xml
<!-- Test Starters -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa-test</artifactId>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security-test</artifactId>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-validation-test</artifactId>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-webmvc-test</artifactId>
    <scope>test</scope>
</dependency>
```

| Starter de Test | Propósito |
|-----------------|-----------|
| `data-jpa-test` | Testing de repositorios |
| `security-test` | Testing de seguridad |
| `validation-test` | Testing de validaciones |
| `webmvc-test` | Testing de controladores |

---

## 4. ⚙️ Configuración del Build

### 4.1 Plugins

```xml
<build>
    <plugins>
        <!-- Maven Compiler Plugin -->
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-compiler-plugin</artifactId>
            <configuration>
                <annotationProcessorPaths>
                    <path>
                        <groupId>org.projectlombok</groupId>
                        <artifactId>lombok</artifactId>
                    </path>
                </annotationProcessorPaths>
            </configuration>
        </plugin>

        <!-- Spring Boot Maven Plugin -->
        <plugin>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-maven-plugin</artifactId>
            <configuration>
                <excludes>
                    <exclude>
                        <groupId>org.projectlombok</groupId>
                        <artifactId>lombok</artifactId>
                    </exclude>
                </excludes>
            </configuration>
        </plugin>
    </plugins>
</build>
```

### 4.2 Versiones

| Componente | Versión |
|------------|---------|
| Java | `21` |
| Spring Boot | `4.0.5` |
| PostgreSQL Driver | Gestionado por parent |
| Hibernate ORM | Gestionado por parent |
| jjwt | `0.12.5` |
| springdoc-openapi | `2.5.0` |
| Lombok | Gestionado por parent |
| JTS Core | `1.18.2` |

---

## 5. 📦 Estructura de Dependencias

```
spring-boot-starter-parent (4.0.5)
│
├── spring-boot-starter-webmvc
│   ├── spring-boot-starter
│   ├── spring-boot-starter-json
│   ├── spring-web
│   └── spring-webmvc
│
├── spring-boot-starter-data-jpa
│   ├── spring-boot-starter
│   ├── jakarta.persistence-api
│   ├── spring-data-jpa
│   └── hibernate-core
│
├── spring-boot-starter-security
│   ├── spring-boot-starter
│   ├── spring-security-core
│   ├── spring-security-web
│   └── spring-security-config
│
├── spring-boot-starter-validation
│   ├── spring-boot-starter
│   ├── jakarta.validation-api
│   └── hibernate-validator
│
├── hibernate-spatial
│   └── hibernate-core
│
└── jjwt (0.12.5)
```

---

## 6. 🔄 Gestión de Versiones

### 6.1 Versiones Gestionados por Spring Boot Parent

| Dependencia | Versión |
|-------------|---------|
| Spring Framework | `6.2.x` |
| Tomcat | `10.1.x` |
| Hibernate | `6.4.x` |
| Jackson | `2.17.x` |
| Logback | `1.5.x` |
| SLF4J | `2.0.x` |

### 6.2 Versiones Explícitas Definidas

| Dependencia | Versión | Razón |
|-------------|---------|-------|
| `jjwt-api` | `0.12.5` | Última versión estable |
| `springdoc-openapi` | `2.5.0` | Compatible con Spring Boot 3.x |
| `jts-core` | `1.18.2` | Última versión LTS |

---

## 7. 📋 Dependency Tree (Resumen)

```
com.saferoute:saferoute:0.0.1-SNAPSHOT
├── org.springframework.boot:spring-boot-starter-webmvc
├── org.springframework.boot:spring-boot-starter-data-jpa
├── org.springframework.boot:spring-boot-starter-security
├── org.springframework.boot:spring-boot-starter-validation
├── org.postgresql:postgresql (runtime)
├── org.hibernate.orm:hibernate-spatial
├── org.locationtech.jts:jts-core:1.18.2
├── io.jsonwebtoken:jjwt-api:0.12.5
├── io.jsonwebtoken:jjwt-impl:0.12.5 (runtime)
├── io.jsonwebtoken:jjwt-jackson:0.12.5 (runtime)
├── org.springdoc:springdoc-openapi-starter-webmvc-ui:2.5.0
├── org.projectlombok:lombok (optional)
└── org.springframework.boot:spring-boot-devtools (runtime, optional)
```

---

## 8. ⚠️ Notas Importantes

### 8.1 Scope `runtime`

Las dependencias con `scope: runtime` no son necesarias para compilación, solo para ejecución:

- `postgresql` - Driver JDBC
- `jjwt-impl` - Implementación JWT
- `jjwt-jackson` - Serialización Jackson

### 8.2 Scope `test`

Las dependencias de test solo están disponibles en la fase de test:

```bash
mvn test
```

### 8.3 Lombok y Annotation Processing

Para que Lombok funcione correctamente con IDEs:

```xml
<annotationProcessorPaths>
    <path>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
    </path>
</annotationProcessorPaths>
```

---

## 9. 🚀 Comandos Útiles

### 9.1 Ver Dependencias

```bash
# Listar todas las dependencias
mvn dependency:tree

# Filtrar dependencias específicas
mvn dependency:tree -Dincludes=com.saferoute

# Dependencias no usadas
mvn dependency:analyze
```

### 9.2 Compilación

```bash
# Compilar proyecto
mvn compile

# Compilar con tests
mvn test-compile

# Package (JAR)
mvn package
```

### 9.3 Ejecución

```bash
# Ejecutar aplicación
mvn spring-boot:run

# Con perfil específico
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

---

## 10. 📚 Referencias

- [Spring Boot Reference - Build Tools](https://docs.spring.io/spring-boot/docs/current/reference/html/build-tools.html#build-tool-maven)
- [Maven Dependency Scope](https://maven.apache.org/guides/introduction/introduction-to-dependency-mechanism.html#dependency-scope)
- [Hibernate Spatial Documentation](https://docs.jboss.org/hibernate/orm/current/userguide/html_single/Hibernate_User_Guide.html#spatial)
- [JJWT Library](https://github.com/jwtk/jjwt)
- [SpringDoc OpenAPI](https://springdoc.org/)
