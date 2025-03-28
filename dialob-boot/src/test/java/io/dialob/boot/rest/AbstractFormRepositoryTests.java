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
package io.dialob.boot.rest;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.umd.cs.findbugs.annotations.NonNull;
import io.dialob.api.form.Form;
import io.dialob.api.form.FormItem;
import io.dialob.api.form.ImmutableForm;
import io.dialob.api.form.ImmutableFormItem;
import io.dialob.api.proto.Action;
import io.dialob.api.proto.ActionsFactory;
import io.dialob.api.proto.ImmutableAction;
import io.dialob.api.questionnaire.ContextValue;
import io.dialob.api.questionnaire.ImmutableQuestionnaire;
import io.dialob.api.questionnaire.ImmutableQuestionnaireMetadata;
import io.dialob.api.questionnaire.Questionnaire;
import io.dialob.api.rest.IdAndRevision;
import io.dialob.common.Constants;
import io.dialob.form.service.api.FormDatabase;
import io.dialob.questionnaire.service.api.QuestionnaireDatabase;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.SerializationUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.AopTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.*;
import java.util.function.Consumer;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
abstract class AbstractFormRepositoryTests {

  static final NoExceptionResponseErrorHandler NO_EXCEPTION_RESPONSE_ERROR_HANDLER = new NoExceptionResponseErrorHandler();

  MockMvc mockMvc;

  String tenantId = "00000000-0000-0000-0000-000000000000";

  static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

  MultiValueMap<String, String> tenantParam = new LinkedMultiValueMap<>();

  AbstractFormRepositoryTests() {
    tenantParam.add("tenantId","00000000-0000-0000-0000-000000000000");
  }

  static class TestConfiguration {
    @Bean
    @Primary
    public QuestionnaireDatabase questionnaireDatabase() {
      QuestionnaireDatabase questionnaireDatabase = Mockito.mock(QuestionnaireDatabase.class);

      defineInMemoryPeristence(questionnaireDatabase);
      return questionnaireDatabase;
    }
  }

  @Inject
  QuestionnaireDatabase questionnaireDatabase;

  @MockitoBean
  FormDatabase formDatabase;

  String getContextPath() {
    Assertions.fail("add getContextPath method");
    return null;
  }

  URI uri(String... paths) {
    return UriComponentsBuilder.newInstance()
      .scheme("http")
      .host("localhost")
      .path(getContextPath())
      .pathSegment(paths)
      .build().toUri();
  }

  static void defineInMemoryPeristence(QuestionnaireDatabase questionnaireDatabase) {
    HashMap<String, Questionnaire> database = new HashMap<>();
    questionnaireDatabase = AopTestUtils.getTargetObject(questionnaireDatabase);

    doAnswer(invocation -> database.get(invocation.getArgument(1))).when(questionnaireDatabase).findOne(any(String.class), any(String.class));
    doAnswer(invocation -> {
      ImmutableQuestionnaire document = invocation.getArgument(1);
      if (document.getId() == null) {
        document = document.withId(UUID.randomUUID().toString());
        document = document.withRev("1-" + document.getId());
      } else {
        String rev = document.getRev();
        String revNumber = rev.substring(0, rev.indexOf('-'));
        document = document.withId((Integer.parseInt(revNumber) + 1) + "-" + document.getId());
      }
      database.put(document.getId(), SerializationUtils.clone(document));
      return document;
    }).when(questionnaireDatabase).save(any(String.class), any());
  }


  @BeforeEach
  void resetMocks() {
    if (Mockito.mockingDetails(questionnaireDatabase).isMock()) {
      Mockito.reset(questionnaireDatabase);
      defineInMemoryPeristence(questionnaireDatabase);

    }
    if (Mockito.mockingDetails(formDatabase).isMock()) {
      Mockito.reset(formDatabase);
    }
  }

  Form shouldFindForm(Form form) {
    when(formDatabase.findOne(eq(tenantId), eq(form.getId()))).thenReturn(form);
    when(formDatabase.findOne(eq(tenantId), eq(form.getId()), any())).thenReturn(form);
    when(formDatabase.exists(eq(tenantId), eq(form.getId()))).thenReturn(true);
    return form;
  }

