---

description: "Task list template for feature implementation"
---

# Tasks: [FEATURE NAME]

**Input**: Design documents from `/specs/[###-feature-name]/`
**Prerequisites**: plan.md (required), spec.md (required for user stories), research.md, data-model.md, contracts/

**Tests**: The examples below include test tasks. Tests for authentication behavior,
Flyway migrations, frontend lint/build, and critical endpoint flows are REQUIRED by constitution.

**Organization**: Tasks are grouped by user story to enable independent implementation and testing of each story.

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Can run in parallel (different files, no dependencies)
- **[Story]**: Which user story this task belongs to (e.g., US1, US2, US3)
- Include exact file paths in descriptions

## Path Conventions

- **Spring Boot app**: `src/main/java/`, `src/main/resources/`, `src/test/java/`
- **Angular app**: `frontend/src/app/`, `frontend/src/environments/`
- **Migrations**: `src/main/resources/db/migration/`
- **Docker assets**: `docker/compose/` or repository-root `docker-compose.yml`
- Paths shown below assume a backend + Angular frontend structure - adjust if modules are used

<!-- 
  ============================================================================
  IMPORTANT: The tasks below are SAMPLE TASKS for illustration purposes only.
  
  The /speckit.tasks command MUST replace these with actual tasks based on:
  - User stories from spec.md (with their priorities P1, P2, P3...)
  - Feature requirements from plan.md
  - Entities from data-model.md
  - Endpoints from contracts/
  
  Tasks MUST be organized by user story so each story can be:
  - Implemented independently
  - Tested independently
  - Delivered as an MVP increment
  
  DO NOT keep these sample tasks in the generated tasks.md file.
  ============================================================================
-->

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Project initialization and basic structure

- [ ] T001 Create/verify Spring Boot 3 + Java 17 project structure per implementation plan
- [ ] T002 Initialize core dependencies (web, security, data-jpa, flyway, postgresql, springdoc-openapi, Angular 22)
- [ ] T003 [P] Configure build and quality tooling (backend checks, frontend lint/build)
- [ ] T004 [P] Create local container runtime files for PostgreSQL (Docker Compose)

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Core infrastructure that MUST be complete before ANY user story can be implemented

**⚠️ CRITICAL**: No user story work can begin until this phase is complete

Examples of foundational tasks (adjust based on your project):

- [ ] T005 Configure Spring profiles and environment-based datasource properties
- [ ] T006 Setup Flyway baseline and first migration files in src/main/resources/db/migration/
- [ ] T007 [P] Configure Spring Security Basic Authentication with explicit public endpoint policy
- [ ] T008 [P] Setup global error handling and validation response model
- [ ] T009 Create shared base entities/repositories used across stories
- [ ] T010 [P] Enable Swagger/OpenAPI generation and Swagger UI endpoint
- [ ] T011 [P] Setup Angular feature modular skeleton (pages, components, services, models, guards/interceptors)
- [ ] T012 [P] Setup frontend environment profiles and centralized HTTP error/loading handling
- [ ] T013 Validate Dockerized local integration (frontend + backend + PostgreSQL) and API connectivity

**Checkpoint**: Foundation ready - user story implementation can now begin in parallel

---

## Phase 3: User Story 1 - [Title] (Priority: P1) 🎯 MVP

**Goal**: [Brief description of what this story delivers]

**Independent Test**: [How to verify this story works on its own]

### Tests for User Story 1 ⚠️

> **NOTE: Write these tests FIRST, ensure they FAIL before implementation**

- [ ] T014 [P] [US1] API/security test for [endpoint] in src/test/java/.../[Name]SecurityTest.java
- [ ] T015 [P] [US1] Integration test for [user journey] in src/test/java/.../[Name]IntegrationTest.java
- [ ] T016 [P] [US1] Frontend lint/build validation for story scope in frontend/

### Implementation for User Story 1

- [ ] T017 [P] [US1] Create [Entity1] in src/main/java/.../model/[Entity1].java
- [ ] T018 [P] [US1] Create [Entity2] in src/main/java/.../model/[Entity2].java
- [ ] T019 [US1] Implement [Service] in src/main/java/.../service/[Service].java (depends on T017, T018)
- [ ] T020 [US1] Implement [endpoint/feature] in src/main/java/.../controller/[Controller].java
- [ ] T021 [US1] Add request/response validation and exception mapping
- [ ] T022 [US1] Implement typed frontend service + page/component flow in frontend/src/app/
- [ ] T023 [US1] Align frontend form validation and accessibility baseline for story flows
- [ ] T024 [US1] Update OpenAPI annotations/spec for user story 1 endpoints

**Checkpoint**: At this point, User Story 1 should be fully functional and testable independently

---

## Phase 4: User Story 2 - [Title] (Priority: P2)

**Goal**: [Brief description of what this story delivers]

**Independent Test**: [How to verify this story works on its own]

### Tests for User Story 2 ⚠️

- [ ] T025 [P] [US2] API/security test for [endpoint] in src/test/java/.../[Name]SecurityTest.java
- [ ] T026 [P] [US2] Integration test for [user journey] in src/test/java/.../[Name]IntegrationTest.java
- [ ] T027 [P] [US2] Frontend lint/build validation for story scope in frontend/

### Implementation for User Story 2

