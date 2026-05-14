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

import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.SerializationFeature;
import io.dialob.api.form.Form;
import io.dialob.api.questionnaire.Questionnaire;
import io.dialob.db.spi.spring.DatabaseExceptionMapper;
import io.dialob.form.service.api.FormDatabase;
import io.dialob.questionnaire.csvserializer.CSVSerializer;
import io.dialob.questionnaire.printout.DialobPrintoutWriter;
import io.dialob.questionnaire.service.api.QuestionnaireDatabase;
import io.dialob.questionnaire.service.api.session.QuestionnaireSessionBuilderFactory;
import io.dialob.questionnaire.service.api.session.QuestionnaireSessionSaveService;
import io.dialob.questionnaire.service.api.session.QuestionnaireSessionService;
import io.dialob.rest.DialobRestAutoConfiguration;
import io.dialob.security.tenant.CurrentTenant;
import io.dialob.security.user.CurrentUserProvider;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.io.InputStream;
import java.util.function.Consumer;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@SpringJUnitConfig(classes = {DatabaseExceptionMapper.class,
  CSVSerializerTest.TestConfiguration.class,
  QuestionnairesRestServiceController.class,
  DialobRestAutoConfiguration.class})
@EnableWebMvc
@WebAppConfiguration
class CSVSerializerTest {

  public static final MediaType MEDIA_TYPE_CSV = MediaType.parseMediaType("text/csv");

  @Configuration(proxyBeanMethods = false)
  public static class TestConfiguration {
    @Bean
    public ObjectMapper objectMapper() {
      return new ObjectMapper()
        .rebuild().configure(SerializationFeature.INDENT_OUTPUT, true).build();
    }

    @Bean
    CSVSerializer csvSerializer(QuestionnaireDatabase questionnaireDatabase, CurrentTenant currentTenant) {
      return new CSVSerializer(questionnaireDatabase, currentTenant);
    }

    @Bean
    DialobPrintoutWriter dialobPrintoutWriter() {
      return new DialobPrintoutWriter();
    }

  }

  @Inject
  private ObjectMapper objectMapper;

  @Inject
  private WebApplicationContext webApplicationContext;

  @MockitoBean
  private FormDatabase formDatabase;

  @MockitoBean
  private CurrentTenant currentTenant;

  @MockitoBean
  private QuestionnaireDatabase questionnaireDatabase;

  @MockitoBean
  private QuestionnaireSessionSaveService questionnaireSessionSaveService;

  @MockitoBean
  private QuestionnaireSessionBuilderFactory questionnaireSessionBuilderFactory;

  @MockitoBean
  private QuestionnaireSessionService questionnaireSessionService;

  @MockitoBean
  private CurrentUserProvider currentUserProvider;

  private MockMvc mockMvc;

  private static final String FORM_ID = "2aa63bb2e7769b856cb487ccf3c50f2f";
  private static final String SURVEY_FORM_ID = "d8745b7dff6dfb1ba3c1af9ac7e2aba8";

