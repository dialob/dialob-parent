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

import com.google.common.collect.ImmutableSet;
import edu.umd.cs.findbugs.annotations.NonNull;
import io.dialob.rule.parser.api.ValueType;
import io.dialob.session.engine.program.EvalContext;
import io.dialob.session.engine.program.model.Expression;
import io.dialob.session.engine.session.command.EventMatcher;
import io.dialob.session.engine.session.model.ItemState;
import io.dialob.session.model.ItemId;
import org.immutables.value.Value;

import java.util.Set;

import static io.dialob.session.engine.session.command.EventMatchers.whenDisabledUpdatedEvent;

@Value.Immutable
public interface IsDisabledOperator extends Expression {

  @Value.Parameter
  ItemId getItemId();

  @Override
  default Boolean eval(@NonNull EvalContext evalContext) {
    return evalContext.getItemState(this.getItemId()).map(ItemState::isDisabled).orElse(true);
  }

  @Override
  @NonNull
  default ValueType getValueType() {
    return ValueType.BOOLEAN;
  }

  @Override
  @NonNull
  default Set<EventMatcher> getEvalRequiredConditions() {
    return ImmutableSet.of(whenDisabledUpdatedEvent(getItemId()));
  }

}
