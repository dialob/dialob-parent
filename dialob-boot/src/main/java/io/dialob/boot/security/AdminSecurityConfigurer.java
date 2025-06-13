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
import io.dialob.boot.settings.AdminApplicationSettings;
import io.dialob.common.Permissions;
import io.dialob.security.spring.AuthenticationStrategy;
import io.dialob.security.spring.tenant.TenantAccessEvaluator;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;

@Configuration(proxyBeanMethods = false)
@Profile("ui")
public class AdminSecurityConfigurer extends WebUISecurityConfigurer {

  public AdminSecurityConfigurer(@NonNull AdminApplicationSettings settings,
                                 @NonNull TenantAccessEvaluator tenantPermissionEvaluator,
                                 @NonNull AuthenticationStrategy authenticationStrategy) {
    super(settings.getContextPath(), tenantPermissionEvaluator, authenticationStrategy);
  }

  protected HttpSecurity configurePermissions(HttpSecurity http) throws Exception {
    // @formatter:off
    var path = PathPatternRequestMatcher.withDefaults();
    var contextPath = StringUtils.prependIfMissing(StringUtils.removeEnd(getContextPath(), "/"), "/");
    return http
      .securityMatcher(requestMatcher())
      .authorizeHttpRequests(customizer -> customizer
        .requestMatchers(path.matcher(HttpMethod.GET, contextPath + "/swagger/**")).permitAll()
        .requestMatchers(path.matcher(HttpMethod.GET, contextPath + "/swagger-resources")).permitAll()
        .requestMatchers(path.matcher(HttpMethod.GET, contextPath + "/swagger-resources/**")).permitAll()
        .requestMatchers(path.matcher(HttpMethod.GET, contextPath + "/swagger-ui/**")).permitAll()
        .requestMatchers(path.matcher(HttpMethod.GET, contextPath + "/webjars/**")).permitAll()
        .requestMatchers(path.matcher(HttpMethod.GET,  "/_uuids")).hasAnyAuthority(Permissions.QUESTIONNAIRES_POST, Permissions.FORMS_POST)
        .requestMatchers(path.matcher(HttpMethod.GET,contextPath + "**")).hasAuthority(Permissions.MANAGER_VIEW)
        .anyRequest().denyAll());
    // @formatter:on
  }

  @Bean
  @Order(SecurityConfiguration.ADMIN_CHAIN_ORDER)
  SecurityFilterChain adminFilterChain(HttpSecurity http) throws Exception {
    return super.filterChain(http);
  }

}
