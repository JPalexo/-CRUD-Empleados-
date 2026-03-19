# Feature Specification: CRUD de Empleados

**Feature Branch**: `001-crud-empleados`  
**Created**: 2026-03-17  
**Status**: Draft  
**Input**: User description: "crea un crud de empleados, con los campos, clave, nombre, direccion y telefono, clave seria la clave primaria, nombre direccion y telefono de 100 espacios. + ampliacion: clave con prefijo EMP- y numerico autogenerado como PK compuesta, y despliegue en dos dockers (backend y postgres)."

## Clarifications

### Session 2026-03-17

- Q: Como debe modelarse fisicamente la PK del empleado con formato `EMP-{numero}`? -> A: La PK se persistira como compuesta (`clave_prefijo` + `clave_numero`) y la API expondra la clave concatenada `EMP-{numero}`.
- Q: Swagger UI y `/v3/api-docs` deben requerir autenticacion? -> A: Si, deben requerir Basic Auth en todos los ambientes.
- Q: Que hacer si el cliente envia `clave` manual al crear empleado? -> A: Rechazar la solicitud como invalida (error de validacion), porque la clave es exclusivamente autogenerada.
- Q: Que formato numerico debe usar la clave `EMP-{numero}`? -> A: El numero se representa sin relleno de ceros a la izquierda (por ejemplo, `EMP-1`, `EMP-2`, `EMP-10`).

### Session 2026-03-18

- Q: Como debe presentarse el listado de empleados en GET? -> A: Debe devolverse en formato paginado, incluyendo metadata de paginacion.
- Q: Como debe exponerse la version de API? -> A: Debe versionarse en la URL; para esta feature la ruta base es `v1`.
- Q: Que codigo HTTP debe devolver una ruta CRUD sin version o con version no publicada? -> A: Debe devolver `404` en ambos casos.
- Q: Que estructura debe usar la respuesta paginada del listado? -> A: Debe usar el sobre `{data, pagination}` con metadata dentro de `pagination`.
- Q: Que orden debe usar el listado paginado? -> A: Debe usar orden fijo por `clave_numero` ascendente.
- Q: Que comportamiento debe tener el listado cuando faltan parametros `page` o `size`? -> A: Debe aplicar defaults (`page=0`, `size=10`) al parametro faltante.
- Q: Cual debe ser la base del indice de pagina en la API? -> A: La paginacion debe ser cero-basada (`page=0` es la primera pagina).

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Registrar empleado (Priority: P1)

Como usuario autorizado, quiero registrar empleados para contar con su informacion basica desde el inicio.

**Why this priority**: Sin alta de empleados no existe informacion que pueda consultarse, actualizarse o eliminarse.

**Independent Test**: Se prueba de forma independiente registrando un empleado valido y verificando que queda disponible para consulta.

**Acceptance Scenarios**:

1. **Given** que no existe un empleado con la misma clave compuesta, **When** se registra un empleado con `nombre`, `direccion` y `telefono` validos, **Then** el sistema guarda el empleado y genera una `clave` con formato `EMP-{numero}`.
2. **Given** que ya existen empleados registrados, **When** se registra un nuevo empleado valido, **Then** el numero de la `clave` generada es consecutivo y no se repite.
3. **Given** que `nombre`, `direccion` o `telefono` superan 100 caracteres, **When** se intenta registrar el empleado, **Then** el sistema rechaza la operacion con un mensaje de validacion.

---

### User Story 2 - Consultar empleados (Priority: P2)

Como usuario autorizado, quiero consultar el listado de empleados en formato paginado y su detalle para revisar su informacion registrada de forma ordenada.

**Why this priority**: Permite usar la informacion cargada para actividades operativas y de validacion.

**Independent Test**: Se prueba cargando empleados y consultando listado por pagina (`page`, `size`) para validar metadata y contenido; adicionalmente se valida busqueda por clave.

**Acceptance Scenarios**:

