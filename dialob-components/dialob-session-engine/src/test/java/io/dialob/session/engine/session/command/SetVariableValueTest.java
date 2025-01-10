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

import static org.junit.jupiter.api.Assertions.assertEquals;

class SetVariableValueTest {

  @Test
  public void shouldSetContextValue() {
    ImmutableSetVariableValue setVariableValue = ImmutableSetVariableValue.builder()
      .targetId(IdUtils.toId("c1"))
      .value("new value")
      .build();

    EvalContext context = Mockito.mock(EvalContext.class);
    ItemState itemState = new ItemState(IdUtils.toId("c1"), null, "context", null, null);
    assertEquals("new value", setVariableValue.update(context, itemState).getValue());
  }
}
