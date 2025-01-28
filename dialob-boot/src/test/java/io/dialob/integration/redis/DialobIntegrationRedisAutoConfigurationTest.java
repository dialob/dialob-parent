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
package io.dialob.integration.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.dialob.integration.api.Constants;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.test.context.runner.WebApplicationContextRunner;
import org.springframework.integration.redis.channel.SubscribableRedisChannel;

class DialobIntegrationRedisAutoConfigurationTest implements ProvideTestRedis {

  @Test
  void shouldSetupRedisChannelWhenRedisIsAvailable() {
    new WebApplicationContextRunner()
      .withConfiguration(AutoConfigurations.of(
        RedisAutoConfiguration.class,
        DialobIntegrationRedisAutoConfiguration.class
       ))
      .withBean(ObjectMapper.class)
      .withPropertyValues("spring.data.redis.port=" + redis.getMappedPort(6379))
      .withPropertyValues("spring.data.redis.host=" + redis.getHost())
      .run(context -> Assertions.assertThat(context)
        .getBean(Constants.DIALOB_NODE_STATUS_CHANNEL_BEAN).isInstanceOf(SubscribableRedisChannel.class));
  }

  @Test
  void shouldSetupDummyChannelWhenRedisIsNotAvailable() {
    new WebApplicationContextRunner()
      .withConfiguration(AutoConfigurations.of(
        DialobIntegrationRedisAutoConfiguration.class
      ))
      .withBean(ObjectMapper.class)
      .withPropertyValues("spring.data.redis.port=" + redis.getMappedPort(6379))
      .withPropertyValues("spring.data.redis.host=" + redis.getHost())
      .run(context -> Assertions.assertThat(context)
        .doesNotHaveBean(Constants.DIALOB_NODE_STATUS_CHANNEL_BEAN));
  }

}
