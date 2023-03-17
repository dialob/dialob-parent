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
package io.dialob.security.aws.elb;

import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationProvider;
import org.springframework.security.web.authentication.preauth.RequestHeaderAuthenticationFilter;

import com.nimbusds.jwt.proc.JWTProcessor;

import io.dialob.security.spring.ApiKeyCurrentUserProvider;
import io.dialob.security.spring.AuthenticationStrategy;
import io.dialob.security.user.CurrentUserProvider;
import io.dialob.security.user.DelegateCurrentUserProvider;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ElbAuthenticationStrategy implements AuthenticationStrategy {

  private final GrantedAuthoritiesMapper grantedAuthoritiesMapper;
  private final JWTProcessor jwtProcessor;

  // idtoken = x-amzn-oidc-data = ELB + userinfo provided
  // accesstoken = x-amzn-oidc-accesstoken = IDP provided
  // subId = X-Amzn-Oidc-Identity = principal

  @Getter
  @Setter
  private String credentialsRequestHeader = "X-Amzn-Oidc-Data";

  @Getter
  @Setter
  private String principalRequestHeader = "X-Amzn-Oidc-Identity";

  @Setter
  @Getter
  private String groupsClaim = "cognito:groups";

  public ElbAuthenticationStrategy(@NonNull GrantedAuthoritiesMapper grantedAuthoritiesMapper, @NonNull JWTProcessor jwtProcessor) {
    this.grantedAuthoritiesMapper = grantedAuthoritiesMapper;
    this.jwtProcessor = jwtProcessor;
  }

  public HttpSecurity configureAuthentication(@NonNull HttpSecurity http, @NonNull AuthenticationManager authenticationManager) throws Exception {

    final RequestHeaderAuthenticationFilter filter = createAuthenticationFilter(authenticationManager);
    http.addFilter(filter);

    return http.sessionManagement()
      .sessionCreationPolicy(SessionCreationPolicy.STATELESS).and();
  }

  @NonNull
  protected RequestHeaderAuthenticationFilter createAuthenticationFilter(@NonNull AuthenticationManager authenticationManager) {
    final RequestHeaderAuthenticationFilter filter = new RequestHeaderAuthenticationFilter();


    LOGGER.debug("principalRequestHeader = {}, credentialsRequestHeader = {}", principalRequestHeader, credentialsRequestHeader);
    filter.setPrincipalRequestHeader(principalRequestHeader);

    filter.setCredentialsRequestHeader(credentialsRequestHeader);

    filter.setAuthenticationManager(authenticationManager);

    // No anonymous access
    filter.setExceptionIfHeaderMissing(false);
    filter.setContinueFilterChainOnUnsuccessfulAuthentication(false);
    filter.setAuthenticationDetailsSource(createAuthenticationDetailsSource());

    // No need to check for principal changes since we are not using sessions anyway (we are using
    // SessionCreationPolicy.STATELESS)
    filter.setCheckForPrincipalChanges(false);
    return filter;
  }

  public GrantedAuthoritiesMapper getGrantedAuthoritiesMapper() {
    return grantedAuthoritiesMapper;
  }

  public JWTProcessor getJwtProcessor() {
    return jwtProcessor;
  }

  @NonNull
  protected ElbBasedPreAuthenticatedWebAuthenticationDetailsSource createAuthenticationDetailsSource() {
    ElbBasedPreAuthenticatedWebAuthenticationDetailsSource elbBasedPreAuthenticatedWebAuthenticationDetailsSource = new ElbBasedPreAuthenticatedWebAuthenticationDetailsSource(getGrantedAuthoritiesMapper(), getJwtProcessor());
    elbBasedPreAuthenticatedWebAuthenticationDetailsSource.setCredentialsRequestHeader(credentialsRequestHeader);
    elbBasedPreAuthenticatedWebAuthenticationDetailsSource.setGroupsClaim(groupsClaim);
    return elbBasedPreAuthenticatedWebAuthenticationDetailsSource;
  }

  @Override
  public boolean configure(AuthenticationManagerBuilder auth) throws Exception {
    PreAuthenticatedAuthenticationProvider authenticationProvider = new PreAuthenticatedAuthenticationProvider();
    authenticationProvider.setThrowExceptionWhenTokenRejected(true);
    authenticationProvider.setPreAuthenticatedUserDetailsService(new ElbPreAuthenticatedGrantedAuthoritiesUserDetailsService());
    auth.authenticationProvider(authenticationProvider);
    return true;
  }


}
