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

FROM amazoncorretto:17
ARG RELEASE_VERSION
ENV BASH_ENV /etc/profile
RUN echo "java:x:2000:2000:Java:/java:/sbin/nologin:" >> /etc/passwd && \
    echo "java:x:2000:" >> /etc/group && \
    mkdir -p /java/agents /java/classpath
RUN chown -R java:java /java
WORKDIR /java
USER java
ADD target/dialob-session-boot-$RELEASE_VERSION.jar /java/dialob-session-boot.jar
ENTRYPOINT ["java", "-jar", "dialob-session-boot.jar"]
