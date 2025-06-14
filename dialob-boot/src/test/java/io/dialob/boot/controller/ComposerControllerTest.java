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
package io.dialob.boot.controller;

import io.dialob.boot.security.SecurityConfiguration;
import io.dialob.boot.settings.*;
import io.dialob.questionnaire.service.api.QuestionnaireDatabase;
import io.dialob.security.spring.tenant.TenantAccessEvaluator;
import io.dialob.security.tenant.CurrentTenant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.AdditionalAnswers;
import org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2ClientAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.util.Optional;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.Mockito.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.MOCK;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = MOCK, properties = {
  "spring.profiles.active=ui",
  "tenantId=itest",
  "spring.jackson.deserialization.READ_DATE_TIMESTAMPS_AS_NANOSECONDS=false",
  "spring.jackson.serialization.WRITE_DATES_AS_TIMESTAMPS=false",
  "spring.jackson.serialization.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS=false",
  "dialob.db.database-type=none",
  "spring.security.oauth2.client.registration[0].provider=own",
  "spring.security.oauth2.client.registration[0].clientId=cl1",
  "spring.security.oauth2.client.registration[0].clientSecret=xxx",
  "spring.security.oauth2.client.registration[0].authorizationGrantType=authorization_code",
  "spring.security.oauth2.client.registration[0].redirectUri=/login",
  "spring.security.oauth2.client.registration[0].scope[0]=openid",
  "spring.security.oauth2.client.registration[0].clientName=test",
  "spring.security.oauth2.client.provider[own].authorizationUri=http://localhost:880",
  "spring.security.oauth2.client.provider[own].tokenUri=http://localhost:880",
  "spring.security.oauth2.client.provider[own].jwkSetUri=http://localhost:880"
})
@ContextConfiguration(classes = {
  SecurityConfiguration.class,
  ComposerController.class,
  OAuth2ClientAutoConfiguration.class,
  ComposerControllerTest.Config.class
})
@EnableConfigurationProperties({
  AdminApplicationSettings.class,
  ComposerApplicationSettings.class,
  QuestionnaireApplicationSettings.class,
  ReviewApplicationSettings.class,
})
@EnableWebSecurity
@EnableWebMvc
class ComposerControllerTest extends AbstractUIControllerTest {

  @Configuration(proxyBeanMethods = false)
  public static class Config {

    @Bean
    public TenantAccessEvaluator tenantAccessEvaluator() {
      return TenantAccessEvaluator.ALL_ACCESS_EVALUATOR;
    }

    @Bean
    public PageSettingsProvider settingsPageSettingsProvider(CurrentTenant currentTenant,
                                                             QuestionnaireDatabase questionnaireDatabase,
                                                             QuestionnaireApplicationSettings settings,
                                                             ReviewApplicationSettings reviewSettings,
                                                             Optional<AdminApplicationSettings> adminApplicationSettings,
                                                             ComposerApplicationSettings composerApplicationSettings) {
      return new SettingsPageSettingsProvider(currentTenant, questionnaireDatabase, settings, reviewSettings, composerApplicationSettings, adminApplicationSettings);
    }

  }
  @MockitoBean
  CurrentTenant currentTenant;

  @MockitoBean
  QuestionnaireDatabase questionnaireDatabase;

  @MockitoBean
  public TenantAccessEvaluator tenantAccessEvaluator;

  @MockitoBean
  public PageSettingsProvider pageSettingsProvider;

  @MockitoBean
  public GrantedAuthoritiesMapper grantedAuthoritiesMapper;

  @BeforeEach
  public void beforeEach() {
    doReturn(true).when(tenantAccessEvaluator).doesUserHaveAccessToTenant(any());
    when(grantedAuthoritiesMapper.mapAuthorities(anyCollection())).thenAnswer(AdditionalAnswers.returnsFirstArg());
  }


  @Test
  @WithMockUser(username = "testUser", authorities = {"composer.view", "itest"})
  void test() throws Exception {

    PageAttributes pageAttributes = mock(PageAttributes.class);
    when(pageSettingsProvider.findPageSettings("composer")).thenReturn(pageAttributes);

    mockMvc.perform(get("/composer/").params(tenantParam).accept(MediaType.TEXT_HTML))
      .andExpect(status().isOk())
      .andExpect(content().string(containsString("<title>Dialob: Composer</title>")))
      .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
      .andExpect(content().encoding("UTF-8"))
      .andExpect(content().string(containsString("\"filling_app_url\":\"\\/fill\"")))
      .andExpect(content().string(containsString("\"backend_api_url\":\"\\/api\"")))
      .andExpect(content().string(containsString("\"documentation_url\":\"https:\\/\\/docs.dialob.io\"")))
      .andReturn();
  }

}
