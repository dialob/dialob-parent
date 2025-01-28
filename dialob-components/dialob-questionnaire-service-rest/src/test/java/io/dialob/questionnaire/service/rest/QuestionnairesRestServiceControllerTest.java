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

import com.google.common.collect.Lists;
import edu.umd.cs.findbugs.annotations.NonNull;
import io.dialob.api.form.Form;
import io.dialob.api.form.ImmutableForm;
import io.dialob.api.form.ImmutableFormMetadata;
import io.dialob.api.proto.*;
import io.dialob.api.questionnaire.*;
import io.dialob.db.spi.exceptions.DocumentNotFoundException;
import io.dialob.db.spi.spring.DatabaseExceptionMapper;
import io.dialob.form.service.api.FormDatabase;
import io.dialob.questionnaire.csvserializer.CSVSerializer;
import io.dialob.questionnaire.service.api.QuestionnaireDatabase;
import io.dialob.questionnaire.service.api.session.*;
import io.dialob.rest.DialobRestAutoConfiguration;
import io.dialob.security.tenant.CurrentTenant;
import io.dialob.security.user.CurrentUserProvider;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.internal.hamcrest.HamcrestArgumentMatcher;
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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.function.Consumer;

import static io.dialob.api.questionnaire.QuestionnaireFactory.questionnaire;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {DatabaseExceptionMapper.class,
  QuestionnairesRestServiceControllerTest.TestConfiguration.class,
  QuestionnairesRestServiceController.class,
  DialobRestAutoConfiguration.class})
@EnableWebMvc
@WebAppConfiguration
public class QuestionnairesRestServiceControllerTest {

  public static final QuestionnaireSession.DispatchActionsResult EMPTY_IMMUTABLE_ACTIONS =
    ImmutableQuestionnaireSession.DispatchActionsResult.builder()
      .actions(ImmutableActions.builder().build())
      .isDidComplete(false)
      .build();

  @Configuration(proxyBeanMethods = false)
  public static class TestConfiguration {

    @Bean
    CSVSerializer csvSerializer(QuestionnaireDatabase questionnaireDatabase, CurrentTenant currentTenant) {
      return new CSVSerializer(questionnaireDatabase, currentTenant);
    }

  }

  @Autowired
  private WebApplicationContext webApplicationContext;


  @MockitoBean
  private FormDatabase formDatabase;

  @MockitoBean
  private QuestionnaireDatabase questionnaireDatabase;

  @MockitoBean
  private QuestionnaireSessionService questionnaireSessionService;

  @MockitoBean
  private QuestionnaireSessionSaveService questionnaireSessionSaveService;

  @MockitoBean
  private QuestionnaireSessionBuilderFactory questionnaireSessionBuilderFactory;

  @MockitoBean
  private CurrentTenant currentTenant;

  @MockitoBean
  private CurrentUserProvider currentUserProvider;

  private MockMvc mockMvc;

  @Autowired
  private QuestionnairesRestServiceController controller;

  @BeforeEach
  public void setUp() {
    mockMvc = webAppContextSetup(webApplicationContext).build();
    reset(questionnaireSessionService, currentTenant);
    when(currentTenant.getId()).thenReturn("t-123");
  }

  @Test
  void shouldReturnQuestionnaireStatus() throws Exception {
    final QuestionnaireSession questionnaireSession = mock(QuestionnaireSession.class);

    when(questionnaireSessionService.findOne("1234")).thenReturn(questionnaireSession);
    when(questionnaireSession.getStatus()).thenReturn(Questionnaire.Metadata.Status.OPEN);

    mockMvc.perform(get("/questionnaires/{questionnaireId}/status", "1234"))
      .andExpect(status().isOk())
      .andExpect(content().contentType(MediaType.APPLICATION_JSON))
      .andExpect(jsonPath("$", is("OPEN")));

    verify(questionnaireSessionService).findOne("1234");
    verify(questionnaireSession).getStatus();
    verifyNoMoreInteractions(questionnaireSessionService, questionnaireSession);
  }


  @Test
  void shouldUpdateQuestionnaireStatus() throws Exception {
    final QuestionnaireSession session = mock(QuestionnaireSession.class);

    when(questionnaireSessionService.findOne("1234")).thenReturn(session);
    when(session.getStatus()).thenReturn(Questionnaire.Metadata.Status.OPEN).thenReturn(Questionnaire.Metadata.Status.COMPLETED);
    when(questionnaireSessionSaveService.save(session)).thenReturn(session);

    mockMvc.perform(put("/questionnaires/{questionnaireId}/status", "1234")
      .content("\"COMPLETED\"")
      .contentType(MediaType.APPLICATION_JSON)
    )
      .andExpect(status().isOk())
      .andExpect(content().contentType(MediaType.APPLICATION_JSON))
      .andExpect(jsonPath("$", is("COMPLETED")));

    verifyCompleteAction(session, "1234");
    verify(questionnaireSessionService).findOne("1234");
    verify(session, times(2)).getStatus();
    verify(session).dispatchActions(any());
    verify(questionnaireSessionSaveService).save(session);
    verifyNoMoreInteractions(questionnaireSessionService, questionnaireSessionSaveService, session);
  }

