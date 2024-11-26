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
package io.dialob.questionnaire.service.rest;

import edu.umd.cs.findbugs.annotations.NonNull;
import io.dialob.api.form.Form;
import io.dialob.api.proto.Action;
import io.dialob.api.proto.ActionItem;
import io.dialob.api.proto.ActionsFactory;
import io.dialob.api.proto.ValueSet;
import io.dialob.api.questionnaire.Error;
import io.dialob.api.questionnaire.*;
import io.dialob.api.rest.*;
import io.dialob.common.Constants;
import io.dialob.form.service.api.FormDatabase;
import io.dialob.questionnaire.csvserializer.CSVSerializer;
import io.dialob.questionnaire.service.api.QuestionnaireDatabase;
import io.dialob.questionnaire.service.api.session.QuestionnaireSession;
import io.dialob.questionnaire.service.api.session.QuestionnaireSessionBuilderFactory;
import io.dialob.questionnaire.service.api.session.QuestionnaireSessionSaveService;
import io.dialob.questionnaire.service.api.session.QuestionnaireSessionService;
import io.dialob.rest.type.ApiException;
import io.dialob.security.tenant.CurrentTenant;
import io.dialob.security.user.CurrentUserProvider;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.ZoneId;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static io.dialob.api.proto.ActionsFactory.*;
import static java.util.Collections.singletonList;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.springframework.http.ResponseEntity.notFound;

@RestController
@Slf4j
public class QuestionnairesRestServiceController implements QuestionnairesRestService {

  private final QuestionnaireSessionService questionnaireSessionService;

  private final QuestionnaireSessionSaveService questionnaireSessionSaveService;

  private final QuestionnaireSessionBuilderFactory questionnaireSessionBuilderFactory;

  private final QuestionnaireDatabase questionnaireRepository;

  private final FormDatabase formDatabase;

  private final CurrentTenant currentTenant;

  private final CurrentUserProvider currentUserProvider;

  private final CSVSerializer csvSerializer;

  public QuestionnairesRestServiceController(@NonNull QuestionnaireSessionService questionnaireSessionService,
                                             QuestionnaireSessionSaveService questionnaireSessionSaveService,
                                             @NonNull QuestionnaireSessionBuilderFactory questionnaireSessionBuilderFactory,
                                             @NonNull QuestionnaireDatabase questionnaireRepository,
                                             @NonNull FormDatabase formDatabase,
                                             @NonNull CurrentTenant currentTenant,
                                             @NonNull CurrentUserProvider currentUserProvider,
                                             @NonNull CSVSerializer csvSerializer) {
    this.questionnaireSessionService = questionnaireSessionService;
    this.questionnaireSessionSaveService = questionnaireSessionSaveService;
    this.questionnaireSessionBuilderFactory = questionnaireSessionBuilderFactory;
    this.questionnaireRepository = questionnaireRepository;
    this.formDatabase = formDatabase;
    this.currentTenant = currentTenant;
    this.currentUserProvider = currentUserProvider;
    this.csvSerializer = csvSerializer;
  }

  /**
   *
   */
  @Override
  public ResponseEntity<IdAndRevision> postQuestionnaire(@NonNull Questionnaire questionnaire) {
    final Questionnaire.Metadata metadata = questionnaire.getMetadata();
    final String formId = metadata.getFormId();
    final String formRev = metadata.getFormRev();
    final String owner = metadata.getOwner();
    final String submitUrl = metadata.getSubmitUrl();
    final String language = metadata.getLanguage();
    final Questionnaire.Metadata.Status status = metadata.getStatus();
    LOGGER.debug("POST /questionnaire {formId: '{}',formRev:'{}'}", formId, formRev);
    if (!formDatabase.exists(this.currentTenant.getId(), formId)) {
      throw new ApiException(ImmutableErrors.builder().addErrors(ImmutableErrors.Error.builder()
        .code("NotExists")
        .context("metadata.formId")
        .error("Form do not exist")
        .rejectedValue(formId).build())
        .status(HttpStatus.UNPROCESSABLE_ENTITY.value()).build());
    }
    final QuestionnaireSession session = questionnaireSessionBuilderFactory.createQuestionnaireSessionBuilder()
      .createOnly(true)
      .formId(formId)
      .formRev(formRev)
      .creator(currentUserProvider.getUserId())
      .owner(owner)
      .submitUrl(submitUrl)
      .contextValues(questionnaire.getContext())
      .answers(questionnaire.getAnswers())
      .language(language)
      .status(status)
      .activeItem(questionnaire.getActiveItem())
      .valueSets(questionnaire.getValueSets())
      .additionalProperties(metadata.getAdditionalProperties())
      .build();
    final Questionnaire sessionQuestionnaire = session.getQuestionnaire();
    String sessionId = sessionQuestionnaire.getId();
    String rev = session.getQuestionnaire().getRev();
    if (sessionId == null) {
      throw new ApiException(ImmutableErrors.builder().addErrors(ImmutableErrors.Error.builder()
        .code("noSessionId")
        .context("_id")
        .error("No session id").build())
        .status(HttpStatus.INTERNAL_SERVER_ERROR.value()).build());
    }
    LOGGER.debug("questionnaire '{}' created", sessionId);
    return ResponseEntity.status(HttpStatus.CREATED).body(ImmutableIdAndRevision.builder().id(sessionId).rev(rev).build());
  }

