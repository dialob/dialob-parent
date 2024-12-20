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
package io.dialob.boot.rest;

import io.dialob.db.spi.spring.DatabaseExceptionMapper;
import io.dialob.security.spring.oauth2.StreamingGrantedAuthoritiesMapper;
import io.dialob.security.user.CurrentUser;
import io.dialob.security.user.CurrentUserProvider;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInfo;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.WebApplicationContext;

import java.util.Collections;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

@Slf4j
public class AbstractSecuredRestTests extends AbstractFormRepositoryTests {

  protected MultiValueMap<String, String> tenantParam = new LinkedMultiValueMap<>();

  public AbstractSecuredRestTests() {
    tenantParam.add("tenantId","00000000-0000-0000-0000-000000000000");
  }

  @Configuration(proxyBeanMethods = false)
  public static class TestConfiguration {
    @Bean
    public GrantedAuthoritiesMapper grantedAuthoritiesMapper() {
      return new StreamingGrantedAuthoritiesMapper(Collections.emptyList());
    }

    @Bean
    public DatabaseExceptionMapper databaseExceptionMapper() {
      return new DatabaseExceptionMapper();
    }

  }

  @MockitoBean
  public CurrentUserProvider currentUserProvider;

  @Inject
  public WebApplicationContext wac;

  @BeforeEach
  public void setupCurrentUser(TestInfo testInfo) {
    String methodName = testInfo.getDisplayName();
    when(currentUserProvider.get()).thenReturn(new CurrentUser(methodName, methodName, "", "", ""));
    DefaultMockMvcBuilder builder = MockMvcBuilders
      .webAppContextSetup(this.wac)
      .apply(springSecurity());
    this.mockMvc = builder.build();
  }
}
