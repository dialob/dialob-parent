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

import io.dialob.session.engine.program.EvalContext;
import io.dialob.session.engine.program.model.Expression;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CoerceToDecimalOperatorTest {

  @Test
  void shouldEvalNullToNull() {
    Expression expression = mock();
    EvalContext context = mock();
    var op = ImmutableCoerceToDecimalOperator.builder()
      .expression(expression).build();

    when(expression.eval(context)).thenReturn(null);
    assertNull(op.eval(context));
  }

  @Test
  void shouldEvalStringToBigDecimal() {
    Expression expression = mock();
    EvalContext context = mock();
    var op = ImmutableCoerceToDecimalOperator.builder()
      .expression(expression).build();

    when(expression.eval(context)).thenReturn("1.0");
    assertEquals(BigDecimal.valueOf(10,1), op.eval(context));
  }

  @Test
  void shouldEvalDoubleToBigDecimal() {
    Expression expression = mock();
    EvalContext context = mock();
    var op = ImmutableCoerceToDecimalOperator.builder()
      .expression(expression).build();

    when(expression.eval(context)).thenReturn(1.0);
    assertEquals(BigDecimal.valueOf(10,1), op.eval(context));
  }

  @Test
  void shouldEvalNumberToBigDecimal() {
    Expression expression = mock();
    EvalContext context = mock();
    var op = ImmutableCoerceToDecimalOperator.builder()
      .expression(expression).build();

    when(expression.eval(context)).thenReturn(Integer.valueOf(1));
    assertEquals(BigDecimal.valueOf(1,0), op.eval(context));
  }

  @Test
  void shouldEvalBigDecimalToBigDecimal() {
    Expression expression = mock();
    EvalContext context = mock();
    var op = ImmutableCoerceToDecimalOperator.builder()
      .expression(expression).build();

    when(expression.eval(context)).thenReturn(BigDecimal.valueOf(100, 2));
    assertEquals(BigDecimal.valueOf(100, 2), op.eval(context));
  }

  @Test
  void shouldNotEvalObject() {
    Expression expression = mock();
    EvalContext context = mock();
    var op = ImmutableCoerceToDecimalOperator.builder()
      .expression(expression).build();

    when(expression.eval(context)).thenReturn(new Object());
    assertThrows(IllegalStateException.class, () -> op.eval(context));
  }


}
