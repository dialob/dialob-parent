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
import io.dialob.session.engine.session.model.ErrorState;

import java.util.Set;

import static io.dialob.session.engine.session.command.EventMatchers.anyError;
import static io.dialob.session.engine.session.command.EventMatchers.errorActivity;

public record IsAnyInvalidAnswersOperator() implements Expression {

  private static final Expression INSTANCE = new IsAnyInvalidAnswersOperator();

  public static Expression instance() {
    return INSTANCE;
  }

  public static final Set<EventMatcher> ANY_ERROR = Set.of(errorActivity(anyError()));

  @Override
  public Boolean eval(@NonNull EvalContext context) {
    return context.getErrorStates().stream().anyMatch(ErrorState::isActive);
  }

  @NonNull
  @Override
  public ValueType getValueType() {
    return ValueType.BOOLEAN;
  }

  @NonNull
  @Override
  public Set<EventMatcher> getEvalRequiredConditions() {
    return ANY_ERROR;
  }

}
