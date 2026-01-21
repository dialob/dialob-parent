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
package io.dialob.session.engine.session.command;

import io.dialob.session.engine.program.EvalContext;
import io.dialob.session.engine.session.model.IdUtils;
import io.dialob.session.engine.session.model.ItemId;
import io.dialob.session.engine.session.model.ItemState;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SetAnswerTest {

  @Test
  void shouldUpdateAnswerAndValue() {
    ItemId itemId = IdUtils.toId("item1");
    SetAnswer command = new SetAnswer(itemId, "newAnswer", List.of());
    EvalContext context = Mockito.mock(EvalContext.class);
    ItemState itemState = Mockito.mock(ItemState.class);
    ItemState.UpdateBuilder updateBuilder = Mockito.mock(ItemState.UpdateBuilder.class);
    ItemState updatedState = Mockito.mock(ItemState.class);

    when(itemState.isDisabled()).thenReturn(false);
    when(itemState.isReadOnly()).thenReturn(false);
    when(itemState.isActive()).thenReturn(true);
    when(itemState.type()).thenReturn("text");
    when(itemState.update()).thenReturn(updateBuilder);
    when(updateBuilder.setAnswer("newAnswer")).thenReturn(updateBuilder);
    when(updateBuilder.setValue("newAnswer")).thenReturn(updateBuilder);
    when(updateBuilder.get()).thenReturn(updatedState);

    ItemState result = command.update(context, itemState);

    assertEquals(updatedState, result);

    verify(itemState).isDisabled();
    verify(itemState).isReadOnly();
    verify(itemState).isActive();
    verify(itemState, times(2)).type();
    verify(itemState).update();

    verifyNoMoreInteractions(itemState);
  }

  @Test
  void shouldNotUpdateIfDisabled() {
    ItemId itemId = IdUtils.toId("item1");
    SetAnswer command = new SetAnswer(itemId, "newAnswer", List.of());
    EvalContext context = Mockito.mock(EvalContext.class);
    ItemState itemState = Mockito.mock(ItemState.class);

    when(itemState.isDisabled()).thenReturn(true);
    when(itemState.isReadOnly()).thenReturn(false);
    when(itemState.isActive()).thenReturn(true);
    when(itemState.type()).thenReturn("text");

    ItemState result = command.update(context, itemState);

    assertEquals(itemState, result);
  }

  @Test
  void shouldNotUpdateIfReadOnly() {
    ItemId itemId = IdUtils.toId("item1");
    SetAnswer command = new SetAnswer(itemId, "newAnswer", List.of());
    EvalContext context = Mockito.mock(EvalContext.class);
    ItemState itemState = Mockito.mock(ItemState.class);

    when(itemState.isDisabled()).thenReturn(false);
    when(itemState.isReadOnly()).thenReturn(true);
    when(itemState.isActive()).thenReturn(true);
    when(itemState.type()).thenReturn("text");

    ItemState result = command.update(context, itemState);

    assertEquals(itemState, result);
  }

  @Test
  void shouldNotUpdateIfInactive() {
    ItemId itemId = IdUtils.toId("item1");
    SetAnswer command = new SetAnswer(itemId, "newAnswer", List.of());
    EvalContext context = Mockito.mock(EvalContext.class);
    ItemState itemState = Mockito.mock(ItemState.class);

    when(itemState.isDisabled()).thenReturn(false);
    when(itemState.isReadOnly()).thenReturn(false);
    when(itemState.isActive()).thenReturn(false);
    when(itemState.type()).thenReturn("text");

    ItemState result = command.update(context, itemState);

    assertEquals(itemState, result);
  }

  @Test
  void shouldUpdateIfActivatingEvenIfDisabled() {
    ItemId itemId = IdUtils.toId("item1");
    SetAnswer command = new SetAnswer(itemId, "newAnswer", List.of());
    EvalContext context = Mockito.mock(EvalContext.class);
    ItemState itemState = Mockito.mock(ItemState.class);
    ItemState.UpdateBuilder updateBuilder = Mockito.mock(ItemState.UpdateBuilder.class);
    ItemState updatedState = Mockito.mock(ItemState.class);

    when(context.isActivating()).thenReturn(true);
    when(itemState.isDisabled()).thenReturn(true); // Disabled but activating
    when(itemState.type()).thenReturn("text");
    when(itemState.update()).thenReturn(updateBuilder);
    when(updateBuilder.setAnswer("newAnswer")).thenReturn(updateBuilder);
    when(updateBuilder.setValue("newAnswer")).thenReturn(updateBuilder);
    when(updateBuilder.get()).thenReturn(updatedState);

    ItemState result = command.update(context, itemState);

    assertEquals(updatedState, result);
  }

  @Test
  void shouldNotUpdateIfNotQuestionType() {
    ItemId itemId = IdUtils.toId("item1");
    SetAnswer command = new SetAnswer(itemId, "newAnswer", List.of());
    EvalContext context = Mockito.mock(EvalContext.class);
    ItemState itemState = Mockito.mock(ItemState.class);

    when(itemState.isDisabled()).thenReturn(false);
    when(itemState.isReadOnly()).thenReturn(false);
    when(itemState.isActive()).thenReturn(true);
    when(itemState.type()).thenReturn("group"); // Not a question type

    ItemState result = command.update(context, itemState);

    assertEquals(itemState, result);
  }

  @Test
  void shouldCreateNewInstanceWithTargetId() {
    ItemId itemId = IdUtils.toId("item1");
    ItemId newItemId = IdUtils.toId("item2");
    SetAnswer command = new SetAnswer(itemId, "newAnswer", List.of());

    SetAnswer newCommand = (SetAnswer) command.withTargetId(newItemId);

    assertEquals(newItemId, newCommand.targetId());
    assertEquals("newAnswer", newCommand.answer());
    assertEquals(command.triggers(), newCommand.triggers());
  }
}
