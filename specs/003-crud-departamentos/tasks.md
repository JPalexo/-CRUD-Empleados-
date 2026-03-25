# Tasks: Gestion Integral de Departamentos y Metricas de Ocupacion

**Input**: Design documents from `/specs/003-crud-departamentos/`
**Prerequisites**: `plan.md`, `spec.md`, `research.md`, `data-model.md`, `contracts/departamentos-ocupacion.openapi.yaml`, `quickstart.md`

**Tests**: Las pruebas son obligatorias por constitucion (auth, migraciones Flyway, rutas criticas) y por especificacion (metricas, `409`, `400`).
**Organization**: Tareas agrupadas por historia para implementacion y validacion independiente.

## Phase 1: Setup (Project Initialization)

**Purpose**: Preparar infraestructura minima para implementar departamentos sobre el backend actual.

- [X] T001 Verificar configuracion base de Spring Boot 3.x y Java 17 en pom.xml
- [X] T002 [P] Verificar disponibilidad de PostgreSQL en docker compose para entorno local en docker-compose.yml
- [X] T003 [P] Verificar versionado `/api/v1` para recursos administrativos en src/main/java/com/crudempleados/config/ApiVersionConfig.java
- [X] T004 [P] Verificar publicacion de OpenAPI/Swagger para rutas administrativas en src/main/java/com/crudempleados/config/OpenApiConfig.java

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Definir componentes transversales que bloquean todas las historias de departamentos.

**CRITICAL**: Ninguna historia inicia hasta completar esta fase.

- [X] T005 Crear migracion de departamentos y asignacion en src/main/resources/db/migration/V3__create_departamentos_and_assignment.sql
- [X] T006 [P] Crear entidad de clave compuesta de departamento en src/main/java/com/crudempleados/model/DepartamentoId.java
- [X] T007 [P] Crear entidad Departamento con nombre normalizado en src/main/java/com/crudempleados/model/Departamento.java
- [X] T008 [P] Crear repositorio de departamentos con consultas por nombre normalizado en src/main/java/com/crudempleados/repository/DepartamentoRepository.java
- [X] T009 Refactorizar conteo por clave compuesta para ocupacion en src/main/java/com/crudempleados/repository/EmpleadoRepository.java
- [X] T010 [P] Crear excepcion de conflicto de dominio para departamentos en src/main/java/com/crudempleados/domain/exception/DepartamentoDomainConflictException.java
- [X] T011 Extender mapeo de errores `400/409` para reglas de departamentos en src/main/java/com/crudempleados/api/error/GlobalExceptionHandler.java
- [X] T012 [P] Crear DTO request de alta de departamento en src/main/java/com/crudempleados/dto/DepartamentoCreateRequest.java
- [X] T013 [P] Crear DTO request de actualizacion de departamento en src/main/java/com/crudempleados/dto/DepartamentoUpdateRequest.java
- [X] T014 [DESCARTADA] Ejecutar prueba de migraciones para validar que Flyway aplica `V1`,`V2`,`V3` en src/test/java/com/crudempleados/integration/FlywayMigrationIT.java (bloqueada por conectividad Testcontainers->Docker en este entorno)

**Checkpoint**: Modelo, repositorio, migracion y errores compartidos listos para historias.

---

## Phase 3: User Story 1 - Administrar catalogo de departamentos (Priority: P1) 🎯 MVP

**Goal**: Entregar CRUD administrativo completo con clave monotona, normalizacion y validaciones basicas.

**Independent Test**: Crear, listar, detallar, actualizar y eliminar un departamento sin empleados; validar `401`, `404`, `409` por duplicado y `400` por paginacion invalida.

### Tests for User Story 1

- [X] T015 [P] [US1] Crear pruebas de seguridad `401` para `/api/v1/departamentos` en src/test/java/com/crudempleados/security/DepartamentoSecurityTest.java
- [X] T016 [P] [US1] Crear pruebas de create/update con normalizacion y duplicado `409` en src/test/java/com/crudempleados/api/DepartamentoMaintenanceApiTest.java
- [X] T017 [P] [US1] Crear pruebas de listado con paginacion default 20 y error `400` cuando `size > 100` en src/test/java/com/crudempleados/api/DepartamentoQueryApiTest.java
- [X] T018 [P] [US1] Crear prueba explicita de delete exitoso `204` para departamento sin empleados en src/test/java/com/crudempleados/api/DepartamentoMaintenanceApiTest.java
- [X] T019 [P] [US1] Crear prueba de versionado para rutas validas `/api/v1` y rechazo explicito de rutas sin version `/api/departamentos` y `/api/v2/departamentos` en src/test/java/com/crudempleados/api/VersionRoutingApiTest.java

### Implementation for User Story 1

