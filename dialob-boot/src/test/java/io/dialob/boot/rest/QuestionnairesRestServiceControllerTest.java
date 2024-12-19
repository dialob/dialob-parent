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

import io.dialob.api.form.ImmutableForm;
import io.dialob.api.form.ImmutableFormItem;
import io.dialob.api.form.ImmutableFormMetadata;
import io.dialob.api.questionnaire.Questionnaire;
import io.dialob.boot.ApplicationAutoConfiguration;
import io.dialob.boot.settings.AdminApplicationSettings;
import io.dialob.boot.settings.ComposerApplicationSettings;
import io.dialob.boot.settings.QuestionnaireApplicationSettings;
import io.dialob.boot.settings.ReviewApplicationSettings;
import io.dialob.db.spi.exceptions.DocumentNotFoundException;
import io.dialob.form.service.api.FormDatabase;
import io.dialob.questionnaire.service.api.QuestionnaireDatabase;
import io.dialob.questionnaire.service.api.event.QuestionnaireEventPublisher;
import io.dialob.questionnaire.service.api.session.FormFinder;
import io.dialob.questionnaire.service.rest.DialobQuestionnaireServiceRestAutoConfiguration;
import io.dialob.rest.RestApiExceptionMapper;
import io.dialob.rule.parser.function.FunctionRegistry;
import io.dialob.security.spring.DialobSecuritySpringAutoConfiguration;
import io.dialob.security.tenant.CurrentTenant;
import io.dialob.security.tenant.Tenant;
import io.dialob.settings.DialobSettings;
import io.dialob.spring.boot.engine.DialobSessionEngineAutoConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.internal.verification.api.VerificationData;
import org.mockito.invocation.Invocation;
import org.mockito.verification.VerificationMode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.AopTestUtils;

import java.util.Iterator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.MOCK;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = MOCK,
  properties = {
    "tenantId=itest",
    "dialob.security.enabled=true",
    "dialob.db.database-type=none",
    "spring.autoconfigure.exclude[0]=org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration",
    "spring.autoconfigure.exclude[1]=org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration",
    "spring.autoconfigure.exclude[2]=org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration",
    "spring.autoconfigure.exclude[3]=org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration",
    "spring.security.oauth2.client.registration[0].provider=own",
    "spring.security.oauth2.client.registration[0].clientId=cl1",
    "spring.security.oauth2.client.registration[0].clientSecret=xxx",
    "spring.security.oauth2.client.registration[0].authorizationGrantType=authorization_code",
    "spring.security.oauth2.client.registration[0].redirectUri=/login",
    "spring.security.oauth2.client.registration[0].scope[0]=openid",
    "spring.security.oauth2.client.registration[0].clientName=test",
    "spring.security.oauth2.client.provider[own].authorizationUri=http://localhost:880",
    "spring.security.oauth2.client.provider[own].tokenUri=http://localhost:880",
    "spring.security.oauth2.client.provider[own].jwkSetUri=http://localhost:880"
  },
  classes = {
    AbstractSecuredRestTests.TestConfiguration.class,
    QuestionnairesRestServiceControllerTest.TestConfiguration.class,
    DialobQuestionnaireServiceRestAutoConfiguration.class,
    DialobSessionEngineAutoConfiguration.class,
    DialobSecuritySpringAutoConfiguration.class,
    ApplicationAutoConfiguration.class,
    RestApiExceptionMapper.class,
  })
@EnableConfigurationProperties({
  ServerProperties.class,
  DialobSettings.class,
  ComposerApplicationSettings.class,
  QuestionnaireApplicationSettings.class,
  AdminApplicationSettings.class,
  ReviewApplicationSettings.class
})
@EnableWebSecurity
public class QuestionnairesRestServiceControllerTest extends AbstractSecuredRestTests {

  @MockitoBean
  private CurrentTenant currentTenant;

  @MockitoBean
  private FunctionRegistry functionRegistry;

  @MockitoBean
  private QuestionnaireEventPublisher questionnaireEventPublisher;

  public String tenantId = "00000000-0000-0000-0000-000000000000";

  @BeforeEach
  void setupTenant() {
    when(currentTenant.getId()).thenReturn(tenantId);
    when(currentTenant.get()).thenReturn(Tenant.of(tenantId));
  }

  @Value("${server.servlet.context-path:/}")
  protected String contextPath;

  @Override
  public String getContextPath() {
    return contextPath;
  }

  @Configuration(proxyBeanMethods = false)
  @EnableAutoConfiguration
  public static class TestConfiguration extends AbstractFormRepositoryTests.TestConfiguration {

    @Bean
    @Primary
    public FormDatabase formDatabase() {
      return Mockito.mock(FormDatabase.class);
    }

