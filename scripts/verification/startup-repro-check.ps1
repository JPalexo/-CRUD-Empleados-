param(
    [int]$Cycles = 20,
    [int]$MinPassPercentage = 95
)

$successCount = 0
$failureCount = 0

function Get-HttpStatusCode {
    param(
        [string]$Uri,
        [string]$Method = "GET",
        [string]$Body,
        [string]$ContentType = "application/json"
    )

    try {
        $args = @("-s", "-o", "NUL", "-w", "%{http_code}", "-X", $Method, $Uri)
        if ($Body) {
            $args += @("-H", "Content-Type: $ContentType", "-d", $Body)
        }

        $status = (& curl.exe @args).Trim()
        if ($LASTEXITCODE -ne 0) {
            return -1
        }

        $parsed = 0
        if ([int]::TryParse($status, [ref]$parsed)) {
            return $parsed
        }

        return -1
    } catch {
        return -1
    }
}

function Test-BackendHttpReady {
    $status = Get-HttpStatusCode -Uri "http://localhost:8080/v3/api-docs" -Method "GET"
    return $status -eq 401 -or $status -eq 200
}

function Test-EmployeeLoginPublicRoute {
    $status = Get-HttpStatusCode -Uri "http://localhost:8080/api/v1/empleados/login" -Method "POST" -Body "{}" -ContentType "application/json"
    return $status -eq 400
}

function Test-AdminCrudProtected {
    $status = Get-HttpStatusCode -Uri "http://localhost:8080/api/v1/empleados" -Method "GET"
    return $status -eq 401
}

for ($cycle = 1; $cycle -le $Cycles; $cycle++) {
    Write-Host "[Cycle $cycle/$Cycles] Starting docker compose stack..."
    docker compose up -d --build | Out-Null

    if ($LASTEXITCODE -ne 0) {
        Write-Error "[Cycle $cycle] FAIL: docker compose up returned non-zero exit code."
        $failureCount++
        docker compose down --remove-orphans | Out-Null
        continue
    }

    $backendRunning = $false
    $postgresRunning = $false
    $appStarted = $false
    $flywayDetected = $false
    $httpReady = $false
    $loginPublicReady = $false
    $adminProtected = $false

    for ($attempt = 1; $attempt -le 15; $attempt++) {
        Start-Sleep -Seconds 2

        $psOutput = docker compose ps
        $backendRunning = $psOutput -match "crud-empleados-backend" -and $psOutput -match "Up"
        $postgresRunning = $psOutput -match "crud-empleados-postgres" -and $psOutput -match "Up"

        if (-not $backendRunning -or -not $postgresRunning) {
            continue
        }

        $backendLogs = docker compose logs backend --no-color --tail 400
        $flywayDetected = $backendLogs -match "Flyway|schema history|Successfully applied"
        $appStarted = $backendLogs -match "Started .*Application"
        $httpReady = Test-BackendHttpReady
        $loginPublicReady = Test-EmployeeLoginPublicRoute
        $adminProtected = Test-AdminCrudProtected

        if ($appStarted -and $httpReady -and $loginPublicReady -and $adminProtected) {
            break
        }
    }

    if (-not $backendRunning -or -not $postgresRunning) {
        Write-Error "[Cycle $cycle] FAIL: one container did not start correctly (backend=$backendRunning, postgres=$postgresRunning)."
        $failureCount++
        docker compose down --remove-orphans | Out-Null
        continue
    }

    if (-not $appStarted -or -not $httpReady -or -not $loginPublicReady -or -not $adminProtected) {
        Write-Error "[Cycle $cycle] FAIL: backend did not satisfy readiness+smoke checks (appStarted=$appStarted, httpReady=$httpReady, loginPublicReady=$loginPublicReady, adminProtected=$adminProtected)."
        $failureCount++
        docker compose down --remove-orphans | Out-Null
        continue
    }

    if (-not $flywayDetected) {
        Write-Warning "[Cycle $cycle] PASS with warning: Flyway log marker not found, but backend is started and HTTP-ready."
    } else {
        Write-Host "[Cycle $cycle] PASS: backend + postgres up, Flyway detected, login route public, admin route protected."
    }

    $successCount++
    docker compose down --remove-orphans | Out-Null
}

$passRate = [math]::Round(($successCount * 100.0) / [math]::Max($Cycles, 1), 2)
Write-Host "Completed $Cycles cycles: $successCount pass / $failureCount fail (pass rate $passRate%)."

if ($passRate -lt $MinPassPercentage) {
    throw "Startup reproducibility gate failed: $passRate% < required $MinPassPercentage%."
}
