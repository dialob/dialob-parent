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
import io.dialob.api.proto.ActionsFactory;
import io.dialob.questionnaire.service.api.session.QuestionnaireSession;
import io.dialob.questionnaire.service.api.session.QuestionnaireSessionService;
import io.dialob.rule.parser.function.FunctionRegistry;
import io.dialob.session.engine.program.EvalResult;
import io.dialob.session.engine.session.model.IdUtils;

import java.util.Collections;

public class AsyncFunctionInvoker {

  private final FunctionRegistry registry;

  private final QuestionnaireSessionService service;

  public AsyncFunctionInvoker(@NonNull FunctionRegistry registry,
                              @NonNull QuestionnaireSessionService service) {
    this.registry = registry;
    this.service = service;
  }

  public EvalResult.UpdatedItemsVisitor.AsyncFunctionCallVisitor createVisitor(String sessionId) {
    return asyncFunctionCall ->
      registry.invokeFunctionAsync(new AsyncFunctionCallback(service, sessionId, IdUtils.toString(asyncFunctionCall.getTargetId().get())),
        asyncFunctionCall.functionName(),
        asyncFunctionCall.args());
  }


  private record AsyncFunctionCallback(
    QuestionnaireSessionService service,
    String sessionId,
    String targetId
  ) implements FunctionRegistry.FunctionCallback {

    @Override
      public void succeeded(Object result) {
        QuestionnaireSession questionnaireSession = service.findOne(sessionId, true);
        questionnaireSession.dispatchActions(Collections.singletonList(ActionsFactory.setValue(
          targetId,
          result
        )));
      }

      @Override
      public void failed(@NonNull String error) {
        QuestionnaireSession questionnaireSession = service.findOne(sessionId, true);
        questionnaireSession.dispatchActions(Collections.singletonList(ActionsFactory.setFailed(
          targetId,
          error
        )));
      }
    }


}
