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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.lang.NonNull;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.oauth2.client.endpoint.OAuth2AccessTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest;

import com.nimbusds.jwt.proc.JWTProcessor;

import io.dialob.boot.settings.AdminApplicationSettings;
import io.dialob.boot.settings.ComposerApplicationSettings;
import io.dialob.boot.settings.QuestionnaireApplicationSettings;
import io.dialob.boot.settings.ReviewApplicationSettings;
import io.dialob.security.aws.elb.ElbAuthenticationStrategy;
import io.dialob.security.key.ServletRequestApiKeyExtractor;
import io.dialob.security.spring.AuthenticationStrategy;
import io.dialob.security.spring.apikey.ApiKeyAuthoritiesProvider;
import io.dialob.security.spring.apikey.ApiKeyValidator;
import io.dialob.security.spring.apikey.ClientApiKeyService;
import io.dialob.security.spring.apikey.FixedClientApiKeyService;
import io.dialob.security.spring.apikey.HmacSHA256ApiKeyValidator;
import io.dialob.security.spring.apikey.RequestHeaderApiKeyExtractor;
import io.dialob.security.spring.tenant.TenantAccessEvaluator;
import io.dialob.security.user.CurrentUserProvider;
import io.dialob.settings.DialobSettings;
import lombok.extern.slf4j.Slf4j;

