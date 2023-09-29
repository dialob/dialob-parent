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
package io.dialob.questionnaire.service.sockjs;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Slf4j
public class ExtractURITemplateVariablesToAttributesInterceptor implements HandshakeInterceptor {

  private final List<String> attributesToExtract = new ArrayList<>();

  public ExtractURITemplateVariablesToAttributesInterceptor(String... attributesToExtract) {
    this.attributesToExtract.addAll(Arrays.asList(attributesToExtract));
  }

  @Override
  public boolean beforeHandshake(@NonNull final ServerHttpRequest request, @NonNull final ServerHttpResponse response, @NonNull final WebSocketHandler wsHandler, @NonNull final Map<String, Object> attributes) throws Exception {
    if (request instanceof ServletServerHttpRequest) {
      final ServletServerHttpRequest serverRequest = (ServletServerHttpRequest) request;
      jakarta.servlet.http.HttpServletRequest servletRequest = serverRequest.getServletRequest();
      final Map<String, String> variables = (Map<String, String>) servletRequest.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
      LOGGER.debug("variables {}", variables);
      if (variables != null) {
        for (final String attribute : attributesToExtract) {
          final String value = variables.get(attribute);
          if (value != null) {
            attributes.put(attribute, value);
          }
        }
      }
    }

    return true;
  }

  @Override
  public void afterHandshake(@NonNull final ServerHttpRequest request, @NonNull final ServerHttpResponse response, @NonNull final WebSocketHandler wsHandler, final Exception exception) {
    // Nothing todo
  }
}
