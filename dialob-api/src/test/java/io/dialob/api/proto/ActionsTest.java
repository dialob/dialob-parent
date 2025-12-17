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
package io.dialob.api.proto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ActionsTest {

  @Test
  void hasBuilder() {
    Actions actions = new Actions.Builder()
      .addActions(new Action.Builder().id("action1").type(Action.Type.ITEM).build())
      .build();
    assertNotNull(actions);
    assertEquals(1, actions.getActions().size());
    assertEquals("action1", actions.getActions().get(0).getId());
  }

}
