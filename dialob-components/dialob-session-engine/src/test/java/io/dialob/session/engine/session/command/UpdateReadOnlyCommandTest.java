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
import io.dialob.session.engine.program.expr.arith.BooleanOperators;
import io.dialob.session.engine.session.model.IdUtils;
import io.dialob.session.engine.session.model.ItemId;
import io.dialob.session.engine.session.model.ItemState;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;

class UpdateReadOnlyCommandTest {

  @Test
  void shouldSetReadOnly() {
    var command = CommandFactory.readOnlyUpdate(IdUtils.toId("c1"), BooleanOperators.TRUE);

    EvalContext context = Mockito.mock(EvalContext.class);
    ItemId id = IdUtils.toId("c1");
    ItemState itemState = ItemState.builder()
      .id(id)
      .type("context")
      .status(ItemState.Status.NEW)
      .bits(ItemState.ACTIVE_BIT | ItemState.ROWS_CAN_BE_ADDED_BIT)
      .build();

    assertFalse(itemState.isReadOnly());
    itemState = command.update(context, itemState);
    assertTrue(itemState.isReadOnly());
  }

  @Test
  void shouldUpdateTargetId() {
    UpdateReadOnlyCommand command = (UpdateReadOnlyCommand) CommandFactory.readOnlyUpdate(IdUtils.toId("c1"), BooleanOperators.TRUE);
    UpdateReadOnlyCommand state = (UpdateReadOnlyCommand) command.withTargetId(IdUtils.toId("c2"));
    assertEquals(IdUtils.toId("c2"), state.targetId());
    assertSame(command.expression(), state.expression());
    assertEquals(command.triggers(), state.triggers());
  }

}
