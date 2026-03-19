# Feature Specification: Login Simbolico de Empleado

**Feature Branch**: `002-employee-user-login`  
**Created**: 2026-03-19  
**Status**: Draft  
**Input**: User description: "Agregar inicio de sesion simbolico para usuarios empleados con email y password al crear empleados, independiente del Basic Auth admin del CRUD actual."

## Clarifications

### Session 2026-03-18

- Q: Como debe persistirse la password del empleado? -> A: Se debe almacenar solo como hash unidireccional con salt (no reversible).
- Q: Que debe devolver el login simbolico cuando es exitoso? -> A: Debe devolver confirmacion de autenticacion con identidad basica del empleado, sin emitir token.
- Q: Que politica debe aplicar el sistema ante intentos fallidos consecutivos de login simbolico del mismo empleado? -> A: No debe bloquear la cuenta por intentos fallidos consecutivos; solo debe registrar eventos fallidos.
- Q: El endpoint de login simbolico de empleado debe ser publico o protegido por Basic Auth administrativo? -> A: Debe ser publico y operar de forma independiente del Basic Auth administrativo.
- Q: Cual debe ser la regla canonica de normalizacion para email antes de guardar y autenticar? -> A: Aplicar trim y lowercase siempre al email en alta y login.

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Registrar empleado con credenciales (Priority: P1)

Como usuario administrador del personal, quiero crear cada empleado con email y password para que el empleado tenga una identidad de acceso desde el alta.

**Why this priority**: Sin credenciales en el alta no existe base funcional para que el empleado pueda iniciar sesion despues.

**Independent Test**: Puede probarse creando un empleado con datos validos, email unico y password valida, y verificando que el empleado queda habilitado para autenticarse.

**Acceptance Scenarios**:

1. **Given** un alta de empleado con datos requeridos, email valido y password valida, **When** se confirma el registro, **Then** el sistema crea el empleado con una cuenta de acceso asociada.
2. **Given** un email ya registrado para otro empleado, **When** se intenta crear un nuevo empleado con ese mismo email, **Then** el sistema rechaza la solicitud e informa conflicto por email en uso.
3. **Given** un alta de empleado sin email o sin password, **When** se envia la solicitud, **Then** el sistema rechaza la operacion con errores de validacion claros.
4. **Given** un email con mayusculas y espacios al inicio o final, **When** se crea el empleado, **Then** el sistema normaliza el email con trim+lowercase antes de persistir y aplicar unicidad.

---

### User Story 2 - Iniciar sesion simbolico como empleado (Priority: P2)

Como empleado registrado, quiero iniciar sesion con mi email y password para validar mi identidad y obtener confirmacion de acceso.

**Why this priority**: Este flujo entrega el valor directo de la feature al usuario empleado y habilita futuras capacidades para su perfil.

**Independent Test**: Puede probarse consumiendo solo el flujo de login simbolico con credenciales validas e invalidas sin depender de cambios adicionales en CRUD.

**Acceptance Scenarios**:

1. **Given** un empleado con credenciales activas y validas, **When** ingresa email y password correctos, **Then** el sistema devuelve un resultado de login exitoso con la identidad del empleado.
2. **Given** un empleado existente, **When** ingresa password incorrecta, **Then** el sistema rechaza el acceso con mensaje generico de autenticacion fallida.
3. **Given** un email no registrado, **When** se intenta iniciar sesion, **Then** el sistema responde el mismo resultado de autenticacion fallida sin revelar si el email existe.
4. **Given** que no hay credenciales administrativas en la solicitud, **When** se invoca el endpoint de login simbolico, **Then** el sistema procesa el intento usando solo email/password de empleado.
5. **Given** un empleado registrado con email normalizado, **When** intenta login con variaciones de mayusculas o espacios laterales del mismo email, **Then** el sistema autentica usando trim+lowercase del email.
6. **Given** un empleado con credencial no habilitada, **When** intenta iniciar sesion, **Then** el sistema rechaza el acceso con la misma respuesta generica de autenticacion fallida.

---

### User Story 3 - Mantener independencia del acceso admin (Priority: P3)

Como responsable del sistema, quiero que el login simbolico de empleados no altere el mecanismo actual de autenticacion administrativa del CRUD.

**Why this priority**: Protege la operacion existente y evita regresiones en seguridad para los usuarios administradores.

