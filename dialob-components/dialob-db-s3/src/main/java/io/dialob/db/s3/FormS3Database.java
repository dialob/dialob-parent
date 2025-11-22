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

import tools.jackson.databind.ObjectMapper;
import edu.umd.cs.findbugs.annotations.NonNull;
import io.dialob.api.form.Form;
import io.dialob.form.service.api.FormDatabase;
import software.amazon.awssdk.services.s3.S3Client;

import java.util.function.Consumer;

public class FormS3Database extends AbstractS3Database<Form> implements FormDatabase {
  public FormS3Database(S3Client s3Client, ObjectMapper objectMapper, String bucketName, String prefix) {
    super(s3Client, Form.class, objectMapper, bucketName, prefix);
  }


  @Override
  public void findAllMetadata(String tenantId, Form.Metadata metadata, @NonNull Consumer<FormMetadataRow> consumer) {
    forAllObjects(tenantId, object -> {
      String id = extractObjectName(object.key());
      consumer.accept(FormMetadataRow.of(
        id,
        new Form.Metadata.Builder()
          .lastSaved(object.lastModified())
          .tenantId(tenantId)
          .label(id)
          .build()
      ));
    });
  }
  @NonNull
  @Override
  protected Form updateDocumentId(@NonNull Form form, String id) {
    return new Form.Builder().from(form).id(id).build();
  }

  @NonNull
  @Override
  protected Form updateDocumentRev(@NonNull Form form, String rev) {
    return new Form.Builder().from(form).rev(rev).build();
  }

  @Override
  protected String tenantPrefix(String tenantId) {
    return "questionnaires/" + tenantId;
  }

}
