#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR=$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)
MYSQL_BIN=${MYSQL_BIN:-mysql}
MYSQL_HOST=${SEATFLOW_SMOKE_DB_HOST:-127.0.0.1}
MYSQL_PORT=${SEATFLOW_SMOKE_DB_PORT:-3306}
MYSQL_USER=${SEATFLOW_SMOKE_DB_USER:-root}
MYSQL_PASSWORD=${SEATFLOW_SMOKE_DB_PASSWORD:-password}
DATABASE=${SEATFLOW_SMOKE_DB_NAME:-seatflow_smoke}

if [[ "$DATABASE" != *smoke* && "$DATABASE" != *test* ]]; then
  echo "拒绝重置非测试数据库：$DATABASE" >&2
  exit 1
fi

MYSQL_ARGS=(-h "$MYSQL_HOST" -P "$MYSQL_PORT" -u "$MYSQL_USER")
export MYSQL_PWD="$MYSQL_PASSWORD"

"$MYSQL_BIN" "${MYSQL_ARGS[@]}" -e \
  "drop database if exists \`$DATABASE\`; create database \`$DATABASE\` character set utf8mb4 collate utf8mb4_unicode_ci;"
"$MYSQL_BIN" "${MYSQL_ARGS[@]}" "$DATABASE" < "$ROOT_DIR/sql/ry_20260417.sql"
"$MYSQL_BIN" "${MYSQL_ARGS[@]}" "$DATABASE" < "$ROOT_DIR/sql/quartz.sql"
"$MYSQL_BIN" "${MYSQL_ARGS[@]}" "$DATABASE" < "$ROOT_DIR/sql/seatflow_20260702.sql"
"$MYSQL_BIN" "${MYSQL_ARGS[@]}" "$DATABASE" -e \
  "update sys_config set config_value='false' where config_key='sys.account.captchaEnabled';"

if command -v redis-cli >/dev/null 2>&1; then
  redis-cli -h "${SEATFLOW_SMOKE_REDIS_HOST:-127.0.0.1}" \
    -p "${SEATFLOW_SMOKE_REDIS_PORT:-6379}" \
    -n "${SEATFLOW_SMOKE_REDIS_DATABASE:-15}" flushdb >/dev/null
fi

echo "冒烟数据库 $DATABASE 已重置。"
