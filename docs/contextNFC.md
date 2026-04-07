# NFC Context - Sistema de Identificación de Estudiantes

<!-- SafeRoute NFC Implementation Guide -->

---

## 1. 📌 Visión General

El sistema NFC permite identificar estudiantes mediante tarjetas RFID/NFC al abordar o descender del vehículo escolar. Proporciona:

- **Trazaibilidad**: Registro automático de eventos BOARD/DROP
- **Seguridad**: Verificación de identidad del estudiante
- **Eficiencia**: Elimina necesidad de registro manual
- **Histórico**: Mantiene historial de todas las tarjetas asignadas

---

## 2. 🎯 Objetivos del Sistema

### 2.1 Objetivo Principal
Automatizar el registro de eventos de estudiantes (BOARD/DROP) mediante el escaneo de tarjetas NFC, eliminando el registro manual por parte del conductor.

### 2.2 Objetivos Específicos
1. Asignar tarjetas NFC a estudiantes de forma segura
2. Mantener un solo NFC activo por estudiante
3. Escanear NFC para detectar estudiante rápidamente
4. Integrar con eventos de ruta automáticamente
5. Mantener historial de todas las asignaciones

---

## 3. 🔄 Casos de Uso

### 3.1 UC1: Asignar NFC a Estudiante (ADMIN)

**Actor**: Administrador

**Flujo**:
1. ADMIN prepara la tarjeta NFC física
2. ADMIN envía request POST `/api/v1/students/{id}/nfc`
3. Sistema valida que el UID no esté asignado a otro estudiante
4. Sistema desactiva cualquier NFC activo previo del estudiante
5. Sistema crea nuevo registro NFC como activo
6. Sistema retorna respuesta con el NFC asignado

**Reglas de negocio**:
- Solo ADMIN puede asignar NFCs
- El UID debe ser único en todo el sistema
- Solo puede haber un NFC activo por estudiante
- El NFC anterior se inactiva automáticamente (no se elimina)

**Request**:
```json
POST /api/v1/students/{studentId}/nfc
{
  "nfcUid": "ABC123456789",
  "notes": "Nueva tarjeta - reemplaza la anterior"
}
```

**Response** (201):
```json
{
  "id": "uuid",
  "studentId": "uuid",
  "studentName": "Juan Pérez",
  "nfcUid": "ABC123456789",
  "isActive": true,
  "assignedAt": "2026-04-06T10:00:00",
  "deactivatedAt": null,
  "assignedBy": "uuid-del-admin",
  "notes": "Nueva tarjeta - reemplaza la anterior",
  "createdAt": "2026-04-06T10:00:00",
  "updatedAt": "2026-04-06T10:00:00"
}
```

**Errores**:
- `400`: UID del NFC inválido o vacío
- `404`: Estudiante no encontrado
- `409`: El UID ya está asignado a otro estudiante

---

### 3.2 UC2: Escanear NFC (DRIVER)

**Actor**: Conductor durante una ruta activa

**Flujo**:
1. DRIVER escanea tarjeta NFC con el dispositivo
2. App móvil envía request POST `/api/v1/nfc/scan`
3. Sistema busca NFC activo por UID
4. Sistema retorna estudiante asociado
5. App móvil muestra info del estudiante
6. (Opcional) App pregunta si el estudiante sube o baja

**Reglas de negocio**:
- DRIVER debe tener una ruta activa (IN_PROGRESS)
- El NFC debe estar activo
- El sistema debe determinar si es BOARD o DROP

**Request**:
```json
POST /api/v1/nfc/scan
{
  "nfcUid": "ABC123456789"
}
```

**Response** (200):
```json
{
  "id": "uuid",
  "studentId": "uuid",
  "studentName": "Juan Pérez",
  "nfcUid": "ABC123456789",
  "isActive": true,
  "assignedAt": "2026-04-06T10:00:00"
}
```

**Errores**:
- `404`: NFC no encontrado o inactivo

---

### 3.3 UC3: Registrar Evento desde NFC

**Actor**: Sistema (después de escanear)

**Flujo completo**:
1. DRIVER escanea NFC
2. Sistema retorna estudiante
3. App determina acción (BOARD o DROP):
   - Busca último evento del estudiante en la ruta actual
   - Si no hay eventos → es BOARD (sube)
   - Si último evento es BOARD → es DROP (baja)
   - Si último evento es DROP → es BOARD (sube de nuevo)
