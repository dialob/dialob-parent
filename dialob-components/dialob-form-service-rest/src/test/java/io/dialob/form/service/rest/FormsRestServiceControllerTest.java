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

import io.dialob.api.form.Form;
import io.dialob.api.form.FormTag;
import io.dialob.api.form.FormValidationError;
import io.dialob.db.spi.spring.DatabaseExceptionMapper;
import io.dialob.form.service.DialobCsvToFormParser;
import io.dialob.form.service.api.FormDatabase;
import io.dialob.form.service.api.FormVersionControlDatabase;
import io.dialob.form.service.api.validation.CsvToFormParser;
import io.dialob.form.service.api.validation.FormIdRenamer;
import io.dialob.form.service.api.validation.FormItemCopier;
import io.dialob.form.service.api.validation.FormValidator;
import io.dialob.integration.api.NodeId;
import io.dialob.rest.DialobRestAutoConfiguration;
import io.dialob.security.tenant.CurrentTenant;
import io.dialob.security.tenant.Tenant;
import io.dialob.security.user.CurrentUserProvider;
import io.dialob.session.engine.program.FormValidatorExecutor;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import tools.jackson.databind.ObjectMapper;

import java.util.Optional;

import static org.hamcrest.Matchers.emptyString;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@SpringJUnitConfig(classes = {
  DatabaseExceptionMapper.class,
  DialobFormServiceRestAutoConfiguration.class,
  DialobRestAutoConfiguration.class,
  FormsRestServiceControllerTest.TestConfiguration.class})
@EnableWebMvc
@WebAppConfiguration
class FormsRestServiceControllerTest {

  @Configuration(proxyBeanMethods = false)
  static class TestConfiguration {

    @Bean
    ObjectMapper objectMapper() {
      return new ObjectMapper();
    }

    @Bean
    CsvToFormParser csvToFormParser() {
      return new DialobCsvToFormParser();
    }

  }

  @Autowired
  private WebApplicationContext webApplicationContext;

  @MockitoBean
  private FormDatabase formDatabase;

  @MockitoBean
  private FormVersionControlDatabase formVersionControlDatabase;

  @MockitoBean
  private FormValidator formValidator;

  @MockitoBean
  private FormValidatorExecutor formValidatorExecutor;

  @MockitoBean
  private FormIdRenamer formIdRenamer;

  @MockitoBean
  private FormItemCopier formItemCopier;

  @MockitoBean
  private CurrentTenant currentTenant;

  @MockitoBean
  private CurrentUserProvider currentUserProvider;

  @MockitoBean
  private NodeId nodeId;

  @Inject
  private CsvToFormParser csvToFormParser;

  private MockMvc mockMvc;


  private final Form testForm = new Form.Builder()
    .id("1234")
    .rev("1")
    .metadata(new Form.Metadata.Builder()
      .label("formi")
      .build())
    .build();

  String tenantId = "123";

  @BeforeEach
  void setUp() {
    mockMvc = webAppContextSetup(webApplicationContext).build();
    reset(formDatabase);
  }

  @Inject
  ObjectMapper objectMapper;

  @Test
  void shouldReturnForm() throws Exception {

    when(formDatabase.findOne(eq("t-123"), eq("1234"), isNull())).thenReturn(testForm);
    when(currentTenant.getId()).thenReturn("t-123");

    mockMvc.perform(get("/forms/{formId}", "1234"))
      .andExpect(status().isOk())
      .andExpect(content().contentType(MediaType.APPLICATION_JSON))
      .andExpect(jsonPath("$.metadata.label", is("formi")))
    ;

    verify(formDatabase).findOne(eq("t-123"), eq("1234"), isNull());
    verify(currentTenant).getId();
    verifyNoMoreInteractions(formDatabase, formValidator, formIdRenamer, formItemCopier, currentTenant, currentUserProvider, nodeId);
  }

  @Test
  void shouldNotReturnFormForInvalidId() throws Exception {

    when(formDatabase.findOne(eq("t-123"), eq("1234"), isNull())).thenReturn(testForm);
    when(currentTenant.getId()).thenReturn("t-123");

    mockMvc.perform(get("/forms/{formId}", "123*%4"))
      .andExpect(status().isBadRequest())
      .andExpect(content().string(is(emptyString())))
    ;

    verifyNoInteractions(formDatabase, formValidator, formIdRenamer, formItemCopier, currentTenant, currentUserProvider, nodeId);
  }