  /**
   * @param status
   */
  @Override
  public ResponseEntity<List<QuestionnaireListItem>> getQuestionnaires(String owner,
                                                                       String formId,
                                                                       String formName,
                                                                       String formTag,
                                                                       Questionnaire.Metadata.Status status) {
    List<QuestionnaireListItem> result = new ArrayList<>();
    questionnaireRepository.findAllMetadata(
      currentTenant.getId(),
      owner,
      formId,
      formName,
      formTag,
      status,
      row -> result.add(ImmutableQuestionnaireListItem.builder().id(row.getId()).metadata(row.getValue()).build()));
    return ResponseEntity.ok(result);
  }

  /**
   *
   * @param questionnaireId
   * @return
   */
  @Override
  public ResponseEntity<Questionnaire> getQuestionnaire(String questionnaireId) {
    LOGGER.debug("GET /questionnaire/{}", questionnaireId);
    return ResponseEntity.ok(questionnaireRepository.findOne(currentTenant.getId(), questionnaireId));
  }

  /**
   *
   * @param questionnaireId
   * @return
   */
  @Override
  public ResponseEntity<Response> deleteQuestionnaire(String questionnaireId) {
    LOGGER.debug("DELETE /questionnaire/{}", questionnaireId);
    questionnaireRepository.delete(currentTenant.getId(), questionnaireId);
    return ResponseEntity.ok(ImmutableResponse.builder().ok(true).build());
  }

  /**
   *
   * @param questionnaireId
   * @param questionnaire
   * @return
   */
  @Override
  public ResponseEntity<Questionnaire> putQuestionnaire(
    final String questionnaireId,
    final Questionnaire questionnaire)
  {
    return inSession(questionnaireId, questionnaireSession -> {
      final List<Action> actions = new ArrayList<>();
      final String activeItem = questionnaireSession.getActiveItem().orElse(null);

      if (questionnaire.getActiveItem() != null && !questionnaire.getActiveItem().equals(activeItem)) {
        actions.add(gotoPage(questionnaire.getActiveItem()));
      }
      actions.add(removeAnswers());
      questionnaire.getAnswers().forEach(answer -> actions.add(answer(answer.getId(), answer.getValue())));
      questionnaire.getContext().forEach(answer -> actions.add(setValue(answer.getId(), answer.getValue())));
      if (questionnaire.getMetadata().getStatus() == Questionnaire.Metadata.Status.COMPLETED) {
        actions.add(complete(questionnaire.getId()));
      }
      final Questionnaire updatedQuestionnaire = updateQuestionnaire(questionnaireSession, actions).getQuestionnaire();
      return ResponseEntity.ok(updatedQuestionnaire);
    });
  }

  /**
   *
   * @param questionnaireId
   * @return
   */
  @Override
  public ResponseEntity<Questionnaire.Metadata.Status> getQuestionnaireStatus(final String questionnaireId){
    return inSession(questionnaireId, questionnaireSession -> ResponseEntity.ok(questionnaireSession.getStatus()));
  }

