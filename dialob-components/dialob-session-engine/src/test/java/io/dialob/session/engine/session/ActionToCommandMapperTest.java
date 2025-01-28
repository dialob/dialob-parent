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
package io.dialob.session.engine.session;

import io.dialob.api.proto.Action;
import io.dialob.api.proto.ImmutableAction;
import io.dialob.session.engine.session.command.*;
import io.dialob.session.engine.session.model.IdUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ActionToCommandMapperTest {

  /**
   * This test class verifies the functionality of the `apply` method in the {@link ActionToCommandMapper} class.
   * The `apply` method converts {@link Action} objects into corresponding {@link Command} objects.
   */

  @Test
  void testApplyAnswerAction() {
    // Arrange
    Action action = ImmutableAction.builder()
      .type(Action.Type.ANSWER)
      .id("answerId")
      .answer("testAnswer")
      .build();

    // Act
    Command<?> command = ActionToCommandMapper.INSTANCE.apply(action);

    // Assert
    assertNotNull(command);
    assertInstanceOf(SetAnswer.class, command);
    SetAnswer setAnswerCommand = (SetAnswer) command;
    assertEquals(IdUtils.toId("answerId"), setAnswerCommand.getTargetId());
    assertEquals("testAnswer", setAnswerCommand.getAnswer());
  }

  @Test
  void testApplySetValueAction() {
    // Arrange
    Action action = ImmutableAction.builder()
      .type(Action.Type.SET_VALUE)
      .id("varId")
      .value("newValue")
      .build();

    // Act
    Command<?> command = ActionToCommandMapper.INSTANCE.apply(action);

    // Assert
    assertNotNull(command);
    assertInstanceOf(SetVariableValue.class, command);
    SetVariableValue setVariableValueCommand = (SetVariableValue) command;
    assertEquals(IdUtils.toId("varId"), setVariableValueCommand.getTargetId());
    assertEquals("newValue", setVariableValueCommand.getValue());
  }

  @Test
  void testApplySetFailedAction() {
    // Arrange
    Action action = ImmutableAction.builder()
      .type(Action.Type.SET_FAILED)
      .id("varId")
      .build();

    // Act
    Command<?> command = ActionToCommandMapper.INSTANCE.apply(action);

    // Assert
    assertNotNull(command);
    assertInstanceOf(SetVariableFailed.class, command);
    SetVariableFailed setFailedCommand = (SetVariableFailed) command;
    assertEquals(IdUtils.toId("varId"), setFailedCommand.getTargetId());
  }

  @Test
  void testApplyNextPageAction() {
    // Arrange
    Action action = ImmutableAction.builder()
      .type(Action.Type.NEXT)
      .build();

    // Act
    Command<?> command = ActionToCommandMapper.INSTANCE.apply(action);

    // Assert
    assertNotNull(command);
    assertInstanceOf(NextPage.class, command);
  }

  @Test
  void testApplyPreviousPageAction() {
    // Arrange
    Action action = ImmutableAction.builder()
      .type(Action.Type.PREVIOUS)
      .build();

    // Act
    Command<?> command = ActionToCommandMapper.INSTANCE.apply(action);

    // Assert
    assertNotNull(command);
    assertInstanceOf(PrevPage.class, command);
  }

  @Test
  void testApplyGotoPageAction() {
    // Arrange
    Action action = ImmutableAction.builder()
      .type(Action.Type.GOTO)
      .id("pageId")
      .build();

    // Act
    Command<?> command = ActionToCommandMapper.INSTANCE.apply(action);

    // Assert
    assertNotNull(command);
    assertInstanceOf(GotoPage.class, command);
    GotoPage gotoPageCommand = (GotoPage) command;
    assertEquals(IdUtils.toId("questionnaire"), gotoPageCommand.getTargetId());
    assertEquals(IdUtils.toId("pageId"), gotoPageCommand.getPage());
  }

  @Test
  void testApplyCompleteAction() {
    // Arrange
    Action action = ImmutableAction.builder()
      .type(Action.Type.COMPLETE)
      .build();

    // Act
    Command<?> command = ActionToCommandMapper.INSTANCE.apply(action);

    // Assert
    assertNotNull(command);
    assertInstanceOf(Complete.class, command);
  }

  @Test
  void testApplyAddRowAction() {
    // Arrange
    Action action = ImmutableAction.builder()
      .type(Action.Type.ADD_ROW)
      .id("rowId")
      .build();

    // Act
    Command<?> command = ActionToCommandMapper.INSTANCE.apply(action);

    // Assert
    assertNotNull(command);
    assertInstanceOf(AddRow.class, command);
    AddRow addRowCommand = (AddRow) command;
    assertEquals(IdUtils.toId("rowId"), addRowCommand.getTargetId());
  }

  @Test
  void testApplyDeleteRowAction() {
    // Arrange
    Action action = ImmutableAction.builder()
      .type(Action.Type.DELETE_ROW)
      .id("rowId.0")
      .build();

    // Act
    Command<?> command = ActionToCommandMapper.INSTANCE.apply(action);

    // Assert
    assertNotNull(command);
    assertInstanceOf(DeleteRow.class, command);
    DeleteRow deleteRowCommand = (DeleteRow) command;
    assertEquals(IdUtils.toId("rowId"), deleteRowCommand.getTargetId());
  }

  @Test
  void deleteRowActionIsNotOnWrongTarget() {
    // Arrange
    Action action = ImmutableAction.builder()
      .type(Action.Type.DELETE_ROW)
      .id("rowId")
      .build();

    // Act
    Command<?> command = ActionToCommandMapper.INSTANCE.apply(action);

    // Assert
    assertNotNull(command);
    assertInstanceOf(NopCommand.class, command);
    NopCommand nopCommand = (NopCommand) command;
    assertEquals(IdUtils.toId("rowId"), nopCommand.getTargetId());
  }

  @Test
  void testApplySetLocaleAction() {
    // Arrange
    Action action = ImmutableAction.builder()
      .type(Action.Type.SET_LOCALE)
      .value("en-US")
      .build();

    // Act
    Command<?> command = ActionToCommandMapper.INSTANCE.apply(action);

    // Assert
    assertNotNull(command);
    assertInstanceOf(SetLocale.class, command);
    SetLocale setLocaleCommand = (SetLocale) command;
    assertEquals("en-US", setLocaleCommand.getLocale());
  }

  @Test
  void testApplyUnsupportedAction() {
    // Arrange
    Action action = ImmutableAction.builder()
      .type(Action.Type.RESET)
      .build();

    // Act
    Command<?> command = ActionToCommandMapper.INSTANCE.apply(action);

    // Assert
    assertNull(command);
  }

}
