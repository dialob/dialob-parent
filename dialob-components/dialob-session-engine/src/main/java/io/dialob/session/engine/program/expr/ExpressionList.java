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
package io.dialob.session.engine.program.expr;

import edu.umd.cs.findbugs.annotations.NonNull;
import io.dialob.rule.parser.api.ValueType;
import io.dialob.session.engine.program.EvalContext;
import io.dialob.session.engine.program.model.Expression;
import io.dialob.session.engine.session.command.EventMatcher;
import org.immutables.value.Value;

import java.util.*;
import java.util.stream.Stream;

@Value.Immutable
public interface ExpressionList extends Expression {

  List<Expression> getExpressions();

  @Override
  default Object eval(@NonNull EvalContext evalContext) {
    return getExpressions().stream()
      .map(expression -> expression.eval(evalContext))
      .filter(Objects::nonNull)
      .flatMap(o -> {
        if (o instanceof Collection collection) {
          return collection.stream();
        }
        return Stream.of(o);
      })
      .filter(Objects::nonNull)
      .toList();
  }

  @NonNull
  @Override
  default ValueType getValueType() {
    return ValueType.arrayOf(ValueType.STRING);
  }

  @NonNull
  @Override
  default Set<EventMatcher> getEvalRequiredConditions() {
    var deps = new HashSet<EventMatcher>();
    getExpressions().forEach(arg -> deps.addAll(arg.getEvalRequiredConditions()));
    return Set.copyOf(deps);
  }

}
