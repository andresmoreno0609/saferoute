# Tasks Pending

<!-- This file tracks pending development tasks. When a task is completed, remove it from this file. -->

---

## Pendientes por Implementar

### NFC - Sistema de Identificación de Estudiantes

**Descripción:** Sistema de tarjetas NFC para identificar estudiantes al abordar/bajar del transporte escolar.

**Relevancia:** Alta - Funcionalidad crítica para el sistema de transporte

#### Endpoints Pendientes

| Endpoint | Método | Descripción |
|----------|--------|-------------|
| `/api/v1/students/{id}/nfc` | POST | Asignar NFC a estudiante |
| `/api/v1/students/{id}/nfc` | GET | Obtener NFC activo del estudiante |
| `/api/v1/students/{id}/nfc` | DELETE | Desactivar NFC del estudiante |
| `/api/v1/students/{id}/nfc/history` | GET | Ver historial de NFCs del estudiante |
| `/api/v1/nfc/scan` | POST | Escanear NFC (detectar estudiante) |

#### Componentes a Crear

- [ ] **DTOs:** `AssignNfcRequest`, `NfcScanRequest`, `StudentNfcResponse`
- [ ] **UseCases:** `AssignNfcUseCase`, `DeactivateNfcUseCase`, `ScanNfcUseCase`, `GetNfcHistoryUseCase`
- [ ] **Adapter:** `StudentNfcAdapter`
- [ ] **Controller:** `StudentNfcController`
- [ ] **Integración con Route:** Al escanear NFC → registrar evento BOARD/DROP

#### Reglas de Negocio

1. Solo UN NFC activo por estudiante a la vez
2. Al asignar nuevo NFC, el anterior se inactiva automáticamente
3. Mantener histórico de todos los NFCs (inactivos)
4. NFC UID debe ser único en todo el sistema
5. Al escanear NFC: determinar estudiante → crear evento en ruta activa

---

*Última actualización: 2026-04-06*