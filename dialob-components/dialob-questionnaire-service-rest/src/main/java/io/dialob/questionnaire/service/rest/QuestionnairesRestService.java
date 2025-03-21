/*
 * Copyright © 2015 - 2025 ReSys (info@dialob.io)
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
import io.dialob.api.annotation.Nullable;
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
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import static io.dialob.common.Constants.*;

/**
 * This interface defines a RESTful API for managing questionnaires, answers, and related metadata.
 * It contains methods for CRUD operations on questionnaires and answers, exporting data,
 * and retrieving metadata or errors associated with questionnaires.
 * <p>
 * Endpoints support different HTTP methods such as GET, POST, PUT, and DELETE,
 * and handle input and output in JSON or CSV formats. The methods use OpenAPI annotations
 * to document their usage and expected inputs/outputs.
 * <p>
 * Mappings:
 * - Base path for the service is configurable via the "dialob.api.context-path" property.
 * - All methods produce specific content types such as JSON or CSV and validate input parameters
 * against defined patterns.
 */
@RequestMapping("${dialob.api.context-path:}/questionnaires")
@OpenAPIDefinition(info = @Info(title = "DialobQuestionnaireService"), tags = {
  @Tag(name = "dialob-questionnaire-service")

})
public interface QuestionnairesRestService {

  /**
   * Handles the HTTP POST request to create a new questionnaire.
   *
   * @param questionnaire The new questionnaire object to be created. It must be non-null and adheres to the validation constraints.
   * @return A ResponseEntity containing the ID and revision of the created questionnaire.
   */
  @Operation(summary = OpenApiDoc.QUESTIONNAIRE.POST_QUEST_SUMMARY)
  @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
  ResponseEntity<IdAndRevision> postQuestionnaire(
    @Parameter(name = "questionnaire", required = true, description = "New questionnaire object")
    @NonNull @RequestBody @Validated Questionnaire questionnaire);

  /**
   * Retrieves a list of questionnaires filtered by the provided query parameters.
   *
   * @param owner    Optional. Specifies the owner of the questionnaire.
   * @param formId   Optional. Filters the questionnaires by form ID.
   * @param formName Optional. Filters the questionnaires by form name.
   * @param formTag  Optional. Filters the questionnaires by form tag.
   * @param status   Optional. Filters the questionnaires by their status.
   * @return A ResponseEntity containing a list of QuestionnaireListItem objects that match the specified filters.
   */
  @GetMapping(produces = {MediaType.APPLICATION_JSON_VALUE})
  ResponseEntity<List<QuestionnaireListItem>> getQuestionnaires(@RequestParam(name = "owner", required = false) String owner,
                                                                @RequestParam(name = "formId", required = false) String formId,
                                                                @RequestParam(name = "formName", required = false) String formName,
                                                                @RequestParam(name = "formTag", required = false) String formTag,
                                                                @RequestParam(name = "status", required = false) Questionnaire.Metadata.Status status);

