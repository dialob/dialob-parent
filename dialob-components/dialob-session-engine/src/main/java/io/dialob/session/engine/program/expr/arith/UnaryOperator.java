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
import io.dialob.session.engine.program.EvalContext;
import io.dialob.session.engine.program.model.Expression;
import io.dialob.session.engine.session.command.EventMatcher;
import org.immutables.value.Value;

import java.util.Set;

public interface UnaryOperator extends Expression {

  @Value.Parameter
  Expression getExpression();

  @Override
  default Object eval(@NonNull EvalContext evalContext) {
    Object value = getExpression().eval(evalContext);
    return value == null ? null : apply(value);
  }

  @NonNull
  @Override
  default Set<EventMatcher> getEvalRequiredConditions() {
    return getExpression().getEvalRequiredConditions();
  }

  Object apply(@Nonnull Object value);
}
