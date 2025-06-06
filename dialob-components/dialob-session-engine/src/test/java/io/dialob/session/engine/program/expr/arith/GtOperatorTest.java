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

import static org.mockito.Mockito.when;

class GtOperatorTest {


  @Test
  void testLogic() {
    Expression lh = Mockito.mock(Expression.class);
    Expression rh = Mockito.mock(Expression.class);
    GtOperator operator = ImmutableGtOperator.builder().lhs(lh).rhs(rh).build();
    EvalContext context = Mockito.mock(EvalContext.class);

    when(lh.eval(context)).thenReturn("1");
    when(rh.eval(context)).thenReturn("2");
    Assertions.assertFalse(operator.eval(context));

    when(lh.eval(context)).thenReturn("same");
    when(rh.eval(context)).thenReturn("same");
    Assertions.assertFalse(operator.eval(context));

    when(lh.eval(context)).thenReturn("2");
    when(rh.eval(context)).thenReturn("1");
    Assertions.assertTrue(operator.eval(context));


    // TODO should return null instead?
    when(lh.eval(context)).thenReturn("nonnull");
    when(rh.eval(context)).thenReturn(null);
    Assertions.assertNull(operator.eval(context));

    when(lh.eval(context)).thenReturn(null);
    when(rh.eval(context)).thenReturn("nonnull");
    Assertions.assertNull(operator.eval(context));

    when(lh.eval(context)).thenReturn(null);
    when(rh.eval(context)).thenReturn(null);
    Assertions.assertNull(operator.eval(context));
  }

}
