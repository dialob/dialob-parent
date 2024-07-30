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

import edu.umd.cs.findbugs.annotations.NonNull;
import io.dialob.security.key.ServletRequestApiKeyExtractor;
import io.dialob.security.spring.AuthenticationStrategy;
import io.dialob.security.spring.apikey.ApiKeyAuthoritiesProvider;
import io.dialob.security.spring.apikey.ApiKeyRequestMatcher;
import io.dialob.security.spring.apikey.ApiKeyValidator;
import io.dialob.security.spring.apikey.ClientApiKeyService;
import io.dialob.security.spring.filter.ApiKeyAuthenticationFilter;
import io.dialob.security.spring.tenant.TenantAccessEvaluator;
import io.dialob.settings.DialobSettings;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;
import org.springframework.security.web.util.matcher.AndRequestMatcher;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.AnyRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import java.util.ArrayList;
import java.util.List;


@Configuration
public class ApiServiceSecurityConfigurer extends AbstractApiSecurityConfigurer {

  public static final RequestMatcher SESSION_NOT_EXISTS_MATCHER = request -> request.getSession(false) == null;

  private final ClientApiKeyService apiKeyService;

  private final ApiKeyAuthoritiesProvider apiKeyAuthoritiesProvider;

  private final ServletRequestApiKeyExtractor keyRequestExtractor;

  private final ApiKeyValidator apiKeyValidator;

  private final AuthenticationManager authenticationManager;

  private final boolean allRequests;

  private RequestMatcher requestMatcher;

  private RequestMatcher apiKeyRequestMatcher;

  public ApiServiceSecurityConfigurer(@NonNull ClientApiKeyService apiKeyService,
                                      @NonNull ApiKeyAuthoritiesProvider apiKeyAuthoritiesProvider,
                                      @NonNull ApiKeyValidator apiKeyValidator,
                                      @NonNull DialobSettings settings,
                                      @NonNull ServletRequestApiKeyExtractor keyRequestExtractor,
                                      @NonNull TenantAccessEvaluator tenantPermissionEvaluator,
                                      @NonNull AuthenticationStrategy authenticationStrategy,
                                      @NonNull AuthenticationManager authenticationManager,
                                      @NonNull Environment env) {
    super(settings.getApi().getContextPath(), tenantPermissionEvaluator, authenticationStrategy);
    this.apiKeyService = apiKeyService;
    this.apiKeyAuthoritiesProvider = apiKeyAuthoritiesProvider;
    this.keyRequestExtractor = keyRequestExtractor;
    this.apiKeyValidator = apiKeyValidator;
    this.authenticationManager = authenticationManager;
    this.allRequests = env.acceptsProfiles(Profiles.of("!ui"));
  }

  protected RequestMatcher apiKeyRequestMatcher() {
    if (apiKeyRequestMatcher == null) {
      if (allRequests) {
        apiKeyRequestMatcher = AnyRequestMatcher.INSTANCE;
      } else {
        apiKeyRequestMatcher = new ApiKeyRequestMatcher(this.keyRequestExtractor);
      }
    }
    return apiKeyRequestMatcher;
  }

  protected RequestMatcher requestMatcher() {
    if (requestMatcher == null) {
      List<RequestMatcher> requestMatchers = new ArrayList<>();
      if (StringUtils.isNotBlank(getContextPath())) {
        requestMatchers.add(new AntPathRequestMatcher(getContextPath() + "/**"));
      }
      if (!allRequests) {
        requestMatchers.add(SESSION_NOT_EXISTS_MATCHER);
        requestMatchers.add(apiKeyRequestMatcher());
      }
      if (requestMatchers.isEmpty()) {
        requestMatcher = AnyRequestMatcher.INSTANCE;
      } else {
        requestMatcher = new AndRequestMatcher(requestMatchers);
      }
    }
    return requestMatcher;
  }

  @Override
  protected HttpSecurity configureAuthentication(HttpSecurity http) throws Exception {
    // Disable authentication
    return http
      .sessionManagement((configurer) -> configurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
      .logout(AbstractHttpConfigurer::disable);
  }

  @Override
  protected HttpSecurity configureCsrf(HttpSecurity http) throws Exception {
    return http.csrf(AbstractHttpConfigurer::disable);
  }

  @Override
  protected HttpSecurity configureFrameOptions(HttpSecurity http) throws Exception {
    return http.headers(customizer -> customizer.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable));
  }

  @Override
  protected HttpSecurity configureMDCPrincipalFilter(HttpSecurity http) throws Exception {
    return super.configureMDCPrincipalFilter(http)
      .addFilterBefore(new ApiKeyAuthenticationFilter(
          authenticationManager,
          keyRequestExtractor,
          apiKeyRequestMatcher()),
        AnonymousAuthenticationFilter.class);
  }

  @Bean
  @Order(Ordered.HIGHEST_PRECEDENCE)
  SecurityFilterChain apiFilterChain(HttpSecurity http) throws Exception {
    return super.filterChain(http);
  }

}
