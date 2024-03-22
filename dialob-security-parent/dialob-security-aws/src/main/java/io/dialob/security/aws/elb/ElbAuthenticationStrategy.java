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

import com.nimbusds.jwt.proc.JWTProcessor;
import io.dialob.security.spring.AuthenticationStrategy;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import edu.umd.cs.findbugs.annotations.NonNull;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.web.authentication.preauth.RequestHeaderAuthenticationFilter;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;

@Slf4j
public class ElbAuthenticationStrategy implements AuthenticationStrategy {

  private final GrantedAuthoritiesMapper grantedAuthoritiesMapper;
  private final JWTProcessor jwtProcessor;
  private final AuthenticationManager authenticationManager;

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

  public ElbAuthenticationStrategy(@NonNull GrantedAuthoritiesMapper grantedAuthoritiesMapper, @NonNull JWTProcessor jwtProcessor, AuthenticationManager authenticationManager) {
    this.grantedAuthoritiesMapper = grantedAuthoritiesMapper;
    this.jwtProcessor = jwtProcessor;
    this.authenticationManager = authenticationManager;
  }

  public HttpSecurity configureAuthentication(@NonNull HttpSecurity http) throws Exception {

    final RequestHeaderAuthenticationFilter filter = createAuthenticationFilter(authenticationManager);
    http.addFilter(filter);

    return http.sessionManagement()
      .sessionCreationPolicy(SessionCreationPolicy.STATELESS).and();
  }

  @NonNull
  RequestHeaderAuthenticationFilter createAuthenticationFilter(@NonNull AuthenticationManager authenticationManager) {
    final RequestHeaderAuthenticationFilter filter = new RequestHeaderAuthenticationFilter();

    HttpSessionSecurityContextRepository securityContextRepository = new HttpSessionSecurityContextRepository();
    securityContextRepository.setAllowSessionCreation(false);
    filter.setSecurityContextRepository(securityContextRepository);

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

  GrantedAuthoritiesMapper getGrantedAuthoritiesMapper() {
    return grantedAuthoritiesMapper;
  }

  JWTProcessor getJwtProcessor() {
    return jwtProcessor;
  }

  @NonNull
  ElbBasedPreAuthenticatedWebAuthenticationDetailsSource createAuthenticationDetailsSource() {
    ElbBasedPreAuthenticatedWebAuthenticationDetailsSource elbBasedPreAuthenticatedWebAuthenticationDetailsSource = new ElbBasedPreAuthenticatedWebAuthenticationDetailsSource(getGrantedAuthoritiesMapper(), getJwtProcessor());
    elbBasedPreAuthenticatedWebAuthenticationDetailsSource.setCredentialsRequestHeader(credentialsRequestHeader);
    elbBasedPreAuthenticatedWebAuthenticationDetailsSource.setGroupsClaim(groupsClaim);
    return elbBasedPreAuthenticatedWebAuthenticationDetailsSource;
  }


}
