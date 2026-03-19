# Implementation Plan: Login Simbolico de Empleado

**Branch**: `002-employee-user-login` | **Date**: 2026-03-18 | **Spec**: `/specs/002-employee-user-login/spec.md`
**Input**: Feature specification from `/specs/002-employee-user-login/spec.md`

**Note**: This template is filled in by the `/speckit.plan` command. See `.specify/templates/plan-template.md` for the execution workflow.

## Summary

Implementar login simbolico de empleado con email/password en dos frentes:
1) extender el alta de empleado para crear credenciales en la misma transaccion,
2) exponer un endpoint publico de login de empleado independiente del canal
administrativo Basic Auth. El diseno mantiene Java 17 + Spring Boot 3, persiste
credenciales con hash+salt no reversible, normaliza email con trim+lowercase,
registra eventos de login para trazabilidad y preserva intacta la seguridad del
CRUD administrativo existente.

## Technical Context

**Language/Version**: Java 17  
**Primary Dependencies**: Spring Boot 3.3.5 (Web, Validation, Data JPA,
Security, Actuator), Flyway, PostgreSQL driver, springdoc-openapi 2.6.0  
**Storage**: PostgreSQL 16 (esquema versionado con Flyway)  
**Testing**: JUnit 5, Spring Boot Test, MockMvc, Spring Security Test,
Testcontainers PostgreSQL, scripts de verificacion PowerShell  
**Target Platform**: Contenedores Linux (Docker Compose) + JVM server
**Project Type**: Backend REST API (Spring Boot monolito)  
**Performance Goals**: p95 de login valido < 2s con 50 solicitudes concurrentes;
respuesta de login utilizable dentro de 60s tras alta de empleado  
**Constraints**: mantener Basic Auth admin para CRUD, endpoint de login empleado
publico y sin token, hash+salt no reversible, email canonico trim+lowercase,
sin bloqueo de cuenta por intentos fallidos, OpenAPI sincronizado  
**Scale/Scope**: 1 endpoint existente extendido (POST alta), 1 endpoint nuevo de
login simbolico, 2 nuevas entidades de persistencia (credenciales y evento),
1 migracion Flyway obligatoria (`V2__add_employee_login_tables.sql`) para
crear estructuras de credenciales y eventos; `V3__...` solo si aplica criterio
formal de particion (backfill de datos o rollout por fases con riesgo operativo),
cobertura API/integracion/seguridad

### Migration Partition Criteria

- **Default (aprobado para esta feature)**: una sola migracion `V2` que incluye
  DDL de `empleado_credenciales` + `empleado_login_eventos`, indices y
  constraints, sin backfill.
- **Criterio para dividir en V3**: separar una segunda migracion solo si aparece
  al menos una de estas condiciones:
  1. se requiere backfill de datos historicos o transformacion masiva,
  2. se necesita despliegue por fases con compatibilidad temporal,
  3. hay riesgo alto de bloqueo/tiempo de ejecucion que exija ventana separada.
- **Regla de gobernanza**: si se activa V3, debe actualizarse `plan.md`,
  `research.md`, `tasks.md` y `quickstart.md` con racional explicito y orden de
  aplicacion.

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

Gate Status (pre-Phase 0): PASS

- [x] Stack gate: se mantiene Spring Boot 3.3.5 + Java 17 sin cambiar baseline.
- [x] Security gate: solo `/api/v1/empleados/login` se declara publico y el resto
  continua con Basic Auth administrativo.
- [x] Data gate: todo cambio de esquema se entrega via migraciones Flyway
  versionadas para PostgreSQL.
- [x] Runtime gate: validacion local via Docker Compose (`postgres` + `backend`).
- [x] Contract gate: se planifica actualizar OpenAPI para alta extendida y login.
- [x] Verification gate: se incorporan pruebas para alta+credenciales, login,
  separacion de canales, migraciones y no regresion admin.

Gate Status (post-Phase 1 design): PASS

- [x] Stack gate: el diseno detallado (`research.md`, `data-model.md`) no cambia
  baseline Java 17 + Spring Boot 3.
- [x] Security gate: `contracts/employee-login.openapi.yaml` delimita login
  publico de empleado y canal admin con Basic Auth.
- [x] Data gate: `data-model.md` define tablas nuevas bajo estrategia Flyway.
- [x] Runtime gate: `quickstart.md` usa Docker Compose para validacion local.
- [x] Contract gate: contrato OpenAPI especifica entradas/salidas y errores.
- [x] Verification gate: quickstart y decisiones de investigacion cubren pruebas
  de auth, no regresion, migracion y flujos principales.

## Project Structure

### Documentation (this feature)

```text
specs/002-employee-user-login/
├── plan.md
├── research.md
├── data-model.md
├── quickstart.md
├── contracts/
│   └── employee-login.openapi.yaml
└── tasks.md
```

### Source Code (repository root)

```text
src/
├── main/
│   ├── java/com/crudempleados/
│   │   ├── api/error/
│   │   ├── config/
│   │   │   └── security/
│   │   ├── controller/
│   │   ├── domain/
│   │   ├── dto/
│   │   ├── model/
│   │   ├── repository/
│   │   └── service/
│   └── resources/
│       ├── application.yml
│       └── db/migration/
│           ├── V1__create_empleados_table.sql
│           └── V2__add_employee_login_tables.sql
└── test/
    └── java/com/crudempleados/
        ├── api/
        ├── integration/
        ├── security/
        └── support/

docker-compose.yml
scripts/verification/
```

**Structure Decision**: mantener arquitectura en capas existente y agregar
artefactos de login en los mismos paquetes (`controller`, `service`, `repository`,
`model`, `dto`) para minimizar friccion con el codigo actual. Las migraciones se
concentran en `src/main/resources/db/migration` con estrategia default de una sola
`V2` y criterio formal de particion a `V3` solo bajo condiciones de riesgo,
y la validacion operativa conserva
`docker-compose.yml` y scripts de `scripts/verification`.

## Complexity Tracking

No constitutional violations identified.
