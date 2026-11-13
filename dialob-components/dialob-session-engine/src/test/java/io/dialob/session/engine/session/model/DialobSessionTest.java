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
import io.dialob.security.tenant.ResysSecurityConstants;
import io.dialob.session.engine.program.EvalContext;
import io.dialob.session.engine.session.command.Command;
import io.dialob.session.engine.session.command.SessionUpdateCommand;
import io.dialob.session.engine.session.command.Trigger;
import io.dialob.session.engine.session.protobuf.StateReader;
import io.dialob.session.engine.session.protobuf.StateWriter;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class DialobSessionTest {

  static DialobSession dialobSessionOf(
    List<ItemState> itemStates)
  {
    var itemStatesMap = new HashMap<ItemId, ItemState>();
    itemStates.forEach(item -> itemStatesMap.put(item.id(), item));
    return new DialobSession(
      Objects.requireNonNullElseGet("tenant", ResysSecurityConstants.DEFAULT_TENANT::id),
      "1",
      "2",
      null,
      null,
      null,
      "fi",
      new ItemStates.Builder()
        .itemStates(itemStatesMap)
        .build(),
      ItemStates.EMPTY
    );
  }
  public static final ItemState ITEM_STATE;

  static {
    ItemId id = IdUtils.toId("q1");
    ITEM_STATE = ItemState.builder()
      .id(id)
      .prototypeId(null)
      .type("text")
      .view(null)
      .status(ItemState.Status.NEW)
      .bits(ItemState.DISPLAY_ITEM_BIT | ItemState.ACTIVE_BIT | ItemState.ROWS_CAN_BE_ADDED_BIT)
      .valueSetId(null)
      .answer(null)
      .value(null)
      .defaultValue(null)
      .activePage(null)
      .build();
  }

  @Test
  void noopCommandShouldNotTriggerAnyChanges() {
    DialobSession session = dialobSessionOf(List.of());
    EvalContext context = Mockito.mock();
    when(context.mutableItemStates()).thenReturn(new MutableItemStates(session.getItemStates()));
    Command<?> command = new SessionUpdateCommand() {

      @Override
      public List<Trigger<ItemStates>> triggers() {
        return List.of();
      }

      @NonNull
      @Override
      public ItemStates update(@NonNull EvalContext context, @NonNull ItemStates target) {
        return target;
      }
    };
    session = session.applyCommand(context, command);

    verify(context, times(3)).mutableItemStates();
    verifyNoMoreInteractions(context);
  }

  @Test
  void newItemsShouldTriggerUpdate() {
    DialobSession session = dialobSessionOf(List.of());
    EvalContext context = Mockito.mock();
    when(context.mutableItemStates()).thenReturn(new MutableItemStates(session.getItemStates()));
    Command<?> command = new SessionUpdateCommand() {

      @Override
      public List<Trigger<ItemStates>> triggers() {
        return List.of();
      }

      @NonNull
      @Override
      public ItemStates update(@NonNull EvalContext context, @NonNull ItemStates target) {
        return new ItemStates.Builder().from(target).putItemStates(ITEM_STATE.id(), ITEM_STATE).build();
      }
    };
    session = session.applyCommand(context, command);

    verify(context, times(4)).mutableItemStates();
    verify(context).registerUpdate(any(ItemState.class), isNull());
    verifyNoMoreInteractions(context);
  }

  @Test
  void removedItemsShouldTriggerUpdate() {
    DialobSession session = dialobSessionOf(List.of(ITEM_STATE));
    EvalContext context = Mockito.mock();
    when(context.mutableItemStates()).thenReturn(new MutableItemStates(session.getItemStates()));
    Command<?> command = new SessionUpdateCommand() {

      @Override
      public List<Trigger<ItemStates>> triggers() {
        return List.of();
      }

      @NonNull
      @Override
      public ItemStates update(@NonNull EvalContext context, @NonNull ItemStates target) {
        return new ItemStates.Builder().build();
      }
    };
    session = session.applyCommand(context, command);

    verify(context, times(4)).mutableItemStates();
    verify(context).registerUpdate(isNull(), any(ItemState.class));
    verifyNoMoreInteractions(context);
  }

  @Test
  void itemUpdateShouldTriggerUpdate() {
    DialobSession session = dialobSessionOf(List.of(ITEM_STATE));
    EvalContext context = Mockito.mock();
    when(context.mutableItemStates()).thenReturn(new MutableItemStates(session.getItemStates()));
    Command<?> command = new SessionUpdateCommand() {

      @Override
      public List<Trigger<ItemStates>> triggers() {
        return List.of();
      }

      @NonNull
      @Override
      public ItemStates update(@NonNull EvalContext context, @NonNull ItemStates target) {
        return new ItemStates.Builder().putItemStates(ITEM_STATE.id(), ItemState.builder()
          .id(ITEM_STATE.id())
          .prototypeId(null)
          .type("text")
          .view(null)
          .status(ItemState.Status.NEW)
          .bits(ItemState.DISPLAY_ITEM_BIT | ItemState.ACTIVE_BIT | ItemState.ROWS_CAN_BE_ADDED_BIT)
          .valueSetId(null)
          .answer("hello")
          .value(null)
          .defaultValue(null)
          .activePage(null)
          .build()).build();
      }
    };
    session = session.applyCommand(context, command);

    verify(context, times(4)).mutableItemStates();
    verify(context).registerUpdate(any(ItemState.class), any(ItemState.class));
    verifyNoMoreInteractions(context);
  }

  @Test
  void shouldWriteAndReadDialobSession() throws IOException {
    Instant now = Instant.now();
    DialobSession session = new DialobSession(
      "tenant1",
      "session1",
      "rev1",
      now,
      now.plusSeconds(10),
      now.minusSeconds(10),
      "en",
      ItemStates.EMPTY,
      ItemStates.EMPTY
    );

    StateWriter writer = Mockito.mock(StateWriter.class);
    session.writeTo(writer);

    verify(writer).writeString("tenant1");
    verify(writer).writeNullableString("session1");
    verify(writer).writeNullableString("rev1");
    verify(writer).writeString("en");
    verify(writer).writeDate(now);
    verify(writer).writeNullableDate(now.plusSeconds(10));
    verify(writer).writeNullableDate(now.minusSeconds(10));
    // ItemStates.writeTo is called twice (itemStates and prototypes)
    // Since ItemStates.EMPTY is used, it writes 0 three times for each call
    verify(writer, times(6)).writeInt(0);

    StateReader reader = Mockito.mock(StateReader.class);
    when(reader.readString()).thenReturn("tenant1", "en");
    when(reader.readNullableString()).thenReturn("session1", "rev1");
    when(reader.readDate()).thenReturn(now);
    when(reader.readNullableDate()).thenReturn(now.plusSeconds(10), now.minusSeconds(10));
    when(reader.readInt()).thenReturn(0); // For ItemStates

    DialobSession readSession = DialobSession.readFrom(reader);

    assertEquals(session.getTenantId(), readSession.getTenantId());
    assertEquals(session.getId(), readSession.getId());
    assertEquals(session.getRevision(), readSession.getRevision());
    assertEquals(session.getLanguage(), readSession.getLanguage());
    assertEquals(session.getLastUpdate(), readSession.getLastUpdate());
    assertEquals(session.getCompleted(), readSession.getCompleted());
    assertEquals(session.getOpened(), readSession.getOpened());
    assertEquals(session.getItemStates(), readSession.getItemStates());
    assertEquals(session.prototypes(), readSession.prototypes());
  }

}
