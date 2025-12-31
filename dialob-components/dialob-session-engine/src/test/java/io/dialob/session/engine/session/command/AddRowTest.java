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
import org.mockito.Mockito;

import java.math.BigInteger;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class AddRowTest {

  @Test
  void shouldAddRow() {
    var addRow = CommandFactory.addRow(IdUtils.toId("rows"));

    EvalContext context = Mockito.mock(EvalContext.class);

    ItemId id = IdUtils.toId("rows");
    ItemState itemState = ItemState.builder()
      .id(id)
      .type("rowgroup")
      .status(ItemState.Status.NEW)
      .bits(ItemState.DISPLAY_ITEM_BIT | ItemState.ACTIVE_BIT | ItemState.ROWS_CAN_BE_ADDED_BIT)
      .build();
    assertNull(itemState.getValue());

    itemState = addRow.update(context, itemState);
    assertEquals(1, ((List<BigInteger>)itemState.getValue()).size());
    org.assertj.core.api.Assertions.assertThat(((List<BigInteger>)itemState.getValue())).containsExactly(
      BigInteger.ZERO
    );

    itemState = addRow.update(context, itemState);
    org.assertj.core.api.Assertions.assertThat(((List<BigInteger>)itemState.getValue())).containsExactly(
      BigInteger.ZERO,
      BigInteger.ONE
    );
  }

  @Test
  void shouldNotAddRowIfRowsCannotBeAdded() {
    var addRow = CommandFactory.addRow(IdUtils.toId("rows"));
    EvalContext context = Mockito.mock(EvalContext.class);

    ItemId id = IdUtils.toId("rows");
    ItemState itemState = ItemState.builder()
      .id(id)
      .type("rowgroup")
      .status(ItemState.Status.NEW)
      .bits(ItemState.DISPLAY_ITEM_BIT | ItemState.ACTIVE_BIT | ItemState.ROWS_CAN_BE_ADDED_BIT)
      .build();
    itemState = itemState.update().setRowsCanBeAdded(false).get();

    assertEquals(0, itemState.items().size());
    itemState = addRow.update(context, itemState);
    assertEquals(0, itemState.items().size());
  }
}
