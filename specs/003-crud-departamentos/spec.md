# Feature Specification: Gestion Integral de Departamentos y Metricas de Ocupacion

**Feature Branch**: `003-crud-departamentos`  
**Created**: 2026-03-24  
**Status**: Draft  
**Input**: User description: "Ajustar la feature 003 para consolidar CRUD completo de departamentos, metricas de ocupacion e integridad de dominio sobre el estado ya implementado."

## Objetivo

Implementar y formalizar el CRUD completo de departamentos asegurando la integridad
 de metricas de capacidad y restricciones de dominio, sobre el comportamiento ya
 operativo de la aplicacion.

## Alcance Funcional

- Creacion: `POST /api/v1/departamentos` con generacion de clave `DEP-{numero}` y normalizacion de nombre (`trim + lowercase`).
- Lectura: `GET` de listado y detalle con campos calculados `ocupacionActual`, `capacidadMaxima` (fija en 3), `porcentajeOcupacion` y `estaLleno`.
- Actualizacion: `PUT` para modificar nombre manteniendo unicidad por nombre normalizado.
- Eliminacion: `DELETE` protegido; prohibido eliminar departamentos con empleados asignados (`409`).

## Contexto Existente

- El modelo de empleados y las migraciones `V1`/`V2` ya existen; la migracion `V3__create_departamentos_and_assignment.sql` forma parte de esta feature.
- El enfoque de esta feature es completar y formalizar capa de servicio/controladores y el contrato funcional de ocupacion.

## Clarifications

### Session 2026-03-24

- Q: Se mantiene la seguridad administrativa actual para departamentos? -> A: Si, todos los endpoints de departamentos permanecen bajo Basic Auth admin.
- Q: La capacidad de departamento es configurable o fija? -> A: Es fija en 3 empleados por departamento para esta version.
- Q: Que metricas de ocupacion deben exponerse? -> A: `ocupacionActual`, `capacidadMaxima`, `porcentajeOcupacion`, `estaLleno`.
- Q: Como calcular el redondeo de `porcentajeOcupacion`? -> A: Truncar a 2 decimales (sin redondeo).
- Q: Que empleados cuentan para `ocupacionActual`? -> A: Todos los empleados registrados vinculados al departamento por clave compuesta (prefijo + numero).
- Q: Que estrategia de concurrencia aplica para asegurar atomicidad de cupo? -> A: Control de concurrencia mediante locking pesimista a nivel de base de datos.
- Q: Que mecanismo exacto de locking se adopta para esta feature? -> A: Locking pesimista en base de datos para operaciones criticas de capacidad.
- Q: Como se administra la numeracion de claves `DEP-{numero}` tras eliminaciones? -> A: La numeracion no se reutiliza; siempre se incrementa de forma monotona.
- Q: Que politica de paginacion se adopta para listado de departamentos? -> A: `size` default 20 y maximo 100.
- Q: Como se maneja una solicitud de listado con `size` superior al maximo? -> A: Se rechaza con `400` y mensaje de validacion.

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Administrar catalogo de departamentos (Priority: P1)

Como usuario administrativo, quiero crear, consultar, actualizar y eliminar departamentos para gestionar la estructura organizacional.

**Why this priority**: Sin catalogo de departamentos no existe base para medir ocupacion por area.

**Independent Test**: Crear departamento, consultar listado/detalle, actualizar nombre y eliminar un departamento sin empleados.

**Acceptance Scenarios**:

1. **Given** un nombre de departamento valido, **When** se crea el departamento, **Then** el sistema genera clave `DEP-{numero}` y responde `201`.
2. **Given** departamentos existentes, **When** se consulta listado paginado, **Then** el sistema devuelve `{data, pagination}` con metadatos coherentes y politica `size` default 20, maximo 100.
3. **Given** una clave existente, **When** se consulta detalle, **Then** devuelve el recurso esperado.
4. **Given** un departamento sin empleados asignados, **When** se elimina, **Then** responde `204`.
5. **Given** una solicitud sin credenciales administrativas validas, **When** se invoca cualquier endpoint de `/api/v1/departamentos`, **Then** el sistema responde `401` (acceso denegado).
6. **Given** una solicitud de listado con `size` mayor a 100, **When** se procesa la peticion, **Then** el sistema responde `400` con detalle de validacion.

