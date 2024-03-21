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
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static io.dialob.api.proto.ActionsFactory.*;
import static java.util.Collections.singletonList;
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
   * @param questionnaire
   * @return
   */
  @Override
  @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<IdAndRevision> postQuestionnaire(@NonNull @RequestBody @Validated Questionnaire questionnaire) {
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
      .setCreateOnly(true)
      .setFormId(formId)
      .setFormRev(formRev)
      .setCreator(currentUserProvider.getUserId())
      .setOwner(owner)
      .setSubmitUrl(submitUrl)
      .setContextValues(questionnaire.getContext())
      .setAnswers(questionnaire.getAnswers())
      .setLanguage(language)
      .setStatus(status)
      .setActiveItem(questionnaire.getActiveItem())
      .setValueSets(questionnaire.getValueSets())
      .setAdditionalProperties(metadata.getAdditionalProperties())
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
   * @return
   * @param owner
   * @param formId
   * @param formTag
   * @param status
   */
  @Override
  @GetMapping(produces = {MediaType.APPLICATION_JSON_VALUE})
  public ResponseEntity<List<QuestionnaireListItem>> getQuestionnaires(@RequestParam(name = "owner", required = false) String owner,
                                                                       @RequestParam(name = "formId", required = false) String formId,
                                                                       @RequestParam(name = "formName", required = false) String formName,
                                                                       @RequestParam(name = "formTag", required = false) String formTag,
                                                                       @RequestParam(name = "status", required = false) Questionnaire.Metadata.Status status) {
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
  @GetMapping(path = "{questionnaireId}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Questionnaire> getQuestionnaire(@PathVariable("questionnaireId") String questionnaireId) {
    LOGGER.debug("GET /questionnaire/{}", questionnaireId);
    return ResponseEntity.ok(questionnaireRepository.findOne(currentTenant.getId(), questionnaireId));
  }

  /**
   *
   * @param questionnaireId
   * @return
   */
  @Override
  @DeleteMapping(path = "{questionnaireId}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Response> deleteQuestionnaire(@PathVariable("questionnaireId") String questionnaireId) {
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
  @PutMapping(path = "{questionnaireId}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Questionnaire> putQuestionnaire(
    @PathVariable("questionnaireId") final String questionnaireId,
    @RequestBody @Validated final Questionnaire questionnaire)
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
  @GetMapping(path = "{questionnaireId}/status", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Questionnaire.Metadata.Status> getQuestionnaireStatus(@PathVariable("questionnaireId") final String questionnaireId){
    return inSession(questionnaireId, questionnaireSession -> ResponseEntity.ok(questionnaireSession.getStatus()));
  }

  /**
   *
   * @param questionnaireId
   * @param status
   * @return
   */
  @Override
  @PutMapping(path = "{questionnaireId}/status", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Questionnaire.Metadata.Status> putQuestionnaireStatus(@PathVariable("questionnaireId") final String questionnaireId, @RequestBody Questionnaire.Metadata.Status status){
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
  @GetMapping(path = "{questionnaireId}/answers", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<List<Answer>> getQuestionnaireAnswers(@PathVariable("questionnaireId") String questionnaireId) {
    LOGGER.debug("GET /questionnaires/{}/answers", questionnaireId);
    return inSession(questionnaireId, questionnaireSession -> ResponseEntity.ok(questionnaireSession.getAnswers()));
  }

  /**
   *
   * @param questionnaireId
   * @return
   */
  @Override
  @GetMapping(path = "{questionnaireId}/errors", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<List<Error>> getQuestionnaireErrors(@PathVariable("questionnaireId") String questionnaireId) {
    LOGGER.debug("GET /questionnaires/{}/errors", questionnaireId);
    return inSession(questionnaireId, questionnaireSession -> ResponseEntity.ok(questionnaireSession.getErrors()));
  }

  /**
   *
   * @param questionnaireId
   * @param answers
   * @return
   */
  @Override
  @PostMapping(path = "{questionnaireId}/answers", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<List<Error>> putQuestionnaireAnswers(
    @PathVariable("questionnaireId") String questionnaireId,
    @RequestBody List<Answer> answers)
  {
    return inSession(questionnaireId, questionnaireSession -> {
      updateQuestionnaire(questionnaireSession, answers.stream().map(answer ->
        ActionsFactory.answer(answer.getId(), answer.getValue())).collect(Collectors.toList()));
      return ResponseEntity.ok(questionnaireSession.getErrors());
    });
  }

  @Override
  @PutMapping(path = "{questionnaireId}/answers/{answerId}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = {MediaType.APPLICATION_JSON_VALUE})
  public ResponseEntity<List<Error>> putQuestionnaireAnswer(
    @PathVariable("questionnaireId") String questionnaireId,
    @PathVariable("answerId") String answerId,
    @RequestBody Object answer) // multivalued answer?
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
  @DeleteMapping(path = "{questionnaireId}/answers/{answerId}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<List<Error>> deleteQuestionnaireAnswer(
    @PathVariable("questionnaireId") String questionnaireId,
    @PathVariable("answerId") String answerId)
  {
    return putQuestionnaireAnswers(questionnaireId, singletonList(QuestionnaireFactory.answer(answerId, null)));
  }

  /**
   *
   * @param questionnaireId
   * @return
   */
  @Override
  @GetMapping(path = "{questionnaireId}/pages", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Items> getQuestionnairePages(@PathVariable("questionnaireId") String questionnaireId) {
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
  @PutMapping(path = "{questionnaireId}/pages", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Items> putQuestionnairePages(@NonNull @PathVariable("questionnaireId") String questionnaireId, @RequestBody Items pages) {
    return inSession(questionnaireId, questionnaireSession -> {
      final String activeItem = pages.getActiveItem();
      if (!questionnaireSession.getActiveItem().filter(sessionActiveItem -> sessionActiveItem.equals(activeItem)).isPresent()) {
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
  @GetMapping(path = "{questionnaireId}/items", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<List<ActionItem>> getQuestionnaireItems(@PathVariable("questionnaireId") String questionnaireId) {
    return inSession(questionnaireId, questionnaireSession -> ResponseEntity.ok(questionnaireSession.getItems()));
  }

  @Override
  @GetMapping(path = "{questionnaireId}/items/{itemId}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ActionItem> getQuestionnaireItem(@PathVariable("questionnaireId") String questionnaireId, @PathVariable("itemId") String itemId) {
    return inSession(questionnaireId, questionnaireSession -> questionnaireSession.getItemById(itemId).map(ResponseEntity::ok).orElse(notFound().build()));
  }

  @Override
  @GetMapping(path = "{questionnaireId}/items/{itemId}/rows", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<List<String>> getQuestionnaireItemRows(@PathVariable("questionnaireId") String questionnaireId, @PathVariable("itemId") String itemId) {
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
    if ( question.getType().equals("rowgroup") ) {
      return question;
    }
    return null;
  }

  @Override
  @PostMapping(path = "{questionnaireId}/items/{itemId}/rows", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<List<String>> postQuestionnaireItemRow(@PathVariable("questionnaireId") String questionnaireId, @PathVariable("itemId") String itemId) {
    return inSession(questionnaireId, questionnaireSession ->
      updateQuestionnaire(questionnaireSession, Collections.singletonList(ActionsFactory.addRow(itemId)))
        .getItemById(itemId)
        .map(this::selectRowgroup)
        .map(ActionItem::getItems)
        .map(ResponseEntity::ok)
        .orElseGet(() -> notFound().build()));
  }


  @Override
  @DeleteMapping(path = "{questionnaireId}/items/{itemId}/rows/{rowId}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<List<String>> deleteQuestionnaireItemRow(@PathVariable("questionnaireId") String questionnaireId, @PathVariable("itemId") String itemId, @PathVariable("rowId") String rowId) {
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
  @GetMapping(path = "{questionnaireId}/valueSets", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<List<ValueSet>> getQuestionnaireValueSets(@PathVariable("questionnaireId") String questionnaireId) {
    return inSession(questionnaireId, questionnaireSession -> ResponseEntity.ok(questionnaireSession.getValueSets()));
  }

  @Override
  @GetMapping(path = "{questionnaireId}/valueSets/{valueSetId}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ValueSet> getQuestionnaireValueSet(@PathVariable("questionnaireId") String questionnaireId, @PathVariable("valueSetId") String valueSetId) {
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
  public ResponseEntity<String> getCsv(Optional<String> formId, Optional<String> formName, Optional<String> formTag, Optional<List<String>> questionnaires, Optional<String> language, Optional<LocalDateTime> startDate, Optional<LocalDateTime> endDate) {

    final List<QuestionnaireDatabase.MetadataRow> questionnaireMetadataList = new ArrayList<>();
    Form form;

    if (questionnaires.isPresent()) {
      questionnaires.ifPresent(q -> q.stream().map(qId -> questionnaireRepository.findMetadata(currentTenant.getId(), qId))
        .filter(metadataRow -> metadataRow.getValue().getStatus().equals(Questionnaire.Metadata.Status.COMPLETED))
        .forEach(questionnaireMetadataList::add));

      // Validate questionnaire list for same form
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
      if (formId.isPresent()) {
        form = formDatabase.findOne(currentTenant.getId(), formId.get());
      } else {
        if (!formName.isPresent() && !formTag.isPresent()) {
          throw new ApiException(ImmutableErrors.builder().addErrors(ImmutableErrors.Error.builder()
            .code("formNameNotSet")
            .context("")
            .error("Form name and tag or form ID is not set").build())
            .status(HttpStatus.BAD_REQUEST.value()).build());
        }
        form = formDatabase.findOne(currentTenant.getId(), formName.get(), formTag.get());
      }

      questionnaireRepository.findAllMetadata(currentTenant.getId(),
        null,
        formId.orElse(null),
        formName.orElse(null),
        formTag.orElse(null),
        Questionnaire.Metadata.Status.COMPLETED,
        metadata -> {
          // Filter by start and end date using last answer timestamp. TODO: use completion date when it gets available
          if (startDate.isPresent() && startDate.get().isAfter(metadata.getValue().getLastAnswer().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime())) {
            return;
          }
          if (endDate.isPresent() && endDate.get().isBefore(metadata.getValue().getLastAnswer().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime())) {
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
      String csv = csvSerializer.serializeQuestionnaires(questionnaireMetadataList.stream().map(md -> md.getId()).toArray(String[]::new), form, language.orElse("en"));
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
