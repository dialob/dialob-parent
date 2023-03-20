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

import com.nimbusds.jwt.proc.JWTProcessor;
import io.dialob.security.aws.elb.ElbAuthenticationStrategy;
import io.dialob.security.aws.elb.ElbPreAuthenticatedGrantedAuthoritiesUserDetailsService;
import io.dialob.security.aws.elb.PreAuthenticatedCurrentUserProvider;
import io.dialob.security.key.ServletRequestApiKeyExtractor;
import io.dialob.security.spring.ApiKeyCurrentUserProvider;
import io.dialob.security.spring.AuthenticationStrategy;
import io.dialob.security.spring.OAuth2SpringSecurityCurrentUserProvider;
import io.dialob.security.spring.apikey.*;
import io.dialob.security.user.CurrentUserProvider;
import io.dialob.security.user.DelegateCurrentUserProvider;
import io.dialob.settings.DialobSettings;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.oauth2.client.endpoint.OAuth2AccessTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationProvider;

import java.util.List;

@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(name = "dialob.security.enabled", havingValue = "true")
@EnableWebSecurity
@Import(ApiServiceSecurityConfigurer.class)
@Slf4j
public class SecurityConfiguration {

  @Bean
  public ServletRequestApiKeyExtractor requestParameterServletApiKeyExtractor() {
    return new RequestHeaderApiKeyExtractor();
  }

  @Bean
  public ApiKeyValidator apiKeyValidator(DialobSettings apiSettings) {
    return new HmacSHA256ApiKeyValidator(apiSettings.getApi().getApiKeySalt().getBytes());
  }

  @Bean
  public ClientApiKeyService clientApiKeyService(DialobSettings apiSettings) {
    List<DialobSettings.ApiSettings.ApiKey> apiKeys = apiSettings.getApi().getApiKeys();
    LOGGER.info("{} api keys found.", apiKeys.size());
    final FixedClientApiKeyService.FixedClientApiKeyServiceBuilder builder = FixedClientApiKeyService.builder();
    apiKeys.forEach(apiKey -> builder.addKey(apiKey.getClientId(), apiKey.getHash(), apiKey.getTenantId(), apiKey.getPermissions()));
    return builder.build();
  }

  @Bean
  AuthenticationProvider apiKeyAuthenticationProvider(@NonNull ClientApiKeyService apiKeyService,
                                                      @NonNull ApiKeyAuthoritiesProvider apiKeyAuthoritiesProvider,
                                                      @NonNull ApiKeyValidator apiKeyValidator) {
    return new ApiKeyAuthenticationProvider(apiKeyService, apiKeyAuthoritiesProvider, apiKeyValidator);
  }

  @Configuration(proxyBeanMethods = false)
  @ConditionalOnProperty(name = "dialob.security.enabled", havingValue = "true")
  @Import({
    QuestionnaireSecurityConfigurer.class,
    AdminSecurityConfigurer.class,
    WebApiSecurityConfigurer.class,
    ComposerSecurityConfigurer.class,
    ReviewSecurityConfigurer.class,
  })
  public static class DialobSecurityConfigurerConfiguration {
    @Bean
    public ActuatorEndpointSecurityConfigurer actuatorEndpointSecurityConfigurer() {
      return new ActuatorEndpointSecurityConfigurer();
    }

    @Bean
    @ConditionalOnProperty(name = "dialob.security.authenticationMethod", havingValue = "OAUTH2", matchIfMissing = true)
    public AuthenticationStrategy authenticationStrategyOauth2(
                                                         GrantedAuthoritiesMapper grantedAuthoritiesMapper,
                                                         OAuth2AccessTokenResponseClient<OAuth2AuthorizationCodeGrantRequest> accessTokenResponseClient) {
      return new OAuth2AuthenticationStrategy(grantedAuthoritiesMapper, accessTokenResponseClient);
    }

    @Bean
    @ConditionalOnProperty(name = "dialob.security.authenticationMethod", havingValue = "AWSELB")
    public AuthenticationStrategy authenticationStrategyElb(DialobSettings dialobSettings,
                                                            GrantedAuthoritiesMapper grantedAuthoritiesMapper,
                                                            JWTProcessor jwtProcessor,
                                                            AuthenticationManager authenticationManager) {
      var elbAuthenticationStrategy = new ElbAuthenticationStrategy(grantedAuthoritiesMapper, jwtProcessor, authenticationManager);
      dialobSettings.getAws().getElb().getPrincipalRequestHeader().ifPresent(elbAuthenticationStrategy::setPrincipalRequestHeader);
      dialobSettings.getAws().getElb().getCredentialsRequestHeader().ifPresent(elbAuthenticationStrategy::setCredentialsRequestHeader);
      return elbAuthenticationStrategy;
    }

    @Bean
    public AuthenticationProvider preAuthenticatedAuthenticationProvider() {
      PreAuthenticatedAuthenticationProvider authenticationProvider = new PreAuthenticatedAuthenticationProvider();
      authenticationProvider.setThrowExceptionWhenTokenRejected(true);
      authenticationProvider.setPreAuthenticatedUserDetailsService(new ElbPreAuthenticatedGrantedAuthoritiesUserDetailsService());
      return authenticationProvider;
    }

    @Bean
    @ConditionalOnProperty(name = "dialob.security.authenticationMethod", havingValue = "OAUTH2", matchIfMissing = true)
    public CurrentUserProvider currentUserProviderO2() {
      return new DelegateCurrentUserProvider(
        new OAuth2SpringSecurityCurrentUserProvider(),
        new ApiKeyCurrentUserProvider()
      );
    }

    @Bean
    @ConditionalOnProperty(name = "dialob.security.authenticationMethod", havingValue = "AWSELB")
    public CurrentUserProvider currentUserProviderELB() {
      return new DelegateCurrentUserProvider(
        new PreAuthenticatedCurrentUserProvider(),
        new ApiKeyCurrentUserProvider()
      );
    }
  }

  @Bean
  public AuthenticationManager authenticationManager(List<AuthenticationProvider> providerList) {
    if (providerList.isEmpty()) {
      return authentication -> authentication;
    }
    return new ProviderManager(providerList);
  }


}
