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
package io.dialob.session.boot;

import io.dialob.api.proto.ImmutableActions;
import io.dialob.questionnaire.service.api.session.QuestionnaireSession;
import io.dialob.questionnaire.service.api.session.QuestionnaireSessionSaveService;
import io.dialob.questionnaire.service.api.session.QuestionnaireSessionService;
import io.dialob.session.rest.DefaultAnswerController;
import io.dialob.settings.CorsSettings;
import io.dialob.settings.DialobSettings;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.MOCK;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(
  webEnvironment = MOCK,
  properties = {
    "server.servlet.contextPath=/dialob",
    "dialob.session.rest.context=/answers",
    "dialob.session.sockjs.webSocketEnabled=false",
    "dialob.session.cache.type=NONE",
    "dialob.session.cache.type=LOCAL"
  }, classes = {
  ApplicationAutoConfiguration.class
})
@EnableWebMvc
@EnableConfigurationProperties(DialobSettings.class)
class ApplicationCorsTest {

  @MockitoBean
  DefaultAnswerController answerController;

  @MockitoBean
  QuestionnaireSessionSaveService questionnaireSessionSaveService;

  @Inject
  public WebApplicationContext wac;

  @Inject
  public DialobSettings dialobSettings;

  @MockitoBean
  public QuestionnaireSessionService questionnaireSessionService;

  private MockMvc mockMvc;

  public URI session(String... paths) {
    return UriComponentsBuilder.newInstance()
      .scheme("http")
      .host("localhost")
      .path("/answers")
      .pathSegment(paths)
      .build().toUri();
  }

  @BeforeEach
  public void setupCurrentUser(TestInfo testInfo) {
    Mockito.reset(answerController, questionnaireSessionService);
    dialobSettings.getSession().getRest().getCors().clear();
    this.mockMvc = MockMvcBuilders
      .webAppContextSetup(this.wac)
      .apply(springSecurity()).build();
  }

  @Test
  void shouldNotGetAnyCorsHeaderWhenCorsIsUndefined() throws Exception {
    when(answerController.getState("123456")).thenAnswer(inv -> ResponseEntity.ok(ImmutableActions.builder().build()));
    mockMvc.perform(get(session("123456"))
        .header("Origin", "localhost") // triggers cors evaluation...
        .accept(MediaType.APPLICATION_JSON))
      .andExpect(status().isOk())
      .andExpect(header().doesNotExist("Access-Control-Allow-Credentials"))
      .andExpect(header().doesNotExist("Access-Control-Allow-Headers"))
      .andExpect(header().doesNotExist("Access-Control-Allow-Methods"))
      .andExpect(header().doesNotExist("Access-Control-Allow-Origin"))
      .andExpect(header().doesNotExist("Access-Control-Expose-Headers"))
      .andExpect(header().doesNotExist("Access-Control-Max-Age"))
      .andExpect(content().string("{}"));

    verify(answerController).getState("123456");
    verifyNoMoreInteractions(answerController);
  }

  @Test
  void shouldGetDefaultCorsHeadersIfCorsConfigured() throws Exception {
    QuestionnaireSession questionnaireSession = Mockito.mock(QuestionnaireSession.class);

    when(answerController.getState("123456")).thenAnswer(inv -> ResponseEntity.ok(ImmutableActions.builder().build()));
    when(questionnaireSessionService.findOne("123456")).thenReturn(questionnaireSession);
    when(questionnaireSession.getTenantId()).thenReturn("tenant-id");
    when(questionnaireSession.getSessionId()).thenReturn(Optional.of("123456"));

    CorsSettings corsSettings = new CorsSettings();
    corsSettings.getAllowedOrigins().add("localhost");
    corsSettings.setAllowCredentials(true);
    dialobSettings.getSession().getRest().getCors().put("default", corsSettings);

    mockMvc.perform(get(session("123456"))
        .header("Origin", "localhost") // triggers cors evaluation...
        .accept(MediaType.APPLICATION_JSON))
      .andExpect(status().isOk())
      .andExpect(header().string("Access-Control-Allow-Credentials", "true"))
      .andExpect(header().doesNotExist("Access-Control-Allow-Headers"))
      .andExpect(header().doesNotExist("Access-Control-Allow-Methods"))
      .andExpect(header().string("Access-Control-Allow-Origin", "localhost"))
      .andExpect(header().doesNotExist("Access-Control-Expose-Headers"))
      .andExpect(header().doesNotExist("Access-Control-Max-Age"))
      .andExpect(content().string("{}"));

    verify(answerController).getState("123456");
    verify(questionnaireSessionService).findOne("123456");
    verify(questionnaireSession).getTenantId();
    verifyNoMoreInteractions(answerController, questionnaireSession, questionnaireSessionService);
  }

