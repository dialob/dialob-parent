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


import io.dialob.integration.api.event.EventPublisher;
import io.dialob.questionnaire.service.api.event.QuestionnaireEventPublisher;
import io.dialob.questionnaire.service.api.session.QuestionnaireSessionSaveService;
import io.dialob.questionnaire.service.api.session.QuestionnaireSessionService;
import io.dialob.questionnaire.service.submit.CompleteQuestionnaireEventDelegateToAnswerSubmitHandler;
import io.dialob.security.tenant.CurrentTenant;
import io.dialob.settings.DialobSettings;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.task.TaskExecutionAutoConfiguration;
import org.springframework.boot.autoconfigure.task.TaskSchedulingAutoConfiguration;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

import static org.assertj.core.api.Assertions.assertThat;

class DialobQuestionnaireServiceAutoConfigurationTest {

  @Configuration(proxyBeanMethods = false)
  @EnableScheduling
  public static class TestConfiguration {

    @Bean
    public QuestionnaireSessionService questionnaireSessionService() {
      return Mockito.mock(QuestionnaireSessionService.class);
    }

    @Bean
    public MeterRegistry meterRegistry() {
      return new SimpleMeterRegistry();
    }

    @Bean
    public DialobSettings dialobSettings() {
      return Mockito.mock(DialobSettings.class);
    }

    @Bean
    public CurrentTenant currentTenant() {
      return Mockito.mock(CurrentTenant.class);
    }

    @Bean
    public QuestionnaireSessionSaveService questionnaireSessionSaveService() {
      return Mockito.mock(QuestionnaireSessionSaveService.class);
    }

    @Bean
    public CacheManager cacheManager() {
      return Mockito.mock(CacheManager.class);
    }

  }

  @Test
  public void testDialobQuestionnaireServiceAutoConfiguration() {
    new ApplicationContextRunner()
      .withUserConfiguration(TestConfiguration.class)
      .withConfiguration(AutoConfigurations.of(
        TaskSchedulingAutoConfiguration.class,
        TaskExecutionAutoConfiguration.class,
        DialobQuestionnaireServiceAutoConfiguration.class))
      .run(context -> {
        assertThat(context).hasSingleBean(QuestionnaireSessionProcessingService.class);
        assertThat(context).hasSingleBean(EventPublisher.class);
        assertThat(context).hasSingleBean(QuestionnaireEventPublisher.class);
        assertThat(context).hasSingleBean(CompleteQuestionnaireEventDelegateToAnswerSubmitHandler.class);
        assertThat(context).doesNotHaveBean(PeriodicPersistenceService.class);
        assertThat(context).hasBean("postSubmitHandler");
      });
  }

  @Test
  public void testDialobQuestionnaireServiceAutoConfiguration1() {
    new ApplicationContextRunner()
      .withUserConfiguration(TestConfiguration.class)
      .withConfiguration(AutoConfigurations.of(
        TaskSchedulingAutoConfiguration.class,
        TaskExecutionAutoConfiguration.class,
        DialobQuestionnaireServiceAutoConfiguration.class))
      .withSystemProperties("dialob.session.postSubmitHandler.enabled=false")
      .run(context -> {
        assertThat(context).hasSingleBean(QuestionnaireSessionProcessingService.class);
        assertThat(context).hasSingleBean(EventPublisher.class);
        assertThat(context).hasSingleBean(QuestionnaireEventPublisher.class);
        assertThat(context).doesNotHaveBean(CompleteQuestionnaireEventDelegateToAnswerSubmitHandler.class);
        assertThat(context).doesNotHaveBean(PeriodicPersistenceService.class);
        assertThat(context).hasBean("postSubmitHandler");
      });
  }
  @Test
  public void testDialobQuestionnaireServiceAutoConfigurationWithAutoSave() {
    new ApplicationContextRunner()
      .withUserConfiguration(TestConfiguration.class)
      .withConfiguration(AutoConfigurations.of(
        TaskSchedulingAutoConfiguration.class,
        TaskExecutionAutoConfiguration.class,
        DialobQuestionnaireServiceAutoConfiguration.class))
      .withSystemProperties("dialob.session.autosave.enabled=true")
      .run(context -> {
        assertThat(context).hasSingleBean(QuestionnaireSessionProcessingService.class);
        assertThat(context).hasSingleBean(EventPublisher.class);
        assertThat(context).hasSingleBean(QuestionnaireEventPublisher.class);
        assertThat(context).hasSingleBean(CompleteQuestionnaireEventDelegateToAnswerSubmitHandler.class);
        assertThat(context).hasSingleBean(PeriodicPersistenceService.class);
        assertThat(context).hasBean("postSubmitHandler");
      });
  }

}



