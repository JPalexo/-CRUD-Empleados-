# Implementation Plan: Frontend CRUD de Empleados y Departamentos

**Branch**: `004-frontend-crud-ui` | **Date**: 2026-03-25 | **Spec**: `/specs/004-frontend-crud-ui/spec.md`
**Input**: Feature specification from `/specs/004-frontend-crud-ui/spec.md`

## Summary

Implementar frontend administrativo oficial en Angular 22 LTS para gestionar
empleados y departamentos sobre APIs versionadas `/api/v1`, con enfoque en:

- CRUD completo de empleados y departamentos desde UI;
- asignacion/reasignacion por `departamentoClave` con visibilidad de ocupacion;
- manejo centralizado de errores HTTP y estados de carga;
- control de sesion con expiracion por inactividad (30 min);
- politicas de integridad: bloqueo de borrado de departamentos con empleados
  asignados y deteccion de conflictos de concurrencia en ediciones.

## Technical Context

**Language/Version**: Java 17 (backend), TypeScript strict mode (frontend Angular 22 LTS)
**Primary Dependencies**: Spring Boot 3.x, Spring Security Basic Auth, Spring Data JPA, Flyway, springdoc-openapi, PostgreSQL driver, Angular 22, RxJS
**Storage**: PostgreSQL (sin cambios de esquema planificados para esta feature)
**Testing**: JUnit 5/MockMvc para backend existente, pruebas unitarias de Angular, verificacion de lint/build frontend e integracion funcional contra backend local
**Target Platform**: Backend JVM Linux-compatible + navegador web desktop/mobile
**Project Type**: Full-stack monorepo (backend Spring Boot + frontend Angular)
**Performance Goals**: Operaciones CRUD en UI con respuesta percibida < 2s en entorno local de validacion, feedback de error/estado consistente en 100% de pantallas de alcance
**Constraints**: Mantener Basic Auth en backend, consumir solo `/api/v1`, no introducir secretos hardcodeados, respetar constitucion v1.1.0, validar integracion local con Docker
**Scale/Scope**: 3 user stories (empleados, departamentos, asignacion), 14 FR funcionales, cobertura de conflictos de sesion/concurrencia/eliminacion protegida

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

- [x] Stack gate: proposal remains on Spring Boot 3.x + Java 17.
- [x] Frontend gate: proposal uses Angular 22 LTS with strict TypeScript.
- [x] Security gate: all non-public endpoints enforce Basic Authentication.
- [x] Data gate: no schema changes required; existing Flyway baseline preserved for PostgreSQL.
- [x] Runtime gate: local integration path includes Dockerized PostgreSQL.
- [x] Integration gate: frontend-backend verification is planned in local Docker workflow.
- [x] Contract gate: consumed API contract defined under `/specs/004-frontend-crud-ui/contracts/`.
- [x] API consumption gate: frontend service layer consumes versioned /api/v1 endpoints.
- [x] Verification gate: tests include auth/session behavior, CRUD flows, conflict handling, and frontend quality gates.

## Project Structure

### Documentation (this feature)

```text
specs/004-frontend-crud-ui/
├── plan.md
├── research.md
├── data-model.md
├── quickstart.md
├── contracts/
│   └── frontend-admin-ui.openapi.yaml
└── tasks.md
```

### Source Code (repository root)
<!--
  ACTION REQUIRED: Replace the placeholder tree below with the concrete layout
  for this feature. Delete unused options and expand the chosen structure with
  real paths (e.g., apps/admin, packages/something). The delivered plan must
  not include Option labels.
-->

```text
src/
├── main/java/com/crudempleados/
│   ├── config/
│   ├── controller/
│   ├── dto/
│   ├── model/
│   ├── repository/
│   └── service/
├── main/resources/
│   ├── application.yml
│   └── db/migration/
└── test/java/com/crudempleados/

frontend/                      # nuevo modulo UI Angular 22
├── src/app/
│   ├── pages/
│   ├── components/
│   ├── services/
│   ├── models/
│   └── core/
│       ├── guards/
│       └── interceptors/
└── src/environments/

docker-compose.yml
```

**Structure Decision**: Mantener backend existente sin cambios de esquema y crear
modulo frontend Angular desacoplado por capas funcionales. La validacion de
integracion se ejecuta contra el backend local con PostgreSQL en Docker Compose.

## Phase 0: Research Output

Archivo generado: `/specs/004-frontend-crud-ui/research.md`

Decisiones consolidadas:

- sesion persistente con timeout por inactividad (30 min);
- borrado de departamento bloqueado cuando existan empleados asignados;
- estrategia de concurrencia con deteccion de conflicto y recarga obligatoria;
- consumo estricto de endpoints `/api/v1` mediante servicios tipados;
- manejo centralizado de errores/loading y baseline de accesibilidad/responsive;
- verificacion de integracion real sobre runtime dockerizado local.

## Phase 1: Design & Contracts Output

Archivos generados:

- `/specs/004-frontend-crud-ui/data-model.md`
- `/specs/004-frontend-crud-ui/contracts/frontend-admin-ui.openapi.yaml`
- `/specs/004-frontend-crud-ui/quickstart.md`

Diseno formalizado:

- modelo de datos frontend para `Empleado`, `Departamento`, formularios,
  estados de sesion y estados de vista CRUD;
- contrato de endpoints backend consumidos por la UI (login + CRUD empleados/
  departamentos + respuestas de conflicto/validacion);
- flujo de verificacion end-to-end con gates de lint/build frontend e
  integracion funcional contra backend local.

## Post-Design Constitution Check

- [x] Stack gate validado.
- [x] Frontend gate validado con Angular 22 + TypeScript estricto.
- [x] Security gate validado: backend mantiene Basic Auth y login controlado.
- [x] Data gate validado: sin cambios de esquema fuera de Flyway.
- [x] Runtime gate validado: quickstart usa PostgreSQL dockerizado.
- [x] Integration gate validado: flujo UI <-> backend definido en quickstart.
- [x] Contract gate validado: contrato de consumo en `/contracts/frontend-admin-ui.openapi.yaml`.
- [x] API consumption gate validado: consumo restringido a `/api/v1`.
- [x] Verification gate validado: incluye session timeout, conflictos, validaciones, lint/build.

## Complexity Tracking

Sin violaciones constitucionales; no se requieren excepciones.
