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
package io.dialob.session.engine;

import io.dialob.api.form.ImmutableForm;
import io.dialob.api.form.ImmutableFormItem;
import io.dialob.api.form.ImmutableFormMetadata;
import io.dialob.api.form.ImmutableValidation;
import io.dialob.api.proto.Action;
import io.dialob.rule.parser.function.FunctionRegistry;
import io.dialob.session.engine.program.DialobProgram;
import io.dialob.session.engine.program.DialobSessionEvalContextFactory;
import io.dialob.session.engine.session.ActionToCommandMapper;
import io.dialob.session.engine.session.DialobSessionUpdater;
import io.dialob.session.engine.session.model.DialobSession;
import io.dialob.session.engine.session.model.ItemId;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class DialobProgramFromFormCompilerTest extends AbstractDialobProgramTest {

  @Override
  protected void assertNotRequired(DialobSession session, ItemId itemId) {
    super.assertNotRequired(session, itemId);
  }

  @Test
  void shouldSetRequiredOnForAllQuestionsOfRequired() {
    FunctionRegistry functionRegistry = Mockito.mock(FunctionRegistry.class);
    DialobSessionEvalContextFactory sessionContextFactory = new DialobSessionEvalContextFactory(functionRegistry, null);
    DialobProgramFromFormCompiler compiler = new DialobProgramFromFormCompiler(functionRegistry);

    DialobProgram dialobProgram = compiler.compileForm(ImmutableForm.builder()
      .id("123")
      .name("123")
      .putData("questionnaire", ImmutableFormItem.builder()
        .id("questionnaire")
        .type("questionnaire")
        .addItems("g")
        .build())
      .putData("g", ImmutableFormItem.builder()
        .id("g")
        .type("group")
        .addItems("q1","q2","n1")
        .build())
      .putData("q1", ImmutableFormItem.builder()
        .id("q1")
        .type("text")
        .build())
      .putData("q2", ImmutableFormItem.builder()
        .id("q2")
        .required("false")
        .type("text")
        .build())
      .putData("n1", ImmutableFormItem.builder()
        .id("n1")
        .type("note")
        .build())
      .metadata(ImmutableFormMetadata.builder()
        .label("xxx")
        .putAdditionalProperties("answersRequiredByDefault", true)
        .build())
      .build());


    DialobSession session = dialobProgram.createSession(sessionContextFactory, null, null, "fi", null);
    assertNotNull(session);
    DialobSessionUpdater dialobSessionUpdater = sessionContextFactory.createSessionUpdater(dialobProgram, session, false);
    assertErrorActive(session, toRef("q1"), "REQUIRED");
    assertErrorInactive(session, toRef("q2"), "REQUIRED");
    assertErrorInactive(session, toRef("n1"), "REQUIRED");
    dialobSessionUpdater.applyCommands(ActionToCommandMapper.toCommands(answer(toRef("q1"), "answer")));
    assertErrorInactive(session, toRef("q1"), "REQUIRED");
    assertErrorInactive(session, toRef("q2"), "REQUIRED");
    assertErrorInactive(session, toRef("n1"), "REQUIRED");
    dialobSessionUpdater.applyCommands(ActionToCommandMapper.toCommands(answer(toRef("q2"), "answer")));
    assertErrorInactive(session, toRef("q1"), "REQUIRED");
    assertErrorInactive(session, toRef("q2"), "REQUIRED");
    assertErrorInactive(session, toRef("n1"), "REQUIRED");
    dialobSessionUpdater.applyCommands(ActionToCommandMapper.toCommands(answer(toRef("q2"), null)));
    assertErrorInactive(session, toRef("q1"), "REQUIRED");
    assertErrorInactive(session, toRef("q2"), "REQUIRED");
    assertErrorInactive(session, toRef("n1"), "REQUIRED");
    dialobSessionUpdater.applyCommands(ActionToCommandMapper.toCommands(answer(toRef("q1"), null)));
    assertErrorActive(session, toRef("q1"), "REQUIRED");
    assertErrorInactive(session, toRef("q2"), "REQUIRED");
    assertErrorInactive(session, toRef("n1"), "REQUIRED");

    Mockito.verifyNoMoreInteractions(functionRegistry);
  }

  @Test
  void shouldSetRequiredOnForAllQuestionsOfRequiredInMultiRow() {
    FunctionRegistry functionRegistry = Mockito.mock(FunctionRegistry.class);
    DialobSessionEvalContextFactory sessionContextFactory = new DialobSessionEvalContextFactory(functionRegistry, null);
    DialobProgramFromFormCompiler compiler = new DialobProgramFromFormCompiler(functionRegistry);

    DialobProgram dialobProgram = compiler.compileForm(ImmutableForm.builder()
      .id("123")
      .name("123")
      .putData("questionnaire", ImmutableFormItem.builder()
        .id("questionnaire")
        .type("questionnaire")
        .addItems("g")
        .build())
      .putData("g", ImmutableFormItem.builder()
        .id("g")
        .type("group")
        .addItems("rg")
        .build())
      .putData("rg", ImmutableFormItem.builder()
        .id("rg")
        .type("rowgroup")
        .addItems("q1","q2")
        .build())
      .putData("q1", ImmutableFormItem.builder()
        .id("q1")
        .type("text")
        .build())
      .putData("q2", ImmutableFormItem.builder()
        .id("q2")
        .required("false")
        .type("text")
        .build())
      .metadata(ImmutableFormMetadata.builder()
        .label("xxx")
        .putAdditionalProperties("answersRequiredByDefault", true)
        .build())
      .build());

    DialobSession session = dialobProgram.createSession(sessionContextFactory, null, null, "fi", null);
    assertNotNull(session);
    DialobSessionUpdater dialobSessionUpdater = sessionContextFactory.createSessionUpdater(dialobProgram, session, false);

    dialobSessionUpdater.applyCommands(ActionToCommandMapper.toCommands(addRow(toRef("rg"))));
    assertErrorActive(session, toRef("rg.0.q1"), "REQUIRED");
    assertErrorInactive(session, toRef("rg.0.q2"), "REQUIRED");
    dialobSessionUpdater.applyCommands(ActionToCommandMapper.toCommands(answer(toRef("rg.0.q1"), "answer")));
    assertErrorInactive(session, toRef("rg.0.q1"), "REQUIRED");
    assertErrorInactive(session, toRef("rg.0.q2"), "REQUIRED");
    dialobSessionUpdater.applyCommands(ActionToCommandMapper.toCommands(answer(toRef("rg.0.q2"), "answer")));
    assertErrorInactive(session, toRef("rg.0.q1"), "REQUIRED");
    assertErrorInactive(session, toRef("rg.0.q2"), "REQUIRED");
    dialobSessionUpdater.applyCommands(ActionToCommandMapper.toCommands(answer(toRef("rg.0.q2"), null)));
    assertErrorInactive(session, toRef("rg.0.q1"), "REQUIRED");
    assertErrorInactive(session, toRef("rg.0.q2"), "REQUIRED");
    dialobSessionUpdater.applyCommands(ActionToCommandMapper.toCommands(answer(toRef("rg.0.q1"), null)));
    assertErrorActive(session, toRef("rg.0.q1"), "REQUIRED");
    assertErrorInactive(session, toRef("rg.0.q2"), "REQUIRED");

    Mockito.verifyNoMoreInteractions(functionRegistry);
  }


  @Test
  void testIsBlankAndIsNullOperators() {
    FunctionRegistry functionRegistry = Mockito.mock(FunctionRegistry.class);
    DialobSessionEvalContextFactory sessionContextFactory = new DialobSessionEvalContextFactory(functionRegistry, null);
    DialobProgramFromFormCompiler compiler = new DialobProgramFromFormCompiler(functionRegistry);

    DialobProgram dialobProgram = compiler.compileForm(ImmutableForm.builder()
      .id("123")
      .name("123")
      .putData("questionnaire", ImmutableFormItem.builder()
        .id("questionnaire")
        .type("questionnaire")
        .addItems("g")
        .build())
      .putData("g", ImmutableFormItem.builder()
        .id("g")
        .type("group")
        .addItems("q1","q2")
        .build())
      .putData("q1", ImmutableFormItem.builder()
        .id("q1")
        .type("text")
        .addValidations(ImmutableValidation.builder().rule("answer is blank").message(Map.of("fi","blank")).build())
        .build())
      .putData("q2", ImmutableFormItem.builder()
        .id("q2")
        .addValidations(ImmutableValidation.builder().rule("answer is null").message(Map.of("fi","null")).build())
        .type("text")
        .build())
      .metadata(ImmutableFormMetadata.builder()
        .label("xxx")
        .build())
      .build());

    DialobSession session = dialobProgram.createSession(sessionContextFactory, null, null, "fi", null);
    assertNotNull(session);
    DialobSessionUpdater dialobSessionUpdater = sessionContextFactory.createSessionUpdater(dialobProgram, session, false);

    assertErrorActive(session, toRef("q1"), "q1_error1");
    assertErrorActive(session, toRef("q2"), "q2_error1");

    dialobSessionUpdater.applyCommands(ActionToCommandMapper.toCommands(answer(toRef("q1"), "answer")));
    assertErrorInactive(session, toRef("q1"), "q1_error1");
    assertErrorActive(session, toRef("q2"), "q2_error1");

    dialobSessionUpdater.applyCommands(ActionToCommandMapper.toCommands(answer(toRef("q2"), "answer")));
    assertErrorInactive(session, toRef("q1"), "q1_error1");
    assertErrorInactive(session, toRef("q2"), "q2_error1");

    dialobSessionUpdater.applyCommands(ActionToCommandMapper.toCommands(answer(toRef("q1"), null)));
    assertErrorActive(session, toRef("q1"), "q1_error1");
    assertErrorInactive(session, toRef("q2"), "q2_error1");

    dialobSessionUpdater.applyCommands(ActionToCommandMapper.toCommands(answer(toRef("q2"), null)));
    assertErrorActive(session, toRef("q1"), "q1_error1");
    assertErrorActive(session, toRef("q2"), "q2_error1");

    dialobSessionUpdater.applyCommands(ActionToCommandMapper.toCommands(answer(toRef("q1"), " ")));
    assertErrorActive(session, toRef("q1"), "q1_error1");
    assertErrorActive(session, toRef("q2"), "q2_error1");

    dialobSessionUpdater.applyCommands(ActionToCommandMapper.toCommands(answer(toRef("q2"), "")));
    assertErrorActive(session, toRef("q1"), "q1_error1");
    assertErrorInactive(session, toRef("q2"), "q2_error1");

    dialobSessionUpdater.applyCommands(ActionToCommandMapper.toCommands(answer(toRef("q2"), " ")));
    assertErrorActive(session, toRef("q1"), "q1_error1");
    assertErrorInactive(session, toRef("q2"), "q2_error1");

    Mockito.verifyNoMoreInteractions(functionRegistry);
  }


  @Test
  void testLocaleUpdateEffectOnErrors() {
    FunctionRegistry functionRegistry = Mockito.mock(FunctionRegistry.class);
    DialobSessionEvalContextFactory sessionContextFactory = new DialobSessionEvalContextFactory(functionRegistry, null);
    DialobProgramFromFormCompiler compiler = new DialobProgramFromFormCompiler(functionRegistry);

    DialobProgram dialobProgram = compiler.compileForm(ImmutableForm.builder()
      .id("123")
      .name("123")
      .putData("questionnaire", ImmutableFormItem.builder()
        .id("questionnaire")
        .type("questionnaire")
        .addItems("g")
        .build())
      .putData("g", ImmutableFormItem.builder()
        .id("g")
        .type("group")
        .addItems("q1","q2")
        .build())
      .putData("q1", ImmutableFormItem.builder()
        .id("q1")
        .type("text")
        .addValidations(ImmutableValidation.builder().rule("answer is blank").message(Map.of("fi","fi", "en","en")).build())
        .build())
      .putData("q2", ImmutableFormItem.builder()
        .id("q2")
        .addValidations(ImmutableValidation.builder().rule("answer is null").message(Map.of("fi","fi", "en","en")).build())
        .type("text")
        .build())
      .metadata(ImmutableFormMetadata.builder()
        .label("xxx")
        .build())
      .build());

    DialobSession session = dialobProgram.createSession(sessionContextFactory, null, null, "fi", null);
    assertNotNull(session);
    DialobSessionUpdater dialobSessionUpdater = sessionContextFactory.createSessionUpdater(dialobProgram, session, false);

    assertErrorLabel(session, toRef("q1"), "q1_error1","fi");
    assertErrorLabel(session, toRef("q2"), "q2_error1","fi");
    dialobSessionUpdater.applyCommands(ActionToCommandMapper.toCommands(setLocale("en")));
    assertErrorLabel(session, toRef("q1"), "q1_error1","en");
    assertErrorLabel(session, toRef("q2"), "q2_error1","en");
    dialobSessionUpdater.applyCommands(ActionToCommandMapper.toCommands(setLocale("fi")));
    assertErrorLabel(session, toRef("q1"), "q1_error1","fi");
    assertErrorLabel(session, toRef("q2"), "q2_error1","fi");

    Mockito.verifyNoMoreInteractions(functionRegistry);
  }


  @Test
  void shouldInactivateNestedGroups() {
    FunctionRegistry functionRegistry = Mockito.mock(FunctionRegistry.class);
    DialobSessionEvalContextFactory sessionContextFactory = new DialobSessionEvalContextFactory(functionRegistry, null);
    DialobProgramFromFormCompiler compiler = new DialobProgramFromFormCompiler(functionRegistry);

    DialobProgram dialobProgram = compiler.compileForm(ImmutableForm.builder()
      .id("123")
      .name("123")
      .putData("questionnaire", ImmutableFormItem.builder()
        .id("questionnaire")
        .type("questionnaire")
        .addItems("g2")
        .build())
      .putData("g2", ImmutableFormItem.builder()
        .id("g2")
        .type("group")
        .activeWhen("false")
        .addItems("g22","q1")
        .build())
      .putData("g22", ImmutableFormItem.builder()
        .id("g22")
        .type("group")
        .addItems("q3")
        .build())
      .putData("q3", ImmutableFormItem.builder()
        .id("q3")
        .type("text")
        .build())
      .putData("q1", ImmutableFormItem.builder()
        .id("q1")
        .type("text")
        .build())
      .metadata(ImmutableFormMetadata.builder()
        .label("xxx")
        .putAdditionalProperties("answersRequiredByDefault", true)
        .build())
      .build());


    DialobSession session = dialobProgram.createSession(sessionContextFactory, null, null, "fi", null);
    assertNotNull(session);
    DialobSessionUpdater dialobSessionUpdater = sessionContextFactory.createSessionUpdater(dialobProgram, session, false);
    dialobSessionUpdater.applyCommands(ActionToCommandMapper.toCommands(setLocale("en")));

    assertDisabled(session, toRef("g2"));
    assertDisabled(session, toRef("g22"));
    assertDisabled(session, toRef("q3"));
    assertDisabled(session, toRef("q1"));
    assertErrorDisabled(session, toRef("q3"), "REQUIRED");

    assertInactive(session, toRef("q1"));
    assertInactive(session, toRef("g2"));
    assertInactive(session, toRef("g22"));
    assertInactive(session, toRef("q3"));
    assertErrorInactive(session, toRef("q3"), "REQUIRED");

    Mockito.verifyNoMoreInteractions(functionRegistry);
  }



  @Test
  void shouldSubSequentPagesShouldNotPreventNextPage() {
    FunctionRegistry functionRegistry = Mockito.mock(FunctionRegistry.class);
    DialobSessionEvalContextFactory sessionContextFactory = new DialobSessionEvalContextFactory(functionRegistry, null);
    DialobProgramFromFormCompiler compiler = new DialobProgramFromFormCompiler(functionRegistry);

    DialobProgram dialobProgram = compiler.compileForm(ImmutableForm.builder()
      .id("123")
      .name("123")
      .putData("questionnaire", ImmutableFormItem.builder()
        .id("questionnaire")
        .type("questionnaire")
        .addItems("p1","p2","p3")
        .build())
      .putData("p1", ImmutableFormItem.builder()
        .id("p1")
        .type("page")
        .addItems("g11")
        .build())
      .putData("p2", ImmutableFormItem.builder()
        .id("p2")
        .type("page")
        .addItems("g21")
        .build())
      .putData("p3", ImmutableFormItem.builder()
        .id("p3")
        .type("page")
        .addItems("g31")
        .build())
      .putData("g11", ImmutableFormItem.builder()
        .id("g11")
        .type("group")
        .addItems("q111")
        .build())
      .putData("g21", ImmutableFormItem.builder()
        .id("g21")
        .type("group")
        .addItems("q211")
        .build())
      .putData("g31", ImmutableFormItem.builder()
        .id("g31")
        .type("group")
        .addItems("q311")
        .build())
      .putData("q111", ImmutableFormItem.builder()
        .id("q111")
        .type("text")
        .build())
      .putData("q211", ImmutableFormItem.builder()
        .id("q211")
        .type("text")
        .build())
      .putData("q311", ImmutableFormItem.builder()
        .id("q311")
        .type("text")
        .build())
      .metadata(ImmutableFormMetadata.builder()
        .label("xxx")
        .putAdditionalProperties("answersRequiredByDefault", true)
        .build())
      .build());


    DialobSession session = dialobProgram.createSession(sessionContextFactory, null, null, "fi", null);
    assertNotNull(session);
    DialobSessionUpdater dialobSessionUpdater = sessionContextFactory.createSessionUpdater(dialobProgram, session, false);
    dialobSessionUpdater.applyCommands(ActionToCommandMapper.toCommands(setLocale("en")));

    assertEnabled(session, toRef("p1"));
    assertDisabled(session, toRef("p2"));
    assertDisabled(session, toRef("p3"));
    assertEnabled(session, toRef("g11"));
    assertDisabled(session, toRef("g21"));
    assertDisabled(session, toRef("g31"));
    assertEnabled(session, toRef("q111"));
    assertDisabled(session, toRef("q211"));
    assertDisabled(session, toRef("q311"));

    assertErrorActive(session, toRef("q111"), "REQUIRED");
    assertErrorActive(session, toRef("q211"), "REQUIRED");
    assertErrorActive(session, toRef("q311"), "REQUIRED");

    assertAllowedAction(session, Action.Type.ANSWER);
    assertDisallowedAction(session, Action.Type.NEXT);
    assertDisallowedAction(session, Action.Type.PREVIOUS);
    assertDisallowedAction(session, Action.Type.COMPLETE);

    dialobSessionUpdater.applyCommands(ActionToCommandMapper.toCommands(answer(toRef("q111"), "Hello")));
    assertErrorInactive(session, toRef("q111"), "REQUIRED");
    assertErrorActive(session, toRef("q211"), "REQUIRED");
    assertErrorActive(session, toRef("q311"), "REQUIRED");

    assertAllowedAction(session, Action.Type.ANSWER);
    assertAllowedAction(session, Action.Type.NEXT);
    assertDisallowedAction(session, Action.Type.PREVIOUS);
    assertDisallowedAction(session, Action.Type.COMPLETE);

    //
    dialobSessionUpdater.applyCommands(ActionToCommandMapper.toCommands(nextPage()));
    assertAllowedAction(session, Action.Type.ANSWER);
    assertDisallowedAction(session, Action.Type.NEXT);
    assertAllowedAction(session, Action.Type.PREVIOUS);
    assertDisallowedAction(session, Action.Type.COMPLETE);

    //
    dialobSessionUpdater.applyCommands(ActionToCommandMapper.toCommands(answer(toRef("q211"), "Hello")));
    assertErrorInactive(session, toRef("q111"), "REQUIRED");
    assertErrorInactive(session, toRef("q211"), "REQUIRED");
    assertErrorActive(session, toRef("q311"), "REQUIRED");

    assertAllowedAction(session, Action.Type.ANSWER);
    assertAllowedAction(session, Action.Type.NEXT);
    assertAllowedAction(session, Action.Type.PREVIOUS);
    assertDisallowedAction(session, Action.Type.COMPLETE);

    //
    dialobSessionUpdater.applyCommands(ActionToCommandMapper.toCommands(nextPage()));
    assertAllowedAction(session, Action.Type.ANSWER);
    assertDisallowedAction(session, Action.Type.NEXT);
    assertAllowedAction(session, Action.Type.PREVIOUS);
    assertDisallowedAction(session, Action.Type.COMPLETE);


    dialobSessionUpdater.applyCommands(ActionToCommandMapper.toCommands(answer(toRef("q311"), "Hello")));
    assertErrorInactive(session, toRef("q111"), "REQUIRED");
    assertErrorInactive(session, toRef("q211"), "REQUIRED");
    assertErrorInactive(session, toRef("q311"), "REQUIRED");

    assertAllowedAction(session, Action.Type.ANSWER);
    assertDisallowedAction(session, Action.Type.NEXT);
    assertAllowedAction(session, Action.Type.PREVIOUS);
    assertAllowedAction(session, Action.Type.COMPLETE);


    Mockito.verifyNoMoreInteractions(functionRegistry);
  }
}
