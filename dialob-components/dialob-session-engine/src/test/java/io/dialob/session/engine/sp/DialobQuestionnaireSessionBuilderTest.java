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
package io.dialob.session.engine.sp;

import io.dialob.api.form.Form;
import io.dialob.api.form.ImmutableForm;
import io.dialob.api.form.ImmutableFormItem;
import io.dialob.api.form.ImmutableFormMetadata;
import io.dialob.questionnaire.service.api.event.QuestionnaireEventPublisher;
import io.dialob.questionnaire.service.api.session.FormFinder;
import io.dialob.questionnaire.service.api.session.QuestionnaireSession;
import io.dialob.questionnaire.service.api.session.QuestionnaireSessionSaveService;
import io.dialob.questionnaire.service.api.session.QuestionnaireSessionService;
import io.dialob.rule.parser.function.FunctionRegistry;
import io.dialob.session.engine.DialobProgramFromFormCompiler;
import io.dialob.session.engine.DialobProgramService;
import io.dialob.session.engine.QuestionnaireDialobProgramService;
import io.dialob.session.engine.program.DialobProgram;
import io.dialob.session.engine.program.DialobSessionEvalContextFactory;
import io.dialob.session.engine.program.EvalContext;
import io.dialob.session.engine.session.DialobSessionUpdater;
import io.dialob.session.engine.session.model.DialobSession;
import org.junit.jupiter.api.Test;
import org.mockito.AdditionalAnswers;
import org.mockito.Mockito;

import java.time.Instant;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class DialobQuestionnaireSessionBuilderTest {

  @Test
  public void shouldInitializeSessionWithCorrectActiveItem() {
    final QuestionnaireEventPublisher questionnaireEventPublisher = mock();
    final FormFinder formFinder = mock();
    final FunctionRegistry functionRegistry = mock();
    final QuestionnaireSessionService questionnaireSessionService = mock();
    final QuestionnaireSessionSaveService questionnaireSessionSaveService = mock();
    final DialobProgram dialobProgram = mock();
    final DialobSession dialobSession = mock();
    final DialobSessionUpdater dialobSessionUpdater = mock();
    final Consumer<EvalContext.UpdatedItemsVisitor> consumer = mock();
    final AsyncFunctionInvoker asyncFunctionInvoker = mock();

    final DialobProgramFromFormCompiler programFromFormCompiler = new DialobProgramFromFormCompiler(functionRegistry);
    final DialobSessionEvalContextFactory sessionContextFactory = new DialobSessionEvalContextFactory(functionRegistry, null);
    final DialobProgramService dialobProgramService = QuestionnaireDialobProgramService.newBuilder()
      .setFormDatabase(formFinder)
      .setProgramFromFormCompiler(programFromFormCompiler)
      .build();
    final Form form = ImmutableForm.builder()
      .id("123")
      .putData("questionnaire", ImmutableFormItem.builder().id("questionnaire").type("questionnaire").build())
      .metadata(ImmutableFormMetadata.builder().label("test form").build())
      .build();

    when(formFinder.findForm("123", null)).thenReturn(form);
    when(dialobProgram.createSession(any(), any(), any(), any(), any(), any(), any(), any(), any(), any())).thenReturn(dialobSession);
    when(questionnaireSessionSaveService.save(any())).then(AdditionalAnswers.returnsFirstArg());
    when(dialobSessionUpdater.applyCommands(any())).thenReturn(consumer);
    when(dialobSession.getLastUpdate()).thenReturn(Instant.now());

    DialobQuestionnaireSessionBuilder builder = new DialobQuestionnaireSessionBuilder(questionnaireEventPublisher,
      dialobProgramService,
      formFinder,
      questionnaireSessionSaveService,
      sessionContextFactory,
      asyncFunctionInvoker);

    QuestionnaireSession session = builder.formId("123").activeItem("page3").createOnly(true).build();

    assertNotNull(session);
    assertEquals("page3", session.getQuestionnaire().getActiveItem());

    verify(formFinder, times(2)).findForm("123", null); // TODO twice??
    verify(questionnaireSessionSaveService).save(any(QuestionnaireSession.class));

    Mockito.verifyNoMoreInteractions(
      questionnaireEventPublisher,
      formFinder,
      functionRegistry,
      questionnaireSessionService,
      dialobProgram,
      dialobSession,
      asyncFunctionInvoker);
  }

}
