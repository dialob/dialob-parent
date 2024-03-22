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
package io.dialob.questionnaire.service.submit;

import io.dialob.api.questionnaire.Questionnaire;
import io.dialob.questionnaire.service.api.AnswerSubmitHandler;
import lombok.extern.slf4j.Slf4j;
import edu.umd.cs.findbugs.annotations.NonNull;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public abstract class AbstractNormalizingPostSubmitHandler implements AnswerSubmitHandler {

  @Override
  public void submit(@NonNull AnswerSubmitHandler.Settings submitHandlerSettings, @NonNull Questionnaire document) {
    Map<String,Object> entries = new HashMap<>();
    document.getAnswers().forEach(answer -> entries.put(answer.getId(), answer.getValue()));
    document.getContext().forEach(contextValue -> entries.put(contextValue.getId(), contextValue.getValue()));

    entries.put("_id", document.getId());
    entries.put("_rev", document.getRev());
    entries.put("_formId", document.getMetadata().getFormId());
    entries.put("_formName", document.getMetadata().getFormName());
    sendDocument(submitHandlerSettings, entries);
  }

  protected abstract void sendDocument(AnswerSubmitHandler.Settings submitHandlerSettings, Map<String, Object> normalizedDocument);
}