  @Test
  void shouldGetQuestionAnswers() throws Exception {
    final QuestionnaireSession questionnaireSession = mock(QuestionnaireSession.class);

    when(questionnaireSessionService.findOne("1234")).thenReturn(questionnaireSession);
    when(questionnaireSession.getAnswers()).thenReturn(
      Arrays.asList(
        ImmutableAnswer.builder().id("question1").value("1").build(),
        ImmutableAnswer.builder().id("question2").value(Arrays.asList("a", "b", "c")).build()
      ));

    mockMvc.perform(get("/questionnaires/{questionnaireId}/answers", "1234")
      .content("\"COMPLETED\"")
      .contentType(MediaType.APPLICATION_JSON)
    )
      .andExpect(status().isOk())
      .andExpect(content().contentType(MediaType.APPLICATION_JSON))
      .andExpect(jsonPath("$", hasSize(2)))
      .andExpect(jsonPath("$[0].id", is("question1")))
      .andExpect(jsonPath("$[1].id", is("question2")))
      .andExpect(jsonPath("$[1].value", hasSize(3)))
      .andExpect(jsonPath("$[1].value[1]", is("b")))
    ;
    verify(questionnaireSessionService).findOne("1234");
    verify(questionnaireSession).getAnswers();
    verifyNoMoreInteractions(questionnaireSessionService, questionnaireSession);
  }

  @Test
  void shouldUpdateQuestionAnswer() throws Exception {
    final QuestionnaireSession questionnaireSession = mock(QuestionnaireSession.class);

    when(questionnaireSessionService.findOne("1234")).thenReturn(questionnaireSession);
    when(questionnaireSession.getErrors()).thenReturn(Collections.emptyList());
    when(questionnaireSession.dispatchActions(any())).thenReturn(EMPTY_IMMUTABLE_ACTIONS);
    when(questionnaireSessionSaveService.save(questionnaireSession)).thenReturn(questionnaireSession);

    mockMvc.perform(put("/questionnaires/{questionnaireId}/answers/{questionId}", "1234", "question1")
      .content("\"123\"")
      .contentType(MediaType.APPLICATION_JSON)
    )
      .andExpect(status().isOk())
      .andExpect(content().contentType(MediaType.APPLICATION_JSON))
      .andExpect(jsonPath("$", hasSize(0)))
    ;
    verify(questionnaireSessionService).findOne("1234");
    verify(questionnaireSessionSaveService).save(questionnaireSession);
    verify(questionnaireSession).dispatchActions(any());
    verify(questionnaireSession).getErrors();
    verify(questionnaireSession, times(1)).getStatus();
    verifyNoMoreInteractions(questionnaireSessionService, questionnaireSession);
  }

  @Test
  void shouldRemoveQuestionAnswer() throws Exception {
    final QuestionnaireSession session = mock(QuestionnaireSession.class);

    when(questionnaireSessionService.findOne("1234")).thenReturn(session);
    when(session.getErrors()).thenReturn(Collections.emptyList());
    when(questionnaireSessionSaveService.save(session)).thenReturn(session);

    mockMvc.perform(delete("/questionnaires/{questionnaireId}/answers/{questionId}", "1234", "question1"))
      .andExpect(status().isOk())
      .andExpect(content().contentType(MediaType.APPLICATION_JSON))
      .andExpect(content().string("[]"))
    ;
    verify(questionnaireSessionService).findOne("1234");
    verify(questionnaireSessionSaveService).save(session);
    verifyAnswerAction(session, "question1", null);
    verify(session).getErrors();
    verify(session).dispatchActions(any());
    verify(session, times(1)).getStatus();
    verifyNoMoreInteractions(questionnaireSessionService, session);
  }


  @Test
  void shouldUpdateMultiValueQuestionAnswerAsArrayOfStrings() throws Exception {
    final QuestionnaireSession session = mock(QuestionnaireSession.class);

    when(questionnaireSessionService.findOne("1234")).thenReturn(session);
    when(session.getErrors()).thenReturn(Collections.emptyList());
    when(questionnaireSessionSaveService.save(session)).thenReturn(session);

    mockMvc.perform(put("/questionnaires/{questionnaireId}/answers/{questionId}", "1234", "question1")
      .content("[\"set1\",\"set2\",\"set3\",\"set4\",null]")
      .contentType(MediaType.APPLICATION_JSON)
    )
      .andExpect(status().isOk())
      .andExpect(content().contentType(MediaType.APPLICATION_JSON))
      .andExpect(jsonPath("$", hasSize(0)))
    ;
    verify(questionnaireSessionService).findOne("1234");
    verify(questionnaireSessionSaveService).save(session);
    verify(session).dispatchActions(any());
    verifyAnswerAction(session, "question1", Arrays.asList("set1", "set2", "set3", "set4"));
    verify(session).getErrors();
    verify(session, times(1)).getStatus();
    verifyNoMoreInteractions(questionnaireSessionService, session);
  }

  @Test
  void shouldRejectObjectAnswers() throws Exception {
    final QuestionnaireSession session = mock(QuestionnaireSession.class);

    when(questionnaireSessionService.findOne("1234")).thenReturn(session);
    when(session.getErrors()).thenReturn(Collections.emptyList());

    mockMvc.perform(put("/questionnaires/{questionnaireId}/answers/{questionId}", "1234", "question1")
      .content("{}")
      .contentType(MediaType.APPLICATION_JSON)
    )
      .andExpect(status().isBadRequest())
      .andExpect(content().contentType(MediaType.APPLICATION_JSON))
      .andExpect(content().string("[{\"id\":\"question1\",\"code\":\"invalid_answer\",\"description\":\"Cannot handle answer data\"}]"))
    ;
    verifyNoMoreInteractions(questionnaireSessionService, session);
  }

