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
package io.dialob.db.azure.blob.storage;

import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.azure.storage.blob.models.BlobItem;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.dialob.api.form.Form;
import io.dialob.api.form.ImmutableForm;
import io.dialob.api.form.ImmutableFormMetadata;
import io.dialob.db.spi.exceptions.DocumentNotFoundException;
import net.bytebuddy.utility.RandomString;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.testcontainers.containers.GenericContainer;

import java.util.function.Consumer;

import static org.mockito.ArgumentMatchers.argThat;

class FormAzureBlobStorageDatabaseTest {


  private static final String AZURITE_IMAGE = "mcr.microsoft.com/azure-storage/azurite:3.35.0";
  private static final GenericContainer<?> AZURITE_CONTAINER = new GenericContainer<>(AZURITE_IMAGE)
    .withCommand("azurite-blob", "--blobHost", "0.0.0.0")
    .withExposedPorts(10000);

  private static final String DEFAULT_AZURITE_CONNECTION_STRING = "DefaultEndpointsProtocol=http;AccountName=devstoreaccount1;AccountKey=Eby8vdM02xNOcqFlqUwJPLlmEtlCDXJ1OUzFT50uSRZ6IFsuFq2UVErCz4I6tq/K1SZFPTOtr/KBHBeksoGMGw==;BlobEndpoint=http://127.0.0.1:%s/devstoreaccount1;";
  private static final String CONTAINER_NAME = RandomString.make().toLowerCase();

  ObjectMapper objectMapper = new ObjectMapper();

  private static BlobServiceClient blobServiceClient;
  private static BlobContainerClient blobContainerClient;


  @BeforeAll
  public static void init() {
    AZURITE_CONTAINER.start();
    var blobPort = AZURITE_CONTAINER.getMappedPort(10000);
    blobServiceClient = new BlobServiceClientBuilder().connectionString(DEFAULT_AZURITE_CONNECTION_STRING.formatted(blobPort)).buildClient();
    blobContainerClient = blobServiceClient.createBlobContainer(CONTAINER_NAME);
  }

  @Test
  void shouldGetSameObjectBackFromStorage() {
    FormAzureBlobStorageDatabase database = new FormAzureBlobStorageDatabase(blobContainerClient, objectMapper, "forms", null);
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
  void shouldRevisionObject() {
    FormAzureBlobStorageDatabase database = new FormAzureBlobStorageDatabase(blobContainerClient, objectMapper, "forms", null);
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
  void shouldThrowDocumentNotFoundExceptionIfObjectNotFound() {
    var database = new FormAzureBlobStorageDatabase(blobContainerClient, objectMapper, "forms", null);
    Assertions.assertThrows(DocumentNotFoundException.class, () -> database.findOne("00000000-0000-0000-0000-000000000000", "not-exists"));
  }


  @Test
  void shouldGetFalseFromExistsWhenDocumentDoNotExists() {
    var database = new FormAzureBlobStorageDatabase(blobContainerClient, objectMapper, "forms", null);
    Assertions.assertFalse(database.exists("00000000-0000-0000-0000-000000000000", "not-exists"));
  }

  @Test
  void shouldGetTrueFromExistsWhenDocumentDoExists() {
    var database = new FormAzureBlobStorageDatabase(blobContainerClient, objectMapper, "forms", null);
    Form saved = database.save("00000000-0000-0000-0000-000000000000", ImmutableForm.builder()
      .metadata(ImmutableFormMetadata.builder()
        .tenantId("00000000-0000-0000-0000-000000000000")
        .label("test")
        .build())
      .build());

    Assertions.assertTrue(database.exists("00000000-0000-0000-0000-000000000000", saved.getId()));
  }

  @Test
  void shouldBeAbleDeleteNonExistingDocument() {
    FormAzureBlobStorageDatabase database = new FormAzureBlobStorageDatabase(blobContainerClient, objectMapper, "forms", null);
    Assertions.assertFalse(database.exists("00000000-0000-0000-0000-000000000000", "not-exists"));
    Assertions.assertFalse(database.delete("00000000-0000-0000-0000-000000000000", "not-exists"));
  }

  @Test
  void shouldBeAbleDeleteExistingDocument() {
    FormAzureBlobStorageDatabase database = new FormAzureBlobStorageDatabase(blobContainerClient, objectMapper, "forms", null);
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
  void shouldScanBucket() {
    FormAzureBlobStorageDatabase database = new FormAzureBlobStorageDatabase(blobServiceClient.createBlobContainer("should-scan-bucket"), objectMapper, "forms", null);

    Consumer<BlobItem> scanner = Mockito.mock();
    database.forAllObjects("00000000-0000-0000-0000-000000000000", scanner);
    Mockito.verifyNoMoreInteractions(scanner);

    Form saved = database.save("00000000-0000-0000-0000-000000000000", ImmutableForm.builder()
      .metadata(ImmutableFormMetadata.builder()
        .tenantId("00000000-0000-0000-0000-000000000000")
        .label("test")
        .build())
      .build());
    database.forAllObjects("00000000-0000-0000-0000-000000000000", scanner);
    Mockito.verify(scanner).accept(argThat(summary -> summary.getName().endsWith(saved.getId())));
    Mockito.verifyNoMoreInteractions(scanner);
  }

}
