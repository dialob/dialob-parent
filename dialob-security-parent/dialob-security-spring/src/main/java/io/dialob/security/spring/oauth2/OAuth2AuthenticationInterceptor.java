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
package io.dialob.security.spring.oauth2;

import feign.Feign;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import feign.auth.BasicAuthRequestInterceptor;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import io.dialob.security.spring.oauth2.model.Token;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;

@Slf4j
public class OAuth2AuthenticationInterceptor implements RequestInterceptor {

  private final OAuth2TokenService oAuth2TokenService;

  private final String scope;

  private Token token;

  private Instant tokenExpires;

  public OAuth2AuthenticationInterceptor(String url, String clientId, String clientSecret) {
    this(url, clientId, clientSecret, "scim.read");
  }

  public OAuth2AuthenticationInterceptor(String url, String clientId, String clientSecret, String scope) {
    this(Feign.builder()
      .encoder(new JacksonEncoder())
      .decoder(new JacksonDecoder())
      .requestInterceptor(new BasicAuthRequestInterceptor(clientId, clientSecret))
      .target(OAuth2TokenService.class, url), scope);
  }

  public OAuth2AuthenticationInterceptor(OAuth2TokenService oAuth2TokenService, String scope) {
    this.oAuth2TokenService = oAuth2TokenService;
    this.scope = scope;
  }

  @Override
  public void apply(RequestTemplate template) {
    template.header("Authorization", "Bearer " + getAccessToken());
  }

  private String getAccessToken() {
    synchronized (this) {
      if (isTokenExpired()) {
        renewToken();
      }
    }
    return token.getAccessToken();
  }

  private void renewToken() {
    this.token = oAuth2TokenService.getToken(scope);
    this.tokenExpires = Instant.now().plusSeconds(token.getExpiresIn());
    LOGGER.info("Fetched a new '{}' token. Token expires: {}.", this.token.getTokenType(), this.tokenExpires);
  }

  private boolean isTokenExpired() {
    return token == null || Instant.now().isAfter(this.tokenExpires.minusMillis(30L * 1000L));
  }
}
