/*
 * Copyright © 2015 - 2021 ReSys (info@dialob.io)
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
import io.dialob.session.model.IdUtils;
import io.dialob.session.model.ItemId;
import org.immutables.value.Value;

import java.math.BigInteger;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.toList;

@Value.Immutable
public interface CollectRowFieldsOperator extends Expression {

  @Value.Parameter
  ItemId getItemId();

  @Value.Parameter
  ValueType getType();

  @Override
  default Object eval(@NonNull EvalContext evalContext) {
    return getItemId().getParent().flatMap(ItemId::getParent)
      .map(rgId -> (List<BigInteger>) evalContext.getItemValue(rgId)).orElse(Collections.emptyList())
      .stream().map(rowNumber -> IdUtils.withIndex(getItemId(), rowNumber.intValue()))
    .map(evalContext::getItemValue).collect(toList());
  }

  @NonNull
  @Override
  default Set<EventMatcher> getEvalRequiredConditions() {
    Set<EventMatcher> matchers = new HashSet<>();
    getItemId().getParent()
      .flatMap(ItemId::getParent)
      .map(EventMatchers::whenValueUpdated)
      .ifPresent(matchers::add);

//    matchers.add(EventMatchers.whenItemAdded(getItemId()));
  //  getItemId().getParent().ifPresent(rowId -> matchers.add(EventMatchers.whenItemRemoved(rowId)));

    matchers.add(EventMatchers.whenValueUpdated(getItemId()));

    return Collections.unmodifiableSet(matchers);
  }

  @NonNull
  @Override
  default ValueType getValueType() {
    return ValueType.arrayOf(getType());
  }

}
