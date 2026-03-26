# Tasks: Frontend CRUD de Empleados y Departamentos

**Input**: Design documents from `/specs/004-frontend-crud-ui/`
**Prerequisites**: plan.md (required), spec.md (required), research.md, data-model.md, contracts/

**Tests**: Se incluyen tareas de verificacion automatizada y manual para cumplir
gates constitucionales (build Java 17, lint/build frontend, integracion docker,
y consistencia OpenAPI/Swagger), mas evidencia medible SC-003/SC-006.

**Organization**: Tasks grouped by user story for independent delivery and validation.

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Can run in parallel (different files, no blocking dependency)
- **[Story]**: User story label (US1, US2, US3)
- Every task includes concrete file path(s)

## Phase 1: Setup (Project Initialization)

**Purpose**: Initialize frontend workspace and baseline tooling for Angular 22.

- [ ] T001 Create Angular 22 workspace scaffold in frontend/package.json
- [ ] T002 Configure TypeScript strict mode in frontend/tsconfig.json
- [ ] T003 [P] Configure Angular app bootstrap and routes entry in frontend/src/main.ts
- [ ] T004 [P] Create root app shell and router outlet in frontend/src/app/app.component.ts
- [ ] T005 [P] Create base route config in frontend/src/app/app.routes.ts
- [ ] T006 Configure environment files for dev/test/prod in frontend/src/environments/environment.ts

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Implement shared cross-cutting frontend infrastructure required by all stories.

**CRITICAL**: User story work starts only after this phase is complete.

- [ ] T007 Create typed API base client service in frontend/src/app/services/api-client.service.ts
- [ ] T008 [P] Implement auth/session state service with inactivity timeout in frontend/src/app/services/auth-session.service.ts
- [ ] T009 [P] Implement HTTP error mapping interceptor in frontend/src/app/core/interceptors/http-error.interceptor.ts
- [ ] T010 [P] Implement activity tracking interceptor for session expiration in frontend/src/app/core/interceptors/activity.interceptor.ts
- [ ] T011 [P] Implement route guard for protected admin pages in frontend/src/app/core/guards/auth.guard.ts
- [ ] T012 Create shared loading state service in frontend/src/app/services/loading-state.service.ts
- [ ] T013 [P] Define design tokens and global styles in frontend/src/styles/tokens.css
- [ ] T014 Define shared API error model in frontend/src/app/models/api-error.model.ts
- [ ] T015 Wire global providers (interceptors/guard/services) in frontend/src/app/app.config.ts

**Checkpoint**: Shared frontend foundation complete.

---

## Phase 3: User Story 1 - Gestionar Empleados (Priority: P1) 🎯 MVP

**Goal**: Enable admin login/session and full empleados CRUD from UI.

**Independent Test**: Admin can list/create/edit/delete empleados from UI using backend `/api/v1/empleados` endpoints without Postman.

- [ ] T016 [P] [US1] Create Empleado domain models in frontend/src/app/models/empleado.model.ts
- [ ] T017 [P] [US1] Implement Empleado API service in frontend/src/app/services/empleados.service.ts
- [ ] T018 [P] [US1] Create login page component for admin access in frontend/src/app/pages/login/login-page.component.ts
- [ ] T019 [US1] Implement login form and auth flow binding in frontend/src/app/pages/login/login-page.component.html
- [ ] T020 [P] [US1] Create empleados list page component in frontend/src/app/pages/empleados/empleados-list-page.component.ts
- [ ] T021 [US1] Implement empleados list template with paging and loading/error states in frontend/src/app/pages/empleados/empleados-list-page.component.html
- [ ] T022 [P] [US1] Create empleado form component for create/update in frontend/src/app/components/empleados/empleado-form.component.ts
- [ ] T023 [US1] Implement empleado form validations aligned to backend rules in frontend/src/app/components/empleados/empleado-form.component.html
- [ ] T024 [US1] Implement delete confirmation flow for empleados in frontend/src/app/components/empleados/empleado-delete-dialog.component.ts
- [ ] T025 [US1] Register login and empleados routes in frontend/src/app/app.routes.ts