**Independent Test**: Puede probarse validando que el acceso administrativo funciona igual que antes y que las credenciales de empleado no sirven como acceso administrativo.

**Acceptance Scenarios**:

1. **Given** un usuario administrador con credenciales validas, **When** accede al CRUD administrativo, **Then** conserva el mismo comportamiento de autenticacion previo a esta feature.
2. **Given** un empleado con login simbolico exitoso, **When** intenta usar sus credenciales en el canal administrativo, **Then** el sistema rechaza el acceso.
3. **Given** actividad de login simbolico de empleados, **When** se evalua el acceso administrativo, **Then** no hay cambios en reglas, permisos ni credenciales del administrador.

### Edge Cases

- El sistema debe normalizar email con trim+lowercase en alta y login para evitar cuentas duplicadas y fallos de autenticacion por formato.
- Si un empleado fue eliminado, cualquier intento posterior de login simbolico debe ser rechazado con respuesta generica de autenticacion fallida.
- Si un empleado tiene credencial no habilitada, cualquier intento de login simbolico debe ser rechazado con la misma respuesta generica.
- Si se realizan multiples intentos fallidos consecutivos con un mismo email, el sistema debe responder de forma consistente sin filtrar datos sensibles, sin bloqueo automatico de cuenta.
- Si existen empleados historicos sin credenciales de acceso, no deben autenticarse por login simbolico hasta ser habilitados por el proceso definido.
- Si la creacion de empleado falla por validacion de credenciales, no debe persistirse un alta parcial del empleado.
- Si se accede a endpoints administrativos sin Basic Auth valido, deben seguir rechazando el acceso aunque el endpoint de login simbolico sea publico.

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: El sistema MUST requerir email y password en el alta de empleados para habilitar su acceso como usuario empleado.
- **FR-002**: El sistema MUST validar formato de email, aplicar trim+lowercase en alta y login, y usar ese valor normalizado como base unica de unicidad y de comparacion en autenticacion.
- **FR-003**: El sistema MUST aplicar una politica minima de password que incluya longitud entre 8 y 64 caracteres y al menos una letra y un numero.
- **FR-004**: El sistema MUST crear empleado y credenciales como una unica operacion de negocio, evitando resultados parciales.
- **FR-005**: El sistema MUST ofrecer un flujo de login simbolico para empleados usando email y password.
- **FR-006**: El sistema MUST devolver una respuesta de login simbolico exitoso que confirme identidad basica del empleado y estado de autenticacion, y MUST NOT emitir JWT, token opaco ni identificador de sesion.
- **FR-007**: El sistema MUST rechazar logins invalidos con una respuesta generica que no exponga si el email existe ni cual campo fallo.
- **FR-008**: El sistema MUST mantener sin cambios el mecanismo de autenticacion administrativa del CRUD existente.
- **FR-009**: El sistema MUST impedir que las credenciales de empleado se usen para autenticacion administrativa.
- **FR-010**: El sistema MUST devolver errores de validacion con estructura consistente que identifique al menos el campo invalido y la regla incumplida cuando el alta no cumpla reglas de email o password.
- **FR-011**: El sistema MUST impedir login simbolico para empleados eliminados y para empleados con credencial no habilitada, aplicando la misma semantica de respuesta generica definida para credenciales invalidas.
- **FR-012**: El sistema MUST registrar eventos de intento de login simbolico (exitosos y fallidos) para trazabilidad operativa.
- **FR-013**: El sistema MUST documentar la separacion de canales entre login simbolico y acceso administrativo con criterios verificables en artefactos sincronizados: contrato OpenAPI (requisitos de seguridad por endpoint), quickstart (pasos de validacion de ambos canales) y documento de alcance de la feature (sin emision de token y sin sustitucion del canal administrativo).
- **FR-014**: El sistema MUST almacenar la password unicamente como hash unidireccional con salt y MUST NOT persistirla en texto plano ni en formato reversible.
- **FR-015**: El sistema MUST NOT bloquear cuentas de empleado por intentos fallidos consecutivos de login simbolico y MUST registrar cada intento fallido para trazabilidad.
- **FR-016**: El endpoint de login simbolico de empleados MUST ser publico (sin requerir Basic Auth administrativo previo) y MUST autenticar exclusivamente con email/password de empleado.

### Constitutional Constraints *(mandatory for every feature)*

