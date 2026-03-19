# Tasks: Login Simbolico de Empleado

**Input**: Design documents from `/specs/002-employee-user-login/`  
**Prerequisites**: `plan.md` (required), `spec.md` (required), `research.md`, `data-model.md`, `contracts/`, `quickstart.md`

**Tests**: Se incluyen tareas de prueba porque la especificacion y la constitucion exigen cobertura de autenticacion, no regresion, migraciones y flujos criticos.  
**Organization**: Tareas agrupadas por historia de usuario para habilitar implementacion y validacion incremental.

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Tarea paralelizable (archivos distintos, sin dependencia de tareas incompletas)
- **[Story]**: Etiqueta de historia (`[US1]`, `[US2]`, `[US3]`) solo en fases de historias
- Todas las tareas incluyen ruta de archivo explicita

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Preparar base de trabajo para login simbolico sin romper CRUD existente.

- [X] T001 Add login route constant for employee authentication in src/main/java/com/crudempleados/config/ApiVersionConfig.java
- [X] T002 [P] Create DTO scaffolds for employee login request/response in src/main/java/com/crudempleados/dto/EmpleadoLoginRequest.java and src/main/java/com/crudempleados/dto/EmpleadoLoginResponse.java
- [X] T003 [P] Create test data helpers for email/password payloads in src/test/java/com/crudempleados/support/EmpleadoTestDataFactory.java
- [X] T004 [P] Prepare login request placeholders in postman/CRUD_Empleados_Local.postman_collection.json and postman/CRUD_Empleados_Local.postman_environment.json

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Infraestructura bloqueante de seguridad, datos y manejo de errores.

**CRITICAL**: Ninguna historia debe comenzar antes de completar esta fase.

- [X] T005 Create Flyway migration for credential and login event tables in src/main/resources/db/migration/V2__add_employee_login_tables.sql
- [X] T006 Create shared JPA models for credentials and login events in src/main/java/com/crudempleados/model/EmpleadoCredencial.java and src/main/java/com/crudempleados/model/EmpleadoLoginEvento.java
- [X] T007 [P] Create repositories for credentials and login events in src/main/java/com/crudempleados/repository/EmpleadoCredencialRepository.java and src/main/java/com/crudempleados/repository/EmpleadoLoginEventoRepository.java
- [X] T008 [P] Implement shared email normalization and password policy utilities in src/main/java/com/crudempleados/domain/EmailNormalizer.java and src/main/java/com/crudempleados/domain/PasswordPolicy.java
- [X] T009 Configure security rules to allow public login endpoint and protect admin CRUD in src/main/java/com/crudempleados/config/security/SecurityConfig.java
- [X] T010 [P] Add auth-domain exception types for generic login failure handling in src/main/java/com/crudempleados/domain/exception/InvalidEmployeeCredentialsException.java and src/main/java/com/crudempleados/domain/exception/DuplicateEmployeeEmailException.java
- [X] T011 [P] Extend global exception mapping for duplicate email and generic login failures in src/main/java/com/crudempleados/api/error/GlobalExceptionHandler.java
- [X] T012 [P] Update OpenAPI runtime metadata baseline for login feature release in src/main/java/com/crudempleados/config/OpenApiConfig.java

**Checkpoint**: Foundation complete; historias pueden implementarse con bajo riesgo de retrabajo.

---

## Phase 3: User Story 1 - Registrar empleado con credenciales (Priority: P1) 🎯 MVP

**Goal**: Crear empleados con `email` y `password` en una sola operacion transaccional.

**Independent Test**: `POST /api/v1/empleados` con Basic Auth admin crea empleado + credencial; valida formato de email/password, normaliza email, rechaza duplicados sin crear datos parciales y verifica que la password solo se persiste como hash+salt (sin texto plano).

### Tests for User Story 1

- [X] T013 [P] [US1] Add API tests for create-with-credentials success and duplicate-email conflict in src/test/java/com/crudempleados/api/EmpleadoCreateApiTest.java
- [X] T014 [P] [US1] Add API validation tests for email format and password policy rules in src/test/java/com/crudempleados/api/EmpleadoCreateValidationTest.java
- [X] T015 [P] [US1] Add integration test for atomic employee+credential persistence in src/test/java/com/crudempleados/integration/EmpleadoCreateCredentialsIT.java

### Implementation for User Story 1

