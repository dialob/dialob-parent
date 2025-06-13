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
package io.dialob.boot.security;

import io.dialob.security.spring.AuthenticationStrategy;
import io.dialob.security.spring.tenant.TenantAccessEvaluator;
import io.dialob.settings.DialobSettings;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.AdditionalAnswers;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.mock.web.MockServletContext;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AbstractApiSecurityConfigurerTest {

  @EnableWebSecurity
  static class MockEnableWebSecurityConfiguration {
  }

  @Test
  void securityConfigurationMapsToQuestionnaires() {
    new ApplicationContextRunner()
      .withBean(TenantAccessEvaluator.class, () -> mock(TenantAccessEvaluator.class))
      .withBean(AuthenticationStrategy.class, () -> {
        var authenticationStrategy = mock(AuthenticationStrategy.class);
        try {
          when(authenticationStrategy.configureAuthentication(any())).thenAnswer(AdditionalAnswers.returnsFirstArg());
        } catch (Exception e) {
          throw new RuntimeException(e);
        }
        return authenticationStrategy;
      })
      .withUserConfiguration(MockEnableWebSecurityConfiguration.class)
      .run(context -> {
        var http = context.getBean(HttpSecurity.class);
        var configurer = new AbstractApiSecurityConfigurer("/api", context.getBean(TenantAccessEvaluator.class), context.getBean(AuthenticationStrategy.class), DialobSettings.TenantSettings.Mode.URL_PARAM) {
        };
        var chain = configurer.filterChain(http);
        Assertions.assertEquals(13, chain.getFilters().size());

        MockServletContext servletContext = new MockServletContext();

        assertTrue(chain.matches(MockMvcRequestBuilders.get("/api").buildRequest(servletContext)));
        assertFalse(chain.matches(MockMvcRequestBuilders.get("/api2").buildRequest(servletContext)));
      });
  }

  @Test
  void shouldNotRequireTenantUrlParameterWhenTenantModeIsNotUrlParam() {
    DialobSettings.TenantSettings.Mode tenantMode = DialobSettings.TenantSettings.Mode.FIXED;
    AbstractApiSecurityConfigurer configurer = new AbstractApiSecurityConfigurer("/api", mock(TenantAccessEvaluator.class), mock(AuthenticationStrategy.class), tenantMode) {
      @Override
      protected HttpSecurity configurePermissions(HttpSecurity http) throws Exception {
        return http;
      }
    };
    var matcher = configurer.getTenantRequiredMatcher();
    assertFalse(matcher.matches(MockMvcRequestBuilders.get("/api/something").buildRequest(new MockServletContext())));
  }

  @Test
  void shouldRequireTenantUrlParameterWhenTenantModeIsUrlParam() {
    DialobSettings.TenantSettings.Mode tenantMode = DialobSettings.TenantSettings.Mode.URL_PARAM;
    AbstractApiSecurityConfigurer configurer = new AbstractApiSecurityConfigurer("/api", mock(TenantAccessEvaluator.class), mock(AuthenticationStrategy.class), tenantMode) {
      @Override
      protected HttpSecurity configurePermissions(HttpSecurity http) throws Exception {
        return http;
      }
    };
    var matcher = configurer.getTenantRequiredMatcher();
    assertTrue(matcher.matches(MockMvcRequestBuilders.get("/api/something").buildRequest(new MockServletContext())));
  }
}