  /**
   * A record representing parameters for obtaining a CSV export.
   * <p>
   * This record encapsulates a set of optional parameters that may be used to filter
   * or customize the resulting CSV output. Validation constraints are applied to
   * ensure correct formats for certain fields.
   * <p>
   * Each field in this record can be supplied as a request parameter.
   * Default values are assigned to some fields if they are not provided.
   */
  record GetCsv(

    /**
     * Represents an optional form identifier used as a request parameter.
     * <p>
     * The value of this variable is validated against a predefined regular
     * expression pattern defined by VALID_FORM_ID_PATTERN. It can be used
     * to filter or identify a specific form during a CSV export operation.
     * <p>
     * This parameter is marked as optional, meaning it is not required to
     * be provided in the request. If not supplied, it will be null by default.
     */
    @RequestParam(required = false)
    @Nullable
    @Pattern(regexp = VALID_FORM_ID_PATTERN)
    String formId,

    /**
     * Represents an optional form name used as a request parameter.
     * <p> <p>
     * The value of this variable is validated against a predefined regular
     * expression pattern specified by VALID_FORM_NAME_PATTERN. It may be used
     * to filter or identify specific forms during operations such as data
     * retrieval or processing.
     * <p> <p>
     * This parameter is optional and therefore does not need to be included
     * in the request. If not provided, the value will be null by default.
     */
    @RequestParam(required = false)
    @Nullable
    @Pattern(regexp = VALID_FORM_NAME_PATTERN)
    String formName,

    /**
     * Represents an optional form tag used as a request parameter.
     * <p> <p>
     * The value of this variable is validated against a predefined regular
     * expression pattern specified by VALID_FORM_NAME_PATTERN. It may be
     * utilized to filter or identify specific tags associated with forms during
     * operations such as data retrieval or processing.
     * <p> <p>
     * This parameter is optional and does not need to be included in the request.
     * If not provided, the value will default to null.
     */
    @RequestParam(required = false)
    @Nullable
    @Pattern(regexp = VALID_FORM_NAME_PATTERN)
    String formTag,

    // BUG RequestParam's name attribute here does not work with records
    @RequestParam(required = false)
    List<@Pattern(regexp = QUESTIONNAIRE_ID_PATTERN) String> questionnaire,

    /**
     * Represents the locale or language preference for the requested operation.
     * <p> <p>
     * This parameter is optional and may be omitted. If provided, it allows specifying
     * the desired locale for any language or region-specific processing.
     */
    @RequestParam(required = false)
    Locale language,

    @RequestParam(required = false)
    @Nullable
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    LocalDateTime from,

    @RequestParam(required = false)
    @Nullable
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    LocalDateTime to
  ) {
    public GetCsv {
      // defaults
      language = Objects.requireNonNullElse(language, Locale.ENGLISH);
      questionnaire = Objects.requireNonNullElseGet(questionnaire, Collections::emptyList);
    }
  }

  /**
   * Handles HTTP GET requests to produce a CSV response.
   *
   * @param getCsv the request payload containing the necessary filters or parameters
   *               for generating the CSV, validated using the @Valid annotation
   * @return a ResponseEntity containing the generated CSV content as a string with
   * a "text/csv" content type
   */
  @GetMapping(produces = {"text/csv"})
  ResponseEntity<String> getCsv(@Valid GetCsv getCsv);

  /**
   * Retrieves a questionnaire by its unique identifier.
   *
   * @param questionnaireId the unique identifier for the questionnaire; must match the defined pattern
   * @return a ResponseEntity containing the retrieved Questionnaire object if found, or an appropriate error response
   */
  @Operation(summary = OpenApiDoc.QUESTIONNAIRE.GET_QUESTID_SUMMARY, description = OpenApiDoc.QUESTIONNAIRE.GET_QUESTID_OP)
  @GetMapping(path = "{questionnaireId}", produces = MediaType.APPLICATION_JSON_VALUE)
  ResponseEntity<Questionnaire> getQuestionnaire(
    @Parameter(description = OpenApiDoc.QUESTIONNAIRE.QUEST_ID)
    @PathVariable("questionnaireId") @Pattern(regexp = QUESTIONNAIRE_ID_PATTERN)
    String questionnaireId);

  /**
   * Deletes a questionnaire by its unique identifier.
   *
   * @param questionnaireId the unique identifier of the questionnaire to delete.
   *                        Must match the defined pattern.
   * @return a ResponseEntity containing the response status and any associated information or error details.
   */
  @Operation(summary = OpenApiDoc.QUESTIONNAIRE.DELETE_QUEST_SUMMARY, description = OpenApiDoc.QUESTIONNAIRE.DELETE_QUEST_OP)
  @DeleteMapping(path = "{questionnaireId}")
  ResponseEntity<Response> deleteQuestionnaire(
    @Parameter(description = OpenApiDoc.QUESTIONNAIRE.QUEST_ID)
    @PathVariable("questionnaireId") @Pattern(regexp = QUESTIONNAIRE_ID_PATTERN) String questionnaireId);