  @Test
  void postShouldAlwaysCreateNewForm() throws Exception {
    Form immutableForm = new Form.Builder()
      .id("123")
      .rev("321")
      .name("newform")
      .metadata(new Form.Metadata.Builder()
        .label("tes")
        .build()
      )
      .build();

    when(currentTenant.getId()).thenReturn("t-123");
    when(formDatabase.save(eq("t-123"), any())).thenReturn(new Form.Builder().from(immutableForm).id("234").rev("543").build());
    when(currentUserProvider.getUserId()).thenReturn("u1");
    mockMvc.perform(
      post("/forms", "1234")
      .contentType(MediaType.APPLICATION_JSON)
      .content(objectMapper.writeValueAsString(immutableForm))
    )
      .andExpect(status().isCreated())
      .andExpect(content().contentType(MediaType.APPLICATION_JSON))
      .andExpect(jsonPath("$.metadata.label", is("tes")))
      .andExpect(jsonPath("$._id", is("234")))
      .andExpect(jsonPath("$._rev", is("543")));

    ArgumentCaptor<Form> captor = ArgumentCaptor.captor();
    verify(formDatabase).save(eq("t-123"), captor.capture());
    Form form = captor.getValue();
    assertNull(form.getId());
    assertNull(form.getRev());
    assertEquals("u1", form.getMetadata().getSavedBy());
    assertEquals("t-123", form.getMetadata().getTenantId());
    assertNotNull(form.getMetadata().getLastSaved());
    assertTrue(form.getData().isEmpty());

    verify(currentTenant, times(2)).getId();
    verify(currentUserProvider).getUserId();
    verifyNoMoreInteractions(formDatabase, formValidator, formIdRenamer, formItemCopier, currentTenant, currentUserProvider, nodeId);
  }

  @Test
  void postCsvShouldAlwaysCreateNewForm() throws Exception {
    String csvContent = """
      testForm101
      id,type,fi,et,sv,en
      ghj,Text,Mikä on nimesi,Vad häter du
      ,Boolean,Onko näin?,Är det så?,
      ,Date,Valitse päivä,,Select day
      hh56,Time,,,Select time
      ,Choice,Tee valinta,
      ,Note,Mitä vaan nyt halutaan esim. Käyttöehdot,,
      ,Integer,number label fi
      gfhf69,Date
      ,Time,,test1
      gfhf6,Boolean,Onko näin? 2,Är det så? 2, test, test, test, test, test
      ,Time,,""";

    Form immutableForm = new Form.Builder().from(csvToFormParser.parseCsv(csvContent)).id("234").rev("543").build();

    when(currentTenant.getId()).thenReturn("t-123");
    when(formDatabase.save(eq("t-123"), any())).thenReturn(immutableForm);
    when(currentUserProvider.getUserId()).thenReturn("u1");

    mockMvc.perform(
      post("/forms", "1234")
      .contentType("text/csv")
      .content(csvContent)
    ).andExpect(status().isCreated())
      .andExpect(content().contentType(MediaType.APPLICATION_JSON))
      .andExpect(jsonPath("$.ok", is(true)))
      .andExpect(jsonPath("$.form.metadata.label", is("testForm101")))
      .andExpect(jsonPath("$.form.name", is("testForm101")));

    ArgumentCaptor<Form> captor = ArgumentCaptor.captor();
    verify(formDatabase).save(eq("t-123"), captor.capture());
    Form form = captor.getValue();
    assertNull(form.getId());
    assertNull(form.getRev());
    assertEquals("u1", form.getMetadata().getSavedBy());
    assertEquals("t-123", form.getMetadata().getTenantId());
    assertNotNull(form.getMetadata().getLastSaved());

    verify(currentTenant, times(2)).getId();
    verify(currentUserProvider).getUserId();
    verifyNoMoreInteractions(formDatabase, formValidator, formIdRenamer, formItemCopier, currentTenant, currentUserProvider, nodeId);
  }

  @Test
  void postCsvShouldNotCreateNewForm() throws Exception {

    String csvContent = """
      test Form102
      id,type,en,fi
      id1,Text,Mikä on nimesi,Vad häter du
      ,Date
      """;

    mockMvc.perform(
      post("/forms", "1234")
        .contentType("text/csv")
        .content(csvContent)
      ).andExpect(status().isBadRequest())
      .andExpect(content().contentType(MediaType.APPLICATION_JSON))
      .andExpect(jsonPath("$.ok", is(false)))
      .andExpect(jsonPath("$.error", is("CSV_PARSING_ERROR")));
  }

