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
import io.dialob.session.engine.program.expr.arith.Constant;
import io.dialob.session.engine.program.expr.arith.NumberOperators;
import io.dialob.session.engine.program.expr.arith.Operators;
import io.dialob.session.engine.program.model.Expression;
import io.dialob.session.engine.session.command.event.Event;
import io.dialob.session.engine.session.command.event.ItemAddedEvent;
import io.dialob.session.engine.session.model.*;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.*;

import static io.dialob.session.engine.session.command.CommandFactory.ItemStatePredicates.GROUP_ITEMS_CHANGED;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class CommandFactoryTest {

  @Test
  void emptyItemsListDoNotTriggerChange() {
    ItemId id1 = new ItemRef("i1", null);
    ItemState itemState = ItemState.builder()
      .id(id1)
      .type("rowgroup")
      .status(ItemState.Status.NEW)
      .bits(ItemState.DISPLAY_ITEM_BIT | ItemState.ACTIVE_BIT | ItemState.ROWS_CAN_BE_ADDED_BIT)
      .build();

    ItemId id = new ItemRef("i1", null);
    ItemState itemState2 = ItemState.builder()
      .id(id)
      .type("rowgroup")
      .status(ItemState.Status.NEW)
      .bits(ItemState.DISPLAY_ITEM_BIT | ItemState.ACTIVE_BIT | ItemState.ROWS_CAN_BE_ADDED_BIT)
      .build();

    assertFalse(GROUP_ITEMS_CHANGED.test(itemState, itemState2));

    itemState = itemState.update().setItems(List.of()).get();
    assertFalse(GROUP_ITEMS_CHANGED.test(itemState, itemState2));

    itemState2 = itemState2.update().setItems(List.of()).get();
    assertFalse(GROUP_ITEMS_CHANGED.test(itemState, itemState2));

    itemState = itemState.update().setItems(List.of(new ItemRef("i1", null))).get();
    itemState2 = itemState.update().setItems(List.of(new ItemRef("i1", null))).get();
    assertFalse(GROUP_ITEMS_CHANGED.test(itemState, itemState2));

  }

  @Test
  void differenceOnItemsShouldTriggerChanges() {
    ItemId id1 = new ItemRef("i1", null);
    ItemState itemState = ItemState.builder()
      .id(id1)
      .type("rowgroup")
      .status(ItemState.Status.NEW)
      .bits(ItemState.DISPLAY_ITEM_BIT | ItemState.ACTIVE_BIT | ItemState.ROWS_CAN_BE_ADDED_BIT)
      .build();

    ItemId id = new ItemRef("i1", null);
    ItemState itemState2 = ItemState.builder()
      .id(id)
      .type("rowgroup")
      .status(ItemState.Status.NEW)
      .bits(ItemState.DISPLAY_ITEM_BIT | ItemState.ACTIVE_BIT | ItemState.ROWS_CAN_BE_ADDED_BIT)
      .build();
    itemState = itemState.update().setItems(List.of(new ItemRef("i1", null))).get();
    itemState2 = itemState2.update().setItems(List.of()).get();
    assertTrue(GROUP_ITEMS_CHANGED.test(itemState, itemState2));

    itemState = itemState.update().setItems(List.of(new ItemRef("i2", null))).get();
    itemState2 = itemState.update().setItems(List.of(new ItemRef("i1", null))).get();
    assertTrue(GROUP_ITEMS_CHANGED.test(itemState, itemState2));

    itemState = itemState.update().setItems(List.of()).get();
    itemState2 = itemState.update().setItems(List.of(new ItemRef("i1", null))).get();
    assertTrue(GROUP_ITEMS_CHANGED.test(itemState, itemState2));


    itemState = itemState.update().setItems(List.of(new ItemRef("i2", null))).get();
    itemState2 = itemState.update().setItems(Arrays.asList(new ItemRef("i2", null), new ItemRef("i1", null))).get();
    assertTrue(GROUP_ITEMS_CHANGED.test(itemState, itemState2));
  }

  @Test
  void shouldNotTriggerItself() {
    ItemId itemId = IdUtils.toId("q1");
    Expression expression =
      Operators.and(Operators.isActive(itemId), new NumberOperators().lt(Operators.var("q1", ValueType.INTEGER), Constant.builder().valueType(ValueType.INTEGER).value(0).build()));
    //;
    var updateValidationCommand = CommandFactory.updateValidationCommand(new ErrorId(itemId, "err"), expression);
    Set<EventMatcher> eventMatchers = updateValidationCommand.eventMatchers();
    List<Event> eventList = updateValidationCommand.triggers().stream().map(Trigger::allEvents).flatMap(List::stream).toList();
    Iterator<EventMatcher> i = eventMatchers.iterator();
    EventMatcher eventMatcher = i.next();
    assertFalse(eventMatcher.matches(eventList.getFirst()));
    assertFalse(eventMatcher.matches(eventList.get(1)));
    eventMatcher = i.next();
    assertFalse(eventMatcher.matches(eventList.getFirst()));
    assertFalse(eventMatcher.matches(eventList.get(1)));
  }

  @Test
  void shouldTriggerRowInstantiationWhenItemsChange() {
    SessionUpdateCommand command = CommandFactory.createRowGroupFromPrototypeCommand(IdUtils.toId("g1.*"));
    ItemId id1 = IdUtils.toId("g1");
    ItemState itemState1 = ItemState.builder()
      .id(id1)
      .type("rowgroup")
      .status(ItemState.Status.NEW)
      .bits(ItemState.DISPLAY_ITEM_BIT | ItemState.ACTIVE_BIT | ItemState.ROWS_CAN_BE_ADDED_BIT)
      .build();
    ItemId id = IdUtils.toId("g1.0");
    ItemState itemRow = ItemState.builder()
      .id(id)
      .type("group")
      .status(ItemState.Status.NEW)
      .bits(ItemState.DISPLAY_ITEM_BIT | ItemState.ACTIVE_BIT | ItemState.ROWS_CAN_BE_ADDED_BIT)
      .build();
    ItemState itemState2 = itemState1.update().setItems(List.of(IdUtils.toId("g1.0"))).get();

    ItemStates itemStates1 = new ItemStates.Builder()
      .putItemStates(itemState1.id(), itemState1)
      .build();

    ItemStates itemStates2 = new ItemStates.Builder()
      .putItemStates(itemRow.id(), itemRow)
      .putItemStates(itemState2.id(), itemState2)
      .build();

    List<Event> events = command.triggers().stream().flatMap(itemStatesTrigger -> itemStatesTrigger.apply(itemStates1, itemStates2)).toList();

    assertFalse(events.isEmpty());
    assertEquals(new ItemAddedEvent(IdUtils.toId("g1.0"), IdUtils.toId("g1.*")), events.getFirst());
  }

  @Test
  void testGroupItemsChange() {

    ItemState original = Mockito.mock(ItemState.class);
    when(original.items()).thenReturn(Collections.emptyList());
    ItemState updated = Mockito.mock(ItemState.class);
    when(updated.items()).thenReturn(Collections.emptyList());
    ItemState original2 = Mockito.mock(ItemState.class);
    when(original2.items()).thenReturn(List.of(IdUtils.toId("q1")));
    ItemState updated2 = Mockito.mock(ItemState.class);
    when(updated2.items()).thenReturn(List.of(IdUtils.toId("q1")));


    assertFalse(GROUP_ITEMS_CHANGED.test(null, null));
    assertTrue(GROUP_ITEMS_CHANGED.test(null, updated));
    assertTrue(GROUP_ITEMS_CHANGED.test(original, null));
    assertFalse(GROUP_ITEMS_CHANGED.test(original, updated));
    assertTrue(GROUP_ITEMS_CHANGED.test(original, updated2));
    assertFalse(GROUP_ITEMS_CHANGED.test(original2, updated2));
  }

}
