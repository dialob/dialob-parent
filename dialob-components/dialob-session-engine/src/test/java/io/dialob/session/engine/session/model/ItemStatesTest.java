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

import io.dialob.session.engine.session.protobuf.StateReader;
import io.dialob.session.engine.session.protobuf.StateWriter;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class ItemStatesTest {

  @Test
  void shouldWriteAndReadEmptyItemStates() throws IOException {
    ItemStates itemStates = ItemStates.EMPTY;
    StateWriter writer = Mockito.mock(StateWriter.class);

    itemStates.writeTo(writer);

    verify(writer, times(3)).writeInt(0);
    verifyNoMoreInteractions(writer);
  }

  @Test
  void shouldReadEmptyItemStates() throws IOException {
    StateReader reader = Mockito.mock(StateReader.class);
    when(reader.readInt()).thenReturn(0);

    ItemStates itemStates = ItemStates.readFrom(reader);

    assertEquals(0, itemStates.itemStates().size());
    assertEquals(0, itemStates.valueSetStates().size());
    assertEquals(0, itemStates.errorStates().size());
    verify(reader, times(3)).readInt();
  }

  @Test
  void shouldWriteItemStates() throws IOException {
    ItemId itemId = IdUtils.toId("item1");
    ItemState itemState = Mockito.mock(ItemState.class);
    when(itemState.id()).thenReturn(itemId);

    ValueSetId valueSetId = new ValueSetId("vs1");
    ValueSetState valueSetState = Mockito.mock(ValueSetState.class);
    when(valueSetState.id()).thenReturn(valueSetId);

    ErrorId errorId = new ErrorId(itemId, "err1");
    ErrorState errorState = Mockito.mock(ErrorState.class);
    when(errorState.id()).thenReturn(errorId);

    ItemStates itemStates = new ItemStates(
      Map.of(itemId, itemState),
      Map.of(valueSetId, valueSetState),
      Map.of(errorId, errorState)
    );

    StateWriter writer = Mockito.mock(StateWriter.class);

    itemStates.writeTo(writer);

    verify(writer, times(3)).writeInt(1);
    verify(itemState).writeTo(writer);
    verify(valueSetState).writeTo(writer);
    verify(errorState).writeTo(writer);
  }
}
