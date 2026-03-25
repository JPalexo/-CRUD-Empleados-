# Quickstart: CRUD de Empleados v1

## Purpose

Levantar el entorno local con dos contenedores (`backend` + `postgres`) y validar el contrato
de la API versionada `v1`, incluyendo listado paginado cero-basado.

## Prerequisites

- Java 17
- Maven 3.9+ (o Maven Wrapper)
- Docker Desktop (con Docker Compose)
- PowerShell 7+

## 0) Preparar configuracion

El repositorio incluye `.env` con variables requeridas para Compose. Verifica al menos:

- `CRUD_DB_NAME`
- `CRUD_DB_USER`
- `CRUD_DB_PASSWORD`
- `CRUD_BASIC_USER`
- `CRUD_BASIC_PASSWORD`

## 1) Verificar baseline Java 17

```powershell
java -version
.\mvnw.cmd -v
```

Resultado esperado: ambos comandos reportan Java 17.

## 2) Build y pruebas base

```powershell
.\mvnw.cmd clean verify
```

Verificacion operacional adicional:

```powershell
pwsh .\scripts\verification\java17-build-check.ps1
```

## 3) Levantar contenedores

```powershell
docker compose up -d postgres
docker compose up -d --build backend
docker compose ps
```

Nota: si hiciste cambios de codigo recientes, usa siempre `--build` para evitar ejecutar una imagen backend desactualizada.
Resultado esperado:

- `postgres` en estado `healthy`.
- `backend` en estado `running`.
- Flyway ejecutado al inicio del backend.

## 4) Verificar seguridad en documentacion

- `http://localhost:8081/swagger-ui/index.html`
- `http://localhost:8081/v3/api-docs`

Sin credenciales debe responder `401`.

## 5) Validar CRUD versionado (`/api/v1/empleados`)

Crear departamento base (requisito para alta de empleado):

```powershell
curl.exe -u admin:admin123 -X POST "http://localhost:8081/api/v1/departamentos" -H "Content-Type: application/json" -d "{\"nombre\":\"Departamento Base Empleados\"}"
```

Guardar la `clave` devuelta (ejemplo: `DEP-1`) y usarla en `departamentoClave`.

Crear empleado (sin `clave`):

```powershell
curl.exe -u admin:admin123 -X POST "http://localhost:8081/api/v1/empleados" -H "Content-Type: application/json" -d "{\"nombre\":\"Ana Lopez\",\"direccion\":\"Calle 123\",\"telefono\":\"5512345678\",\"email\":\"ana.lopez@empresa.com\",\"password\":\"abc12345\",\"departamentoClave\":\"DEP-1\"}"
```

Resultado esperado: `201 Created` con `clave` en formato `EMP-{numero}`.

La respuesta de create y detalle debe incluir tambien `departamentoClave` con formato `DEP-{numero}` del departamento asignado.

Obtener listado paginado (primera pagina explicita):

```powershell
curl.exe -u admin:admin123 "http://localhost:8081/api/v1/empleados?page=0&size=10"
```

Resultado esperado: sobre con forma `{ "data": [...], "pagination": {"page":0,"size":10,"totalElements":...,"totalPages":...} }`.

Obtener detalle:

```powershell
curl.exe -u admin:admin123 "http://localhost:8081/api/v1/empleados/EMP-1"
```

Actualizar:

```powershell
curl.exe -u admin:admin123 -X PUT "http://localhost:8081/api/v1/empleados/EMP-1" -H "Content-Type: application/json" -d "{\"nombre\":\"Ana L.\",\"direccion\":\"Calle 456\",\"telefono\":\"5599999999\",\"departamentoClave\":\"DEP-2\"}"
```

`departamentoClave` es opcional en update. Si se envia, el sistema reasigna el empleado al departamento indicado (debe existir y tener formato `DEP-{numero}`).

Eliminar:

```powershell
curl.exe -u admin:admin123 -X DELETE "http://localhost:8081/api/v1/empleados/EMP-1"
```

## 6) Validar defaults y reglas de paginacion

Falta `page` (debe aplicar `page=0`):

```powershell
curl.exe -u admin:admin123 "http://localhost:8081/api/v1/empleados?size=10"
```

Falta `size` (debe aplicar `size=10`):

```powershell
curl.exe -u admin:admin123 "http://localhost:8081/api/v1/empleados?page=0"
```

Paginacion invalida (`page=-1` o `size=0`) debe responder `400`.

## 7) Validaciones negativas de contrato

Alta con `clave` manual (debe responder `400`):

```powershell
curl.exe -u admin:admin123 -X POST "http://localhost:8081/api/v1/empleados" -H "Content-Type: application/json" -d "{\"clave\":\"EMP-999\",\"nombre\":\"Invalido\",\"direccion\":\"X\",\"telefono\":\"Y\",\"email\":\"invalido@empresa.com\",\"password\":\"abc12345\",\"departamentoClave\":\"DEP-1\"}"
```

Alta sin `departamentoClave` (debe responder `400`):

```powershell
curl.exe -u admin:admin123 -X POST "http://localhost:8081/api/v1/empleados" -H "Content-Type: application/json" -d "{\"nombre\":\"Sin Depto\",\"direccion\":\"X\",\"telefono\":\"Y\",\"email\":\"sin.depto@empresa.com\",\"password\":\"abc12345\"}"
```

Clave con ceros a la izquierda (debe responder `400`):

```powershell
curl.exe -u admin:admin123 "http://localhost:8081/api/v1/empleados/EMP-0007"
```

Ruta sin version (debe responder `404`):

```powershell
curl.exe -u admin:admin123 "http://localhost:8081/api/empleados"
```

Version no publicada (debe responder `404`):

```powershell
curl.exe -u admin:admin123 "http://localhost:8081/api/v2/empleados"
```

## 8) Verificar reproducibilidad de arranque

```powershell
pwsh .\scripts\verification\startup-repro-check.ps1 -Cycles 20 -MinPassPercentage 95
```

Resultado esperado: tasa de exito >= 95%.

## 9) Detener entorno

```powershell
docker compose down
```
