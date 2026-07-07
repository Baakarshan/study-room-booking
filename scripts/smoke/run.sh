#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR=$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)
MAVEN_CMD=${MAVEN_CMD:-}
BACKEND_LOG=${TMPDIR:-/tmp}/seatflow-smoke-backend.log
FRONTEND_LOG=${TMPDIR:-/tmp}/seatflow-smoke-frontend.log

resolve_maven_cmd() {
  if [[ -n "$MAVEN_CMD" ]]; then
    return
  fi
  if command -v mvn >/dev/null 2>&1 && mvn -v >/dev/null 2>&1; then
    MAVEN_CMD="mvn"
    return
  fi
  if command -v mise >/dev/null 2>&1; then
    MAVEN_CMD="mise exec maven@3.9.16 -- mvn"
    return
  fi
  local fallback_maven="$HOME/.local/share/mise/installs/maven/3.9.16/apache-maven-3.9.16/bin/mvn"
  if [[ -x "$fallback_maven" ]]; then
    MAVEN_CMD="$fallback_maven"
    return
  fi
  echo "未找到可用 Maven，请先安装 Maven，或通过 MAVEN_CMD 指定命令。" >&2
  exit 1
}

cleanup() {
  [[ -n "${BACKEND_PID:-}" ]] && kill "$BACKEND_PID" 2>/dev/null || true
  [[ -n "${FRONTEND_PID:-}" ]] && kill "$FRONTEND_PID" 2>/dev/null || true
}
trap cleanup EXIT

"$ROOT_DIR/scripts/smoke/reset-db.sh"
resolve_maven_cmd

cd "$ROOT_DIR"
if [[ "${SEATFLOW_SMOKE_SKIP_BUILD:-0}" != "1" ]]; then
  bash -lc "$MAVEN_CMD -pl ruoyi-admin -am package -DskipTests"
fi
java -jar ruoyi-admin/target/ruoyi-admin.jar --spring.profiles.active=druid,smoke \
  >"$BACKEND_LOG" 2>&1 &
BACKEND_PID=$!

cd "$ROOT_DIR/seatflow-ui"
npm run dev -- --mode smoke --host 127.0.0.1 --port 15173 \
  >"$FRONTEND_LOG" 2>&1 &
FRONTEND_PID=$!

for _ in {1..60}; do
  if curl -fsS http://127.0.0.1:18080/captchaImage >/dev/null \
    && curl -fsS http://127.0.0.1:15173/login >/dev/null; then
    break
  fi
  sleep 1
done

if ! curl -fsS http://127.0.0.1:18080/captchaImage >/dev/null; then
  echo "后端启动失败，日志：$BACKEND_LOG" >&2
  exit 1
fi

if ! curl -fsS http://127.0.0.1:15173/login >/dev/null; then
  echo "前端启动失败，日志：$FRONTEND_LOG" >&2
  exit 1
fi

npx playwright test "$@"
