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
package io.dialob.session.boot;

import io.dialob.api.proto.*;
import io.dialob.api.questionnaire.ImmutableQuestionnaire;
import io.dialob.api.questionnaire.Questionnaire;
import io.dialob.cache.QuestionnaireSessionCache;
import io.dialob.questionnaire.service.api.FormActions;
import io.dialob.questionnaire.service.api.FormActionsUpdatesCallback;
import io.dialob.questionnaire.service.api.QuestionnaireDatabase;
import io.dialob.questionnaire.service.api.event.QuestionnaireEventPublisher;
import io.dialob.questionnaire.service.api.session.*;
import io.dialob.security.tenant.CurrentTenant;
import io.dialob.security.user.CurrentUserProvider;
import io.dialob.session.engine.QuestionnaireDialobProgramService;
import io.dialob.session.engine.program.DialobProgram;
import io.dialob.session.engine.program.DialobSessionEvalContextFactory;
import io.dialob.session.engine.sp.AsyncFunctionInvoker;
import io.dialob.session.engine.sp.DialobQuestionnaireSessionBuilder;
import io.dialob.session.engine.sp.DialobQuestionnaireSessionService;
import lombok.Data;
import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.AbstractListAssert;
import org.assertj.core.api.Assertions;
import org.mockito.Mockito;

import java.util.*;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.internal.verification.VerificationModeFactory.times;

public class FillAssertionBuilder {


  private final CurrentTenant currentTenant;
  private final Questionnaire questionnaire;
  private boolean completed;

  @Data
  static class ValidationEntry {

    private Consumer<QuestionnaireSession> sessionConsumer;

    private Action action;

    private Consumer<AbstractListAssert<?, ? extends List<? extends Action>, Action, ? extends AbstractAssert<?, Action>>> assertter;

    private Actions actions;
  }

  // activation triggers fires first actions
  int expectedActionsEventCount = 0;

  private List<ValidationEntry> validationEntries =  new ArrayList<>();

  private String formId;

  private final QuestionnaireDialobProgramService dialobProgramService;

  private final FormFinder formFinder;

  private final CurrentUserProvider currentUserProvider;

  private final DialobSessionEvalContextFactory sessionContextFactory;

  private final QuestionnaireSessionSaveService questionnaireSessionSaveService;

  public FillAssertionBuilder(CurrentTenant currentTenant, QuestionnaireDialobProgramService dialobProgramService, FormFinder formFinder, CurrentUserProvider currentUserProvider, String formId, DialobSessionEvalContextFactory sessionContextFactory, QuestionnaireSessionSaveService questionnaireSessionSaveService) {
    this.currentTenant = currentTenant;
    this.dialobProgramService = dialobProgramService;
    this.formFinder = formFinder;
    this.currentUserProvider = currentUserProvider;
    this.formId = formId;
    this.sessionContextFactory = sessionContextFactory;
    this.questionnaireSessionSaveService = questionnaireSessionSaveService;
    questionnaire = null;
  }
  public FillAssertionBuilder(CurrentTenant currentTenant, QuestionnaireDialobProgramService dialobProgramService, FormFinder formFinder, CurrentUserProvider currentUserProvider, Questionnaire questionnaire, DialobSessionEvalContextFactory sessionContextFactory, QuestionnaireSessionSaveService questionnaireSessionSaveService) {
    this.currentTenant = currentTenant;
    this.dialobProgramService = dialobProgramService;
    this.formFinder = formFinder;
    this.currentUserProvider = currentUserProvider;
    this.questionnaire = questionnaire;
    this.formId = questionnaire.getMetadata().getFormId();
    this.sessionContextFactory = sessionContextFactory;
    this.questionnaireSessionSaveService = questionnaireSessionSaveService;
  }

  public FillAssertionBuilder assertState(Consumer<AbstractListAssert<?, ? extends List<? extends Action>, Action, ? extends AbstractAssert<?, Action>>> assertter) {
    ValidationEntry validationEntry = new ValidationEntry();
    validationEntry.setAssertter(assertter);
    validationEntries.add(validationEntry);
    return this;
  }

  public FillAssertionBuilder queueAction(Action action) {
    ValidationEntry validationEntry = new ValidationEntry();
    validationEntry.setAction(action);
    validationEntries.add(validationEntry);
    expectedActionsEventCount++;
    return this;
  }

  public FillAssertionBuilder answer(String questionId, Object answer) {
    return queueAction(ImmutableAction.builder()
      .type(Action.Type.ANSWER)
      .id(questionId)
      .answer(answer).build());
  }

  public FillAssertionBuilder checkSession(Consumer<QuestionnaireSession> sessionConsumer) {
    ValidationEntry validationEntry = new ValidationEntry();
    validationEntry.setSessionConsumer(sessionConsumer);
    validationEntries.add(validationEntry);
    return this;
  }

