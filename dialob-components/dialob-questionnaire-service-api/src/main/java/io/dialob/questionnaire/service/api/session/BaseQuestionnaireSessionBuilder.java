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
import io.dialob.api.form.Form;
import io.dialob.api.proto.ValueSet;
import io.dialob.api.questionnaire.*;
import io.dialob.questionnaire.service.api.FormDataMissingException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

@Slf4j
public abstract class BaseQuestionnaireSessionBuilder implements QuestionnaireSessionBuilder {

  public static final String LATEST_REV = "LATEST";

  private final FormFinder formFinder;

  private boolean createOnly;

  private Questionnaire questionnaire;

  private String formRev;

  private String activeItem;

  private String formId;

  private String creator;

  private String owner;

  private String submitUrl;

  private List<ContextValue> contextValues;

  private String language = "en";

  private List<Answer> answers;

  private List<ValueSet> valueSets;

  private Questionnaire.Metadata.Status status;

  private Map<String, Object> additionalProperties;

  protected BaseQuestionnaireSessionBuilder(@NonNull FormFinder formFinder) {
    this.formFinder = formFinder;
  }

  @Override
  @NonNull
  public QuestionnaireSessionBuilder activeItem(String activeItem) {
    this.activeItem = activeItem;
    return this;
  }

  @Override
  @NonNull
  public QuestionnaireSessionBuilder formId(String formId) {
    this.formId = formId;
    return this;
  }

  @NonNull
  public QuestionnaireSessionBuilder creator(String creator) {
    this.creator = creator;
    return this;
  }

  @NonNull
  public QuestionnaireSessionBuilder owner(String owner) {
    this.owner = owner;
    return this;
  }

  @Override
  @NonNull
  public QuestionnaireSessionBuilder formRev(String formRev) {
    this.formRev = formRev;
    return this;
  }

  @Override
  @NonNull
  public QuestionnaireSessionBuilder submitUrl(String submitUrl) {
    this.submitUrl = submitUrl;
    return this;
  }

  @Override
  @NonNull
  public QuestionnaireSessionBuilder contextValues(List<ContextValue> contextValues) {
    this.contextValues = contextValues;
    return this;
  }

  @Override
  @NonNull
  public QuestionnaireSessionBuilder createOnly(boolean createOnly) {
    this.createOnly = createOnly;
    return this;
  }

  @Override
  @NonNull
  public QuestionnaireSessionBuilder language(String language) {
    this.language = language;
    if (StringUtils.isBlank(language)) {
      this.language = "en";
    }
    return this;
  }

  @Override
  @NonNull
  public QuestionnaireSessionBuilder status(Questionnaire.Metadata.Status status) {
    this.status = status;
    return this;
  }

  @Override
  @NonNull
  public QuestionnaireSessionBuilder answers(List<Answer> answers) {
    this.answers = answers;
    return this;
  }

  @NonNull
  @Override
  public QuestionnaireSessionBuilder valueSets(List<ValueSet> valueSets) {
    this.valueSets = valueSets;
    return this;
  }

  @NonNull
  @Override
  public QuestionnaireSessionBuilder additionalProperties(Map<String, Object> additionalProperties) {
    this.additionalProperties = additionalProperties;
    return this;
  }

  @Override
  @NonNull
  public QuestionnaireSessionBuilder questionnaire(Questionnaire questionnaire) {
    this.questionnaire = questionnaire;
    if (this.questionnaire != null) {
      final Questionnaire.Metadata metadata = this.questionnaire.getMetadata();
      if (activeItem == null) {
        activeItem(this.questionnaire.getActiveItem());
      }
      if (formId == null) {
        formId(metadata.getFormId());
      }
      if (formRev == null) {
        formRev(metadata.getFormRev());
      }
      if (owner == null) {
        owner(metadata.getOwner());
      }
      if (creator == null) {
        creator(metadata.getCreator());
      }
      if (additionalProperties == null) {
        additionalProperties(metadata.getAdditionalProperties());
      }
      language(metadata.getLanguage());
    }
    return this;
  }

  @NonNull
  protected Questionnaire createNewQuestionnaire(@NonNull String formId, String formRev, String formName, String label, String submitUrl, String creator, String owner, Map<String, Object> additionalProperties, boolean useLatest) {
    final ImmutableQuestionnaire.Builder questionnaire = ImmutableQuestionnaire.builder()
      .metadata(ImmutableQuestionnaireMetadata.builder()
        .formId(formId)
        .formName(formName)
        .formRev(useLatest ? LATEST_REV : formRev)
        .label(label)
        .creator(creator)
        .owner(StringUtils.defaultString(owner, creator))
        .status(status != null ? status : Questionnaire.Metadata.Status.NEW)
        .created(new Date())
        .language(getLanguage())
        .submitUrl(this.submitUrl != null ? this.submitUrl : submitUrl)
        .additionalProperties(additionalProperties != null ? additionalProperties : Collections.emptyMap())
        .build());

    if (contextValues != null) {
      questionnaire.context(contextValues);
    }
    if (answers != null) {
      questionnaire.answers(answers);
    }
    if (valueSets != null) {
      questionnaire.valueSets(valueSets);
    }
    questionnaire.activeItem(this.activeItem);
    return questionnaire.build();
  }

  @NonNull
  protected abstract QuestionnaireSession createQuestionnaireSession(boolean newSession, @NonNull Form formDocument);

  @NonNull
  public QuestionnaireSession build() {
    Objects.requireNonNull(formId, "QuestionnaireSessionBuilder.formId is required");

    boolean useLatest = LATEST_REV.equals(formRev);
    boolean newSession = getQuestionnaire() == null;
    String formRev = null;

    if (!useLatest) {
      formRev = this.formRev;
    }
    Form formDocument;
    try {
      formDocument = formFinder.findForm(formId, formRev);
    } catch (io.dialob.db.spi.exceptions.DatabaseException e) {
      LOGGER.debug("Could not load form {}: {}", formId, e.getMessage());
      throw new FormDataMissingException(formId, formRev);
    }
    if (newSession) {
      Form.Metadata metadata = formDocument.getMetadata();
      final String formId = formDocument.getId();
      String formName = null;
      if (!formId.equals(this.formId)) {
        formName = this.formId;
      }
      this.questionnaire = createNewQuestionnaire(formId, formDocument.getRev(), formName, metadata.getLabel(), metadata.getDefaultSubmitUrl(), creator, owner, additionalProperties, useLatest);
    }
    return createQuestionnaireSession(newSession, formDocument);
  }


  protected boolean isCreateOnly() {
    return createOnly;
  }

  protected Questionnaire getQuestionnaire() {
    return questionnaire;
  }

  protected String getLanguage() {
    return language;
  }

}
