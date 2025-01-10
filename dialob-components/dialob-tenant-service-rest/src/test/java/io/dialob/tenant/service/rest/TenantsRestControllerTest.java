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
package io.dialob.tenant.service.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import io.dialob.security.tenant.Tenant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.DelegatingWebMvcConfiguration;

import java.util.Arrays;
import java.util.Collections;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(SpringExtension.class)
@SpringBootTest
@ContextConfiguration(classes = {
  TenantsRestController.class,
  DelegatingWebMvcConfiguration.class})
@EnableConfigurationProperties(ServerProperties.class)
class TenantsRestControllerTest {

  @Configuration(proxyBeanMethods = false)
  public static class TestConfiguration {

    @Bean
    public ObjectMapper objectMapper() {
      return new ObjectMapper()
        .registerModule(new Jdk8Module());
    }
  }

  public MockMvc mockMvc;

  @MockitoBean
  TenantsProvider tenantsProvider;

  @Autowired
  WebApplicationContext wac;

  @BeforeEach
  public void setupCurrentUser() {
    this.mockMvc = MockMvcBuilders
      .webAppContextSetup(this.wac).build();
  }

  @Test
  public void shouldReturnEmptyTenants() throws Exception {
    Mockito.when(tenantsProvider.getTenants()).thenReturn(Collections.emptyList());
    mockMvc
      .perform(get("/tenants")
        .accept(MediaType.APPLICATION_JSON))
      .andExpect(status().isOk())
      .andExpect(content().string("[]"))
      .andReturn();
  }

  @Test
  public void shouldReturnListOfTenants() throws Exception {
    Mockito.when(tenantsProvider.getTenants()).thenReturn(Arrays.asList(
      Tenant.of("0123-321", "hello"),
      Tenant.of("0123-322")
    ));
    mockMvc
      .perform(get("/tenants")
        .accept(MediaType.APPLICATION_JSON))
      .andExpect(status().isOk())
      .andExpect(content().string("[{\"id\":\"0123-321\",\"name\":\"hello\"},{\"id\":\"0123-322\"}]"))
      .andReturn();
  }
}
