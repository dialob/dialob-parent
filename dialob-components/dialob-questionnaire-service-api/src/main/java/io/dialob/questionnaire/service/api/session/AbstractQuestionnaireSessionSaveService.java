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
package io.dialob.questionnaire.service.api.session;


import edu.umd.cs.findbugs.annotations.NonNull;
import io.dialob.api.questionnaire.Questionnaire;
import io.dialob.questionnaire.service.api.QuestionnaireDatabase;
import io.dialob.security.tenant.CurrentTenant;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AbstractQuestionnaireSessionSaveService implements QuestionnaireSessionSaveService {

  private final QuestionnaireDatabase questionnaireDatabase;

  private final CurrentTenant currentTenant;

  protected AbstractQuestionnaireSessionSaveService(@NonNull QuestionnaireDatabase questionnaireDatabase, CurrentTenant currentTenant) {
    this.questionnaireDatabase = questionnaireDatabase;
    this.currentTenant = currentTenant;
  }

  @Override
  @NonNull
  public QuestionnaireSession save(@NonNull QuestionnaireSession questionnaireSession) {
    final Questionnaire questionnaire = questionnaireDatabase.save(currentTenant.getId(), questionnaireSession.getQuestionnaire());
    return questionnaireSession.withIdAndRev(questionnaire.getId(), questionnaire.getRev());
  }

}
