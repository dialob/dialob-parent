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

import edu.umd.cs.findbugs.annotations.NonNull;
import io.dialob.security.tenant.Tenant;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Objects;

public class GrantedTenantAccessEvaluator implements TenantAccessEvaluator {

  private final Tenant publicTenant;

  public GrantedTenantAccessEvaluator() {
    this(null);
  }

  public GrantedTenantAccessEvaluator(final Tenant publicTenant) {
    this.publicTenant = publicTenant;
  }

  @Override
  public boolean doesUserHaveAccessToTenant(@NonNull Tenant tenant) {
    if (publicTenant != null && publicTenant.id().equals(tenant.id())) {
      return true;
    }
    SecurityContext securityContext = SecurityContextHolder.getContext();
    if (securityContext != null && securityContext.getAuthentication() instanceof AbstractAuthenticationToken) {
      final AbstractAuthenticationToken authentication = (AbstractAuthenticationToken) securityContext.getAuthentication();
      if (canAccessAnyTenant(authentication)) {
        return true;
      }
      return authentication.getAuthorities().stream().filter(a -> a instanceof TenantGrantedAuthority)
        .anyMatch(a -> ((TenantGrantedAuthority) a).getTenantId().equals(tenant.id()));
    }
    return false;
  }

  protected boolean canAccessAnyTenant(AbstractAuthenticationToken authentication) {
    Objects.requireNonNull(authentication);
    return false;
  }
}
