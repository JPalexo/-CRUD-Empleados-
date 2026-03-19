# Tasks: CRUD de Empleados v1 (Versionado + Paginacion)

**Input**: Design documents from `/specs/001-crud-empleados/`
**Prerequisites**: `plan.md` (required), `spec.md` (required for user stories), `research.md`, `data-model.md`, `contracts/`, `quickstart.md`

**Tests**: Se incluyen pruebas porque la especificacion define criterios de aceptacion medibles para seguridad, versionado, paginacion y comportamiento de errores.

**Organization**: Tasks are grouped by user story to enable independent implementation and testing of each story.

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Can run in parallel (different files, no dependencies)
- **[Story]**: Which user story this task belongs to (`US1`, `US2`, `US3`)
- Include exact file paths in descriptions

## Phase 1: Setup (Project Initialization)

**Purpose**: Preparar estructura base para versionado v1 y respuesta paginada.

- [X] T001 Create API version constants for v1 routes in src/main/java/com/crudempleados/config/ApiVersionConfig.java
- [X] T002 [P] Create pagination response DTO skeletons in src/main/java/com/crudempleados/dto/EmpleadoPageResponse.java and src/main/java/com/crudempleados/dto/PaginationMetadata.java
- [X] T003 [P] Add base request examples for v1 routes in postman/CRUD_Empleados_Local.postman_collection.json
- [X] T004 [P] Add versioned route variables in postman/CRUD_Empleados_Local.postman_environment.json

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Cambios compartidos que bloquean todas las historias hasta completarse.

**CRITICAL**: No user story work can begin until this phase is complete.

- [X] T005 Move create controller base mapping to v1 route in src/main/java/com/crudempleados/controller/EmpleadoCreateController.java
- [X] T006 Move query controller base mapping to v1 route in src/main/java/com/crudempleados/controller/EmpleadoQueryController.java
- [X] T007 Move maintenance controller base mapping to v1 route in src/main/java/com/crudempleados/controller/EmpleadoMaintenanceController.java
- [X] T008 [P] Add page/size validation and defaults entry point in src/main/java/com/crudempleados/controller/EmpleadoQueryController.java
- [X] T009 [P] Ensure pagination validation errors are mapped consistently in src/main/java/com/crudempleados/api/error/GlobalExceptionHandler.java
- [X] T010 [P] Review security behavior for versioned CRUD and docs routes in src/main/java/com/crudempleados/config/security/SecurityConfig.java
- [X] T011 [P] Create version-routing regression test scaffold in src/test/java/com/crudempleados/api/VersionRoutingApiTest.java
- [X] T012 [P] Sync base path and pagination contract defaults in specs/001-crud-empleados/contracts/empleados-openapi.yaml
- [X] T013 [P] Sync quickstart to v1 route and pagination defaults in specs/001-crud-empleados/quickstart.md

**Checkpoint**: Foundation ready - user story implementation can now begin.

---

## Phase 3: User Story 1 - Registrar empleado (Priority: P1) MVP

**Goal**: Permitir alta versionada en `/api/v1/empleados` con clave autogenerada y validaciones de payload.

**Independent Test**: `POST /api/v1/empleados` devuelve `201` y `clave` canonica; incluir `clave` en payload devuelve `400`; longitudes > 100 devuelven `400`; sin auth devuelve `401`.

### Tests for User Story 1

- [X] T014 [P] [US1] Add create endpoint success and auth tests for v1 path in src/test/java/com/crudempleados/api/EmpleadoCreateApiTest.java
- [X] T015 [P] [US1] Add create payload validation boundary tests for field limits in src/test/java/com/crudempleados/api/EmpleadoCreateValidationTest.java
- [X] T016 [P] [US1] Add generated key sequence continuity checks in src/test/java/com/crudempleados/integration/EmpleadoCreateSequenceIT.java

### Implementation for User Story 1

- [X] T017 [US1] Implement POST mapping on `/api/v1/empleados` in src/main/java/com/crudempleados/controller/EmpleadoCreateController.java
- [X] T018 [US1] Keep create service response aligned to canonical `EMP-{numero}` output in src/main/java/com/crudempleados/service/EmpleadoCreateService.java
- [X] T019 [US1] Ensure mapper returns canonical key format for create response in src/main/java/com/crudempleados/service/EmpleadoMapper.java
- [X] T020 [US1] Update create examples for v1 endpoint in postman/CRUD_Empleados_Local.postman_collection.json
- [X] T021 [US1] Sync POST contract examples and error cases in specs/001-crud-empleados/contracts/empleados-openapi.yaml