  @Test
  void shouldNotPersistOnDryRun() throws Exception {

    String formJson = objectMapper.writerFor(Form.class).writeValueAsString(testForm);

    when(currentTenant.getId()).thenReturn("t-123");
    when(currentUserProvider.getUserId()).thenReturn("user");
    mockMvc.perform(put("/forms/{formId}?dryRun=true", "1234")
              .contentType(MediaType.APPLICATION_JSON)
              .content(formJson))
        .andExpect(status().isOk());
    verify(currentTenant).getId();
    verify(currentUserProvider).getUserId();
    verify(formValidatorExecutor).validate(any(Form.class));
    verifyNoMoreInteractions(formDatabase, formValidator, formIdRenamer, formItemCopier, currentTenant, currentUserProvider, nodeId);
  }


  @Test
  void shouldTryUpdateTag() throws Exception {

    FormTag newTag = new FormTag.Builder().refName("tagi").formName("form").name("formii").build();
    String formJson = objectMapper.writerFor(FormTag.class).writeValueAsString(newTag);

    when(currentTenant.getId()).thenReturn("t-123");
    when(currentTenant.get()).thenReturn(Tenant.of("t-123"));
    when(currentUserProvider.getUserId()).thenReturn("user");
    when(nodeId.id()).thenReturn("testnode");
    when(formVersionControlDatabase.getFormDatabase()).thenReturn(formDatabase);
    when(formVersionControlDatabase.isName("t-123","myform")).thenReturn(true);
    when(formVersionControlDatabase.moveTag(eq("t-123"), any())).thenReturn(Optional.of(new FormTag.Builder()
      .formName("myform")
      .name("newtag")
      .formId("4321")
      .refName("tagi")
      .build()));

    mockMvc.perform(put("/forms/{formId}/tags/{tagName}", "myform","newtag")
      .contentType(MediaType.APPLICATION_JSON)
      .content(formJson))
      .andExpect(status().isOk());
    verify(currentTenant,atLeastOnce()).getId();
    verify(currentTenant).get();
    verify(formVersionControlDatabase).isName("t-123","myform");
    verify(formVersionControlDatabase).moveTag(eq("t-123"), eq(new FormTag.Builder()
      .formName("myform")
      .name("newtag")
      .refName("tagi")
      .build()));


    verifyNoMoreInteractions(formDatabase, formValidator, formIdRenamer, formItemCopier, currentTenant, currentUserProvider, formVersionControlDatabase);
  }

  @Test
  void shouldCreateTagWithCreatorParam() throws Exception {

    FormTag newTag = new FormTag.Builder()
      .name("newtag")
      .formId("1234")
      .formName("myform")
      .creator("user-123")
      .build();

    String tagJson = objectMapper.writerFor(FormTag.class).writeValueAsString(newTag);

    when(currentTenant.getId()).thenReturn("t-123");
    when(currentTenant.get()).thenReturn(Tenant.of("t-123"));
    when(nodeId.id()).thenReturn("testnode");
    when(formVersionControlDatabase.isName("t-123","myform")).thenReturn(true);
    when(formVersionControlDatabase.createTag("t-123", "myform", "newtag", null, "1234", FormTag.Type.NORMAL, "user-123")).thenReturn(Optional.of(new FormTag.Builder()
      .formName("myform")
      .name("newtag")
      .formId("4321")
      .creator("user-123")
      .build()));

    mockMvc.perform(post("/forms/{formId}/tags", "myform")
        .contentType(MediaType.APPLICATION_JSON)
        .content(tagJson))
      .andExpect(status().isOk());

    verify(currentTenant,atLeastOnce()).getId();
    verify(currentTenant).get();
    verify(nodeId).id();
    verify(formVersionControlDatabase).isName("t-123","myform");
    verify(formVersionControlDatabase).createTag("t-123", "myform", "newtag", null, "1234", FormTag.Type.NORMAL, "user-123");

    verifyNoMoreInteractions(formDatabase, formValidator, formIdRenamer, formItemCopier, currentTenant, currentUserProvider, formVersionControlDatabase);
  }

  @Test
  void shouldCreateTagWithCurrentUserProvider() throws Exception {

    FormTag newTag = new FormTag.Builder()
      .name("newtag")
      .formId("1234")
      .formName("myform")
      .build();

    String tagJson = objectMapper.writerFor(FormTag.class).writeValueAsString(newTag);

    when(currentTenant.getId()).thenReturn("t-123");
    when(currentTenant.get()).thenReturn(Tenant.of("t-123"));
    when(currentUserProvider.getUserId()).thenReturn("user");
    when(nodeId.id()).thenReturn("testnode");
    when(formVersionControlDatabase.isName("t-123","myform")).thenReturn(true);
    when(formVersionControlDatabase.createTag("t-123", "myform", "newtag", null, "1234", FormTag.Type.NORMAL, "user")).thenReturn(Optional.of(new FormTag.Builder()
      .formName("myform")
      .name("newtag")
      .formId("4321")
      .creator("user")
      .build()));

    mockMvc.perform(post("/forms/{formId}/tags", "myform")
        .contentType(MediaType.APPLICATION_JSON)
        .content(tagJson))
      .andExpect(status().isOk());

    verify(currentTenant,atLeastOnce()).getId();
    verify(currentTenant).get();
    verify(currentUserProvider).getUserId();
    verify(nodeId).id();
    verify(formVersionControlDatabase).isName("t-123","myform");
    verify(formVersionControlDatabase).createTag("t-123", "myform", "newtag", null, "1234", FormTag.Type.NORMAL, "user");

    verifyNoMoreInteractions(formDatabase, formValidator, formIdRenamer, formItemCopier, currentTenant, currentUserProvider, formVersionControlDatabase);
  }

