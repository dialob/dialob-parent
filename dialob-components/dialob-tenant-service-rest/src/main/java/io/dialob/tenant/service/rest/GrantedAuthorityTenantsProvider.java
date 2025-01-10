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
package io.dialob.tenant.service.rest;

import edu.umd.cs.findbugs.annotations.NonNull;
import io.dialob.security.spring.tenant.TenantGrantedAuthority;
import io.dialob.security.tenant.Tenant;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

class GrantedAuthorityTenantsProvider implements TenantsProvider {

  @NonNull
  public List<Tenant> getTenants() {
    SecurityContext securityContext = SecurityContextHolder.getContext();
    Authentication authentication = securityContext.getAuthentication();
    List<Tenant> tenants = Collections.emptyList();
    if (authentication instanceof AbstractAuthenticationToken) {
      AbstractAuthenticationToken token = (AbstractAuthenticationToken) authentication;
      tenants = token.getAuthorities().stream()
        .filter(a -> a instanceof TenantGrantedAuthority)
        .map(a -> (TenantGrantedAuthority) a)
        .map(a -> Tenant.of(a.getTenantId(), a.getAuthority()))
        .collect(Collectors.toList());
    }
    return tenants;
  }

}
