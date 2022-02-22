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
package io.dialob.session.boot;

import static io.dialob.api.proto.Action.Type.ANSWER;
import static io.dialob.api.proto.Action.Type.COMPLETE;
import static io.dialob.api.proto.Action.Type.ERROR;
import static io.dialob.api.proto.Action.Type.ITEM;
import static io.dialob.api.proto.Action.Type.LOCALE;
import static io.dialob.api.proto.Action.Type.NEXT;
import static io.dialob.api.proto.Action.Type.REMOVE_ERROR;
import static io.dialob.api.proto.Action.Type.REMOVE_ITEMS;
import static io.dialob.api.proto.Action.Type.RESET;
import static io.dialob.api.proto.Action.Type.VALUE_SET;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.tuple;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.time.Clock;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.inject.Inject;

import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.AbstractIterableAssert;
import org.assertj.core.api.AbstractListAssert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.lang.NonNull;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.util.SerializationUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.protobuf.CodedInputStream;
import com.google.protobuf.CodedOutputStream;

import io.dialob.api.form.Form;
import io.dialob.api.form.ImmutableForm;
import io.dialob.api.form.ImmutableFormItem;
import io.dialob.api.form.ImmutableFormMetadata;
import io.dialob.api.form.ImmutableFormValueSet;
import io.dialob.api.form.ImmutableFormValueSetEntry;
import io.dialob.api.form.ImmutableValidation;
import io.dialob.api.form.ImmutableVariable;
import io.dialob.api.proto.Action;
import io.dialob.api.proto.ActionItem;
import io.dialob.api.proto.ImmutableValueSetEntry;
import io.dialob.api.questionnaire.ImmutableContextValue;
import io.dialob.api.questionnaire.ImmutableQuestionnaire;
import io.dialob.api.questionnaire.ImmutableQuestionnaireMetadata;
import io.dialob.api.questionnaire.Questionnaire;
import io.dialob.questionnaire.service.api.QuestionnaireDatabase;
import io.dialob.questionnaire.service.api.session.FormFinder;
import io.dialob.questionnaire.service.api.session.QuestionnaireSessionBuilderFactory;
import io.dialob.questionnaire.service.api.session.QuestionnaireSessionSaveService;
import io.dialob.questionnaire.service.api.session.QuestionnaireSessionService;
import io.dialob.rule.parser.api.ValueType;
import io.dialob.rule.parser.api.VariableNotDefinedException;
import io.dialob.rule.parser.function.DefaultFunctions;
import io.dialob.rule.parser.function.FunctionRegistry;
import io.dialob.security.tenant.CurrentTenant;
import io.dialob.security.user.UnauthenticatedCurrentUserProvider;
import io.dialob.session.engine.DialobProgramFromFormCompiler;
import io.dialob.session.engine.DialobProgramService;
import io.dialob.session.engine.QuestionnaireDialobProgramService;
import io.dialob.session.engine.program.DialobSessionEvalContextFactory;
import io.dialob.session.engine.session.model.DialobSession;
import io.dialob.session.engine.sp.AsyncFunctionInvoker;
import io.dialob.session.engine.sp.DialobQuestionnaireSession;
import io.dialob.session.engine.sp.DialobQuestionnaireSessionSaveService;
import io.dialob.session.engine.sp.DialobQuestionnaireSessionService;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {DialobQuestionnaireSessionServiceTest.TestConfiguration.class})
public class DialobQuestionnaireSessionServiceTest {


  @Configuration(proxyBeanMethods = false)
  public static class TestConfiguration {

    @Bean
    public FunctionRegistry functionRegistry() throws VariableNotDefinedException {
      final FunctionRegistry functionRegistry = mock(FunctionRegistry.class);

      when(functionRegistry.returnTypeOf("today")).thenReturn(ValueType.DATE);
      doAnswer(invocationOnMock -> {
        ((FunctionRegistry.FunctionCallback) invocationOnMock.getArgument(0)).succeeded(LocalDate.now());
        return null;
      }).when(functionRegistry).invokeFunction(any(FunctionRegistry.FunctionCallback.class), eq("today"));

      when(functionRegistry.returnTypeOf(eq("isLyt"),eq(ValueType.STRING))).thenReturn(ValueType.BOOLEAN);
      doAnswer(invocationOnMock -> {
        ((FunctionRegistry.FunctionCallback) invocationOnMock.getArgument(0)).succeeded(DefaultFunctions.isLyt(invocationOnMock.getArgument(1)));
        return null;
      }).when(functionRegistry).invokeFunction(any(FunctionRegistry.FunctionCallback.class), eq("isLyt"));

      when(functionRegistry.returnTypeOf(eq("isHetu"),eq(ValueType.STRING))).thenReturn(ValueType.BOOLEAN);
      doAnswer(invocationOnMock -> {
        ((FunctionRegistry.FunctionCallback) invocationOnMock.getArgument(0)).succeeded(DefaultFunctions.isHetu(invocationOnMock.getArgument(1)));
        return null;
      }).when(functionRegistry).invokeFunction(any(FunctionRegistry.FunctionCallback.class), eq("isHetu"));

      when(functionRegistry.returnTypeOf(eq("count"),eq(ValueType.arrayOf(ValueType.INTEGER)))).thenReturn(ValueType.INTEGER);
      doAnswer(invocationOnMock -> {
        ((FunctionRegistry.FunctionCallback) invocationOnMock.getArgument(0)).succeeded(DefaultFunctions.count(invocationOnMock.getArgument(1)));
        return null;
      }).when(functionRegistry).invokeFunction(any(FunctionRegistry.FunctionCallback.class), eq("count"));


      when(functionRegistry.returnTypeOf("lengthOf", ValueType.STRING)).thenReturn(ValueType.INTEGER);
      doAnswer(invocation -> {
        FunctionRegistry.FunctionCallback cb = invocation.getArgument(0);
        String string = invocation.getArgument(2);
        if (string == null) {
          cb.succeeded(0);
        } else {
          cb.succeeded(string.length());
        }
        return null;
      }).when(functionRegistry).invokeFunction(any(), eq("lengthOf"), any());

      when(functionRegistry.returnTypeOf("count", ValueType.arrayOf(ValueType.STRING))).thenReturn(ValueType.INTEGER);
      return functionRegistry;
    }

    @Bean
    public ObjectMapper objectMapper() {
      return new ObjectMapper().registerModules(new JavaTimeModule(), new Jdk8Module());
    }

    @Bean
    public DefaultFunctions defaultFunctions(FunctionRegistry functionRegistry) {
      return new DefaultFunctions(functionRegistry);
    }

    @Bean
    public DialobSessionEvalContextFactory sessionContextFactory(FunctionRegistry functionRegistry, AsyncFunctionInvoker asyncFunctionInvoker) {
      return new DialobSessionEvalContextFactory(functionRegistry, Clock.systemDefaultZone(), null);
    }

    @Bean
    public DialobProgramService dialobProgramService(FormFinder formFinder, FunctionRegistry functionRegistry) {
      return QuestionnaireDialobProgramService.newBuilder()
        .setFormDatabase(formFinder)
        .setProgramFromFormCompiler(new DialobProgramFromFormCompiler(functionRegistry))
        .build();
    }

    @Bean
    public AsyncFunctionInvoker asyncFunctionInvoker() {
      return mock(AsyncFunctionInvoker.class);
    }

    @Bean
    public FormFinder formFinder() {
      return mock(FormFinder.class);
    }

    @Bean
    public CurrentTenant currentTenant() {
      final CurrentTenant currentTenant = mock(CurrentTenant.class);
      Mockito.when(currentTenant.getId()).thenReturn("unitTest");
      return currentTenant;
    }

    @Bean
    public TaskExecutor taskExecutor() {
      return new SyncTaskExecutor();
    }

    @Bean
    public QuestionnaireSessionBuilderFactory questionnaireSessionBuilderFactory() {
      return mock(QuestionnaireSessionBuilderFactory.class);
    }

    @Bean
    public QuestionnaireSessionService questionnaireSessionService(@NonNull QuestionnaireDatabase questionnaireDatabase,
                                                                   @NonNull QuestionnaireSessionBuilderFactory questionnaireSessionBuilderFactory,
                                                                   @NonNull CurrentTenant currentTenant
    ) {
      return new DialobQuestionnaireSessionService(questionnaireDatabase, questionnaireSessionBuilderFactory, currentTenant);
    }

    @Bean
    public QuestionnaireSessionSaveService questionnaireSessionSaveService(@NonNull QuestionnaireDatabase questionnaireDatabase, CurrentTenant currentTenant) {
      return new DialobQuestionnaireSessionSaveService(questionnaireDatabase, currentTenant);
    }

    @Bean
    public QuestionnaireDatabase questionnaireDatabase() {
      return Mockito.mock(QuestionnaireDatabase.class);
    }
  }

  @Inject
  private FormFinder formFinder;

  @Inject
  private DialobSessionEvalContextFactory sessionContextFactory;

  @Inject
  private QuestionnaireDatabase questionnaireDatabase;

  @Inject
  private QuestionnaireSessionService questionnaireSessionService;

  @Inject
  private QuestionnaireSessionSaveService questionnaireSessionSaveService;

  @Inject
  private QuestionnaireDialobProgramService dialobProgramService;

  @Inject
  private ObjectMapper objectMapper;

  @Inject
  private TaskExecutor taskExecutor;

  @Inject
  private FunctionRegistry functionRegistry;

  @Inject
  private CurrentTenant currentTenant;

  private String tenantId = "123";

  @BeforeEach
  public void before() {
    MockitoAnnotations.initMocks(this);
  }

