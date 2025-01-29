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
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DefaultTenantGrantedAuthorityProviderTest {

  @AfterEach
  void cleanContext() {
    SecurityContextHolder.clearContext();
  }

  @Test
  void shouldNotReturnTenantWhenNoPublicOrSecurityContext() {
    var provider = new DefaultTenantGrantedAuthorityProvider();
    assertTrue(provider.get().isEmpty());
  }

  @Test
  void shouldReturnPublicTenantWhenNoSecurityContext() {
    Tenant tenant = mock();
    var provider = new DefaultTenantGrantedAuthorityProvider(tenant);
    assertTrue(provider.get().isPresent());
    assertSame(tenant, provider.get().get());

    verifyNoMoreInteractions(tenant);
  }

  @Test
  void shouldNotReturnTenantWhenNoPublicOrNoAuthenticationInSecurityContext() {
    SecurityContext context = mock();
    SecurityContextHolder.setContext(context);
    var provider = new DefaultTenantGrantedAuthorityProvider();
    assertTrue(provider.get().isEmpty());

    verify(context).getAuthentication();
    verifyNoMoreInteractions(context);
  }


  @Test
  void shouldNotGiveAccessToAnyTenantIfUserDoesNotHaveAuthorities() {
    SecurityContext context = mock();
    SecurityContextHolder.setContext(context);
    AbstractAuthenticationToken authentication = mock();
    when(context.getAuthentication()).thenReturn(authentication);
    when(authentication.getAuthorities()).thenReturn(List.of());
    var provider = new DefaultTenantGrantedAuthorityProvider();
    assertTrue(provider.get().isEmpty());

    verify(context).getAuthentication();
    verify(authentication).getAuthorities();
    verifyNoMoreInteractions(context, authentication);
  }

  @Test
  void shouldGiveAccessToAnyTenantIfUserDoesNotHaveAuthorities() {
    SecurityContext context = mock();
    SecurityContextHolder.setContext(context);
    AbstractAuthenticationToken authentication = mock();
    when(context.getAuthentication()).thenReturn(authentication);
    when(authentication.getAuthorities()).thenReturn(List.of(ImmutableTenantGrantedAuthority.of("123", "read")));
    var provider = new DefaultTenantGrantedAuthorityProvider();
    Optional<Tenant> optionalTenant = provider.get();
    assertTrue(optionalTenant.isPresent());
    var tenant = optionalTenant.get();
    assertEquals("123", tenant.id());
    assertEquals("read", tenant.name());

    verify(context).getAuthentication();
    verify(authentication).getAuthorities();
    verifyNoMoreInteractions(context, authentication);
  }


}
