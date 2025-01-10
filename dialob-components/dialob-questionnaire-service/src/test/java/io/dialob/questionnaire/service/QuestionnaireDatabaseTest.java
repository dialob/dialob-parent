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
package io.dialob.questionnaire.service;

import io.dialob.api.questionnaire.Questionnaire;
import io.dialob.questionnaire.service.api.QuestionnaireDatabase;
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

import java.util.Arrays;

import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@EnableCaching
public class QuestionnaireDatabaseTest {

  static QuestionnaireDatabase questionnaireDatabaseMock = mock(QuestionnaireDatabase.class);

  private String tenantId = "t-123";

  @Inject
  public CacheManager cacheManager;

  @Configuration(proxyBeanMethods = false)
  @ImportResource("classpath:dialob-questionnaire-service-cache-context.xml")
  public static class TestConfiguration {

    @Bean
    public QuestionnaireDatabase questionnaireDatabase() {
      return questionnaireDatabaseMock;
    }

    @Bean
    public CacheManager cacheManager() {
      return Mockito.mock(CacheManager.class);
    }

  }

  @BeforeEach
  public void setup() {
    Mockito.reset(questionnaireDatabaseMock);
  }

  @Inject
  public QuestionnaireDatabase questionnaireDatabase;

  @Test
  public void shouldStoreQuestionnaireIntoCacheIfIsNotThereAlready() {
    final Cache cache = setupCache("questionnaireCache");
    Questionnaire questionnaire = mock(Questionnaire.class);
    doReturn(questionnaire).when(questionnaireDatabaseMock).findOne(tenantId, "123");
    when(cache.get(Arrays.asList("q","t-123", "123",null))).thenReturn(null);

    questionnaireDatabase.findOne(tenantId, "123");

    verify(cache).getName();
    verify(cache).get(Arrays.asList("q","t-123", "123",null));
    verify(questionnaireDatabaseMock).findOne(tenantId, "123");
    verify(cache).put(Arrays.asList("q","t-123", "123",null), questionnaire);
    verifyNoMoreInteractions(cache, questionnaireDatabaseMock);
  }

  @Test
  public void shouldReturnQuestionnaireFromCache() {
    final Cache cache = setupCache("questionnaireCache");
    Questionnaire questionnaire = mock(Questionnaire.class);
    when(cache.get(Arrays.asList("q","t-123", "123",null))).thenReturn(() -> questionnaire);

    questionnaireDatabase.findOne(tenantId, "123");

    verify(cache).getName();
    verify(cache).get(Arrays.asList("q","t-123", "123",null));
    verifyNoMoreInteractions(cache, questionnaireDatabaseMock);
  }

  @Test
  public void shouldCheckQuestionnaireFromCache() {
    final Cache cache = setupCache("questionnaireCache");
    doReturn(true).when(questionnaireDatabaseMock).exists("t-123", "123");

    questionnaireDatabase.exists(tenantId, "123");

    verify(cache).getName();
    verify(cache).get(Arrays.asList("e","t-123", "123",null));
    verify(cache).put(Arrays.asList("e","t-123", "123",null), true);
    verify(questionnaireDatabaseMock).exists("t-123", "123");
    verifyNoMoreInteractions(cache, questionnaireDatabaseMock);
  }



  @Test
  public void saveShouldUpdateObjectInCacheWithReturnValue() {
    final Cache cache = setupCache("questionnaireCache");
    final Questionnaire questionnaireIn = mock(Questionnaire.class);
    final Questionnaire questionnaireOut = mock(Questionnaire.class);
    when(questionnaireOut.getId()).thenReturn("123");
    when(questionnaireOut.getRev()).thenReturn("r-123");
    doReturn(questionnaireOut).when(questionnaireDatabaseMock).save(tenantId, questionnaireIn);

    questionnaireDatabase.save(tenantId, questionnaireIn);

    verify(cache, times(3)).getName();
    verify(cache).put(Arrays.asList("q","t-123", "123",null), questionnaireOut);
    verify(cache).put(Arrays.asList("q","t-123", "123","r-123"), questionnaireOut);
    verify(cache).evict(Arrays.asList("e","t-123", "123",null));
    verify(questionnaireOut, times(3)).getId();
    verify(questionnaireDatabaseMock).save(tenantId, questionnaireIn);
    verifyNoMoreInteractions(cache, questionnaireDatabaseMock);
  }

  @Test
  public void deleteShouldEvictQuestionnaireFromCache() {
    final Cache cache = setupCache("questionnaireCache");

    questionnaireDatabase.delete(tenantId, "123");

    verify(cache).getName();
    verify(cache).clear();
    verify(questionnaireDatabaseMock).delete(tenantId, "123");
    verifyNoMoreInteractions(cache, questionnaireDatabaseMock);
  }

  private Cache setupCache(String cacheName) {
    final Cache cache = mock(Cache.class);
    when(cache.getName()).thenReturn(cacheName);
    when(cacheManager.getCache(cacheName)).thenReturn(cache);
    return cache;
  }

}