1. **Given** que existen empleados registrados, **When** se solicita el listado de empleados con parametros de pagina validos, **Then** el sistema devuelve una pagina de resultados en formato `{data, pagination}` con metadata (`page`, `size`, `totalElements`, `totalPages`) dentro de `pagination`.
2. **Given** una pagina solicitada que excede el total disponible, **When** se consulta el listado paginado, **Then** el sistema devuelve `{data: [], pagination: ...}` con metadata consistente y sin error de servidor.
3. **Given** una `clave` existente, **When** se consulta el empleado por `clave` en la ruta versionada, **Then** el sistema devuelve el registro correspondiente.
4. **Given** parametros de paginacion invalidos (por ejemplo `page < 0` o `size <= 0`), **When** se consulta el listado, **Then** el sistema rechaza la solicitud con mensaje de validacion.
5. **Given** multiples empleados en el sistema, **When** se consulta cualquier pagina del listado, **Then** los registros devueltos respetan orden fijo por `clave_numero` ascendente para mantener estabilidad entre paginas.
6. **Given** que falta `page` o `size` en la solicitud, **When** se consulta el listado paginado, **Then** el sistema aplica defaults (`page=0`, `size=10`) al parametro faltante y responde en formato `{data, pagination}`.
7. **Given** que existen suficientes empleados para mas de una pagina, **When** se consulta el listado con `page=0`, **Then** el sistema devuelve la primera pagina y refleja `page=0` en `pagination`.

---

### User Story 3 - Actualizar y eliminar empleados (Priority: P3)

Como usuario autorizado, quiero actualizar y eliminar empleados para mantener la informacion correcta y vigente.

**Why this priority**: Completa el ciclo de mantenimiento de datos una vez que el alta y consulta ya aportan valor.

**Independent Test**: Se prueba modificando un empleado existente y eliminando otro, verificando resultado en consultas posteriores.

**Acceptance Scenarios**:

1. **Given** una `clave` existente, **When** se actualiza `nombre`, `direccion` o `telefono` con valores validos, **Then** el sistema guarda los cambios.
2. **Given** una `clave` existente, **When** se elimina el empleado, **Then** el sistema confirma la eliminacion y el empleado deja de aparecer en consultas.
3. **Given** una `clave` inexistente, **When** se intenta actualizar o eliminar, **Then** el sistema rechaza la operacion e informa que el recurso no existe.

### Edge Cases