---

### User Story 2 - Visualizar metricas de ocupacion (Priority: P1)

Como usuario administrativo, quiero ver metricas de ocupacion en listado y detalle de departamentos para monitorear capacidad.

**Why this priority**: Entrega visibilidad operativa inmediata para decisiones de carga por area.

**Independent Test**: Comparar metricas para una misma clave entre listado y detalle y validar truncado del porcentaje.

**Acceptance Scenarios**:

1. **Given** departamentos con empleados vinculados, **When** se consulta listado, **Then** cada item incluye `ocupacionActual`, `capacidadMaxima`, `porcentajeOcupacion` y `estaLleno`.
2. **Given** un departamento concreto, **When** se consulta detalle, **Then** las metricas coinciden con la misma formula del listado.
3. **Given** un resultado de porcentaje con mas de dos decimales, **When** se expone la metrica, **Then** el valor se trunca a 2 decimales sin redondeo.

---

### User Story 3 - Proteger integridad de dominio (Priority: P2)

Como responsable del sistema, quiero que las reglas de dominio eviten inconsistencias de capacidad y borrado.

**Why this priority**: Garantiza consistencia de datos y evita estados invalidos.

**Independent Test**: Intentar borrar un departamento con personal y validar conflicto `409`; validar atomicidad con concurrencia sobre cupo.

**Acceptance Scenarios**:

1. **Given** un departamento con empleados vinculados, **When** se intenta eliminar, **Then** el sistema responde `409`.
2. **Given** accesos concurrentes que compiten por estado de cupo, **When** se evalua ocupacion, **Then** el sistema aplica locking pesimista de base de datos para mantener atomicidad y evitar sobrecupo.
3. **Given** un nombre duplicado tras normalizacion, **When** se intenta crear o actualizar, **Then** el sistema responde `409`.

### Edge Cases

- Nombre de departamento duplicado por variaciones de espacios/mayusculas debe tratarse como conflicto.
- Clave de departamento con formato invalido debe responder `400`.
- La eliminacion de un departamento no libera su numero para reutilizacion futura de clave.
- Solicitudes de listado deben operar con `size` default 20 y tope maximo de 100.
- Solicitudes con `size` superior a 100 deben rechazarse con `400`.
- `porcentajeOcupacion` se calcula truncando a 2 decimales, sin redondeo.
- Rutas sin version o en `v2` deben responder `404`.
- `ocupacionActual` considera todos los empleados registrados vinculados al departamento (sin filtro por estado).
- En escenarios de concurrencia, la atomicidad se garantiza mediante locking pesimista en base de datos.

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: El sistema MUST exponer CRUD administrativo de departamentos en `/api/v1/departamentos`.
- **FR-002**: El sistema MUST generar clave funcional de departamento con formato `DEP-{numero}` usando una secuencia monotona sin reutilizacion de numeros eliminados.
- **FR-003**: El sistema MUST mantener unicidad de nombre de departamento sobre valor normalizado (trim + lowercase).
- **FR-004**: El sistema MUST devolver listado de departamentos con estructura paginada `{data, pagination}`.
- **FR-022**: El sistema MUST aplicar en listado de departamentos una paginacion con `size` por defecto de 20 y limite maximo de 100.
- **FR-023**: El sistema MUST rechazar con `400` cualquier solicitud de listado con `size` superior a 100.
- **FR-005**: El sistema MUST impedir eliminacion de departamentos con empleados vinculados y responder `409`.
- **FR-012**: El sistema MUST exponer en departamentos las metricas `ocupacionActual`, `capacidadMaxima`, `porcentajeOcupacion` y `estaLleno`.
- **FR-021**: El sistema MUST calcular `ocupacionActual` como el total de empleados registrados vinculados al mismo departamento por clave compuesta (prefijo + numero).
- **FR-013**: El sistema MUST mantener consistencia de metricas entre listado y detalle de departamentos.
- **FR-020**: El sistema MUST calcular `porcentajeOcupacion` truncando a 2 decimales (sin redondeo) y aplicar la misma regla en listado y detalle.
- **FR-014**: El sistema MUST responder `409` para conflictos de dominio: nombre duplicado y departamento en uso.
- **FR-015**: El sistema MUST mantener Basic Auth administrativa para endpoints de departamentos.
- **FR-017**: El sistema MUST responder `404` para rutas sin version o versiones no publicadas.
- **FR-018**: El sistema MUST mantener contrato OpenAPI y coleccion Postman sincronizados con el comportamiento real de departamentos.
- **FR-019**: El sistema MUST garantizar atomicidad de validaciones de cupo mediante locking pesimista a nivel de base de datos.
- **FR-024**: El sistema MUST validar performance bajo carga controlada local para `GET /api/v1/departamentos?page=0&size=20` y `GET /api/v1/departamentos/{clave}` con 20 usuarios virtuales durante 5 minutos, verificando umbrales medibles de latencia y tasa de error.

