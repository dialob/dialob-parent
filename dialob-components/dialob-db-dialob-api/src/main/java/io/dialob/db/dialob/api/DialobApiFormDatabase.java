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
package io.dialob.db.dialob.api;

import io.dialob.api.form.Form;
import io.dialob.api.form.ImmutableForm;
import io.dialob.form.service.api.FormDatabase;
import org.springframework.lang.NonNull;

import java.util.function.Consumer;

public class DialobApiFormDatabase extends AbstractDialobApiDatabase<Form> implements FormDatabase {


  public DialobApiFormDatabase(DialobApiTemplate dialobApiTemplate) {
    super(dialobApiTemplate, "forms", Form.class);
  }

  @Override
  public void findAllMetadata(String tenantId, Form.Metadata metadata, @NonNull Consumer<FormMetadataRow> consumer) {
    // TODO implement this
  }

  @NonNull
  @Override
  protected Form updateDocumentId(@NonNull Form document, String id) {
    return ImmutableForm.builder().from(document).id(id).build();
  }

  @NonNull
  @Override
  protected Form updateDocumentRev(@NonNull Form document, String rev) {
    return ImmutableForm.builder().from(document).rev(rev).build();
  }
}
