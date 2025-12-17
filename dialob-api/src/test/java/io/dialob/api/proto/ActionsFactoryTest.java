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
package io.dialob.api.proto;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ActionsFactoryTest {

  @Test
  void shouldCreateAnswerAction() {
    Action action = ActionsFactory.answer("q1", "answer1");

    assertEquals(Action.Type.ANSWER, action.getType());
    assertEquals("q1", action.getId());
    assertEquals("answer1", action.getAnswer());
  }

  @Test
  void shouldCreateAnswerActionWithNullAnswer() {
    Action action = ActionsFactory.answer("q1", null);

    assertEquals(Action.Type.ANSWER, action.getType());
    assertEquals("q1", action.getId());
    assertNull(action.getAnswer());
  }

  @Test
  void shouldReturnSameRemoveAnswersActionInstance() {
    Action action1 = ActionsFactory.removeAnswers();
    Action action2 = ActionsFactory.removeAnswers();

    assertSame(ActionsFactory.REMOVE_ANSWERS_ACTION, action1);
    assertSame(ActionsFactory.REMOVE_ANSWERS_ACTION, action2);
    assertEquals(Action.Type.REMOVE_ANSWERS, action1.getType());
  }

  @Test
  void shouldReturnSameNextActionInstance() {
    Action action1 = ActionsFactory.next();
    Action action2 = ActionsFactory.next();

    assertSame(ActionsFactory.NEXT_ACTION, action1);
    assertSame(ActionsFactory.NEXT_ACTION, action2);
    assertEquals(Action.Type.NEXT, action1.getType());
  }

  @Test
  void shouldReturnSamePreviousActionInstance() {
    Action action1 = ActionsFactory.previous();
    Action action2 = ActionsFactory.previous();

    assertSame(ActionsFactory.PREVIOUS_ACTION, action1);
    assertSame(ActionsFactory.PREVIOUS_ACTION, action2);
    assertEquals(Action.Type.PREVIOUS, action1.getType());
  }

  @Test
  void shouldCreateCompleteAction() {
    Action action = ActionsFactory.complete("questionnaire1");

    assertEquals(Action.Type.COMPLETE, action.getType());
    assertEquals("questionnaire1", action.getId());
  }

  @Test
  void shouldCreateGotoPageAction() {
    Action action = ActionsFactory.gotoPage("page1");

    assertEquals(Action.Type.GOTO, action.getType());
    assertEquals("page1", action.getId());
  }

  @Test
  void shouldCreateAddRowAction() {
    Action action = ActionsFactory.addRow("group1");

    assertEquals(Action.Type.ADD_ROW, action.getType());
    assertEquals("group1", action.getId());
  }

  @Test
  void shouldCreateDeleteRowAction() {
    Action action = ActionsFactory.deleteRow("row1");

    assertEquals(Action.Type.DELETE_ROW, action.getType());
    assertEquals("row1", action.getId());
  }

  @Test
  void shouldCreateRowsActionWithVarargs() {
    Action action = ActionsFactory.rows("table1", "row1", "row2", "row3");

    assertEquals(Action.Type.ROWS, action.getType());
    assertEquals("table1", action.getId());
    assertEquals(Arrays.asList("row1", "row2", "row3"), action.getIds());
  }

  @Test
  void shouldCreateRowsActionWithEmptyVarargs() {
    Action action = ActionsFactory.rows("table1");

    assertEquals(Action.Type.ROWS, action.getType());
    assertEquals("table1", action.getId());
    assertNotNull(action.getIds());
    assertTrue(action.getIds().isEmpty());
  }

  @Test
  void shouldCreateRowsActionWithIterable() {
    List<String> ids = Arrays.asList("row1", "row2", "row3");
    Action action = ActionsFactory.rows("table1", ids);

    assertEquals(Action.Type.ROWS, action.getType());
    assertEquals("table1", action.getId());
    assertEquals(ids, action.getIds());
  }

  @Test
  void shouldCreateSetValueAction() {
    Action action = ActionsFactory.setValue("var1", "value1");

    assertEquals(Action.Type.SET_VALUE, action.getType());
    assertEquals("var1", action.getId());
    assertEquals("value1", action.getValue());
  }

  @Test
  void shouldCreateSetValueActionWithNullValue() {
    Action action = ActionsFactory.setValue("var1", null);

    assertEquals(Action.Type.SET_VALUE, action.getType());
    assertEquals("var1", action.getId());
    assertNull(action.getValue());
  }

  @Test
  void shouldCreateSetFailedAction() {
    Action action = ActionsFactory.setFailed("var1", "error message");

    assertEquals(Action.Type.SET_FAILED, action.getType());
    assertEquals("var1", action.getId());
    assertEquals("error message", action.getValue());
  }

  @Test
  void shouldCreateSetLocaleAction() {
    Action action = ActionsFactory.setLocale("en");

    assertEquals(Action.Type.SET_LOCALE, action.getType());
    assertEquals("en", action.getValue());
    assertNull(action.getId());
  }

  @Test
  void shouldCreateActionsWithRevAndMultipleActions() {
    Action action1 = ActionsFactory.next();
    Action action2 = ActionsFactory.previous();

    Actions actions = ActionsFactory.actions("rev123", action1, action2);

    assertEquals("rev123", actions.getRev());
    assertEquals(2, actions.getActions().size());
    assertTrue(actions.getActions().contains(action1));
    assertTrue(actions.getActions().contains(action2));
  }

  @Test
  void shouldCreateActionsWithNullRev() {
    Action action1 = ActionsFactory.next();

    Actions actions = ActionsFactory.actions((String) null, action1);

    assertNull(actions.getRev());
    assertEquals(1, actions.getActions().size());
    assertTrue(actions.getActions().contains(action1));
  }

  @Test
  void shouldCreateActionsWithoutRev() {
    Action action1 = ActionsFactory.next();
    Action action2 = ActionsFactory.previous();

    Actions actions = ActionsFactory.actions(action1, action2);

    assertNull(actions.getRev());
    assertEquals(2, actions.getActions().size());
    assertTrue(actions.getActions().contains(action1));
    assertTrue(actions.getActions().contains(action2));
  }

  @Test
  void shouldCreateActionsWithNoActions() {
    Actions actions = ActionsFactory.actions();

    assertNull(actions.getRev());
    assertNotNull(actions.getActions());
    assertTrue(actions.getActions().isEmpty());
  }

  @Test
  void shouldCreateActionsWithRevAndNoActions() {
    Actions actions = ActionsFactory.actions("rev123", new Action[0]);

    assertEquals("rev123", actions.getRev());
    assertNotNull(actions.getActions());
    assertTrue(actions.getActions().isEmpty());
  }

  @Test
  void shouldCreateAnswerActionWithComplexObject() {
    List<String> complexAnswer = Arrays.asList("option1", "option2");
    Action action = ActionsFactory.answer("q1", complexAnswer);

    assertEquals(Action.Type.ANSWER, action.getType());
    assertEquals("q1", action.getId());
    assertEquals(complexAnswer, action.getAnswer());
  }

  @Test
  void shouldCreateSetValueActionWithComplexObject() {
    List<Integer> complexValue = Arrays.asList(1, 2, 3);
    Action action = ActionsFactory.setValue("var1", complexValue);

    assertEquals(Action.Type.SET_VALUE, action.getType());
    assertEquals("var1", action.getId());
    assertEquals(complexValue, action.getValue());
  }
}
