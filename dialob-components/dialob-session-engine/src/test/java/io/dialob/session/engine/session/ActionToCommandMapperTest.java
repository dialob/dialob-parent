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
import io.dialob.session.engine.session.command.Command;
import io.dialob.session.engine.session.command.CommandFactory;
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
    Action action = new Action.Builder()
      .type(Action.Type.ANSWER)
      .id("answerId")
      .answer("testAnswer")
      .build();

    // Act
    Command<?> command = ActionToCommandMapper.INSTANCE.apply(action);

    // Assert
    assertNotNull(command);
    assertEquals(CommandFactory.setAnswer(IdUtils.toId("answerId"), "testAnswer"), command);
  }

  @Test
  void testApplySetValueAction() {
    // Arrange
    Action action = new Action.Builder()
      .type(Action.Type.SET_VALUE)
      .id("varId")
      .value("newValue")
      .build();

    // Act
    Command<?> command = ActionToCommandMapper.INSTANCE.apply(action);

    // Assert
    assertNotNull(command);
    assertEquals(CommandFactory.setVariableValue(IdUtils.toId("varId"), "newValue"), command);
  }

  @Test
  void testApplySetFailedAction() {
    // Arrange
    Action action = new Action.Builder()
      .type(Action.Type.SET_FAILED)
      .id("varId")
      .build();

    // Act
    Command<?> command = ActionToCommandMapper.INSTANCE.apply(action);

    // Assert
    assertNotNull(command);
    assertEquals(CommandFactory.setVariableFailed(IdUtils.toId("varId")), command);
  }

  @Test
  void testApplyNextPageAction() {
    // Arrange
    Action action = new Action.Builder()
      .type(Action.Type.NEXT)
      .build();

    // Act
    Command<?> command = ActionToCommandMapper.INSTANCE.apply(action);

    // Assert
    assertNotNull(command);
    assertEquals("io.dialob.session.engine.session.command.NextPage", command.getClass().getCanonicalName());
  }

  @Test
  void testApplyPreviousPageAction() {
    // Arrange
    Action action = new Action.Builder()
      .type(Action.Type.PREVIOUS)
      .build();

    // Act
    Command<?> command = ActionToCommandMapper.INSTANCE.apply(action);

    // Assert
    assertNotNull(command);
    assertEquals("io.dialob.session.engine.session.command.PrevPage", command.getClass().getCanonicalName());
  }

  @Test
  void testApplyGotoPageAction() {
    // Arrange
    Action action = new Action.Builder()
      .type(Action.Type.GOTO)
      .id("pageId")
      .build();

    // Act
    Command<?> command = ActionToCommandMapper.INSTANCE.apply(action);

    // Assert
    assertNotNull(command);
    assertEquals(CommandFactory.gotoPage(IdUtils.toId("pageId")), command);
  }

  @Test
  void testApplyCompleteAction() {
    // Arrange
    Action action = new Action.Builder()
      .type(Action.Type.COMPLETE)
      .build();

    // Act
    Command<?> command = ActionToCommandMapper.INSTANCE.apply(action);

    // Assert
    assertNotNull(command);
    assertEquals("io.dialob.session.engine.session.command.Complete", command.getClass().getCanonicalName());
  }

  @Test
  void testApplyAddRowAction() {
    // Arrange
    Action action = new Action.Builder()
      .type(Action.Type.ADD_ROW)
      .id("rowId")
      .build();

    // Act
    Command<?> command = ActionToCommandMapper.INSTANCE.apply(action);

    // Assert
    assertNotNull(command);
    assertEquals(CommandFactory.addRow(IdUtils.toId("rowId")), command);
  }

  @Test
  void testApplyDeleteRowAction() {
    // Arrange
    Action action = new Action.Builder()
      .type(Action.Type.DELETE_ROW)
      .id("rowId.0")
      .build();

    // Act
    Command<?> command = ActionToCommandMapper.INSTANCE.apply(action);

    // Assert
    assertNotNull(command);
    assertEquals(CommandFactory.deleteRow(IdUtils.toId("rowId.0")), command);
  }

  @Test
  void deleteRowActionIsNotOnWrongTarget() {
    // Arrange
    Action action = new Action.Builder()
      .type(Action.Type.DELETE_ROW)
      .id("rowId")
      .build();

    // Act
    Command<?> command = ActionToCommandMapper.INSTANCE.apply(action);

    // Assert
    assertNotNull(command);
    assertEquals(CommandFactory.nop(IdUtils.toId("rowId")), command);
  }

  @Test
  void testApplySetLocaleAction() {
    // Arrange
    Action action = new Action.Builder()
      .type(Action.Type.SET_LOCALE)
      .value("en-US")
      .build();

    // Act
    Command<?> command = ActionToCommandMapper.INSTANCE.apply(action);

    // Assert
    assertNotNull(command);
    assertEquals(CommandFactory.setLocale("en-US"), command);
  }

  @Test
  void testApplyUnsupportedAction() {
    // Arrange
    Action action = new Action.Builder()
      .type(Action.Type.RESET)
      .build();

    // Act
    Command<?> command = ActionToCommandMapper.INSTANCE.apply(action);

    // Assert
    assertNull(command);
  }

}
