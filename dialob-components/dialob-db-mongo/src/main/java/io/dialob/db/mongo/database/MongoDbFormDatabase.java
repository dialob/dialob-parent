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
package io.dialob.db.mongo.database;

import edu.umd.cs.findbugs.annotations.NonNull;
import io.dialob.api.form.Form;
import io.dialob.api.form.ModifiableForm;
import io.dialob.db.mongo.repository.FormRepository;
import io.dialob.form.service.api.FormDatabase;
import io.dialob.form.service.api.ImmutableFormMetadataRow;

import java.util.function.Consumer;

public class MongoDbFormDatabase extends BaseMongoDbDatabase<Form, ModifiableForm, FormRepository> implements FormDatabase {

  public MongoDbFormDatabase(final FormRepository repository) {
    super(repository);
  }

  @Override
  protected ModifiableForm toMongo(Form document) {
    return ModifiableForm.create().from(document);
  }

  @Override
  public void findAllMetadata(String tenantId, Form.Metadata metadata, @NonNull Consumer<FormMetadataRow> consumer) {
    doMongo(repository -> {
      repository.findAllMetadata().forEach(document -> consumer.accept(ImmutableFormMetadataRow.of(document.getId(), document.getMetadata())));
      return null;
    });
  }
}
