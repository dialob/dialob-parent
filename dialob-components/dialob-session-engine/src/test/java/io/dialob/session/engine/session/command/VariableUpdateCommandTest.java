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

import io.dialob.session.engine.program.model.Expression;
import io.dialob.session.engine.session.model.IdUtils;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

class VariableUpdateCommandTest {

  @Test
  void test() {
    Expression expression = mock();
    var command = new  VariableUpdateCommand(IdUtils.toId("target"), expression, List.of());
    assertEquals(IdUtils.toId("target"), command.targetId());
    VariableUpdateCommand target2 = command.withTargetId(IdUtils.toId("target2"));
    assertEquals(IdUtils.toId("target2"), target2.targetId());
    assertEquals(expression, target2.expression());
  }

}
