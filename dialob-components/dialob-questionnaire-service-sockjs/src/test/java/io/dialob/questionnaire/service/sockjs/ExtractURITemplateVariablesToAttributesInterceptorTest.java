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

import org.junit.jupiter.api.Test;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.socket.WebSocketHandler;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

public class ExtractURITemplateVariablesToAttributesInterceptorTest {

  @Test
  public void shouldNotExtractAnything() throws Exception {
    ExtractURITemplateVariablesToAttributesInterceptor interceptor = new ExtractURITemplateVariablesToAttributesInterceptor();
    Map<String,Object> attributes = new HashMap<>();
    WebSocketHandler wsHandler = mock(WebSocketHandler.class);
    ServletServerHttpRequest request = mock(ServletServerHttpRequest.class);
    ServerHttpResponse response = mock(ServerHttpResponse.class);
    HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);

    when(request.getServletRequest()).thenReturn(httpServletRequest);

    interceptor.beforeHandshake(request, response, wsHandler, attributes);

    assertTrue(attributes.isEmpty());
    verify(request).getServletRequest();
    verifyNoMoreInteractions(request,response,wsHandler);
  }

  @Test
  public void shouldExtractId() throws Exception {
    ExtractURITemplateVariablesToAttributesInterceptor interceptor = new ExtractURITemplateVariablesToAttributesInterceptor("id");
    Map<String,Object> attributes = new HashMap<>();
    Map<String,Object> variables = new HashMap<>();
    variables.put("id","123");
    WebSocketHandler wsHandler = mock(WebSocketHandler.class);
    ServletServerHttpRequest request = mock(ServletServerHttpRequest.class);
    ServerHttpResponse response = mock(ServerHttpResponse.class);
    HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);


    when(request.getServletRequest()).thenReturn(httpServletRequest);
    when(httpServletRequest.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE)).thenReturn(variables);

    interceptor.beforeHandshake(request, response, wsHandler, attributes);

    assertEquals(1, attributes.size());
    assertEquals("123", attributes.get("id"));
    verify(request).getServletRequest();
    verifyNoMoreInteractions(request,response,wsHandler);
  }
}
