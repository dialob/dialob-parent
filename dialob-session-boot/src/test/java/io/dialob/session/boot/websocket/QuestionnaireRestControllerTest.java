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
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.socket.config.annotation.EnableWebSocket;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

import static java.util.Arrays.asList;
import static org.assertj.core.groups.Tuple.tuple;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
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
class QuestionnaireRestControllerTest extends AbstractWebSocketTests {


  @Test
  void testGetQuestionnaires() throws Exception {
    ImmutableForm.Builder formBuilder = ImmutableForm.builder().id("123").rev("321")
      .metadata(ImmutableFormMetadata.builder().label("Kysely").build());
    addQuestionnaire(formBuilder, builder -> builder.addClassName("main-questionnaire"));


    Form form = formBuilder.build();
    when(formDatabase.findOne(eq(tenantId), eq(form.getId()))).thenReturn(form);
    when(formDatabase.findOne(eq(tenantId), eq(form.getId()), any())).thenReturn(form);
    when(formDatabase.exists(eq(tenantId), eq(form.getId()))).thenReturn(true);


    createAndOpenSession("123", "321")
      .expectActivated()
      .expectActions(actions -> {
        Assertions.assertThat(actions.getActions())
          .extracting("type", "item.id", "item.items")
          .containsOnly(
            tuple(Action.Type.RESET,   null, null),
            tuple(Action.Type.LOCALE,   null, null),
            tuple(Action.Type.ITEM, "questionnaire", null)
          );
      })
      .finallyAssert(webSocketMessage -> {

    }).execute();

  }


  @Test
  void testNextPage() throws Exception {

    ImmutableForm.Builder formBuilder = ImmutableForm.builder().id("testNextPage").rev("321")
      .metadata(ImmutableFormMetadata.builder().label("Kysely").build());

    addQuestionnaire(formBuilder, builder -> builder.addClassName("main-questionnaire").addItems("g1", "g2"));
    addItem(formBuilder, "g1", builder -> builder.type("group").putLabel("en","Group1"));
    addItem(formBuilder, "g2", builder -> builder.type("group").putLabel("en","Group2"));

    when(formDatabase.findOne(eq(tenantId), eq("testNextPage"), any())).thenReturn(formBuilder.build());
    when(formDatabase.exists(eq(tenantId), eq("testNextPage"))).thenReturn(true);

    // -- doLogin();
    createAndOpenSession("testNextPage")
      .expectActivated()
      .expectActions(actions -> {
        Assertions.assertThat(actions.getActions())
          .extracting("type", "item.id", "item.items")
          .containsOnly(
            tuple(Action.Type.RESET,   null, null),
            tuple(Action.Type.LOCALE,  null, null),
            tuple(Action.Type.ITEM, "questionnaire", asList("g1", "g2")),
            tuple(Action.Type.ITEM, "g1", null)
          );
      }).next()
    .nextPage()
    .expectActions(actions -> {
      assertEquals(3, actions.getActions().size());
      Iterator<Action> i = actions.getActions().iterator();
      Action action = i.next();
      assertEquals(Action.Type.REMOVE_ITEMS, action.getType());
      assertEquals(List.of("g1"), action.getIds());

      action = i.next();
      assertEquals(Action.Type.ITEM, action.getType());
      assertEquals("g2", action.getItem().getId());

      action = i.next();
      assertEquals(Action.Type.ITEM, action.getType());
      assertEquals("questionnaire", action.getItem().getId());
      assertEquals("g2", action.getItem().getActiveItem());
      assertEquals(asList("g1", "g2"), action.getItem().getItems());
      assertEquals(asList("g1", "g2"), action.getItem().getAvailableItems());
      assertFalse(i.hasNext());

    }).next()
    .nextPage()
    .expectUpdateWithoutActions().next()
    .prevPage()
    .expectActions(actions -> {
      // Previous Page  g1 -> g2
      assertEquals(3, actions.getActions().size());
      Iterator<Action> i = actions.getActions().iterator();
      Action action = i.next();
      assertEquals(Action.Type.REMOVE_ITEMS, action.getType());
      assertEquals(List.of("g2"), action.getIds());

      action = i.next();
      assertEquals(Action.Type.ITEM, action.getType());
      assertEquals("g1", action.getItem().getId());

      action = i.next();
      assertEquals(Action.Type.ITEM, action.getType());
      assertEquals("questionnaire", action.getItem().getId());
      assertEquals("g1", action.getItem().getActiveItem());
      assertEquals(asList("g1", "g2"), action.getItem().getItems());
      assertEquals(asList("g1", "g2"), action.getItem().getAvailableItems());

      assertFalse(i.hasNext());

    }).execute();
  }


