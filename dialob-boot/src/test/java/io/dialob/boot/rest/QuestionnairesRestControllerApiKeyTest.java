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

import io.dialob.api.questionnaire.Questionnaire;
import io.dialob.boot.Application;
import io.dialob.boot.security.SecurityConfiguration;
import io.dialob.boot.settings.AdminApplicationSettings;
import io.dialob.boot.settings.ComposerApplicationSettings;
import io.dialob.boot.settings.QuestionnaireApplicationSettings;
import io.dialob.boot.settings.ReviewApplicationSettings;
import io.dialob.form.service.api.FormDatabase;
import io.dialob.form.service.api.repository.FormListItem;
import io.dialob.integration.api.event.FormUpdatedEvent;
import io.dialob.questionnaire.service.api.QuestionnaireDatabase;
import io.dialob.questionnaire.service.api.session.FormFinder;
import io.dialob.questionnaire.service.rest.DialobQuestionnaireServiceRestAutoConfiguration;
import io.dialob.rule.parser.function.FunctionRegistry;
import io.dialob.security.UUIDUtils;
import io.dialob.security.spring.oauth2.StreamingGrantedAuthoritiesMapper;
import io.dialob.security.tenant.CurrentTenant;
import io.dialob.security.user.CurrentUser;
import io.dialob.security.user.CurrentUserProvider;
import io.dialob.settings.DialobSettings;
import io.dialob.spring.boot.engine.DialobSessionEngineAutoConfiguration;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.event.EventListener;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.AopTestUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import tools.jackson.databind.ObjectMapper;

import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT, properties = {
  "spring.aop.proxy-target-class=false",
  "tenantId=localhost",
  "spring.jackson.datatype.datetime.READ_DATE_TIMESTAMPS_AS_NANOSECONDS=false",
  "spring.jackson.datatype.datetime.WRITE_DATES_AS_TIMESTAMPS=false",
  "spring.jackson.datatype.datetime.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS=false",
  "spring.autoconfigure.exclude[0]=org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration",
  "spring.autoconfigure.exclude[1]=org.springframework.boot.data.redis.autoconfigure.DataRedisAutoConfiguration",
  "dialob.security.enabled=true",
  "dialob.security.authenticationMethod=NONE",
  "dialob.db.database-type=none"
}, classes = {
  Application.class,
  QuestionnairesRestControllerApiKeyTest.TestConfiguration.class,
  DialobQuestionnaireServiceRestAutoConfiguration.class,
  DialobSessionEngineAutoConfiguration.class,
  SecurityConfiguration.class
})
@EnableConfigurationProperties({
  DialobSettings.class,
  ComposerApplicationSettings.class,
  QuestionnaireApplicationSettings.class,
  AdminApplicationSettings.class,
  ReviewApplicationSettings.class
})
class QuestionnairesRestControllerApiKeyTest {

  public interface ListenerMock {
    @EventListener
    void onFormUpdatedEvent(FormUpdatedEvent event);
  }

  @org.springframework.boot.test.context.TestConfiguration
  public static class TestConfiguration {

    @Bean
    public ObjectMapper objectMapper() {
      return new ObjectMapper();
    }

    @Bean
    public GrantedAuthoritiesMapper grantedAuthoritiesMapper() {
      return new StreamingGrantedAuthoritiesMapper(Collections.emptyList());
    }

    @Bean
    public QuestionnaireDatabase questionnaireDatabase() {
      return mock();
    }

    @Bean
    public FormDatabase formDatabase() {
      return mock();
    }

    @Bean
    public ListenerMock listenerMock() {
      return mock();
    }

    @Bean
    @Primary
    public FormFinder formFinder(FormDatabase formDatabase, CurrentTenant currentTenant) {
      return (id, rev) -> formDatabase.findOne(currentTenant.getId(), id, rev);
    }

  }

  @Inject
  private QuestionnaireDatabase questionnaireDatabase;

