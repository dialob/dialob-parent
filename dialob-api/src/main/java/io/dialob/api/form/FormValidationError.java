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
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.gson.Gson;
import org.immutables.value.Value;

import java.io.Serializable;
import java.util.Optional;

@Value.Immutable
@JsonSerialize(as = ImmutableFormValidationError.class)
@JsonDeserialize(as = ImmutableFormValidationError.class)
@Gson.TypeAdapters
@JsonInclude(content = JsonInclude.Include.NON_NULL, value = JsonInclude.Include.NON_EMPTY)
@Value.Style(validationMethod = Value.Style.ValidationMethod.NONE, jdkOnly = true)
public interface FormValidationError extends Serializable {

  enum Level {
    INFO,
    WARNING,
    ERROR,
    FATAL
  }

  enum Type {
    VISIBILITY,
    VALIDATION,
    REQUIREMENT,
    VARIABLE,
    GENERAL,
    CLASSNAME,
    @Deprecated // Unused
    VALUE_ENTRY,
    VALUESET,
    VALUESET_ENTRY,
    CANADDROW,
    CANREMOVEROW
  }

  String getItemId();

  String getMessage();

  @Value.Default
  default Level getLevel() {
    return Level.ERROR;
  }

  Type getType();

  Optional<String> getExpression();

  Optional<Integer> getStartIndex();

  Optional<Integer> getEndIndex();

  Optional<Integer> getIndex();

}
