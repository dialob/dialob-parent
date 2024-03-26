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
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration(proxyBeanMethods = false)
@Import(TenantsRestController.class)
public class DialobTenantServiceAutoConfiguration {

  @Configuration(proxyBeanMethods = false)
  @ConditionalOnProperty(name = "dialob.security.enabled", havingValue = "true")
  static class DialobTenantServiceWithSecurityConfiguration {

    @ConditionalOnProperty(prefix = "dialob.tenant", name = "mode", havingValue = "URL_PARAM", matchIfMissing = true)
    @Bean
    public TenantsProvider grantedAuthorityTenantService() {
      return new GrantedAuthorityTenantsProvider();
    }

    @ConditionalOnProperty(prefix = "dialob.tenant", name = "mode", havingValue = "FIXED")
    @Bean
    public TenantsProvider fixedTenantService(CurrentTenant currentTenant) {
      return new FixedTenantsProvider(currentTenant);
    }
  }

  @Configuration(proxyBeanMethods = false)
  @ConditionalOnProperty(name = "dialob.security.enabled", havingValue = "false", matchIfMissing = true)
  static class DialobTenantServiceWithoutSecurityConfiguration {

    @Bean
    public TenantsProvider fixedTenantService(CurrentTenant currentTenant) {
      return new FixedTenantsProvider(currentTenant);
    }
  }
}
