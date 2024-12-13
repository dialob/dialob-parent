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
package io.dialob.tenant.service.rest;

import io.dialob.security.spring.tenant.ImmutableTenantGrantedAuthority;
import io.dialob.security.tenant.Tenant;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GrantedAuthorityTenantsProviderTest {

  @Test
  public void shouldReturnEmptyListOfTenantIfUnauthenticated() {

    SecurityContextHolder.createEmptyContext();

    TenantsProvider tenantsProvider = new GrantedAuthorityTenantsProvider();
    List<Tenant> tenants = tenantsProvider.getTenants();

    assertTrue(tenants.isEmpty());
  }

  @Test
  public void shouldReturnListOfTenantsFromAuthorizations() {

    SecurityContextHolder.setContext(new SecurityContextImpl(new TestingAuthenticationToken("","", Arrays.asList(
      ImmutableTenantGrantedAuthority.of("12312", "aa")
    ))));

    TenantsProvider tenantsProvider = new GrantedAuthorityTenantsProvider();
    List<Tenant> tenants = tenantsProvider.getTenants();

    assertFalse(tenants.isEmpty());
    assertEquals("12312", tenants.get(0).id());
  }

}
