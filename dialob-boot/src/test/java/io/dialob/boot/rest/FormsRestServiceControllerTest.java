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

import io.dialob.api.form.Form;
import io.dialob.api.form.FormItem;
import io.dialob.api.form.FormTag;
import io.dialob.boot.Application;
import io.dialob.db.spi.exceptions.DocumentNotFoundException;
import io.dialob.form.service.DialobFormServiceAutoConfiguration;
import io.dialob.form.service.api.FormDatabase;
import io.dialob.form.service.api.FormVersionControlDatabase;
import io.dialob.form.service.rest.DialobFormServiceRestAutoConfiguration;
import io.dialob.integration.api.event.FormUpdatedEvent;
import io.dialob.integration.queue.DialobIntegrationQueueAutoConfiguration;
import io.dialob.questionnaire.service.api.session.FormFinder;
import io.dialob.rest.RestApiExceptionMapper;
import io.dialob.rule.parser.function.FunctionRegistry;
import io.dialob.security.spring.DialobSecuritySpringAutoConfiguration;
import io.dialob.security.tenant.CurrentTenant;
import io.dialob.security.tenant.Tenant;
import io.dialob.spring.boot.engine.DialobSessionEngineAutoConfiguration;
import jakarta.inject.Inject;
import org.hamcrest.CustomTypeSafeMatcher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.internal.hamcrest.HamcrestArgumentMatcher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.validation.ValidationAutoConfiguration;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.event.EventListener;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.Instant;
import java.util.Optional;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.MOCK;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = MOCK, properties = {
  "dialob.security.enabled=true",
  "tenantId=itest",
  "spring.jackson.deserialization.READ_DATE_TIMESTAMPS_AS_NANOSECONDS=false",
  "spring.jackson.serialization.WRITE_DATES_AS_TIMESTAMPS=false",
  "spring.jackson.serialization.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS=false",
  "dialob.db.database-type=none",
  "spring.security.oauth2.client.registration[0].provider=own",
  "spring.security.oauth2.client.registration[0].clientId=cl1",
  "spring.security.oauth2.client.registration[0].clientSecret=xxx",
  "spring.security.oauth2.client.registration[0].authorizationGrantType=authorization_code",
  "spring.security.oauth2.client.registration[0].redirectUri=/login",
  "spring.security.oauth2.client.registration[0].scope[0]=openid",
  "spring.security.oauth2.client.registration[0].clientName=test",
  "spring.security.oauth2.client.provider[own].authorizationUri=http://localhost:880",
  "spring.security.oauth2.client.provider[own].tokenUri=http://localhost:880",
  "spring.security.oauth2.client.provider[own].jwkSetUri=http://localhost:880",
  "spring.autoconfigure.exclude[0]=org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration",
  "spring.autoconfigure.exclude[1]=org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration",
  "spring.data.redis.repositories.enabled=false",
  "management.health.db.enabled=false"
})
@ContextConfiguration(classes = {
  AbstractSecuredRestTests.TestConfiguration.class,
  Application.class,
  FormsRestServiceControllerTest.TestConfiguration.class,
  DialobFormServiceRestAutoConfiguration.class,
  DialobSessionEngineAutoConfiguration.class,
  DialobFormServiceAutoConfiguration.class,
  DialobIntegrationQueueAutoConfiguration.class,
  DialobSecuritySpringAutoConfiguration.class,
  ValidationAutoConfiguration.class,
  RestApiExceptionMapper.class
})
@EnableConfigurationProperties(ServerProperties.class)
class FormsRestServiceControllerTest extends AbstractSecuredRestTests {

  public interface ListenerMock {
    @EventListener
    void onFormUpdatedEvent(FormUpdatedEvent event);
  }

  public static class TestConfiguration extends AbstractFormRepositoryTests.TestConfiguration {
    @Primary
    @Bean
    public FormDatabase formDatabase() {
      return Mockito.mock(FormDatabase.class);
    }

    @Bean
    public FormFinder formFinder(FormDatabase formDatabase, CurrentTenant currentTenant) {
      return (formId, formRev) -> formDatabase.findOne(currentTenant.getId(), formId, formRev);
    }

  }

  @Inject
  private ApplicationEventPublisher applcationApplicationEventPublisher;

  @MockitoBean
  private ListenerMock listenerMock;

  @MockitoBean
  private CurrentTenant currentTenant;
  @MockitoBean
  private FormVersionControlDatabase formVersionControlDatabase;
  @MockitoBean
  private FunctionRegistry functionRegistry;

  @BeforeEach
  @Override
  public void resetMocks() {
    reset(formDatabase, listenerMock, formVersionControlDatabase);
  }

