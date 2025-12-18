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
package io.dialob.api.form;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.dialob.api.annotation.ApiType;
import io.dialob.api.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import org.immutables.value.Value;

import java.util.Date;
import java.util.Objects;

@Value.Builder
@JsonDeserialize(builder = FormTag.Builder.class)
@JsonInclude(content = JsonInclude.Include.NON_NULL, value = JsonInclude.Include.NON_EMPTY)
@ApiType
@Value.Style(validationMethod = Value.Style.ValidationMethod.NONE)
public record FormTag(
  @NotNull
  @Getter
  String formName,

  @NotNull
  @Getter
  String name,

  @Nullable
  @Getter
  String refName,

  @Nullable
  @Getter
  Date created,

  @Nullable
  @Getter
  String formId,

  @Nullable
  @Getter
  String description,

  @Nullable
  @Getter
  String creator,

  @NotNull
  @Getter
  Type type

) implements FormEntity {

  public FormTag {
    type = Objects.requireNonNullElse(type, Type.NORMAL);
  }

  public static class Builder extends FormTagBuilder {
  }

  public enum Type {
    NORMAL,
    MUTABLE
  }

}
