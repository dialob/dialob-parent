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
package io.dialob.boot.rest;

import edu.umd.cs.findbugs.annotations.NonNull;
import io.dialob.api.form.Form;
import io.dialob.api.form.ImmutableFormMetadata;
import io.dialob.boot.Application;
import io.dialob.boot.security.SecurityConfiguration;
import io.dialob.boot.settings.AdminApplicationSettings;
import io.dialob.boot.settings.ComposerApplicationSettings;
import io.dialob.boot.settings.QuestionnaireApplicationSettings;
import io.dialob.boot.settings.ReviewApplicationSettings;
import io.dialob.form.service.DialobFormServiceAutoConfiguration;
import io.dialob.form.service.api.FormDatabase;
import io.dialob.form.service.api.repository.FormListItem;
import io.dialob.form.service.rest.DialobFormServiceRestAutoConfiguration;
import io.dialob.integration.api.event.FormUpdatedEvent;
import io.dialob.integration.queue.DialobIntegrationQueueAutoConfiguration;
import io.dialob.questionnaire.service.api.QuestionnaireDatabase;
import io.dialob.questionnaire.service.api.session.FormFinder;
import io.dialob.rest.RestApiExceptionMapper;
import io.dialob.rule.parser.function.FunctionRegistry;
import io.dialob.security.UUIDUtils;
import io.dialob.security.spring.DialobSecuritySpringAutoConfiguration;
import io.dialob.security.tenant.CurrentTenant;
import io.dialob.security.tenant.Tenant;
import io.dialob.security.user.CurrentUser;
import io.dialob.security.user.CurrentUserProvider;
import io.dialob.settings.DialobSettings;
import io.dialob.spring.boot.engine.DialobSessionEngineAutoConfiguration;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.autoconfigure.validation.ValidationAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.ServletWebServerFactoryAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.event.EventListener;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = RANDOM_PORT, properties = {
  "spring.jackson.deserialization.READ_DATE_TIMESTAMPS_AS_NANOSECONDS=false",
  "spring.jackson.serialization.WRITE_DATES_AS_TIMESTAMPS=false",
  "spring.jackson.serialization.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS=false",
  "spring.autoconfigure.exclude[0]=org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration",
  "spring.autoconfigure.exclude[1]=org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration",
  "dialob.security.authenticationMethod=NONE",
  "dialob.security.enabled=true",
  "dialob.db.database-type=none"
})
@ContextConfiguration(classes = {
  AbstractSecuredRestTests.TestConfiguration.class,
  Application.class,
  FormsRestServiceControllerApiKeyTest.TestConfiguration.class,
  ServletWebServerFactoryAutoConfiguration.class,
  SecurityConfiguration.class,
  DialobFormServiceRestAutoConfiguration.class,
  DialobSessionEngineAutoConfiguration.class,
  DialobFormServiceAutoConfiguration.class,
  DialobSecuritySpringAutoConfiguration.class,
  DialobIntegrationQueueAutoConfiguration.class,
  ValidationAutoConfiguration.class,
  RestApiExceptionMapper.class

})
@EnableConfigurationProperties({
  DialobSettings.class,
  ComposerApplicationSettings.class,
  QuestionnaireApplicationSettings.class,
  AdminApplicationSettings.class,
  ReviewApplicationSettings.class
})
public class FormsRestServiceControllerApiKeyTest {

  @MockitoBean
  private CurrentTenant currentTenant;
  @MockitoBean
  private FunctionRegistry functionRegistry;

  public interface ListenerMock {
    @EventListener
    void onFormUpdatedEvent(FormUpdatedEvent event);
  }

  public String tenantId = "00000000-0000-0000-0000-000000000000";

  @BeforeEach
  void setupTenant() {
    when(currentTenant.getId()).thenReturn(tenantId);
    when(currentTenant.get()).thenReturn(Tenant.of(tenantId));
  }

  @Configuration(proxyBeanMethods = false)
  public static class TestConfiguration {

