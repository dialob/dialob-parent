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

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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

  private Map<String,Object> additionalProperties;

  protected BaseQuestionnaireSessionBuilder(@NonNull FormFinder formFinder) {
    this.formFinder = formFinder;
  }

  @Override
  @NonNull
  public QuestionnaireSessionBuilder setActiveItem(String activeItem) {
    this.activeItem = activeItem;
    return this;
  }

  @Override
  @NonNull
  public QuestionnaireSessionBuilder setFormId(String formId) {
    this.formId = formId;
    return this;
  }

  public QuestionnaireSessionBuilder setCreator(String creator) {
    this.creator = creator;
    return this;
  }

  public QuestionnaireSessionBuilder setOwner(String owner) {
    this.owner = owner;
    return this;
  }

  @Override
  @NonNull
  public QuestionnaireSessionBuilder setFormRev(String formRev) {
    this.formRev = formRev;
    return this;
  }

  @Override
  @NonNull
  public QuestionnaireSessionBuilder setSubmitUrl(String submitUrl) {
    this.submitUrl = submitUrl;
    return this;
  }

  @Override
  @NonNull
  public QuestionnaireSessionBuilder setContextValues(List<ContextValue> contextValues) {
    this.contextValues = contextValues;
    return this;
  }

  @Override
  @NonNull
  public QuestionnaireSessionBuilder setCreateOnly(boolean createOnly) {
    this.createOnly = createOnly;
    return this;
  }

  @Override
  @NonNull
  public QuestionnaireSessionBuilder setLanguage(String language) {
    this.language = language;
    if (StringUtils.isBlank(language)) {
      this.language = "en";
    }
    return this;
  }

  @Override
  @NonNull
  public QuestionnaireSessionBuilder setStatus(Questionnaire.Metadata.Status status) {
    this.status = status;
    return this;
  }

  @Override
  @NonNull
  public QuestionnaireSessionBuilder setAnswers(List<Answer> answers) {
    this.answers = answers;
    return this;
  }

  @NonNull
  @Override
  public QuestionnaireSessionBuilder setValueSets(List<ValueSet> valueSets) {
    this.valueSets = valueSets;
    return this;
  }

  @NonNull
  @Override
  public QuestionnaireSessionBuilder setAdditionalProperties(Map<String,Object> additionalProperties) {
    this.additionalProperties = additionalProperties;
    return this;
  }

  @Override
  @NonNull
  public QuestionnaireSessionBuilder setQuestionnaire(Questionnaire questionnaire) {
    this.questionnaire = questionnaire;
    if (this.questionnaire != null) {
      final Questionnaire.Metadata metadata = this.questionnaire.getMetadata();
      if (activeItem == null) {
        setActiveItem(this.questionnaire.getActiveItem());
      }
      if (formId == null) {
        setFormId(metadata.getFormId());
      }
      if (formRev == null) {
        setFormRev(metadata.getFormRev());
      }
      if (owner == null) {
        setOwner(metadata.getOwner());
      }
      if (creator == null) {
        setCreator(metadata.getCreator());
      }
      if (additionalProperties == null) {
        setAdditionalProperties(metadata.getAdditionalProperties());
      }
      setLanguage(metadata.getLanguage());
    }
    return this;
  }

  @NonNull
  protected Questionnaire createNewQuestionnaire(@NonNull String formId, String formRev, String formName, String label, String submitUrl, String creator, String owner, Map<String, Object> additionalProperties, boolean useLatest) {
    final ImmutableQuestionnaire.Builder questionnaire = ImmutableQuestionnaire.builder();
    final ImmutableQuestionnaireMetadata.Builder metadata = ImmutableQuestionnaireMetadata.builder();

    if (additionalProperties != null) {
      metadata.putAllAdditionalProperties(additionalProperties);
    }
    metadata.formId(formId);
    metadata.formName(formName);
    metadata.label(label);
    metadata.creator(creator);
    metadata.owner(StringUtils.defaultString(owner, creator));
    metadata.status(getStatus() != null ? getStatus() : Questionnaire.Metadata.Status.NEW );
    if (useLatest) {
      metadata.formRev(LATEST_REV);
    } else {
      metadata.formRev(formRev);
    }
    metadata.created(new Date());
    if (getSubmitUrl() != null) {
      metadata.submitUrl(getSubmitUrl());
    } else {
      metadata.submitUrl(submitUrl);
    }
    metadata.language(getLanguage());

    questionnaire.metadata(metadata.build());

    if (this.getContextValues() != null) {
      questionnaire.context(this.getContextValues());
    }
    if (this.getAnswers() != null) {
      questionnaire.answers(getAnswers());
    }
    if (this.getValueSets() != null) {
      questionnaire.valueSets(getValueSets());
    }
    questionnaire.activeItem(this.activeItem);
    return questionnaire.build();
  }

  @NonNull
  protected abstract QuestionnaireSession createQuestionnaireSession(boolean newSession, @NonNull Form formDocument);

  @NonNull
  public QuestionnaireSession build() {
    Objects.requireNonNull(getFormId(), "QuestionnaireSessionBuilder.formId is required");

    boolean useLatest = LATEST_REV.equals(this.getFormRev());
    boolean newSession = getQuestionnaire() == null;
    String formRev = null;

    if (!useLatest) {
      formRev = this.getFormRev();
    }
    Form formDocument;
    try {
      formDocument = formFinder.findForm(this.getFormId(), formRev);
    } catch (io.dialob.db.spi.exceptions.DatabaseException e) {
      LOGGER.debug("Could not load form {}: {}", this.getFormId(), e.getMessage());
      throw new FormDataMissingException(this.getFormId(), formRev);
    }
    if (newSession) {
      Form.Metadata metadata = formDocument.getMetadata();
      final String formId = formDocument.getId();
      String formName = null;
      if (!formId.equals(this.getFormId())) {
        formName = this.getFormId();
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

  protected String getFormRev() {
    return formRev;
  }

  protected String getActiveItem() {
    return activeItem;
  }

  protected String getFormId() {
    return formId;
  }

  protected String getSubmitUrl() {
    return submitUrl;
  }

  protected List<ContextValue> getContextValues() {
    return contextValues;
  }

  protected String getLanguage() {
    return language;
  }

  protected List<Answer> getAnswers() {
    return answers;
  }

  protected List<ValueSet> getValueSets() {
    return valueSets;
  }

  protected Questionnaire.Metadata.Status getStatus() {
    return status;
  }
}
