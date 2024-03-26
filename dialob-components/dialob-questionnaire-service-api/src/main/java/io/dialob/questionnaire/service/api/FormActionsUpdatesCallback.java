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
package io.dialob.questionnaire.service.api;

import edu.umd.cs.findbugs.annotations.NonNull;
import io.dialob.api.proto.ActionItem;
import io.dialob.api.proto.ValueSet;
import io.dialob.api.questionnaire.Error;
import io.dialob.questionnaire.service.api.session.QuestionnaireSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Locale;


public class FormActionsUpdatesCallback implements QuestionnaireSession.UpdatesCallback {

  private static final Logger LOGGER = LoggerFactory.getLogger(FormActionsUpdatesCallback.class);

  private final FormActions formActions;

  public FormActionsUpdatesCallback(@NonNull FormActions formActions) {
    this.formActions = formActions;
  }

  @NonNull
  @Override
  public FormActionsUpdatesCallback questionAdded(@NonNull ActionItem question) {
    LOGGER.debug("newQuestion({})", question);
    formActions.newQuestion(question);
    return this;
  }

  @NonNull
  @Override
  public FormActionsUpdatesCallback questionUpdated(@NonNull ActionItem question) {
    LOGGER.debug("updateQuestion({})", question);
    formActions.updateQuestion(question);
    return this;
  }

  @NonNull
  @Override
  public FormActionsUpdatesCallback questionRemoved(@NonNull String itemId) {
    LOGGER.debug("removeQuestion({})", itemId);
    formActions.removeQuestion(itemId);
    return this;
  }

  @NonNull
  @Override
  public FormActionsUpdatesCallback valueSetAdded(@NonNull ValueSet valueSet) {
    LOGGER.debug("valueSetAdded({})", valueSet);
    formActions.newValueSet(valueSet);
    return this;
  }

  @NonNull
  @Override
  public FormActionsUpdatesCallback valueSetUpdated(@NonNull ValueSet valueSet) {
    LOGGER.debug("valueSetUpdated({})", valueSet);
    formActions.updateValueSet(valueSet);
    return this;
  }

  @NonNull
  @Override
  public FormActionsUpdatesCallback valueSetRemoved(@NonNull String valueSetId) {
    LOGGER.debug("valueSetRemoved({})", valueSetId);
    formActions.removeValueSet(valueSetId);
    return this;
  }

  @NonNull
  @Override
  public FormActionsUpdatesCallback errorAdded(@NonNull Error error) {
    LOGGER.debug("addError({})", error);
    formActions.addError(error);
    return this;
  }

  @NonNull
  @Override
  public FormActionsUpdatesCallback errorRemoved(@NonNull Error error) {
    LOGGER.debug("removeError({})", error);
    formActions.removeError(error);
    return this;
  }

  @NonNull
  @Override
  public FormActionsUpdatesCallback removeAll() {
    LOGGER.debug("removeAll()");
    formActions.removeAll();
    return this;
  }

  @NonNull
  @Override
  public FormActionsUpdatesCallback locale(Locale locale) {
    LOGGER.debug("locale({})", locale);
    formActions.locale(locale);
    return this;
  }

  @NonNull
  @Override
  public FormActionsUpdatesCallback completed() {
    LOGGER.debug("completed()");
    formActions.complete();
    return this;
  }
}
