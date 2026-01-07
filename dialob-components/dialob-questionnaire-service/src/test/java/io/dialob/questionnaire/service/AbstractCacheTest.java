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

import io.dialob.questionnaire.service.api.QuestionnaireDatabase;
import io.dialob.questionnaire.service.api.session.QuestionnaireSessionSaveService;
import io.dialob.questionnaire.service.api.session.QuestionnaireSessionService;
import org.junit.jupiter.api.Assertions;
import org.springframework.aop.support.AopUtils;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.AopTestUtils;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public abstract class AbstractCacheTest {

  @MockitoBean("cacheManager")
  protected CacheManager cacheManager;

  @Configuration(proxyBeanMethods = false)
  @ImportResource("classpath:dialob-questionnaire-service-cache-context.xml")
  public static class TestConfiguration {

    // AOP won't decorate @MockitoBean's
    @Bean
    public QuestionnaireSessionService questionnaireSessionService() {
      return mock(QuestionnaireSessionService.class);
    }

    @Bean
    public QuestionnaireSessionSaveService questionnaireSessionSaveService() {
      return mock(QuestionnaireSessionSaveService.class);
    }

    @Bean
    public QuestionnaireDatabase questionnaireDatabase() {
      return mock(QuestionnaireDatabase.class);
    }
  }


  protected Cache setupCache(String cacheName) {
    final Cache cache = mock();
    when(cache.getName()).thenReturn(cacheName);
    when(cacheManager.getCache(cacheName)).thenReturn(cache);
    return cache;
  }


  protected <T> T unwrap(T mock) {
    Assertions.assertTrue(AopUtils.isAopProxy(mock));
    return AopTestUtils.getTargetObject(mock);
  }

}
