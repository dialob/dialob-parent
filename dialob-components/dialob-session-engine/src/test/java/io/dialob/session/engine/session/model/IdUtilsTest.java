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
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class IdUtilsTest {

  @Test
  void testToString() {
    assertNull(IdUtils.toString((ItemId) null));
    assertNull(IdUtils.toString(null));
    assertEquals("var1", IdUtils.toString(IdUtils.toId("var1")));
    assertEquals("1", IdUtils.toString(ImmutableItemIndex.of(1,Optional.empty())));
    assertEquals("10", IdUtils.toString(ImmutableItemIndex.of(10,Optional.empty())));
    assertEquals("10.var1", IdUtils.toString(ImmutableItemRef.of("var1", Optional.of(ImmutableItemIndex.of(10,Optional.empty())))));
    assertEquals("a.10.var1", IdUtils.toString(ImmutableItemRef.of("var1", Optional.of(ImmutableItemIndex.of(10,Optional.of(IdUtils.toId("a")))))));

    assertEquals("var1.10.a", IdUtils.toString(ImmutableItemRef.of("a", Optional.of(ImmutableItemIndex.of(10,Optional.of(IdUtils.toId("var1")))))));
    assertEquals("var1.10", IdUtils.toString(ImmutableItemIndex.of(10,Optional.of(IdUtils.toId("var1")))));
    assertEquals("var1.*", IdUtils.toString(ImmutableItemIdPartial.of(Optional.of(IdUtils.toId("var1")))));
  }


  @Test
  void testToId() {
    assertNull(IdUtils.toIdNullable(null));
    assertEquals(IdUtils.toId("var1"), IdUtils.toId("var1"));
    assertEquals(ImmutableItemIndex.of(1,Optional.empty()), IdUtils.toId("1"));
    assertEquals(ImmutableItemIndex.of(10,Optional.empty()), IdUtils.toId("10"));
    assertEquals(ImmutableItemRef.of("var1", Optional.of(ImmutableItemIndex.of(10,Optional.empty()))), IdUtils.toId("10.var1"));
    assertEquals(ImmutableItemRef.of("var1", Optional.of(ImmutableItemIndex.of(10,Optional.of(IdUtils.toId("a"))))), IdUtils.toId("a.10.var1"));

    assertEquals(ImmutableItemRef.of("a", Optional.of(ImmutableItemIndex.of(10,Optional.of(IdUtils.toId("var1"))))), IdUtils.toId("var1.10.a"));
    assertEquals(ImmutableItemIndex.of(10,Optional.of(IdUtils.toId("var1"))), IdUtils.toId("var1.10"));
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
    CodedOutputStream output = CodedOutputStream.newInstance(bo);
    IdUtils.writeIdTo(IdUtils.toId("l1"), output);
    IdUtils.writeIdTo(IdUtils.toId("l1.*.p2"), output);
    IdUtils.writeIdTo(IdUtils.toId("l1.2"), output);
    output.flush();

    CodedInputStream stream = CodedInputStream.newInstance(new ByteArrayInputStream(bo.toByteArray()));
    Assertions.assertEquals(IdUtils.toId("l1"), IdUtils.readIdFrom(stream));
    Assertions.assertEquals(IdUtils.toId("l1.*.p2"), IdUtils.readIdFrom(stream));
    Assertions.assertEquals(IdUtils.toId("l1.2"), IdUtils.readIdFrom(stream));
  }
}
