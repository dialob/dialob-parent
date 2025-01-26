#!/usr/bin/env bash
#
# Copyright © 2015 - 2025 ReSys (info@dialob.io)
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

set -e

OPTS=""

if [[ ! -z "$DOCKER_REGISTRY" ]]; then
	OPTS="$OPTS -Djib.to.imagePath=$DOCKER_REGISTRY" 
	PROFILES="$PROFILES jib"
fi

if [[ ! -z "$GITHUB_REF_NAME" ]]; then
	OPTS="$OPTS -DbranchName=$GITHUB_REF_NAME"
else
	OPTS="$OPTS -DbranchName=$(git rev-parse --abbrev-ref HEAD)"
fi

if [[ ! -z "$PROFILES" ]]; then
	PROFILES=-P"$(echo $PROFILES | xargs | tr ' ' ',')"
fi

./mvnw -B clean install $PROFILES \
    -Dmaven.javadoc.skip=false \
    $OPTS
