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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

import javax.inject.Inject;

import io.dialob.integration.redis.ProvideTestRedis;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
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
import org.springframework.lang.NonNull;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.AopTestUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import io.dialob.api.questionnaire.ImmutableQuestionnaireMetadata;
import io.dialob.api.questionnaire.Questionnaire;
import io.dialob.boot.Application;
import io.dialob.form.service.api.FormDatabase;
import io.dialob.form.service.api.repository.FormListItem;
import io.dialob.integration.api.event.FormUpdatedEvent;
import io.dialob.questionnaire.service.api.QuestionnaireDatabase;
import io.dialob.questionnaire.service.api.session.FormFinder;
import io.dialob.security.UUIDUtils;
import io.dialob.security.spring.oauth2.StreamingGrantedAuthoritiesMapper;
import io.dialob.security.spring.tenant.TenantAccessEvaluator;
import io.dialob.security.tenant.CurrentTenant;
import io.dialob.security.user.CurrentUser;
import io.dialob.security.user.CurrentUserProvider;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = RANDOM_PORT, properties = {
  "spring.aop.proxy-target-class=false",
  "tenantId=localhost",
  "spring.jackson.deserialization.READ_DATE_TIMESTAMPS_AS_NANOSECONDS=false",
  "spring.jackson.serialization.WRITE_DATES_AS_TIMESTAMPS=false",
  "spring.jackson.serialization.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS=false",
  "dialob.db.database-type=none"
})
@ContextConfiguration(classes = {Application.class, QuestionnairesRestControllerApiKeyTest.TestConfiguration.class})
public class QuestionnairesRestControllerApiKeyTest implements ProvideTestRedis {

  public interface ListenerMock {
    @EventListener
    void onFormUpdatedEvent(FormUpdatedEvent event);
  }

  @Configuration
  public static class TestConfiguration {
    @Bean
    public GrantedAuthoritiesMapper grantedAuthoritiesMapper() {
      return new StreamingGrantedAuthoritiesMapper(Arrays.asList());
    }

    @Bean
    public QuestionnaireDatabase questionnaireDatabase() {
      return Mockito.mock(QuestionnaireDatabase.class);
    }

    @Bean
    public FormDatabase formDatabase() {
      return Mockito.mock(FormDatabase.class);
    }

    @Bean
    public ListenerMock listenerMock() {
      return Mockito.mock(ListenerMock.class);
    }

    @Bean
    @Primary
    public FormFinder formFinder(FormDatabase formDatabase, CurrentTenant currentTenant) {
      return (id, rev) -> formDatabase.findOne(currentTenant.getId(), id, rev);
    }

    @Bean
    public TenantAccessEvaluator tenantAccessEvaluator() {
      return tenant -> true;
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

  @MockBean
  private CurrentUserProvider currentUserProvider;

  @BeforeEach
  public void resetMocks(TestInfo testInfo) {
    reset(questionnaireDatabaseMock(), listenerMock);
    String testMethodName = testInfo.getTestMethod().get().getName();
    when(currentUserProvider.get()).thenReturn(new CurrentUser(testMethodName, testMethodName,"","",""));
  }

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
  public void shouldLookupQuestionnairesFromRepository() throws Exception {
    doAnswer(invocation -> {
      Consumer<QuestionnaireDatabase.MetadataRow> consumer = (Consumer<QuestionnaireDatabase.MetadataRow>) invocation.getArguments()[6];
      consumer.accept(new QuestionnaireDatabase.MetadataRow() {
        @NonNull
        @Override
        public String getId() {
          return "1";
        }

        @NonNull
        @Override
        public Questionnaire.Metadata getValue() {
          return ImmutableQuestionnaireMetadata.builder().formId("").label("l1").build();
        }
      });
      consumer.accept(new QuestionnaireDatabase.MetadataRow() {
        @NonNull
        @Override
        public String getId() {
          return "2";
        }

        @NonNull
        @Override
        public Questionnaire.Metadata getValue() {
          return ImmutableQuestionnaireMetadata.builder().formId("").label("l2").build();
        }
      });
      return null;
    }).when(questionnaireDatabaseMock()).findAllMetadata(anyString(), isNull(), isNull(), isNull(), isNull(), isNull(), any(Consumer.class));

    ResponseEntity<List<FormListItem>> response = restTemplate.exchange("http://localhost:" + port + "/api/questionnaires", HttpMethod.GET, createHttpEntity(UUID.fromString("00000000-0000-0000-0000-000000000000"),"localsecret"), new ParameterizedTypeReference<List<FormListItem>>() {});

    assertEquals(200, response.getStatusCodeValue());
    List<FormListItem> r = response.getBody();
    assertEquals(2, r.size());
    assertEquals("l1",r.get(0).getMetadata().getLabel());
    assertEquals("l2",r.get(1).getMetadata().getLabel());

    verify(questionnaireDatabaseMock(), times(1)).findAllMetadata(anyString(), isNull(), isNull(), isNull(), isNull(), isNull(), any());
  }

  private QuestionnaireDatabase questionnaireDatabaseMock() {
    return AopTestUtils.getTargetObject(questionnaireDatabase);
  }

  @Test
  public void shouldRejectInvalidKey() throws Exception {
    HttpEntity httpEntity = createHttpEntity(UUID.fromString("00000000-0000-0000-0000-000000000000"),"wrongsecret");
    Assertions.assertThrows(HttpClientErrorException.class, () -> {
      try {
        ResponseEntity<List<FormListItem>> response = restTemplate.exchange("http://localhost:" + port + "/api/questionnaires", HttpMethod.GET, httpEntity, new ParameterizedTypeReference<>() {
        });
      } catch (HttpClientErrorException e) {
        assertEquals(403, e.getRawStatusCode());
        throw e;
      }
    });
  }
}
