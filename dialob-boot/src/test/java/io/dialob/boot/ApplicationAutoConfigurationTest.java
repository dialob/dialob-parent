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
package io.dialob.boot;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.dialob.boot.controller.AdminController;
import io.dialob.boot.controller.ComposerController;
import io.dialob.boot.controller.FillController;
import io.dialob.boot.controller.ReviewController;
import io.dialob.boot.security.*;
import io.dialob.form.service.api.FormDatabase;
import io.dialob.questionnaire.service.api.QuestionnaireDatabase;
import io.dialob.security.spring.tenant.TenantAccessEvaluator;
import io.dialob.security.tenant.CurrentTenant;
import io.dialob.settings.DialobSettingsAutoConfiguration;
import io.dialob.tenant.DialobTenantConfigurationAutoConfiguration;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.security.oauth2.client.servlet.OAuth2ClientAutoConfiguration;
import org.springframework.boot.autoconfigure.thymeleaf.ThymeleafAutoConfiguration;
import org.springframework.boot.test.context.runner.WebApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import static org.assertj.core.api.Assertions.assertThat;

class ApplicationAutoConfigurationTest {

  @Configuration(proxyBeanMethods = false)
  public static class TestConfiguration {

    @Bean
    public QuestionnaireDatabase questionnaireDatabase() {
      return Mockito.mock(QuestionnaireDatabase.class);
    }

    // required by ui profille
    @Bean
    public FormDatabase formDatabase() {
      return Mockito.mock(FormDatabase.class);
    }
    @Bean
    public ObjectMapper objectMapper() {
      return new ObjectMapper();
    }

  }

  @Configuration(proxyBeanMethods = false)
  @EnableWebMvc
  public static class SecurityTestConfiguration extends TestConfiguration{


    // required by security
    @Bean
    public GrantedAuthoritiesMapper grantedAuthoritiesMapper() {
      return Mockito.mock(GrantedAuthoritiesMapper.class);
    }
    @Bean
    public TenantAccessEvaluator tenantAccessEvaluator() {
      return Mockito.mock(TenantAccessEvaluator.class);
    }
  }

  @Test
  public void testApplicationAutoConfigurationWithoutAnyProfile() {
    new WebApplicationContextRunner()
      .withUserConfiguration(TestConfiguration.class)
      .withConfiguration(AutoConfigurations.of(
        ApplicationAutoConfiguration.class,
        DialobTenantConfigurationAutoConfiguration.class,
        DialobSettingsAutoConfiguration.class
      ))
      .run(context -> {
        assertThat(context)
          .hasSingleBean(CurrentTenant.class)
          .hasSingleBean(DialobProgramExceptionHandlers.class)
          .doesNotHaveBean(ComposerController.class)
          .doesNotHaveBean(ReviewController.class)
          .doesNotHaveBean(AdminController.class)
          .doesNotHaveBean(FillController.class)
          .doesNotHaveBean(ApiServiceSecurityConfigurer.class)
        ;
      });
  }

