#!/usr/bin/env bash

export CDN_DOMAIN=cdn.resys.io
export GITHUB_REPOSITORY_OWNER=dialob

if [ -z "$PUBLISHED_PACKAGES" ]; then
  for PROJECT in $(pnpm -r ls --json | jq -r '.[] | .path'); do
    echo $PROJECT
    ./dialob-build-parent/dist.sh $PROJECT $*
  done
else
  for PROJECT in `comm -12 <(echo $PUBLISHED_PACKAGES | jq -r '.[] | .name') <(pnpm m ls --json | jq -r '.[] | select(.private) | .name')`; do
    ./dialob-build-parent/dist.sh -r $(pnpm -r --filter=$PROJECT ls --json | jq -r '.[] | .path') $*
  done
fi



