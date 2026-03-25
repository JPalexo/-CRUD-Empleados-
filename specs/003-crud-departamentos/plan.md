# Implementation Plan: Gestion Integral de Departamentos y Metricas de Ocupacion

**Branch**: `003-crud-departamentos` | **Date**: 2026-03-24 | **Spec**: `/specs/003-crud-departamentos/spec.md`
**Input**: Feature specification from `/specs/003-crud-departamentos/spec.md`

## Summary

Implementar CRUD administrativo de departamentos con metricas de ocupacion
consistentes, validaciones de dominio y seguridad Basic Auth. El plan formaliza:

- componente dedicado de capacidad (`DepartamentoCapacityService`) con
  responsabilidades y fronteras explicitas;
- objetivo de performance medible con umbral, ventana de carga y criterio de
  aceptacion reproducible.

La entrega incluye migracion Flyway V3 como parte de la feature (V1 y V2 ya
existentes), contrato OpenAPI actualizado y coleccion Postman alineada.

## Technical Context

**Language/Version**: Java 17  
**Primary Dependencies**: Spring Boot 3.x, Spring Web, Spring Security, Spring Data JPA, Flyway, springdoc-openapi  
**Storage**: PostgreSQL 16 (entorno local via Docker Compose)  
**Testing**: JUnit 5, Spring Boot Test, MockMvc, integracion contra PostgreSQL dockerizado  
**Target Platform**: JVM server / contenedor Linux  
**Project Type**: Backend REST API (Spring Boot monolith)  
**Performance Goals**: Para `GET /api/v1/departamentos?page=0&size=20` y `GET /api/v1/departamentos/{clave}`, con 20 usuarios virtuales por 5 minutos en entorno local: p95 <= 300 ms, error rate < 1% y p99 <= 500 ms  
**Constraints**: Mantener Basic Auth, versionado `/api/v1`, migraciones Flyway versionadas, Docker Compose local, OpenAPI + Postman sincronizados, truncado de porcentaje a 2 decimales sin redondeo  
**Scale/Scope**: 4 endpoints de departamentos (GET listado/detalle, POST, PUT, DELETE), 2 entidades funcionales (Departamento y Empleado como fuente de ocupacion), regla de capacidad fija = 3

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

- [x] Stack gate: se mantiene Spring Boot 3.x + Java 17.
- [x] Security gate: endpoints administrativos bajo Basic Authentication.
- [x] Data gate: cambios de esquema en Flyway (V3 como entrega de esta feature).
- [x] Runtime gate: validacion local con PostgreSQL en Docker Compose.
- [x] Contract gate: OpenAPI/Swagger y Postman actualizados para cada cambio de API.
- [x] Verification gate: cobertura explicita de auth, migraciones y flujos API criticos.

## Project Structure

### Documentation (this feature)

```text
specs/003-crud-departamentos/
├── plan.md
├── research.md
├── data-model.md
├── quickstart.md
├── contracts/
│   └── departamentos-ocupacion.openapi.yaml
└── tasks.md
```

### Source Code (repository root)

```text
src/
├── main/
│   ├── java/com/crudempleados/
│   │   ├── api/error/
│   │   ├── config/
│   │   ├── config/security/
│   │   ├── controller/
│   │   ├── domain/
│   │   ├── dto/
│   │   ├── model/
│   │   ├── repository/
│   │   └── service/
│   └── resources/
│       └── db/migration/
│           ├── V1__create_empleados_table.sql
│           ├── V2__add_employee_login_tables.sql
│           └── V3__create_departamentos_and_assignment.sql
└── test/
    └── java/com/crudempleados/
        ├── api/
        ├── integration/
        ├── security/
        └── performance/

docker-compose.yml
postman/
```

**Structure Decision**: Reutilizar arquitectura por capas y formalizar un
servicio de capacidad (`DepartamentoCapacityService`) como modulo de dominio para
validaciones de cupo/ocupacion bajo concurrencia pesimista.

## Phase 0: Research Output

Archivo generado: `/specs/003-crud-departamentos/research.md`

Decisiones consolidadas:

- locking pesimista para atomicidad de cupo;
- secuencia monotona de `DEP-{numero}` sin reutilizacion;
- paginacion default 20, maximo 100 y `400` por overflow;
- formalizacion de `DepartamentoCapacityService` como frontera de reglas de
  capacidad y ocupacion;
- objetivo de performance con SLI/SLO medibles para endpoints de consulta.

## Phase 1: Design & Contracts Output

Archivos generados:

- `/specs/003-crud-departamentos/data-model.md`
- `/specs/003-crud-departamentos/contracts/departamentos-ocupacion.openapi.yaml`
- `/specs/003-crud-departamentos/quickstart.md`

Diseno formalizado:

- `DepartamentoCapacityService`
  - Entrada: `DepartamentoId` y contexto de operacion (create/update/delete/query).
  - Responsabilidades: calcular `ocupacionActual`, determinar `estaLleno`, aplicar
    regla de `capacidadMaxima=3`, centralizar chequeos de dominio para evitar
    sobrecupo y borrado invalido.
  - Integracion: invocado por `DepartamentoQueryService` y
    `DepartamentoDeleteService`; usa repositorios con locking pesimista para
    operaciones criticas.
- Criterio medible de performance
  - SLI: latencia p95/p99 y tasa de error en endpoints de consulta.
  - SLO local: p95 <= 300 ms, p99 <= 500 ms, error rate < 1% con 20 VUs por 5 min.
  - Evidencia: reporte de prueba de carga y registro en quickstart.

## Post-Design Constitution Check

- [x] Stack gate validado.
- [x] Security gate validado con escenarios `401`.
- [x] Data gate validado con V3 en Flyway.
- [x] Runtime gate validado con Docker Compose.
- [x] Contract gate validado en OpenAPI y Postman.
- [x] Verification gate validado: auth, migraciones, CRUD, metricas y performance con criterio medible.

## Complexity Tracking

Sin violaciones constitucionales; no se requieren excepciones.

## Implementation Execution Evidence (2026-03-24)

- Build constitucional Java 17 ejecutado con `mvn clean verify`.
- Resultado: BUILD SUCCESS.
- Comando de performance FR-024/SC-014 ejecutado con parametros formales:
  - `mvn -Dtest=DepartamentoQueryPerformanceTest -Dperf.users=20 -Dperf.duration.ms=300000 -Dperf.p95.max.ms=300 -Dperf.p99.max.ms=500 -Dperf.max.error.rate=0.01 test`
- Resultado de performance en esta sesion: prueba SKIPPED por Testcontainers (`Could not find a valid Docker environment`).
- Verificacion de infraestructura local: `docker version` responde client/server; persiste incompatibilidad de deteccion Testcontainers.

Pendientes para cierre completo de validacion:

1. Restablecer conectividad Testcontainers-Docker para correr pruebas IT/performance no omitidas.
2. Registrar valores reales p95/p99/error rate en `performance-report-fr024-sc014.md`.
