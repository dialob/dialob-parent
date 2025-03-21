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

import com.google.protobuf.CodedInputStream;
import com.google.protobuf.CodedOutputStream;
import io.dialob.api.proto.Action;
import io.dialob.session.engine.program.EvalContext;
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
  void shouldClone() {
    ItemState itemState = new ItemState(IdUtils.toId("question1"), null, "text", null, true, null, null, null, null, null);
    ItemState itemState2 = new ItemState(itemState);
    assertNotSame(itemState, itemState2);
    assertEquals(itemState, itemState2);
    assertEquals(itemState.hashCode(), itemState2.hashCode());
  }

  @Test
  void shouldNotCreateNewIfUpdateHaveNotEffect() {
    ItemState itemState = new ItemState(IdUtils.toId("question1"), null, "text", null, true, null, null, null, null, null);
    EvalContext context = mock(EvalContext.class);
    assertSame(itemState,
      itemState.update()
        .setStatus(ItemState.Status.NEW)
        .get());
    verifyNoMoreInteractions(context);
  }


  @Test
  void shouldCreateNewIfUpdateHaveNotEffect() {
    ItemState itemState = new ItemState(IdUtils.toId("question1"), null, "text", null, true, null, null, null, null, null);
    EvalContext context = mock(EvalContext.class);
    ItemState itemState1 = itemState.update()
      .setStatus(ItemState.Status.OK)
      .get();
    assertNotSame(itemState,
      itemState1);
    assertEquals(ItemState.Status.OK, itemState1.getStatus());
    verifyNoMoreInteractions(context);
  }

  @Test
  void shouldSerializeAndDeserialize() throws IOException {
    var buffer = new ByteArrayOutputStream();
    CodedOutputStream outputStream = CodedOutputStream.newInstance(buffer);

    var itemState1 = new ItemState(IdUtils.toId("questionnaire"), null, "questionnaire", null, true, null, null, null, null, null);
    itemState1.writeTo(outputStream);
    var itemState2 = new ItemState(IdUtils.toId("questionnaire"), null, "questionnaire", null, true, null, null, null, null, null);
    itemState2 = itemState2.update().setAllowedActions(Set.of(Action.Type.ANSWER)).get();
    itemState2.writeTo(outputStream);
    var itemState3 = new ItemState(IdUtils.toId("group"), null, "group", null, true, null, null, null, null, null);
    itemState3 = itemState3.update().setItems(List.of(IdUtils.toId("q1"))).setClassNames(List.of("class1")).get();
    itemState3.writeTo(outputStream);

    outputStream.flush();

    var inputStream = CodedInputStream.newInstance(buffer.toByteArray());
    Assertions.assertEquals(itemState1, ItemState.readFrom(inputStream));
    Assertions.assertEquals(itemState2, ItemState.readFrom(inputStream));
    Assertions.assertEquals(itemState3, ItemState.readFrom(inputStream));

  }

}
