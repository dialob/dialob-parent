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

import com.fasterxml.jackson.databind.ObjectMapper;
import io.dialob.api.form.Form;
import io.dialob.api.form.FormItem;
import io.dialob.api.form.ImmutableForm;
import io.dialob.api.form.ImmutableFormItem;
import io.dialob.api.questionnaire.ContextValue;
import io.dialob.api.questionnaire.ImmutableQuestionnaire;
import io.dialob.api.questionnaire.ImmutableQuestionnaireMetadata;
import io.dialob.api.questionnaire.Questionnaire;
import io.dialob.form.service.api.FormDatabase;
import io.dialob.questionnaire.service.api.QuestionnaireDatabase;
import io.dialob.questionnaire.service.api.session.FormFinder;
import io.dialob.questionnaire.service.sockjs.WebSocketRequestTestTemplate;
import io.dialob.security.tenant.CurrentTenant;
import io.dialob.security.tenant.Tenant;
import io.dialob.session.boot.ProvideTestRedis;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.SerializationUtils;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.AopTestUtils;
import org.springframework.web.socket.WebSocketSession;

import java.util.*;
import java.util.function.Consumer;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

@Slf4j
abstract class AbstractWebSocketTests implements ProvideTestRedis {

  String tenantId = "00000000-0000-0000-0000-000000000000";

  @MockitoBean
  CurrentTenant currentTenant;

  @MockitoBean
  QuestionnaireDatabase questionnaireDatabase;
  @MockitoBean
  FormDatabase formDatabase;

  @BeforeEach
  void setupCurrentTenant() {
    when(currentTenant.getId()).thenReturn(tenantId);
    when(currentTenant.get()).thenReturn(Tenant.of(tenantId));
    defineInMemoryPersistence(questionnaireDatabase);
  }

  public static class TestConfiguration {

    @Bean
    @Primary
    public FormFinder formFinder(FormDatabase formDatabase, CurrentTenant currentTenant) {
      return (id, rev) -> formDatabase.findOne(currentTenant.getId(), id, rev);
    }
  }

  public static class TestThreadPoolTaskScheduler extends ThreadPoolTaskScheduler {
    private final List<Runnable> delayedTasks = new ArrayList<>();

    @Override
    public void execute(Runnable task) {
      delayedTasks.add(task);
    }

    public boolean flush() {
      if (delayedTasks.isEmpty()) {
        return false;
      }
      delayedTasks.forEach(super::execute);
      delayedTasks.clear();
      return true;
    }

  }

  protected static void defineInMemoryPersistence(QuestionnaireDatabase questionnaireDatabase) {
    HashMap<String, Questionnaire> database = new HashMap<>();

    questionnaireDatabase = AopTestUtils.getTargetObject(questionnaireDatabase);

    doAnswer(invocation -> database.get(invocation.getArgument(1))).when(questionnaireDatabase).findOne(anyString(), anyString());
    doAnswer(invocation -> {
      ImmutableQuestionnaire document = invocation.getArgument(1);
      if (document.getId() == null) {
        document = document.withId(UUID.randomUUID().toString());
        document = document.withRev("1-" + document.getId());
      } else {
        String rev = document.getRev();
        String revNumber = rev.substring(0, rev.indexOf('-'));
        document = document.withRev((Integer.parseInt(revNumber) + 1) + "-" + document.getId());
      }
      database.put(document.getId(), SerializationUtils.clone(document));
      return document;
    }).when(questionnaireDatabase).save(anyString(), any());
  }


  @Inject
  protected ApplicationContext ctx;

  @Inject
  protected ObjectMapper objectMapper;

  @LocalServerPort
  private Integer port;

  public String uri() {
    return "ws://localhost:" + port + "/session/socket";
  }




  protected Form shouldFindForm(Form form) {
    when(formDatabase.findOne(eq(tenantId), eq(form.getId()))).thenReturn(form);
    when(formDatabase.findOne(eq(tenantId), eq(form.getId()), any())).thenReturn(form);
    when(formDatabase.exists(eq(tenantId), eq(form.getId()))).thenReturn(true);
    return form;
  }

  protected Questionnaire createQuestionnaireDocument(String questionnaireId, String questionnaireRev, Form formDocument) {
    return createQuestionnaireDocument(questionnaireId, questionnaireRev, formDocument.getId(), formDocument.getRev());
  }