    @Bean
    @Primary
    public FormFinder formFinder(FormDatabase formDatabase, CurrentTenant currentTenant) {
      return (formId, formRev) -> formDatabase.findOne(currentTenant.getId(), formId, formRev);
    }

    @Bean
    @Primary
    public QuestionnaireDatabase questionnaireDatabase() {
      return Mockito.mock(QuestionnaireDatabase.class);
    }

  }

  @Test
  @WithMockUser(username = "testUser", authorities = {"itest", "questionnaires.post", "questionnaires.get", "tenant.all"})
  public void testGetQuestionnaires() throws Exception {
    ImmutableForm.Builder formDocument = ImmutableForm.builder()
      .metadata(ImmutableFormMetadata.builder().label("Kysely").build())
      .id("123")
      .rev("321");
    addQuestionnaire(formDocument, builder -> builder.addClassName("main-questionnaire"));
    shouldFindForm(formDocument.build());

    Session session = createQuestionnaire("123");
    verify(questionnaireDatabaseMock(), new VerificationMode() {
      @Override
      public void verify(VerificationData data) {
        Iterator<Invocation> iterator = data.getAllInvocations().iterator();
        Invocation invocation = iterator.next();
        Questionnaire d = invocation.getArgument(1);
        assertEquals("123", d.getMetadata().getFormId());
        assertEquals("321", d.getMetadata().getFormRev());
        assertEquals("Kysely", d.getMetadata().getLabel());
        assertEquals(Questionnaire.Metadata.Status.NEW, d.getMetadata().getStatus());
        assertFalse(iterator.hasNext());
      }

      @Override
      public VerificationMode description(String description) {
        return this;
      }
    }).save(anyString(), any(Questionnaire.class));
  }


  @Test
  @WithMockUser(username = "testUser", authorities = {"questionnaires.get", "itest", "tenant.all"})
  public void shouldReturn404IfQuestionnaireDoNotExists() throws Exception {
    when(questionnaireDatabaseMock().findOne(tenantId, "00000")).thenThrow(new DocumentNotFoundException("not_found"));
    mockMvc.perform(get(uri("api", "questionnaires", "00000")).params(tenantParam))
      .andExpect(status().isNotFound())
      .andExpect(jsonPath("$.status").value(404))
      .andExpect(jsonPath("$.message").value("not_found"))
      .andExpect(jsonPath("$.error").value("Not Found"))
      .andExpect(jsonPath("$.timestamp").exists());
  }

  private QuestionnaireDatabase questionnaireDatabaseMock() {
    return AopTestUtils.<QuestionnaireDatabase>getTargetObject(questionnaireDatabase);
  }


  @Test
  @WithMockUser(username = "testUser", authorities = {"questionnaires.get", "itest", "tenant.all"})
  public void return200EvenQuestionnairesFormDoNotExists() throws Exception {
    Questionnaire questionnaire = createQuestionnaireDocument("abc123edf3", "1-invalidQ", "notexists", "1-notexists");
    when(questionnaireDatabaseMock().findOne(tenantId, "abc123edf3")).thenReturn(questionnaire);

    mockMvc.perform(get(uri("api", "questionnaires", "abc123edf3")).params(tenantParam))
      .andExpect(status().isOk());

    verify(questionnaireDatabaseMock()).findOne(tenantId, "abc123edf3");
    verifyNoMoreInteractions(questionnaireDatabaseMock(), formDatabase);
  }

  @Test
  @WithMockUser(username = "testUser", authorities = {"itest"})
  public void cannotFetchQuestionnairesWithoutAuthority() throws Exception {
    mockMvc.perform(get(uri("api", "questionnaires", "abc123edf1")).params(tenantParam))
      .andExpect(status().isForbidden());
    verifyNoMoreInteractions(questionnaireDatabaseMock(), formDatabase);
  }

  @Test
  @WithMockUser(username = "testUser", authorities = {"itest", "tenant.all"})
  public void cannotDeleteQuestionnairesWithoutAuthority() throws Exception {
    mockMvc.perform(delete(uri("api", "questionnaires", "abc123edf1")).params(tenantParam).with(csrf()))
      .andExpect(status().isForbidden());
    verifyNoMoreInteractions(questionnaireDatabaseMock(), formDatabase);
  }

  @Test
  @WithMockUser(username = "testUser", authorities = {"itest","questionnaires.delete", "tenant.all"})
  public void canDeleteQuestionnairesWithAuthority() throws Exception {
    doReturn("00000000-0000-0000-0000-000000000000").when(currentTenant).getId();
    mockMvc.perform(delete(uri("api", "questionnaires", "abc123edf")).params(tenantParam).with(csrf()))
      .andExpect(status().isOk());
    verify(questionnaireDatabaseMock()).delete(tenantId, "abc123edf");
    verifyNoMoreInteractions(questionnaireDatabaseMock(), formDatabase);
  }

