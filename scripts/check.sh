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

run_workspace_check "$repository_root/frame-forward-app" npm run typecheck
run_workspace_check "$repository_root/frame-forward-server" mvn test
run_workspace_check "$repository_root/contracts" npm run validate
