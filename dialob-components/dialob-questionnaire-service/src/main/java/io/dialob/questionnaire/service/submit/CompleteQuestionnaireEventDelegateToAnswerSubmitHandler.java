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
package io.dialob.questionnaire.service.submit;

import io.dialob.questionnaire.service.api.AnswerSubmitHandler;
import io.dialob.questionnaire.service.api.event.QuestionnaireCompletedEvent;
import io.dialob.questionnaire.service.api.session.QuestionnaireSession;
import io.dialob.questionnaire.service.api.session.QuestionnaireSessionService;
import io.dialob.settings.DialobSettings;
import io.dialob.settings.SubmitHandlerSettings;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.EventListener;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

@Slf4j
public class CompleteQuestionnaireEventDelegateToAnswerSubmitHandler {

  private final DialobSettings dialobSettings;

  private final QuestionnaireSessionService questionnaireSessionService;

  private final ApplicationContext applicationContext;

  public CompleteQuestionnaireEventDelegateToAnswerSubmitHandler(DialobSettings dialobSettings,
                                                                 QuestionnaireSessionService questionnaireSessionService,
                                                                 ApplicationContext applicationContext) {
    this.dialobSettings = dialobSettings;
    this.questionnaireSessionService = questionnaireSessionService;
    this.applicationContext = applicationContext;
  }

  @EventListener
  protected void onCompleteQuestionnaireHandler(QuestionnaireCompletedEvent event) {
    LOGGER.debug("Received: {}", event);
    String tenantId = event.getTenant().id();
    AnswerSubmitHandler.Settings submitHandlerSettings = new AnswerSubmitHandler.Settings() {

      @Override
      public String getBeanName() {
        SubmitHandlerSettings tenantSubmitHandlerSettings = dialobSettings.getSubmitHandlers().get(tenantId);
        if (tenantSubmitHandlerSettings != null) {
          return tenantSubmitHandlerSettings.getBeanName();
        }
        return null;
      }

      @Override
      public Map<String, Object> getProperties() {
        SubmitHandlerSettings tenantSubmitHandlerSettings = dialobSettings.getSubmitHandlers().get(tenantId);
        if (tenantSubmitHandlerSettings != null) {
          return tenantSubmitHandlerSettings.getProperties();
        }
        return Collections.emptyMap();
      }
    };
    String questionnaireId = event.getQuestionnaireId();
    QuestionnaireSession questionnaireSession = questionnaireSessionService.findOne(questionnaireId);
    if (questionnaireSession != null) {
      createSubmitHandler(submitHandlerSettings)
        .ifPresent(answerSubmitHandler -> {
          LOGGER.debug("call submit handler {} for {}", submitHandlerSettings.getBeanName(), questionnaireSession.getId());
          answerSubmitHandler.submit(submitHandlerSettings, questionnaireSession.getQuestionnaire());
        });
    } else {
      LOGGER.warn("Cannot do submit. Questionnaire {} is missing", questionnaireId);
    }
  }

  private Optional<AnswerSubmitHandler> createSubmitHandler(AnswerSubmitHandler.Settings submitHandlerSettings) {
    String beanName = submitHandlerSettings.getBeanName();
    if (beanName != null ) {
      try {
        LOGGER.debug("Looking submit handler {}", beanName);
        return Optional.of(applicationContext.getBean(beanName, AnswerSubmitHandler.class));
      } catch (BeansException e) {
        LOGGER.error("no answer submit handler '{}' defined. Error: {}", beanName, e.getMessage());
      }
    }
    return Optional.empty();
  }
}
