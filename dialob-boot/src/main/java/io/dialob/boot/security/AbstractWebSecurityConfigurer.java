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

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.Ordered;
import org.springframework.lang.NonNull;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.access.ExceptionTranslationFilter;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import io.dialob.security.aws.elb.ElbAuthenticationStrategy;
import io.dialob.security.spring.AuthenticationStrategy;
import io.dialob.security.spring.filter.MDCPrincipalFilter;
import io.dialob.security.spring.tenant.DefaultTenantGrantedAuthorityProvider;
import io.dialob.security.spring.tenant.RequestParameterTenantScopeFilter;
import io.dialob.security.spring.tenant.TenantAccessEvaluator;

public abstract class AbstractWebSecurityConfigurer extends WebSecurityConfigurerAdapter implements Ordered {

  private static final MDCPrincipalFilter MDC_PRINCIPAL_FILTER = new MDCPrincipalFilter();

  private int order = Ordered.LOWEST_PRECEDENCE;

  private final String contextPath;

  private final TenantAccessEvaluator tenantPermissionEvaluator;

  private final AuthenticationStrategy authenticationStrategy;

  private final CookieCsrfTokenRepository csrfTokenRepository = new CookieCsrfTokenRepository();

  public AbstractWebSecurityConfigurer(String contextPath,
                                       TenantAccessEvaluator tenantPermissionEvaluator,
                                       AuthenticationStrategy authenticationStrategy) {
    this.contextPath = StringUtils.defaultString(contextPath, "/");
    this.tenantPermissionEvaluator = tenantPermissionEvaluator;
    this.authenticationStrategy = authenticationStrategy;
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    // @formatter:off
    http = configurePermissions(http);
    http = configureLogout(http);
    http = configureFrameOptions(http);
    http = configureAuthenticationManager(http);
    http = configureCsrf(http);
    http = configureAuthentication(http);
    http = configureCors(http);
    http = configureRequestParameterTenantScopeFilter(http);
    http = configureMDCPrincipalFilter(http);
    // @formatter:on
  }

  protected HttpSecurity configureAuthenticationManager(HttpSecurity http) {
    return http;
  }

  protected abstract HttpSecurity configurePermissions(HttpSecurity http) throws Exception;

  protected RequestMatcher requestMatcher() {
    return new AntPathRequestMatcher(StringUtils.appendIfMissing(getContextPath(), "/") + "**");
  }

  protected HttpSecurity configureMDCPrincipalFilter(HttpSecurity http) throws Exception {
    // @formatter:off
    return http.addFilterAfter(MDC_PRINCIPAL_FILTER, AnonymousAuthenticationFilter.class);
    // @formatter:on
  }

  protected HttpSecurity configureRequestParameterTenantScopeFilter(HttpSecurity http) {
    // @formatter:off
    getRequestParameterTenantScopeFilter()
      .ifPresent(requestParameterTenantScopeFilter -> http.addFilterAfter(requestParameterTenantScopeFilter, ExceptionTranslationFilter.class));
    return http;
    // @formatter:on
  }

  protected HttpSecurity configureCsrf(HttpSecurity http) throws Exception {
    // @formatter:off
    if (authenticationStrategy instanceof ElbAuthenticationStrategy) {
      http = http.csrf()
        .csrfTokenRepository(csrfTokenRepository)
        // {@link CsrfAuthenticationStrategy} resets csrf token on each onauthentication event to prevent cross session
        // token sharing. However when preauthentication filter is used, onauthentication is triggered on every
        // request. This causes CSRF token to change for each request and client cannot follow this. Therefore we'll
        // accept token to be shared cross sessions in single browser window, than not to have CSRF protection at all.
        .sessionAuthenticationStrategy((authentication, request, response) -> {})
        .and();
    }
    return http;
    // @formatter:on
  }

  protected HttpSecurity configureCors(HttpSecurity http) throws Exception {
    // @formatter:off
    return http;
    // @formatter:on
  }

  protected HttpSecurity configureAuthentication(HttpSecurity http) throws Exception {
    return this.authenticationStrategy.configureAuthentication(http, authenticationManager());
  }

  protected HttpSecurity configureFrameOptions(HttpSecurity http) throws Exception {
    // @formatter:off
    return http
      .headers()
      .frameOptions()
      .sameOrigin()
      .and();
    // @formatter:on
  }

  protected HttpSecurity configureLogout(HttpSecurity http) throws Exception {
    // @formatter:off
    return http
      .logout()
      .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
      .logoutSuccessUrl("/")
      .and();
    // @formatter:on
  }

  protected final String getContextPath() {
    return contextPath;
  }

  @Override
  public int getOrder() {
    return order;
  }

  public <T extends AbstractWebSecurityConfigurer> T withOrder(int order) {
    this.order = order;
    return (T) this;
  }


  @NonNull
  protected Optional<RequestParameterTenantScopeFilter> getRequestParameterTenantScopeFilter() {
    final RequestParameterTenantScopeFilter requestParameterTenantScopeFilter = new RequestParameterTenantScopeFilter(
      getTenantPermissionEvaluator(),
      getDefaultTenantSupplier()
    );
    // TODO DialobSettings is needed here
//    requestParameterTenantScopeFilter.setParameterName(dialobSettings.getTenant().getUrlParameter());
    // It's not required to have a tenant in request scope
    requestParameterTenantScopeFilter.setTenantRequiredMatcher(getTenantRequiredMatcher());
    return Optional.of(requestParameterTenantScopeFilter);
  }

  @NonNull
  protected RequestMatcher getTenantRequiredMatcher() {
    return request -> false;
  }

  @NonNull
  protected TenantAccessEvaluator getTenantPermissionEvaluator() {
    return tenantPermissionEvaluator;
  }

  @NonNull
  protected DefaultTenantGrantedAuthorityProvider getDefaultTenantSupplier() {
    return new DefaultTenantGrantedAuthorityProvider();
  }
}
