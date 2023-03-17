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
package io.dialob.session.boot;

import java.util.Optional;

import io.dialob.security.aws.elb.ElbPreAuthenticatedGrantedAuthoritiesUserDetailsService;
import io.dialob.security.aws.elb.PreAuthenticatedCurrentUserProvider;
import io.dialob.security.spring.ApiKeyCurrentUserProvider;
import io.dialob.security.user.DelegateCurrentUserProvider;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.lang.NonNull;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationProvider;
import org.springframework.security.web.util.matcher.AnyRequestMatcher;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.standard.ServletServerContainerFactoryBean;

import com.nimbusds.jwt.proc.JWTProcessor;

import io.dialob.questionnaire.service.api.session.QuestionnaireSessionService;
import io.dialob.questionnaire.service.sockjs.ExtractURITemplateVariablesToAttributesInterceptor;
import io.dialob.security.aws.elb.ElbAuthenticationStrategy;
import io.dialob.security.spring.AuthenticationStrategy;
import io.dialob.security.user.CurrentUserProvider;
import io.dialob.session.rest.OnlyOwnerCanAccessSessionPermissionEvaluator;
import io.dialob.session.rest.SessionPermissionEvaluator;
import io.dialob.settings.DialobSettings;
import io.dialob.settings.SessionSettings;
import lombok.extern.slf4j.Slf4j;

@Configuration(proxyBeanMethods = false)
@Slf4j
public class ApplicationAutoConfiguration {

  @Order(50)
  @Configuration(proxyBeanMethods = false)
  @EnableWebSecurity
  public static class RestApiSecurityConfigurer extends WebSecurityConfigurerAdapter {

    private final SessionSettings sessionSettings;
    private final QuestionnaireSessionService questionnaireSessionService;
    private final Optional<AuthenticationStrategy> authenticationStrategy;

    public RestApiSecurityConfigurer(@NonNull DialobSettings dialobSettings,
                                     @NonNull QuestionnaireSessionService questionnaireSessionService,
                                     Optional<AuthenticationStrategy> authenticationStrategy) {
      this.sessionSettings = dialobSettings.getSession();
      this.questionnaireSessionService = questionnaireSessionService;
      this.authenticationStrategy = authenticationStrategy;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
      http = http
        .securityMatcher(AnyRequestMatcher.INSTANCE);
      if (authenticationStrategy.isPresent()) {
        authenticationStrategy.get().configureAuthentication(http, authenticationManager());

      }
      if (this.sessionSettings.getRest().isRequireAuthenticated()) {
        http = http.authorizeHttpRequests().anyRequest().authenticated().and();
      }
      http
        .cors().configurationSource(corsConfigurationSource()).and()
        .csrf().disable();
    }

    CorsConfigurationSource corsConfigurationSource() {
      final TenantFromRequestResolver tenantFromRequestResolver = new SessionRestTenantFromRequestResolver(questionnaireSessionService);
      return new TenantBasedCorsConfigurationSource(sessionSettings.getRest().getCors()::get, tenantFromRequestResolver);
    }

  }

  @Configuration(proxyBeanMethods = false)
  @ConditionalOnProperty(prefix = "dialob.session.security", name="enabled", havingValue = "true")
  public static class AwsSecurityConfiguration {

    @Bean
    public AuthenticationStrategy authenticationStrategy(DialobSettings dialobSettings, GrantedAuthoritiesMapper grantedAuthoritiesMapper, JWTProcessor jwtProcessor) {
      ElbAuthenticationStrategy elbAuthenticationStrategy = new ElbAuthenticationStrategy(grantedAuthoritiesMapper, jwtProcessor);
      dialobSettings.getAws().getElb().getPrincipalRequestHeader().ifPresent(elbAuthenticationStrategy::setPrincipalRequestHeader);
      dialobSettings.getAws().getElb().getCredentialsRequestHeader().ifPresent(elbAuthenticationStrategy::setCredentialsRequestHeader);
      return elbAuthenticationStrategy;
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
      PreAuthenticatedAuthenticationProvider authenticationProvider = new PreAuthenticatedAuthenticationProvider();
      authenticationProvider.setThrowExceptionWhenTokenRejected(true);
      authenticationProvider.setPreAuthenticatedUserDetailsService(new ElbPreAuthenticatedGrantedAuthoritiesUserDetailsService());
      return authenticationProvider;
    }

    @Bean
    public CurrentUserProvider currentUserProvider() {
      return new DelegateCurrentUserProvider(
        new PreAuthenticatedCurrentUserProvider(),
        new ApiKeyCurrentUserProvider()
      );
    }

    @Bean
    public SessionPermissionEvaluator onlyOwnerCanAccessSessionPermissionEvaluator(QuestionnaireSessionService questionnaireSessionService) {
      return new OnlyOwnerCanAccessSessionPermissionEvaluator(questionnaireSessionService);
    }

  }


  @Configuration(proxyBeanMethods = false)
  @EnableWebSocket
  @ConditionalOnProperty(prefix = "dialob.session.sockjs", name="webSocketEnabled", havingValue = "true")
  public static class SockJSWebSocketConfigurer implements WebSocketConfigurer {

    private final WebSocketHandler perConnectionWebSocketHandler;

    private final SessionSettings.SockJSSettings settings;

    private final TaskScheduler taskScheduler;

    public SockJSWebSocketConfigurer(DialobSettings settings, WebSocketHandler perConnectionWebSocketHandler, TaskScheduler taskScheduler) {
      this.perConnectionWebSocketHandler = perConnectionWebSocketHandler;
      this.settings = settings.getSession().getSockjs();
      this.taskScheduler = taskScheduler;
    }

    @Override
    public void registerWebSocketHandlers(@NonNull WebSocketHandlerRegistry webSocketHandlerRegistry) {
      if (settings.isEnabled()) {
        webSocketHandlerRegistry
          .addHandler(perConnectionWebSocketHandler, settings.getContextPath())
          .setAllowedOrigins(settings.getAllowedOrigins().toArray(new String[0]))
          .withSockJS()
          .setClientLibraryUrl(settings.getLibraryUrl())
          .setWebSocketEnabled(settings.isWebSocketEnabled())
          .setInterceptors(new ExtractURITemplateVariablesToAttributesInterceptor(
            StringUtils.defaultString(settings.getUrlAttributes().getSessionId(), "sessionId"),
            StringUtils.defaultString(settings.getUrlAttributes().getTenantId(), "tenantId")
          ))
          .setTaskScheduler(taskScheduler);
        if (settings.isWebSocketEnabled()) {
          LOGGER.info("Configuring WebSocket endpoint {}", settings.getContextPath());
        }
      }
    }

    @Bean
    public ServletServerContainerFactoryBean createWebSocketContainer() {
      ServletServerContainerFactoryBean container = new ServletServerContainerFactoryBean();
      container.setMaxTextMessageBufferSize(settings.getMaxTextMessageBufferSize());
      container.setMaxBinaryMessageBufferSize(settings.getMaxBinaryMessageBufferSize());
      return container;
    }


  }

}
