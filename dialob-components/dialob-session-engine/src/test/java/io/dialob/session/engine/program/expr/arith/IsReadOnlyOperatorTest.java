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

import static io.dialob.session.engine.session.command.EventMatchers.whenReadOnlyUpdated;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class IsReadOnlyOperatorTest {

  @Test
  void shouldReturnBooleanType() {
    IsReadOnlyOperator operator = new IsReadOnlyOperator.Builder().itemId(IdUtils.toId("item1")).build();
    assertEquals(ValueType.BOOLEAN, operator.getValueType());
  }

  @Test
  void shouldReturnEvalRequiredConditions() {
    ItemId itemId = IdUtils.toId("item1");
    IsReadOnlyOperator operator = new IsReadOnlyOperator.Builder().itemId(itemId).build();
    Set<EventMatcher> conditions = operator.getEvalRequiredConditions();
    assertEquals(Set.of(whenReadOnlyUpdated(itemId)), conditions);
  }

  @Test
  void shouldEvalToTrueWhenItemIsReadOnly() {
    ItemId itemId = IdUtils.toId("item1");
    IsReadOnlyOperator operator = new IsReadOnlyOperator.Builder().itemId(itemId).build();
    EvalContext context = Mockito.mock(EvalContext.class);
    ItemState itemState = Mockito.mock(ItemState.class);

    when(context.getItemState(itemId)).thenReturn(Optional.of(itemState));
    when(itemState.isReadOnly()).thenReturn(true);

    assertTrue(operator.eval(context));
  }

  @Test
  void shouldEvalToFalseWhenItemIsNotReadOnly() {
    ItemId itemId = IdUtils.toId("item1");
    IsReadOnlyOperator operator = new IsReadOnlyOperator.Builder().itemId(itemId).build();
    EvalContext context = Mockito.mock(EvalContext.class);
    ItemState itemState = Mockito.mock(ItemState.class);

    when(context.getItemState(itemId)).thenReturn(Optional.of(itemState));
    when(itemState.isReadOnly()).thenReturn(false);

    assertFalse(operator.eval(context));
  }

  @Test
  void shouldEvalToFalseWhenItemStateIsMissing() {
    ItemId itemId = IdUtils.toId("item1");
    IsReadOnlyOperator operator = new IsReadOnlyOperator.Builder().itemId(itemId).build();
    EvalContext context = Mockito.mock(EvalContext.class);

    when(context.getItemState(itemId)).thenReturn(Optional.empty());

    assertFalse(operator.eval(context));
  }
}
