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
import io.dialob.rule.parser.api.ObjectValueType;
import io.dialob.rule.parser.api.ValueType;
import io.dialob.session.engine.program.EvalContext;
import io.dialob.session.engine.program.model.Expression;
import io.dialob.session.engine.session.command.EventMatcher;

import java.util.*;
import java.util.stream.Collectors;

public record ObjectOperator(Collection<Expression> fields) implements Expression {

  @NonNull
  @Override
  public ValueType getValueType() {
    return ObjectValueType.objectOf(Collections.emptyMap());
  }

  @Override
  public Object eval(@NonNull EvalContext evalContext) {
    Map<String, Object> map = new HashMap<>();
    for (var entry : fields) {
      var result = entry.eval(evalContext);
      if (result instanceof Map<?, ?> resultMap) {
        map.putAll((Map<String, Object>) resultMap);
      }
    }
    return map;
  }

  @NonNull
  @Override
  public Set<EventMatcher> getEvalRequiredConditions() {
    return fields.stream()
      .map(Expression::getEvalRequiredConditions)
      .flatMap(Set::stream)
      .collect(Collectors.toSet());
  }

}
