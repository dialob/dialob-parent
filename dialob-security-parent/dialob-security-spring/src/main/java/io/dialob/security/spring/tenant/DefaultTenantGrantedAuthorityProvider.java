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

import io.dialob.security.tenant.Tenant;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

public class DefaultTenantGrantedAuthorityProvider implements DefaultTenantSupplier {

  private Tenant publicTenant;

  public DefaultTenantGrantedAuthorityProvider() {
    this(null);
  }

  public DefaultTenantGrantedAuthorityProvider(Tenant publicTenant) {
    this.publicTenant = publicTenant;
  }

  @Override
  public Optional<Tenant> get() {
    SecurityContext securityContext = SecurityContextHolder.getContext();
    if (securityContext != null && securityContext.getAuthentication() instanceof AbstractAuthenticationToken token) {
      return Optional.ofNullable(token.getAuthorities().stream()
        .filter(grantedAuthority -> grantedAuthority instanceof TenantGrantedAuthority)
        .findFirst()
        .map(grantedAuthority -> Tenant.of(((TenantGrantedAuthority) grantedAuthority).getTenantId(), grantedAuthority.getAuthority()))
        .orElse(publicTenant));
    }
    return Optional.ofNullable(publicTenant);
  }
}
