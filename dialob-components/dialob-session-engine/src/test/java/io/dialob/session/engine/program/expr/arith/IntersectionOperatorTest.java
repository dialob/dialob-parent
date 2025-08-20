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
import io.dialob.session.engine.session.model.ImmutableValueSetId;
import io.dialob.session.engine.session.model.ValueSetState;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

class IntersectionOperatorTest {

  @Test
  public void testIntersection() {
    EvalContext context = Mockito.mock();
    Expression lhs = Mockito.mock();
    Expression rhs = Mockito.mock();
    Object result;

    var vss = new ValueSetState("vs1").update().setEntries(List.of(
      ValueSetState.Entry.of("a", "Label A"),
      ValueSetState.Entry.of("b", "Label B"),
      ValueSetState.Entry.of("c", "Label C")
    )).get();

    when(context.getValueSetState(eq(ImmutableValueSetId.of("vs1"))))
      .thenReturn(Optional.of(vss));

    IntersectionOperator valueSetToListOperator = ImmutableIntersectionOperator.builder()
      .lhs(lhs)
      .rhs(rhs)
      .build();

    when(lhs.eval(eq(context))).thenReturn(null);
    when(rhs.eval(eq(context))).thenReturn(null);
    result = valueSetToListOperator.eval(context);
    Assertions.assertTrue(((List<?>) result).isEmpty());

    when(lhs.eval(eq(context))).thenReturn(List.of());
    when(rhs.eval(eq(context))).thenReturn(null);
    result = valueSetToListOperator.eval(context);
    Assertions.assertTrue(((List<?>) result).isEmpty());

    when(lhs.eval(eq(context))).thenReturn(null);
    when(rhs.eval(eq(context))).thenReturn(List.of());
    result = valueSetToListOperator.eval(context);
    Assertions.assertTrue(((List<?>) result).isEmpty());

    when(lhs.eval(eq(context))).thenReturn(List.of());
    when(rhs.eval(eq(context))).thenReturn(List.of());
    result = valueSetToListOperator.eval(context);
    Assertions.assertTrue(((List<?>) result).isEmpty());

    when(lhs.eval(eq(context))).thenReturn(List.of("a"));
    when(rhs.eval(eq(context))).thenReturn(List.of());
    result = valueSetToListOperator.eval(context);
    Assertions.assertTrue(((List<?>) result).isEmpty());

    when(lhs.eval(eq(context))).thenReturn(List.of());
    when(rhs.eval(eq(context))).thenReturn(List.of("a"));
    result = valueSetToListOperator.eval(context);
    Assertions.assertTrue(((List<?>) result).isEmpty());

    when(lhs.eval(eq(context))).thenReturn(List.of("b"));
    when(rhs.eval(eq(context))).thenReturn(List.of("a"));
    result = valueSetToListOperator.eval(context);
    Assertions.assertTrue(((List<?>) result).isEmpty());

    when(lhs.eval(eq(context))).thenReturn(List.of("a"));
    when(rhs.eval(eq(context))).thenReturn(List.of("a"));
    result = valueSetToListOperator.eval(context);
    Assertions.assertIterableEquals(
      List.of("a"), (Iterable<?>) result);

    when(lhs.eval(eq(context))).thenReturn(List.of("a", "b"));
    when(rhs.eval(eq(context))).thenReturn(List.of("b", "c"));
    result = valueSetToListOperator.eval(context);
    Assertions.assertIterableEquals(
      List.of("b"), (Iterable<?>) result);

  }


}
