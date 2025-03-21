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
package io.dialob.questionnaire.service.api;

import edu.umd.cs.findbugs.annotations.NonNull;
import io.dialob.api.proto.Action;
import io.dialob.api.proto.Actions;
import io.dialob.questionnaire.service.api.session.QuestionnaireSession;

import java.util.List;
import java.util.function.Function;

public interface ActionProcessingService {

  @NonNull
  @Deprecated // TODO replace with computeSessionUpdate
  Actions answerQuestion(@NonNull String questionnaireId, String revision, @NonNull List<Action> actions);

  @NonNull
  QuestionnaireSession computeSessionUpdate(@NonNull String questionnaireId, boolean openIfClosed, Function<QuestionnaireSession,QuestionnaireSession> updateFunction);

}
