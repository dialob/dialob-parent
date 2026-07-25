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
package io.dialob.questionnaire.service.api.session;

import edu.umd.cs.findbugs.annotations.NonNull;
import io.dialob.api.form.Form;
import io.dialob.api.questionnaire.Questionnaire;
import io.dialob.questionnaire.service.api.FormDataMissingException;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class BaseQuestionnaireSessionBuilderTest {

  static class TestQuestionnaireSessionBuilder extends BaseQuestionnaireSessionBuilder {

    protected TestQuestionnaireSessionBuilder(@NonNull FormFinder formFinder) {
      super(formFinder);
    }

    @NonNull
    @Override
    protected QuestionnaireSession createQuestionnaireSession(boolean newSession, @NonNull Form formDocument) {
      return Mockito.mock(QuestionnaireSession.class);
    }
  }

  @Test
  void shouldBuildNewSession() {
    FormFinder formFinder = Mockito.mock(FormFinder.class);
    Form form = Mockito.mock(Form.class);
    Form.Metadata metadata = Mockito.mock(Form.Metadata.class);

    when(formFinder.findForm("form1", null)).thenReturn(form);
    when(form.getId()).thenReturn("form1");
    when(form.getRev()).thenReturn("rev1");
    when(form.getMetadata()).thenReturn(metadata);
    when(metadata.getLabel()).thenReturn("Form Label");

    TestQuestionnaireSessionBuilder builder = new TestQuestionnaireSessionBuilder(formFinder);
    builder.formId("form1");

    QuestionnaireSession session = builder.build();

    assertNotNull(session);
    assertNotNull(builder.getQuestionnaire());
    assertEquals("form1", builder.getQuestionnaire().getMetadata().getFormId());
    assertEquals("rev1", builder.getQuestionnaire().getMetadata().getFormRev());
    assertEquals("Form Label", builder.getQuestionnaire().getMetadata().getLabel());
    assertEquals(Questionnaire.Metadata.Status.NEW, builder.getQuestionnaire().getMetadata().getStatus());
  }

  @Test
  void shouldBuildSessionFromExistingQuestionnaire() {
    FormFinder formFinder = Mockito.mock(FormFinder.class);
    Form form = Mockito.mock(Form.class);
    Questionnaire questionnaire = Mockito.mock(Questionnaire.class);
    Questionnaire.Metadata metadata = Mockito.mock(Questionnaire.Metadata.class);

    when(questionnaire.getMetadata()).thenReturn(metadata);
    when(metadata.getFormId()).thenReturn("form1");
    when(metadata.getFormRev()).thenReturn("rev1");
    when(formFinder.findForm("form1", "rev1")).thenReturn(form);

    TestQuestionnaireSessionBuilder builder = new TestQuestionnaireSessionBuilder(formFinder);
    builder.questionnaire(questionnaire);

    QuestionnaireSession session = builder.build();

    assertNotNull(session);
    assertEquals(questionnaire, builder.getQuestionnaire());
  }

  @Test
  void shouldThrowExceptionIfFormNotFound() {
    FormFinder formFinder = Mockito.mock(FormFinder.class);
    when(formFinder.findForm("form1", null)).thenThrow(new io.dialob.db.spi.exceptions.DatabaseException("Not found"));

    TestQuestionnaireSessionBuilder builder = new TestQuestionnaireSessionBuilder(formFinder);
    builder.formId("form1");

    assertThrows(FormDataMissingException.class, builder::build);
  }

  @Test
  void shouldUseLatestRevision() {
    FormFinder formFinder = Mockito.mock(FormFinder.class);
    Form form = Mockito.mock(Form.class);
    Form.Metadata metadata = Mockito.mock(Form.Metadata.class);

    when(formFinder.findForm("form1", null)).thenReturn(form);
    when(form.getId()).thenReturn("form1");
    when(form.getRev()).thenReturn("rev1");
    when(form.getMetadata()).thenReturn(metadata);

    TestQuestionnaireSessionBuilder builder = new TestQuestionnaireSessionBuilder(formFinder);
    builder.formId("form1").formRev(BaseQuestionnaireSessionBuilder.LATEST_REV);

    builder.build();

    assertEquals(BaseQuestionnaireSessionBuilder.LATEST_REV, builder.getQuestionnaire().getMetadata().getFormRev());
    verify(formFinder).findForm("form1", null);
  }

  @Test
  void shouldSetAdditionalProperties() {
    FormFinder formFinder = Mockito.mock(FormFinder.class);
    Form form = Mockito.mock(Form.class);
    Form.Metadata metadata = Mockito.mock(Form.Metadata.class);

    when(formFinder.findForm("form1", null)).thenReturn(form);
    when(form.getId()).thenReturn("form1");
    when(form.getMetadata()).thenReturn(metadata);

    TestQuestionnaireSessionBuilder builder = new TestQuestionnaireSessionBuilder(formFinder);
    builder.formId("form1").additionalProperties(Map.of("prop1", "value1"));

    builder.build();

    assertEquals("value1", builder.getQuestionnaire().getMetadata().getAdditionalProperties().get("prop1"));
  }

}
