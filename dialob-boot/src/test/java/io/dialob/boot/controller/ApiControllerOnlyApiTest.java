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

import io.dialob.boot.security.ApiServiceSecurityConfigurer;
import io.dialob.common.Permissions;
import io.dialob.form.service.rest.FormsRestServiceController;
import io.dialob.questionnaire.service.rest.QuestionnairesRestServiceController;
import io.dialob.security.key.ApiKey;
import io.dialob.security.key.ImmutableApiKey;
import io.dialob.security.key.ServletRequestApiKeyExtractor;
import io.dialob.security.spring.AuthenticationStrategy;
import io.dialob.security.spring.apikey.*;
import io.dialob.security.spring.tenant.ImmutableTenantGrantedAuthority;
import io.dialob.security.spring.tenant.TenantAccessEvaluator;
import io.dialob.security.tenant.ImmutableTenant;
import io.dialob.security.tenant.Tenant;
import io.dialob.settings.DialobSettings;
import io.dialob.tenant.service.rest.DialobTenantServiceAutoConfiguration;
import jakarta.inject.Inject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.AdditionalAnswers;
import org.mockito.Mockito;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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
  "dialob.api.contextPath=/api",
  "spring.profiles.active=none",
  "dialob.security.enabled=true"
})
@ContextConfiguration(classes = {
  DialobTenantServiceAutoConfiguration.class,
  ApiControllerOnlyApiTest.TestConfiguration.class,
  ApiServiceSecurityConfigurer.class
})
@EnableConfigurationProperties(DialobSettings.class)
public class ApiControllerOnlyApiTest extends AbstractControllerTest {

  @Configuration(proxyBeanMethods = false)
  public static class TestConfiguration {

    @Bean
    public AuthenticationStrategy authenticationStrategy() throws Exception {
      AuthenticationStrategy authenticationStrategy = Mockito.mock(AuthenticationStrategy.class);
      Mockito.doAnswer(AdditionalAnswers.returnsFirstArg()).when(authenticationStrategy).configureAuthentication(any());
      return authenticationStrategy;
    }

    @Bean
    public AuthenticationManager authenticationManager(List<AuthenticationProvider> providerList) {
      if (providerList.isEmpty()) {
        return authentication -> authentication;
      }
      return new ProviderManager(providerList);
    }

    @Bean
    public ServletRequestApiKeyExtractor servletRequestApiKeyExtractor() {
      return new RequestHeaderApiKeyExtractor();
    }

    @Bean
    AuthenticationProvider apiKeyAuthenticationProvider(@NonNull ClientApiKeyService apiKeyService,
                                                        @NonNull ApiKeyAuthoritiesProvider apiKeyAuthoritiesProvider,
                                                        @NonNull ApiKeyValidator apiKeyValidator) {
      return new ApiKeyAuthenticationProvider(apiKeyService, apiKeyAuthoritiesProvider, apiKeyValidator);
    }

  }

  @Inject
  public AuthenticationStrategy authenticationStrategy;

  @MockBean
  public ClientRegistrationRepository clientRegistrationRepository;

  @MockBean
  public TenantAccessEvaluator tenantAccessEvaluator;

  @MockBean
  public FormsRestServiceController formsRestServiceController;

  @MockBean
  public QuestionnairesRestServiceController questionnairesRestServiceController;

  @MockBean
  public ClientApiKeyService clientApiKeyService;

  @MockBean
  public ApiKeyAuthoritiesProvider apiKeyAuthoritiesProvider;

  @MockBean
  public ApiKeyValidator apiKeyValidator;

  @BeforeEach
  public void resetMocks() {
    Mockito.reset(
      clientRegistrationRepository,
      tenantAccessEvaluator,
      formsRestServiceController,
      questionnairesRestServiceController,
      clientApiKeyService,
      apiKeyAuthoritiesProvider,
      apiKeyValidator);
  }

  @AfterEach
  public void verifyMocks() {
    verifyNoMoreInteractions(
      clientRegistrationRepository,
      tenantAccessEvaluator,
      formsRestServiceController,
      questionnairesRestServiceController,
      clientApiKeyService,
      apiKeyAuthoritiesProvider,
      apiKeyValidator);
  }

  @Test
  public void cannotAccessFormsWithoutApiKey() throws Exception {
    mockMvc.perform(get("/api/forms").accept(MediaType.APPLICATION_JSON))
      .andExpect(status().isForbidden())
      .andReturn();
    mockMvc.perform(get("/api/questionnaires").accept(MediaType.APPLICATION_JSON))
      .andExpect(status().isForbidden())
      .andReturn();
  }