  @Test
  void shouldUpdateQuestionAnswers() throws Exception {
    final QuestionnaireSession questionnaireSession = mock(QuestionnaireSession.class);

    when(questionnaireSessionService.findOne("1234")).thenReturn(questionnaireSession);
    when(questionnaireSession.getErrors()).thenReturn(Collections.emptyList());
    when(questionnaireSession.dispatchActions(argThat(new HamcrestArgumentMatcher<>(new BaseMatcher<Collection<Action>>() {
      @Override
      public boolean matches(Object item) {
        Collection<Action> consumer = (Collection<Action>) item;
        return true;
      }

      @Override
      public void describeTo(Description description) {

      }
    })))).thenReturn(EMPTY_IMMUTABLE_ACTIONS);
    when(questionnaireSessionSaveService.save(questionnaireSession)).thenReturn(questionnaireSession);

    //
    mockMvc.perform(post("/questionnaires/{questionnaireId}/answers", "1234")
      .content("[{\"id\":\"question1\",\"value\":\"123\"},{\"id\":\"question2\",\"value\":\"abc\"}]")
      .contentType(MediaType.APPLICATION_JSON)
    )
      .andExpect(status().isOk())
      .andExpect(content().contentType(MediaType.APPLICATION_JSON))
      .andExpect(jsonPath("$", hasSize(0)));
    verify(questionnaireSessionService).findOne("1234");
    verify(questionnaireSessionSaveService).save(questionnaireSession);
    verify(questionnaireSession).dispatchActions(any());
    verify(questionnaireSession).getErrors();
    verify(questionnaireSession, times(1)).getStatus();
    verifyNoMoreInteractions(questionnaireSessionService, questionnaireSession);
  }


  @Test
  void shouldReturn422IfFormDoNotExists() throws Exception {
    when(formDatabase.exists("t-123", "not-form")).thenReturn(false);

    mockMvc.perform(post("/questionnaires")
      .content("{\"metadata\":{\"formId\":\"not-form\"}}").contentType(MediaType.APPLICATION_JSON))
      .andExpect(status().isUnprocessableEntity())
      .andExpect(content().contentType(MediaType.APPLICATION_JSON))
      .andExpect(jsonPath("$.errors", hasSize(1)))
      .andExpect(jsonPath("$.errors[0].context", equalTo("metadata.formId")))
      .andExpect(jsonPath("$.errors[0].rejectedValue", equalTo("not-form")));

    verify(formDatabase).exists("t-123", "not-form");
    verifyNoMoreInteractions(formDatabase);
  }


  @Test
  void shouldPostNewQuestionnaire() throws Exception {
    Form formDocument = ImmutableForm.builder().metadata(ImmutableFormMetadata.builder().label("label").build()).build();
    when(formDatabase.exists("t-123", "new-form")).thenReturn(true);
    QuestionnaireSessionBuilder builder = mock(QuestionnaireSessionBuilder.class);
    QuestionnaireSession session = mock(QuestionnaireSession.class);
    Questionnaire questionnaire = ImmutableQuestionnaire.builder()
      .id("new-questionnaire")
      .rev("1-new-questionnaire")
      .metadata(ImmutableQuestionnaireMetadata.builder().formId("shouldPostNewQuestionnaire").build())
      .build();

    when(questionnaireSessionBuilderFactory.createQuestionnaireSessionBuilder()).thenReturn(builder);
    when(builder.createOnly(true)).thenReturn(builder);
    when(builder.formId(anyString())).thenReturn(builder);
    when(builder.formRev(any())).thenReturn(builder);
    when(builder.submitUrl(any())).thenReturn(builder);
    when(builder.contextValues(any())).thenReturn(builder);
    when(builder.answers(any())).thenReturn(builder);
    when(builder.valueSets(any())).thenReturn(builder);
    when(builder.language(any())).thenReturn(builder);
    when(builder.creator(any())).thenReturn(builder);
    when(builder.owner(any())).thenReturn(builder);
    when(builder.status(any())).thenReturn(builder);
    when(builder.activeItem(any())).thenReturn(builder);
    when(builder.additionalProperties(any())).thenReturn(builder);
    when(builder.build()).thenReturn(session);
    when(session.getQuestionnaire()).thenReturn(questionnaire);
    when(session.getSessionId()).thenReturn(Optional.of("new-questionnaire"));

    mockMvc.perform(post("/questionnaires")
      .content("{\"activeItem\":\"page3\",\"metadata\":{\"formId\":\"new-form\"}}").contentType(MediaType.APPLICATION_JSON))
      .andExpect(status().isCreated())
      .andExpect(content().contentType(MediaType.APPLICATION_JSON))
      .andExpect(jsonPath("$._id", equalTo("new-questionnaire")))
      .andExpect(jsonPath("$._rev", equalTo("1-new-questionnaire")))
    ;
    verify(formDatabase).exists("t-123", "new-form");
    verify(builder).createOnly(true);
    verify(builder).formId("new-form");
    verify(builder).formRev(null);
    verify(builder).creator(null);
    verify(builder).owner(null);
    verify(builder).submitUrl(null);
    verify(builder).contextValues(anyList());
    verify(builder).answers(anyList());
    verify(builder).valueSets(anyList());
    verify(builder).language(null);
    verify(builder).status(Questionnaire.Metadata.Status.NEW);
    verify(builder).activeItem("page3");
    verify(builder).additionalProperties(any());
    verify(builder).build();
    verifyNoMoreInteractions(formDatabase, builder);

  }

