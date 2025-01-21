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
package io.dialob.form.service.rest;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import io.dialob.api.form.Form;
import io.dialob.api.form.FormPutResponse;
import io.dialob.api.form.FormTag;
import io.dialob.api.rest.Response;
import io.dialob.common.Constants;
import io.dialob.form.service.api.repository.FormListItem;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

/**
 * REST service interface for managing forms and related functionalities in the Dialob Form Service.
 */
@RequestMapping(value = "${dialob.api.context-path:}/forms")
@OpenAPIDefinition(info = @Info(title = "DialobFormService"), tags = {
  @Tag(name = "dialob-form-service")
})
public interface FormsRestService {

  /**
   * Retrieves a list of forms, with optional metadata filtering.
   *
   * @param metadata an optional query parameter to filter the forms based on metadata
   * @return a ResponseEntity containing a list of FormListItem objects
   */
  @GetMapping(produces = {MediaType.APPLICATION_JSON_VALUE})
  ResponseEntity<List<FormListItem>> getForms(@RequestParam(name = "metadata", required = false) String metadata);

  /**
   * Creates a copy of the specified item using the provided form data.
   *
   * @param itemId the ID of the item to be copied, must follow the valid ID pattern
   * @param form the form data used for creating the item copy, must not be null and must adhere to validation rules
   * @return a ResponseEntity containing the result of the copy operation as a FormPutResponse object
   */
  @PostMapping(path = "/actions/itemCopy", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
  ResponseEntity<FormPutResponse> itemCopy(@RequestParam(name = "itemId") @Pattern(regexp = Constants.VALID_ID_PATTERN) String itemId, @Validated @RequestBody Form form);

  /**
   * Creates a new form by processing the provided form data.
   *
   * @param formDocument the form data to be created, must not be null and must adhere to validation rules
   * @return a ResponseEntity containing the created form
   */
  @Operation(summary = OpenApiDoc.POST_FORM.POST_FORM_SUMMARY, description = OpenApiDoc.POST_FORM.POST_FORM_OP)
  @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
  ResponseEntity<Form> postForm(
    @Parameter(description = "New form", name = "form", required = true)
    @RequestBody
    @Valid Form formDocument);

  /**
   * Processes the provided CSV to create or update forms and returns the result of the operation.
   *
   * @param form the CSV representation of the form data to be processed
   * @return a ResponseEntity containing the result of the operation as a FormPutResponse object
   */
  @Operation(summary = OpenApiDoc.POST_FORM.POST_FORM_SUMMARY, description = OpenApiDoc.POST_FORM.POST_FORM_OP)
  @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = "text/csv")
  ResponseEntity<FormPutResponse> postFormFromCsv(
    @Parameter(description = "New form", name = "form", required = true)
    @RequestBody
    String form);

