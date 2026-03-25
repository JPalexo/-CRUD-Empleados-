# Quickstart: Backend CRUD de Departamentos y Metricas de Ocupacion

## Purpose

Validar el alcance backend de la feature 003: CRUD de departamentos,
normalizacion de nombre, metrica dinamica de ocupacion y borrado protegido (`409`).

## Prerequisites

- Java 17
- Maven Wrapper
- Docker Compose
- Credenciales Basic Auth administrativas

## 1) Build base

```powershell
.\mvnw.cmd clean verify
```

## 2) Levantar PostgreSQL en Docker

```powershell
docker compose up -d postgres
docker compose ps
```

## 3) Ejecutar backend y verificar migraciones

```powershell
.\mvnw.cmd spring-boot:run
```

Esperado:

- Flyway aplica `V1`, `V2`, `V3` sin errores.
- API disponible en `http://localhost:8081` (host port por defecto del compose de esta feature).

## 4) Validar CRUD de departamentos

### Crear (normalizacion + clave DEP-n)

```powershell
curl.exe -u admin:admin123 -X POST "http://localhost:8081/api/v1/departamentos" -H "Content-Type: application/json" -d "{\"nombre\":\"  Ventas  \"}"
```

Validar:

- `201`
- `clave` formato `DEP-{numero}`
- unicidad por nombre normalizado

### Listar con metrica

```powershell
curl.exe -u admin:admin123 "http://localhost:8081/api/v1/departamentos?page=0&size=10"
```

Validar campos:

- `ocupacionActual`
- `capacidadMaxima` (3)
- `porcentajeOcupacion` (truncado 2 decimales, sin redondeo)
- `estaLleno`

### Validar politica de paginacion (default y maximo)

```powershell
curl.exe -u admin:admin123 "http://localhost:8081/api/v1/departamentos?page=0"
```

Validar:

- si no se envia `size`, aplica default 20.

```powershell
curl.exe -u admin:admin123 "http://localhost:8081/api/v1/departamentos?page=0&size=101"
```

Validar:

- respuesta `400` con mensaje de validacion por exceder maximo 100.

### Detalle con metrica consistente

```powershell
curl.exe -u admin:admin123 "http://localhost:8081/api/v1/departamentos/DEP-1"
```

### Actualizar nombre

```powershell
curl.exe -u admin:admin123 -X PUT "http://localhost:8081/api/v1/departamentos/DEP-1" -H "Content-Type: application/json" -d "{\"nombre\":\"comercial\"}"
```

### Delete protegido

```powershell
curl.exe -u admin:admin123 -X DELETE "http://localhost:8081/api/v1/departamentos/DEP-1"
```

Si hay empleados asignados, esperado: `409`.

## 5) Pruebas clave

```powershell
.\mvnw.cmd -Dtest=DepartamentoQueryApiTest,DepartamentoMaintenanceApiTest test
```

Cobertura esperada:

- metrica cambia al agregar/quitar empleados
- conflicto `409` al borrar departamento no vacio
- `400` cuando `size > 100`

## 6) Verificacion de seguridad/versionado

- `/api/v1/departamentos` sin auth -> `401`
- `/api/departamentos` -> `404`
- `/api/v2/departamentos` -> `404`

## 7) Verificacion de performance (criterio medible)

Objetivo medible para consultas de departamentos:

- scope: `GET /api/v1/departamentos?page=0&size=20` y `GET /api/v1/departamentos/{clave}`
- carga: 20 usuarios virtuales durante 5 minutos
- aceptacion:
	- p95 <= 300 ms
	- p99 <= 500 ms
	- error rate < 1%

Ejecucion sugerida (segun herramienta disponible en el repo/equipo):

```powershell
# Ejemplo: ejecutar suite/perfil de performance existente
.\mvnw.cmd -Dtest=CrudLatencyIT test
```

Evidencia esperada:

- reporte de latencias por percentil (p95/p99)
- porcentaje de errores
- conclusion de cumplimiento/no cumplimiento respecto a umbrales

Resultado de esta ejecucion (2026-03-24):

- comando ejecutado con configuracion FR-024/SC-014: `mvn -Dtest=DepartamentoQueryPerformanceTest -Dperf.users=20 -Dperf.duration.ms=300000 -Dperf.p95.max.ms=300 -Dperf.p99.max.ms=500 -Dperf.max.error.rate=0.01 test`
- resultado de build: SUCCESS
- estado de prueba: SKIPPED por Testcontainers (`Could not find a valid Docker environment`)
- verificacion Docker CLI en la misma sesion: `docker version` responde correctamente

Si este bloqueo aparece, corregir conectividad Testcontainers->Docker y repetir la corrida para obtener p95/p99/error rate reales.
