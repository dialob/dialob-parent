#!/usr/bin/env bash

SCRIPT_DIR=$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" &>/dev/null && pwd)

DIST=$SCRIPT_DIR/dist.sh

OUTPUT=()

if [ -z "$PUBLISHED_PACKAGES" ] || [ "$PUBLISHED_PACKAGES" = "[]" ]; then
  for PROJECT in $(pnpm -r ls --json | jq -r '.[] | .path'); do
    $DIST $PROJECT $*
    OUTPUT+="$PROJECT"
  done
else
  for PROJECT in `comm -12 <(echo $PUBLISHED_PACKAGES | jq -r '.[] | .name') <(pnpm m ls --json | jq -r '.[] | select(.private) | .name')`; do
    $DIST -r $(pnpm -r --filter=$PROJECT ls --json | jq -r '.[] | .path') $*
    OUTPUT+="$PROJECT
  done
fi

if [ -e "$GITHUB_OUTPUT" ]; then
  ARTIFACTS=$(jq --compact-output --null-input '[ $ARGS.positional | .[] | select( . != "" ) ]' --args -- ${OUTPUT[@]})
  echo "artifacts=${ARTIFACTS}" | tee -a $GITHUB_OUTPUT
fi
