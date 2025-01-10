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
package io.dialob.questionnaire.service.sockjs;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.dialob.api.proto.*;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.mockito.Mockito;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.socket.*;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import java.io.IOException;
import java.net.URI;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
public class WebSocketRequestTestTemplate {

  private final String uri;

  private final String jsessionid;

  protected String revision;

  protected WebSocketSession webSocketSession;

  protected WebSocketHandler webSocketHandler;

  protected SockJsClient sockJsClient;

  protected ObjectMapper objectMapper;

  protected List<String> websocketMessages = new ArrayList<>();

  public WebSocketRequestTestTemplate(ObjectMapper objectMapper, String uri, String jsessionid) {
    this.objectMapper = objectMapper;
    this.uri = uri;
    this.jsessionid = jsessionid;
  }

  public void openSession() throws Exception {
    if (webSocketSession == null) {
      final StandardWebSocketClient standardWebSocketClient = new StandardWebSocketClient();
      ArrayList<Transport> transports = new ArrayList();
      WebSocketTransport transport = new WebSocketTransport(standardWebSocketClient);
      transports.add(transport);
      sockJsClient = new SockJsClient(transports);
      WebSocketHttpHeaders headers = new WebSocketHttpHeaders();
      if (jsessionid != null) {
        headers.add("Cookie", jsessionid);
      }
      final ListenableFuture<WebSocketSession> listenableFuture = sockJsClient.doHandshake(webSocketHandler, headers, new URI(uri));
      assertFalse(listenableFuture.isDone());
      webSocketSession = listenableFuture.get(10, TimeUnit.SECONDS);
      assertTrue(listenableFuture.isDone());
      assertNotNull(webSocketSession.getId());
    }
  }

  public static abstract class WhenMessage {

    private final String name;

    private Long delay;

    private Expectations expectations;

    protected WhenMessage(String name) {
      this.name = name;
    }

    public abstract void accept(WebSocketSession webSocketSession) throws Exception;

    public String getName() {
      return name;
    }

    public void setDelay(Long delay) {
      this.delay = delay;
    }

    public Long getDelay() {
      return delay;
    }

    public Expectations getExpectations() {
      return expectations;
    }

    public void setExpectations(Expectations expectations) {
      this.expectations = expectations;
    }
  }

  public interface Expectation {
    void accept(WebSocketMessage message) throws Exception;
  }

  public static class NamedExpectation implements Expectation {

    private final String name;
    private final Expectation delegate;

    public NamedExpectation(String name, Expectation delegate) {
      this.name = name;
      this.delegate = delegate;
    }

    @Override
    public void accept(WebSocketMessage message) throws Exception {
      delegate.accept(message);
    }

    @Override
    public String toString() {
      return name;
    }
  }

  public interface Expectations extends Expectation {
    boolean isCompleted();
    boolean isSuccess();
    void addExpect(String name, Expectation expectConsumer);

    void addAccept(String name, Expectation expectConsumer);
  }

  public class StepsBuilder {

    private WhenMessage whenMessage;

    public ExpectionBuilder when(WhenMessage whenMessage) {
      this.whenMessage = whenMessage;
      steps.add(this.whenMessage);
      this.whenMessage.setExpectations(new OutOfOrderExpectations());
      return new ExpectionBuilder(this);
    }

    public WhenMessage getWhenMessage() {
      return whenMessage;
    }

    public ExpectionBuilder when(String name, Actions actions) {
      return when(new WhenMessage(name) {
        @Override
        public void accept(final WebSocketSession webSocketSession) throws Exception {
          final WebSocketMessage<String> webSockerMessage = new TextMessage(objectMapper.writeValueAsString(ImmutableActions.builder().from(actions).rev(revision).build()));
          websocketMessages.add("--> " + webSockerMessage.getPayload());
          webSocketSession.sendMessage(webSockerMessage);
        }
      });
    }

    public ExpectionBuilder when(String name, Action action) {
      return when(name, ImmutableActions.builder()
        .addActions(action).build());
    }

    public ExpectionBuilder answerQuestion(String questionId, String answer) {
      return when("answerQuestion(\"" + questionId + "\",\"" + answer + "\")", ImmutableAction.builder()
        .type(Action.Type.ANSWER)
        .answer(answer)
        .id(questionId).build());
    }

    public ExpectionBuilder answerQuestion(String questionId, List<String> answer) {
      return when("answerQuestion(\"" + questionId + "\",\"" + answer + "\")", ImmutableAction.builder()
        .type(Action.Type.ANSWER)
        .answer(answer)
        .id(questionId).build());
    }

    public ExpectionBuilder setLocale(String locale) {
      return when("setLocale(\"" + locale + "\")", ActionsFactory.setLocale(locale));
    }


