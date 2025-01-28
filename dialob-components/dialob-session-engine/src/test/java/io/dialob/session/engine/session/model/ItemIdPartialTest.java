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

import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class ItemIdPartialTest {

  @Test
  void test() {
    ItemId itemIdPartial = ImmutableItemIdPartial.of(Optional.of(ImmutableItemRef.of("i1", Optional.empty())));
    ItemId itemId = IdUtils.toId("i1.3");
    IdUtils.matches(itemIdPartial, itemId);
    assertTrue(IdUtils.matches(itemIdPartial, itemId));
  }

  @Test
  void partialErrorsShouldNotMatch() {
    ErrorId errorId1 = ImmutableErrorId.of(IdUtils.toId("i1.3"),"ERR");
    ErrorId errorId2 = ImmutableErrorId.of(IdUtils.toId("i2.3"),"ERR");
    assertNotEquals(errorId1,errorId2);
    assertFalse(IdUtils.matches(errorId1, errorId2));
  }

  @Test
  void partialErrorsShouldMatch() {
    ErrorId errorId1 = ImmutableErrorId.of(IdUtils.toId("i1.*"),"ERR");
    ErrorId errorId2 = ImmutableErrorId.of(IdUtils.toId("i1.3"),"ERR");
    assertNotEquals(errorId1,errorId2);
    assertTrue(IdUtils.matches(errorId1, errorId2));
  }

}
