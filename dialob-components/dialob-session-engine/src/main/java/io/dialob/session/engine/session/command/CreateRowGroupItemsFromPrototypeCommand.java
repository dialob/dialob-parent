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

import com.google.common.collect.Sets;
import edu.umd.cs.findbugs.annotations.NonNull;
import io.dialob.session.engine.program.EvalContext;
import io.dialob.session.engine.session.model.ItemId;
import io.dialob.session.engine.session.model.ItemState;
import io.dialob.session.engine.session.model.ItemStates;

import java.util.List;
import java.util.Objects;
import java.util.Set;

import static io.dialob.session.engine.session.command.EventMatchers.whenItemRemoved;
import static io.dialob.session.engine.session.command.EventMatchers.whenRowGroupItemsInit;
import static java.util.stream.Collectors.toMap;

record CreateRowGroupItemsFromPrototypeCommand(
  ItemId targetId,
  ItemId itemPrototypeId,
  List<Trigger<ItemStates>> triggers
) implements SessionUpdateCommand, UpdateCommand<ItemId, ItemStates> {

  @NonNull
  @Override
  public UpdateCommand<ItemId, ItemStates> withTargetId(@NonNull ItemId targetId) {
    return new CreateRowGroupItemsFromPrototypeCommand(targetId, itemPrototypeId(), triggers);
  }

  @NonNull
  @Override
  public ItemStates update(@NonNull final EvalContext context, @NonNull final ItemStates itemStates) {
    final ItemState currentItemState = itemStates.itemStates().get(targetId());
    Set<ItemId> currentItems = currentItemState != null ? Set.copyOf(currentItemState.getItems()) : Set.of();
    Set<ItemId> originalItems = context.getOriginalItemState(targetId()).map(state -> Set.copyOf(state.getItems())).orElse(Set.of());

    final Sets.SetView<ItemId> newItems = Sets.difference(currentItems, originalItems);
    final Sets.SetView<ItemId> removedItems = Sets.difference(originalItems, currentItems);
    if (newItems.isEmpty() && removedItems.isEmpty()) {
      return itemStates;
    }
    // remove removed items and errors related to those
    final ItemStates.Builder builder = new ItemStates.Builder()
      .from(itemStates)
      .itemStates(itemStates.itemStates().values().stream().filter(item -> !removedItems.contains(item.id())).collect(toMap(itemState -> Objects.requireNonNull(itemState.id()), item -> item)))
      .errorStates(itemStates.errorStates().values().stream().filter(errorState -> !removedItems.contains(errorState.id().itemId())).collect(toMap(errorState -> Objects.requireNonNull(errorState.id()), errorState -> errorState)));

    // add new items states
    newItems.stream()
      .flatMap(itemId -> context.findPrototype(itemId).map(prototype -> prototype.withId(itemId)).stream())
      .forEach(itemState -> builder.putItemStates(itemState.id(), itemState));

    // add error states
    newItems.stream()
      .flatMap(itemId -> context.findErrorPrototypes(itemId).map(prototype -> prototype.withErrorId(prototype.id().withItemId(itemId))))
      .forEach(errorState -> builder.putErrorStates(errorState.id(), errorState));
    return builder.build();
  }

  @NonNull
  @Override
  public Set<EventMatcher> eventMatchers() {
    return Set.of(
      whenRowGroupItemsInit(itemPrototypeId()),
      whenItemRemoved(itemPrototypeId())
    );
  }

}