  /**
   *
   * @param questionnaireId
   * @param status
   * @return
   */
  @Override
  public ResponseEntity<Questionnaire.Metadata.Status> putQuestionnaireStatus(final String questionnaireId, Questionnaire.Metadata.Status status){
    return inSession(questionnaireId, questionnaireSession -> {
      if (status == Questionnaire.Metadata.Status.COMPLETED) {
        updateQuestionnaire(questionnaireSession, Collections.singletonList(ActionsFactory.complete(questionnaireId)));
      }
      return ResponseEntity.ok(questionnaireSession.getStatus());
    });
  }

  /**
   *
   * @param questionnaireId
   * @return
   */
  @Override
  public ResponseEntity<List<Answer>> getQuestionnaireAnswers(String questionnaireId) {
    LOGGER.debug("GET /questionnaires/{}/answers", questionnaireId);
    return inSession(questionnaireId, questionnaireSession -> ResponseEntity.ok(questionnaireSession.getAnswers()));
  }

  /**
   *
   * @param questionnaireId
   * @return
   */
  @Override
  public ResponseEntity<List<Error>> getQuestionnaireErrors(String questionnaireId) {
    LOGGER.debug("GET /questionnaires/{}/errors", questionnaireId);
    return inSession(questionnaireId, questionnaireSession -> ResponseEntity.ok(questionnaireSession.getErrors()));
  }

  @Override
  public ResponseEntity<List<Error>> putQuestionnaireAnswers(
    String questionnaireId,
    List<Answer> answers)
  {
    return inSession(questionnaireId, questionnaireSession -> {
      updateQuestionnaire(questionnaireSession, answers.stream().map(answer ->
        ActionsFactory.answer(answer.getId(), answer.getValue())).collect(Collectors.toList()));
      return ResponseEntity.ok(questionnaireSession.getErrors());
    });
  }

  @Override
  public ResponseEntity<List<Error>> putQuestionnaireAnswer(
    String questionnaireId,
    String answerId,
    Object answer) // multivalued answer?
  {
    if (!isValidAnswerValue(answer)) {
      return ResponseEntity.badRequest().body(singletonList(ImmutableError.builder().id(answerId).code("invalid_answer").description("Cannot handle answer data").build()));
    }
    if (answer instanceof List) {
      // convert to List<String>
      answer = ((List) answer).stream()
        .filter(Objects::nonNull)
        .map(Object::toString)
        .collect(Collectors.toList());
    }
    return putQuestionnaireAnswers(questionnaireId, singletonList(QuestionnaireFactory.answer(answerId, answer)));
  }

  boolean isValidAnswerValue(Object answer) {
    if (answer == null ||
      answer instanceof BigDecimal ||
      answer instanceof BigInteger ||
      answer instanceof Integer ||
      answer instanceof Long ||
      answer instanceof Double ||
      answer instanceof Boolean ||
      answer instanceof String) {
      return true;
    }
    if (answer instanceof List) {
      List<Object> list = (List<Object>) answer;
      return list.stream().map(i -> i == null || i instanceof String).reduce(Boolean.TRUE, (a, i) -> a && i);
    }
    return false;
  }

  /**
   *
   * @param questionnaireId
   * @param answerId
   * @return
   */
  @Override
  public ResponseEntity<List<Error>> deleteQuestionnaireAnswer(
    String questionnaireId,
    String answerId)
  {
    return putQuestionnaireAnswers(questionnaireId, singletonList(QuestionnaireFactory.answer(answerId, null)));
  }

  /**
   *
   * @param questionnaireId
   * @return
   */
  @Override
  public ResponseEntity<Items> getQuestionnairePages(String questionnaireId) {
    return inSession(questionnaireId, questionnaireSession -> questionnaireSession
      .getItemById(Constants.QUESTIONNAIRE)
      .map(question -> {
        ImmutableItems.Builder builder = ImmutableItems.builder();
        if (question.getItems() != null) {
          builder = builder.items(question.getItems());
        }
        if (question.getAvailableItems() != null) {
          builder = builder.availableItems(question.getAvailableItems());
        }
        if (question.getActiveItem() != null) {
          builder = builder.activeItem(question.getActiveItem());
        }
        return (Items) builder.build();
      })
      .map(ResponseEntity::ok).orElse(notFound().build()));
  }

