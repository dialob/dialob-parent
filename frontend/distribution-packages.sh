#!/usr/bin/env bash

SCRIPT_DIR=$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" &>/dev/null && pwd)

DIST=$SCRIPT_DIR/dist.sh

PROJECT_NAMES=()

if [ -z "$PUBLISHED_PACKAGES" ] || [ "$PUBLISHED_PACKAGES" = "[]" ]; then
  ALL_PROJECTS=$(pnpm -r ls --json)
  for PROJECT in $(echo $ALL_PROJECTS | jq -cr '.[] | {name:.name,path:.path}'); do
    PROJECT_PATH=$(echo $PROJECT | jq -r .path)
    $DIST $PROJECT_PATH $* && PROJECT_NAMES+=("$(echo $PROJECT | jq -r .name)")
  done
else
  for PROJECT_NAME in `comm -12 <(echo $PUBLISHED_PACKAGES | jq -r '.[] | .name' | sort) <(pnpm m ls --json | jq -r '.[] | select(.private) | .name' | sort)`; do
    $DIST -r $(pnpm -r --filter=$PROJECT_NAME ls --json | jq -r '.[] | .path') $* && PROJECT_NAMES+=("$PROJECT_NAME")
  done
fi

if [ ! -z "$GITHUB_OUTPUT" ]; then
  ARTIFACTS=$(jq --compact-output --null-input '[ $ARGS.positional | .[] | select( . != "" ) ]' --args -- ${PROJECT_NAMES[@]})
  echo "artifacts=${ARTIFACTS}" | tee -a $GITHUB_OUTPUT
fi