  @Test
  void shouldUpdateQuestionnaire() throws Exception {
    QuestionnaireSession session = mock(QuestionnaireSession.class);
    when(questionnaireSessionService.findOne("123")).thenReturn(session);
    when(questionnaireSessionSaveService.save(session)).thenReturn(session);

    when(session.getQuestionnaire()).thenReturn(QuestionnaireFactory.questionnaire(null, "form1"));

    mockMvc.perform(put("/questionnaires/123")
      .content("{\"activeItem\":\"new-page\",\"answers\":[{\"id\":\"q1\",\"value\":\"new answer\"},{\"id\":\"q2\",\"value\":\"another answer\"}],\"metadata\":{\"formId\":\"form1\",\"status\":\"COMPLETED\"}}").contentType(MediaType.APPLICATION_JSON))
      .andExpect(status().isOk())
      .andExpect(content().contentType(MediaType.APPLICATION_JSON))
      .andExpect(content().string("{\"metadata\":{\"formId\":\"form1\",\"status\":\"NEW\"}}"));


    verify(session).dispatchActions(Arrays.asList(
      ActionsFactory.gotoPage("new-page"),
      ActionsFactory.removeAnswers(),
      ActionsFactory.answer("q1", "new answer"),
      ActionsFactory.answer("q2", "another answer"),
      ActionsFactory.complete(null)
    ));

    verify(questionnaireSessionService).findOne("123");
    verify(questionnaireSessionSaveService).save(session);
    verifyNoMoreInteractions(questionnaireSessionService, questionnaireSessionSaveService);
  }

  @Test
  void shouldIgnoreNonCompleteStatusChanges() throws Exception {
    QuestionnaireSession session = mock(QuestionnaireSession.class);
    when(questionnaireSessionService.findOne("123")).thenReturn(session);

    Questionnaire questionnaire = questionnaire(null, "shouldIgnoreNonCompleteStatusChanges");
    when(session.getQuestionnaire()).thenReturn(questionnaire);
    when(questionnaireSessionSaveService.save(session)).thenReturn(session);

    mockMvc.perform(put("/questionnaires/123")
      .content("{\"metadata\":{\"formId\":\"form1\",\"status\":\"NEW\"}}").contentType(MediaType.APPLICATION_JSON))
      .andExpect(status().isOk())
      .andExpect(content().contentType(MediaType.APPLICATION_JSON))
      .andExpect(content().string("{\"metadata\":{\"formId\":\"shouldIgnoreNonCompleteStatusChanges\",\"status\":\"NEW\"}}"));

    verifyRemoveAnswersAction(session);

    verify(questionnaireSessionService).findOne("123");
    verify(questionnaireSessionSaveService).save(session);
    verifyNoMoreInteractions(questionnaireSessionService, questionnaireSessionSaveService);
  }

  @Test
  void shouldNotTryCompleteAlreadyCompletedForm() throws Exception {
    QuestionnaireSession session = mock(QuestionnaireSession.class);
    when(questionnaireSessionService.findOne("123")).thenReturn(session);
    when(session.getStatus()).thenReturn(Questionnaire.Metadata.Status.COMPLETED);
    when(questionnaireSessionSaveService.save(session)).thenReturn(session);

    Questionnaire questionnaire = questionnaire(null, "shouldNotTryCompleteAlreadyCompletedForm");
    when(session.getQuestionnaire()).thenReturn(questionnaire);

    mockMvc.perform(put("/questionnaires/123")
      .content("{\"metadata\":{\"formId\":\"form1\",\"status\":\"COMPLETED\"}}").contentType(MediaType.APPLICATION_JSON))
      .andExpect(status().isOk())
      .andExpect(content().contentType(MediaType.APPLICATION_JSON))
      .andExpect(content().string("{\"metadata\":{\"formId\":\"shouldNotTryCompleteAlreadyCompletedForm\",\"status\":\"NEW\"}}"));


    verify(questionnaireSessionService).findOne("123");
    verifyNoMoreInteractions(questionnaireSessionService, questionnaireSessionSaveService);
  }

  @Test
  void shouldQueryQuestionnairesAndGetEmptyList() throws Exception {
    QuestionnaireSession session = mock(QuestionnaireSession.class);

    doAnswer(invocation -> {
      return null;
    }).when(questionnaireDatabase).findAllMetadata(eq("t-123"), isNull(), isNull(), isNull(), isNull(), isNull(), any());

    mockMvc.perform(get("/questionnaires").accept(MediaType.APPLICATION_JSON))
      .andExpect(status().isOk())
      .andExpect(content().contentType(MediaType.APPLICATION_JSON))
      .andExpect(content().string("[]"));

    verify(questionnaireDatabase).findAllMetadata(eq("t-123"), isNull(), isNull(), isNull(), isNull(), isNull(), any());
    verifyNoMoreInteractions(questionnaireSessionService, questionnaireSessionSaveService);
  }

  @Test
  void shouldQueryActiveQuestionnaires() throws Exception {
    QuestionnaireSession session = mock(QuestionnaireSession.class);

    doAnswer(invocation -> {
      Consumer<QuestionnaireDatabase.MetadataRow> consumer = invocation.getArgument(6);
      consumer.accept(new QuestionnaireDatabase.MetadataRow() {
        @NonNull
        @Override
        public String getId() {
          return "123";
        }

        @NonNull
        @Override
        public Questionnaire.Metadata getValue() {
          return ImmutableQuestionnaireMetadata.builder().formId("f1").build();
        }
      });
      consumer.accept(new QuestionnaireDatabase.MetadataRow() {
        @NonNull
        @Override
        public String getId() {
          return "124";
        }

        @NonNull
        @Override
        public Questionnaire.Metadata getValue() {
          return ImmutableQuestionnaireMetadata.builder().formId("f2").build();
        }
      });
      return null;
    }).when(questionnaireDatabase).findAllMetadata(eq("t-123"), isNull(), isNull(), isNull(), isNull(), isNull(), any());

    mockMvc.perform(get("/questionnaires?active=true").accept(MediaType.APPLICATION_JSON))
      .andExpect(status().isOk())
      .andExpect(content().contentType(MediaType.APPLICATION_JSON))
      .andExpect(content().string("[{\"id\":\"123\",\"metadata\":{\"formId\":\"f1\",\"status\":\"NEW\"}},{\"id\":\"124\",\"metadata\":{\"formId\":\"f2\",\"status\":\"NEW\"}}]"));

    verifyNoMoreInteractions(questionnaireSessionService, questionnaireSessionSaveService);
  }

