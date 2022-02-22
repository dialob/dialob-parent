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

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpMethod;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.DefaultAuthenticationEventPublisher;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

import io.dialob.common.Permissions;
import io.dialob.security.spring.AuthenticationStrategy;
import io.dialob.security.spring.tenant.TenantAccessEvaluator;

public class AdminSecurityConfigurer extends WebUISecurityConfigurer {

  private final ApplicationEventPublisher applicationEventPublisher;

  public AdminSecurityConfigurer(@NonNull String contextPath,
                                 @NonNull ApplicationEventPublisher applicationEventPublisher,
                                 @NonNull TenantAccessEvaluator tenantPermissionEvaluator,
                                 @NonNull AuthenticationStrategy authenticationStrategy) {
    super(contextPath, tenantPermissionEvaluator, authenticationStrategy);
    this.applicationEventPublisher = applicationEventPublisher;
  }

  protected HttpSecurity configurePermissions(HttpSecurity http) throws Exception {
    // @formatter:off
    String contextPath = getContextPath();
    contextPath = StringUtils.removeEnd(contextPath, "/");
    return http
      .requestMatcher(requestMatcher())
      .authorizeRequests()
        .antMatchers(HttpMethod.GET,
          contextPath + "/swagger/**",
          contextPath + "/swagger-resources",
          contextPath + "/swagger-resources/**",
          contextPath + "/swagger-ui/**",
          contextPath + "/webjars/**").permitAll()
        .antMatchers(HttpMethod.GET,  "/_uuids").hasAnyAuthority(Permissions.QUESTIONNAIRES_POST, Permissions.FORMS_POST)
        .antMatchers(HttpMethod.GET,contextPath + "**").hasAuthority(Permissions.MANAGER_VIEW)
        .anyRequest().denyAll()
        .and();
    // @formatter:on
  }

  @Override
  protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    super.configure(auth);
    auth.authenticationEventPublisher(new DefaultAuthenticationEventPublisher(applicationEventPublisher));
  }
}
