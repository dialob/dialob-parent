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
import io.dialob.api.proto.ActionItem;
import io.dialob.api.proto.ValueSet;
import io.dialob.api.questionnaire.Answer;
import io.dialob.api.questionnaire.Error;
import io.dialob.api.questionnaire.Questionnaire;
import io.dialob.api.questionnaire.QuestionnaireListItem;
import io.dialob.api.rest.IdAndRevision;
import io.dialob.api.rest.Items;
import io.dialob.api.rest.Response;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RequestMapping("${dialob.api.context-path:}/questionnaires")
@OpenAPIDefinition(info = @Info(title = "DialobQuestionnaireService"), tags = {
  @Tag(name = "dialob-questionnaire-service")

})
public interface QuestionnairesRestService {

  @Operation(summary = OpenApiDoc.QUESTIONNAIRE.POST_QUEST_SUMMARY)
  @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
  ResponseEntity<IdAndRevision> postQuestionnaire(
    @Parameter(name = "questionnaire", required = true, description = "New questionnaire object")
    @NonNull @RequestBody @Validated Questionnaire questionnaire);

  @GetMapping(produces = {MediaType.APPLICATION_JSON_VALUE})
  ResponseEntity<List<QuestionnaireListItem>> getQuestionnaires(@RequestParam(name = "owner", required = false) String owner,
                                                                @RequestParam(name = "formId", required = false) String formId,
                                                                @RequestParam(name = "formName", required = false) String formName,
                                                                @RequestParam(name = "formTag", required = false) String formTag,
                                                                @RequestParam(name = "status", required = false) Questionnaire.Metadata.Status status);

  @GetMapping(produces = {"text/csv"})
  ResponseEntity<String> getCsv(
    @RequestParam(name = "formId") Optional<String> formId,
    @RequestParam(name = "formName") Optional<String> formName,
    @RequestParam(name = "formTag") Optional<String> formTag,
    @RequestParam(name = "questionnaire") Optional<List<String>> questionnaires,
    @RequestParam(name = "language") Optional<String> language,
    @RequestParam(name = "from") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Optional<LocalDateTime> startDate,
    @RequestParam(name = "to") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Optional<LocalDateTime> endDate
  );

  @Operation (summary = OpenApiDoc.QUESTIONNAIRE.GET_QUESTID_SUMMARY, description = OpenApiDoc.QUESTIONNAIRE.GET_QUESTID_OP)
  @GetMapping(path = "{questionnaireId}", produces = MediaType.APPLICATION_JSON_VALUE)
  ResponseEntity<Questionnaire> getQuestionnaire(
	@Parameter (description = OpenApiDoc.QUESTIONNAIRE.QUEST_ID)
	@PathVariable("questionnaireId")
	String questionnaireId);

  @Operation (summary = OpenApiDoc.QUESTIONNAIRE.DELETE_QUEST_SUMMARY, description = OpenApiDoc.QUESTIONNAIRE.DELETE_QUEST_OP)
  @DeleteMapping(path = "{questionnaireId}")
  ResponseEntity<Response> deleteQuestionnaire(
	@Parameter (description = OpenApiDoc.QUESTIONNAIRE.QUEST_ID)
	@PathVariable("questionnaireId") String questionnaireId);

