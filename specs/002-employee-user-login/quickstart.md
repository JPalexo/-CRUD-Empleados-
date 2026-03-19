# Quickstart: Login Simbolico de Empleado

**Feature**: 002-employee-user-login

## Prerequisitos

- Java 17
- Maven 4+ (o `mvnw` si aplica)
- Docker Desktop / Docker Engine con Compose
- Variables de entorno requeridas:
  - `CRUD_DB_NAME`
  - `CRUD_DB_USER`
  - `CRUD_DB_PASSWORD`
  - `CRUD_BASIC_USER`
  - `CRUD_BASIC_PASSWORD`

## 1) Levantar entorno local

```powershell
docker compose up -d --build
```

Validar que ambos contenedores esten arriba:

```powershell
docker compose ps
```

## 2) Crear empleado con credenciales (canal admin)

Este endpoint sigue protegido por Basic Auth administrativo.

```powershell
$pair = "${env:CRUD_BASIC_USER}:${env:CRUD_BASIC_PASSWORD}"
$basic = [Convert]::ToBase64String([Text.Encoding]::UTF8.GetBytes($pair))
$headers = @{ Authorization = "Basic $basic"; "Content-Type" = "application/json" }

$body = @{
  nombre = "Empleado Login"
  direccion = "Av Principal 123"
  telefono = "555-0101"
  email = "  EMPLEADO.LOGIN@EMPRESA.COM  "
  password = "abc12345"
} | ConvertTo-Json

Invoke-RestMethod -Method Post -Uri "http://localhost:8080/api/v1/empleados" -Headers $headers -Body $body
```

Resultado esperado:

- HTTP 201
- Empleado creado
- Email persistido en forma normalizada (`empleado.login@empresa.com`)

## 3) Login simbolico de empleado (endpoint publico)

Este endpoint NO requiere Basic Auth administrativo.

```powershell
$loginBody = @{
  email = " EMPLEADO.LOGIN@EMPRESA.COM "
  password = "abc12345"
} | ConvertTo-Json

Invoke-RestMethod -Method Post -Uri "http://localhost:8080/api/v1/empleados/login" -ContentType "application/json" -Body $loginBody
```

Resultado esperado:

- HTTP 200
- Confirmacion de autenticacion + identidad basica
- Sin JWT/token/sesion en la respuesta

## 4) Verificar no regresion de seguridad admin

Sin Basic Auth, los endpoints administrativos deben seguir protegidos.

```powershell
try {
  Invoke-WebRequest -Method Get -Uri "http://localhost:8080/api/v1/empleados" -TimeoutSec 10
} catch {
  $_.Exception.Response.StatusCode.Value__
}
```

Resultado esperado: `401`.

## 5) Verificar login fallido generico

```powershell
$invalidBody = @{ email = "empleado.login@empresa.com"; password = "wrong123" } | ConvertTo-Json
try {
  Invoke-WebRequest -Method Post -Uri "http://localhost:8080/api/v1/empleados/login" -ContentType "application/json" -Body $invalidBody -TimeoutSec 10
} catch {
  $_.Exception.Response.StatusCode.Value__
}
```

Resultado esperado:

- HTTP 401 (o codigo de autenticacion definido)
- Mensaje generico, sin revelar si el email existe

## 6) Ejecutar pruebas

```powershell
mvn clean test
```

Verificaciones recomendadas adicionales:

```powershell
pwsh ./scripts/verification/java17-build-check.ps1
pwsh ./scripts/verification/startup-repro-check.ps1 -Cycles 3 -MinPassPercentage 100
```

## 7) Apagar entorno

```powershell
docker compose down --remove-orphans
```

## 8) Evidencia SC-012 (separacion de canales y politica de migraciones)

Para dejar evidencia verificable de SC-012 en esta feature:

1. Ejecutar y documentar estas comprobaciones de seguridad por canal:
  - `POST /api/v1/empleados/login` sin Basic Auth administrativo (debe procesar login de empleado).
  - `GET /api/v1/empleados` sin Basic Auth administrativo (debe responder `401`).
  - `GET /v3/api-docs` sin Basic Auth administrativo (debe responder `401`).
2. Guardar referencias de ejecucion en artefactos de verificacion (`scripts/verification/logs/`).
3. Aplicar politica de migraciones para cambios posteriores:
  - `V2__add_employee_login_tables.sql` se considera inmutable una vez aplicada.
  - Cualquier ajuste tardio de esquema o rollout por fases se implementa en una nueva migracion `V3__...` (no mutar `V2`).
