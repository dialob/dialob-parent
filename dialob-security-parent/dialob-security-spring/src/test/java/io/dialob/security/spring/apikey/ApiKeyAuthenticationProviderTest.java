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

import io.dialob.security.key.ImmutableApiKey;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ApiKeyAuthenticationProviderTest {

  @Test
  void shouldIgnoreNullAuthentication() {
    ClientApiKeyService apiKeyService = Mockito.mock(ClientApiKeyService.class);
    ApiKeyAuthoritiesProvider apiKeyAuthoritiesProvider = Mockito.mock(ApiKeyAuthoritiesProvider.class);
    ApiKeyValidator apiKeyValidator = Mockito.mock(ApiKeyValidator.class);

    ApiKeyAuthenticationProvider authenticationProvider = new ApiKeyAuthenticationProvider(apiKeyService, apiKeyAuthoritiesProvider, apiKeyValidator);
    assertNull(authenticationProvider.authenticate(null));
    Mockito.verifyNoMoreInteractions(apiKeyService, apiKeyAuthoritiesProvider, apiKeyValidator);
  }

  @Test
  void shouldIgnoreNonApiKeyAuthentication() {
    ClientApiKeyService apiKeyService = Mockito.mock(ClientApiKeyService.class);
    ApiKeyAuthoritiesProvider apiKeyAuthoritiesProvider = Mockito.mock(ApiKeyAuthoritiesProvider.class);
    ApiKeyValidator apiKeyValidator = Mockito.mock(ApiKeyValidator.class);

    ApiKeyAuthenticationProvider authenticationProvider = new ApiKeyAuthenticationProvider(apiKeyService, apiKeyAuthoritiesProvider, apiKeyValidator);
    final AnonymousAuthenticationToken authentication = new AnonymousAuthenticationToken("key", "principal", List.of(new SimpleGrantedAuthority("ROLE")));
    assertSame(authentication, authenticationProvider.authenticate(authentication));
    Mockito.verifyNoMoreInteractions(apiKeyService, apiKeyAuthoritiesProvider, apiKeyValidator);
  }

  @Test
  void shouldNotReauthenticateValidApiKeyAuthentication() {
    ClientApiKeyService apiKeyService = Mockito.mock(ClientApiKeyService.class);
    ApiKeyAuthoritiesProvider apiKeyAuthoritiesProvider = Mockito.mock(ApiKeyAuthoritiesProvider.class);
    ApiKeyValidator apiKeyValidator = Mockito.mock(ApiKeyValidator.class);

    ApiKeyAuthenticationProvider authenticationProvider = new ApiKeyAuthenticationProvider(apiKeyService, apiKeyAuthoritiesProvider, apiKeyValidator);
    final Authentication authentication = new ApiKeyAuthenticationToken(Collections.emptyList(), ImmutableApiKey.of("client").withHash("hash"));
    assertSame(authentication, authenticationProvider.authenticate(authentication));
    Mockito.verifyNoMoreInteractions(apiKeyService, apiKeyAuthoritiesProvider, apiKeyValidator);
  }

  @Test
  void shouldAuthenticateNonValidApiKeyAuthentication() {
    ClientApiKeyService apiKeyService = Mockito.mock(ClientApiKeyService.class);
    ApiKeyAuthoritiesProvider apiKeyAuthoritiesProvider = Mockito.mock(ApiKeyAuthoritiesProvider.class);
    ApiKeyValidator apiKeyValidator = Mockito.mock(ApiKeyValidator.class);

    final ImmutableApiKey validApiKey = ImmutableApiKey.builder()
      .clientId("client")
      .hash("hash")
      .build();
    when(apiKeyService.findByClientId("client")).thenReturn(Optional.of(validApiKey));
    when(apiKeyAuthoritiesProvider.loadAuthorities(validApiKey)).thenReturn(Collections.emptyList());


    ApiKeyAuthenticationProvider authenticationProvider = new ApiKeyAuthenticationProvider(apiKeyService, apiKeyAuthoritiesProvider, apiKeyValidator);
    final Authentication authentication = new ApiKeyAuthenticationToken(Collections.emptyList(), ImmutableApiKey.of("client").withToken("token"));
    final Authentication authentication2 = authenticationProvider.authenticate(authentication);


    assertFalse(authentication.isAuthenticated());
    assertTrue(authentication2.isAuthenticated());
    assertEquals("client",authentication2.getPrincipal());
    assertEquals("hash",authentication2.getCredentials());

    verify(apiKeyService).findByClientId("client");
    verify(apiKeyAuthoritiesProvider).loadAuthorities(validApiKey);
    verify(apiKeyValidator).validateApiKey(validApiKey, ImmutableApiKey.of("client").withToken("token"));

    Mockito.verifyNoMoreInteractions(apiKeyService, apiKeyAuthoritiesProvider, apiKeyValidator);
  }

  @Test
  void shouldThrowAccessDeniedIfApiKeyDoNotExists() {
    ClientApiKeyService apiKeyService = Mockito.mock(ClientApiKeyService.class);
    ApiKeyAuthoritiesProvider apiKeyAuthoritiesProvider = Mockito.mock(ApiKeyAuthoritiesProvider.class);
    ApiKeyValidator apiKeyValidator = Mockito.mock(ApiKeyValidator.class);

    when(apiKeyService.findByClientId("client")).thenReturn(Optional.empty());

    ApiKeyAuthenticationProvider authenticationProvider = new ApiKeyAuthenticationProvider(apiKeyService, apiKeyAuthoritiesProvider, apiKeyValidator);
    assertThrows(
      BadCredentialsException.class,
      () -> authenticationProvider.authenticate(new ApiKeyAuthenticationToken(Collections.emptyList(), ImmutableApiKey.of("client").withToken("token"))));

    verify(apiKeyService).findByClientId("client");
    Mockito.verifyNoMoreInteractions(apiKeyService, apiKeyAuthoritiesProvider, apiKeyValidator);

  }
}