**Checkpoint**: User Story 1 is fully functional and independently testable.

---

## Phase 4: User Story 2 - Consultar empleados (Priority: P2)

**Goal**: Exponer listado paginado `{data, pagination}` y consulta por clave en rutas versionadas.

**Independent Test**: `GET /api/v1/empleados?page=0&size=10` devuelve sobre paginado con orden por `clave_numero` ascendente; defaults se aplican si falta `page` o `size`; parametros invalidos devuelven `400`; `/api/empleados` y `/api/v2/empleados` devuelven `404`.

### Tests for User Story 2

- [X] T022 [P] [US2] Add paginated list happy-path tests for envelope and metadata in src/test/java/com/crudempleados/api/EmpleadoQueryApiTest.java
- [X] T023 [P] [US2] Add defaults tests when `page` or `size` is omitted in src/test/java/com/crudempleados/api/EmpleadoQueryApiTest.java
- [X] T024 [P] [US2] Add invalid pagination parameter tests (`page < 0`, `size <= 0`, `size > 100`) in src/test/java/com/crudempleados/integration/EmpleadoQueryIT.java
- [X] T025 [P] [US2] Add fixed-order and empty-page behavior tests in src/test/java/com/crudempleados/integration/EmpleadoQueryIT.java
- [X] T026 [P] [US2] Add routing tests for unversioned and unpublished version paths in src/test/java/com/crudempleados/api/VersionRoutingApiTest.java

### Implementation for User Story 2

- [X] T027 [P] [US2] Add repository pagination query ordered by `claveNumero` asc in src/main/java/com/crudempleados/repository/EmpleadoRepository.java
- [X] T028 [P] [US2] Implement paginated response DTOs for `{data, pagination}` in src/main/java/com/crudempleados/dto/EmpleadoPageResponse.java and src/main/java/com/crudempleados/dto/PaginationMetadata.java
- [X] T029 [US2] Implement paginated query service with defaults and limits in src/main/java/com/crudempleados/service/EmpleadoQueryService.java
- [X] T030 [US2] Implement `GET /api/v1/empleados` with query params `page` and `size` in src/main/java/com/crudempleados/controller/EmpleadoQueryController.java
- [X] T031 [US2] Keep detail lookup versioned and key format validation in src/main/java/com/crudempleados/controller/EmpleadoQueryController.java
- [X] T032 [US2] Sync GET list/detail contract responses and parameter defaults in specs/001-crud-empleados/contracts/empleados-openapi.yaml
- [X] T033 [US2] Update Postman list/detail requests to v1 and pagination parameters in postman/CRUD_Empleados_Local.postman_collection.json

**Checkpoint**: User Stories 1 and 2 are independently functional.

---

## Phase 5: User Story 3 - Actualizar y eliminar empleados (Priority: P3)

**Goal**: Mantener actualizacion y eliminacion en rutas versionadas con manejo consistente de errores.

**Independent Test**: `PUT` y `DELETE` sobre `/api/v1/empleados/{clave}` funcionan con auth, devuelven `404` para clave inexistente y mantienen validaciones de clave.

### Tests for User Story 3

- [X] T034 [P] [US3] Add update/delete success and auth tests for v1 paths in src/test/java/com/crudempleados/api/EmpleadoMaintenanceApiTest.java
- [X] T035 [P] [US3] Add not-found persistence tests for update/delete flows in src/test/java/com/crudempleados/integration/EmpleadoMaintenanceIT.java
- [X] T036 [P] [US3] Update timed CRUD workflow to use v1 routes in src/test/java/com/crudempleados/integration/EmpleadoCrudWorkflowTimedIT.java

### Implementation for User Story 3

