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

import edu.umd.cs.findbugs.annotations.NonNull;
import io.dialob.api.proto.Actions;
import io.dialob.questionnaire.service.api.event.QuestionnaireEventPublisher;
import io.dialob.session.engine.program.DialobProgram;
import io.dialob.session.engine.program.DialobSessionEvalContextFactory;
import io.dialob.session.engine.program.EvalContext;
import io.dialob.session.engine.session.DialobSessionUpdater;
import io.dialob.session.engine.session.model.DialobSession;

public record DialobQuestionnaireSessionServiceFacade(
  @NonNull
  QuestionnaireEventPublisher eventPublisher,
  @NonNull
  DialobSessionEvalContextFactory sessionContextFactory,
  @NonNull
  AsyncFunctionInvoker asyncFunctionInvoker
) implements DialobQuestionnaireSession.ServiceFacade {

  @Override
  public DialobSessionUpdater createDialobSessionUpdater(@NonNull DialobProgram dialobProgram, @NonNull DialobSession dialobSession, boolean activating) {
    return sessionContextFactory.createSessionUpdater(dialobProgram, dialobSession, activating);
  }

  @Override
  public EvalContext.UpdatedItemsVisitor.AsyncFunctionCallVisitor createAsyncFunctionCallVisitor(String sessionId) {
    return asyncFunctionInvoker.createVisitor(sessionId);
  }

  @Override
  public void created(@NonNull String questionnaireId) {
    eventPublisher.created(questionnaireId);
  }

  @Override
  public void opened(@NonNull String questionnaireId) {
    eventPublisher.opened(questionnaireId);
  }

  @Override
  public void completed(String tenantId, @NonNull String questionnaireId) {
    eventPublisher.completed(tenantId, questionnaireId);
  }

  @Override
  public void actions(@NonNull String questionnaireId, @NonNull Actions actions) {
    eventPublisher.actions(questionnaireId, actions);
  }

}
