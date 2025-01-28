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

import io.dialob.rule.parser.api.ValueType;
import io.dialob.session.engine.program.expr.arith.ImmutableConstant;
import io.dialob.session.engine.program.expr.arith.NumberOperators;
import io.dialob.session.engine.program.expr.arith.Operators;
import io.dialob.session.engine.program.model.Expression;
import io.dialob.session.engine.session.command.event.Event;
import io.dialob.session.engine.session.command.event.ImmutableItemAddedEvent;
import io.dialob.session.engine.session.model.*;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.*;
import java.util.stream.Collectors;

import static io.dialob.session.engine.session.command.CommandFactory.ItemStatePredicates.GROUP_ITEMS_CHANGED;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class CommandFactoryTest {

  @Test
  void emptyItemsListDoNotTriggerChange() {
    ItemState itemState = new ItemState(
      ImmutableItemRef.of("i1", Optional.empty()),
      null, "rowgroup",
      null,
      true,
      null,
      null,
      null,
      null, null);

    ItemState itemState2 = new ItemState(
      ImmutableItemRef.of("i1", Optional.empty()),
      null, "rowgroup",
      null,
      true,
      null,
      null,
      null,
      null, null);

    assertFalse(GROUP_ITEMS_CHANGED.test(itemState, itemState2));

    itemState = itemState.update().setItems(Arrays.asList()).get();
    assertFalse(GROUP_ITEMS_CHANGED.test(itemState, itemState2));

    itemState2 = itemState2.update().setItems(Arrays.asList()).get();
    assertFalse(GROUP_ITEMS_CHANGED.test(itemState, itemState2));

    itemState = itemState.update().setItems(Arrays.asList(ImmutableItemRef.of("i1", Optional.empty()))).get();
    itemState2 = itemState.update().setItems(Arrays.asList(ImmutableItemRef.of("i1", Optional.empty()))).get();
    assertFalse(GROUP_ITEMS_CHANGED.test(itemState, itemState2));

  }

  @Test
  void differenceOnItemsShouldTriggerChanges() {
    ItemState itemState = new ItemState(
      ImmutableItemRef.of("i1", Optional.empty()),
      null, "rowgroup",
      null,
      true,
      null,
      null,
      null,
      null, null);

    ItemState itemState2 = new ItemState(
      ImmutableItemRef.of("i1", Optional.empty()),
      null, "rowgroup",
      null,
      true,
      null,
      null,
      null,
      null, null);
    itemState = itemState.update().setItems(Arrays.asList(ImmutableItemRef.of("i1", Optional.empty()))).get();
    itemState2 = itemState2.update().setItems(Arrays.asList()).get();
    assertTrue(GROUP_ITEMS_CHANGED.test(itemState, itemState2));

    itemState = itemState.update().setItems(Arrays.asList(ImmutableItemRef.of("i2", Optional.empty()))).get();
    itemState2 = itemState.update().setItems(Arrays.asList(ImmutableItemRef.of("i1", Optional.empty()))).get();
    assertTrue(GROUP_ITEMS_CHANGED.test(itemState, itemState2));

    itemState = itemState.update().setItems(Arrays.asList()).get();
    itemState2 = itemState.update().setItems(Arrays.asList(ImmutableItemRef.of("i1", Optional.empty()))).get();
    assertTrue(GROUP_ITEMS_CHANGED.test(itemState, itemState2));


    itemState = itemState.update().setItems(Arrays.asList(ImmutableItemRef.of("i2", Optional.empty()))).get();
    itemState2 = itemState.update().setItems(Arrays.asList(ImmutableItemRef.of("i2", Optional.empty()), ImmutableItemRef.of("i1", Optional.empty()))).get();
    assertTrue(GROUP_ITEMS_CHANGED.test(itemState, itemState2));
  }

  @Test
  void shouldNotTriggerItself() {
    ItemId itemId = IdUtils.toId("q1");
    Expression expression =
      Operators.and(Operators.isActive(itemId), new NumberOperators().lt(Operators.var("q1", ValueType.INTEGER), ImmutableConstant.builder().valueType(ValueType.INTEGER).value(0).build()));
    //;
    UpdateValidationCommand updateValidationCommand = CommandFactory.updateValidationCommand(ImmutableErrorId.of(itemId, "err"), expression);
    Set<EventMatcher> eventMatchers = updateValidationCommand.getEventMatchers();
    List<Event> eventList = updateValidationCommand.getTriggers().stream().map(Trigger::getAllEvents).flatMap(List::stream).collect(Collectors.toList());
    Iterator<EventMatcher> i = eventMatchers.iterator();
    EventMatcher eventMatcher = i.next();
    assertFalse(eventMatcher.matches(eventList.get(0)));
    assertFalse(eventMatcher.matches(eventList.get(1)));
    eventMatcher = i.next();
    assertFalse(eventMatcher.matches(eventList.get(0)));
    assertFalse(eventMatcher.matches(eventList.get(1)));
  }

  @Test
  void shouldTriggerRowInstantiationWhenItemsChange() {
    SessionUpdateCommand command = CommandFactory.createRowGroupFromPrototypeCommand(IdUtils.toId("g1.*"));
    ItemState itemState1 = new ItemState(IdUtils.toId("g1"), null, "rowgroup", null, true, null, null, null, null, null);
    ItemState itemRow = new ItemState(IdUtils.toId("g1.0"), null, "group", null, true, null, null, null, null, null);
    ItemState itemState2 = itemState1.update().setItems(Arrays.asList(IdUtils.toId("g1.0"))).get();

    ItemStates itemStates1 = ImmutableItemStates.builder()
      .putItemStates(itemState1.getId(), itemState1)
      .build();

    ItemStates itemStates2 = ImmutableItemStates.builder()
      .putItemStates(itemRow.getId(), itemRow)
      .putItemStates(itemState2.getId(), itemState2)
      .build();

    List<Event> events = command.getTriggers().stream().flatMap(itemStatesTrigger -> itemStatesTrigger.apply(itemStates1, itemStates2)).collect(Collectors.toList());

    assertFalse(events.isEmpty());
    assertEquals(ImmutableItemAddedEvent.of(IdUtils.toId("g1.0"), IdUtils.toId("g1.*")), events.get(0));
  }

  @Test
  void testGroupItemsChange() {

    ItemState original = Mockito.mock(ItemState.class);
    when(original.getItems()).thenReturn(Collections.emptyList());
    ItemState updated = Mockito.mock(ItemState.class);
    when(updated.getItems()).thenReturn(Collections.emptyList());
    ItemState original2 = Mockito.mock(ItemState.class);
    when(original2.getItems()).thenReturn(Arrays.asList(IdUtils.toId("q1")));
    ItemState updated2 = Mockito.mock(ItemState.class);
    when(updated2.getItems()).thenReturn(Arrays.asList(IdUtils.toId("q1")));


    assertFalse(GROUP_ITEMS_CHANGED.test(null, null));
    assertTrue(GROUP_ITEMS_CHANGED.test(null, updated));
    assertTrue(GROUP_ITEMS_CHANGED.test(original, null));
    assertFalse(GROUP_ITEMS_CHANGED.test(original, updated));
    assertTrue(GROUP_ITEMS_CHANGED.test(original, updated2));
    assertFalse(GROUP_ITEMS_CHANGED.test(original2, updated2));
  }

}
