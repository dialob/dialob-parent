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

import io.dialob.api.form.*;
import io.dialob.api.proto.Action;
import io.dialob.integration.api.event.ImmutableFormUpdatedEvent;
import io.dialob.security.tenant.ImmutableTenant;
import io.dialob.session.boot.Application;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;
import java.util.function.Consumer;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Java6Assertions.tuple;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

//
// NOTE! This tests fails randomly, due race condition between actions sent over websocket.
//
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {Application.class, QuestionnaireOnlineUpdateTest.TestConfiguration.class})
@SpringBootTest(webEnvironment = RANDOM_PORT, properties = {"dialob.db.database-type=none"})
@EnableCaching
public class QuestionnaireOnlineUpdateTest extends AbstractWebSocketTests {

  @Inject
  private ApplicationEventPublisher applicationEventPublisher;

  @Test
  public void updateFormOnline() throws Exception {

    ImmutableForm.Builder updateFormOnlineBuilder = ImmutableForm.builder();
    Consumer<ImmutableForm.Builder> initializer = formBuilder -> {
      FormItem formItemBean = addQuestionnaire(formBuilder, builder -> builder.addClassName("main-questionnaire").addItems("g1") );
      addItem(formBuilder, "g1", builder -> builder.type("group").putLabel("en", "Ryhma").addItems("q1"));
      addItem(formBuilder, "q1", builder -> builder.type("text").putLabel("en", "Kysymys"));
      formBuilder.metadata(ImmutableFormMetadata.builder().label("Kysely").build());
    };
    initializer.accept(updateFormOnlineBuilder);
    updateFormOnlineBuilder
      .id("updateFormOnline")
      .rev("1")
      .build();

    final ImmutableForm form1 = updateFormOnlineBuilder.build();
    shouldFindForm(form1);

    createAndOpenSession("updateFormOnline")
      .expectActivated()
      .expectActions(actions -> {
        assertThat(actions.getActions())
          .extracting("type",                     "item.id", "item.type", "item.label", "item.items").containsOnly(
          tuple(Action.Type.RESET,              null,            null,             null,          null),
          tuple(Action.Type.LOCALE,             null,            null,             null,          null),
          tuple(Action.Type.ITEM,    "questionnaire", "questionnaire",         "Kysely",  asList("g1")),
          tuple(Action.Type.ITEM,               "q1",          "text",        "Kysymys",          null),
          tuple(Action.Type.ITEM,               "g1",         "group",          "Ryhma",  asList("q1"))
        );
      })
      .nextAfterDelay(500l)
      .answerQuestion("q1", "vastaus")
      .expectActions(actions -> {
        assertThat(actions.getActions()).isNullOrEmpty();

        // Update Form and notify about it
        ImmutableForm.Builder updateFormOnlineBuilder2 = ImmutableForm.builder()
          .id("updateFormOnline")
          .rev("2");
        initializer.accept(updateFormOnlineBuilder2);

        FormItem formItemBean = form1.getData().get("g1");
        formItemBean = ImmutableFormItem.builder().from(formItemBean).items(asList("q1", "q2")).build();
        updateFormOnlineBuilder2.putData(formItemBean.getId(), formItemBean);
        addItem(updateFormOnlineBuilder2, "q2", builder -> builder.type("text").putLabel("en", "Kysymys 2"));
        Form formDocument2 = shouldFindForm(updateFormOnlineBuilder2.build());
        applicationEventPublisher.publishEvent(ImmutableFormUpdatedEvent.builder()
          .source("sourc3")
          .formId("updateFormOnline")
          .tenant(ImmutableTenant.of("testTenant", Optional.empty()))
          .revision("2")
          .build());

      })
      .expectPassivation()
      .nextAfterDelay(500)
      .answerQuestion("q1", "vastaus")
      .expectActions(actions -> {
        assertThat(actions.getActions())  // We'll get a full form because FormUpdatedEvent trigger session eviction from cache
          .extracting("type",                     "item.id", "item.type", "item.label", "item.items", "item.value").containsOnly(
                tuple(Action.Type.RESET,           null,            null,             null,             null,             null),
                tuple(Action.Type.LOCALE,          null,            null,             null,             null,             null),
                tuple(Action.Type.ITEM, "questionnaire", "questionnaire",         "Kysely",     asList("g1"),             null),
                tuple(Action.Type.ITEM,            "q1",          "text",        "Kysymys",             null,        "vastaus"),
                tuple(Action.Type.ITEM,            "q2",          "text",      "Kysymys 2",             null,             null),
                tuple(Action.Type.ITEM,            "g1",         "group",          "Ryhma",asList("q1", "q2"),            null)
        );
      })
      .nextAfterDelay(500)
      .answerQuestion("q1", "vastaus 2")
      .expectUpdateWithoutActions()
      // Do not trigger model updates. Other's will receive this same 'answerQuestion' action. (Propably not the best way to respond..)
      .nextAfterDelay(500).execute();
  }

}
