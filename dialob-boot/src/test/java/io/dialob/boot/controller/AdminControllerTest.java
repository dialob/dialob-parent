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
import io.dialob.boot.settings.AdminApplicationSettings;
import io.dialob.boot.settings.ComposerApplicationSettings;
import io.dialob.boot.settings.QuestionnaireApplicationSettings;
import io.dialob.boot.settings.ReviewApplicationSettings;
import io.dialob.security.spring.tenant.TenantAccessEvaluator;
import io.dialob.settings.DialobSettings;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.AdditionalAnswers;
import org.springframework.boot.autoconfigure.security.oauth2.client.servlet.OAuth2ClientAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.Mockito.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.MOCK;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = MOCK, properties = {
  "tenantId=itest",
  "dialob.security.enabled=true",
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
}, classes = {
  SecurityConfiguration.class,
  AdminController.class,
  OAuth2ClientAutoConfiguration.class
})
@EnableConfigurationProperties({
  DialobSettings.class,
  AdminApplicationSettings.class,
  ComposerApplicationSettings.class,
  QuestionnaireApplicationSettings.class,
  ReviewApplicationSettings.class
})
class AdminControllerTest extends AbstractUIControllerTest {

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
  @WithMockUser(username = "testUser", authorities = {"manager.view"})
  void adminShouldGetPage() throws Exception {
    PageAttributes pageAttributes = mock(PageAttributes.class);
    when(pageSettingsProvider.findPageSettings("admin")).thenReturn(pageAttributes);
    when(pageAttributes.getTemplate()).thenReturn("admin");
    mockMvc.perform(get("/").params(tenantParam).accept(MediaType.TEXT_HTML))
      .andExpect(status().isOk())
      .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
      .andExpect(content().encoding("UTF-8"))
      .andExpect(xpath("//title").string("Dialob"))
      .andExpect(xpath("//div[@id='root']").exists())
      .andReturn();
  }

  @Test
  @WithMockUser(username = "testUser", authorities = {"manager.view"})
  void adminShouldGetConfig() throws Exception {
    PageAttributes pageAttributes = mock(PageAttributes.class);
    when(pageSettingsProvider.findPageSettings("admin")).thenReturn(pageAttributes);
    when(pageAttributes.getTemplate()).thenReturn("admin");
    mockMvc.perform(get("/config.json").params(tenantParam).accept(MediaType.APPLICATION_JSON))
      .andExpect(status().isOk())
      .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
      .andExpect(content().encoding("UTF-8"))
      .andExpect(jsonPath("$.url").value("/api"))
      .andExpect(jsonPath("$.documentation").value("https://docs.dialob.io"))
      .andExpect(jsonPath("$.fillUrl").value("/fill"))
      .andExpect(jsonPath("$.reviewUrl").value("/review"))
      .andExpect(jsonPath("$.csrf.parameterName").value("_csrf"))
      .andExpect(jsonPath("$.csrf.headerName").value("X-CSRF-TOKEN"))
      .andExpect(jsonPath("$.csrf.token").isNotEmpty()) // Ensure the token exists but don't hardcode it
      .andExpect(jsonPath("$.composerUrl").value("/composer"))
      .andExpect(jsonPath("$.tenantId").value("00000000-0000-0000-0000-000000000000"))
      .andExpect(jsonPath("$.versioning").value(false))
      .andReturn();
  }

  @Test
  void shouldRedirectToAuthorization() throws Exception {
    mockMvc.perform(get("/").params(tenantParam).accept(MediaType.TEXT_HTML))
      .andDo(print())
      .andExpect(status().isFound())
      .andExpect(redirectedUrlPattern("**/oauth2/authorization/default"))
      .andReturn();
  }

}
