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

import io.dialob.api.proto.Actions;
import io.dialob.questionnaire.service.api.event.QuestionnaireEventPublisher;
import io.dialob.session.engine.program.DialobProgram;
import io.dialob.session.engine.program.DialobSessionEvalContextFactory;
import io.dialob.session.engine.session.model.DialobSession;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class DialobQuestionnaireSessionServiceFacadeTest {

  @Test
  void shouldDelegateCalls() {
    QuestionnaireEventPublisher eventPublisher = Mockito.mock(QuestionnaireEventPublisher.class);
    DialobSessionEvalContextFactory sessionContextFactory = Mockito.mock(DialobSessionEvalContextFactory.class);
    AsyncFunctionInvoker asyncFunctionInvoker = Mockito.mock(AsyncFunctionInvoker.class);

    DialobQuestionnaireSessionServiceFacade facade = new DialobQuestionnaireSessionServiceFacade(eventPublisher, sessionContextFactory, asyncFunctionInvoker);

    DialobProgram dialobProgram = mock(DialobProgram.class);
    DialobSession dialobSession = mock(DialobSession.class);
    facade.createDialobSessionUpdater(dialobProgram, dialobSession, true);
    verify(sessionContextFactory).createSessionUpdater(dialobProgram, dialobSession, true);

    facade.createAsyncFunctionCallVisitor("sid");
    verify(asyncFunctionInvoker).createVisitor("sid");

    facade.created("qid");
    verify(eventPublisher).created("qid");

    facade.opened("qid");
    verify(eventPublisher).opened("qid");

    facade.completed("tid", "qid");
    verify(eventPublisher).completed("tid", "qid");

    Actions actions = mock(Actions.class);
    facade.actions("qid", actions);
    verify(eventPublisher).actions("qid", actions);
  }
}
