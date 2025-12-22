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
import io.dialob.session.engine.session.command.EventMatchers;
import io.dialob.session.engine.session.model.IdUtils;
import io.dialob.session.engine.session.model.ItemId;
import org.immutables.value.Value;

import java.math.BigInteger;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Value.Builder
@Value.Style(
  jakarta = true,
  jdkOnly = true,
  overshadowImplementation = true,
  visibility = Value.Style.ImplementationVisibility.PACKAGE
)
public record CollectRowFieldsOperator(
  ItemId itemId,
  ValueType type
) implements Expression {

  public static CollectRowFieldsOperator of(ItemId itemId, ValueType valueType) {
    return new CollectRowFieldsOperator.Builder()
      .itemId(itemId)
      .type(valueType)
      .build();
  }

  public static final class Builder extends CollectRowFieldsOperatorBuilder {}

  public ItemId getItemId() {
    return itemId();
  }

  public ValueType getType() {
    return type();
  }

  @Override
  public Object eval(@NonNull EvalContext evalContext) {
    return getItemId().getParent().flatMap(ItemId::getParent)
      .map(rgId -> (List<BigInteger>) evalContext.getItemValue(rgId)).orElse(Collections.emptyList())
      .stream().map(rowNumber -> IdUtils.withIndex(getItemId(), rowNumber.intValue()))
    .map(evalContext::getItemValue).toList();
  }

  @NonNull
  @Override
  public Set<EventMatcher> getEvalRequiredConditions() {
    Set<EventMatcher> matchers = new HashSet<>();
    getItemId().getParent()
      .flatMap(ItemId::getParent)
      .map(EventMatchers::whenValueUpdated)
      .ifPresent(matchers::add);

    matchers.add(EventMatchers.whenValueUpdated(getItemId()));

    return Collections.unmodifiableSet(matchers);
  }

  @NonNull
  @Override
  public ValueType getValueType() {
    return ValueType.arrayOf(getType());
  }

}
