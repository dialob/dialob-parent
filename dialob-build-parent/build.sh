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

#!/usr/bin/env bash
set -e

mvn clean install -Pdialob-release --settings dialob-build-parent/ci-maven-settings.xml -B -Dmaven.javadoc.skip=false -Dorg.slf4j.simpleLogger.log.org.apache.maven.cli.transfer.Slf4jMavenTransferListener=warn

readonly local RELEASE_VERSION=latest
readonly local DIALOB_BOOT_IMAGE=docker.io/resys/dialob-boot
readonly local DIALOB_SESSION_IMAGE=docker.io/resys/dialob-session-boot

docker image build -t ${DIALOB_BOOT_IMAGE}:${RELEASE_VERSION} --build-arg RELEASE_VERSION=${RELEASE_VERSION} dialob-boot/
docker image build -t ${DIALOB_SESSION_IMAGE}:${RELEASE_VERSION} --build-arg RELEASE_VERSION=${RELEASE_VERSION} dialob-session-boot/
docker push ${DIALOB_SESSION_IMAGE}:${RELEASE_VERSION}
docker push ${DIALOB_BOOT_IMAGE}:${RELEASE_VERSION}
