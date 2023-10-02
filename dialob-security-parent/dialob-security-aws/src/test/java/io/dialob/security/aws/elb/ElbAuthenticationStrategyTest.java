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

import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.proc.JWTProcessor;
import io.dialob.security.spring.tenant.ImmutableGroupGrantedAuthority;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationProvider;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.security.web.authentication.preauth.RequestHeaderAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.KeyPair;
import java.time.Instant;
import java.util.Arrays;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.*;

class ElbAuthenticationStrategyTest  extends TestBase {

  @Test
  public void test() throws Exception {

    KeyPair kp = createKeyPair();
    Instant now = Instant.now();

    String accessToken = accesstoken(now,kp);
    String idToken = idToken(now,kp);

    JWTProcessor jwtProcessor = Mockito.mock(JWTProcessor.class);
    AuthenticationManager authenticationManager = Mockito.mock(AuthenticationManager.class);
    JWTClaimsSet claimSet = new JWTClaimsSet.Builder().claim("cognito:groups",Arrays.asList("admin")).build();
    when(jwtProcessor.process(anyString(),isNull())).thenReturn(claimSet);

    GrantedAuthoritiesMapper grantedAuthoritiesMapper = authorities -> authorities;
    ElbAuthenticationStrategy elbAuthenticationStrategy = new ElbAuthenticationStrategy(grantedAuthoritiesMapper, jwtProcessor, authenticationManager);

    PreAuthenticatedAuthenticationProvider authenticationProvider = new PreAuthenticatedAuthenticationProvider();
    authenticationProvider.setThrowExceptionWhenTokenRejected(true);
    authenticationProvider.setPreAuthenticatedUserDetailsService(new ElbPreAuthenticatedGrantedAuthoritiesUserDetailsService());


    RequestHeaderAuthenticationFilter filter = elbAuthenticationStrategy.createAuthenticationFilter(new ProviderManager(Arrays.asList(authenticationProvider)));

    HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
    HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
    FilterChain chain = Mockito.mock(FilterChain.class);


    when(request.getHeader("x-amz-cf-id")).thenReturn("cf-123");
    when(request.getHeader("x-amzn-trace-id")).thenReturn("trc-id-321");
    when(request.getHeader("X-Amzn-Oidc-Accesstoken")).thenReturn(accessToken);
    when(request.getHeader("X-Amzn-Oidc-Data")).thenReturn(idToken);
    when(request.getHeader("X-Amzn-Oidc-Identity")).thenReturn("00000000-0000-0000-0000-000000000002");


    filter.doFilter(request, response, chain);

    PreAuthenticatedAuthenticationToken authenticationToken = (PreAuthenticatedAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
    Assertions.assertEquals("00000000-0000-0000-0000-000000000002", authenticationToken.getName());
    Assertions.assertEquals(idToken, authenticationToken.getCredentials());
    Assertions.assertEquals("00000000-0000-0000-0000-000000000002", ((UserDetails)authenticationToken.getPrincipal()).getUsername());
    Assertions.assertIterableEquals(Arrays.asList(ImmutableGroupGrantedAuthority.of("admin", "admin")), ((UserDetails)authenticationToken.getPrincipal()).getAuthorities());
    Assertions.assertIterableEquals(Arrays.asList(ImmutableGroupGrantedAuthority.of("admin", "admin")), authenticationToken.getAuthorities());

    SecurityContextHolder.clearContext();

    verify(jwtProcessor).process(anyString(),isNull());
    verify(request).getHeader("X-Amzn-Oidc-Identity");
    verify(request, times(2)).getHeader("X-Amzn-Oidc-Data");
    verify(request).getRemoteAddr();
    verify(request).getSession(false);
    verify(chain).doFilter(request, response);

    Mockito.verifyNoMoreInteractions(request, response, chain, jwtProcessor);

  }


}