  @Test
  void shouldGetQuestionnaire() throws Exception {
    QuestionnaireSession session = mock(QuestionnaireSession.class);

    Questionnaire questionnaire = questionnaire(null, "shouldGetQuestionnaire");
    when(questionnaireDatabase.findOne("t-123", "abc123")).thenReturn(questionnaire);

    mockMvc.perform(get("/questionnaires/abc123").accept(MediaType.APPLICATION_JSON))
      .andExpect(status().isOk())
      .andExpect(content().contentType(MediaType.APPLICATION_JSON))
      .andExpect(content().string("{\"metadata\":{\"formId\":\"shouldGetQuestionnaire\",\"status\":\"NEW\"}}"));

    verify(questionnaireDatabase).findOne("t-123", "abc123");
    verifyNoMoreInteractions(questionnaireDatabase);
  }

  @Test
  void shouldNotGetQuestionnaireWithInvalidId() throws Exception {
    Questionnaire questionnaire = questionnaire(null, "shouldGetQuestionnaire");
    when(questionnaireDatabase.findOne("t-123", "abc123")).thenReturn(questionnaire);

    mockMvc.perform(get("/questionnaires/efsf").accept(MediaType.APPLICATION_JSON))
      .andExpect(status().isBadRequest())
      .andExpect(content().string(is(emptyString())));

    verifyNoInteractions(questionnaireDatabase);
  }

  @Test
  void shouldGet404QuestionnaireWhenNotFound() throws Exception {
    when(questionnaireDatabase.findOne("t-123", "abc123")).thenThrow(DocumentNotFoundException.class);

    mockMvc.perform(get("/questionnaires/abc123").accept(MediaType.APPLICATION_JSON))
      .andExpect(status().isNotFound())
      .andExpect(content().contentType(MediaType.APPLICATION_JSON))
      .andExpect(jsonPath(".status").value(404))
      .andExpect(jsonPath(".error").value("Not Found"));

    verify(questionnaireDatabase).findOne("t-123", "abc123");
    verifyNoMoreInteractions(questionnaireDatabase);
  }

  @Test
  void shouldGetQuestionnairePages() throws Exception {
    QuestionnaireSession session = mock(QuestionnaireSession.class);

    when(questionnaireSessionService.findOne("abc123")).thenReturn(session);
    ActionItem questionnaireItem = ImmutableActionItem.builder()
      .id("questionnaire")
      .type("questionnaire")
      .activeItem("page3")
      .items(Arrays.asList("page1", "page2", "page3"))
      .availableItems(Arrays.asList("page1", "page3")).build();
    when(session.getItemById("questionnaire")).thenReturn(Optional.of(questionnaireItem));
    mockMvc.perform(get("/questionnaires/abc123/pages").accept(MediaType.APPLICATION_JSON))
      .andExpect(status().isOk())
      .andExpect(content().contentType(MediaType.APPLICATION_JSON))
      .andExpect(content().string("{\"activeItem\":\"page3\",\"items\":[\"page1\",\"page2\",\"page3\"],\"availableItems\":[\"page1\",\"page3\"]}"));

    verify(questionnaireSessionService).findOne("abc123");
    verify(session).getItemById("questionnaire");
    verifyNoMoreInteractions(questionnaireSessionService, session);
  }

  @Test
  void pagesUpdateShouldTryNavigateToPages() throws Exception {
    QuestionnaireSession session = mock(QuestionnaireSession.class);
    ActionItem questionnaireItem = ImmutableActionItem.builder()
      .id("questionnaire")
      .type("questionnaire")
      .activeItem("page3")
      .items(Arrays.asList("page1", "page2", "page3"))
      .availableItems(Arrays.asList("page1", "page3")).build();
    when(session.getItemById("questionnaire")).thenReturn(Optional.of(questionnaireItem));

    when(questionnaireSessionService.findOne("abc123")).thenReturn(session);
    when(questionnaireSessionSaveService.save(session)).thenReturn(session);
    when(session.getActiveItem()).thenReturn(Optional.empty());
    mockMvc.perform(
      put("/questionnaires/abc123/pages").accept(MediaType.APPLICATION_JSON)
        .content("{\"activeItem\":\"page3\",\"items\":[\"page1\",\"page2\",\"page3\"],\"availableItems\":[\"page1\",\"page3\"]}")
        .contentType(MediaType.APPLICATION_JSON))
      .andExpect(status().isOk())
      .andExpect(content().contentType(MediaType.APPLICATION_JSON))
      .andExpect(content().string("{\"activeItem\":\"page3\",\"items\":[\"page1\",\"page2\",\"page3\"],\"availableItems\":[\"page1\",\"page3\"]}"));


    verifyGotoPageAction(session, "page3");
    verify(questionnaireSessionService).findOne("abc123");
    verify(questionnaireSessionSaveService).save(session);
    verify(session).getItemById("questionnaire");
    verify(session).getStatus();
    verify(session).getActiveItem();
    verify(session).dispatchActions(any());
    verifyNoMoreInteractions(questionnaireSessionService, session);
  }

