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
package io.dialob.db.file;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.umd.cs.findbugs.annotations.NonNull;
import io.dialob.api.form.Form;
import io.dialob.api.form.ImmutableForm;
import io.dialob.form.service.api.FormDatabase;
import io.dialob.form.service.api.ImmutableFormMetadataRow;

import java.util.function.Consumer;

public class FormFileDatabase extends AbstractFileDatabase<Form> implements FormDatabase {
  public FormFileDatabase(String directory,
                          ObjectMapper objectMapper) {
    super(Form.class, directory, objectMapper);
  }

  @Override
  public void findAllMetadata(String tenantId, Form.Metadata metadata, @NonNull Consumer<FormMetadataRow> consumer) {
    forAllFiles(file -> {
      final Form document = loadFile(file);
      if (document != null) {
        consumer.accept(ImmutableFormMetadataRow.of(document.getId(), document.getMetadata()));
      }
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
