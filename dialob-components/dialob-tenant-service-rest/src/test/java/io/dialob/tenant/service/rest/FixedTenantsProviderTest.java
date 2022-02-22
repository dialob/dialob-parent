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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import io.dialob.security.tenant.CurrentTenant;
import io.dialob.security.tenant.ImmutableTenant;
import io.dialob.security.tenant.Tenant;

class FixedTenantsProviderTest {

  @Test
  public void shouldReturnCurrentTenantAsTenant() {
    CurrentTenant currentTenant = Mockito.mock(CurrentTenant.class);
    Mockito.when(currentTenant.get()).thenReturn(ImmutableTenant.of("123", Optional.empty()));

    FixedTenantsProvider fixedTenantsProvider = new FixedTenantsProvider(currentTenant);
    List<Tenant> tenants = fixedTenantsProvider.getTenants();

    assertEquals(1, tenants.size());
    assertEquals("123", tenants.get(0).getId());
    assertFalse(tenants.get(0).getName().isPresent());
  }

}
