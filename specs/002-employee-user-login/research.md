# Research: Login Simbolico de Empleado

**Date**: 2026-03-18  
**Feature**: 002-employee-user-login

## Decision 1: Persistir credenciales en tabla separada 1:1

- Decision: crear `empleado_credenciales` separada de `empleados`, con FK compuesta (`clave_prefijo`, `clave_numero`) y unicidad sobre `email_normalizado`.
- Rationale: mantiene desacoplados datos de negocio y datos sensibles, facilita evolucion de seguridad y reduce impacto sobre el CRUD existente.
- Alternatives considered:
  - Agregar `email` y `password_hash` en `empleados`: mas simple al inicio, pero mezcla dominios y complica futuras rotaciones/politicas de seguridad.
  - Externalizar autenticacion en un IdP: fuera de alcance para login simbolico de esta iteracion.

## Decision 2: Hash de password con PasswordEncoder delegating (BCrypt)

- Decision: usar `PasswordEncoderFactories.createDelegatingPasswordEncoder()` y persistir solo hash+salt no reversible.
- Rationale: ya existe en el proyecto, cumple requerimiento de hash unidireccional y evita introducir librerias nuevas.
- Alternatives considered:
  - Argon2 dedicado: robusto, pero agrega dependencia/configuracion no necesaria para esta iteracion.
  - SHA-256 con salt manual: mas propenso a errores y no recomendado frente a algoritmos adaptativos.

## Decision 3: Endpoint de login simbolico publico y aislado

- Decision: exponer `POST /api/v1/empleados/login` publico y mantener Basic Auth para el resto de endpoints.
- Rationale: respeta la separacion funcional pedida (empleado vs admin) y preserva la seguridad actual del CRUD.
- Alternatives considered:
  - Requerir Basic Auth admin para login: contradice independencia de canal.
  - Endpoint interno no publico: no permite autenticacion directa del empleado.

## Decision 4: Normalizacion canonica de email

- Decision: aplicar `trim + lowercase (Locale.ROOT)` en alta y login, y comparar siempre con el valor normalizado.
- Rationale: elimina duplicados por formato y evita fallos de autenticacion por mayusculas o espacios laterales.
- Alternatives considered:
  - Solo trim: deja ambiguedad por case.
  - Comparacion exacta sin normalizar: genera UX inconsistente y posibles duplicados logicos.

## Decision 5: Alta transaccional de empleado + credenciales

- Decision: ejecutar la creacion de empleado y credencial en una sola transaccion de servicio.
- Rationale: garantiza FR-004 (sin resultados parciales) y simplifica consistencia de datos.
- Alternatives considered:
  - Dos operaciones separadas en cascada: riesgo de empleado sin credencial ante fallos intermedios.
  - Orquestacion asincrona: complejidad innecesaria para alcance actual.

## Decision 6: Respuesta de login exitoso sin token

- Decision: devolver confirmacion de autenticacion e identidad basica del empleado, sin JWT ni sesion.
- Rationale: cumple clarificacion de producto y evita modificar el modelo de sesion/autorizacion del sistema.
- Alternatives considered:
  - JWT: expande alcance a autorizacion y renovacion de token.
  - Token opaco: requiere infraestructura adicional de sesion.

## Decision 7: Fallos de login con mensaje generico y sin bloqueo

- Decision: ante credenciales invalidas, devolver error generico uniforme; no bloquear cuenta por intentos consecutivos y registrar cada intento.
- Rationale: cumple clarificaciones de seguridad/operacion y evita fuga de informacion sobre existencia de email.
- Alternatives considered:
  - Bloqueo temporal por intentos: no alineado con decision de negocio.
  - Mensajes especificos por causa: aumenta riesgo de enumeracion de cuentas.

## Decision 8: Trazabilidad en base de datos para intentos de login

- Decision: crear `empleado_login_eventos` para registrar intentos exitosos/fallidos con marca temporal y email normalizado de entrada.
- Rationale: ofrece auditoria durable y verificable para FR-012 y pruebas de cumplimiento.
- Alternatives considered:
  - Solo logs de aplicacion: menos estructurado para consulta de auditoria.
  - Sin persistencia de eventos: incumple requerimiento de trazabilidad.

## Decision 9: Compatibilidad con empleados historicos sin credenciales

- Decision: empleados sin fila en `empleado_credenciales` no autentican por login simbolico y reciben el mismo error generico.
- Rationale: evita migraciones de datos invasivas y mantiene rollout incremental.
- Alternatives considered:
  - Forzar credenciales para todos en migracion inicial: alto riesgo operativo y necesidad de secretos temporales.

## Decision 10: Cobertura de pruebas orientada a no regresion y seguridad

- Decision: ampliar pruebas API/integracion/seguridad para alta con credenciales, login publico, no regresion Basic Auth admin, normalizacion de email y persistencia de eventos.
- Rationale: satisface gates constitucionales de seguridad, contrato y verificacion.
- Alternatives considered:
  - Solo pruebas unitarias: insuficiente para validar seguridad y comportamiento HTTP real.

## Decision 11: Criterio formal de particion de migraciones Flyway

- Decision: usar una sola migracion `V2__add_employee_login_tables.sql` como
  estrategia default para esta feature; abrir `V3__...` solo bajo criterio formal
  de particion.
- Rationale: elimina ambiguedad de planificacion, reduce overhead operativo y
  mantiene trazabilidad consistente entre plan, tasks y ejecucion.
- Alternatives considered:
  - Definir siempre 2 migraciones: agrega complejidad sin beneficio cuando no hay
    backfill ni rollout por fases.
  - Decidir caso por caso sin regla escrita: introduce incertidumbre y retrabajo.

### Criterio formal de particion (cuando SI crear V3)

- Backfill/transformacion de datos historicos.
- Necesidad de despliegue por fases con compatibilidad temporal.
- Riesgo operativo alto por bloqueos/tiempos de ejecucion que justifique ventana separada.

## Trazabilidad SC-012 (documentacion consistente)

- **Separacion de canales**:
  - Contrato: `contracts/employee-login.openapi.yaml` declara `security: []` en login simbolico y Basic Auth para alta admin.
  - Quickstart: validaciones explicitas de login publico y endpoints admin protegidos.
- **Politica de migraciones tardias**:
  - Regla: no mutar `V2__add_employee_login_tables.sql` una vez aplicada.
  - Ajustes posteriores se entregan como `V3__...` con racional y orden de aplicacion documentado.
