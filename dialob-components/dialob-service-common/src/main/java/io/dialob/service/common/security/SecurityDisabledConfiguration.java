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
package io.dialob.service.common.security;

import edu.umd.cs.findbugs.annotations.NonNull;
import io.dialob.security.spring.tenant.DefaultTenantSupplier;
import io.dialob.security.spring.tenant.RequestParameterTenantScopeFilter;
import io.dialob.security.spring.tenant.TenantAccessEvaluator;
import io.dialob.security.tenant.ImmutableTenant;
import io.dialob.settings.DialobSettings;
import io.dialob.settings.DialobSettings.TenantSettings;
import io.dialob.settings.DialobSettings.TenantSettings.Mode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.ExceptionTranslationFilter;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.cors.CorsConfiguration;

import java.util.Arrays;
import java.util.Optional;

@Configuration(proxyBeanMethods = false)
@Slf4j
@ConditionalOnProperty(prefix = "dialob.security", name = "enabled", havingValue = "false")
public class SecurityDisabledConfiguration {

  private static final CorsConfiguration PERMIT_ALL_CORS;

  static {
    PERMIT_ALL_CORS = new CorsConfiguration();
    PERMIT_ALL_CORS.setAllowedMethods(Arrays.asList("GET","HEAD","POST","PUT","DELETE"));
    PERMIT_ALL_CORS.applyPermitDefaultValues();
  }

  private final TenantSettings tenantSettings;

  public SecurityDisabledConfiguration(DialobSettings dialobSettings) {
    this.tenantSettings = dialobSettings.getTenant();
  }

  @Bean
  SecurityFilterChain filterChain(HttpSecurity http, TenantAccessEvaluator tenantPermissionEvaluator) throws Exception {
    LOGGER.warn("Security disabled!");
    http.securityMatcher("/**")
        .authorizeHttpRequests(customizer -> customizer.anyRequest().permitAll())
        .csrf(AbstractHttpConfigurer::disable)
        .cors(customizer -> customizer.configurationSource(request -> PERMIT_ALL_CORS))
        .headers(customizer -> customizer.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable));
    return configureRequestParameterTenantScopeFilter(http, tenantPermissionEvaluator).build();
  }

  protected HttpSecurity configureRequestParameterTenantScopeFilter(HttpSecurity http, TenantAccessEvaluator tenantPermissionEvaluator) {
    // @formatter:off
    getRequestParameterTenantScopeFilter(tenantPermissionEvaluator)
      .ifPresent(requestParameterTenantScopeFilter -> http.addFilterAfter(requestParameterTenantScopeFilter, ExceptionTranslationFilter.class));
    return http;
    // @formatter:on
  }

  @NonNull
  protected Optional<RequestParameterTenantScopeFilter> getRequestParameterTenantScopeFilter(TenantAccessEvaluator tenantPermissionEvaluator) {
    final RequestParameterTenantScopeFilter requestParameterTenantScopeFilter = new RequestParameterTenantScopeFilter(
      tenantPermissionEvaluator,
      getDefaultTenantSupplier()
    );
    requestParameterTenantScopeFilter.setTenantRequiredMatcher(getTenantRequiredMatcher());
    requestParameterTenantScopeFilter.setParameterName(tenantSettings.getUrlParameter());
    return Optional.of(requestParameterTenantScopeFilter);
  }

  private DefaultTenantSupplier getDefaultTenantSupplier() {
    if (StringUtils.isEmpty(tenantSettings.getFixedId())) {
      return Optional::empty;
    }
    return () -> Optional.of(ImmutableTenant.of(tenantSettings.getFixedId(), Optional.empty()));
  }

  @NonNull
  protected RequestMatcher getTenantRequiredMatcher() {
    return request -> tenantSettings.getMode() == Mode.URL_PARAM;
  }

  @Bean
  TenantAccessEvaluator tenantPermissionEvaluator() {
    return tenant -> true;
  }
}

