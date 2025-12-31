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
package io.dialob.session.engine.program.expr.arith;

import io.dialob.session.engine.program.EvalContext;
import io.dialob.session.engine.session.model.IdUtils;
import io.dialob.session.engine.session.model.ItemId;
import io.dialob.session.engine.session.model.ItemState;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

class IsNullOperatorTest {

  @Test
  void shouldInspectItemById() {

    IsNullOperator operator = new IsNullOperator.Builder().itemId(IdUtils.toId("itemi")).build();
    EvalContext context = Mockito.mock(EvalContext.class);
    ItemId id = IdUtils.toId("itemi");
    ItemState item = ItemState.builder()
      .id(id)
      .prototypeId(null)
      .type("text")
      .view(null)
      .valueSetId(null)
      .status(ItemState.Status.NEW)
      .bits(ItemState.ACTIVE_BIT | ItemState.ROWS_CAN_BE_ADDED_BIT)
      .build();

    when(context.getItemState(IdUtils.toId("itemi"))).thenReturn(Optional.of(item));

    assertTrue(operator.eval(context));

    item = item.update().setValue("nonnull").get();
    when(context.getItemState(IdUtils.toId("itemi"))).thenReturn(Optional.of(item));
    assertFalse(operator.eval(context));

  }

}
