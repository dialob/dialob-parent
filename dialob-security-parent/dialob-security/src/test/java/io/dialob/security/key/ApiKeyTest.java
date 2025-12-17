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
package io.dialob.security.key;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ApiKeyTest {

  @Test
  void shouldCreateApiKeyWithClientIdOnly() {
    ApiKey apiKey = ApiKey.of("client123");

    assertEquals("client123", apiKey.getClientId());
    assertFalse(apiKey.getToken().isPresent());
    assertFalse(apiKey.getHash().isPresent());
    assertFalse(apiKey.getTenantId().isPresent());
    assertFalse(apiKey.getOwner().isPresent());
    assertFalse(apiKey.getCreated().isPresent());
    assertFalse(apiKey.getStartDateTime().isPresent());
    assertFalse(apiKey.getEndDateTime().isPresent());
  }

  @Test
  void shouldCreateApiKeyUsingBuilder() {
    LocalDateTime now = LocalDateTime.now();
    LocalDateTime start = now.minusDays(1);
    LocalDateTime end = now.plusDays(30);

    ApiKey apiKey = new ApiKey.Builder()
      .clientId("client123")
      .token("token123")
      .hash("hash123")
      .tenantId("tenant1")
      .owner("owner1")
      .created(now)
      .startDateTime(start)
      .endDateTime(end)
      .build();

    assertEquals("client123", apiKey.getClientId());
    assertEquals("token123", apiKey.getToken().orElse(null));
    assertEquals("hash123", apiKey.getHash().orElse(null));
    assertEquals("tenant1", apiKey.getTenantId().orElse(null));
    assertEquals("owner1", apiKey.getOwner().orElse(null));
    assertEquals(now, apiKey.getCreated().orElse(null));
    assertEquals(start, apiKey.getStartDateTime().orElse(null));
    assertEquals(end, apiKey.getEndDateTime().orElse(null));
  }

  @Test
  void shouldAddTokenToExistingApiKey() {
    ApiKey original = ApiKey.of("client123");
    ApiKey withToken = original.withToken("newToken");

    assertEquals("client123", withToken.getClientId());
    assertEquals("newToken", withToken.getToken().orElse(null));
    assertFalse(withToken.getHash().isPresent());
  }

  @Test
  void shouldReplaceTokenWhenCallingWithToken() {
    ApiKey original = new ApiKey.Builder()
      .clientId("client123")
      .token("oldToken")
      .build();

    ApiKey withNewToken = original.withToken("newToken");

    assertEquals("newToken", withNewToken.getToken().orElse(null));
    assertNotEquals(original.getToken().orElse(null), withNewToken.getToken().orElse(null));
  }

  @Test
  void shouldAddHashToExistingApiKey() {
    ApiKey original = ApiKey.of("client123");
    ApiKey withHash = original.withHash("newHash");

    assertEquals("client123", withHash.getClientId());
    assertEquals("newHash", withHash.getHash().orElse(null));
    assertFalse(withHash.getToken().isPresent());
  }

  @Test
  void shouldReplaceHashWhenCallingWithHash() {
    ApiKey original = new ApiKey.Builder()
      .clientId("client123")
      .hash("oldHash")
      .build();

    ApiKey withNewHash = original.withHash("newHash");

    assertEquals("newHash", withNewHash.getHash().orElse(null));
    assertNotEquals(original.getHash().orElse(null), withNewHash.getHash().orElse(null));
  }

  @Test
  void shouldPreserveOtherFieldsWhenUsingWithToken() {
    LocalDateTime now = LocalDateTime.now();
    ApiKey original = new ApiKey.Builder()
      .clientId("client123")
      .hash("hash123")
      .tenantId("tenant1")
      .owner("owner1")
      .created(now)
      .build();

    ApiKey withToken = original.withToken("token123");

    assertEquals("client123", withToken.getClientId());
    assertEquals("hash123", withToken.getHash().orElse(null));
    assertEquals("tenant1", withToken.getTenantId().orElse(null));
    assertEquals("owner1", withToken.getOwner().orElse(null));
    assertEquals(now, withToken.getCreated().orElse(null));
    assertEquals("token123", withToken.getToken().orElse(null));
  }

  @Test
  void shouldPreserveOtherFieldsWhenUsingWithHash() {
    LocalDateTime now = LocalDateTime.now();
    ApiKey original = new ApiKey.Builder()
      .clientId("client123")
      .token("token123")
      .tenantId("tenant1")
      .owner("owner1")
      .created(now)
      .build();

    ApiKey withHash = original.withHash("hash123");

    assertEquals("client123", withHash.getClientId());
    assertEquals("token123", withHash.getToken().orElse(null));
    assertEquals("tenant1", withHash.getTenantId().orElse(null));
    assertEquals("owner1", withHash.getOwner().orElse(null));
    assertEquals(now, withHash.getCreated().orElse(null));
    assertEquals("hash123", withHash.getHash().orElse(null));
  }

  @Test
  void shouldBeValidWhenHashPresentAndTokenAbsent() {
    ApiKey apiKey = new ApiKey.Builder()
      .clientId("client123")
      .hash("hash123")
      .build();

    assertTrue(apiKey.isValid());
  }

  @Test
  void shouldNotBeValidWhenHashAbsent() {
    ApiKey apiKey = new ApiKey.Builder()
      .clientId("client123")
      .token("token123")
      .build();

    assertFalse(apiKey.isValid());
  }

  @Test
  void shouldNotBeValidWhenBothHashAndTokenPresent() {
    ApiKey apiKey = new ApiKey.Builder()
      .clientId("client123")
      .hash("hash123")
      .token("token123")
      .build();

    assertFalse(apiKey.isValid());
  }

  @Test
  void shouldNotBeValidWhenBothHashAndTokenAbsent() {
    ApiKey apiKey = ApiKey.of("client123");

    assertFalse(apiKey.isValid());
  }

  @Test
  void shouldReturnClientIdDirectly() {
    ApiKey apiKey = ApiKey.of("client123");

    assertEquals("client123", apiKey.getClientId());
    assertEquals("client123", apiKey.clientId());
  }

  @Test
  void shouldReturnEmptyOptionalForNullToken() {
    ApiKey apiKey = ApiKey.of("client123");

    assertTrue(apiKey.getToken().isEmpty());
  }

  @Test
  void shouldReturnEmptyOptionalForNullHash() {
    ApiKey apiKey = ApiKey.of("client123");

    assertTrue(apiKey.getHash().isEmpty());
  }

  @Test
  void shouldReturnEmptyOptionalForNullTenantId() {
    ApiKey apiKey = ApiKey.of("client123");

    assertTrue(apiKey.getTenantId().isEmpty());
  }

  @Test
  void shouldReturnEmptyOptionalForNullOwner() {
    ApiKey apiKey = ApiKey.of("client123");

    assertTrue(apiKey.getOwner().isEmpty());
  }

  @Test
  void shouldReturnEmptyOptionalForNullCreated() {
    ApiKey apiKey = ApiKey.of("client123");

    assertTrue(apiKey.getCreated().isEmpty());
  }

  @Test
  void shouldReturnEmptyOptionalForNullStartDateTime() {
    ApiKey apiKey = ApiKey.of("client123");

    assertTrue(apiKey.getStartDateTime().isEmpty());
  }

  @Test
  void shouldReturnEmptyOptionalForNullEndDateTime() {
    ApiKey apiKey = ApiKey.of("client123");

    assertTrue(apiKey.getEndDateTime().isEmpty());
  }

  @Test
  void shouldBeEqualForSameValues() {
    ApiKey apiKey1 = new ApiKey.Builder()
      .clientId("client123")
      .token("token123")
      .hash("hash123")
      .build();

    ApiKey apiKey2 = new ApiKey.Builder()
      .clientId("client123")
      .token("token123")
      .hash("hash123")
      .build();

    assertEquals(apiKey1, apiKey2);
    assertEquals(apiKey1.hashCode(), apiKey2.hashCode());
  }

  @Test
  void shouldNotBeEqualForDifferentClientId() {
    ApiKey apiKey1 = ApiKey.of("client123");
    ApiKey apiKey2 = ApiKey.of("client456");

    assertNotEquals(apiKey1, apiKey2);
  }

  @Test
  void shouldNotBeEqualForDifferentToken() {
    ApiKey apiKey1 = new ApiKey.Builder()
      .clientId("client123")
      .token("token1")
      .build();

    ApiKey apiKey2 = new ApiKey.Builder()
      .clientId("client123")
      .token("token2")
      .build();

    assertNotEquals(apiKey1, apiKey2);
  }

  @Test
  void shouldBeImmutable() {
    ApiKey original = ApiKey.of("client123");
    ApiKey modified = original.withToken("token123");

    assertNotSame(original, modified);
    assertFalse(original.getToken().isPresent());
    assertTrue(modified.getToken().isPresent());
  }

  @Test
  void shouldSupportToString() {
    ApiKey apiKey = ApiKey.of("client123");

    String str = apiKey.toString();
    assertNotNull(str);
    assertTrue(str.contains("client123"));
  }

  @Test
  void shouldReturnPresentOptionalForNonNullValues() {
    LocalDateTime now = LocalDateTime.now();
    ApiKey apiKey = new ApiKey.Builder()
      .clientId("client123")
      .token("token123")
      .hash("hash123")
      .tenantId("tenant1")
      .owner("owner1")
      .created(now)
      .startDateTime(now.minusDays(1))
      .endDateTime(now.plusDays(30))
      .build();

    assertTrue(apiKey.getToken().isPresent());
    assertTrue(apiKey.getHash().isPresent());
    assertTrue(apiKey.getTenantId().isPresent());
    assertTrue(apiKey.getOwner().isPresent());
    assertTrue(apiKey.getCreated().isPresent());
    assertTrue(apiKey.getStartDateTime().isPresent());
    assertTrue(apiKey.getEndDateTime().isPresent());
  }
}