- Si se intenta enviar `clave` manual en el alta, el sistema debe rechazar la solicitud con error de validacion.
- Si el prefijo de una `clave` no es `EMP-`, debe rechazarse como identificador invalido.
- Si el numero de una `clave` contiene ceros a la izquierda (por ejemplo `EMP-0007`), debe rechazarse como formato invalido.
- Si el sistema se reinicia, la siguiente `clave` autogenerada debe continuar la secuencia sin duplicados.
- `nombre`, `direccion` y `telefono` de exactamente 100 caracteres deben aceptarse.
- `nombre`, `direccion` y `telefono` de 101 o mas caracteres deben rechazarse.
- Si `size` supera el maximo permitido, el sistema debe rechazar la solicitud con un error de validacion claro.
- Si falta `page` o `size`, el sistema debe completar el faltante con el default correspondiente (`page=0`, `size=10`).
- Si se solicita `page=0`, el sistema debe tratarlo como la primera pagina del listado.
- Si se crean nuevos empleados entre consultas de distintas paginas, el orden base del listado debe mantenerse por `clave_numero` ascendente.
- Intentos concurrentes de alta no deben generar la misma `clave`.
- Eliminar un empleado ya eliminado debe responder de forma controlada sin corromper datos.
- Si se solicita una ruta CRUD sin segmento de version, debe responder `404`.
- Si se solicita una version de API no publicada, debe responder `404`.
- Si uno de los dos contenedores (backend o postgres) no inicia, el entorno debe reportar el fallo de forma clara.
- Cualquier intento de acceder a Swagger UI o `/v3/api-docs` sin credenciales validas debe devolver `401` en todos los ambientes.

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: El sistema MUST permitir crear empleados con los campos `nombre`, `direccion` y `telefono`.
- **FR-002**: El sistema MUST permitir listar empleados en formato paginado mediante parametros `page` y `size`, retornando el sobre `{data, pagination}` donde `pagination` incluye (`page`, `size`, `totalElements`, `totalPages`) y `data` se ordena por `clave_numero` ascendente.
- **FR-003**: El sistema MUST permitir consultar un empleado por `clave` usando rutas versionadas.
- **FR-004**: El sistema MUST permitir actualizar `nombre`, `direccion` y `telefono` de un empleado existente.
- **FR-005**: El sistema MUST permitir eliminar un empleado por `clave`.
- **FR-006**: El sistema MUST generar la `clave` de empleado con prefijo fijo `EMP-` y un numero autoincremental representado en decimal sin ceros a la izquierda.
- **FR-007**: El sistema MUST validar que `nombre`, `direccion` y `telefono` tengan longitud maxima de 100 caracteres.
- **FR-008**: El sistema MUST rechazar operaciones con datos invalidos e informar el motivo de forma clara.
- **FR-009**: El sistema MUST restringir el acceso de operaciones CRUD a usuarios autenticados.
- **FR-010**: El sistema MUST reflejar en su documentacion funcional cualquier cambio de entrada, salida o reglas de validacion del CRUD.
- **FR-011**: El sistema MUST persistir la llave primaria del empleado como compuesta por `clave_prefijo` (valor fijo `EMP-`) y `clave_numero` (valor numerico autoincremental).
- **FR-012**: El sistema MUST exponer y aceptar la `clave` de consulta/actualizacion/eliminacion como valor logico concatenado en formato `EMP-{numero}`, donde `{numero}` es decimal positivo sin ceros a la izquierda.
- **FR-013**: La implementacion MUST levantarse en dos contenedores Docker: uno para backend y uno para PostgreSQL.
- **FR-014**: Swagger UI y `/v3/api-docs` MUST requerir Basic Authentication en local, test y produccion.
- **FR-015**: El sistema MUST rechazar la creacion de empleado cuando el cliente incluya `clave` en el payload, ya que ese campo es de generacion interna.
- **FR-016**: El sistema MUST versionar los endpoints de negocio en la URL, usando el patron `/api/v{version}/...`, y MUST NOT exponer rutas CRUD equivalentes sin version.
- **FR-017**: La version inicial de los endpoints CRUD de empleados MUST publicarse bajo `/api/v1/empleados`.
- **FR-018**: El sistema MUST rechazar solicitudes a rutas CRUD sin version y a versiones no publicadas con respuesta `404` y mensaje de error claro y consistente.
- **FR-019**: El sistema MUST usar indice de pagina cero-basado (`page=0` representa la primera pagina) y validar `page >= 0`, `size > 0` y `size <= 100` para el listado paginado.
- **FR-020**: Cuando falte `page` o `size`, el sistema MUST aplicar defaults (`page=0`, `size=10`) al parametro faltante.

### Constitutional Constraints *(mandatory for every feature)*

- **CC-001**: La implementacion MUST cumplir el baseline tecnologico definido por la constitucion vigente del proyecto.
- **CC-002**: Cualquier cambio de estructura de datos MUST gestionarse mediante artefactos de migracion versionados.
- **CC-003**: Las excepciones de seguridad para endpoints publicos MUST documentarse y justificarse.
- **CC-004**: La aceptacion de la feature MUST incluir verificacion en entorno local con servicios de infraestructura en contenedores.
- **CC-005**: La documentacion de contrato de API MUST mantenerse sincronizada con las reglas del CRUD.
- **CC-006**: La configuracion de la aplicacion MUST usar perfiles por ambiente y secretos externalizados (por ejemplo, variables de entorno), sin credenciales hardcodeadas en codigo o archivos versionados.
- **CC-007**: Antes de merge, la aceptacion de la feature MUST incluir evidencia de build exitoso ejecutado con Java 17.

### Key Entities *(include if feature involves data)*