### Constitutional Constraints *(mandatory for every feature)*

- **CC-001**: La feature MUST respetar Java 17 + Spring Boot 3.x + PostgreSQL + Flyway.
- **CC-002**: Los cambios de esquema MUST entregarse mediante migraciones Flyway versionadas.
- **CC-003**: Las rutas administrativas de departamentos MUST permanecer protegidas con Basic Auth y tener pruebas de acceso denegado.
- **CC-004**: Debe existir cobertura de pruebas de API e integracion para rutas, capacidad y ocupacion.
- **CC-005**: Debe existir evidencia reproducible en entorno local con Docker Compose.

### Key Entities *(include if feature involves data)*

- **Departamento**: Unidad organizativa con clave `DEP-{numero}`, nombre unico normalizado y capacidad maxima efectiva de 3.
- **DepartamentoMetricas**: Proyeccion de ocupacion para listado/detalle (`ocupacionActual`, `capacidadMaxima`, `porcentajeOcupacion`, `estaLleno`).

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: El 100% de creates validos de departamento devuelve `201` con clave `DEP-{numero}`.
- **SC-003**: El 100% de creates posteriores a eliminaciones mantiene incremento monotono de `DEP-{numero}` sin reutilizar numeros previos.
- **SC-002**: El 100% de intentos con nombre de departamento duplicado normalizado devuelve `409`.
- **SC-012**: El 100% de listados sin parametro `size` usa 20 por defecto y el 100% de listados con `size` valido aplica tope maximo de 100.
- **SC-013**: El 100% de solicitudes con `size` mayor a 100 devuelve `400` con mensaje de validacion.
- **SC-004**: El 100% de intentos de borrar departamentos con empleados vinculados devuelve `409` sin cambios parciales.
- **SC-005**: El 100% de responses de departamentos (listado/detalle) incluye las 4 metricas de ocupacion.
- **SC-006**: El 100% de comparaciones listado vs detalle para misma clave mantiene coherencia de metricas.
- **SC-010**: El 100% de respuestas con `porcentajeOcupacion` refleja truncamiento consistente a 2 decimales (sin redondeo).
- **SC-011**: El 100% de calculos de `ocupacionActual` coincide con el total de empleados registrados vinculados a cada departamento.
- **SC-007**: El 100% de accesos sin credenciales Basic Auth a `/api/v1/departamentos` devuelve `401`.
- **SC-008**: El 100% de rutas sin version o `v2` en recursos administrativos devuelve `404`.
- **SC-014**: En prueba de carga local de 5 minutos con 20 usuarios virtuales sobre `GET /api/v1/departamentos?page=0&size=20` y `GET /api/v1/departamentos/{clave}`, el sistema mantiene p95 <= 300 ms, p99 <= 500 ms y error rate < 1%.

## Assumptions

- La capacidad maxima efectiva de esta version se mantiene fija en 3 empleados por departamento.
- El sistema ya cuenta con migraciones V1 y V2 aplicadas; V3 se introduce en esta feature y debe aplicarse por Flyway.
- La logica de conteo de ocupacion se basa en clave compuesta (prefijo + numero), de acuerdo con la implementacion tecnica ya validada.

## Dependencies

- Migraciones Flyway: `V1__create_empleados_table.sql`, `V2__add_employee_login_tables.sql`, `V3__create_departamentos_and_assignment.sql`.
- Seguridad administrativa en `SecurityConfig` y versionado en `ApiVersionConfig`.
- Coleccion Postman local para validacion funcional y demo de endpoints de departamentos.
