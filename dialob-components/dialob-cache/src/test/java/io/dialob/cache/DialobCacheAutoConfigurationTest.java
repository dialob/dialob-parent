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
package io.dialob.cache;

import io.dialob.common.Constants;
import io.dialob.questionnaire.service.api.session.QuestionnaireSession;
import io.dialob.questionnaire.service.api.session.QuestionnaireSessionSaveService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.AdditionalAnswers;
import org.mockito.Mockito;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.cache.CacheManager;
import org.springframework.cache.support.NoOpCache;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheManager;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class DialobCacheAutoConfigurationTest {

  @Configuration(proxyBeanMethods = false)
  public static class TestConfiguration {

    @Bean
    public QuestionnaireSessionSaveService sessionService() {
      return mock(QuestionnaireSessionSaveService.class);
    }

  }

  @Test
  public void testDialobCacheAutoConfiguration() {
    new ApplicationContextRunner()
      .withConfiguration(AutoConfigurations.of(DialobCacheAutoConfiguration.class))
      .withUserConfiguration(TestConfiguration.class)
      .run(context -> {
        assertThat(context)
          .doesNotHaveBean(RedisCacheManager.class)
          .hasSingleBean(ScheduledSessionEvictionPolicy.class)
          .hasBean("cacheManager");

        CacheManager cacheManager = (CacheManager) context.getBean("cacheManager");
        assertThat(cacheManager.getCache(Constants.SESSION_CACHE_NAME))
          .isNotNull()
          .isInstanceOf(LocalQuestionnaireSessionCache.class);

      });
  }

  @Test
  public void testDialobCacheAutoConfigurationWithoutEvictPersistance() {
    new ApplicationContextRunner()
      .withConfiguration(AutoConfigurations.of(DialobCacheAutoConfiguration.class))
      .withUserConfiguration(TestConfiguration.class)
      .withSystemProperties("dialob.session.cache.persist-on-eviction=false")

      .run(context -> {
        assertThat(context)
          .doesNotHaveBean(RedisCacheManager.class)
          .hasSingleBean(ScheduledSessionEvictionPolicy.class)
          .hasBean("cacheManager");

        CacheManager cacheManager = (CacheManager) context.getBean("cacheManager");
        QuestionnaireSessionSaveService sss = context.getBean(QuestionnaireSessionSaveService.class);
        Mockito.reset(sss);
        LocalQuestionnaireSessionCache cache = (LocalQuestionnaireSessionCache) cacheManager.getCache(Constants.SESSION_CACHE_NAME);
        Assertions.assertNotNull(cache);

        QuestionnaireSession questionnaireSession = mock();
        when(questionnaireSession.getSessionId()).thenReturn(Optional.of("123"));
        when(questionnaireSession.getRev()).thenReturn("1");
        when(questionnaireSession.getTenantId()).thenReturn("t1");
        when(questionnaireSession.isActive()).thenReturn(true);
        cache.put(questionnaireSession);
        Assertions.assertEquals(1, cache.size());

        ScheduledSessionEvictionPolicy policy = context.getBean(ScheduledSessionEvictionPolicy.class);
        policy.evict("123");
        Assertions.assertEquals(0, cache.size());
        // Not called for evicted session
        verifyNoInteractions(sss);
      });
  }

  @Test
  public void testDialobCacheAutoConfigurationWithEvictPersistance() {
    new ApplicationContextRunner()
      .withConfiguration(AutoConfigurations.of(DialobCacheAutoConfiguration.class))
      .withUserConfiguration(TestConfiguration.class)
      .withSystemProperties("dialob.session.cache.persist-on-eviction=true")

      .run(context -> {
        assertThat(context)
          .doesNotHaveBean(RedisCacheManager.class)
          .hasSingleBean(ScheduledSessionEvictionPolicy.class)
          .hasBean("cacheManager");

        CacheManager cacheManager = (CacheManager) context.getBean("cacheManager");
        QuestionnaireSessionSaveService sss = context.getBean(QuestionnaireSessionSaveService.class);
        Mockito.reset(sss);
        when(sss.save(any())).thenAnswer(AdditionalAnswers.returnsFirstArg());
        LocalQuestionnaireSessionCache cache = (LocalQuestionnaireSessionCache) cacheManager.getCache(Constants.SESSION_CACHE_NAME);
        Assertions.assertNotNull(cache);

        QuestionnaireSession questionnaireSession = mock();
        when(questionnaireSession.getSessionId()).thenReturn(Optional.of("123"));
        when(questionnaireSession.getRev()).thenReturn("1");
        when(questionnaireSession.getTenantId()).thenReturn("t1");
        when(questionnaireSession.isActive()).thenReturn(true);
        cache.put(questionnaireSession);
        Assertions.assertEquals(1, cache.size());

        ScheduledSessionEvictionPolicy policy = context.getBean(ScheduledSessionEvictionPolicy.class);
        policy.evict("123");
        Assertions.assertEquals(0, cache.size());
        // Called for evicted session
        verify(sss).save(questionnaireSession);
        verifyNoMoreInteractions(sss);
      });
  }



  @Test
  public void testDialobCacheAutoConfigurationWithoutLocalCache() {
    new ApplicationContextRunner()
      .withConfiguration(AutoConfigurations.of(DialobCacheAutoConfiguration.class))
      .withUserConfiguration(TestConfiguration.class)
      .withSystemProperties("dialob.session.cache.type=NONE")
      .run(context -> {
        assertThat(context)
          .doesNotHaveBean(RedisCacheManager.class)
          .doesNotHaveBean(ScheduledSessionEvictionPolicy.class)
          .hasBean("cacheManager");
        CacheManager cacheManager = (CacheManager) context.getBean("cacheManager");
        assertThat(cacheManager.getCache(Constants.SESSION_CACHE_NAME))
          .isNotNull()
          .isInstanceOf(NoOpCache.class);
      });
  }


  // TODO make redis co-operate with local caching
  @Test
  public void testDialobCacheAutoConfigurationUsingRedisCache() {
    new ApplicationContextRunner()
      .withConfiguration(AutoConfigurations.of(DialobCacheAutoConfiguration.class))
      .withUserConfiguration(TestConfiguration.class)
      .withSystemProperties("dialob.session.cache.type=REDIS")
      .run(context -> {
        assertThat(context)
          .doesNotHaveBean(RedisCacheManager.class)
          .doesNotHaveBean(ScheduledSessionEvictionPolicy.class)
          .hasBean("cacheManager");
        CacheManager cacheManager = (CacheManager) context.getBean("cacheManager");
        assertThat(cacheManager.getCache(Constants.SESSION_CACHE_NAME))
          .isNotNull()
          .isInstanceOf(NoOpCache.class);
      });
  }


}