  /**
   * Updates a form with the specified form ID using the provided details.
   *
   * @param formId       The logical form name or document ID. Must match the specified pattern.
   * @param oldId        The old ID associated with the form, if applicable. Optional. Must match the specified pattern.
   * @param newId        The new ID to replace the old ID, if applicable. Optional. Must match the specified pattern.
   * @param forced       Flag to determine if the operation should be forced. Default value is false.
   * @param dryRun       Flag to determine if the operation should be simulated without actual changes. Default value is false.
   * @param formDocument The new form data to update the form. Must not be null.
   * @return A ResponseEntity containing a FormPutResponse object representing the result of the update operation.
   */
  @Operation(summary = OpenApiDoc.PUT_FORM.PUT_FORM_SUMMARY, description = OpenApiDoc.PUT_FORM.PUT_FORM_OP)
  @PutMapping(path = "{formId}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
  ResponseEntity<FormPutResponse> putForm(
    @PathVariable("formId") @Pattern(regexp = Constants.VALID_FORM_ID_PATTERN) @Parameter(description = OpenApiDoc.GENERAL.FORM_ID) String formId,
    @Parameter(description = OpenApiDoc.PUT_FORM.OLD_ID) @Pattern(regexp = Constants.VALID_ID_PATTERN) @RequestParam(name = "oldId", required = false) String oldId,
    @Parameter(description = OpenApiDoc.PUT_FORM.NEW_ID) @Pattern(regexp = Constants.VALID_ID_PATTERN) @RequestParam(name = "newId", required = false) String newId,
    @RequestParam(name = "force", required = false, defaultValue = "false") @Parameter(description = OpenApiDoc.PUT_FORM.FORCED) boolean forced,
    @RequestParam(name = "dryRun", required = false, defaultValue = "false") @Parameter(description = OpenApiDoc.PUT_FORM.DRY_RUN) boolean dryRun,
    @Parameter(description = "New form data", name = "form", required = true) @Validated @NotNull @RequestBody Form formDocument);

  /**
   * Deletes a form identified by the given form ID.
   *
   * @param formId the unique identifier of the form to be deleted; it must match the valid form ID pattern
   * @return a ResponseEntity containing a Response object with the status and details of the operation
   */
  @Operation(summary = OpenApiDoc.DELETE_FORM.DELETE_SUMMARY, description = OpenApiDoc.DELETE_FORM.DELETE_OP)
  @DeleteMapping(path = "{formId}")
  ResponseEntity<Response> deleteForm(
    @Parameter(description = OpenApiDoc.GENERAL.FORM_ID)
    @PathVariable("formId") @Pattern(regexp = Constants.VALID_FORM_ID_PATTERN) String formId);

  /**
   * Retrieves a form based on the given form ID. An optional revision parameter can
   * also be provided to fetch a specific version of the form.
   *
   * @param formId the unique identifier of the form, must conform to the pattern defined by VALID_FORM_ID_PATTERN
   * @param rev optional parameter specifying the revision of the form, must conform to the pattern defined by VALID_REV_PATTERN
   * @return a {@link ResponseEntity} containing the requested {@link Form} if found, or appropriate error status if not
   */
  @Operation(summary = OpenApiDoc.FORM_ID.GET_FORMID_SUMMARY, description = OpenApiDoc.FORM_ID.GET_FORMID_OP)
  @GetMapping(path = "{formId}", produces = MediaType.APPLICATION_JSON_VALUE)
  ResponseEntity<Form> getForm(
    @Parameter(description = OpenApiDoc.GENERAL.FORM_ID)
    @PathVariable("formId") @Pattern(regexp = Constants.VALID_FORM_ID_PATTERN) String formId,
    @Parameter(description = OpenApiDoc.GENERAL.REV)
    @RequestParam(name = "rev", required = false) @Pattern(regexp = Constants.VALID_REV_PATTERN) String rev);

  /**
   * Retrieves a list of tags associated with a specific form.
   *
   * @param formId The identifier of the form for which tags are to be retrieved. Must conform to the valid form ID pattern.
   * @return A ResponseEntity containing a list of FormTag objects associated with the specified form.
   */
  @Operation(summary = OpenApiDoc.TAG.GET_TAGS_SUMMARY, description = OpenApiDoc.TAG.GET_TAGS_OP)
  @GetMapping(path = "{formId}/tags", produces = MediaType.APPLICATION_JSON_VALUE)
  ResponseEntity<List<FormTag>> getFormTags(
    @Parameter(description = OpenApiDoc.GENERAL.FORM_ID)
    @PathVariable("formId") @Pattern(regexp = Constants.VALID_FORM_ID_PATTERN) String formId);

  /**
   * Retrieves a specific tag associated with a form identified by its ID.
   *
   * @param formId The ID of the form for which the tag is being retrieved. It must match the valid form ID pattern.
   * @param tagName The name of the tag to be retrieved. It must match the valid form tag pattern.
   * @return A ResponseEntity containing the retrieved FormTag object if found, or an error response if not.
   */

  @Operation(summary = OpenApiDoc.TAG.GET_TAG_NAME_SUMMARY, description = OpenApiDoc.TAG.GET_TAG_NAME_OP)
  @GetMapping(path = "{formId}/tags/{tagName}", produces = MediaType.APPLICATION_JSON_VALUE)
  ResponseEntity<FormTag> getFormTag(
    @Parameter(description = OpenApiDoc.GENERAL.FORM_ID)
    @PathVariable("formId") @Pattern(regexp = Constants.VALID_FORM_ID_PATTERN) String formId,
    @Parameter(description = OpenApiDoc.GENERAL.TAG_NAME)
    @PathVariable("tagName") @Pattern(regexp = Constants.VALID_FORM_TAG_PATTERN) String tagName);

  /**
   * Handles the creation of a form's tag by performing the required operations
   * based on the provided form ID, revision, and snapshot parameters.
   *
   * @param formId The unique identifier of the form. Must match the pattern defined in {@link Constants#VALID_FORM_ID_PATTERN}.
   * @param rev An optional revision identifier for the form. If provided, it must match the pattern defined in {@link Constants#VALID_REV_PATTERN}.
   * @param snapshot A boolean flag to indicate whether a snapshot of the tag state should be generated. Default value is false.
   * @param tag The FormTag object containing tag details to be created. This parameter is required.
   * @return A ResponseEntity containing the Response object with details of the created tag, including any metadata and status information.
   */
  @Operation(summary = OpenApiDoc.TAG.POST_FORM_TAG_SUMMARY, description = OpenApiDoc.TAG.POST_FORM_TAG_OP)
  @PostMapping(path = "{formId}/tags", produces = MediaType.APPLICATION_JSON_VALUE)
  ResponseEntity<Response> postFormTag(
    @PathVariable("formId") @Pattern(regexp = Constants.VALID_FORM_ID_PATTERN) @Parameter(description = OpenApiDoc.GENERAL.FORM_ID) String formId,
    @RequestParam(name = "rev", required = false) @Pattern(regexp = Constants.VALID_REV_PATTERN) @Parameter(description = OpenApiDoc.GENERAL.REV) String rev,
    @RequestParam(name = "snapshot", required = false, defaultValue = "false") @Parameter(description = OpenApiDoc.TAG.SNAPSHOT) boolean snapshot,
    @Parameter(name = "tag", required = true, description = OpenApiDoc.TAG.TAG_OBJ)
    @RequestBody FormTag tag);

  /**
   * Updates the latest tag for the specified form.
   *
   * @param formId the unique identifier of the form; must match the valid form ID pattern specified by {@link Constants#VALID_FORM_ID_PATTERN}.
   * @param tag the tag object containing the updated information for the latest tag.
   * @return a {@link ResponseEntity} containing a {@link Response} object indicating the result of the operation.
   */
  @Operation(summary = OpenApiDoc.TAG.TAG_LATEST_SUMMARY, description = OpenApiDoc.TAG.TAG_LATEST_OP)
  @PutMapping(path = "{formId}/tags/latest", produces = MediaType.APPLICATION_JSON_VALUE)
  ResponseEntity<Response> putFormTagLatest(
    @PathVariable("formId") @Pattern(regexp = Constants.VALID_FORM_ID_PATTERN) @Parameter(description = OpenApiDoc.GENERAL.FORM_ID) String formId,
    @Parameter(name = "tag", required = true, description = OpenApiDoc.TAG.TAG_OBJ) @RequestBody FormTag tag);

  /**
   * Updates or creates a tag associated with a specific form.
   *
   * @param formId the ID of the form to which the tag will be added or updated;
   *               must match the valid form ID pattern defined in {@link Constants#VALID_FORM_ID_PATTERN}.
   * @param tagName the name of the tag to be updated or created;
   *                must match the valid tag name pattern defined in {@link Constants#VALID_FORM_TAG_PATTERN}.
   * @param updateTag the details of the tag to update or create;
   *                  provided in the request body and required.
   * @return a ResponseEntity containing the status and details of the operation encapsulated within
   *         a Response object.
   */
  @PutMapping(path = "{formId}/tags/{tagName}", produces = MediaType.APPLICATION_JSON_VALUE)
  ResponseEntity<Response> putFormTag(
    @PathVariable("formId") @Pattern(regexp = Constants.VALID_FORM_ID_PATTERN) String formId,
    @PathVariable("tagName") @Pattern(regexp = Constants.VALID_FORM_TAG_PATTERN) String tagName,
    @Parameter(description = "Updated tag", name = "tag", required = true)
    @RequestBody FormTag updateTag
  );

}
