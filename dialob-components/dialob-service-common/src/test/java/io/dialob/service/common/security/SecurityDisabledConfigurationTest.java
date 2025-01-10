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
package io.dialob.service.common.security;

import io.dialob.settings.DialobSettings;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.MOCK;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = MOCK, properties = {
  "dialob.security.enabled=false"
})
@ContextConfiguration(classes = {
  SecurityDisabledConfiguration.class
})
@EnableWebSecurity
@EnableWebMvc
@EnableConfigurationProperties(DialobSettings.class)
class SecurityDisabledConfigurationTest {

  public MockMvc mockMvc;

  @Autowired
  public WebApplicationContext wac;

  @BeforeEach
  public void setupCurrentUser(TestInfo testInfo) {
    String methodName = testInfo.getDisplayName();
    DefaultMockMvcBuilder builder = MockMvcBuilders
      .webAppContextSetup(this.wac)
      .apply(SecurityMockMvcConfigurers.springSecurity());
    this.mockMvc = builder.build();
  }

  @Test
  @WithMockUser(username = "testUser", authorities = {"admin", "itest"})
  public void shouldRejectPATCHMethod() throws Exception {
    mockMvc.perform(options("/review/123")
      .header("Access-Control-Request-Method","TRACE")
      .header("Access-Control-Request-Headers","origin, x-requested-with")
      .header("Origin","https://foo.bar.org"))
      .andExpect(content().string("Invalid CORS request"))
      .andExpect(status().isForbidden());
  }

  @Test
  public void shouldAcceptPUTMethod() throws Exception {
    mockMvc.perform(options("/review/123")
      .header("Access-Control-Request-Method","PUT")
      .header("Access-Control-Request-Headers","origin, x-requested-with")
      .header("Origin","https://foo.bar.org"))
      .andExpect(status().isOk())
      .andExpect(header().stringValues("Access-Control-Allow-Methods", "GET,HEAD,POST,PUT,DELETE"))
      .andExpect(header().stringValues("Access-Control-Allow-Headers", "origin, x-requested-with"))
      .andExpect(header().stringValues("Access-Control-Allow-Origin", "*"));
  }

}
