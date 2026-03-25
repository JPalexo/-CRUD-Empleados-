# Research: Gestion Integral de Departamentos y Metricas de Ocupacion

**Date**: 2026-03-24
**Feature**: 003-crud-departamentos

## Decision 1: Refactor de conteo en EmpleadoRepository

- Decision: eliminar/evitar el camino legacy por string concatenado y usar exclusivamente conteo por clave compuesta (prefijo + numero).
- Rationale: evita inconsistencias de ocupacion y errores por parsing/formato de clave.
- Alternatives considered:
  - Mantener ambos metodos: descartado por ambiguedad y riesgo de drift.

## Decision 2: Estrategia de concurrencia para cupo

- Decision: usar locking pesimista en base de datos para operaciones criticas de capacidad/ocupacion.
- Rationale: minimiza carrera de concurrencia y garantiza atomicidad sin sobrecupo en evaluaciones simultaneas.
- Alternatives considered:
  - Locking optimista: descartado por mayor complejidad de reintentos y ventanas de conflicto en esta iteracion.
  - Cola de serializacion: descartado por sobreingenieria fuera de alcance.

## Decision 3: Capacidad fija de negocio

- Decision: mantener `capacidadMaxima = 3` como regla de dominio backend.
- Rationale: regla ya aceptada en especificacion y casos de uso.
- Alternatives considered:
  - Capacidad configurable por departamento: fuera de alcance de esta iteracion.

## Decision 4: Calculo de metrica en servicio de consulta

- Decision: calcular en `DepartamentoQueryService` `ocupacionActual`, `capacidadMaxima`, `porcentajeOcupacion` y `estaLleno` en tiempo real.
- Rationale: mantiene coherencia entre listado y detalle sin datos derivados persistidos.
- Alternatives considered:
  - Persistir metrica precomputada: mayor complejidad y riesgo de desincronizacion.

## Decision 5: Precision del porcentaje

- Decision: truncar `porcentajeOcupacion` a 2 decimales, sin redondeo.
- Rationale: regla explicita del dominio y resultados deterministas entre endpoints.
- Alternatives considered:
  - Redondeo matematico: descartado por no cumplir aclaracion de negocio.

## Decision 6: Normalizacion fuerte de nombre

- Decision: aplicar `trim()` + `toLowerCase()` en `DepartamentoCreateService` y flujo de update.
- Rationale: elimina duplicados fantasma por variaciones de formato.
- Alternatives considered:
  - Unicidad literal en DB: insuficiente para equivalencias semanticas.

## Decision 7: Borrado protegido por dependencia

- Decision: `DepartamentoDeleteService` valida empleados asociados y lanza conflicto `409` cuando el departamento no este vacio.
- Rationale: protege integridad de dominio y evita huellas de FK invalidas.
- Alternatives considered:
  - Borrado en cascada: descartado por perdida accidental de datos de empleados.

## Decision 8: Politica de clave `DEP-{numero}`

- Decision: no reutilizar numeros de departamentos eliminados; secuencia monotona creciente.
- Rationale: conserva trazabilidad historica, evita colisiones semanticas y simplifica auditoria.
- Alternatives considered:
  - Reutilizacion de huecos: descartada por ambiguedad historica.

## Decision 9: Politica de paginacion de listado

- Decision: `size` por defecto 20, maximo 100; si `size > 100` responder `400` con error de validacion.
- Rationale: evita respuestas excesivas, establece contrato estable y reduce ambiguedad de cliente.
- Alternatives considered:
  - Clamping silencioso a 100: descartado por esconder errores del cliente.
  - `size` fijo: descartado por reducir flexibilidad operativa.

## Decision 10: Controladores segregados por responsabilidad

- Decision: mantener `DepartamentoCreateController`, `DepartamentoQueryController` y `DepartamentoMaintenanceController` bajo `/api/v1/departamentos`.
- Rationale: mantiene coherencia con arquitectura por capas y facilita pruebas focalizadas.
- Alternatives considered:
  - Un controlador unico monolitico: menor claridad y mantenibilidad.

## Decision 11: Estrategia de pruebas backend

- Decision: pruebas de API/integracion para `201/200/204`, `401`, `404`, `409` y `400` de paginacion, incluyendo consistencia de metricas y reglas de clave monotona.
- Rationale: verifica comportamiento real contra PostgreSQL y reglas de integridad.
- Alternatives considered:
  - Solo mocks/unit tests: no valida integracion real de repositorio + servicio + DB.

## Decision 12: Formalizacion de componente de capacidad

- Decision: consolidar reglas de cupo/ocupacion en `DepartamentoCapacityService`.
- Rationale: evita logica duplicada entre query/delete y define una frontera de dominio unica para capacidad.
- Alternatives considered:
  - Repartir reglas entre servicios CRUD: descartado por riesgo de drift e inconsistencias.

## Decision 13: Objetivo de performance con criterio medible

- Decision: establecer SLO local para consultas de departamentos: p95 <= 300 ms, p99 <= 500 ms y error rate < 1% con 20 VUs durante 5 minutos.
- Rationale: convierte el objetivo de rendimiento en criterio verificable y comparable por iteracion.
- Alternatives considered:
  - Meta cualitativa sin umbral numerico: descartada por no ser auditable.
