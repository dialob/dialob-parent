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
import io.dialob.session.engine.session.command.event.*;
import io.dialob.session.engine.session.model.ErrorId;
import io.dialob.session.engine.session.model.ItemId;
import io.dialob.session.engine.session.model.ItemState;
import io.dialob.session.engine.session.model.ValueSetId;
import org.immutables.value.Value;

import java.io.Serializable;
import java.util.function.BiPredicate;
import java.util.stream.Stream;

import static java.util.Objects.requireNonNull;

@Value.Enclosing
public class Triggers {

  public static final ActivePageUpdatedEvent ACTIVE_PAGE_UPDATED_EVENT = ImmutableActivePageUpdatedEvent.builder().build();
  public static final AvailableItemsUpdatedEvent AVAILABLE_ITEMS_UPDATED_EVENT = ImmutableAvailableItemsUpdatedEvent.builder().build();
  public static final AnyInvalidAnswersUpdatedEvent ANY_INVALID_ANSWERS_UPDATED_EVENT = ImmutableAnyInvalidAnswersUpdatedEvent.builder().build();

  static class TriggerBuilder<T> {

    private EventsProvider<T> eventsProvider;
    private Event event;

    public TriggerBuilder(@NonNull EventsProvider<T> eventsProvider) {
      this.eventsProvider = requireNonNull(eventsProvider);
    }

    public TriggerBuilder(Event event) {
      this.event = event;
    }

    public Trigger<T> when(@NonNull BiPredicate<T, T> predicate) {
      if (event != null) {
        return ImmutableStaticTrigger.<T>builder().when(predicate).addAllEvents(event).build();
      }
      return ImmutableDynamicTrigger.<T>builder().when(predicate).eventsProvider(eventsProvider).build();
    }
  }

  @FunctionalInterface
  public interface EventsProvider<T> extends Serializable {
    Stream<Event> createEvents(T originalState, T updatedState);
  }

  public static <T> TriggerBuilder<T> trigger(@NonNull EventsProvider<T> eventsProvider) {
    return new TriggerBuilder<>(requireNonNull(eventsProvider));
  }

  public static <T> TriggerBuilder<T> trigger(@NonNull Event event) {
    return new TriggerBuilder<>(requireNonNull(event));
  }

  public static TargetEvent onTarget(@NonNull ItemId targetId) {
    return stateChangedEvent(targetId);
  }

  public static ValueSetEvent valueSetUpdatedEvent(@NonNull ValueSetId valueSetId) {
    return ImmutableValueSetUpdatedEvent.of(valueSetId);
  }

  public static TargetEvent stateChangedEvent(@NonNull ItemId targetId) {
    return ImmutableTargetEvent.of(targetId);
  }

  public static SessionUpdatedEvent sessionLocaleUpdatedEvent() {
    return SessionLocaleUpdatedEvent.INSTANCE;
  }

  public static Event errorActivityUpdatedEvent(@NonNull ErrorId errorId) {
    return ImmutableErrorActiveUpdatedEvent.of(errorId);
  }

  public static Event activityUpdatedEvent(TargetEvent targetEvent) {
    return ImmutableActiveUpdatedEvent.of(targetEvent);
  }

  public static Event rowsCanBeAddedUpdatedEvent(TargetEvent targetEvent) {
    return ImmutableRowsCanBeAddedUpdatedEvent.of(targetEvent);
  }

  public static Event rowCanBeRemovedUpdatedEvent(TargetEvent targetEvent) {
    return ImmutableRowCanBeRemovedUpdatedEvent.of(targetEvent);
  }

  public static Event labelUpdatedEvent(TargetEvent targetEvent) {
    return ImmutableLabelUpdatedEvent.of(targetEvent);
  }

  public static Event descriptionUpdatedEvent(TargetEvent targetEvent) {
    return ImmutableDescriptionUpdatedEvent.of(targetEvent);
  }

  public static Event requiredUpdatedEvent(TargetEvent targetEvent) {
    return ImmutableRequiredUpdatedEvent.of(targetEvent);
  }

  public static Event answeredUpdatedEvent(TargetEvent targetEvent) {
    return ImmutableAnsweredUpdatedEvent.of(targetEvent);
  }

  public static Event validityUpdatedEvent(TargetEvent targetEvent) {
    return ImmutableValidUpdatedEvent.of(targetEvent);
  }

  public static Event itemsChangedEvent(TargetEvent targetEvent) {
    return ImmutableItemsChangedEvent.of(targetEvent);
  }

  @Value.Immutable
  interface RowGroupItemsInitEventsProvider extends EventsProvider<ItemState> {

    @Value.Parameter
    ItemId getPrototypeId();

    @Override
    default Stream<Event> createEvents(ItemState originalState, ItemState updatedState) {
      return originalState == null && updatedState == null ?
        Stream.of(ImmutableRowGroupItemsInitEvent.of(getPrototypeId(), getPrototypeId(), onTarget(getPrototypeId()))) :
        Stream.of(ImmutableRowGroupItemsInitEvent.of(updatedState.getId(), getPrototypeId(), onTarget(getPrototypeId())));
    }
  }


  public static EventsProvider<ItemState> rowGroupItemsInitEvent(ItemId prototypeId) {
    return ImmutableTriggers.RowGroupItemsInitEventsProvider.of(prototypeId);
  }

  public static Event disabledUpdatedEvent(TargetEvent targetEvent) {
    return ImmutableDisabledUpdatedEvent.of(targetEvent);
  }

  public static Event statusUpdatedEvent(TargetEvent targetEvent) {
    return ImmutableStatusUpdatedEvent.of(targetEvent);
  }

  @Value.Immutable
  interface GroupItemsUpdatedEventProvider extends EventsProvider<ItemState> {

    @Value.Parameter
    TargetEvent getTargetEvent();

    @Override
    default Stream<Event> createEvents(ItemState originalState, ItemState updatedState) {
      return  Stream.of(itemsChangedEvent(getTargetEvent()));
    }
  }

  public static EventsProvider<ItemState> groupItemsUpdatedEvent(final TargetEvent targetEvent) {
    return ImmutableTriggers.GroupItemsUpdatedEventProvider.of(targetEvent);
  }

  @Value.Immutable
  interface GroupItemsUpdatedEventsProvider extends EventsProvider<ItemState> {

    @Value.Parameter
    ItemId getPrototypeId();

    @Override
    default Stream<Event> createEvents(ItemState originalState, ItemState updatedState) {
      return originalState == null && updatedState == null ?
        Stream.of(itemsChangedEvent(onTarget(getPrototypeId()))) :
        Stream.of(itemsChangedEvent(onTarget(updatedState.getId())));
    }
  }

  public static EventsProvider<ItemState> groupItemsUpdatedEvent(final ItemId prototypeId) {
    return ImmutableTriggers.GroupItemsUpdatedEventsProvider.of(prototypeId);
  }

  public static Event activePageUpdatedEvent() {
    return ACTIVE_PAGE_UPDATED_EVENT;
  }

  public static Event availableItemsUpdatedEvent() {
    return AVAILABLE_ITEMS_UPDATED_EVENT;
  }

  public static Event anyInvalidAnswersUpdatedEvent() {
    return ANY_INVALID_ANSWERS_UPDATED_EVENT;
  }
}
