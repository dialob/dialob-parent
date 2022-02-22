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
package io.dialob.boot.security;

import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.oauth2.client.endpoint.OAuth2AccessTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestRedirectFilter;

import io.dialob.security.spring.ApiKeyCurrentUserProvider;
import io.dialob.security.spring.AuthenticationStrategy;
import io.dialob.security.spring.OAuth2SpringSecurityCurrentUserProvider;
import io.dialob.security.user.CurrentUserProvider;
import io.dialob.security.user.DelegateCurrentUserProvider;

public class OAuth2AuthenticationStrategy implements AuthenticationStrategy {

  private final GrantedAuthoritiesMapper grantedAuthoritiesMapper;

  private final OAuth2AccessTokenResponseClient<OAuth2AuthorizationCodeGrantRequest> accessTokenResponseClient;

  public OAuth2AuthenticationStrategy(@NonNull GrantedAuthoritiesMapper grantedAuthoritiesMapper,
                                      @NonNull OAuth2AccessTokenResponseClient<OAuth2AuthorizationCodeGrantRequest> accessTokenResponseClient) {
    this.grantedAuthoritiesMapper = grantedAuthoritiesMapper;
    this.accessTokenResponseClient = accessTokenResponseClient;
  }

  @Override
  public HttpSecurity configureAuthentication(@NonNull HttpSecurity http, @Nullable AuthenticationManager authenticationManager) throws Exception {
    // @formatter:off
    http = http
      .oauth2Login()
      .tokenEndpoint()
      .accessTokenResponseClient(accessTokenResponseClient).and()
      .loginPage(OAuth2AuthorizationRequestRedirectFilter.DEFAULT_AUTHORIZATION_REQUEST_BASE_URI + "/default") // Skip login provider selection
      .userInfoEndpoint()
      .userAuthoritiesMapper(grantedAuthoritiesMapper)
      .and()
      .and();
    return http.sessionManagement()
      .sessionCreationPolicy(SessionCreationPolicy.ALWAYS)
      .and();
    // @formatter:on
  }

  @Override
  public CurrentUserProvider currentUserProviderBean() {
    return new DelegateCurrentUserProvider(
      new OAuth2SpringSecurityCurrentUserProvider(),
      new ApiKeyCurrentUserProvider()
    );
  }
}