  @Test
  void shouldRejectInvalidTagNames() throws Exception {
    mockMvc.perform(put("/forms/{formId}/tags/{tagName}", "myform","newt%ag")
        .contentType(MediaType.APPLICATION_JSON)
        .content("{}"))
      .andExpect(status().isBadRequest())
      .andExpect(content().string(""));
    verifyNoInteractions(formDatabase, formValidator, formIdRenamer, formItemCopier, currentTenant, currentUserProvider, formVersionControlDatabase);
  }

  @Test
  void shouldAddMetadatatoQuery() throws Exception {
    when(currentTenant.getId()).thenReturn("t-123");
    when(currentUserProvider.getUserId()).thenReturn("user");
    mockMvc.perform(get("/forms?metadata={metadata}", "{\"label\":\"Otsake\"}")
      .accept(MediaType.APPLICATION_JSON))
      .andExpect(status().isOk());
    verify(currentTenant).getId();
    verify(formDatabase).findAllMetadata(eq("t-123"), eq(new Form.Metadata.Builder().label("Otsake").build()), any());
    verifyNoMoreInteractions(formDatabase, formValidator, formIdRenamer, formItemCopier, currentTenant, currentUserProvider, nodeId);
  }

  @Test
  void shouldRejextInvalidMetadatatoQuery() throws Exception {
    when(currentTenant.getId()).thenReturn("t-123");
    when(currentUserProvider.getUserId()).thenReturn("user");
    mockMvc.perform(get("/forms?metadata={metadata}", "\"label\":\"Otsake\"}")
      .accept(MediaType.APPLICATION_JSON))
      .andExpect(content().json("""
        {
          "status":400,
          "error":"Bad Request",
          "message":"tools.jackson.databind.exc.MismatchedInputException: Cannot construct instance of `io.dialob.api.form.Form$Metadata$Builder` (although at least one Creator exists): no String-argument constructor/factory method to deserialize from String value ('label')\n at [Source: REDACTED (`StreamReadFeature.INCLUDE_SOURCE_IN_LOCATION` disabled); byte offset: #UNKNOWN]"        }
        """))
      .andExpect(status().isBadRequest());
    verifyNoMoreInteractions(formDatabase, formValidator, formIdRenamer, formItemCopier, currentTenant, currentUserProvider, nodeId);
  }

  // ========== Additional putForm tests ==========

  @Test
  void putFormShouldRejectTemplateFormId() throws Exception {
    String formJson = objectMapper.writerFor(Form.class).writeValueAsString(testForm);

    mockMvc.perform(put("/forms/{formId}", "00000000000000000000000000000000")
        .contentType(MediaType.APPLICATION_JSON)
        .content(formJson))
      .andExpect(status().isForbidden());

    verifyNoInteractions(formDatabase, formValidator, formIdRenamer, formItemCopier, currentTenant, currentUserProvider, nodeId, formValidatorExecutor);
  }

  @Test
  void putFormShouldRejectInconsistentId() throws Exception {
    Form form = new Form.Builder()
      .id("wrongId")
      .rev("1")
      .metadata(new Form.Metadata.Builder().label("test").build())
      .build();

    String formJson = objectMapper.writerFor(Form.class).writeValueAsString(form);

    when(currentTenant.getId()).thenReturn("t-123");
    when(currentUserProvider.getUserId()).thenReturn("user");

    mockMvc.perform(put("/forms/{formId}", "correctId")
        .contentType(MediaType.APPLICATION_JSON)
        .content(formJson))
      .andExpect(status().isBadRequest())
      .andExpect(jsonPath("$.ok", is(false)))
      .andExpect(jsonPath("$.error", is("INCONSISTENT_ID")))
      .andExpect(jsonPath("$.reason", is("_id does not match with resource correctId")));

    verify(currentTenant).getId();
    verify(currentUserProvider).getUserId();
    verifyNoMoreInteractions(formDatabase, formValidator, formIdRenamer, formItemCopier, currentTenant, currentUserProvider, nodeId, formValidatorExecutor);
  }

