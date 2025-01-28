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
package io.dialob.security;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

class UUIDUtilsTest {

  @Test
  void shouldThrowIllegalArgumentException() {
    Assertions.assertThatThrownBy(()->UUIDUtils.toUUID(new byte[0]))
      .hasMessage("UUID is 16 bytes long. oid is 0 bytes.")
      .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void shouldConvertToUUID() {
    assertEquals("00000000-0000-0000-0000-000000000000", UUIDUtils.toUUID(new byte[] {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0}).toString());
    assertEquals("ff000000-0000-0000-0000-0000000000fe", UUIDUtils.toUUID(new byte[] {(byte)0xff,0,0,0,0,0,0,0,0,0,0,0,0,0,0,(byte)0xfe}).toString());
    assertEquals("01020304-0506-0708-090a-0b0c0d0e0f10", UUIDUtils.toUUID(new byte[] {(byte)0x1,(byte)0x2,(byte)0x3,(byte)0x4,(byte)0x5,(byte)0x6,(byte)0x7,(byte)0x8,
      (byte)0x9,(byte)0xa,(byte)0xb,(byte)0xc,(byte)0xd,(byte)0xe,(byte)0xf,(byte)0x10}).toString());
  }

  @Test
  void shouldConvertToBytes() {
    assertArrayEquals(new byte[] {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0}, UUIDUtils.toBytes(UUID.fromString("00000000-0000-0000-0000-000000000000")));
    assertArrayEquals(new byte[] {(byte)0xff,0,0,0,0,0,0,0,0,0,0,0,0,0,0,(byte)0xfe}, UUIDUtils.toBytes(UUID.fromString("ff000000-0000-0000-0000-0000000000fe")));
    assertArrayEquals(new byte[] {(byte)0x1,(byte)0x2,(byte)0x3,(byte)0x4,(byte)0x5,(byte)0x6,(byte)0x7,(byte)0x8,
      (byte)0x9,(byte)0xa,(byte)0xb,(byte)0xc,(byte)0xd,(byte)0xe,(byte)0xf,(byte)0x10}, UUIDUtils.toBytes(UUID.fromString("01020304-0506-0708-090a-0b0c0d0e0f10")));
  }


}
