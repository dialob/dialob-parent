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
package io.dialob.db.mongo;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class MongoQuestionnaireIdObfuscatorTest {

  @Test
  public void test() {
    MongoQuestionnaireIdObfuscator idObfuscator = new MongoQuestionnaireIdObfuscator();
    assertEquals("0123", idObfuscator.toMongoId("123"));
    assertEquals("9123", idObfuscator.toMongoId("9123"));
    assertEquals("abcdef", idObfuscator.toMongoId("ABCDEF"));
    assertEquals("00abcdef", idObfuscator.toMongoId("00ABCDEF"));
    assertNull(idObfuscator.toMongoId(""));
    assertNull(idObfuscator.toMongoId(null));
    assertNull(idObfuscator.toMongoId("z"));
  }

}
