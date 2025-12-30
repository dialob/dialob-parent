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

import static io.dialob.session.engine.session.command.EventMatchers.whenActiveUpdated;
import static io.dialob.session.engine.session.command.EventMatchers.whenAnsweredUpdated;

@Value.Builder
@Value.Style(
  jakarta = true,
  jdkOnly = true,
  overshadowImplementation = true,
  visibility = Value.Style.ImplementationVisibility.PACKAGE
)
public record IsAnsweredOperator(
  ItemId questionId
) implements Expression {

  public static final class Builder extends IsAnsweredOperatorBuilder {
  }

  @Override
  public Boolean eval(@NonNull EvalContext evalContext) {
    ItemId itemId = evalContext.mapTo(questionId(), false);
    return evalContext.getItemState(itemId).map(itemState -> itemState.isAnswered() && itemState.isActive()).orElse(false);
  }

  @NonNull
  @Override
  public ValueType getValueType() {
    return ValueType.BOOLEAN;
  }

  @NonNull
  @Override
  public Set<EventMatcher> getEvalRequiredConditions() {
    return Set.of(
      whenAnsweredUpdated(questionId()),
      whenActiveUpdated(questionId())
    );
  }
}
