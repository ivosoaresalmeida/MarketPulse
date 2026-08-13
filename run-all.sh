#!/usr/bin/env bash

set -u

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
MVNW="$ROOT_DIR/mvnw"
COMPOSE_FILE="$ROOT_DIR/compose.yml"

DB_CONTAINERS=(
  "marketpulse-postgres"
)

SERVICE_LABELS=(
  "api-gateway"
  "portfolio-service"
  "user-service"
)

SERVICE_MODULES=(
  "services/api-gateway"
  "services/portfolio-service"
  "services/user-service"
)

SERVICE_PORTS=(
  "8080"
  "8081"
  "8082"
)

PIDS=()
SHUTTING_DOWN=0

log() {
  printf '[run-all] %s\n' "$*"
}

error() {
  printf '[run-all] ERROR: %s\n' "$*" >&2
}

is_container_running() {
  local name="$1"
  podman ps --filter "name=^${name}$" --format '{{.Names}}' | grep -Fxq "$name"
}

is_container_existing() {
  local name="$1"
  podman container exists "$name"
}

ensure_compose_cmd() {
  if podman compose version >/dev/null 2>&1; then
    COMPOSE_CMD=(podman compose -f "$COMPOSE_FILE")
    return 0
  fi

  if command -v podman-compose >/dev/null 2>&1; then
    COMPOSE_CMD=(podman-compose -f "$COMPOSE_FILE")
    return 0
  fi

  return 1
}

ensure_postgres_containers() {
  local missing=()
  local name

  for name in "${DB_CONTAINERS[@]}"; do
    if is_container_running "$name"; then
      continue
    fi

    if is_container_existing "$name"; then
      log "Starting existing PostgreSQL container: ${name}"
      podman start "$name" >/dev/null || return 1
    else
      missing+=("$name")
    fi
  done

  if [ "${#missing[@]}" -eq 0 ]; then
    log "PostgreSQL containers already running."
    return 0
  fi

  log "Starting missing PostgreSQL containers: ${missing[*]}"

  if ! ensure_compose_cmd; then
    error "Neither 'podman compose' nor 'podman-compose' is available."
    return 1
  fi

  (cd "$ROOT_DIR" && "${COMPOSE_CMD[@]}" up -d postgres) || return 1

  for name in "${DB_CONTAINERS[@]}"; do
    if ! is_container_running "$name"; then
      error "Container '$name' is not running after compose up."
      return 1
    fi
  done

  log "PostgreSQL containers are running."
}

start_service() {
  local label="$1"
  local module="$2"
  local port="$3"

  log "Starting ${label} (${module}) on port ${port}..."

  (
    cd "$ROOT_DIR" || exit 1
    "$MVNW" -pl "$module" spring-boot:run 2>&1 | sed -u "s/^/[${label}] /"
  ) &

  PIDS+=("$!")
}

stop_services() {
  local rc=0
  local pid

  [ "$SHUTTING_DOWN" -eq 1 ] && return 0
  SHUTTING_DOWN=1

  if [ "${#PIDS[@]}" -gt 0 ]; then
    log "Stopping Spring Boot services..."
  fi

  for pid in "${PIDS[@]}"; do
    kill "$pid" 2>/dev/null || true
  done

  for pid in "${PIDS[@]}"; do
    if ! wait "$pid" 2>/dev/null; then
      rc=$?
    fi
  done

  return "$rc"
}

on_interrupt() {
  log "Ctrl+C received."
  stop_services
  exit 130
}

on_term() {
  stop_services
  exit 143
}

trap on_interrupt INT
trap on_term TERM

main() {
  local i
  local wait_rc

  if ! command -v podman >/dev/null 2>&1; then
    error "Podman is not installed or not on PATH."
    exit 1
  fi

  if [ ! -x "$MVNW" ]; then
    error "Maven Wrapper not found or not executable at: $MVNW"
    exit 1
  fi

  if [ ! -f "$COMPOSE_FILE" ]; then
    error "compose.yml not found at: $COMPOSE_FILE"
    exit 1
  fi

  ensure_postgres_containers || exit 1

  for i in "${!SERVICE_LABELS[@]}"; do
    start_service "${SERVICE_LABELS[$i]}" "${SERVICE_MODULES[$i]}" "${SERVICE_PORTS[$i]}"
  done

  log "All services started. Press Ctrl+C to stop Spring Boot processes."

  wait -n "${PIDS[@]}"
  wait_rc=$?

  if [ "$SHUTTING_DOWN" -eq 0 ]; then
    error "A service exited unexpectedly. Shutting down remaining services."
    stop_services
  fi

  exit "$wait_rc"
}

main "$@"
