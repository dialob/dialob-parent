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

import io.dialob.boot.security.WebApiSecurityConfigurer;
import io.dialob.common.Permissions;
import io.dialob.form.service.rest.FormsRestServiceController;
import io.dialob.questionnaire.service.rest.QuestionnairesRestServiceController;
import io.dialob.security.spring.AuthenticationStrategy;
import io.dialob.security.spring.tenant.TenantAccessEvaluator;
import io.dialob.security.tenant.Tenant;
import io.dialob.settings.DialobSettings;
import io.dialob.tenant.service.rest.DialobTenantServiceAutoConfiguration;
import io.dialob.tenant.service.rest.TenantsRestController;
import jakarta.inject.Inject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.AdditionalAnswers;
import org.mockito.Mockito;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Collections;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.MOCK;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = MOCK, properties = {
  "spring.jackson.deserialization.READ_DATE_TIMESTAMPS_AS_NANOSECONDS=false",
  "spring.jackson.serialization.WRITE_DATES_AS_TIMESTAMPS=false",
  "spring.jackson.serialization.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS=false",
  "dialob.db.database-type=none",
  "dialob.security.enabled=true",
  "dialob.api.contextPath=/api"
})
@ContextConfiguration(classes = {
  TenantsRestController.class,
  WebApiSecurityConfigurer.class,
  DialobTenantServiceAutoConfiguration.class,
  ApiControllerTest.TestConfiguration.class
})
@EnableConfigurationProperties(DialobSettings.class)
class ApiControllerTest extends AbstractControllerTest {

  @org.springframework.boot.test.context.TestConfiguration(proxyBeanMethods = false)
  public static class TestConfiguration {

    @Bean
    public AuthenticationStrategy authenticationStrategy() throws Exception {
      AuthenticationStrategy authenticationStrategy = Mockito.mock(AuthenticationStrategy.class);
      Mockito.doAnswer(AdditionalAnswers.returnsFirstArg()).when(authenticationStrategy).configureAuthentication(any());
      return authenticationStrategy;
    }

  }

  @Inject
  public AuthenticationStrategy authenticationStrategy;

  @MockitoBean
  public ClientRegistrationRepository clientRegistrationRepository;

  @MockitoBean
  public TenantAccessEvaluator tenantAccessEvaluator;

  @MockitoBean
  public FormsRestServiceController formsRestServiceController;

  @MockitoBean
  public QuestionnairesRestServiceController questionnairesRestServiceController;

  @BeforeEach
  public void resetMocks() {
    Mockito.reset(
      clientRegistrationRepository,
      tenantAccessEvaluator,
      formsRestServiceController,
      questionnairesRestServiceController);
  }

  @AfterEach
  public void verifyMocks() {
    verifyNoMoreInteractions(
      clientRegistrationRepository,
      tenantAccessEvaluator,
      formsRestServiceController,
      questionnairesRestServiceController);
  }

  @Test
  @WithDialobUser(username = "apiUser", authorities = {}, tenants = {})
  void fetchTenantsWithoutTenantAccess() throws Exception {
    mockMvc.perform(get("/api/tenants").accept(MediaType.APPLICATION_JSON))
      .andExpect(status().isOk())
      .andExpect(content().string(containsString("[]")))
      .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON_VALUE))
      .andReturn();
  }

  @Test
  @WithDialobUser(username = "apiUser", authorities = {}, tenants = {"testi"})
  void fetchTenantsWithAccessToOneTenant() throws Exception {
    doReturn(true).when(tenantAccessEvaluator).doesUserHaveAccessToTenant(Tenant.of("testi", "testi"));

    mockMvc.perform(get("/api/tenants").accept(MediaType.APPLICATION_JSON))
      .andExpect(status().isOk())
      .andExpect(content().string(containsString("[{\"id\":\"testi\",\"name\":\"testi\"}]")))
      .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON_VALUE))
      .andReturn();

    verify(tenantAccessEvaluator).doesUserHaveAccessToTenant(Tenant.of("testi", "testi"));
  }

  @Test
  @WithDialobUser(username = "apiUser", authorities = {}, tenants = {})
  void cannotAccessFormsWithoutTenant() throws Exception {

    mockMvc.perform(get("/api/forms").accept(MediaType.APPLICATION_JSON))
      .andExpect(status().isForbidden())
      .andReturn();

    mockMvc.perform(get("/api/questionnaires").accept(MediaType.APPLICATION_JSON))
      .andExpect(status().isForbidden())
      .andReturn();

  }

  @Test
  @WithDialobUser(username = "apiUser", authorities = {Permissions.FORMS_GET, Permissions.QUESTIONNAIRES_GET}, tenants = {"testi"})
  void canAccessFormsWithTenant() throws Exception {
    doReturn(true).when(tenantAccessEvaluator).doesUserHaveAccessToTenant(Tenant.of("testi", "testi"));

    doReturn(ResponseEntity.ok(Collections.emptyList())).when(formsRestServiceController).getForms(null);
    doReturn(ResponseEntity.ok(Collections.emptyList())).when(questionnairesRestServiceController).getQuestionnaires(null, null, null, null, null);

    mockMvc.perform(get("/api/forms").accept(MediaType.APPLICATION_JSON))
      .andExpect(status().isOk())
      .andExpect(content().string(containsString("[]")))
      .andReturn();

    mockMvc.perform(get("/api/questionnaires").accept(MediaType.APPLICATION_JSON))
      .andExpect(status().isOk())
      .andExpect(content().string(containsString("[]")))
      .andReturn();

    verify(tenantAccessEvaluator, times(2)).doesUserHaveAccessToTenant(Tenant.of("testi", "testi"));
    verify(formsRestServiceController).getForms(null);
    verify(questionnairesRestServiceController).getQuestionnaires(null, null, null, null, null);

  }

}
