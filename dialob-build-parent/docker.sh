#!/usr/bin/env bash
#
# Copyright © 2015 - 2021 ReSys (info@dialob.io)
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

echo "Build and push docker images"

DIALOB_BOOT_IMAGE=docker.io/resys/dialob-boot
DIALOB_SESSION_IMAGE=docker.io/resys/dialob-session-boot
RELEASE_VERSION=$(cat dialob-build-parent/release.version)

echo " docker.io/resys/dialob-session-boot:${RELEASE_VERSION}"
echo " docker.io/resys/dialob-boot:${RELEASE_VERSION}"

mvn clean package

docker image build -t ${DIALOB_BOOT_IMAGE} --build-arg RELEASE_VERSION=${RELEASE_VERSION} dialob-boot/
docker image build -t ${DIALOB_SESSION_IMAGE} --build-arg RELEASE_VERSION=${RELEASE_VERSION} dialob-session-boot/

docker tag ${DIALOB_SESSION_IMAGE}:${RELEASE_VERSION} ${DIALOB_SESSION_IMAGE}:latest
docker tag ${DIALOB_BOOT_IMAGE}:${RELEASE_VERSION} ${DIALOB_BOOT_IMAGE}:latest
docker push ${DIALOB_SESSION_IMAGE}:latest
docker push ${DIALOB_BOOT_IMAGE}:latest

