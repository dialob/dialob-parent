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

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.proc.BadJOSEException;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.proc.JWTProcessor;
import io.dialob.security.spring.tenant.ImmutableGroupGrantedAuthority;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import edu.umd.cs.findbugs.annotations.NonNull;
import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedCredentialsNotFoundException;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedGrantedAuthoritiesWebAuthenticationDetails;

import java.text.ParseException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


public class ElbBasedPreAuthenticatedWebAuthenticationDetailsSource implements
  AuthenticationDetailsSource<HttpServletRequest, PreAuthenticatedGrantedAuthoritiesWebAuthenticationDetails> {

  private final GrantedAuthoritiesMapper grantedAuthoritiesMapper;

  private final JWTProcessor jwtProcessor;

  @Setter
  @Getter
  private String credentialsRequestHeader = "X-Amzn-Oidc-Data";

  @Setter
  @Getter
  private String groupsClaim = "cognito:groups";

  public ElbBasedPreAuthenticatedWebAuthenticationDetailsSource(@NonNull GrantedAuthoritiesMapper grantedAuthoritiesMapper,
                                                                @NonNull JWTProcessor jwtProcessor) {
    this.grantedAuthoritiesMapper = grantedAuthoritiesMapper;
    this.jwtProcessor = jwtProcessor;
  }

  @Override
  public PreAuthenticatedGrantedAuthoritiesWebAuthenticationDetails buildDetails(HttpServletRequest request) {
    try {
      String accessTokenHeader = request.getHeader(credentialsRequestHeader);
      if (StringUtils.isBlank(accessTokenHeader)) {
        return new PreAuthenticatedGrantedAuthoritiesWebAuthenticationDetails(
          request, Collections.emptyList());
      }
      JWTClaimsSet accesstoken = jwtProcessor.process(accessTokenHeader, null);
      Collection<? extends GrantedAuthority> authorities = Collections.emptyList();
      List<String> stringListClaim = accesstoken.getStringListClaim(groupsClaim);
      if (stringListClaim != null) {
        authorities = grantedAuthoritiesMapper.mapAuthorities(
          stringListClaim.stream().map(claim -> ImmutableGroupGrantedAuthority.of(claim,claim)).collect(Collectors.toList())
        );
      }
      return new PreAuthenticatedGrantedAuthoritiesWebAuthenticationDetails(
        request, authorities);
    } catch (ParseException | BadJOSEException | JOSEException e) {
      throw new PreAuthenticatedCredentialsNotFoundException("Could not parse token :" + e.getMessage());
    }
  }

}
