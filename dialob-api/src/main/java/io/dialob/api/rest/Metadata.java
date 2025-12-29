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
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import org.immutables.value.Value;

import java.io.Serializable;
import java.time.Instant;

@Value.Builder
@JsonDeserialize(builder = Metadata.Builder.class)
@JsonInclude(content = JsonInclude.Include.NON_NULL)
@ApiType
public record Metadata(

  @Nullable
  @Getter
  String description,

  @NotNull
  @Size(min = 1)
  @Getter
  String label,

  @Nullable
  @Getter
  Instant created,

  @Nullable
  @Getter
  Instant lastSaved,

  @Nullable
  @Getter
  Boolean valid

) implements Serializable {

  public static class Builder extends MetadataBuilder {
  }


}
