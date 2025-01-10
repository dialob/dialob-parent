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
package io.dialob.questionnaire.service;

import io.dialob.common.Constants;
import io.dialob.integration.api.event.EventPublisher;
import io.dialob.questionnaire.service.api.event.QuestionnaireEventPublisher;
import io.dialob.questionnaire.service.api.session.QuestionnaireSessionSaveService;
import io.dialob.questionnaire.service.api.session.QuestionnaireSessionService;
import io.dialob.questionnaire.service.submit.CompleteQuestionnaireEventDelegateToAnswerSubmitHandler;
import io.dialob.questionnaire.service.submit.PostSubmitHandler;
import io.dialob.security.tenant.CurrentTenant;
import io.dialob.settings.DialobSettings;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.task.TaskExecutionAutoConfiguration;
import org.springframework.cache.CacheManager;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.util.Optional;

@Configuration(proxyBeanMethods = false)
@Slf4j
@ImportResource("classpath:dialob-questionnaire-service-cache-context.xml")
public class DialobQuestionnaireServiceAutoConfiguration {

  @Bean
  public QuestionnaireSessionProcessingService questionnaireSessionActionProcessingService(
    QuestionnaireSessionService questionnaireSessionService,
    QuestionnaireSessionSaveService questionnaireSessionSaveService,
    MeterRegistry meterRegistry,
    @Qualifier(Constants.SESSION_CACHE_MANAGER_BEAN) Optional<CacheManager> sessionCacheManager,
    QuestionnaireEventPublisher eventPublisher
  ) {
    return new QuestionnaireSessionProcessingService(questionnaireSessionService, meterRegistry, sessionCacheManager, questionnaireSessionSaveService, eventPublisher);
  }

  @Bean
  @ConditionalOnProperty(prefix = "dialob.session.postSubmitHandler", name = "enabled", havingValue = "true", matchIfMissing = true)
  public CompleteQuestionnaireEventDelegateToAnswerSubmitHandler completeQuestionnaireEventDelegateToAnswerSubmitHandler(
    DialobSettings dialobSettings,
    QuestionnaireSessionService questionnaireSessionService,
    ApplicationContext applicationContext)
  {
    LOGGER.info("Enabling CompleteQuestionnaireEventDelegateToAnswerSubmitHandler");
    return new CompleteQuestionnaireEventDelegateToAnswerSubmitHandler(dialobSettings, questionnaireSessionService, applicationContext);
  }

  // TODO should be "ext" service
  @Bean("postSubmitHandler")
  public PostSubmitHandler postSubmitHandler() {
    return new PostSubmitHandler();
  }

  @Bean
  public EventPublisher questionnaireSessionEventPublisher(@Qualifier(TaskExecutionAutoConfiguration.APPLICATION_TASK_EXECUTOR_BEAN_NAME) TaskExecutor taskExecutor, ApplicationEventPublisher applicationEventPublisher) {
    return new QuestionnaireSessionEventPublisher(taskExecutor, applicationEventPublisher);
  }

  @Bean
  @ConditionalOnProperty(prefix = "dialob.session.autosave", name = "enabled", havingValue = "true")
  public PeriodicPersistenceService periodicPersistenceService(QuestionnaireSessionService questionnaireSessionService,
                                                               QuestionnaireSessionSaveService questionnaireSessionSaveService,
                                                               ThreadPoolTaskScheduler taskScheduler,
                                                               DialobSettings settings,
                                                               CurrentTenant currentTenant) {
    return new PeriodicPersistenceService(questionnaireSessionService, questionnaireSessionSaveService, taskScheduler, settings, currentTenant);
  }

  @Bean
  public QuestionnaireEventPublisher questionnaireEventPublisher(EventPublisher applicationEventPublisher, CurrentTenant currentTenant) {
    return new QuestionnaireEventPublisher(applicationEventPublisher, currentTenant);
  }
}
