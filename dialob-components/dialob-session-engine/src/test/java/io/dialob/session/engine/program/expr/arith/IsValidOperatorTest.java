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
import io.dialob.session.engine.session.model.ErrorState;
import io.dialob.session.engine.session.model.IdUtils;
import io.dialob.session.engine.session.model.ItemId;
import io.dialob.session.engine.session.model.ItemState;
import org.junit.jupiter.api.Test;
import org.mockito.AdditionalAnswers;
import org.mockito.Mockito;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class IsValidOperatorTest {

  @Test
  void shouldBeValidIfNoErrorStates() {
    IsValidOperator operator = ImmutableIsValidOperator.of(IdUtils.toId("q1"));
    EvalContext context = Mockito.mock(EvalContext.class);
    when(context.mapTo(any(ItemId.class),anyBoolean())).thenAnswer(AdditionalAnswers.returnsFirstArg());
    when(context.getItemState(IdUtils.toId("q1"))).thenReturn(Optional.of(new ItemState(IdUtils.toId("q1"), null, "text", null, null)));
    when(context.getErrorStates()).thenReturn(Collections.emptyList());
    assertTrue(operator.eval(context));
    verify(context).mapTo(eq(IdUtils.toId("q1")),anyBoolean());
    verify(context).getItemState(IdUtils.toId("q1"));
    verify(context).getErrorStates();
    verifyNoMoreInteractions(context);
  }

  @Test
  void shouldBeNonValidIfErrorStateIsActive() {
    IsValidOperator operator = ImmutableIsValidOperator.of(IdUtils.toId("q1"));
    EvalContext context = Mockito.mock(EvalContext.class);
    when(context.mapTo(any(ItemId.class),anyBoolean())).thenAnswer(AdditionalAnswers.returnsFirstArg());
    when(context.getItemState(IdUtils.toId("q1"))).thenReturn(Optional.of(new ItemState(IdUtils.toId("q1"), null, "text", null, null)));
    ErrorState errorState = new ErrorState(IdUtils.toId("q1"), "error1", "error");
    errorState = errorState.update(context).setActive(true).get();
    when(context.getErrorStates()).thenReturn(Collections.singletonList(errorState));
    assertFalse(operator.eval(context));
    verify(context).mapTo(eq(IdUtils.toId("q1")),anyBoolean());
    verify(context).getItemState(IdUtils.toId("q1"));
    verify(context).getErrorStates();
    verifyNoMoreInteractions(context);
  }

  @Test
  void shouldBeValidIfNonMatchingErrorStateIsActive() {
    IsValidOperator operator = ImmutableIsValidOperator.of(IdUtils.toId("q1"));
    EvalContext context = Mockito.mock(EvalContext.class);
    when(context.mapTo(any(ItemId.class),anyBoolean())).thenAnswer(AdditionalAnswers.returnsFirstArg());
    when(context.getItemState(IdUtils.toId("q1"))).thenReturn(Optional.of(new ItemState(IdUtils.toId("q1"), null, "text", null, null)));
    ErrorState errorState = new ErrorState(IdUtils.toId("q2"), "error1", "error");
    errorState = errorState.update(context).setActive(true).get();
    when(context.getErrorStates()).thenReturn(Collections.singletonList(errorState));
    assertTrue(operator.eval(context));
    verify(context).mapTo(eq(IdUtils.toId("q1")),anyBoolean());
    verify(context).getItemState(IdUtils.toId("q1"));
    verify(context).getErrorStates();
    verifyNoMoreInteractions(context);
  }

  @Test
  void shouldBeValidGroupIsValid() {
    IsValidOperator operator = ImmutableIsValidOperator.of(IdUtils.toId("q1"));
    EvalContext context = Mockito.mock(EvalContext.class);
    when(context.mapTo(any(ItemId.class),anyBoolean())).thenAnswer(AdditionalAnswers.returnsFirstArg());
    when(context.getItemState(IdUtils.toId("q1"))).thenReturn(Optional.of(
      new ItemState(IdUtils.toId("q1"), null, "group", null, null).update().setInvalidAnswers(false).get()
    ));
    assertTrue(operator.eval(context));
    verify(context).mapTo(eq(IdUtils.toId("q1")),anyBoolean());
    verify(context).getItemState(IdUtils.toId("q1"));
    verifyNoMoreInteractions(context);
  }

  @Test
  void shouldBeInvalidGroupIsInvalid() {
    IsValidOperator operator = ImmutableIsValidOperator.of(IdUtils.toId("q1"));
    EvalContext context = Mockito.mock(EvalContext.class);
    when(context.mapTo(any(ItemId.class),anyBoolean())).thenAnswer(AdditionalAnswers.returnsFirstArg());
    when(context.getItemState(IdUtils.toId("q1"))).thenReturn(Optional.of(
      new ItemState(IdUtils.toId("q1"), null, "group", null, null).update().setInvalidAnswers(true).get()
    ));
    assertFalse(operator.eval(context));
    verify(context).mapTo(eq(IdUtils.toId("q1")),anyBoolean());
    verify(context).getItemState(IdUtils.toId("q1"));
    verifyNoMoreInteractions(context);
  }


  @Test
  void shouldLookupInsideOwnRow() {
    IsValidOperator operator = ImmutableIsValidOperator.of(IdUtils.toId("rg.*.q1"));
    EvalContext context = Mockito.mock(EvalContext.class);
    when(context.mapTo(any(ItemId.class),anyBoolean())).thenReturn(IdUtils.toId("rg.1.q1"));
    when(context.getItemState(IdUtils.toId("rg.1.q1"))).thenReturn(Optional.of(new ItemState(IdUtils.toId("rg.1.q1"), null, "text", null, null)));
    ErrorState errorState = new ErrorState(IdUtils.toId("rg.1.q1"), "error1", "error");
    errorState = errorState.update(context).setActive(true).get();
    when(context.getErrorStates()).thenReturn(Collections.singletonList(errorState));
    assertFalse(operator.eval(context));
    verify(context).mapTo(eq(IdUtils.toId("rg.*.q1")),anyBoolean());
    verify(context).getItemState(IdUtils.toId("rg.1.q1"));
    verify(context).getErrorStates();
    verifyNoMoreInteractions(context);
  }

  @Test
  void errorInOtherGroupShouldNotHaveEffect() {
    IsValidOperator operator = ImmutableIsValidOperator.of(IdUtils.toId("rg.*.q1"));
    EvalContext context = Mockito.mock(EvalContext.class);
    when(context.mapTo(any(ItemId.class),anyBoolean())).thenReturn(IdUtils.toId("rg.1.q1"));
    when(context.getItemState(IdUtils.toId("rg.1.q1"))).thenReturn(Optional.of(new ItemState(IdUtils.toId("rg.1.q1"), null, "text", null, null)));
    ErrorState errorState = new ErrorState(IdUtils.toId("rg.2.q1"), "error1", "error");
    errorState = errorState.update(context).setActive(true).get();
    when(context.getErrorStates()).thenReturn(Collections.singletonList(errorState));
    assertTrue(operator.eval(context));
    verify(context).mapTo(eq(IdUtils.toId("rg.*.q1")),anyBoolean());
    verify(context).getItemState(IdUtils.toId("rg.1.q1"));
    verify(context).getErrorStates();
    verifyNoMoreInteractions(context);
  }

}
