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
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
import org.springframework.security.web.util.matcher.NegatedRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;


public abstract class AbstractApiSecurityConfigurer extends AbstractWebSecurityConfigurer {

  public AbstractApiSecurityConfigurer(String contextPath,
                                       TenantAccessEvaluator tenantPermissionEvaluator,
                                       @NonNull AuthenticationStrategy authenticationStrategy) {
    super(contextPath, tenantPermissionEvaluator, authenticationStrategy);
  }

  protected HttpSecurity configurePermissions(@NonNull HttpSecurity http) throws Exception {
    // @formatter:off
    var path = PathPatternRequestMatcher.withDefaults();
    return http
      .securityMatcher(requestMatcher())
      .authorizeHttpRequests(registry ->
        registry.requestMatchers(path.matcher(HttpMethod.GET, getContextPath() + "/questionnaires/**")).hasAuthority(Permissions.QUESTIONNAIRES_GET)
          .requestMatchers(path.matcher(HttpMethod.HEAD,  getContextPath() + "/questionnaires/**")).hasAuthority(Permissions.QUESTIONNAIRES_GET)
          .requestMatchers(path.matcher(HttpMethod.POST, getContextPath() + "/questionnaires/**")).hasAuthority(Permissions.QUESTIONNAIRES_POST)
          .requestMatchers(path.matcher(HttpMethod.PUT, getContextPath() + "/questionnaires/**")).hasAuthority(Permissions.QUESTIONNAIRES_PUT)
          .requestMatchers(path.matcher(HttpMethod.DELETE, getContextPath() + "/questionnaires/**")).hasAuthority(Permissions.QUESTIONNAIRES_DELETE)
          .requestMatchers(path.matcher(HttpMethod.GET,  getContextPath() + "/forms/**")).hasAuthority(Permissions.FORMS_GET)
          .requestMatchers(path.matcher(HttpMethod.HEAD,  getContextPath() + "/forms/**")).hasAuthority(Permissions.FORMS_GET)
          .requestMatchers(path.matcher(HttpMethod.POST,  getContextPath() + "/forms/**")).hasAuthority(Permissions.FORMS_POST)
          .requestMatchers(path.matcher(HttpMethod.PUT,  getContextPath() + "/forms/**")).hasAuthority(Permissions.FORMS_PUT)
          .requestMatchers(path.matcher(HttpMethod.DELETE,  getContextPath() + "/forms/**")).hasAuthority(Permissions.FORMS_DELETE)
          .requestMatchers(path.matcher(HttpMethod.GET,  getContextPath() + "/tags/**")).hasAuthority(Permissions.FORMS_GET)
          .requestMatchers(path.matcher(HttpMethod.GET,  getContextPath() + "/tenants/**")).authenticated()
          .anyRequest().denyAll());
    // @formatter:on
  }


  @NonNull
  @Override
  protected RequestMatcher getTenantRequiredMatcher() {
    return new NegatedRequestMatcher(PathPatternRequestMatcher.withDefaults().matcher(getContextPath() + "/tenants/**"));
  }
}
