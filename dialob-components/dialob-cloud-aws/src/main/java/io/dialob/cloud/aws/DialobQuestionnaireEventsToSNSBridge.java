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
package io.dialob.cloud.aws;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.dialob.questionnaire.service.api.event.QuestionnaireCompletedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import software.amazon.awssdk.services.sns.SnsAsyncClient;
import software.amazon.awssdk.services.sns.model.MessageAttributeValue;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class DialobQuestionnaireEventsToSNSBridge extends AbstractEventsToSNSBridge {

  public DialobQuestionnaireEventsToSNSBridge(SnsAsyncClient amazonSNS, ObjectMapper objectMapper, String topicARN) {
    super(amazonSNS, objectMapper, topicARN);
  }

  @EventListener
  public void onQuestionnaireCompletedEvent(QuestionnaireCompletedEvent event) {
    LOGGER.info("Publishing event {} to SNS Topic {}", event, this.getTopicARN());
    Map<String, MessageAttributeValue> attributes = new HashMap<>();
    attributes.put("tenantId", MessageAttributeValue.builder().stringValue(event.getTenant().getId()).dataType("String").build());

    String subject = "QuestionnaireCompleted";
    publish(subject, event, attributes);

  }

}
