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
import com.fasterxml.jackson.databind.SerializationFeature;
import io.dialob.questionnaire.service.api.AnswerSubmitHandler;
import io.dialob.questionnaire.service.submit.AbstractNormalizingPostSubmitHandler;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.util.Map;

/**
 * This class is responsible for handling the submission of normalized documents
 * to an AWS S3 bucket. It extends the AbstractNormalizingPostSubmitHandler, leveraging
 * its method to normalize the submitted data before sending it to S3.
 *
 * The class uses AWS SDK's S3Client to facilitate interactions with S3, and
 * Jackson's ObjectMapper to serialize the data into JSON format according to
 * the required configurations.
 *
 * Features:
 * - Serializes normalized questionnaire data into JSON format.
 * - Sends the serialized document to an S3 bucket, using the document's unique
 *   identifier as the object key.
 *
 * Constructor Details:
 * - The constructor accepts an S3Client and an ObjectMapper as parameters.
 * - The ObjectMapper is configured to order map entries by keys to ensure
 *   consistent serialization output.
 *
 * Key Overrides:
 * - The sendDocument method is overridden to handle the process of constructing
 *   and uploading the JSON document to S3.
 *
 * Logging:
 * - Debug logs are provided to track the process of sending documents to S3.
 * - Errors encountered during the JSON serialization process are logged.
 */
@Slf4j
public class S3NormalizingPostSubmitHandler extends AbstractNormalizingPostSubmitHandler {

  private final S3Client s3Client;

  private final ObjectMapper objectMapper;

  public S3NormalizingPostSubmitHandler(S3Client s3Client, ObjectMapper objectMapper) {
    this.s3Client = s3Client;
    this.objectMapper = objectMapper.copy().configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true);
  }

  /**
   * Sends a document to an AWS S3 bucket. The document is serialized to JSON format and
   * stored in the specified bucket with a key derived from the document's unique identifier.
   *
   * @param submitHandlerSettings the settings containing properties required for submission,
   *                              including the target S3 bucket.
   * @param entries a map representing the document to be sent, where the document's unique
   *                identifier is assumed to be stored under the "_id" key.
   */
  @Override
  protected void sendDocument(AnswerSubmitHandler.Settings submitHandlerSettings, Map<String, Object> entries) {
    LOGGER.debug("sending {} to aws bucket {}", entries.get("_id"), submitHandlerSettings.getProperties().get("bucket"));
    final Map<String, Object> properties = submitHandlerSettings.getProperties();
    try {
      s3Client.putObject(
        PutObjectRequest.builder()
          .bucket((String) properties.get("bucket"))
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
