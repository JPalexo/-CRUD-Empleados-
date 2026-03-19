# Data Model: Login Simbolico de Empleado

**Feature**: 002-employee-user-login  
**Date**: 2026-03-18

## 1. Empleado (existente)

- Purpose: entidad principal de negocio para CRUD de empleados.
- Primary Key: compuesta (`clave_prefijo`, `clave_numero`).

### Fields relevantes

- `clave_prefijo` (varchar(4), NOT NULL, fixed `EMP-`)
- `clave_numero` (bigint, NOT NULL, > 0)
- `nombre` (varchar(100), NOT NULL)
- `direccion` (varchar(100), NOT NULL)
- `telefono` (varchar(100), NOT NULL)

### Notes

- No se cambia la semantica de la PK ni el formato de `clave` logica.
- Borrado de empleado implica que no debe autenticarse luego.

## 2. CredencialEmpleado (nueva)

- Purpose: almacenar identidad y secreto de acceso simbolico del empleado.
- Table propuesta: `empleado_credenciales`.
- Cardinality: 1:1 con `Empleado`.

### Fields

- `clave_prefijo` (varchar(4), NOT NULL, FK -> empleados.clave_prefijo)
- `clave_numero` (bigint, NOT NULL, FK -> empleados.clave_numero)
- `email_normalizado` (varchar(254), NOT NULL, UNIQUE)
- `password_hash` (varchar(255), NOT NULL)
- `habilitada` (boolean, NOT NULL, default true)
- `created_at` (timestamp with time zone, NOT NULL)
- `updated_at` (timestamp with time zone, NOT NULL)

### Constraints

- PK compuesta (`clave_prefijo`, `clave_numero`)
- FK compuesta con `ON DELETE CASCADE`
- Unique index en `email_normalizado`
- Check de email no vacio tras normalizacion
- Check de `password_hash` no vacio

## 3. EventoLoginEmpleado (nuevo)

- Purpose: trazabilidad de intentos de login (exitosos y fallidos).
- Table propuesta: `empleado_login_eventos`.

### Fields

- `id` (bigserial, PK)
- `email_normalizado_input` (varchar(254), NOT NULL)
- `clave_prefijo` (varchar(4), NULL)
- `clave_numero` (bigint, NULL)
- `resultado` (varchar(16), NOT NULL: `SUCCESS` | `FAILURE`)
- `motivo` (varchar(64), NOT NULL; ejemplo: `INVALID_CREDENTIALS`)
- `ocurrido_en` (timestamp with time zone, NOT NULL)

### Constraints

- FK opcional (`clave_prefijo`, `clave_numero`) cuando se identifica empleado
- Index por (`email_normalizado_input`, `ocurrido_en desc`)
- Index por `ocurrido_en`

## Relationships

- `Empleado` 1 --- 0..1 `CredencialEmpleado`
- `Empleado` 1 --- 0..N `EventoLoginEmpleado`

## Validation Rules

- Email:
  - Normalizacion obligatoria: `trim + lowercase` antes de persistir/comparar.
  - Debe cumplir formato de email valido.
  - Debe ser unico sobre valor normalizado.
- Password:
  - Longitud minima 8, maxima 64.
  - Debe incluir al menos una letra y un numero.
  - Nunca se persiste en texto plano.
- Login:
  - Respuesta generica en fallo para evitar enumeracion.
  - Sin bloqueo de cuenta por intentos fallidos consecutivos.

## State Transitions

### CredencialEmpleado

- `CREATED/HABILITADA` -> estado inicial en alta de empleado.
- `HABILITADA` -> `NO_AUTENTICABLE` cuando empleado se elimina (por cascada o ausencia de registro).
- `HABILITADA` -> `DESHABILITADA` reservado para futuras iteraciones (sin endpoint en alcance actual).

### Resultado de login

- `REQUEST_RECEIVED` -> `SUCCESS` (credenciales validas)
- `REQUEST_RECEIVED` -> `FAILURE` (cualquier causa de autenticacion)

Cada transicion terminal debe generar un registro en `empleado_login_eventos`.
