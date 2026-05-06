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

import io.dialob.api.form.Form;
import io.dialob.api.form.FormItem;
import io.dialob.api.form.Validation;
import io.dialob.api.form.Variable;
import io.dialob.api.proto.Action;
import io.dialob.rule.parser.function.FunctionRegistry;
import io.dialob.session.engine.program.DialobProgram;
import io.dialob.session.engine.program.DialobSessionEvalContextFactory;
import io.dialob.session.engine.session.DialobSessionUpdater;
import io.dialob.session.engine.session.model.DialobSession;
import io.dialob.session.engine.session.model.ItemId;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigInteger;
import java.util.Map;

import static io.dialob.session.engine.session.ActionToCommandMapper.toCommands;
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

    var dialobProgram = compiler.compileForm(new Form.Builder()
      .id("123")
      .name("123")
      .putData("questionnaire", new FormItem.Builder()
        .id("questionnaire")
        .type("questionnaire")
        .addItems("g")
        .build())
      .putData("g", new FormItem.Builder()
        .id("g")
        .type("group")
        .addItems("q1","q2","n1")
        .build())
      .putData("q1", new FormItem.Builder()
        .id("q1")
        .type("text")
        .build())
      .putData("q2", new FormItem.Builder()
        .id("q2")
        .required("false")
        .type("text")
        .build())
      .putData("n1", new FormItem.Builder()
        .id("n1")
        .type("note")
        .build())
      .metadata(new Form.Metadata.Builder()
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
    dialobSessionUpdater.applyCommands(toCommands(answer(toRef("q1"), "answer")));
    assertErrorInactive(session, toRef("q1"), "REQUIRED");
    assertErrorInactive(session, toRef("q2"), "REQUIRED");
    assertErrorInactive(session, toRef("n1"), "REQUIRED");
    dialobSessionUpdater.applyCommands(toCommands(answer(toRef("q2"), "answer")));
    assertErrorInactive(session, toRef("q1"), "REQUIRED");
    assertErrorInactive(session, toRef("q2"), "REQUIRED");
    assertErrorInactive(session, toRef("n1"), "REQUIRED");
    dialobSessionUpdater.applyCommands(toCommands(answer(toRef("q2"), null)));
    assertErrorInactive(session, toRef("q1"), "REQUIRED");
    assertErrorInactive(session, toRef("q2"), "REQUIRED");
    assertErrorInactive(session, toRef("n1"), "REQUIRED");
    dialobSessionUpdater.applyCommands(toCommands(answer(toRef("q1"), null)));
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

    DialobProgram dialobProgram = compiler.compileForm(new Form.Builder()
      .id("123")
      .name("123")
      .putData("questionnaire", new FormItem.Builder()
        .id("questionnaire")
        .type("questionnaire")
        .addItems("g")
        .build())
      .putData("g", new FormItem.Builder()
        .id("g")
        .type("group")
        .addItems("rg")
        .build())
      .putData("rg", new FormItem.Builder()
        .id("rg")
        .type("rowgroup")
        .addItems("q1","q2")
        .build())
      .putData("q1", new FormItem.Builder()
        .id("q1")
        .type("text")
        .build())
      .putData("q2", new FormItem.Builder()
        .id("q2")
        .required("false")
        .type("text")
        .build())
      .metadata(new Form.Metadata.Builder()
        .label("xxx")
        .putAdditionalProperties("answersRequiredByDefault", true)
        .build())
      .build());

    DialobSession session = dialobProgram.createSession(sessionContextFactory, null, null, "fi", null);
    assertNotNull(session);
    DialobSessionUpdater dialobSessionUpdater = sessionContextFactory.createSessionUpdater(dialobProgram, session, false);

    dialobSessionUpdater.applyCommands(toCommands(addRow(toRef("rg"))));
    assertErrorActive(session, toRef("rg.0.q1"), "REQUIRED");
    assertErrorInactive(session, toRef("rg.0.q2"), "REQUIRED");
    dialobSessionUpdater.applyCommands(toCommands(answer(toRef("rg.0.q1"), "answer")));
    assertErrorInactive(session, toRef("rg.0.q1"), "REQUIRED");
    assertErrorInactive(session, toRef("rg.0.q2"), "REQUIRED");
    dialobSessionUpdater.applyCommands(toCommands(answer(toRef("rg.0.q2"), "answer")));
    assertErrorInactive(session, toRef("rg.0.q1"), "REQUIRED");
    assertErrorInactive(session, toRef("rg.0.q2"), "REQUIRED");
    dialobSessionUpdater.applyCommands(toCommands(answer(toRef("rg.0.q2"), null)));
    assertErrorInactive(session, toRef("rg.0.q1"), "REQUIRED");
    assertErrorInactive(session, toRef("rg.0.q2"), "REQUIRED");
    dialobSessionUpdater.applyCommands(toCommands(answer(toRef("rg.0.q1"), null)));
    assertErrorActive(session, toRef("rg.0.q1"), "REQUIRED");
    assertErrorInactive(session, toRef("rg.0.q2"), "REQUIRED");

    Mockito.verifyNoMoreInteractions(functionRegistry);
  }

  @Test
  void shouldCalculateExpressionVariablesInRowgroupsContext() {
    FunctionRegistry functionRegistry = Mockito.mock(FunctionRegistry.class);
    DialobSessionEvalContextFactory sessionContextFactory = new DialobSessionEvalContextFactory(functionRegistry, null);
    DialobProgramFromFormCompiler compiler = new DialobProgramFromFormCompiler(functionRegistry);

    DialobProgram dialobProgram = compiler.compileForm(new Form.Builder()
      .id("123")
      .name("123")
      .putData("questionnaire", new FormItem.Builder()
        .id("questionnaire")
        .type("questionnaire")
        .addItems("g")
        .build())
      .putData("g", new FormItem.Builder()
        .id("g")
        .type("group")
        .addItems("rg")
        .build())
      .putData("rg", new FormItem.Builder()
        .id("rg")
        .type("rowgroup")
        .addItems("q1","q2","q3","summa")
        .build())
      .putData("q1", new FormItem.Builder()
        .id("q1")
        .type("number")
        .build())
      .putData("q2", new FormItem.Builder()
        .id("q2")
        .type("number")
        .build())
      .putData("q3", new FormItem.Builder()
        .id("q3")
        .activeWhen("q1 + q2 > 3")
        .type("number")
        .build())
      .addVariables(new Variable.Builder()
        .name("summa")
        .context(false)
        .expression("q1 + q2")
        .build())
      .metadata(new Form.Metadata.Builder()
        .label("xxx")
        .putAdditionalProperties("answersRequiredByDefault", true)
        .build())
      .build());

    DialobSession session = dialobProgram.createSession(sessionContextFactory, null, null, "fi", null);
    assertNotNull(session);
    DialobSessionUpdater dialobSessionUpdater = sessionContextFactory.createSessionUpdater(dialobProgram, session, false);

    dialobSessionUpdater.applyCommands(toCommands(addRow(toRef("rg"))));
    assertVariableEquals(session, null, toRef("rg.0.summa"));
    dialobSessionUpdater.applyCommands(toCommands(answer(toRef("rg.0.q1"), "1")));
    assertInactive(session, toRef("rg.0.q3"));
    dialobSessionUpdater.applyCommands(toCommands(answer(toRef("rg.0.q2"), "2")));
//    assertActive(session, toRef("rg.0.q3"));
    assertVariableEquals(session, BigInteger.valueOf(3), toRef("rg.0.summa"));
    dialobSessionUpdater.applyCommands(toCommands(answer(toRef("rg.0.q2"), null)));
    dialobSessionUpdater.applyCommands(toCommands(answer(toRef("rg.0.q1"), null)));
    assertVariableEquals(session, null, toRef("rg.0.summa"));

    Mockito.verifyNoMoreInteractions(functionRegistry);
  }

  @Test
  void shouldCalculateExpressionVariablesInRowgroupsContextAndUpdateLabels() {
    FunctionRegistry functionRegistry = Mockito.mock(FunctionRegistry.class);
    DialobSessionEvalContextFactory sessionContextFactory = new DialobSessionEvalContextFactory(functionRegistry, null);
    DialobProgramFromFormCompiler compiler = new DialobProgramFromFormCompiler(functionRegistry);

    DialobProgram dialobProgram = compiler.compileForm(new Form.Builder()
      .id("123")
      .name("123")
      .putData("questionnaire", new FormItem.Builder()
        .id("questionnaire")
        .type("questionnaire")
        .addItems("g")
        .build())
      .putData("g", new FormItem.Builder()
        .id("g")
        .type("group")
        .addItems("rg")
        .build())
      .putData("rg", new FormItem.Builder()
        .id("rg")
        .type("rowgroup")
        .addItems("q1","q2","info","summa")
        .build())
      .putData("q1", new FormItem.Builder()
        .id("q1")
        .type("number")
        .build())
      .putData("q2", new FormItem.Builder()
        .id("q2")
        .type("number")
        .build())
      .putData("info", new FormItem.Builder()
        .id("info")
        .type("note")
        .label(Map.of("fi", "Summa on {summa}"))
        .build())
      .addVariables(new Variable.Builder()
        .name("summa")
        .context(false)
        .expression("q1 + q2")
        .build())
      .metadata(new Form.Metadata.Builder()
        .label("xxx")
        .putAdditionalProperties("answersRequiredByDefault", true)
        .build())
      .build());

    DialobSession session = dialobProgram.createSession(sessionContextFactory, null, null, "fi", null);
    assertNotNull(session);
    DialobSessionUpdater dialobSessionUpdater = sessionContextFactory.createSessionUpdater(dialobProgram, session, false);

    dialobSessionUpdater.applyCommands(toCommands(addRow(toRef("rg"))));
    assertVariableEquals(session, null, toRef("rg.0.summa"));
    dialobSessionUpdater.applyCommands(toCommands(answer(toRef("rg.0.q1"), "1")));
    assertActive(session, toRef("rg.0.info"));
    assertLabel(session, toRef("rg.0.info"), "Summa on null");
    dialobSessionUpdater.applyCommands(toCommands(answer(toRef("rg.0.q2"), "2")));
//    assertActive(session, toRef("rg.0.q3"));
    assertVariableEquals(session, BigInteger.valueOf(3), toRef("rg.0.summa"));
    assertLabel(session, toRef("rg.0.info"), "Summa on 3");
    dialobSessionUpdater.applyCommands(toCommands(answer(toRef("rg.0.q2"), null)));
    dialobSessionUpdater.applyCommands(toCommands(answer(toRef("rg.0.q1"), null)));
    assertVariableEquals(session, null, toRef("rg.0.summa"));

    Mockito.verifyNoMoreInteractions(functionRegistry);
  }



  @Test
  void testIsBlankAndIsNullOperators() {
    FunctionRegistry functionRegistry = Mockito.mock(FunctionRegistry.class);
    DialobSessionEvalContextFactory sessionContextFactory = new DialobSessionEvalContextFactory(functionRegistry, null);
    DialobProgramFromFormCompiler compiler = new DialobProgramFromFormCompiler(functionRegistry);

    DialobProgram dialobProgram = compiler.compileForm(new Form.Builder()
      .id("123")
      .name("123")
      .putData("questionnaire", new FormItem.Builder()
        .id("questionnaire")
        .type("questionnaire")
        .addItems("g")
        .build())
      .putData("g", new FormItem.Builder()
        .id("g")
        .type("group")
        .addItems("q1","q2")
        .build())
      .putData("q1", new FormItem.Builder()
        .id("q1")
        .type("text")
        .addValidations(new Validation.Builder().rule("answer is blank").message(Map.of("fi","blank")).build())
        .build())
      .putData("q2", new FormItem.Builder()
        .id("q2")
        .addValidations(new Validation.Builder().rule("answer is null").message(Map.of("fi","null")).build())
        .type("text")
        .build())
      .metadata(new Form.Metadata.Builder()
        .label("xxx")
        .build())
      .build());

    DialobSession session = dialobProgram.createSession(sessionContextFactory, null, null, "fi", null);
    assertNotNull(session);
    DialobSessionUpdater dialobSessionUpdater = sessionContextFactory.createSessionUpdater(dialobProgram, session, false);

    assertErrorActive(session, toRef("q1"), "q1_error1");
    assertErrorActive(session, toRef("q2"), "q2_error1");

    dialobSessionUpdater.applyCommands(toCommands(answer(toRef("q1"), "answer")));
    assertErrorInactive(session, toRef("q1"), "q1_error1");
    assertErrorActive(session, toRef("q2"), "q2_error1");

    dialobSessionUpdater.applyCommands(toCommands(answer(toRef("q2"), "answer")));
    assertErrorInactive(session, toRef("q1"), "q1_error1");
    assertErrorInactive(session, toRef("q2"), "q2_error1");

    dialobSessionUpdater.applyCommands(toCommands(answer(toRef("q1"), null)));
    assertErrorActive(session, toRef("q1"), "q1_error1");
    assertErrorInactive(session, toRef("q2"), "q2_error1");

    dialobSessionUpdater.applyCommands(toCommands(answer(toRef("q2"), null)));
    assertErrorActive(session, toRef("q1"), "q1_error1");
    assertErrorActive(session, toRef("q2"), "q2_error1");

    dialobSessionUpdater.applyCommands(toCommands(answer(toRef("q1"), " ")));
    assertErrorActive(session, toRef("q1"), "q1_error1");
    assertErrorActive(session, toRef("q2"), "q2_error1");

    dialobSessionUpdater.applyCommands(toCommands(answer(toRef("q2"), "")));
    assertErrorActive(session, toRef("q1"), "q1_error1");
    assertErrorInactive(session, toRef("q2"), "q2_error1");

    dialobSessionUpdater.applyCommands(toCommands(answer(toRef("q2"), " ")));
    assertErrorActive(session, toRef("q1"), "q1_error1");
    assertErrorInactive(session, toRef("q2"), "q2_error1");

    Mockito.verifyNoMoreInteractions(functionRegistry);
  }


  @Test
  void testLocaleUpdateEffectOnErrors() {
    FunctionRegistry functionRegistry = Mockito.mock(FunctionRegistry.class);
    DialobSessionEvalContextFactory sessionContextFactory = new DialobSessionEvalContextFactory(functionRegistry, null);
    DialobProgramFromFormCompiler compiler = new DialobProgramFromFormCompiler(functionRegistry);

    DialobProgram dialobProgram = compiler.compileForm(new Form.Builder()
      .id("123")
      .name("123")
      .putData("questionnaire", new FormItem.Builder()
        .id("questionnaire")
        .type("questionnaire")
        .addItems("g")
        .build())
      .putData("g", new FormItem.Builder()
        .id("g")
        .type("group")
        .addItems("q1","q2")
        .build())
      .putData("q1", new FormItem.Builder()
        .id("q1")
        .type("text")
        .addValidations(new Validation.Builder().rule("answer is blank").message(Map.of("fi","fi", "en","en")).build())
        .build())
      .putData("q2", new FormItem.Builder()
        .id("q2")
        .addValidations(new Validation.Builder().rule("answer is null").message(Map.of("fi","fi", "en","en")).build())
        .type("text")
        .build())
      .metadata(new Form.Metadata.Builder()
        .label("xxx")
        .build())
      .build());

    DialobSession session = dialobProgram.createSession(sessionContextFactory, null, null, "fi", null);
    assertNotNull(session);
    DialobSessionUpdater dialobSessionUpdater = sessionContextFactory.createSessionUpdater(dialobProgram, session, false);

    assertErrorLabel(session, toRef("q1"), "q1_error1","fi");
    assertErrorLabel(session, toRef("q2"), "q2_error1","fi");
    dialobSessionUpdater.applyCommands(toCommands(setLocale("en")));
    assertErrorLabel(session, toRef("q1"), "q1_error1","en");
    assertErrorLabel(session, toRef("q2"), "q2_error1","en");
    dialobSessionUpdater.applyCommands(toCommands(setLocale("fi")));
    assertErrorLabel(session, toRef("q1"), "q1_error1","fi");
    assertErrorLabel(session, toRef("q2"), "q2_error1","fi");

    Mockito.verifyNoMoreInteractions(functionRegistry);
  }

  @Test
  void shouldInactivateNestedGroups() {
    FunctionRegistry functionRegistry = Mockito.mock(FunctionRegistry.class);
    DialobSessionEvalContextFactory sessionContextFactory = new DialobSessionEvalContextFactory(functionRegistry, null);
    DialobProgramFromFormCompiler compiler = new DialobProgramFromFormCompiler(functionRegistry);

    DialobProgram dialobProgram = compiler.compileForm(new Form.Builder()
      .id("123")
      .name("123")
      .putData("questionnaire", new FormItem.Builder()
        .id("questionnaire")
        .type("questionnaire")
        .addItems("g2")
        .build())
      .putData("g2", new FormItem.Builder()
        .id("g2")
        .type("group")
        .activeWhen("false")
        .addItems("g22","q1")
        .build())
      .putData("g22", new FormItem.Builder()
        .id("g22")
        .type("group")
        .addItems("q3")
        .build())
      .putData("q3", new FormItem.Builder()
        .id("q3")
        .type("text")
        .build())
      .putData("q1", new FormItem.Builder()
        .id("q1")
        .type("text")
        .build())
      .metadata(new Form.Metadata.Builder()
        .label("xxx")
        .putAdditionalProperties("answersRequiredByDefault", true)
        .build())
      .build());


    DialobSession session = dialobProgram.createSession(sessionContextFactory, null, null, "fi", null);
    assertNotNull(session);
    DialobSessionUpdater dialobSessionUpdater = sessionContextFactory.createSessionUpdater(dialobProgram, session, false);
    dialobSessionUpdater.applyCommands(toCommands(setLocale("en")));

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

    DialobProgram dialobProgram = compiler.compileForm(new Form.Builder()
      .id("123")
      .name("123")
      .putData("questionnaire", new FormItem.Builder()
        .id("questionnaire")
        .type("questionnaire")
        .addItems("p1","p2","p3")
        .build())
      .putData("p1", new FormItem.Builder()
        .id("p1")
        .type("page")
        .addItems("g11")
        .build())
      .putData("p2", new FormItem.Builder()
        .id("p2")
        .type("page")
        .addItems("g21")
        .build())
      .putData("p3", new FormItem.Builder()
        .id("p3")
        .type("page")
        .addItems("g31")
        .build())
      .putData("g11", new FormItem.Builder()
        .id("g11")
        .type("group")
        .addItems("q111")
        .build())
      .putData("g21", new FormItem.Builder()
        .id("g21")
        .type("group")
        .addItems("q211")
        .build())
      .putData("g31", new FormItem.Builder()
        .id("g31")
        .type("group")
        .addItems("q311")
        .build())
      .putData("q111", new FormItem.Builder()
        .id("q111")
        .type("text")
        .build())
      .putData("q211", new FormItem.Builder()
        .id("q211")
        .type("text")
        .build())
      .putData("q311", new FormItem.Builder()
        .id("q311")
        .type("text")
        .build())
      .metadata(new Form.Metadata.Builder()
        .label("xxx")
        .putAdditionalProperties("answersRequiredByDefault", true)
        .build())
      .build());


    DialobSession session = dialobProgram.createSession(sessionContextFactory, null, null, "fi", null);
    assertNotNull(session);
    DialobSessionUpdater dialobSessionUpdater = sessionContextFactory.createSessionUpdater(dialobProgram, session, false);
    dialobSessionUpdater.applyCommands(toCommands(setLocale("en")));

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

    dialobSessionUpdater.applyCommands(toCommands(answer(toRef("q111"), "Hello")));
    assertErrorInactive(session, toRef("q111"), "REQUIRED");
    assertErrorActive(session, toRef("q211"), "REQUIRED");
    assertErrorActive(session, toRef("q311"), "REQUIRED");

    assertAllowedAction(session, Action.Type.ANSWER);
    assertAllowedAction(session, Action.Type.NEXT);
    assertDisallowedAction(session, Action.Type.PREVIOUS);
    assertDisallowedAction(session, Action.Type.COMPLETE);

    //
    dialobSessionUpdater.applyCommands(toCommands(nextPage()));
    assertAllowedAction(session, Action.Type.ANSWER);
    assertDisallowedAction(session, Action.Type.NEXT);
    assertAllowedAction(session, Action.Type.PREVIOUS);
    assertDisallowedAction(session, Action.Type.COMPLETE);

    //
    dialobSessionUpdater.applyCommands(toCommands(answer(toRef("q211"), "Hello")));
    assertErrorInactive(session, toRef("q111"), "REQUIRED");
    assertErrorInactive(session, toRef("q211"), "REQUIRED");
    assertErrorActive(session, toRef("q311"), "REQUIRED");

    assertAllowedAction(session, Action.Type.ANSWER);
    assertAllowedAction(session, Action.Type.NEXT);
    assertAllowedAction(session, Action.Type.PREVIOUS);
    assertDisallowedAction(session, Action.Type.COMPLETE);

    //
    dialobSessionUpdater.applyCommands(toCommands(nextPage()));
    assertAllowedAction(session, Action.Type.ANSWER);
    assertDisallowedAction(session, Action.Type.NEXT);
    assertAllowedAction(session, Action.Type.PREVIOUS);
    assertDisallowedAction(session, Action.Type.COMPLETE);


    dialobSessionUpdater.applyCommands(toCommands(answer(toRef("q311"), "Hello")));
    assertErrorInactive(session, toRef("q111"), "REQUIRED");
    assertErrorInactive(session, toRef("q211"), "REQUIRED");
    assertErrorInactive(session, toRef("q311"), "REQUIRED");

    assertAllowedAction(session, Action.Type.ANSWER);
    assertDisallowedAction(session, Action.Type.NEXT);
    assertAllowedAction(session, Action.Type.PREVIOUS);
    assertAllowedAction(session, Action.Type.COMPLETE);


    Mockito.verifyNoMoreInteractions(functionRegistry);
  }

  @Test
  void shouldSetReadOnlyOnForAllQuestionsOfReadOnly() {
    FunctionRegistry functionRegistry = Mockito.mock(FunctionRegistry.class);
    DialobSessionEvalContextFactory sessionContextFactory = new DialobSessionEvalContextFactory(functionRegistry, null);
    DialobProgramFromFormCompiler compiler = new DialobProgramFromFormCompiler(functionRegistry);

    var dialobProgram = compiler.compileForm(new Form.Builder()
      .id("123")
      .name("123")
      .putData("questionnaire", new FormItem.Builder()
        .id("questionnaire")
        .type("questionnaire")
        .addItems("p1", "p2", "p3")
        .build())
      .putData("p1", new FormItem.Builder()
        .id("p1")
        .type("page")
        .addItems("g11","g12")
        .build())
      .putData("p2", new FormItem.Builder()
        .id("p2")
        .type("page")
        .readOnlyWhen("q111")
        .addItems("g21","g22")
        .build())
      .putData("p3", new FormItem.Builder()
        .id("p3")
        .type("page")
        .addItems("sg")
        .build())


      .putData("g11", new FormItem.Builder()
        .id("g11")
        .type("group")
        .addItems("q111","q112")
        .build())
      .putData("g12", new FormItem.Builder()
        .id("g12")
        .type("group")
        .readOnlyWhen("q112")
        .addItems("q121","q122")
        .build())
      .putData("g21", new FormItem.Builder()
        .id("g21")
        .type("group")
        .addItems("q211","q212")
        .build())
      .putData("g22", new FormItem.Builder()
        .id("g22")
        .type("group")
        .addItems("q221","q222")
        .build())
      .putData("sg", new FormItem.Builder()
        .id("sg")
        .type("surveygroup")
        .readOnlyWhen("q221")
        .build())


      .putData("q111", new FormItem.Builder()
        .id("q111")
        .type("boolean")
        .build())
      .putData("q112", new FormItem.Builder()
        .id("q112")
        .type("boolean")
        .build())
      .putData("q121", new FormItem.Builder()
        .id("q121")
        .type("boolean")
        .build())
      .putData("q122", new FormItem.Builder()
        .id("q122")
        .type("boolean")
        .build())
      .putData("q211", new FormItem.Builder()
        .id("q211")
        .type("boolean")
        .build())
      .putData("q212", new FormItem.Builder()
        .id("q212")
        .type("boolean")
        .build())
      .putData("q221", new FormItem.Builder()
        .id("q221")
        .type("boolean")
        .build())
      .putData("q222", new FormItem.Builder()
        .id("q222")
        .readOnlyWhen("q221")
        .type("boolean")
        .build())



      .metadata(new Form.Metadata.Builder()
        .label("xxx")
        .build())
      .build());


    DialobSession session = dialobProgram.createSession(sessionContextFactory, null, null, "fi", null);
    assertNotNull(session);
    DialobSessionUpdater updater = sessionContextFactory.createSessionUpdater(dialobProgram, session, false);

    updater.applyCommands(toCommands(answer(toRef("q111"), "true")));
    assertNotReadOnly(session, toRef("q111"));
    assertNotReadOnly(session, toRef("q112"));
    assertNotReadOnly(session, toRef("q121"));
    assertNotReadOnly(session, toRef("q122"));
    assertReadOnly(session, toRef("q211"));
    assertReadOnly(session, toRef("q212"));
    assertReadOnly(session, toRef("q221"));
    assertNotReadOnly(session, toRef("q222"));
    assertNotReadOnly(session, toRef("sg"));

    updater.applyCommands(toCommands(answer(toRef("q111"), "false")));
    updater.applyCommands(toCommands(answer(toRef("q112"), "true")));
    assertNotReadOnly(session, toRef("q111"));
    assertNotReadOnly(session, toRef("q112"));
    assertReadOnly(session, toRef("q121"));
    assertReadOnly(session, toRef("q122"));
    assertNotReadOnly(session, toRef("q211"));
    assertNotReadOnly(session, toRef("q212"));
    assertNotReadOnly(session, toRef("q221"));
    assertNotReadOnly(session, toRef("q222"));
    assertNotReadOnly(session, toRef("sg"));

    updater.applyCommands(toCommands(answer(toRef("q112"), "false")));
    updater.applyCommands(toCommands(nextPage()));
    updater.applyCommands(toCommands(answer(toRef("q221"), "true")));
    assertNotReadOnly(session, toRef("q111"));
    assertNotReadOnly(session, toRef("q112"));
    assertNotReadOnly(session, toRef("q121"));
    assertNotReadOnly(session, toRef("q122"));
    assertNotReadOnly(session, toRef("q211"));
    assertNotReadOnly(session, toRef("q212"));
    assertNotReadOnly(session, toRef("q221"));
    assertReadOnly(session, toRef("q222"));
    assertReadOnly(session, toRef("sg"));


    updater.applyCommands(toCommands(answer(toRef("q1"), null)));

    Mockito.verifyNoMoreInteractions(functionRegistry);
  }
}
