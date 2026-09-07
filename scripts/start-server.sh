#!/usr/bin/env sh

set -eu

ROOT_DIR=$(CDPATH= cd -- "$(dirname -- "$0")/.." && pwd)
ENV_FILE="$ROOT_DIR/.env"

if [ ! -f "$ENV_FILE" ]; then
  echo "Missing $ENV_FILE. Copy .env.example to .env and fill in local values." >&2
  exit 1
fi

set -a
# shellcheck disable=SC1090
. "$ENV_FILE"
set +a

cd "$ROOT_DIR/frame-forward-server"
mvn -pl bootstrap -am package install:install -DskipTests -Dspring-boot.repackage.skip=true
exec mvn -pl bootstrap spring-boot:run -Dspring-boot.run.profiles=dev