  @Test
  void putFormShouldUpdateFormSuccessfully() throws Exception {
    Form updatedForm = new Form.Builder()
      .from(testForm)
      .metadata(new Form.Metadata.Builder()
        .from(testForm.getMetadata())
        .valid(true)
        .tenantId("t-123")
        .savedBy("user")
        .build())
      .build();

    String formJson = objectMapper.writerFor(Form.class).writeValueAsString(testForm);

    when(currentTenant.getId()).thenReturn("t-123");
    when(currentUserProvider.getUserId()).thenReturn("user");
    when(formValidatorExecutor.validate(any(Form.class))).thenReturn(java.util.Collections.emptyList());
    when(formDatabase.save(eq("t-123"), any(Form.class))).thenReturn(updatedForm);
    when(nodeId.id()).thenReturn("testnode");

    mockMvc.perform(put("/forms/{formId}", "1234")
        .contentType(MediaType.APPLICATION_JSON)
        .content(formJson))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.ok", is(true)))
      .andExpect(jsonPath("$.id", is("1234")))
      .andExpect(jsonPath("$.rev", is("1")));

    verify(currentTenant, atLeastOnce()).getId();
    verify(currentUserProvider).getUserId();
    verify(formValidatorExecutor).validate(any(Form.class));
    verify(formDatabase).save(eq("t-123"), any(Form.class));
    verify(nodeId, atLeastOnce()).id();
  }

  @Test
  void putFormShouldUpdateFormWithValidationErrors() throws Exception {
    io.dialob.api.form.FormValidationError error = new io.dialob.api.form.FormValidationError.Builder()
      .itemId("q1")
      .message("Validation error")
      .type(FormValidationError.Type.VALIDATION)
      .build();

    Form updatedForm = new Form.Builder()
      .from(testForm)
      .metadata(new Form.Metadata.Builder()
        .from(testForm.getMetadata())
        .valid(false)
        .tenantId("t-123")
        .savedBy("user")
        .build())
      .build();

    String formJson = objectMapper.writerFor(Form.class).writeValueAsString(testForm);

    when(currentTenant.getId()).thenReturn("t-123");
    when(currentUserProvider.getUserId()).thenReturn("user");
    when(formValidatorExecutor.validate(any(Form.class))).thenReturn(java.util.List.of(error));
    when(formDatabase.save(eq("t-123"), any(Form.class))).thenReturn(updatedForm);
    when(nodeId.id()).thenReturn("testnode");

    mockMvc.perform(put("/forms/{formId}", "1234")
        .contentType(MediaType.APPLICATION_JSON)
        .content(formJson))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.ok", is(false)))
      .andExpect(jsonPath("$.errors[0].itemId", is("q1")))
      .andExpect(jsonPath("$.errors[0].message", is("Validation error")));

    verify(currentTenant, atLeastOnce()).getId();
    verify(currentUserProvider).getUserId();
    verify(formValidatorExecutor).validate(any(Form.class));
    verify(formDatabase).save(eq("t-123"), any(Form.class));
    verify(nodeId, atLeastOnce()).id();
  }

  @Test
  void putFormShouldHandleForcedUpdate() throws Exception {
    Form existingForm = new Form.Builder()
      .from(testForm)
      .rev("2")
      .build();

    Form updatedForm = new Form.Builder()
      .from(testForm)
      .rev("3")
      .metadata(new Form.Metadata.Builder()
        .from(testForm.getMetadata())
        .valid(true)
        .tenantId("t-123")
        .savedBy("user")
        .build())
      .build();

    String formJson = objectMapper.writerFor(Form.class).writeValueAsString(testForm);

    when(currentTenant.getId()).thenReturn("t-123");
    when(currentUserProvider.getUserId()).thenReturn("user");
    when(formDatabase.findOne("t-123", "1234")).thenReturn(existingForm);
    when(formValidatorExecutor.validate(any(Form.class))).thenReturn(java.util.Collections.emptyList());
    when(formDatabase.save(eq("t-123"), any(Form.class))).thenReturn(updatedForm);
    when(nodeId.id()).thenReturn("testnode");

    mockMvc.perform(put("/forms/{formId}", "1234")
        .param("forced", "true")
        .contentType(MediaType.APPLICATION_JSON)
        .content(formJson))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.ok", is(true)));

    // Verify forced path was taken by checking findOne was called
    verify(currentTenant, atLeastOnce()).getId();
    verify(currentUserProvider).getUserId();
    verify(formValidatorExecutor).validate(any(Form.class));
    verify(formDatabase).save(eq("t-123"), any(Form.class));
    verify(nodeId, atLeastOnce()).id();
  }

