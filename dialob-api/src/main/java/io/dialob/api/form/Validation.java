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
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.dialob.api.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import org.immutables.gson.Gson;
import org.immutables.value.Value;

import java.io.Serializable;
import java.util.Map;

@Value.Immutable
@JsonSerialize(as = ImmutableValidation.class)
@JsonDeserialize(builder = Validation.Builder.class)
@Gson.TypeAdapters
@JsonInclude(content = JsonInclude.Include.NON_NULL, value = JsonInclude.Include.NON_EMPTY)
@Value.Style(validationMethod = Value.Style.ValidationMethod.NONE, jdkOnly = true, overshadowImplementation = true, visibility = Value.Style.ImplementationVisibility.PACKAGE)
public interface Validation extends Serializable {

  class Builder extends ImmutableValidation.Builder { }

  @NotNull
  @JsonSetter(nulls = Nulls.AS_EMPTY)
  Map<String, String> getMessage();

  @Nullable
  String getRule();

}
