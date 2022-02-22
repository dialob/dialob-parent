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

import com.google.common.collect.ImmutableSet;
import io.dialob.session.engine.program.EvalContext;
import io.dialob.session.engine.session.model.ImmutableItemIndex;
import io.dialob.session.engine.session.model.ItemId;
import io.dialob.session.engine.session.model.ItemState;
import org.immutables.value.Value;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.stream.Collectors;

import static io.dialob.session.engine.session.command.EventMatchers.whenValueUpdated;

@Value.Immutable
public interface InitRowGroupItemsCommand extends AbstractUpdateCommand<ItemId,ItemState>, ItemUpdateCommand {

  @Nonnull
  @Override
  default ItemState update(@Nonnull EvalContext context, @Nonnull ItemState itemState) {
    List<Integer> rowNumbers = (List<Integer>) itemState.getValue();
    if (rowNumbers == null) {
      rowNumbers = Collections.emptyList();
    }
    rowNumbers = new ArrayList<>(rowNumbers);
    List<ItemId> newItems = rowNumbers.stream().map(row -> ImmutableItemIndex.of(row, Optional.of(getTargetId()))).collect(Collectors.toList());
    return itemState.update()
      .setItems(newItems)
      .get();
  }

  @Nonnull
  @Override
  default Set<EventMatcher> getEventMatchers() {
    return ImmutableSet.of(whenValueUpdated(getTargetId()));
  }
}