@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(name = "dialob.security.enabled", havingValue = "true")
@EnableWebSecurity
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
  public static WebSecurityConfigurerAdapter apiServiceSecurityConfigurer(@NonNull Environment env,
                                                                          @NonNull ClientApiKeyService apiKeyService,
                                                                          @NonNull ApiKeyAuthoritiesProvider apiKeyAuthoritiesProvider,
                                                                          @NonNull ApiKeyValidator apiRequestValidator,
                                                                          @NonNull DialobSettings settings,
                                                                          @NonNull ServletRequestApiKeyExtractor keyRequestExtractor,
                                                                          @NonNull TenantAccessEvaluator tenantPermissionEvaluator,
                                                                          @NonNull AuthenticationStrategy authenticationStrategy) {
    return new ApiServiceSecurityConfigurer(
      apiKeyService,
      apiKeyAuthoritiesProvider,
      apiRequestValidator,
      settings,
      keyRequestExtractor,
      tenantPermissionEvaluator,
      authenticationStrategy,
      env.acceptsProfiles(Profiles.of("!ui"))) // when ui is disabled apply filter to all requests
      .withOrder(120);
  }


  @Configuration(proxyBeanMethods = false)
  @ConditionalOnProperty(name = "dialob.security.enabled", havingValue = "true")
  public static class DialobSecurityConfigurerConfiguration {
    @Bean
    public WebSecurityConfigurerAdapter actuatorEndpointSecurityConfigurer() {
      return new ActuatorEndpointSecurityConfigurer()
        .withOrder(160);
    }

    @Bean
    public AuthenticationStrategy authenticationStrategy(DialobSettings dialobSettings,
                                                         Optional<GrantedAuthoritiesMapper> grantedAuthoritiesMapper,
                                                         Optional<OAuth2AccessTokenResponseClient<OAuth2AuthorizationCodeGrantRequest>> accessTokenResponseClient,
                                                         Optional<JWTProcessor> jwtProcessor) {
      List<String> missingBeans = new ArrayList<>();
      if (!grantedAuthoritiesMapper.isPresent()) {
        LOGGER.error("grantedAuthoritiesMapper bean not present.");
        missingBeans.add("grantedAuthoritiesMapper");
      }
      if (missingBeans.isEmpty() && dialobSettings.getSecurity().getAuthenticationMethod() == DialobSettings.SecuritySettings.AuthenticationMethod.OAUTH2) {
        if (!accessTokenResponseClient.isPresent()) {
          LOGGER.error("accessTokenResponseClient bean not present.");
          missingBeans.add("accessTokenResponseClient");
        } else {
          return new OAuth2AuthenticationStrategy(grantedAuthoritiesMapper.get(), accessTokenResponseClient.get());
        }
      }
      if (missingBeans.isEmpty() && dialobSettings.getSecurity().getAuthenticationMethod() == DialobSettings.SecuritySettings.AuthenticationMethod.AWSELB) {
        if (!jwtProcessor.isPresent()) {
          LOGGER.error("jwtProcessor bean not present.");
          missingBeans.add("jwtProcessor");
        } else {
          ElbAuthenticationStrategy elbAuthenticationStrategy = new ElbAuthenticationStrategy(grantedAuthoritiesMapper.get(), jwtProcessor.get());
          dialobSettings.getAws().getElb().getPrincipalRequestHeader().ifPresent(elbAuthenticationStrategy::setPrincipalRequestHeader);
          dialobSettings.getAws().getElb().getCredentialsRequestHeader().ifPresent(elbAuthenticationStrategy::setCredentialsRequestHeader);
          return elbAuthenticationStrategy;
        }
      }
      if (!missingBeans.isEmpty()) {
        throw new RuntimeException("Cannot create bean 'authenticationStrategy' due missing beans: " + missingBeans.stream().collect(Collectors.joining(",")));
      }
      return (http, authenticationManager) -> http;
    }

    @Bean
    @ConditionalOnBean(AuthenticationStrategy.class)
    public CurrentUserProvider currentUserProvider(AuthenticationStrategy authenticationStrategy) {
      return authenticationStrategy.currentUserProviderBean();
    }

    @Bean
    @Profile("ui")
    public WebSecurityConfigurerAdapter reviewSecurityConfigurer(@NonNull ReviewApplicationSettings settings,
                                                                 @NonNull TenantAccessEvaluator tenantPermissionEvaluator,
                                                                 @NonNull AuthenticationStrategy authenticationStrategy) {
      return new ReviewSecurityConfigurer(settings.getContextPath(), tenantPermissionEvaluator, authenticationStrategy)
        .withOrder(150);
    }

    @Bean
    @Profile("ui")
    public WebSecurityConfigurerAdapter questionnaireSecurityConfigurer(@NonNull QuestionnaireApplicationSettings settings,
                                                                        @NonNull TenantAccessEvaluator tenantPermissionEvaluator,
                                                                        @NonNull AuthenticationStrategy authenticationStrategy) {
      return new QuestionnaireSecurityConfigurer(settings.getContextPath(), tenantPermissionEvaluator, authenticationStrategy)
        .withOrder(140);
    }


    @Bean
    @Profile("ui")
    public WebSecurityConfigurerAdapter composerSecurityConfigurer(@NonNull ComposerApplicationSettings settings,
                                                                   @NonNull TenantAccessEvaluator tenantPermissionEvaluator,
                                                                   @NonNull AuthenticationStrategy authenticationStrategy) {
      return new ComposerSecurityConfigurer(settings.getContextPath(), tenantPermissionEvaluator, authenticationStrategy)
        .withOrder(130);
    }

    @Bean
    @Profile("ui")
    public WebSecurityConfigurerAdapter webApiSecurityConfigurer(@NonNull DialobSettings settings,
                                                                 @NonNull TenantAccessEvaluator tenantPermissionEvaluator,
                                                                 @NonNull AuthenticationStrategy authenticationStrategy) {
      return new WebApiSecurityConfigurer(settings, tenantPermissionEvaluator, authenticationStrategy)
        .withOrder(125);
    }

    @Bean
    @Profile("ui")
    public WebSecurityConfigurerAdapter adminSecurityConfigurer(@NonNull AdminApplicationSettings adminApplicationSettings,
                                                                @NonNull ApplicationEventPublisher applicationEventPublisher,
                                                                @NonNull TenantAccessEvaluator tenantPermissionEvaluator,
                                                                @NonNull AuthenticationStrategy authenticationStrategy) {
      return new AdminSecurityConfigurer(adminApplicationSettings.getContextPath(), applicationEventPublisher, tenantPermissionEvaluator, authenticationStrategy);
    }
  }

}