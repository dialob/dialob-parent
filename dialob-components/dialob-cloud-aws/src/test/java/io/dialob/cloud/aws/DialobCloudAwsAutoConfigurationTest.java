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
package io.dialob.cloud.aws;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.dialob.settings.DialobSettings;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.assertj.ApplicationContextAssert;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;


class DialobCloudAwsAutoConfigurationTest {

  @Configuration(proxyBeanMethods = false)
  public static class MockConfigurations {

    @Bean
    public ObjectMapper objectMapper() {
      return new ObjectMapper();
    }

    @Bean
    public AwsCredentialsProvider awsCredentialsProvider() {
      return Mockito.mock(AwsCredentialsProvider.class);
    }

  }

  @Test
  void shouldConfigureTopicsWhenARNAvailable() {
    new ApplicationContextRunner()
      .withPropertyValues(
        "dialob.aws.enabled=true",
        "dialob.aws.sns.formEventsTopicARN=forms-topic",
        "dialob.aws.sns.questionnaireEventsTopicARN=questionnaire-topic"
      )
      .withUserConfiguration(MockConfigurations.class, DialobSettings.class)
      .withConfiguration(AutoConfigurations.of(DialobCloudAwsAutoConfiguration.class))
      .run(context -> {
        ApplicationContextAssert<ConfigurableApplicationContext> contextAssert = Assertions.assertThat(context);
        contextAssert
          .getBean(DialobFormEventsToSNSBridge.class).isNotNull();
        contextAssert
          .getBean(DialobQuestionnaireEventsToSNSBridge.class).isNotNull();
      });
  }

  @Test
  void shouldDisableTopicWhenARNNotdefined() {
    new ApplicationContextRunner()
      .withPropertyValues(
        "dialob.aws.enabled=true",
        "dialob.aws.sns.questionnaireEventsTopicARN=questionnaire-topic"
      )
      .withUserConfiguration(MockConfigurations.class, DialobSettings.class)
      .withConfiguration(AutoConfigurations.of(DialobCloudAwsAutoConfiguration.class))
      .run(context -> {
        ApplicationContextAssert<ConfigurableApplicationContext> contextAssert = Assertions.assertThat(context);
        contextAssert
          .getBean(DialobFormEventsToSNSBridge.class).isNull();
        contextAssert
          .getBean(DialobQuestionnaireEventsToSNSBridge.class).isNotNull();
      });
  }

  @Test
  void shouldDisableTopicsWhenARNNotdefined() {
    new ApplicationContextRunner()
      .withPropertyValues(
        "dialob.aws.enabled=true"
      )
      .withUserConfiguration(MockConfigurations.class, DialobSettings.class)
      .withConfiguration(AutoConfigurations.of(DialobCloudAwsAutoConfiguration.class))
      .run(context -> {
        ApplicationContextAssert<ConfigurableApplicationContext> contextAssert = Assertions.assertThat(context);
        contextAssert
          .getBean(DialobFormEventsToSNSBridge.class).isNull();
        contextAssert
          .getBean(DialobQuestionnaireEventsToSNSBridge.class).isNull();
      });
  }

  @Test
  void shouldDisableTopicsWhenDialobAwsEnabledIsUndefined() {
    new ApplicationContextRunner()
      .withUserConfiguration(MockConfigurations.class, DialobSettings.class)
      .withConfiguration(AutoConfigurations.of(DialobCloudAwsAutoConfiguration.class))
      .run(context -> {
        ApplicationContextAssert<ConfigurableApplicationContext> contextAssert = Assertions.assertThat(context);
        contextAssert
          .getBean(DialobFormEventsToSNSBridge.class).isNull();
        contextAssert
          .getBean(DialobQuestionnaireEventsToSNSBridge.class).isNull();
        contextAssert.getBean("S3SubmitHandler").isNull();
      });
  }

}