  protected FillAssertionBuilder fillForm(String formFile) throws java.io.IOException {
    return fillForm(objectMapper.readValue(Thread.currentThread().getContextClassLoader().getResourceAsStream(formFile), Form.class));
  }

  protected FillAssertionBuilder fillForm(Form formDocument) {
    String docId = UUID.randomUUID().toString();
    String formId = formDocument.getId();

    Mockito.reset(questionnaireDatabase, formFinder);
    when(questionnaireDatabase.save(anyString(), any(Questionnaire.class))).thenAnswer(invocation -> {
      ImmutableQuestionnaire questionnaire = (ImmutableQuestionnaire) invocation.getArguments()[1];
      return questionnaire.withId(docId);
    });

    when(formFinder.findForm(eq(formId), any())).thenReturn(formDocument);

    return new FillAssertionBuilder(currentTenant, dialobProgramService, formFinder, UnauthenticatedCurrentUserProvider.INSTANCE, formId, sessionContextFactory, questionnaireSessionSaveService);
  }

  protected FillAssertionBuilder fillForm(String formFile, String questionnaireState) throws java.io.IOException {
    Form formDocument = objectMapper.readValue(Thread.currentThread().getContextClassLoader().getResourceAsStream(formFile), Form.class);
    Questionnaire questionnaire = objectMapper.readValue(Thread.currentThread().getContextClassLoader().getResourceAsStream(questionnaireState), Questionnaire.class);
    return fillForm(formDocument, questionnaire);
  }

    protected FillAssertionBuilder fillForm(Form formDocument, Questionnaire questionnaire) throws java.io.IOException {
    String docId = UUID.randomUUID().toString();
    String formId = formDocument.getId();
    questionnaire = ImmutableQuestionnaire.builder().from(questionnaire).id(docId).metadata(ImmutableQuestionnaireMetadata.builder().from(questionnaire.getMetadata()).formId(formId).build()).build();

    Mockito.reset(questionnaireDatabase, formFinder);
    when(questionnaireDatabase.findOne(tenantId, docId)).thenReturn(questionnaire);

    when(formFinder.findForm(eq(formId), any())).thenReturn(formDocument);
    return new FillAssertionBuilder(currentTenant, dialobProgramService, formFinder, UnauthenticatedCurrentUserProvider.INSTANCE, questionnaire, sessionContextFactory, questionnaireSessionSaveService);
  }


  @Test
  public void issues127and128() throws Exception {
    fillForm("io/dialob/session/engine/issue-127.json")
      .assertState(assertion -> {
        assertion.hasSize(9)
          .extracting("type", "ids", "item.id", "item.label", "error.id").containsOnly(
          tuple(LOCALE, null, null, null, null),
          tuple(RESET, null, null, null, null),
          tuple(ITEM, null, "questionnaire", "Simple Example, FFRL", null),
          tuple(ITEM, null, "page1", "FIRST PAGE", null),
          tuple(ITEM, null, "group3", "Conclusions", null),
          tuple(ITEM, null, "group1", "Basic questions", null),
          tuple(ITEM, null, "question1", "What is your age?", null),
          tuple(VALUE_SET, null, null, null, null),
          tuple(ERROR, null, null, null, "question1")
        );
        questionnaire(assertion)
          .extracting("activeItem", "availableItems", "allowedActions").containsExactlyInAnyOrder(
          tuple("page1", asList("page1"), asSet(ANSWER))
        );
      })
      .answer("question1", "40")
      .assertThat(assertion -> assertion.hasSize(5)
        .extracting("type", "ids", "item.id", "item.label", "error.id").containsExactlyInAnyOrder(
          tuple(REMOVE_ERROR, null, null, null, "question1"),
          tuple(ITEM, null, "question3", "Are you married?", null),
          tuple(ITEM, null, "question2", "What is your yearly income?", null),
          tuple(ITEM, null, "group2", "Marital status", null),
          tuple(ERROR, null, null, null, "question3")
        ))
      .answer("question2", "60000")
      .assertThat(assertion -> assertion.hasSize(0)
        .extracting("type", "ids", "item.id", "item.label").containsExactlyInAnyOrder(
        ))
      .answer("question3", "true")
      .assertThat(assertion -> {
        assertion.hasSize(3)
          .extracting("type", "ids", "item.id", "item.label", "error.id").containsExactlyInAnyOrder(
          tuple(REMOVE_ERROR, null, null, null, "question3"),
          tuple(ITEM, null, "question4", "Is your spouse working?", null),
          tuple(ITEM, null, "questionnaire", "Simple Example, FFRL", null)
        );
        questionnaire(assertion)
          .extracting("activeItem", "availableItems", "allowedActions").containsExactlyInAnyOrder(
          tuple("page1", asList("page1"), asSet(COMPLETE, ANSWER))
        );
      })
      .answer("question4", "true")
      .assertThat(assertion -> assertion.hasSize(1)
        .extracting("type", "ids", "item.id", "item.label").containsExactlyInAnyOrder(
          tuple(ITEM, null, "question5", "What is your spouse's yearly income?")
        ))
      .answer("question5", "60000")
      .assertThat(assertion -> assertion.hasSize(2)
        .extracting("type", "ids", "item.id", "item.label").containsExactlyInAnyOrder(
          tuple(ITEM, null, "question11", "You are a wealthy couple"),
          tuple(ITEM, null, "question6", "In what line of business is your spouse working?")
        ))
      .answer("question6", "opt2")
      .assertThat(assertion -> {
        assertion.hasSize(2)
          .extracting("type", "ids", "item.id", "item.label").containsExactlyInAnyOrder(
          tuple(ITEM, null, "question9", "Insurance rocks! Check out the new page!"),
          tuple(ITEM, null, "questionnaire", "Simple Example, FFRL")
        );
        questionnaire(assertion)
          .extracting("activeItem", "availableItems", "allowedActions").containsExactlyInAnyOrder(
          tuple("page1", asList("page1", "page2"), asSet(COMPLETE, ANSWER, NEXT))
        );
      })
      .answer("question5", "59999")
      .assertThat(assertion -> assertion.hasSize(1)
        .extracting("type", "ids", "item.id", "item.label").containsExactlyInAnyOrder(
          tuple(REMOVE_ITEMS, Arrays.asList("question11"), null, null)
        ))
      .apply();
  }

  @Test
  public void issue21() throws Exception {
    fillForm("io/dialob/session/engine/issue21.json")
      .answer("question1", LocalDate.now().plusDays(2).toString())
      .assertThat(assertion -> {
        assertion.hasSize(2)
          .extracting("type", "ids", "item.id", "item.label", "error.id").containsExactlyInAnyOrder(
          tuple(ITEM, null, "questionnaire", "Kysely", null),
          tuple(ERROR, null, null, null, "question1")
        );
      })
      .nextPage()
      .assertThat(assertion -> {
        assertion.hasSize(0);  // error on page prevents next page
      })
      .answer("question1", LocalDate.now().toString())
      .assertThat(assertion -> {
        assertion.hasSize(2)
          .extracting("type", "ids", "item.id", "item.label", "error.id").containsExactlyInAnyOrder(
          tuple(REMOVE_ERROR, null, null, null, "question1"),
          tuple(ITEM, null, "questionnaire", "Kysely", null)
        );
      })
      .nextPage()
      .assertThat(assertion -> {
        assertion
          .extracting("type", "ids", "item.id", "item.label", "error.id").containsExactlyInAnyOrder(
          tuple(REMOVE_ITEMS, Arrays.asList("group1", "page1", "question1"), null, null, null),
          tuple(ITEM, null, "page2", "Second page", null),
          tuple(ITEM, null, "question2", "Sivu kysymys", null),
          tuple(ITEM, null, "group2", "New Group", null),
          tuple(ITEM, null, "questionnaire", "Kysely", null)
        );
      })
      .nextPage()
      .assertThat(assertion -> {
        assertion.hasSize(0);  // page4 not available
      })
      .previousPage()
      .assertThat(assertion -> {
        assertion
          .extracting("type", "ids", "item.id", "item.label", "error.id").containsExactlyInAnyOrder(
          tuple(REMOVE_ITEMS, Arrays.asList("page2", "group2", "question2"), null, null, null),
          tuple(ITEM, null, "page1", "New Page", null),
          tuple(ITEM, null, "question1", "New Question", null),
          tuple(ITEM, null, "group1", "New Group", null),
          tuple(ITEM, null, "questionnaire", "Kysely", null)
        );
      })
//      .answer("question1", LocalDate.now().toString())
      .answer("question1", "2019-01-01")
      .assertThat(assertion -> {
        assertion.hasSize(1)
          .extracting("type", "ids", "item.id", "item.label", "error.id").containsExactlyInAnyOrder(
          tuple(ITEM, null, "questionnaire", "Kysely", null)
        );
      })
      .nextPage()
      .assertThat(assertion -> {
        assertion.hasSize(5)
          .extracting("type", "ids", "item.id", "item.label", "error.id").containsExactlyInAnyOrder(
          tuple(REMOVE_ITEMS, Arrays.asList("group1", "page1", "question1"), null, null, null),
          tuple(ITEM, null, "page2", "Second page", null),
          tuple(ITEM, null, "question2", "Sivu kysymys", null),
          tuple(ITEM, null, "group2", "New Group", null),
          tuple(ITEM, null, "questionnaire", "Kysely", null)
        );
      })
      .nextPage()
      .assertThat(assertion -> {
        assertion.hasSize(7) // Now page4 is available, because question1 is whenValidUpdated
          .extracting("type", "ids", "item.id", "item.label", "error.id").containsExactlyInAnyOrder(
          tuple(REMOVE_ITEMS, Arrays.asList("page2", "group2", "question2"), null, null, null),
          tuple(ITEM, null, "page3", "third page", null),
          tuple(ITEM, null, "question3", "New Question", null),
          tuple(ITEM, null, "question4", "New Question", null),
          tuple(ITEM, null, "question5", "New Question", null),
          tuple(ITEM, null, "group3", "New Group", null),
          tuple(ITEM, null, "questionnaire", "Kysely", null)
        );
      })
      .apply();
  }

