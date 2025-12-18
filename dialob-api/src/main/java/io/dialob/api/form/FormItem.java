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

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.dialob.api.annotation.AllowNulls;
import io.dialob.api.annotation.ApiType;
import io.dialob.api.annotation.Nullable;
import io.dialob.api.rest.HasId;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import org.immutables.value.Value;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Value.Builder
@JsonDeserialize(builder = FormItem.Builder.class)
@JsonIgnoreProperties({"style", "options"})
@JsonInclude(content = JsonInclude.Include.NON_NULL, value = JsonInclude.Include.NON_EMPTY)
@ApiType
@Value.Style(validationMethod = Value.Style.ValidationMethod.NONE)
public record FormItem(

  @NotNull
  String id,

  @NotNull
  @Getter
  String type,

  @Nullable
  @Getter
  String view,

  @NotNull
  @JsonSetter(nulls = Nulls.AS_EMPTY)
  @Getter
  Map<String, String> label,

  @NotNull
  @JsonSetter(nulls = Nulls.AS_EMPTY)
  @Getter
  Map<String, String> description,

  @Nullable
  @Getter
  String required,

  @NotNull
  @JsonSetter(nulls = Nulls.AS_EMPTY)
  @Getter
  Map<String, String> requiredErrorText,

  @Nullable
  @Getter
  Boolean readOnly,

  @NotNull
  @JsonSetter(nulls = Nulls.AS_EMPTY)
  @Getter
  List<String> items,

  @NotNull
  @JsonSetter(nulls = Nulls.AS_EMPTY)
  @Getter
  List<String> className,

  @Nullable
  @Getter
  String activeWhen,

  @Nullable
  @Getter
  String canAddRowWhen,

  @Nullable
  @Getter
  String canRemoveRowWhen,

  @NotNull
  @JsonSetter(nulls = Nulls.AS_EMPTY)
  @Getter
  List<Validation> validations,

  @Nullable
  @Getter
  String valueSetId,

  @Nullable
  @Getter
  Object defaultValue,

  @Nullable @AllowNulls
  @Getter
  Map<String, Object> props,

  @JsonInclude
  @JsonAnyGetter
  @AllowNulls
  @Getter
  Map<String, Object> additionalProperties

) implements HasId<String>, Serializable {

  public static class Builder extends FormItemBuilder {
  }


}
