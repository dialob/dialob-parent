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

import com.adobe.testing.s3mock.junit5.S3MockExtension;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.dialob.api.form.Form;
import io.dialob.api.form.ImmutableForm;
import io.dialob.api.form.ImmutableFormMetadata;
import io.dialob.db.spi.exceptions.DocumentNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.S3Object;

import java.util.function.Consumer;

import static org.mockito.ArgumentMatchers.argThat;

@ExtendWith(S3MockExtension.class)
class FormS3DatabaseTest {

  ObjectMapper objectMapper = new ObjectMapper();

  @BeforeAll
  public static void init(final S3Client s3Client) {
    s3Client.createBucket(CreateBucketRequest.builder().bucket("testii").build());
  }

  @Test
  public void shouldGetSameObjectBackFromStorage(final S3Client s3Client) throws Exception {
    FormS3Database database = new FormS3Database(s3Client, objectMapper, "testii", "forms");
    Form saved = database.save("00000000-0000-0000-0000-000000000000", ImmutableForm.builder()
      .metadata(ImmutableFormMetadata.builder()
        .tenantId("00000000-0000-0000-0000-000000000000")
        .label("test")
        .build())
      .build());
    Form loaded = database.findOne(saved.getMetadata().getTenantId(), saved.getId());
    Assertions.assertEquals(saved, loaded);
  }

  @Test
  public void shouldRevisionObject(final S3Client s3Client) throws Exception {
    FormS3Database database = new FormS3Database(s3Client, objectMapper, "testii", "forms");
    Form saved = database.save("00000000-0000-0000-0000-000000000000", ImmutableForm.builder()
      .metadata(ImmutableFormMetadata.builder()
        .tenantId("00000000-0000-0000-0000-000000000000")
        .label("test")
        .build())
      .build());

    Assertions.assertEquals("1", saved.getRev());
    saved = database.save("00000000-0000-0000-0000-000000000000", saved);
    Assertions.assertEquals("2", saved.getRev());
  }



  @Test
  public void shouldThrowDocumentNotFoundExceptionIfObjectNotFound(final S3Client s3Client) throws Exception {
    FormS3Database database = new FormS3Database(s3Client, objectMapper, "testii", "forms");
    Assertions.assertThrows(DocumentNotFoundException.class, () -> database.findOne("00000000-0000-0000-0000-000000000000", "not-exists"));
  }


  @Test
  public void shouldGetFalseFromExistsWhenDocumentDoNotExists(final S3Client s3Client) throws Exception {
    FormS3Database database = new FormS3Database(s3Client, objectMapper, "testii", "forms");
    Assertions.assertFalse(database.exists("00000000-0000-0000-0000-000000000000", "not-exists"));
  }

  @Test
  public void shouldGetTrueFromExistsWhenDocumentDoExists(final S3Client s3Client) throws Exception {
    FormS3Database database = new FormS3Database(s3Client, objectMapper, "testii", "forms");
    Form saved = database.save("00000000-0000-0000-0000-000000000000", ImmutableForm.builder()
      .metadata(ImmutableFormMetadata.builder()
        .tenantId("00000000-0000-0000-0000-000000000000")
        .label("test")
        .build())
      .build());

    Assertions.assertTrue(database.exists("00000000-0000-0000-0000-000000000000", saved.getId()));
  }

  @Test
  public void shouldBeAbleDeleteNonExistingDocument(final S3Client s3Client) throws Exception {
    FormS3Database database = new FormS3Database(s3Client, objectMapper, "testii", "forms");
    Assertions.assertFalse(database.exists("00000000-0000-0000-0000-000000000000", "not-exists"));
    Assertions.assertFalse(database.delete("00000000-0000-0000-0000-000000000000", "not-exists"));
  }

  @Test
  public void shouldBeAbleDeleteExistingDocument(final S3Client s3Client) throws Exception {
    FormS3Database database = new FormS3Database(s3Client, objectMapper, "testii", "forms");
    Form saved = database.save("00000000-0000-0000-0000-000000000000", ImmutableForm.builder()
      .metadata(ImmutableFormMetadata.builder()
        .tenantId("00000000-0000-0000-0000-000000000000")
        .label("test")
        .build())
      .build());

    Assertions.assertTrue(database.exists("00000000-0000-0000-0000-000000000000", saved.getId()));
    Assertions.assertTrue(database.delete("00000000-0000-0000-0000-000000000000", saved.getId()));
    Assertions.assertFalse(database.exists("00000000-0000-0000-0000-000000000000", saved.getId()));
  }

  @Test
  public void shouldScanBucket(final S3Client s3Client) throws Exception {
    s3Client.createBucket(CreateBucketRequest.builder().bucket("should-scan-bucket").build());
    FormS3Database database = new FormS3Database(s3Client, objectMapper, "should-scan-bucket", "forms");

    Consumer<S3Object> scanner = Mockito.mock(Consumer.class);
    database.forAllObjects("00000000-0000-0000-0000-000000000000", scanner);
    Mockito.verifyNoMoreInteractions(scanner);

    Form saved = database.save("00000000-0000-0000-0000-000000000000", ImmutableForm.builder()
      .metadata(ImmutableFormMetadata.builder()
        .tenantId("00000000-0000-0000-0000-000000000000")
        .label("test")
        .build())
      .build());
    database.forAllObjects("00000000-0000-0000-0000-000000000000", scanner);
    Mockito.verify(scanner).accept(argThat(summary -> summary.key().endsWith(saved.getId())));
    Mockito.verifyNoMoreInteractions(scanner);
  }

}