  @Test
  public void noteVariableTest() throws Exception {
    fillForm("io/dialob/session/engine/noteVariable.json")
      .answer("question1", "matches")
      .assertThat(assertion -> assertion.hasSize(0))
      .answer("question2", "2")
      .assertThat(assertion -> assertion
        .extracting("type", "ids", "item.id", "item.label").containsExactly(
          tuple(ITEM, null, "question3", "Answer of first question is matches 7")
        ))
      .answer("question1", "new")
      .assertThat(assertion -> assertion
        .extracting("type", "ids", "item.id", "item.label").containsExactly(
          tuple(ITEM, null, "question3", "Answer of first question is new 7")
        ))
      .apply();
  }

  @Test
  public void multichoiceQuestionTest() throws Exception {
    fillForm("io/dialob/session/engine/multichoice-question.json")
      .answer("mcquestion", Arrays.asList("A", "C"))
      .assertThat(assertion -> assertion.hasSize(0))
      .apply();
  }

  @Test
  public void multichoiceConstraintTestFeature44() throws Exception {
    fillForm("io/dialob/session/engine/multichoice-constraint.json")
      .answer("mcquestion", Arrays.asList("A", "C"))
      .assertThat(assertion -> assertion.hasSize(0))
      .answer("mcquestion", Arrays.asList("A", "C", "D"))
      .assertThat(assertion -> assertion
        .extracting("type", "item.id", "error.id").containsExactlyInAnyOrder(
          tuple(ITEM, "questionnaire", null),
          tuple(ERROR, null, "mcquestion")
        ))
      .answer("mcquestion", Arrays.asList("C", "D"))
      .assertThat(assertion -> assertion
        .extracting("type", "item.id", "error.id").containsExactlyInAnyOrder(
          tuple(ITEM, "questionnaire", null),
          tuple(REMOVE_ERROR, null, "mcquestion")
        ))
      .apply();
  }

  @Test
  public void hiddenGroupTest() throws Exception {
    fillForm("io/dialob/session/engine/hiddengroup.json")
      .answer("question1", true)
      .assertThat(assertion -> assertion
        .extracting("type", "item.id").containsExactlyInAnyOrder(
          tuple(ITEM, "question2"),
          tuple(ITEM, "group2")
        ))
      .answer("question2", true)
      .assertThat(assertion -> assertion
        .extracting("type", "item.id").containsExactly(
          tuple(ITEM, "question3")
        ))
      .answer("question1", false)
      .assertThat(assertion -> assertion.hasSize(1)
        .extracting("type", "ids").containsExactlyInAnyOrder( // question3 is also removed as it is controlled by a question from hidden group
          tuple(REMOVE_ITEMS, Arrays.asList("question3", "group2", "question2"))
        ))
      .apply();
  }


  @Test
  public void isValidInVariableExpression() throws Exception {
    fillForm("io/dialob/session/engine/is-valid-testing.json")
      .assertState(assertion -> assertion.extracting("type", "ids", "item.id", "item.disabled").containsExactlyInAnyOrder(
        tuple(RESET, null, null, null),
        tuple(LOCALE, null, null, null),
        tuple(ITEM, null, "questionnaire", null),
        tuple(ITEM, null, "page1", null),
        tuple(ITEM, null, "group1", null),
        tuple(ITEM, null, "question1", null),
        tuple(ITEM, null, "question3", null),
        tuple(ITEM, null, "question4", null),
        tuple(ERROR, null, null, null)
      ))
      .answer("question1", "x")
      .assertThat(assertion -> assertion
        .extracting("type", "ids", "item.id", "item.label").containsExactlyInAnyOrder(
          tuple(REMOVE_ERROR, null, null, null),
          tuple(ITEM, null, "question2", "Shown if previous ones are ok"),
          tuple(ITEM, null, "question4", "Question1 is ok true"),
          tuple(ITEM, null, "questionnaire", "is valid testing")
        ))
      .answer("question1", null)
      .assertThat(assertion -> assertion
        .extracting("type", "ids", "item.id", "item.label", "error.description").containsExactlyInAnyOrder(
          tuple(REMOVE_ITEMS, Arrays.asList("question2"), null, null, null),
          tuple(ITEM, null, "questionnaire", "is valid testing", null),
          tuple(ITEM, null, "question4", "Question1 is ok false", null),
          tuple(ERROR, null, null, null, "Fill in the missing information.")
        ))
      .answer("question1", "a")
      .assertThat(assertion -> assertion
        .extracting("type", "ids", "item.id", "item.label").containsExactlyInAnyOrder(
          tuple(REMOVE_ERROR, null, null, null),
          tuple(ITEM, null, "question2", "Shown if previous ones are ok"),
          tuple(ITEM, null, "question4", "Question1 is ok true"),
          tuple(ITEM, null, "questionnaire", "is valid testing")
        ))
      .answer("question3", "1970-01-01")
      .assertThat(assertion -> assertion.hasSize(0))
      .apply();
  }

  @Test
  public void shouldShowInactiveQuestions() throws Exception {
    fillForm("io/dialob/session/engine/inactive-present.json")
      .assertState(assertion -> assertion.extracting("type", "ids", "item.id", "item.disabled").containsExactlyInAnyOrder(
        tuple(RESET, null, null, null),
        tuple(LOCALE, null, null, null),
        tuple(ITEM, null, "questionnaire", null),
        tuple(ITEM, null, "page1", null),
        tuple(ITEM, null, "page2", true),
        tuple(ITEM, null, "group1", null),
        tuple(ITEM, null, "group2", true),
        tuple(ITEM, null, "question1", null),
        tuple(ITEM, null, "question2", null),
        tuple(ITEM, null, "question3", true),
        tuple(ITEM, null, "question4", true)
      ))
      .answer("question1", "ok")
      .assertThat(assertion -> assertion.hasSize(1).extracting("type", "ids").contains(
        tuple(REMOVE_ITEMS, Arrays.asList("question4"))
      ))
      .answer("question1", null)
      .assertThat(assertion -> assertion.hasSize(1).extracting("type", "item.id", "item.disabled").contains(
        tuple(ITEM, "question4", true)
      ))
      .nextPage()
      .assertThat(assertion -> assertion.extracting("type", "ids", "item.id", "item.disabled").containsExactlyInAnyOrder(
        tuple(ITEM, null, "questionnaire", null),
        tuple(ITEM, null, "page2", null),
        tuple(ITEM, null, "page1", true),
        tuple(ITEM, null, "group2", null),
        tuple(ITEM, null, "group1", true),
        tuple(ITEM, null, "question3", null),
        tuple(ITEM, null, "question4", null),
        tuple(ITEM, null, "question1", true),
        tuple(ITEM, null, "question2", true)
      ))
      .apply();
  }

  @Test
  public void shouldShowAllQuestions() throws Exception {
    fillForm("io/dialob/session/engine/show-all-items.json")
      .assertState(assertion -> assertion.extracting("type", "ids", "item.id", "item.disabled").containsExactlyInAnyOrder(
        tuple(RESET, null, null, null),
        tuple(LOCALE, null, null, null),
        tuple(ITEM, null, "questionnaire", null),
        tuple(ITEM, null, "page1", null),
        tuple(ITEM, null, "page2", true),
        tuple(ITEM, null, "group1", null),
        tuple(ITEM, null, "group2", true),
        tuple(ITEM, null, "question1", null),
        tuple(ITEM, null, "question2", null),
        tuple(ITEM, null, "question3", true),
        tuple(ITEM, null, "question4", true)
      ))
      .answer("question1", "ok")
      .assertThat(assertion -> assertion.hasSize(1).extracting("type", "item.id", "item.disabled", "item.inactive").contains(
        tuple(ITEM, "question4", true, true)
      ))
      .answer("question1", null)
      .assertThat(assertion -> assertion.hasSize(1).extracting("type", "item.id", "item.disabled", "item.inactive").contains(
        tuple(ITEM, "question4", true, null)
      ))
      .nextPage()
      .assertThat(assertion -> assertion.extracting("type", "ids", "item.id", "item.disabled", "item.inactive").containsExactlyInAnyOrder(
        tuple(ITEM, null, "questionnaire", null, null),
        tuple(ITEM, null, "page2", null, null),
        tuple(ITEM, null, "page1", true, null),
        tuple(ITEM, null, "group2", null, null),
        tuple(ITEM, null, "group1", true, null),
        tuple(ITEM, null, "question3", null, null),
        tuple(ITEM, null, "question4", null, null),
        tuple(ITEM, null, "question1", true, null),
        tuple(ITEM, null, "question2", true, null)
      ))
      .apply();
  }


