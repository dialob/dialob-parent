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

import edu.umd.cs.findbugs.annotations.NonNull;
import io.dialob.session.engine.program.EvalContext;
import io.dialob.session.engine.session.model.ItemId;
import io.dialob.session.engine.session.model.ItemIndex;
import io.dialob.session.engine.session.model.ItemState;

import java.math.BigInteger;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static io.dialob.session.engine.session.command.EventMatchers.whenValueUpdated;

record InitRowGroupItemsCommand(
  ItemId targetId,
  List<Trigger<ItemState>> triggers
) implements AbstractUpdateCommand<ItemId,ItemState>, ItemUpdateCommand {

  @NonNull
  @Override
  public ItemState update(@NonNull EvalContext context, @NonNull ItemState itemState) {
    List<BigInteger> rowNumbers = (List<BigInteger>) itemState.getValue();
    if (rowNumbers == null) {
      rowNumbers = Collections.emptyList();
    }
    var newItems = rowNumbers.stream().map(row -> {
      ItemId parent = targetId();
      return (ItemId) new ItemIndex(row.intValue(), parent);
    }).toList();
    return itemState.update()
      .setItems(newItems)
      .get();
  }

  @NonNull
  @Override
  public Set<EventMatcher> eventMatchers() {
    return Set.of(whenValueUpdated(targetId()));
  }

  @NonNull
  @Override
  public UpdateCommand<ItemId, ItemState> withTargetId(@NonNull ItemId targetId) {
    return new InitRowGroupItemsCommand(targetId, triggers);
  }
}