  @Operation (summary = OpenApiDoc.QUESTIONNAIRE.PUT_QUEST_SUMMARY, description = OpenApiDoc.QUESTIONNAIRE.PUT_QUEST_OP)
  @PutMapping(path = "{questionnaireId}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
  ResponseEntity<Questionnaire> putQuestionnaire(
	@Parameter(description = OpenApiDoc.QUESTIONNAIRE.QUEST_ID)
    @PathVariable("questionnaireId")  String questionnaireId,
    @Parameter(name = "questionnaire", required = true, description = OpenApiDoc.QUESTIONNAIRE.QUEST_OBJ)
    @RequestBody @Validated Questionnaire questionnaire);

  @Operation(summary = OpenApiDoc.QUESTIONNAIRE.GET_STATUS_SUMMARY, description = OpenApiDoc.QUESTIONNAIRE.GET_STATUS_OP)
  @GetMapping(path = "{questionnaireId}/status", produces = MediaType.APPLICATION_JSON_VALUE)
  ResponseEntity<Questionnaire.Metadata.Status> getQuestionnaireStatus(@PathVariable("questionnaireId") @Parameter(description = OpenApiDoc.QUESTIONNAIRE.QUEST_ID) String questionnaireId);

  @Operation(summary = OpenApiDoc.QUESTIONNAIRE.PUT_STATUS_SUMMARY)
  @PutMapping(path = "{questionnaireId}/status", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
  ResponseEntity<Questionnaire.Metadata.Status> putQuestionnaireStatus(@PathVariable("questionnaireId") @Parameter(description = OpenApiDoc.QUESTIONNAIRE.QUEST_ID) String questionnaireId,
                                                                       @Parameter(name = "status", required = true, description = OpenApiDoc.QUESTIONNAIRE.PUT_NEW_STATUS)
                                                                       @RequestBody Questionnaire.Metadata.Status status);

  @Operation (summary = OpenApiDoc.QUESTIONNAIRE.GET_ANSWERS_SUMMARY, description = OpenApiDoc.QUESTIONNAIRE.GET_ANSWERS_OP)
  @GetMapping(path = "{questionnaireId}/answers", produces = MediaType.APPLICATION_JSON_VALUE)
  ResponseEntity<List<Answer>> getQuestionnaireAnswers(
	@Parameter (description = OpenApiDoc.QUESTIONNAIRE.QUEST_ID)
    @PathVariable("questionnaireId") String questionnaireId);

  @Operation (summary = OpenApiDoc.QUESTIONNAIRE.ERRORS_SUMMARY, description = OpenApiDoc.QUESTIONNAIRE.ERRORS_OP)
  @GetMapping(path = "{questionnaireId}/errors", produces = MediaType.APPLICATION_JSON_VALUE)
  ResponseEntity<List<Error>> getQuestionnaireErrors(
	@Parameter (description = OpenApiDoc.QUESTIONNAIRE.QUEST_ID)
	@PathVariable("questionnaireId") String questionnaireId);

  @Operation(summary = OpenApiDoc.QUESTIONNAIRE.POST_ANSWER_SUMMARY)
  @PostMapping(path = "{questionnaireId}/answers", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
  ResponseEntity<List<Error>> putQuestionnaireAnswers(
    @PathVariable("questionnaireId") @Parameter(description = OpenApiDoc.QUESTIONNAIRE.QUEST_ID) String questionnaireId,
    @Parameter(name = "answers", required = true, description = OpenApiDoc.QUESTIONNAIRE.NEW_ANSWERS_OBJ)
    @RequestBody List<Answer> answers);

  /**
   * Updates answer
   *
   * @param questionnaireId target questionnaire id
   * @param answerId answer id
   * @param answer new answer value
   * @return
   */
  @Operation(summary = OpenApiDoc.QUESTIONNAIRE.PUT_ANSWER_SUMMARY, description = OpenApiDoc.QUESTIONNAIRE.PUT_ANSWER_OP)
  @PutMapping(path = "{questionnaireId}/answers/{answerId}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = {MediaType.APPLICATION_JSON_VALUE})
  ResponseEntity<List<Error>> putQuestionnaireAnswer(
    @PathVariable("questionnaireId") @Parameter(description = OpenApiDoc.QUESTIONNAIRE.QUEST_ID) String questionnaireId,
    @PathVariable("answerId") @Parameter (description = OpenApiDoc.QUESTIONNAIRE.ANSWER_ID)String answerId,
    @Parameter(name = "answer", required = true, description = OpenApiDoc.QUESTIONNAIRE.ANSWER_OBJ)
    @RequestBody Object answer) // multivalued answer?
  ;

  @Operation(summary = OpenApiDoc.QUESTIONNAIRE.DELETE_ANS_SUMMARY, description = OpenApiDoc.QUESTIONNAIRE.DELETE_ANS_OP)
  @DeleteMapping(path = "{questionnaireId}/answers/{answerId}", produces = MediaType.APPLICATION_JSON_VALUE)
  ResponseEntity<List<Error>> deleteQuestionnaireAnswer(
    @PathVariable("questionnaireId") @Parameter(description = OpenApiDoc.QUESTIONNAIRE.QUEST_ID)String questionnaireId,
    @PathVariable("answerId") @Parameter(description = OpenApiDoc.QUESTIONNAIRE.ANSWER_ID) String answerId);

  @Operation(summary = OpenApiDoc.QUESTIONNAIRE.GET_PAGES_SUMMARY, description = OpenApiDoc.QUESTIONNAIRE.GET_PAGES_OP)
  @GetMapping(path = "{questionnaireId}/pages", produces = MediaType.APPLICATION_JSON_VALUE)
  ResponseEntity<Items> getQuestionnairePages(@PathVariable("questionnaireId") @Parameter(description = OpenApiDoc.QUESTIONNAIRE.QUEST_ID) String questionnaireId);

  @Operation(summary = OpenApiDoc.QUESTIONNAIRE.PUT_PAGES_SUMMARY)
  @PutMapping(path = "{questionnaireId}/pages", produces = MediaType.APPLICATION_JSON_VALUE)
  ResponseEntity<Items> putQuestionnairePages(@NonNull @PathVariable("questionnaireId")
  		@Parameter(description = OpenApiDoc.QUESTIONNAIRE.QUEST_ID)String questionnaireId,
        @Parameter(name = "pages", required = true, description = OpenApiDoc.QUESTIONNAIRE.PAGES) @RequestBody Items pages);

  @Operation(summary = OpenApiDoc.QUESTIONNAIRE.GET_ITEMS_SUMMARY, description = OpenApiDoc.QUESTIONNAIRE.GET_ITEMS_OP)
  @GetMapping(path = "{questionnaireId}/items", produces = MediaType.APPLICATION_JSON_VALUE)
  ResponseEntity<List<ActionItem>> getQuestionnaireItems(@PathVariable("questionnaireId") @Parameter(description = OpenApiDoc.QUESTIONNAIRE.QUEST_ID) String questionnaireId);

  @Operation(summary = OpenApiDoc.QUESTIONNAIRE.GET_ITEM_SUMMARY, description = OpenApiDoc.QUESTIONNAIRE.GET_ITEM_OP)
  @GetMapping(path = "{questionnaireId}/items/{itemId}", produces = MediaType.APPLICATION_JSON_VALUE)
  ResponseEntity<ActionItem> getQuestionnaireItem(@PathVariable("questionnaireId") @Parameter(description = OpenApiDoc.QUESTIONNAIRE.ITEM_ID) String questionnaireId, @PathVariable("itemId") String itemId);

  @Operation(summary = OpenApiDoc.QUESTIONNAIRE.GET_ROWS_SUMMARY, description = OpenApiDoc.QUESTIONNAIRE.GET_ROWS_OP)
  @GetMapping(path = "{questionnaireId}/items/{itemId}/rows", produces = MediaType.APPLICATION_JSON_VALUE)
  ResponseEntity<List<String>> getQuestionnaireItemRows(@PathVariable("questionnaireId") @Parameter(description = OpenApiDoc.QUESTIONNAIRE.QUEST_ID) String questionnaireId,
	@PathVariable("itemId") @Parameter (description = OpenApiDoc.QUESTIONNAIRE.ITEM_ID) String itemId);

  @Operation(summary = OpenApiDoc.QUESTIONNAIRE.POST_ROW_SUMMARY, description = OpenApiDoc.QUESTIONNAIRE.POST_ROW_OP)
  @PostMapping(path = "{questionnaireId}/items/{itemId}/rows", produces = MediaType.APPLICATION_JSON_VALUE)
  ResponseEntity<List<String>> postQuestionnaireItemRow(@PathVariable("questionnaireId") @Parameter(description = OpenApiDoc.QUESTIONNAIRE.QUEST_ID) String questionnaireId,
	@Parameter(description = OpenApiDoc.QUESTIONNAIRE.ITEM_ID)
	@PathVariable("itemId") String itemId);

  @Operation(summary = OpenApiDoc.QUESTIONNAIRE.DELETE_ROW_ITEM_SUMMARY, description = OpenApiDoc.QUESTIONNAIRE.DELETE_ROW_ITEM_OP)
  @DeleteMapping(path = "{questionnaireId}/items/{itemId}/rows/{rowId}", produces = MediaType.APPLICATION_JSON_VALUE)
  ResponseEntity<List<String>> deleteQuestionnaireItemRow(@PathVariable("questionnaireId") @Parameter(description = OpenApiDoc.QUESTIONNAIRE.QUEST_ID) String questionnaireId,
	@Parameter (description = OpenApiDoc.QUESTIONNAIRE.ITEM_ID)
	@PathVariable("itemId") String itemId,
	@Parameter (description = OpenApiDoc.QUESTIONNAIRE.ROW_ID)
	@PathVariable("rowId") String rowId);

  @Operation(summary = OpenApiDoc.QUESTIONNAIRE.GET_VALUESETS_SUMMARY, description = OpenApiDoc.QUESTIONNAIRE.GET_VALUESETS_OP)
  @GetMapping(path = "{questionnaireId}/valueSets", produces = MediaType.APPLICATION_JSON_VALUE)
  ResponseEntity<List<ValueSet>> getQuestionnaireValueSets(@PathVariable("questionnaireId") @Parameter(description = OpenApiDoc.QUESTIONNAIRE.QUEST_ID) String questionnaireId);

  @Operation(summary = OpenApiDoc.QUESTIONNAIRE.GET_VALUESET_SUMMARY, description = OpenApiDoc.QUESTIONNAIRE.GET_VALUESET_OP)
  @GetMapping(path = "{questionnaireId}/valueSets/{valueSetId}", produces = MediaType.APPLICATION_JSON_VALUE)
  ResponseEntity<ValueSet> getQuestionnaireValueSet(@PathVariable("questionnaireId") @Parameter(description = OpenApiDoc.QUESTIONNAIRE.QUEST_ID) String questionnaireId,
   @PathVariable("valueSetId") @Parameter(description = OpenApiDoc.QUESTIONNAIRE.VALUESET_ID) String valueSetId);
}