  @Test
  void shouldGetQuestionnaireErrors() throws Exception {
    QuestionnaireSession session = mock(QuestionnaireSession.class);

    when(questionnaireSessionService.findOne("abc123")).thenReturn(session);
    when(session.getErrors()).thenReturn(Arrays.asList(
      ImmutableError.builder().description("error").code("bad_error").id("q1").build(),
      ImmutableError.builder().description("not good error").code("worse_error").id("q2").build(),
      ImmutableError.builder().description("really not good error").code("worse_error_twice").id("q2").build()
    ));
    mockMvc.perform(get("/questionnaires/abc123/errors").accept(MediaType.APPLICATION_JSON))
      .andExpect(status().isOk())
      .andExpect(content().contentType(MediaType.APPLICATION_JSON))
      .andExpect(content().string("[{\"id\":\"q1\",\"code\":\"bad_error\",\"description\":\"error\"},{\"id\":\"q2\",\"code\":\"worse_error\",\"description\":\"not good error\"},{\"id\":\"q2\",\"code\":\"worse_error_twice\",\"description\":\"really not good error\"}]"));

    verify(questionnaireSessionService).findOne("abc123");
    verify(session).getErrors();
    verifyNoMoreInteractions(questionnaireSessionService, session);
  }

  @Test
  void shouldDeleteQuestionnaire() throws Exception {
    QuestionnaireSession session = mock(QuestionnaireSession.class);

    doReturn(true).when(questionnaireDatabase).delete("t-123", "abc123");

    mockMvc.perform(delete("/questionnaires/abc123").accept(MediaType.APPLICATION_JSON))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.ok").value(true));

    verify(questionnaireDatabase).delete("t-123", "abc123");
    verifyNoMoreInteractions(questionnaireSessionService, questionnaireDatabase, session);
  }

  @Test
  void shouldGet404ForNonExistingQuestionnaireOnDelete() throws Exception {
    QuestionnaireSession session = mock(QuestionnaireSession.class);

    doThrow(DocumentNotFoundException.class).when(questionnaireDatabase).delete("t-123", "abc123");

    mockMvc.perform(delete("/questionnaires/abc123").accept(MediaType.APPLICATION_JSON))
      .andExpect(status().isNotFound());

    verify(questionnaireDatabase).delete("t-123", "abc123");
    verifyNoMoreInteractions(questionnaireSessionService, questionnaireDatabase, session);
  }

  @Test
  void shouldGetItems() throws Exception {
    final QuestionnaireSession session = mock(QuestionnaireSession.class);
    when(questionnaireSessionService.findOne("abc123")).thenReturn(session);
    ActionItem question1 = ImmutableActionItem.builder().id("question1").type("text").build();
    ActionItem question2 = ImmutableActionItem.builder().id("question2").type("text").build();

    when(session.getItems()).thenReturn(Arrays.asList(question1, question2));
    mockMvc.perform(get("/questionnaires/abc123/items").accept(MediaType.APPLICATION_JSON))
      .andExpect(status().isOk())
      .andExpect(content().contentType(MediaType.APPLICATION_JSON))
      .andExpect(content().string("[{\"id\":\"question1\",\"type\":\"text\"},{\"id\":\"question2\",\"type\":\"text\"}]"));

    verify(session).getItems();
    verify(questionnaireSessionService).findOne("abc123");
    verifyNoMoreInteractions(questionnaireSessionService, session);
  }

  @Test
  void shouldGetItemById() throws Exception {
    final QuestionnaireSession session = mock(QuestionnaireSession.class);
    when(questionnaireSessionService.findOne("abc123")).thenReturn(session);
    final ActionItem question1 = ImmutableActionItem.builder()
      .id("question1").type("text").build();

    when(session.getItemById("question1")).thenReturn(Optional.of(question1));
    mockMvc.perform(get("/questionnaires/abc123/items/question1").accept(MediaType.APPLICATION_JSON))
      .andExpect(status().isOk())
      .andExpect(content().contentType(MediaType.APPLICATION_JSON))
      .andExpect(content().string("{\"id\":\"question1\",\"type\":\"text\"}"));

    verify(session).getItemById("question1");
    verify(questionnaireSessionService).findOne("abc123");
    verifyNoMoreInteractions(questionnaireSessionService, session);
  }

  @Test
  void shouldGetItemByIdAndGet404ForUnknownItem() throws Exception {
    final QuestionnaireSession session = mock(QuestionnaireSession.class);
    when(questionnaireSessionService.findOne("abc123")).thenReturn(session);

    when(session.getItemById("question1")).thenReturn(Optional.empty());
    mockMvc.perform(get("/questionnaires/abc123/items/question1").accept(MediaType.APPLICATION_JSON))
      .andExpect(status().isNotFound());

    verify(session).getItemById("question1");
    verify(questionnaireSessionService).findOne("abc123");
    verifyNoMoreInteractions(questionnaireSessionService, session);
  }

