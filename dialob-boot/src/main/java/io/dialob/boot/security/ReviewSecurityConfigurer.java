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

import edu.umd.cs.findbugs.annotations.NonNull;
import io.dialob.boot.settings.ReviewApplicationSettings;
import io.dialob.security.spring.AuthenticationStrategy;
import io.dialob.security.spring.tenant.TenantAccessEvaluator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@Profile("ui")
public class ReviewSecurityConfigurer extends WebUISecurityConfigurer {

  public ReviewSecurityConfigurer(@NonNull ReviewApplicationSettings settings,
                                  @NonNull TenantAccessEvaluator tenantPermissionEvaluator,
                                  @NonNull AuthenticationStrategy authenticationStrategy) {
    super(settings.getContextPath(), tenantPermissionEvaluator, authenticationStrategy);
  }

  protected HttpSecurity configurePermissions(HttpSecurity http) throws Exception {
    // @formatter:off
    return http
      .securityMatcher(requestMatcher())
      .authorizeHttpRequests(customizer -> customizer
        .anyRequest().permitAll());
    // @formatter:on
  }

  @Bean
  @Order(150)
  SecurityFilterChain reviewFilterChain(HttpSecurity http) throws Exception {
    return super.filterChain(http);
  }

}
