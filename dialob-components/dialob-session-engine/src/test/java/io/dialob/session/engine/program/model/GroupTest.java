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
package io.dialob.session.engine.program.model;

import io.dialob.session.engine.session.model.IdUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class GroupTest {

  @Test
  void shouldAcceptNullPropertyValues() {
    var group = ImmutableGroup.builder()
      .id(IdUtils.toId("x"))
      .type("g")
      .itemsExpression(Mockito.mock(Expression.class))
      .putProps("x", null)
      .build();

    assertNotNull(group.getProps());
    Assertions.assertTrue(group.getProps().containsKey("x"));
    Assertions.assertNull(group.getProps().get("x"));
  }

}
