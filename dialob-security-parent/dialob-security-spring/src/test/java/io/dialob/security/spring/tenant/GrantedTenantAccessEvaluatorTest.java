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

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class GrantedTenantAccessEvaluatorTest {

  @AfterEach
  void cleanContext() {
    SecurityContextHolder.clearContext();
  }

  @Test
  void unauthenticatedShouldNotAccessAnyTenant() {
    var evaluator = new GrantedTenantAccessEvaluator();
    Tenant tenant = mock();
    assertFalse(evaluator.doesUserHaveAccessToTenant(tenant));
    verifyNoMoreInteractions(tenant);
  }


  @Test
  void authenticatedShouldNotAccessAnyTenant() {
    Tenant publicTenant = mock();
    Tenant tenant = mock();
    var evaluator = new GrantedTenantAccessEvaluator(publicTenant);
    when(publicTenant.id()).thenReturn("123");
    when(tenant.id()).thenReturn("123");
    assertTrue(evaluator.doesUserHaveAccessToTenant(tenant));

    verify(tenant).id();
    verify(publicTenant).id();
    verifyNoMoreInteractions(tenant);
  }

  @Test
  void checkDoesAuthenticationContainTenantAccess() {
    Tenant tenant = mock();

    SecurityContext context = mock();
    SecurityContextHolder.setContext(context);
    AbstractAuthenticationToken authentication = mock();
    when(context.getAuthentication()).thenReturn(authentication);
    when(authentication.getAuthorities()).thenReturn(List.of(ImmutableTenantGrantedAuthority.of("123", "read")));

    var evaluator = new GrantedTenantAccessEvaluator();
    when(tenant.id()).thenReturn("123");
    assertTrue(evaluator.doesUserHaveAccessToTenant(tenant));

    verify(tenant).id();
    verifyNoMoreInteractions(tenant);
  }



  @Test
  void defaultAccessToAnyTenant() {
    var evaluator = new GrantedTenantAccessEvaluator();
    AbstractAuthenticationToken token = mock();
    assertFalse(evaluator.canAccessAnyTenant(token));
    verifyNoMoreInteractions(token);
  }

  //    ;


}
