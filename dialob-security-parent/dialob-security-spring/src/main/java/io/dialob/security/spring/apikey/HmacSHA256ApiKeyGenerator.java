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

import io.dialob.security.UUIDUtils;
import io.dialob.security.key.ApiKey;
import io.dialob.security.key.ImmutableApiKey;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.UUID;

public class HmacSHA256ApiKeyGenerator implements ApiKeyGenerator {

  private final SecretKeySpec secretKey;

  private final SecureRandom secureRandom;

  public HmacSHA256ApiKeyGenerator(byte[] key) throws NoSuchAlgorithmException {
    this.secretKey = new SecretKeySpec(key, "HmacSHA256");
    this.secureRandom = SecureRandom.getInstance("SHA1PRNG");
  }

  @Override
  public ApiKey generateApiKey(String clientId, byte[] secret) {
    String token = null;
    try {
      Mac hmacSHA256 = Mac.getInstance("HmacSHA256");
      hmacSHA256.init(secretKey);
      final byte[] hash = hmacSHA256.doFinal(secret);
      final byte[] clientIdUUID = UUIDUtils.toBytes(UUID.fromString(clientId));
      byte[] tokenBytes = new byte[30];
      System.arraycopy(clientIdUUID, 0, tokenBytes, 0, 16);
      System.arraycopy(secret, 0, tokenBytes, 16, 14);
      return ImmutableApiKey.builder()
        .clientId(clientId)
        .hash(Base64.getEncoder().encodeToString(hash))
        .token(Base64.getEncoder().encodeToString(tokenBytes))
        .build();
    } catch (Exception e) {
      throw new IllegalStateException(e);
    }
  }

  @Override
  public ApiKey generateApiKey(String clientId) {
    byte[] secret = new byte[14];
    secureRandom.nextBytes(secret);
    return generateApiKey(clientId, secret);
  }
}

