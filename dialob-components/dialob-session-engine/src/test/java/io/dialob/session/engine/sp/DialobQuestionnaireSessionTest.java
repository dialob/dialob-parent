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
import io.dialob.api.proto.ValueSet;
import io.dialob.api.questionnaire.Answer;
import io.dialob.api.questionnaire.Error;
import io.dialob.api.questionnaire.Questionnaire;
import io.dialob.api.questionnaire.VariableValue;
import io.dialob.questionnaire.service.api.event.QuestionnaireEventPublisher;
import io.dialob.questionnaire.service.api.session.QuestionnaireSession;
import io.dialob.session.engine.program.DialobProgram;
import io.dialob.session.engine.session.model.*;
import io.dialob.session.engine.session.protobuf.StateReader;
import io.dialob.session.engine.session.protobuf.StateWriter;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.Instant;
import java.util.*;
import java.util.function.Predicate;

import static io.dialob.session.engine.session.model.DialobSession.QUESTIONNAIRE_REF;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DialobQuestionnaireSessionTest {

  static DialobSession dialobSessionOf(
    Instant lastUpdate,
    Instant opened,
    List<ItemState> itemStates) {
    var itemStatesMap = new HashMap<ItemId, ItemState>();
    var itemPrototypesMap = new HashMap<ItemId, ItemState>();
    var valueSetStatesMap = new HashMap<ValueSetId, ValueSetState>();
    var errorStatesMap = new HashMap<ErrorId, ErrorState>();
    var errorPrototypesMap = new HashMap<ErrorId, ErrorState>();
    itemStates.forEach(item -> itemStatesMap.put(item.id(), item));
    return new DialobSession(
      "tenant",
      "id",
      "rev",
      lastUpdate,
      null,
      opened,
      "fi",
      new ItemStates.Builder()
        .itemStates(itemStatesMap)
        .errorStates(errorStatesMap)
        .valueSetStates(valueSetStatesMap)
        .build(),
      new ItemStates.Builder()
        .itemStates(itemPrototypesMap)
        .errorStates(errorPrototypesMap)
        .build()
    );
  }

  @Test
  void testObjectVisibilityWhenShowInactiveIsFalse() {
    var serviceFacade = new DialobQuestionnaireSessionServiceFacade(mock(),mock(),mock());
    DialobSession dialobSession = mock();
    DialobProgram dialobProgram = mock();
    Questionnaire questionnaire = new Questionnaire.Builder().metadata(new Questionnaire.Metadata.Builder().formId("123").build()).build();
    DialobQuestionnaireSession dialobQuestionnaireSession = DialobQuestionnaireSession.builder()
      .serviceFacade(serviceFacade)
      .dialobSession(dialobSession)
      .dialobProgram(dialobProgram)
      .rev(questionnaire.getRev())
      .metadata(questionnaire.getMetadata())
      .questionClientVisibility(QuestionnaireSession.QuestionClientVisibility.ONLY_ENABLED)
      .build();

    Predicate<SessionObject> predicate = dialobQuestionnaireSession.getIsVisiblePredicate();

    SessionObject sessionObject = mock();

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
    var serviceFacade = new DialobQuestionnaireSessionServiceFacade(mock(),mock(),mock());
    DialobSession dialobSession = mock(DialobSession.class);
    DialobProgram dialobProgram = mock(DialobProgram.class);
    Questionnaire questionnaire = new Questionnaire.Builder().metadata(new Questionnaire.Metadata.Builder().formId("123").build()).build();
    DialobQuestionnaireSession dialobQuestionnaireSession = DialobQuestionnaireSession.builder()
      .serviceFacade(serviceFacade)
      .dialobSession(dialobSession)
      .dialobProgram(dialobProgram)
      .rev(questionnaire.getRev())
      .metadata(questionnaire.getMetadata())
      .questionClientVisibility(QuestionnaireSession.QuestionClientVisibility.SHOW_DISABLED)
      .build();

    Predicate<SessionObject> predicate = dialobQuestionnaireSession.getIsVisiblePredicate();

    SessionObject sessionObject = mock(SessionObject.class);

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
    var serviceFacade = new DialobQuestionnaireSessionServiceFacade(mock(),mock(),mock());
    DialobSession dialobSession = mock();
    DialobProgram dialobProgram = mock();
    Questionnaire questionnaire = new Questionnaire.Builder().metadata(new Questionnaire.Metadata.Builder().formId("123").build()).build();
    DialobQuestionnaireSession dialobQuestionnaireSession = DialobQuestionnaireSession.builder()
      .serviceFacade(serviceFacade)
      .dialobSession(dialobSession)
      .dialobProgram(dialobProgram)
      .rev(questionnaire.getRev())
      .metadata(questionnaire.getMetadata())
      .questionClientVisibility(QuestionnaireSession.QuestionClientVisibility.ALL)
      .build();

    Predicate<SessionObject> predicate = dialobQuestionnaireSession.getIsVisiblePredicate();

    SessionObject sessionObject = mock(SessionObject.class);

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
    QuestionnaireEventPublisher eventPublisher = mock(QuestionnaireEventPublisher.class);

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

    DialobSession dialobSession = dialobSessionOf(
      null, null,
      List.of(rowItemState, rowItemState2, rowItemState3)
    );
    DialobProgram dialobProgram = mock(DialobProgram.class);
    var serviceFacade = new DialobQuestionnaireSessionServiceFacade(mock(),mock(),mock());
    Questionnaire questionnaire = new Questionnaire.Builder().metadata(new Questionnaire.Metadata.Builder().formId("123").build()).build();
    DialobQuestionnaireSession session = DialobQuestionnaireSession.builder()
      .serviceFacade(serviceFacade)
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
    QuestionnaireEventPublisher eventPublisher = mock(QuestionnaireEventPublisher.class);

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

    DialobSession dialobSession = dialobSessionOf(
      lastAnswer, opened,
      List.of(rowItemState, rowItemState2, rowItemState3)
    );
    dialobSession.complete();

    DialobProgram dialobProgram = mock(DialobProgram.class);
    var serviceFacade = new DialobQuestionnaireSessionServiceFacade(mock(),mock(),mock());
    Questionnaire questionnaire = new Questionnaire.Builder().metadata(new Questionnaire.Metadata.Builder().formId("123").build()).build();
    DialobQuestionnaireSession session = DialobQuestionnaireSession.builder()
      .serviceFacade(serviceFacade)
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
    var serviceFacade = new DialobQuestionnaireSessionServiceFacade(mock(),mock(),mock());
    DialobProgram dialobProgram = mock(DialobProgram.class);

    ItemState item1 = mock(ItemState.class);
    when(item1.id()).thenReturn(IdUtils.toId("item1"));
    when(item1.type()).thenReturn("text");
    when(item1.isActive()).thenReturn(true);
    when(item1.isDisplayItem()).thenReturn(true);
    when(item1.isDisabled()).thenReturn(false);

    ItemState item2 = mock(ItemState.class);
    when(item2.id()).thenReturn(IdUtils.toId("item2"));
    when(item2.type()).thenReturn("text");
    when(item2.isActive()).thenReturn(false);
    when(item2.isDisplayItem()).thenReturn(true);
    when(item2.isDisabled()).thenReturn(false);

    ItemState item3 = mock(ItemState.class);
    when(item3.id()).thenReturn(IdUtils.toId("item3"));
    when(item3.type()).thenReturn("note");
    when(item3.isActive()).thenReturn(true);
    when(item3.isDisplayItem()).thenReturn(true);
    when(item3.isDisabled()).thenReturn(false);

    DialobSession dialobSession = dialobSessionOf(
      null, null,
      List.of(item1, item2, item3)
    );

    Questionnaire questionnaire = new Questionnaire.Builder().metadata(new Questionnaire.Metadata.Builder().formId("123").build()).build();
    DialobQuestionnaireSession session = DialobQuestionnaireSession.builder()
      .serviceFacade(serviceFacade)
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
    var serviceFacade = new DialobQuestionnaireSessionServiceFacade(mock(),mock(),mock());
    DialobProgram dialobProgram = mock(DialobProgram.class);

    ItemState activeItem = mock(ItemState.class);
    when(activeItem.id()).thenReturn(IdUtils.toId("active"));
    when(activeItem.type()).thenReturn("text");
    when(activeItem.isActive()).thenReturn(true);
    when(activeItem.isDisplayItem()).thenReturn(true);
    when(activeItem.isDisabled()).thenReturn(false);

    ItemState inactiveItem = mock(ItemState.class);
    when(inactiveItem.id()).thenReturn(IdUtils.toId("inactive"));
    when(inactiveItem.type()).thenReturn("text");
    when(inactiveItem.isActive()).thenReturn(false);
    when(inactiveItem.isDisplayItem()).thenReturn(true);
    when(inactiveItem.isDisabled()).thenReturn(false);

    ItemState disabledItem = mock(ItemState.class);
    when(disabledItem.id()).thenReturn(IdUtils.toId("disabled"));
    when(disabledItem.type()).thenReturn("text");
    when(disabledItem.isActive()).thenReturn(true);
    when(disabledItem.isDisplayItem()).thenReturn(true);
    when(disabledItem.isDisabled()).thenReturn(true);

    DialobSession dialobSession = dialobSessionOf(
      null, null,
      List.of(activeItem, inactiveItem, disabledItem)
    );

    Questionnaire questionnaire = new Questionnaire.Builder().metadata(new Questionnaire.Metadata.Builder().formId("123").build()).build();

    // Case 1: ONLY_ENABLED
    DialobQuestionnaireSession sessionOnlyEnabled = DialobQuestionnaireSession.builder()
      .serviceFacade(serviceFacade)
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
      .serviceFacade(serviceFacade)
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
      .serviceFacade(serviceFacade)
      .dialobSession(dialobSession)
      .dialobProgram(dialobProgram)
      .rev(questionnaire.getRev())
      .metadata(questionnaire.getMetadata())
      .questionClientVisibility(QuestionnaireSession.QuestionClientVisibility.ALL)
      .build();

    assertThat(sessionAll.getVisibleItems()).extracting("id")
      .containsExactlyInAnyOrder("active", "inactive", "disabled");
  }

  @Test
  void shouldWriteAndReadDialobQuestionnaireSession() throws IOException {
    var serviceFacade = new DialobQuestionnaireSessionServiceFacade(mock(),mock(),mock());
    DialobProgram dialobProgram = mock(DialobProgram.class);
    DialobSession dialobSession = dialobSessionOf(Instant.now(), Instant.now(), List.of());

    Questionnaire.Metadata metadata = new Questionnaire.Metadata.Builder()
      .formId("form1")
      .status(Questionnaire.Metadata.Status.OPEN)
      .build();

    DialobQuestionnaireSession session = DialobQuestionnaireSession.builder()
      .serviceFacade(serviceFacade)
      .dialobSession(dialobSession)
      .dialobProgram(dialobProgram)
      .rev("rev1")
      .metadata(metadata)
      .questionClientVisibility(QuestionnaireSession.QuestionClientVisibility.ONLY_ENABLED)
      .build();

    StateWriter writer = mock(StateWriter.class);
    session.writeTo(writer);

    verify(writer).writeNullableString("rev1");
    verify(writer).writeInt(QuestionnaireSession.QuestionClientVisibility.ONLY_ENABLED.ordinal());
    verify(writer, times(8)).writeInt(DialobQuestionnaireSession.State.NEW.ordinal());
    verify(writer).writeString("OPEN");
    verify(writer).writeString("form1");
    // ... verify other metadata writes

    StateReader reader = mock(StateReader.class);
    when(reader.readNullableString()).thenReturn("rev1", "rev2", null, "fi", "label", "creator", "owner", "tenant", "submitUrl", "id", "rev");
    when(reader.readInt()).thenReturn(
      QuestionnaireSession.QuestionClientVisibility.ONLY_ENABLED.ordinal(),
      DialobQuestionnaireSession.State.NEW.ordinal(),
      0, // additional properties count
      0, // item states count
      0  // prototypes count
    );
    when(reader.readString()).thenReturn("OPEN", "form1", "tenant", "fi");
    when(reader.readDate()).thenReturn(Instant.now());

    DialobQuestionnaireSession.Builder builder = DialobQuestionnaireSession.builder();
    builder.readFrom(reader);
    DialobQuestionnaireSession readSession = builder
      .serviceFacade(serviceFacade)
      .dialobProgram(dialobProgram)
      .build();

    assertEquals("rev1", readSession.getRev());
    assertEquals(QuestionnaireSession.QuestionClientVisibility.ONLY_ENABLED, readSession.getQuestionClientVisibility());
    assertEquals(Questionnaire.Metadata.Status.NEW, readSession.getQuestionnaireMetadata().getStatus());
    assertEquals("form1", readSession.getQuestionnaireMetadata().getFormId());
  }

  @Test
  void shouldReturnProvidedValueSets() {
    var serviceFacade = new DialobQuestionnaireSessionServiceFacade(mock(),mock(),mock());
    DialobProgram dialobProgram = mock(DialobProgram.class);

    ItemState questionnaire = mock();

    ValueSetState valueSetState1 = new ValueSetState(new ValueSetId("vs1"), List.of(
      new ValueSetState.Entry("k1", "v1", true),
      new ValueSetState.Entry("k2", "v2", false)
    ));

    ValueSetState valueSetState2 = new ValueSetState(new ValueSetId("vs2"), List.of(
      new ValueSetState.Entry("k3", "v3", true)
    ));

    DialobSession dialobSession = new DialobSession(
      "tenant",
      "id",
      "rev",
      Instant.now(),
      null,
      Instant.now(),
      "en",
      new ItemStates.Builder()
        .itemStates(Map.of(
          QUESTIONNAIRE_REF, questionnaire
        ))
        .valueSetStates(Map.of(
          valueSetState1.id(), valueSetState1,
          valueSetState2.id(), valueSetState2
        ))
        .build(),
      ItemStates.EMPTY
    );

    Questionnaire.Metadata metadata = new Questionnaire.Metadata.Builder()
      .formId("form1")
      .status(Questionnaire.Metadata.Status.OPEN)
      .build();

    DialobQuestionnaireSession session = DialobQuestionnaireSession.builder()
      .serviceFacade(serviceFacade)
      .dialobSession(dialobSession)
      .dialobProgram(dialobProgram)
      .rev("rev1")
      .metadata(metadata)
      .questionClientVisibility(QuestionnaireSession.QuestionClientVisibility.ONLY_ENABLED)
      .build();

    List<ValueSet> providedValueSets = new ArrayList<>(session.getQuestionnaire().getValueSets());

    assertThat(providedValueSets).hasSize(2);
    assertThat(providedValueSets).extracting("id").containsExactlyInAnyOrder("vs1", "vs2");

    ValueSet vs1 = providedValueSets.stream().filter(vs -> vs.getId().equals("vs1")).findFirst().orElseThrow();
    assertThat(vs1.getEntries()).hasSize(1);
    assertThat(vs1.getEntries().getFirst().getKey()).isEqualTo("k1");

    ValueSet vs2 = providedValueSets.stream().filter(vs -> vs.getId().equals("vs2")).findFirst().orElseThrow();
    assertThat(vs2.getEntries()).hasSize(1);
    assertThat(vs2.getEntries().getFirst().getKey()).isEqualTo("k3");
  }

  @Test
  void shouldReturnErrors() {
    var serviceFacade = new DialobQuestionnaireSessionServiceFacade(mock(),mock(),mock());
    DialobProgram dialobProgram = mock(DialobProgram.class);

    ItemState questionnaire = mock();

    ErrorState errorState1 = new ErrorState(new ErrorId(IdUtils.toId("item1"), "err1"), "Error 1", true, true);
    ErrorState errorState2 = new ErrorState(new ErrorId(IdUtils.toId("item2"), "err2"), "Error 2", false, false);
    ErrorState errorState3 = new ErrorState(new ErrorId(IdUtils.toId("item3"), "err3"), "Error 3", true, false);
    ErrorState errorState4 = new ErrorState(new ErrorId(IdUtils.toId("item4"), "err4"), "Error 4", false, true);

    DialobSession dialobSession = new DialobSession(
      "tenant",
      "id",
      "rev",
      Instant.now(),
      null,
      Instant.now(),
      "en",
      new ItemStates.Builder()
        .itemStates(Map.of(
          QUESTIONNAIRE_REF, questionnaire
        ))
        .errorStates(Map.of(
          errorState1.id(), errorState1,
          errorState2.id(), errorState2,
          errorState3.id(), errorState3,
          errorState4.id(), errorState4
        ))
        .build(),
      ItemStates.EMPTY
    );

    DialobQuestionnaireSession session = DialobQuestionnaireSession.builder()
      .serviceFacade(serviceFacade)
      .dialobSession(dialobSession)
      .dialobProgram(dialobProgram)
      .rev("rev1")
      .metadata(new Questionnaire.Metadata.Builder()
        .formId("form1")
        .status(Questionnaire.Metadata.Status.OPEN)
        .build())
      .questionClientVisibility(QuestionnaireSession.QuestionClientVisibility.ONLY_ENABLED)
      .build();

    List<Error> errors = session.getErrors();

    assertThat(errors).hasSize(2);
    assertThat(errors).extracting("id", "code", "description")
      .containsExactlyInAnyOrder(
        Tuple.tuple("item1", "err1", "Error 1"),
        Tuple.tuple("item3", "err3", "Error 3")
      );
  }

  @Test
  void shouldReturnVariableValues() {
    var serviceFacade = new DialobQuestionnaireSessionServiceFacade(mock(),mock(),mock());
    DialobProgram dialobProgram = mock(DialobProgram.class);

    ItemState questionnaire = mock();

    ItemState var1 = ItemState.builder()
      .id(IdUtils.toId("var1"))
      .type("variable")
      .value("value1")
      .bits(ItemState.ACTIVE_BIT)
      .status(ItemState.Status.NEW)
      .build();

    ItemState var2 = ItemState.builder()
      .id(IdUtils.toId("var2"))
      .type("variable")
      .value(123)
      .bits(ItemState.ACTIVE_BIT)
      .status(ItemState.Status.NEW)
      .build();

    ItemState inActiveVar = ItemState.builder()
      .id(IdUtils.toId("inActiveVar"))
      .type("variable")
      .value("inActiveVar")
      .status(ItemState.Status.NEW)
      .build();

    ItemState notVar = ItemState.builder()
      .id(IdUtils.toId("notVar"))
      .type("text")
      .value("textValue")
      .bits(ItemState.ACTIVE_BIT)
      .status(ItemState.Status.NEW)
      .build();

    DialobSession dialobSession = new DialobSession(
      "tenant",
      "id",
      "rev",
      Instant.now(),
      null,
      Instant.now(),
      "en",
      new ItemStates.Builder()
        .itemStates(Map.of(
          QUESTIONNAIRE_REF, questionnaire,
          var1.id(), var1,
          var2.id(), var2,
          notVar.id(), notVar,
          inActiveVar.id(), inActiveVar
        ))
        .build(),
      ItemStates.EMPTY
    );

    DialobQuestionnaireSession session = DialobQuestionnaireSession.builder()
      .serviceFacade(serviceFacade)
      .dialobSession(dialobSession)
      .dialobProgram(dialobProgram)
      .rev("rev1")
      .metadata(new Questionnaire.Metadata.Builder()
        .formId("form1")
        .status(Questionnaire.Metadata.Status.OPEN)
        .build())
      .questionClientVisibility(QuestionnaireSession.QuestionClientVisibility.ONLY_ENABLED)
      .build();

    List<VariableValue> variableValues = session.getVariableValues();

    assertThat(variableValues).hasSize(3);
    assertThat(variableValues).extracting("id", "value")
      .containsExactlyInAnyOrder(
        Tuple.tuple("var1", "value1"),
        Tuple.tuple("var2", "123"),
        Tuple.tuple("inActiveVar", null)
      );
  }

  @Test
  void shouldReturnValueSets() {
    var serviceFacade = new DialobQuestionnaireSessionServiceFacade(mock(),mock(),mock());
    DialobProgram dialobProgram = mock(DialobProgram.class);

    ItemState questionnaire = mock();

    ValueSetState valueSetState1 = new ValueSetState(new ValueSetId("vs1"), List.of(
      new ValueSetState.Entry("k1", "v1", true),
      new ValueSetState.Entry("k2", "v2", false)
    ));

    ValueSetState valueSetState2 = new ValueSetState(new ValueSetId("vs2"), List.of(
      new ValueSetState.Entry("k3", "v3", true)
    ));

    DialobSession dialobSession = new DialobSession(
      "tenant",
      "id",
      "rev",
      Instant.now(),
      null,
      Instant.now(),
      "en",
      new ItemStates.Builder()
        .itemStates(Map.of(
          QUESTIONNAIRE_REF, questionnaire
        ))
        .valueSetStates(Map.of(
          valueSetState1.id(), valueSetState1,
          valueSetState2.id(), valueSetState2
        ))
        .build(),
      ItemStates.EMPTY
    );

    DialobQuestionnaireSession session = DialobQuestionnaireSession.builder()
      .serviceFacade(serviceFacade)
      .dialobSession(dialobSession)
      .dialobProgram(dialobProgram)
      .rev("rev1")
      .metadata(new Questionnaire.Metadata.Builder()
        .formId("form1")
        .status(Questionnaire.Metadata.Status.OPEN)
        .build())
      .questionClientVisibility(QuestionnaireSession.QuestionClientVisibility.ONLY_ENABLED)
      .build();

    List<ValueSet> valueSets = session.getValueSets();

    assertThat(valueSets).hasSize(2);
    assertThat(valueSets).extracting("id").containsExactlyInAnyOrder("vs1", "vs2");

    ValueSet vs1 = valueSets.stream().filter(vs -> vs.getId().equals("vs1")).findFirst().orElseThrow();
    assertThat(vs1.getEntries()).hasSize(2);
    assertThat(vs1.getEntries()).extracting("key", "value")
      .containsExactlyInAnyOrder(
        Tuple.tuple("k1", "v1"),
        Tuple.tuple("k2", "v2")
      );

    ValueSet vs2 = valueSets.stream().filter(vs -> vs.getId().equals("vs2")).findFirst().orElseThrow();
    assertThat(vs2.getEntries()).hasSize(1);
    assertThat(vs2.getEntries().getFirst().getKey()).isEqualTo("k3");
  }

}
