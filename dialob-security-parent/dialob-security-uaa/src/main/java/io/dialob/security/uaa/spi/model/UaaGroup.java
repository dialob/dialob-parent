/*
 * Copyright © 2015 - 2021 ReSys (info@dialob.io)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.dialob.security.uaa.spi.model;

import java.util.List;

import org.immutables.value.Value;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;


@Value.Immutable
@Value.Enclosing
@JsonSerialize(as = ImmutableUaaGroup.class)
@JsonDeserialize(as = ImmutableUaaGroup.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public interface UaaGroup extends UaaEntity {

  @Value.Immutable
  @JsonSerialize(as = ImmutableUaaGroup.Member.class)
  @JsonDeserialize(as = ImmutableUaaGroup.Member.class)
  @JsonIgnoreProperties(ignoreUnknown = true)
  interface Member {

    @Nullable
    String getValue();

    @Nullable
    String getType();

    @Nullable
    String getOrigin();

  }

  String getDisplayName();

  @Nullable
  String getDescription();

  List<Member> getMembers();

  @Nullable
  String getZoneId();

  @Nullable
  UaaMeta getMeta();

}
