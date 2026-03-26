# Implementation Plan: Frontend CRUD de Empleados y Departamentos

**Branch**: `004-frontend-crud-ui` | **Date**: 2026-03-25 | **Spec**: `/specs/004-frontend-crud-ui/spec.md`
**Input**: Feature specification from `/specs/004-frontend-crud-ui/spec.md`

## Summary

Implementar frontend administrativo en Angular 22 LTS para empleados y departamentos
consumiendo APIs versionadas `/api/v1`, con autenticacion Basic Auth en backend,
sesion por inactividad y manejo de conflictos. Este plan formaliza, ademas, una
arquitectura de validacion medible para:

- SC-003: exactitud y latencia de mensajes de validacion en entradas invalidas.
- SC-006: operabilidad CRUD completa en resoluciones desktop y mobile.

La validacion medible queda trazada a tareas T061, T062 y T063.

## Technical Context

**Language/Version**: Java 17 (backend), TypeScript estricto (frontend Angular 22 LTS)
**Primary Dependencies**: Spring Boot 3.x, Spring Security Basic Auth, Spring Data JPA, Flyway, springdoc-openapi, PostgreSQL driver, Angular 22, RxJS
**Storage**: PostgreSQL (sin cambios de esquema requeridos para esta feature)
**Testing**: JUnit 5/Spring Boot Test para backend existente, pruebas frontend (unitarias + validacion funcional), verificacion de lint/build e integracion local
**Target Platform**: Backend JVM Linux-compatible + navegador desktop/mobile
**Project Type**: Full-stack monorepo (backend Spring Boot + frontend Angular)
**Performance Goals**:
- SC-003: >= 95% de intentos invalidos con mensaje correcto y latencia <= 2s (p95).
- SC-006: 100% de acciones CRUD principales ejecutables sin bloqueo funcional en 1366x768 y 390x844.
**Constraints**: mantener `/api/v1`, Angular 22 LTS, Basic Auth, Docker local para PostgreSQL/integracion, sin secretos hardcodeados, constitucion v1.1.0
**Scale/Scope**: 3 user stories (empleados, departamentos, asignacion), 14 FR, 9 SC, evidencia cuantitativa obligatoria para SC-003/SC-006

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

- [x] Stack gate: se mantiene Spring Boot 3.x + Java 17 en backend.
- [x] Frontend gate: implementacion en Angular 22 LTS con TypeScript estricto.
- [x] Security gate: endpoints administrativos bajo Basic Auth; excepciones documentadas.
- [x] Data gate: sin cambios de schema fuera de Flyway.
- [x] Runtime gate: validacion local con Docker Compose y PostgreSQL.
- [x] Integration gate: verificacion frontend-backend en entorno local dockerizado.
- [x] Contract gate: consumo y validacion de contrato OpenAPI vigente.
- [x] API versioning gate: consumo frontend restringido a `/api/v1`.
- [x] Verification gate: tareas y evidencia para auth, build/lint, integracion y SC medibles.

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
├── performance-report-sc003.md
├── performance-report-sc006.md
└── tasks.md
```

### Source Code (repository root)

```text
src/
├── main/
│   ├── java/com/crudempleados/
│   └── resources/
└── test/
    └── java/com/crudempleados/

frontend/
├── src/
│   ├── app/
│   │   ├── pages/
│   │   ├── components/
│   │   ├── services/
│   │   ├── models/
│   │   └── core/
│   │       ├── guards/
│   │       └── interceptors/
│   └── environments/
└── tests/

