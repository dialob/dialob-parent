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
package io.dialob.function;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.dialob.rule.parser.function.FunctionRegistry;
import io.dialob.security.tenant.CurrentTenant;

class DialobFunctionAutoConfigurationTest {

  @Configuration(proxyBeanMethods = false)
  static class TestConfiguration {

    @Bean
    public CurrentTenant currentTenant() {
      return Mockito.mock(CurrentTenant.class);
    }

  }


  @Test
  public void shouldSetupFunctionRegistryBean() {

    new ApplicationContextRunner()
      .withConfiguration(AutoConfigurations.of(DialobFunctionAutoConfiguration.class))
      .withUserConfiguration(TestConfiguration.class)
      .run(context -> {
        Assertions.assertThat(context)
          .hasSingleBean(FunctionRegistry.class);
      });
  }
}
