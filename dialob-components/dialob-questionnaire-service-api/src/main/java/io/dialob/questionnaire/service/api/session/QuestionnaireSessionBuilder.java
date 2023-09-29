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
import io.dialob.api.proto.ValueSet;
import io.dialob.api.questionnaire.Answer;
import io.dialob.api.questionnaire.ContextValue;
import io.dialob.api.questionnaire.Questionnaire;

import java.util.List;
import java.util.Map;

public interface QuestionnaireSessionBuilder {

  @NonNull
  QuestionnaireSession build();

  @NonNull
  QuestionnaireSessionBuilder setCreateOnly(boolean createOnly);

  @NonNull
  QuestionnaireSessionBuilder setActiveItem(String activeItem);

  @NonNull
  QuestionnaireSessionBuilder setFormId(String formId);

  @NonNull
  QuestionnaireSessionBuilder setFormRev(String formRev);

  @NonNull
  QuestionnaireSessionBuilder setCreator(String owner);

  @NonNull
  QuestionnaireSessionBuilder setOwner(String owner);

  @NonNull
  QuestionnaireSessionBuilder setSubmitUrl(String submitUrl);

  @NonNull
  QuestionnaireSessionBuilder setLanguage(String language);

  @NonNull
  QuestionnaireSessionBuilder setStatus(Questionnaire.Metadata.Status status);

  @NonNull
  QuestionnaireSessionBuilder setAdditionalProperties(Map<String, Object> additionalProperties);

  @NonNull
  QuestionnaireSessionBuilder setQuestionnaire(Questionnaire questionnaire);

  @NonNull
  QuestionnaireSessionBuilder setContextValues(List<ContextValue> contextValues);

  @NonNull
  QuestionnaireSessionBuilder setAnswers(List<Answer> answers);

  @NonNull
  QuestionnaireSessionBuilder setValueSets(List<ValueSet> valueSets);
}
