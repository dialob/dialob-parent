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

import io.dialob.rule.parser.api.ObjectValueType;
import io.dialob.rule.parser.api.ValueType;
import io.dialob.session.engine.program.EvalContext;
import io.dialob.session.engine.program.model.Expression;
import io.dialob.session.engine.session.command.EventMatcher;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

class KeyValueOperatorTest {

  @Test
  void shouldEvaluateToMapWithKeyAndValue() {
    Expression valueExpr = Mockito.mock(Expression.class);
    EvalContext context = Mockito.mock(EvalContext.class);

    when(valueExpr.eval(context)).thenReturn("testValue");

    KeyValueOperator operator = new KeyValueOperator("myKey", valueExpr);
    Object result = operator.eval(context);

    assertTrue(result instanceof Map);
    Map<?, ?> resultMap = (Map<?, ?>) result;
    assertEquals(1, resultMap.size());
    assertEquals("testValue", resultMap.get("myKey"));
  }

  @Test
  void shouldEvaluateWithDifferentValueTypes() {
    Expression valueExpr = Mockito.mock(Expression.class);
    EvalContext context = Mockito.mock(EvalContext.class);

    // Test with Integer value
    when(valueExpr.eval(context)).thenReturn(42);
    KeyValueOperator operator = new KeyValueOperator("numKey", valueExpr);
    Map<?, ?> resultMap = (Map<?, ?>) operator.eval(context);
    assertEquals(42, resultMap.get("numKey"));

    // Test with Boolean value
    when(valueExpr.eval(context)).thenReturn(true);
    resultMap = (Map<?, ?>) operator.eval(context);
    assertEquals(true, resultMap.get("numKey"));

    // Test with List value
    when(valueExpr.eval(context)).thenReturn(java.util.List.of("a", "b", "c"));
    resultMap = (Map<?, ?>) operator.eval(context);
    assertEquals(java.util.List.of("a", "b", "c"), resultMap.get("numKey"));
  }

  @Test
  void shouldReturnObjectValueType() {
    Expression valueExpr = Mockito.mock(Expression.class);
    KeyValueOperator operator = new KeyValueOperator("key", valueExpr);

    ValueType valueType = operator.getValueType();

    assertTrue(valueType instanceof ObjectValueType);
    assertEquals(ObjectValueType.objectOf(Collections.emptyMap()), valueType);
  }

  @Test
  void shouldDelegateEvalRequiredConditionsToValue() {
    Expression valueExpr = Mockito.mock(Expression.class);
    EventMatcher matcher1 = Mockito.mock(EventMatcher.class);
    EventMatcher matcher2 = Mockito.mock(EventMatcher.class);
    Set<EventMatcher> expectedMatchers = Set.of(matcher1, matcher2);

    when(valueExpr.getEvalRequiredConditions()).thenReturn(expectedMatchers);

    KeyValueOperator operator = new KeyValueOperator("key", valueExpr);
    Set<EventMatcher> actualMatchers = operator.getEvalRequiredConditions();

    assertEquals(expectedMatchers, actualMatchers);
  }

  @Test
  void shouldReturnEmptySetWhenValueHasNoRequiredConditions() {
    Expression valueExpr = Mockito.mock(Expression.class);
    when(valueExpr.getEvalRequiredConditions()).thenReturn(Collections.emptySet());

    KeyValueOperator operator = new KeyValueOperator("key", valueExpr);
    Set<EventMatcher> actualMatchers = operator.getEvalRequiredConditions();

    assertTrue(actualMatchers.isEmpty());
  }

}
