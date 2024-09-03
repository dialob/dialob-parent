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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.umd.cs.findbugs.annotations.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.messaging.support.GenericMessage;

import java.io.IOException;

@Slf4j
public class JsonMessageConverter<T> implements MessageConverter {

  private final ObjectMapper mapper;

  private final Class<T>  type;

  public JsonMessageConverter(ObjectMapper mapper, Class<T>  type) {
    this.mapper = mapper;
    this.type = type;
  }

  @Override
  public Object fromMessage(@NonNull Message<?> message, @NonNull Class<?> targetClass) {
    try {
      return mapper.writeValueAsString(message.getPayload());
    } catch (JsonProcessingException e) {
      LOGGER.debug("Could not parse message payload. Ignoring message ", e);
    }
    return null;
  }

  @Override
  public Message<?> toMessage(@NonNull Object payload, MessageHeaders headers) {
    try {
      return new GenericMessage<>(mapper.readValue((String) payload, type));
    } catch (IOException e) {
      LOGGER.debug("Could not create message. Skipping message.", e);
    }
    return null;
  }
}