docker-compose.yml
```

**Structure Decision**: mantener backend existente y añadir/modularizar `frontend/`
con capas pages/components/services/models/guards/interceptors. La evidencia de
validacion medible se almacena en artefactos de `specs/004-frontend-crud-ui/`.

## Phase 0: Research Output

Archivo generado: `/specs/004-frontend-crud-ui/research.md`

Decisiones consolidadas (sin `NEEDS CLARIFICATION` pendientes):

- sesion persistente con timeout por inactividad de 30 minutos;
- bloqueo de eliminacion de departamento con empleados asignados;
- deteccion de conflicto de concurrencia con recarga obligatoria;
- servicios tipados sobre endpoints versionados `/api/v1`;
- manejo centralizado de error/loading;
- baseline responsive y accesibilidad;
- verificacion de integracion contra runtime local dockerizado.

## Phase 1: Design & Contracts Output

Archivos generados:

- `/specs/004-frontend-crud-ui/data-model.md`
- `/specs/004-frontend-crud-ui/contracts/frontend-admin-ui.openapi.yaml`
- `/specs/004-frontend-crud-ui/quickstart.md`

Diseno formalizado:

- modelos frontend para `Empleado`, `Departamento`, estado de sesion y estado CRUD;
- flujo de autenticacion/sesion y control de rutas protegidas;
- interceptores para actividad, errores HTTP y consistencia UX;
- contrato operativo de endpoints backend consumidos por UI bajo `/api/v1`.

## Arquitectura de Validacion Medible (SC-003 y SC-006)

### 1) Metodologia de medicion

- Enfoque: medicion reproducible por escenarios controlados, con evidencia primaria
  (network timing + captura UI) y consolidacion en reportes versionables.
- Ejecucion:
  - SC-003 por matriz de casos invalidos (campos obligatorios, formato email,
    `departamentoClave` invalido, reglas de backend retornadas en 4xx).
  - SC-006 por checklist CRUD operativo en dos viewport fijos (1366x768 y 390x844).
- Repeticion minima:
  - SC-003: 20 intentos invalidos por modulo (empleados y departamentos), total >= 40.
  - SC-006: 5 acciones CRUD principales x 2 modulos x 2 resoluciones = 20 verificaciones.
- Normalizacion:
  - mismo dataset base,
  - misma version backend/frontend,
  - backend ejecutando en entorno local con Docker para dependencia de BD.

### 2) Escenarios de validacion

- SC-003 (latencia + exactitud de validacion)
  - Escenario V1: alta empleado con email invalido.
  - Escenario V2: alta/edicion empleado sin campos requeridos.
  - Escenario V3: asignacion con `departamentoClave` inexistente.
  - Escenario V4: alta/edicion departamento con nombre invalido/duplicado normalizado.
  - Resultado esperado: mensaje correcto por regla y tiempo de feedback <= 2s.
- SC-006 (operabilidad responsive)
  - Escenario R1 (1366x768): listar, crear, editar, eliminar, reasignar sin bloqueo.
  - Escenario R2 (390x844): listar, crear, editar, eliminar, reasignar sin bloqueo.
  - Resultado esperado: 100% de acciones ejecutables y finalizadas correctamente.

### 3) Instrumentos y registro de evidencia

- Instrumentos:
  - DevTools Network para marca temporal `requestStart`/`responseEnd`.
  - Captura de UI (pantalla/video corto) para confirmar mensaje visible/accion final.
  - Checklist estructurado por accion y viewport.
- Artefactos de evidencia:
  - `/specs/004-frontend-crud-ui/performance-report-sc003.md`
  - `/specs/004-frontend-crud-ui/performance-report-sc006.md`
  - `/specs/004-frontend-crud-ui/quickstart.md` (resumen consolidado y enlaces)
- Formato minimo por registro:
  - `timestamp`, `escenario`, `modulo`, `resultado esperado`, `resultado observado`,
    `latencia_ms` (cuando aplique), `pass/fail`, `evidencia adjunta`.

### 4) Umbrales de aceptacion

- SC-003:
  - Exactitud: mensajes correctos en >= 95% de intentos invalidos.
  - Latencia: p95 de `latencia_ms` <= 2000 ms.
  - Criterio de rechazo: cualquiera de los dos umbrales incumplido.
- SC-006:
  - Operabilidad: 100% de acciones CRUD principales completables en ambas resoluciones.
  - Criterio de rechazo: una sola accion bloqueada en cualquier viewport.

### 5) Trazabilidad a tareas

- T061 -> implementa ejecucion y medicion SC-003 + reporte `performance-report-sc003.md`.
- T062 -> implementa ejecucion y medicion SC-006 + reporte `performance-report-sc006.md`.
- T063 -> consolida evidencia de T061/T062 en `quickstart.md` con estado de cumplimiento final.

Matriz de trazabilidad:

| Criterio | Evidencia primaria | Tarea(s) | Artefacto de salida |
|----------|--------------------|----------|---------------------|
| SC-003 | matriz de intentos invalidos + latencias | T061 | performance-report-sc003.md |
| SC-006 | checklist CRUD por viewport | T062 | performance-report-sc006.md |
| SC-003/SC-006 cierre | resumen y links verificables | T063 | quickstart.md |

## Post-Design Constitution Check

- [x] Stack gate validado.
- [x] Frontend gate validado (Angular 22 LTS + TS estricto).
- [x] Security gate validado (Basic Auth + matriz positiva/negativa en tasks).
- [x] Data gate validado (sin cambios fuera de Flyway).
- [x] Runtime gate validado (Docker local obligatorio en quickstart).
- [x] Integration gate validado (flujo UI-backend local).
- [x] Contract gate validado (OpenAPI alineado en contracts).
- [x] API versioning gate validado (`/api/v1` como unico prefijo de consumo).
- [x] Verification gate validado (T061-T063 formalizan medicion y evidencia).

## Complexity Tracking

Sin violaciones constitucionales; no se requieren excepciones.
