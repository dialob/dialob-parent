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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

@EnableWebSecurity
@EnableWebMvc
public abstract class AbstractControllerTest {

  public MockMvc mockMvc;

  @Autowired
  public WebApplicationContext wac;

  protected MultiValueMap<String, String> tenantParam = new LinkedMultiValueMap<>();

  public AbstractControllerTest() {
    tenantParam.add("tenantId","00000000-0000-0000-0000-000000000000");
  }

  @BeforeEach
  public void setupCurrentUser(TestInfo testInfo) {
    String methodName = testInfo.getDisplayName();
    DefaultMockMvcBuilder builder = MockMvcBuilders
      .webAppContextSetup(this.wac)
      .apply(springSecurity());
    this.mockMvc = builder.build();
  }

}