  @Test
  void shouldGetQuestionRows() throws Exception {
    final QuestionnaireSession session = mock(QuestionnaireSession.class);
    when(questionnaireSessionService.findOne("abc123")).thenReturn(session);
    final ActionItem question1 = ImmutableActionItem.builder()
      .id("question1")
      .type("rowgroup")
      .items(Arrays.asList("row1", "row2", "row3")).build();

    when(session.getItemById("question1")).thenReturn(Optional.of(question1));
    mockMvc.perform(get("/questionnaires/abc123/items/question1/rows").accept(MediaType.APPLICATION_JSON))
      .andExpect(status().isOk())
      .andExpect(content().contentType(MediaType.APPLICATION_JSON))
      .andExpect(content().string("[\"row1\",\"row2\",\"row3\"]"));

    verify(session).getItemById("question1");
    verify(questionnaireSessionService).findOne("abc123");
    verifyNoMoreInteractions(questionnaireSessionService, session);
  }

  @Test
  void shouldGet404IfQuestionIsNotRowgroup() throws Exception {
    final QuestionnaireSession session = mock(QuestionnaireSession.class);
    when(questionnaireSessionService.findOne("abc123")).thenReturn(session);
    final ActionItem question1 = ImmutableActionItem.builder()
      .id("question1")
      .type("group")
      .items(Arrays.asList("row1", "row2", "row3")).build();
    when(session.getItemById("question1")).thenReturn(Optional.of(question1));

    mockMvc.perform(get("/questionnaires/abc123/items/question1/rows").accept(MediaType.APPLICATION_JSON))
      .andExpect(status().isNotFound());

    verify(session).getItemById("question1");
    verify(questionnaireSessionService).findOne("abc123");
    verifyNoMoreInteractions(questionnaireSessionService, questionnaireSessionSaveService, session);
  }

  @Test
  void postShouldAddNewRow() throws Exception {
    final QuestionnaireSession session = mock(QuestionnaireSession.class);

    when(questionnaireSessionService.findOne("abc123")).thenReturn(session);
    final ActionItem question1 = ImmutableActionItem.builder()
      .id("question1")
      .type("rowgroup")
      .items(Arrays.asList("row1", "row2", "row3")).build();
    when(questionnaireSessionSaveService.save(session)).thenReturn(session);

    when(session.getItemById("question1")).thenReturn(Optional.of(question1));
    mockMvc.perform(post("/questionnaires/abc123/items/question1/rows").content("{}").contentType(MediaType.APPLICATION_JSON))
      .andExpect(status().isOk())
      .andExpect(content().contentType(MediaType.APPLICATION_JSON))
      .andExpect(content().string("[\"row1\",\"row2\",\"row3\"]"));

    verifyAddRowAction(session, "question1");
    verify(session).dispatchActions(any());
    verify(session).getItemById("question1");
    verify(questionnaireSessionService).findOne("abc123");
    verify(session).getStatus();
    verify(questionnaireSessionSaveService).save(session);
    verifyNoMoreInteractions(questionnaireSessionService, questionnaireSessionSaveService, session);
  }

  @Test
  void deleteShouldRemoveRow() throws Exception {
    final QuestionnaireSession session = mock(QuestionnaireSession.class);

    when(questionnaireSessionService.findOne("abc123")).thenReturn(session);
    final ActionItem question1 = ImmutableActionItem.builder()
      .id("question1")
      .type("rowgroup")
      .items(Arrays.asList("row1", "row2", "row3")).build();
    when(questionnaireSessionSaveService.save(session)).thenReturn(session);

    when(session.getItemById("question1")).thenReturn(Optional.of(question1));
    mockMvc.perform(delete("/questionnaires/abc123/items/question1/rows/row1"))
      .andExpect(status().isOk())
      .andExpect(content().contentType(MediaType.APPLICATION_JSON))
      .andExpect(content().string("[\"row1\",\"row2\",\"row3\"]"));

    verifyDeleteRowAction(session, "row1");
    verify(session).dispatchActions(any());
    verify(session, times(2)).getItemById("question1");
    verify(questionnaireSessionService).findOne("abc123");
    verify(session).getStatus();
    verify(questionnaireSessionSaveService).save(session);
    verifyNoMoreInteractions(questionnaireSessionService, questionnaireSessionSaveService, session);
  }

  @Test
  void shouldGetQuestionValueSets() throws Exception {
    final QuestionnaireSession session = mock(QuestionnaireSession.class);
    when(questionnaireSessionService.findOne("abc123")).thenReturn(session);
    when(session.getValueSets()).thenReturn(Arrays.asList(
      ImmutableValueSet.builder().id("vs1").entries(Arrays.asList(ImmutableValueSetEntry.builder().key("vs1-key1").value("vs1-value1").build(), ImmutableValueSetEntry.builder().key("vs1-key2").value("vs1-value2").build())).build(),
      ImmutableValueSet.builder().id("vs2").entries(Arrays.asList(ImmutableValueSetEntry.builder().key("vs2-key1").value("vs2-value1").build(), ImmutableValueSetEntry.builder().key("vs2-key2").value("vs2-value2").build())).build()
    ));

    mockMvc.perform(get("/questionnaires/abc123/valueSets").accept(MediaType.APPLICATION_JSON))
      .andExpect(status().isOk())
      .andExpect(content().contentType(MediaType.APPLICATION_JSON))
      .andExpect(content().string("[{\"id\":\"vs1\",\"entries\":[{\"key\":\"vs1-key1\",\"value\":\"vs1-value1\"},{\"key\":\"vs1-key2\",\"value\":\"vs1-value2\"}]},{\"id\":\"vs2\",\"entries\":[{\"key\":\"vs2-key1\",\"value\":\"vs2-value1\"},{\"key\":\"vs2-key2\",\"value\":\"vs2-value2\"}]}]"));

    verify(session).getValueSets();
    verify(questionnaireSessionService).findOne("abc123");
    verifyNoMoreInteractions(questionnaireSessionService, session);
  }