  public FillAssertionBuilder addRow(String id) {
    return queueAction(ImmutableAction.builder()
      .type(Action.Type.ADD_ROW)
      .id(id).build());
  }

  public FillAssertionBuilder setLocale(String locale) {
    return queueAction(ActionsFactory.setLocale(locale));
  }

  public FillAssertionBuilder deleteRow(String id) {
    return queueAction(ImmutableAction.builder()
      .type(Action.Type.DELETE_ROW)
      .id(id).build());
  }

  public FillAssertionBuilder nextPage() {
    return queueAction(ImmutableAction.builder()
      .type(Action.Type.NEXT).build());
  }

  public FillAssertionBuilder previousPage() {
    return queueAction(ImmutableAction.builder()
      .type(Action.Type.PREVIOUS).build());
  }

  public FillAssertionBuilder complete(boolean expected) {
    completed = expected;
    return queueAction(ImmutableAction.builder()
      .type(Action.Type.COMPLETE).build());
  }

  public FillAssertionBuilder complete() {
    return complete(true);
  }


  public FillAssertionBuilder assertThat(Consumer<AbstractListAssert<?, ? extends List<? extends Action>, Action, ? extends AbstractAssert<?, Action>>> consumer) {
    validationEntries.get(validationEntries.size() - 1).assertter = consumer;
    return this;
  }

  public void apply() {
    final QuestionnaireEventPublisher applicationEventPublisher = mock(QuestionnaireEventPublisher.class);

    final DialobProgram dialobProgram = dialobProgramService.findByFormId(this.formId);
    String sessionId = UUID.randomUUID().toString();

    QuestionnaireSessionCache cache = mock(QuestionnaireSessionCache.class);
    final QuestionnaireDatabase questionnaireDatabase = mock(QuestionnaireDatabase.class);
    when(questionnaireDatabase.save(anyString(), any(Questionnaire.class))).then(invocation -> {
      final ImmutableQuestionnaire questionnaire = invocation.getArgument(0);
      return questionnaire.withId(sessionId);
    });

    AsyncFunctionInvoker asyncFunctionInvoker = mock(AsyncFunctionInvoker.class);

    final QuestionnaireSessionBuilderFactory questionnaireSessionBuilderFactory = () -> new DialobQuestionnaireSessionBuilder(applicationEventPublisher,
      dialobProgramService,
      formFinder,
      questionnaireSessionSaveService,
            sessionContextFactory,
      asyncFunctionInvoker
    );

    QuestionnaireSessionService questionnaireSessionService = new DialobQuestionnaireSessionService(
      questionnaireDatabase,
      questionnaireSessionBuilderFactory, currentTenant);

    final QuestionnaireSessionBuilder questionnaireSessionBuilder = questionnaireSessionBuilderFactory.createQuestionnaireSessionBuilder();
    if (this.questionnaire == null) {
      questionnaireSessionBuilder.formId(this.formId);
    } else {
      questionnaireSessionBuilder
        .language("en")
        .questionnaire(questionnaire);
    }
    QuestionnaireSession session = questionnaireSessionBuilder.build();

    for (final ValidationEntry validationEntry : validationEntries) {
      if (validationEntry.getSessionConsumer() != null) {
        validationEntry.getSessionConsumer().accept(session);
        validationEntry.setActions(ImmutableActions.builder().build());
      } else if (validationEntry.getAction() != null) {
        validationEntry.setActions(session.dispatchActions(Collections.singletonList(validationEntry.getAction())).getActions());
      } else {
        FormActions formActions =  new FormActions();
        session.buildFullForm(new FormActionsUpdatesCallback(formActions));
        validationEntry.setActions(ImmutableActions.builder().actions(formActions.getActions()).build());
      }
    }

    if (this.questionnaire == null) {
      verify(applicationEventPublisher).created(anyString());
    }
    verify(applicationEventPublisher).opened(anyString());
    if (this.completed) {
      verify(applicationEventPublisher).completed(anyString(), anyString());

    }
    verify(applicationEventPublisher,times(expectedActionsEventCount)).actions(anyString(), any(Actions.class));
    assertEvents();
    Mockito.verifyNoMoreInteractions(applicationEventPublisher);
  }

  public void assertEvents() {
    Iterator<ValidationEntry> ai = validationEntries.iterator();
    while(ai.hasNext()) {
      final ValidationEntry validationEntry = ai.next();
      if ( validationEntry.getAssertter() != null) {
        validationEntry.getAssertter().accept(Assertions.<Action>assertThat(validationEntry.actions.getActions()));
      }
    }
    assertFalse(ai.hasNext());
  }
}
