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
package io.dialob.integration.queue.redis;

import io.dialob.integration.api.Constants;
import io.dialob.integration.api.NodeId;
import io.dialob.integration.api.event.DistributedEvent;
import jakarta.inject.Named;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.support.MessageBuilderFactory;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageDeliveryException;

@Slf4j
public class DistributedEventBridge {

  private final MessageChannel messageChannel;

  private final ApplicationEventPublisher publisher;

  private final MessageBuilderFactory messageBuilderFactory;

  private final NodeId nodeId;

  public DistributedEventBridge(@Named(Constants.DIALOB_NODE_STATUS_CHANNEL_BEAN) MessageChannel messageChannel, ApplicationEventPublisher publisher, MessageBuilderFactory messageBuilderFactory, NodeId nodeId) {
    this.messageChannel = messageChannel;
    this.publisher = publisher;
    this.messageBuilderFactory = messageBuilderFactory;
    this.nodeId = nodeId;
  }

  @EventListener
  public void onNodeStatus(DistributedEvent event) {
    if (isFromThisNode(event)) {
      LOGGER.debug("Publish distributed event: {}", event);
      try {
        messageChannel.send(messageBuilderFactory
          .withPayload(event)
          .build());
      } catch (MessageDeliveryException mde) {
        LOGGER.debug("Ups!");
        throw mde;
      }
    }
  }

  @ServiceActivator(inputChannel = Constants.DIALOB_NODE_STATUS_CHANNEL_BEAN)
  public void receiveNodeStatus(DistributedEvent event) {
    LOGGER.debug("Received distributed event: {}", event);
    if (!isFromThisNode(event)) {
      publisher.publishEvent(event);
    }
  }

  private boolean isFromThisNode(DistributedEvent event) {
    return nodeId.getId().equals(event.getSource());
  }
}
