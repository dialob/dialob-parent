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
package io.dialob.questionnaire.service.api;

import edu.umd.cs.findbugs.annotations.NonNull;
import io.dialob.api.proto.Action;
import io.dialob.api.proto.Actions;
import io.dialob.api.proto.ActionsFactory;

import java.util.Collections;
import java.util.List;

// TODO remove this. move action type filtering.
@Deprecated
public interface QuestionnaireActionsService {

  default Actions handleAction(@NonNull String questionnaireId, @NonNull Action action, String revision) {
    if (isAcceptableAction(action)) {
      return answerQuestion(questionnaireId, revision, Collections.singletonList(action));
    }
    return ActionsFactory.actions();
  }

  default boolean isAcceptableAction(Action action) {
    return action.getType().isClientAction();
  }

  @NonNull
  Actions answerQuestion(@NonNull String questionnaireId, String revision, @NonNull List<Action> actions);

}
