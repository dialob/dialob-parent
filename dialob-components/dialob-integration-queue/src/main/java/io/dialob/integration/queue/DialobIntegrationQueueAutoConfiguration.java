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
package io.dialob.integration.queue;

import io.dialob.integration.api.Constants;
import io.dialob.integration.api.ImmutableNodeId;
import io.dialob.integration.api.NodeId;
import io.dialob.integration.queue.redis.DistributedEventBridge;
import jakarta.inject.Named;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.support.MessageBuilderFactory;
import org.springframework.messaging.MessageChannel;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.UUID;

@Configuration(proxyBeanMethods = false)
@AutoConfigureAfter(name = "io.dialob.integration.redis.DialobIntegrationRedisAutoConfiguration")
public class DialobIntegrationQueueAutoConfiguration {

  private static final Logger LOGGER = LoggerFactory.getLogger(DialobIntegrationQueueAutoConfiguration.class);

  @Bean
  public NodeId nodeId(ServerProperties serverProperties) throws UnknownHostException {
    String host = InetAddress.getLocalHost().getHostName();
    NodeId nodeId = ImmutableNodeId.builder().id(UUID.randomUUID().toString()).host(host).port(serverProperties.getPort()).build();
    LOGGER.info("Created node : {}", nodeId);
    return nodeId;
  }

  @Bean
  @ConditionalOnBean(name = Constants.DIALOB_NODE_STATUS_CHANNEL_BEAN)
  public DistributedEventBridge distributedEventBridge(@Named(Constants.DIALOB_NODE_STATUS_CHANNEL_BEAN) MessageChannel messageChannel, ApplicationEventPublisher publisher, MessageBuilderFactory messageBuilderFactory, NodeId nodeId) {
    return new DistributedEventBridge(messageChannel, publisher, messageBuilderFactory, nodeId);
  }

}