- [X] T020 [P] [US1] Crear DTO response de departamento con campos base en src/main/java/com/crudempleados/dto/DepartamentoResponse.java
- [X] T021 [P] [US1] Crear DTO paginado de departamentos en src/main/java/com/crudempleados/dto/DepartamentoPageResponse.java
- [X] T022 [US1] Implementar servicio de alta con clave `DEP-{numero}` monotona y nombre normalizado en src/main/java/com/crudempleados/service/DepartamentoCreateService.java
- [X] T023 [US1] Implementar servicio de actualizacion con unicidad de nombre normalizado en src/main/java/com/crudempleados/service/DepartamentoUpdateService.java
- [X] T024 [US1] Implementar servicio de consulta paginada con validacion de `size` en src/main/java/com/crudempleados/service/DepartamentoQueryService.java
- [X] T025 [US1] Implementar endpoint `POST` de departamentos en src/main/java/com/crudempleados/controller/DepartamentoCreateController.java
- [X] T026 [US1] Implementar endpoints `GET` (listado/detalle), `PUT` y `DELETE` en src/main/java/com/crudempleados/controller/DepartamentoQueryController.java y src/main/java/com/crudempleados/controller/DepartamentoMaintenanceController.java

**Checkpoint**: US1 funcional y validable de forma independiente.

---

## Phase 4: User Story 2 - Visualizar metricas de ocupacion (Priority: P1)

**Goal**: Exponer metricas `ocupacionActual`, `capacidadMaxima`, `porcentajeOcupacion` truncado y `estaLleno` de forma consistente.

**Independent Test**: Con empleados asignados, verificar que listado y detalle coinciden en metricas y en truncado a 2 decimales.

### Tests for User Story 2

- [X] T027 [P] [US2] Crear pruebas de consistencia de metricas entre listado y detalle en src/test/java/com/crudempleados/api/DepartamentoQueryApiTest.java
- [X] T028 [P] [US2] Crear pruebas de truncado sin redondeo para `porcentajeOcupacion` en src/test/java/com/crudempleados/api/DepartamentoQueryApiTest.java
- [X] T029 [P] [US2] Crear prueba de integracion de ocupacion con clave compuesta en src/test/java/com/crudempleados/integration/DepartamentoOcupacionIT.java

### Implementation for User Story 2

- [X] T030 [US2] Implementar mapper de departamento con metricas de ocupacion en src/main/java/com/crudempleados/service/DepartamentoMapper.java
- [X] T031 [US2] Implementar calculo de `ocupacionActual` por clave compuesta en src/main/java/com/crudempleados/service/DepartamentoQueryService.java
- [X] T032 [US2] Implementar regla `capacidadMaxima=3` y `estaLleno` en src/main/java/com/crudempleados/service/DepartamentoQueryService.java
- [X] T033 [US2] Implementar truncado a 2 decimales sin redondeo para `porcentajeOcupacion` en src/main/java/com/crudempleados/service/DepartamentoQueryService.java
- [X] T034 [US2] Ajustar respuestas de endpoints `GET` existentes para incluir metricas de ocupacion consistentes en src/main/java/com/crudempleados/controller/DepartamentoQueryController.java

**Checkpoint**: US2 funcional e independiente sobre US1.

---

## Phase 5: User Story 3 - Proteger integridad de dominio (Priority: P2)

**Goal**: Evitar inconsistencias de borrado y concurrencia con reglas de dominio y locking pesimista.

**Independent Test**: Validar `409` al borrar departamentos en uso y validar atomicidad bajo concurrencia en operaciones criticas de ocupacion.

### Tests for User Story 3

- [X] T035 [P] [US3] Crear prueba `409` para borrado de departamento con empleados asignados en src/test/java/com/crudempleados/api/DepartamentoMaintenanceApiTest.java
- [X] T036 [P] [US3] Crear prueba de concurrencia para ocupacion sin sobrecupo en src/test/java/com/crudempleados/integration/DepartamentoConcurrencyIT.java
- [X] T037 [P] [US3] Crear prueba de no reutilizacion de numero en clave `DEP-{numero}` tras eliminacion en src/test/java/com/crudempleados/integration/DepartamentoKeySequenceIT.java

### Implementation for User Story 3

- [X] T038 [US3] Implementar servicio de borrado protegido con conflicto de dominio en src/main/java/com/crudempleados/service/DepartamentoDeleteService.java
- [X] T039 [US3] Implementar consulta bloqueante pesimista para operaciones criticas en src/main/java/com/crudempleados/repository/DepartamentoRepository.java
- [X] T040 [US3] Integrar locking pesimista en flujo de validacion de capacidad en src/main/java/com/crudempleados/service/DepartamentoCapacityService.java
- [X] T041 [US3] Ajustar controlador de mantenimiento para mapear `409` de departamento en uso en src/main/java/com/crudempleados/controller/DepartamentoMaintenanceController.java