  @Value("${server.servlet.context-path:/}")
  protected String contextPath;

  @BeforeEach
  void setupTenant() {
    when(currentTenant.getId()).thenReturn(tenantId);
    when(currentTenant.get()).thenReturn(Tenant.of(tenantId));
  }

  @Override
  public String getContextPath() {
    return contextPath;
  }

  @Test
  @WithMockUser(username = "testUser", authorities = {"forms.get", "itest", "tenant.all"})
  void shouldLookupFormsFromRepository() throws Exception {
    doAnswer(invocation -> {
      Consumer consumer = (Consumer) invocation.getArguments()[2];
      consumer.accept(FormDatabase.FormMetadataRow.of("1", new Form.Metadata.Builder().label("l1").build()));
      consumer.accept(FormDatabase.FormMetadataRow.of("2", new Form.Metadata.Builder().label("l2").build()));
      return null;
    }).when(formDatabase).findAllMetadata(eq(tenantId), isNull(), any(Consumer.class));

    mockMvc.perform(get(uri("api", "forms")).params(tenantParam).accept(MediaType.APPLICATION_JSON))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$[0].metadata.label").value("l1"))
      .andExpect(jsonPath("$[1].metadata.label").value("l2"));
    verify(formDatabase, times(1)).findAllMetadata(eq(tenantId), isNull(), any());
    verifyNoMoreInteractions(formDatabase);
  }

  @Test
  @WithMockUser(username = "testUser", authorities = {"forms.get", "itest", "tenant.all"})
  void shouldLookupFormFromRepository() throws Exception {

    Form formDocument = new Form.Builder()
      .id("form-id")
      .rev("2")
      .metadata(new Form.Metadata.Builder().created(Instant.parse("2015-11-05T12:00:00.000Z")).label("test").build())
      .build();

    when(formDatabase.findOne(tenantId, "form-id", null)).thenReturn(formDocument);

    mockMvc.perform(get(uri("api", "forms", "form-id")).params(tenantParam).accept(MediaType.APPLICATION_JSON))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$._id").value("form-id"))
      .andExpect(jsonPath("$._rev").value("2"))
      .andExpect(jsonPath("$.metadata.created").value("2015-11-05T12:00:00Z"));

    verify(formDatabase, times(1)).findOne(tenantId, "form-id", null);
  }

  @Test
  @WithMockUser(username = "testUser", authorities = {"forms.get", "itest", "tenant.all"})
  void shouldReturn404IfFormDoNotExists() throws Exception {
    when(formDatabase.findOne(tenantId, "form-id", null)).thenThrow(new DocumentNotFoundException("not_found"));

    mockMvc.perform(get(uri("api", "forms", "form-id")).params(tenantParam).accept(MediaType.APPLICATION_JSON))
      .andExpect(status().isNotFound())
      .andExpect(jsonPath("$.status").value(404))
      .andExpect(jsonPath("$.message").value("not_found"))
      .andExpect(jsonPath("$.error").value("Not Found"))
      .andExpect(jsonPath("$.timestamp").exists());

    verify(formDatabase, times(1)).findOne(tenantId, "form-id", null);
  }

  @Test
  @WithMockUser(username = "testUser", authorities = {"forms.get", "itest", "tenant.all"})
  void shouldReturnTemplateForm() throws Exception {
    mockMvc.perform(get(uri("api", "forms", "00000000000000000000000000000000")).params(tenantParam).accept(MediaType.APPLICATION_JSON))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.data.length()").value(4));
  }

  @Test
  @WithMockUser(username = "testUser", authorities = {"itest", "forms.put", "tenant.all"})
  void shouldTriggerEventOnFormUpdate() throws Exception {
    when(formDatabase.save(anyString(), any())).thenAnswer(invocation -> {
      Form arg = (Form) invocation.getArguments()[1];
      return arg.withRev("1");
    });
    Form formDocument = new Form.Builder()
      .id("new-form")
      .rev("old")
      .putData("questionnaire", new FormItem.Builder().id("questionnaire").type("questionnaire").build())
      .metadata(new Form.Metadata.Builder().created(Instant.parse("2015-11-05T12:00:00Z")).label("test").build())
      .build();

    // We need to return cfrs token on update action
    mockMvc.perform(put(uri("api", "forms", formDocument.getId())).params(tenantParam).with(csrf().asHeader())
      .accept(MediaType.APPLICATION_JSON)
      .contentType(MediaType.APPLICATION_JSON)
      .content(OBJECT_MAPPER.writeValueAsBytes(formDocument)))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.id").value("new-form"))
      .andExpect(jsonPath("$.rev").value("1"))
      .andExpect(jsonPath("$.ok").value(true))
      ;
    verify(formDatabase, times(1)).save(anyString(), any());
    verify(listenerMock, times(1)).onFormUpdatedEvent(ArgumentMatchers.argThat(new HamcrestArgumentMatcher<>(new CustomTypeSafeMatcher<>("matches new-form with rev 1") {
      @Override
      protected boolean matchesSafely(FormUpdatedEvent event) {
        assertEquals("new-form", event.formId());
        assertEquals("1", event.revision());
        return true;
      }
    })));
  }