    @Primary
    @Bean
    public QuestionnaireDatabase questionnaireDatabase() {
      return Mockito.mock(QuestionnaireDatabase.class);
    }
    @Bean
    public FormFinder formFinder(FormDatabase formDatabase, CurrentTenant currentTenant) {
      return (formId, formRev) -> formDatabase.findOne(currentTenant.getId(), formId, formRev);
    }
  }

  @MockitoBean
  private FormDatabase formDatabase;

  @Inject
  private ApplicationEventPublisher applicationEventPublisher;

  @MockitoBean
  private ListenerMock listenerMock;

  @LocalServerPort
  private Integer port;

  @MockitoBean
  private CurrentUserProvider currentUserProvider;

  @BeforeEach
  public void resetMocks(TestInfo testInfo) {
    reset(formDatabase, listenerMock);
    String testMethodName = testInfo.getTestMethod().get().getName();
    when(currentUserProvider.get()).thenReturn(new CurrentUser(testMethodName, testMethodName,"","",""));
  }

  RestTemplate restTemplate = new RestTemplate();

  private HttpEntity createHttpEntity(UUID clientId, String clientSecret) {
    HttpHeaders httpHeaders = new HttpHeaders();
    byte[] secretBytes = clientSecret.getBytes();
    byte[] token = new byte[16 + secretBytes.length];

    System.arraycopy(UUIDUtils.toBytes(clientId), 0, token, 0, 16);
    System.arraycopy(secretBytes,                 0, token, 16, secretBytes.length);
    /// UUIDUtils.toBytes(clientId)) + "." + token

    httpHeaders.set("x-api-key", Base64.getEncoder().encodeToString(token));
    httpHeaders.set(HttpHeaders.CONTENT_TYPE, org.springframework.http.MediaType.APPLICATION_JSON_VALUE);
    return new HttpEntity(httpHeaders);
  }

  @Test
  public void shouldLookupFormsFromRepository() throws Exception {
    doAnswer(invocation -> {
      Consumer consumer = (Consumer) invocation.getArguments()[2];
      consumer.accept(new FormDatabase.FormMetadataRow() {
        @NonNull
        @Override
        public String getId() {
          return "1";
        }

        @NonNull
        @Override
        public Form.Metadata getValue() {
          return ImmutableFormMetadata.builder().label("l1").build();
        }
      });
      consumer.accept(new FormDatabase.FormMetadataRow() {
        @NonNull
        @Override
        public String getId() {
          return "2";
        }

        @NonNull
        @Override
        public Form.Metadata getValue() {
          return ImmutableFormMetadata.builder().label("l2").build();
        }
      });
      return null;
    }).when(formDatabase).findAllMetadata(anyString(), isNull(), any(Consumer.class));

    HttpEntity httpEntity = createHttpEntity(UUID.fromString("00000000-0000-0000-0000-000000000000"),"localsecret");

    ResponseEntity<List<FormListItem>> response = restTemplate.exchange("http://localhost:" + port + "/api/forms", HttpMethod.GET, httpEntity, new ParameterizedTypeReference<>() { });
    assertEquals(200, response.getStatusCodeValue());
    verify(formDatabase, times(1)).findAllMetadata(anyString(), isNull(), any());
    List<FormListItem> r = response.getBody();
    assertEquals(2, r.size());
    assertEquals("l1",r.get(0).getMetadata().getLabel());
    assertEquals("l2",r.get(1).getMetadata().getLabel());

    verify(formDatabase, only()).findAllMetadata(anyString(), isNull(), any());
  }

  @Test
  public void shouldRejectInvalidKey() throws Exception {
    HttpEntity httpEntity = createHttpEntity(UUID.fromString("00000000-0000-0000-0000-000000000000"),"wrongtoken");
    Assertions.assertThrows(HttpClientErrorException.class, () -> {
      try {
        ResponseEntity<List<FormListItem>> response = restTemplate.exchange("http://localhost:" + port + "/api/forms", HttpMethod.GET, httpEntity, new ParameterizedTypeReference<List<FormListItem>>() {});
      } catch (HttpClientErrorException e) {
        assertEquals(403, e.getRawStatusCode());
        throw e;
      }
    });
  }

}
