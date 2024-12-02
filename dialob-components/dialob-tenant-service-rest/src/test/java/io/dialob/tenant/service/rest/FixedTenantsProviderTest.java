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

import io.dialob.security.tenant.CurrentTenant;
import io.dialob.security.tenant.Tenant;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class FixedTenantsProviderTest {

  @Test
  public void shouldReturnCurrentTenantAsTenant() {
    CurrentTenant currentTenant = Mockito.mock(CurrentTenant.class);
    Mockito.when(currentTenant.get()).thenReturn(Tenant.of("123"));

    FixedTenantsProvider fixedTenantsProvider = new FixedTenantsProvider(currentTenant);
    List<Tenant> tenants = fixedTenantsProvider.getTenants();

    assertEquals(1, tenants.size());
    assertEquals("123", tenants.get(0).id());
    assertNull(tenants.get(0).name());
  }

}
