# Data Model: Frontend CRUD de Empleados y Departamentos

**Feature**: 004-frontend-crud-ui
**Date**: 2026-03-25

## 1. Domain entities consumed by frontend

### Empleado

- `clave`: string (immutable identifier in UI)
- `nombre`: string
- `apellido`: string
- `email`: string
- `departamentoClave`: string

Validation rules:
- `email` must match backend accepted format.
- required fields must block submit when invalid.
- `departamentoClave` must reference an existing department.

### Departamento

- `clave`: string (`DEP-{numero}`)
- `nombre`: string
- `ocupacionActual`: number
- `capacidadMaxima`: number (expected 3)
- `porcentajeOcupacion`: number
- `estaLleno`: boolean

Validation rules:
- `nombre` required.
- duplicate normalized names rejected by backend and surfaced in UI.

## 2. Frontend request models

### EmpleadoCreateForm

- `nombre`: string
- `apellido`: string
- `email`: string
- `password`: string
- `departamentoClave`: string

### EmpleadoUpdateForm

- `nombre`: string
- `apellido`: string
- `email`: string
- `departamentoClave`: string

### DepartamentoCreateForm

- `nombre`: string

### DepartamentoUpdateForm

- `nombre`: string

## 3. Frontend response models

### PageResponse<T>

- `data`: T[]
- `pagination.page`: number
- `pagination.size`: number
- `pagination.totalElements`: number
- `pagination.totalPages`: number

### ApiError

- `status`: number
- `error`: string
- `message`: string
- `path`: string
- `timestamp`: string

## 4. UI state models

### AuthSessionState

- `isAuthenticated`: boolean
- `lastActivityAt`: datetime
- `expiresAt`: datetime
- `principal`: string

State transitions:
- `Unauthenticated -> Authenticated`: login success.
- `Authenticated -> Unauthenticated`: manual logout.
- `Authenticated -> Expired`: 30 min inactivity timeout.
- `Expired -> Authenticated`: successful re-login.

### CrudViewState<T>

- `items`: T[]
- `isLoading`: boolean
- `error`: ApiError | null
- `isSubmitting`: boolean
- `conflictDetected`: boolean

State transitions:
- `Idle -> Loading -> Loaded`
- `Loaded -> Submitting -> Loaded`
- `Submitting -> Conflict` when backend reports edit concurrency conflict.
- `Any -> Error` on non-recoverable HTTP failure.

## 5. Relationship model

- One `Departamento` to many `Empleado`.
- Reassignment updates `Empleado.departamentoClave` and changes department occupancy.
- Department deletion is invalid while linked employees exist.

## 6. Behavioral invariants

- Frontend must never call unversioned endpoints.
- Save operations must be blocked on invalid form state.
- Concurrency conflict must not silently overwrite existing backend state.
- Session expiration must block protected actions until reauthentication.
