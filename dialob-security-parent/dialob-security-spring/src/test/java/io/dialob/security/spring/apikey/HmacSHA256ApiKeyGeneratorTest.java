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
package io.dialob.security.spring.apikey;

import io.dialob.security.key.ApiKey;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HmacSHA256ApiKeyGeneratorTest {

  @Test
  void test() throws Exception {
    ApiKeyGenerator generator = new HmacSHA256ApiKeyGenerator("123".getBytes());
    ApiKey apiKey = generator.generateApiKey("00000000-0000-0000-0000-000000000000");
    assertEquals("00000000-0000-0000-0000-000000000000", apiKey.getClientId());
    assertEquals(40, apiKey.getToken().get().length());
    assertEquals(44, apiKey.getHash().get().length());
  }

  @Test
  void test2() throws Exception {
    ApiKeyGenerator generator = new HmacSHA256ApiKeyGenerator("123".getBytes());
    ApiKey apiKey = generator.generateApiKey("00000000-0000-0000-0000-000000000000", new byte[] {0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00});
    assertEquals("00000000-0000-0000-0000-000000000000", apiKey.getClientId());
    assertEquals("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", apiKey.getToken().get());
    assertEquals("VzSXEaX2SMthfysq9l+1a6Dl9nFwz1xVWZz0iX9VJsw=", apiKey.getHash().get());
  }


  @Test
  @Disabled
  void generate() throws Exception {
    ApiKeyGenerator generator = new HmacSHA256ApiKeyGenerator("123".getBytes());
    ApiKey apiKey = generator.generateApiKey(UUID.randomUUID().toString());
    System.out.println("clientId : " + apiKey.getClientId());
    System.out.println("token : " + apiKey.getToken().get());
    System.out.println("hash  : " + apiKey.getHash().get());

  }
}
