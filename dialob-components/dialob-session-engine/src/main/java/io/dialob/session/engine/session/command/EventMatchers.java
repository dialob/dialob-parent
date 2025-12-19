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
import io.dialob.session.engine.session.model.IdUtils;
import io.dialob.session.engine.session.model.ItemId;
import io.dialob.session.engine.session.model.ValueSetId;

public final class EventMatchers {

  private static final EventMatchers.ActivePageEventMatcher ACTIVE_PAGE_EVENT_MATCHER = EventMatchers.ActivePageEventMatcher.instance();

  private static final EventMatchers.AvailableItemsEventMatcher AVAILABLE_ITEMS_EVENT_MATCHER = EventMatchers.AvailableItemsEventMatcher.instance();

  private static final EventMatchers.AnyErrorEventMatcher ANY_ERROR_EVENT_MATCHER = EventMatchers.AnyErrorEventMatcher.instance();

  private EventMatchers() {
  }

  public static ErrorEventMatcher anyError() {
    return ANY_ERROR_EVENT_MATCHER;
  }

  public static ErrorEventMatcher error(@NonNull ErrorId errorId) {
    return new EventMatchers.ErrorIdEventMatcher(errorId);
  }

  public static ErrorEventMatcher targetError(@NonNull ItemId itemId) {
    return new EventMatchers.TargetErrorEventMatcher(itemId);
  }

  public static EventMatcher whenActiveUpdated(@NonNull ItemId itemId) {
    return new EventMatchers.IsActiveTargetEventMatcher(itemId);
  }

  public static EventMatcher whenValueUpdated(@NonNull ItemId itemId) {
    return new EventMatchers.TargetIdEventMatcher(itemId);
  }

  public static EventMatcher whenRequiredUpdated(@NonNull ItemId itemId) {
    return new EventMatchers.IsRequiredTargetEventMatcher(itemId);
  }

  public static EventMatcher whenDisabledUpdatedEvent(@NonNull ItemId itemId) {
    return new EventMatchers.IsDisabledTargetEventMatcher(itemId);
  }

  public static EventMatcher whenRowsCanBeAddedUpdatedEvent(@NonNull ItemId itemId) {
    return EventMatchers.RowsCanBeAddedUpdatedEventMatcher.instance();
  }

  public static EventMatcher whenRowCanBeRemovedUpdatedEvent(@NonNull ItemId itemId) {
    return EventMatchers.RowCanBeRemovedUpdatedEventMatcher.instance();
  }

  public static EventMatcher whenValidUpdated(@NonNull ItemId itemId) {
    return new EventMatchers.IsValidTargetEventMatcher(itemId);
  }

  public static EventMatcher whenAnyInvalidAnswersUpdated() {
    return EventMatchers.AnyInvalidAnswersUpdatedEventMatcher.instance();
  }

  public static EventMatcher whenActivePageUpdated() {
    return ACTIVE_PAGE_EVENT_MATCHER;
  }

  public static EventMatcher whenAvailableItemsUpdated() {
    return AVAILABLE_ITEMS_EVENT_MATCHER;
  }

  public static EventMatcher whenAnsweredUpdated(@NonNull ItemId itemId) {
    return EventMatchers.AnsweredTargetEventMatcher.of(itemId);
  }

  public static EventMatcher whenItemsChanged(@NonNull ItemId groupId) {
    return new EventMatchers.ItemsChangedEventMatcher(groupId);
  }

  public static EventMatcher whenItemAdded(@NonNull ItemId prototypeId) {
    return new EventMatchers.ItemAddedEventMatcher(prototypeId);
  }
  public static EventMatcher whenItemRemoved(@NonNull ItemId prototypeId) {
    return new EventMatchers.ItemRemovedEventMatcher(prototypeId);
  }
  public static EventMatcher whenRowGroupItemsInit(@NonNull ItemId prototypeId) {
    return new EventMatchers.RowGroupItemsInitEventMatcher(prototypeId);
  }

