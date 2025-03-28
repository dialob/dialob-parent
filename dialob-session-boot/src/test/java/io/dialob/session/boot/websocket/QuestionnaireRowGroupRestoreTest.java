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
import io.dialob.api.form.ImmutableValidation;
import io.dialob.api.proto.Action;
import io.dialob.api.questionnaire.ImmutableAnswer;
import io.dialob.api.questionnaire.ImmutableQuestionnaire;
import io.dialob.api.questionnaire.ImmutableQuestionnaireMetadata;
import io.dialob.api.questionnaire.Questionnaire;
import io.dialob.cache.DialobCacheAutoConfiguration;
import io.dialob.function.DialobFunctionAutoConfiguration;
import io.dialob.questionnaire.service.DialobQuestionnaireServiceAutoConfiguration;
import io.dialob.questionnaire.service.sockjs.DialobQuestionnaireServiceSockJSAutoConfiguration;
import io.dialob.session.boot.Application;
import io.dialob.session.boot.ApplicationAutoConfiguration;
import io.dialob.settings.DialobSettings;
import io.dialob.spring.boot.engine.DialobSessionEngineAutoConfiguration;
import jakarta.inject.Inject;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.socket.config.annotation.EnableWebSocket;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static java.util.Arrays.asList;
import static org.assertj.core.groups.Tuple.tuple;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = RANDOM_PORT, properties = {
  "dialob.db.database-type=none",
  "spring.autoconfigure.exclude[0]=org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration",
  "dialob.session.cache.type=LOCAL",
  "dialob.security.groups-claim=true"
}, classes = {
  Application.class,
  ApplicationAutoConfiguration.class,
  QuestionnaireRowGroupRestoreTest.TestConfiguration.class,
  DialobQuestionnaireServiceSockJSAutoConfiguration.class,
  DialobFunctionAutoConfiguration.class,
  DialobQuestionnaireServiceAutoConfiguration.class,
  DialobSessionEngineAutoConfiguration.class,
  DialobCacheAutoConfiguration.class,
})
@EnableCaching
@EnableWebSocket
@EnableConfigurationProperties({DialobSettings.class})
class QuestionnaireRowGroupRestoreTest extends AbstractWebSocketTests {

  @Inject
  private ApplicationEventPublisher applcationApplicationEventPublisher;

  @Test
  void shouldAddAndRemoveRows() throws Exception {
    ImmutableForm.Builder formBuilder1 = ImmutableForm.builder()
      .id("testGetQuestionnaires-123")
      .rev("1")
      .metadata(ImmutableFormMetadata.builder().label("Kysely").build());


    Consumer<ImmutableForm.Builder> initializer = formBuilder -> {
      addQuestionnaire(formBuilder, builder -> builder.addClassName("main-questionnaire").addItems("p1"));
      addItem(formBuilder, "p1", builder -> builder.type("group").putLabel("en","Sivu").addItems("g1"));
      addItem(formBuilder, "g1", builder -> builder.type("rowgroup").putLabel("en","Ryhma").addItems("q1", "q2", "q3"));
      addItem(formBuilder, "q1", builder -> builder.type("text").putLabel("en","Kysymys 1"));
      addItem(formBuilder, "q2", builder -> builder.type("text").putLabel("en","Kysymys 2").addValidations(
        ImmutableValidation.builder().message(Map.of("en","error")).rule("answer = \"wrong answer\"").build()
      ));
      addItem(formBuilder, "q3", builder -> builder.type("text").putLabel("en","Kysymys 3").activeWhen("q2 = \"correct answer\""));
      formBuilder.metadata(ImmutableFormMetadata.builder().label("Kysely").build());
    };
    initializer.accept(formBuilder1);
    final ImmutableForm form = formBuilder1.build();
    shouldFindForm(form);

    Questionnaire questionnaire = ImmutableQuestionnaire.builder()
      .addAnswers(ImmutableAnswer.of("g1", Arrays.asList(BigInteger.TWO,BigInteger.ONE)))
      .addAnswers(ImmutableAnswer.of("g1.1.q1", "Hello"))
      .addAnswers(ImmutableAnswer.of("g1.1.q2", "correct answer"))
      .addAnswers(ImmutableAnswer.of("g1.2.q2", "wrong answer"))
      .addAnswers(ImmutableAnswer.of("g1.1.q3", "hello 3"))
      .metadata(ImmutableQuestionnaireMetadata.builder()
        .formId(form.getId())
        .formRev(form.getRev())
        .created(new Date())
        .status(Questionnaire.Metadata.Status.OPEN)
        .build()).build();

    createAndOpenSession(questionnaire)
      .expectActivated()
      .expectActions(actions -> {
        Assertions.assertThat(actions.getActions())
          .extracting("type", "item.id", "item.items", "item.value", "item.type")
          .containsOnly(
            tuple(Action.Type.RESET,   null, null, null, null),
            tuple(Action.Type.LOCALE,   null, null, null, null),
            tuple(Action.Type.ITEM, "questionnaire", List.of("p1"), null, "questionnaire"),
            tuple(Action.Type.ITEM, "g1", asList("g1.2", "g1.1"), asList(2,1), "rowgroup"),
            tuple(Action.Type.ITEM, "g1.1", asList("g1.1.q1", "g1.1.q2", "g1.1.q3"), null, "row"),
            tuple(Action.Type.ITEM, "g1.2", asList("g1.2.q1", "g1.2.q2", "g1.2.q3"), null, "row"),
            tuple(Action.Type.ITEM, "p1", List.of("g1"), null, "group"),
            tuple(Action.Type.ITEM, "g1.1.q1", null, "Hello", "text"),
            tuple(Action.Type.ITEM, "g1.1.q2", null, "correct answer", "text"),
            tuple(Action.Type.ITEM, "g1.1.q3", null, "hello 3", "text"),
            tuple(Action.Type.ITEM, "g1.2.q1", null, null, "text"),
            tuple(Action.Type.ITEM, "g1.2.q2", null, "wrong answer", "text"),
            tuple(Action.Type.ERROR,   null, null, null, null)
            );
      })
      .finallyAssert(webSocketMessage -> { }).execute();
  }

}
