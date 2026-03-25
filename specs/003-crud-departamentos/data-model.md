# Data Model: Gestion Integral de Departamentos y Metricas de Ocupacion

**Feature**: 003-crud-departamentos
**Date**: 2026-03-24

## 1. Departamento (persistente)

- `clave_prefijo` (varchar, PK compuesta, valor canonico `DEP-`)
- `clave_numero` (bigint, PK compuesta, secuencial)
- `nombre` (varchar, valor de presentacion)
- `nombre_normalizado` (varchar, unico, `trim+lowercase`)
- `created_at` (timestamp)
- `updated_at` (timestamp)

### Regla de generacion de clave

- `clave` funcional: `DEP-{numero}`
- `numero` proviene de secuencia monotona creciente.
- Los numeros de departamentos eliminados NO se reutilizan.

### Constraints

- PK compuesta (`clave_prefijo`, `clave_numero`)
- Unicidad de `nombre_normalizado`

## 2. Empleado (relacion para ocupacion)

Campos relevantes para metrica de ocupacion:

- `departamento_clave_prefijo`
- `departamento_clave_numero`

### Regla de conteo

`ocupacionActual` = total de empleados registrados cuyo par (`departamento_clave_prefijo`, `departamento_clave_numero`) coincide con el departamento consultado.

No existe filtro por estado activo/inactivo en esta version.

## 3. DTOs de entrada/salida

### DepartamentoCreateRequest

- `nombre` (required)

### DepartamentoUpdateRequest

- `nombre` (required)

### DepartamentoResponse

- `clave`
- `nombre`
- `ocupacionActual`
- `capacidadMaxima` (constante 3)
- `porcentajeOcupacion` (truncado a 2 decimales sin redondeo)
- `estaLleno` (`ocupacionActual >= 3`)

### DepartamentoPageResponse

- `data[]` (DepartamentoResponse)
- `pagination` (`page`, `size`, `totalElements`, `totalPages`)

### Parametros de paginacion (query)

- `page` (opcional, default 0)
- `size` (opcional, default 20, maximo 100)

### ApiError (respuesta de validacion)

- `status` (integer HTTP status)
- `error` (string)
- `message` (string)
- `path` (string)
- `timestamp` (string/date-time)

Se usa para errores como `400` cuando `size > 100`.

## 4. Reglas de estado/negocio

- Create: normaliza nombre y valida unicidad.
- Update: normaliza nombre y valida unicidad excluyendo el propio registro.
- Delete: si `ocupacionActual > 0`, lanzar conflicto `409`.
- Query list/detail: calcular metricas dinamicas de forma consistente en un mismo servicio.
- Paginacion: cuando `size > 100`, rechazar solicitud con `400`.
- Concurrencia: validar cupo y consistencia mediante locking pesimista en operaciones criticas.

## 5. Componente de dominio: DepartamentoCapacityService

Responsabilidades:

- calcular `ocupacionActual` a partir de clave compuesta;
- derivar `estaLleno` con `capacidadMaxima = 3`;
- centralizar validaciones de capacidad para operaciones criticas (incluyendo delete);
- coordinar uso de locking pesimista en operaciones que requieren atomicidad.

Interacciones:

- consumido por `DepartamentoQueryService` para metrica de lectura;
- consumido por `DepartamentoDeleteService` para proteger integridad de borrado;
- depende de `EmpleadoRepository` y `DepartamentoRepository`.

Contrato interno esperado:

- `buildMetrics(DepartamentoId id) -> DepartamentoMetricas`
- `assertDeletionAllowed(DepartamentoId id)`
- `assertCapacityInvariant(DepartamentoId id)`

## 6. Criterio medible de performance

SLI/SLO locales para endpoints de consulta de departamentos:

- Endpoint scope: `GET /api/v1/departamentos?page=0&size=20` y `GET /api/v1/departamentos/{clave}`.
- Carga: 20 usuarios virtuales, duracion 5 minutos.
- Umbrales de aceptacion:
	- p95 <= 300 ms
	- p99 <= 500 ms
	- error rate < 1%

Evidencia minima:

- reporte de ejecucion en artefactos de prueba de performance;
- registro de resultado en quickstart/plan.

## 7. Migraciones relevantes

- `V3__create_departamentos_and_assignment.sql` define tabla y relacion base para departamentos.