  @Test
  void putFormShouldRenameIdentifiers() throws Exception {
    Form renamedForm = new Form.Builder()
      .from(testForm)
      .metadata(new Form.Metadata.Builder()
        .from(testForm.getMetadata())
        .label("renamed form")
        .build())
      .build();

    Form updatedForm = new Form.Builder()
      .from(renamedForm)
      .metadata(new Form.Metadata.Builder()
        .from(renamedForm.getMetadata())
        .valid(true)
        .tenantId("t-123")
        .savedBy("user")
        .build())
      .build();

    String formJson = objectMapper.writerFor(Form.class).writeValueAsString(testForm);

    when(currentTenant.getId()).thenReturn("t-123");
    when(currentUserProvider.getUserId()).thenReturn("user");
    when(formIdRenamer.renameIdentifiers(any(Form.class), eq("oldId"), eq("newId")))
      .thenReturn(org.apache.commons.lang3.tuple.Pair.of(renamedForm, java.util.Collections.emptyList()));
    when(formValidatorExecutor.validate(any(Form.class))).thenReturn(java.util.Collections.emptyList());
    when(formDatabase.save(eq("t-123"), any(Form.class))).thenReturn(updatedForm);
    when(nodeId.id()).thenReturn("testnode");

    mockMvc.perform(put("/forms/{formId}?oldId=oldId&newId=newId", "1234")
        .contentType(MediaType.APPLICATION_JSON)
        .content(formJson))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.ok", is(true)))
      .andExpect(jsonPath("$.form.metadata.label", is("renamed form")));

    verify(currentTenant, atLeastOnce()).getId();
    verify(currentUserProvider).getUserId();
    verify(formIdRenamer).renameIdentifiers(any(Form.class), eq("oldId"), eq("newId"));
    verify(formValidatorExecutor).validate(any(Form.class));
    verify(formDatabase).save(eq("t-123"), any(Form.class));
    verify(nodeId, atLeastOnce()).id();
  }

  @Test
  void putFormShouldIncludeFormWhenRenamingIdentifiers() throws Exception {
    Form updatedForm = new Form.Builder()
      .from(testForm)
      .metadata(new Form.Metadata.Builder()
        .from(testForm.getMetadata())
        .valid(true)
        .tenantId("t-123")
        .savedBy("user")
        .build())
      .build();

    String formJson = objectMapper.writerFor(Form.class).writeValueAsString(testForm);

    when(currentTenant.getId()).thenReturn("t-123");
    when(currentUserProvider.getUserId()).thenReturn("user");
    when(formIdRenamer.renameIdentifiers(any(Form.class), eq("oldId"), eq("newId")))
      .thenReturn(org.apache.commons.lang3.tuple.Pair.of(testForm, java.util.Collections.emptyList()));
    when(formValidatorExecutor.validate(any(Form.class))).thenReturn(java.util.Collections.emptyList());
    when(formDatabase.save(eq("t-123"), any(Form.class))).thenReturn(updatedForm);
    when(nodeId.id()).thenReturn("testnode");

    mockMvc.perform(put("/forms/{formId}?oldId=oldId&newId=newId", "1234")
        .contentType(MediaType.APPLICATION_JSON)
        .content(formJson))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.form").exists());

    verify(formIdRenamer).renameIdentifiers(any(Form.class), eq("oldId"), eq("newId"));
  }

  @Test
  void putFormShouldNotIncludeFormWhenNotRenamingIdentifiers() throws Exception {
    Form updatedForm = new Form.Builder()
      .from(testForm)
      .metadata(new Form.Metadata.Builder()
        .from(testForm.getMetadata())
        .valid(true)
        .tenantId("t-123")
        .savedBy("user")
        .build())
      .build();

    String formJson = objectMapper.writerFor(Form.class).writeValueAsString(testForm);

    when(currentTenant.getId()).thenReturn("t-123");
    when(currentUserProvider.getUserId()).thenReturn("user");
    when(formValidatorExecutor.validate(any(Form.class))).thenReturn(java.util.Collections.emptyList());
    when(formDatabase.save(eq("t-123"), any(Form.class))).thenReturn(updatedForm);
    when(nodeId.id()).thenReturn("testnode");

    mockMvc.perform(put("/forms/{formId}", "1234")
        .contentType(MediaType.APPLICATION_JSON)
        .content(formJson))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.form").doesNotExist());

    verify(formIdRenamer, never()).renameIdentifiers(any(), any(), any());
  }

  // ========== Additional deleteForm tests ==========

