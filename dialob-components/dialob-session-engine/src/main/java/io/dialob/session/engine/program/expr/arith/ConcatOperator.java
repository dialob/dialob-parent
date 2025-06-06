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
import org.immutables.value.Value;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Value.Immutable
public interface ConcatOperator extends Expression {

  List<Expression> getExpressions();

  @Override
  default String eval(@NonNull EvalContext context) {
    return getExpressions().stream().map(expression -> (String) expression.eval(context)).collect(Collectors.joining());
  }

  @NonNull
  @Override
  default ValueType getValueType() {
    return ValueType.STRING;
  }

  @NonNull
  @Override
  default Set<EventMatcher> getEvalRequiredConditions() {
    return getExpressions().stream()
      .map(Expression::getEvalRequiredConditions)
      .flatMap(Collection::stream)
      .collect(Collectors.toUnmodifiableSet());
  }

}
