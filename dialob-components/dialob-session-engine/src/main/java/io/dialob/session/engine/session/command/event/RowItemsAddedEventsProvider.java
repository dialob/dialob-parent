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
package io.dialob.session.engine.session.command.event;

import com.google.common.collect.Sets;
import io.dialob.session.engine.session.command.Triggers;
import io.dialob.session.engine.session.model.ItemId;
import io.dialob.session.engine.session.model.ItemStates;
import org.immutables.value.Value;

import java.util.Set;
import java.util.stream.Stream;

@Value.Immutable
public interface RowItemsAddedEventsProvider extends Triggers.EventsProvider<ItemStates> {

  @Value.Parameter
  ItemId getRowProtoTypeId();

  default Stream<Event> createEvents(ItemStates originalState, ItemStates updatedState) {
    if (originalState == null && updatedState == null) {
      return Stream.of(ImmutableItemAddedEvent.of(getRowProtoTypeId(), getRowProtoTypeId()));
    }
    if (updatedState == null) {
      return Stream.empty();
    }
    Set<ItemId> newItems = updatedState.getItemStates().keySet();
    if (originalState != null) {
      newItems = Sets.newHashSet(newItems);
      newItems.removeAll(originalState.getItemStates().keySet());
    }
    return newItems.stream().map(itemId -> ImmutableItemAddedEvent.of(itemId, getRowProtoTypeId()));
  }
}
