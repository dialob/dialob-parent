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

import io.dialob.api.questionnaire.Answer;
import io.dialob.api.questionnaire.ImmutableQuestionnaire;
import io.dialob.api.questionnaire.ImmutableQuestionnaireMetadata;
import io.dialob.api.questionnaire.Questionnaire;
import io.dialob.questionnaire.service.api.event.QuestionnaireEventPublisher;
import io.dialob.questionnaire.service.api.session.QuestionnaireSession;
import io.dialob.session.engine.program.DialobProgram;
import io.dialob.session.engine.program.DialobSessionEvalContextFactory;
import io.dialob.session.engine.session.model.DialobSession;
import io.dialob.session.engine.session.model.IdUtils;
import io.dialob.session.engine.session.model.ItemState;
import io.dialob.session.engine.session.model.SessionObject;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.*;
import java.util.function.Predicate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class DialobQuestionnaireSessionTest {

  @Test
  void testObjectVisibilityWhenShowInactiveIsFalse() {
    QuestionnaireEventPublisher eventPublisher = Mockito.mock(QuestionnaireEventPublisher.class);
    DialobSessionEvalContextFactory sessionContextFactory = Mockito.mock(DialobSessionEvalContextFactory.class);
    DialobSession dialobSession = Mockito.mock(DialobSession.class);
    DialobProgram dialobProgram = Mockito.mock(DialobProgram.class);
    AsyncFunctionInvoker asyncFunctionInvoker = Mockito.mock(AsyncFunctionInvoker.class);
    Questionnaire questionnaire = ImmutableQuestionnaire.builder().metadata(ImmutableQuestionnaireMetadata.builder().formId("123").build()).build();
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
    Questionnaire questionnaire = ImmutableQuestionnaire.builder().metadata(ImmutableQuestionnaireMetadata.builder().formId("123").build()).build();
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
    Questionnaire questionnaire = ImmutableQuestionnaire.builder().metadata(ImmutableQuestionnaireMetadata.builder().formId("123").build()).build();
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
      DialobQuestionnaireSession.convertRows(Arrays.asList("g[1]")));
  }

  @Test
  void shouldPersistRowGroupContainerIntoAnswers() {
    // given
    QuestionnaireEventPublisher eventPublisher = Mockito.mock(QuestionnaireEventPublisher.class);
    DialobSessionEvalContextFactory sessionContextFactory = Mockito.mock(DialobSessionEvalContextFactory.class);

    ItemState rowItemState = new ItemState(IdUtils.toId("rowg"), null, "rowgroup", null, true, null, null, null, null, null);
    ItemState rowItemState2 = new ItemState(IdUtils.toId("rowg2"), null, "rowgroup", null, true, null, Arrays.asList(1,2,3), Arrays.asList(1,2,3), null, null);
    ItemState rowItemState3 = new ItemState(IdUtils.toId("rowg3.1"), IdUtils.toId("rowg3"), "rowgroup", null, true, null, Arrays.asList(1,2,3), Arrays.asList(1,2,3), null, null);

    DialobSession dialobSession = new DialobSession(
      "tenant",
      "id",
      "rev",
      "fi",
      Arrays.asList(rowItemState, rowItemState2, rowItemState3),
      new ArrayList<>(),
      new ArrayList<>(),
      new ArrayList<>(),
      new ArrayList<>(), null, null, null);
    DialobProgram dialobProgram = Mockito.mock(DialobProgram.class);
    AsyncFunctionInvoker asyncFunctionInvoker = Mockito.mock(AsyncFunctionInvoker.class);
    Questionnaire questionnaire = ImmutableQuestionnaire.builder().metadata(ImmutableQuestionnaireMetadata.builder().formId("123").build()).build();
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
        Tuple.tuple("rowg2", Arrays.asList(1,2,3), null)
      );

    assertThat(session.getActiveItems()).containsOnlyOnce("rowg3.1", "rowg", "rowg2");

    verify(dialobProgram,times(2)).getItem(any());
    verifyNoMoreInteractions(eventPublisher, dialobProgram);
  }


  @Test
  void shouldNotHandleAnswersOnCompletedQuestionannaires() {
    // given
    QuestionnaireEventPublisher eventPublisher = Mockito.mock(QuestionnaireEventPublisher.class);
    DialobSessionEvalContextFactory sessionContextFactory = Mockito.mock(DialobSessionEvalContextFactory.class);

    ItemState rowItemState = new ItemState(IdUtils.toId("rowg"), null, "rowgroup", null, true, null, null, null, null, null);
    ItemState rowItemState2 = new ItemState(IdUtils.toId("rowg2"), null, "rowgroup", null, true, null, Arrays.asList(1,2,3), Arrays.asList(1,2,3), null, null);
    ItemState rowItemState3 = new ItemState(IdUtils.toId("rowg3.1"), IdUtils.toId("rowg3"), "rowgroup", null, true, null, Arrays.asList(1,2,3), Arrays.asList(1,2,3), null, null);

    Date opened = new Date(1L);
    Date lastAnswer = new Date(2L);

    DialobSession dialobSession = new DialobSession(
      "tenant",
      "id",
      "rev",
      "fi",
      Arrays.asList(rowItemState, rowItemState2, rowItemState3),
      new ArrayList<>(),
      new ArrayList<>(),
      new ArrayList<>(),
      new ArrayList<>(), null, opened, lastAnswer);
    dialobSession.complete();

    DialobProgram dialobProgram = Mockito.mock(DialobProgram.class);
    AsyncFunctionInvoker asyncFunctionInvoker = Mockito.mock(AsyncFunctionInvoker.class);
    Questionnaire questionnaire = ImmutableQuestionnaire.builder().metadata(ImmutableQuestionnaireMetadata.builder().formId("123").build()).build();
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
    QuestionnaireSession.DispatchActionsResult result = session.dispatchActions(Arrays.asList());

    // expect
    assertNull(result.getActions().getActions());

    assertNotNull(session.getDialobSession().getCompleted());
    assertEquals(1L, session.getDialobSession().getOpened().toEpochMilli());
    assertEquals(2L, session.getDialobSession().getLastAnswer().toEpochMilli());

    verifyNoMoreInteractions(eventPublisher, dialobProgram);
  }



}
