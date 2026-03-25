# Feature Specification: Frontend CRUD de Empleados y Departamentos

**Feature Branch**: `004-frontend-crud-ui`  
**Created**: 2026-03-25  
**Status**: Draft  
**Input**: User description: "Frontend CRUD de Empleados y Departamentos con login, CRUD completo, asignacion por departamentoClave, ocupacion de departamentos, validaciones alineadas backend y soporte desktop/mobile"

## Clarifications

### Session 2026-03-25

- Q: Como debe persistir la sesion del admin en frontend? -> A: Persistir sesion con expiracion por inactividad (30 min) y requerir re-login al expirar.
- Q: Que politica debe aplicar al eliminar un departamento con empleados asignados? -> A: Bloquear eliminacion y mostrar conflicto.
- Q: Como resolver conflictos de concurrencia al editar el mismo registro? -> A: Detectar conflicto y bloquear guardado hasta recargar datos.

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Gestionar Empleados (Priority: P1)

Como admin autenticado, quiero listar, crear, editar y eliminar empleados desde
la interfaz para operar el ciclo de vida completo sin depender de Postman.

**Why this priority**: La gestion de empleados es el flujo principal del negocio
y el mayor impacto operativo en uso diario.

**Independent Test**: Puede validarse ejecutando solo la vista de empleados,
completando alta, edicion y baja, y confirmando resultados consistentes en la UI.

**Acceptance Scenarios**:

1. **Given** un admin autenticado, **When** abre la pantalla de empleados,
   **Then** visualiza la lista paginada con datos actuales.
2. **Given** un formulario de alta valido, **When** confirma la creacion,
   **Then** el empleado se registra y aparece en la lista actualizada.
3. **Given** un empleado existente, **When** actualiza sus datos y guarda,
   **Then** la interfaz muestra los cambios persistidos.
4. **Given** un empleado existente, **When** confirma eliminacion,
   **Then** el registro desaparece de la lista y la operacion queda confirmada.

---

### User Story 2 - Gestionar Departamentos (Priority: P2)

Como admin autenticado, quiero listar, crear, editar y eliminar departamentos
para mantener la estructura organizativa vigente.

**Why this priority**: Es la capacidad base que sostiene la asignacion de
empleados y la visibilidad de capacidad por area.

**Independent Test**: Puede validarse operando solo la vista de departamentos,
incluyendo alta, edicion y baja con mensajes de exito/error esperados.

**Acceptance Scenarios**:

1. **Given** un admin autenticado, **When** abre la pantalla de departamentos,
   **Then** ve lista, capacidad y ocupacion por cada registro.
2. **Given** un formulario de departamento valido, **When** crea o edita,
   **Then** los cambios quedan reflejados en la vista.
3. **Given** un departamento eliminable, **When** confirma eliminacion,
   **Then** se remueve y la UI refleja el nuevo estado.
4. **Given** un departamento con empleados asignados, **When** intenta eliminar,
   **Then** la UI bloquea la accion y muestra mensaje de conflicto.

---

### User Story 3 - Asignar Empleado a Departamento (Priority: P3)

Como admin autenticado, quiero asignar o reasignar empleados por
departamentoClave y visualizar ocupacion/capacidad para evitar sobrecargas.

**Why this priority**: Completa la operacion transversal entre ambos modulos y
reduce errores de asignacion manual.

**Independent Test**: Puede validarse con un flujo de reasignacion desde detalle
de empleado, verificando cambio de departamento y metricas actualizadas.

**Acceptance Scenarios**:

1. **Given** un empleado activo y departamentos validos,
   **When** el admin selecciona un departamentoClave y guarda,
   **Then** el empleado queda asociado al nuevo departamento.
2. **Given** una reasignacion que viola reglas de capacidad,
   **When** el admin intenta confirmar,
   **Then** la UI muestra error claro y no aplica cambios.

---

### Edge Cases

<!--
  ACTION REQUIRED: The content in this section represents placeholders.
  Fill them out with the right edge cases.
-->

- Si faltan credenciales o son invalidas, la UI debe bloquear acceso a vistas
  protegidas y redirigir al flujo de autenticacion.
- Si la sesion expira por inactividad, la UI debe redirigir a autenticacion,
  conservar la ruta previa y los filtros de lista, y requerir re-login.
- Si el backend no responde o hay timeout, la UI debe mostrar estado de error
  recuperable y permitir reintento.
- Si un formulario envia datos invalidos, la UI debe presentar mensajes por campo
  consistentes con la regla violada.
- Si se intenta guardar con departamentoClave inexistente, la UI debe informar la
  inconsistencia y mantener el formulario editable.
- Si se intenta eliminar un departamento con empleados asignados, la UI debe
  bloquear la eliminacion e informar que primero debe reasignar empleados.
- Si la navegacion ocurre en mobile o teclado unicamente, los controles deben
  mantenerse utilizables sin perdida de acciones principales.
- Si dos admins editan el mismo registro en paralelo, la UI debe detectar
  conflicto al guardar, bloquear la sobreescritura y solicitar recarga.

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: El sistema MUST permitir al admin autenticado listar empleados con
  paginacion y visualizacion de datos clave (`clave`, `nombre`, `apellido`,
  `email`, `departamentoClave`).
