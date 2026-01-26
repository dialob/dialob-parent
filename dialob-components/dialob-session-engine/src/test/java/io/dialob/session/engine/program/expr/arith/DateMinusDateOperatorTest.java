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
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDate;
import java.time.Period;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class DateMinusDateOperatorTest {

  @Test
  void shouldReturnPeriodType() {
    Expression lhs = Mockito.mock(Expression.class);
    Expression rhs = Mockito.mock(Expression.class);
    DateMinusDateOperator operator = new DateMinusDateOperator.Builder().lhs(lhs).rhs(rhs).build();
    assertEquals(ValueType.PERIOD, operator.getValueType());
  }

  @Test
  void shouldCalculatePeriodBetweenDates() {
    Expression lhs = Mockito.mock(Expression.class);
    Expression rhs = Mockito.mock(Expression.class);
    DateMinusDateOperator operator = new DateMinusDateOperator.Builder().lhs(lhs).rhs(rhs).build();
    EvalContext context = Mockito.mock(EvalContext.class);

    LocalDate date1 = LocalDate.of(2023, 10, 27);
    LocalDate date2 = LocalDate.of(2023, 10, 20);

    when(lhs.eval(context)).thenReturn(date1);
    when(rhs.eval(context)).thenReturn(date2);

    assertEquals(Period.ofDays(7), operator.eval(context));
  }

  @Test
  void shouldReturnNullIfLhsIsNull() {
    Expression lhs = Mockito.mock(Expression.class);
    Expression rhs = Mockito.mock(Expression.class);
    DateMinusDateOperator operator = new DateMinusDateOperator.Builder().lhs(lhs).rhs(rhs).build();
    EvalContext context = Mockito.mock(EvalContext.class);

    when(lhs.eval(context)).thenReturn(null);
    when(rhs.eval(context)).thenReturn(LocalDate.now());

    assertNull(operator.eval(context));
  }

  @Test
  void shouldReturnNullIfRhsIsNull() {
    Expression lhs = Mockito.mock(Expression.class);
    Expression rhs = Mockito.mock(Expression.class);
    DateMinusDateOperator operator = new DateMinusDateOperator.Builder().lhs(lhs).rhs(rhs).build();
    EvalContext context = Mockito.mock(EvalContext.class);

    when(lhs.eval(context)).thenReturn(LocalDate.now());
    when(rhs.eval(context)).thenReturn(null);

    assertNull(operator.eval(context));
  }
}
