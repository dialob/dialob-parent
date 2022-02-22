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

import org.springframework.core.Ordered;
import org.springframework.http.HttpMethod;
import org.springframework.lang.NonNull;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.NegatedRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.cors.CorsConfiguration;

import io.dialob.common.Permissions;
import io.dialob.security.spring.AuthenticationStrategy;
import io.dialob.security.spring.tenant.TenantAccessEvaluator;


public abstract class AbstractApiSecurityConfigurer extends AbstractWebSecurityConfigurer implements Ordered {

  public static final CorsConfiguration PERMIT_ALL = new CorsConfiguration().applyPermitDefaultValues();

  public static final CorsConfiguration ALLOW_SAME_ORIGIN = new CorsConfiguration();

  public static final RequestMatcher SESSION_EXISTS_MATCHER = request -> request.getSession(false) != null;

  public AbstractApiSecurityConfigurer(String contextPath,
                                       TenantAccessEvaluator tenantPermissionEvaluator,
                                       @NonNull AuthenticationStrategy authenticationStrategy) {
    super(contextPath, tenantPermissionEvaluator, authenticationStrategy);
  }

  protected HttpSecurity configurePermissions(HttpSecurity http) throws Exception {
    // @formatter:off
    return http
      .requestMatcher(requestMatcher())
      .authorizeRequests()
        .antMatchers(HttpMethod.GET, getContextPath() + "/questionnaires/**").hasAuthority(Permissions.QUESTIONNAIRES_GET)
        .antMatchers(HttpMethod.HEAD,  getContextPath() + "/questionnaires/**").hasAuthority(Permissions.QUESTIONNAIRES_GET)
        .antMatchers(HttpMethod.POST, getContextPath() + "/questionnaires/**").hasAuthority(Permissions.QUESTIONNAIRES_POST)
        .antMatchers(HttpMethod.PUT, getContextPath() + "/questionnaires/**").hasAuthority(Permissions.QUESTIONNAIRES_PUT)
        .antMatchers(HttpMethod.DELETE, getContextPath() + "/questionnaires/**").hasAuthority(Permissions.QUESTIONNAIRES_DELETE)
        .antMatchers(HttpMethod.GET,  getContextPath() + "/forms/**").hasAuthority(Permissions.FORMS_GET)
        .antMatchers(HttpMethod.HEAD,  getContextPath() + "/forms/**").hasAuthority(Permissions.FORMS_GET)
        .antMatchers(HttpMethod.POST,  getContextPath() + "/forms/**").hasAuthority(Permissions.FORMS_POST)
        .antMatchers(HttpMethod.PUT,  getContextPath() + "/forms/**").hasAuthority(Permissions.FORMS_PUT)
        .antMatchers(HttpMethod.DELETE,  getContextPath() + "/forms/**").hasAuthority(Permissions.FORMS_DELETE)
        .antMatchers(HttpMethod.GET,  getContextPath() + "/tenants/**").authenticated()
        .anyRequest().denyAll()
        .and();
    // @formatter:on
  }


  @NonNull
  @Override
  protected RequestMatcher getTenantRequiredMatcher() {
    return new NegatedRequestMatcher(new AntPathRequestMatcher(getContextPath() + "/tenants/**"));
  }
}
