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

import java.util.Optional;

import org.springframework.lang.NonNull;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

import io.dialob.security.spring.AuthenticationStrategy;
import io.dialob.security.spring.tenant.RequestParameterTenantScopeFilter;
import io.dialob.security.spring.tenant.TenantAccessEvaluator;

public class QuestionnaireSecurityConfigurer extends WebUISecurityConfigurer {

  public QuestionnaireSecurityConfigurer(@NonNull String contextPath,
                                         @NonNull TenantAccessEvaluator tenantPermissionEvaluator,
                                         @NonNull AuthenticationStrategy authenticationStrategy) {
    super(contextPath, tenantPermissionEvaluator, authenticationStrategy);
  }

  protected HttpSecurity configurePermissions(HttpSecurity http) throws Exception {
    // @formatter:off
    return http
      .securityMatcher(requestMatcher())
      .authorizeHttpRequests()
        .anyRequest().permitAll()
        .and();
    // @formatter:on
  }

  @NonNull
  @Override
  protected Optional<RequestParameterTenantScopeFilter> getRequestParameterTenantScopeFilter() {
    return Optional.empty();
  }
}
