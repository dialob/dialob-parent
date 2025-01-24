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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.services.sns.SnsAsyncClient;
import software.amazon.awssdk.services.sns.model.MessageAttributeValue;
import software.amazon.awssdk.services.sns.model.PublishRequest;

import java.util.Map;

@Slf4j
public class AbstractEventsToSNSBridge {

  private final SnsAsyncClient amazonSNS;

  @Getter
  private final String topicARN;

  private final ObjectMapper objectMapper;

  public AbstractEventsToSNSBridge(SnsAsyncClient amazonSNS, ObjectMapper objectMapper, String topicARN) {
    this.amazonSNS = amazonSNS;
    this.objectMapper = objectMapper;
    this.topicARN = topicARN;
  }

  protected void publish(String subject, Object message, Map<String, MessageAttributeValue> attributes) {
    try {
      PublishRequest publishRequest = PublishRequest.builder()
        .topicArn(topicARN)
        .subject(subject)
        .messageAttributes(attributes)
        .message(objectMapper.writeValueAsString(message))
        .build();
      this.amazonSNS.publish(publishRequest).whenComplete((publishResponse, throwable) -> {
        LOGGER.debug("publish response: {}", publishResponse);
        if (throwable != null) {
          LOGGER.error("Failed to publish", throwable);
        }
      });
    } catch (JsonProcessingException e) {
      LOGGER.error("Could not publish event: {}", message, e);
    }
  }


}