  @Test
  @WithMockUser(username = "testUser", authorities = {"questionnaires.post", "itest", "tenant.all"})
  public void return422WhenTryingToCreateQuestionnaireForNonExistingForm() throws Exception {
    Questionnaire questionnaire = createQuestionnaireDocument("invalidQ", "1-invalidQ", "notexists", "1-notexists");
    when(formDatabase.exists(tenantId, "notexists")).thenReturn(false);

    mockMvc.perform(post(uri("api", "questionnaires")).params(tenantParam)
      .content(OBJECT_MAPPER.writeValueAsBytes(questionnaire))
      .contentType(MediaType.APPLICATION_JSON)
      .accept(MediaType.APPLICATION_JSON)
      .with(csrf()))
      .andExpect(status().isUnprocessableEntity())
      .andExpect(jsonPath("$.errors[0].code").value("NotExists"))
      .andExpect(jsonPath("$.errors[0].context").value("metadata.formId"))
      .andExpect(jsonPath("$.errors[0].rejectedValue").value("notexists"));

    verify(formDatabase).exists("00000000-0000-0000-0000-000000000000", "notexists");
    verifyNoMoreInteractions(questionnaireDatabaseMock(), formDatabase);
  }

  @Test
  @WithMockUser(username = "testUser", authorities = {"questionnaires.post", "itest", "tenant.all"})
  public void return422WhenTryingToCreateQuestionnaireForINvalidForm() throws Exception {
    Questionnaire questionnaire = createQuestionnaireDocument("invalidQ", "1-invalidQ", "invalid", "1-invalid");
    when(formDatabase.exists(tenantId, "invalid")).thenReturn(true);
    when(formDatabase.findOne(tenantId, "invalid", "1-invalid")).thenReturn(ImmutableForm.builder()
      .id("invalid")
      .rev("1-invalid")
      .metadata(ImmutableFormMetadata.builder()
        .label("invalid")
        .build())
      .build());


    mockMvc.perform(post(uri("api", "questionnaires")).params(tenantParam)
      .content(OBJECT_MAPPER.writeValueAsBytes(questionnaire))
      .contentType(MediaType.APPLICATION_JSON)
      .accept(MediaType.APPLICATION_JSON)
      .with(csrf()))
      .andExpect(status().isUnprocessableEntity())
      .andExpect(jsonPath("$.status").value(422))
      .andExpect(jsonPath("$.error").value("Unprocessable Entity"))
      .andExpect(jsonPath("$.message").value("Form do not have root"));

    verify(formDatabase).exists(tenantId, "invalid");
    verify(formDatabase, times(2)).findOne(tenantId, "invalid", "1-invalid");
    verifyNoMoreInteractions(questionnaireDatabaseMock(), formDatabase);
  }

  @Test
  @WithMockUser(username = "testUser", authorities = {"questionnaires.post", "itest", "tenant.all"})
  public void return422WhenTryingToCreateQuestionnaireForInvalidForm2() throws Exception {
    Questionnaire questionnaire = createQuestionnaireDocument("invalidQ", "1-invalidQ", "invalid2", "1-invalid2");
    when(formDatabase.exists(tenantId, "invalid2")).thenReturn(true);
    when(formDatabase.findOne(tenantId, "invalid2", "1-invalid2")).thenReturn(ImmutableForm.builder()
      .id("invalid2")
      .rev("1-invalid2")
      .putData("questionnaire", ImmutableFormItem.builder()
        .id("questionnaire")
        .type("questionnaire")
        .addItems("q1")
        .build())
      .putData("q1", ImmutableFormItem.builder()
        .id("q1")
        .type("text")
        .activeWhen("virhe")
        .build())
      .metadata(ImmutableFormMetadata.builder()
        .label("invalid")
        .build())
      .build());


    mockMvc.perform(post(uri("api", "questionnaires")).params(tenantParam)
      .content(OBJECT_MAPPER.writeValueAsBytes(questionnaire))
      .contentType(MediaType.APPLICATION_JSON)
      .accept(MediaType.APPLICATION_JSON)
      .with(csrf()))
      .andExpect(status().isUnprocessableEntity())
      .andExpect(jsonPath("$.status").value(422))
      .andExpect(jsonPath("$.error").value("Unprocessable Entity"))
      .andExpect(jsonPath("$.message").value("Could not compile program due errors."));

    verify(formDatabase).exists(tenantId, "invalid2");
    verify(formDatabase, times(2)).findOne(tenantId, "invalid2", "1-invalid2");
    verifyNoMoreInteractions(questionnaireDatabaseMock(), formDatabase);
  }

}