4. Sistema crea evento STUDENT_EVENT (BOARD o DROP)
5. Sistema envía notificación al acudiente

**Lógica de determinación de evento**:
```
function determineEventType(studentId, routeId):
    lastEvent = getLastEventForStudentInRoute(studentId, routeId)
    
    if lastEvent == null:
        return BOARD  // Primer evento - sube al vehículo
    else if lastEvent.eventType == BOARD:
        return DROP   // Ya subió - ahora baja
    else if lastEvent.eventType == DROP:
        return BOARD  // Ya bajó - puede subir de nuevo (ej: diferente vuelta)
    else if lastEvent.eventType == ARRIVAL:
        return DROP   // Llegó al colegio - ahora vuelve a casa
```

---

### 3.4 UC4: Verificar Disponibilidad de Conductor (previo a NFC)

**Validación previa**:
Para que un conductor pueda usar el sistema NFC durante una ruta, debe:
1. Tener `isVerified = true` (verificado por ADMIN)
2. Tener un vehículo asignado
3. Tener licencia vigente (documento activo)
4. El vehículo debe tener todos los documentos obligatorios vigentes

El conductor puede consultar su disponibilidad:
```
GET /api/v1/drivers/{id}/availability
```

**Response**:
```json
{
  "available": true,
  "reason": null,
  "documentsRequired": ["LICENCIA", "SOAP", "SEGURO", "TECNOMECANICA", "TARJETA_PROPIEDAD"],
  "documentsMissing": [],
  "documentsExpired": []
}
```

---

## 4. 🔧 Flujo Técnico Completo

### 4.1 Diagrama de Secuencia (Escaneo → Evento)

```
┌─────────┐     ┌─────────┐     ┌─────────┐     ┌─────────┐     ┌─────────┐
│ Driver  │     │  App    │     │  API    │     │ Service │     │   DB    │
└────┬────┘     └────┬────┘     └────┬────┘     └────┬────┘     └────┬────┘
     │               │               │               │               │
     │ 1.Scan NFC   │               │               │               │
     │─────────────►│               │               │               │
     │               │ 2.POST scan  │               │               │
     │               │─────────────►│               │               │
     │               │               │ 3.Find NFC   │               │
     │               │               │─────────────►│               │
     │               │               │               │ 4.Query DB   │
     │               │               │               │─────────────►│
     │               │               │               │◄─────────────│
     │               │               │◄─────────────│               │
     │               │◄─────────────│               │               │
     │◄─────────────│               │               │               │
     │               │               │               │               │
     │ 5.Show student              │               │               │
     │               │               │               │               │
     │ 6.Confirm action             │               │               │
     │ (BOARD/DROP)│               │               │               │
     │               │               │               │               │
     │               │ 7.POST event │               │               │
     │               │─────────────►│               │               │
     │               │               │ 8.Create    │               │
     │               │               │  event       │               │
     │               │               │─────────────►│               │
     │               │               │               │ 9.Insert    │
     │               │               │               │─────────────►│
     │               │               │               │◄─────────────│
     │               │               │               │◄─────────────│
     │               │               │◄─────────────│               │
     │               │◄─────────────│               │               │
     │◄─────────────│               │               │               │
     │               │               │               │               │
     │ 10.Send notification         │               │               │
```

### 4.2 Flujo de Datos

```
NFC Scan Request
       │
       ▼
┌──────────────────┐
│  ScanNfcUseCase  │
│  - Validar input │
│  - Buscar NFC    │
│  - Retornar stud │
└──────────────────┘
       │
       ▼
┌──────────────────────────────┐
│ Determinar Evento (BOARD/DROP)│
│ - Consultar último evento     │
│ - Aplicar lógica de transición│
└──────────────────────────────┘
       │
       ▼
┌──────────────────────────────┐
│   CreateStudentEventUseCase  │
│ - Crear evento               │
│ - Guardar en DB              │
│ - Enviar notificación         │
└──────────────────────────────┘
       │
       ▼
    Event Created
    + Notification sent
```

---

## 5. 📡 Especificación de Endpoints

### 5.1 Endpoints NFC

| Método | Endpoint | Acceso | Descripción |
|--------|----------|--------|-------------|
| POST | `/api/v1/students/{id}/nfc` | ADMIN | Asignar NFC |
| GET | `/api/v1/students/{id}/nfc` | ADMIN/DRIVER | Obtener NFC activo |
| DELETE | `/api/v1/students/{id}/nfc` | ADMIN | Desactivar NFC |
| GET | `/api/v1/students/{id}/nfc/history` | ADMIN | Ver historial |
| POST | `/api/v1/nfc/scan` | ADMIN/DRIVER | Escanear NFC |