    public ExpectionBuilder addRow(String rowGroupId) {
      return when("addRow(\"" + rowGroupId + "\")", ImmutableAction.builder()
        .type(Action.Type.ADD_ROW)
        .id(rowGroupId).build());
    }

    public ExpectionBuilder deleteRow(String rowGroupId) {
      return when("deleteRow(\"" + rowGroupId + "\")", ImmutableAction.builder()
        .type(Action.Type.DELETE_ROW)
        .id(rowGroupId).build());
    }

    public ExpectionBuilder nextPage() {
      final Action action = ImmutableAction.builder()
        .type(Action.Type.NEXT).build();
      return when("nextPage()", action);
    }

    public ExpectionBuilder prevPage() {
      return when("prevPage()", ImmutableAction.builder()
        .type(Action.Type.PREVIOUS).build());
    }

    public boolean execute() throws Exception {
      return WebSocketRequestTestTemplate.this.execute();
    }

    public void setDelay(long delay) {
      this.whenMessage.setDelay(delay);
    }
  }

  public class ExpectionBuilder {
    private StepsBuilder stepsBuilder;

    public ExpectionBuilder(StepsBuilder stepsBuilder) {
      this.stepsBuilder = stepsBuilder;
    }

    public ExpectionBuilder expect(String name, Expectation expectConsumer) {
      WhenMessage whenMessage = stepsBuilder.getWhenMessage();
      whenMessage.getExpectations().addExpect(name, expectConsumer);
      return this;
    }

    public ExpectionBuilder accept(String name, Expectation expectConsumer) {
      WhenMessage whenMessage = stepsBuilder.getWhenMessage();
      whenMessage.getExpectations().addAccept(name, expectConsumer);
      return this;
    }

    public ExpectionBuilder expectActions(Consumer<Actions> expectConsumer) {
      return expectActions("expectActions", expectConsumer);
    }

    public ExpectionBuilder expectActions(String name, Consumer<Actions> expectConsumer) {
      return expect(name, webSocketMessage -> {
        TextMessage textMessage = (TextMessage) webSocketMessage;
        Actions actions;
        try {
          String message = textMessage.getPayload();
          actions = objectMapper.readValue(message, Actions.class);
          revision = actions.getRev();
        } catch (IOException e) {
          throw new RuntimeException(e);
        }
        expectConsumer.accept(actions);
      });
    }

    public ExpectionBuilder expectActivated() {
      return this;
    }
    public ExpectionBuilder expectPassivation() {
      return this;
    }

    public ExpectionBuilder expectRemoveAll() {
      return expectActions("expectPassivation", actions -> {
        final Iterator<Action> i = actions.getActions().iterator();
        final Action action = i.next();
        assertEquals(Action.Type.RESET, action.getType());
        assertFalse(i.hasNext());
      });
    }

    public ExpectionBuilder expectUpdateWithoutActions() {
      return expectActions("expectUpdateWithoutActions", actions -> {
        if(actions.getActions() == null || actions.getActions().isEmpty()) {
          return;
        }
        LOGGER.error("{} do not match with expectUpdateWithoutActions", actions.toString());
        Assertions.fail("We didn't expect any actions, but got some.");
      });
    }

    public StepsBuilder nextAfterDelay(long after) {
      stepsBuilder.setDelay(after);
      return next();
    }

    public StepsBuilder next() {
      return stepsBuilder;
    }

    public boolean execute() throws Exception {
      return next().execute();
    }

    public WebSocketRequestTestTemplate finallyAssert(FinalAssert assertConsumer) {
      WebSocketRequestTestTemplate.this.assertConsumer = assertConsumer;
      return WebSocketRequestTestTemplate.this;
    }
  }


  public interface FinalAssert {
    void accept(WebSocketHandler webSocketMessage) throws Exception;
  }


  private List<WhenMessage> steps = new ArrayList<>();

  private FinalAssert assertConsumer;

  public boolean execute() throws Exception {
    final AtomicReference<WhenMessage> activeStep = new AtomicReference<>(null);
    final AtomicBoolean handlerFailed = new AtomicBoolean(false);
    final Iterator<WhenMessage> whenMessageIterator = steps.iterator();
    webSocketHandler = Mockito.spy(new WebSocketHandler() {
      @Override
      public void afterConnectionEstablished(WebSocketSession session) throws Exception {
      }

      // Here we receive messages
      @Override
      public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        websocketMessages.add("<-- " + message.getPayload());
        synchronized (activeStep) {
          WhenMessage step = activeStep.get();
          if (step == null) {
            LOGGER.error("Unexpected message {}", message.getPayload());
            handlerFailed.set(true);
            return;
          } else {
            Expectations expectations = step.getExpectations();
            expectations.accept(message);
            if (expectations.isCompleted()) {
              if (expectations.isSuccess()) {
                activeStep.compareAndSet(step, null);
              } else {
                LOGGER.error("Failed expectations {}", expectations);
              }
              activeStep.notifyAll();
            }
          }
        }
      }

      @Override
      public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
      }

      @Override
      public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
      }

