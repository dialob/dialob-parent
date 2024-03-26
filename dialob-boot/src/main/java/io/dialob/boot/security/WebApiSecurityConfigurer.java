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
package io.dialob.boot.security;

import io.dialob.security.spring.AuthenticationStrategy;
import io.dialob.security.spring.tenant.TenantAccessEvaluator;
import io.dialob.settings.DialobSettings;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import edu.umd.cs.findbugs.annotations.NonNull;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@Profile("ui")
public class WebApiSecurityConfigurer extends AbstractApiSecurityConfigurer {

  private final DialobSettings settings;

  public WebApiSecurityConfigurer(@NonNull DialobSettings settings,
                                  @NonNull TenantAccessEvaluator tenantPermissionEvaluator, AuthenticationStrategy authenticationStrategy) {
    super(settings.getApi().getContextPath(), tenantPermissionEvaluator, authenticationStrategy);
    this.settings = settings;
  }

  @Override
  protected HttpSecurity configureCors(HttpSecurity http) {
    return settings.getApi().getCors().toCorsConfiguration().map(corsConfiguration -> {
      try {
        return http.cors(customizer -> customizer.configurationSource(request -> corsConfiguration));
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }).orElse(http);
  }

  @Bean
  @Order(125)
  SecurityFilterChain webApiFilterChain(HttpSecurity http) throws Exception {
    return super.filterChain(http);
  }

}
