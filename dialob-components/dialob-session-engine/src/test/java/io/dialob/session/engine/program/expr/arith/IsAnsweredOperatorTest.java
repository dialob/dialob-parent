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
import io.dialob.session.engine.session.command.EventMatcher;
import io.dialob.session.engine.session.model.IdUtils;
import io.dialob.session.engine.session.model.ItemId;
import io.dialob.session.engine.session.model.ItemState;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;
import java.util.Set;

import static io.dialob.session.engine.session.command.EventMatchers.whenActiveUpdated;
import static io.dialob.session.engine.session.command.EventMatchers.whenAnsweredUpdated;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class IsAnsweredOperatorTest {

  @Test
  void shouldReturnBooleanType() {
    IsAnsweredOperator operator = new IsAnsweredOperator.Builder().questionId(IdUtils.toId("item1")).build();
    assertEquals(ValueType.BOOLEAN, operator.getValueType());
  }

  @Test
  void shouldReturnEvalRequiredConditions() {
    ItemId itemId = IdUtils.toId("item1");
    IsAnsweredOperator operator = new IsAnsweredOperator.Builder().questionId(itemId).build();
    Set<EventMatcher> conditions = operator.getEvalRequiredConditions();
    assertEquals(Set.of(whenAnsweredUpdated(itemId), whenActiveUpdated(itemId)), conditions);
  }

  @Test
  void shouldEvalToTrueWhenItemIsAnsweredAndActive() {
    ItemId itemId = IdUtils.toId("item1");
    IsAnsweredOperator operator = new IsAnsweredOperator.Builder().questionId(itemId).build();
    EvalContext context = Mockito.mock(EvalContext.class);
    ItemState itemState = Mockito.mock(ItemState.class);

    when(context.mapTo(itemId, false)).thenReturn(itemId);
    when(context.getItemState(itemId)).thenReturn(Optional.of(itemState));
    when(itemState.isAnswered()).thenReturn(true);
    when(itemState.isActive()).thenReturn(true);

    assertTrue(operator.eval(context));
  }

  @Test
  void shouldEvalToFalseWhenItemIsNotAnswered() {
    ItemId itemId = IdUtils.toId("item1");
    IsAnsweredOperator operator = new IsAnsweredOperator.Builder().questionId(itemId).build();
    EvalContext context = Mockito.mock(EvalContext.class);
    ItemState itemState = Mockito.mock(ItemState.class);

    when(context.mapTo(itemId, false)).thenReturn(itemId);
    when(context.getItemState(itemId)).thenReturn(Optional.of(itemState));
    when(itemState.isAnswered()).thenReturn(false);
    when(itemState.isActive()).thenReturn(true);

    assertFalse(operator.eval(context));
  }

  @Test
  void shouldEvalToFalseWhenItemIsInactive() {
    ItemId itemId = IdUtils.toId("item1");
    IsAnsweredOperator operator = new IsAnsweredOperator.Builder().questionId(itemId).build();
    EvalContext context = Mockito.mock(EvalContext.class);
    ItemState itemState = Mockito.mock(ItemState.class);

    when(context.mapTo(itemId, false)).thenReturn(itemId);
    when(context.getItemState(itemId)).thenReturn(Optional.of(itemState));
    when(itemState.isAnswered()).thenReturn(true);
    when(itemState.isActive()).thenReturn(false);

    assertFalse(operator.eval(context));
  }

  @Test
  void shouldEvalToFalseWhenItemStateIsMissing() {
    ItemId itemId = IdUtils.toId("item1");
    IsAnsweredOperator operator = new IsAnsweredOperator.Builder().questionId(itemId).build();
    EvalContext context = Mockito.mock(EvalContext.class);

    when(context.mapTo(itemId, false)).thenReturn(itemId);
    when(context.getItemState(itemId)).thenReturn(Optional.empty());

    assertFalse(operator.eval(context));
  }
}
