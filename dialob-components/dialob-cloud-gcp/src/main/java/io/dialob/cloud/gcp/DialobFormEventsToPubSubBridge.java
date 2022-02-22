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
package io.dialob.cloud.gcp;

import lombok.extern.slf4j.Slf4j;
import io.dialob.integration.api.event.FormDeletedEvent;
import io.dialob.integration.api.event.FormTaggedEvent;
import io.dialob.integration.api.event.FormUpdatedEvent;
import org.springframework.context.event.EventListener;

@Slf4j
public class DialobFormEventsToPubSubBridge {

  private final DialobFormEventMessagingGateway messagingGateway;

  public DialobFormEventsToPubSubBridge(DialobFormEventMessagingGateway messagingGateway) {
    this.messagingGateway = messagingGateway;
  }

  @EventListener
  public void onFormTaggedEvent(FormTaggedEvent event) {
    messagingGateway.onFormTaggedEvent(event);
  }

  @EventListener
  public void onFormDeletedEvent(FormDeletedEvent event) {
    messagingGateway.onFormDeletedEvent(event);
  }

  @EventListener
  public void onFormUpdatedEvent(FormUpdatedEvent event) {
    messagingGateway.onFormUpdatedEvent(event);
  }
}