  @Test
  void shouldGetPreFlightDefaultCorsHeadersIfCorsConfigured() throws Exception {
    QuestionnaireSession questionnaireSession = Mockito.mock(QuestionnaireSession.class);
    when(questionnaireSession.getTenantId()).thenReturn("tenant-id");
    when(questionnaireSession.getSessionId()).thenReturn(Optional.of("123456"));
    when(questionnaireSessionService.findOne("123456")).thenReturn(questionnaireSession);

    final CorsSettings corsSettings = new CorsSettings();
    corsSettings.getAllowedOrigins().add("localhost");
    corsSettings.getAllowedMethods().add("PUT");
    corsSettings.setAllowCredentials(true);
    dialobSettings.getSession().getRest().getCors().put("default", corsSettings);

    mockMvc.perform(options(session("123456"))
        .header("Origin", "localhost") // triggers cors evaluation...
        .header("Access-Control-Request-Method", "PUT") // preflight requests asks permissions to methods
        .accept(MediaType.APPLICATION_JSON))
      .andExpect(status().isOk())
      .andExpect(header().string("Access-Control-Allow-Credentials", "true"))
      .andExpect(header().doesNotExist("Access-Control-Allow-Headers"))
      .andExpect(header().string("Access-Control-Allow-Methods", "PUT"))
      .andExpect(header().string("Access-Control-Allow-Origin", "localhost"))
      .andExpect(header().doesNotExist("Access-Control-Expose-Headers"))
      .andExpect(header().string("Access-Control-Max-Age", "1800"));

    verify(questionnaireSessionService).findOne("123456");
    verify(questionnaireSession).getTenantId();
    verifyNoMoreInteractions(answerController, questionnaireSession, questionnaireSessionService);
  }

  @Test
  void shouldGetPreFlightTenantSpecificCorsHeadersIfCorsConfigured() throws Exception {
    QuestionnaireSession questionnaireSession = Mockito.mock(QuestionnaireSession.class);
    when(questionnaireSession.getTenantId()).thenReturn("tenant-id");
    when(questionnaireSession.getSessionId()).thenReturn(Optional.of("123456"));
    when(questionnaireSessionService.findOne("123456")).thenReturn(questionnaireSession);

    CorsSettings corsSettings = new CorsSettings();
    corsSettings.getAllowedOrigins().add("localhost");
    corsSettings.getAllowedMethods().add("PUT");
    corsSettings.setAllowCredentials(true);
    dialobSettings.getSession().getRest().getCors().put("default", corsSettings);

    corsSettings = new CorsSettings();
    corsSettings.getAllowedOrigins().add("3rd-party-host");
    corsSettings.getAllowedMethods().add("PUT");
    corsSettings.getAllowedHeaders().add("content-type");
    dialobSettings.getSession().getRest().getCors().put("tenant-id", corsSettings);

    mockMvc.perform(options(session("123456"))
        .header("Origin", "3rd-party-host") // triggers cors evaluation...
        .header("Access-Control-Request-Method", "PUT") // preflight requests asks permissions to methods
        .accept(MediaType.APPLICATION_JSON))
      .andExpect(status().isOk())
      .andExpect(header().doesNotExist("Access-Control-Allow-Credentials"))
      .andExpect(header().doesNotExist("Access-Control-Allow-Headers"))
      .andExpect(header().string("Access-Control-Allow-Methods", "PUT"))
      .andExpect(header().string("Access-Control-Allow-Origin", "3rd-party-host"))
      .andExpect(header().doesNotExist("Access-Control-Expose-Headers"))
      .andExpect(header().string("Access-Control-Max-Age", "1800"));

    verify(questionnaireSessionService).findOne("123456");
    verify(questionnaireSession).getTenantId();
    verifyNoMoreInteractions(answerController, questionnaireSession, questionnaireSessionService);
  }

  @Test
  void shouldGetDefaultCorsIfTenantDoNotHaveCorsDefinedAndDefaultDoNotExists() throws Exception {
    QuestionnaireSession questionnaireSession = Mockito.mock(QuestionnaireSession.class);
    when(questionnaireSession.getTenantId()).thenReturn("tenant-id-other");
    when(questionnaireSession.getSessionId()).thenReturn(Optional.of("123456"));
    when(questionnaireSessionService.findOne("123456")).thenReturn(questionnaireSession);

    CorsSettings corsSettings = new CorsSettings();
    corsSettings.getAllowedOrigins().add("3rd-party-host");
    corsSettings.getAllowedMethods().add("PUT");
    corsSettings.getAllowedHeaders().add("content-type");
    dialobSettings.getSession().getRest().getCors().put("tenant-id", corsSettings);

    mockMvc.perform(options(session("123456"))
        .header("Origin", "3rd-party-host") // triggers cors evaluation...
        .header("Access-Control-Request-Method", "PUT") // preflight requests asks permissions to methods
        .accept(MediaType.APPLICATION_JSON))
      .andExpect(status().isForbidden())
      .andExpect(header().doesNotExist("Access-Control-Allow-Credentials"))
      .andExpect(header().doesNotExist("Access-Control-Allow-Headers"))
      .andExpect(header().doesNotExist("Access-Control-Allow-Methods"))
      .andExpect(header().doesNotExist("Access-Control-Allow-Origin"))
      .andExpect(header().doesNotExist("Access-Control-Expose-Headers"))
      .andExpect(header().doesNotExist("Access-Control-Max-Age"));

    verify(questionnaireSessionService).findOne("123456");
    verify(questionnaireSession).getTenantId();
    verifyNoMoreInteractions(answerController, questionnaireSession, questionnaireSessionService);
  }

}
