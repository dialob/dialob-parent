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
import io.dialob.boot.settings.AdminApplicationSettings;
import io.dialob.boot.settings.ComposerApplicationSettings;
import io.dialob.boot.settings.QuestionnaireApplicationSettings;
import io.dialob.boot.settings.ReviewApplicationSettings;
import io.dialob.security.aws.elb.ElbAuthenticationStrategy;
import io.dialob.security.spring.AuthenticationStrategy;
import io.dialob.security.spring.apikey.ApiKeyRequestMatcher;
import io.dialob.security.spring.tenant.TenantAccessEvaluator;
import io.dialob.settings.DialobSettings;
import org.junit.jupiter.api.Test;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.oauth2.client.endpoint.OAuth2AccessTokenResponseClient;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class SecurityConfigurationTest {


  @org.springframework.boot.test.context.TestConfiguration(proxyBeanMethods = false)
  @EnableConfigurationProperties(ReviewApplicationSettings.class)
  public static class MockConfiguration {

    @Bean
    GrantedAuthoritiesMapper grantedAuthoritiesMapper() {
      return mock(GrantedAuthoritiesMapper.class);
    }

    @Bean
    OAuth2AccessTokenResponseClient oAuth2AccessTokenResponseClient() {
      return mock(OAuth2AccessTokenResponseClient.class);
    }

    @Bean
    TenantAccessEvaluator tenantAccessEvaluator() {
      return mock(TenantAccessEvaluator.class);
    }

    @Bean
    ClientRegistrationRepository clientRegistrationRepository() {
      return mock(ClientRegistrationRepository.class);
    }

    @Bean
    JWTProcessor jwtProcessor() {
      return mock(JWTProcessor.class);
    }
  }


  @Test
  public void testSecurityConfigurationTestSecurityDisabled() {
    new ApplicationContextRunner()
      .withPropertyValues(
        "dialob.security.enabled=false",
        "review.contextPath=/review")
      .withUserConfiguration(
        SecurityConfiguration.class,
        DialobSettings.class,
        ComposerApplicationSettings.class,
        QuestionnaireApplicationSettings.class,
        ReviewApplicationSettings.class,
        AdminApplicationSettings.class)
      .run(context -> {
        assertThat(context)
          .doesNotHaveBean(SecurityConfiguration.class)
          .doesNotHaveBean(ActuatorEndpointSecurityConfiguration.class)
          .doesNotHaveBean(ReviewSecurityConfigurer.class)
          .doesNotHaveBean(QuestionnaireSecurityConfigurer.class)
          .doesNotHaveBean(ComposerSecurityConfigurer.class)
          .doesNotHaveBean(WebApiSecurityConfigurer.class)
          .doesNotHaveBean(AdminSecurityConfigurer.class);
      });
  }

  @Test
  public void testSecurityConfigurationTestSecurityOAuth2Enabled() {
    new ApplicationContextRunner()
      .withPropertyValues(
        "spring.profiles.active=ui",
        "dialob.security.enabled=true",
        "review.contextPath=/review",
        "questionnaire.contextPath=/questionnaire",
        "admin.contextPath=/",
        "composer.contextPath=/composer")
      .withUserConfiguration(
        SecurityConfiguration.class,
        DialobSettings.class,
        ComposerApplicationSettings.class,
        QuestionnaireApplicationSettings.class,
        AdminApplicationSettings.class,
        SecurityConfigurationTest.MockConfiguration.class)
      .run(context -> {
        assertThat(context)
          .hasSingleBean(SecurityConfiguration.class)
          .hasSingleBean(ActuatorEndpointSecurityConfiguration.class)
          .hasSingleBean(ReviewSecurityConfigurer.class)
          .hasSingleBean(QuestionnaireSecurityConfigurer.class)
          .hasSingleBean(ComposerSecurityConfigurer.class)
          .hasSingleBean(WebApiSecurityConfigurer.class)
          .hasSingleBean(AdminSecurityConfigurer.class)
          .getBean(AuthenticationStrategy.class).isInstanceOf(OAuth2AuthenticationStrategy.class);
      });
  }

  @Test
  public void testSecurityConfigurationInAPIOnlyConfiguration() {
    new ApplicationContextRunner()
      .withPropertyValues(
        "spring.profiles.active=",
        "dialob.security.enabled=true")
      .withUserConfiguration(
        SecurityConfiguration.class,
        DialobSettings.class,
        ComposerApplicationSettings.class,
        QuestionnaireApplicationSettings.class,
        AdminApplicationSettings.class,
        SecurityConfigurationTest.MockConfiguration.class)
      .run(context -> {
        assertThat(context)
          .hasSingleBean(SecurityConfiguration.class)
          .hasSingleBean(ActuatorEndpointSecurityConfiguration.class)
          .hasSingleBean(ApiServiceSecurityConfigurer.class)
          .doesNotHaveBean(ReviewSecurityConfigurer.class)
          .doesNotHaveBean(QuestionnaireSecurityConfigurer.class)
          .doesNotHaveBean(ComposerSecurityConfigurer.class)
          .doesNotHaveBean(WebApiSecurityConfigurer.class)
          .doesNotHaveBean(AdminSecurityConfigurer.class)
          .getBean(AuthenticationStrategy.class).isInstanceOf(OAuth2AuthenticationStrategy.class);
      });
  }



  @Test
  public void testSecurityConfigurationTestSecurityAwsElbEnabled() {
    new ApplicationContextRunner()
      .withPropertyValues(
        "spring.profiles.active=ui",
        "dialob.security.enabled=true",
        "dialob.security.authenticationMethod=AWSELB",
        "review.contextPath=/review",
        "questionnaire.contextPath=/questionnaire",
        "admin.contextPath=/",
        "composer.contextPath=/composer")
      .withUserConfiguration(
        SecurityConfiguration.class,
        DialobSettings.class,
        ComposerApplicationSettings.class,
        QuestionnaireApplicationSettings.class,
        AdminApplicationSettings.class,
        SecurityConfigurationTest.MockConfiguration.class)
      .run(context -> {
        assertThat(context)
          .hasSingleBean(SecurityConfiguration.class)
          .hasSingleBean(ActuatorEndpointSecurityConfiguration.class)
          .hasSingleBean(ReviewSecurityConfigurer.class)
          .hasSingleBean(QuestionnaireSecurityConfigurer.class)
          .hasSingleBean(ComposerSecurityConfigurer.class)
          .hasSingleBean(WebApiSecurityConfigurer.class)
          .hasSingleBean(AdminSecurityConfigurer.class)
          .hasSingleBean(ApiServiceSecurityConfigurer.class)
          .doesNotHaveBean(ApiKeyRequestMatcher.class)
          .getBean(AuthenticationStrategy.class).isInstanceOf(ElbAuthenticationStrategy.class);
      });
  }
}
