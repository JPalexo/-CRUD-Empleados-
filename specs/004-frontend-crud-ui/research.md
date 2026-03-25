# Research: Frontend CRUD de Empleados y Departamentos

**Date**: 2026-03-25
**Feature**: 004-frontend-crud-ui

## Decision 1: Session persistence policy in frontend

- Decision: Persist admin session with inactivity timeout of 30 minutes and force re-login after expiration.
- Rationale: Balances usability and security for protected admin operations.
- Alternatives considered:
  - No persistence: rejected due to excessive reauthentication friction.
  - Persist until manual logout: rejected due to longer unattended session risk.

## Decision 2: Department deletion behavior with assigned employees

- Decision: Block department deletion when employees remain assigned and return a conflict message.
- Rationale: Preserves business integrity and avoids silent reassignment side effects.
- Alternatives considered:
  - Auto-move to unassigned department: rejected due to implicit business mutation.
  - Cascade delete employees: rejected due to destructive data loss risk.

## Decision 3: Concurrency handling on edits

- Decision: Detect concurrent update conflicts and block save until user reloads latest data.
- Rationale: Prevents last-write-wins data corruption in administrative edits.
- Alternatives considered:
  - Last write wins: rejected due to hidden overwrites.
  - Pessimistic UI locking: rejected due to higher UX and implementation complexity.

## Decision 4: Frontend architecture baseline

- Decision: Implement Angular 22 with strict TypeScript and feature-modular structure: pages, components, services, models, guards/interceptors.
- Rationale: Required by constitution and improves long-term maintainability.
- Alternatives considered:
  - Flat component-only structure: rejected due to poor scalability.
  - Different framework: rejected by constitutional constraint.

## Decision 5: API integration strategy

- Decision: Consume only versioned backend endpoints under /api/v1 through typed Angular services.
- Rationale: Enforces stable contracts and keeps integration aligned with backend versioning policy.
- Alternatives considered:
  - Direct HttpClient calls from components: rejected to avoid duplication and weak typing.

## Decision 6: Error/loading handling strategy

- Decision: Centralize HTTP error mapping and loading state management via interceptor + shared UI state helpers.
- Rationale: Delivers consistent behavior across all CRUD screens.
- Alternatives considered:
  - Per-component ad hoc handling: rejected for inconsistency and repetition.

## Decision 7: Validation alignment

- Decision: Mirror backend validation constraints in frontend forms and preserve server-side errors as source of truth.
- Rationale: Improves user feedback while preventing drift from backend rules.
- Alternatives considered:
  - Frontend-only validation definitions: rejected due to mismatch risk.

## Decision 8: Responsive and accessibility baseline

- Decision: Support desktop and mobile layouts with keyboard navigability, explicit labels, visible focus states, and contrast-safe tokens.
- Rationale: Required by constitution and critical for operational usability.
- Alternatives considered:
  - Desktop-only optimization: rejected due to mandatory responsive requirement.

## Decision 9: Local integration verification path

- Decision: Validate frontend-backend integration against local Dockerized backend stack and versioned APIs.
- Rationale: Matches constitutional runtime gate and avoids environment parity issues.
- Alternatives considered:
  - Mock-only frontend verification: rejected because it does not prove real integration.
