package io.dialob.session.engine.session.model;
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

import io.dialob.session.engine.session.protobuf.StateReader;
import io.dialob.session.engine.session.protobuf.StateWriter;
import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Mode;
import nl.jqno.equalsverifier.Warning;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ValueSetStateTest {

  @Test
  void shouldEqual() {
    EqualsVerifier.forClass(ValueSetState.class)
      .set(Mode.skipMockito())
      .suppress(Warning.NULL_FIELDS)
      .verify();
  }

  @Test
  void shouldNotCreateANewInstanceIfNothingChanges() {
    ValueSetState original = new ValueSetState(new ValueSetId("vs1"), List.of(
      new ValueSetState.Entry("k1", "v1", true),
      new ValueSetState.Entry("k2", "v2", false)
    ));
    assertSame(original, original.update()
      .get());
    assertNotSame(original, original.update()
      .setEntries(List.of(
        new ValueSetState.Entry("k3", "v1", true),
        new ValueSetState.Entry("k4", "v2", false)
      ))
      .get());
  }

  @Test
  void shouldMapNullToEmptyEntries() {
    ValueSetState original = new ValueSetState(new ValueSetId("vs1"), List.of(
      new ValueSetState.Entry("k1", "v1", true),
      new ValueSetState.Entry("k2", "v2", false)
    ));
    assertSame(original, original.update().get());
    assertEquals(List.of(), original.update()
      .setEntries(null)
      .get().entries());
  }

  @Test
  void shouldWriteAndReadValueSetState() throws IOException {
    ValueSetState original = new ValueSetState(new ValueSetId("vs1"), List.of(
      new ValueSetState.Entry("k1", "v1", true),
      new ValueSetState.Entry("k2", "v2", false)
    ));

    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    StateWriter writer = StateWriter.newInstance(bos);
    original.writeTo(writer);
    writer.flush();

    StateReader reader = StateReader.newInstance(bos.toByteArray());
    ValueSetState read = ValueSetState.readFrom(reader);

    assertEquals(original, read);
  }

  @Test
  void shouldWriteAndReadEmptyValueSetState() throws IOException {
    ValueSetState original = new ValueSetState(new ValueSetId("vs1"), List.of());

    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    StateWriter writer = StateWriter.newInstance(bos);
    original.writeTo(writer);
    writer.flush();

    StateReader reader = StateReader.newInstance(bos.toByteArray());
    ValueSetState read = ValueSetState.readFrom(reader);

    assertEquals(original, read);
  }

}
