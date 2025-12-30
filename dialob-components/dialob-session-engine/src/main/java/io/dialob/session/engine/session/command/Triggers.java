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
import java.util.List;
import java.util.function.BiPredicate;
import java.util.stream.Stream;

import static java.util.Objects.requireNonNull;

@Value.Enclosing
public class Triggers {

  public static final Event ACTIVE_PAGE_UPDATED_EVENT = ActivePageUpdatedEvent.instance();
  public static final Event AVAILABLE_ITEMS_UPDATED_EVENT = AvailableItemsUpdatedEvent.instance();
  public static final Event ANY_INVALID_ANSWERS_UPDATED_EVENT = AnyInvalidAnswersUpdatedEvent.instance();

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
        return new StaticTrigger<>(predicate, List.of(event));
      }
      return new DynamicTrigger<>(predicate, eventsProvider);
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
    return new ValueSetUpdatedEvent(valueSetId);
  }

  public static TargetEvent stateChangedEvent(@NonNull ItemId targetId) {
    return TargetEvent.of(targetId);
  }

  public static SessionUpdatedEvent sessionLocaleUpdatedEvent() {
    return SessionLocaleUpdatedEvent.instance();
  }

  public static Event errorActivityUpdatedEvent(@NonNull ErrorId errorId) {
    return new ErrorActiveUpdatedEvent(errorId);
  }

  public static Event activityUpdatedEvent(TargetEvent targetEvent) {
    return new ActiveUpdatedEvent(targetEvent);
  }

  public static Event rowsCanBeAddedUpdatedEvent(TargetEvent targetEvent) {
    return new RowsCanBeAddedUpdatedEvent(targetEvent);
  }

  public static Event rowCanBeRemovedUpdatedEvent(TargetEvent targetEvent) {
    return new RowCanBeRemovedUpdatedEvent(targetEvent);
  }

  public static Event labelUpdatedEvent(TargetEvent targetEvent) {
    return new LabelUpdatedEvent(targetEvent);
  }

  public static Event descriptionUpdatedEvent(TargetEvent targetEvent) {
    return new DescriptionUpdatedEvent(targetEvent);
  }

  public static Event requiredUpdatedEvent(TargetEvent targetEvent) {
    return new RequiredUpdatedEvent(targetEvent);
  }

  public static Event answeredUpdatedEvent(TargetEvent targetEvent) {
    return new AnsweredUpdatedEvent(targetEvent);
  }

  public static Event validityUpdatedEvent(TargetEvent targetEvent) {
    return new ValidUpdatedEvent(targetEvent);
  }

  public static Event itemsChangedEvent(TargetEvent targetEvent) {
    return ItemsChangedEvent.of(targetEvent);
  }

  record RowGroupItemsInitEventsProvider(
    ItemId prototypeId
  ) implements EventsProvider<ItemState> {

    @Override
    public Stream<Event> createEvents(ItemState originalState, ItemState updatedState) {
      return originalState == null && updatedState == null ?
        Stream.of(new RowGroupItemsInitEvent(prototypeId(), prototypeId(), onTarget(prototypeId()))) :
        Stream.of(new RowGroupItemsInitEvent(updatedState.getId(), prototypeId(), onTarget(prototypeId())));
    }
  }


  public static EventsProvider<ItemState> rowGroupItemsInitEvent(ItemId prototypeId) {
    return new RowGroupItemsInitEventsProvider(prototypeId);
  }

  public static Event disabledUpdatedEvent(TargetEvent targetEvent) {
    return new DisabledUpdatedEvent(targetEvent);
  }

  public static Event statusUpdatedEvent(TargetEvent targetEvent) {
    return new StatusUpdatedEvent(targetEvent);
  }

  record GroupItemsUpdatedEventProvider(
    TargetEvent targetEvent
  ) implements EventsProvider<ItemState> {


    @Override
    public Stream<Event> createEvents(ItemState originalState, ItemState updatedState) {
      return Stream.of(itemsChangedEvent(targetEvent()));
    }
  }

  public static EventsProvider<ItemState> groupItemsUpdatedEvent(final TargetEvent targetEvent) {
    return new Triggers.GroupItemsUpdatedEventProvider(targetEvent);
  }

  record GroupItemsUpdatedEventsProvider(
    ItemId prototypeId
  ) implements EventsProvider<ItemState> {

    @Override
    public Stream<Event> createEvents(ItemState originalState, ItemState updatedState) {
      return originalState == null && updatedState == null ?
        Stream.of(itemsChangedEvent(onTarget(prototypeId()))) :
        Stream.of(itemsChangedEvent(onTarget(updatedState.getId())));
    }
  }

  public static EventsProvider<ItemState> groupItemsUpdatedEvent(final ItemId prototypeId) {
    return new Triggers.GroupItemsUpdatedEventsProvider(prototypeId);
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