  @Override
  public ResponseEntity<Items> putQuestionnairePages(@NonNull String questionnaireId, Items pages) {
    return inSession(questionnaireId, questionnaireSession -> {
      final String activeItem = pages.getActiveItem();
      if (questionnaireSession.getActiveItem().filter(sessionActiveItem -> sessionActiveItem.equals(activeItem)).isEmpty()) {
        updateQuestionnaire(questionnaireSession, Collections.singletonList(gotoPage(activeItem)));
        return questionnaireSession
          .getItemById(Constants.QUESTIONNAIRE)
          .map(question -> {
            ImmutableItems.Builder builder = ImmutableItems.builder();
            if (question.getItems() != null) {
              builder = builder.items(question.getItems());
            }
            if (question.getAvailableItems() != null) {
              builder = builder.availableItems(question.getAvailableItems());
            }
            if (question.getActiveItem() != null) {
              builder = builder.activeItem(question.getActiveItem());
            }
            return (Items) builder.build();
          })
          .map(ResponseEntity::ok).orElse(notFound().build());
      }
      return notFound().build();
    });
  }

  @Override
  public ResponseEntity<List<ActionItem>> getQuestionnaireItems(String questionnaireId) {
    return inSession(questionnaireId, questionnaireSession -> ResponseEntity.ok(questionnaireSession.getItems()));
  }

  @Override
  public ResponseEntity<ActionItem> getQuestionnaireItem(String questionnaireId, String itemId) {
    return inSession(questionnaireId, questionnaireSession -> questionnaireSession.getItemById(itemId).map(ResponseEntity::ok).orElse(notFound().build()));
  }

  @Override
  public ResponseEntity<List<String>> getQuestionnaireItemRows(String questionnaireId, String itemId) {
    return inSession(questionnaireId, questionnaireSession ->
      questionnaireSession
        .getItemById(itemId)
        .map(this::selectRowgroup)
        .map(ActionItem::getItems)
        .map(ResponseEntity::ok)
        .orElse(notFound().build()));
  }

  @Nullable
  private ActionItem selectRowgroup(ActionItem question) {
    if ( "rowgroup".equals(question.getType()) ) {
      return question;
    }
    return null;
  }

  @Override
  public ResponseEntity<List<String>> postQuestionnaireItemRow(String questionnaireId, String itemId) {
    return inSession(questionnaireId, questionnaireSession ->
      updateQuestionnaire(questionnaireSession, Collections.singletonList(ActionsFactory.addRow(itemId)))
        .getItemById(itemId)
        .map(this::selectRowgroup)
        .map(ActionItem::getItems)
        .map(ResponseEntity::ok)
        .orElseGet(() -> notFound().build()));
  }


  @Override
  public ResponseEntity<List<String>> deleteQuestionnaireItemRow(String questionnaireId, String itemId, String rowId) {
    return inSession(questionnaireId, questionnaireSession -> questionnaireSession.getItemById(itemId)
      .map(this::selectRowgroup)
      .map(ActionItem::getItems)
      .map(items -> items.contains(rowId) ? items : null)
      .flatMap(items -> updateQuestionnaire(questionnaireSession, Collections.singletonList(ActionsFactory.deleteRow(rowId)))
        .getItemById(itemId)
        .map(this::selectRowgroup)
        .map(ActionItem::getItems))
      .map(ResponseEntity::ok).orElse(notFound().build()));
  }

  @Override
  public ResponseEntity<List<ValueSet>> getQuestionnaireValueSets(String questionnaireId) {
    return inSession(questionnaireId, questionnaireSession -> ResponseEntity.ok(questionnaireSession.getValueSets()));
  }

  @Override
  public ResponseEntity<ValueSet> getQuestionnaireValueSet(String questionnaireId, String valueSetId) {
    return inSession(questionnaireId, questionnaireSession -> questionnaireSession.getValueSets().stream().filter(valueSet1 -> valueSet1.getId().equals(valueSetId)).findFirst().map(ResponseEntity::ok).orElse(notFound().build()));
  }

  protected QuestionnaireSession updateQuestionnaire(QuestionnaireSession questionnaireSession, Collection<Action> actions) {
    if (questionnaireSession.getStatus() != Questionnaire.Metadata.Status.COMPLETED) {
      questionnaireSession.dispatchActions(actions);
      questionnaireSession = questionnaireSessionSaveService.save(questionnaireSession);
    }
    return questionnaireSession;
  }

