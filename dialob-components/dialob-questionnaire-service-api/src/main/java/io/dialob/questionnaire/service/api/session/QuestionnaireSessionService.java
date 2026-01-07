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
package io.dialob.questionnaire.service.api.session;


import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;


public interface QuestionnaireSessionService {

  /**
   * This method returns a session from cache if it's present and adds a new session to cache.
   * Requires cache configuration.
   *
   * @param questionnaireId id of the questionnaire
   * @return cached or a new session
   */
  @Nullable
  default QuestionnaireSession findOne(@NonNull String questionnaireId) {
    return findOne(questionnaireId, true);
  }

  /**
   * This method returns a session from cache if it's present and adds a new session to cache if requested.
   * Requires cache configuration.
   *
   * @param questionnaireId id of the questionnaire
   * @param openIfClosed load session if not present in cache
   * @return session or null if session is not present in cache
   */
  @Nullable
  QuestionnaireSession findOne(@NonNull String questionnaireId, boolean openIfClosed);

}
