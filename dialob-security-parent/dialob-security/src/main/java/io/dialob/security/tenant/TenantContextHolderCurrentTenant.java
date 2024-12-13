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

import edu.umd.cs.findbugs.annotations.NonNull;
import org.slf4j.MDC;

import java.util.Objects;

public class TenantContextHolderCurrentTenant implements CurrentTenant {

  private static final ThreadLocal<Tenant> TENANT_THREAD_LOCAL = new ThreadLocal<>();

  public static final CurrentTenant INSTANCE = new TenantContextHolderCurrentTenant();

  protected TenantContextHolderCurrentTenant() {}

  public static void runInTenantContext(@NonNull Tenant tenant, @NonNull Runnable runnable) {
    Tenant originalTenant = TENANT_THREAD_LOCAL.get();
    TENANT_THREAD_LOCAL.set(Objects.requireNonNull(tenant));
    MDC.put(LoggingContextKeys.MDC_TENANT_ID_KEY, tenant.id());
    try {
      runnable.run();
    } finally {
      if (originalTenant == null) {
        MDC.remove(LoggingContextKeys.MDC_TENANT_ID_KEY);
      } else {
        MDC.put(LoggingContextKeys.MDC_TENANT_ID_KEY, originalTenant.id());
      }
      TENANT_THREAD_LOCAL.set(originalTenant);
    }
  }

  public static void setTenant(Tenant tenant) {
    MDC.put(LoggingContextKeys.MDC_TENANT_ID_KEY, tenant.id());
    TENANT_THREAD_LOCAL.set(tenant);
  }

  public static void removeTenant() {
    TENANT_THREAD_LOCAL.remove();
    MDC.remove(LoggingContextKeys.MDC_TENANT_ID_KEY);
  }

  @Override
  public Tenant get() {
    Tenant tenant = TENANT_THREAD_LOCAL.get();
    if (tenant == null) {
      throw new NoTenantInScopeException();
    }
    return tenant;
  }

  @Override
  public boolean isInTenantScope() {
    return TENANT_THREAD_LOCAL.get() != null;
  }
}