  @Test
  public void shouldUseDefaultValueIfNotAnswered() throws Exception {
    fillForm("io/dialob/session/engine/default-values.json")
      .assertState(assertion -> assertion.hasSize(8).extracting("type", "ids", "item.id", "item.label").containsExactlyInAnyOrder(
        tuple(RESET, null, null, null),
        tuple(ITEM, null, "value1", "New Question"),
        tuple(ITEM, null, "value2", "New Question"),
        tuple(ITEM, null, "question1", "Sum is 0"),
        tuple(ITEM, null, "group1", "New Group"),
        tuple(ITEM, null, "page1", "New Page"),
        tuple(ITEM, null, "questionnaire", "New Dialog"),
        tuple(LOCALE, null, null, null)
      ))
      .answer("value1", "2")
      .assertThat(assertion -> assertion.hasSize(2).extracting("type", "item.id", "item.label").containsExactlyInAnyOrder(
        tuple(ITEM, "cond", "value1 is answered"),
        tuple(ITEM, "question1", "Sum is 2")
      ))
      .answer("value2", "5")
      .assertThat(assertion -> assertion.hasSize(1).extracting("type", "item.id", "item.label").containsExactlyInAnyOrder(
        tuple(ITEM, "question1", "Sum is 7")
      ))
      .answer("value1", null)
      .assertThat(assertion -> assertion.hasSize(2).extracting("type", "item.id", "item.label").containsExactlyInAnyOrder(
        tuple(REMOVE_ITEMS, null, null),
        tuple(ITEM, "question1", "Sum is 5")
      ))
      .answer("value2", null)
      .assertThat(assertion -> assertion.hasSize(1).extracting("type", "item.id", "item.label").containsExactlyInAnyOrder(
        tuple(ITEM, "question1", "Sum is 0")
      ))
      .apply();
  }

  @Test
  public void issue78() throws Exception {
    fillForm("io/dialob/session/engine/issue-78-form.json", "io/dialob/session/engine/issue-78-questionnaire.json")
      .assertState(assertion -> assertion.extracting("type", "item.id", "item.label", "item.required").contains(
        tuple(RESET, null, null, null),
        tuple(ITEM, "question1", "A", true),
        tuple(ITEM, "question3", "Hide B", null),
        tuple(ITEM, "question5", "sum=0", null)
      ).doesNotContain(
        tuple(ITEM, "question2", "B", null),
        tuple(ITEM, "question4", null, null)
      ))
      .answer("question1", 0L)
      .assertThat(assertion -> assertion.extracting("type", "item.id", "item.label").contains(
        tuple(ITEM, "question4", "A=0 B=0\n")
      ))
      .answer("question1b", 0L)
      .assertThat(assertion -> assertion.extracting("type", "item.id", "item.label").doesNotContain(
        tuple(ITEM, "question5", "sum=0"),
        tuple(ITEM, "question5", "sum=0")
      ))
      .answer("question3", false)
      .assertThat(assertion -> assertion.extracting("type", "item.id", "item.label").contains(
        tuple(ITEM, "question2", "B")
      ))
      .answer("question2", 1L)
      .assertThat(assertion -> assertion.extracting("type", "item.id", "item.label").contains(
        tuple(ITEM, "question4", "A=0 B=1\n"),
        tuple(ITEM, "question5", "sum=1")
      ))
      .answer("question2", null)
      .assertThat(assertion -> assertion.extracting("type", "item.id", "item.label").contains(
        tuple(ITEM, "question5", "sum=0")
      ))
      .apply();
  }

  @Test
  public void issue108() throws Exception {
    fillForm("io/dialob/session/engine/108-form.json", "io/dialob/session/engine/108-questionnaire.json")
      .assertState(assertion -> assertion.extracting("type", "ids", "item.id", "item.label", "item.activeItem", "item.availableItems").contains(
        tuple(RESET, null, null, null, null, null),
        tuple(ITEM, null, "questionnaire", "CompleteTest", "page1", Arrays.asList("page1"))
      ))
      .complete()
      .assertThat(assertion -> assertion.extracting("type").containsExactly(
        COMPLETE
      ))
      .apply();
  }

  @Test
  public void issue111() throws Exception {
    fillForm("io/dialob/session/engine/issue-111-form.json", "io/dialob/session/engine/issue-111-questionnaire.json")
      .assertState(assertion -> assertion.extracting("type", "item.id", "item.activeItem", "item.availableItems").contains(
        tuple(ITEM, "questionnaire", "page1", Arrays.asList("page1", "page2", "page5", "page6")
        ))
      ).apply();
  }

  @Test
  public void descriptionAndIssue221() throws Exception {
    fillForm("io/dialob/session/engine/description.json")
      .assertState(assertion -> assertion.extracting("type", "item.id", "item.label", "item.description", "item.view").contains(
        tuple(ITEM, "page1", "page label", "page description", "differentpage"),
        tuple(ITEM, "group1", "group label", "group description", null),
        tuple(ITEM, "text1", "question label", "question description", null),
        tuple(ITEM, "surveygroup1", "surveygroup label", "surveygroup description", null),
        tuple(ITEM, "rowgroup1", "rowgroup label", "rowgroup description", null)
      ))
      .apply();
  }

  @Test
  public void shouldCompile() throws Exception {
    final FillAssertionBuilder fillAssertionBuilder = fillForm("io/dialob/session/engine/79.json");
    assertNotNull(fillAssertionBuilder);
    fillAssertionBuilder.apply();
  }


  @Test
  public void multichoicecompile() throws IOException {
    final FillAssertionBuilder fillAssertionBuilder = fillForm("io/dialob/session/engine/multichoise.json");
    assertNotNull(fillAssertionBuilder);
  }

  @Test
  public void survey() throws Exception {
    fillForm("io/dialob/session/engine/survey.json")
      .assertState(assertion -> assertion.extracting("type", "item.id", "item.valueSetId").contains(
        tuple(RESET, null, null),
        tuple(ITEM, "questionnaire", null),
        tuple(ITEM, "page1", null),
        tuple(ITEM, "surveygroup1", "vs1"),
        tuple(ITEM, "survey1", null)
      ))
      .assertState(assertion -> assertion.extracting("type", "valueSet.id").contains(
        tuple(VALUE_SET, "vs1")
      ))
      .answer("survey1", "a")
      .assertThat(assertion -> assertion.hasSize(0))
      .apply();
  }

  @Test
  public void serializeTest() throws Exception {
    fillForm("io/dialob/session/engine/serialize.json")
      .assertState(assertion -> assertion.extracting("type", "item.id", "item.valueSetId").containsExactlyInAnyOrder(
        tuple(RESET, null, null),
        tuple(LOCALE, null, null),
        tuple(ITEM, "questionnaire", null),
        tuple(ITEM, "group1", null),
        tuple(ITEM, "page1", null),
        tuple(ITEM, "q2", null),
        tuple(ITEM, "q1", null),
        tuple(ITEM, "rg1", null),
        tuple(ITEM, "boolean1", null),
        tuple(ITEM, "number1", null),
        tuple(ITEM, "decimal1", null),
        tuple(ITEM, "date1", null),
        tuple(ITEM, "date2", null),
        tuple(ITEM, "note1", null),
        tuple(ITEM, "survey1", null),
        tuple(ITEM, "time1", null),
        tuple(ITEM, "group2", null),
        tuple(ITEM, "list1", null),
        tuple(ITEM, "multichoice1", null),
        tuple(ITEM, "text1", null),
        tuple(ITEM, "surveygroup1", "vs1"),
        tuple(VALUE_SET, null, null)

      ))
      .addRow("rg1")
      .assertThat(assertion -> assertion.extracting("type", "item.id").containsExactlyInAnyOrder(
        tuple(ITEM, "rg1.0.rgq1"),
        tuple(ITEM, "rg1.0"),
        tuple(ITEM, "rg1")
      ))
      .answer("text1", "answer here")
      .assertThat(assertion -> assertion.extracting("type", "item.id").containsExactlyInAnyOrder(
      ))
      .answer("date1", "2018-01-01")
      .assertThat(assertion -> assertion.extracting("type", "item.id").containsExactlyInAnyOrder(
      ))
      .answer("date2", "xxx")
      .assertThat(assertion -> assertion.extracting("type", "item.id").containsExactlyInAnyOrder(
      ))
      .answer("time1", "10:10:56.200")
      .assertThat(assertion -> assertion.extracting("type", "item.id").containsExactlyInAnyOrder(
      ))
      .answer("boolean1", true)
      .assertThat(assertion -> assertion.extracting("type", "item.id").containsExactlyInAnyOrder(
      ))
      .answer("number1", 10000)
      .assertThat(assertion -> assertion.extracting("type", "item.id").containsExactlyInAnyOrder(
      ))
      .answer("number1", 3.141)
      .assertThat(assertion -> assertion.extracting("type", "item.id").containsExactlyInAnyOrder(
      ))
      .answer("decimal1", 3.141)
      .assertThat(assertion -> assertion.extracting("type", "item.id").containsExactlyInAnyOrder(
      ))
      .answer("multichoice1", Arrays.asList("a", "c"))
      .assertThat(assertion -> assertion.extracting("type", "item.id").containsExactlyInAnyOrder(
      ))
      .checkSession(questionnaireSession -> {
        DialobQuestionnaireSession dialobQuestionnaireSession = (DialobQuestionnaireSession) questionnaireSession;

        long timeStart = System.nanoTime();
        final DialobSession dialobSession = dialobQuestionnaireSession.getDialobSession();
        byte[] sessionData = SerializationUtils.serialize(dialobSession);
        System.out.println("session serialize time " + (System.nanoTime() - timeStart) / 1e6 + ", size " + sessionData.length);

        timeStart = System.nanoTime();
        SerializationUtils.deserialize(sessionData);
        System.out.println("session deserialize time " + (System.nanoTime() - timeStart) / 1e6);

        try {
          final ByteBuffer byteBuffer = ByteBuffer.allocate(65536);
          CodedOutputStream codedOutputStream = CodedOutputStream.newInstance(byteBuffer);
          timeStart = System.nanoTime();
          dialobSession.writeTo(codedOutputStream);
          codedOutputStream.flush();
          System.out.println("protobuf session serialize time " + (System.nanoTime() - timeStart) / 1e6 + ", size " + codedOutputStream.getTotalBytesWritten());
          sessionData = byteBuffer.array();


          CodedInputStream input = CodedInputStream.newInstance(sessionData);
          timeStart = System.nanoTime();
          DialobSession readSession = DialobSession.readFrom(input);
          System.out.println("protobuf session deserialize time " + (System.nanoTime() - timeStart) / 1e6);
          assertNotSame(dialobSession, readSession);
          assertEquals(dialobSession, readSession);

        } catch (IOException e) {
          throw new RuntimeException(e);
        }
      }).
      assertState(assertion -> assertion.extracting("type", "item.id", "item.value", "item.answered")
        .containsExactlyInAnyOrder(
          tuple(RESET, null, null, null),
          tuple(LOCALE, null, null, null),
          tuple(ITEM, "boolean1", true, true),
          tuple(ITEM, "number1", 3.141, true),
          tuple(ITEM, "date1", "2018-01-01", true),
          tuple(ITEM, "date2", "xxx", false),
          tuple(ITEM, "note1", null, false),
          tuple(ITEM, "surveygroup1", null, false),
          tuple(ITEM, "group1", null, false),
          tuple(ITEM, "rg1.0.rgq1", null, false),
          tuple(ITEM, "time1", "10:10:56.200", true),
          tuple(ITEM, "page1", null, false),
          tuple(ITEM, "group2", null, false),
          tuple(ITEM, "rg1.0", null, false),
          tuple(ITEM, "survey1", null, false),
          tuple(ITEM, "list1", null, false),
          tuple(ITEM, "rg1", Arrays.asList(0), true),
          tuple(ITEM, "questionnaire", null, false),
          tuple(ITEM, "multichoice1", Arrays.asList("a", "c"), true),
          tuple(ITEM, "decimal1", 3.141, true),
          tuple(ITEM, "q2", null, false),
          tuple(ITEM, "q1", null, false),
          tuple(ITEM, "text1", "answer here", true),
          tuple(VALUE_SET, null, null, null)
        ))
      .apply();
  }

