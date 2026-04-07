# Tasks: Gestión de Vehículos y Documentos

## Phase 1: Entidades y Repositorios

- [ ] 1.1 Crear VehicleEntity (id, plate UNIQUE, model, brand, color, capacity, createdAt, updatedAt)
- [ ] 1.2 Crear VehicleDocumentEntity (id, vehicle_id FK, documentType enum, fileUrl, startDate, endDate nullable, isActive, createdAt, updatedAt)
- [ ] 1.3 Crear DriverDocumentEntity (id, driver_id FK, documentType enum, documentNumber, licenseCategory nullable, fileUrl, startDate, endDate nullable, isActive, createdAt, updatedAt)
- [ ] 1.4 Modificar DriverEntity: remover vehiclePlate, vehicleModel, vehicleColor; agregar vehicle_id FK (1:1), actualizar relaciones
- [ ] 1.5 Crear VehicleRepository con métodos findByPlate, findByDriverId
- [ ] 1.6 Crear VehicleDocumentRepository con métodos findByVehicleAndType, findActiveByType
- [ ] 1.7 Crear DriverDocumentRepository con métodos findByDriverAndType, findActiveByType

## Phase 2: DTOs

- [ ] 2.1 Crear VehicleRequest (plate, model, brand, color, capacity)
- [ ] 2.2 Crear VehicleResponse (id, plate, model, brand, color, capacity, createdAt, updatedAt)
- [ ] 2.3 Crear VehicleDocumentRequest (documentType, fileUrl, startDate, endDate)
- [ ] 2.4 Crear VehicleDocumentResponse (id, documentType, fileUrl, startDate, endDate, isActive)
- [ ] 2.5 Crear DriverDocumentRequest (documentType, documentNumber, licenseCategory, fileUrl, startDate, endDate)
- [ ] 2.6 Crear DriverDocumentResponse (id, documentType, documentNumber, licenseCategory, fileUrl, startDate, endDate, isActive)
- [ ] 2.7 Actualizar DriverRequest: remover campos vehicle (plate, model, color), agregar vehicleId
- [ ] 2.8 Actualizar DriverResponse: incluir vehicle info y documentos del conductor

## Phase 3: Use Cases y Servicios

- [ ] 3.1 Crear VehicleMapper (mapRequestToEntity, mapEntityToResponse)
- [ ] 3.2 Crear VehicleDocumentMapper
- [ ] 3.3 Crear DriverDocumentMapper
- [ ] 3.4 Crear CreateVehicleUseCase (validar plate única, crear vehículo)
- [ ] 3.5 Crear UpdateVehicleUseCase (actualizar datos vehículo)
- [ ] 3.6 Crear GetVehicleUseCase (obtener vehículo por ID)
- [ ] 3.7 Crear AddVehicleDocumentUseCase (lógica de inactivar documento anterior del mismo tipo)
- [ ] 3.8 Crear UpdateVehicleDocumentUseCase
- [ ] 3.9 Crear GetVehicleDocumentsUseCase (listar todos o solo activos)
- [ ] 3.10 Crear AddDriverDocumentUseCase (lógica de inactivar documento anterior del mismo tipo)
- [ ] 3.11 Crear UpdateDriverDocumentUseCase
- [ ] 3.12 Crear GetDriverDocumentsUseCase (listar todos o solo activos)
- [ ] 3.13 Crear DriverAvailabilityService (lógica de disponibilidad: vehículo + documentos vigentes)
- [ ] 3.14 Modificar DriverService para usar Vehicle y documentos en validación de disponibilidad
- [ ] 3.15 Crear AssignVehicleToDriverUseCase (asignar vehículo a conductor, validar 1:1)

## Phase 4: Controladores y Endpoints

- [ ] 4.1 Crear VehicleController (CRUD: POST, GET, PUT /vehicles)
- [ ] 4.2 Crear DriverDocumentController (CRUD: POST, GET, PUT, DELETE /drivers/{driverId}/documents)
- [ ] 4.3 Actualizar DriverController (actualizar DTOs y respuestas para incluir vehicle y documents)
- [ ] 4.4 Crear VehicleDocumentController (CRUD: POST, GET, PUT, DELETE /vehicles/{vehicleId}/documents)

## Phase 5: Validación en Rutas

- [ ] 5.1 Modificar CreateRouteUseCase para validar disponibilidad del conductor (vehicle + documentos vigentes)
- [ ] 5.2 Modificar UpdateRouteUseCase para validar disponibilidad al cambiar conductor en ruta activa
- [ ] 5.3 Agregar endpoint GET /drivers/{id}/availability en DriverController

## Phase 6: Documentación

- [ ] 6.1 Actualizar docs/contextDatabase.md con nuevas entidades (VehicleEntity, VehicleDocumentEntity, DriverDocumentEntity) y relaciones
- [ ] 6.2 Actualizar docs/contextFeatures.md si hay cambios en features afectadas
- [ ] 6.3 Actualizar docs/contextAPI.md con nuevos endpoints de vehículos y documentos
- [ ] 6.4 Actualizar README.md con información de la nueva funcionalidad

## Phase 7: Testing

- [ ] 7.1 Tests unitarios para DriverAvailabilityService (isAvailableToWork con vehicle + documents válidos/vencidos)
- [ ] 7.2 Tests unitarios para DriverDocumentService (inactivar documento anterior del mismo tipo)
- [ ] 7.3 Tests unitarios para VehicleDocumentService (inactivar documento anterior del mismo tipo)
- [ ] 7.4 Tests de integración para VehicleController (CRUD completo)
- [ ] 7.5 Tests de integración para DriverDocumentController (CRUD con lógica de activación)
- [ ] 7.6 Tests de integración para VehicleDocumentController
- [ ] 7.7 Test de integración: asignación de ruta a conductor sin documentos vigentes debe fallar
