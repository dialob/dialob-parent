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
import io.dialob.security.aws.DialobSecurityAwsAutoConfiguration;
import io.dialob.security.spring.DialobSecuritySpringAutoConfiguration;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedGrantedAuthoritiesUserDetailsService;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.not;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.MOCK;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("aws")
@SpringBootTest(webEnvironment = MOCK, properties = {
  "tenantId=itest",
  "dialob.security.enabled=true",
  "dialob.security.authenticationMethod=AWSELB",
  "spring.jackson.deserialization.READ_DATE_TIMESTAMPS_AS_NANOSECONDS=false",
  "spring.jackson.serialization.WRITE_DATES_AS_TIMESTAMPS=false",
  "spring.jackson.serialization.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS=false",
  "dialob.db.database-type=none",
  "dialob.security.groupPermissions.oprh\\.admin=manager.view",
  "dialob.security.groups-claim=true"
})
@ContextConfiguration(classes = {
  SecurityConfiguration.class,
  AdminController.class,
  DialobSecurityAwsAutoConfiguration.class,
  DialobSecuritySpringAutoConfiguration.class,
  AdminControllerAwsElbTest.TestConfiguration.class,
})
@EnableConfigurationProperties({
  AdminApplicationSettings.class,
  ComposerApplicationSettings.class,
  QuestionnaireApplicationSettings.class,
  ReviewApplicationSettings.class,
})
class AdminControllerAwsElbTest extends AbstractUIControllerTest {

  @Configuration(proxyBeanMethods = false)
  public static class TestConfiguration {



    @Bean
    public AuthenticationUserDetailsService<PreAuthenticatedAuthenticationToken> authenticationUserDetailsService() {
      return new PreAuthenticatedGrantedAuthoritiesUserDetailsService();
    }
  }

  @MockitoBean
  public PageSettingsProvider pageSettingsProvider;


  @BeforeEach
  public void beforeEach() {
  }

  @Test
  void adminShouldGetPage() throws Exception {
    PageAttributes pageAttributes = mock(PageAttributes.class);
    when(pageSettingsProvider.findPageSettings("admin")).thenReturn(pageAttributes);
    when(pageAttributes.getTemplate()).thenReturn("admin");

    MvcResult result = mockMvc.perform(get("/")
      .params(tenantParam)
      .accept(MediaType.TEXT_HTML)
      .cookie(new Cookie("XSRF-TOKEN", "xsrf-token"))
      .header("X-Amzn-Oidc-Identity","06805268-ba62-4dc1-bbe0-8ffda94607e5")
      .header("X-Amzn-Oidc-Accesstoken","eyJraWQiOiJaanBcL0xucTBUNnZLcmJ2RW5WeHRGUThhNDM0YzJaRDJvUFFKWnZtbTdUQT0iLCJhbGciOiJSUzI1NiJ9.eyJzdWIiOiIwNjgwNTI2OC1iYTYyLTRkYzEtYmJlMC04ZmZkYTk0NjA3ZTUiLCJjb2duaXRvOmdyb3VwcyI6WyJvcHJoLmFkbWluIl0sInRva2VuX3VzZSI6ImFjY2VzcyIsInNjb3BlIjoib3BlbmlkIiwiYXV0aF90aW1lIjoxNTcwNzEwNTk1LCJpc3MiOiJodHRwczpcL1wvY29nbml0by1pZHAuZXUtY2VudHJhbC0xLmFtYXpvbmF3cy5jb21cL2V1LWNlbnRyYWwtMV9vNVR3ekk3a3EiLCJleHAiOjE1NzA3MjEwNDksImlhdCI6MTU3MDcxNzQ0OSwidmVyc2lvbiI6MiwianRpIjoiMTAyOWRjYjItMDQwOC00ZmNjLWJkYzMtMTM0OGIyYzQ3Yzk3IiwiY2xpZW50X2lkIjoiMjhmMGJqbWY3c2pkOWU0bGI1ZXQ1bWRwcWsiLCJ1c2VybmFtZSI6IjA2ODA1MjY4LWJhNjItNGRjMS1iYmUwLThmZmRhOTQ2MDdlNSJ9.lXuIkDDWtqlfPw4wb1OXWNkA5tLyGjSKa5pL-Bds94bcotUI_IO2w9vQc8aR6WFEK6bnflMTAIZ81GHlmcLI_H_XY1nj7ftxaF28dwpk-oiDOEdUhc4SZY-5nu3EMxPkC3pN3d-waJ4bru7Pv6HdNAvfuR5YHRKvVjBfiO3OW2yFzLDJtKz2kPbyqYBcyt6f6zvAJ0Z1Xuh4Tm6jwjTWIbxI1ppVkkpHJ5xLeeJZiRCpgLvAY5rW9Qql25atRpmBjpMUJaEkEwTBnimf4G6S6aVbfrFTX0DYBoeci0jBxeLaCMCwiQPMJg637irO9r23GxGQgt_mubjp2zwzWOXL4A"))

      .andExpect(status().isOk())
      .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
      .andExpect(content().encoding("UTF-8"))
      .andExpect(xpath("//title").string("Dialob"))
      .andExpect(xpath("//div[@id='root']").exists())
      .andExpect(header().string(
        "Set-Cookie",
        not(containsString("XSRF-TOKEN=;")) // Make sure CSRF token is not reset
      )).andReturn();
  }
}