  @Test
  public void issue164() throws Exception {
    fillForm("io/dialob/session/engine/issue-164.json")
      .assertState(assertion -> {
        assertion.hasSize(9)
          .extracting("type", "ids", "item.id", "item.label", "error.id").containsExactlyInAnyOrder(
          tuple(LOCALE, null, null, null, null),
          tuple(RESET, null, null, null, null),
          tuple(ITEM, null, "questionnaire", "Testing all kind of things", null),
          tuple(ITEM, null, "number1", "Give a number", null),
          tuple(ITEM, null, "decimal1", "Give a decimal", null),
          tuple(ITEM, null, "group3", null, null),
          tuple(ITEM, null, "group2", null, null),
          tuple(ITEM, null, "group1", null, null),
          tuple(VALUE_SET, null, null, null, null)
        );
      })
      .answer("number1", "40")
      .assertThat(assertion -> assertion
        .extracting("type", "ids", "item.id", "item.label", "error.id").containsExactlyInAnyOrder(
          tuple(ITEM, null, "surveygroup1", "Take a survey", null),
          tuple(ITEM, null, "survey1", "Survey item1", null),
          tuple(ITEM, null, "survey2", "Survey item2", null)
        ))
      .answer("decimal1", "50")
      .assertThat(assertion -> assertion
        .extracting("type", "ids", "item.id", "item.label", "error.id").containsExactlyInAnyOrder(
          tuple(ITEM, null, "boolean1", "Yes of No", null),
          tuple(ITEM, null, "boolean2", "Yes of No 2", null)
        ))
      .apply();
  }

  @Test
  public void issue169() throws Exception {
    fillForm("io/dialob/session/engine/issue-169.json")
      .assertState(assertion -> {
        assertion
          .extracting("type", "ids", "item.id", "item.label", "error.id").containsExactlyInAnyOrder(
          tuple(LOCALE, null, null, null, null),
          tuple(RESET, null, null, null, null),
          tuple(ITEM, null, "questionnaire", "Testing all kind of things", null),
          tuple(ITEM, null, "decimal1", "Give a boolean", null),
          tuple(ITEM, null, "group1", null, null),
          tuple(VALUE_SET, null, null, null, null)
        );
      })
      .answer("decimal1", "true")
      .assertThat(assertion -> assertion
        .extracting("type", "ids", "item.id", "item.label", "error.id").containsExactlyInAnyOrder(
          tuple(ITEM, null, "surveygroup1", "Take a survey", null),
          tuple(ITEM, null, "survey1", "Survey item1", null)
        ))
      .answer("survey1", "first")
      .assertThat(assertion -> assertion
        .extracting("type", "ids", "item.id", "item.label", "error.id").containsExactlyInAnyOrder(
          tuple(ITEM, null, "boolean1", "Yes of No", null)
        ))
      .answer("decimal1", null)
      .assertThat(assertion -> assertion
        .extracting("type", "ids", "item.id", "item.label", "error.id").containsExactlyInAnyOrder(
          tuple(REMOVE_ITEMS, Arrays.asList("surveygroup1", "boolean1", "survey1"), null, null, null)
        ))
      .apply();
  }

  @Test
  public void issue170() throws Exception {
    fillForm("io/dialob/session/engine/issue-170.json")
      .assertState(assertion -> {
        assertion
          .extracting("type", "ids", "item.id", "item.label", "error.id").containsExactlyInAnyOrder(
          tuple(LOCALE, null, null, null, null),
          tuple(RESET, null, null, null, null),
          tuple(ITEM, null, "questionnaire", "in rule test", null),
          tuple(ITEM, null, "list1", null, null),
          tuple(ITEM, null, "group1", null, null),
          tuple(ITEM, null, "group2", null, null),
          tuple(VALUE_SET, null, null, null, null)
        );
      })
      .answer("list1", "c")
      .assertThat(assertion -> assertion.isEmpty())
      .answer("list1", "a")
      .assertThat(assertion -> assertion
        .extracting("type", "ids", "item.id", "item.label", "error.id").containsExactlyInAnyOrder(
          tuple(ITEM, null, "text1", "Whatever", null),
          tuple(ITEM, null, "group3", "Whatever group", null)
        ))
      .answer("list1", "b")
      .assertThat(assertion -> assertion.isEmpty())
      .answer("list1", "c")
      .assertThat(assertion -> assertion
        .extracting("type", "ids", "item.id", "item.label", "error.id").containsExactlyInAnyOrder(
          tuple(REMOVE_ITEMS, Arrays.asList("text1", "group3"), null, null, null)
        ))
      .answer("list1", null)
      .assertThat(assertion -> assertion.isEmpty())
      .apply();
  }

  @Test
  public void issue126() throws Exception {
    fillForm("io/dialob/session/engine/issue-126.json")
      .assertState(assertion -> {
        assertion
          .extracting("type", "ids", "item.id", "item.label", "error.id").containsExactlyInAnyOrder(
          tuple(LOCALE, null, null, null, null),
          tuple(RESET, null, null, null, null),
          tuple(ITEM, null, "questionnaire", "Issue 126: Is answered is not working in multi-row type", null),
          tuple(ITEM, null, "rowgroup1", null, null),
          tuple(ITEM, null, "group1", null, null),
          tuple(ITEM, null, "group2", null, null)
        );
      })
      .addRow("rowgroup1")
      .assertThat(assertion -> assertion
        .extracting("type", "ids", "item.id", "item.label", "item.items").containsExactlyInAnyOrder(
          tuple(ITEM, null, "rowgroup1.0.boolean1", "vastaus", null),
          tuple(ITEM, null, "rowgroup1.0", null, Arrays.asList("rowgroup1.0.text1", "rowgroup1.0.boolean1", "rowgroup1.0.number1")),
          tuple(ITEM, null, "rowgroup1.0.text1", "kysy", null),
          tuple(ITEM, null, "rowgroup1", null, Arrays.asList("rowgroup1.0"))
        ))
      .answer("rowgroup1.0.text1", "hello")
      .assertThat(assertion -> assertion
        .extracting("type", "ids", "item.id", "item.label", "item.items").containsExactlyInAnyOrder(
          tuple(ITEM, null, "rowgroup1.0.number1", null, null)
        ))
      .apply();
  }

  @Test
  public void issue175() throws Exception {
    fillForm("io/dialob/session/engine/issue-175.json")
      .assertState(assertion -> {
        assertion
          .extracting("type", "ids", "item.id", "item.label", "error.id").containsExactlyInAnyOrder(
          tuple(RESET, null, null, null, null),
          tuple(LOCALE, null, null, null, null),
          tuple(ITEM, null, "group1", null, null),
          tuple(ITEM, null, "questionnaire", "Testi1", null),
          tuple(ITEM, null, "number1", "Ika", null),
          tuple(ITEM, null, "carOwner", "Onko sinulla auto?", null),
          tuple(ITEM, null, "willToOwnCar", "Haluaisitko omistaa auton?", null),
          tuple(ITEM, null, "group2", "Auton omistaminen", null),
          tuple(VALUE_SET, null, null, null, null)
        );
      })
      .answer("carOwner", "true")
      .assertThat(assertion -> assertion
        .extracting("type", "ids", "item.id", "item.label", "error.id", "error.code").containsExactlyInAnyOrder(
          tuple(ITEM, null, "carBrand", "Minkä merkkinen auto sinulla on?", null, null),
          tuple(ITEM, null, "carModel", "Malli", null, null),
          tuple(ITEM, null, "questionnaire", "Testi1", null, null),
          tuple(ERROR, null, null, null, "carBrand", "REQUIRED")
        ))
      .answer("willToOwnCar", "No")
      .assertThat(assertion -> assertion
        .extracting("type", "ids", "item.id", "item.label", "error.id", "error.code").containsExactlyInAnyOrder(
          tuple(ERROR, null, null, null, "carBrand", "carBrand_error1")
        ))
      .apply();
  }

