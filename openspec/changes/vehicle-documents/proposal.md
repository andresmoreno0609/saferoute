# Proposal: Gestión de Vehículos y Documentos Obligatorios

## Intent

Centralizar la gestión de vehículos y documentos legales (SOAP, Seguro, Tecno-mecánica, Tarjeta de propiedad, Licencia de conducir) con validación de vigencia. Un conductor sin documentos vigentes NO puede trabajar ni asignarse estudiantes.

## Scope

### In Scope
- Crear VehicleEntity con datos del vehículo (placa, modelo, color, marca)
- Crear VehicleDocumentEntity para SOAP, Seguro, Tecno-mecánica, Tarjeta de propiedad
- Crear DriverLicenseEntity para Licencia de conducir
- Implementar lógica de disponibilidad basada en documentos vigentes
- Actualizar DriverService para validar disponibilidad
- Afectar asignación de estudiantes y rutas activas

### Out of Scope
- Notificaciones automáticas por vencimiento (futuro)
- Historial de documentos vencidos (futuro)
- Módulo de reportes de cumplimiento

## Approach

**Recomendado: Entidades separadas (Vehicle + Documents)**

Justificación:
- SRP: cada entidad tiene responsabilidad única
- Escalabilidad: un conductor puede cambiar de vehículo sin perder historial
- Queries: filtrado eficiente por documento vigente
- Mantenimiento: cambios en documentos no afectan DriverEntity

Alternativa descartada (extender DriverEntity): viola SRP, acopla conceptos distintos.

## Affected Areas

| Area | Impact | Description |
|------|--------|-------------|
| `common/entity/` | New | VehicleEntity, VehicleDocumentEntity, DriverLicenseEntity |
| `driver/adapter/` | Modified | DriverAdapter - mappings a nuevas entidades |
| `common/usecase/DriverService` | Modified | Lógica de validación de disponibilidad |
| `route/` | Modified | Validación al asignar conductor a ruta |

## Modelo de Datos Propuesto

```
DriverEntity (1) ---- (1) VehicleEntity
                          │
                          │ (1..4) VehicleDocumentEntity
                          │   - type: SOAP, SEGURO, TECNOMECANICA, TARJETA_PROPIEDAD
                          │   - startDate, endDate
                          │   - isActive
                          │
DriverEntity (1) ---- (1) DriverLicenseEntity
                            - licenseNumber
                            - category
                            - startDate, endDate
                            - isActive
```

## Lógica de Disponibilidad

```java
public boolean isAvailableToWork(DriverEntity driver) {
    boolean hasLicense = driver.getLicense().getEndDate().isAfter(now());
    boolean hasVehicle = driver.getVehicle() != null;
    boolean allDocsValid = driver.getVehicle().getDocuments()
        .stream()
        .allMatch(doc -> doc.getEndDate().isAfter(now()));
    
    return hasLicense && hasVehicle && allDocsValid;
}
```

## Impacto en Rutas

- **Asignación**: Si conductor pierde disponibilidad durante ruta activa, la ruta continúa pero NO puede asignarse nuevos estudiantes
- **Alerta**: Notificar al administrador cuando un conductor en servicio pierde vigencia
- **Historial**: Rutas completadas mantienen referencia al estado del conductor al momento

## Rollback Plan

1. Revertir migraciones de nuevas tablas
2. Restaurar campos planos vehiclePlate, vehicleModel, vehicleColor en DriverEntity
3. Desplegar versión anterior del servicio
4. Validar que DriverService.useOldFields() funcione con datos existentes

## Risks

| Risk | Likelihood | Mitigation |
|------|------------|------------|
| Datos existentes sin documentos | Medium | Migrar datos existentes como documentos vigentes con fecha fin grande |
| Downtime durante migración | Low | Ejecutar migración con downtime programado, rollback inmediato |
| Queries lentas por joins | Medium | Agregar índices en `driver_id`, `end_date`, `type` |

## Success Criteria

- [ ] Driver con documentos vigentes puede ser asignado a estudiantes
- [ ] Driver sin documentos vigentes recibe error al intentar asignación
- [ ] API retorna `available: true/false` en GET /drivers/{id}
- [ ] Unit tests cubren casos: todos vigentes, uno vencido, sin vehículo