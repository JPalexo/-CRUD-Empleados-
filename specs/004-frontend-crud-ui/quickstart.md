# Quickstart: Frontend CRUD Empleados/Departamentos

## Purpose

Validate end-to-end behavior of the Angular admin UI against the existing
Spring Boot backend in local Docker-enabled development.

## Prerequisites

- Node.js LTS and npm
- Java 17
- Maven Wrapper
- Docker Desktop with compose support
- Admin credentials for Basic Auth backed APIs

## 1) Start backend dependencies

```powershell
docker compose up -d postgres
```

## 2) Start backend API

```powershell
.\mvnw.cmd spring-boot:run
```

Expected:
- API available at `http://localhost:8081`
- OpenAPI available at `http://localhost:8081/v3/api-docs`

## 3) Start frontend app (Angular 22)

```powershell
cd frontend
npm install
npm run start
```

Expected:
- Frontend available at local dev URL (for example `http://localhost:4200`)
- Frontend configured to call `http://localhost:8081/api/v1`

## 4) Validate authentication/session behavior

- Login with valid admin credentials.
- Keep UI idle for 30 minutes and verify automatic session expiration.
- After expiration, verify protected operations require re-login.

## 5) Validate empleados CRUD from UI

- List page loads with paging.
- Create empleado with valid `departamentoClave`.
- Update empleado data.
- Delete empleado with explicit confirmation.
- Invalid form values show validation feedback aligned with backend responses.

## 6) Validate departamentos CRUD and occupancy

- List departamentos with occupancy fields.
- Create and update departamento.
- Attempt delete of department with assigned empleados and verify conflict message.
- Reassign empleados then retry delete and verify expected success behavior.

## 7) Validate concurrency conflict handling

- Open same empleado/departamento in two browser sessions.
- Save changes in session A.
- Attempt save in session B and verify conflict is detected and save is blocked.
- Reload data and confirm user can retry with latest state.

## 8) Quality gates

```powershell
cd frontend
npm run lint
npm run build
```

Expected:
- Lint has no blocking errors.
- Build succeeds.

## 9) Integration gate evidence

Capture and attach:
- frontend lint/build outputs
- screenshot or log of conflict handling on deletion/concurrency
- proof of versioned API calls under `/api/v1`
