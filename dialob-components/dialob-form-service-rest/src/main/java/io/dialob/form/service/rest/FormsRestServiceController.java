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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.dialob.api.form.*;
import io.dialob.api.rest.ImmutableResponse;
import io.dialob.api.rest.Response;
import io.dialob.db.spi.exceptions.DocumentNotFoundException;
import io.dialob.form.service.api.FormDatabase;
import io.dialob.form.service.api.FormVersionControlDatabase;
import io.dialob.form.service.api.repository.FormListItem;
import io.dialob.form.service.api.validation.FormIdRenamer;
import io.dialob.form.service.api.validation.FormItemCopier;
import io.dialob.integration.api.NodeId;
import io.dialob.integration.api.event.ImmutableFormDeletedEvent;
import io.dialob.integration.api.event.ImmutableFormTaggedEvent;
import io.dialob.integration.api.event.ImmutableFormUpdatedEvent;
import io.dialob.security.tenant.CurrentTenant;
import io.dialob.security.tenant.Tenant;
import io.dialob.security.user.CurrentUserProvider;
import io.dialob.session.engine.program.FormValidatorExecutor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
@Slf4j
public class FormsRestServiceController implements FormsRestService {

  public static final String TEMPLATE_FORM_ID = "00000000000000000000000000000000";

  private static final ResponseEntity<Response> OK = ResponseEntity.ok(ImmutableResponse.builder().ok(true).build());
  public static final ResponseEntity<Response> NOT_MODIFIED_RESPONSE = ResponseEntity.status(HttpStatus.NOT_MODIFIED).body(ImmutableResponse.builder().ok(false).build());

  private static ResponseEntity<Response> ok() {
    return OK;
  }

  private Form templateForm;

  private final ApplicationEventPublisher eventPublisher;

  private final FormDatabase formDatabase;

  private final Optional<FormVersionControlDatabase> formVersionControlDatabase;

  private final FormValidatorExecutor validator;

  private final FormIdRenamer renamer;

  private final ObjectMapper objectMapper;

  private final NodeId nodeId;

  private final FormItemCopier formItemCopier;

  private final CurrentTenant currentTenant;

  private final CurrentUserProvider currentUserProvider;

  public FormsRestServiceController(ApplicationEventPublisher eventPublisher,
                                    FormDatabase formDatabase,
                                    Optional<FormVersionControlDatabase> formVersionControlDatabase,
                                    FormValidatorExecutor validator,
                                    FormIdRenamer renamer,
                                    ObjectMapper objectMapper,
                                    NodeId nodeId,
                                    FormItemCopier formItemCopier,
                                    CurrentTenant currentTenant,
                                    CurrentUserProvider currentUserProvider)
  {
    this.eventPublisher = eventPublisher;
    this.formDatabase = formDatabase;
    this.formVersionControlDatabase = formVersionControlDatabase;
    this.validator = validator;
    this.renamer = renamer;
    this.objectMapper = objectMapper;
    this.nodeId = nodeId;
    this.formItemCopier = formItemCopier;
    this.currentTenant = currentTenant;
    this.currentUserProvider = currentUserProvider;
  }

  @Override
  public ResponseEntity<List<FormListItem>> getForms(String metadata) {
    LOGGER.debug("getForms");
    List<FormListItem> result = new ArrayList<>();
    Form.Metadata formMetadata = null;
    if (StringUtils.isNotBlank(metadata)) {
      try {
        formMetadata = objectMapper.readValue(metadata, Form.Metadata.class);
      } catch (JsonProcessingException e) {
        throw new InvalidMetadataQueryException(e);
      }
    }
    formDatabase.findAllMetadata(currentTenant.getId(), formMetadata, row -> result.add(new FormListItem(row.getId(), row.getValue())));
    return ResponseEntity.ok(result);
  }

  @Override
  public ResponseEntity<FormPutResponse> itemCopy(String itemId, Form form) {
    if (form == null) {
      return ResponseEntity.badRequest().body(null);
    }
    Pair<Form, List<FormValidationError>> resultPair = formItemCopier.copyFormItem(form, itemId);
    ImmutableFormPutResponse.Builder putResponseBuilder = ImmutableFormPutResponse.builder()
      .id(form.getId())
      .rev(form.getRev()).ok(true);
    if (!resultPair.getRight().isEmpty()) {
      resultPair.getRight().forEach(putResponseBuilder::addErrors);
    }
    putResponseBuilder.form(resultPair.getLeft());

    // Response is still OK even if there are rule building errors as the document is still saved.
    return ResponseEntity.ok(putResponseBuilder.build());
  }

