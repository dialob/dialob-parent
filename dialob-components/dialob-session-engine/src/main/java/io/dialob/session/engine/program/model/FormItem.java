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
package io.dialob.session.engine.program.model;

import edu.umd.cs.findbugs.annotations.NonNull;
import io.dialob.rule.parser.api.ValueType;
import io.dialob.session.engine.session.model.ItemId;
import org.immutables.value.Value;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Value.Builder
@Value.Style(jdkOnly = true, overshadowImplementation = true, visibility = Value.Style.ImplementationVisibility.PACKAGE)
public record FormItem(

  @Nullable String view,

  @Nullable Map<String, @org.checkerframework.checker.nullness.qual.Nullable Object> props,

  @Nullable
  Expression className,

  @Nullable
  Expression activeExpression,

  @Nullable
  Expression requiredExpression,

  @Nullable
  Expression disabledExpression,

  @Nullable
  Expression labelExpression,

  @Nullable
  Expression descriptionExpression,

  @NonNull
  List<Error> errors,

  @NonNull
  ItemId id,

  @NonNull
  String type,

  @Nullable
  ValueType valueType,

  @Nullable
  String valueSetId,

  @Nullable
  Object defaultValue,

  @Value.Default.Boolean(false)
  boolean isPrototype
) implements DisplayItem {

  @Override
  public Optional<Expression> classNameOptional() {
    return Optional.ofNullable(className);
  }

  @Override
  public Optional<Expression> activeExpressionOptional() {
    return Optional.ofNullable(activeExpression);
  }

  @Override
  public Optional<Expression> requiredExpressionOptional() {
    return Optional.ofNullable(requiredExpression);
  }

  @Override
  public Optional<Expression> disabledExpressionOptional() {
    return Optional.ofNullable(disabledExpression);
  }

  @Override
  public Optional<Expression> labelExpressionOptional() {
    return Optional.ofNullable(labelExpression);
  }

  @Override
  public Optional<Expression> descriptionExpressionOptional() {
    return Optional.ofNullable(descriptionExpression);
  }

  @Override
  public Optional<String> valueSetIdOptional() {
    return Optional.ofNullable(valueSetId);
  }

  @Override
  public Optional<Object> defaultValueOptional() {
    return Optional.ofNullable(defaultValue);
  }

  public static final class Builder extends FormItemBuilder {
  }

}
