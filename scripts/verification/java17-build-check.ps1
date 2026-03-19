$timestamp = Get-Date -Format "yyyyMMdd-HHmmss"
$logsDir = "scripts/verification/logs"
$logFile = Join-Path $logsDir "java17-build-check-$timestamp.log"

New-Item -ItemType Directory -Path $logsDir -Force | Out-Null

function Write-Log {
    param([string]$Line)
    $Line | Tee-Object -FilePath $logFile -Append
}

$javaVersionOutput = & java -version 2>&1
$javaVersionOutput | ForEach-Object { Write-Log $_ }
if (($javaVersionOutput -join "`n") -notmatch 'version "17|openjdk version "17') {
    throw "Java runtime is not version 17."
}

$mvnCommand = $null
if (Test-Path "./mvnw.cmd") {
    $mvnCommand = "./mvnw.cmd"
} elseif (Test-Path "./mvnw") {
    $mvnCommand = "./mvnw"
} else {
    $mvnCommand = "mvn"
}

$mvnVersionOutput = & $mvnCommand -v 2>&1
$mvnVersionOutput | ForEach-Object { Write-Log $_ }
if (($mvnVersionOutput -join "`n") -notmatch "Java version:\s*17") {
    throw "Maven is not running with Java 17."
}

Write-Log "Running clean verify with $mvnCommand"
$verifyOutput = & $mvnCommand clean verify 2>&1
$verifyOutput | ForEach-Object { Write-Log $_ }
if ($LASTEXITCODE -ne 0) {
    throw "Build verification failed. Check $logFile"
}

Write-Log "PASS: Java 17 build verification completed successfully."
Write-Host "Verification logs: $logFile"