  @Test
  void shouldInterpolateValueSetEntriesMultiChoice() throws Exception {
    ImmutableForm.Builder formBuilder = ImmutableForm.builder().id("shouldInterpolateValueSetEntryyx").rev("321")
      .metadata(ImmutableFormMetadata.builder().label("Kysely").build());

    addQuestionnaire(formBuilder, builder -> builder.addClassName("main-questionnaire").addItems("g1"));
    addItem(formBuilder, "g1", builder -> builder.type("group").putLabel("en","Group1").addItems("selection1","note1"));
    addItem(formBuilder, "selection1", builder -> builder.type("multichoice").valueSetId("vs1"));
    addItem(formBuilder, "note1", builder -> builder.type("note").putLabel("en","Your selection is {selection1}"));

    FormValueSet formValueSetBean = ImmutableFormValueSet.builder().id("vs1").addEntries(
      ImmutableFormValueSetEntry.builder().id("e1").putLabel("en", "Selectino 1").build(),
      ImmutableFormValueSetEntry.builder().id("e2").putLabel("en", "Selectino 2").build(),
      ImmutableFormValueSetEntry.builder().id("e3").putLabel("en", "Selectino 3").build()
    ).build();
    formBuilder.addValueSets(formValueSetBean);

    when(formDatabase.findOne(eq(tenantId), eq("shouldInterpolateValueSetEntryyx"), any())).thenReturn(formBuilder.build());
    when(formDatabase.exists(eq(tenantId), eq("shouldInterpolateValueSetEntryyx"))).thenReturn(true);

    // -- doLogin();
    createAndOpenSession("shouldInterpolateValueSetEntryyx")
      .expectActivated()
      .expectActions(actions -> {
        Assertions.assertThat(actions.getActions())
          .extracting("type", "item.id", "item.items")
          .containsOnly(
            tuple(Action.Type.RESET,   null, null),
            tuple(Action.Type.LOCALE,          null,            null),
            tuple(Action.Type.ITEM, "selection1", null),
            tuple(Action.Type.ITEM, "questionnaire", List.of("g1")),
            tuple(Action.Type.ITEM, "g1", asList("selection1","note1")),
            tuple(Action.Type.VALUE_SET, null, null)
          );
      }).next()
      .answerQuestion("selection1", List.of("e2"))
      .expectActions(actions -> {
        Assertions.assertThat(actions.getActions())
          .extracting("type", "item.id", "item.label")
          .containsOnly(
            tuple(Action.Type.ITEM, "note1", "Your selection is Selectino 2")
          );
      }).next()
      .answerQuestion("selection1", asList("e1", "e3"))
      .expectActions(actions -> {
        Assertions.assertThat(actions.getActions())
          .extracting("type", "item.id", "item.label")
          .containsOnly(
            tuple(Action.Type.ITEM, "note1", "Your selection is Selectino 1, Selectino 3")
          );
      }).next()
      .answerQuestion("selection1",(String)null)
      .expectActions(actions -> {
        Assertions.assertThat(actions.getActions())
          .extracting("type", "ids")
          .containsOnly(
            tuple(Action.Type.REMOVE_ITEMS, List.of("note1"))
          );
      })
      .execute();

    verify(formDatabase, times(2)).findOne(eq(tenantId), eq("shouldInterpolateValueSetEntryyx"), any());
    verifyNoMoreInteractions(formDatabase);
  }

