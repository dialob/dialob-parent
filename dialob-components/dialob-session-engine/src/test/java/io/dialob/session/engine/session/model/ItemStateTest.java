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

import io.dialob.api.proto.Action;
import io.dialob.session.engine.program.EvalContext;
import io.dialob.session.engine.session.protobuf.StateReader;
import io.dialob.session.engine.session.protobuf.StateWriter;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoMoreInteractions;

class ItemStateTest {


  @Test
  void shouldNotCreateNewIfUpdateHaveNotEffect() {
    ItemId id = IdUtils.toId("question1");
    ItemState itemState = ItemState.builder()
      .id(id)
      .type("text")
      .status(ItemState.Status.NEW)
      .bits(ItemState.DISPLAY_ITEM_BIT | ItemState.ACTIVE_BIT | ItemState.ROWS_CAN_BE_ADDED_BIT)
      .build();
    EvalContext context = mock(EvalContext.class);
    assertSame(itemState,
      itemState.update()
        .setStatus(ItemState.Status.NEW)
        .get());
    verifyNoMoreInteractions(context);
  }


  @Test
  void shouldCreateNewIfUpdateHaveNotEffect() {
    ItemId id = IdUtils.toId("question1");
    ItemState itemState = ItemState.builder()
      .id(id)
      .type("text")
      .status(ItemState.Status.NEW)
      .bits(ItemState.DISPLAY_ITEM_BIT | ItemState.ACTIVE_BIT | ItemState.ROWS_CAN_BE_ADDED_BIT)
      .build();
    EvalContext context = mock(EvalContext.class);
    ItemState itemState1 = itemState.update()
      .setStatus(ItemState.Status.OK)
      .get();
    assertNotSame(itemState,
      itemState1);
    assertEquals(ItemState.Status.OK, itemState1.status());
    verifyNoMoreInteractions(context);
  }

  @Test
  void shouldSerializeAndDeserialize() throws IOException {
    var buffer = new ByteArrayOutputStream();
    var outputStream = StateWriter.newInstance(buffer);

    ItemId id2 = IdUtils.toId("questionnaire");
    var itemState1 = ItemState.builder()
      .id(id2)
      .type("questionnaire")
      .status(ItemState.Status.NEW)
      .bits(ItemState.DISPLAY_ITEM_BIT | ItemState.ACTIVE_BIT | ItemState.ROWS_CAN_BE_ADDED_BIT)
      .build();
    itemState1.writeTo(outputStream);
    ItemId id1 = IdUtils.toId("questionnaire");
    var itemState2 = ItemState.builder()
      .id(id1)
      .type("questionnaire")
      .status(ItemState.Status.NEW)
      .bits(ItemState.DISPLAY_ITEM_BIT | ItemState.ACTIVE_BIT | ItemState.ROWS_CAN_BE_ADDED_BIT)
      .build();
    itemState2 = itemState2.update().setAllowedActions(Set.of(Action.Type.ANSWER)).get();
    itemState2.writeTo(outputStream);
    ItemId id = IdUtils.toId("group");
    var itemState3 = ItemState.builder()
      .id(id)
      .type("group")
      .status(ItemState.Status.NEW)
      .bits(ItemState.DISPLAY_ITEM_BIT | ItemState.ACTIVE_BIT | ItemState.ROWS_CAN_BE_ADDED_BIT)
      .build();
    itemState3 = itemState3.update().setItems(List.of(IdUtils.toId("q1"))).setClassNames(List.of("class1")).get();
    itemState3.writeTo(outputStream);

    outputStream.flush();

    var inputStream = StateReader.newInstance(buffer.toByteArray());
    Assertions.assertEquals(itemState1, ItemState.readFrom(inputStream));
    Assertions.assertEquals(itemState2, ItemState.readFrom(inputStream));
    Assertions.assertEquals(itemState3, ItemState.readFrom(inputStream));
  }

  @Test
  void shouldEqualsAndHashCode() {
    EqualsVerifier.forClass(ItemState.class)
      .verify();
  }

}