  public static EventMatcher errorActivity(@NonNull ErrorEventMatcher errorEventMatcher) {
    return new EventMatchers.ErrorActivityEventMatcher(errorEventMatcher);
  }

  public static EventMatcher whenValueSetUpdated(@NonNull ValueSetId valueSetId) {
    return new EventMatchers.ValueSetUpdatedEventMatcher(valueSetId);
  }

  public static EventMatcher whenSessionLocaleUpdated() {
    return event -> event instanceof SessionLocaleUpdatedEvent;
  }

  public record TargetIdEventMatcher(
      ItemId targetId
    ) implements EventMatcher {

    @Override
    public boolean matches(Event event) {
      if (event instanceof TargetEvent(ItemId id)) {
        return IdUtils.matches(targetId(), id);
      }
      return false;
    }
  }

  interface QuestionnaireEventMatcher extends EventMatcher {
  }

  interface AttributeEventMatcher<E extends AttributeEvent> extends EventMatcher {

    ItemId targetMatcher();

    default ItemId getTargetMatcher() {
      return targetMatcher();
    }

    boolean eventTypeMatches(Event event);

    @Override
    default boolean matches(Event event) {
      if (eventTypeMatches(event)) {
        E attributeEvent = (E) event;
        TargetEvent targetEvent = attributeEvent.target();
        return IdUtils.matches(getTargetMatcher(), targetEvent.targetId());
      }
      return false;
    }
  }

  record IsActiveTargetEventMatcher(
    ItemId targetMatcher
  ) implements AttributeEventMatcher<ActiveUpdatedEvent> {
    @Override
    public boolean eventTypeMatches(Event event) {
      return event instanceof ActiveUpdatedEvent;
    }
  }

  record IsRequiredTargetEventMatcher(
    ItemId targetMatcher
  ) implements AttributeEventMatcher<ActiveUpdatedEvent> {
    @Override
    public boolean eventTypeMatches(Event event) {
      return event instanceof RequiredUpdatedEvent;
    }
  }

  record IsDisabledTargetEventMatcher(
    ItemId targetMatcher
  ) implements AttributeEventMatcher<DisabledUpdatedEvent> {
    @Override
    public boolean eventTypeMatches(Event event) {
      return event instanceof DisabledUpdatedEvent;
    }
  }

  record IsValidTargetEventMatcher(
    ItemId targetMatcher
  ) implements AttributeEventMatcher<ValidUpdatedEvent> {
    @Override
    public boolean eventTypeMatches(Event event) {
      return event instanceof ValidUpdatedEvent;
    }
  }

  record AnyInvalidAnswersUpdatedEventMatcher() implements EventMatcher {

    private static final AnyInvalidAnswersUpdatedEventMatcher INSTANCE = new AnyInvalidAnswersUpdatedEventMatcher();

    public static EventMatcher instance() {
      return INSTANCE;
    }

    @Override
    public boolean matches(Event event) {
      return event instanceof AnyInvalidAnswersUpdatedEvent;
    }
  }

  record ItemAddedEventMatcher(
    ItemId prototypeId
  ) implements EventMatcher {

    @Override
    public boolean matches(Event event) {
      return event instanceof ItemAddedEvent iae && iae.getPrototypeId().equals(prototypeId());
    }
  }

  record ItemRemovedEventMatcher(
    ItemId prototypeId
  ) implements EventMatcher {

    @Override
    public boolean matches(Event event) {
      return event instanceof ItemRemovedEvent ire && IdUtils.matches(prototypeId(), ire.getRemoveItemId());
    }
  }

  record RowGroupItemsInitEventMatcher(
    ItemId prototypeId
  ) implements EventMatcher {

    @Override
    public boolean matches(Event event) {
      return event instanceof RowGroupItemsInitEvent rgiie && rgiie.getPrototypeId().equals(prototypeId());
    }
  }

  record RowsCanBeAddedUpdatedEventMatcher() implements EventMatcher {

    private static final RowsCanBeAddedUpdatedEventMatcher INSTANCE = new RowsCanBeAddedUpdatedEventMatcher();

    public static EventMatcher instance() {
      return INSTANCE;
    }

