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

import com.redis.testcontainers.RedisContainer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.utility.DockerImageName;

import java.io.IOException;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.Socket;

public interface ProvideTestRedis {
  RedisContainer redis = new RedisContainer(DockerImageName.parse("redis:7.0"));

  @DynamicPropertySource
  static void redisProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.data.redis.host", redis::getHost);
    registry.add("spring.data.redis.port", () -> redis.getMappedPort(6379));
  }

  @BeforeAll
  static void startRedis() {
    redis.start();
    var port = redis.getMappedPort(RedisContainer.REDIS_PORT);
    InetSocketAddress endpoint = new InetSocketAddress(redis.getHost(), port);
    int i = 100;
    while (--i > 0) {
      try(Socket socket = new Socket()) {
        socket.connect(endpoint);
        return;
      } catch (ConnectException e) {
        try {
          Thread.sleep(100L);
        } catch (InterruptedException ex) {
          throw new RuntimeException(ex);
        }
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
  }

  @AfterAll
  static void stopRedis() {
    redis.stop();
  }

}