- [X] T016 [US1] Extend create request contract with email and password fields in src/main/java/com/crudempleados/dto/EmpleadoCreateRequest.java
- [X] T017 [US1] Implement transactional creation of employee credentials in src/main/java/com/crudempleados/service/EmpleadoCreateService.java
- [X] T018 [US1] Implement credential mapping defaults and normalization flow in src/main/java/com/crudempleados/service/EmpleadoCredentialService.java
- [X] T019 [US1] Keep create endpoint behavior aligned with new credential requirements in src/main/java/com/crudempleados/controller/EmpleadoCreateController.java
- [X] T020 [US1] Sync create endpoint contract for required credentials and conflict response in specs/002-employee-user-login/contracts/employee-login.openapi.yaml
- [X] T021 [US1] Update create flow examples with credentials in specs/002-employee-user-login/quickstart.md and postman/CRUD_Empleados_Local.postman_collection.json

**Checkpoint**: US1 queda funcional y verificable como MVP.

---

## Phase 4: User Story 2 - Iniciar sesion simbolico como empleado (Priority: P2)

**Goal**: Exponer login simbolico publico (`/api/v1/empleados/login`) sin token y con respuesta consistente.

**Independent Test**: Login exitoso devuelve identidad basica sin token; fallos de credenciales devuelven respuesta generica; variantes de email por formato autentican sobre valor normalizado; secuencias de 5 fallos consecutivos mantienen politica sin lockout y con trazabilidad por intento.

### Tests for User Story 2

- [X] T022 [P] [US2] Add API tests for public login success response without token in src/test/java/com/crudempleados/api/EmpleadoLoginApiTest.java
- [X] T023 [P] [US2] Add API tests for generic invalid-credentials behavior in src/test/java/com/crudempleados/api/EmpleadoLoginApiTest.java
- [X] T024 [P] [US2] Add integration tests for trim+lowercase email login normalization in src/test/java/com/crudempleados/integration/EmpleadoLoginIT.java

### Implementation for User Story 2

- [X] T025 [P] [US2] Implement login request/response DTOs with identity payload in src/main/java/com/crudempleados/dto/EmpleadoLoginRequest.java and src/main/java/com/crudempleados/dto/EmpleadoLoginResponse.java
- [X] T026 [US2] Implement login service for credential lookup, hash validation, and generic failures in src/main/java/com/crudempleados/service/EmpleadoLoginService.java
- [X] T027 [US2] Add public login controller endpoint in src/main/java/com/crudempleados/controller/EmpleadoLoginController.java
- [X] T028 [US2] Persist login attempt events from success/failure paths in src/main/java/com/crudempleados/service/EmpleadoLoginEventService.java
- [X] T029 [US2] Sync login endpoint contract and schemas in specs/002-employee-user-login/contracts/employee-login.openapi.yaml
- [X] T030 [US2] Add login requests and assertions for success/failure in postman/CRUD_Empleados_Local.postman_collection.json

**Checkpoint**: US1 + US2 operan de forma consistente para alta y autenticacion de empleado.

---

## Phase 5: User Story 3 - Mantener independencia del acceso admin (Priority: P3)

**Goal**: Preservar canal admin Basic Auth y asegurar que credenciales de empleado no otorguen acceso administrativo.

**Independent Test**: Endpoints administrativos mantienen `401` sin Basic Auth admin; credenciales de empleado no acceden al CRUD; empleados eliminados o con credencial no habilitada no autentican en login simbolico.

### Tests for User Story 3

- [X] T031 [P] [US3] Add security regression tests for protected admin CRUD endpoints in src/test/java/com/crudempleados/security/SwaggerSecurityTest.java and src/test/java/com/crudempleados/api/VersionRoutingApiTest.java
- [X] T032 [P] [US3] Add isolation test to ensure employee credentials cannot authenticate admin CRUD in src/test/java/com/crudempleados/security/EmployeeCredentialIsolationIT.java
- [X] T033 [P] [US3] Add integration test for login rejection after employee deletion or non-enabled credential state in src/test/java/com/crudempleados/integration/EmpleadoLoginIT.java

### Implementation for User Story 3

- [X] T034 [US3] Enforce auth-channel separation rules in src/main/java/com/crudempleados/config/security/SecurityConfig.java
- [X] T035 [US3] Ensure delete flow invalidates employee login capability via data lifecycle rules in src/main/java/com/crudempleados/service/EmpleadoDeleteService.java and src/main/java/com/crudempleados/service/EmpleadoLoginService.java
- [X] T036 [US3] Keep generic failure semantics for non-enabled/missing credential records in src/main/java/com/crudempleados/service/EmpleadoLoginService.java
- [X] T037 [US3] Update no-regression verification steps for security channel separation in specs/002-employee-user-login/quickstart.md

**Checkpoint**: Las tres historias quedan completas con separacion de canales validada.

---

## Phase 6: Polish & Cross-Cutting Concerns

