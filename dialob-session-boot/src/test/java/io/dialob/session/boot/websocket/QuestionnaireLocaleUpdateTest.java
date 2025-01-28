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

import io.dialob.api.form.*;
import io.dialob.api.proto.Action;
import io.dialob.api.proto.ValueSetEntry;
import io.dialob.cache.DialobCacheAutoConfiguration;
import io.dialob.function.DialobFunctionAutoConfiguration;
import io.dialob.questionnaire.service.DialobQuestionnaireServiceAutoConfiguration;
import io.dialob.questionnaire.service.sockjs.DialobQuestionnaireServiceSockJSAutoConfiguration;
import io.dialob.session.boot.Application;
import io.dialob.session.boot.ApplicationAutoConfiguration;
import io.dialob.settings.DialobSettings;
import io.dialob.spring.boot.engine.DialobSessionEngineAutoConfiguration;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.socket.config.annotation.EnableWebSocket;

import java.util.Arrays;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Java6Assertions.tuple;
import static org.mockito.Mockito.when;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

//
// NOTE! This tests fails randomly, due race condition between actions sent over websocket.
//
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = RANDOM_PORT, properties = {
  "dialob.db.database-type=none",
  "spring.autoconfigure.exclude[0]=org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration",
  "dialob.session.cache.type=LOCAL"
}, classes = {
  Application.class,
  ApplicationAutoConfiguration.class,
  QuestionnaireLocaleUpdateTest.TestConfiguration.class,
  DialobQuestionnaireServiceSockJSAutoConfiguration.class,
  DialobFunctionAutoConfiguration.class,
  DialobQuestionnaireServiceAutoConfiguration.class,
  DialobSessionEngineAutoConfiguration.class,
  DialobCacheAutoConfiguration.class,
})
@EnableCaching
@EnableWebSocket
@EnableConfigurationProperties({DialobSettings.class})
class QuestionnaireLocaleUpdateTest extends AbstractWebSocketTests {


  @Inject
  private ApplicationEventPublisher applicationEventPublisher;

