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

import io.dialob.api.form.FormTag;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;


@RequestMapping(value = "${dialob.api.context-path:}/tags")
@OpenAPIDefinition
public interface FormTagsRestService {

  @Operation(summary = OpenApiDoc.TAG.TAG_SUMMARY, description = OpenApiDoc.TAG.TAG_OP)


  @GetMapping(produces = {MediaType.APPLICATION_JSON_VALUE}) ResponseEntity <List<FormTag>> getTags(
    @Parameter(description = OpenApiDoc.GENERAL.FORM_NAME) @RequestParam(name = "formName", required = false) String formName,
    @Parameter(description = OpenApiDoc.GENERAL.FORM_ID) @RequestParam(name = "formId", required = false) String formId,
    @Parameter(description = OpenApiDoc.GENERAL.TAG_NAME)  @RequestParam(name = "name", required = false) String name
  );

}