  Questionnaire createQuestionnaireDocument(String questionnaireId, String questionnaireRev, Form formDocument) {
    return createQuestionnaireDocument(questionnaireId, questionnaireRev, formDocument.getId(), formDocument.getRev());
  }

  Questionnaire createQuestionnaireDocument(String questionnaireId, String questionnaireRev, Form formDocument, List<ContextValue> context) {
    return createQuestionnaireDocument(questionnaireId, questionnaireRev, formDocument.getId(), formDocument.getRev(), context);
  }

  Questionnaire createQuestionnaireDocument(String questionnaireId, String questionnaireRev, String formId, String formRev) {
    return createQuestionnaireDocument(questionnaireId, questionnaireRev, formId, formRev, null);
  }

  Questionnaire createQuestionnaireDocument(String questionnaireId, String questionnaireRev, String formId, String formRev, List<ContextValue> context) {
    return createQuestionnaireDocument(questionnaireId, questionnaireRev, formId, formRev, context, builder -> {});
  }

  Questionnaire createQuestionnaireDocument(String questionnaireId, String questionnaireRev, String formId, String formRev, List<ContextValue> context, Consumer<ImmutableQuestionnaire.Builder> builderCallback) {
    ImmutableQuestionnaire.Builder builder = ImmutableQuestionnaire.builder()
      .metadata(ImmutableQuestionnaireMetadata.builder()
        .formId(formId)
        .formRev(formRev)
        .created(new Date())
        .status(Questionnaire.Metadata.Status.OPEN)
        .build())
      .id(questionnaireId)
      .rev(questionnaireRev)
      .context(context == null ? new ArrayList<>() : context);
    if (builderCallback != null) {
      builderCallback.accept(builder);
    }
    Questionnaire questionnaire = builder.build();
    // TODO tests will fail!!!
    when(questionnaireDatabase.findOne(tenantId, questionnaireId)).thenReturn(questionnaire);
    return questionnaire;
  }

  FormItem addQuestionnaire(ImmutableForm.Builder formBuilder, Consumer<ImmutableFormItem.Builder> builderConsumer) {
    return addItem(formBuilder, Constants.QUESTIONNAIRE, builder -> {
      builder.type(Constants.QUESTIONNAIRE);
      builderConsumer.accept(builder);
    });
  }

  FormItem addItem(ImmutableForm.Builder formBuilder, String itemId, Consumer<ImmutableFormItem.Builder> builderConsumer) {
    ImmutableFormItem.Builder builder = ImmutableFormItem.builder().id(itemId);
    builderConsumer.accept(builder);
    FormItem formItemBean = builder.build();
    formBuilder.putData(formItemBean.getId(), formItemBean);
    return formItemBean;
  }

  FormItem addGroup(ImmutableForm.Builder formBuilder, String groupId, Consumer<ImmutableFormItem.Builder> builderConsumer, String... items) {
    return addItem(formBuilder, groupId, builder -> {
      builderConsumer.accept(builder.type("group")
        .items(asList(items)));
    });
  }

