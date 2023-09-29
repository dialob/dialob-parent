/*
 * Copyright © 2015 - 2021 ReSys (info@dialob.io)
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

import edu.umd.cs.findbugs.annotations.NonNull;
import io.dialob.session.engine.program.EvalContext;
import io.dialob.session.engine.session.model.ItemId;
import io.dialob.session.engine.session.model.ItemIndex;
import io.dialob.session.engine.session.model.ItemState;
import org.immutables.value.Value;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Value.Immutable
public interface DeleteRow extends AbstractUpdateCommand<ItemId, ItemState>, ItemUpdateCommand {

  @Value.Parameter(order = 1)
  ItemId getToBeRemoved();

  @Override
  @NonNull
  default ItemState update(@NonNull EvalContext context, @NonNull ItemState itemState) {
    List<Integer> rowNumbers = (List<Integer>) itemState.getValue();
    if (rowNumbers == null) {
      rowNumbers = Collections.emptyList();
    }
    rowNumbers = new ArrayList<>(rowNumbers);
    ItemId toBeRemoved = getToBeRemoved();

    var rowToBeRemoved = context.getItemState(toBeRemoved);
    if (rowToBeRemoved.isPresent() && !rowToBeRemoved.get().isRowCanBeRemoved()) {
      return itemState;
    }

    Integer rowToRemove = null;
    if (toBeRemoved instanceof ItemIndex) {
      rowToRemove = ((ItemIndex) toBeRemoved).getIndex();
    }
    rowNumbers.remove(rowToRemove);
    return itemState.update()
      .setAnswer(rowNumbers)
      .setValue(rowNumbers)
      .get();
  }

}
