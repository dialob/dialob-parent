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
import io.dialob.api.annotation.Nullable;
import lombok.Getter;
import org.immutables.value.Value;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;

@Value.Builder
@JsonDeserialize(builder = FormValidationError.Builder.class)
@JsonInclude(content = JsonInclude.Include.NON_NULL, value = JsonInclude.Include.NON_EMPTY)
@Value.Style(validationMethod = Value.Style.ValidationMethod.NONE, jdkOnly = true, overshadowImplementation = true, visibility = Value.Style.ImplementationVisibility.PACKAGE)
public record FormValidationError(

  @Getter
  String itemId,

  @Getter
  String message,

  @Getter
  Level level,

  @Getter
  Type type,

  @Nullable
  String expression,

  @Nullable
  Integer startIndex,

  @Nullable
  Integer endIndex,

  @Nullable
  Integer index

) implements Serializable {

  public FormValidationError {
    level = Objects.requireNonNullElse(level, Level.ERROR);
  }

  public Optional<String> getExpression() {
    return Optional.ofNullable(expression);
  }

  public Optional<Integer> getStartIndex() {
    return Optional.ofNullable(startIndex);
  }

  public Optional<Integer> getEndIndex() {
    return Optional.ofNullable(endIndex);
  }

  public Optional<Integer> getIndex() {
    return Optional.ofNullable(index);
  }


  public static class Builder extends FormValidationErrorBuilder {
  }

  public enum Level {
    INFO,
    WARNING,
    ERROR,
    FATAL
  }

  public enum Type {
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

}
