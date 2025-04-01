#!/usr/bin/env bash

for PROJECT in `comm -12 <(echo $PUBLISHED_PACKAGES | jq -r '.[] | .name') <(pnpm m ls --json | jq -r '.[] | select(.private) | .name')`; do
  pnpm -r --filter=$PROJECT run dist
done
