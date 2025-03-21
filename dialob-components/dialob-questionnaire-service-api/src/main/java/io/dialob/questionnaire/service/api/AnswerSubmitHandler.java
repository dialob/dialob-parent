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
import io.dialob.api.questionnaire.Questionnaire;

import java.util.Map;

/**
 * The AnswerSubmitHandler interface provides functionality for handling the submission
 * of questionnaire answers. Implementations of this interface define how the submitted
 * data is processed and stored or forwarded to a specific system.
 */
public interface AnswerSubmitHandler {

  interface Settings {

    String getBeanName();

    Map<String,Object> getProperties();

  }

  /**
   * Handles the submission of a questionnaire document using the provided settings.
   * The method processes the given questionnaire based on the configurations defined
   * in the submitHandlerSettings parameter.
   *
   * @param submitHandlerSettings configurations and properties used for handling the submission,
   *                              such as system-specific settings or parameters.
   * @param document the questionnaire to be submitted, including answers, metadata, context values,
   *                 and associated properties.
   */
  void submit(@NonNull Settings submitHandlerSettings, @NonNull Questionnaire document);
}
