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
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.sns.SnsAsyncClient;

import java.util.Optional;

@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(prefix = "dialob.aws", name = "enabled", havingValue = "true")
@Slf4j
public class DialobCloudAwsAutoConfiguration {

  @Bean
  public SnsAsyncClient snsAsyncClient(Optional<AwsCredentialsProvider> credentialsProviderOptional, DialobSettings settings) {
    return credentialsProviderOptional.map(awsCredentialsProvider ->
      SnsAsyncClient.builder()
        .credentialsProvider(awsCredentialsProvider)
        .region(Region.EU_CENTRAL_1)
      .build())
      .orElseGet(() -> SnsAsyncClient.create());
  }

  @Bean
  public S3Client s3Client(Optional<AwsCredentialsProvider> credentialsProviderOptional, DialobSettings settings) {
    return credentialsProviderOptional.map(awsCredentialsProvider ->
      S3Client.builder()
        .credentialsProvider(awsCredentialsProvider)
        .region(Region.of(settings.getAws().getRegion()))
      .build()).orElseGet(() -> S3Client.create());
  }

  @Bean
  @ConditionalOnProperty(prefix = "dialob.aws.sns", name = "formEventsTopicARN")
  public DialobFormEventsToSNSBridge dialobFormEventsToSNSBridge(SnsAsyncClient client, ObjectMapper objectMapper, DialobSettings settings) {
    String topicARN = settings.getAws().getSns().getFormEventsTopicARN();
    LOGGER.info("Delegate form events to {}", topicARN);
    return new DialobFormEventsToSNSBridge(client, objectMapper, topicARN);
  }

  @Bean
  @ConditionalOnProperty(prefix = "dialob.aws.sns", name = "questionnaireEventsTopicARN")
  public DialobQuestionnaireEventsToSNSBridge dialobQuestionnaireEventsToSNSBridge(SnsAsyncClient client, ObjectMapper objectMapper, DialobSettings settings) {
    String topicARN = settings.getAws().getSns().getQuestionnaireEventsTopicARN();
    LOGGER.info("Delegate questionnaire events to {}", topicARN);
    return new DialobQuestionnaireEventsToSNSBridge(client, objectMapper, topicARN);
  }

}
