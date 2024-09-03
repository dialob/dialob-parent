/*
 * Copyright © 2015 - 2021 ReSys (info@dialob.io)
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
package io.dialob.questionnaire.service.api.test;

import io.dialob.api.proto.Action;
import io.dialob.api.proto.Actions;
import io.dialob.api.proto.ImmutableAction;
import io.dialob.api.proto.ImmutableActions;
import io.dialob.api.questionnaire.Questionnaire;
import io.dialob.integration.api.event.EventPublisher;
import io.dialob.questionnaire.service.api.FormActions;
import io.dialob.questionnaire.service.api.FormActionsUpdatesCallback;
import io.dialob.questionnaire.service.api.event.QuestionnaireActionsEvent;
import io.dialob.questionnaire.service.api.session.QuestionnaireSession;
import io.dialob.questionnaire.service.api.session.QuestionnaireSessionBuilder;
import io.dialob.questionnaire.service.api.session.QuestionnaireSessionService;
import jakarta.inject.Provider;
import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.AbstractListAssert;
import org.assertj.core.api.Assertions;
import org.mockito.ArgumentCaptor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.internal.verification.VerificationModeFactory.times;

public class FillAssertionBuilder {


  static class ValidationEntry {
    Action action;
    Consumer<AbstractListAssert<?, ? extends List<? extends Action>, Action, ? extends AbstractAssert<?, Action>>> assertter;
    public Actions actions;
  }

  int expectedEventCount = 1;

  boolean expectCreated = true;

  private List<ValidationEntry> validationEntries =  new ArrayList<>();

  private QuestionnaireSessionService questionnaireSessionService;

  private Provider<QuestionnaireSessionBuilder> questionnaireSessionBuilderFactory;

  private String formId;

  private Questionnaire questionnaire;

  private EventPublisher questionnaireSessionEventPublisher;

  public FillAssertionBuilder(QuestionnaireSessionService questionnaireSessionService, EventPublisher questionnaireSessionEventPublisher, String formId) {
    this.questionnaireSessionService = questionnaireSessionService;
    this.questionnaireSessionEventPublisher = questionnaireSessionEventPublisher;
    this.formId = formId;
  }

  public FillAssertionBuilder(QuestionnaireSessionService questionnaireSessionService, EventPublisher questionnaireSessionEventPublisher, Questionnaire questionnaire) {
    this.questionnaireSessionService = questionnaireSessionService;
    this.questionnaireSessionEventPublisher = questionnaireSessionEventPublisher;
    this.questionnaire = questionnaire;
    if (this.questionnaire.getMetadata().getStatus() == Questionnaire.Metadata.Status.OPEN) {
      // no created event
      expectedEventCount--;
      expectCreated = false;
    }
  }

  public FillAssertionBuilder assertState(Consumer<AbstractListAssert<?, ? extends List<? extends Action>, Action, ? extends AbstractAssert<?, Action>>> consumer) {
    ValidationEntry validationEntry = new ValidationEntry();
    validationEntry.assertter = consumer;
    validationEntries.add(validationEntry);
    return this;
  }

  public FillAssertionBuilder answer(String questionId, Object answer) {
    final ImmutableAction action = ImmutableAction.builder()
      .type(Action.Type.ANSWER)
      .id(questionId)
      .answer(answer).build();
    ValidationEntry validationEntry = new ValidationEntry();
    validationEntry.action = action;
    validationEntries.add(validationEntry);
    expectedEventCount++;
    return this;
  }
  public FillAssertionBuilder nextPage() {
    final Action action = ImmutableAction.builder()
      .type(Action.Type.NEXT).build();
    ValidationEntry validationEntry = new ValidationEntry();
    validationEntry.action = action;
    validationEntries.add(validationEntry);
    expectedEventCount++;
    return this;
  }

  public FillAssertionBuilder previousPage() {
    final Action action = ImmutableAction.builder()
      .type(Action.Type.PREVIOUS).build();
    ValidationEntry validationEntry = new ValidationEntry();
    validationEntry.action = action;
    validationEntries.add(validationEntry);
    expectedEventCount++;
    return this;
  }

  public FillAssertionBuilder complete() {
    final Action action = ImmutableAction.builder()
      .type(Action.Type.COMPLETE).build();
    ValidationEntry validationEntry = new ValidationEntry();
    validationEntry.action = action;
    validationEntries.add(validationEntry);
    expectedEventCount+=2;
    return this;
  }


  public FillAssertionBuilder assertThat(Consumer<AbstractListAssert<?, ? extends List<? extends Action>, Action, ? extends AbstractAssert<?, Action>>> consumer) {
    validationEntries.get(validationEntries.size() - 1).assertter = consumer;
    return this;
  }

  public void apply() {
    apply(true);
  }

  public void apply(boolean verify) {
    final QuestionnaireSession session;
    QuestionnaireSessionBuilder questionnaireSessionBuilder = questionnaireSessionBuilderFactory.get();
    if (this.questionnaire != null) {
      questionnaireSessionBuilder = questionnaireSessionBuilder
        .questionnaire(questionnaire);
    } else if (this.formId != null) {
      questionnaireSessionBuilder = questionnaireSessionBuilder
        .formId(this.formId);
    } else {
      org.junit.jupiter.api.Assertions.fail("define formId or questionnaire");
    }
    session = questionnaireSessionBuilder.build();
    for (final ValidationEntry validationEntry : validationEntries) {
      if (validationEntry.action != null) {
        validationEntry.actions = session.dispatchActions(session.getRevision(), Arrays.asList(validationEntry.action)).getActions();
      } else {
        FormActions formActions =  new FormActions();
        session.buildFullForm(new FormActionsUpdatesCallback(formActions));
        validationEntry.actions = ImmutableActions.builder().actions(formActions.getActions()).build();
      }
    }
    session.close();
    if (verify) {
      ArgumentCaptor<QuestionnaireActionsEvent> questionnaireActionsEventsCaptor = ArgumentCaptor.forClass(QuestionnaireActionsEvent.class);
      verify(questionnaireSessionEventPublisher,times(expectedEventCount)).publish(questionnaireActionsEventsCaptor.capture());
      assertEvents(questionnaireActionsEventsCaptor.getAllValues());
      verifyNoMoreInteractions(questionnaireSessionEventPublisher);
    }
  }

  public void assertEvents(List<QuestionnaireActionsEvent> events) {
    Iterator<ValidationEntry> ai = validationEntries.iterator();
    while(ai.hasNext()) {
      final ValidationEntry validationEntry = ai.next();
      validationEntry.assertter.accept(Assertions.<Action>assertThat(validationEntry.actions.getActions()));
    }
    assertFalse(ai.hasNext());
  }
}
