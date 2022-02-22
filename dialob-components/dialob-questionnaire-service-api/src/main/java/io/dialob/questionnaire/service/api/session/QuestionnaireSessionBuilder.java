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

import io.dialob.api.proto.ValueSet;
import io.dialob.api.questionnaire.Answer;
import io.dialob.api.questionnaire.ContextValue;
import io.dialob.api.questionnaire.Questionnaire;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Map;

public interface QuestionnaireSessionBuilder {

  @Nonnull
  QuestionnaireSession build();

  @Nonnull
  QuestionnaireSessionBuilder setCreateOnly(boolean createOnly);

  @Nonnull
  QuestionnaireSessionBuilder setActiveItem(String activeItem);

  @Nonnull
  QuestionnaireSessionBuilder setFormId(String formId);

  @Nonnull
  QuestionnaireSessionBuilder setFormRev(String formRev);

  @Nonnull
  QuestionnaireSessionBuilder setCreator(String owner);

  @Nonnull
  QuestionnaireSessionBuilder setOwner(String owner);

  @Nonnull
  QuestionnaireSessionBuilder setSubmitUrl(String submitUrl);

  @Nonnull
  QuestionnaireSessionBuilder setLanguage(String language);

  @Nonnull
  QuestionnaireSessionBuilder setStatus(Questionnaire.Metadata.Status status);

  @Nonnull
  QuestionnaireSessionBuilder setAdditionalProperties(Map<String, Object> additionalProperties);

  @Nonnull
  QuestionnaireSessionBuilder setQuestionnaire(Questionnaire questionnaire);

  @Nonnull
  QuestionnaireSessionBuilder setContextValues(List<ContextValue> contextValues);

  @Nonnull
  QuestionnaireSessionBuilder setAnswers(List<Answer> answers);

  @Nonnull
  QuestionnaireSessionBuilder setValueSets(List<ValueSet> valueSets);
}
