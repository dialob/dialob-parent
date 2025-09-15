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
package io.dialob.session.engine.program.expr;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class OutputFormatterTest {

  @Test
  void test() {
    var formatter = new OutputFormatter("fi");
    assertEquals("1.1.2018", formatter.format(LocalDate.of(2018, 1, 1), null));
    assertEquals("12.15", formatter.format(LocalTime.of(12, 15, 10), null));
    assertEquals("2018", formatter.format(LocalDate.of(2018, 1, 1), "YYYY"));
    assertEquals("15", formatter.format(LocalTime.of(12, 15, 10), "mm"));
  }

}
