#!/usr/bin/env bash
set -euo pipefail

BASE_URL="${BASE_URL:-http://localhost:8081}"
SPEC="${SPEC:-cypress/e2e/smoke.cy.js}"
WAIT_RETRIES="${WAIT_RETRIES:-45}"
WAIT_SECONDS="${WAIT_SECONDS:-2}"
START_STACK="${START_STACK:-1}"
CLEANUP="${CLEANUP:-0}"

# Valores por defecto para entorno local (alineados con CI)
export CRUD_DB_PASSWORD="${CRUD_DB_PASSWORD:-empleados123}"
export CRUD_BASIC_USER="${CRUD_BASIC_USER:-admin}"
export CRUD_BASIC_PASSWORD="${CRUD_BASIC_PASSWORD:-admin123}"

log() {
  echo "[cypress-smoke-local] $*"
}

on_error() {
  log "Error durante la ejecucion. Mostrando logs recientes de backend/postgres..."
  docker compose logs --no-color --tail=120 backend postgres || true
}

cleanup() {
  if [[ "$CLEANUP" == "1" ]]; then
    log "Apagando stack (CLEANUP=1)..."
    docker compose down -v || true
  fi
}

trap on_error ERR
trap cleanup EXIT

if [[ "$START_STACK" == "1" ]]; then
  log "Levantando servicios postgres + backend..."
  docker compose up -d --build postgres backend
fi

HEALTH_URL="${BASE_URL%/}/actuator/health"
log "Esperando healthcheck en ${HEALTH_URL} ..."

for ((i=1; i<=WAIT_RETRIES; i++)); do
  # Con seguridad activa, /actuator/health puede devolver 401.
  # Nos importa conectividad de red (HTTP distinto de 000), no un 200 exacto.
  http_code="$(curl -s -o /dev/null -w "%{http_code}" "$HEALTH_URL" || true)"

  if [[ "$http_code" != "000" && -n "$http_code" ]]; then
    log "Backend disponible (HTTP ${http_code}). Ejecutando Cypress smoke..."
    npx --yes cypress run --config "baseUrl=${BASE_URL}" --spec "$SPEC"
    log "Smoke test finalizado correctamente."
    exit 0
  fi

  if (( i == WAIT_RETRIES )); then
    log "Backend no disponible despues de ${WAIT_RETRIES} intentos."
    exit 1
  fi

  sleep "$WAIT_SECONDS"
done
