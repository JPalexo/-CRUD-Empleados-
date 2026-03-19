# Implementation Plan: CRUD de Empleados v1 (Paginacion + Versionado URL)

**Branch**: `001-crud-empleados` | **Date**: 2026-03-18 | **Spec**: `/specs/001-crud-empleados/spec.md`
**Input**: Feature specification from `/specs/001-crud-empleados/spec.md`

## Summary

Se planifica evolucionar el CRUD actual para publicar la API bajo rutas versionadas `/api/v1/empleados`,
incorporar listado paginado con sobre `{data, pagination}`, defaults (`page=0`, `size=10`), validaciones
de rango (`page >= 0`, `size > 0`, `size <= 100`) y orden estable por `clave_numero` ascendente.
La solucion mantiene Basic Auth en todos los endpoints (incluyendo Swagger y `/v3/api-docs`), preserva
PK compuesta (`clave_prefijo`, `clave_numero`) con secuencia de PostgreSQL y sincroniza contrato OpenAPI.

## Technical Context

**Language/Version**: Java 17 (mandatory)
**Primary Dependencies**: Spring Boot 3.3.5, Spring Web, Spring Validation, Spring Data JPA, Spring Security, Flyway, springdoc-openapi, PostgreSQL driver
**Storage**: PostgreSQL 16 + Flyway SQL migrations
**Testing**: JUnit 5, Spring Boot Test, MockMvc, Spring Security Test, Testcontainers, integration tests existentes (`api`, `integration`, `performance`)
**Target Platform**: Linux container runtime (Docker) + local Docker Compose
**Project Type**: Backend REST API (monolito Spring Boot)
**Performance Goals**:
- P95 < 2s para operaciones CRUD validas bajo 50 usuarios concurrentes (SC-002)
- Flujo alta-consulta-actualizacion-eliminacion < 3 minutos para usuario autorizado (SC-004)
**Constraints**:
- Rutas de negocio solo versionadas (`/api/v1/empleados`)
- Rutas sin version y versiones no publicadas deben devolver 404
- Respuesta paginada obligatoria en formato `{data, pagination}`
- Orden estable por `clave_numero` ascendente
- Defaults: `page=0`, `size=10`; limites: `page >= 0`, `0 < size <= 100`
- Basic Auth obligatorio para API y documentacion
- Sin credenciales hardcodeadas; configuracion por perfiles + variables de entorno
**Scale/Scope**:
- 5 endpoints CRUD versionados (`GET list`, `GET by clave`, `POST`, `PUT`, `DELETE`)
- Ajustes en controladores/servicios/DTO para paginacion y versionado
- Actualizacion de contrato OpenAPI, quickstart y suite de pruebas asociadas

**Unknowns / NEEDS CLARIFICATION**: None. Todas las aclaraciones de alto impacto quedaron resueltas en la spec (2026-03-18).

## Constitution Check (Pre-Research)

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

| Gate | Status | Evidence / Plan |
|------|--------|-----------------|
| Stack gate (Spring Boot 3.x + Java 17) | PASS | `pom.xml` usa Spring Boot 3.3.5 y `java.version` 17 |
| Security gate (Basic Auth) | PASS | `SecurityConfig` aplica `anyRequest().authenticated()` + httpBasic |
| Data gate (PostgreSQL + Flyway) | PASS | `application.yml` + `db/migration/V1__create_empleados_table.sql` |
| Runtime gate (Dockerized local integration) | PASS | `docker-compose.yml` define `backend` + `postgres` |
| Contract gate (OpenAPI/Swagger sync) | PASS | `OpenApiConfig` y contrato en `specs/001-crud-empleados/contracts` |
| Verification gate (auth, migration, key flows) | PASS | Existen pruebas `api`, `integration` y `performance`; se ampliaran para paginacion/versionado |

**Gate Result**: PASS. No se detectan violaciones constitucionales no justificadas.

## Phase 0: Research Output

Documento generado: `/specs/001-crud-empleados/research.md`

Objetivo de investigacion ejecutado:
- Definir patron de versionado URL v1 sin rutas legacy publicadas.
- Definir contrato paginado `{data, pagination}` con defaults y orden estable.
- Definir estrategia de validacion para `page`/`size` y errores de negocio.
- Definir sincronizacion OpenAPI + pruebas de seguridad/contrato.

Resultado: decisiones cerradas con rationale y alternativas para implementacion sin ambiguedades pendientes.

## Phase 1: Design & Contracts Output

Artefactos generados:
- `/specs/001-crud-empleados/data-model.md`
- `/specs/001-crud-empleados/contracts/empleados-openapi.yaml`
- `/specs/001-crud-empleados/quickstart.md`

Alcance de diseno:
- Modelo de respuesta paginada y metadata.
- Reglas de validacion de paginacion, formato de clave y rechazo de payload con `clave`.
- Definicion de endpoints versionados `/api/v1/empleados`.
- Casos de error 400/401/404 coherentes con la spec.

## Project Structure

### Documentation (this feature)

```text
specs/001-crud-empleados/
├── plan.md
├── research.md
├── data-model.md
├── quickstart.md
├── contracts/
│   └── empleados-openapi.yaml
└── tasks.md
```

### Source Code (repository root)

```text
src/
├── main/
│   ├── java/com/crudempleados/
│   │   ├── api/error/
│   │   ├── config/
│   │   ├── controller/
│   │   ├── domain/
│   │   ├── dto/
│   │   ├── model/
│   │   ├── repository/
│   │   └── service/
│   └── resources/
│       ├── application.yml
│       └── db/migration/
│           └── V1__create_empleados_table.sql
└── test/
    └── java/com/crudempleados/
        ├── api/
        ├── integration/
        ├── performance/
        └── security/

docker-compose.yml
Dockerfile
scripts/verification/
```

**Structure Decision**: Se conserva la arquitectura por capas existente (controller/service/repository/model), se mantiene Flyway en `src/main/resources/db/migration`, y la verificacion local sigue en dos contenedores Docker (`backend`, `postgres`) con configuracion externalizada por entorno.

## Post-Design Constitution Check

| Gate | Status | Design Alignment |
|------|--------|------------------|
| Stack gate | PASS | Diseno no introduce cambios de version ni stack fuera de Java 17 / Spring Boot 3 |
| Security gate | PASS | Contrato mantiene Basic Auth obligatorio en todas las rutas publicadas |
| Data gate | PASS | Modelo mantiene PostgreSQL + Flyway; no hay cambios fuera de migraciones versionadas |
| Runtime gate | PASS | Quickstart exige validacion en Docker Compose con backend + postgres |
| Contract gate | PASS | Contrato OpenAPI versionado y paginado sincronizado con la spec |
| Verification gate | PASS | Quickstart y plan contemplan cobertura para auth, validaciones y rutas versionadas |

**Post-Design Gate Result**: PASS.

## Complexity Tracking

No constitutional violations identified. This section remains empty by design.
