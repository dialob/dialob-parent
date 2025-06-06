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
package io.dialob.session.engine.session.model;

import edu.umd.cs.findbugs.annotations.NonNull;
import io.dialob.session.engine.program.EvalContext;
import io.dialob.session.engine.session.command.Command;
import io.dialob.session.engine.session.command.SessionUpdateCommand;
import io.dialob.session.engine.session.command.Trigger;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

class DialobSessionTest {

  public static final ItemState ITEM_STATE = new ItemState(
    IdUtils.toId("q1"),
    null, "text", null,
    true, null, null, null,

    null, null);

  @Test
  void noopCommandShouldNotTriggerAnyChanges() {
    List<ItemState> items = List.of();
    List<ItemState> prototypes = List.of();
    List<ValueSetState> valueSets = List.of();
    List<ErrorState> errors = List.of();
    DialobSession session = new DialobSession("tenant", "1", "2", "fi", items, prototypes, valueSets, errors, Collections.emptyList(), null, null, null);
    EvalContext context = Mockito.mock(EvalContext.class);
    Command<?> command = new SessionUpdateCommand() {

      @Override
      public List<Trigger<ItemStates>> getTriggers() {
        return List.of();
      }

      @NonNull
      @Override
      public ItemStates update(@NonNull EvalContext context, @NonNull ItemStates target) {
        return target;
      }
    };
    session.applyUpdate(context, command);

    verifyNoMoreInteractions(context);
  }

  @Test
  void newItemsShouldTriggerUpdate() {
    List<ItemState> items = List.of();
    List<ItemState> prototypes = List.of();
    List<ValueSetState> valueSets = List.of();
    List<ErrorState> errors = List.of();
    DialobSession session = new DialobSession("tenant", "1", "2", "fi", items, prototypes, valueSets, errors, Collections.emptyList(), null, null, null);
    EvalContext context = Mockito.mock();
    Command<?> command = new SessionUpdateCommand() {

      @Override
      public List<Trigger<ItemStates>> getTriggers() {
        return List.of();
      }

      @NonNull
      @Override
      public ItemStates update(@NonNull EvalContext context, @NonNull ItemStates target) {
        return ImmutableItemStates.builder().from(target).putItemStates(ITEM_STATE.getId(), ITEM_STATE).build();
      }
    };
    session.applyUpdate(context, command);

    verify(context).registerUpdate(any(ItemState.class), isNull());
    verifyNoMoreInteractions(context);
  }

  @Test
  void removedItemsShouldTriggerUpdate() {
    List<ItemState> items = List.of(ITEM_STATE);
    List<ItemState> prototypes = List.of();
    List<ValueSetState> valueSets = List.of();
    List<ErrorState> errors = List.of();
    DialobSession session = new DialobSession("tenant", "1", "2", "fi", items, prototypes, valueSets, errors, Collections.emptyList(), null, null, null);
    EvalContext context = Mockito.mock();
    Command<?> command = new SessionUpdateCommand() {

      @Override
      public List<Trigger<ItemStates>> getTriggers() {
        return List.of();
      }

      @NonNull
      @Override
      public ItemStates update(@NonNull EvalContext context, @NonNull ItemStates target) {
        return ImmutableItemStates.builder().build();
      }
    };
    session.applyUpdate(context, command);

    verify(context).registerUpdate(isNull(), any(ItemState.class));
    verifyNoMoreInteractions(context);
  }

  @Test
  void itemUpdateShouldTriggerUpdate() {
    List<ItemState> items = List.of(ITEM_STATE);
    List<ItemState> prototypes = List.of();
    List<ValueSetState> valueSets = List.of();
    List<ErrorState> errors = List.of();
    DialobSession session = new DialobSession("tenant", "1", "2", "fi", items, prototypes, valueSets, errors, Collections.emptyList(), null, null, null);
    EvalContext context = Mockito.mock();
    Command<?> command = new SessionUpdateCommand() {

      @Override
      public List<Trigger<ItemStates>> getTriggers() {
        return List.of();
      }

      @NonNull
      @Override
      public ItemStates update(@NonNull EvalContext context, @NonNull ItemStates target) {
        return ImmutableItemStates.builder().putItemStates(ITEM_STATE.getId(), new ItemState(
          ITEM_STATE.getId(),
          null, "text", null,
          true, null, "hello", null,

          null, null)).build();
      }
    };
    session.applyUpdate(context, command);

    verify(context).registerUpdate(any(ItemState.class), any(ItemState.class));
    verifyNoMoreInteractions(context);
  }


}
