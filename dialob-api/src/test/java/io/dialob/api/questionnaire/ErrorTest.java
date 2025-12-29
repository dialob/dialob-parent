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
package io.dialob.api.questionnaire;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ErrorTest {

  @Test
  void hasBuilder() {
    Error error = new Error.Builder()
      .id("123")
      .code("ERR001")
      .description("An error occurred")
      .build();
    assertNotNull(error);
    assertEquals("123", error.id());
    assertEquals("ERR001", error.code());
    assertEquals("An error occurred", error.description());
  }

  @Test
  void copyOfReturnsSameInstance() {
    var element = new Error.Builder().id("k").code("c").build();
    assertSame(element, Error.copyOf(element));
  }


}