  @BeforeEach
  public void setUp() {
    Questionnaire q1 = loadQuestionnaire("/csvTestQuestionnaire1.json");
    Questionnaire q2 = loadQuestionnaire("/csvTestQuestionnaire2.json");
    Questionnaire q3 = loadQuestionnaire("/csvTestQuestionnaire3.json");
    Questionnaire q4 = loadQuestionnaire("/csvTestQuestionnaire4.json");
    Questionnaire qSurvey = loadQuestionnaire("/csvTestQuestionnaireSurvey.json");

    mockMvc = webAppContextSetup(webApplicationContext).build();
    reset(currentTenant, formDatabase, questionnaireDatabase);
    when(currentTenant.getId()).thenReturn("t-123");

    when(formDatabase.findOne("t-123", FORM_ID)).thenReturn(loadForm("/csvTestForm.json"));
    doAnswer(invocation -> {
      Consumer<QuestionnaireDatabase.MetadataRow> consumer = (Consumer<QuestionnaireDatabase.MetadataRow>) invocation.getArguments()[6];
      consumer.accept(getQuestionnaireMetadataRow(q1));
      consumer.accept(getQuestionnaireMetadataRow(q2));
      return null;
    }).when(questionnaireDatabase).findAllMetadata(eq("t-123"), isNull(), eq(FORM_ID), isNull(), isNull(), eq(Questionnaire.Metadata.Status.COMPLETED), any(Consumer.class));

    when(formDatabase.findOne("t-123", "2d6298231cde7d107b3f015a43d6b8d8")).thenReturn(loadForm("/csvTestForm2.json"));

    when(formDatabase.findOne("t-123", SURVEY_FORM_ID)).thenReturn(loadForm("/csvTestFormSurvey.json"));
    doAnswer(invocation -> {
      Consumer<QuestionnaireDatabase.MetadataRow> consumer = (Consumer<QuestionnaireDatabase.MetadataRow>) invocation.getArguments()[6];
      consumer.accept(getQuestionnaireMetadataRow(qSurvey));
      return null;
    }).when(questionnaireDatabase).findAllMetadata(eq("t-123"), isNull(), eq(SURVEY_FORM_ID), isNull(), isNull(), eq(Questionnaire.Metadata.Status.COMPLETED), any(Consumer.class));

    when(questionnaireDatabase.findMetadata("t-123", "1")).thenReturn(getQuestionnaireMetadataRow(q1));
    when(questionnaireDatabase.findMetadata("t-123", "2")).thenReturn(getQuestionnaireMetadataRow(q2));
    when(questionnaireDatabase.findMetadata("t-123", "3")).thenReturn(getQuestionnaireMetadataRow(q3));
    when(questionnaireDatabase.findMetadata("t-123", "715d10726ca9d9348e2d29eff33267bc")).thenReturn(getQuestionnaireMetadataRow(q4));
    when(questionnaireDatabase.findMetadata("t-123", "survey")).thenReturn(getQuestionnaireMetadataRow(qSurvey));

    when(questionnaireDatabase.findOne("t-123", "1")).thenReturn(q1);
    when(questionnaireDatabase.findOne("t-123", "2")).thenReturn(q2);
    when(questionnaireDatabase.findOne("t-123", "3")).thenReturn(q3);
    when(questionnaireDatabase.findOne("t-123", "715d10726ca9d9348e2d29eff33267bc")).thenReturn(q4);
    when(questionnaireDatabase.findOne("t-123", "survey")).thenReturn(qSurvey);
  }

  private Form loadForm(String formName) {
    InputStream formInput = this.getClass().getResourceAsStream(formName);
    return objectMapper.readValue(formInput, Form.class);
  }

  private Questionnaire loadQuestionnaire(String questionnaireName) {
    InputStream formInput = this.getClass().getResourceAsStream(questionnaireName);
    return objectMapper.readValue(formInput, Questionnaire.class);
  }

  private QuestionnaireDatabase.MetadataRow getQuestionnaireMetadataRow(Questionnaire questionnaire) {
    return new QuestionnaireDatabase.MetadataRow.Builder().id(questionnaire.getId()).value(new Questionnaire.Metadata.Builder().from(questionnaire.getMetadata()).build()).build();
  }

  @Test
  void getAllDataByFormId() throws Exception {
    mockMvc.perform(get("/questionnaires?formId="+FORM_ID).accept(MEDIA_TYPE_CSV))
      .andExpect(status().isOk())
      .andExpect(content().contentType(MEDIA_TYPE_CSV))
      .andExpect(content().string("""
        BooleanInputEN,boolean1,TextInputEN,text1\r
        Yes,true,TextAnswer,\r
        No,false,Something,\r
        """));
  }

  @Test
  void getAllDataByFormIdLanguage() throws Exception {
    mockMvc.perform(get("/questionnaires?formId="+FORM_ID+"&language=fi").accept(MEDIA_TYPE_CSV))
      .andExpect(status().isOk())
      .andExpect(content().contentType(MEDIA_TYPE_CSV))
      .andExpect(content().string("""
        BooleanInputFI,boolean1,TextInputFI,text1\r
        Kyllä,true,TextAnswer,\r
        Ei,false,Something,\r
        """));
  }