  @Override
  public ResponseEntity<Form> postForm(Form formDocument) {
    Form form = updateMetadata(formDocument);
    form = ImmutableForm.builder().from(form).id(null).rev(null).build();
    Form savedForm = formDatabase.save(currentTenant.getId(), form);
    URI uri = ServletUriComponentsBuilder
      .fromCurrentRequest().path("/{id}")
      .buildAndExpand(savedForm.getId()).toUri();
    return ResponseEntity.created(uri).body(savedForm);
  }

  @Override
  public ResponseEntity<FormPutResponse> putForm(String formId,
                                                 String oldId,
                                                 String newId,
                                                 boolean forced,
                                                 boolean dryRun,
                                                 final Form formBody) {
    //
    if (isTemplateFormId(formId)) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
    }
    Form form = updateMetadata(formBody);


    if (formId.equals(formBody.getName())) {
      String formName = formId;
      formId = formVersionControlDatabase.flatMap(formVersionControlDatabase1 -> formVersionControlDatabase1
        .findTag(currentTenant.getId(), formName, "LATEST")
        .map(FormTag::getFormId))
        .orElseThrow(() -> new DocumentNotFoundException("No form document \"" + formName + "\""));
    }

    // form._id and request must match
    if (forced) {
      Form existingForm = formDatabase.findOne(currentTenant.getId(), formId);
      form = ImmutableForm.builder()
        .from(form)
        .id(formId)
        .rev(existingForm.getRev())
        .build();
    } else if (!formId.equals(formBody.getId())) {
      return ResponseEntity
        .badRequest()
        .body(ImmutableFormPutResponse.builder().ok(false).error("INCONSISTENT_ID").reason("_id does not match with resource " + formId).build());
    }
    final String prevRev = form.getRev();

    boolean includeForm = false;
    List<FormValidationError> errors = new ArrayList<>();

    if (StringUtils.isNotBlank(oldId) && StringUtils.isNotBlank(newId)) {
      Pair<Form, List<FormValidationError>> resultPair = renamer.renameIdentifiers(form, oldId, newId);
      form = resultPair.getLeft();
      errors.addAll(resultPair.getRight());
      includeForm = true;
    }

    errors.addAll(validator.validate(form));

    Form updatedForm;
    if (!dryRun) {
      updatedForm = formDatabase.save(currentTenant.getId(), ImmutableForm.builder().from(form).metadata(ImmutableFormMetadata.builder().from(form.getMetadata()).valid(errors.isEmpty()).build()).build());
      eventPublisher.publishEvent(ImmutableFormUpdatedEvent.builder().source(getNodeId().getId()).tenant(Tenant.of(updatedForm.getMetadata().getTenantId())).formId(formId).revision(updatedForm.getRev()).build());
    } else {
      updatedForm = form;
    }
    ImmutableFormPutResponse.Builder putResponse = ImmutableFormPutResponse.builder().id(updatedForm.getId()).rev(updatedForm.getRev());
    if (!errors.isEmpty()) {
      putResponse.ok(false);
      errors.forEach(putResponse::addErrors);
    } else {
      putResponse.ok(true);
    }
    if (includeForm) {
      putResponse.form(updatedForm);
    }

