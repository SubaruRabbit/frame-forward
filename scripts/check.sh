#!/usr/bin/env sh

set -eu

run_workspace_check() {
  working_directory=$1
  shift

  (
    cd "$working_directory"
    "$@"
  )
}

script_directory=$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)
repository_root=$(dirname -- "$script_directory")

quality_base_ref=${QUALITY_BASE_REF:-HEAD}

run_workspace_check "$repository_root/frame-forward-app" npm run quality
run_workspace_check "$repository_root/frame-forward-server" env "QUALITY_BASE_REF=$quality_base_ref" mvn -q -DskipTests=false verify
run_workspace_check "$repository_root/contracts" npm run quality
