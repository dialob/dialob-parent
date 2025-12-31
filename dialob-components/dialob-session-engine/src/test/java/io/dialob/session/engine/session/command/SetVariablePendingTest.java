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
import io.dialob.session.engine.session.model.ItemId;
import io.dialob.session.engine.session.model.ItemState;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;

class SetVariablePendingTest {

  @Test
  void updatesItemStateToPending() {
    SetVariablePending setVariablePending = new SetVariablePending(IdUtils.toId("item"), Collections.emptyList());
    EvalContext context = mock(EvalContext.class);
    ItemId id = IdUtils.toId("item");
    ItemState itemState = ItemState.builder()
      .id(id)
      .prototypeId(null)
      .type("variable")
      .view(null)
      .status(ItemState.Status.NEW)
      .bits(0 | ItemState.ACTIVE_BIT | ItemState.ROWS_CAN_BE_ADDED_BIT)
      .valueSetId(null)
      .answer("answer")
      .value("Hello")
      .defaultValue(null)
      .activePage(null)
      .build().update().setLabel("label").get();

    ItemState updatedItemState = setVariablePending.update(context, itemState);

    assertNull(updatedItemState.getValue());
    assertEquals(ItemState.Status.PENDING, updatedItemState.status());
  }

  @Test
  void retainsOtherItemStateProperties() {
    var setVariablePending = CommandFactory.setVariablePending(IdUtils.toId("item"));
    EvalContext context = mock(EvalContext.class);
    ItemId id = IdUtils.toId("item");
    ItemState itemState = ItemState.builder()
      .id(id)
      .prototypeId(null)
      .type("variable")
      .view(null)
      .status(ItemState.Status.NEW)
      .bits(0 | ItemState.ACTIVE_BIT | ItemState.ROWS_CAN_BE_ADDED_BIT)
      .valueSetId(null)
      .answer("answer")
      .value("Hello")
      .defaultValue(null)
      .activePage(null)
      .build().update().setLabel("label").get();

    ItemState updatedItemState = setVariablePending.update(context, itemState);

    assertEquals("answer", updatedItemState.answer());
    assertEquals("label", updatedItemState.label());
  }
}
