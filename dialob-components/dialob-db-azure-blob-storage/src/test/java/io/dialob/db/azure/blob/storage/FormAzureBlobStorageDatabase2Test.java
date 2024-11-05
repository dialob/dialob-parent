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
package io.dialob.db.azure.blob.storage;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.dialob.api.form.ImmutableForm;
import io.dialob.api.form.ImmutableFormMetadata;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class FormAzureBlobStorageDatabase2Test {

  @Test
  public void shouldTrimPrefix() {

    BlobContainerClient blobContainerClient = Mockito.mock();
    BlobClient blobClient = Mockito.mock();
    when(blobContainerClient.getBlobClient(anyString())).thenReturn(blobClient);

    FormAzureBlobStorageDatabase database = new FormAzureBlobStorageDatabase(blobContainerClient, new ObjectMapper(), "\\alternative/sub///");
    database.save("00000000-0000-0000-0000-000000000000", ImmutableForm.builder()
      .metadata(ImmutableFormMetadata.builder()
        .tenantId("00000000-0000-0000-0000-000000000000")
        .label("test")
        .build())
      .build());

    verify(blobContainerClient).getBlobClient(matches("alternative/sub/00000000-0000-0000-0000-000000000000/[0-9a-f]{32}"));
    verifyNoMoreInteractions(blobContainerClient);
  }

}
