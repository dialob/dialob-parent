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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.dialob.api.form.Form;
import io.dialob.api.questionnaire.ImmutableQuestionnaireMetadata;
import io.dialob.api.questionnaire.Questionnaire;
import io.dialob.db.spi.spring.DatabaseExceptionMapper;
import io.dialob.form.service.api.FormDatabase;
import io.dialob.questionnaire.csvserializer.CSVSerializer;
import io.dialob.questionnaire.service.api.ImmutableMetadataRow;
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
import org.junit.jupiter.api.extension.ExtendWith;
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

import java.io.IOException;
import java.io.InputStream;
import java.util.function.Consumer;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {DatabaseExceptionMapper.class,
  CSVSerializerTest.TestConfiguration.class,
  QuestionnairesRestServiceController.class,
  DialobRestAutoConfiguration.class})
@EnableWebMvc
@WebAppConfiguration
public class CSVSerializerTest {

  @Configuration(proxyBeanMethods = false)
  public static class TestConfiguration {
    @Bean
    public ObjectMapper objectMapper() {
      return new ObjectMapper()
        .registerModules(new JavaTimeModule())
        .enable(SerializationFeature.INDENT_OUTPUT);
    }

    @Bean
    CSVSerializer csvSerializer(QuestionnaireDatabase questionnaireDatabase, CurrentTenant currentTenant) {
      return new CSVSerializer(questionnaireDatabase, currentTenant);
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
    try {
      return objectMapper.readValue(formInput, Form.class);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private Questionnaire loadQuestionnaire(String questionnaireName) {
    InputStream formInput = this.getClass().getResourceAsStream(questionnaireName);
    try {
      return objectMapper.readValue(formInput, Questionnaire.class);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private QuestionnaireDatabase.MetadataRow getQuestionnaireMetadataRow(Questionnaire questionnaire) {
    return ImmutableMetadataRow.builder().id(questionnaire.getId()).value(ImmutableQuestionnaireMetadata.builder().from(questionnaire.getMetadata()).build()).build();
  }

  @Test
  public void getAllDataByFormId() throws Exception {
    mockMvc.perform(get("/questionnaires?formId="+FORM_ID).accept(MediaType.parseMediaType("text/csv")))
      .andExpect(status().isOk())
      .andExpect(content().contentType(MediaType.parseMediaType("text/csv")))
      .andExpect(content().string("TextInputEN,text1,BooleanInputEN,boolean1\r\n" +
        "TextAnswer,,Yes,true\r\n" +
        "Something,,No,false\r\n"));
  }

  @Test
  public void getAllDataByFormIdLanguage() throws Exception {
    mockMvc.perform(get("/questionnaires?formId="+FORM_ID+"&language=fi").accept(MediaType.parseMediaType("text/csv")))
      .andExpect(status().isOk())
      .andExpect(content().contentType(MediaType.parseMediaType("text/csv")))
      .andExpect(content().string("TextInputFI,text1,BooleanInputFI,boolean1\r\n" +
        "TextAnswer,,Kyllä,true\r\n" +
        "Something,,Ei,false\r\n"));
  }

  @Test
  public void getAllDataByFormIdBooleanLanguageFallback() throws Exception {
    mockMvc.perform(get("/questionnaires?formId="+FORM_ID+"&language=sv").accept(MediaType.parseMediaType("text/csv")))
      .andExpect(status().isOk())
      .andExpect(content().contentType(MediaType.parseMediaType("text/csv")))
      .andExpect(content().string(",text1,1. null,boolean1\r\n" +
        "TextAnswer,,Yes,true\r\n" +
        "Something,,No,false\r\n"));
  }

  @Test
  public void getByQuestionnaireId() throws Exception {
    mockMvc.perform(get("/questionnaires?questionnaire=1").accept(MediaType.parseMediaType("text/csv")))
      .andExpect(status().isOk())
      .andExpect(content().contentType(MediaType.parseMediaType("text/csv")))
      .andExpect(content().string("TextInputEN,text1,BooleanInputEN,boolean1\r\n" +
        "TextAnswer,,Yes,true\r\n"));
  }

  @Test
  public void getByQuestionnaireId2() throws Exception {
    mockMvc.perform(get("/questionnaires?questionnaire=715d10726ca9d9348e2d29eff33267bc").accept(MediaType.parseMediaType("text/csv")))
      .andExpect(status().isOk())
      .andExpect(content().contentType(MediaType.parseMediaType("text/csv")));
  }

  @Test
  public void getByQuestionnaireIdInvalidId() throws Exception {
    mockMvc.perform(get("/questionnaires?questionnaire=715d10726ca9d9348e2d29eff33267bc,unacceptable-id").accept(MediaType.parseMediaType("text/csv")))
      .andExpect(status().isBadRequest());
  }

  @Test
  public void getByQuestionnaireIdWrongForm() throws Exception {
    mockMvc.perform(get("/questionnaires?questionnaire=1,3").accept(MediaType.parseMediaType("text/csv")))
      .andExpect(status().is4xxClientError());
  }

  @Test
  public void getAllDataByFormIdFilterFrom() throws Exception {
    mockMvc.perform(get("/questionnaires?formId="+FORM_ID+"&from=2020-10-03T07:04:00").accept(MediaType.parseMediaType("text/csv")))
      .andExpect(status().isOk())
      .andExpect(content().contentType(MediaType.parseMediaType("text/csv")))
      .andExpect(content().string("TextInputEN,text1,BooleanInputEN,boolean1\r\n" +
        "Something,,No,false\r\n"));
  }

  @Test
  public void getAllDataByFormIdFilterTo() throws Exception {
    mockMvc.perform(get("/questionnaires?formId="+FORM_ID+"&to=2020-10-03T07:04:00").accept(MediaType.parseMediaType("text/csv")))
      .andExpect(status().isOk())
      .andExpect(content().contentType(MediaType.parseMediaType("text/csv")))
      .andExpect(content().string("TextInputEN,text1,BooleanInputEN,boolean1\r\n" +
        "TextAnswer,,Yes,true\r\n"));
  }

  @Test
  public void failNoMatch() throws Exception {
    mockMvc.perform(get("/questionnaires?formId="+FORM_ID+"&from=2021-10-03T07:04:00").accept(MediaType.parseMediaType("text/csv")))
      .andExpect(status().isNotFound());
  }

  @Test
  public void failWithoutCriteria() throws Exception {
    mockMvc.perform(get("/questionnaires").accept(MediaType.parseMediaType("text/csv")))
      .andExpect(status().is4xxClientError());
  }

  @Test
  public void getAllDataByFormIdSurvey() throws Exception {
    mockMvc.perform(get("/questionnaires?formId="+SURVEY_FORM_ID).accept(MediaType.parseMediaType("text/csv")))
      .andExpect(status().isOk())
      .andExpect(content().contentType(MediaType.parseMediaType("text/csv")))
      .andExpect(content().string("Survey Q 1,survey1,Survey Q 2,survey2,Survey Q 3,survey3\r\n" +
        "Choice1,a,Choice2,b,,\r\n"));
  }

}