  @Test
  void shouldGetQuestionValueSet() throws Exception {
    final QuestionnaireSession session = mock(QuestionnaireSession.class);
    when(questionnaireSessionService.findOne("abc123")).thenReturn(session);
    when(session.getValueSets()).thenReturn(Arrays.asList(
      ImmutableValueSet.builder().id("vs1").entries(Arrays.asList(ImmutableValueSetEntry.builder().key("vs1-key1").value("vs1-value1").build(), ImmutableValueSetEntry.builder().key("vs1-key2").value("vs1-value2").build())).build(),
      ImmutableValueSet.builder().id("vs2").entries(Arrays.asList(ImmutableValueSetEntry.builder().key("vs2-key1").value("vs2-value1").build(), ImmutableValueSetEntry.builder().key("vs2-key2").value("vs2-value2").build())).build()
    ));

    mockMvc.perform(get("/questionnaires/abc123/valueSets/vs2").accept(MediaType.APPLICATION_JSON))
      .andExpect(status().isOk())
      .andExpect(content().contentType(MediaType.APPLICATION_JSON))
      .andExpect(content().string("{\"id\":\"vs2\",\"entries\":[{\"key\":\"vs2-key1\",\"value\":\"vs2-value1\"},{\"key\":\"vs2-key2\",\"value\":\"vs2-value2\"}]}"));

    verify(session).getValueSets();
    verify(questionnaireSessionService).findOne("abc123");
    verifyNoMoreInteractions(questionnaireSessionService, session);
  }

  @Test
  void shouldPutQuestionnaire() throws Exception {
    QuestionnaireSession session = mock(QuestionnaireSession.class);
    when(questionnaireSessionService.findOne("123")).thenReturn(session);
    when(questionnaireSessionSaveService.save(session)).thenReturn(session);

    Questionnaire questionnaire = questionnaire(null, "shouldPutQuestionnaire");
    when(session.getQuestionnaire()).thenReturn(questionnaire);

    mockMvc.perform(put("/questionnaires/123")
      .content("{\"metadata\":{\"formId\":\"form1\"},\"context\":[{\"id\":\"c1\",\"value\":\"new value\"}]}").contentType(MediaType.APPLICATION_JSON))
      .andExpect(status().isOk())
      .andExpect(content().contentType(MediaType.APPLICATION_JSON))
      .andExpect(content().string("{\"metadata\":{\"formId\":\"shouldPutQuestionnaire\",\"status\":\"NEW\"}}"));

    verify(session).dispatchActions(Arrays.asList(
      ActionsFactory.removeAnswers(),
      ActionsFactory.setValue("c1", "new value")
    ));

    verify(questionnaireSessionService).findOne("123");
    verify(questionnaireSessionSaveService).save(session);
    verifyNoMoreInteractions(questionnaireSessionService, questionnaireSessionSaveService);
  }


  private void verifyCompleteAction(QuestionnaireSession session, String id) {
    verify(session).dispatchActions(Collections.singletonList(ImmutableAction.builder()
      .type(Action.Type.COMPLETE).id(id).build()));
  }

  private void verifyRemoveAnswersAction(QuestionnaireSession session) {
    verify(session).dispatchActions(Collections.singletonList(ActionsFactory.removeAnswers()));
  }

  private void verifyAnswerAction(QuestionnaireSession session, String questionId, Object newAnswer) {
    verify(session).dispatchActions(Collections.singletonList(ActionsFactory.answer(questionId, newAnswer)));
  }

  private void verifyGotoPageAction(QuestionnaireSession session, String gotoPage) {
    verify(session).dispatchActions(Collections.singletonList(ActionsFactory.gotoPage(gotoPage)));
  }

  private void verifyAddRowAction(QuestionnaireSession session, String questionId) {
    verify(session).dispatchActions(Collections.singletonList(ActionsFactory.addRow(questionId)));
  }

  private void verifyDeleteRowAction(QuestionnaireSession session, String rowId) {
    verify(session).dispatchActions(Collections.singletonList(ActionsFactory.deleteRow(rowId)));
  }

  private void verifySetValueAction(QuestionnaireSession session, String id, Object value) {
    verify(session).dispatchActions(Collections.singletonList(ActionsFactory.setValue(id,value)));
  }

  @Test
  void verifyAcceptableAnswerValues() {
    Assertions.assertTrue(controller.isValidAnswerValue(null));
    Assertions.assertTrue(controller.isValidAnswerValue("string"));
    Assertions.assertTrue(controller.isValidAnswerValue(1));
    Assertions.assertTrue(controller.isValidAnswerValue(1L));
    Assertions.assertTrue(controller.isValidAnswerValue(1.0));
    Assertions.assertTrue(controller.isValidAnswerValue(BigInteger.ONE));
    Assertions.assertTrue(controller.isValidAnswerValue(BigDecimal.ONE));
    Assertions.assertTrue(controller.isValidAnswerValue(true));
    Assertions.assertTrue(controller.isValidAnswerValue(false));
    Assertions.assertTrue(controller.isValidAnswerValue(Lists.newArrayList("a", "b")));
    Assertions.assertFalse(controller.isValidAnswerValue(Lists.newArrayList(1, "b")));
    Assertions.assertFalse(controller.isValidAnswerValue(Lists.newArrayList("1", 1)));
    Assertions.assertTrue(controller.isValidAnswerValue(Lists.newArrayList(null, "b")));
    Assertions.assertTrue(controller.isValidAnswerValue(Lists.newArrayList()));
  }

}
