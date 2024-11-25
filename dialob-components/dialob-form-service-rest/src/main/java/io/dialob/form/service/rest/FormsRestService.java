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
package io.dialob.form.service.rest;

import io.dialob.api.form.Form;
import io.dialob.api.form.FormPutResponse;
import io.dialob.api.form.FormTag;
import io.dialob.api.rest.Response;
import io.dialob.form.service.api.repository.FormListItem;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping(value = "${dialob.api.context-path:}/forms")
@OpenAPIDefinition(info = @Info(title = "DialobFormService"), tags = {
  @Tag(name = "dialob-form-service")
})
public interface FormsRestService {

  String VALID_FORM_ID_PATTERN = "[a-zA-Z0-9-_]+";

  @GetMapping(produces = {MediaType.APPLICATION_JSON_VALUE})
  ResponseEntity<List<FormListItem>> getForms(@RequestParam(name = "metadata", required = false) String metadata);

  @PostMapping(path = "/actions/itemCopy", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
  ResponseEntity<FormPutResponse> itemCopy(@RequestParam(name = "itemId") String itemId, @Validated @RequestBody Form form);

  @Operation(summary = OpenApiDoc.POST_FORM.POST_FORM_SUMMARY, description = OpenApiDoc.POST_FORM.POST_FORM_OP)
  @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
  ResponseEntity<Form> postForm(
    @Parameter(description = "New form", name = "form", required = true)
    @RequestBody
    @Valid Form formDocument);

  /**
   * @param formId       logical form name or document id
   * @param oldId
   * @param newId
   * @param formDocument
   * @return
   */
  @Operation(summary = OpenApiDoc.PUT_FORM.PUT_FORM_SUMMARY, description = OpenApiDoc.PUT_FORM.PUT_FORM_OP)
  @PutMapping(path = "{formId}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
  ResponseEntity<FormPutResponse> putForm(
    @PathVariable("formId") @Pattern(regexp = VALID_FORM_ID_PATTERN) @Parameter(description = OpenApiDoc.GENERAL.FORM_ID) String formId,
    @Parameter(description = OpenApiDoc.PUT_FORM.OLD_ID) @RequestParam(name = "oldId", required = false) String oldId,
    @Parameter(description = OpenApiDoc.PUT_FORM.NEW_ID) @RequestParam(name = "newId", required = false) String newId,
    @RequestParam(name = "force", required = false, defaultValue = "false") @Parameter(description = OpenApiDoc.PUT_FORM.FORCED) boolean forced,
    @RequestParam(name = "dryRun", required = false, defaultValue = "false") @Parameter(description = OpenApiDoc.PUT_FORM.DRY_RUN) boolean dryRun,
    @Parameter(description = "New form data", name = "form", required = true) @Validated @NotNull @RequestBody Form formDocument);

  /**
   * @param formId logical form name or document id
   * @return
   */
  @Operation(summary = OpenApiDoc.DELETE_FORM.DELETE_SUMMARY, description = OpenApiDoc.DELETE_FORM.DELETE_OP)
  @DeleteMapping(path = "{formId}")
  ResponseEntity<Response> deleteForm(
    @Parameter(description = OpenApiDoc.GENERAL.FORM_ID)
    @PathVariable("formId") @Pattern(regexp = VALID_FORM_ID_PATTERN) String formId);

  /**
   * @param formId document id or logical form name
   * @param rev    name of tag
   * @return
   */
  @Operation(summary = OpenApiDoc.FORM_ID.GET_FORMID_SUMMARY, description = OpenApiDoc.FORM_ID.GET_FORMID_OP)
  @GetMapping(path = "{formId}", produces = MediaType.APPLICATION_JSON_VALUE)
  ResponseEntity<Form> getForm(
    @Parameter(description = OpenApiDoc.GENERAL.FORM_ID)
    @PathVariable("formId") @Pattern(regexp = VALID_FORM_ID_PATTERN) String formId,
    @Parameter(description = OpenApiDoc.GENERAL.REV)
    @RequestParam(name = "rev", required = false) String rev);

  /**
   * @param formId logical form name or document id
   * @return list of tags
   */
  @Operation(summary = OpenApiDoc.TAG.GET_TAGS_SUMMARY, description = OpenApiDoc.TAG.GET_TAGS_OP)
  @GetMapping(path = "{formId}/tags", produces = MediaType.APPLICATION_JSON_VALUE)
  ResponseEntity<List<FormTag>> getFormTags(
    @Parameter(description = OpenApiDoc.GENERAL.FORM_ID)
    @PathVariable("formId") @Pattern(regexp = VALID_FORM_ID_PATTERN) String formId);

  /**
   * @param formId  logical form name
   * @param tagName
   * @return
   */

  @Operation(summary = OpenApiDoc.TAG.GET_TAG_NAME_SUMMARY, description = OpenApiDoc.TAG.GET_TAG_NAME_OP)
  @GetMapping(path = "{formId}/tags/{tagName}", produces = MediaType.APPLICATION_JSON_VALUE)
  ResponseEntity<FormTag> getFormTag(
    @Parameter(description = OpenApiDoc.GENERAL.FORM_ID)
    @PathVariable("formId") @Pattern(regexp = VALID_FORM_ID_PATTERN) String formId,
    @Parameter(description = OpenApiDoc.GENERAL.TAG_NAME)
    @PathVariable("tagName") String tagName);

  /**
   * @param formId logical form name
   * @param rev
   * @param tag
   * @return
   */
  @Operation(summary = OpenApiDoc.TAG.POST_FORM_TAG_SUMMARY, description = OpenApiDoc.TAG.POST_FORM_TAG_OP)
  @PostMapping(path = "{formId}/tags", produces = MediaType.APPLICATION_JSON_VALUE)
  ResponseEntity<Response> postFormTag(
    @PathVariable("formId") @Pattern(regexp = VALID_FORM_ID_PATTERN) @Parameter(description = OpenApiDoc.GENERAL.FORM_ID) String formId,
    @RequestParam(name = "rev", required = false) @Parameter(description = OpenApiDoc.GENERAL.REV) String rev,
    @RequestParam(name = "snapshot", required = false, defaultValue = "false") @Parameter(description = OpenApiDoc.TAG.SNAPSHOT) boolean snapshot,
    @Parameter(name = "tag", required = true, description = OpenApiDoc.TAG.TAG_OBJ)
    @RequestBody FormTag tag);

  /**
   * @param formId logical form name
   * @param tag
   * @return
   */
  @Operation(summary = OpenApiDoc.TAG.TAG_LATEST_SUMMARY, description = OpenApiDoc.TAG.TAG_LATEST_OP)
  @PutMapping(path = "{formId}/tags/latest", produces = MediaType.APPLICATION_JSON_VALUE)
  ResponseEntity<Response> putFormTagLatest(
    @PathVariable("formId") @Pattern(regexp = VALID_FORM_ID_PATTERN) @Parameter(description = OpenApiDoc.GENERAL.FORM_ID) String formId,
    @Parameter(name = "tag", required = true, description = OpenApiDoc.TAG.TAG_OBJ) @RequestBody FormTag tag);

  /**
   * Update tag. Only mutable tag can be modified. Mutable tags can be set to existing tags only.
   *
   * @param formId    logical form name
   * @param updateTag
   * @return updated tag
   */
  @PutMapping(path = "{formId}/tags/{tagName}", produces = MediaType.APPLICATION_JSON_VALUE)
  ResponseEntity<Response> putFormTag(
    @PathVariable("formId") @Pattern(regexp = VALID_FORM_ID_PATTERN) String formId,
    @PathVariable("tagName") String tagName,
    @Parameter(description = "Updated tag", name = "tag", required = true)
    @RequestBody FormTag updateTag
  );

}