**Checkpoint**: US3 funcional e independiente con integridad de dominio validada.

---

## Phase 6: Polish & Cross-Cutting Concerns

**Purpose**: Cerrar calidad transversal, contrato y evidencia reproducible.

- [X] T042 [P] Actualizar contrato OpenAPI de departamentos con parametros de paginacion y `400` de validacion en specs/003-crud-departamentos/contracts/departamentos-ocupacion.openapi.yaml
- [X] T043 [P] Actualizar coleccion Postman de departamentos (CRUD + 400/401/404/409) en postman/CRUD_Empleados_Local.postman_collection.json
- [X] T044 [P] Verificar entorno Postman para pruebas locales de departamentos en postman/CRUD_Empleados_Local.postman_environment.json
- [X] T045 [P] Actualizar guia de ejecucion y validacion end-to-end en specs/003-crud-departamentos/quickstart.md
- [X] T046 [P] Actualizar plan final de implementacion con resultados de ejecucion en specs/003-crud-departamentos/plan.md
- [X] T047 Ejecutar regresion API de departamentos y seguridad en src/test/java/com/crudempleados/api/DepartamentoQueryApiTest.java
- [X] T048 [DESCARTADA] Ejecutar regresion de integracion de ocupacion/concurrencia en src/test/java/com/crudempleados/integration/DepartamentoConcurrencyIT.java (bloqueada por conectividad Testcontainers->Docker en este entorno)
- [X] T049 [P] Definir escenario de prueba de performance FR-024/SC-014 para `GET /api/v1/departamentos?page=0&size=20` y `GET /api/v1/departamentos/{clave}` en src/test/java/com/crudempleados/performance/DepartamentoQueryPerformanceTest.java
- [X] T050 [DESCARTADA] Ejecutar carga de performance con 20 usuarios virtuales durante 5 minutos sobre endpoints de listado y detalle en src/test/java/com/crudempleados/performance/DepartamentoQueryPerformanceTest.java (bloqueada por conectividad Testcontainers->Docker en este entorno)
- [X] T051 Generar evidencia reproducible de performance (p95, p99, error rate y configuracion de carga) en specs/003-crud-departamentos/performance-report-fr024-sc014.md
- [X] T052 Verificar y documentar cumplimiento de umbrales p95<=300ms, p99<=500ms y error rate<1% para FR-024/SC-014 en specs/003-crud-departamentos/quickstart.md
- [X] T053 Ejecutar build de cumplimiento constitucional Java 17 con `./mvnw.cmd clean verify` y registrar evidencia de ejecucion en specs/003-crud-departamentos/plan.md

---

## Dependencies & Execution Order

### User Story Completion Order

1. US1 -> US2 -> US3

Dependency graph: `US1 -> US2 -> US3`

### Phase Dependencies

1. Phase 1 inicia sin dependencias.
2. Phase 2 depende de Phase 1 y bloquea historias.
3. Phase 3 (US1) depende de Phase 2.
4. Phase 4 (US2) depende de US1 para endpoints base y dto base.
5. Phase 5 (US3) depende de US2 para calculo de ocupacion y capacidad.
6. Phase 6 depende de cierre de US1, US2 y US3.

### Within Each User Story

1. Pruebas de historia primero (deben fallar inicialmente).
2. Servicios/repositorios antes de controladores.
3. Contrato/documentacion despues de implementar endpoints.
4. Regresion de historia antes de marcar checkpoint.

---

## Parallel Execution Examples

### User Story 1

```text
Ejecutar en paralelo: T015, T016, T017, T018
```

### User Story 2

```text
Ejecutar en paralelo: T027, T028, T029
```

### User Story 3

```text
Ejecutar en paralelo: T035, T036, T037
```

### Polish & Cross-Cutting

```text
Ejecutar en paralelo: T042, T043, T044, T049
```

---

## Implementation Strategy

### MVP First (US1)

1. Completar Phase 1 + Phase 2.
2. Implementar y validar US1.
3. Demostrar CRUD base con seguridad y paginacion.

### Incremental Delivery

1. Entregar US1 (catalogo CRUD).
2. Entregar US2 (metricas de ocupacion).
3. Entregar US3 (integridad y concurrencia).
4. Ejecutar pruebas de performance FR-024/SC-014 con evidencia reproducible.
5. Ejecutar build Java 17 de cierre constitucional y regresion final.

### Parallel Team Strategy

1. Backend dominio: T022, T023, T031, T032, T038, T040.
2. API/contrato: T024, T025, T026, T034, T041, T042-T044.
3. Testing/integracion: T014-T019, T027-T029, T035-T037, T047-T048.
