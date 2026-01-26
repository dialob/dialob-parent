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

class DatePlusPeriodOperatorTest {

  @Test
  void shouldReturnDateType() {
    Expression lhs = Mockito.mock(Expression.class);
    Expression rhs = Mockito.mock(Expression.class);
    DatePlusPeriodOperator operator = new DatePlusPeriodOperator.Builder().lhs(lhs).rhs(rhs).build();
    assertEquals(ValueType.DATE, operator.getValueType());
  }

  @Test
  void shouldAddPeriodToDate() {
    Expression lhs = Mockito.mock(Expression.class);
    Expression rhs = Mockito.mock(Expression.class);
    DatePlusPeriodOperator operator = new DatePlusPeriodOperator.Builder().lhs(lhs).rhs(rhs).build();
    EvalContext context = Mockito.mock(EvalContext.class);

    LocalDate date = LocalDate.of(2023, 10, 20);
    Period period = Period.ofDays(7);

    when(lhs.eval(context)).thenReturn(date);
    when(rhs.eval(context)).thenReturn(period);

    assertEquals(LocalDate.of(2023, 10, 27), operator.eval(context));
  }

  @Test
  void shouldReturnNullIfLhsIsNull() {
    Expression lhs = Mockito.mock(Expression.class);
    Expression rhs = Mockito.mock(Expression.class);
    DatePlusPeriodOperator operator = new DatePlusPeriodOperator.Builder().lhs(lhs).rhs(rhs).build();
    EvalContext context = Mockito.mock(EvalContext.class);

    when(lhs.eval(context)).thenReturn(null);
    when(rhs.eval(context)).thenReturn(Period.ofDays(1));

    assertNull(operator.eval(context));
  }

  @Test
  void shouldReturnNullIfRhsIsNull() {
    Expression lhs = Mockito.mock(Expression.class);
    Expression rhs = Mockito.mock(Expression.class);
    DatePlusPeriodOperator operator = new DatePlusPeriodOperator.Builder().lhs(lhs).rhs(rhs).build();
    EvalContext context = Mockito.mock(EvalContext.class);

    when(lhs.eval(context)).thenReturn(LocalDate.now());
    when(rhs.eval(context)).thenReturn(null);

    assertNull(operator.eval(context));
  }
}
