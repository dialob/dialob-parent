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
package io.dialob.db.gcdatastore.database;

import io.dialob.api.questionnaire.Questionnaire;
import io.dialob.db.gcdatastore.repository.QuestionnaireRepository;
import io.dialob.questionnaire.service.api.ImmutableMetadataRow;
import io.dialob.questionnaire.service.api.QuestionnaireDatabase;
import org.springframework.lang.NonNull;

import java.util.function.Consumer;

public class DatastoreQuestionnaireDatabase extends BaseDatastoreDatabase<Questionnaire, QuestionnaireRepository> implements QuestionnaireDatabase {

  public DatastoreQuestionnaireDatabase(final QuestionnaireRepository repository) {
    super(repository);
  }

  @Override
  public void findAllMetadata(String tenantId, String ownerId, String formId, String formName, String formTag, Questionnaire.Metadata.Status status, @NonNull Consumer<MetadataRow> consumer) {
    handleAction(repository -> {
      repository.findAllMetadata().stream().forEach(document -> consumer.accept(ImmutableMetadataRow.of(document.getId(), document.getMetadata())));
      return null;
    });
  }

}
