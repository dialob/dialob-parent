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

import edu.umd.cs.findbugs.annotations.NonNull;
import io.dialob.api.form.*;
import io.dialob.api.rest.Errors;
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
import io.dialob.security.tenant.CurrentTenant;
import io.dialob.security.tenant.Tenant;
import io.dialob.spring.boot.engine.DialobSessionEngineAutoConfiguration;
import jakarta.inject.Inject;
import org.hamcrest.CustomTypeSafeMatcher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.internal.hamcrest.HamcrestArgumentMatcher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.validation.ValidationAutoConfiguration;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.event.EventListener;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.Instant;
import java.util.Date;
import java.util.Optional;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.MOCK;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
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
  ValidationAutoConfiguration.class,
  RestApiExceptionMapper.class
})
@EnableConfigurationProperties(ServerProperties.class)
public class FormsRestServiceControllerTest extends AbstractSecuredRestTests {

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

  @MockBean
  private ListenerMock listenerMock;

  @MockBean
  private CurrentTenant currentTenant;
  @MockBean
  private FormVersionControlDatabase formVersionControlDatabase;
  @MockBean
  private FunctionRegistry functionRegistry;

  @BeforeEach
  public void resetMocks() {
    reset(formDatabase, listenerMock, formVersionControlDatabase);
  }

