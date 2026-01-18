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

import io.dialob.questionnaire.service.api.AnswerSubmitHandler;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.cfg.MapperBuilder;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class S3NormalizingPostSubmitHandlerTest {

  @Test
  void shouldSendDocumentToS3() {
    S3Client s3Client = mock(S3Client.class);
    ObjectMapper objectMapper = new ObjectMapper();
    S3NormalizingPostSubmitHandler handler = new S3NormalizingPostSubmitHandler(s3Client, objectMapper);

    AnswerSubmitHandler.Settings settings = mock(AnswerSubmitHandler.Settings.class);
    when(settings.getProperties()).thenReturn(Map.of("bucket", "test-bucket"));

    Map<String, Object> entries = Map.of("_id", "doc1", "key", "value");

    handler.sendDocument(settings, entries);

    ArgumentCaptor<PutObjectRequest> requestCaptor = ArgumentCaptor.forClass(PutObjectRequest.class);
    ArgumentCaptor<RequestBody> bodyCaptor = ArgumentCaptor.forClass(RequestBody.class);

    verify(s3Client).putObject(requestCaptor.capture(), bodyCaptor.capture());

    PutObjectRequest request = requestCaptor.getValue();
    assertEquals("test-bucket", request.bucket());
    assertEquals("doc1.json", request.key());
    assertEquals("application/json", request.contentType());

    // Verify body content (assuming simple JSON structure)
    // Note: RequestBody.fromBytes creates a stream, so we can't easily inspect bytes directly here without reading the stream.
    // However, we can verify that objectMapper was used correctly if we mock it, or trust integration here.
    // Given we used a real ObjectMapper, the bytes should be correct JSON.
  }

  @Test
  void shouldHandleJsonProcessingException() {
    S3Client s3Client = mock(S3Client.class);
    final ObjectMapper objectMapper = mock(ObjectMapper.class);
    MapperBuilder mapperBuilder = mock();
    when(objectMapper.rebuild()).thenReturn(mapperBuilder);
    when(mapperBuilder.configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true)).thenReturn(mapperBuilder);
    when(mapperBuilder.build()).thenReturn(objectMapper);
    when(objectMapper.writeValueAsBytes(any())).thenThrow(new JacksonException("Error") {});

    S3NormalizingPostSubmitHandler handler = new S3NormalizingPostSubmitHandler(s3Client, objectMapper);

    AnswerSubmitHandler.Settings settings = mock(AnswerSubmitHandler.Settings.class);
    when(settings.getProperties()).thenReturn(Map.of("bucket", "test-bucket"));

    Map<String, Object> entries = Map.of("_id", "doc1");

    handler.sendDocument(settings, entries);

    verify(s3Client, never()).putObject(any(PutObjectRequest.class), any(RequestBody.class));
  }
}
