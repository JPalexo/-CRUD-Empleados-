# Research: CRUD de Empleados v1 (Paginacion + Versionado URL)

## Scope

Feature: `CRUD de Empleados` on branch `001-crud-empleados`.

Estado de aclaraciones: no quedan items `NEEDS CLARIFICATION` abiertos para esta feature.

## Decision Log

### 1) Versionado de endpoints en URL

- Decision: Publicar solo rutas `/api/v1/empleados` y `/api/v1/empleados/{clave}` para operaciones CRUD.
- Rationale: La spec exige versionado en URL y prohibe exponer rutas equivalentes sin version.
- Alternatives considered: Mantener rutas legacy `/api/empleados` (rechazada por FR-016/FR-018); versionado por header (rechazada porque la spec define version en URL).

### 2) Comportamiento para rutas no versionadas o versiones no publicadas

- Decision: Solicitudes a rutas CRUD sin version y versiones distintas de `v1` deben responder `404` con error consistente.
- Rationale: Evita ambiguedad de contrato y obliga consumo explicito de la version publicada.
- Alternatives considered: `400` por version invalida (rechazada por aclaracion de negocio); redireccion automatica a `v1` (rechazada por riesgo de comportamiento implicito).

### 3) Estrategia de paginacion en listado

- Decision: Implementar listado con `page` y `size` cero-basado, defaults `page=0`, `size=10`, y validaciones `page >= 0`, `size > 0`, `size <= 100`.
- Rationale: Cubre FR-019/FR-020 y mantiene consistencia con convencion de paginacion de Spring Data.
- Alternatives considered: Paginacion 1-basada (rechazada por aclaracion final); parametros obligatorios sin defaults (rechazada por UX y FR-020).

### 4) Forma de respuesta del listado

- Decision: Retornar siempre sobre `{data, pagination}` en el `GET` listado.
- Rationale: Desacopla metadata de paginacion del arreglo de datos y estandariza contrato para clientes.
- Alternatives considered: Retornar `Page<T>` nativo de Spring (rechazada por acoplamiento de framework); retornar solo arreglo sin metadata (rechazada por requerimiento de paginacion).

### 5) Orden estable para navegacion por paginas

- Decision: Ordenar listado por `clave_numero` ascendente en todas las consultas paginadas.
- Rationale: Mantiene estabilidad entre paginas y evita resultados no deterministas.
- Alternatives considered: Orden por `nombre` (rechazada por no reflejar secuencia de altas); orden no especificado (rechazada por inestabilidad entre paginas).

### 6) Modelo de identidad del empleado

- Decision: Persistir PK compuesta (`clave_prefijo`, `clave_numero`) y exponer `clave` logica en formato `EMP-{numero}` sin ceros a la izquierda.
- Rationale: Respeta la decision de negocio y mantiene integridad en base relacional.
- Alternatives considered: PK unica tipo string `clave` (rechazada por requerimiento de persistencia compuesta); numerico puro en API (rechazada por contrato funcional).

### 7) Contrato de creacion y validacion

- Decision: `POST` acepta solo `nombre`, `direccion`, `telefono` y rechaza payload que incluya `clave`.
- Rationale: La clave es de generacion interna y no debe ser controlada por cliente.
- Alternatives considered: Ignorar `clave` enviada por cliente (rechazada porque oculta errores de consumo); permitir override administrativo (rechazada por alcance actual).

### 8) Seguridad y documentacion

- Decision: Mantener Basic Auth para todo endpoint HTTP, incluyendo Swagger UI y `/v3/api-docs`.
- Rationale: Cumple constitucion y evita superficies expuestas sin autenticacion.
- Alternatives considered: Exponer docs en local sin auth (rechazada por aclaracion); migrar a JWT en esta iteracion (rechazada por alcance).

### 9) Integracion de persistencia y migraciones

- Decision: Mantener PostgreSQL + Flyway con secuencia `empleados_clave_numero_seq` para `clave_numero`.
- Rationale: Asegura evolucion reproducible y monotona de la llave numerica.
- Alternatives considered: `ddl-auto` de Hibernate (rechazada por falta de control de cambios); generacion en memoria de secuencia (rechazada por riesgo en concurrencia).

### 10) Cobertura de pruebas para la iteracion

- Decision: Agregar/ajustar pruebas API e integracion para versionado URL, paginacion, defaults, orden fijo, `404` por rutas no publicadas y `401` sin credenciales.
- Rationale: Los cambios de contrato requieren cobertura de regresion y seguridad.
- Alternatives considered: Validacion manual unicamente con Postman (rechazada por baja repetibilidad); pruebas solo unitarias (rechazada por necesidad de validar contrato HTTP completo).

## Best-Practice Notes

- Mantener DTOs de contrato separados de entidades JPA.
- Centralizar conversion entre `clave` logica y PK compuesta en una unica capa de dominio/servicio.
- Evitar exponer tipos internos de framework (`PageImpl`) en contrato publico.
- Mantener documentacion OpenAPI sincronizada con validaciones reales de controlador.
