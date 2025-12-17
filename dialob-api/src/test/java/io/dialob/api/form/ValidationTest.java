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
package io.dialob.api.form;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ValidationTest {

  @Test
  void hasBuilder() {
    Validation validation = new Validation.Builder()
      .rule("value > 10")
      .putMessage("k","Value must be greater than 10")
      .build();
    assertNotNull(validation);
    assertEquals("value > 10", validation.getRule());
    assertEquals("Value must be greater than 10", validation.getMessage().get("k"));
  }

}