  @Test
  public void testApplicationAutoConfigurationWithUIProfile() {
    new WebApplicationContextRunner()
      .withSystemProperties(
        "spring.profiles.active=ui",
        "review.contextPath=/review",
        "composer.contextPath=/composer",
        "questionnaire.contextPath=/fill"
      )
      .withUserConfiguration(TestConfiguration.class)
      .withConfiguration(AutoConfigurations.of(
        ApplicationAutoConfiguration.class,
        DialobTenantConfigurationAutoConfiguration.class,
        DialobSettingsAutoConfiguration.class,
        ThymeleafAutoConfiguration.class
      ))
      .run(context -> {
        assertThat(context)
          .hasSingleBean(CurrentTenant.class)
          .hasSingleBean(DialobProgramExceptionHandlers.class)
          .hasSingleBean(ComposerController.class)
          .hasSingleBean(ReviewController.class)
          .hasSingleBean(AdminController.class)
          .hasSingleBean(FillController.class)
          .doesNotHaveBean(ApiServiceSecurityConfigurer.class)
        ;
      });
  }
  @Test
  public void testApplicationAutoConfigurationWithUIProfileAndSecurityEnabled() {
    new WebApplicationContextRunner()
      .withSystemProperties(
        "spring.profiles.active=ui",
        "dialob.security.enabled=true",
        "review.contextPath=/review",
        "admin.contextPath=/admin",
        "composer.contextPath=/composer",
        "questionnaire.contextPath=/fill",
        "spring.security.oauth2.client.registration.default.provider=test-provider",
        "spring.security.oauth2.client.registration.default.clientId=test",
        "spring.security.oauth2.client.registration.default.authorizationGrantType=authorization_code",
        "spring.security.oauth2.client.registration.default.redirectUri=redirectUri",
        "spring.security.oauth2.client.provider.test-provider.authorizationUri=authorizationUri",
        "spring.security.oauth2.client.provider.test-provider.tokenUri=tokenUri"
      )
      .withUserConfiguration(SecurityTestConfiguration.class)
      .withConfiguration(AutoConfigurations.of(
        ApplicationAutoConfiguration.class,
        DialobTenantConfigurationAutoConfiguration.class,
        DialobSettingsAutoConfiguration.class,
        OAuth2ClientAutoConfiguration.class,
        ThymeleafAutoConfiguration.class
      ))
      .run(context -> {
        assertThat(context)
          .hasSingleBean(CurrentTenant.class)
          .hasSingleBean(DialobProgramExceptionHandlers.class)
          .hasSingleBean(ComposerController.class)
          .hasSingleBean(ReviewController.class)
          .hasSingleBean(AdminController.class)
          .hasSingleBean(FillController.class)
          .hasSingleBean(ReviewSecurityConfigurer.class)
          .hasSingleBean(QuestionnaireSecurityConfigurer.class)
          .hasSingleBean(ComposerSecurityConfigurer.class)
          .hasSingleBean(WebApiSecurityConfigurer.class)
          .hasSingleBean(AdminSecurityConfigurer.class)
          .hasSingleBean(ApiServiceSecurityConfigurer.class)
        ;
      });
  }

  @Test
  public void testApplicationAutoConfigurationWithoutUIProfileAndSecurityEnabled() {
    new WebApplicationContextRunner()
      .withSystemProperties(
        "spring.profiles.active=",
        "dialob.security.enabled=true",
        "review.contextPath=/review",
        "admin.contextPath=/admin",
        "composer.contextPath=/composer",
        "questionnaire.contextPath=/fill",
        "spring.security.oauth2.client.registration.default.provider=test-provider",
        "spring.security.oauth2.client.registration.default.clientId=test",
        "spring.security.oauth2.client.registration.default.authorizationGrantType=authorization_code",
        "spring.security.oauth2.client.registration.default.redirectUri=redirectUri",
        "spring.security.oauth2.client.provider.test-provider.authorizationUri=authorizationUri",
        "spring.security.oauth2.client.provider.test-provider.tokenUri=tokenUri"
      )
      .withUserConfiguration(SecurityTestConfiguration.class)
      .withConfiguration(AutoConfigurations.of(
        ApplicationAutoConfiguration.class,
        DialobTenantConfigurationAutoConfiguration.class,
        DialobSettingsAutoConfiguration.class,
        OAuth2ClientAutoConfiguration.class,
        ThymeleafAutoConfiguration.class
      ))
      .run(context -> {
        assertThat(context)
          .hasSingleBean(CurrentTenant.class)
          .hasSingleBean(DialobProgramExceptionHandlers.class)
          .doesNotHaveBean(ComposerController.class)
          .doesNotHaveBean(ReviewController.class)
          .doesNotHaveBean(AdminController.class)
          .doesNotHaveBean(FillController.class)
          .doesNotHaveBean(ReviewSecurityConfigurer.class)
          .doesNotHaveBean(QuestionnaireSecurityConfigurer.class)
          .doesNotHaveBean(ComposerSecurityConfigurer.class)
          .doesNotHaveBean(WebApiSecurityConfigurer.class)
          .doesNotHaveBean(AdminSecurityConfigurer.class)
          .hasSingleBean(ApiServiceSecurityConfigurer.class)
        ;
      });
  }

}
