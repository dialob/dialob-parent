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

import io.dialob.session.engine.program.EvalContext;
import io.dialob.session.engine.session.command.event.ItemsChangedEvent;
import io.dialob.session.engine.session.command.event.TargetEvent;
import io.dialob.session.engine.session.model.IdUtils;
import io.dialob.session.engine.session.model.ItemState;
import io.dialob.session.engine.session.model.ItemStates;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class CreateRowGroupFromPrototypeCommandTest {

  @Test
  void shouldNotMakeAnyChangesIfPrototypeDoNotExists() {
    EvalContext context = Mockito.mock(EvalContext.class);
    ItemStates states = Mockito.mock(ItemStates.class);
    ItemState groupState1 = Mockito.mock(ItemState.class);
    ItemState groupState2 = Mockito.mock(ItemState.class);
    when(context.getOriginalItemState(IdUtils.toId("g1"))).thenReturn(Optional.of(groupState1));
    when(context.findPrototype(IdUtils.toId("g1.*"))).thenReturn(Optional.empty());
    when(states.itemStates()).thenReturn(Map.of(IdUtils.toId("g1"), groupState2));
    var command = CommandFactory.createRowGroupFromPrototypeCommand(IdUtils.toId("g1.*"));
    ItemStates newStates = command.update(context, states);
    Assertions.assertSame(states, newStates);

    verify(states, times(1)).itemStates();
    verify(context).getOriginalItemState(IdUtils.toId("g1"));
    verify(context).findPrototype(IdUtils.toId("g1.*"));
    verifyNoMoreInteractions(context, states);
  }


  @Test
  void shouldAddANewItemStateOnStateWhenPrototypeExists() {
    EvalContext context = Mockito.mock(EvalContext.class);
    ItemStates states = Mockito.mock(ItemStates.class);
    ItemState groupState1 = Mockito.mock(ItemState.class);
    ItemState groupState2 = Mockito.mock(ItemState.class);
    when(states.errorStates()).thenReturn(Collections.emptyMap());
    when(states.itemStates()).thenReturn(Map.of(IdUtils.toId("g1"), groupState2));
    when(states.valueSetStates()).thenReturn(Collections.emptyMap());
    when(context.findPrototype(IdUtils.toId("g1.*"))).thenReturn(Optional.of(new ItemState(IdUtils.toId("g1.*"), null, "text", null, true, null, null, null, null, null)));
    when(context.getOriginalItemState(IdUtils.toId("g1"))).thenReturn(Optional.of(groupState1));
    when(groupState1.getItems()).thenReturn(List.of());
    when(groupState2.getItems()).thenReturn(List.of(IdUtils.toId("g1.0")));
    when(groupState1.getId()).thenReturn(IdUtils.toId("g1"));
    when(groupState2.getId()).thenReturn(IdUtils.toId("g1"));

    var command = CommandFactory.createRowGroupFromPrototypeCommand(IdUtils.toId("g1.*"));
    ItemStates newStates = command.update(context, states);
    assertNotSame(states, newStates);
    assertTrue(newStates.itemStates().containsKey(IdUtils.toId("g1.0")));

    verify(states).errorStates();
    verify(states, times(3)).itemStates();
    verify(states).valueSetStates();
    verify(context).findPrototype(IdUtils.toId("g1.*"));
    verify(context).getOriginalItemState(IdUtils.toId("g1"));
    verifyNoMoreInteractions(context, states);
  }


  @Test
  @Disabled
  void eventMatcherShouldReactOnItemsChangedEvent() {
    var command = CommandFactory.createRowGroupFromPrototypeCommand(IdUtils.toId("g1.*.q1"));
    Assertions.assertTrue(
      command.eventMatchers().stream()
        .anyMatch(eventMatcher -> eventMatcher.matches(ItemsChangedEvent.of(TargetEvent.of(IdUtils.toId("g1.0"))))));
  }

  @Test
  void eventMatcherShouldNotReactOnItemsChangedEventOfDifferentGroup() {
    var command = CommandFactory.createRowGroupFromPrototypeCommand(IdUtils.toId("g1.*.q1"));
    Assertions.assertFalse(
      command.eventMatchers().stream()
        .anyMatch(eventMatcher -> eventMatcher.matches(ItemsChangedEvent.of(TargetEvent.of(IdUtils.toId("g2"))))));
  }


}
