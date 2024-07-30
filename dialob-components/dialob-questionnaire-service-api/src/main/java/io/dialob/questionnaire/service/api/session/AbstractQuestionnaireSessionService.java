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
public abstract class AbstractQuestionnaireSessionService implements QuestionnaireSessionService {

  private final QuestionnaireDatabase questionnaireDatabase;
  private final QuestionnaireSessionBuilderFactory questionnaireSessionBuilderFactory;
  private final CurrentTenant currentTenant;

  protected AbstractQuestionnaireSessionService(@NonNull QuestionnaireDatabase questionnaireDatabase,
                                                @NonNull QuestionnaireSessionBuilderFactory questionnaireSessionBuilderFactory, CurrentTenant currentTenant) {
    this.questionnaireDatabase = questionnaireDatabase;
    this.questionnaireSessionBuilderFactory = questionnaireSessionBuilderFactory;
    this.currentTenant = currentTenant;
  }

  protected QuestionnaireSession restore(String questionnaireDocumentId) {
    return restore(questionnaireDatabase.findOne(currentTenant.getId(), questionnaireDocumentId));
  }

  protected QuestionnaireSession restore(Questionnaire questionnaire) {
    LOGGER.debug("Restoring questionnaire session {} rev {}", questionnaire.getId(), questionnaire.getRev());
    return questionnaireSessionBuilderFactory.createQuestionnaireSessionBuilder()
      .setQuestionnaire(questionnaire)
      .build();
  }

  /**
   * This implementation returns a session from cache if it's present and adds a new session to cache if requested.
   * Requires cache configuration.
   *
   * @param questionnaireId
   * @param openIfClosed create a new session if not present
   * @return a new session or nothing
   */
  @Override
  public QuestionnaireSession findOne(@NonNull String questionnaireId, boolean openIfClosed) {
    if (openIfClosed) {
      return restore(questionnaireId);
    }
    return null;
  }
}
