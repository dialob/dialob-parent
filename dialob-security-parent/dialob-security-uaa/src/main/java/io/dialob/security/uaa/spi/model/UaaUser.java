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

import java.time.OffsetDateTime;
import java.util.List;

import org.immutables.value.Value;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;



@Value.Immutable
@Value.Enclosing
@JsonSerialize(as = ImmutableUaaUser.class)
@JsonDeserialize(as = ImmutableUaaUser.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public interface UaaUser extends UaaEntity {

  @Value.Immutable
  @JsonSerialize(as = ImmutableUaaUser.Group.class)
  @JsonDeserialize(as = ImmutableUaaUser.Group.class)
  @JsonIgnoreProperties(ignoreUnknown = true)
  interface Group {

    @Nullable
    String getValue();

    @Nullable
    String getDisplay();

    @Nullable
    String getType();

  }

  @Value.Immutable
  @JsonSerialize(as = ImmutableUaaUser.Approval.class)
  @JsonDeserialize(as = ImmutableUaaUser.Approval.class)
  @JsonIgnoreProperties(ignoreUnknown = true)
  interface Approval {

    @Nullable
    String getUserId();

    @Nullable
    String getClientId();

    @Nullable
    String getScope();

    @Nullable
    String getStatus();

    @Nullable
    OffsetDateTime getLastUpdatedAt();

    @Nullable
    OffsetDateTime getExpiresAt();

  }

  @Value.Immutable
  @JsonSerialize(as = ImmutableUaaUser.Name.class)
  @JsonDeserialize(as = ImmutableUaaUser.Name.class)
  @JsonIgnoreProperties(ignoreUnknown = true)
  interface Name {

    @Nullable
    String getFamilyName();

    @Nullable
    String getGivenName();

  }

  @Value.Immutable
  @JsonSerialize(as = ImmutableUaaUser.Email.class)
  @JsonDeserialize(as = ImmutableUaaUser.Email.class)
  @JsonIgnoreProperties(ignoreUnknown = true)
  interface Email {

    @Nullable
    String getValue();

    @Nullable
    Boolean getPrimary();

  }


  @Nullable
  UaaMeta getMeta();

  @Nullable
  Name getName();

  List<Email> getEmails();

  @Nullable
  String getExternalId();

  @Nullable
  String getUserName();

  List<Group> getGroups();

  List<Approval> getApprovals();

  @Nullable
  Boolean getActive();

  @Nullable
  Boolean getVerified();

  @Nullable
  String getOrigin();

  @Nullable
  String getZoneId();

  @Nullable
  OffsetDateTime getPasswordLastModified();

  @Nullable
  Long getPreviousLogonTime();

  @Nullable
  Long getLastLogonTime();

}
