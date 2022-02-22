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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import io.dialob.questionnaire.service.api.AnswerSubmitHandler;
import io.dialob.questionnaire.service.submit.AbstractNormalizingPostSubmitHandler;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

import java.util.Map;

@Slf4j
public class S3NormalizingPostSubmitHandler extends AbstractNormalizingPostSubmitHandler {

  private final ObjectMapper objectMapper;

  public S3NormalizingPostSubmitHandler(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper.copy().configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true);
  }

  @Override
  protected void sendDocument(AnswerSubmitHandler.Settings submitHandlerSettings, Map<String, Object> entries) {
    LOGGER.debug("sending {} to aws bucket {}", entries.get("_id"), submitHandlerSettings.getProperties().get("bucket"));
    final Map<String, Object> properties = submitHandlerSettings.getProperties();
    S3Client s3Client = S3Client.builder()
      .region(Region.of((String) properties.getOrDefault("region", "eu-central-1")))
      .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create((String) properties.get("accessKey"), (String) properties.get("secretKey"))))
      .build();
    try {
      PutObjectResponse result = s3Client.putObject(
        PutObjectRequest.builder()
          .bucket((String) properties.get("bucket"))
          .acl(ObjectCannedACL.PUBLIC_READ)
          .key(entries.get("_id") + ".json")
          .contentType("application/json")
          .build(),
        RequestBody.fromBytes(objectMapper.writeValueAsBytes(entries))
      );
    } catch (JsonProcessingException e) {
      LOGGER.error("could not construct normalized document" ,e);
    }
  }
}
