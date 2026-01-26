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

import io.dialob.rule.parser.api.ValueType;
import io.dialob.session.engine.program.EvalContext;
import io.dialob.session.engine.program.model.Expression;
import io.dialob.session.engine.session.command.EventMatcher;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class ConditionalListOperatorTest {

  @Test
  void shouldReturnArrayOfStringType() {
    ConditionalListOperator<String> operator = new ConditionalListOperator.Builder<String>().build();
    assertEquals(ValueType.arrayOf(ValueType.STRING), operator.getValueType());
  }

  @Test
  void shouldReturnEvalRequiredConditions() {
    Expression expr1 = Mockito.mock(Expression.class);
    Expression expr2 = Mockito.mock(Expression.class);
    EventMatcher matcher1 = Mockito.mock(EventMatcher.class);
    EventMatcher matcher2 = Mockito.mock(EventMatcher.class);

    when(expr1.getEvalRequiredConditions()).thenReturn(Set.of(matcher1));
    when(expr2.getEvalRequiredConditions()).thenReturn(Set.of(matcher2));

    ConditionalListOperator<String> operator = new ConditionalListOperator.Builder<String>()
      .addItems(new Pair<>(expr1, "item1"))
      .addItems(new Pair<>(expr2, "item2"))
      .build();

    Set<EventMatcher> conditions = operator.getEvalRequiredConditions();
    assertEquals(Set.of(matcher1, matcher2), conditions);
  }

  @Test
  void shouldEvalToFilteredList() {
    Expression expr1 = Mockito.mock(Expression.class);
    Expression expr2 = Mockito.mock(Expression.class);
    Expression expr3 = Mockito.mock(Expression.class);
    EvalContext context = Mockito.mock(EvalContext.class);

    when(expr1.eval(context)).thenReturn(true);
    when(expr2.eval(context)).thenReturn(false);
    when(expr3.eval(context)).thenReturn(true);

    ConditionalListOperator<String> operator = new ConditionalListOperator.Builder<String>()
      .addItems(new Pair<>(expr1, "item1"))
      .addItems(new Pair<>(expr2, "item2"))
      .addItems(new Pair<>(expr3, "item3"))
      .build();

    List<String> result = (List<String>) operator.eval(context);
    assertEquals(List.of("item1", "item3"), result);
  }
}
