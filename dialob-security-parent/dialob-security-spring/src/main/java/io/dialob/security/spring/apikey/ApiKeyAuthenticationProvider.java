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
import edu.umd.cs.findbugs.annotations.NonNull;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Objects;

public class ApiKeyAuthenticationProvider implements AuthenticationProvider {

  private final ClientApiKeyService apiKeyService;

  private final ApiKeyAuthoritiesProvider apiKeyAuthoritiesProvider;

  private final ApiKeyValidator apiKeyValidator;

  public ApiKeyAuthenticationProvider(@NonNull ClientApiKeyService apiKeyService,
                                      @NonNull ApiKeyAuthoritiesProvider apiKeyAuthoritiesProvider,
                                      ApiKeyValidator apiKeyValidator) {
    this.apiKeyService = Objects.requireNonNull(apiKeyService);
    this.apiKeyAuthoritiesProvider = Objects.requireNonNull(apiKeyAuthoritiesProvider);
    this.apiKeyValidator = apiKeyValidator != null ? apiKeyValidator : (apiKey, token) -> {};
  }

  @Override
  public Authentication authenticate(Authentication authentication) {
    if (authentication != null && supports(authentication.getClass())) {
      ApiKeyAuthenticationToken apiAuthenticationToken = (ApiKeyAuthenticationToken) authentication;
      ApiKey apiKey = (ApiKey) apiAuthenticationToken.getDetails();
      if (apiKey.isValid()) {
        return authentication;
      }
      return apiKeyService.findByClientId(apiKey.getClientId()).map(loadedApiKey -> {
        apiKeyValidator.validateApiKey(loadedApiKey, apiKey);
        Collection<GrantedAuthority> grantedAuthorities = apiKeyAuthoritiesProvider.loadAuthorities(loadedApiKey);
        return new ApiKeyAuthenticationToken(grantedAuthorities, loadedApiKey);
      }).orElseThrow(() -> new BadCredentialsException("Invalid API key"));
    }
    return authentication;
  }

  @Override
  public boolean supports(Class<?> authentication) {
    return ApiKeyAuthenticationToken.class.isAssignableFrom(authentication);
  }
}
