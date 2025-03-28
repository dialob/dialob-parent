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

import static org.mockito.Mockito.*;

class InOperatorTest {

  @Test
  void shouldFindFromSet() {
    Expression lhs = Mockito.mock(Expression.class);
    Expression rhs = Mockito.mock(Expression.class);
    EvalContext context = Mockito.mock(EvalContext.class);

    when(lhs.eval(context)).thenReturn("b");
    when(rhs.eval(context)).thenReturn((List.of("b")));

    ImmutableInOperator op = ImmutableInOperator.builder()
      .lhs(lhs)
      .rhs(rhs)
      .build();


    Assertions.assertTrue(op.eval(context));

    verify(lhs).eval(context);
    verify(rhs).eval(context);
    verifyNoMoreInteractions(lhs, rhs, context);
  }

  @Test
  void shouldNotFindFromSet() {
    Expression lhs = Mockito.mock(Expression.class);
    Expression rhs = Mockito.mock(Expression.class);
    EvalContext context = Mockito.mock(EvalContext.class);

    when(lhs.eval(context)).thenReturn("b");
    when(rhs.eval(context)).thenReturn((List.of("c")));

    ImmutableInOperator op = ImmutableInOperator.builder()
      .lhs(lhs)
      .rhs(rhs)
      .build();


    Assertions.assertFalse(op.eval(context));

    verify(lhs).eval(context);
    verify(rhs).eval(context);
    verifyNoMoreInteractions(lhs, rhs, context);
  }

}