    // Response is still OK even if there are rule building errors as the document is still saved.
    return ResponseEntity.ok(putResponse.build());
  }

  private Form updateMetadata(Form form) {
    Date now = Date.from(Instant.now());
    final ImmutableFormMetadata.Builder builder = ImmutableFormMetadata.builder().from(form.getMetadata());
    builder.lastSaved(now);
    builder.tenantId(currentTenant.getId());
    String userId = currentUserProvider.getUserId();
    builder.savedBy(userId);
    if (form.getRev() == null) {
      builder.created(now);
      builder.creator(userId);
    }
    return ImmutableForm.builder().from(form).metadata(builder.build()).build();
  }

  @Override
  public ResponseEntity<Response> deleteForm(String formId) {
    if (isTemplateFormId(formId)) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null); // Or Response.Status.METHOD_NOT_ALLOWED ??
    }
    formDatabase.delete(currentTenant.getId(), formId);
    eventPublisher.publishEvent(ImmutableFormDeletedEvent.builder().source(getNodeId().getId()).tenant(currentTenant.get()).formId(formId).build());
    return ok();
  }

  @Override
  public ResponseEntity<Form> getForm(String formId, String rev) {
    if (isTemplateFormId(formId)) {
      return ResponseEntity.ok(getTemplateForm());
    }
    final Form formDocument = formDatabase.findOne(currentTenant.getId(), formId, rev);
    return ResponseEntity.ok(formDocument);
  }

  @Override
  public ResponseEntity<List<FormTag>> getFormTags(String formId) {
    return formVersionControlDatabase
      .map(versionControlDatabase -> ResponseEntity.ok(versionControlDatabase.findTags(currentTenant.getId(), formId, null)))
      .orElse(ResponseEntity.notFound().build());
  }

  @Override
  public ResponseEntity<FormTag> getFormTag(String formId, String tagName) {
    return formVersionControlDatabase
      .flatMap(versionControlDatabase -> versionControlDatabase.findTag(currentTenant.getId(), formId, tagName).map(ResponseEntity::ok))
      .orElse(ResponseEntity.notFound().build());
  }

  @Override
  public ResponseEntity<Response> putFormTagLatest(String formId, FormTag tag) {
    return formVersionControlDatabase.map(versionControlDatabase -> {
      if (versionControlDatabase.updateLatest(currentTenant.getId(), formId, tag)) {
        return fireFormTaggedEvent(Optional.of(ImmutableFormTag.builder().from(tag).formId(StringUtils.defaultString(formId)).build()));
      }
      return NOT_MODIFIED_RESPONSE;
    }).orElse(ResponseEntity.notFound().build());
  }

  @Override
  public ResponseEntity<Response> postFormTag(String formId, String rev, boolean snapshot, FormTag tag) {
    return formVersionControlDatabase.map(versionControlDatabase -> {
      String formName = tag.getFormName();
      String tagName = tag.getName();
      String formDocumentId = formId;
      if (versionControlDatabase.isName(currentTenant.getId(), formDocumentId)) {
        formDocumentId = tag.getFormId();
      }
      Optional<FormTag> formTag;
      if (formDocumentId == null) {
        formTag = versionControlDatabase.createTagOnLatest(currentTenant.getId(), formName, tagName, tag.getDescription(), snapshot);
      } else {
        formTag = versionControlDatabase.createTag(currentTenant.getId(), formName, tagName, tag.getDescription(), formDocumentId, tag.getType());
      }
      return fireFormTaggedEvent(formTag);
    }).orElse(ResponseEntity.notFound().build());
  }

  @Override
  public ResponseEntity<Response> putFormTag(String formId, String tagName, FormTag requestTag) {
    if (StringUtils.isBlank(requestTag.getRefName())) {
      return ResponseEntity.badRequest().body(ImmutableFormPutResponse.builder().ok(false).error("INCOMPLETE").reason("ref_name is required field").build());

    }
    return formVersionControlDatabase.map(versionControlDatabase -> {
      String formName = formId;
      if (!versionControlDatabase.isName(currentTenant.getId(), formName)) {
        Form form = versionControlDatabase.getFormDatabase().findOne(currentTenant.getId(), formId);
        formName = form.getName();
      }
      FormTag updateTag = ImmutableFormTag.builder().from(requestTag).formName(formName).name(tagName).build();
      Optional<FormTag> formTag = versionControlDatabase.moveTag(currentTenant.getId(), updateTag);
      return fireFormTaggedEvent(formTag);
    }).orElse(ResponseEntity.notFound().build());
  }

  protected ResponseEntity<Response> fireFormTaggedEvent(Optional<FormTag> formTag) {
    return formTag.map(newTag -> {
      eventPublisher.publishEvent(ImmutableFormTaggedEvent.builder()
        .tenant(currentTenant.get())
        .source(getNodeId().getId())
        .formName(newTag.getFormName())
        .tagName(newTag.getName())
        .formId(newTag.getFormId())
        .refName(newTag.getRefName())
        .build());
      return OK;
    }).orElse(NOT_MODIFIED_RESPONSE);
  }

  protected boolean isTemplateFormId(String formId) {
    return TEMPLATE_FORM_ID.equals(formId);
  }

  protected Form getTemplateForm() {
    if (templateForm != null) {
      return templateForm;
    }
    try {
      InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("forms/" + TEMPLATE_FORM_ID + ".json");
      templateForm = objectMapper.readValue(inputStream, Form.class);
    } catch (IOException e) {
      LOGGER.error("Couldn't read template form.", e);
    }
    return templateForm;
  }

  private NodeId getNodeId() {
    return nodeId;
  }

}
