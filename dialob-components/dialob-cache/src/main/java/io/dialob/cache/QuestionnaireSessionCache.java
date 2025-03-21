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
package io.dialob.cache;

import io.dialob.questionnaire.service.api.session.QuestionnaireSession;
import org.springframework.cache.Cache;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Interface for managing a cache of {@link QuestionnaireSession} objects. This interface extends the base {@link Cache}.
 * It provides methods to interact with the cache including retrieving the size, iterating over the sessions,
 * and evicting sessions with pre-defined closing behavior.
 */
public interface QuestionnaireSessionCache extends Cache {

  /**
   * Retrieves the number of items currently stored in the cache.
   *
   * @return the total number of {@link QuestionnaireSession} objects present in the cache
   */
  int size();

  /**
   * Executes the given {@link Consumer} action for each {@link QuestionnaireSession} in the cache.
   *
   * @param sessionConsumer a {@link Consumer} functional interface that operates on each {@link QuestionnaireSession}
   */
  void forEach(Consumer<QuestionnaireSession> sessionConsumer);

  /**
   * Removes a {@link QuestionnaireSession} from the cache based on the specified questionnaire ID.
   * Allows customization of session behavior prior to removal using the provided callback function.
   *
   * @param questionnaireId the identifier of the questionnaire session to be evicted from the cache
   * @param beforeCloseCallback a callback function to modify or handle the {@link QuestionnaireSession}
   *                            before it is closed and evicted from the cache
   */
  void evict(String questionnaireId, Function<QuestionnaireSession,QuestionnaireSession> beforeCloseCallback);

}
