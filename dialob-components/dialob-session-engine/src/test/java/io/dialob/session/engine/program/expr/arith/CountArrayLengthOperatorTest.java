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
import io.dialob.session.engine.session.model.IdUtils;
import io.dialob.session.engine.session.model.ItemState;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class CountArrayLengthOperatorTest {

  @Test
  void nonExistingObjectIsEmpty() {
    EvalContext context = mock();
    var op = ImmutableCountArrayLengthOperator.builder()
      .itemId(IdUtils.toId("test")).build();
    when(context.getItemState(any())).thenReturn(Optional.empty());

    assertThat(op.eval(context)).isEqualTo(BigInteger.ZERO);

    verify(context, Mockito.times(1)).getItemState(IdUtils.toId("test"));
    verifyNoMoreInteractions(context);
  }

  @Test
  void stateHasNullValue() {
    EvalContext context = mock();
    var op = ImmutableCountArrayLengthOperator.builder()
      .itemId(IdUtils.toId("test")).build();
    when(context.getItemState(any())).thenReturn(Optional.of(new ItemState(IdUtils.toId("test"), null, "note", null, null)));

    assertThat(op.eval(context)).isEqualTo(BigInteger.ZERO);

    verify(context, Mockito.times(1)).getItemState(IdUtils.toId("test"));
    verifyNoMoreInteractions(context);
  }

  @Test
  void stateValueIsArray() {
    EvalContext context = mock();
    var op = ImmutableCountArrayLengthOperator.builder()
      .itemId(IdUtils.toId("test")).build();
    when(context.getItemState(any())).thenReturn(Optional.of(new ItemState(IdUtils.toId("test"), null, "note", null, null).update()
      .setValue(new Object[10]).get()));

    assertThat(op.eval(context)).isEqualTo(BigInteger.TEN);

    verify(context, Mockito.times(1)).getItemState(IdUtils.toId("test"));
    verifyNoMoreInteractions(context);
  }

  @Test
  void stateValueIsList() {
    EvalContext context = mock();
    var op = ImmutableCountArrayLengthOperator.builder()
      .itemId(IdUtils.toId("test")).build();
    when(context.getItemState(any())).thenReturn(Optional.of(new ItemState(IdUtils.toId("test"), null, "note", null, null).update()
      .setValue(List.of(1,2,3)).get()));

    assertThat(op.eval(context)).isEqualTo(BigInteger.valueOf(3));

    verify(context, Mockito.times(1)).getItemState(IdUtils.toId("test"));
    verifyNoMoreInteractions(context);
  }

  @Test
  void stateValueIsString() {
    EvalContext context = mock();
    var op = ImmutableCountArrayLengthOperator.builder()
      .itemId(IdUtils.toId("test")).build();
    when(context.getItemState(any())).thenReturn(Optional.of(new ItemState(IdUtils.toId("test"), null, "note", null, null).update()
      .setValue("123").get()));

    assertThat(op.eval(context)).isEqualTo(BigInteger.ZERO);

    verify(context, Mockito.times(1)).getItemState(IdUtils.toId("test"));
    verifyNoMoreInteractions(context);
  }

}
