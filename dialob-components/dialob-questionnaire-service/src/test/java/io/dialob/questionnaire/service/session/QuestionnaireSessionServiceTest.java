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
package io.dialob.questionnaire.service.session;

import io.dialob.questionnaire.service.api.session.QuestionnaireSession;
import io.dialob.questionnaire.service.api.session.QuestionnaireSessionSaveService;
import io.dialob.questionnaire.service.api.session.QuestionnaireSessionService;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@EnableCaching
public class QuestionnaireSessionServiceTest {

  static QuestionnaireSessionService questionnaireSessionServiceMock = mock(QuestionnaireSessionService.class);

  static QuestionnaireSessionSaveService questionnaireSessionSaveServiceMock = mock(QuestionnaireSessionSaveService.class);

  @Inject
  public CacheManager cacheManager;

  @Configuration(proxyBeanMethods = false)
  @ImportResource("classpath:dialob-questionnaire-service-cache-context.xml")
  public static class TestConfiguration {
    @Bean
    public QuestionnaireSessionService questionnaireSessionService() {
      return questionnaireSessionServiceMock;
    }
    @Bean
    public QuestionnaireSessionSaveService questionnaireSessionSaveService() {
      return questionnaireSessionSaveServiceMock;
    }


    @Bean
    public CacheManager cacheManager() {
      return Mockito.mock(CacheManager.class);
    }

  }

  @BeforeEach
  public void setup() {
    Mockito.reset(questionnaireSessionServiceMock);
  }

  @Inject
  public QuestionnaireSessionService questionnaireSessionService;
  @Inject
  public QuestionnaireSessionSaveService questionnaireSessionSaveService;

  @Test
  public void findOneShouldCacheNonNullResult() {
    Cache cache = setupCache();

    when(cache.get("123")).thenReturn(null);
    QuestionnaireSession session = mock(QuestionnaireSession.class);
    when(questionnaireSessionServiceMock.findOne("123")).thenReturn(session);

    questionnaireSessionService.findOne("123");

    verify(cache).getName();
    verify(cache).get("123");
    verify(cache).put("123", session);
    verify(questionnaireSessionServiceMock).findOne("123");
    verifyNoMoreInteractions(cache, questionnaireSessionServiceMock);
  }

  @Test
  public void findOneShouldNotCacheNullResult() {
    Cache cache = setupCache();

    when(cache.get("123")).thenReturn(null);
    QuestionnaireSession session = mock(QuestionnaireSession.class);
    when(questionnaireSessionServiceMock.findOne("123")).thenReturn(null);

    questionnaireSessionService.findOne("123");

    verify(cache).getName();
    verify(cache).get("123");
    verify(questionnaireSessionServiceMock).findOne("123");
    verifyNoMoreInteractions(cache, questionnaireSessionServiceMock);
  }

  @Test
  public void findOneWithOpenFalseShouldReturnNullIfItemNotInCache() {
    Cache cache = setupCache();

    when(cache.get("123")).thenReturn(null);
    QuestionnaireSession session = mock(QuestionnaireSession.class);

    assertNull(questionnaireSessionService.findOne("123", false));

    verify(cache).getName();
    verify(cache).get("123");
    verify(questionnaireSessionServiceMock).findOne("123", false);
    verifyNoMoreInteractions(cache, questionnaireSessionServiceMock);
  }

  @Test
  public void saveShouldCacheResult() {
    Cache cache = setupCache();

    QuestionnaireSession session = mock(QuestionnaireSession.class);
    QuestionnaireSession sessionOut = mock(QuestionnaireSession.class);
    when(sessionOut.getSessionId()).thenReturn(Optional.of("321"));
    when(questionnaireSessionSaveServiceMock.save(session)).thenReturn(sessionOut);

    assertSame(sessionOut, questionnaireSessionSaveService.save(session));

    verify(cache).getName();
    verify(cache).put(Optional.of("321"), sessionOut);
    verify(questionnaireSessionSaveServiceMock).save(session);
    verifyNoMoreInteractions(cache, questionnaireSessionServiceMock, session);
  }


  private Cache setupCache() {
    final Cache cache = mock(Cache.class);
    when(cache.getName()).thenReturn("sessionCache");
    when(cacheManager.getCache("sessionCache")).thenReturn(cache);
    return cache;
  }


}
