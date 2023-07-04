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

import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Objects;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.security.authentication.BadCredentialsException;

import io.dialob.security.key.ApiKey;

public class HmacSHA256ApiKeyValidator implements ApiKeyValidator {

  private final SecretKeySpec secretKey;

  public HmacSHA256ApiKeyValidator(byte[] key) {
    this.secretKey = new SecretKeySpec(key, "HmacSHA256");
  }

  @Override
  public void validateApiKey(ApiKey apiKey, ApiKey requestKey) {
    if (apiKey == null || !apiKey.isValid()) {
      throw new BadCredentialsException("Invalid API key");
    }
    LocalDateTime now = LocalDateTime.now();
    if (apiKey.getStartDateTime().map(now::isBefore).orElse(false)
      || apiKey.getEndDateTime().map(now::isAfter).orElse(false)) {
      throw new BadCredentialsException("API key expired");
    }
    if (requestKey.getToken().isPresent() && apiKey.getHash().isPresent()) {
      final String token = requestKey.getToken().get();
      try {
        if (!verifyToken(Base64.getDecoder().decode(token), apiKey.getHash().get())) {
          throw new BadCredentialsException("Could not validate API key");
        }
      } catch (IllegalStateException | IllegalArgumentException e) {
        throw new BadCredentialsException("Could not decode API key");
      }
    } else {
      throw new BadCredentialsException("Could not validate API key");
    }
  }

  protected boolean verifyToken(byte[] plain, String hash) {
    try {
      Mac hmacSHA256 = Mac.getInstance("HmacSHA256");
      hmacSHA256.init(secretKey);
      return Objects.equals(
        Base64.getEncoder().encodeToString(hmacSHA256.doFinal(plain)),
        hash);
    } catch (Exception e) {
      throw new IllegalStateException(e);
    }
  }
}
