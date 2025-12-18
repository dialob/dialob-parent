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
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import org.immutables.value.Value;

import java.io.Serializable;

@Value.Builder
@JsonDeserialize(builder = Variable.Builder.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Value.Style(validationMethod = Value.Style.ValidationMethod.NONE, jakarta = true, jdkOnly = true, overshadowImplementation = true, visibility = Value.Style.ImplementationVisibility.PACKAGE)
public record Variable(

  @NotNull
  @Getter
  String name,

  @Nullable
  @Getter
  String expression,

  @Nullable
  @Getter
  Object defaultValue,

  /**
   * @return true when this is context variable
   */
  @Nullable
  @Getter
  Boolean context,

  /**
   * @return true when context variable can be published and sent to client.
   */
  @Nullable
  @Getter
  Boolean published,

  @Nullable
  @Getter
  String contextType,

  @Nullable
  @Getter
  String description

) implements Serializable {

  public static Variable of(String name, String expression) {
    return new Variable.Builder().name(name).expression(expression).build();
  }

  public static class Builder extends VariableBuilder {
  }

}
