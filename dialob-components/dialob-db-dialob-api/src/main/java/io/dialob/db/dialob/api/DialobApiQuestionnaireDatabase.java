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

import io.dialob.api.questionnaire.ImmutableQuestionnaire;
import io.dialob.api.questionnaire.Questionnaire;
import io.dialob.questionnaire.service.api.QuestionnaireDatabase;
import edu.umd.cs.findbugs.annotations.NonNull;

import java.util.function.Consumer;

public class DialobApiQuestionnaireDatabase extends AbstractDialobApiDatabase<Questionnaire> implements QuestionnaireDatabase {


  public DialobApiQuestionnaireDatabase(DialobApiTemplate dialobApiTemplate) {
    super(dialobApiTemplate, "questionnaires", Questionnaire.class);
  }


  @Override
  public void findAllMetadata(String tenantId, String ownerId, String formId, String formName, String formTag, Questionnaire.Metadata.Status status, @NonNull Consumer<MetadataRow> consumer) {
    throw new UnsupportedOperationException("findAllMetadata not supported");

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
