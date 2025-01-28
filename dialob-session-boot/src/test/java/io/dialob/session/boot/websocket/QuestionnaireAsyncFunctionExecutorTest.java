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
package io.dialob.session.boot.websocket;

import io.dialob.api.form.ImmutableForm;
import io.dialob.api.form.ImmutableFormMetadata;
import io.dialob.api.form.ImmutableVariable;
import io.dialob.api.proto.Action;
import io.dialob.cache.DialobCacheAutoConfiguration;
import io.dialob.function.DialobFunctionAutoConfiguration;
import io.dialob.questionnaire.service.DialobQuestionnaireServiceAutoConfiguration;
import io.dialob.questionnaire.service.api.QuestionnaireDatabase;
import io.dialob.questionnaire.service.sockjs.DialobQuestionnaireServiceSockJSAutoConfiguration;
import io.dialob.rule.parser.function.FunctionRegistry;
import io.dialob.session.boot.Application;
import io.dialob.session.boot.ApplicationAutoConfiguration;
import io.dialob.settings.DialobSettings;
import io.dialob.spring.boot.engine.DialobSessionEngineAutoConfiguration;
import jakarta.inject.Inject;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.AopTestUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.socket.config.annotation.EnableWebSocket;

import java.util.List;

import static org.assertj.core.groups.Tuple.tuple;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;


@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = RANDOM_PORT, properties = {
  "dialob.db.database-type=none",
  "spring.autoconfigure.exclude[0]=org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration",
  "dialob.session.cache.type=LOCAL"
}, classes = {
  Application.class,
  ApplicationAutoConfiguration.class,
  QuestionnaireAsyncFunctionExecutorTest.TestConfiguration.class,
  DialobQuestionnaireServiceSockJSAutoConfiguration.class,
  DialobFunctionAutoConfiguration.class,
  DialobQuestionnaireServiceAutoConfiguration.class,
  DialobSessionEngineAutoConfiguration.class,
  DialobCacheAutoConfiguration.class,
})
@EnableCaching
@EnableWebSocket
@EnableConfigurationProperties({DialobSettings.class})
public class QuestionnaireAsyncFunctionExecutorTest extends AbstractWebSocketTests {

  @Inject
  private FunctionRegistry functionRegistry;

  public static String testFunction(String input) {
    try {
      Thread.sleep(200L);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
    return "got it " + input;
  }

  @Test
  void shouldEvaluateFunctionAsynchronously() throws Exception {
    RestTemplate restTemplate = Mockito.mock(RestTemplate.class);

    functionRegistry.configureFunction("testFunction", QuestionnaireAsyncFunctionExecutorTest.class, true);

    ImmutableForm.Builder formBuilder = ImmutableForm.builder()
      .id("shouldEvaluateFunctionAsynchronously")
      .rev("321")
      .metadata(ImmutableFormMetadata.builder().label("Kysely").build());

    addQuestionnaire(formBuilder, builder -> builder.addClassName("main-questionnaire").addItems("g1"));
    addItem(formBuilder, "g1", builder -> builder.type("group").putLabel("en","Group1").addItems("question1","note1","note2"));
    addItem(formBuilder, "note1", builder -> builder.type("note").putLabel("en","{testResult}"));
    addItem(formBuilder, "note2", builder -> builder.type("note").putLabel("en","Things got Weird").activeWhen("testFunction(question1) = \"got it Weird\""));
    addItem(formBuilder, "question1", builder -> builder.type("text").putLabel("en","Question 1"));

    formBuilder.addVariables(ImmutableVariable.builder()
      .name("testResult")
      .expression("testFunction(question1)").build());

    shouldFindForm(formBuilder.build());

    // @formatter: off
    createAndOpenSession("shouldEvaluateFunctionAsynchronously")
//      .expectActivated()
      .expectActions(actions -> {
        Assertions.assertThat(actions.getActions())
          .extracting("type","item.id","item.label")
          .containsOnly(
            tuple(Action.Type.RESET,  null,             null),
            tuple(Action.Type.LOCALE, null,             null),
            tuple(Action.Type.ITEM,   "questionnaire", "Kysely"),
            tuple(Action.Type.ITEM,   "g1",            "Group1"),
            tuple(Action.Type.ITEM,   "question1",     "Question 1")
          );
      })
    .nextAfterDelay(500l)
    .answerQuestion("question1", "Weird")
    .expectUpdateWithoutActions()
    .expectActions(actions -> {
      Assertions.assertThat(actions.getActions())
        .extracting("type","item.id","item.label")
        .containsOnly(
          tuple(Action.Type.ITEM, "note2", "Things got Weird"),
          tuple(Action.Type.ITEM, "note1", "got it Weird")
        );
      }).next()
    .answerQuestion("question1", "not more Weird")
    .expectUpdateWithoutActions()
    .expectActions("expect 1", actions -> {
      Assertions.assertThat(actions.getActions())
        .extracting("type","item.id",        "item.label", "ids")
        .containsOnly(
          tuple(Action.Type.REMOVE_ITEMS,        null,                    null, List.of("note2")),
          tuple(Action.Type.ITEM,     "note1", "got it not more Weird",         null)
        );
    })
    .finallyAssert(webSocketMessage -> {
        verify(formDatabase, times(2)).findOne(eq(tenantId), eq("shouldEvaluateFunctionAsynchronously"), any());
        final QuestionnaireDatabase targetObject = AopTestUtils.getTargetObject(questionnaireDatabase);
        verify(targetObject, times(1)).save(eq(tenantId), any());
        verify(targetObject, times(1)).findOne(eq(tenantId), any());
        verifyNoMoreInteractions(formDatabase, targetObject, restTemplate);
    }).execute();
    // @formatter: on

  }
}
