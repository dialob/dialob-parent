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
import io.dialob.session.engine.session.model.ImmutableItemStates;
import io.dialob.session.engine.session.model.ItemId;
import io.dialob.session.engine.session.model.ItemState;
import io.dialob.session.engine.session.model.ItemStates;
import org.immutables.value.Value;

import java.util.Objects;
import java.util.Set;

import static java.util.stream.Collectors.toMap;

@Value.Immutable
public interface CreateRowGroupFromPrototypeCommand extends SessionUpdateCommand {

  @Value.Parameter
  ItemId getItemPrototypeId();

  @NonNull
  @Override
  default ItemStates update(@NonNull final EvalContext context, @NonNull final ItemStates itemStates) {
    return getItemPrototypeId().getParent().flatMap(groupId -> {
      var currentItems = Set.copyOf(itemStates.getItemStates().get(groupId).getItems());
      var originalItems = context.getOriginalItemState(groupId).map(state -> Set.copyOf(state.getItems())).orElse(Set.of());

      final Sets.SetView<ItemId> newItems = Sets.difference(currentItems, originalItems);
      final Sets.SetView<ItemId> removedItems = Sets.difference(originalItems, currentItems);
      return context.findPrototype(getItemPrototypeId()).map(prototypeState -> (ItemStates) ImmutableItemStates.builder()
        .from(itemStates)
        .itemStates(itemStates.getItemStates().values().stream().filter(item -> !removedItems.contains(item.getId())).collect(toMap(itemState -> Objects.requireNonNull(itemState.getId()), item -> item)))
        .putAllItemStates(newItems.stream().map(prototypeState::withId).collect(toMap(ItemState::getId, item -> item)))
        .build());
    }).orElse(itemStates);
  }

  @NonNull
  @Override
  default Set<EventMatcher> getEventMatchers() {
    return getItemPrototypeId().getParent()
      .map(EventMatchers::whenItemsChanged)
      .map(Set::of)
      .orElseGet(Set::of);
  }
}
