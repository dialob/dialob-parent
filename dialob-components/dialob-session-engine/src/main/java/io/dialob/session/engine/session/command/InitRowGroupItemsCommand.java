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
import edu.umd.cs.findbugs.annotations.NonNull;
import io.dialob.session.engine.program.EvalContext;
import io.dialob.session.engine.session.model.ItemState;
import io.dialob.session.model.ImmutableItemIndex;
import io.dialob.session.model.ItemId;
import org.immutables.value.Value;

import java.math.BigInteger;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static io.dialob.session.engine.session.command.EventMatchers.whenValueUpdated;

@Value.Immutable
public interface InitRowGroupItemsCommand extends AbstractUpdateCommand<ItemId,ItemState>, ItemUpdateCommand {

  @NonNull
  @Override
  default ItemState update(@NonNull EvalContext context, @NonNull ItemState itemState) {
    List<BigInteger> rowNumbers = (List<BigInteger>) itemState.getValue();
    if (rowNumbers == null) {
      rowNumbers = Collections.emptyList();
    }
    List<ItemId> newItems = rowNumbers.stream().map(row -> ImmutableItemIndex.of(row.intValue(), Optional.of(getTargetId()))).collect(Collectors.toList());
    return itemState.update()
      .setItems(newItems)
      .get();
  }

  @NonNull
  @Override
  default Set<EventMatcher> getEventMatchers() {
    return ImmutableSet.of(whenValueUpdated(getTargetId()));
  }
}
