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

import io.dialob.api.questionnaire.ImmutableAnswer;
import io.dialob.api.questionnaire.ImmutableContextValue;
import io.dialob.api.questionnaire.ImmutableQuestionnaire;
import io.dialob.api.questionnaire.ImmutableQuestionnaireMetadata;
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

    submitHandler.submit(settings, ImmutableQuestionnaire.builder()
      .id("q-12")
      .rev("123")
      .metadata(ImmutableQuestionnaireMetadata.builder()
        .formId("f-1")
        .build())
      .addAnswers(ImmutableAnswer.of("a1", false))
      .addAnswers(ImmutableAnswer.of("a2", 1))
      .addAnswers(ImmutableAnswer.of("a3", "hello"))
      .addAnswers(ImmutableAnswer.of("a4", 1.0))
      .addAnswers(ImmutableAnswer.of("a5", null))
      .addContext(ImmutableContextValue.of("c1", false))
      .addContext(ImmutableContextValue.of("c2", 1))
      .addContext(ImmutableContextValue.of("c3", "hello"))
      .addContext(ImmutableContextValue.of("c4", 1.0))
      .addContext(ImmutableContextValue.of("c5", null))
      .build());


    Assertions.assertEquals(14, result.getValue().size());
  }

}
