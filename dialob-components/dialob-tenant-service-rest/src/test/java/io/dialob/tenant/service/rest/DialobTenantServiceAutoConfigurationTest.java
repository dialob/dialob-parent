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
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.assertj.core.api.Assertions.assertThat;

class DialobTenantServiceAutoConfigurationTest {

  @Configuration(proxyBeanMethods = false)
  public static class TestConfiguration {
    @Bean
    public CurrentTenant currentTenant() {
      return Mockito.mock(CurrentTenant.class);
    }
  }

  @Test
  public void whenSecurityIsDisabledTenantsProviderIsFixed() {
    new ApplicationContextRunner()
      .withPropertyValues("dialob.security.enabled=false")
      .withUserConfiguration(TestConfiguration.class)
      .withConfiguration(AutoConfigurations.of(DialobTenantServiceAutoConfiguration.class))
      .run(context -> {
        assertThat(context)
          .getBean(TenantsProvider.class)
          .isInstanceOf(FixedTenantsProvider.class);
      });
  }

  @Test
  public void whenSecurityIsEnabledTenantsProviderIsFromSecurityContext() {
    new ApplicationContextRunner()
      .withPropertyValues("dialob.security.enabled=true")
      .withConfiguration(AutoConfigurations.of(DialobTenantServiceAutoConfiguration.class))
      .run(context -> {
        assertThat(context)
          .getBean(TenantsProvider.class)
          .isInstanceOf(GrantedAuthorityTenantsProvider.class);
      });
  }

  @Test
  public void whenSecurityIsEnabledTenantsProviderIsFixedIfRequested() {
    new ApplicationContextRunner()
      .withPropertyValues("dialob.security.enabled=true")
      .withPropertyValues("dialob.tenant.mode=FIXED")
      .withUserConfiguration(TestConfiguration.class)
      .withConfiguration(AutoConfigurations.of(DialobTenantServiceAutoConfiguration.class))
      .run(context -> {
        assertThat(context)
          .getBean(TenantsProvider.class)
          .isInstanceOf(FixedTenantsProvider.class);
      });
  }


}
