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

SCRIPT_DIR=$( cd -- "$( dirname -- "${BASH_SOURCE[0]}" )" &> /dev/null && pwd )

run_build () {
	./mvnw -B clean deploy \
			-Prelease,jib \
			-Dmaven.javadoc.skip=false \
			-Djib.to.imagePath=$DOCKER_REGISTRY \
			-DbranchName=$GITHUB_REF_NAME
}

# No changes, skip release
readonly local last_release_commit_hash=$(git log --author="$BOT_NAME" --pretty=format:"%H" -1)
echo "Last commit:    ${last_release_commit_hash} by $BOT_NAME"
echo "Current commit: ${GITHUB_SHA}"
if [[ "${last_release_commit_hash}" = "${GITHUB_SHA}" ]]; then
     echo "No changes, skipping release"
     #exit 0
fi

# Config GIT
echo "Setup git user name to '$BOT_NAME' and email to '$BOT_EMAIL' GPG key ID $GPG_KEY_ID"
git config --global user.name "$BOT_NAME";
git config --global user.email "$BOT_EMAIL";

echo "Git checkout branch: '${GITHUB_REF_NAME}' commit: '${GITHUB_SHA}'"

# Current and next version
RELEASE_VERSION=$(cat $SCRIPT_DIR/next-release.version | xargs)
if [[ $RELEASE_VERSION =~ ([0-9]+)$ ]]; then
  MINOR_VERSION=${BASH_REMATCH[1]}
  echo "Releasing   : '${RELEASE_VERSION}'"
  MAJOR_VERSION=${RELEASE_VERSION:0:`expr ${#RELEASE_VERSION} - ${#MINOR_VERSION}`}
  NEW_MINOR_VERSION=`expr ${MINOR_VERSION} + 1`
  NEXT_RELEASE_VERSION=${MAJOR_VERSION}${NEW_MINOR_VERSION}
  echo "Next version: '${NEXT_RELEASE_VERSION}'"
else
  echo "Could not parse version : '$RELEASE_VERSION'"
  exit 1
fi

echo -n ${NEXT_RELEASE_VERSION} > $SCRIPT_DIR/next-release.version


PROJECT_VERSION=$(./mvnw -q -Dexec.executable=echo -Dexec.args='${project.version}' --non-recursive exec:exec)
echo "Dev version: '${PROJECT_VERSION}'"

./mvnw versions:set -DnewVersion=${RELEASE_VERSION}
git commit -am "Release ${RELEASE_VERSION}"
git tag -a ${RELEASE_VERSION} -m "release ${RELEASE_VERSION}"

run_build
 
git push origin ${RELEASE_VERSION}

echo "### Version ${RELEASE_VERSION} release build" >> $GITHUB_STEP_SUMMARY
