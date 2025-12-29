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
package io.dialob.db.s3;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.dialob.api.questionnaire.Questionnaire;
import io.dialob.questionnaire.service.api.QuestionnaireDatabase;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Object;

import java.time.Instant;
import java.util.function.Consumer;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class QuestionnaireS3DatabaseTest {

  @Test
  void shouldSendQuestionnaireToS3() {
    S3Client s3Client = mock();
    QuestionnaireS3Database database = new QuestionnaireS3Database(s3Client, new ObjectMapper(), "bucket", "prefix");
    var result = database.save("t1", new Questionnaire.Builder()
      .metadata(new Questionnaire.Metadata.Builder()
        .tenantId("t1")
        .formId("form1")
        .label("label")
        .build())
      .build());
    Assertions.assertThat(result).isNotNull();
    verify(s3Client, Mockito.times(1)).putObject(any(PutObjectRequest.class), any(RequestBody.class));
    verifyNoMoreInteractions(s3Client);
  }

  @Test
  void shouldFindAllMetadata() {
    S3Client s3Client = mock();
    Consumer<QuestionnaireDatabase.MetadataRow> consumer = Mockito.mock();
    QuestionnaireS3Database database = new QuestionnaireS3Database(s3Client, new ObjectMapper(), "bucket", "prefix");
    ListObjectsV2Response listObjectsV2Response = mock();
    when(s3Client.listObjectsV2(any(ListObjectsV2Request.class))).thenReturn(listObjectsV2Response);
    Instant now = Instant.now();
    when(listObjectsV2Response.contents()).thenReturn(java.util.List.of(
      S3Object.builder()
        .key("key1")
        .lastModified(now)
        .build()
    ));

    database.findAllMetadata("t1", null, null, null, null, null, consumer);

    verify(s3Client, times(1)).listObjectsV2(any(ListObjectsV2Request.class));
    verify(listObjectsV2Response, times(1)).contents();
    verify(listObjectsV2Response, times(1)).nextContinuationToken();
    verify(consumer, times(1)).accept(argThat(argument -> argument.equals(new QuestionnaireDatabase.MetadataRow.Builder()
        .id("key1")
        .value(new Questionnaire.Metadata.Builder()
          .status(Questionnaire.Metadata.Status.NEW)
          .lastAnswer(now)
          .build())
      .build())));
    verifyNoMoreInteractions(s3Client, listObjectsV2Response, consumer);
  }

}
