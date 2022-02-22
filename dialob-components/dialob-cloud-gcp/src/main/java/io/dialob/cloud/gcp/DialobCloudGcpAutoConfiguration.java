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
package io.dialob.cloud.gcp;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.spring.pubsub.core.PubSubTemplate;
import com.google.cloud.spring.pubsub.integration.outbound.PubSubMessageHandler;
import com.google.cloud.spring.pubsub.support.converter.JacksonPubSubMessageConverter;
import io.dialob.settings.DialobSettings;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.MessageHandler;

@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(prefix = "spring.cloud.gcp.pubsub", name = "enabled", havingValue = "true")
@IntegrationComponentScan(basePackages = "io.dialob.cloud.gcp")
@Slf4j
public class DialobCloudGcpAutoConfiguration {

  @Bean
  public JacksonPubSubMessageConverter jacksonPubSubMessageConverter(ObjectMapper objectMapper) {
    return new JacksonPubSubMessageConverter(objectMapper);
  }

  @Bean
  @ServiceActivator(inputChannel = "dialobQuestionnairePubSubOutputChannel")
  @ConditionalOnProperty(prefix = "dialob.gcp.pubsub", name = "questionnaireEventsTopic")
  public MessageHandler questionnaireMessageHandler(PubSubTemplate pubSubTemplate, DialobSettings settings) {
    return new PubSubMessageHandler(pubSubTemplate, settings.getGcp().getPubsub().getQuestionnaireEventsTopic());
  }

  @Bean
  @ServiceActivator(inputChannel = "dialobFormPubSubOutputChannel")
  @ConditionalOnProperty(prefix = "dialob.gcp.pubsub", name = "formEventsTopic")
  public MessageHandler formMessageHandler(PubSubTemplate pubSubTemplate, DialobSettings settings) {
    return new PubSubMessageHandler(pubSubTemplate, settings.getGcp().getPubsub().getFormEventsTopic());
  }

  @Bean
  @ConditionalOnProperty(prefix = "dialob.gcp.pubsub", name = "questionnaireEventsTopic")
  public DialobQuestionnaireEventsToPubSubBridge dialobQuestionnaireEventsToPubSubBridge(DialobQuestionnaireEventsMessagingGateway messagingGateway) {
    return new DialobQuestionnaireEventsToPubSubBridge(messagingGateway);
  }

  @Bean
  @ConditionalOnProperty(prefix = "dialob.gcp.pubsub", name = "formEventsTopic")
  public DialobFormEventsToPubSubBridge dialobFormEventsToPubSubBridge(DialobFormEventMessagingGateway messagingGateway) {
    return new DialobFormEventsToPubSubBridge(messagingGateway);
  }

}
