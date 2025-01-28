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

import com.fasterxml.jackson.databind.ObjectMapper;
import io.dialob.api.form.*;
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
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.util.Optional;

import static org.hamcrest.Matchers.emptyString;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {
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


  private final Form testForm = ImmutableForm.builder()
    .id("1234")
    .rev("1")
    .metadata(ImmutableFormMetadata.builder()
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
    ImmutableForm immutableForm = ImmutableForm.builder()
      .id("123")
      .rev("321")
      .name("newform")
      .metadata(ImmutableFormMetadata.builder()
        .label("tes")
        .build()
      )
      .build();

    when(currentTenant.getId()).thenReturn("t-123");
    when(formDatabase.save(eq("t-123"), any())).thenReturn(ImmutableForm.builder().from(immutableForm).id("234").rev("543").build());
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
    StringBuilder csvBuilder = new StringBuilder();
    csvBuilder
      .append("testForm101\n")
      .append("id,type,fi,et,sv,en\n")
      .append("ghj,Text,Mikä on nimesi,Vad häter du\n")
      .append(",Boolean,Onko näin?,Är det så?,\n")
      .append(",Date,Valitse päivä,,Select day\n")
      .append("hh56,Time,,,Select time\n")
      .append(",Choice,Tee valinta,\n")
      .append(",Note,Mitä vaan nyt halutaan esim. Käyttöehdot,,\n")
      .append(",Integer,number label fi\n")
      .append("gfhf69,Date\n")
      .append(",Time,,test1\n")
      .append("gfhf6,Boolean,Onko näin? 2,Är det så? 2, test, test, test, test, test\n")
      .append(",Time,,");
    String csvContent = csvBuilder.toString();

    ImmutableForm immutableForm = ImmutableForm.builder().from(csvToFormParser.parseCsv(csvContent)).id("234").rev("543").build();

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
    StringBuilder csvBuilder = new StringBuilder();
    csvBuilder
      .append("test Form102\n")
      .append("id,type,en,fi\n")
      .append("id1,Text,Mikä on nimesi,Vad häter du\n")
      .append(",Date\n");

    String csvContent = csvBuilder.toString();

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

    FormTag newTag = ImmutableFormTag.builder().refName("tagi").build();
    String formJson = objectMapper.writerFor(FormTag.class).writeValueAsString(newTag);

    when(currentTenant.getId()).thenReturn("t-123");
    when(currentTenant.get()).thenReturn(Tenant.of("t-123"));
    when(currentUserProvider.getUserId()).thenReturn("user");
    when(nodeId.getId()).thenReturn("testnode");
    when(formVersionControlDatabase.getFormDatabase()).thenReturn(formDatabase);
    when(formVersionControlDatabase.isName("t-123","myform")).thenReturn(true);
    when(formVersionControlDatabase.moveTag(eq("t-123"), any())).thenReturn(Optional.of(ImmutableFormTag.builder()
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
    verify(formVersionControlDatabase).moveTag(eq("t-123"), eq(ImmutableFormTag.builder()
      .formName("myform")
      .name("newtag")
      .refName("tagi")
      .build()));


    verifyNoMoreInteractions(formDatabase, formValidator, formIdRenamer, formItemCopier, currentTenant, currentUserProvider, formVersionControlDatabase);
  }

  @Test
  void shouldCreateTagWithCreatorParam() throws Exception {

    FormTag newTag = ImmutableFormTag.builder()
      .name("newtag")
      .formId("1234")
      .formName("myform")
      .creator("user-123")
      .build();

    String tagJson = objectMapper.writerFor(FormTag.class).writeValueAsString(newTag);

    when(currentTenant.getId()).thenReturn("t-123");
    when(currentTenant.get()).thenReturn(Tenant.of("t-123"));
    when(nodeId.getId()).thenReturn("testnode");
    when(formVersionControlDatabase.isName("t-123","myform")).thenReturn(true);
    when(formVersionControlDatabase.createTag("t-123", "myform", "newtag", null, "1234", FormTag.Type.NORMAL, "user-123")).thenReturn(Optional.of(ImmutableFormTag.builder()
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
    verify(nodeId).getId();
    verify(formVersionControlDatabase).isName("t-123","myform");
    verify(formVersionControlDatabase).createTag("t-123", "myform", "newtag", null, "1234", FormTag.Type.NORMAL, "user-123");

    verifyNoMoreInteractions(formDatabase, formValidator, formIdRenamer, formItemCopier, currentTenant, currentUserProvider, formVersionControlDatabase);
  }

  @Test
  void shouldCreateTagWithCurrentUserProvider() throws Exception {

    FormTag newTag = ImmutableFormTag.builder()
      .name("newtag")
      .formId("1234")
      .formName("myform")
      .build();

    String tagJson = objectMapper.writerFor(FormTag.class).writeValueAsString(newTag);

    when(currentTenant.getId()).thenReturn("t-123");
    when(currentTenant.get()).thenReturn(Tenant.of("t-123"));
    when(currentUserProvider.getUserId()).thenReturn("user");
    when(nodeId.getId()).thenReturn("testnode");
    when(formVersionControlDatabase.isName("t-123","myform")).thenReturn(true);
    when(formVersionControlDatabase.createTag("t-123", "myform", "newtag", null, "1234", FormTag.Type.NORMAL, "user")).thenReturn(Optional.of(ImmutableFormTag.builder()
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
    verify(nodeId).getId();
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
    verify(formDatabase).findAllMetadata(eq("t-123"), eq(ImmutableFormMetadata.builder().label("Otsake").build()), any());
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
          "message":"com.fasterxml.jackson.databind.exc.MismatchedInputException: Cannot construct instance of `io.dialob.api.form.ImmutableFormMetadata$Json` (although at least one Creator exists): no String-argument constructor/factory method to deserialize from String value ('label')\\n at [Source: REDACTED (`StreamReadFeature.INCLUDE_SOURCE_IN_LOCATION` disabled); line: 1, column: 1]"
        }
        """))
      .andExpect(status().isBadRequest());
    verifyNoMoreInteractions(formDatabase, formValidator, formIdRenamer, formItemCopier, currentTenant, currentUserProvider, nodeId);
  }
}