- [X] T037 [US3] Implement PUT/DELETE mapping on `/api/v1/empleados/{clave}` in src/main/java/com/crudempleados/controller/EmpleadoMaintenanceController.java
- [X] T038 [US3] Keep update service behavior aligned to versioned route contract in src/main/java/com/crudempleados/service/EmpleadoUpdateService.java
- [X] T039 [US3] Keep delete service behavior aligned to versioned route contract in src/main/java/com/crudempleados/service/EmpleadoDeleteService.java
- [X] T040 [US3] Sync PUT/DELETE route paths and responses in specs/001-crud-empleados/contracts/empleados-openapi.yaml
- [X] T041 [US3] Update update/delete Postman requests to v1 routes in postman/CRUD_Empleados_Local.postman_collection.json

**Checkpoint**: All user stories are independently functional.

---

## Phase 6: Polish & Cross-Cutting Concerns

**Purpose**: Cierre transversal de seguridad, rendimiento, reproducibilidad y documentacion.

- [X] T042 [P] Strengthen docs and routing security assertions in src/test/java/com/crudempleados/security/SwaggerSecurityTest.java and src/test/java/com/crudempleados/api/VersionRoutingApiTest.java
- [X] T043 [P] Validate SC-002 performance target with current paginated endpoints in src/test/java/com/crudempleados/performance/CrudLatencyIT.java
- [X] T044 [P] Validate SC-004 timed workflow using versioned CRUD routes in src/test/java/com/crudempleados/integration/EmpleadoCrudWorkflowTimedIT.java
- [X] T045 [P] Update operational verification steps for versioning and pagination defaults in specs/001-crud-empleados/quickstart.md
- [X] T046 [P] Update final decision log for v1 routing and pagination behavior in specs/001-crud-empleados/research.md
- [X] T047 [P] Run Java 17 build verification evidence generation in scripts/verification/java17-build-check.ps1 and scripts/verification/logs/
- [X] T048 [P] Run two-container startup reproducibility evidence in scripts/verification/startup-repro-check.ps1
- [X] T049 [P] Validate Postman environment and collection variables for v1 execution flow in postman/CRUD_Empleados_Local.postman_environment.json and postman/CRUD_Empleados_Local.postman_collection.json

---

## Dependencies & Execution Order

### User Story Completion Order (Dependency Graph)

1. **US1 (P1)** -> MVP de alta en ruta versionada.
2. **US2 (P2)** -> listados paginados y consulta por clave con contrato completo.
3. **US3 (P3)** -> mantenimiento (update/delete) sobre rutas versionadas.

### Phase Dependencies

1. **Phase 1 (Setup)**: Starts immediately.
2. **Phase 2 (Foundational)**: Depends on Phase 1 and blocks all stories.
3. **Phase 3 (US1)**: Starts after Phase 2.
4. **Phase 4 (US2)**: Starts after Phase 2.
5. **Phase 5 (US3)**: Starts after Phase 2.
6. **Phase 6 (Polish)**: Starts after selected stories are complete.

### Within Each Story

1. Tests for the story first.
2. DTO/repository updates before service logic.
3. Service logic before controller mapping updates.
4. Contract and Postman sync after endpoint behavior is stable.

---

## Parallel Execution Examples

### User Story 1

```text
Run in parallel:
- T014 [US1] Create endpoint success/auth tests
- T015 [US1] Validation boundary tests
- T016 [US1] Sequence continuity tests
```

### User Story 2

```text
Run in parallel:
- T022 [US2] Paginated envelope tests
- T023 [US2] Defaults tests
- T024 [US2] Invalid pagination tests
- T025 [US2] Fixed-order/empty-page tests
- T026 [US2] Version-routing tests
- T027 [US2] Repository pagination query
- T028 [US2] Pagination DTOs
```

### User Story 3

```text
Run in parallel:
- T034 [US3] Maintenance API tests
- T035 [US3] Maintenance integration tests
- T036 [US3] Timed workflow regression
```

---

## Implementation Strategy

### MVP First

1. Complete Phase 1 + Phase 2.
2. Complete Phase 3 (US1) and validate independent test criteria.
3. Release MVP with create flow on `/api/v1/empleados`.

### Incremental Delivery

1. Add Phase 4 (US2) for paginated query and routing hardening.
2. Add Phase 5 (US3) for maintenance operations on versioned routes.
3. Execute Phase 6 for non-functional evidence and cross-cutting sync.

### Team Parallelization

1. One track handles routing/security foundation (T005-T011).
2. One track prepares pagination DTO/repository items (T027-T028).
3. One track advances test suites per story, then merges contract updates.