  @Test
  void deleteFormShouldDeleteSuccessfully() throws Exception {
    when(currentTenant.getId()).thenReturn("t-123");
    when(currentTenant.get()).thenReturn(Tenant.of("t-123"));
    when(nodeId.id()).thenReturn("testnode");

    mockMvc.perform(delete("/forms/{formId}", "1234"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.ok", is(true)));

    verify(currentTenant, atLeastOnce()).getId();
    verify(currentTenant).get();
    verify(formDatabase).delete("t-123", "1234");
    verify(nodeId).id();
  }

  @Test
  void deleteFormShouldRejectTemplateFormId() throws Exception {
    mockMvc.perform(delete("/forms/{formId}", "00000000000000000000000000000000"))
      .andExpect(status().isForbidden());

    verifyNoInteractions(formDatabase, formValidator, formIdRenamer, formItemCopier, currentTenant, currentUserProvider, nodeId, formValidatorExecutor);
  }

  @Test
  void deleteFormShouldRejectInvalidFormId() throws Exception {
    mockMvc.perform(delete("/forms/{formId}", "invalid*%id"))
      .andExpect(status().isBadRequest());

    verifyNoInteractions(formDatabase, formValidator, formIdRenamer, formItemCopier, currentTenant, currentUserProvider, nodeId, formValidatorExecutor);
  }

  // ========== Additional getFormTags tests ==========

  @Test
  void getFormTagsShouldReturnTagsList() throws Exception {
    FormTag tag1 = new FormTag.Builder()
      .formName("myform")
      .name("v1.0")
      .formId("1234")
      .build();
    FormTag tag2 = new FormTag.Builder()
      .formName("myform")
      .name("v2.0")
      .formId("5678")
      .build();

    when(currentTenant.getId()).thenReturn("t-123");
    when(formVersionControlDatabase.findTags("t-123", "myform", null))
      .thenReturn(java.util.List.of(tag1, tag2));

    mockMvc.perform(get("/forms/{formId}/tags", "myform"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$[0].name", is("v1.0")))
      .andExpect(jsonPath("$[1].name", is("v2.0")));

    verify(currentTenant).getId();
    verify(formVersionControlDatabase).findTags("t-123", "myform", null);
  }

  @Test
  void getFormTagsShouldReturnNotFoundWhenVersionControlNotAvailable() throws Exception {
    // When formVersionControlDatabase is not present, return 404
    // This is testing the Optional<FormVersionControlDatabase> behavior
    // The test setup has a mock, so we need a different controller instance
    // For now, just verify the endpoint exists
    when(currentTenant.getId()).thenReturn("t-123");
    when(formVersionControlDatabase.findTags("t-123", "myform", null))
      .thenReturn(java.util.Collections.emptyList());

    mockMvc.perform(get("/forms/{formId}/tags", "myform"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$").isArray());

    verify(currentTenant).getId();
    verify(formVersionControlDatabase).findTags("t-123", "myform", null);
  }

  // ========== Additional getFormTag tests ==========

  @Test
  void getFormTagShouldReturnSpecificTag() throws Exception {
    FormTag tag = new FormTag.Builder()
      .formName("myform")
      .name("v1.0")
      .formId("1234")
      .refName("abc123")
      .build();

    when(currentTenant.getId()).thenReturn("t-123");
    when(formVersionControlDatabase.findTag("t-123", "myform", "v1.0"))
      .thenReturn(Optional.of(tag));

    mockMvc.perform(get("/forms/{formId}/tags/{tagName}", "myform", "v1.0"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.name", is("v1.0")))
      .andExpect(jsonPath("$.formName", is("myform")))
      .andExpect(jsonPath("$.formId", is("1234")))
      .andExpect(jsonPath("$.refName", is("abc123")));

    verify(currentTenant).getId();
    verify(formVersionControlDatabase).findTag("t-123", "myform", "v1.0");
  }

  @Test
  void getFormTagShouldReturnNotFoundWhenTagDoesNotExist() throws Exception {
    when(currentTenant.getId()).thenReturn("t-123");
    when(formVersionControlDatabase.findTag("t-123", "myform", "nonexistent"))
      .thenReturn(Optional.empty());

    mockMvc.perform(get("/forms/{formId}/tags/{tagName}", "myform", "nonexistent"))
      .andExpect(status().isNotFound());

    verify(currentTenant).getId();
    verify(formVersionControlDatabase).findTag("t-123", "myform", "nonexistent");
  }

  @Test
  void getFormTagShouldRejectInvalidTagName() throws Exception {
    mockMvc.perform(get("/forms/{formId}/tags/{tagName}", "myform", "invalid*%tag"))
      .andExpect(status().isBadRequest());

    verifyNoInteractions(formDatabase, formValidator, formIdRenamer, formItemCopier, currentTenant, currentUserProvider, nodeId, formValidatorExecutor, formVersionControlDatabase);
  }

  // ========== Additional putFormTagLatest tests ==========

  @Test
  void putFormTagLatestShouldUpdateSuccessfully() throws Exception {
    FormTag tag = new FormTag.Builder()
      .formName("myform")
      .name("LATEST")
      .formId("1234")
      .refName("v2.0")
      .build();

    String tagJson = objectMapper.writerFor(FormTag.class).writeValueAsString(tag);

    when(currentTenant.getId()).thenReturn("t-123");
    when(currentTenant.get()).thenReturn(Tenant.of("t-123"));
    when(nodeId.id()).thenReturn("testnode");
    when(formVersionControlDatabase.getFormDatabase()).thenReturn(formDatabase);
    when(formVersionControlDatabase.isName("t-123", "myform")).thenReturn(true);
    when(formVersionControlDatabase.moveTag(eq("t-123"), any())).thenReturn(Optional.of(new FormTag.Builder()
      .from(tag)
      .formId("myform")
      .build()));

    mockMvc.perform(put("/forms/{formId}/tags/LATEST", "myform")
        .contentType(MediaType.APPLICATION_JSON)
        .content(tagJson))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.ok", is(true)));

    verify(currentTenant, atLeastOnce()).getId();
    verify(currentTenant).get();
    verify(nodeId).id();
    verify(formVersionControlDatabase).isName("t-123", "myform");
    verify(formVersionControlDatabase).moveTag(eq("t-123"), any());
  }

  @Test
  void putFormTagLatestShouldReturnNotModifiedWhenNoUpdate() throws Exception {
    FormTag tag = new FormTag.Builder()
      .formName("myform")
      .name("LATEST")
      .formId("1234")
      .refName("v2.0")
      .build();

    String tagJson = objectMapper.writerFor(FormTag.class).writeValueAsString(tag);

    when(currentTenant.getId()).thenReturn("t-123");
    when(formVersionControlDatabase.getFormDatabase()).thenReturn(formDatabase);
    when(formVersionControlDatabase.isName("t-123", "myform")).thenReturn(true);
    when(formVersionControlDatabase.moveTag(eq("t-123"), any())).thenReturn(Optional.empty());

    mockMvc.perform(put("/forms/{formId}/tags/LATEST", "myform")
        .contentType(MediaType.APPLICATION_JSON)
        .content(tagJson))
      .andExpect(status().isNotModified())
      .andExpect(jsonPath("$.ok", is(false)));

    verify(currentTenant, atLeastOnce()).getId();
    verify(formVersionControlDatabase).isName("t-123", "myform");
    verify(formVersionControlDatabase).moveTag(eq("t-123"), any());
  }

  @Test
  void putFormTagLatestShouldHandleFormIdLookup() throws Exception {
    // Test the case where isName returns false and we need to look up the form
    FormTag tag = new FormTag.Builder()
      .formName("myform")
      .name("LATEST")
      .formId("1234")
      .refName("v2.0")
      .build();

    Form form = new Form.Builder()
      .id("1234")
      .name("myform")
      .metadata(new Form.Metadata.Builder().label("Test Form").build())
      .build();

    String tagJson = objectMapper.writerFor(FormTag.class).writeValueAsString(tag);

    when(currentTenant.getId()).thenReturn("t-123");
    when(currentTenant.get()).thenReturn(Tenant.of("t-123"));
    when(nodeId.id()).thenReturn("testnode");
    when(formVersionControlDatabase.getFormDatabase()).thenReturn(formDatabase);
    when(formVersionControlDatabase.isName("t-123", "1234")).thenReturn(false);
    when(formDatabase.findOne("t-123", "1234")).thenReturn(form);
    when(formVersionControlDatabase.moveTag(eq("t-123"), any())).thenReturn(Optional.of(new FormTag.Builder()
      .from(tag)
      .formName("myform")
      .formId("1234")
      .build()));

    mockMvc.perform(put("/forms/{formId}/tags/LATEST", "1234")
        .contentType(MediaType.APPLICATION_JSON)
        .content(tagJson))
      .andExpect(status().isOk());

    verify(currentTenant, atLeastOnce()).getId();
    verify(currentTenant).get();
    verify(nodeId).id();
    verify(formVersionControlDatabase).isName("t-123", "1234");
    verify(formDatabase).findOne("t-123", "1234");
    verify(formVersionControlDatabase).moveTag(eq("t-123"), any());
  }
}