  @Test
  void updateFormLocaleOnline() throws Exception {
    when(currentTenant.getId()).thenReturn(tenantId);

    ImmutableForm.Builder updateFormOnlineBuilder = ImmutableForm.builder();
    Consumer<ImmutableForm.Builder> initializer = formBuilder -> {
      FormItem formItemBean = addQuestionnaire(formBuilder, builder -> builder.addClassName("main-questionnaire").addItems("g1") );
      addItem(formBuilder, "g1", builder -> builder.type("group").putLabel("en", "Group").putLabel("fi","Ryhmä").addItems("q1","g2"));
      addItem(formBuilder, "g2", builder -> builder.type("group").putLabel("en", "Group 2").putLabel("fi","Ryhmä 2").addItems("q2"));
      addItem(formBuilder, "q1", builder -> builder.type("text").putLabel("en", "Question").putLabel("fi","Kysymys").putDescription("en","Hard one").putDescription("fi","Vaikea"));
      addItem(formBuilder, "q2", builder -> builder.type("text").putLabel("en", "Question 2").putLabel("fi","Kysymys 2").activeWhen("language = 'fi'"));
      formBuilder.addValueSets(ImmutableFormValueSet.builder()
        .id("vs1")
        .addEntries(
          ImmutableFormValueSetEntry.builder()
            .id("choice-1")
            .putLabel("en","Choice 1")
            .putLabel("fi","Valinta 1")
            .build(),
          ImmutableFormValueSetEntry.builder()
            .id("choice-2")
            .putLabel("en","Choice 2")
            .putLabel("fi","Valinta 2")
            .build()
        ).build());
      formBuilder.metadata(ImmutableFormMetadata.builder().languages(Arrays.asList("en","fi")).label("Kysely").build());
    };
    initializer.accept(updateFormOnlineBuilder);
    updateFormOnlineBuilder
      .id("updateFormLocaleOnline")
      .rev("1")
      .build();

    final ImmutableForm form1 = updateFormOnlineBuilder.build();
    shouldFindForm(form1);

    createAndOpenSession("updateFormLocaleOnline")
      .expectActivated()
      .expectActions(actions -> {
        assertThat(actions.getActions())
          .extracting("type",                     "item.id", "item.type", "item.label", "item.description","value").containsExactlyInAnyOrder(
          tuple(Action.Type.RESET,           null,            null,             null,     null, null),
          tuple(Action.Type.LOCALE,          null,            null,             null,     null, "en"),
          tuple(Action.Type.ITEM, "questionnaire", "questionnaire",         "Kysely",     null, null),
          tuple(Action.Type.ITEM,            "g1",         "group",          "Group",     null, null),
          tuple(Action.Type.ITEM,            "g2",         "group",          "Group 2",     null, null),
          tuple(Action.Type.ITEM,            "q1",          "text",       "Question",     "Hard one", null),
          tuple(Action.Type.VALUE_SET,       null,            null,             null,             null, null)
        );
        assertThat(actions.getActions()).extracting(action -> action.getValueSet() == null ? null : action.getValueSet().getEntries().stream().map(ValueSetEntry::getValue).collect(Collectors.toList()))
          .containsOnly(
            null,
            null,
            null,
            null,
            null,
            Arrays.asList("Choice 1", "Choice 2")
          );
      })
      .nextAfterDelay(500L)
      .setLocale("fi")
      .expectActions(actions -> {
        assertThat(actions.getActions())  // We'll get a full form because FormUpdatedEvent trigger session eviction from cache
          .extracting("type",                     "item.id", "item.type", "item.label", "item.description","value").containsExactlyInAnyOrder(
          tuple(Action.Type.LOCALE,          null,            null,             null,     null, "fi"),
          tuple(Action.Type.ITEM,            "q1",          "text",       "Kysymys",     "Vaikea", null),
          tuple(Action.Type.ITEM,            "q2",          "text",       "Kysymys 2",    null, null),
          tuple(Action.Type.ITEM,            "g1",         "group",          "Ryhmä",     null, null),
          tuple(Action.Type.ITEM,            "g2",         "group",          "Ryhmä 2",     null, null),
          tuple(Action.Type.VALUE_SET,       null,            null,             null,             null, null)
        );
        assertThat(actions.getActions()).extracting(action -> action.getValueSet() == null ? null : action.getValueSet().getEntries().stream().map(ValueSetEntry::getValue).collect(Collectors.toList()))
          .containsOnly(
            null,
            null,
            null,
            null,
            null,
            Arrays.asList("Valinta 1", "Valinta 2")
          );
      })
      .nextAfterDelay(500)
      .setLocale("en")
      .expectActions(actions -> {
        assertThat(actions.getActions())  // We'll get a full form because FormUpdatedEvent trigger session eviction from cache
          .extracting("type",                     "item.id", "item.type", "item.label", "item.description","value").containsExactlyInAnyOrder(
          tuple(Action.Type.REMOVE_ITEMS,    null,            null,             null,     null, null),
          tuple(Action.Type.LOCALE,          null,            null,             null,     null, "en"),
          tuple(Action.Type.ITEM,            "q1",          "text",       "Question",     "Hard one", null),
          tuple(Action.Type.ITEM,            "g1",         "group",          "Group",     null, null),
          tuple(Action.Type.ITEM,            "g2",         "group",          "Group 2",     null, null),
          tuple(Action.Type.VALUE_SET,       null,            null,             null,     null, null)
        );
        assertThat(actions.getActions()).extracting(action -> action.getValueSet() == null ? null : action.getValueSet().getEntries().stream().map(ValueSetEntry::getValue).collect(Collectors.toList()))
          .containsOnly(
            null,
            null,
            null,
            null,
            null,
            Arrays.asList("Choice 1", "Choice 2")
          );
      })
      .nextAfterDelay(500)
      .answerQuestion("q1", "vastaus 2")
      .expectUpdateWithoutActions()
      // Do not trigger model updates. Other's will receive this same 'answerQuestion' action. (Propably not the best way to respond..)
      .nextAfterDelay(500).execute();
  }

}
