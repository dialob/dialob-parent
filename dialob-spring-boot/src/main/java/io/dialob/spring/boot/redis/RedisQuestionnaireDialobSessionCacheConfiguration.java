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
package io.dialob.spring.boot.redis;

import com.google.common.collect.ImmutableSet;
import edu.umd.cs.findbugs.annotations.NonNull;
import io.dialob.common.Constants;
import io.dialob.questionnaire.service.api.event.QuestionnaireEventPublisher;
import io.dialob.questionnaire.service.api.session.QuestionnaireSessionService;
import io.dialob.session.engine.DialobProgramService;
import io.dialob.session.engine.program.DialobSessionEvalContextFactory;
import io.dialob.session.engine.sp.AsyncFunctionInvoker;
import io.dialob.settings.DialobSettings;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import java.util.Optional;

@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(value = "dialob.session.cache.type", havingValue = "REDIS")
@Import(RedisAutoConfiguration.class)
public class RedisQuestionnaireDialobSessionCacheConfiguration {

  @Bean
  public QuestionnaireDialobSessionRedisSerializer dialobSessionSerializer(@NonNull QuestionnaireSessionService questionnaireSessionService,
                                                                           @NonNull DialobProgramService dialobProgramService,
                                                                           @NonNull Optional<MeterRegistry> meterRegistry,
                                                                           @NonNull DialobSettings dialobSettings,
                                                                           @NonNull QuestionnaireEventPublisher eventPublisher,
                                                                           @NonNull DialobSessionEvalContextFactory sessionContextFactory,
                                                                           @NonNull AsyncFunctionInvoker asyncFunctionInvoker) {
    return new QuestionnaireDialobSessionRedisSerializer(questionnaireSessionService,
      eventPublisher, dialobProgramService, sessionContextFactory, asyncFunctionInvoker, meterRegistry,
      dialobSettings.getSession().getCache().getBufferSize());
  }

  @Bean(name = Constants.SESSION_CACHE_MANAGER_BEAN)
  public RedisCacheManager sessionCacheManager(@NonNull RedisConnectionFactory redisConnectionFactory,
                                               @NonNull QuestionnaireDialobSessionRedisSerializer dialobSessionSerializer) {
    return RedisCacheManager.builder(redisConnectionFactory)
      .cacheDefaults(RedisCacheConfiguration
        .defaultCacheConfig()
        .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(dialobSessionSerializer)))
      .initialCacheNames(ImmutableSet.of(Constants.SESSION_CACHE_NAME))
      .disableCreateOnMissingCache()
      .build();
  }
}
