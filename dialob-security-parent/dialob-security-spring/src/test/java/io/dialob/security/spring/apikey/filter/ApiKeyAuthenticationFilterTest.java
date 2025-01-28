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
package io.dialob.security.spring.apikey.filter;

import io.dialob.security.key.ApiKey;
import io.dialob.security.key.ImmutableApiKey;
import io.dialob.security.key.ServletRequestApiKeyExtractor;
import io.dialob.security.spring.apikey.ApiKeyAuthenticationException;
import io.dialob.security.spring.apikey.ApiKeyAuthenticationToken;
import io.dialob.security.spring.apikey.ApiKeyValidator;
import io.dialob.security.spring.filter.ApiKeyAuthenticationFilter;
import jakarta.servlet.DispatcherType;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.util.matcher.RequestMatcher;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ApiKeyAuthenticationFilterTest {

  @Mock
  private AuthenticationManager authenticationManager;

  @Mock
  private ServletRequestApiKeyExtractor keyRequestExtractor;

  @Mock
  private RequestMatcher requestMatcher;

  @Mock
  private ApiKeyValidator apiRequestValidator;

  @InjectMocks
  private ApiKeyAuthenticationFilter apiKeyAuthenticationFilter;

  @Mock
  private HttpServletRequest request;

  @Mock
  private HttpServletResponse response;

  @Mock
  private FilterChain filterChain;

  @Test
  void shouldThrowBadCredentialsExceptionIfKeyCouldNotBeExtracted() {
    Assertions.assertThrows(BadCredentialsException.class, () -> {
      try {
        apiKeyAuthenticationFilter.attemptAuthentication(request);
      } finally {
        verify(keyRequestExtractor).extract(request);
        verifyNoMoreInteractions(keyRequestExtractor);
      }
    });
  }

  @Test
  void shouldThrowAccessDeniedExceptionIfRequestIsAlreadyAuthenticated() {
    Assertions.assertThrows(ApiKeyAuthenticationException.class, () -> {
      try {
        SecurityContextHolder.setStrategyName("MODE_GLOBAL");
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("user", "pass"));
        when(keyRequestExtractor.extract(request)).thenReturn(ImmutableApiKey.of("cli").withToken("sig"));
        apiKeyAuthenticationFilter.attemptAuthentication(request);
      } finally {
        verify(keyRequestExtractor).extract(request);
        verifyNoMoreInteractions(keyRequestExtractor);
        SecurityContextHolder.clearContext();
      }
    });
  }


  @Test
  void shouldThrowApiKeyAuthenticationExceptionIfApiKeyCannotBeAuthenticated() throws Exception {
    Assertions.assertThrows(ApiKeyAuthenticationException.class, () -> {
      ApiKey apiKey = ImmutableApiKey.of("cli").withToken("sig");
      ApiKeyAuthenticationToken apiAuthenticationToken = Mockito.mock(ApiKeyAuthenticationToken.class);
      try {
        when(keyRequestExtractor.extract(request)).thenReturn(apiKey);
        when(apiAuthenticationToken.isAuthenticated()).thenReturn(false);
        when(authenticationManager.authenticate(any(ApiKeyAuthenticationToken.class))).thenReturn(apiAuthenticationToken);
        apiKeyAuthenticationFilter.attemptAuthentication(request);

      } finally {
        verify(apiAuthenticationToken).isAuthenticated();
        verify(keyRequestExtractor).extract(request);
        verify(authenticationManager).authenticate(any(ApiKeyAuthenticationToken.class));
        verifyNoMoreInteractions(keyRequestExtractor, apiRequestValidator, apiAuthenticationToken);
      }
    });
  }

  @Test
  void shouldThrowApiKeyAuthenticationExceptionIfApiKeyCannotBeAuthenticated2() throws Exception {
    Assertions.assertThrows(ApiKeyAuthenticationException.class, () -> {
      ApiKey apiKey = ImmutableApiKey.of("cli").withToken("sig");
      try {
        when(keyRequestExtractor.extract(request)).thenReturn(apiKey);
        when(authenticationManager.authenticate(any(ApiKeyAuthenticationToken.class))).thenReturn(null);
        apiKeyAuthenticationFilter.attemptAuthentication(request);

      } finally {
        verify(keyRequestExtractor).extract(request);
        verify(authenticationManager).authenticate(any(ApiKeyAuthenticationToken.class));
        verifyNoMoreInteractions(keyRequestExtractor, apiRequestValidator);
      }
    });
  }

  @Test
  void shouldAcceptValidatedKey() throws Exception {
    ApiKey apiKey = ImmutableApiKey.of("cli").withToken("sig");
    ApiKeyAuthenticationToken apiAuthenticationToken = Mockito.mock(ApiKeyAuthenticationToken.class);
    when(keyRequestExtractor.extract(request)).thenReturn(apiKey);
    when(apiAuthenticationToken.isAuthenticated()).thenReturn(true);
    when(authenticationManager.authenticate(any(ApiKeyAuthenticationToken.class))).thenReturn(apiAuthenticationToken);
    Authentication authentication = apiKeyAuthenticationFilter.attemptAuthentication(request);
    assertNotNull(authentication);
    verify(apiAuthenticationToken).isAuthenticated();
    verify(keyRequestExtractor).extract(request);
    verify(authenticationManager).authenticate(any(ApiKeyAuthenticationToken.class));
    verifyNoMoreInteractions(keyRequestExtractor, apiRequestValidator, apiAuthenticationToken);
  }


  @Test
  void shouldCallNextFilterIfRequestDoMatch() throws Exception {
    when(requestMatcher.matches(request)).thenReturn(false);
    when(request.getDispatcherType()).thenReturn(DispatcherType.REQUEST);

    apiKeyAuthenticationFilter.doFilter(request, response, filterChain);
    verify(filterChain).doFilter(request, response);
    verify(requestMatcher).matches(request);
    verifyNoMoreInteractions(keyRequestExtractor, apiRequestValidator, filterChain, requestMatcher);
  }
}
