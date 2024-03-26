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
package io.dialob.security.spring.tenant;

import io.dialob.security.tenant.ImmutableTenant;
import io.dialob.security.tenant.ResysSecurityConstants;
import io.dialob.security.tenant.Tenant;
import io.dialob.security.tenant.TenantContextHolderCurrentTenant;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import edu.umd.cs.findbugs.annotations.NonNull;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.util.matcher.AnyRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

public class RequestParameterTenantScopeFilter extends OncePerRequestFilter {

  public static final Logger LOGGER = LoggerFactory.getLogger(RequestParameterTenantScopeFilter.class);

  public static final String CURRENT_TENANT_ATTR = "CURRENT_TENANT_ATTR";

  private String parameterName = "tenantId";

  private final TenantAccessEvaluator tenantAccessEvaluator;

  private final DefaultTenantSupplier defaultTenantSupplier;

  private RequestMatcher tenantRequiredMatcher = AnyRequestMatcher.INSTANCE;

  public RequestParameterTenantScopeFilter(@NonNull TenantAccessEvaluator tenantPermissionEvaluator) {
    this(tenantPermissionEvaluator, () -> Optional.of(ResysSecurityConstants.DEFAULT_TENANT));
  }

  public RequestParameterTenantScopeFilter(@NonNull TenantAccessEvaluator tenantPermissionEvaluator,
                                           @NonNull DefaultTenantSupplier defaultTenantSupplier) {
    this.tenantAccessEvaluator = Objects.requireNonNull(tenantPermissionEvaluator);
    this.defaultTenantSupplier = Objects.requireNonNull(defaultTenantSupplier);
  }

  public void setParameterName(@NonNull String parameterName) {
    this.parameterName = parameterName;
  }

  public void setTenantRequiredMatcher(@NonNull RequestMatcher tenantRequiredMatcher) {
    this.tenantRequiredMatcher = Objects.requireNonNull(tenantRequiredMatcher);
  }

  private Tenant resolveTenantFromRequest(HttpServletRequest request) {
    Tenant tenant = (Tenant) request.getAttribute(CURRENT_TENANT_ATTR);
    if (tenant == null) {
      String tenantId = request.getParameter(parameterName);
      if (tenantId != null) {
        tenant = ImmutableTenant.of(tenantId, Optional.empty());
      } else {
        tenant = defaultTenantSupplier.get().orElse(null);
      }
    }
    return tenant;
  }

  @Override
  protected void doFilterInternal(@NonNull HttpServletRequest request,
                                  @NonNull HttpServletResponse response,
                                  @NonNull FilterChain filterChain) throws ServletException, IOException {
    try {
      final Tenant tenant = resolveTenantFromRequest(request);
      if (tenant != null) {
        if (!tenantAccessEvaluator.doesUserHaveAccessToTenant(tenant)) {
          tenantAccessDenied(String.format("Access to tenant %s denied.", tenant.getId()));
          return;
        }
        request.setAttribute(CURRENT_TENANT_ATTR, tenant);
        TenantContextHolderCurrentTenant.setTenant(tenant);
      } else {
        if (tenantRequiredMatcher.matches(request)) {
          tenantAccessDenied("User do not have access to any tenant.");
          return;
        }
      }
      filterChain.doFilter(request, response);
    } finally {
      request.removeAttribute(CURRENT_TENANT_ATTR);
      TenantContextHolderCurrentTenant.removeTenant();
    }
  }

  private void tenantAccessDenied(String message) {
    throw new AccessDeniedException(message);
  }
}