- [ ] T028 [P] [US2] Create/update entity in src/main/java/.../model/[Entity].java
- [ ] T029 [US2] Implement service logic in src/main/java/.../service/[Service].java
- [ ] T030 [US2] Implement endpoint in src/main/java/.../controller/[Controller].java
- [ ] T031 [US2] Add or update Flyway migration if schema changes
- [ ] T032 [US2] Implement typed frontend integrations and responsive UI updates
- [ ] T033 [US2] Integrate with User Story 1 components (if needed)

**Checkpoint**: At this point, User Stories 1 AND 2 should both work independently

---

## Phase 5: User Story 3 - [Title] (Priority: P3)

**Goal**: [Brief description of what this story delivers]

**Independent Test**: [How to verify this story works on its own]

### Tests for User Story 3 ⚠️

- [ ] T034 [P] [US3] API/security test for [endpoint] in src/test/java/.../[Name]SecurityTest.java
- [ ] T035 [P] [US3] Integration test for [user journey] in src/test/java/.../[Name]IntegrationTest.java
- [ ] T036 [P] [US3] Frontend lint/build validation for story scope in frontend/

### Implementation for User Story 3

- [ ] T037 [P] [US3] Create/update entity in src/main/java/.../model/[Entity].java
- [ ] T038 [US3] Implement service in src/main/java/.../service/[Service].java
- [ ] T039 [US3] Implement endpoint in src/main/java/.../controller/[Controller].java
- [ ] T040 [US3] Implement frontend models/pages/components with typed API services
- [ ] T041 [US3] Update Swagger/OpenAPI and migration artifacts as applicable

**Checkpoint**: All user stories should now be independently functional

---

[Add more user story phases as needed, following the same pattern]

---

## Phase N: Polish & Cross-Cutting Concerns

**Purpose**: Improvements that affect multiple user stories

- [ ] TXXX [P] Documentation updates in docs/
- [ ] TXXX Code cleanup and refactoring
- [ ] TXXX Performance optimization across all stories
- [ ] TXXX [P] Additional unit/integration tests in src/test/java/
- [ ] TXXX [P] Frontend accessibility and responsive validation in frontend/
- [ ] TXXX Security hardening and auth rule review
- [ ] TXXX Validate Flyway history and rollback/readiness notes
- [ ] TXXX Verify Swagger/OpenAPI publication and endpoint accuracy
- [ ] TXXX Run quickstart.md validation

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)**: No dependencies - can start immediately
- **Foundational (Phase 2)**: Depends on Setup completion - BLOCKS all user stories
- **User Stories (Phase 3+)**: All depend on Foundational phase completion
  - User stories can then proceed in parallel (if staffed)
  - Or sequentially in priority order (P1 → P2 → P3)
- **Polish (Final Phase)**: Depends on all desired user stories being complete

### User Story Dependencies

- **User Story 1 (P1)**: Can start after Foundational (Phase 2) - No dependencies on other stories
- **User Story 2 (P2)**: Can start after Foundational (Phase 2) - May integrate with US1 but should be independently testable
- **User Story 3 (P3)**: Can start after Foundational (Phase 2) - May integrate with US1/US2 but should be independently testable

### Within Each User Story

- Security and integration tests MUST be written and FAIL before implementation
- Models before services
- Services before endpoints
- Migrations before code that depends on new schema
- Core implementation before integration
- Story complete before moving to next priority

### Parallel Opportunities

- All Setup tasks marked [P] can run in parallel
- All Foundational tasks marked [P] can run in parallel (within Phase 2)
- Once Foundational phase completes, all user stories can start in parallel (if team capacity allows)
- All tests for a user story marked [P] can run in parallel
- Models within a story marked [P] can run in parallel
- Different user stories can be worked on in parallel by different team members

---

## Parallel Example: User Story 1

```bash
# Launch all tests for User Story 1 together (if tests requested):
Task: "API/security test for [endpoint] in src/test/java/.../[Name]SecurityTest.java"
Task: "Integration test for [user journey] in src/test/java/.../[Name]IntegrationTest.java"

# Launch all models for User Story 1 together:
Task: "Create [Entity1] in src/main/java/.../model/[Entity1].java"
Task: "Create [Entity2] in src/main/java/.../model/[Entity2].java"
```

---

## Implementation Strategy

### MVP First (User Story 1 Only)

1. Complete Phase 1: Setup
2. Complete Phase 2: Foundational (CRITICAL - blocks all stories)
3. Complete Phase 3: User Story 1
4. **STOP and VALIDATE**: Test User Story 1 independently
5. Deploy/demo if ready

### Incremental Delivery

1. Complete Setup + Foundational → Foundation ready
2. Add User Story 1 → Test independently → Deploy/Demo (MVP!)
3. Add User Story 2 → Test independently → Deploy/Demo
4. Add User Story 3 → Test independently → Deploy/Demo
5. Each story adds value without breaking previous stories

### Parallel Team Strategy

With multiple developers:

1. Team completes Setup + Foundational together
2. Once Foundational is done:
   - Developer A: User Story 1
   - Developer B: User Story 2
   - Developer C: User Story 3
3. Stories complete and integrate independently

---

## Notes

- [P] tasks = different files, no dependencies
- [Story] label maps task to specific user story for traceability
- Each user story should be independently completable and testable
- Verify tests fail before implementing
- Commit after each task or logical group
- Stop at any checkpoint to validate story independently
- Avoid: vague tasks, same file conflicts, cross-story dependencies that break independence