  @Test
  public void issue13() throws Exception {
    fillForm("io/dialob/session/engine/issue-13.json")
      .assertState(assertion -> {
        assertion
          .extracting("type", "ids", "item.id", "item.label", "error.id").containsExactlyInAnyOrder(
          tuple(RESET, null, null, null, null),
          tuple(LOCALE, null, null, null, null),
          tuple(ITEM, null, "group1", null, null),
          tuple(ITEM, null, "questionnaire", "Multirow testing", null),
          tuple(ITEM, null, "rowgroup1", null, null)
        );
      })
      .addRow("rowgroup1")
      .assertThat(assertion -> assertion
        .extracting("type", "ids", "item.id", "item.label", "error.id", "error.code").containsExactlyInAnyOrder(
          tuple(ITEM, null, "rowgroup1.0.requireChoise", null, null, null),
          tuple(ITEM, null, "rowgroup1.0.number1", null, null, null),
          tuple(ITEM, null, "rowgroup1.0.note1", null, null, null),
          tuple(ITEM, null, "rowgroup1.0", null, null, null),
          tuple(ITEM, null, "rowgroup1.0.text1", null, null, null),
          tuple(ITEM, null, "rowgroup1.0.list1", null, null, null),
          tuple(ITEM, null, "rowgroup1", null, null, null)
        ))
      .answer("rowgroup1.0.requireChoise","true")
      .assertThat(assertion -> assertion
        .extracting("type", "ids", "item.id", "item.label", "error.id", "error.code").containsExactlyInAnyOrder(
          tuple(ITEM, null, "questionnaire","Multirow testing", null, null),
          tuple(ITEM, null, "rowgroup1.0.list1", null, null, null),
          tuple(ERROR, null, null, null, "rowgroup1.0.list1", "REQUIRED")
        ))
      .answer("rowgroup1.0.number1","101")
      .assertThat(assertion -> assertion
        .extracting("type", "ids", "item.id", "item.label", "error.id", "error.code").containsExactlyInAnyOrder(
          tuple(ERROR, null, null, null, "rowgroup1.0.number1", "number1_error1")
        ))
      .answer("rowgroup1.0.number1","-10")
      .assertThat(assertion -> assertion
        .extracting("type", "ids", "item.id", "item.label", "error.id", "error.code").containsExactlyInAnyOrder(
          tuple(ERROR, null, null, null, "rowgroup1.0.list1", "list1_error1"),
          tuple(REMOVE_ERROR, null, null, null, "rowgroup1.0.number1", "number1_error1")
        ))
      .answer("rowgroup1.0.requireChoise","false")
      .assertThat(assertion -> assertion
        .extracting("type", "ids", "item.id", "item.label", "error.id", "error.code").containsExactlyInAnyOrder(
          tuple(REMOVE_ERROR, null, null, null, "rowgroup1.0.list1", "REQUIRED"),
          tuple(ITEM, null, "rowgroup1.0.list1", null, null, null)
        ))
      .apply();
  }

  @Test
  public void issue134() throws Exception {
    fillForm("io/dialob/session/engine/issue-134.json")
      .assertState(assertion -> {
        assertion
          .extracting("type", "ids", "item.id", "item.label", "item.allowedActions", "error.id").containsExactlyInAnyOrder(
          tuple(RESET, null, null, null, null, null),
          tuple(LOCALE, null, null, null, null, null),
          tuple(ITEM, null, "group1", null, null, null),
          tuple(ITEM, null, "questionnaire", "Test NEXT", Sets.newHashSet(Action.Type.ANSWER, Action.Type.NEXT, Action.Type.COMPLETE), null),
          tuple(ITEM, null, "group2", null, null, null),
          tuple(ITEM, null, "text1", null, null, null),
          tuple(ITEM, null, "enableError", null, null, null)
        );
      })
      .answer("enableError","true")
      .assertThat(assertion -> assertion
        .extracting("type", "ids", "item.id", "item.label", "item.allowedActions", "error.id").containsExactlyInAnyOrder(
          tuple(ITEM, null, "questionnaire", "Test NEXT", Sets.newHashSet(Action.Type.ANSWER), null),
          tuple(ERROR, null, null, null, null, "text1")
        ))
      .answer("enableError","false")
      .assertThat(assertion -> assertion
        .extracting("type", "ids", "item.id", "item.label", "item.allowedActions", "error.id").containsExactlyInAnyOrder(
          tuple(ITEM, null, "questionnaire", "Test NEXT", Sets.newHashSet(Action.Type.ANSWER, Action.Type.NEXT, Action.Type.COMPLETE), null),
          tuple(REMOVE_ERROR, null, null, null, null, "text1")
        ))
      .nextPage()
      .assertThat(assertion -> assertion
        .extracting("type", "ids", "item.id", "item.label", "item.allowedActions", "error.id").containsExactlyInAnyOrder(
          tuple(REMOVE_ITEMS, Arrays.asList("group1", "text1", "enableError", "group2"), null, null, null, null),
          tuple(ITEM, null, "page2Error", null, null, null),
          tuple(ITEM, null, "group5", null, null, null),
          tuple(ITEM, null, "group3", null, null, null),
          tuple(ITEM, null, "questionnaire", "Test NEXT", Sets.newHashSet(Action.Type.ANSWER, Action.Type.PREVIOUS, Action.Type.NEXT, Action.Type.COMPLETE), null)
        ))
      .answer("page2Error","error")
      .assertThat(assertion -> assertion
        .extracting("type", "ids", "item.id", "item.label", "item.allowedActions", "error.id").containsExactlyInAnyOrder(
          tuple(ITEM, null, "questionnaire", "Test NEXT", Sets.newHashSet(Action.Type.ANSWER, Action.Type.PREVIOUS), null),
          tuple(ERROR, null, null, null, null, "page2Error")
        ))
      .previousPage()
      .assertThat(assertion -> assertion
        .extracting("type", "ids", "item.id", "item.label", "item.allowedActions", "error.id").containsExactlyInAnyOrder(
          tuple(REMOVE_ITEMS, Arrays.asList("page2Error", "group5", "group3"), null, null, null, null),
          tuple(ITEM, null, "questionnaire", "Test NEXT", Sets.newHashSet(Action.Type.ANSWER, Action.Type.NEXT), null),
          tuple(ITEM, null, "group1", null, null, null),
          tuple(ITEM, null, "group2", null, null, null),
          tuple(ITEM, null, "text1", null, null, null),
          tuple(ITEM, null, "enableError", null, null, null),
          tuple(REMOVE_ERROR, null, null, null, null, "page2Error")
        ))
      .apply();
  }

  @Test
  public void issue158() throws Exception {
    fillForm("io/dialob/session/engine/issue-158.json")
      .assertState(assertion -> {
        assertion
          .extracting("type", "ids", "item.id", "item.label", "item.props").containsExactlyInAnyOrder(
          tuple(RESET, null, null, null, null),
          tuple(LOCALE, null, null, null, null),
          tuple(ITEM, null, "group1", "The Life of Human", Maps.toMap(Arrays.asList("test"), v -> 1)),
          tuple(ITEM, null, "questionnaire", "Perfect Logic", null),
          tuple(ITEM, null, "page1", "Logic", null),
          tuple(ITEM, null, "name", "Hello What is Your Name?", null)
        );
      })
      .answer("name","asdffdsa")
      .assertThat(assertion -> {
        assertion
          .extracting("type", "ids", "item.id", "item.label", "item.props").containsExactlyInAnyOrder(
          tuple(ITEM, null, "problem", "Do  You Have A Problem?", null)
        );
      })
      .answer("solveable",false)
      .answer("problem",false)
      .assertThat(assertion -> {
        assertion
          .extracting("type", "ids", "item.id", "item.label", "item.props").containsExactlyInAnyOrder(
          tuple(ITEM, null, "question3", "So Why Do You Worry asdffdsa?", Maps.toMap(Arrays.asList("test2"), v -> "2"))
        );
      })
      .apply();
  }

  @Test
  public void issue176() throws Exception {
    fillForm("io/dialob/session/engine/issue-176.json")
      .assertState(assertion -> {
        assertion
          .extracting("type", "ids", "item.id", "item.label", "error.id").containsExactlyInAnyOrder(
          tuple(RESET, null, null, null, null),
          tuple(LOCALE, null, null, null, null),
          tuple(ITEM, null, "group1", null, null),
          tuple(ITEM, null, "questionnaire", "MultiChoice", null),
          tuple(ITEM, null, "group2", null, null),
          tuple(ITEM, null, "multichoice1", "Test", null),
          tuple(VALUE_SET, null, null, null, null)
        );
      })
      .answer("multichoice1",Arrays.asList("b"))
      .assertThat(assertion -> assertion
        .extracting("type", "ids", "item.id", "item.label", "error.id", "error.code").containsExactlyInAnyOrder(
          tuple(ITEM, null, "text1", "Write something", null, null)
        ))
      .apply();
  }

  @Test
  public void issue187() throws Exception {
    fillForm("io/dialob/session/engine/issue-187.json", "io/dialob/session/engine/issue-187-questionnaire.json")
      .assertState(assertion -> {
        assertion
          .extracting("type", "ids", "item.id", "valueSet.id", "valueSet.entries").containsExactlyInAnyOrder(
          tuple(RESET, null, null, null, null),
          tuple(LOCALE, null, null, null, null),
          tuple(ITEM, null, "questionnaire", null, null),
          tuple(VALUE_SET, null, null, "vs1", Arrays.asList(ImmutableValueSetEntry.of("a","First"), ImmutableValueSetEntry.of("b","Second"), ImmutableValueSetEntry.of("c","Third"), ImmutableValueSetEntry.of("custom1","Custom 1"), ImmutableValueSetEntry.of("custom2","Custom 2")))
        );
      })
      .apply();
  }

