# Data Model: CRUD de Empleados v1

## Domain Overview

La feature mantiene el modelo de empleado con PK compuesta en persistencia y clave logica para API.
Se agrega modelo explicito para respuesta paginada del listado y para versionado de contrato en URL.

## Entity: Empleado

### Persisted Fields

| Field | Type | Required | Constraints | Description |
|------|------|----------|-------------|-------------|
| clave_prefijo | string | yes | PK part, valor fijo `EMP-`, largo 4 | Prefijo de la clave |
| clave_numero | long | yes | PK part, generado por secuencia, `> 0` | Componente numerico de la clave |
| nombre | string | yes | not blank, max 100 | Nombre del empleado |
| direccion | string | yes | not blank, max 100 | Direccion del empleado |
| telefono | string | yes | not blank, max 100 | Telefono del empleado |

### Logical/API Fields

| Field | Type | Source | Constraints |
|------|------|--------|-------------|
| clave | string | derivado de `clave_prefijo + clave_numero` | `^EMP-[1-9][0-9]*$` |
| nombre | string | persistido | max 100 |
| direccion | string | persistido | max 100 |
| telefono | string | persistido | max 100 |

## Entity: SecuenciaClaveEmpleado

| Field | Type | Required | Constraints | Description |
|------|------|----------|-------------|-------------|
| nombre_secuencia | string | yes | `empleados_clave_numero_seq` | Secuencia DB para `clave_numero` |
| valor_actual | long | yes | monotono creciente | Ultimo numero asignado |

Regla: la secuencia nunca debe retroceder ni duplicar numeros en altas concurrentes.

## Entity: PaginaEmpleados

### Shape

```json
{
  "data": [EmpleadoResponse],
  "pagination": {
    "page": 0,
    "size": 10,
    "totalElements": 0,
    "totalPages": 0
  }
}
```

### Pagination Fields

| Field | Type | Required | Constraints | Description |
|------|------|----------|-------------|-------------|
| page | integer | yes | `>= 0`, cero-basado | Indice de pagina actual |
| size | integer | yes | `> 0` y `<= 100` | Tamano de pagina solicitado |
| totalElements | long | yes | `>= 0` | Total de registros |
| totalPages | integer | yes | `>= 0` | Total de paginas |

Reglas:
- Defaults cuando faltan parametros: `page=0`, `size=10`.
- Orden fijo de `data`: `clave_numero` ascendente.

## Entity: VersionApi

| Field | Type | Required | Constraints | Description |
|------|------|----------|-------------|-------------|
| major | string | yes | valor publicado `v1` | Version mayor del contrato |
| basePath | string | yes | `/api/v1` | Prefijo de rutas de negocio |
| estado | string | yes | `published` o `unpublished` | Estado de disponibilidad |

Regla: rutas CRUD sin version o con version no publicada deben devolver `404`.

## Entity: ResultadoValidacion

| Field | Type | Required | Description |
|------|------|-------------|
| timestamp | string(date-time) | yes | Momento del error |
| status | integer | yes | Codigo HTTP |
| error | string | yes | Nombre del error HTTP |
| message | string | yes | Mensaje de validacion o negocio |
| path | string | yes | Ruta solicitada |

## Validation Rules

- `POST` acepta solo `nombre`, `direccion`, `telefono`; `clave` en payload se rechaza.
- `nombre`, `direccion`, `telefono`: not blank y maximo 100.
- `clave` de path para `GET/PUT/DELETE`: `^EMP-[1-9][0-9]*$` (sin ceros a la izquierda).
- Paginacion: `page >= 0`, `0 < size <= 100`.
- Defaults parciales: si falta uno de los parametros, se completa con su default (`page=0`, `size=10`).

## Relationships

- `PaginaEmpleados.data[*]` referencia instancias de `Empleado` serializadas como `EmpleadoResponse`.
- `Empleado.clave_numero` es generado por `SecuenciaClaveEmpleado`.
- `VersionApi` gobierna el prefijo de las rutas de `Empleado`.

## State Transitions

| Current State | Operation | Next State | Rule |
|--------------|-----------|------------|------|
| NonExistent | Create (`POST /api/v1/empleados`) | Active | Se asigna `clave_numero` siguiente y `clave_prefijo='EMP-'` |
| Active | Read (`GET` item/list) | Active | No mutacion |
| Active | Update (`PUT`) | Active | Solo mutan `nombre`, `direccion`, `telefono` |
| Active | Delete (`DELETE`) | NonExistent | Borrado fisico en esta version |
| NonExistent | Update/Delete/Get item | NonExistent | Responde `404` |

## Persistence Artifacts

- Tabla: `empleados`
- PK: (`clave_prefijo`, `clave_numero`)
- Secuencia: `empleados_clave_numero_seq`
- Migracion activa: `src/main/resources/db/migration/V1__create_empleados_table.sql`

## Contract Mapping Notes

- Conversor de clave debe mapear `EMP-{numero}` hacia PK compuesta antes del acceso al repositorio.
- Listado paginado debe serializar siempre el sobre `{data, pagination}`; no exponer `Page` de framework en contrato publico.
