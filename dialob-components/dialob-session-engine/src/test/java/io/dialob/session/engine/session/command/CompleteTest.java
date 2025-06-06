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
package io.dialob.session.engine.session.command;

import io.dialob.session.engine.program.EvalContext;
import io.dialob.session.engine.session.model.IdUtils;
import io.dialob.session.engine.session.model.ItemState;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.mockito.Mockito.when;

class CompleteTest {

  @Test
  void shouldNotCompleteWhenQuestionnaireHasInvalidAnswers() {
    Complete complete = ImmutableComplete.builder().targetId(IdUtils.toId("questionnaire")).build();
    EvalContext context = Mockito.mock(EvalContext.class);
    ItemState questionnaire = Mockito.mock(ItemState.class);
    when(questionnaire.isInvalidAnswers()).thenReturn(true);

    complete.update(context, questionnaire);

    Mockito.verify(questionnaire).isInvalidAnswers();
    Mockito.verifyNoMoreInteractions(context, questionnaire);
  }

  @Test
  void shouldCompleteWhenQuestionnaireDoNotHaveInvalidAnswers() {
    Complete complete = ImmutableComplete.builder().targetId(IdUtils.toId("questionnaire")).build();
    EvalContext context = Mockito.mock(EvalContext.class);
    ItemState questionnaire = Mockito.mock(ItemState.class);
    when(questionnaire.isInvalidAnswers()).thenReturn(false);

    complete.update(context, questionnaire);

    Mockito.verify(questionnaire).isInvalidAnswers();
    Mockito.verify(context).complete();
    Mockito.verifyNoMoreInteractions(context, questionnaire);

  }

}
