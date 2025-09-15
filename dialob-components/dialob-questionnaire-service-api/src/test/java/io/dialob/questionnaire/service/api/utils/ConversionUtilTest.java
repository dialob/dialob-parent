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
package io.dialob.questionnaire.service.api.utils;

import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ConversionUtilTest {

  static class TestType {
    @Override
    public String toString() {
      return "TestType";
    }
  }

  @Test
  void test() {
    assertNull(ConversionUtil.toJSON(null));
    Object json = ConversionUtil.toJSON(new String[0]);
    assertTrue(json.getClass().isArray());
    assertEquals(List.of("a"), ConversionUtil.toJSON(List.of("a")));
    assertEquals("1970-01-01", ConversionUtil.toJSON(new Date(321L)));
    assertEquals("TestType", ConversionUtil.toJSON(new TestType()));
  }

}