  Session createQuestionnaire(String formId) throws Exception {
    MvcResult mvcResult = mockMvc
      .perform(post(uri("api","questionnaires")).params(tenantParam)
        .content("{\"metadata\": {\"formId\":\"" + formId + "\"}}")
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON)
        .with(csrf()))
      .andExpect(status().isCreated())
      .andReturn();
    MockHttpServletResponse response = mvcResult.getResponse();
    return new Session(OBJECT_MAPPER.readValue(response.getContentAsString(), IdAndRevision.class));
  }

  Session createEditorQuestionnaire(String formId) throws Exception {
    MvcResult mvcResult = mockMvc
      .perform(post(uri("api", "questionnaires")).params(tenantParam)
          .content("{\"metadata\": {\"formId\":\"" + formId + "\",\"formRev\":\"LATEST\"}}")
          .contentType(MediaType.APPLICATION_JSON)
          .accept(MediaType.APPLICATION_JSON)
        .with(csrf()))
      .andExpect(status().isOk()).andReturn();
    MockHttpServletResponse response = mvcResult.getResponse();
    return new Session(OBJECT_MAPPER.readValue(response.getContentAsString(), IdAndRevision.class));
  }

  <T> HttpEntity<T> httpEntity(T document, HttpHeaders httpHeaders) {
    if (httpHeaders == null) {
      httpHeaders = new HttpHeaders();
    }
    httpHeaders.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
    return new HttpEntity<>(document, httpHeaders);
  }

  <T> HttpEntity<T> httpEntity(HttpHeaders httpHeaders) {
    return httpEntity(null, httpHeaders);
  }


  private static class NoExceptionResponseErrorHandler extends DefaultResponseErrorHandler {
    @Override
    public void handleError(@NonNull ClientHttpResponse response) {
      LOGGER.trace("Response error: {}", response);
    }
  }

  class Session {
    IdAndRevision entity;

    String revision;

    Session(IdAndRevision entity) {
      this.entity = entity;
    }

    ParameterizedTypeReference<List<Action>> actionListGenericType = new ParameterizedTypeReference<>() {
    };

    List<Action> getAllActions() throws Exception {
      MvcResult mvcResult = mockMvc.perform(
        get(uri("api", "questionnaires", entity.getId(), "actions")).params(tenantParam)
          .header("FF-Request-Order-Token", revision)
          .accept(MediaType.APPLICATION_JSON_VALUE)
          .with(csrf()))
        .andExpect(status().isOk())
        .andReturn();
      revision = mvcResult.getResponse().getHeader("FF-Request-Order-Token");
      return OBJECT_MAPPER.readValue(mvcResult.getResponse().getContentAsString(), (JavaType) actionListGenericType.getType());
    }

    List<Action> answerQuestion(String questionId, String answer) throws Exception {
      final Action action = ActionsFactory.answer(questionId, answer);
      ResponseEntity<List<Action>> response = postAction(action);
      assertEquals(HttpStatus.OK, response.getStatusCode());
      return response.getBody();
    }

    List<Action> addRow(String rowGroupId) throws Exception {
      ResponseEntity<List<Action>> response = postAction(ActionsFactory.addRow(rowGroupId));
      assertEquals(HttpStatus.OK, response.getStatusCode());
      return response.getBody();
    }

    List<Action> deleteRow(String rowId) throws Exception {
      ResponseEntity<List<Action>> response = postAction(ActionsFactory.deleteRow(rowId));
      assertEquals(HttpStatus.OK, response.getStatusCode());
      return response.getBody();
    }

    List<Action> previousPage() throws Exception {
      ResponseEntity<List<Action>> response = postAction(Action.Type.PREVIOUS);
      assertEquals(HttpStatus.OK, response.getStatusCode());
      return response.getBody();
    }

    List<Action> nextPage() throws Exception {
      ResponseEntity<List<Action>> response = postAction(Action.Type.NEXT);
      assertEquals(HttpStatus.OK, response.getStatusCode());
      return response.getBody();
    }

    ResponseEntity<List<Action>> postAction(Action.Type actionType) throws Exception {
      return postAction(ImmutableAction.builder().type(actionType).build());
    }

    ResponseEntity<List<Action>> postAction(Action action) throws Exception {
      List<Action> actions = singletonList(action);
      MvcResult mvcResult = mockMvc.perform(
        post(uri("api", "questionnaires", action.getId(), "actions")).params(tenantParam)
          .header("FF-Request-Order-Token", revision)
          .content(OBJECT_MAPPER.writeValueAsString(actions))
          .accept(MediaType.APPLICATION_JSON_VALUE)
          .with(csrf()))
        .andExpect(status().isOk())
        .andReturn();
      revision = mvcResult.getResponse().getHeader("FF-Request-Order-Token");
      return OBJECT_MAPPER.readValue(mvcResult.getResponse().getContentAsString(), (JavaType) actionListGenericType.getType());
    }

    String getId() {
      return entity.getId();
    }
  }
}
