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
package io.dialob.session.boot.websocket;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

import io.dialob.cache.DialobCacheAutoConfiguration;
import io.dialob.function.DialobFunctionAutoConfiguration;
import io.dialob.questionnaire.service.DialobQuestionnaireServiceAutoConfiguration;
import io.dialob.questionnaire.service.api.event.QuestionnaireEventPublisher;
import io.dialob.questionnaire.service.sockjs.DialobQuestionnaireServiceSockJSAutoConfiguration;
import io.dialob.security.tenant.CurrentTenant;
import io.dialob.session.boot.ApplicationAutoConfiguration;
import io.dialob.settings.DialobSettings;
import io.dialob.spring.boot.engine.DialobSessionEngineAutoConfiguration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.autoconfigure.web.servlet.ServletWebServerFactoryAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import io.dialob.api.proto.Action;
import io.dialob.db.spi.exceptions.DocumentNotFoundException;
import io.dialob.session.boot.Application;
import org.springframework.web.socket.config.annotation.EnableWebSocket;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = RANDOM_PORT, properties = {
  "dialob.db.database-type=none",
  "dialob.session.cache.type=LOCAL"
}, classes = {
  Application.class,
  ApplicationAutoConfiguration.class,
  QuestionnaireWebSocketTest.TestConfiguration.class,
  DialobQuestionnaireServiceSockJSAutoConfiguration.class,
  DialobFunctionAutoConfiguration.class,
  DialobQuestionnaireServiceAutoConfiguration.class,
  DialobSessionEngineAutoConfiguration.class,
  DialobCacheAutoConfiguration.class,
})
@EnableCaching
@EnableWebSocket
@EnableConfigurationProperties({DialobSettings.class})
public class QuestionnaireWebSocketTest extends AbstractWebSocketTests {

  @Test
  public void shouldGetQUESTIONNAIRE_NOT_FOUNDActionIfAskedQuestionnaireDoNotExists2() throws Exception {
    when(questionnaireDatabase.findOne(eq(tenantId), any())).thenThrow(DocumentNotFoundException.class);
    openSession("q-1")
      .expectActions(actions -> {
        assertEquals(1, actions.getActions().size());
        assertNull(actions.getActions().get(0).getTrace());
        assertEquals(Action.Type.SERVER_ERROR, actions.getActions().get(0).getType());
        assertEquals("not found", actions.getActions().get(0).getMessage());
        assertEquals("q-1", actions.getActions().get(0).getId());
      })
      .finallyAssert(webSocketHandler -> {
        verify(webSocketHandler).afterConnectionEstablished(any(WebSocketSession.class));
        verify(webSocketHandler).handleMessage(any(WebSocketSession.class), any(TextMessage.class));
        Mockito.verifyNoMoreInteractions(webSocketHandler);
      }).execute();
  }

  @Test
  public void connectWebsocketPerQuestionnaireID() throws Exception {
    when(questionnaireDatabase.findOne(eq(tenantId), any())).thenThrow(DocumentNotFoundException.class);
    openSession("1234")
      .expectActions(actions -> {
        assertEquals(1, actions.getActions().size());
        assertNull(actions.getActions().get(0).getTrace());
        assertEquals(Action.Type.SERVER_ERROR, actions.getActions().get(0).getType());
        assertEquals("1234", actions.getActions().get(0).getId());
      })
      .finallyAssert(webSocketHandler -> {
        verify(webSocketHandler).afterConnectionEstablished(any(WebSocketSession.class));
        verify(webSocketHandler).handleMessage(any(WebSocketSession.class), any(TextMessage.class));
        Mockito.verifyNoMoreInteractions(webSocketHandler);
      }).execute();
  }
}
