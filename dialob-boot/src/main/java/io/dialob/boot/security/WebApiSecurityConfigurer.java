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

import org.springframework.lang.NonNull;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

import io.dialob.security.spring.AuthenticationStrategy;
import io.dialob.security.spring.tenant.TenantAccessEvaluator;
import io.dialob.settings.DialobSettings;

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
        return http.cors().configurationSource(request -> corsConfiguration).and();
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }).orElse(http);
  }

}
