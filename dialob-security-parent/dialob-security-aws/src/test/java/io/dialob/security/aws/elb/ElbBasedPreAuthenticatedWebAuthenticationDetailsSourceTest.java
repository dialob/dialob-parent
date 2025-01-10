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
package io.dialob.security.aws.elb;

import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.proc.JWTProcessor;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedGrantedAuthoritiesWebAuthenticationDetails;

import java.security.KeyPair;
import java.time.Instant;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ElbBasedPreAuthenticatedWebAuthenticationDetailsSourceTest extends TestBase {

  @Test
  public void test() throws Exception {

    KeyPair kp = createKeyPair();
    Instant now = Instant.now();

    String accesstoken = accesstoken(now,kp);
    String idToken = idToken(now,kp);

    HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
    JWTProcessor jwtProcessor = Mockito.mock(JWTProcessor.class);

    JWTClaimsSet claimSet = new JWTClaimsSet.Builder().build();
    when(jwtProcessor.process(anyString(),isNull())).thenReturn(claimSet);

    when(request.getHeader("x-amz-cf-id")).thenReturn("32131231");
    when(request.getHeader("x-amzn-trace-id")).thenReturn("Root=1-132\",");
    when(request.getHeader("X-Amzn-Oidc-Accesstoken")).thenReturn(accesstoken);
    when(request.getHeader("X-Amzn-Oidc-Data")).thenReturn(idToken);
    when(request.getHeader("X-Amzn-Oidc-Identity")).thenReturn("00000000-0000-0000-0000-000000000001");

    ElbBasedPreAuthenticatedWebAuthenticationDetailsSource detailsSource = new ElbBasedPreAuthenticatedWebAuthenticationDetailsSource(authorities -> authorities, jwtProcessor);
    PreAuthenticatedGrantedAuthoritiesWebAuthenticationDetails details = detailsSource.buildDetails(request);

    verify(jwtProcessor).process(anyString(),isNull());
    verify(request).getRemoteAddr();
    verify(request).getSession(false);
    verify(request).getHeader("X-Amzn-Oidc-Data");
    Mockito.verifyNoMoreInteractions(request, jwtProcessor);
  }
}
