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

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class ObjectOperatorTest {

  @Test
  void shouldEvaluateToEmptyMapWhenNoFields() {
    ObjectOperator operator = new ObjectOperator(Collections.emptyList());
    EvalContext context = Mockito.mock(EvalContext.class);

    Object result = operator.eval(context);

    assertTrue(result instanceof Map);
    Map<?, ?> resultMap = (Map<?, ?>) result;
    assertTrue(resultMap.isEmpty());
  }

  @Test
  void shouldEvaluateToMapWithSingleField() {
    Expression field = Mockito.mock(Expression.class);
    EvalContext context = Mockito.mock(EvalContext.class);

    when(field.eval(context)).thenReturn(Map.of("key1", "value1"));

    ObjectOperator operator = new ObjectOperator(List.of(field));
    Map<?, ?> resultMap = (Map<?, ?>) operator.eval(context);

    assertEquals(1, resultMap.size());
    assertEquals("value1", resultMap.get("key1"));
  }

  @Test
  void shouldMergeMultipleFieldsIntoSingleMap() {
    Expression field1 = Mockito.mock(Expression.class);
    Expression field2 = Mockito.mock(Expression.class);
    Expression field3 = Mockito.mock(Expression.class);
    EvalContext context = Mockito.mock(EvalContext.class);

    when(field1.eval(context)).thenReturn(Map.of("key1", "value1"));
    when(field2.eval(context)).thenReturn(Map.of("key2", 42));
    when(field3.eval(context)).thenReturn(Map.of("key3", true));

    ObjectOperator operator = new ObjectOperator(List.of(field1, field2, field3));
    Map<?, ?> resultMap = (Map<?, ?>) operator.eval(context);

    assertEquals(3, resultMap.size());
    assertEquals("value1", resultMap.get("key1"));
    assertEquals(42, resultMap.get("key2"));
    assertEquals(true, resultMap.get("key3"));
  }

  @Test
  void shouldHandleFieldsWithMultipleEntries() {
    Expression field1 = Mockito.mock(Expression.class);
    Expression field2 = Mockito.mock(Expression.class);
    EvalContext context = Mockito.mock(EvalContext.class);

    Map<String, Object> map1 = new HashMap<>();
    map1.put("key1", "value1");
    map1.put("key2", "value2");

    Map<String, Object> map2 = new HashMap<>();
    map2.put("key3", "value3");
    map2.put("key4", "value4");

    when(field1.eval(context)).thenReturn(map1);
    when(field2.eval(context)).thenReturn(map2);

    ObjectOperator operator = new ObjectOperator(List.of(field1, field2));
    Map<?, ?> resultMap = (Map<?, ?>) operator.eval(context);

    assertEquals(4, resultMap.size());
    assertEquals("value1", resultMap.get("key1"));
    assertEquals("value2", resultMap.get("key2"));
    assertEquals("value3", resultMap.get("key3"));
    assertEquals("value4", resultMap.get("key4"));
  }

  @Test
  void shouldOverwriteWithLaterFieldsWhenKeysCollide() {
    Expression field1 = Mockito.mock(Expression.class);
    Expression field2 = Mockito.mock(Expression.class);
    EvalContext context = Mockito.mock(EvalContext.class);

    when(field1.eval(context)).thenReturn(Map.of("key", "firstValue"));
    when(field2.eval(context)).thenReturn(Map.of("key", "secondValue"));

    ObjectOperator operator = new ObjectOperator(List.of(field1, field2));
    Map<?, ?> resultMap = (Map<?, ?>) operator.eval(context);

    assertEquals(1, resultMap.size());
    assertEquals("secondValue", resultMap.get("key"));
  }

  @Test
  void shouldIgnoreNonMapResults() {
    Expression field1 = Mockito.mock(Expression.class);
    Expression field2 = Mockito.mock(Expression.class);
    Expression field3 = Mockito.mock(Expression.class);
    Expression field4 = Mockito.mock(Expression.class);
    EvalContext context = Mockito.mock(EvalContext.class);

    when(field1.eval(context)).thenReturn(Map.of("key1", "value1"));
    when(field2.eval(context)).thenReturn("not a map");
    when(field3.eval(context)).thenReturn(42);
    when(field4.eval(context)).thenReturn(Map.of("key2", "value2"));

    ObjectOperator operator = new ObjectOperator(List.of(field1, field2, field3, field4));
    Map<?, ?> resultMap = (Map<?, ?>) operator.eval(context);

    assertEquals(2, resultMap.size());
    assertEquals("value1", resultMap.get("key1"));
    assertEquals("value2", resultMap.get("key2"));
  }

  @Test
  void shouldHandleNullFieldResults() {
    Expression field1 = Mockito.mock(Expression.class);
    Expression field2 = Mockito.mock(Expression.class);
    EvalContext context = Mockito.mock(EvalContext.class);

    when(field1.eval(context)).thenReturn(Map.of("key1", "value1"));
    when(field2.eval(context)).thenReturn(null);

    ObjectOperator operator = new ObjectOperator(List.of(field1, field2));
    Map<?, ?> resultMap = (Map<?, ?>) operator.eval(context);

    assertEquals(1, resultMap.size());
    assertEquals("value1", resultMap.get("key1"));
  }

  @Test
  void shouldReturnObjectValueType() {
    Expression field = Mockito.mock(Expression.class);
    ObjectOperator operator = new ObjectOperator(List.of(field));

    ValueType valueType = operator.getValueType();

    assertTrue(valueType instanceof ObjectValueType);
    assertEquals(ObjectValueType.objectOf(Collections.emptyMap()), valueType);
  }

  @Test
  void shouldAggregateEvalRequiredConditionsFromAllFields() {
    Expression field1 = Mockito.mock(Expression.class);
    Expression field2 = Mockito.mock(Expression.class);
    Expression field3 = Mockito.mock(Expression.class);

    EventMatcher matcher1 = Mockito.mock(EventMatcher.class);
    EventMatcher matcher2 = Mockito.mock(EventMatcher.class);
    EventMatcher matcher3 = Mockito.mock(EventMatcher.class);

    when(field1.getEvalRequiredConditions()).thenReturn(Set.of(matcher1));
    when(field2.getEvalRequiredConditions()).thenReturn(Set.of(matcher2, matcher3));
    when(field3.getEvalRequiredConditions()).thenReturn(Collections.emptySet());

    ObjectOperator operator = new ObjectOperator(List.of(field1, field2, field3));
    Set<EventMatcher> actualMatchers = operator.getEvalRequiredConditions();

    assertEquals(3, actualMatchers.size());
    assertTrue(actualMatchers.contains(matcher1));
    assertTrue(actualMatchers.contains(matcher2));
    assertTrue(actualMatchers.contains(matcher3));
  }

  @Test
  void shouldReturnEmptySetWhenNoFieldsHaveRequiredConditions() {
    Expression field1 = Mockito.mock(Expression.class);
    Expression field2 = Mockito.mock(Expression.class);

    when(field1.getEvalRequiredConditions()).thenReturn(Collections.emptySet());
    when(field2.getEvalRequiredConditions()).thenReturn(Collections.emptySet());

    ObjectOperator operator = new ObjectOperator(List.of(field1, field2));
    Set<EventMatcher> actualMatchers = operator.getEvalRequiredConditions();

    assertTrue(actualMatchers.isEmpty());
  }

  @Test
  void shouldReturnEmptySetWhenNoFields() {
    ObjectOperator operator = new ObjectOperator(Collections.emptyList());
    Set<EventMatcher> actualMatchers = operator.getEvalRequiredConditions();

    assertTrue(actualMatchers.isEmpty());
  }

  @Test
  void shouldDeduplicateEventMatchersFromMultipleFields() {
    Expression field1 = Mockito.mock(Expression.class);
    Expression field2 = Mockito.mock(Expression.class);

    EventMatcher matcher1 = Mockito.mock(EventMatcher.class);
    EventMatcher matcher2 = Mockito.mock(EventMatcher.class);

    // Both fields return the same matchers
    when(field1.getEvalRequiredConditions()).thenReturn(Set.of(matcher1, matcher2));
    when(field2.getEvalRequiredConditions()).thenReturn(Set.of(matcher1, matcher2));

    ObjectOperator operator = new ObjectOperator(List.of(field1, field2));
    Set<EventMatcher> actualMatchers = operator.getEvalRequiredConditions();

    // Should deduplicate to only 2 unique matchers
    assertEquals(2, actualMatchers.size());
    assertTrue(actualMatchers.contains(matcher1));
    assertTrue(actualMatchers.contains(matcher2));
  }

}
