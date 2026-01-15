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
import edu.umd.cs.findbugs.annotations.Nullable;
import io.dialob.session.engine.session.model.ItemId;
import org.immutables.value.Value;

import java.util.Optional;

@Value.Builder
@Value.Style(jdkOnly = true, jdk9Collections = true, overshadowImplementation = true, visibility = Value.Style.ImplementationVisibility.PACKAGE)
public record Error(
  @Nullable
  String code,

  @NonNull
  ItemId itemId,

  @NonNull
  Expression validationExpression,

  @Nullable
  Expression disabledExpression,

  @Nullable
  Expression label,

  @Value.Default.Boolean(false)
  boolean isPrototype

) implements StructuralNode {

  public static final class Builder extends ErrorBuilder {
  }

  public Optional<Expression> disabledExpressionOptional() {
    return Optional.ofNullable(disabledExpression());
  }

}