  @Test
  @WithMockUser(username = "testUser", authorities = {"itest", "forms.post", "tenant.all"})
  @Disabled// TODO
  void shouldReturnErrorWhenRootITemIsMissing() throws Exception {
    when(formDatabase.save(anyString(), any())).thenAnswer(invocation -> {
      Form arg = (Form) invocation.getArguments()[0];
      return arg.withRev("1");
    });
    // We need to return cfrs token on update action
    mockMvc.perform(post(uri("api", "forms")).params(tenantParam).with(csrf().asHeader())
      .accept(MediaType.APPLICATION_JSON)
      .contentType(MediaType.APPLICATION_JSON)
      .content("{\"metadata\":{\"label\":\"test\"}}".getBytes()))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.id").value("new-form"))
      .andExpect(jsonPath("$.rev").value("1"))
      .andExpect(jsonPath("$.ok").value(true));
    verify(formDatabase, times(1)).save(anyString(), any());
    verify(listenerMock, times(1)).onFormUpdatedEvent(ArgumentMatchers.argThat(new HamcrestArgumentMatcher<>(new CustomTypeSafeMatcher<>("matches new-form with rev 1") {
      @Override
      protected boolean matchesSafely(FormUpdatedEvent event) {
        assertEquals("new-form", event.formId());
        assertEquals("1", event.revision());
        return true;
      }
    })));
  }


  @Test
  @WithMockUser(username = "testUser", authorities = {"itest", "forms.put", "tenant.all"})
  void shouldRejectUpdateByNameWhenNotForced() throws Exception {
    when(formVersionControlDatabase.findTag(tenantId, "form-name","LATEST"))
      .thenReturn(Optional.of(new FormTag.Builder().name("name").formName("form-name").formId("123-123").created(Instant.now()).build()));
    Form formDocument = new Form.Builder()
      .name("form-name")
      .putData("questionnaire", new FormItem.Builder().id("questionnaire").type("questionnaire").build())
      .metadata(new Form.Metadata.Builder().label("labeli").created(Instant.parse("2015-11-05T12:00:00Z")).build())
      .build();

    // We need to return cfrs token on update action
    mockMvc.perform(put(uri("api", "forms", "form-name")).params(tenantParam).with(csrf().asHeader())
      .accept(MediaType.APPLICATION_JSON)
      .contentType(MediaType.APPLICATION_JSON)
      .content(OBJECT_MAPPER.writeValueAsBytes(formDocument)))
      .andExpect(status().isBadRequest())
      .andExpect(jsonPath("$.error").value("INCONSISTENT_ID"));

    verify(formVersionControlDatabase, times(1)).findTag(tenantId, "form-name","LATEST");
  }

  @Test
  @WithMockUser(username = "testUser", authorities = {"itest", "forms.put", "tenant.all"})
  void shouldAcceptUpdateByNameWhenForced() throws Exception {
    doReturn("00000000-0000-0000-0000-000000000000").when(currentTenant).getId();

    Form formDocument = new Form.Builder()
      .name("form-name")
      .putData("questionnaire", new FormItem.Builder().id("questionnaire").type("questionnaire").build())
      .metadata(new Form.Metadata.Builder().label("labeli").created(Instant.parse("2015-11-05T12:00:00Z")).tenantId("3tt").build())
      .build();

    when(formDatabase.findOne(tenantId, "123-123")).thenReturn(new Form.Builder().from(formDocument).id("123-123").rev("321").build());

    when(formVersionControlDatabase.findTag(tenantId, "form-name","LATEST"))
      .thenReturn(Optional.of(new FormTag.Builder().name("name").formName("form-name").formId("123-123").created(Instant.now()).build()));
    when(formDatabase.save(anyString(), any())).thenAnswer(invocation -> {
      Form arg = (Form) invocation.getArguments()[1];
      return arg.withRev("124");
    });

    // We need to return cfrs token on update action
    mockMvc.perform(put(uri("api", "forms", "form-name")).params(tenantParam).param("force", "true").with(csrf().asHeader())
      .accept(MediaType.APPLICATION_JSON)
      .contentType(MediaType.APPLICATION_JSON)
      .content(OBJECT_MAPPER.writeValueAsBytes(formDocument)))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.id").value("123-123"))
      .andExpect(jsonPath("$.rev").value("124"))
      .andExpect(jsonPath("$.ok").value(true));

    verify(formVersionControlDatabase, times(1)).findTag(tenantId, "form-name","LATEST");
    verify(formDatabase, times(1)).findOne(tenantId, "123-123");
  }


