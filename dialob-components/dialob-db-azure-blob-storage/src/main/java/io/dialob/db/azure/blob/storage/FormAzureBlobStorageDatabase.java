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
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.umd.cs.findbugs.annotations.NonNull;
import io.dialob.api.form.Form;
import io.dialob.api.form.ImmutableForm;
import io.dialob.api.form.ImmutableFormMetadata;
import io.dialob.form.service.api.FormDatabase;
import io.dialob.form.service.api.ImmutableFormMetadataRow;

import java.util.Date;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * FormAzureBlobStorageDatabase is an implementation of the FormDatabase interface that
 * provides methods for managing form documents stored in Azure Blob Storage. It extends
 * the AbstractAzureBlobStorageDatabase to leverage its generic functionality while
 * specializing it for Form documents.
 * <p>
 * Features:
 * - Interacts with Azure Blob Storage to store, retrieve, and manage form documents.
 * - Uses JSON serialization/deserialization (via ObjectMapper) for storing and reading document data.
 * - Supports operations such as saving, deleting, checking existence, and retrieving metadata for forms.
 * - Provides efficient handling of form metadata with tenant-specific scoping.
 * <p>
 * This class handles document-specific operations, including updating document IDs or revisions
 * as part of the save process.
 */
public class FormAzureBlobStorageDatabase extends AbstractAzureBlobStorageDatabase<Form> implements FormDatabase {

  public FormAzureBlobStorageDatabase(BlobContainerClient blobContainerClient, ObjectMapper objectMapper, String prefix, String suffix) {
    super(blobContainerClient, Form.class, objectMapper, Objects.requireNonNullElse(prefix, "forms"), suffix);
  }


  @Override
  public void findAllMetadata(String tenantId, Form.Metadata metadata, @NonNull Consumer<FormMetadataRow> consumer) {
    forAllObjects(tenantId, object -> {
      String id = extractObjectName(object.getName());
      consumer.accept(ImmutableFormMetadataRow.of(
        id,
        ImmutableFormMetadata.builder()
          .lastSaved(Date.from(object.getProperties().getLastModified().toInstant()))
          .tenantId(tenantId)
          .build()
      ));
    });
  }
  @NonNull
  @Override
  protected Form updateDocumentId(@NonNull Form form, String id) {
    return ImmutableForm.builder().from(form).id(id).build();
  }

  @NonNull
  @Override
  protected Form updateDocumentRev(@NonNull Form form, String rev) {
    return ImmutableForm.builder().from(form).rev(rev).build();
  }

}
