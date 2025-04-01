#!/usr/bin/env bash

SCRIPT_DIR=$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" &>/dev/null && pwd)

export CDN_DOMAIN=cdn.resys.io
export GITHUB_REPOSITORY_OWNER=dialob

DIST=$SCRIPT_DIR/dist.sh

if [ -z "$PUBLISHED_PACKAGES" ]; then
  for PROJECT in $(pnpm -r ls --json | jq -r '.[] | .path'); do
    echo $PROJECT
    $DIST $PROJECT $*
  done
else
  for PROJECT in `comm -12 <(echo $PUBLISHED_PACKAGES | jq -r '.[] | .name') <(pnpm m ls --json | jq -r '.[] | select(.private) | .name')`; do
    $DIST -r $(pnpm -r --filter=$PROJECT ls --json | jq -r '.[] | .path') $*
  done
fi