**Checkpoint**: Empleados CRUD works end-to-end with authenticated session.

---

## Phase 4: User Story 2 - Gestionar Departamentos (Priority: P2)

**Goal**: Enable full departamentos CRUD with occupancy metrics and protected delete policy.

**Independent Test**: Admin can list/create/edit departamentos, view occupancy metrics, and gets conflict when deleting departamentos with assigned empleados.

- [ ] T026 [P] [US2] Create Departamento domain models in frontend/src/app/models/departamento.model.ts
- [ ] T027 [P] [US2] Implement Departamento API service in frontend/src/app/services/departamentos.service.ts
- [ ] T028 [P] [US2] Create departamentos list page component in frontend/src/app/pages/departamentos/departamentos-list-page.component.ts
- [ ] T029 [US2] Implement departamentos list template with occupancy/capacity columns in frontend/src/app/pages/departamentos/departamentos-list-page.component.html
- [ ] T030 [P] [US2] Create departamento form component for create/update in frontend/src/app/components/departamentos/departamento-form.component.ts
- [ ] T031 [US2] Implement departamento form validation and submit states in frontend/src/app/components/departamentos/departamento-form.component.html
- [ ] T032 [US2] Implement conflict UI for protected department delete in frontend/src/app/components/departamentos/departamento-delete-dialog.component.ts
- [ ] T033 [US2] Register departamentos route and navigation entry in frontend/src/app/app.routes.ts
- [ ] T051 [US2] Implement department edit conflict detection and reload-required UX for FR-014 in frontend/src/app/components/departamentos/departamento-form.component.ts
- [ ] T052 [US2] Add conflict banner rendering for departamentos edit flow in frontend/src/app/components/departamentos/departamento-form.component.html

**Checkpoint**: Departamentos CRUD + occupancy visualization + conflict policy functional.

---

## Phase 5: User Story 3 - Asignar Empleado a Departamento (Priority: P3)

**Goal**: Support assign/reassign flows with concurrency conflict handling and occupancy consistency in UI.

**Independent Test**: Admin can reassign empleado by `departamentoClave`, sees updated occupancy data, and receives actionable conflict prompt on concurrent update.

- [ ] T034 [P] [US3] Implement reassignment API methods in frontend/src/app/services/empleados.service.ts
- [ ] T035 [P] [US3] Create assignment selector component in frontend/src/app/components/empleados/departamento-selector.component.ts
- [ ] T036 [US3] Bind assignment selector into empleado edit flow in frontend/src/app/components/empleados/empleado-form.component.ts
- [ ] T037 [US3] Implement occupancy refresh orchestration after reassignment in frontend/src/app/pages/departamentos/departamentos-list-page.component.ts
- [ ] T038 [P] [US3] Implement UI handling for edit concurrency conflict (reload required) in frontend/src/app/services/conflict-resolution.service.ts
- [ ] T039 [US3] Show reload-and-retry conflict messaging in frontend/src/app/components/shared/conflict-banner.component.ts
- [ ] T040 [US3] Add keyboard focus and accessibility states for reassignment/conflict actions in frontend/src/styles/accessibility.css

**Checkpoint**: Assignment/reassignment and conflict handling fully operational.

---

## Phase 6: Polish & Cross-Cutting Concerns

**Purpose**: Final hardening, documentation, and integration validation.