    @Override
    public boolean matches(Event event) {
      return event instanceof RowsCanBeAddedUpdatedEvent;
    }
  }

  record RowCanBeRemovedUpdatedEventMatcher() implements EventMatcher {
    private static final RowCanBeRemovedUpdatedEventMatcher INSTANCE = new RowCanBeRemovedUpdatedEventMatcher();

    public static EventMatcher instance() {
      return INSTANCE;
    }


    @Override
    public boolean matches(Event event) {
      return event instanceof RowCanBeRemovedUpdatedEvent;
    }
  }

  record ItemsChangedEventMatcher(
    ItemId targetMatcher
  ) implements AttributeEventMatcher<ItemsChangedEvent> {
    @Override
    public boolean eventTypeMatches(Event event) {
      return event instanceof ItemsChangedEvent;
    }
  }

  record AnsweredTargetEventMatcher(
    ItemId targetMatcher
  ) implements AttributeEventMatcher<AnsweredUpdatedEvent> {

    public static AnsweredTargetEventMatcher of(ItemId targetMatcher) {
      return new AnsweredTargetEventMatcher(targetMatcher);
    }

    @Override
    public ItemId getTargetMatcher() {
      return targetMatcher;
    }

    @Override
    public boolean eventTypeMatches(Event event) {
      return event instanceof AnsweredUpdatedEvent;
    }
  }

  record ActivePageEventMatcher() implements QuestionnaireEventMatcher {

    private static final ActivePageEventMatcher INSTANCE = new ActivePageEventMatcher();

    private static ActivePageEventMatcher instance() {
      return INSTANCE;
    }

    @Override
    public boolean matches(Event event) {
      return event instanceof ActivePageUpdatedEvent;
    }
  }

  record AvailableItemsEventMatcher() implements QuestionnaireEventMatcher {

    private static final AvailableItemsEventMatcher INSTANCE = new AvailableItemsEventMatcher();

    private static AvailableItemsEventMatcher instance() {
      return INSTANCE;
    }

    @Override
    public boolean matches(Event event) {
      return event instanceof AvailableItemsUpdatedEvent;
    }
  }

  interface ErrorEventMatcher extends EventMatcher {
  }

  record AnyErrorEventMatcher() implements ErrorEventMatcher {
    private static final AnyErrorEventMatcher INSTANCE = new AnyErrorEventMatcher();

    private static AnyErrorEventMatcher instance() {
      return INSTANCE;
    }

    @Override
    public boolean matches(Event event) {
      return event instanceof ErrorEvent;
    }
  }

  record TargetErrorEventMatcher(
    ItemId targetId
  ) implements ErrorEventMatcher {

    @Override
    public boolean matches(Event event) {
      if (event instanceof ErrorEvent errorEvent) {
        return targetId().equals(errorEvent.getErrorId().itemId());
      }
      return false;
    }
  }

  record ErrorIdEventMatcher(
    ErrorId errorId
  ) implements ErrorEventMatcher {

    @Override
    public boolean matches(Event event) {
      if (event instanceof ErrorEvent targetEvent) {
        return errorId().equals(targetEvent.getErrorId());
      }
      return false;
    }
  }

  record ErrorActivityEventMatcher(
    ErrorEventMatcher errorEventMatcher
  ) implements ErrorEventMatcher {

    @Override
    public boolean matches(Event event) {
      if (event instanceof ErrorEvent) {
        ErrorActiveUpdatedEvent targetEvent = (ErrorActiveUpdatedEvent) event;
        return errorEventMatcher().matches(targetEvent);
      }
      return false;
    }

  }

  interface ValueSetEventMatcher extends EventMatcher {

    ValueSetId valueSetId();

  }

  record ValueSetUpdatedEventMatcher(
    ValueSetId valueSetId
  ) implements ValueSetEventMatcher {
    @Override
    public boolean matches(Event event) {
      if (event instanceof ValueSetUpdatedEvent targetEvent) {
        return valueSetId().equals(targetEvent.getValueSetId());
      }
      return false;
    }
  }

}
