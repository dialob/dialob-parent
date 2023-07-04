/*
 * Copyright © 2015 - 2021 ReSys (info@dialob.io)
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
import io.dialob.security.key.ImmutableApiKey;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.BadCredentialsException;

import java.time.LocalDateTime;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.assertFalse;

class HmacSHA256ApiKeyValidatorTest {

  @Test
  public void shouldVerifyToken() {
    HmacSHA256ApiKeyValidator validator = new HmacSHA256ApiKeyValidator(")cA^!}6uvD0n21,2yNfl[kAl>b|oU*2W".getBytes());
    ApiKey key = new RequestHeaderApiKeyExtractor().createApiKey("3eWxtU1dS02mgRzrmEfSwpL/9wL3/OZWUQdcSDsV");
    final byte[] bytes = Base64.getDecoder().decode(key.getToken().get());
    Assertions.assertTrue(validator.verifyToken(
      bytes,
      "FbHscNC8NtgFpul+drDI37Mfo5NyDxx6AQtv59Ehq7I="));
    assertFalse(validator.verifyToken(
      bytes,
      "kG0621+jXaUCcPpBJIluHXjlA//sWO3zGaq5BAuq+lQ="));
    assertFalse(validator.verifyToken(
      bytes,
      ""));
    assertFalse(validator.verifyToken(
      new byte[0],
      "FbHscNC8NtgFpul+drDI37Mfo5NyDxx6AQtv59Ehq7I="));
    assertFalse(validator.verifyToken(
      new byte[0],
      ""));
  }



  @Test
  public void shouldValidate() {
    HmacSHA256ApiKeyValidator validator = new HmacSHA256ApiKeyValidator("hash".getBytes()) {
      @Override
      protected boolean verifyToken(byte[] plain, String hash) {
        return true;
      }
    };
    Assertions.assertThrows(BadCredentialsException.class, () -> validator.validateApiKey(ImmutableApiKey.builder()
      .clientId("123-123")
      .hash("abc")
      .build(), ImmutableApiKey.builder()
      .clientId("123-123")
      .hash("hh")
      .build()
    ));
    Assertions.assertThrows(BadCredentialsException.class, () -> validator.validateApiKey(ImmutableApiKey.builder()
      .clientId("123-123")
      .token("abc")
      .build(),ImmutableApiKey.builder()
      .clientId("123-123")
      .hash("hh")
      .build()
    ));
    Assertions.assertThrows(BadCredentialsException.class, () -> validator.validateApiKey(ImmutableApiKey.builder()
      .clientId("123-123")
      .token("abc")
      .build(),ImmutableApiKey.builder()
      .clientId("123-123")
      .build()
    ));
    Assertions.assertThrows(BadCredentialsException.class, () -> validator.validateApiKey(ImmutableApiKey.builder()
      .clientId("123-123")
      .build(),ImmutableApiKey.builder()
      .clientId("123-123")
      .hash("hh")
      .build()
    ));
    Assertions.assertThrows(BadCredentialsException.class, () -> validator.validateApiKey(ImmutableApiKey.builder()
      .clientId("123-123")
      .hash("abc")
      .endDateTime(LocalDateTime.now().minusDays(1))
      .build(),ImmutableApiKey.builder()
      .clientId("123-123")
      .token("hh")
      .build()
    ));
    Assertions.assertThrows(BadCredentialsException.class, () -> validator.validateApiKey(ImmutableApiKey.builder()
      .clientId("123-123")
      .hash("abc")
      .startDateTime(LocalDateTime.now().plusDays(1))
      .build(),ImmutableApiKey.builder()
      .clientId("123-123")
      .token("hh")
      .build()
    ));

    //
    Assertions.assertDoesNotThrow(() -> validator.validateApiKey(ImmutableApiKey.builder()
      .clientId("123-123")
      .hash("abc")
      .build(),ImmutableApiKey.builder()
      .clientId("123-123")
      .token("hh")
      .build()
    ));
    Assertions.assertDoesNotThrow(() -> validator.validateApiKey(ImmutableApiKey.builder()
      .clientId("123-123")
      .hash("abc")
      .startDateTime(LocalDateTime.now().minusDays(1))
      .endDateTime(LocalDateTime.now().plusDays(1))
      .build(),ImmutableApiKey.builder()
      .clientId("123-123")
      .token("hh")
      .build()
    ));
  }
}
