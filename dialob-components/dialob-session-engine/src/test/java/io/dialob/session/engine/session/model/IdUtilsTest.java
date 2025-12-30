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
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class IdUtilsTest {

  @Test
  void testToString() {
    assertNull(IdUtils.toString((ItemId) null));
    assertNull(IdUtils.toString(null));
    assertEquals("var1", IdUtils.toString(IdUtils.toId("var1")));
    assertEquals("1", IdUtils.toString(new ItemIndex(1, null)));
    assertEquals("10", IdUtils.toString(new ItemIndex(10, null)));
    assertEquals("10.var1", IdUtils.toString(new ItemRef("var1", new ItemIndex(10, null))));
    assertEquals("a.10.var1", IdUtils.toString(new ItemRef("var1", new ItemIndex(10, IdUtils.toId("a")))));

    assertEquals("var1.10.a", IdUtils.toString(new ItemRef("a", new ItemIndex(10, IdUtils.toId("var1")))));
    assertEquals("var1.10", IdUtils.toString(new ItemIndex(10, IdUtils.toId("var1"))));
    assertEquals("var1.*", IdUtils.toString(new ItemIdPartial(IdUtils.toId("var1"))));
  }


  @Test
  void testToId() {
    assertNull(IdUtils.toIdNullable(null));
    assertEquals(IdUtils.toId("var1"), IdUtils.toId("var1"));
    assertEquals(new ItemIndex(1, null), IdUtils.toId("1"));
    assertEquals(new ItemIndex(10, null), IdUtils.toId("10"));
    assertEquals(new ItemRef("var1", new ItemIndex(10, null)), IdUtils.toId("10.var1"));
    assertEquals(new ItemRef("var1", new ItemIndex(10, IdUtils.toId("a"))), IdUtils.toId("a.10.var1"));

    assertEquals(new ItemRef("a", new ItemIndex(10, IdUtils.toId("var1"))), IdUtils.toId("var1.10.a"));
    assertEquals(new ItemIndex(10, IdUtils.toId("var1")), IdUtils.toId("var1.10"));
  }


  @Test
  void testMatching() {
    assertTrue(IdUtils.matches(IdUtils.toId("g1"), IdUtils.toId("g1")));
    assertTrue(IdUtils.matches(IdUtils.toId("g1.*"), IdUtils.toId("g1.*")));
    assertTrue(IdUtils.matches(IdUtils.toId("g1.*"), IdUtils.toId("g1.0")));
    assertTrue(IdUtils.matches(IdUtils.toId("g1.0"), IdUtils.toId("g1.*")));
    assertTrue(IdUtils.matches(IdUtils.toId("g1.*.q"), IdUtils.toId("g1.*.q")));
    assertTrue(IdUtils.matches(IdUtils.toId("g1.*.q"), IdUtils.toId("g1.0.q")));
    assertTrue(IdUtils.matches(IdUtils.toId("g1.0.q"), IdUtils.toId("g1.*.q")));
    assertFalse(IdUtils.matches(IdUtils.toId("g2.0.q"), IdUtils.toId("g1.*.q")));
    assertFalse(IdUtils.matches(IdUtils.toId("g1.0.q1"), IdUtils.toId("g1.*.q")));
    assertFalse(IdUtils.matches(IdUtils.toId("g1"), IdUtils.toId("g1.*")));
    assertFalse(IdUtils.matches(IdUtils.toId("g1.*"), IdUtils.toId("g1")));
    assertFalse(IdUtils.matches(IdUtils.toId("q"), IdUtils.toId("*.q")));
    assertFalse(IdUtils.matches(IdUtils.toId("*.q"), IdUtils.toId("q")));
    assertFalse(IdUtils.matches(IdUtils.toId("g1"), IdUtils.toId("g1.0")));
    assertFalse(IdUtils.matches(IdUtils.toId("g1.0"), IdUtils.toId("g1")));
    assertFalse(IdUtils.matches(IdUtils.toId("g1"), IdUtils.toId("g1.0.q")));
    assertFalse(IdUtils.matches(IdUtils.toId("g1.0.q"), IdUtils.toId("g1")));
    assertFalse(IdUtils.matches(IdUtils.toId("g2"), IdUtils.toId("g1")));
  }

  @Test
  void readId() throws IOException {
    ByteArrayOutputStream bo = new ByteArrayOutputStream();
    var output = StateWriter.newInstance(bo);
    ItemId id2 = IdUtils.toId("l1");
    output.writeNullableId(id2);
    ItemId id1 = IdUtils.toId("l1.*.p2");
    output.writeNullableId(id1);
    ItemId id = IdUtils.toId("l1.2");
    output.writeNullableId(id);
    output.flush();

    var stream = StateReader.newInstance(new ByteArrayInputStream(bo.toByteArray()));
    Assertions.assertEquals(IdUtils.toId("l1"), stream.readNullableId());
    Assertions.assertEquals(IdUtils.toId("l1.*.p2"), stream.readNullableId());
    Assertions.assertEquals(IdUtils.toId("l1.2"), stream.readNullableId());
  }

  @Test
  void errorId() {
    assertEquals("item:error", IdUtils.toString(new ErrorId(IdUtils.toId("item"), "error")));
  }

  @Test
  void invalidId() {
    Assertions.assertThrows(IllegalArgumentException.class, () -> IdUtils.toId(""));
  }
}
