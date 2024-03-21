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
package io.dialob.session.boot;

import io.dialob.questionnaire.service.api.session.QuestionnaireSessionService;
import io.dialob.questionnaire.service.sockjs.DialobQuestionnaireServiceSockJSAutoConfiguration;
import io.dialob.security.aws.DialobSecurityAwsAutoConfiguration;
import io.dialob.security.spring.AuthenticationStrategy;
import io.dialob.security.user.CurrentUserProvider;
import io.dialob.session.rest.SessionPermissionEvaluator;
import io.dialob.settings.DialobSettingsAutoConfiguration;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.WebApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;

class ApplicationAutoConfigurationTest {

  @Configuration(proxyBeanMethods = false)
  public static class TestConfiguration {

    @Bean
    public TaskScheduler taskScheduler() {
      return Mockito.mock(TaskScheduler.class);
    }

    @Bean
    public QuestionnaireSessionService questionnaireSessionService() {
      return Mockito.mock(QuestionnaireSessionService.class);
    }
  }

  @Test
  public void testApplicationAutoConfigurationWithoutWebSocketSupport() {
    new WebApplicationContextRunner()
      .withUserConfiguration(TestConfiguration.class)
      .withConfiguration(AutoConfigurations.of(
        DialobSettingsAutoConfiguration.class,
        DialobQuestionnaireServiceSockJSAutoConfiguration.class,
        ApplicationAutoConfiguration.class
      ))
      .withSystemProperties(
        "dialob.session.sockjs.webSocketEnabled=false",
        "dialob.session.sockjs.allowedOrigins=*"
      )
      .run(context -> {
        assertThat(context)
          .hasSingleBean(ApplicationAutoConfiguration.RestApiSecurityConfigurer.class)
          .doesNotHaveBean(AuthenticationStrategy.class)
          .doesNotHaveBean(CurrentUserProvider.class)
          .doesNotHaveBean(ApplicationAutoConfiguration.SockJSWebSocketConfigurer.class)
        ;
      });
  }

  @Test
  public void testApplicationAutoConfigurationAwsSecurityEnabled() {
    new WebApplicationContextRunner()
      .withUserConfiguration(TestConfiguration.class)
      .withConfiguration(AutoConfigurations.of(
        DialobSettingsAutoConfiguration.class,
        DialobQuestionnaireServiceSockJSAutoConfiguration.class,
        ApplicationAutoConfiguration.class,
        DialobSecurityAwsAutoConfiguration.class
      ))
      .withSystemProperties(
        "spring.profiles.active=aws",
        "dialob.session.security.enabled=true",
        "dialob.session.sockjs.webSocketEnabled=false",
        "dialob.session.sockjs.allowedOrigins=*"
      )
      .run(context -> {
        assertThat(context)
          .hasSingleBean(ApplicationAutoConfiguration.RestApiSecurityConfigurer.class)
          .hasSingleBean(AuthenticationStrategy.class)
          .hasSingleBean(CurrentUserProvider.class)
          .hasSingleBean(SessionPermissionEvaluator.class)
          .doesNotHaveBean(ApplicationAutoConfiguration.SockJSWebSocketConfigurer.class);
        // Make sure that this is not default implementation
        assertFalse(context.getBean(SessionPermissionEvaluator.class)
          .hasAccess(null,null));
      });
  }


  @Test
  @Disabled // context runner do not mock ServerContainer required by websocket supprt
  public void testApplicationAutoConfigurationWittWebSocketSupport() {
    new WebApplicationContextRunner()
      .withUserConfiguration(TestConfiguration.class)
      .withConfiguration(AutoConfigurations.of(
        DialobSettingsAutoConfiguration.class,
        DialobQuestionnaireServiceSockJSAutoConfiguration.class,
        ApplicationAutoConfiguration.class
      ))
      .withSystemProperties(
        "dialob.session.sockjs.webSocketEnabled=true",
        "dialob.session.sockjs.allowedOrigins=*"
      )
      .run(context -> {
        assertThat(context)
          .hasSingleBean(ApplicationAutoConfiguration.RestApiSecurityConfigurer.class)
          .hasSingleBean(ApplicationAutoConfiguration.SockJSWebSocketConfigurer.class)
        ;
      });
  }

}