  @Test
  public void testDynamicValuesetEntries() throws Exception {
    fillForm("io/dialob/session/engine/dynamic-valueset-entries.json")
      .assertState(assertion -> {
        assertion
          .extracting("type", "ids", "item.id", "valueSet.id", "valueSet.entries").contains(
          tuple(VALUE_SET, null, null, "vs1", Arrays.asList(ImmutableValueSetEntry.of("a","First"), ImmutableValueSetEntry.of("c","Third")))
        );
        //, ImmutableValueSetEntry.of("b","Second"), ImmutableValueSetEntry.of("c","Third"), ImmutableValueSetEntry.of("custom1","Custom 1"), ImmutableValueSetEntry.of("custom2","Custom 2")
      })
      .answer("text1","b is ok")
      .assertThat(assertion -> assertion
        .extracting("type", "ids", "item.id", "valueSet.id", "valueSet.entries").contains(
          tuple(VALUE_SET, null, null, "vs1", Arrays.asList(ImmutableValueSetEntry.of("a","First"), ImmutableValueSetEntry.of("b","Second")))
        ))
      .answer("text1","b is not ok")
      .assertThat(assertion -> assertion
        .extracting("type", "ids", "item.id", "valueSet.id", "valueSet.entries").contains(
          tuple(VALUE_SET, null, null, "vs1", Arrays.asList(ImmutableValueSetEntry.of("a","First"), ImmutableValueSetEntry.of("c","Third")))
        ))
      .apply();
  }

  @Test
  public void testPublishedVariables() throws Exception {
    fillForm(ImmutableForm.builder()
      .id("test")
      .metadata(ImmutableFormMetadata.builder()
        .label("test")
        .build())
        .putData("questionnaire", ImmutableFormItem.builder()
          .id("questionnaire")
          .type("questionnaire")
          .addItems("gg")
          .build())
        .putData("gg", ImmutableFormItem.builder()
          .id("gg")
          .type("group")
          .addItems("qq")
          .build())
        .putData("qq", ImmutableFormItem.builder()
          .id("qq")
          .type("number")
          .build())
      .addVariables(
        ImmutableVariable.builder()
          .context(true)
          .name("ctx")
          .published(true)
          .contextType("text")
          .build(),
        ImmutableVariable.builder()
          .context(false)
          .name("var")
          .published(true)
          .expression("5 + qq")
          .build(),
        ImmutableVariable.builder()
          .context(true)
          .name("ctx2")
          .contextType("text")
          .build(),
        ImmutableVariable.builder()
          .context(false)
          .name("var2")
          .expression("'xxx'")
          .build()
      )
      .build(),
      ImmutableQuestionnaire.builder()
        .metadata(ImmutableQuestionnaireMetadata.builder()
          .formId("test")
          .build())
        .addContext(ImmutableContextValue.builder()
          .id("ctx")
          .value("testivalue")
          .build())
        .build())
      .assertState(assertion -> {
        assertion
          .extracting("type", "ids", "item.id", "item.value").containsExactlyInAnyOrder(
          tuple(RESET, null, null, null),
          tuple(LOCALE, null, null, null),
          tuple(ITEM, null, "questionnaire", null),
          tuple(ITEM, null, "ctx", "testivalue"),
          tuple(ITEM, null, "var",null),
          tuple(ITEM, null, "gg", null),
          tuple(ITEM, null, "qq", null)
        );
      })
      .answer("qq", 6)
      .assertThat(assertion -> assertion
        .extracting("type", "ids", "item.id", "item.value").containsExactlyInAnyOrder(
          tuple(ITEM, null, "var",11)
        ))
      .apply();
  }


  @Test
  public void testReducer() throws Exception {
    fillForm(ImmutableForm.builder()
        .id("test")
        .metadata(ImmutableFormMetadata.builder()
          .label("test")
          .build())
        .putData("questionnaire", ImmutableFormItem.builder()
          .id("questionnaire")
          .type("questionnaire")
          .addItems("g")
          .build())
        .putData("g", ImmutableFormItem.builder()
          .id("g")
          .type("group")
          .addItems("rg")
          .addItems("note1")
          .build())
        .putData("rg", ImmutableFormItem.builder()
          .id("rg")
          .type("rowgroup")
          .addItems("qq")
          .build())
        .putData("qq", ImmutableFormItem.builder()
          .id("qq")
          .type("number")
          .build())
        .putData("note1", ImmutableFormItem.builder()
          .id("note1")
          .activeWhen("sum of qq > 100")
          .type("note")
          .build())
        .build(),
      ImmutableQuestionnaire.builder()
        .metadata(ImmutableQuestionnaireMetadata.builder()
          .formId("test")
          .build())
        .build())
      .assertState(assertion -> {
        assertion
          .extracting("type", "ids", "item.id", "item.value").containsExactlyInAnyOrder(
          tuple(RESET, null, null, null),
          tuple(LOCALE, null, null, null),
          tuple(ITEM, null, "questionnaire", null),
          tuple(ITEM, null, "rg", null),
          tuple(ITEM, null, "g", null)
        );
      })
      .addRow("rg")
      .assertThat(assertion -> assertion
        .extracting("type", "ids", "item.id", "item.value").containsExactlyInAnyOrder(
          tuple(ITEM, null, "rg.0.qq", null),
          tuple(ITEM, null, "rg.0", null),
          tuple(ITEM, null, "rg", Arrays.asList(0))
        ))
      .addRow("rg")
      .assertThat(assertion -> assertion
        .extracting("type", "ids", "item.id", "item.value").containsExactlyInAnyOrder(
          tuple(ITEM, null, "rg.1.qq", null),
          tuple(ITEM, null, "rg.1", null),
          tuple(ITEM, null, "rg", Arrays.asList(0,1))
        ))
      .addRow("rg")
      .assertThat(assertion -> assertion
        .extracting("type", "ids", "item.id", "item.value").containsExactlyInAnyOrder(
          tuple(ITEM, null, "rg.2.qq", null),
          tuple(ITEM, null, "rg.2", null),
          tuple(ITEM, null, "rg", Arrays.asList(0,1,2))
        ))
      .answer("rg.0.qq", 50)
      .assertThat(assertion -> assertion
        .extracting("type", "ids", "item.id", "item.value").containsExactlyInAnyOrder(
        ))
      .answer("rg.1.qq", 51)
      .assertThat(assertion -> assertion
        .extracting("type", "ids", "item.id", "item.value").containsExactlyInAnyOrder(
          tuple(ITEM, null, "note1", null)
        ))
      .deleteRow("rg.0")
      .assertThat(assertion -> assertion
        .extracting("type", "ids", "item.id", "item.value").containsExactlyInAnyOrder(
          tuple(REMOVE_ITEMS, Arrays.asList("rg.0.qq", "rg.0", "note1"), null, null),
          tuple(ITEM, null, "rg", Arrays.asList(1,2))
        ))
      .apply();
  }

  @Test
  public void testRowCount() throws Exception {
    fillForm(ImmutableForm.builder()
        .id("test")
        .metadata(ImmutableFormMetadata.builder()
          .label("test")
          .build())
        .putData("questionnaire", ImmutableFormItem.builder()
          .id("questionnaire")
          .type("questionnaire")
          .addItems("g")
          .build())
        .putData("g", ImmutableFormItem.builder()
          .id("g")
          .type("group")
          .addItems("rg")
          .addItems("note1")
          .build())
        .putData("rg", ImmutableFormItem.builder()
          .id("rg")
          .type("rowgroup")
          .addItems("qq")
          .build())
        .putData("qq", ImmutableFormItem.builder()
          .id("qq")
          .type("number")
          .build())
        .putData("note1", ImmutableFormItem.builder()
          .id("note1")
          .label(Map.of("en","{rc}"))
          .type("note")
          .build())
        .addVariables(ImmutableVariable.of("rc", "count(rg)"))
        .build(),
      ImmutableQuestionnaire.builder()
        .metadata(ImmutableQuestionnaireMetadata.builder()
          .formId("test")
          .build())
        .build())
      .assertState(assertion -> {
        assertion
          .extracting("type", "ids", "item.id", "item.label").containsExactlyInAnyOrder(
          tuple(RESET, null, null, null),
          tuple(LOCALE, null, null, null),
          tuple(ITEM, null, "questionnaire", "test"),
          tuple(ITEM, null, "rg", null),
          tuple(ITEM, null, "g", null),
          tuple(ITEM, null, "note1", "0")
        );
      })
      .addRow("rg")
      .assertThat(assertion -> assertion
        .extracting("type", "ids", "item.id", "item.label").containsExactlyInAnyOrder(
          tuple(ITEM, null, "rg.0.qq", null),
          tuple(ITEM, null, "rg.0", null),
          tuple(ITEM, null, "rg", null),
          tuple(ITEM, null, "note1", "1")
        ))
      .addRow("rg")
      .assertThat(assertion -> assertion
        .extracting("type", "ids", "item.id", "item.label").containsExactlyInAnyOrder(
          tuple(ITEM, null, "rg.1.qq", null),
          tuple(ITEM, null, "rg.1", null),
          tuple(ITEM, null, "rg", null),
          tuple(ITEM, null, "note1", "2")
        ))
      .addRow("rg")
      .assertThat(assertion -> assertion
        .extracting("type", "ids", "item.id", "item.label").containsExactlyInAnyOrder(
          tuple(ITEM, null, "rg.2.qq", null),
          tuple(ITEM, null, "rg.2", null),
          tuple(ITEM, null, "rg", null),
          tuple(ITEM, null, "note1", "3")
        ))
      .deleteRow("rg.2")
      .assertThat(assertion -> assertion
        .extracting("type", "ids", "item.id", "item.label").containsExactlyInAnyOrder(
          tuple(REMOVE_ITEMS, Arrays.asList("rg.2", "rg.2.qq"), null, null),
          tuple(ITEM, null, "rg", null),
          tuple(ITEM, null, "note1", "2")
        ))
      .deleteRow("rg.0")
      .assertThat(assertion -> assertion
        .extracting("type", "ids", "item.id", "item.label").containsExactlyInAnyOrder(
          tuple(REMOVE_ITEMS, Arrays.asList("rg.0.qq", "rg.0"), null, null),
          tuple(ITEM, null, "rg", null),
          tuple(ITEM, null, "note1", "1")
        ))
      .apply();
  }


