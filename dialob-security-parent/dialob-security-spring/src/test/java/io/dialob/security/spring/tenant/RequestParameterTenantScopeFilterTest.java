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
package io.dialob.security.spring.tenant;

import io.dialob.security.tenant.ResysSecurityConstants;
import io.dialob.security.tenant.Tenant;
import io.dialob.security.tenant.TenantContextHolderCurrentTenant;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.util.matcher.RequestMatcher;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RequestParameterTenantScopeFilterTest {

  @Mock
  private TenantAccessEvaluator tenantAccessEvaluator;

  @Mock
  private DefaultTenantSupplier defaultTenantSupplier;

  @Mock
  private HttpServletRequest request;

  @Mock
  private HttpServletResponse response;

  @Mock
  private FilterChain filterChain;

  private RequestParameterTenantScopeFilter filter;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    filter = new RequestParameterTenantScopeFilter(tenantAccessEvaluator, defaultTenantSupplier);
  }

  @AfterEach
  void tearDown() {
    TenantContextHolderCurrentTenant.removeTenant();
  }

  @Test
  void shouldSetTenantFromRequestAttribute() throws ServletException, IOException {
    Tenant tenant = Tenant.of("tenant1");
    when(request.getAttribute(RequestParameterTenantScopeFilter.CURRENT_TENANT_ATTR)).thenReturn(tenant);
    when(tenantAccessEvaluator.doesUserHaveAccessToTenant(tenant)).thenReturn(true);

    filter.doFilterInternal(request, response, filterChain);

    verify(request).setAttribute(RequestParameterTenantScopeFilter.CURRENT_TENANT_ATTR, tenant);
    verify(filterChain).doFilter(request, response);
    verify(request).removeAttribute(RequestParameterTenantScopeFilter.CURRENT_TENANT_ATTR);
  }

  @Test
  void shouldSetTenantFromRequestParameter() throws ServletException, IOException {
    Tenant tenant = Tenant.of("tenant1");
    when(request.getAttribute(RequestParameterTenantScopeFilter.CURRENT_TENANT_ATTR)).thenReturn(null);
    when(request.getParameter("tenantId")).thenReturn("tenant1");
    when(tenantAccessEvaluator.doesUserHaveAccessToTenant(tenant)).thenReturn(true);

    filter.doFilterInternal(request, response, filterChain);

    verify(request).setAttribute(RequestParameterTenantScopeFilter.CURRENT_TENANT_ATTR, tenant);
    verify(filterChain).doFilter(request, response);
  }

  @Test
  void shouldSetDefaultTenantWhenNoParameterOrAttribute() throws ServletException, IOException {
    Tenant defaultTenant = ResysSecurityConstants.DEFAULT_TENANT;
    when(request.getAttribute(RequestParameterTenantScopeFilter.CURRENT_TENANT_ATTR)).thenReturn(null);
    when(request.getParameter("tenantId")).thenReturn(null);
    when(defaultTenantSupplier.get()).thenReturn(Optional.of(defaultTenant));
    when(tenantAccessEvaluator.doesUserHaveAccessToTenant(defaultTenant)).thenReturn(true);

    filter.doFilterInternal(request, response, filterChain);

    verify(request).setAttribute(RequestParameterTenantScopeFilter.CURRENT_TENANT_ATTR, defaultTenant);
    verify(filterChain).doFilter(request, response);
  }

  @Test
  void shouldThrowAccessDeniedExceptionWhenAccessDenied() throws ServletException, IOException {
    Tenant tenant = Tenant.of("tenant1");
    when(request.getAttribute(RequestParameterTenantScopeFilter.CURRENT_TENANT_ATTR)).thenReturn(null);
    when(request.getParameter("tenantId")).thenReturn("tenant1");
    when(tenantAccessEvaluator.doesUserHaveAccessToTenant(tenant)).thenReturn(false);

    assertThrows(AccessDeniedException.class, () -> filter.doFilterInternal(request, response, filterChain));

    verify(filterChain, never()).doFilter(request, response);
  }

  @Test
  void shouldThrowAccessDeniedExceptionWhenNoTenantAndRequired() throws ServletException, IOException {
    when(request.getAttribute(RequestParameterTenantScopeFilter.CURRENT_TENANT_ATTR)).thenReturn(null);
    when(request.getParameter("tenantId")).thenReturn(null);
    when(defaultTenantSupplier.get()).thenReturn(Optional.empty());

    // Default matcher matches any request

    assertThrows(AccessDeniedException.class, () -> filter.doFilterInternal(request, response, filterChain));

    verify(filterChain, never()).doFilter(request, response);
  }

  @Test
  void shouldProceedWhenNoTenantAndNotRequired() throws ServletException, IOException {
    RequestMatcher matcher = mock(RequestMatcher.class);
    when(matcher.matches(request)).thenReturn(false);
    filter.setTenantRequiredMatcher(matcher);

    when(request.getAttribute(RequestParameterTenantScopeFilter.CURRENT_TENANT_ATTR)).thenReturn(null);
    when(request.getParameter("tenantId")).thenReturn(null);
    when(defaultTenantSupplier.get()).thenReturn(Optional.empty());

    filter.doFilterInternal(request, response, filterChain);

    verify(filterChain).doFilter(request, response);
    verifyNoInteractions(tenantAccessEvaluator);
  }

  @Test
  void shouldUseCustomParameterName() throws ServletException, IOException {
    filter.setParameterName("customTenantId");
    Tenant tenant = Tenant.of("tenant1");
    when(request.getAttribute(RequestParameterTenantScopeFilter.CURRENT_TENANT_ATTR)).thenReturn(null);
    when(request.getParameter("customTenantId")).thenReturn("tenant1");
    when(tenantAccessEvaluator.doesUserHaveAccessToTenant(tenant)).thenReturn(true);

    filter.doFilterInternal(request, response, filterChain);

    verify(filterChain).doFilter(request, response);
  }
}