  @Test
  @WithMockUser(username = "testUser", authorities = {"itest", "forms.put", "tenant.all"})
  void shouldReturnErrorWhenLabelIsMissing() throws Exception {
    // We need to return csrf token on update action
    mockMvc.perform(put(uri("api", "forms", "new-form")).params(tenantParam).with(csrf().asHeader())
      .accept(MediaType.APPLICATION_JSON)
      .contentType(MediaType.APPLICATION_JSON)
      .content("{\"id\":\"new-form\",\"rev\":\"old\",\"data\":{},\"metadata\":{}}"))
      .andExpect(status().isUnprocessableEntity())
      .andExpect(jsonPath("$.message").value("metadata.label: must not be null"))
      .andExpect(jsonPath("$.errors[0].context").value("metadata.label"))
      .andExpect(jsonPath("$.errors[0].error").value("must not be null"));
  }

  @Test
  @Disabled
  @WithMockUser(username = "testUser", authorities = {"itest", "forms.put", "tenant.all"})
  void shouldReturnBadRequestWhenRootItemIsMissing() throws Exception {
    // We need to return csrf token on update action
    mockMvc.perform(put(uri("api", "forms", "123")).params(tenantParam).with(csrf().asHeader())
      .accept(MediaType.APPLICATION_JSON)
      .contentType(MediaType.APPLICATION_JSON)
      .content("{\"_id\":\"123\",\"_rev\":\"old\",\"data\":{},\"metadata\":{\"label\":\"this ok\"}}"))
      .andExpect(status().isBadRequest())
      .andExpect(content().string(""))
      .andExpect(jsonPath("$.message").value("metadata.language: must not be null"))
      .andExpect(jsonPath("$.errors[0].context").value("metadata.language"))
      .andExpect(jsonPath("$.errors[0].error").value("must not be null"));

  }


  @Test
  @WithMockUser(username = "testUser", authorities = {"itest", "forms.put", "tenant.all"})
  void shouldBeAbleToPutLatestTag() throws Exception {
    when(currentTenant.getId()).thenReturn(tenantId);
    when(currentTenant.get()).thenReturn(Tenant.of(tenantId));
    when(formVersionControlDatabase.updateLatest(tenantId, "formii", new FormTag.Builder().name("latest").formName("formii").formId("1243").build())).thenReturn(true);

    // We need to return cfrs token on update action
    mockMvc.perform(put(uri("api", "forms", "formii", "tags", "latest")).params(tenantParam).with(csrf().asHeader())
      .accept(MediaType.APPLICATION_JSON)
      .contentType(MediaType.APPLICATION_JSON)
      .content(OBJECT_MAPPER.writeValueAsBytes(new FormTag.Builder().name("latest").formName("formii").formId("1243").build())))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.ok").value(true));


    verify(formVersionControlDatabase).updateLatest(tenantId, "formii", new FormTag.Builder().name("latest").formName("formii").formId("1243").build());

    verifyNoMoreInteractions(formVersionControlDatabase, listenerMock);

  }

  @Test
  @WithMockUser(username = "testUser", authorities = {"itest", "forms.put", "tenant.all"})
  void shouldNotModifyIfUpdateIsNotDone() throws Exception {
    when(currentTenant.getId()).thenReturn(tenantId);
    when(formVersionControlDatabase.updateLatest(tenantId, "formii", new FormTag.Builder().name("latest").formName("formii").formId("1243").build())).thenReturn(false);

    // We need to return cfrs token on update action
    mockMvc.perform(put(uri("api", "forms", "formii", "tags", "latest")).params(tenantParam).with(csrf().asHeader())
      .accept(MediaType.APPLICATION_JSON)
      .contentType(MediaType.APPLICATION_JSON)
      .content(OBJECT_MAPPER.writeValueAsBytes(new FormTag.Builder().name("latest").formName("formii").formId("1243").build())))
      .andExpect(status().isNotModified())
      .andExpect(jsonPath("$.ok").value(false));


    verify(formVersionControlDatabase).updateLatest(tenantId, "formii", new FormTag.Builder().name("latest").formName("formii").formId("1243").build());

    verifyNoMoreInteractions(formVersionControlDatabase, listenerMock);

  }
}