### 5.2 Endpoints de Integración (Eventos)

| Método | Endpoint | Acceso | Descripción |
|--------|----------|--------|-------------|
| POST | `/api/v1/events/board` | DRIVER | Registrar BOARD manual |
| POST | `/api/v1/events/drop` | DRIVER | Registrar DROP manual |
| POST | `/api/v1/events/from-nfc` | DRIVER | Registrar evento desde NFC |

### 5.3 Endpoints de Disponibilidad

| Método | Endpoint | Acceso | Descripción |
|--------|----------|--------|-------------|
| GET | `/api/v1/drivers/{id}/availability` | ADMIN | Verificar disponibilidad |

---

## 6. 🏗️ Estructura de Datos

### 6.1 Tabla: student_nfc

```sql
CREATE TABLE student_nfc (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    student_id      UUID NOT NULL REFERENCES students(id),
    nfc_uid         VARCHAR(100) NOT NULL UNIQUE,
    is_active       BOOLEAN NOT NULL DEFAULT true,
    assigned_at     TIMESTAMP NOT NULL DEFAULT NOW(),
    deactivated_at  TIMESTAMP,
    assigned_by     UUID REFERENCES users(id),
    notes           VARCHAR(500),
    created_at      TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP NOT NULL DEFAULT NOW()
);

-- Índices
CREATE INDEX idx_student_nfc_student ON student_nfc(student_id);
CREATE INDEX idx_student_nfc_uid ON student_nfc(nfc_uid);
CREATE INDEX idx_student_nfc_active ON student_nfc(student_id, is_active);
```

### 6.2 Entity: StudentNfcEntity

```java
@Entity
@Table(name = "student_nfc")
public class StudentNfcEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private StudentEntity student;

    @Column(name = "nfc_uid", nullable = false, unique = true)
    private String nfcUid;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @Column(name = "assigned_at")
    private LocalDateTime assignedAt;

    @Column(name = "deactivated_at")
    private LocalDateTime deactivatedAt;

    @Column(name = "assigned_by")
    private UUID assignedBy;

    @Column(name = "notes")
    private String notes;

    // timestamps...
}
```

---

## 7. ⚙️ Reglas de Negocio Detalladas

### 7.1 Asignación de NFC

| Regla | Descripción |
|-------|-------------|
| RN-01 | Solo un NFC activo por estudiante |
| RN-02 | UID único en todo el sistema |
| RN-03 | Al asignar nuevo, anterior se inactiva |
| RN-04 | Asignación solo por ADMIN |
| RN-05 | Se registra quién asignó (assignedBy) |
| RN-06 | Notas opcionales |

### 7.2 Escaneo de NFC

| Regla | Descripción |
|-------|-------------|
| RN-10 | Solo NFCs activos son válidos |
| RN-11 | Debe existir ruta activa del conductor |
| RN-12 | Retorna error si NFC inactivo/no existe |
| RN-13 | Logging de cada escaneo |

### 7.3 Eventos desde NFC

| Regla | Descripción |
|-------|-------------|
| RN-20 | BOARD si es el primer evento del estudiante en la ruta |
| RN-21 | DROP si el último evento es BOARD |
| RN-22 | BOARD si el último evento es DROP o ARRIVAL |
| RN-23 | Cada evento genera notificación |
| RN-24 | Evento incluye ubicación del escaneo |

---

## 8. 🔔 Notificaciones

### 8.1 Tipos de Notificación por Evento NFC

| Evento | Título | Mensaje |
|--------|--------|----------|
| BOARD | "Estudiante abordó" | "Tu hijo/hija {nombre} ha abordado la ruta del colegio." |
| DROP | "Estudiante bajó" | "Tu hijo/hija {nombre} ha descendido del vehículo." |

### 8.2 Flujo de Notificación

```
Escaneo NFC
     │
     ▼
Determinar BOARD/DROP
     │
     ▼
Crear StudentEvent
     │
     ▼
Consultar estudiantes → guardianes
     │
     ▼
Para cada guardian con notify_events=true:
     │
     ▼
Enviar notificación FCM
     │
     ▼
Registrar en tabla notifications
```

---