  @Test
  @Tag("github-107")
  void shouldEvaluateValueSetsInCorrectOrder() throws Exception {
    ImmutableForm.Builder formBuilder = ImmutableForm.builder().id("shouldEvaluateValueSetsInCorrectOrder").rev("321")
      .metadata(ImmutableFormMetadata.builder().label("Kysely").build());

    addQuestionnaire(formBuilder, builder -> builder.addClassName("main-questionnaire").addItems("g1"));

    addItem(formBuilder, "g1", builder -> builder.type("group").putLabel("en","Group1").addItems("note1", "selection1", "selection2"));
    addItem(formBuilder, "selection1", builder -> builder.type("list").valueSetId("vs1"));
    addItem(formBuilder, "selection2", builder -> builder.type("list").valueSetId("vs2"));
    addItem(formBuilder, "note1", builder -> builder.type("note").putLabel("en","Your first selection is {selection1} and second selection is {selection2}"));

    formBuilder.addValueSets(ImmutableFormValueSet.builder().id("vs1").addEntries(
      ImmutableFormValueSetEntry.builder().id("e1").putLabel("en", "Selection 1.1").build(),
      ImmutableFormValueSetEntry.builder().id("e2").putLabel("en", "Selection 1.2").build(),
      ImmutableFormValueSetEntry.builder().id("e3").putLabel("en", "Selection 1.3").build()
    ).build());
    formBuilder.addValueSets(ImmutableFormValueSet.builder().id("vs2").addEntries(
      ImmutableFormValueSetEntry.builder().id("f1").putLabel("en", "Selection 2.1").build(),
      ImmutableFormValueSetEntry.builder().id("f2").putLabel("en", "Selection 2.2").build(),
      ImmutableFormValueSetEntry.builder().id("f3").putLabel("en", "Selection 2.3").build()
    ).build());

    when(formDatabase.findOne(eq(tenantId), eq("shouldEvaluateValueSetsInCorrectOrder"), any())).thenReturn(formBuilder.build());
    when(formDatabase.exists(eq(tenantId), eq("shouldEvaluateValueSetsInCorrectOrder"))).thenReturn(true);

    Questionnaire questionnaire = ImmutableQuestionnaire.builder()
      .metadata(ImmutableQuestionnaireMetadata.builder()
        .formId("shouldEvaluateValueSetsInCorrectOrder")
        .created(new Date())
        .language("en")
        .status(Questionnaire.Metadata.Status.OPEN)
        .build())
        .addAnswers(ImmutableAnswer.of("selection1", "e1"))
        .addAnswers(ImmutableAnswer.of("selection2", "f1"))
      .build();

    // -- doLogin();
    createAndOpenSession(questionnaire)
      .expectActivated()
      .expectActions(actions -> {
        Assertions.assertThat(actions.getActions())
          .extracting("type", "item.id", "item.label")
          .containsExactlyInAnyOrder(
            tuple(Action.Type.RESET, null, null),
            tuple(Action.Type.LOCALE, null, null),
            tuple(Action.Type.ITEM, "questionnaire", "Kysely"),
            tuple(Action.Type.ITEM, "selection2", null),
            tuple(Action.Type.ITEM, "selection1", null),
            tuple(Action.Type.ITEM, "g1", "Group1"),
            tuple(Action.Type.ITEM, "note1", "Your first selection is Selection 1.1 and second selection is Selection 2.1"),
            tuple(Action.Type.VALUE_SET, null, null),
            tuple(Action.Type.VALUE_SET, null, null)
          );
      }).next()
      .answerQuestion("selection1", "e2")
      .expectActions(actions -> {
        Assertions.assertThat(actions.getActions())
          .extracting("type", "item.id", "item.label")
          .containsOnly(
            tuple(Action.Type.ITEM, "note1", "Your first selection is Selection 1.2 and second selection is Selection 2.1")
          );
      }).next()
      .answerQuestion("selection2", "f2")
      .expectActions(actions -> {
        Assertions.assertThat(actions.getActions())
          .extracting("type", "item.id", "item.label")
          .containsOnly(
            tuple(Action.Type.ITEM, "note1", "Your first selection is Selection 1.2 and second selection is Selection 2.2")
          );
      }).next()
      .answerQuestion("selection1", (String) null)
      .expectActions(actions -> {
        Assertions.assertThat(actions.getActions())
          .extracting("type", "item.id", "item.label")
          .containsOnly(
            tuple(Action.Type.REMOVE_ITEMS, null, null)
          );
      }).next()
      .execute();

    verify(formDatabase, times(2)).findOne(eq(tenantId), eq("shouldEvaluateValueSetsInCorrectOrder"), any());
    verifyNoMoreInteractions(formDatabase);
  }


