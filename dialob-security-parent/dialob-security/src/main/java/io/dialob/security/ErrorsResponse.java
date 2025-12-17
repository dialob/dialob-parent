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
package io.dialob.security;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import edu.umd.cs.findbugs.annotations.Nullable;
import org.immutables.value.Value;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

@Value.Builder
@JsonSerialize(as = ErrorsResponse.class)
@JsonDeserialize(builder = ErrorsResponse.Builder.class)
@JsonInclude(content = JsonInclude.Include.NON_NULL, value = JsonInclude.Include.NON_EMPTY)
public record ErrorsResponse(
  @Nullable Instant timestamp,
  @Nullable Integer status,
  @Nullable String error,
  @Nullable String message
) implements Serializable {

  public ErrorsResponse {
    timestamp = Objects.requireNonNullElseGet(timestamp, Instant::now);
  }

  public static class Builder extends ErrorsResponseBuilder {
  }

}