## 9. 🧪 Escenarios de Prueba

### 9.1happy Path - Estudiante sube por primera vez

```
1. DRIVER inicia ruta (status = IN_PROGRESS)
2. DRIVER escanea NFC "ABC123"
3. Sistema no encuentra eventos previos
4. Sistema crea evento BOARD
5. Notificación enviada a guardian
```

### 9.2 Estudiante baja (BOARD → DROP)

```
1. Ruta activa con evento BOARD del estudiante
2. DRIVER escanea NFC "ABC123"
3. Sistema detecta último evento = BOARD
4. Sistema crea evento DROP
5. Notificación enviada a guardian
```

### 9.3 Estudiante vuelve a subir (DROP → BOARD)

```
1. Ruta activa con evento DROP del estudiante
2. DRIVER escanea NFC "ABC123"
3. Sistema detecta último evento = DROP
4. Sistema crea evento BOARD
5. Notificación enviada a guardian
```

### 9.4 Error - NFC no encontrado

```
1. DRIVER escanea NFC "XYZ999"
2. Sistema no encuentra NFC con ese UID
3. Retorna 404: "NFC no encontrado o inactivo"
```

### 9.5 Error - NFC inactivo

```
1. ADMIN desactivó NFC "ABC123"
2. DRIVER intenta escanear
3. Sistema encuentra NFC pero isActive = false
4. Retorna 404: "NFC no encontrado o inactivo"
```

### 9.6 Reasignación de NFC

```
1. Estudiante tiene NFC "ABC123" activo
2. ADMIN asigna nuevo NFC "XYZ789"
3. Sistema inactiva "ABC123" (deactivatedAt = now)
4. Sistema crea "XYZ789" como activo
5. Ambas tarjetas en historial
```

---

## 10. ⚠️ Consideraciones de Seguridad

### 10.1 Validaciones

- Verificar que el conductor tenga ruta activa antes de permitir escaneo
- Validar que el conductor sea el asignado a la ruta
- El token JWT debe incluir el driverId para verificar pertenencia

### 10.2 Datos Sensibles

- El UID NFC no es información sensible (como password)
- Pero se debe validar que no sea manipulado

### 10.3 Rate Limiting (Futuro)

- Limitar escaneos excesivos (ej: más de 10 por segundo)
- Prevenir ataques de fuerza bruta con UIDs

---

## 11. 🚀 Mejoras Futuras

### 11.1 Integración automática con eventos

- Modificar ScanNfcUseCase para crear evento automáticamente
- Eliminar paso manual de confirmar BOARD/DROP
- Usar lógica determinística basada en último evento

### 11.2 Detección de anomalías

- Alertar si estudiante intenta abordar dos veces consecutivas
- Alertar si hay demasiado tiempo entre BOARD y DROP

### 11.3 Offline Mode

- Cache de NFCs activos en dispositivo
- Sincronización cuando hay conectividad

### 11.4 Lectores NFC compatibles

- Android NFC API (Foreground Dispatch)
- Lectores USB externos via Bluetooth
- Compatible con tarjetas MIFARE, NTAG, etc.

---

## 12. 📋 Resumen de Componentes

### 12.1 Entities
- `StudentNfcEntity` - Tarjetas NFC de estudiantes

### 12.2 Repositories
- `StudentNfcRepository` - Queries para NFC

### 12.3 Services
- `StudentNfcService` - Lógica de asignación y consulta

### 12.4 UseCases
- `AssignNfcUseCase` - Asignar NFC
- `ScanNfcUseCase` - Escanear NFC
- `DeactivateNfcUseCase` - Desactivar NFC
- `GetNfcHistoryUseCase` - Ver historial
- `GetActiveNfcUseCase` - Obtener NFC activo
- `CreateStudentEventFromNfcUseCase` - (pendiente) Crear evento automáticamente

### 12.5 DTOs
- `AssignNfcRequest` - Request de asignación
- `NfcScanRequest` - Request de escaneo
- `StudentNfcResponse` - Response de NFC

### 12.6 Controller
- `StudentNfcController` - Endpoints REST

---

## 13. 🔗 Referencias

- [contextTasksPending](./contextTasksPending.md) - Pendientes de implementación
- [contextDatabase](./contextDatabase.md) - Esquema de base de datos
- [contextAPI](./contextAPI.md) - Documentación de APIs

---

*Documento creado: 2026-04-06*
*Última actualización: 2026-04-06*