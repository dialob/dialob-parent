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
import io.dialob.questionnaire.service.api.session.FormFinder;
import io.dialob.rule.parser.function.FunctionRegistry;
import io.dialob.session.engine.program.DialobProgram;
import io.dialob.session.engine.program.DialobSessionEvalContextFactory;
import io.dialob.session.engine.program.EvalContext;
import io.dialob.session.engine.session.ActionToCommandMapper;
import io.dialob.session.engine.session.DialobSessionUpdater;
import io.dialob.session.engine.session.model.DialobSession;
import io.dialob.session.engine.session.model.IdUtils;
import io.dialob.session.engine.session.model.ImmutableItemRef;
import io.dialob.session.engine.sp.AsyncFunctionInvoker;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;

import java.math.BigInteger;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

class DialobProgramServiceTest extends AbstractDialobProgramTest {

  @Test
  void shouldConstructFormProgram() throws Exception {
    FormFinder formFinder = mock(FormFinder.class);
    FunctionRegistry functionRegistry = mock(FunctionRegistry.class);
    DialobProgramFromFormCompiler programFromFormCompiler = new DialobProgramFromFormCompiler(functionRegistry);
    AsyncFunctionInvoker asyncFunctionInvoker = mock(AsyncFunctionInvoker.class);
    DialobSessionEvalContextFactory sessionContextFactory = new DialobSessionEvalContextFactory(functionRegistry, null);
    QuestionnaireDialobProgramService service = QuestionnaireDialobProgramService.newBuilder().setFormDatabase(formFinder).setProgramFromFormCompiler(programFromFormCompiler).build();
//    Form formDocument = Mockito.mock(Form.class);
    String formFile = "form.json";
    Form formDocument = loadForm(formFile);

    DialobProgram dialobProgram = programFromFormCompiler.compileForm(formDocument);
    DialobSession dialobSession = dialobProgram.createSession(sessionContextFactory, null, null, "fi", null);
    assertEquals(Optional.of((ImmutableItemRef) IdUtils.toId("page1")), dialobSession.getRootItem().getActivePage());

    DialobSessionUpdater sessionUpdater = sessionContextFactory.createSessionUpdater(dialobProgram, dialobSession, false);

    final EvalContext.UpdatedItemsVisitor visitor = mock(EvalContext.UpdatedItemsVisitor.class);
    final EvalContext.UpdatedItemsVisitor.UpdatedErrorStateVisitor errorVisitor = mock(EvalContext.UpdatedItemsVisitor.UpdatedErrorStateVisitor.class);
    final EvalContext.UpdatedItemsVisitor.UpdatedItemStateVisitor itemVisitor = mock(EvalContext.UpdatedItemsVisitor.UpdatedItemStateVisitor.class);
    final EvalContext.UpdatedItemsVisitor.UpdatedValueSetVisitor valueSetVisitor = mock(EvalContext.UpdatedItemsVisitor.UpdatedValueSetVisitor.class);

    when(visitor.visitUpdatedItems()).thenReturn(Optional.of(itemVisitor));
    when(visitor.visitUpdatedErrorStates()).thenReturn(Optional.of(errorVisitor));
    when(visitor.visitUpdatedValueSets()).thenReturn(Optional.of(valueSetVisitor));

    InOrder order = Mockito.inOrder(visitor, errorVisitor, itemVisitor, valueSetVisitor);

    assertEquals(Optional.of((ImmutableItemRef) IdUtils.toId("page1")), dialobSession.getRootItem().getActivePage());
    sessionUpdater.applyCommands(ActionToCommandMapper.toCommands(nextPage()));
    assertEquals(Optional.of((ImmutableItemRef) IdUtils.toId("page1")), dialobSession.getRootItem().getActivePage());

    sessionUpdater.applyCommands(ActionToCommandMapper.toCommands(answer(toRef("question1"), "35")));
    assertValueEquals(dialobSession,toRef("question1"), BigInteger.valueOf(35));

    sessionUpdater.applyCommands(ActionToCommandMapper.toCommands(answer(toRef("question3"), "true")));
    assertActive(dialobSession, toRef("question3"));
    assertValueEquals(dialobSession,toRef("question3"),true);
    sessionUpdater.applyCommands(ActionToCommandMapper.toCommands(answer(toRef("question4"), "true")));
    assertValueEquals(dialobSession,toRef("question4"),true);

    sessionUpdater.applyCommands(ActionToCommandMapper.toCommands(answer(toRef("question5"), "30001")));
    assertValueEquals(dialobSession,toRef("question5"),BigInteger.valueOf(30001));

    sessionUpdater.applyCommands(ActionToCommandMapper.toCommands(answer(toRef("question6"), "opt2")))
      .accept(visitor);
    assertValueEquals(dialobSession,toRef("question6"),"opt2");

    order.verify(visitor).start();
    order.verify(visitor).visitUpdatedItems();
    order.verify(itemVisitor, times(1)).visitUpdatedItemState(argThat(activeItem("questionnaire")), argThat(activeItem("questionnaire")));
    order.verify(itemVisitor, times(1)).visitUpdatedItemState(argThat(inactiveItem("question9")), argThat(activeItem("question9")));
    order.verify(itemVisitor, times(1)).visitUpdatedItemState(argThat(inactiveItem("page2")), argThat(activeItem("page2")));
    order.verify(itemVisitor).end();
    order.verify(visitor).visitUpdatedErrorStates();
    order.verify(errorVisitor).end();
    order.verify(visitor).visitUpdatedValueSets();
    order.verify(valueSetVisitor).end();
    order.verify(visitor).visitAsyncFunctionCalls();
    order.verify(visitor).end();
    order.verifyNoMoreInteractions();
    Mockito.verifyNoMoreInteractions(visitor);
//    DialobProgram formProgram = service.compileForm(formDocument);
    assertNotNull(dialobProgram);
  }

  public Form loadForm(String formFile) throws java.io.IOException {
    return objectMapper.readValue(Thread.currentThread().getContextClassLoader().getResourceAsStream(formFile),Form.class);
  }

}