**Purpose**: Cierre transversal de calidad, rendimiento, contrato y evidencias operativas.

- [X] T038 [P] Add login latency integration/performance test aligned with p95 target in src/test/java/com/crudempleados/performance/LoginLatencyIT.java
- [X] T039 [P] Run and update full feature regression suite references in src/test/java/com/crudempleados/api/EmpleadoCreateApiTest.java, src/test/java/com/crudempleados/api/EmpleadoLoginApiTest.java, and src/test/java/com/crudempleados/integration/EmpleadoLoginIT.java
- [X] T040 [P] Execute Java 17 build verification and archive evidence in scripts/verification/java17-build-check.ps1 and scripts/verification/logs/
- [X] T041 [P] Execute startup reproducibility verification including login smoke checks in scripts/verification/startup-repro-check.ps1
- [X] T042 [P] Final OpenAPI/Swagger synchronization review for create+login behavior in src/main/java/com/crudempleados/config/OpenApiConfig.java and specs/002-employee-user-login/contracts/employee-login.openapi.yaml
- [X] T043 [P] Final Postman collection/environment synchronization for employee login workflows in postman/CRUD_Empleados_Local.postman_collection.json and postman/CRUD_Empleados_Local.postman_environment.json
- [X] T044 Consolidate SC-012 documentation evidence for channel separation/security rules and late-migration policy (new V3 instead of mutating V2) in specs/002-employee-user-login/quickstart.md and specs/002-employee-user-login/research.md
- [X] T045 [P] Add integration test to validate hash-at-rest storage for employee credentials (SC-007) in src/test/java/com/crudempleados/integration/EmpleadoCredentialSecurityIT.java
- [X] T046 [P] Add integration test for 5 consecutive failed logins without lockout and with per-attempt event traceability (SC-009) in src/test/java/com/crudempleados/integration/EmpleadoLoginAttemptPolicyIT.java
- [X] T047 [P] Add integration test for create-to-first-login success within 60 seconds (SC-002) in src/test/java/com/crudempleados/integration/EmpleadoCreateLoginSlaIT.java

---

## Dependencies & Execution Order

### User Story Completion Order (Dependency Graph)

1. **US1 (P1)**: habilita alta con credenciales y base de datos de autenticacion.
2. **US2 (P2)**: consume credenciales para login simbolico publico.
3. **US3 (P3)**: valida aislamiento definitivo entre canal empleado y canal admin.

Graph:

`US1 -> US2 -> US3`

### Phase Dependencies

1. **Phase 1 (Setup)**: inicia de inmediato.
2. **Phase 2 (Foundational)**: depende de Phase 1 y bloquea historias.
3. **Phase 3 (US1)**: depende de Phase 2.
4. **Phase 4 (US2)**: depende de Phase 2 y de artefactos funcionales de US1.
5. **Phase 5 (US3)**: depende de Phase 2 y de rutas completas de US1/US2.
6. **Phase 6 (Polish)**: depende de cierre funcional de todas las historias.

### Within Each User Story

1. Pruebas de la historia primero (deben fallar inicialmente).
2. DTO/modelos antes de servicios.
3. Servicios antes de controladores/endpoints.
4. Sincronizacion de contrato y Postman despues de estabilizar comportamiento.

---

## Parallel Execution Examples

### User Story 1

```text
Run in parallel:
- T013 [US1] API success/conflict tests
- T014 [US1] Validation tests
- T015 [US1] Atomic integration test
```

### User Story 2

```text
Run in parallel:
- T022 [US2] Login success tests
- T023 [US2] Generic failure tests
- T024 [US2] Normalization integration tests
- T025 [US2] Login DTO implementation
```

### User Story 3

```text
Run in parallel:
- T031 [US3] Security regression tests
- T032 [US3] Employee/admin isolation test
- T033 [US3] Deleted/non-enabled employee login rejection test
```

---

## Implementation Strategy

### MVP First (US1)

1. Completar Phase 1 + Phase 2.
2. Completar US1 (Phase 3).
3. Validar alta con credenciales y consistencia transaccional.

### Incremental Delivery

1. Entregar US1 (alta con credenciales).
2. Entregar US2 (login simbolico publico sin token).
3. Entregar US3 (aislamiento de canales y no regresion admin).
4. Ejecutar cierre transversal en Phase 6.

### Suggested Team Parallelization

1. Track A: migraciones/modelos/repositorios (T005-T007, T016-T018, T025).
2. Track B: seguridad/controladores/servicios de login (T009, T026-T028, T034-T036).
3. Track C: suite de pruebas + contrato + Postman (T013-T015, T022-T024, T031-T033, T042-T043, T045-T047).
