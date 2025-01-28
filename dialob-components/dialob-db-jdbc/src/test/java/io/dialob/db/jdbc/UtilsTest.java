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
package io.dialob.db.jdbc;

import io.dialob.db.spi.exceptions.DocumentNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

class UtilsTest {

  @Test
  void test() throws Exception {
    Assertions.assertArrayEquals(new byte[16], Utils.toOID(""));
    Assertions.assertNull(Utils.toOID(null));
    Assertions.assertArrayEquals(new byte[] {
      (byte) 0x63, (byte) 0x57, (byte) 0x02, (byte) 0xE2, (byte) 0xE3, (byte) 0x5f, (byte) 0x4f, (byte) 0x86,
      (byte) 0xa1, (byte) 0xf9, (byte) 0xc8, (byte) 0x30, (byte) 0xF0, (byte) 0xa4, (byte) 0x58, (byte) 0xea
    },
      Utils.toOID("635702e2-e35f-4f86-a1f9-c830f0a458ea"));
  }

  @Test
  void shouldThrowIllegalArgumentExceptionWhenIdTooLong() {
    Assertions.assertThrows(DocumentNotFoundException.class, () -> Utils.toOID("0000000000000000000000000000000000"));

  }

  @Test
  void shouldThrowIllegalArgumentExceptionWhenHoldIllegalCharacters() {
    Assertions.assertThrows(DocumentNotFoundException.class, () -> Utils.toOID("0000000000000000000000000000000x"));
  }

  @Test
  void revisionMustBeInteger() {
    Assertions.assertThrows(IllegalArgumentException.class, () -> Utils.validateRevValue("a"));
  }

  @Test
  void revisionMayNotBeBlank() {
    Assertions.assertThrows(IllegalArgumentException.class, () -> Utils.validateRevValue(""));
  }

  @Test
  void revisionParsesToInteger() {
    Assertions.assertEquals((Integer)14, Utils.validateRevValue("14"));
    Assertions.assertNull(Utils.validateRevValue(null));
  }

  @Test
  void generates128BitValue() {
    Assertions.assertEquals(16, Utils.generateOID().length);
  }


  @Test
  void shouldConvertOIDToString() {
    Assertions.assertEquals("00000000000000000000000000000000", Utils.toString(new byte[16]));
    Assertions.assertEquals("f0000000000000000000000000000000", Utils.toString(Utils.toOID("f0000000000000000000000000000000")));
    Assertions.assertEquals("f0000000000000000000000000000001", Utils.toString(Utils.toOID("f0000000000000000000000000000001")));
    Assertions.assertEquals("f00000000000123456789abcdef00001", Utils.toString(Utils.toOID("f00000000000123456789abcdef00001")));

  }
}