  protected Questionnaire createQuestionnaireDocument(String questionnaireId, String questionnaireRev, Form formDocument, List<ContextValue> context) {
    return createQuestionnaireDocument(questionnaireId, questionnaireRev, formDocument.getId(), formDocument.getRev(), context);
  }

  protected Questionnaire createQuestionnaireDocument(String questionnaireId, String questionnaireRev, String formId, String formRev) {
    Questionnaire questionnaire = ImmutableQuestionnaire.builder()
      .metadata(ImmutableQuestionnaireMetadata.builder()
        .formId(formId)
        .formRev(formRev)
        .build())
      .id(questionnaireId)
      .rev(questionnaireRev)
      .build();
    when(questionnaireDatabase.findOne(tenantId, questionnaireId)).thenReturn(questionnaire);
    return questionnaire;
  }

  protected Questionnaire createQuestionnaireDocument(String questionnaireId, String questionnaireRev, String formId, String formRev, List<ContextValue> context) {
    Questionnaire questionnaire = ImmutableQuestionnaire.builder()
      .metadata(ImmutableQuestionnaireMetadata.builder()
        .formId(formId)
        .formRev(formRev)
        .build())
      .id(questionnaireId)
      .rev(questionnaireRev)
      .context(context)
      .build();
    when(questionnaireDatabase.findOne(tenantId, questionnaireId)).thenReturn(questionnaire);
    return questionnaire;
  }

  protected FormItem addQuestionnaire(ImmutableForm.Builder formBuilder, Consumer<ImmutableFormItem.Builder> builderConsumer) {
    return addItem(formBuilder, "questionnaire", builder -> {
      builder.type("questionnaire");
      builderConsumer.accept(builder);
    });
  }

  protected FormItem addItem(ImmutableForm.Builder formBuilder, String itemId, Consumer<ImmutableFormItem.Builder> builderConsumer) {
    ImmutableFormItem.Builder builder = ImmutableFormItem.builder().id(itemId);
    builderConsumer.accept(builder);
    FormItem formItemBean = builder.build();
    formBuilder.putData(formItemBean.getId(), formItemBean);
    return formItemBean;
  }

  protected WebSocketRequestTestTemplate.ExpectionBuilder openSession(Questionnaire questionnaire) {
    return openSession(questionnaireDatabase.save(currentTenant.getId(), questionnaire).getId());
  }

  protected WebSocketRequestTestTemplate.ExpectionBuilder openSession(String questionnaireId) {
    String tenantId = "-";
    WebSocketRequestTestTemplate webSocketRequestTestTemplate = new WebSocketRequestTestTemplate(objectMapper, uri() + "?tenantId=" + tenantId + "&sessionId=" + questionnaireId, null);
    return webSocketRequestTestTemplate
      .steps().when(new WebSocketRequestTestTemplate.WhenMessage("openSession(" + questionnaireId + ")") {
        @Override
        public void accept(WebSocketSession webSocketSession) throws Exception {
          webSocketRequestTestTemplate.openSession();
        }
      });
  }

  protected WebSocketRequestTestTemplate.ExpectionBuilder createAndOpenSession(String formId, String rev) {
    Questionnaire questionnaire = ImmutableQuestionnaire.builder()
      .metadata(ImmutableQuestionnaireMetadata.builder()
        .formId(formId)
        .formRev(rev)
        .created(new Date())
        .language("en")
        .status(Questionnaire.Metadata.Status.OPEN)
        .build()).build();
    questionnaire = questionnaireDatabase.save(currentTenant.getId(), questionnaire);
    return openSession(questionnaire.getId());
  }

  protected WebSocketRequestTestTemplate.ExpectionBuilder createAndOpenSession(Questionnaire questionnaire) {
    questionnaire = questionnaireDatabase.save(currentTenant.getId(), questionnaire);
    return openSession(questionnaire.getId());
  }


  protected WebSocketRequestTestTemplate.ExpectionBuilder createAndOpenSession(String formId) {
    return createAndOpenSession(formId, "LATEST");
  }

  protected WebSocketRequestTestTemplate.ExpectionBuilder createAndOpenSession(Form formDocument) {
    return createAndOpenSession(formDocument.getId(), Objects.toString(formDocument.getRev(), "LATEST"));
  }
}
