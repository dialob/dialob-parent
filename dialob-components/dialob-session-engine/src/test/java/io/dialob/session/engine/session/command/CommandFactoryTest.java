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

import java.util.*;

import static io.dialob.session.engine.session.command.CommandFactory.ErrorStateMatcher.ERROR_ACTIVITY_CHANGED;
import static io.dialob.session.engine.session.command.CommandFactory.ItemStatePredicates.*;
import static io.dialob.session.engine.session.command.CommandFactory.ValueStatePredicates.VALUE_SET_STATE_CHANGED;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
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
  void testItemStatesChange() {
    ItemState original = mock();
    ItemState updated = mock();

    var predicates = ITEM_STATE_CHANGED;
    assertFalse(predicates.test(null, null));
    assertTrue(predicates.test(null, updated));
    assertFalse(predicates.test(original, null));
    assertTrue(predicates.test(original, updated));
    assertFalse(predicates.test(original, original));
  }

  @Test
  void testValueSetStatesChange() {
    ValueSetState original = mock();
    ValueSetState updated = mock();

    var predicates = VALUE_SET_STATE_CHANGED;
    assertFalse(predicates.test(null, null));
    assertTrue(predicates.test(null, updated));
    assertFalse(predicates.test(original, null));
    assertTrue(predicates.test(original, updated));
    assertFalse(predicates.test(original, original));
  }

  @Test
  void testErrorStatesChange() {
    ErrorState originalTrue = mock();
    when(originalTrue.isActive()).thenReturn(true);
    ErrorState originalFalse = mock();
    when(originalFalse.isActive()).thenReturn(false);

    ErrorState updatedTrue = mock();
    when(updatedTrue.isActive()).thenReturn(true);
    ErrorState updatedFalse = mock();
    when(updatedFalse.isActive()).thenReturn(false);

    var predicates = ERROR_ACTIVITY_CHANGED;
    assertFalse(predicates.test(null, null));
    assertTrue(predicates.test(null, updatedTrue));
    assertTrue(predicates.test(null, updatedFalse));
    assertFalse(predicates.test(originalTrue, originalTrue));
    assertFalse(predicates.test(originalTrue, updatedTrue));
    assertFalse(predicates.test(originalFalse, updatedFalse));
    assertTrue(predicates.test(originalTrue, updatedFalse));
    assertTrue(predicates.test(originalFalse, updatedTrue));
    assertTrue(predicates.test(originalTrue, null));
    assertTrue(predicates.test(originalFalse, null));
  }

  @Test
  void testGroupItemsChange() {

    ItemState original = mock();
    when(original.items()).thenReturn(Collections.emptyList());
    ItemState updated = mock();
    when(updated.items()).thenReturn(Collections.emptyList());
    ItemState original2 = mock();
    when(original2.items()).thenReturn(List.of(IdUtils.toId("q1")));
    ItemState updated2 = mock();
    when(updated2.items()).thenReturn(List.of(IdUtils.toId("q1")));


    var predicates = GROUP_ITEMS_CHANGED;
    assertFalse(predicates.test(null, null));
    assertTrue(predicates.test(null, updated));
    assertTrue(predicates.test(original, null));
    assertFalse(predicates.test(original, updated));
    assertTrue(predicates.test(original, updated2));
    assertFalse(predicates.test(original2, updated2));
  }

  @Test
  void testRowsCanBeAddedChanged() {

    ItemState originalTrue = mock();
    when(originalTrue.isRowsCanBeAdded()).thenReturn(true);
    ItemState originalFalse = mock();
    when(originalFalse.isRowsCanBeAdded()).thenReturn(false);

    ItemState updatedTrue = mock();
    when(updatedTrue.isRowsCanBeAdded()).thenReturn(true);
    ItemState updatedFalse = mock();
    when(updatedFalse.isRowsCanBeAdded()).thenReturn(false);


    var predicates = ROWS_CAN_BE_ADDED_CHANGED;
    assertFalse(predicates.test(null, null));
    assertTrue(predicates.test(null, updatedTrue));
    assertTrue(predicates.test(null, updatedFalse));
    assertFalse(predicates.test(originalTrue, updatedTrue));
    assertFalse(predicates.test(originalFalse, updatedFalse));
    assertTrue(predicates.test(originalTrue, updatedFalse));
    assertTrue(predicates.test(originalFalse, updatedTrue));
    assertTrue(predicates.test(originalTrue, null));
    assertTrue(predicates.test(originalFalse, null));

  }

  @Test
  void testRowsCanBeRemovedChanged() {
    ItemState originalTrue = mock();
    when(originalTrue.isRowCanBeRemoved()).thenReturn(true);
    ItemState originalFalse = mock();
    when(originalFalse.isRowCanBeRemoved()).thenReturn(false);

    ItemState updatedTrue = mock();
    when(updatedTrue.isRowCanBeRemoved()).thenReturn(true);
    ItemState updatedFalse = mock();
    when(updatedFalse.isRowCanBeRemoved()).thenReturn(false);

    var predicates = ROWS_CAN_BE_REMOVED_CHANGED;

    assertFalse(predicates.test(null, null));
    assertTrue(predicates.test(null, updatedTrue));
    assertTrue(predicates.test(null, updatedFalse));
    assertFalse(predicates.test(originalTrue, updatedTrue));
    assertFalse(predicates.test(originalFalse, updatedFalse));
    assertTrue(predicates.test(originalTrue, updatedFalse));
    assertTrue(predicates.test(originalFalse, updatedTrue));
    assertTrue(predicates.test(originalTrue, null));
    assertTrue(predicates.test(originalFalse, null));
  }

  @Test
  void testItemLabelChanged() {

    ItemState original = mock();
    when(original.label()).thenReturn("original");

    ItemState updated = mock();
    when(updated.label()).thenReturn("updated");
    ItemState updatedSame = mock();
    when(updated.label()).thenReturn("original");

    var pred = ITEM_LABEL_CHANGED;

    assertFalse(pred.test(null, null));
    assertTrue(pred.test(null, updated));
    assertFalse(pred.test(original, original));
    assertFalse(pred.test(original, updated));
    assertTrue(pred.test(original, updatedSame));
    assertTrue(pred.test(original, null));
  }
  @Test
  void testItemDescriptionChanged() {

    ItemState original = mock();
    when(original.description()).thenReturn("original");

    ItemState updated = mock();
    when(updated.description()).thenReturn("updated");
    ItemState updatedSame = mock();
    when(updated.description()).thenReturn("original");

    var pred = ITEM_DESCRIPTION_CHANGED;

    assertFalse(pred.test(null, null));
    assertTrue(pred.test(null, updated));
    assertFalse(pred.test(original, original));
    assertFalse(pred.test(original, updated));
    assertTrue(pred.test(original, updatedSame));
    assertTrue(pred.test(original, null));
  }


  @Test
  void testIsRequiredChanged() {
    ItemState originalTrue = mock();
    when(originalTrue.isRequired()).thenReturn(true);
    ItemState originalFalse = mock();
    when(originalFalse.isRequired()).thenReturn(false);

    ItemState updatedTrue = mock();
    when(updatedTrue.isRequired()).thenReturn(true);
    ItemState updatedFalse = mock();
    when(updatedFalse.isRequired()).thenReturn(false);

    var predicates = ITEM_REQUIRED_CHANGED;

    assertFalse(predicates.test(null, null));
    assertTrue(predicates.test(null, updatedTrue));
    assertTrue(predicates.test(null, updatedFalse));
    assertFalse(predicates.test(originalTrue, originalTrue));
    assertFalse(predicates.test(originalTrue, updatedTrue));
    assertFalse(predicates.test(originalFalse, updatedFalse));
    assertTrue(predicates.test(originalTrue, updatedFalse));
    assertTrue(predicates.test(originalFalse, updatedTrue));
    assertTrue(predicates.test(originalTrue, null));
    assertTrue(predicates.test(originalFalse, null));
  }

  @Test
  void testItemValidityChanged() {
    ItemState originalTrue = mock();
    when(originalTrue.isInvalid()).thenReturn(true);
    ItemState originalFalse = mock();
    when(originalFalse.isInvalid()).thenReturn(false);

    ItemState updatedTrue = mock();
    when(updatedTrue.isInvalid()).thenReturn(true);
    ItemState updatedFalse = mock();
    when(updatedFalse.isInvalid()).thenReturn(false);

    var predicates = ITEM_INVALIDITY_CHANGED;

    assertFalse(predicates.test(null, null));
    assertTrue(predicates.test(null, updatedTrue));
    assertTrue(predicates.test(null, updatedFalse));
    assertFalse(predicates.test(originalTrue, originalTrue));
    assertFalse(predicates.test(originalTrue, updatedTrue));
    assertFalse(predicates.test(originalFalse, updatedFalse));
    assertTrue(predicates.test(originalTrue, updatedFalse));
    assertTrue(predicates.test(originalFalse, updatedTrue));
    assertTrue(predicates.test(originalTrue, null));
    assertTrue(predicates.test(originalFalse, null));
  }

  @Test
  void testInvalidAnswersChanged() {
    ItemState originalTrue = mock();
    when(originalTrue.isInvalidAnswers()).thenReturn(true);
    ItemState originalFalse = mock();
    when(originalFalse.isInvalidAnswers()).thenReturn(false);

    ItemState updatedTrue = mock();
    when(updatedTrue.isInvalidAnswers()).thenReturn(true);
    ItemState updatedFalse = mock();
    when(updatedFalse.isInvalidAnswers()).thenReturn(false);

    var predicates = ITEM_INVALID_ANSWERS_CHANGED;

    assertFalse(predicates.test(null, null));
    assertTrue(predicates.test(null, updatedTrue));
    assertTrue(predicates.test(null, updatedFalse));
    assertFalse(predicates.test(originalTrue, originalTrue));
    assertFalse(predicates.test(originalTrue, updatedTrue));
    assertFalse(predicates.test(originalFalse, updatedFalse));
    assertTrue(predicates.test(originalTrue, updatedFalse));
    assertTrue(predicates.test(originalFalse, updatedTrue));
    assertTrue(predicates.test(originalTrue, null));
    assertTrue(predicates.test(originalFalse, null));
  }

  @Test
  void testAnsweredStateChanged() {
    ItemState originalTrue = mock();
    when(originalTrue.isAnswered()).thenReturn(true);
    ItemState originalFalse = mock();
    when(originalFalse.isAnswered()).thenReturn(false);

    ItemState updatedTrue = mock();
    when(updatedTrue.isAnswered()).thenReturn(true);
    ItemState updatedFalse = mock();
    when(updatedFalse.isAnswered()).thenReturn(false);

    var predicates = ITEM_ANSWERED_STATE_CHANGED;

    assertFalse(predicates.test(null, null));
    assertTrue(predicates.test(null, updatedTrue));
    assertTrue(predicates.test(null, updatedFalse));
    assertFalse(predicates.test(originalTrue, originalTrue));
    assertFalse(predicates.test(originalTrue, updatedTrue));
    assertFalse(predicates.test(originalFalse, updatedFalse));
    assertTrue(predicates.test(originalTrue, updatedFalse));
    assertTrue(predicates.test(originalFalse, updatedTrue));
    assertTrue(predicates.test(originalTrue, null));
    assertTrue(predicates.test(originalFalse, null));
  }

  @Test
  void testItemStatusChanged() {
    ItemState originalNew = mock();
    when(originalNew.status()).thenReturn(ItemState.Status.NEW);
    ItemState originalError = mock();
    when(originalError.status()).thenReturn(ItemState.Status.ERROR);

    ItemState updatedNew = mock();
    when(updatedNew.status()).thenReturn(ItemState.Status.NEW);
    ItemState updatedError = mock();
    when(updatedError.status()).thenReturn(ItemState.Status.ERROR);

    var predicates = ITEM_STATUS_CHANGED;

    assertFalse(predicates.test(null, null));
    assertTrue(predicates.test(null, originalNew));
    assertTrue(predicates.test(null, originalError));
    assertFalse(predicates.test(originalNew, originalNew));
    assertFalse(predicates.test(originalNew, updatedNew));
    assertFalse(predicates.test(originalError, updatedError));
    assertTrue(predicates.test(originalNew, updatedError));
    assertTrue(predicates.test(originalError, updatedNew));
    assertTrue(predicates.test(originalNew, null));
    assertTrue(predicates.test(originalError, null));
  }

}
