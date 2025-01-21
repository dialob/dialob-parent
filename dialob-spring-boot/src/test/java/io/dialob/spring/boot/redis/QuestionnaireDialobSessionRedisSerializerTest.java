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
package io.dialob.spring.boot.redis;

import io.dialob.api.form.ImmutableForm;
import io.dialob.api.form.ImmutableFormMetadata;
import io.dialob.api.questionnaire.ImmutableQuestionnaire;
import io.dialob.api.questionnaire.ImmutableQuestionnaireMetadata;
import io.dialob.api.questionnaire.Questionnaire;
import io.dialob.form.service.api.FormDatabase;
import io.dialob.questionnaire.service.api.QuestionnaireDatabase;
import io.dialob.questionnaire.service.api.event.QuestionnaireEventPublisher;
import io.dialob.questionnaire.service.api.session.QuestionnaireSession;
import io.dialob.questionnaire.service.api.session.QuestionnaireSessionBuilderFactory;
import io.dialob.questionnaire.service.api.session.QuestionnaireSessionSaveService;
import io.dialob.questionnaire.service.api.session.QuestionnaireSessionService;
import io.dialob.rule.parser.function.FunctionRegistry;
import io.dialob.security.tenant.CurrentTenant;
import io.dialob.session.engine.DialobProgramService;
import io.dialob.session.engine.program.DialobProgram;
import io.dialob.session.engine.program.DialobSessionEvalContext;
import io.dialob.session.engine.program.DialobSessionEvalContextFactory;
import io.dialob.session.engine.program.ProgramBuilder;
import io.dialob.session.engine.session.ActiveDialobSessionUpdater;
import io.dialob.session.engine.sp.AsyncFunctionInvoker;
import io.dialob.session.engine.sp.DialobQuestionnaireSession;
import io.dialob.session.engine.sp.DialobQuestionnaireSessionBuilder;
import io.dialob.session.engine.sp.DialobQuestionnaireSessionService;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.context.ApplicationContext;

import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class QuestionnaireDialobSessionRedisSerializerTest {

  @Mock
  DialobProgramService dialobProgramService;

  @Mock
  QuestionnaireEventPublisher eventPublisher;

  @Mock
  FunctionRegistry functionRegistry;

  @Mock
  QuestionnaireDatabase questionnaireDatabase;

  @Mock
  FormDatabase formDatabase;

  @Mock
  DialobSessionEvalContextFactory sessionContextFactory;

  @Mock
  QuestionnaireSessionService questionnaireSessionService;

  @Mock
  QuestionnaireSessionSaveService questionnaireSessionSaveService;

  @Mock
  CurrentTenant currentTenant;

  DialobQuestionnaireSessionService dialobQuestionnaireSessionService;

  QuestionnaireSessionBuilderFactory questionnaireSessionBuilderFactory;

  @Mock
  AsyncFunctionInvoker asyncFunctionInvoker;

  String tenantId = "123";

  @BeforeEach
  public void beforeEach() {
    MockitoAnnotations.initMocks(this);

    ApplicationContext applicationContext = Mockito.mock(ApplicationContext.class);

    questionnaireSessionBuilderFactory = () -> new DialobQuestionnaireSessionBuilder(eventPublisher,
      dialobProgramService,
      (id, rev) -> formDatabase.findOne(tenantId, id, rev),
      questionnaireSessionSaveService,
            sessionContextFactory,
      asyncFunctionInvoker
    );

    dialobQuestionnaireSessionService = new DialobQuestionnaireSessionService(
      questionnaireDatabase,
      questionnaireSessionBuilderFactory,
      currentTenant);

    when(applicationContext.getBean(QuestionnaireSessionService.class)).thenReturn(dialobQuestionnaireSessionService);
  }

  @Test
  public void shouldAcceptNullValues() {
    QuestionnaireDialobSessionRedisSerializer serializer = getQuestionnaireDialobSessionRedisSerializer(65536);

    Assertions.assertNull(serializer.serialize(null));
    Assertions.assertNull(serializer.deserialize(null));

    Mockito.verifyNoMoreInteractions(dialobProgramService, eventPublisher, functionRegistry, questionnaireDatabase, formDatabase, sessionContextFactory);
  }

  @Test
  public void shouldSerializeSimpleSession() {
    final DialobSessionEvalContext evalContext = Mockito.mock(DialobSessionEvalContext.class);

    final QuestionnaireDialobSessionRedisSerializer serializer = getQuestionnaireDialobSessionRedisSerializer(65536);
    ProgramBuilder programBuilder = new ProgramBuilder(functionRegistry);
    final DialobProgram dialobProgram = DialobProgram.createDialobProgram(
      programBuilder
        .setId("test-form")
        .addRoot()
          .addItem("p1")
        .build()
        .build());

    final ImmutableForm form = ImmutableForm.builder()
      .id("test-form")
      .metadata(ImmutableFormMetadata.builder()
        .label("test form")
        .build())
      .build();
    when(formDatabase.findOne(eq(tenantId), eq("test-form"), isNull())).thenReturn(form);
    when(dialobProgramService.findByFormIdAndRev(eq("test-form"), isNull())).thenReturn(dialobProgram);

    when(sessionContextFactory.createSessionUpdater(same(dialobProgram), any(), anyBoolean()))
      .thenAnswer(invocation -> new ActiveDialobSessionUpdater((e) -> evalContext, dialobProgram));

    //     return ;
    final QuestionnaireSession session = questionnaireSessionBuilderFactory.createQuestionnaireSessionBuilder()
      .formId("test-form")
      .questionnaire(ImmutableQuestionnaire.builder()
        .id("questionnaire-12")
        .metadata(ImmutableQuestionnaireMetadata.builder()
          .formId("test-form")
          .owner("tester")
          .created(new Date(1))
          .lastAnswer(new Date(2))
          .status(Questionnaire.Metadata.Status.OPEN)
          .creator("creator")
          .putAdditionalProperties("showDisabled", true)
          .build())
        .build())
      .build();
    final byte[] bytes = serializer.serialize((DialobQuestionnaireSession) session);
    DialobQuestionnaireSession session2 = serializer.deserialize(bytes);
    assertNotNull(session2);
    assertNotSame(session, session2);
    assertEquals(session, session2);
    assertNotSame(((DialobQuestionnaireSession) session).getDialobSession(), session2.getDialobSession());
    Assertions.assertSame(((DialobQuestionnaireSession) session).getDialobProgram(), session2.getDialobProgram());

    verify(dialobProgramService, times(2)).findByFormIdAndRev("test-form", null);
    verify(eventPublisher).opened(any());
    verify(formDatabase).findOne(tenantId, "test-form", null);
    verify(sessionContextFactory, times(2)).createSessionUpdater(same(dialobProgram), any(), anyBoolean());

    Mockito.verifyNoMoreInteractions(dialobProgramService,
      eventPublisher,
      functionRegistry,
      questionnaireDatabase,
      formDatabase,
      sessionContextFactory);
  }

  public QuestionnaireDialobSessionRedisSerializer getQuestionnaireDialobSessionRedisSerializer(int bufferSize) {
    MeterRegistry meterRegistry = new SimpleMeterRegistry();
    return new QuestionnaireDialobSessionRedisSerializer(
      dialobQuestionnaireSessionService,
      eventPublisher, dialobProgramService, sessionContextFactory, asyncFunctionInvoker, Optional.of(meterRegistry),
      bufferSize
    );
  }
}
