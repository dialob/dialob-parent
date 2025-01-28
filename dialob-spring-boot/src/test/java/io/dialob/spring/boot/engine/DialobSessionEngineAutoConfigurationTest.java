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
package io.dialob.spring.boot.engine;

import io.dialob.form.service.api.FormDatabase;
import io.dialob.questionnaire.service.api.QuestionnaireDatabase;
import io.dialob.questionnaire.service.api.event.QuestionnaireEventPublisher;
import io.dialob.rule.parser.function.FunctionRegistry;
import io.dialob.security.tenant.CurrentTenant;
import io.dialob.session.engine.QuestionnaireDialobProgramService;
import io.dialob.session.engine.program.DialobSessionEvalContextFactory;
import io.dialob.session.engine.sp.DialobQuestionnaireSessionService;
import io.dialob.settings.DialobSettings;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

import static org.mockito.Mockito.mock;

class DialobSessionEngineAutoConfigurationTest {

  @Configuration(proxyBeanMethods = false)
  public static class MockConfigurations {

    @Bean
    public FunctionRegistry functionRegistry() {
      return mock(FunctionRegistry.class);
    }

    @Bean
    public QuestionnaireDatabase questionnaireDatabase() {
      return mock(QuestionnaireDatabase.class);
    }

    @Bean
    public FormDatabase formDatabase() {
      return mock(FormDatabase.class);
    }

    @Bean
    public QuestionnaireEventPublisher eventPublisher() {
      return mock(QuestionnaireEventPublisher.class);
    }

    @Bean
    public DialobSettings dialobSettings() {
      return new DialobSettings();
    }

    @Bean
    public CurrentTenant currentTenant() {
      return mock(CurrentTenant.class);
    }

  }

  @Test
  void testDialobSessionEngineAutoConfiguration() {
    new ApplicationContextRunner()
      .withConfiguration(AutoConfigurations.of(DialobSessionEngineAutoConfiguration.class))
      .withUserConfiguration(MockConfigurations.class)
      .run(context -> {
        Assertions.assertThat(context)
          .hasSingleBean(QuestionnaireDialobProgramService.class)
          .hasSingleBean(DialobQuestionnaireSessionService.class)
          .hasSingleBean(DialobSessionEvalContextFactory.class)
          .doesNotHaveBean(Clock.class);
      });
  }

}