- [ ] T041 [P] Update frontend environment configuration guide in specs/004-frontend-crud-ui/quickstart.md
- [ ] T042 [P] Run frontend lint and apply fixes in frontend/src/app/
- [ ] T043 [P] Run frontend production build and resolve build blockers in frontend/src/
- [ ] T044 Validate Docker local integration evidence and API version usage in specs/004-frontend-crud-ui/quickstart.md
- [ ] T045 Perform accessibility/responsive checklist pass in frontend/src/styles/tokens.css
- [ ] T046 Execute constitutional backend build gate with Java 17 using `./mvnw clean verify` and record evidence in specs/004-frontend-crud-ui/quickstart.md
- [ ] T047 Validate Swagger/OpenAPI endpoint output against consumed contract in specs/004-frontend-crud-ui/contracts/frontend-admin-ui.openapi.yaml
- [ ] T048 Execute UI-only acceptance run (no Postman) for empleados/departamentos flows and record evidence in specs/004-frontend-crud-ui/quickstart.md
- [ ] T049 Document authentication exception policy (public vs protected endpoints) in specs/004-frontend-crud-ui/quickstart.md
- [ ] T050 [P] Run frontend unit test suite and address failures in frontend/src/app/
- [ ] T053 Define clean-environment Flyway verification procedure (empty DB bootstrap + migrations apply) in specs/004-frontend-crud-ui/quickstart.md
- [ ] T054 Execute Flyway migration verification on clean PostgreSQL environment and record evidence in specs/004-frontend-crud-ui/quickstart.md
- [ ] T055 Execute positive Basic Auth verification against protected endpoints (`/api/v1/empleados/**`, `/api/v1/departamentos/**`) and record pass evidence in specs/004-frontend-crud-ui/quickstart.md
- [ ] T056 Execute negative Basic Auth verification (missing and invalid credentials) and record 401/403 evidence in specs/004-frontend-crud-ui/quickstart.md
- [ ] T057 Execute FR-014 end-to-end verification for concurrent edit conflicts in empleados and departamentos and record evidence in specs/004-frontend-crud-ui/quickstart.md
- [ ] T061 Measure SC-003 validation feedback latency (<2s) across invalid-data scenarios and record timestamped evidence in specs/004-frontend-crud-ui/performance-report-sc003.md
- [ ] T062 Measure SC-006 responsive operability for 1366x768 and 390x844 with CRUD action checklist evidence in specs/004-frontend-crud-ui/performance-report-sc006.md
- [ ] T063 Consolidate SC-003 and SC-006 measurable evidence summary and links in specs/004-frontend-crud-ui/quickstart.md

---

## Dependencies & Execution Order

### Phase Dependencies

- Setup (Phase 1): starts immediately.
- Foundational (Phase 2): depends on Phase 1 and blocks all user stories.
- User Story phases (3-5): depend on Phase 2 completion.
- Polish (Phase 6): depends on completion of selected user stories.

### User Story Dependencies

- US1 (P1): starts after foundational phase and defines MVP.
- US2 (P2): starts after foundational phase; independent from US1 except shared routing shell.
- US3 (P3): starts after foundational phase and depends on US1/US2 services and pages being present.

### Story Completion Order

1. US1 (MVP)
2. US2
3. US3

---

## Parallel Execution Examples

### User Story 1

- T016 + T017 + T018 can run in parallel (models, service, login page skeleton).
- T020 + T022 can run in parallel (list page and form component).

### User Story 2

- T026 + T027 + T028 can run in parallel (models, service, list component).
- T030 + T032 can run in parallel (form and delete-conflict dialog).

### User Story 3

- T034 + T035 + T038 can run in parallel (API reassignment, selector, conflict service).
- T039 + T040 can run in parallel (conflict UI + accessibility styling).

---

## Implementation Strategy

### MVP First (US1)

1. Complete Phases 1 and 2.
2. Implement US1 (Phase 3) and validate complete empleados workflow.
3. Demo MVP before expanding scope.

### Incremental Delivery

1. Deliver US1 for core operational value.
2. Deliver US2 for department administration and occupancy visibility.
3. Deliver US3 for assignment and concurrency reliability.
4. Run polish gates (lint/build/integration evidence + Java/OpenAPI/UI-only evidence).

### Parallel Team Strategy

1. Team aligns on Phases 1-2 together.
2. Then split by stories:
- Dev A: US1 (empleados).
- Dev B: US2 (departamentos).
- Dev C: US3 (asignacion y conflictos) after shared APIs are available.

---

## Notes

- All tasks follow strict checklist format: `- [ ] T### [P?] [US?] Description with file path`.
- [P] tasks target distinct files to reduce merge contention.
- Story checkpoints define independent validation before next increment.