  @Test
  public void canAccessFormsWithApiKey() throws Exception {
    ApiKey loadedApiKey = ImmutableApiKey.builder()
      .clientId("30313233-3435-3637-3839-313233343536")
      .tenantId("00000000-0000-0000-0000-000000000000")
      .hash("abc")
      .build();
    doReturn(Optional.of(loadedApiKey)).when(clientApiKeyService).findByClientId("30313233-3435-3637-3839-313233343536");
    doNothing().when(apiKeyValidator).validateApiKey(eq(loadedApiKey), any(ApiKey.class));
    doReturn(Arrays.asList(
      new SimpleGrantedAuthority(Permissions.FORMS_GET),
      new SimpleGrantedAuthority(Permissions.QUESTIONNAIRES_GET),
      ImmutableTenantGrantedAuthority.of("00000000-0000-0000-0000-000000000000", "00000000-0000-0000-0000-000000000000")))
      .when(apiKeyAuthoritiesProvider).loadAuthorities(loadedApiKey);
    doReturn(true).when(tenantAccessEvaluator).doesUserHaveAccessToTenant(ImmutableTenant.of("00000000-0000-0000-0000-000000000000", Optional.of("00000000-0000-0000-0000-000000000000")));

    doReturn(ResponseEntity.ok(Collections.emptyList())).when(formsRestServiceController).getForms(null);
    doReturn(ResponseEntity.ok(Collections.emptyList())).when(questionnairesRestServiceController).getQuestionnaires(null, null, null, null, null);

    mockMvc.perform(get("/api/forms").accept(MediaType.APPLICATION_JSON).header("x-api-key","MDEyMzQ1Njc4OTEyMzQ1NmFiYw=="))
      .andExpect(status().isOk())
      .andExpect(content().string(containsString("[]")))
      .andReturn();

    mockMvc.perform(get("/api/questionnaires").accept(MediaType.APPLICATION_JSON).header("x-api-key","MDEyMzQ1Njc4OTEyMzQ1NmFiYw=="))
      .andExpect(status().isOk())
      .andExpect(content().string(containsString("[]")))
      .andReturn();

    verify(clientApiKeyService, times(2)).findByClientId("30313233-3435-3637-3839-313233343536");
    verify(apiKeyValidator, times(2)).validateApiKey(eq(loadedApiKey), any(ApiKey.class));
    verify(apiKeyAuthoritiesProvider, times(2)).loadAuthorities(loadedApiKey);
    verify(tenantAccessEvaluator, times(2)).doesUserHaveAccessToTenant(any(Tenant.class));
    verify(formsRestServiceController).getForms(null);
    verify(questionnairesRestServiceController).getQuestionnaires(null, null, null, null, null);

  }


  @Test
  public void fetchTenantsWithApiKey() throws Exception {
    ApiKey loadedApiKey = ImmutableApiKey.builder()
      .clientId("30313233-3435-3637-3839-313233343536")
      .tenantId("00000000-0000-0000-0000-000000000000")
      .hash("abc")
      .build();
    doReturn(Optional.of(loadedApiKey)).when(clientApiKeyService).findByClientId("30313233-3435-3637-3839-313233343536");
    doNothing().when(apiKeyValidator).validateApiKey(eq(loadedApiKey), any(ApiKey.class));
    doReturn(Arrays.asList(
      ImmutableTenantGrantedAuthority.of("00000000-0000-0000-0000-000000000000", "00000000-0000-0000-0000-000000000000")))
      .when(apiKeyAuthoritiesProvider).loadAuthorities(loadedApiKey);
    doReturn(true).when(tenantAccessEvaluator).doesUserHaveAccessToTenant(ImmutableTenant.of("00000000-0000-0000-0000-000000000000", Optional.of("00000000-0000-0000-0000-000000000000")));

    mockMvc.perform(get("/api/tenants").accept(MediaType.APPLICATION_JSON).header("x-api-key","MDEyMzQ1Njc4OTEyMzQ1NmFiYw=="))
      .andExpect(status().isOk())
      .andExpect(content().string(containsString("[{\"id\":\"00000000-0000-0000-0000-000000000000\",\"name\":\"00000000-0000-0000-0000-000000000000\"}]")))
      .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON_VALUE))
      .andReturn();

    verify(clientApiKeyService).findByClientId("30313233-3435-3637-3839-313233343536");
    verify(apiKeyValidator).validateApiKey(eq(loadedApiKey), any(ApiKey.class));
    verify(apiKeyAuthoritiesProvider).loadAuthorities(loadedApiKey);
    verify(tenantAccessEvaluator).doesUserHaveAccessToTenant(any(Tenant.class));

  }

}
