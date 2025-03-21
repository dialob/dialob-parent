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
package io.dialob.tenant;

import io.dialob.security.tenant.CurrentTenant;
import io.dialob.security.tenant.FixedCurrentTenant;
import io.dialob.security.tenant.TenantContextHolderCurrentTenant;
import io.dialob.settings.DialobSettings;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class DialobTenantConfigurationAutoConfiguration {

  @Configuration(proxyBeanMethods = false)
  @ConditionalOnProperty(prefix = "dialob.tenant", name = "mode", havingValue = "FIXED", matchIfMissing = true)
  public static class FixedTenantConfiguration {
    @Bean
    public CurrentTenant currentTenant(DialobSettings dialobSettings) {
      return new FixedCurrentTenant(dialobSettings.getTenant().getFixedId());
    }
  }

  @Configuration(proxyBeanMethods = false)
  @ConditionalOnProperty(prefix = "dialob.tenant", name = "mode", havingValue = "URL_PARAM")
  public static class RequestParameterScopedTenantConfiguration {
    @Bean
    public CurrentTenant currentTenant() {
      return TenantContextHolderCurrentTenant.INSTANCE;
    }
  }
}
