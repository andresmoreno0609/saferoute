# Tasks Pending

<!-- This file tracks pending development tasks. When a task is completed, remove it from this file. -->

---

## Pendientes por Implementar

### NFC - Sistema de Identificación de Estudiantes

**Descripción:** Sistema de tarjetas NFC para identificar estudiantes al abordar/bajar del transporte escolar.

**Relevancia:** Alta - Funcionalidad crítica para el sistema de transporte

#### Endpoints ✅ COMPLETADOS

| Endpoint | Método | Descripción | Estado |
|----------|--------|-------------|--------|
| `/api/v1/students/{id}/nfc` | POST | Asignar NFC a estudiante | ✅ |
| `/api/v1/students/{id}/nfc` | GET | Obtener NFC activo del estudiante | ✅ |
| `/api/v1/students/{id}/nfc` | DELETE | Desactivar NFC del estudiante | ✅ |
| `/api/v1/students/{id}/nfc/history` | GET | Ver historial de NFCs del estudiante | ✅ |
| `/api/v1/nfc/scan` | POST | Escanear NFC (detectar estudiante) | ✅ |

#### Componentes Creados

- [x] **DTOs:** `AssignNfcRequest`, `NfcScanRequest`, `StudentNfcResponse`
- [x] **UseCases:** `AssignNfcUseCase`, `DeactivateNfcUseCase`, `ScanNfcUseCase`, `GetNfcHistoryUseCase`, `GetActiveNfcUseCase`
- [x] **Adapter:** `StudentNfcAdapter`
- [x] **Controller:** `StudentNfcController`
- [ ] **Integración con Route:** Al escanear NFC → registrar evento BOARD/DROP

#### Reglas de Negocio (Implementadas)

1. ✅ Solo UN NFC activo por estudiante a la vez
2. ✅ Al asignar nuevo NFC, el anterior se inactiva automáticamente
3. ✅ Mantener histórico de todos los NFCs (inactivos)
4. ✅ NFC UID debe ser único en todo el sistema
5. ⏳ Al escanear NFC: determinar estudiante → crear evento en ruta activa

---

### Pendiente: Integración NFC con Rutas

**Descripción:** Cuando se escanea un NFC, automáticamente registrar el evento BOARD o DROP en la ruta activa del conductor.

**Pendiente:**
- [ ] Modificar `ScanNfcUseCase` para detectar si hay una ruta activa
- [ ] Crear evento automáticamente: BOARD (estudiante sube) o DROP (estudiante baja)
- [ ] Necesita lógica para determinar si el estudiante está subiendo o bajando (consultar último evento)

---

*Última actualización: 2026-04-06*
*NFC completado: 2026-04-06*