  @Test
  void getAllDataByFormIdBooleanLanguageFallback() throws Exception {
    mockMvc.perform(get("/questionnaires?formId="+FORM_ID+"&language=ug").accept(MEDIA_TYPE_CSV))
      .andExpect(status().isOk())
      .andExpect(content().contentType(MEDIA_TYPE_CSV))
      .andExpect(content().string("""
        ,boolean1,1. null,text1\r
        Yes,true,TextAnswer,\r
        No,false,Something,\r
        """));
  }

  @Test
  void getByQuestionnaireId() throws Exception {
    mockMvc.perform(get("/questionnaires?questionnaire=1").accept(MEDIA_TYPE_CSV))
      .andExpect(status().isOk())
      .andExpect(content().contentType(MEDIA_TYPE_CSV))
      .andExpect(content().string("""
        BooleanInputEN,boolean1,TextInputEN,text1\r
        Yes,true,TextAnswer,\r
        """));
  }

  @Test
  void getByQuestionnaireId2() throws Exception {
    mockMvc.perform(get("/questionnaires?questionnaire=715d10726ca9d9348e2d29eff33267bc").accept(MEDIA_TYPE_CSV))
      .andExpect(status().isOk())
      .andExpect(content().contentType(MEDIA_TYPE_CSV));
  }

  @Test
  void getByQuestionnaireIdInvalidId() throws Exception {
    mockMvc.perform(get("/questionnaires?questionnaire=715d10726ca9d9348e2d29eff33267bc,unacceptable-id").accept(MEDIA_TYPE_CSV))
      .andExpect(status().isBadRequest());
  }

  @Test
  void getByQuestionnaireIdWrongForm() throws Exception {
    mockMvc.perform(get("/questionnaires?questionnaire=1,3").accept(MEDIA_TYPE_CSV))
      .andExpect(status().is4xxClientError());
  }

  @Test
  void getAllDataByFormIdFilterFrom() throws Exception {
    mockMvc.perform(get("/questionnaires?formId="+FORM_ID+"&from=2020-10-03T07:04:00").accept(MEDIA_TYPE_CSV))
      .andExpect(status().isOk())
      .andExpect(content().contentType(MEDIA_TYPE_CSV))
      .andExpect(content().string("""
        BooleanInputEN,boolean1,TextInputEN,text1\r
        No,false,Something,\r
        """));
  }

  @Test
  void getAllDataByFormIdFilterTo() throws Exception {
    mockMvc.perform(get("/questionnaires?formId="+FORM_ID+"&to=2020-10-03T07:04:00").accept(MEDIA_TYPE_CSV))
      .andExpect(status().isOk())
      .andExpect(content().contentType(MEDIA_TYPE_CSV))
      .andExpect(content().string("""
        BooleanInputEN,boolean1,TextInputEN,text1\r
        Yes,true,TextAnswer,\r
        """));
  }

  @Test
  void failNoMatch() throws Exception {
    mockMvc.perform(get("/questionnaires?formId="+FORM_ID+"&from=2021-10-03T07:04:00").accept(MEDIA_TYPE_CSV))
      .andExpect(status().isNotFound());
  }

  @Test
  void failWithoutCriteria() throws Exception {
    mockMvc.perform(get("/questionnaires").accept(MEDIA_TYPE_CSV))
      .andExpect(status().is4xxClientError());
  }

  @Test
  void getAllDataByFormIdSurvey() throws Exception {
    mockMvc.perform(get("/questionnaires").param("formId", SURVEY_FORM_ID).accept(MEDIA_TYPE_CSV))
      .andExpect(status().isOk())
      .andExpect(content().contentType(MEDIA_TYPE_CSV))
      .andExpect(content().string("""
        Survey Q 1,survey1,Survey Q 2,survey2,Survey Q 3,survey3\r
        Choice1,a,Choice2,b,,\r
        """));
  }

}
