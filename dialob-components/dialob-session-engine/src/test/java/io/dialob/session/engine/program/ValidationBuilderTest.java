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
package io.dialob.session.engine.program;

import io.dialob.api.form.FormValidationError;
import io.dialob.session.engine.session.model.IdUtils;
import io.dialob.session.engine.session.model.ItemId;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.*;

class ValidationBuilderTest {

  @Test
  void validationBuilderShouldMapanswerToParentQuestion() {
    QuestionBuilder qb = mock(QuestionBuilder.class);
    ValidationBuilder vb = new ValidationBuilder(qb, "ec1");
    when(qb.getAliases()).thenReturn(Map.of());
    when(qb.getId()).thenReturn(IdUtils.toId("q1"));
    Map<String, ItemId> aliases = vb.getAliases();

    assertEquals(IdUtils.toId("q1"), aliases.get("answer"));
    assertEquals(1, aliases.size());

    verify(qb, atLeastOnce()).getId();
    verify(qb).getAliases();
    verify(qb).getHoistingGroup();
    verify(qb).getProgramBuilder();
    verifyNoMoreInteractions(qb);
  }

  @Test
  void validationBuilderShouldMergeParentAliases() {
    QuestionBuilder qb = mock(QuestionBuilder.class);
    ValidationBuilder vb = new ValidationBuilder(qb, "ec1");
    when(qb.getAliases()).thenReturn(Map.of("q2", IdUtils.toId("g1.g4")));
    when(qb.getId()).thenReturn(IdUtils.toId("q1"));

    Map<String, ItemId> aliases = vb.getAliases();

    assertEquals(IdUtils.toId("g1.g4"), aliases.get("q2"));
    assertEquals(IdUtils.toId("q1"), aliases.get("answer"));
    assertEquals(2, aliases.size());

    verify(qb, atLeastOnce()).getId();
    verify(qb).getAliases();
    verify(qb).getHoistingGroup();
    verify(qb).getProgramBuilder();
    verifyNoMoreInteractions(qb);
  }

  @Test
  void shouldCreateValidationWhenActivationRuleIsUndefined() {
    QuestionBuilder qb = mock(QuestionBuilder.class);
    Consumer<FormValidationError> errorConsumer = mock(Consumer.class);
    when(qb.getId()).thenReturn(IdUtils.toId("q1"));
    ValidationBuilder vb = new ValidationBuilder(qb, "ec1");
    vb.setPrototype(false).setLabel("fi","err");
    vb.afterExpressionCompilation(errorConsumer);
    assertSame(qb, vb.build());

    verify(qb).getHoistingGroup();
    verify(qb).getId();
    verify(qb).getProgramBuilder();
    verifyNoMoreInteractions(qb, errorConsumer);
  }

}
