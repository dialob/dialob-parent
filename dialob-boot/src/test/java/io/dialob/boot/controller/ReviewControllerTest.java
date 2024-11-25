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
package io.dialob.boot.controller;

import io.dialob.api.questionnaire.ImmutableQuestionnaireMetadata;
import io.dialob.boot.security.SecurityConfiguration;
import io.dialob.boot.settings.*;
import io.dialob.form.service.api.FormDatabase;
import io.dialob.questionnaire.service.api.ImmutableMetadataRow;
import io.dialob.questionnaire.service.api.QuestionnaireDatabase;
import io.dialob.security.spring.tenant.TenantAccessEvaluator;
import io.dialob.security.tenant.CurrentTenant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.AdditionalAnswers;
import org.springframework.boot.autoconfigure.security.oauth2.client.servlet.OAuth2ClientAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

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
  ReviewControllerTest.Config.class,
  SecurityConfiguration.class,
  ReviewController.class,
  ReviewApplicationSettings.class,
  AdminApplicationSettings.class,
  ComposerApplicationSettings.class,
  QuestionnaireApplicationSettings.class,
  OAuth2ClientAutoConfiguration.class
})
class ReviewControllerTest extends AbstractUIControllerTest {

  @Configuration(proxyBeanMethods = false)
  public static class Config {
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

  @MockBean
  QuestionnaireDatabase questionnaireDatabase;

  @MockBean
  FormDatabase formDatabase;

  @MockBean
  public TenantAccessEvaluator tenantAccessEvaluator;

  @MockBean
  public CurrentTenant currentTenant;

  @MockBean
  public GrantedAuthoritiesMapper grantedAuthoritiesMapper;

  @BeforeEach
  public void beforeEach() {
    doReturn(true).when(tenantAccessEvaluator).doesUserHaveAccessToTenant(any());
    when(grantedAuthoritiesMapper.mapAuthorities(anyCollection())).thenAnswer(AdditionalAnswers.returnsFirstArg());
  }


  @Test
  @WithMockUser(username = "testUser", authorities = {"admin", "itest"})
  public void test() throws Exception {
    when(questionnaireDatabase.findMetadata(null, "123")).thenReturn(ImmutableMetadataRow.builder().id("123").value(ImmutableQuestionnaireMetadata.builder().formId("321").tenantId("xx").build()).build());

    mockMvc.perform(get("/review/123").params(tenantParam).accept(MediaType.TEXT_HTML))
      .andExpect(status().isOk())
      .andExpect(content().string(containsString("<title>Dialob</title>")))
      .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
      .andExpect(content().encoding("UTF-8"))
      .andExpect(content().string(containsString("\"questionnaireId\":\"123\"")))
      .andExpect(content().string(containsString("\"apiUrl\":\"\\/api\"")))
      .andReturn();
    verify(questionnaireDatabase, times(1)).findMetadata(null, "123");
    verifyNoMoreInteractions(questionnaireDatabase);

  }

  @Test
  @WithMockUser(username = "testUser", authorities = {"admin", "itest"})
  public void shouldNotAcceptInvalidId() throws Exception {

    mockMvc.perform(get("/review/fgerfe").params(tenantParam).accept(MediaType.TEXT_HTML))
      .andExpect(status().is4xxClientError());
    verifyNoMoreInteractions(questionnaireDatabase);

  }


}