  @Value("${server.context-path:/}")
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
  @WithMockUser(username = "testUser", authorities = {"forms.get", "itest"})
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
    }).when(formDatabase).findAllMetadata(eq(tenantId), isNull(), any(Consumer.class));

    mockMvc.perform(get(uri("api", "forms")).params(tenantParam).accept(MediaType.APPLICATION_JSON))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$[0].metadata.label").value("l1"))
      .andExpect(jsonPath("$[1].metadata.label").value("l2"));
    verify(formDatabase, times(1)).findAllMetadata(eq(tenantId), isNull(), any());
    verifyNoMoreInteractions(formDatabase);
  }

  @Test
  @WithMockUser(username = "testUser", authorities = {"forms.get", "itest"})
  public void shouldLookupFormFromRepository() throws Exception {

    Form formDocument = ImmutableForm.builder()
      .id("form-id")
      .rev("2")
      .metadata(ImmutableFormMetadata.builder().created(new Date(Instant.parse("2015-11-05T12:00:00Z").toEpochMilli())).label("test").build())
      .build();

    when(formDatabase.findOne(tenantId, "form-id", null)).thenReturn(formDocument);

    mockMvc.perform(get(uri("api", "forms", "form-id")).params(tenantParam).accept(MediaType.APPLICATION_JSON))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$._id").value("form-id"))
      .andExpect(jsonPath("$._rev").value("2"))
      .andExpect(jsonPath("$.metadata.created").value("2015-11-05T12:00:00.000+00:00"));

    verify(formDatabase, times(1)).findOne(tenantId, "form-id", null);
  }

  @Test
  @WithMockUser(username = "testUser", authorities = {"forms.get", "itest"})
  public void shouldReturn404IfFormDoNotExists() throws Exception {
    when(formDatabase.findOne(tenantId, "form-id", null)).thenThrow(new DocumentNotFoundException("not_found"));
    ResponseEntity<Errors> response;

    mockMvc.perform(get(uri("api", "forms", "form-id")).params(tenantParam).accept(MediaType.APPLICATION_JSON))
      .andExpect(status().isNotFound())
      .andExpect(jsonPath("$.status").value(404))
      .andExpect(jsonPath("$.message").value("not_found"))
      .andExpect(jsonPath("$.error").value("Not Found"))
      .andExpect(jsonPath("$.timestamp").exists());

    verify(formDatabase, times(1)).findOne(tenantId, "form-id", null);
  }

  @Test
  @WithMockUser(username = "testUser", authorities = {"forms.get", "itest"})
  public void shouldReturnTemplateForm() throws Exception {
    mockMvc.perform(get(uri("api", "forms", "00000000000000000000000000000000")).params(tenantParam).accept(MediaType.APPLICATION_JSON))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.data.length()").value(4));
  }

  @Test
  @WithMockUser(username = "testUser", authorities = {"itest", "forms.put"})
  public void shouldTriggerEventOnFormUpdate() throws Exception {
    when(formDatabase.save(anyString(), any())).thenAnswer(invocation -> {
      ImmutableForm arg = (ImmutableForm) invocation.getArguments()[1];
      return arg.withRev("1");
    });
    Form formDocument = ImmutableForm.builder()
      .id("new-form")
      .rev("old")
      .putData("questionnaire", ImmutableFormItem.builder().id("questionnaire").type("questionnaire").build())
      .metadata(ImmutableFormMetadata.builder().created(new Date(Instant.parse("2015-11-05T12:00:00Z").toEpochMilli())).label("test").build())
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
    verify(listenerMock, times(1)).onFormUpdatedEvent(ArgumentMatchers.argThat(new HamcrestArgumentMatcher<>(new CustomTypeSafeMatcher<FormUpdatedEvent>("matches new-form with rev 1") {
      @Override
      protected boolean matchesSafely(FormUpdatedEvent event) {
        assertEquals("new-form", event.getFormId());
        assertEquals("1", event.getRevision());
        return true;
      }
    })));
  }


  @Test
  @WithMockUser(username = "testUser", authorities = {"itest", "forms.post"})
  @Disabled// TODO
  public void shouldReturnErrorWhenRootITemIsMissing() throws Exception {
    when(formDatabase.save(anyString(), any())).thenAnswer(invocation -> {
      ImmutableForm arg = (ImmutableForm) invocation.getArguments()[0];
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
    verify(listenerMock, times(1)).onFormUpdatedEvent(ArgumentMatchers.argThat(new HamcrestArgumentMatcher<>(new CustomTypeSafeMatcher<FormUpdatedEvent>("matches new-form with rev 1") {
      @Override
      protected boolean matchesSafely(FormUpdatedEvent event) {
        assertEquals("new-form", event.getFormId());
        assertEquals("1", event.getRevision());
        return true;
      }
    })));
  }


  @Test
  @WithMockUser(username = "testUser", authorities = {"itest", "forms.put"})
  public void shouldRejectUpdateByNameWhenNotForced() throws Exception {
    when(formVersionControlDatabase.findTag(tenantId, "form-name","LATEST"))
      .thenReturn(Optional.of(ImmutableFormTag.builder().formName("form-name").formId("123-123").created(new Date()).build()));
    Form formDocument = ImmutableForm.builder()
      .name("form-name")
      .putData("questionnaire", ImmutableFormItem.builder().id("questionnaire").type("questionnaire").build())
      .metadata(ImmutableFormMetadata.builder().label("labeli").created(new Date(Instant.parse("2015-11-05T12:00:00Z").toEpochMilli())).build())
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
  @WithMockUser(username = "testUser", authorities = {"itest", "forms.put"})
  public void shouldAcceptUpdateByNameWhenForced() throws Exception {
    doReturn("00000000-0000-0000-0000-000000000000").when(currentTenant).getId();

    Form formDocument = ImmutableForm.builder()
      .name("form-name")
      .putData("questionnaire", ImmutableFormItem.builder().id("questionnaire").type("questionnaire").build())
      .metadata(ImmutableFormMetadata.builder().label("labeli").created(new Date(Instant.parse("2015-11-05T12:00:00Z").toEpochMilli())).tenantId("3tt").build())
      .build();

    when(formDatabase.findOne(tenantId, "123-123")).thenReturn(ImmutableForm.builder().from(formDocument).id("123-123").rev("321").build());

    when(formVersionControlDatabase.findTag(tenantId, "form-name","LATEST"))
      .thenReturn(Optional.of(ImmutableFormTag.builder().formName("form-name").formId("123-123").created(new Date()).build()));
    when(formDatabase.save(anyString(), any())).thenAnswer(invocation -> {
      ImmutableForm arg = (ImmutableForm) invocation.getArguments()[1];
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
  @WithMockUser(username = "testUser", authorities = {"itest", "forms.put"})
  public void shouldReturnErrorWhenLabelIsMissing() throws Exception {
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
  @WithMockUser(username = "testUser", authorities = {"itest", "forms.put"})
  public void shouldReturnBadRequestWhenRootItemIsMissing() throws Exception {
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
  @WithMockUser(username = "testUser", authorities = {"itest", "forms.put"})
  public void shouldBeAbleToPutLatestTag() throws Exception {
    when(currentTenant.getId()).thenReturn(tenantId);
    when(currentTenant.get()).thenReturn(Tenant.of(tenantId));
    when(formVersionControlDatabase.updateLatest(tenantId, "formii", ImmutableFormTag.builder().name("latest").formName("formii").formId("1243").build())).thenReturn(true);

    // We need to return cfrs token on update action
    mockMvc.perform(put(uri("api", "forms", "formii", "tags", "latest")).params(tenantParam).with(csrf().asHeader())
      .accept(MediaType.APPLICATION_JSON)
      .contentType(MediaType.APPLICATION_JSON)
      .content(OBJECT_MAPPER.writeValueAsBytes(ImmutableFormTag.builder().name("latest").formName("formii").formId("1243").build())))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.ok").value(true));


    verify(formVersionControlDatabase).updateLatest(tenantId, "formii", ImmutableFormTag.builder().name("latest").formName("formii").formId("1243").build());

    verifyNoMoreInteractions(formVersionControlDatabase, listenerMock);

  }

  @Test
  @WithMockUser(username = "testUser", authorities = {"itest", "forms.put"})
  public void shouldNotModifyIfUpdateIsNotDone() throws Exception {
    when(currentTenant.getId()).thenReturn(tenantId);
    when(formVersionControlDatabase.updateLatest(tenantId, "formii", ImmutableFormTag.builder().name("latest").formName("formii").formId("1243").build())).thenReturn(false);

    // We need to return cfrs token on update action
    mockMvc.perform(put(uri("api", "forms", "formii", "tags", "latest")).params(tenantParam).with(csrf().asHeader())
      .accept(MediaType.APPLICATION_JSON)
      .contentType(MediaType.APPLICATION_JSON)
      .content(OBJECT_MAPPER.writeValueAsBytes(ImmutableFormTag.builder().name("latest").formName("formii").formId("1243").build())))
      .andExpect(status().isNotModified())
      .andExpect(jsonPath("$.ok").value(false));


    verify(formVersionControlDatabase).updateLatest(tenantId, "formii", ImmutableFormTag.builder().name("latest").formName("formii").formId("1243").build());

    verifyNoMoreInteractions(formVersionControlDatabase, listenerMock);

  }
}
