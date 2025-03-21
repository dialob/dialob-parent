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
package io.dialob.cache;

import com.github.benmanes.caffeine.cache.Caffeine;
import edu.umd.cs.findbugs.annotations.NonNull;
import io.dialob.common.Constants;
import io.dialob.questionnaire.service.api.session.QuestionnaireSession;
import io.dialob.questionnaire.service.api.session.QuestionnaireSessionSaveService;
import io.dialob.settings.DialobSettings;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.cache.support.CompositeCacheManager;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;

import java.util.*;
import java.util.function.Function;

import static io.dialob.common.Constants.QUESTIONNAIRE_CACHE_NAME;

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(DialobSettings.class)
public class DialobCacheAutoConfiguration {

  @Configuration(proxyBeanMethods = false)
  @ConditionalOnProperty(value = "dialob.session.cache.type", matchIfMissing = true, havingValue = "LOCAL")
  public static class LocalQuestionnaireDialobSessionCacheConfiguration {

    private final QuestionnaireSessionCache questionnaireSessionCache = new LocalQuestionnaireSessionCache(Constants.SESSION_CACHE_NAME);

    @Bean(name = Constants.SESSION_CACHE_MANAGER_BEAN)
    public CacheManager sessionCacheManager() {
      SimpleCacheManager simpleCacheManager = new SimpleCacheManager();
      simpleCacheManager.setCaches(Collections.singletonList(questionnaireSessionCache));
      simpleCacheManager.afterPropertiesSet();
      return simpleCacheManager;
    }

    @Bean
    public ScheduledSessionEvictionPolicy scheduledSessionEvictionPolicy(Optional<QuestionnaireSessionSaveService> sessionService, Optional<CacheManager> cacheManager, DialobSettings dialobSettings) {
      return new ScheduledSessionEvictionPolicy(
        questionnaireSessionCache,
        sessionEvictionCallback(sessionService, dialobSettings),
        cacheManager, dialobSettings.getSession().getCache().getTimeToLive());
    }

    Function<QuestionnaireSession, QuestionnaireSession> sessionEvictionCallback(Optional<QuestionnaireSessionSaveService> sessionService, DialobSettings dialobSettings) {
      if (dialobSettings.getSession().getCache().isPersistOnEviction()) {
        return sessionService.map(ss -> (Function<QuestionnaireSession, QuestionnaireSession>) ss::save).orElseGet(Function::identity);
      }
      // Do nothing
      return Function.identity();
    }
  }

  @Bean(name = Constants.PROGRAM_CACHE_MANAGER_BEAN)
  public CacheManager dialobProgramsCacheManager() {
    CaffeineCacheManager caffeineCacheManager = new CaffeineCacheManager(Constants.PROGRAM_CACHE_NAME);
    caffeineCacheManager.setCaffeine(Caffeine.newBuilder()
      .maximumSize(100)
    );
    caffeineCacheManager.setAllowNullValues(false);
    return caffeineCacheManager;
  }

  @Bean(name = Constants.QUESTIONNAIRE_CACHE_MANAGER_BEAN)
  @ConditionalOnBean(RedisConnectionFactory.class)
  public RedisCacheManager questionnaireCacheManager(@NonNull RedisConnectionFactory redisConnectionFactory) {
    RedisCacheConfiguration redisCacheConfiguration = RedisCacheConfiguration.defaultCacheConfig();
    return RedisCacheManager.builder(redisConnectionFactory)
      .cacheDefaults(redisCacheConfiguration)
      .initialCacheNames(Set.of(QUESTIONNAIRE_CACHE_NAME))
      .disableCreateOnMissingCache()
      .build();
  }

  @Bean(name = Constants.SESSION_ACCESS_CACHE_MANAGER_BEAN)
  public CacheManager sessionAccessCacheManager() {
    CaffeineCacheManager caffeineCacheManager = new CaffeineCacheManager(Constants.SESSION_ACCESS_CACHE_MANAGER_BEAN);
    caffeineCacheManager.setCaffeine(Caffeine.newBuilder()
      .maximumSize(100)
    );
    caffeineCacheManager.setAllowNullValues(false);
    return caffeineCacheManager;
  }


  @Bean
  @Primary
  public CacheManager cacheManager(
    @Qualifier(Constants.SESSION_CACHE_MANAGER_BEAN) Optional<CacheManager> localSessionCacheManager,
    @Qualifier(Constants.QUESTIONNAIRE_CACHE_MANAGER_BEAN) Optional<CacheManager> questionnaireCache,
    @Qualifier(Constants.FORM_CACHE_MANAGER_BEAN) Optional<CacheManager> formCacheManager,
    @Qualifier(Constants.PROGRAM_CACHE_MANAGER_BEAN) Optional<CacheManager> programCacheManager,
    @Qualifier(Constants.SESSION_ACCESS_CACHE_MANAGER_BEAN) Optional<CacheManager> sessionAccessCacheManager )
  {
    final CompositeCacheManager compositeCacheManager = new CompositeCacheManager();
    List<CacheManager> cacheManagers = new ArrayList<>();
    localSessionCacheManager.ifPresent(cacheManagers::add);
    questionnaireCache.ifPresent(cacheManagers::add);
    formCacheManager.ifPresent(cacheManagers::add);
    programCacheManager.ifPresent(cacheManagers::add);
    sessionAccessCacheManager.ifPresent(cacheManagers::add);
    compositeCacheManager.setCacheManagers(cacheManagers);
    compositeCacheManager.setFallbackToNoOpCache(true);
    return compositeCacheManager;
  }

}
