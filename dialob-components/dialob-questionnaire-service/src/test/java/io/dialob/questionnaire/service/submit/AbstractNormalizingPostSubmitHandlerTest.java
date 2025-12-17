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
package io.dialob.questionnaire.service.submit;

import io.dialob.api.questionnaire.Answer;
import io.dialob.api.questionnaire.ContextValue;
import io.dialob.api.questionnaire.Questionnaire;
import io.dialob.questionnaire.service.api.AnswerSubmitHandler;
import org.apache.commons.lang3.mutable.MutableObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Map;

class AbstractNormalizingPostSubmitHandlerTest {

  @Test
  void shouldSerialize() {
    MutableObject<Map<String, Object>> result = new MutableObject<>();
    AnswerSubmitHandler.Settings settings = Mockito.mock(AnswerSubmitHandler.Settings.class);
    final AbstractNormalizingPostSubmitHandler submitHandler = new AbstractNormalizingPostSubmitHandler() {
      @Override
      protected void sendDocument(AnswerSubmitHandler.Settings submitHandlerSettings, Map<String, Object> normalizedDocument) {
        result.setValue(normalizedDocument);
      }
    };

    submitHandler.submit(settings, new Questionnaire.Builder()
      .id("q-12")
      .rev("123")
      .metadata(new Questionnaire.Metadata.Builder()
        .formId("f-1")
        .build())
      .addAnswers(Answer.of("a1", false))
      .addAnswers(Answer.of("a2", 1))
      .addAnswers(Answer.of("a3", "hello"))
      .addAnswers(Answer.of("a4", 1.0))
      .addAnswers(Answer.of("a5", null))
      .addContext(ContextValue.of("c1", false))
      .addContext(ContextValue.of("c2", 1))
      .addContext(ContextValue.of("c3", "hello"))
      .addContext(ContextValue.of("c4", 1.0))
      .addContext(ContextValue.of("c5", null))
      .build());


    Assertions.assertEquals(14, result.getValue().size());
  }

}
