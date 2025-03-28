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
import io.dialob.common.Permissions;
import io.dialob.security.spring.AuthenticationStrategy;
import io.dialob.security.spring.tenant.TenantAccessEvaluator;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.util.matcher.NegatedRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher;


public abstract class AbstractApiSecurityConfigurer extends AbstractWebSecurityConfigurer {

  public AbstractApiSecurityConfigurer(String contextPath,
                                       TenantAccessEvaluator tenantPermissionEvaluator,
                                       @NonNull AuthenticationStrategy authenticationStrategy) {
    super(contextPath, tenantPermissionEvaluator, authenticationStrategy);
  }

  protected HttpSecurity configurePermissions(@NonNull HttpSecurity http) throws Exception {
    // @formatter:off
    return http
      .securityMatcher(requestMatcher())
      .authorizeHttpRequests(registry ->
        registry.requestMatchers(antMatcher(HttpMethod.GET, getContextPath() + "/questionnaires/**")).hasAuthority(Permissions.QUESTIONNAIRES_GET)
          .requestMatchers(antMatcher(HttpMethod.HEAD,  getContextPath() + "/questionnaires/**")).hasAuthority(Permissions.QUESTIONNAIRES_GET)
          .requestMatchers(antMatcher(HttpMethod.POST, getContextPath() + "/questionnaires/**")).hasAuthority(Permissions.QUESTIONNAIRES_POST)
          .requestMatchers(antMatcher(HttpMethod.PUT, getContextPath() + "/questionnaires/**")).hasAuthority(Permissions.QUESTIONNAIRES_PUT)
          .requestMatchers(antMatcher(HttpMethod.DELETE, getContextPath() + "/questionnaires/**")).hasAuthority(Permissions.QUESTIONNAIRES_DELETE)
          .requestMatchers(antMatcher(HttpMethod.GET,  getContextPath() + "/forms/**")).hasAuthority(Permissions.FORMS_GET)
          .requestMatchers(antMatcher(HttpMethod.HEAD,  getContextPath() + "/forms/**")).hasAuthority(Permissions.FORMS_GET)
          .requestMatchers(antMatcher(HttpMethod.POST,  getContextPath() + "/forms/**")).hasAuthority(Permissions.FORMS_POST)
          .requestMatchers(antMatcher(HttpMethod.PUT,  getContextPath() + "/forms/**")).hasAuthority(Permissions.FORMS_PUT)
          .requestMatchers(antMatcher(HttpMethod.DELETE,  getContextPath() + "/forms/**")).hasAuthority(Permissions.FORMS_DELETE)
          .requestMatchers(antMatcher(HttpMethod.GET,  getContextPath() + "/tags/**")).hasAuthority(Permissions.FORMS_GET)
          .requestMatchers(antMatcher(HttpMethod.GET,  getContextPath() + "/tenants/**")).authenticated()
          .anyRequest().denyAll());
    // @formatter:on
  }


  @NonNull
  @Override
  protected RequestMatcher getTenantRequiredMatcher() {
    return new NegatedRequestMatcher(antMatcher(getContextPath() + "/tenants/**"));
  }
}
