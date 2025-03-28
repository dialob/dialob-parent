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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Collections;

class ScopeTest {

  @Test
  void test() {
    Scope scope = ImmutableScope.of(IdUtils.toId("row.10"), Collections.emptySet());
    Assertions.assertEquals("row.10.q1", IdUtils.toString(scope.mapTo(IdUtils.toId("row.*.q1"), true)));
    Assertions.assertEquals("row.10", IdUtils.toString(scope.mapTo(IdUtils.toId("row.*"), true)));
  }

}