      @Override
      public boolean supportsPartialMessages() {
        return false;
      }
    });
    // Here we send messages...
    try {
      while (whenMessageIterator.hasNext()) {
        synchronized (activeStep) {
          WhenMessage whenMessage = whenMessageIterator.next();
          LOGGER.info("Step \"{}\"", whenMessage.getName());
          boolean activeStepClear = activeStep.compareAndSet(null, whenMessage);
          if (!activeStepClear) {
            LOGGER.error("Active step wasn't' clear..");
          }
          whenMessage.accept(webSocketSession);
          activeStep.wait(60000);
          if (activeStep.get() != null) {
            LOGGER.error("Timed out!");
          } else {
            if (whenMessage.getDelay() != null) {
              Thread.sleep(whenMessage.getDelay());
            }
          }
        }
        assertFalse(handlerFailed.get());
        if (activeStep.get() != null) {
          LOGGER.error("{}", activeStep.get().getExpectations());
          fail("Could not complete step '" + activeStep.get().getName() + "'");
        }
      }
      if (assertConsumer != null) {
        assertConsumer.accept(webSocketHandler);
      }
      assertFalse(handlerFailed.get());
    } catch(Error e) {
      websocketMessages.forEach(System.err::println);
      throw e;
    } finally {
      if (webSocketSession != null) {
        webSocketSession.close();
      }
      if (sockJsClient != null) {
        sockJsClient.stop();
      }
    }
    return true;
  }

  public StepsBuilder steps() {
    return new StepsBuilder();
  }

  private class OutOfOrderExpectations implements Expectations {

    private List<Expectation> expectations = new ArrayList<>();

    private List<Expectation> accepted = new ArrayList<>();

    private List<String> unexpected = new ArrayList<>();

    @Override
    public void accept(WebSocketMessage message) throws Exception {
      Iterator<Expectation> i = expectations.iterator();
      List<AssertionError> assertionErrors = new ArrayList<>();
      while(i.hasNext()) {
        final Expectation expectation = i.next();
        try {
          expectation.accept(message);
          i.remove();
          return;
        } catch (AssertionError error) {
          assertionErrors.add(error);
        } catch (Throwable t) {
        }
      }
      i = accepted.iterator();
      while(i.hasNext()) {
        final Expectation expectation = i.next();
        try {
          expectation.accept(message);
          i.remove(); // Just once or multiple??
          return;
        } catch (AssertionError error) {
          assertionErrors.add(error);
        } catch (Throwable t) {
        }
      }
      if (!assertionErrors.isEmpty()) {
        LOGGER.error("Assertions failed\n{}", assertionErrors);
      }
      unexpected.add(((TextMessage)message).getPayload());
    }

    @Override
    public boolean isCompleted() {
      return expectations.isEmpty() || !unexpected.isEmpty();
    }

    @Override
    public boolean isSuccess() {
      return expectations.isEmpty() && unexpected.isEmpty();
    }

    @Override
    public void addExpect(String name, Expectation expectation) {
      if (name != null) {
        expectation = new NamedExpectation(name, expectation);
      }
      expectations.add(expectation);
    }

    @Override
    public void addAccept(String name, Expectation expectConsumer) {
      accepted.add(expectConsumer);
    }

    @Override
    public String toString() {
      return "Unexpected messages: " + unexpected + "\n" +
        "Unsatisfied expectations: " + expectations;
    }
  }


  private class OrderedExpectations implements Expectations {

    private Deque<Expectation> expectations = new ArrayDeque<>();

    private List<Expectation> failed = new ArrayList<>();

    private List<Expectation> succeeded = new ArrayList<>();

    private List<Expectation> accepted = new ArrayList<>();

    @Override
    public void accept(WebSocketMessage message) throws Exception {
      Expectation expectation = expectations.pop();
      try {
        expectation.accept(message);
        succeeded.add(expectation);
      } catch (Throwable throwable) {
        failed.add(expectation);
      }
    }

    @Override
    public boolean isSuccess() {
      return failed.isEmpty();
    }

    @Override
    public boolean isCompleted() {
      return expectations.isEmpty();
    }

    @Override
    public void addExpect(String name, Expectation expectation) {
      if (name != null) {
        expectation = new NamedExpectation(name, expectation);
      }
      expectations.add(expectation);
    }

    @Override
    public void addAccept(String name, Expectation expectConsumer) {
      accepted.add(expectConsumer);
    }
  }
}
