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
package io.dialob.security.spring.filter;

import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.util.Collections;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import io.dialob.security.key.ApiKey;
import io.dialob.security.key.ServletRequestApiKeyExtractor;
import io.dialob.security.spring.apikey.ApiKeyAuthenticationException;
import io.dialob.security.spring.apikey.ApiKeyAuthenticationToken;

public class ApiKeyAuthenticationFilter extends OncePerRequestFilter {

  private static final Logger LOGGER = LoggerFactory.getLogger(ApiKeyAuthenticationFilter.class);

  private final RequestMatcher requestMatcher;

  private final AuthenticationManager authenticationManager;

  private final ServletRequestApiKeyExtractor keyRequestExtractor;

  private final AuthenticationEntryPoint authenticationEntryPoint;

  public ApiKeyAuthenticationFilter(final AuthenticationManager authenticationManager,
                                    final ServletRequestApiKeyExtractor keyRequestExtractor,
                                    final RequestMatcher requestMatcher) {
    super();
    this.keyRequestExtractor = requireNonNull(keyRequestExtractor,
      "keyRequestExtractor may not be null");
    this.authenticationManager = requireNonNull(authenticationManager,
      "authenticationManager may not be null");
    this.requestMatcher = requireNonNull(requestMatcher,
      "requestMatcher may not be null");
    this.authenticationEntryPoint = new ApiKeyAuthenticationEntryPoint();
  }

  @Override
  protected void doFilterInternal(@NonNull final HttpServletRequest request,
                                  @NonNull final HttpServletResponse response,
                                  @NonNull final FilterChain filterChain) throws ServletException, IOException {
    if (!requestMatcher.matches(request)) {
      filterChain.doFilter(request, response);
      return;
    }
    try {
      Authentication authentication = attemptAuthentication(request);
      SecurityContextHolder.getContext().setAuthentication(authentication);
      filterChain.doFilter(request, response);
    } catch (AuthenticationException failed) {
      if (LOGGER.isDebugEnabled()) {
        LOGGER.debug("Apikey access denied", failed);
      } else {
        LOGGER.warn("Apikey access denied");
      }
      authenticationEntryPoint.commence(request, response, failed);
    } finally {
      SecurityContextHolder.clearContext();
    }
  }

  public Authentication attemptAuthentication(final HttpServletRequest request) {
    final ApiKey apikey = keyRequestExtractor.extract(request);
    if (apikey == null) {
      throw new BadCredentialsException("No api key.");
    }
    LOGGER.debug("Found client '{}' API key on request", apikey.getClientId());
    if (isAuthenticated()) {
      LOGGER.debug("Request already authenticated by other means. Rejecting API key access");
      throw new ApiKeyAuthenticationException("API key may not be coexists with another authentication method.");
    }
    Authentication authentication = authenticate(apikey);
    if (authentication == null || !authentication.isAuthenticated()) {
      LOGGER.debug("Couldn't find API key for client '{}'", apikey.getClientId());
      throw new ApiKeyAuthenticationException("Authentication failed");
    }
    return authentication;
  }

  private Authentication authenticate(final ApiKey apikey) {
    Authentication authentication = new ApiKeyAuthenticationToken(Collections.emptyList(), apikey);
    return authenticationManager.authenticate(authentication);
  }

  private boolean isAuthenticated() {
    final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    return authentication != null && !(authentication instanceof AnonymousAuthenticationToken) && !authentication.isAuthenticated();
  }
}
