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
package io.dialob.integration.api;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class NodeIdTest {

  @Test
  void shouldCreateNodeIdWithDefaultConstructor() {
    NodeId nodeId = new NodeId();

    assertNotNull(nodeId.id());
    assertDoesNotThrow(() -> UUID.fromString(nodeId.id()));
  }

  @Test
  void shouldCreateNodeIdWithSpecificId() {
    NodeId nodeId = new NodeId("test-node-123");

    assertEquals("test-node-123", nodeId.id());
  }

  @Test
  void shouldCreateNodeIdWithUUID() {
    String uuid = UUID.randomUUID().toString();
    NodeId nodeId = new NodeId(uuid);

    assertEquals(uuid, nodeId.id());
  }

  @Test
  void shouldRejectNullId() {
    assertThrows(NullPointerException.class, () -> new NodeId(null));
  }

  @Test
  void shouldHaveNullPointerExceptionMessageWhenIdIsNull() {
    NullPointerException exception = assertThrows(
      NullPointerException.class,
      () -> new NodeId(null)
    );

    assertEquals("id must not be null", exception.getMessage());
  }

  @Test
  void shouldGenerateUniqueIdsWithDefaultConstructor() {
    NodeId nodeId1 = new NodeId();
    NodeId nodeId2 = new NodeId();

    assertNotEquals(nodeId1.id(), nodeId2.id());
  }

  @Test
  void shouldBeEqualForSameId() {
    NodeId nodeId1 = new NodeId("same-id");
    NodeId nodeId2 = new NodeId("same-id");

    assertEquals(nodeId1, nodeId2);
    assertEquals(nodeId1.hashCode(), nodeId2.hashCode());
  }

  @Test
  void shouldNotBeEqualForDifferentIds() {
    NodeId nodeId1 = new NodeId("id-1");
    NodeId nodeId2 = new NodeId("id-2");

    assertNotEquals(nodeId1, nodeId2);
  }

  @Test
  void shouldNotBeEqualToNull() {
    NodeId nodeId = new NodeId("test-id");

    assertNotEquals(null, nodeId);
  }

  @Test
  void shouldNotBeEqualToDifferentType() {
    NodeId nodeId = new NodeId("test-id");

    assertNotEquals("test-id", nodeId);
  }

  @Test
  void shouldBeEqualToItself() {
    NodeId nodeId = new NodeId("test-id");

    assertEquals(nodeId, nodeId);
  }

  @Test
  void shouldHaveConsistentHashCode() {
    NodeId nodeId = new NodeId("test-id");

    int hashCode1 = nodeId.hashCode();
    int hashCode2 = nodeId.hashCode();

    assertEquals(hashCode1, hashCode2);
  }

  @Test
  void shouldHaveToStringContainingId() {
    NodeId nodeId = new NodeId("my-node-id");

    String toString = nodeId.toString();

    assertNotNull(toString);
    assertTrue(toString.contains("my-node-id"));
  }

  @Test
  void shouldBeSerializable() {
    NodeId nodeId = new NodeId("test-id");

    assertInstanceOf(java.io.Serializable.class, nodeId);
  }

  @Test
  void shouldCreateNodeIdWithEmptyString() {
    // Empty string is valid, only null is rejected
    NodeId nodeId = new NodeId("");

    assertEquals("", nodeId.id());
  }

  @Test
  void shouldCreateNodeIdWithWhitespace() {
    // Whitespace is valid, only null is rejected
    NodeId nodeId = new NodeId("  ");

    assertEquals("  ", nodeId.id());
  }

  @Test
  void shouldCreateNodeIdWithSpecialCharacters() {
    String specialId = "node-id_123.test@example";
    NodeId nodeId = new NodeId(specialId);

    assertEquals(specialId, nodeId.id());
  }

  @Test
  void shouldAccessIdViaRecordAccessor() {
    NodeId nodeId = new NodeId("test-id");

    assertEquals("test-id", nodeId.id());
  }

  @Test
  void shouldGenerateValidUUIDFormatWithDefaultConstructor() {
    NodeId nodeId = new NodeId();

    // Verify it's a valid UUID format
    assertDoesNotThrow(() -> {
      UUID uuid = UUID.fromString(nodeId.id());
      assertNotNull(uuid);
    });
  }

  @Test
  void shouldBeImmutable() {
    NodeId nodeId = new NodeId("original-id");
    String originalId = nodeId.id();

    // Records are immutable by design
    assertEquals("original-id", nodeId.id());
    assertEquals(originalId, nodeId.id());
  }
}
