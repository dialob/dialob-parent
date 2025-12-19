/*
 * Copyright © 2015 - 2025 ReSys (info@dialob.io)
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
package io.dialob.security.key;

import edu.umd.cs.findbugs.annotations.Nullable;
import org.immutables.value.Value;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Optional;

@Value.Builder
public record ApiKey(
  String clientId,
  @Nullable String token,
  @Nullable String hash,
  @Nullable String tenantId,
  @Nullable String owner,
  @Nullable LocalDateTime created,
  @Nullable LocalDateTime startDateTime,
  @Nullable LocalDateTime endDateTime
) implements Serializable {

  public static class Builder extends ApiKeyBuilder {}

  public static ApiKey of(String clientId) {
    return new Builder().clientId(clientId).build();
  }

  public ApiKey withToken(String token) {
    return new Builder().from(this).token(token).build();
  }

  public ApiKey withHash(String hash) {
    return new Builder().from(this).hash(hash).build();
  }

  public String getClientId() {
    return clientId;
  }

  public Optional<String> getToken() {
    return Optional.ofNullable(token);
  }

  public Optional<String> getHash() {
    return Optional.ofNullable(hash);
  }

  public boolean isValid() {
    return getHash().isPresent() && getToken().isEmpty();
  }

  public Optional<String> getTenantId() {
    return Optional.ofNullable(tenantId);
  }

  public Optional<String> getOwner() {
    return Optional.ofNullable(owner);
  }

  public Optional<LocalDateTime> getCreated() {
    return Optional.ofNullable(created);
  }

  public Optional<LocalDateTime> getStartDateTime() {
    return Optional.ofNullable(startDateTime);
  }

  public Optional<LocalDateTime> getEndDateTime() {
    return Optional.ofNullable(endDateTime);
  }

}
