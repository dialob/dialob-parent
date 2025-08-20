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
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

class SizeOperatorTest {

  @Test
  public void testSizeOperator() {
    EvalContext context = Mockito.mock();
    Expression expression = Mockito.mock();
    SizeOperator valueSetToListOperator = ImmutableSizeOperator.builder()
      .expression(expression)
      .build();

    when(expression.eval(eq(context))).thenReturn(null);
    Assertions.assertNull(valueSetToListOperator.eval(context));

    when(expression.eval(eq(context))).thenReturn(1);
    Assertions.assertNull(valueSetToListOperator.eval(context));

    when(expression.eval(eq(context))).thenReturn("");
    Assertions.assertEquals(0, (Integer) valueSetToListOperator.eval(context));

    when(expression.eval(eq(context))).thenReturn("123");
    Assertions.assertEquals(3, (Integer) valueSetToListOperator.eval(context));

    when(expression.eval(eq(context))).thenReturn(List.of("a", "b"));
    Assertions.assertEquals(2, (Integer) valueSetToListOperator.eval(context));

    when(expression.eval(eq(context))).thenReturn(List.of());
    Assertions.assertEquals(0, (Integer) valueSetToListOperator.eval(context));

    when(expression.eval(eq(context))).thenReturn(new Object[0]);
    Assertions.assertEquals(0, (Integer) valueSetToListOperator.eval(context));

    when(expression.eval(eq(context))).thenReturn(new Object[2]);
    Assertions.assertEquals(2, (Integer) valueSetToListOperator.eval(context));

  }

}
