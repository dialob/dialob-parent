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
package io.dialob.session.engine.sp;

import io.dialob.api.form.Form;
import io.dialob.api.questionnaire.Answer;
import io.dialob.api.questionnaire.ContextValue;
import io.dialob.api.questionnaire.Questionnaire;
import io.dialob.api.questionnaire.VariableValue;
import io.dialob.questionnaire.service.api.event.QuestionnaireEventPublisher;
import io.dialob.questionnaire.service.api.session.BaseQuestionnaireSessionBuilder;
import io.dialob.questionnaire.service.api.session.FormFinder;
import io.dialob.questionnaire.service.api.session.QuestionnaireSession;
import io.dialob.questionnaire.service.api.session.QuestionnaireSessionSaveService;
import io.dialob.session.engine.DialobProgramService;
import io.dialob.session.engine.Utils;
import io.dialob.session.engine.program.DialobProgram;
import io.dialob.session.engine.program.DialobSessionEvalContextFactory;
import io.dialob.session.engine.program.model.VariableItem;
import io.dialob.session.engine.session.model.DialobSession;
import io.dialob.session.engine.session.model.IdUtils;
import io.dialob.session.engine.session.model.ValueSetState;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.Optional;

import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;

@Slf4j
public class DialobQuestionnaireSessionBuilder extends BaseQuestionnaireSessionBuilder {

  private final QuestionnaireEventPublisher eventPublisher;

  private final DialobProgramService dialobProgramService;

  private final QuestionnaireSessionSaveService questionnaireSessionSaveService;

  private final DialobSessionEvalContextFactory sessionContextFactory;

  private final AsyncFunctionInvoker asyncFunctionInvoker;

  public DialobQuestionnaireSessionBuilder(@Nonnull QuestionnaireEventPublisher eventPublisher,
                                           @Nonnull DialobProgramService dialobProgramService,
                                           @Nonnull FormFinder formFinder,
                                           @Nonnull QuestionnaireSessionSaveService questionnaireSessionSaveService,
                                           @Nonnull DialobSessionEvalContextFactory sessionContextFactory,
                                           @Nonnull AsyncFunctionInvoker asyncFunctionInvoker) {
    super(formFinder);
    this.eventPublisher = requireNonNull(eventPublisher);
    this.dialobProgramService = requireNonNull(dialobProgramService);
    this.questionnaireSessionSaveService = requireNonNull(questionnaireSessionSaveService);
    this.sessionContextFactory = requireNonNull(sessionContextFactory);
    this.asyncFunctionInvoker = requireNonNull(asyncFunctionInvoker);
  }

  @Nonnull
  @Override
  protected QuestionnaireSession createQuestionnaireSession(boolean newSession, @Nonnull Form formDocument) {
    Questionnaire questionnaire = getQuestionnaire();
    DialobProgram dialobProgram;
    dialobProgram = dialobProgramService.findByFormIdAndRev(formDocument.getId(), formDocument.getRev());
    DialobSession dialobSession = dialobProgram.createSession(
      sessionContextFactory,
      formDocument.getMetadata().getTenantId(),
      questionnaire.getId(),
      getLanguage(),
      questionnaire.getActiveItem(), (itemId, item) -> {
        final String id = IdUtils.toString(itemId);
        if (item instanceof VariableItem) {
          for (ContextValue contextValue : questionnaire.getContext()) {
            if (id.equals(contextValue.getId())) {
              return Optional.ofNullable(Utils.parse(item.getValueType(), contextValue.getValue()));
            }
          }
          for (VariableValue variableValue : questionnaire.getVariableValues()) {
            if (id.equals(variableValue.getId())) {
              return Optional.ofNullable(Utils.parse(item.getValueType(), variableValue.getValue()));
            }
          }
        } else {
          for (Answer answer : questionnaire.getAnswers()) {
            if (id.equals(answer.getId())) {
              return Optional.ofNullable(answer.getValue());
            }
          }
        }
        return Optional.empty();
      }, valueSetId -> questionnaire.getValueSets().stream()
        .filter(valueSet -> valueSet.getId().equals(valueSetId.getValueSetId()))
        .findFirst()
        .map(valueSet -> valueSet.getEntries().stream().map(entry -> ValueSetState.Entry.of(entry.getKey(), entry.getValue(), true)).collect(toList()))
        .orElse(Collections.emptyList()),
      questionnaire.getMetadata().getCompleted(),
      questionnaire.getMetadata().getOpened(),
      questionnaire.getMetadata().getLastAnswer());
    if (questionnaire.getMetadata().getStatus() == Questionnaire.Metadata.Status.COMPLETED) {
      dialobSession.complete();
    }
    DialobQuestionnaireSession dialobQuestionnaireSession = null;
    try {
      dialobQuestionnaireSession = DialobQuestionnaireSession.builder()
        .eventPublisher(eventPublisher)
        .sessionContextFactory(sessionContextFactory)
        .asyncFunctionInvoker(asyncFunctionInvoker)
        .dialobSession(dialobSession)
        .dialobProgram(dialobProgram)
        .rev(questionnaire.getRev())
        .metadata(questionnaire.getMetadata())
        .questionClientVisibility(getQuestionClientVisibility(formDocument))
        .build();

      if (newSession) {
        // We need questionnaire Id
        dialobQuestionnaireSession = save(dialobQuestionnaireSession);
        if (!isCreateOnly()) {
          dialobQuestionnaireSession.initialize();
        }
      }
      dialobQuestionnaireSession.activate();
      return dialobQuestionnaireSession;
    } catch (Exception e) {
      if (dialobQuestionnaireSession != null) {
        dialobQuestionnaireSession.close();
      }
      throw e;
    }
  }

  @Nonnull
  protected DialobQuestionnaireSession save(DialobQuestionnaireSession dialobQuestionnaireSession) {
    return (DialobQuestionnaireSession) questionnaireSessionSaveService.save(dialobQuestionnaireSession);
  }

  private QuestionnaireSession.QuestionClientVisibility getQuestionClientVisibility(Form formDocument) {
    QuestionnaireSession.QuestionClientVisibility questionClientVisibility = QuestionnaireSession.QuestionClientVisibility.ONLY_ENABLED;
    Object o = formDocument.getMetadata().getAdditionalProperties().get("questionClientVisibility");
    if (o instanceof String) {
      try {
        return QuestionnaireSession.QuestionClientVisibility.valueOf((String) o);
      } catch (IllegalArgumentException e) {
        LOGGER.error("Unknown question client visibility {}", o);
      }
    }
    o = formDocument.getMetadata().getAdditionalProperties().get("showDisabled");
    if (o != null) {
      boolean showDisabled = false;
      if (o instanceof String) {
        showDisabled = Boolean.parseBoolean((String) o);
      } else if (o instanceof Boolean) {
        showDisabled = (Boolean) o;
      }
      if (showDisabled) {
        questionClientVisibility = QuestionnaireSession.QuestionClientVisibility.SHOW_DISABLED;
      }
    }
    return questionClientVisibility;
  }
}