  @Test
  public void testMultiChoiceCount() throws Exception {
    fillForm(ImmutableForm.builder()
        .id("test")
        .metadata(ImmutableFormMetadata.builder()
          .label("test")
          .build())
        .putData("questionnaire", ImmutableFormItem.builder()
          .id("questionnaire")
          .type("questionnaire")
          .addItems("g")
          .build())
        .putData("g", ImmutableFormItem.builder()
          .id("g")
          .type("group")
          .addItems("mc")
          .addItems("note1")
          .addItems("note2")
          .addItems("text1")
          .build())
        .putData("note1", ImmutableFormItem.builder()
          .id("note1")
          .type("note")
          .putLabel("en","{rc}")
          .build())
        .putData("note2", ImmutableFormItem.builder()
          .id("note2")
          .type("note")
          .putLabel("en","wohoo")
          .activeWhen("count(mc) > 0")
          .build())
        .putData("text1", ImmutableFormItem.builder()
          .id("text1")
          .type("text")
          .putLabel("en","?")
          .addValidations(ImmutableValidation.builder()
            .putMessage("en","err")
            .rule("count(mc) = 3")
            .build())
          .build())
        .putData("mc", ImmutableFormItem.builder()
          .id("mc")
          .type("multichoice")
          .valueSetId("vs")
          .build())
        .addVariables(ImmutableVariable.of("rc", "count(mc)"))
        .addValueSets(ImmutableFormValueSet.builder()
          .id("vs")
          .addEntries(
            ImmutableFormValueSetEntry.builder()
              .id("1")
              .putLabel("en","one")
              .build(),
            ImmutableFormValueSetEntry.builder()
              .id("2")
              .putLabel("en","two")
              .build(),
            ImmutableFormValueSetEntry.builder()
              .id("3")
              .putLabel("en","three")
              .build(),
            ImmutableFormValueSetEntry.builder()
              .id("4")
              .putLabel("en","four")
              .build()
          )
          .build())
        .build(),
      ImmutableQuestionnaire.builder()
        .metadata(ImmutableQuestionnaireMetadata.builder()
          .formId("test")
          .build())
        .build())
      .assertState(assertion -> {
        assertion
          .extracting("type", "ids", "item.id", "item.label").containsExactlyInAnyOrder(
          tuple(RESET, null, null, null),
          tuple(LOCALE, null, null, null),
          tuple(ITEM, null, "questionnaire", "test"),
          tuple(ITEM, null, "mc", null),
          tuple(ITEM, null, "g", null),
          tuple(ITEM, null, "note1", "0"),
          tuple(ITEM, null, "text1", "?"),
          tuple(VALUE_SET, null, null, null)
        );
      })
      .answer("mc",Arrays.asList("1"))
      .assertThat(assertion -> assertion
        .extracting("type", "ids", "item.id", "item.label").containsExactlyInAnyOrder(
          tuple(ITEM, null, "note2", "wohoo"),
          tuple(ITEM, null, "note1", "1")
        ))
      .answer("mc",Arrays.asList("1","2","4"))
      .assertThat(assertion -> assertion
        .extracting("type", "ids", "item.id", "item.label").containsExactlyInAnyOrder(
          tuple(ITEM, null, "note1", "3"),
          tuple(ITEM, null, "questionnaire", "test"),
          tuple(ERROR, null, null, null)
        ))
      .answer("mc",Arrays.asList())
      .assertThat(assertion -> assertion
        .extracting("type", "ids", "item.id", "item.label").containsExactlyInAnyOrder(
          tuple(REMOVE_ERROR, null, null, null),
          tuple(REMOVE_ITEMS, Arrays.asList("note2"), null, null),
          tuple(ITEM, null, "questionnaire", "test"),
          tuple(ITEM, null, "note1", "0")
        ))
      .apply();
  }


  @Test
  public void issue284() throws Exception {
    fillForm("io/dialob/session/engine/issue-284.json")
      .assertState(assertion -> {
        assertion
          .extracting("type", "ids", "item.id", "item.label").containsExactlyInAnyOrder(
            tuple(RESET, null, null, null),
            tuple(LOCALE, null, null, null),
            tuple(ITEM, null, "questionnaire", "Uusin test2"),
            tuple(ITEM, null, "page1", "Tämä on sivu"),
            tuple(ITEM, null, "rowgroup1", null),
            tuple(VALUE_SET, null, null, null)
        );
      })
      .addRow("rowgroup1")
      .assertThat(assertion -> assertion
        .extracting("type", "ids", "item.id", "item.label").containsExactlyInAnyOrder(
          tuple(ITEM, null, "rowgroup1.0.number1", null),
          tuple(ITEM, null, "rowgroup1.0", null),
          tuple(ITEM, null, "rowgroup1", null)
        ))
      .answer("rowgroup1.0.number1",11)
      .assertThat(assertion -> assertion
        .extracting("type", "ids", "item.id", "item.label").containsExactlyInAnyOrder(
          tuple(ITEM, null, "questionnaire", "Uusin test2"),
          tuple(ERROR, null, null, null)
        ))
      .answer("rowgroup1.0.number1",10)
      .assertThat(assertion -> assertion
        .extracting("type", "ids", "item.id", "item.label").containsExactlyInAnyOrder(
          tuple(REMOVE_ERROR, null, null, null),
          tuple(ITEM, null, "rowgroup1.0.multichoice2", null),
          tuple(ITEM, null, "questionnaire", "Uusin test2")
        ))
      .apply();
  }


  @Test
  public void cannotCompleteWhenQuestionnaireHasMissingAnswers() throws Exception {
    fillForm(ImmutableForm.builder()
        .id("test")
        .metadata(ImmutableFormMetadata.builder()
          .label("test")
          .build())
        .putData("questionnaire", ImmutableFormItem.builder()
          .id("questionnaire")
          .type("questionnaire")
          .addItems("page1")
          .addItems("page2")
          .build())
        .putData("page1", ImmutableFormItem.builder()
          .id("page1")
          .type("page")
          .addItems("group1")
          .build())
        .putData("page2", ImmutableFormItem.builder()
          .id("page2")
          .type("page")
          .addItems("group2")
          .build())
        .putData("group1", ImmutableFormItem.builder()
          .id("group1")
          .type("group")
          .addItems("text1")
          .build())
        .putData("group2", ImmutableFormItem.builder()
          .id("group2")
          .type("group")
          .addItems("text2")
          .build())
        .putData("text1", ImmutableFormItem.builder()
          .id("text1")
          .type("text")
          .putLabel("en","1")
          .required("false")
          .build())
        .putData("text2", ImmutableFormItem.builder()
          .id("text2")
          .type("text")
          .putLabel("en","2")
          .required("true")
          .build())
        .build(),
      ImmutableQuestionnaire.builder()
        .metadata(ImmutableQuestionnaireMetadata.builder()
          .formId("test")
          .build())
        .build())
      .assertState(assertion -> {
        assertion
          .extracting("type", "ids", "item.id", "item.label").containsExactlyInAnyOrder(
          tuple(RESET, null, null, null),
          tuple(LOCALE, null, null, null),
          tuple(ITEM, null, "group1", null),
          tuple(ITEM, null, "text1", "1"),
          tuple(ITEM, null, "page1", null),
          tuple(ITEM, null, "questionnaire", "test"),
          tuple(ERROR, null, null, null)
        );
      })
      .complete(false) // we are not expecting completion here
      .assertThat(AbstractIterableAssert::isEmpty)
      .nextPage()
      .assertThat(assertion -> assertion
        .extracting("type", "ids", "item.id", "item.label").containsExactlyInAnyOrder(
          tuple(REMOVE_ITEMS, Arrays.asList("group1", "text1", "page1"), null, null),
          tuple(ITEM, null, "text2", "2"),
          tuple(ITEM, null, "page2", null),
          tuple(ITEM, null, "group2", null),
          tuple(ITEM, null, "questionnaire", "test"),
          tuple(ERROR, null, null, null)
        ))
      .answer("text2","Hello")
      .assertThat(assertion -> assertion
        .extracting("type", "ids", "item.id", "item.label").containsExactlyInAnyOrder(
          tuple(ITEM, null, "questionnaire", "test"),
          tuple(REMOVE_ERROR, null, null, null)
        ))
      .complete(true) // now we can complete
      .assertThat(assertion -> assertion
        .extracting("type", "ids", "item.id", "item.label").containsExactlyInAnyOrder(
          tuple(COMPLETE, null, null, null)
        ))
      .apply();
  }


  protected AbstractListAssert<?, List<?>, ?, ? extends AbstractAssert<?, ?>> questionnaire(AbstractListAssert<?, ? extends List<? extends Action>, Action, ? extends AbstractAssert<?, Action>> assertion) {
    return assertion.extracting("item").filteredOn(instance -> instance != null && "questionnaire".equals(((ActionItem) instance).getType()));
  }

  private <T> Set<T> asSet(T... items) {
    HashSet hashSet = new HashSet<T>();
    hashSet.addAll(asList(items));
    return hashSet;
  }

}