- **FR-002**: El sistema MUST permitir crear empleados desde UI con confirmacion
  visual de resultado exitoso o fallido.
- **FR-003**: El sistema MUST permitir editar empleados desde UI y reflejar
  cambios persistidos en la lista sin ambiguedad.
- **FR-004**: El sistema MUST permitir eliminar empleados desde UI con
  confirmacion explicita previa.
- **FR-005**: El sistema MUST permitir listar departamentos mostrando ocupacion
  actual, capacidad maxima y estado de disponibilidad.
- **FR-006**: El sistema MUST permitir crear, editar y eliminar departamentos
  desde la interfaz con mensajes de resultado claros.
- **FR-007**: El sistema MUST permitir asignar y reasignar empleados por
  departamentoClave y actualizar visualmente las metricas relacionadas.
- **FR-008**: El sistema MUST mostrar mensajes de validacion en formularios
  coherentes con las reglas del backend.
- **FR-009**: El sistema MUST manejar errores HTTP y estados de carga de manera
  centralizada y consistente en todas las pantallas.
- **FR-010**: El sistema MUST funcionar correctamente en desktop y mobile para
  todas las acciones CRUD principales.
- **FR-011**: El sistema MUST permitir completar el flujo de administracion sin
  depender de Postman para las operaciones funcionales.
- **FR-012**: El sistema MUST mantener sesion autenticada del admin con
  expiracion por inactividad (30 minutos) y requerir reautenticacion al expirar.
- **FR-013**: El sistema MUST bloquear la eliminacion de departamentos con
  empleados asignados y mostrar mensaje de conflicto accionable.
- **FR-014**: El sistema MUST detectar conflictos de concurrencia en edicion de
  empleados y departamentos, bloquear el guardado en conflicto y requerir
  recarga de datos antes de reintentar.

### Constitutional Constraints *(mandatory for every feature)*

- **CC-001**: Implementation MUST remain compatible with Java 17 and Spring Boot 3.x.
- **CC-002**: Any new DB object (table/index/constraint) MUST be created through Flyway migration files.
- **CC-003**: Auth exceptions (public endpoints) MUST be explicitly documented and justified.
- **CC-004**: Feature acceptance MUST include verification steps for Dockerized PostgreSQL startup.
- **CC-005**: Swagger/OpenAPI output MUST include auth requirements and accurate request/response schemas.
- **CC-006**: Frontend deliverables MUST stay on Angular 22 LTS and strict TypeScript configuration.
- **CC-007**: Acceptance MUST include successful frontend build and lint checks with no blocking errors.
- **CC-008**: Acceptance MUST include local Docker-based frontend-backend integration verification.

### Key Entities *(include if feature involves data)*

- **Empleado**: Persona gestionada por admin con atributos de identificacion,
  contacto y referencia a departamento por departamentoClave.
- **Departamento**: Unidad organizativa con clave unica, capacidad maxima,
  ocupacion actual y estado derivado de capacidad.
- **Asignacion Empleado-Departamento**: Relacion funcional que determina en que
  departamento opera cada empleado y afecta metricas de ocupacion.

## Assumptions

- El rol admin es el unico actor funcional para esta feature en su primera fase.
- Los endpoints CRUD y de autenticacion existentes en /api/v1 se mantienen
  disponibles y estables durante la implementacion.
- No se requiere ampliar el modelo de datos backend para lograr el alcance
  definido en esta especificacion.
- Las reglas de validacion de backend se consideran fuente de verdad para
  mensajes y comportamiento de formularios.

## Dependencies

- Disponibilidad del backend y base de datos en entorno local Docker para
  pruebas de integracion.
- Contrato API vigente y documentado para operaciones de empleados,
  departamentos y autenticacion.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: El 100% de operaciones CRUD de empleados definidas en alcance
  pueden completarse desde UI por un admin autenticado.
- **SC-002**: El 100% de operaciones CRUD de departamentos definidas en alcance
  pueden completarse desde UI por un admin autenticado.
- **SC-003**: Al menos el 95% de intentos con datos invalidos muestran mensajes
  de validacion correctos en menos de 2 segundos.
- **SC-004**: El 100% de flujos de asignacion/reasignacion por departamentoClave
  actualiza correctamente la relacion y la ocupacion mostrada.
- **SC-005**: En pruebas de aceptacion, al menos 9 de cada 10 tareas clave
  (alta, edicion, baja, reasignacion, consulta) se completan sin asistencia.
- **SC-006**: En resoluciones 1366x768 (desktop) y 390x844 (mobile), el 100% de
  acciones CRUD principales (listar, crear, editar, eliminar, reasignar) es
  ejecutable sin bloqueo funcional.
- **SC-007**: El 100% de expiraciones por inactividad fuerzan re-login y no
  permiten acciones protegidas sin nueva autenticacion.
- **SC-008**: El 100% de intentos de eliminar departamentos con empleados
  asignados son bloqueados con mensaje de conflicto visible.
- **SC-009**: El 100% de conflictos de concurrencia detectados evitan
  sobrescritura silenciosa y muestran accion de recarga al usuario.
