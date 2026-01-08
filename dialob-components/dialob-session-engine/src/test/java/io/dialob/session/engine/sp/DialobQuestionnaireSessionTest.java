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
package io.dialob.session.engine.sp;

import io.dialob.api.proto.ActionItem;
import io.dialob.api.questionnaire.Answer;
import io.dialob.api.questionnaire.Questionnaire;
import io.dialob.questionnaire.service.api.event.QuestionnaireEventPublisher;
import io.dialob.questionnaire.service.api.session.QuestionnaireSession;
import io.dialob.session.engine.program.DialobProgram;
import io.dialob.session.engine.program.DialobSessionEvalContextFactory;
import io.dialob.session.engine.session.model.*;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DialobQuestionnaireSessionTest {

  @Test
  void testObjectVisibilityWhenShowInactiveIsFalse() {
    QuestionnaireEventPublisher eventPublisher = Mockito.mock();
    DialobSessionEvalContextFactory sessionContextFactory = Mockito.mock();
    DialobSession dialobSession = Mockito.mock();
    DialobProgram dialobProgram = Mockito.mock();
    AsyncFunctionInvoker asyncFunctionInvoker = Mockito.mock();
    Questionnaire questionnaire = new Questionnaire.Builder().metadata(new Questionnaire.Metadata.Builder().formId("123").build()).build();
    DialobQuestionnaireSession dialobQuestionnaireSession = DialobQuestionnaireSession.builder()
      .eventPublisher(eventPublisher)
      .sessionContextFactory(sessionContextFactory)
      .asyncFunctionInvoker(asyncFunctionInvoker)
      .dialobSession(dialobSession)
      .dialobProgram(dialobProgram)
      .rev(questionnaire.getRev())
      .metadata(questionnaire.getMetadata())
      .questionClientVisibility(QuestionnaireSession.QuestionClientVisibility.ONLY_ENABLED)
      .build();

    Predicate<SessionObject> predicate = dialobQuestionnaireSession.getIsVisiblePredicate();

    SessionObject sessionObject = Mockito.mock();

    when(sessionObject.isActive()).thenReturn(false);
    when(sessionObject.isDisplayItem()).thenReturn(false);
    when(sessionObject.isDisabled()).thenReturn(false);
    assertFalse(predicate.test(sessionObject));

    when(sessionObject.isActive()).thenReturn(true);
    when(sessionObject.isDisplayItem()).thenReturn(false);
    when(sessionObject.isDisabled()).thenReturn(false);
    assertFalse(predicate.test(sessionObject));

    when(sessionObject.isActive()).thenReturn(false);
    when(sessionObject.isDisplayItem()).thenReturn(true);
    when(sessionObject.isDisabled()).thenReturn(false);
    assertFalse(predicate.test(sessionObject));

    when(sessionObject.isActive()).thenReturn(true);
    when(sessionObject.isDisplayItem()).thenReturn(true);
    when(sessionObject.isDisabled()).thenReturn(false);
    assertTrue(predicate.test(sessionObject));

    when(sessionObject.isActive()).thenReturn(false);
    when(sessionObject.isDisplayItem()).thenReturn(false);
    when(sessionObject.isDisabled()).thenReturn(true);
    assertFalse(predicate.test(sessionObject));

    when(sessionObject.isActive()).thenReturn(true);
    when(sessionObject.isDisplayItem()).thenReturn(false);
    when(sessionObject.isDisabled()).thenReturn(true);
    assertFalse(predicate.test(sessionObject));

    when(sessionObject.isActive()).thenReturn(false);
    when(sessionObject.isDisplayItem()).thenReturn(true);
    when(sessionObject.isDisabled()).thenReturn(true);
    assertFalse(predicate.test(sessionObject));

    when(sessionObject.isActive()).thenReturn(true);
    when(sessionObject.isDisplayItem()).thenReturn(true);
    when(sessionObject.isDisabled()).thenReturn(true);
    assertFalse(predicate.test(sessionObject));

  }

  @Test
  void testObjectVisibilityWhenShowInactiveIsTrue() {
    QuestionnaireEventPublisher eventPublisher = Mockito.mock(QuestionnaireEventPublisher.class);
    DialobSessionEvalContextFactory sessionContextFactory = Mockito.mock(DialobSessionEvalContextFactory.class);
    DialobSession dialobSession = Mockito.mock(DialobSession.class);
    DialobProgram dialobProgram = Mockito.mock(DialobProgram.class);
    AsyncFunctionInvoker asyncFunctionInvoker = Mockito.mock(AsyncFunctionInvoker.class);
    Questionnaire questionnaire = new Questionnaire.Builder().metadata(new Questionnaire.Metadata.Builder().formId("123").build()).build();
    DialobQuestionnaireSession dialobQuestionnaireSession = DialobQuestionnaireSession.builder()
      .eventPublisher(eventPublisher)
      .sessionContextFactory(sessionContextFactory)
      .asyncFunctionInvoker(asyncFunctionInvoker)
      .dialobSession(dialobSession)
      .dialobProgram(dialobProgram)
      .rev(questionnaire.getRev())
      .metadata(questionnaire.getMetadata())
      .questionClientVisibility(QuestionnaireSession.QuestionClientVisibility.SHOW_DISABLED)
      .build();

    Predicate<SessionObject> predicate = dialobQuestionnaireSession.getIsVisiblePredicate();

    SessionObject sessionObject = Mockito.mock(SessionObject.class);

    when(sessionObject.isActive()).thenReturn(false);
    when(sessionObject.isDisplayItem()).thenReturn(false);
    when(sessionObject.isDisabled()).thenReturn(false);
    assertFalse(predicate.test(sessionObject));

    when(sessionObject.isActive()).thenReturn(true);
    when(sessionObject.isDisplayItem()).thenReturn(false);
    when(sessionObject.isDisabled()).thenReturn(false);
    assertFalse(predicate.test(sessionObject));

    when(sessionObject.isActive()).thenReturn(false);
    when(sessionObject.isDisplayItem()).thenReturn(true);
    when(sessionObject.isDisabled()).thenReturn(false);
    assertFalse(predicate.test(sessionObject));

    when(sessionObject.isActive()).thenReturn(true);
    when(sessionObject.isDisplayItem()).thenReturn(true);
    when(sessionObject.isDisabled()).thenReturn(false);
    assertTrue(predicate.test(sessionObject));

    when(sessionObject.isActive()).thenReturn(false);
    when(sessionObject.isDisplayItem()).thenReturn(false);
    when(sessionObject.isDisabled()).thenReturn(true);
    assertFalse(predicate.test(sessionObject));

    when(sessionObject.isActive()).thenReturn(true);
    when(sessionObject.isDisplayItem()).thenReturn(false);
    when(sessionObject.isDisabled()).thenReturn(true);
    assertFalse(predicate.test(sessionObject));

    when(sessionObject.isActive()).thenReturn(false);
    when(sessionObject.isDisplayItem()).thenReturn(true);
    when(sessionObject.isDisabled()).thenReturn(true);
    assertFalse(predicate.test(sessionObject));

    when(sessionObject.isActive()).thenReturn(true);
    when(sessionObject.isDisplayItem()).thenReturn(true);
    when(sessionObject.isDisabled()).thenReturn(true); // Also whenDisabledUpdatedEvent ones are displayed
    assertTrue(predicate.test(sessionObject));
  }

  @Test
  void testObjectVisibilityWhenQuestionClientVisiblityIsAll() {
    QuestionnaireEventPublisher eventPublisher = Mockito.mock(QuestionnaireEventPublisher.class);
    DialobSessionEvalContextFactory sessionContextFactory = Mockito.mock(DialobSessionEvalContextFactory.class);
    DialobSession dialobSession = Mockito.mock(DialobSession.class);
    DialobProgram dialobProgram = Mockito.mock(DialobProgram.class);
    AsyncFunctionInvoker asyncFunctionInvoker = Mockito.mock(AsyncFunctionInvoker.class);
    Questionnaire questionnaire = new Questionnaire.Builder().metadata(new Questionnaire.Metadata.Builder().formId("123").build()).build();
    DialobQuestionnaireSession dialobQuestionnaireSession = DialobQuestionnaireSession.builder()
      .eventPublisher(eventPublisher)
      .sessionContextFactory(sessionContextFactory)
      .asyncFunctionInvoker(asyncFunctionInvoker)
      .dialobSession(dialobSession)
      .dialobProgram(dialobProgram)
      .rev(questionnaire.getRev())
      .metadata(questionnaire.getMetadata())
      .questionClientVisibility(QuestionnaireSession.QuestionClientVisibility.ALL)
      .build();

    Predicate<SessionObject> predicate = dialobQuestionnaireSession.getIsVisiblePredicate();

    SessionObject sessionObject = Mockito.mock(SessionObject.class);

    when(sessionObject.isActive()).thenReturn(false);
    when(sessionObject.isDisplayItem()).thenReturn(false);
    when(sessionObject.isDisabled()).thenReturn(false);
    assertFalse(predicate.test(sessionObject));

    when(sessionObject.isActive()).thenReturn(true);
    when(sessionObject.isDisplayItem()).thenReturn(false);
    when(sessionObject.isDisabled()).thenReturn(false);
    assertFalse(predicate.test(sessionObject));

    when(sessionObject.isActive()).thenReturn(false);
    when(sessionObject.isDisplayItem()).thenReturn(true);
    when(sessionObject.isDisabled()).thenReturn(false);
    assertTrue(predicate.test(sessionObject));

    when(sessionObject.isActive()).thenReturn(true);
    when(sessionObject.isDisplayItem()).thenReturn(true);
    when(sessionObject.isDisabled()).thenReturn(false);
    assertTrue(predicate.test(sessionObject));

    when(sessionObject.isActive()).thenReturn(false);
    when(sessionObject.isDisplayItem()).thenReturn(false);
    when(sessionObject.isDisabled()).thenReturn(true);
    assertFalse(predicate.test(sessionObject));

    when(sessionObject.isActive()).thenReturn(true);
    when(sessionObject.isDisplayItem()).thenReturn(false);
    when(sessionObject.isDisabled()).thenReturn(true);
    assertFalse(predicate.test(sessionObject));

    when(sessionObject.isActive()).thenReturn(false);
    when(sessionObject.isDisplayItem()).thenReturn(true);
    when(sessionObject.isDisabled()).thenReturn(true);
    assertTrue(predicate.test(sessionObject));

    when(sessionObject.isActive()).thenReturn(true);
    when(sessionObject.isDisplayItem()).thenReturn(true);
    when(sessionObject.isDisabled()).thenReturn(true); // Also whenDisabledUpdatedEvent ones are displayed
    assertTrue(predicate.test(sessionObject));
  }

  @Test
  void shouldConvertOldRows() {
    Assertions.assertArrayEquals(
      new String[] {"g.1"},
      DialobQuestionnaireSession.convertRows(List.of("g[1]")));
  }

  @Test
  void shouldPersistRowGroupContainerIntoAnswers() {
    // given
    QuestionnaireEventPublisher eventPublisher = Mockito.mock(QuestionnaireEventPublisher.class);
    DialobSessionEvalContextFactory sessionContextFactory = Mockito.mock(DialobSessionEvalContextFactory.class);

    ItemId id2 = IdUtils.toId("rowg");
    ItemState rowItemState = ItemState.builder()
      .id(id2)
      .prototypeId(null)
      .type("rowgroup")
      .view(null)
      .status(ItemState.Status.NEW)
      .bits(ItemState.DISPLAY_ITEM_BIT | ItemState.ACTIVE_BIT | ItemState.ROWS_CAN_BE_ADDED_BIT)
      .valueSetId(null)
      .answer(null)
      .value(null)
      .defaultValue(null)
      .activePage(null)
      .build();
    ItemId id1 = IdUtils.toId("rowg2");
    ItemState rowItemState2 = ItemState.builder()
      .id(id1)
      .prototypeId(null)
      .type("rowgroup")
      .view(null)
      .status(ItemState.Status.NEW)
      .bits(ItemState.DISPLAY_ITEM_BIT | ItemState.ACTIVE_BIT | ItemState.ROWS_CAN_BE_ADDED_BIT)
      .valueSetId(null)
      .answer(List.of(1, 2, 3))
      .value(List.of(1, 2, 3))
      .defaultValue(null)
      .activePage(null)
      .build();
    ItemId id = IdUtils.toId("rowg3.1");
    ItemId prototypeId = IdUtils.toId("rowg3");
    ItemState rowItemState3 = ItemState.builder()
      .id(id)
      .prototypeId(prototypeId)
      .type("rowgroup")
      .view(null)
      .status(ItemState.Status.NEW)
      .bits(ItemState.DISPLAY_ITEM_BIT | ItemState.ACTIVE_BIT | ItemState.ROWS_CAN_BE_ADDED_BIT)
      .valueSetId(null)
      .answer(List.of(1, 2, 3))
      .value(List.of(1, 2, 3))
      .defaultValue(null)
      .activePage(null)
      .build();

    DialobSession dialobSession = DialobSession.of(
      "tenant",
      "id",
      0,
      "rev", null, null, null,
      "fi",
      List.of(rowItemState, rowItemState2, rowItemState3),
      List.of(),
      List.of(),
      List.of(), List.of());
    DialobProgram dialobProgram = Mockito.mock(DialobProgram.class);
    AsyncFunctionInvoker asyncFunctionInvoker = Mockito.mock(AsyncFunctionInvoker.class);
    Questionnaire questionnaire = new Questionnaire.Builder().metadata(new Questionnaire.Metadata.Builder().formId("123").build()).build();
    DialobQuestionnaireSession session = DialobQuestionnaireSession.builder()
      .eventPublisher(eventPublisher)
      .sessionContextFactory(sessionContextFactory)
      .asyncFunctionInvoker(asyncFunctionInvoker)
      .dialobSession(dialobSession)
      .dialobProgram(dialobProgram)
      .rev(questionnaire.getRev())
      .metadata(questionnaire.getMetadata())
      .questionClientVisibility(QuestionnaireSession.QuestionClientVisibility.ONLY_ENABLED)
      .build();

    when(dialobProgram.getItem(any())).thenReturn(Optional.empty());

    // when
    final List<Answer> answers = session.getAnswers();

    // expect
    assertThat(answers).extracting("id", "value", "type")
      .containsExactlyInAnyOrder(
        Tuple.tuple("rowg", null, null),
        Tuple.tuple("rowg2", List.of(1,2,3), null)
      );

    assertThat(session.getActiveItems()).containsOnlyOnce("rowg3.1", "rowg", "rowg2");

    verify(dialobProgram,times(2)).getItem(any());
    verifyNoMoreInteractions(eventPublisher, dialobProgram);
  }


  @Test
  void shouldNotHandleAnswersOnCompletedQuestionnaires() {
    // given
    QuestionnaireEventPublisher eventPublisher = Mockito.mock(QuestionnaireEventPublisher.class);
    DialobSessionEvalContextFactory sessionContextFactory = Mockito.mock(DialobSessionEvalContextFactory.class);

    ItemId id2 = IdUtils.toId("rowg");
    ItemState rowItemState = ItemState.builder()
      .id(id2)
      .prototypeId(null)
      .type("rowgroup")
      .view(null)
      .status(ItemState.Status.NEW)
      .bits(ItemState.DISPLAY_ITEM_BIT | ItemState.ACTIVE_BIT | ItemState.ROWS_CAN_BE_ADDED_BIT)
      .valueSetId(null)
      .answer(null)
      .value(null)
      .defaultValue(null)
      .activePage(null)
      .build();
    ItemId id1 = IdUtils.toId("rowg2");
    ItemState rowItemState2 = ItemState.builder()
      .id(id1)
      .prototypeId(null)
      .type("rowgroup")
      .view(null)
      .status(ItemState.Status.NEW)
      .bits(ItemState.DISPLAY_ITEM_BIT | ItemState.ACTIVE_BIT | ItemState.ROWS_CAN_BE_ADDED_BIT)
      .valueSetId(null)
      .answer(List.of(1, 2, 3))
      .value(List.of(1, 2, 3))
      .defaultValue(null)
      .activePage(null)
      .build();
    ItemId id = IdUtils.toId("rowg3.1");
    ItemId prototypeId = IdUtils.toId("rowg3");
    ItemState rowItemState3 = ItemState.builder()
      .id(id)
      .prototypeId(prototypeId)
      .type("rowgroup")
      .view(null)
      .status(ItemState.Status.NEW)
      .bits(ItemState.DISPLAY_ITEM_BIT | ItemState.ACTIVE_BIT | ItemState.ROWS_CAN_BE_ADDED_BIT)
      .valueSetId(null)
      .answer(List.of(1, 2, 3))
      .value(List.of(1, 2, 3))
      .defaultValue(null)
      .activePage(null)
      .build();

    var opened = Instant.ofEpochMilli(1L);
    var lastAnswer = Instant.ofEpochMilli(2L);

    DialobSession dialobSession = DialobSession.of(
      "tenant",
      "id",
      0,
      "rev", lastAnswer, null, opened,
      "fi",
      List.of(rowItemState, rowItemState2, rowItemState3),
      List.of(),
      List.of(),
      List.of(),
      List.of());
    dialobSession.complete();

    DialobProgram dialobProgram = Mockito.mock(DialobProgram.class);
    AsyncFunctionInvoker asyncFunctionInvoker = Mockito.mock(AsyncFunctionInvoker.class);
    Questionnaire questionnaire = new Questionnaire.Builder().metadata(new Questionnaire.Metadata.Builder().formId("123").build()).build();
    DialobQuestionnaireSession session = DialobQuestionnaireSession.builder()
      .eventPublisher(eventPublisher)
      .sessionContextFactory(sessionContextFactory)
      .asyncFunctionInvoker(asyncFunctionInvoker)
      .dialobSession(dialobSession)
      .dialobProgram(dialobProgram)
      .rev(questionnaire.getRev())
      .metadata(questionnaire.getMetadata())
      .questionClientVisibility(QuestionnaireSession.QuestionClientVisibility.ONLY_ENABLED)
      .build();

    when(dialobProgram.getItem(any())).thenReturn(Optional.empty());

    // when
    QuestionnaireSession.DispatchActionsResult result = session.dispatchActions(List.of());

    // expect
    assertNull(result.actions().getActions());

    assertNotNull(session.getDialobSession().getCompleted());
    assertEquals(Instant.ofEpochMilli(1L), session.getDialobSession().getOpened());
    assertEquals(Instant.ofEpochMilli(2L), session.getDialobSession().getLastAnswer());

    verifyNoMoreInteractions(eventPublisher, dialobProgram);
  }

  @Test
  void shouldReturnVisibleItems() {
    // given
    QuestionnaireEventPublisher eventPublisher = Mockito.mock(QuestionnaireEventPublisher.class);
    DialobSessionEvalContextFactory sessionContextFactory = Mockito.mock(DialobSessionEvalContextFactory.class);
    DialobProgram dialobProgram = Mockito.mock(DialobProgram.class);
    AsyncFunctionInvoker asyncFunctionInvoker = Mockito.mock(AsyncFunctionInvoker.class);

    ItemState item1 = Mockito.mock(ItemState.class);
    when(item1.id()).thenReturn(IdUtils.toId("item1"));
    when(item1.type()).thenReturn("text");
    when(item1.isActive()).thenReturn(true);
    when(item1.isDisplayItem()).thenReturn(true);
    when(item1.isDisabled()).thenReturn(false);

    ItemState item2 = Mockito.mock(ItemState.class);
    when(item2.id()).thenReturn(IdUtils.toId("item2"));
    when(item2.type()).thenReturn("text");
    when(item2.isActive()).thenReturn(false);
    when(item2.isDisplayItem()).thenReturn(true);
    when(item2.isDisabled()).thenReturn(false);

    ItemState item3 = Mockito.mock(ItemState.class);
    when(item3.id()).thenReturn(IdUtils.toId("item3"));
    when(item3.type()).thenReturn("note");
    when(item3.isActive()).thenReturn(true);
    when(item3.isDisplayItem()).thenReturn(true);
    when(item3.isDisabled()).thenReturn(false);

    DialobSession dialobSession = DialobSession.of(
      "tenant",
      "id",
      0,
      "rev", null, null, null,
      "fi",
      List.of(item1, item2, item3),
      List.of(),
      List.of(),
      List.of(), List.of());

    Questionnaire questionnaire = new Questionnaire.Builder().metadata(new Questionnaire.Metadata.Builder().formId("123").build()).build();
    DialobQuestionnaireSession session = DialobQuestionnaireSession.builder()
      .eventPublisher(eventPublisher)
      .sessionContextFactory(sessionContextFactory)
      .asyncFunctionInvoker(asyncFunctionInvoker)
      .dialobSession(dialobSession)
      .dialobProgram(dialobProgram)
      .rev(questionnaire.getRev())
      .metadata(questionnaire.getMetadata())
      .questionClientVisibility(QuestionnaireSession.QuestionClientVisibility.ONLY_ENABLED)
      .build();

    // when
    List<ActionItem> visibleItems = session.getVisibleItems();

    // then
    assertThat(visibleItems).extracting("id")
      .containsExactly("item1", "item3");
  }

  @Test
  void shouldReturnVisibleItemsWithDifferentVisibilities() {
    // given
    QuestionnaireEventPublisher eventPublisher = Mockito.mock(QuestionnaireEventPublisher.class);
    DialobSessionEvalContextFactory sessionContextFactory = Mockito.mock(DialobSessionEvalContextFactory.class);
    DialobProgram dialobProgram = Mockito.mock(DialobProgram.class);
    AsyncFunctionInvoker asyncFunctionInvoker = Mockito.mock(AsyncFunctionInvoker.class);

    ItemState activeItem = Mockito.mock(ItemState.class);
    when(activeItem.id()).thenReturn(IdUtils.toId("active"));
    when(activeItem.type()).thenReturn("text");
    when(activeItem.isActive()).thenReturn(true);
    when(activeItem.isDisplayItem()).thenReturn(true);
    when(activeItem.isDisabled()).thenReturn(false);

    ItemState inactiveItem = Mockito.mock(ItemState.class);
    when(inactiveItem.id()).thenReturn(IdUtils.toId("inactive"));
    when(inactiveItem.type()).thenReturn("text");
    when(inactiveItem.isActive()).thenReturn(false);
    when(inactiveItem.isDisplayItem()).thenReturn(true);
    when(inactiveItem.isDisabled()).thenReturn(false);

    ItemState disabledItem = Mockito.mock(ItemState.class);
    when(disabledItem.id()).thenReturn(IdUtils.toId("disabled"));
    when(disabledItem.type()).thenReturn("text");
    when(disabledItem.isActive()).thenReturn(true);
    when(disabledItem.isDisplayItem()).thenReturn(true);
    when(disabledItem.isDisabled()).thenReturn(true);

    DialobSession dialobSession = DialobSession.of(
      "tenant",
      "id",
      0,
      "rev", null, null, null,
      "fi",
      List.of(activeItem, inactiveItem, disabledItem),
      List.of(),
      List.of(),
      List.of(), List.of());

    Questionnaire questionnaire = new Questionnaire.Builder().metadata(new Questionnaire.Metadata.Builder().formId("123").build()).build();

    // Case 1: ONLY_ENABLED
    DialobQuestionnaireSession sessionOnlyEnabled = DialobQuestionnaireSession.builder()
      .eventPublisher(eventPublisher)
      .sessionContextFactory(sessionContextFactory)
      .asyncFunctionInvoker(asyncFunctionInvoker)
      .dialobSession(dialobSession)
      .dialobProgram(dialobProgram)
      .rev(questionnaire.getRev())
      .metadata(questionnaire.getMetadata())
      .questionClientVisibility(QuestionnaireSession.QuestionClientVisibility.ONLY_ENABLED)
      .build();

    assertThat(sessionOnlyEnabled.getVisibleItems()).extracting("id")
      .containsExactly("active");

    // Case 2: SHOW_DISABLED
    DialobQuestionnaireSession sessionShowDisabled = DialobQuestionnaireSession.builder()
      .eventPublisher(eventPublisher)
      .sessionContextFactory(sessionContextFactory)
      .asyncFunctionInvoker(asyncFunctionInvoker)
      .dialobSession(dialobSession)
      .dialobProgram(dialobProgram)
      .rev(questionnaire.getRev())
      .metadata(questionnaire.getMetadata())
      .questionClientVisibility(QuestionnaireSession.QuestionClientVisibility.SHOW_DISABLED)
      .build();

    assertThat(sessionShowDisabled.getVisibleItems()).extracting("id")
      .containsExactlyInAnyOrder("active", "disabled");

    // Case 3: ALL
    DialobQuestionnaireSession sessionAll = DialobQuestionnaireSession.builder()
      .eventPublisher(eventPublisher)
      .sessionContextFactory(sessionContextFactory)
      .asyncFunctionInvoker(asyncFunctionInvoker)
      .dialobSession(dialobSession)
      .dialobProgram(dialobProgram)
      .rev(questionnaire.getRev())
      .metadata(questionnaire.getMetadata())
      .questionClientVisibility(QuestionnaireSession.QuestionClientVisibility.ALL)
      .build();

    assertThat(sessionAll.getVisibleItems()).extracting("id")
      .containsExactlyInAnyOrder("active", "inactive", "disabled");
  }

}