  /**
   * Updates an existing questionnaire with the provided data.
   *
   * @param questionnaireId the unique identifier of the questionnaire to be updated. Must match the defined pattern.
   * @param questionnaire   the updated questionnaire object containing the new data. Cannot be null and must adhere to validation rules.
   * @return a ResponseEntity containing the updated questionnaire object on success or an error response if the update fails.
   */
  @Operation(summary = OpenApiDoc.QUESTIONNAIRE.PUT_QUEST_SUMMARY, description = OpenApiDoc.QUESTIONNAIRE.PUT_QUEST_OP)
  @PutMapping(path = "{questionnaireId}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
  ResponseEntity<Questionnaire> putQuestionnaire(
    @Parameter(description = OpenApiDoc.QUESTIONNAIRE.QUEST_ID)
    @PathVariable("questionnaireId") @Pattern(regexp = QUESTIONNAIRE_ID_PATTERN) String questionnaireId,
    @Parameter(name = "questionnaire", required = true, description = OpenApiDoc.QUESTIONNAIRE.QUEST_OBJ)
    @RequestBody @Validated Questionnaire questionnaire);

  /**
   * Retrieves the status of a specific questionnaire by its ID.
   *
   * @param questionnaireId the unique identifier of the questionnaire, must match the defined pattern
   * @return a ResponseEntity containing the status metadata of the specified questionnaire
   */
  @Operation(summary = OpenApiDoc.QUESTIONNAIRE.GET_STATUS_SUMMARY, description = OpenApiDoc.QUESTIONNAIRE.GET_STATUS_OP)
  @GetMapping(path = "{questionnaireId}/status", produces = MediaType.APPLICATION_JSON_VALUE)
  ResponseEntity<Questionnaire.Metadata.Status> getQuestionnaireStatus(@PathVariable("questionnaireId") @Pattern(regexp = QUESTIONNAIRE_ID_PATTERN) @Parameter(description = OpenApiDoc.QUESTIONNAIRE.QUEST_ID) String questionnaireId);

  /**
   * Updates the status of a specific questionnaire identified by its unique ID.
   *
   * @param questionnaireId the unique identifier of the questionnaire that needs to be updated; must match the defined pattern.
   * @param status          the new status to be set for the questionnaire; this is a required parameter.
   * @return a ResponseEntity containing the updated status of the questionnaire.
   */
  @Operation(summary = OpenApiDoc.QUESTIONNAIRE.PUT_STATUS_SUMMARY)
  @PutMapping(path = "{questionnaireId}/status", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
  ResponseEntity<Questionnaire.Metadata.Status> putQuestionnaireStatus(@PathVariable("questionnaireId") @Pattern(regexp = QUESTIONNAIRE_ID_PATTERN) @Parameter(description = OpenApiDoc.QUESTIONNAIRE.QUEST_ID) String questionnaireId,
                                                                       @Parameter(name = "status", required = true, description = OpenApiDoc.QUESTIONNAIRE.PUT_NEW_STATUS)
                                                                       @RequestBody Questionnaire.Metadata.Status status);

  /**
   * Retrieves answers for a specific questionnaire identified by the given questionnaire ID.
   *
   * @param questionnaireId the unique identifier of the questionnaire. It must match the specified pattern.
   * @return a ResponseEntity containing a list of answers for the specified questionnaire.
   */
  @Operation(summary = OpenApiDoc.QUESTIONNAIRE.GET_ANSWERS_SUMMARY, description = OpenApiDoc.QUESTIONNAIRE.GET_ANSWERS_OP)
  @GetMapping(path = "{questionnaireId}/answers", produces = MediaType.APPLICATION_JSON_VALUE)
  ResponseEntity<List<Answer>> getQuestionnaireAnswers(
    @Parameter(description = OpenApiDoc.QUESTIONNAIRE.QUEST_ID)
    @PathVariable("questionnaireId") @Pattern(regexp = QUESTIONNAIRE_ID_PATTERN) String questionnaireId);

  /**
   * Retrieves a list of errors associated with the specified questionnaire.
   *
   * @param questionnaireId the unique identifier of the questionnaire for which errors are to be fetched.
   *                        This ID must match the specified pattern QUESTIONNAIRE_ID_PATTERN.
   * @return ResponseEntity containing a list of errors related to the specified questionnaire.
   */
  @Operation(summary = OpenApiDoc.QUESTIONNAIRE.ERRORS_SUMMARY, description = OpenApiDoc.QUESTIONNAIRE.ERRORS_OP)
  @GetMapping(path = "{questionnaireId}/errors", produces = MediaType.APPLICATION_JSON_VALUE)
  ResponseEntity<List<Error>> getQuestionnaireErrors(
    @Parameter(description = OpenApiDoc.QUESTIONNAIRE.QUEST_ID)
    @PathVariable("questionnaireId") @Pattern(regexp = QUESTIONNAIRE_ID_PATTERN) String questionnaireId);

  /**
   * Submits or updates questionnaire answers for the specified questionnaire.
   *
   * @param questionnaireId the unique identifier of the questionnaire, which must match the defined pattern
   * @param answers         a list of answers to be submitted or updated, provided in the request body
   * @return a ResponseEntity containing a list of errors, if any occurred during processing
   */
  @Operation(summary = OpenApiDoc.QUESTIONNAIRE.POST_ANSWER_SUMMARY)
  @PostMapping(path = "{questionnaireId}/answers", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
  ResponseEntity<List<Error>> putQuestionnaireAnswers(
    @PathVariable("questionnaireId") @Pattern(regexp = QUESTIONNAIRE_ID_PATTERN) @Parameter(description = OpenApiDoc.QUESTIONNAIRE.QUEST_ID) String questionnaireId,
    @Parameter(name = "answers", required = true, description = OpenApiDoc.QUESTIONNAIRE.NEW_ANSWERS_OBJ)
    @RequestBody List<Answer> answers);

  /**
   * Updates or creates an answer for a specific questionnaire.
   *
   * @param questionnaireId the unique identifier of the questionnaire, must match the defined pattern
   * @param answerId        the unique identifier of the answer to be updated or created
   * @param answer          the answer object containing the data to be saved, required for the operation
   * @return a response entity containing a list of errors if any issues are encountered during the operation
   */
  @Operation(summary = OpenApiDoc.QUESTIONNAIRE.PUT_ANSWER_SUMMARY, description = OpenApiDoc.QUESTIONNAIRE.PUT_ANSWER_OP)
  @PutMapping(path = "{questionnaireId}/answers/{answerId}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = {MediaType.APPLICATION_JSON_VALUE})
  ResponseEntity<List<Error>> putQuestionnaireAnswer(
    @PathVariable("questionnaireId") @Pattern(regexp = QUESTIONNAIRE_ID_PATTERN) @Parameter(description = OpenApiDoc.QUESTIONNAIRE.QUEST_ID) String questionnaireId,
    @PathVariable("answerId") @Parameter(description = OpenApiDoc.QUESTIONNAIRE.ANSWER_ID) String answerId,
    @Parameter(name = "answer", required = true, description = OpenApiDoc.QUESTIONNAIRE.ANSWER_OBJ)
    @RequestBody Object answer) // multivalued answer?
  ;

  /**
   * Deletes a specific answer associated with a given questionnaire.
   *
   * @param questionnaireId the unique identifier of the questionnaire; must conform to a specific pattern
   * @param answerId        the unique identifier of the answer to be deleted
   * @return a ResponseEntity containing a list of errors, if any, encountered during the deletion process
   */
  @Operation(summary = OpenApiDoc.QUESTIONNAIRE.DELETE_ANS_SUMMARY, description = OpenApiDoc.QUESTIONNAIRE.DELETE_ANS_OP)
  @DeleteMapping(path = "{questionnaireId}/answers/{answerId}", produces = MediaType.APPLICATION_JSON_VALUE)
  ResponseEntity<List<Error>> deleteQuestionnaireAnswer(
    @PathVariable("questionnaireId") @Pattern(regexp = QUESTIONNAIRE_ID_PATTERN) @Parameter(description = OpenApiDoc.QUESTIONNAIRE.QUEST_ID) String questionnaireId,
    @PathVariable("answerId") @Parameter(description = OpenApiDoc.QUESTIONNAIRE.ANSWER_ID) String answerId);

  /**
   * Retrieves the pages of a specific questionnaire.
   *
   * @param questionnaireId the ID of the questionnaire to retrieve pages for, must match the defined pattern
   * @return a ResponseEntity containing the pages of the questionnaire wrapped in an Items object
   */
  @Operation(summary = OpenApiDoc.QUESTIONNAIRE.GET_PAGES_SUMMARY, description = OpenApiDoc.QUESTIONNAIRE.GET_PAGES_OP)
  @GetMapping(path = "{questionnaireId}/pages", produces = MediaType.APPLICATION_JSON_VALUE)
  ResponseEntity<Items> getQuestionnairePages(@PathVariable("questionnaireId") @Pattern(regexp = QUESTIONNAIRE_ID_PATTERN) @Parameter(description = OpenApiDoc.QUESTIONNAIRE.QUEST_ID) String questionnaireId);

  /**
   * Updates the pages of a questionnaire specified by its unique identifier.
   *
   * @param questionnaireId the unique identifier of the questionnaire to update, must match the required pattern
   * @param pages           the updated pages for the questionnaire, must not be null
   * @return a {@link ResponseEntity} containing an {@link Items} object representing the updated pages
   */
  @Operation(summary = OpenApiDoc.QUESTIONNAIRE.PUT_PAGES_SUMMARY)
  @PutMapping(path = "{questionnaireId}/pages", produces = MediaType.APPLICATION_JSON_VALUE)
  ResponseEntity<Items> putQuestionnairePages(@NonNull @PathVariable("questionnaireId") @Pattern(regexp = QUESTIONNAIRE_ID_PATTERN)
                                              @Parameter(description = OpenApiDoc.QUESTIONNAIRE.QUEST_ID) String questionnaireId,
                                              @Parameter(name = "pages", required = true, description = OpenApiDoc.QUESTIONNAIRE.PAGES) @RequestBody Items pages);

  /**
   * Retrieves the list of questionnaire items for a specific questionnaire.
   *
   * @param questionnaireId the identifier of the questionnaire, which must match the specified pattern
   * @return a ResponseEntity containing a list of ActionItem objects associated with the given questionnaire
   */
  @Operation(summary = OpenApiDoc.QUESTIONNAIRE.GET_ITEMS_SUMMARY, description = OpenApiDoc.QUESTIONNAIRE.GET_ITEMS_OP)
  @GetMapping(path = "{questionnaireId}/items", produces = MediaType.APPLICATION_JSON_VALUE)
  ResponseEntity<List<ActionItem>> getQuestionnaireItems(@PathVariable("questionnaireId") @Pattern(regexp = QUESTIONNAIRE_ID_PATTERN) @Parameter(description = OpenApiDoc.QUESTIONNAIRE.QUEST_ID) String questionnaireId);

  /**
   * Retrieves a specific questionnaire item by its ID and the parent questionnaire ID.
   *
   * @param questionnaireId the unique identifier of the questionnaire, must match the specified pattern
   * @param itemId          the unique identifier of the questionnaire item to be retrieved
   * @return a ResponseEntity containing the requested ActionItem object
   */
  @Operation(summary = OpenApiDoc.QUESTIONNAIRE.GET_ITEM_SUMMARY, description = OpenApiDoc.QUESTIONNAIRE.GET_ITEM_OP)
  @GetMapping(path = "{questionnaireId}/items/{itemId}", produces = MediaType.APPLICATION_JSON_VALUE)
  ResponseEntity<ActionItem> getQuestionnaireItem(@PathVariable("questionnaireId") @Pattern(regexp = QUESTIONNAIRE_ID_PATTERN) @Parameter(description = OpenApiDoc.QUESTIONNAIRE.ITEM_ID) String questionnaireId, @PathVariable("itemId") String itemId);

  /**
   * Retrieves the rows associated with a specific questionnaire item.
   *
   * @param questionnaireId the unique identifier of the questionnaire, must match the defined pattern
   * @param itemId          the unique identifier of the item within the questionnaire
   * @return a ResponseEntity containing a list of strings representing the rows of the specified questionnaire item
   */
  @Operation(summary = OpenApiDoc.QUESTIONNAIRE.GET_ROWS_SUMMARY, description = OpenApiDoc.QUESTIONNAIRE.GET_ROWS_OP)
  @GetMapping(path = "{questionnaireId}/items/{itemId}/rows", produces = MediaType.APPLICATION_JSON_VALUE)
  ResponseEntity<List<String>> getQuestionnaireItemRows(@PathVariable("questionnaireId") @Pattern(regexp = QUESTIONNAIRE_ID_PATTERN) @Parameter(description = OpenApiDoc.QUESTIONNAIRE.QUEST_ID) String questionnaireId,
                                                        @PathVariable("itemId") @Parameter(description = OpenApiDoc.QUESTIONNAIRE.ITEM_ID) String itemId);

  /**
   * Adds a new row to a specified item in the given questionnaire.
   *
   * @param questionnaireId the unique identifier of the questionnaire, must match the defined pattern
   * @param itemId          the identifier of the item within the questionnaire where the row will be added
   * @return a ResponseEntity containing a list of strings, representing the result of the operation
   */
  @Operation(summary = OpenApiDoc.QUESTIONNAIRE.POST_ROW_SUMMARY, description = OpenApiDoc.QUESTIONNAIRE.POST_ROW_OP)
  @PostMapping(path = "{questionnaireId}/items/{itemId}/rows", produces = MediaType.APPLICATION_JSON_VALUE)
  ResponseEntity<List<String>> postQuestionnaireItemRow(@PathVariable("questionnaireId") @Pattern(regexp = QUESTIONNAIRE_ID_PATTERN) @Parameter(description = OpenApiDoc.QUESTIONNAIRE.QUEST_ID) String questionnaireId,
                                                        @Parameter(description = OpenApiDoc.QUESTIONNAIRE.ITEM_ID)
                                                        @PathVariable("itemId") String itemId);

  /**
   * Deletes a specific row within a questionnaire item.
   *
   * @param questionnaireId the unique identifier of the questionnaire; must match the defined pattern.
   * @param itemId          the unique identifier of the questionnaire item whose row is to be deleted.
   * @param rowId           the unique identifier of the row within the specified questionnaire item to be deleted.
   * @return a ResponseEntity containing a list of strings, which might represent status messages or errors
   * related to the delete operation.
   */
  @Operation(summary = OpenApiDoc.QUESTIONNAIRE.DELETE_ROW_ITEM_SUMMARY, description = OpenApiDoc.QUESTIONNAIRE.DELETE_ROW_ITEM_OP)
  @DeleteMapping(path = "{questionnaireId}/items/{itemId}/rows/{rowId}", produces = MediaType.APPLICATION_JSON_VALUE)
  ResponseEntity<List<String>> deleteQuestionnaireItemRow(@PathVariable("questionnaireId") @Pattern(regexp = QUESTIONNAIRE_ID_PATTERN) @Parameter(description = OpenApiDoc.QUESTIONNAIRE.QUEST_ID) String questionnaireId,
                                                          @Parameter(description = OpenApiDoc.QUESTIONNAIRE.ITEM_ID)
                                                          @PathVariable("itemId") String itemId,
                                                          @Parameter(description = OpenApiDoc.QUESTIONNAIRE.ROW_ID)
                                                          @PathVariable("rowId") String rowId);

  /**
   * Retrieves a list of value sets associated with a specific questionnaire.
   *
   * @param questionnaireId the unique identifier of the questionnaire, which must match the defined pattern
   * @return a ResponseEntity containing a list of ValueSet objects associated with the specified questionnaire
   */
  @Operation(summary = OpenApiDoc.QUESTIONNAIRE.GET_VALUESETS_SUMMARY, description = OpenApiDoc.QUESTIONNAIRE.GET_VALUESETS_OP)
  @GetMapping(path = "{questionnaireId}/valueSets", produces = MediaType.APPLICATION_JSON_VALUE)
  ResponseEntity<List<ValueSet>> getQuestionnaireValueSets(@PathVariable("questionnaireId") @Pattern(regexp = QUESTIONNAIRE_ID_PATTERN) @Parameter(description = OpenApiDoc.QUESTIONNAIRE.QUEST_ID) String questionnaireId);

  /**
   * Retrieves a specific ValueSet associated with a given Questionnaire.
   *
   * @param questionnaireId the unique identifier of the questionnaire. This must match the specified pattern.
   * @param valueSetId      the unique identifier of the value set to be retrieved.
   * @return a ResponseEntity containing the ValueSet associated with the given questionnaire and value set identifiers.
   */
  @Operation(summary = OpenApiDoc.QUESTIONNAIRE.GET_VALUESET_SUMMARY, description = OpenApiDoc.QUESTIONNAIRE.GET_VALUESET_OP)
  @GetMapping(path = "{questionnaireId}/valueSets/{valueSetId}", produces = MediaType.APPLICATION_JSON_VALUE)
  ResponseEntity<ValueSet> getQuestionnaireValueSet(@PathVariable("questionnaireId") @Pattern(regexp = QUESTIONNAIRE_ID_PATTERN) @Parameter(description = OpenApiDoc.QUESTIONNAIRE.QUEST_ID) String questionnaireId,
                                                    @PathVariable("valueSetId") @Parameter(description = OpenApiDoc.QUESTIONNAIRE.VALUESET_ID) String valueSetId);
}
