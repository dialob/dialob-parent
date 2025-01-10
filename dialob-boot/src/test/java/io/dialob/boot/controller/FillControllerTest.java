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

import edu.umd.cs.findbugs.annotations.NonNull;
import io.dialob.api.questionnaire.ImmutableQuestionnaireMetadata;
import io.dialob.boot.security.OAuth2AuthenticationStrategy;
import io.dialob.boot.security.QuestionnaireSecurityConfigurer;
import io.dialob.boot.settings.*;
import io.dialob.questionnaire.service.api.ImmutableMetadataRow;
import io.dialob.questionnaire.service.api.QuestionnaireDatabase;
import io.dialob.security.spring.AuthenticationStrategy;
import io.dialob.security.spring.tenant.TenantAccessEvaluator;
import io.dialob.security.tenant.CurrentTenant;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.oauth2.client.endpoint.OAuth2AccessTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.not;
import static org.mockito.Mockito.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.MOCK;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = MOCK, properties = {
  "spring.profiles.active=ui,test",
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
  FillControllerTest.Config.class,
  FillController.class,
  QuestionnaireApplicationSettings.class,
  ReviewApplicationSettings.class,
  ComposerApplicationSettings.class,
  GlobalModelAttributesInjector.class
})
class FillControllerTest extends AbstractUIControllerTest {


  @Configuration(proxyBeanMethods = false)
  @Import(QuestionnaireSecurityConfigurer.class)
  public static class Config {

    @Bean
    public TenantAccessEvaluator tenantAccessEvaluator() {
      return tenant -> true;
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

    @Bean
    public AuthenticationStrategy authenticationStrategy(@NonNull GrantedAuthoritiesMapper grantedAuthoritiesMapper,
                                                         @NonNull OAuth2AccessTokenResponseClient<OAuth2AuthorizationCodeGrantRequest> accessTokenResponseClient) {
      return new OAuth2AuthenticationStrategy(grantedAuthoritiesMapper, accessTokenResponseClient);
    }
  }

  @MockitoBean
  ClientRegistrationRepository clientRegistrationRepository;

  @MockitoBean
  QuestionnaireDatabase questionnaireDatabase;

  @MockitoBean
  GrantedAuthoritiesMapper grantedAuthoritiesMapper;

  @MockitoBean
  OAuth2AccessTokenResponseClient<OAuth2AuthorizationCodeGrantRequest> accessTokenResponseClient;

  @MockitoBean
  CurrentTenant currentTenant;

  @Test
  public void shouldRenderFillPageFromURLTemplate() throws Exception {
    when(questionnaireDatabase.findMetadata(null, "123")).thenReturn(ImmutableMetadataRow.builder().id("123").value(ImmutableQuestionnaireMetadata.builder().formId("321").tenantId("xx").build()).build());

    mockMvc.perform(get("/fill/123").params(tenantParam).accept(MediaType.TEXT_HTML))
//      .andExpect(header().string("location",""))
      .andExpect(status().isOk())
      .andExpect(content().string(containsString("<title>Dialob</title>")))
      .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
      .andExpect(content().encoding("UTF-8"))
      .andExpect(content().string(containsString("\"reviewUrl\":\"\\/review\\/123\"")))
      .andExpect(content().string(containsString("\"url\":\"\\/session\\/socket\\/123\"")))
      .andExpect(content().string(containsString("\"restUrl\":\"\\/session\\/dialob\\/123\"")))
      .andExpect(content().string(containsString("\"backendApiUrl\":\"\\/api\"")))
      .andExpect(content().string(containsString("window.dialobOptions")))
      .andExpect(content().string(containsString("javaasc")))
      .andExpect(content().string(containsString("csss")))
      .andReturn();

    verify(questionnaireDatabase, times(1)).findMetadata(null, "123");
    verifyNoMoreInteractions(questionnaireDatabase);
  }

  @Test
  public void shouldRenderFillPageFromDefaultTemplateWhenTenantIsNotDefined() throws Exception {
    when(questionnaireDatabase.findMetadata(null, "123")).thenReturn(ImmutableMetadataRow.builder().id("123").value(ImmutableQuestionnaireMetadata.builder().formId("321").tenantId("yy").build()).build());

    mockMvc.perform(get("/fill/123").params(tenantParam).accept(MediaType.TEXT_HTML))
      .andExpect(status().isOk())
      .andExpect(content().string(containsString("<title>Dialob</title>")))
      .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
      .andExpect(content().encoding("UTF-8"))
      .andExpect(content().string(containsString("\"reviewUrl\":\"\\/review\\/123\"")))
      .andExpect(content().string(containsString("\"url\":\"\\/session\\/socket\\/123\"")))
      .andExpect(content().string(containsString("\"restUrl\":\"\\/session\\/dialob\\/123\"")))
      .andExpect(content().string(containsString("\"backendApiUrl\":\"\\/api\"")))
      .andExpect(content().string(not(containsString("javaasc"))))
      .andExpect(content().string(not(containsString("csss"))))
      .andReturn();

    verify(questionnaireDatabase, times(1)).findMetadata(null, "123");
    verifyNoMoreInteractions(questionnaireDatabase);
  }

  @Test
  public void shouldRejectInvalidId() throws Exception {
    mockMvc.perform(get("/fill/abc\\123").params(tenantParam).accept(MediaType.TEXT_HTML))
      .andExpect(status().isBadRequest());
    verifyNoInteractions(questionnaireDatabase);
  }

}
