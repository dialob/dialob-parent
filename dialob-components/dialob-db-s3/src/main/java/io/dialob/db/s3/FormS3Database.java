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
package io.dialob.db.s3;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.umd.cs.findbugs.annotations.NonNull;
import io.dialob.api.form.Form;
import io.dialob.api.form.ImmutableForm;
import io.dialob.api.form.ImmutableFormMetadata;
import io.dialob.form.service.api.FormDatabase;
import io.dialob.form.service.api.ImmutableFormMetadataRow;
import software.amazon.awssdk.services.s3.S3Client;

import java.util.Date;
import java.util.function.Consumer;

public class FormS3Database extends AbstractS3Database<Form> implements FormDatabase {
  public FormS3Database(S3Client s3Client, ObjectMapper objectMapper, String bucketName, String prefix) {
    super(s3Client, Form.class, objectMapper, bucketName, prefix);
  }


  @Override
  public void findAllMetadata(String tenantId, Form.Metadata metadata, @NonNull Consumer<FormMetadataRow> consumer) {
    forAllObjects(tenantId, object -> {
      String id = extractObjectName(object.key());
      consumer.accept(ImmutableFormMetadataRow.of(
        id,
        ImmutableFormMetadata.builder()
          .lastSaved(new Date(object.lastModified().toEpochMilli()))
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

  @Override
  protected String tenantPrefix(String tenantId) {
    return "questionnaires/" + tenantId;
  }

}