- **CC-001**: La feature MUST respetar la constitucion vigente del proyecto y sus reglas de calidad.
- **CC-002**: La separacion entre acceso de empleado y acceso administrativo MUST quedar explicita en reglas y pruebas de aceptacion.
- **CC-003**: Cualquier cambio en reglas de autenticacion MUST incluir cobertura de no regresion para flujos ya operativos.
- **CC-004**: La aceptacion de la feature MUST incluir evidencia verificable del flujo completo alta+login de empleado.
- **CC-005**: El contrato funcional de entrada y salida MUST mantenerse sincronizado con el comportamiento real del sistema.
- **CC-006**: La excepcion de seguridad del endpoint publico de login simbolico MUST documentarse y justificarse explicitamente como parte de la separacion de canales.

### Key Entities *(include if feature involves data)*

- **Empleado**: Persona registrada en el sistema con datos de negocio y estado operativo (activo/inactivo).
- **CredencialEmpleado**: Identidad de acceso del empleado con email canonico normalizado (trim+lowercase) unico, `password_hash` no reversible con salt y estado de habilitacion para login simbolico.
- **ResultadoLoginSimbolico**: Resultado funcional de autenticacion que indica exito/fallo y, en caso exitoso, estado de autenticacion e identidad basica del empleado autenticado, sin token.
- **EventoLoginEmpleado**: Registro de trazabilidad de cada intento de login simbolico, incluyendo resultado y marca temporal.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: El 100% de altas de empleado sin email o password valida se rechaza sin crear registros parciales.
- **SC-002**: El 100% de empleados creados con credenciales validas puede completar login simbolico exitoso en el primer intento dentro de 60 segundos posteriores al alta.
- **SC-003**: Al menos el 95% de intentos de login simbolico con credenciales validas finaliza en menos de 2 segundos bajo carga normal de 50 solicitudes concurrentes.
- **SC-004**: El 100% de intentos de login simbolico con credenciales invalidas devuelve respuesta generica sin fuga de informacion sensible.
- **SC-005**: El 100% de la suite de humo administrativa definida para esta feature (GET/POST/PUT/DELETE sobre `/api/v1/empleados` con Basic Auth valido y verificacion de `401` sin credenciales) mantiene su resultado esperado en al menos 3 ejecuciones consecutivas sin regresion.
- **SC-006**: El 100% de empleados eliminados o con credencial no habilitada falla autenticacion simbolica en intentos posteriores.
- **SC-007**: El 100% de credenciales persistidas para empleados almacenan solo hash con salt y 0% de passwords en texto plano o en formato reversible.
- **SC-008**: El 100% de respuestas exitosas de login simbolico retorna confirmacion e identidad basica y 0 campos de token/sesion emitidos.
- **SC-009**: El 100% de secuencias de 5 intentos fallidos consecutivos para un mismo email mantiene el comportamiento sin bloqueo de cuenta y con trazabilidad de cada intento.
- **SC-010**: El 100% de pruebas de acceso confirma que el endpoint de login simbolico es consumible sin Basic Auth administrativo y que los endpoints administrativos siguen rechazando acceso no autenticado.
- **SC-011**: El 100% de pruebas con variaciones de mayusculas y espacios laterales en un mismo email produce el mismo resultado de unicidad y autenticacion tras aplicar trim+lowercase.
- **SC-012**: El 100% de los artefactos obligatorios de documentacion de la feature (OpenAPI, quickstart y plan/research) describe de forma consistente la separacion de canales y las reglas de seguridad por endpoint.

## Assumptions

- El login simbolico autentica identidad del empleado, pero no habilita nuevas operaciones de negocio para empleados dentro de esta misma feature.
- La gestion de recuperacion o cambio de password queda fuera de alcance en esta iteracion.
- Cada empleado tiene un unico email de acceso asociado.
- Los empleados historicos sin credenciales se habilitaran por un proceso posterior fuera del alcance de esta especificacion.
- En esta iteracion no se expone un endpoint publico para cambiar el estado de habilitacion de credenciales; aun asi, el login simbolico MUST respetar una credencial no habilitada cuando exista en persistencia.

## Dependencies

- Flujo vigente de alta de empleados disponible para extenderse con datos de acceso.
- Capacidad de ejecutar pruebas de no regresion sobre el acceso administrativo actual.
- Definicion operativa de mensajes de validacion y autenticacion alineada con estandares del negocio.