- **Empleado**: Representa a un empleado del sistema con PK compuesta persistida en `clave_prefijo` (`EMP-`) y `clave_numero` (autoincremental), y con `clave` logica expuesta como `EMP-{numero}` en representacion decimal sin ceros a la izquierda; ademas incluye `nombre` (maximo 100), `direccion` (maximo 100) y `telefono` (maximo 100).
- **SecuenciaClaveEmpleado**: Representa la secuencia numerica monotona que genera `clave_numero` para nuevas altas.
- **PaginaEmpleados**: Representa una respuesta paginada en formato `{data, pagination}`, donde `data` contiene el subconjunto de empleados y `pagination` contiene (`page`, `size`, `totalElements`, `totalPages`).
- **VersionApi**: Representa la version mayor publicada del contrato de endpoints de negocio (por ejemplo `v1`) y su vigencia.
- **Resultado de Validacion**: Representa errores funcionales de entrada, por ejemplo longitud excedida o clave duplicada, con mensaje entendible para el usuario.
- **Artefacto de Migracion**: Define y versiona la estructura persistente del empleado y sus restricciones de unicidad y longitud.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: El 100% de los registros creados persiste una PK compuesta unica (`clave_prefijo`, `clave_numero`) y expone una `clave` unica con patron `^EMP-[1-9][0-9]*$` sin colisiones de secuencia.
- **SC-002**: Al menos el 95% de las operaciones CRUD validas finaliza en menos de 2 segundos bajo una carga de operacion normal definida como 50 usuarios concurrentes durante 10 minutos con mezcla de trafico: 60% consultas, 20% altas, 10% actualizaciones y 10% eliminaciones.
- **SC-003**: El 100% de solicitudes con datos invalidos recibe un resultado de error claro que incluye explicitamente el campo invalido y la regla incumplida (por ejemplo longitud maxima o formato de clave).
- **SC-004**: Un usuario autorizado puede completar el flujo alta-consulta-actualizacion-eliminacion de un empleado en menos de 3 minutos sin asistencia tecnica.
- **SC-005**: El entorno local levanta de forma reproducible en dos contenedores (backend y postgres) en al menos el 95% de ejecuciones, medido en una corrida de 20 ciclos consecutivos de arranque/apagado (`docker compose up -d` y `docker compose down`) con criterio de exito: backend operativo, conexion a PostgreSQL disponible y migraciones Flyway aplicadas.
- **SC-006**: El 100% de intentos de acceso sin autenticacion a Swagger UI y `/v3/api-docs` recibe `401` en cualquier ambiente.
- **SC-007**: El 100% de solicitudes de alta que incluyan `clave` manual son rechazadas con error de validacion y sin crear registros.
- **SC-008**: El 100% de respuestas de listado usa el formato `{data, pagination}` con metadata coherente (`page`, `size`, `totalElements`, `totalPages`), subconjunto consistente de empleados y orden por `clave_numero` ascendente.
- **SC-009**: El 100% de operaciones CRUD de empleados exitosas se ejecuta bajo rutas versionadas `/api/v1/empleados`.
- **SC-010**: El 100% de solicitudes a rutas CRUD sin version o con version no publicada responde `404` con error claro y sin exponer comportamiento ambiguo.
- **SC-011**: El 100% de solicitudes de listado que omitan `page` o `size` reciben respuesta exitosa con defaults aplicados (`page=0`, `size=10`) reflejados en `pagination`.

## Assumptions

- El CRUD sera utilizado por personal interno autenticado.
- No se solicita borrado logico; la eliminacion se considera definitiva.
- La version mayor inicial publicada para este CRUD es `v1`.
- El listado paginado usa indice cero-basado (`page=0` es la primera pagina) y por defecto `page=0` y `size=10` cuando esos parametros no se informan.
- `telefono` se gestiona como texto libre con maximo 100 caracteres, sin formato internacional obligatorio en esta version.
- El prefijo de la clave se mantiene fijo como `EMP-` en esta version.

## Dependencies

- Mecanismo de autenticacion disponible para proteger operaciones del CRUD.
- Persistencia relacional disponible para almacenar empleados con restricciones de integridad.
- Estrategia de versionado de API por URL disponible y gobernada para futuras versiones (`v2`, `v3`, ...).
- Flujo de migraciones y publicacion de documentacion funcional disponible en el proceso del proyecto.
- Definicion de orquestacion local con dos contenedores Docker (backend y postgres).
- Mecanismo de provision de variables de entorno/secretos para credenciales de base de datos segun perfil de ejecucion.
