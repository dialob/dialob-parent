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
package io.dialob.session.engine.program.expr.arith;

import edu.umd.cs.findbugs.annotations.NonNull;
import io.dialob.rule.parser.api.ValueType;
import io.dialob.session.engine.program.EvalContext;
import io.dialob.session.engine.program.model.Expression;
import io.dialob.session.engine.session.command.EventMatcher;
import io.dialob.session.engine.session.model.ItemId;
import org.immutables.value.Value;

import java.util.Set;

import static io.dialob.session.engine.session.command.EventMatchers.whenValueUpdated;

@Value.Builder
@Value.Style(
  jakarta = true,
  jdkOnly = true,
  overshadowImplementation = true,
  visibility = Value.Style.ImplementationVisibility.PACKAGE
)
public record VariableReference<T>(
  @NonNull
  ItemId itemId,
  ValueType valueType
) implements Expression {

  public static Expression of(ItemId itemId, ValueType valueType) {
    return new Builder<>().itemId(itemId).valueType(valueType).build();
  }

  public static final class Builder<T> extends VariableReferenceBuilder<T> {}


  @NonNull
  @Override
  public ValueType getValueType() {
    return valueType;
  }

  @NonNull
  @Override
  public Set<EventMatcher> getEvalRequiredConditions() {
    return Set.of(whenValueUpdated(itemId));
  }

  @Override
  public T eval(@NonNull EvalContext evalContext) {
    return (T) evalContext.getItemValue(itemId);
  }

}
