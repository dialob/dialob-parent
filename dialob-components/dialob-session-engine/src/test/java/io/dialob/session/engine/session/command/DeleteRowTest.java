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

import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DeleteRowTest {

  @Test
  void deleteRowShouldChangeRowStatusToBeRemoved() throws Exception {
    final ItemUpdateCommand deleteRow = CommandFactory.deleteRow(IdUtils.toId("rows.1"));
    final EvalContext context = Mockito.mock(EvalContext.class);

    ItemState itemState = new ItemState(IdUtils.toId("rows"), null, "rowgroup", null, true, null, null, null, null, null);
    itemState = itemState.update()
      .setStatus(ItemState.Status.OK)
      .setRowCanBeRemoved(true)
      .setValue(Arrays.asList(BigInteger.ONE))
      .get();

    itemState = deleteRow.update(context, itemState);

    assertTrue(((List<Integer>)itemState.getValue()).isEmpty());

  }
  @Test
  void deleteRowShouldNotRemoveNonExistingRow() throws Exception {
    final ItemUpdateCommand deleteRow = CommandFactory.deleteRow(IdUtils.toId("rows.1"));
    final EvalContext context = Mockito.mock(EvalContext.class);

    ItemState itemState = new ItemState(IdUtils.toId("rows"), null, "rowgroup", null, true, null, null, null, null, null);
    itemState = itemState.update()
      .setStatus(ItemState.Status.OK)
      .setRowCanBeRemoved(true)
      .setValue(Arrays.asList(2))
      .get();

    itemState = deleteRow.update(context, itemState);

    assertEquals(Arrays.asList(2), ((List<Integer>)itemState.getValue()));

  }

  @Test
  void deleteRowCannotRemoveRowWhenRowsMayNotBeRemoved() throws Exception {
    final ItemUpdateCommand deleteRow = CommandFactory.deleteRow(IdUtils.toId("rows.1"));
    final EvalContext context = Mockito.mock(EvalContext.class);
    final ItemState toBeRemoved = Mockito.mock(ItemState.class);
    Mockito.when(toBeRemoved.isRowCanBeRemoved()).thenReturn(false);

    Mockito.when(context.getItemState(Mockito.any())).thenReturn(Optional.of(toBeRemoved));

    ItemState itemState = new ItemState(IdUtils.toId("rows"), null, "rowgroup", null, true, null, null, null, null, null);
    itemState = itemState.update()
      .setStatus(ItemState.Status.OK)
      .setValue(Arrays.asList(1))
      .setRowCanBeRemoved(false)
      .get();

    itemState = deleteRow.update(context, itemState);
    assertEquals(Arrays.asList(1), ((List<Integer>)itemState.getValue()));

    assertEquals(ItemState.Status.OK, itemState.getStatus());
  }
}
