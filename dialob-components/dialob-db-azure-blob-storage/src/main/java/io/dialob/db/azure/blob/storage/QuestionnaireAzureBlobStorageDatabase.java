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
import io.dialob.api.questionnaire.ImmutableQuestionnaire;
import io.dialob.api.questionnaire.ImmutableQuestionnaireMetadata;
import io.dialob.api.questionnaire.Questionnaire;
import io.dialob.questionnaire.service.api.ImmutableMetadataRow;
import io.dialob.questionnaire.service.api.QuestionnaireDatabase;

import java.util.Date;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * Implements the {@link QuestionnaireDatabase} interface to store and manage
 * {@link Questionnaire} documents as blobs in Azure Blob Storage. Extends the
 * functionality provided by {@link AbstractAzureBlobStorageDatabase}.
 * <p>
 * This class provides CRUD operations for {@link Questionnaire} entities stored in Azure Blob
 * Storage along with metadata-specific query capabilities.
 */
public class QuestionnaireAzureBlobStorageDatabase extends AbstractAzureBlobStorageDatabase<Questionnaire> implements QuestionnaireDatabase {

  public QuestionnaireAzureBlobStorageDatabase(BlobContainerClient blobContainerClient, ObjectMapper objectMapper, String prefix, String suffix) {
    super(blobContainerClient, Questionnaire.class, objectMapper, Objects.requireNonNullElse(prefix, "questionnaires"), suffix);
  }


  @Override
  public void findAllMetadata(String tenantId, String ownerId, String formId, String formName, String formTag, Questionnaire.Metadata.Status status, @NonNull Consumer<MetadataRow> consumer) {
    forAllObjects(tenantId, object -> {
      String id = extractObjectName(object.getName());
      consumer.accept(ImmutableMetadataRow.of(
        id,
        ImmutableQuestionnaireMetadata.builder()
          .lastAnswer(Date.from(object.getProperties().getLastModified().toInstant()))
          .build()
      ));
    });
  }

  @NonNull
  @Override
  protected Questionnaire updateDocumentId(@NonNull Questionnaire document, String id) {
    return ImmutableQuestionnaire.builder().from(document).id(id).build();
  }

  @NonNull
  @Override
  protected Questionnaire updateDocumentRev(@NonNull Questionnaire document, String rev) {
    return ImmutableQuestionnaire.builder().from(document).rev(rev).build();
  }

}
