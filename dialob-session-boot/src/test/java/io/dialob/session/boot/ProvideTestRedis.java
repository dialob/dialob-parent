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
package io.dialob.session.boot;

import com.redis.testcontainers.RedisContainer;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
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
    Socket socket = null;
    InetSocketAddress endpoint = new InetSocketAddress(redis.getHost(), port);
    int i = 1000;
    while (--i > 0) {
      try {
        socket = new Socket();
        socket.connect(endpoint);
        LoggerFactory.getLogger(ProvideTestRedis.class)
          .info("Redis running at {}:{}", redis.getHost(), port);
        return;
      } catch (ConnectException e) {
        try {
          Thread.sleep(100L);
        } catch (InterruptedException ex) {
          throw new RuntimeException(ex);
        }
      } catch (IOException e) {
        throw new RuntimeException(e);
      } finally {
        IOUtils.closeQuietly(socket);
      }
    }
    LoggerFactory.getLogger(ProvideTestRedis.class)
      .info("Redis didn't respond at {}:{}", redis.getHost(), port);

  }
  @AfterAll
  static void stopRedis() {
    redis.stop();
    LoggerFactory.getLogger(ProvideTestRedis.class)
      .info("Redis stopped");
  }

}
