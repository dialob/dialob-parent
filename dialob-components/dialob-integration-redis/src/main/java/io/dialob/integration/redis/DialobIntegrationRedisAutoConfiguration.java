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
package io.dialob.integration.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.dialob.integration.api.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.integration.redis.channel.SubscribableRedisChannel;
import org.springframework.messaging.MessageChannel;

@Configuration(proxyBeanMethods = false)
@AutoConfigureAfter(RedisAutoConfiguration.class)
@Slf4j
public class DialobIntegrationRedisAutoConfiguration {

  @Bean(name = Constants.DIALOB_NODE_STATUS_CHANNEL_BEAN)
  @ConditionalOnBean(RedisConnectionFactory.class)
  public MessageChannel dialobEventsDistributionChannel(RedisConnectionFactory connectionFactory, ObjectMapper mapper) {
    LOGGER.info("Setting channel {} as SubscribableRedisChannel", Constants.DIALOB_NODE_STATUS_QUEUE_NAME);
    final SubscribableRedisChannel channel = new SubscribableRedisChannel(connectionFactory, Constants.DIALOB_NODE_STATUS_QUEUE_NAME);
    channel.setMessageConverter(new JsonDistributedEventMessageConverter(mapper));
    return channel;
  }

}
