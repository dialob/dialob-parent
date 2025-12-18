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
package io.dialob.api.rest;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.dialob.api.annotation.ApiType;
import io.dialob.api.annotation.Nullable;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import org.immutables.value.Value;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;
import java.util.Objects;

@Value.Builder
@JsonDeserialize(builder = Errors.Builder.class)
@JsonInclude(content = JsonInclude.Include.NON_NULL, value = JsonInclude.Include.NON_EMPTY)
@ApiType
public record Errors(
  @Schema(description = "error timestamp")
  @Nullable
  @Getter
  Instant timestamp,

  @Schema(description = "HTTP status code", example = "403")
  @Nullable
  @Getter
  Integer status,

  @Nullable
  @Getter
  String error,

  @Nullable
  @Getter
  String message,

  @Schema(description = "Java stacktrace, if server is configured to send one.")
  @Nullable
  @Getter
  String trace,

  @Nullable
  @Getter
  String path,

  @Schema(description = "List of identified errors in entity")
  @Nullable
  @Getter
  List<Error> errors

) implements Serializable {

  public Errors {
    timestamp = Objects.requireNonNullElseGet(timestamp, Instant::now);
  }

  public static class Builder extends ErrorsBuilder {
  }

  @JsonInclude(JsonInclude.Include.NON_NULL)
  @Value.Builder

  @JsonDeserialize(builder = Errors.Error.Builder.class)
  @ApiType
  public record Error(

    @Schema(description = "Error classifying code")
    @Nullable
    @Getter
    String code,

    @Schema(description = "Javascript path notation to entity attribute")
    @Nullable
    @Getter
    String context,

    @Schema(description = "Invalid value on entity attribute")
    @Nullable
    @Getter
    Object rejectedValue,

    @Schema(description = "Error description")
    @Nullable
    @Getter
    String error

  ) implements Serializable {

    public static class Builder extends ErrorBuilder {
    }

  }
}
