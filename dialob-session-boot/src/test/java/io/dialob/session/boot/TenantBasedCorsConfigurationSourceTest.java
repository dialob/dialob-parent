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

import io.dialob.questionnaire.service.api.session.QuestionnaireSession;
import io.dialob.questionnaire.service.api.session.QuestionnaireSessionService;
import io.dialob.settings.CorsSettings;
import io.dialob.settings.SessionSettings;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.web.cors.CorsConfiguration;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TenantBasedCorsConfigurationSourceTest {

  @Test
  void shouldNotGivePolicyIfNonIsConfigured() throws Exception {
    SessionSettings sessionSettings = new SessionSettings();
    QuestionnaireSessionService questionnaireSessionService = Mockito.mock(QuestionnaireSessionService.class);
    final TenantFromRequestResolver tenantFromRequestResolver = new SessionRestTenantFromRequestResolver(questionnaireSessionService);

    TenantBasedCorsConfigurationSource source = new TenantBasedCorsConfigurationSource(sessionSettings.getRest().getCors()::get, tenantFromRequestResolver);

    HttpServletRequest request = Mockito.mock(HttpServletRequest.class);

    assertNull(source.getCorsConfiguration(request));

    verify(request).getParameter("sessionId");
    verify(request).getPathInfo();
    verifyNoMoreInteractions(request, questionnaireSessionService);
  }

  @Test
  void shouldGiveDefaultPolicyIfRequestDoNotMatch() throws Exception {
    SessionSettings sessionSettings = new SessionSettings();
    QuestionnaireSessionService questionnaireSessionService = Mockito.mock(QuestionnaireSessionService.class);
    final TenantFromRequestResolver tenantFromRequestResolver = new SessionRestTenantFromRequestResolver(questionnaireSessionService);


    CorsSettings corsSettings = new CorsSettings();
    corsSettings.getAllowedOrigins().add("*");
    sessionSettings.getRest().getCors().put("default", corsSettings);

    TenantBasedCorsConfigurationSource source = new TenantBasedCorsConfigurationSource(sessionSettings.getRest().getCors()::get, tenantFromRequestResolver);

    HttpServletRequest request = Mockito.mock(HttpServletRequest.class);

    CorsConfiguration corsConfiguration = source.getCorsConfiguration(request);
    assertNotNull(corsConfiguration);
    assertIterableEquals(List.of("*"), corsConfiguration.getAllowedOrigins());

    verify(request).getParameter("sessionId");
    verify(request).getPathInfo();
    verifyNoMoreInteractions(request, questionnaireSessionService);
  }

  @Test
  void shouldResolveTenantFromQuestionnaireAndGivePolicyConfiguredToThatTenant() throws Exception {
    SessionSettings sessionSettings = new SessionSettings();
    QuestionnaireSessionService questionnaireSessionService = Mockito.mock(QuestionnaireSessionService.class);
    final TenantFromRequestResolver tenantFromRequestResolver = new SessionRestTenantFromRequestResolver(questionnaireSessionService);
    QuestionnaireSession questionnaireSession = Mockito.mock(QuestionnaireSession.class);


    CorsSettings corsSettings = new CorsSettings();
    corsSettings.getAllowedOrigins().add("*");
//    sessionSettings.getRest().getCors().put("default", corsSettings);
    sessionSettings.getRest().getCors().put("tenant-id", corsSettings);

    TenantBasedCorsConfigurationSource source = new TenantBasedCorsConfigurationSource(sessionSettings.getRest().getCors()::get, tenantFromRequestResolver);

    HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
    when(request.getParameter("sessionId")).thenReturn("aabb2233");
    when(questionnaireSessionService.findOne("aabb2233")).thenReturn(questionnaireSession);
    when(questionnaireSession.getTenantId()).thenReturn("tenant-id");

    CorsConfiguration corsConfiguration = source.getCorsConfiguration(request);
    assertNotNull(corsConfiguration);
    assertIterableEquals(List.of("*"), corsConfiguration.getAllowedOrigins());

    verify(request).getParameter("sessionId");
    verify(questionnaireSessionService).findOne("aabb2233");
    verify(questionnaireSession).getTenantId();
    verifyNoMoreInteractions(request, questionnaireSessionService,questionnaireSession);
  }

  @Test
  void shouldResolveTenantFromQuestionnaireAndReturnNullWhenDefaultIsNotConfigured() throws Exception {
    SessionSettings sessionSettings = new SessionSettings();
    QuestionnaireSessionService questionnaireSessionService = Mockito.mock(QuestionnaireSessionService.class);
    final TenantFromRequestResolver tenantFromRequestResolver = new SessionRestTenantFromRequestResolver(questionnaireSessionService);
    QuestionnaireSession questionnaireSession = Mockito.mock(QuestionnaireSession.class);


    CorsSettings corsSettings = new CorsSettings();
    corsSettings.getAllowedOrigins().add("*");
    sessionSettings.getRest().getCors().put("tenant-id", corsSettings);

    TenantBasedCorsConfigurationSource source = new TenantBasedCorsConfigurationSource(sessionSettings.getRest().getCors()::get, tenantFromRequestResolver);

    HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
    when(request.getParameter("sessionId")).thenReturn("abc123");
    when(questionnaireSessionService.findOne("abc123")).thenReturn(questionnaireSession);
    when(questionnaireSession.getTenantId()).thenReturn("tenant-id-unk");

    CorsConfiguration corsConfiguration = source.getCorsConfiguration(request);
    assertNull(corsConfiguration);

    verify(request).getParameter("sessionId");
    verify(questionnaireSessionService).findOne("abc123");
    verify(questionnaireSession).getTenantId();
    verifyNoMoreInteractions(request, questionnaireSessionService,questionnaireSession);
  }

  @Test
  void shouldResolveTenantFromQuestionnaireAndGiveDefaultPolicyWhenThereIsNonForTenant() throws Exception {
    SessionSettings sessionSettings = new SessionSettings();
    QuestionnaireSessionService questionnaireSessionService = Mockito.mock(QuestionnaireSessionService.class);
    final TenantFromRequestResolver tenantFromRequestResolver = new SessionRestTenantFromRequestResolver(questionnaireSessionService);
    QuestionnaireSession questionnaireSession = Mockito.mock(QuestionnaireSession.class);


    CorsSettings corsSettings = new CorsSettings();
    corsSettings.getAllowedOrigins().add("*");
    sessionSettings.getRest().getCors().put("default", corsSettings);
    corsSettings = new CorsSettings();
    corsSettings.getAllowedOrigins().add("tenant");
    sessionSettings.getRest().getCors().put("tenant-id", corsSettings);

    TenantBasedCorsConfigurationSource source = new TenantBasedCorsConfigurationSource(sessionSettings.getRest().getCors()::get, tenantFromRequestResolver);

    HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
    when(request.getParameter("sessionId")).thenReturn("123abc");
    when(questionnaireSessionService.findOne("123abc")).thenReturn(questionnaireSession);
    when(questionnaireSession.getTenantId()).thenReturn("tenant-id-none");

    CorsConfiguration corsConfiguration = source.getCorsConfiguration(request);
    assertNotNull(corsConfiguration);
    assertIterableEquals(List.of("*"), corsConfiguration.getAllowedOrigins());

    verify(request).getParameter("sessionId");
    verify(questionnaireSessionService).findOne("123abc");
    verify(questionnaireSession).getTenantId();
    verifyNoMoreInteractions(request, questionnaireSessionService,questionnaireSession);
  }

}
