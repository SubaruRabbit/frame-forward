#!/usr/bin/env sh

set -eu

pattern='^(build|chore|ci|docs|feat|fix|perf|refactor|revert|style|test)(\([a-z0-9][a-z0-9._/-]*\))?!?: .+$'

validate() {
  message=$1
  if ! printf '%s\n' "$message" | grep -Eq "$pattern"; then
    printf '不符合 Conventional Commits：%s\n' "$message" >&2
    return 1
  fi
}

if [ "${1:-}" = '--stdin' ]; then
  while IFS= read -r message || [ -n "$message" ]; do
    [ -z "$message" ] || validate "$message"
  done
  exit 0
fi

range=${1:-HEAD~1..HEAD}
git log --format=%s "$range" | while IFS= read -r message; do
  validate "$message"
done