  @Inject
  private ApplicationEventPublisher applicationEventPublisher;

  @Inject
  private ListenerMock listenerMock;

  @LocalServerPort
  private Integer port;

  RestTemplate restTemplate = new RestTemplate();

  @MockitoBean
  private FunctionRegistry functionRegistry;

  @MockitoBean
  private CurrentTenant currentTenant;

  @MockitoBean
  private CurrentUserProvider currentUserProvider;

  @BeforeEach
  public void resetMocks(TestInfo testInfo) {
    reset(questionnaireDatabaseMock(), listenerMock);
    String testMethodName = testInfo.getTestMethod().get().getName();
    when(currentUserProvider.get()).thenReturn(new CurrentUser(testMethodName, testMethodName,"","",""));
  }

  private HttpEntity<Void> createHttpEntity(UUID clientId, String clientSecret) {
    HttpHeaders httpHeaders = new HttpHeaders();
    byte[] secretBytes = clientSecret.getBytes();
    byte[] token = new byte[16 + secretBytes.length];

    System.arraycopy(UUIDUtils.toBytes(clientId), 0, token, 0, 16);
    System.arraycopy(secretBytes,                 0, token, 16, secretBytes.length);

    httpHeaders.set("x-api-key", Base64.getEncoder().encodeToString(token));
    httpHeaders.set(HttpHeaders.CONTENT_TYPE, org.springframework.http.MediaType.APPLICATION_JSON_VALUE);
    return new HttpEntity<>(httpHeaders);
  }

  @Test
  void shouldLookupQuestionnairesFromRepository() {
    doReturn("testTenant").when(currentTenant).getId();
    doAnswer(invocation -> {
      Consumer<QuestionnaireDatabase.MetadataRow> consumer = (Consumer<QuestionnaireDatabase.MetadataRow>) invocation.getArguments()[6];
      consumer.accept(new QuestionnaireDatabase.MetadataRow("1", new Questionnaire.Metadata.Builder().formId("").label("l1").build()));
      consumer.accept(new QuestionnaireDatabase.MetadataRow("2", new Questionnaire.Metadata.Builder().formId("").label("l2").build()));
      return null;
    }).when(questionnaireDatabaseMock()).findAllMetadata(anyString(), isNull(), isNull(), isNull(), isNull(), isNull(), any(Consumer.class));

    ResponseEntity<List<FormListItem>> response = restTemplate.exchange("http://localhost:" + port + "/api/questionnaires", HttpMethod.GET, createHttpEntity(UUID.fromString("00000000-0000-0000-0000-000000000000"),"localsecret"), new ParameterizedTypeReference<>() {
    });

    assertEquals(HttpStatus.OK, response.getStatusCode());
    List<FormListItem> r = response.getBody();
    assertNotNull(r);
    assertEquals(2, r.size());
    assertEquals("l1",r.getFirst().getMetadata().getLabel());
    assertEquals("l2",r.get(1).getMetadata().getLabel());

    verify(questionnaireDatabaseMock(), times(1)).findAllMetadata(anyString(), isNull(), isNull(), isNull(), isNull(), isNull(), any());
  }

  private QuestionnaireDatabase questionnaireDatabaseMock() {
    return AopTestUtils.getTargetObject(questionnaireDatabase);
  }

  @Test
  void shouldRejectInvalidKey() {
    var httpEntity = createHttpEntity(UUID.fromString("00000000-0000-0000-0000-000000000000"),"wrongsecret");
    Assertions.assertThrows(HttpClientErrorException.class, () -> {
      try {
        restTemplate.exchange("http://localhost:" + port + "/api/questionnaires", HttpMethod.GET, httpEntity, new ParameterizedTypeReference<>() {
        });
      } catch (HttpClientErrorException e) {
        assertEquals(HttpStatus.FORBIDDEN, e.getStatusCode());
        throw e;
      }
    });
  }
}