  protected <R> R inSession(String questionnaireId, Function<QuestionnaireSession,R> operation) {
    final QuestionnaireSession questionnaireSession = questionnaireSessionService.findOne(questionnaireId);
    return operation.apply(questionnaireSession);
  }

  @Override
  public ResponseEntity<String> getCsv(@Valid GetCsv getCsv) {

    final List<QuestionnaireDatabase.MetadataRow> questionnaireMetadataList = new ArrayList<>();
    Form form;


    List<String> questionnaires = getCsv.questionnaire();
    if (!questionnaires.isEmpty()) {
      questionnaires.stream().map(qId -> questionnaireRepository.findMetadata(currentTenant.getId(), qId))
        .filter(metadataRow -> metadataRow.getValue().getStatus().equals(Questionnaire.Metadata.Status.COMPLETED))
        .forEach(questionnaireMetadataList::add);

      // Validate questionnaires list for same form
      String seenFormId = null;
      for (QuestionnaireDatabase.MetadataRow metadataRow : questionnaireMetadataList) {
        Questionnaire.Metadata metadata = metadataRow.getValue();
        if (seenFormId == null) {
          seenFormId = metadata.getFormId();
        }
        if (!seenFormId.equals(metadata.getFormId())) {
          throw new ApiException(ImmutableErrors.builder().addErrors(ImmutableErrors.Error.builder()
            .code("NotSameForm")
            .context("metadata.formId")
            .error("Requested sessions don't have same form ID")
            .rejectedValue(metadata.getFormId()).build())
            .status(HttpStatus.BAD_REQUEST.value()).build());
        }
      }
      if (seenFormId != null) {
        form = formDatabase.findOne(currentTenant.getId(), seenFormId);
      } else {
        throw new ApiException(ImmutableErrors.builder().addErrors(ImmutableErrors.Error.builder()
          .code("not_found")
          .context("metadata.formId")
          .error("Could not find form").build())
          .status(HttpStatus.NOT_FOUND.value()).build());
      }
    } else {
      // Mode 2 - Query sessions by form
      if (StringUtils.isNotBlank(getCsv.formId())) {
        form = formDatabase.findOne(currentTenant.getId(), getCsv.formId());
      } else {
        if (isBlank(getCsv.formName()) && isBlank(getCsv.formTag())) {
          throw new ApiException(ImmutableErrors.builder().addErrors(ImmutableErrors.Error.builder()
            .code("formNameNotSet")
            .context("")
            .error("Form name and tag or form ID is not set").build())
            .status(HttpStatus.BAD_REQUEST.value()).build());
        }
        form = formDatabase.findOne(currentTenant.getId(), getCsv.formName(), getCsv.formTag());
      }

      questionnaireRepository.findAllMetadata(currentTenant.getId(),
        null,
        getCsv.formId(),
        getCsv.formName(),
        getCsv.formTag(),
        Questionnaire.Metadata.Status.COMPLETED,
        metadata -> {
          // Filter by start and end date using last answer timestamp. TODO: use completion date when it gets available
          if (getCsv.from() != null && getCsv.from().isAfter(metadata.getValue().getLastAnswer().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime())) {
            return;
          }
          if (getCsv.to() != null && getCsv.to().isBefore(metadata.getValue().getLastAnswer().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime())) {
            return;
          }
          questionnaireMetadataList.add(metadata);
        }
      );

      if (questionnaireMetadataList.isEmpty()) {
        throw new ApiException(ImmutableErrors.builder().addErrors(ImmutableErrors.Error.builder()
          .code("noSessionsFound")
          .context("")
          .error("No sessions found with given criteria").build())
          .status(HttpStatus.NOT_FOUND.value()).build());
      }

    }

    try {
      String csv = csvSerializer.serializeQuestionnaires(questionnaireMetadataList.stream().map(QuestionnaireDatabase.MetadataRow::getId).toArray(String[]::new), form, getCsv.language());
      return ResponseEntity.ok().contentType(MediaType.valueOf("text/csv")).body(csv);
    } catch (IOException e) {
      LOGGER.error("CSV Export failed", e);
      throw new ApiException(ImmutableErrors.builder().addErrors(ImmutableErrors.Error.builder()
        .code("CSVExportFailed")
        .context("exception")
        .error(e.getMessage()).build())
        .status(HttpStatus.BAD_REQUEST.value()).build());
    }
  }
}
