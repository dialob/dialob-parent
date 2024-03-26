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

import io.dialob.boot.controller.*;
import io.dialob.boot.security.SecurityConfiguration;
import io.dialob.boot.settings.*;
import io.dialob.questionnaire.service.api.QuestionnaireDatabase;
import io.dialob.security.tenant.CurrentTenant;
import jakarta.annotation.PostConstruct;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.security.oauth2.client.endpoint.DefaultAuthorizationCodeTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AccessTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest;
import org.springframework.session.data.redis.config.ConfigureRedisAction;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.templateresolver.UrlTemplateResolver;

import java.util.Optional;

@Configuration(proxyBeanMethods = false)
@Import({SecurityConfiguration.class})
public class ApplicationAutoConfiguration {

  @Configuration(proxyBeanMethods = false)
  @Profile("ui")
  @Import({
    AdminController.class,
    FillController.class,
    LandingController.class,
    ReviewController.class,
    ComposerController.class,
    GlobalModelAttributesInjector.class,
  })
  @EnableConfigurationProperties({
    AdminApplicationSettings.class,
    ComposerApplicationSettings.class,
    LandingApplicationSettings.class,
    QuestionnaireApplicationSettings.class,
    ReviewApplicationSettings.class
  })
  public static class UIConfiguration {

    @Configuration(proxyBeanMethods = false)
    public static class CustomWebMvcConfigurer implements WebMvcConfigurer {

      private final GlobalModelAttributesInjector globalModelAttributesInjector;

      private final SpringTemplateEngine springTemplateEngine;

      public CustomWebMvcConfigurer(GlobalModelAttributesInjector globalModelAttributesInjector,
                                    SpringTemplateEngine springTemplateEngine) {
        this.globalModelAttributesInjector = globalModelAttributesInjector;
        this.springTemplateEngine = springTemplateEngine;
      }

      @Override
      public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(globalModelAttributesInjector);
      }

      @PostConstruct
      public void templateEngine() {
        if(springTemplateEngine != null) {
          UrlTemplateResolver urlTemplateResolver = new UrlTemplateResolver();
          urlTemplateResolver.setOrder(0);
          urlTemplateResolver.setTemplateMode("HTML");
          springTemplateEngine.addTemplateResolver(urlTemplateResolver);
        }
      }
    }

    @Bean
    public PageSettingsProvider settingsPageSettingsProvider(CurrentTenant currentTenant,
                                                             QuestionnaireDatabase questionnaireDatabase,
                                                             QuestionnaireApplicationSettings settings,
                                                             ReviewApplicationSettings reviewSettings,
                                                             Optional<AdminApplicationSettings> adminApplicationSettings,
                                                             ComposerApplicationSettings composerApplicationSettings) {
      return new SettingsPageSettingsProvider(
        currentTenant,
        questionnaireDatabase,
        settings,
        reviewSettings,
        composerApplicationSettings,
        adminApplicationSettings);
    }

  }

  @Bean
  public OAuth2AccessTokenResponseClient<OAuth2AuthorizationCodeGrantRequest> oauth2AccessTokenResponseClient() {
    return new DefaultAuthorizationCodeTokenResponseClient();
  }

  @Bean
  public DialobProgramExceptionHandlers dialobProgramExceptionHandlers() {
    return new DialobProgramExceptionHandlers();
  }

  // Disable redis keyspace notifications. Notifications require secured Redis instance
  // TODO Make this configurable.
  @Bean
  public ConfigureRedisAction configureRedisAction() {
    return ConfigureRedisAction.NO_OP;
  }


}