  @Test
  void shouldInterpolateValueSetEntryy() throws Exception {

    ImmutableForm.Builder formBuilder = ImmutableForm.builder().id("shouldInterpolateValueSetEntryy").rev("321")
      .metadata(ImmutableFormMetadata.builder().label("Kysely").build());

    addQuestionnaire(formBuilder, builder -> builder.addClassName("main-questionnaire").addItems("g1"));
    addItem(formBuilder, "g1", builder -> builder.type("group").putLabel("en","Group1").addItems("selection1","note1"));
    addItem(formBuilder, "selection1", builder -> builder.type("text").valueSetId("vs1"));
    addItem(formBuilder, "note1", builder -> builder.type("note").putLabel("en","Your selection is {selection1:lowercase}"));

    FormValueSet formValueSetBean = ImmutableFormValueSet.builder().id("vs1").addEntries(
      ImmutableFormValueSetEntry.builder().id("e1").putLabel("en", "Selectino 1").build(),
      ImmutableFormValueSetEntry.builder().id("e2").putLabel("en", "Selectino 2").build(),
      ImmutableFormValueSetEntry.builder().id("e3").putLabel("en", "Selectino 3").build()
    ).build();
    formBuilder.addValueSets(formValueSetBean);

    when(formDatabase.findOne(eq(tenantId), eq("shouldInterpolateValueSetEntryy"), any())).thenReturn(formBuilder.build());
    when(formDatabase.exists(eq(tenantId), eq("shouldInterpolateValueSetEntryy"))).thenReturn(true);

    // -- doLogin();
    createAndOpenSession("shouldInterpolateValueSetEntryy")
      .expectActivated()
      .expectActions(actions -> {
        Assertions.assertThat(actions.getActions())
          .extracting("type", "item.id", "item.items")
          .containsOnly(
            tuple(Action.Type.RESET,   null, null),
            tuple(Action.Type.LOCALE,          null,            null),
            tuple(Action.Type.ITEM, "selection1", null),
            tuple(Action.Type.ITEM, "questionnaire", List.of("g1")),
            tuple(Action.Type.ITEM, "g1", asList("selection1","note1")),
            tuple(Action.Type.VALUE_SET, null, null)
          );
      }).next()
      .answerQuestion("selection1","e2")
      .expectActions(actions -> {
        Assertions.assertThat(actions.getActions())
          .extracting("type", "item.id", "item.label")
          .containsOnly(
            tuple(Action.Type.ITEM, "note1", "Your selection is selectino 2")
          );
      }).next()
      .answerQuestion("selection1","e1")
      .expectActions(actions -> {
        Assertions.assertThat(actions.getActions())
          .extracting("type", "item.id", "item.label")
          .containsOnly(
            tuple(Action.Type.ITEM, "note1", "Your selection is selectino 1")
          );
      }).next()
      .answerQuestion("selection1", (String)null)
      .expectActions(actions -> {
        Assertions.assertThat(actions.getActions())
          .extracting("type", "ids")
          .containsOnly(
            tuple(Action.Type.REMOVE_ITEMS, List.of("note1"))
          );
      })
      .execute();

    verify(formDatabase, times(2)).findOne(eq(tenantId), eq("shouldInterpolateValueSetEntryy"), any());
    verifyNoMoreInteractions(formDatabase);
  }

  @Test
  void shouldHandleBigDecimalVariables() throws Exception {

    ImmutableForm.Builder formBuilder = ImmutableForm.builder().id("shouldHandleBigDecimalVariables").rev("321")
      .metadata(ImmutableFormMetadata.builder().label("bigD").build());

    addQuestionnaire(formBuilder, builder -> builder.addClassName("main-questionnaire").addItems("g1"));
    addItem(formBuilder, "g1", builder -> builder.type("group").putLabel("en","Group1").addItems("value1","note1"));
    addItem(formBuilder, "value1", builder -> builder.type("number").defaultValue("4"));
    addItem(formBuilder, "note1", builder -> builder.type("note").putLabel("en","Your value is {var1}"));

    formBuilder.addVariables(ImmutableVariable.builder()
      .name("var1")
      .expression("value1 * 0.5").build());

    when(formDatabase.findOne(eq(tenantId), eq("shouldHandleBigDecimalVariables"), any())).thenReturn(formBuilder.build());
    when(formDatabase.exists(eq(tenantId), eq("shouldHandleBigDecimalVariables"))).thenReturn(true);

    // -- doLogin();
    createAndOpenSession("shouldHandleBigDecimalVariables")
      .expectActivated()
      .expectActions(actions -> {
        Assertions.assertThat(actions.getActions())
          .extracting("type", "item.id", "item.items")
          .containsOnly(
            tuple(Action.Type.RESET,   null, null),
            tuple(Action.Type.LOCALE,   null, null),
            tuple(Action.Type.ITEM, "questionnaire", List.of("g1")),
            tuple(Action.Type.ITEM, "value1", null),
            tuple(Action.Type.ITEM, "g1", asList("value1","note1")),
            tuple(Action.Type.ITEM, "note1", null)

          );

      }).next()
      .answerQuestion("value1","10")
      .expectActions(actions -> {
        Assertions.assertThat(actions.getActions())
          .extracting("type", "item.id", "item.label")
          .containsOnly(
            tuple(Action.Type.ITEM, "note1", "Your value is 5")
          );
      }).next()
      .answerQuestion("value1","1")
      .expectActions(actions -> {
        Assertions.assertThat(actions.getActions())
          .extracting("type", "item.id", "item.label")
          .containsOnly(
            tuple(Action.Type.ITEM, "note1", "Your value is 0.5")
          );
      }).next()
      .answerQuestion("value1",(String)null)
      .expectActions(actions -> {
        Assertions.assertThat(actions.getActions())
          .extracting("type", "item.id", "item.label")
          .containsOnly(
            tuple(Action.Type.ITEM, "note1", "Your value is 2")
          );
      })
      .execute();

    verify(formDatabase, times(2)).findOne(eq(tenantId), eq("shouldHandleBigDecimalVariables"), any());
    verifyNoMoreInteractions(formDatabase);
  }

}
