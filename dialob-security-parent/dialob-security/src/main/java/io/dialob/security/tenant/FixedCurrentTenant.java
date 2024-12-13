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
package io.dialob.security.tenant;

import java.util.Objects;


public class FixedCurrentTenant implements CurrentTenant {

  private final Tenant tenant;

  public FixedCurrentTenant(String tenantId) {
    this(tenantId, null);
  }

  public FixedCurrentTenant(String tenantId, String name) {
    this(Tenant.of(Objects.requireNonNull(tenantId, "tenant id may no be null"), name));
  }

  public FixedCurrentTenant(Tenant tenant) {
    this.tenant = Objects.requireNonNull(tenant, "tenant may no be null");
  }

  @Override
  public Tenant get() {
    return tenant;
  }

  @Override
  public boolean isInTenantScope() {
    return true;
  }
}
