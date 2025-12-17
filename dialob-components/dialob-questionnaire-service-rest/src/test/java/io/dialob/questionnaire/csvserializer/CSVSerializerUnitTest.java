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
package io.dialob.questionnaire.csvserializer;

import io.dialob.api.form.*;
import io.dialob.api.questionnaire.Answer;
import io.dialob.api.questionnaire.ContextValue;
import io.dialob.api.questionnaire.Questionnaire;
import io.dialob.common.Constants;
import io.dialob.questionnaire.service.api.QuestionnaireDatabase;
import io.dialob.security.tenant.CurrentTenant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.Arrays;
import java.util.Locale;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CSVSerializerUnitTest {

  @Mock
  private QuestionnaireDatabase questionnaireDatabase;

  @Mock
  private CurrentTenant currentTenant;

  private CSVSerializer csvSerializer;

  @BeforeEach
  void setUp() {
    csvSerializer = new CSVSerializer(questionnaireDatabase, currentTenant);
    lenient().when(currentTenant.getId()).thenReturn("test-tenant");
  }

  @Test
  void testSerializeHeaderWithSimpleTextItems() {
    Form form = createFormWithItems(
      createTextItem("text1", "Text Question 1"),
      createTextItem("text2", "Text Question 2")
    );

    String[] headers = csvSerializer.serializeHeader(form, "en");

    assertEquals(4, headers.length);
    assertEquals("Text Question 1", headers[0]);
    assertEquals("text1", headers[1]);
    assertEquals("Text Question 2", headers[2]);
    assertEquals("text2", headers[3]);
  }

  @Test
  void testSerializeHeaderWithDuplicateLabels() {
    Form form = createFormWithItems(
      createTextItem("text1", "Same Label"),
      createTextItem("text2", "Same Label"),
      createTextItem("text3", "Same Label")
    );

    String[] headers = csvSerializer.serializeHeader(form, "en");

    assertEquals(6, headers.length);
    assertEquals("Same Label", headers[0]);
    assertEquals("text1", headers[1]);
    assertEquals("1. Same Label", headers[2]);
    assertEquals("text2", headers[3]);
    assertEquals("2. Same Label", headers[4]);
    assertEquals("text3", headers[5]);
  }

  @Test
  void testSerializeHeaderWithMultichoice() {
    FormValueSet valueSet = new FormValueSet.Builder()
      .id("vs1")
      .addEntries(
        new FormValueSetEntry.Builder()
          .id("choice1")
          .label(Map.of("en", "Choice 1"))
          .build(),
        new FormValueSetEntry.Builder()
          .id("choice2")
          .label(Map.of("en", "Choice 2"))
          .build()
      )
      .build();

    FormItem multichoiceItem = new FormItem.Builder()
      .id("mc1")
      .type(Constants.MULTICHOICE)
      .valueSetId("vs1")
      .label(Map.of("en", "Select Multiple"))
      .build();

    Form form = new Form.Builder()
      .metadata(new Form.Metadata.Builder().label("Test Form").build())
      .putData("mc1", multichoiceItem)
      .addValueSets(valueSet)
      .build();

    String[] headers = csvSerializer.serializeHeader(form, "en");

    // For multichoice: 2 columns per choice (value + key) + 2 for the multichoice itself
    assertEquals(6, headers.length);
    assertEquals("Choice 1", headers[0]);
    assertEquals("choice1", headers[1]);
    assertEquals("Choice 2", headers[2]);
    assertEquals("choice2", headers[3]);
    assertEquals("Select Multiple", headers[4]);
    assertEquals("mc1", headers[5]);
  }

  @Test
  void testSerializeHeaderWithIgnoredTypes() {
    Form form = createFormWithItems(
      createItemWithType("page1", Constants.PAGE, "Page Title"),
      createItemWithType("group1", Constants.GROUP, "Group Title"),
      createItemWithType("note1", Constants.NOTE, "Note Text"),
      createTextItem("text1", "Text Question")
    );

    String[] headers = csvSerializer.serializeHeader(form, "en");

    // Only text1 should be in headers
    assertEquals(2, headers.length);
    assertEquals("Text Question", headers[0]);
    assertEquals("text1", headers[1]);
  }

  @Test
  void testSerializeHeaderWithExportFalseProp() {
    FormItem excludedItem = new FormItem.Builder()
      .id("excluded1")
      .type(Constants.TEXT)
      .label(Map.of("en", "Excluded Question"))
      .props(Map.of("export", "false"))
      .build();

    Form form = createFormWithItems(
      createTextItem("text1", "Included Question"),
      excludedItem
    );

    String[] headers = csvSerializer.serializeHeader(form, "en");

    // Only text1 should be in headers
    assertEquals(2, headers.length);
    assertEquals("Included Question", headers[0]);
    assertEquals("text1", headers[1]);
  }

  @Test
  void testSerializeHeaderWithContextVariables() {
    Form form = new Form.Builder()
      .metadata(new Form.Metadata.Builder().label("Test Form").build())
      .putData("text1", createTextItem("text1", "Question 1"))
      .addVariables(
        new Variable.Builder()
          .name("var1")
          .context(true)
          .build(),
        new Variable.Builder()
          .name("var2")
          .context(false)
          .build(),
        new Variable.Builder()
          .name("var3")
          .context(true)
          .build()
      )
      .build();

    String[] headers = csvSerializer.serializeHeader(form, "en");

    assertEquals(4, headers.length);
    assertEquals("Question 1", headers[0]);
    assertEquals("text1", headers[1]);
    assertEquals("var1", headers[2]);
    assertEquals("var3", headers[3]);
  }

  @Test
  void testSerializeQuestionnairesWithBooleanAnswers() throws IOException {
    Form form = createFormWithItems(
      createBooleanItem("bool1", "Boolean Question EN")
    );

    Questionnaire questionnaire = createQuestionnaire("q1", "en",
      createAnswer("bool1", "BOOLEAN", true)
    );

    when(questionnaireDatabase.findOne("test-tenant", "q1")).thenReturn(questionnaire);

    String result = csvSerializer.serializeQuestionnaires(new String[]{"q1"}, form, Locale.ENGLISH);

    assertTrue(result.contains("Boolean Question EN"));
    assertTrue(result.contains("Yes"));
    assertTrue(result.contains("true"));
  }

  @Test
  void testSerializeQuestionnairesWithBooleanAnswersFinnish() throws IOException {
    Form form = createFormWithItems(
      createBooleanItem("bool1", "Boolean Question FI")
    );

    Questionnaire questionnaire = createQuestionnaire("q1", "fi",
      createAnswer("bool1", "BOOLEAN", true)
    );

    when(questionnaireDatabase.findOne("test-tenant", "q1")).thenReturn(questionnaire);

    String result = csvSerializer.serializeQuestionnaires(new String[]{"q1"}, form, new Locale("fi"));

    assertTrue(result.contains("Kyllä"));
    assertTrue(result.contains("true"));
  }

  @Test
  void testSerializeQuestionnairesWithBooleanAnswersSwedish() throws IOException {
    Form form = createFormWithItems(
      createBooleanItem("bool1", "Boolean Question SV")
    );

    Questionnaire questionnaire = createQuestionnaire("q1", "sv",
      createAnswer("bool1", "BOOLEAN", false)
    );

    when(questionnaireDatabase.findOne("test-tenant", "q1")).thenReturn(questionnaire);

    String result = csvSerializer.serializeQuestionnaires(new String[]{"q1"}, form, new Locale("sv"));

    assertTrue(result.contains("Nej"));
    assertTrue(result.contains("false"));
  }

  @Test
  void testSerializeQuestionnairesWithBooleanAnswersUnsupportedLanguageFallsBackToEnglish() throws IOException {
    Form form = createFormWithItems(
      createBooleanItem("bool1", "Boolean Question")
    );

    Questionnaire questionnaire = createQuestionnaire("q1", "zh",
      createAnswer("bool1", "BOOLEAN", true)
    );

    when(questionnaireDatabase.findOne("test-tenant", "q1")).thenReturn(questionnaire);

    String result = csvSerializer.serializeQuestionnaires(new String[]{"q1"}, form, Locale.CHINESE);

    assertTrue(result.contains("Yes")); // Falls back to English
    assertTrue(result.contains("true"));
  }

  @Test
  void testSerializeQuestionnairesWithTextAnswers() throws IOException {
    Form form = createFormWithItems(
      createTextItem("text1", "Text Question")
    );

    Questionnaire questionnaire = createQuestionnaire("q1", "en",
      createAnswer("text1", "TEXT", "My answer")
    );

    when(questionnaireDatabase.findOne("test-tenant", "q1")).thenReturn(questionnaire);

    String result = csvSerializer.serializeQuestionnaires(new String[]{"q1"}, form, Locale.ENGLISH);

    assertTrue(result.contains("Text Question"));
    assertTrue(result.contains("My answer"));
  }

  @Test
  void testSerializeQuestionnairesWithChoiceAnswers() throws IOException {
    FormValueSet valueSet = new FormValueSet.Builder()
      .id("vs1")
      .addEntries(
        new FormValueSetEntry.Builder()
          .id("choice1")
          .label(Map.of("en", "Choice 1 Label"))
          .build(),
        new FormValueSetEntry.Builder()
          .id("choice2")
          .label(Map.of("en", "Choice 2 Label"))
          .build()
      )
      .build();

    FormItem choiceItem = new FormItem.Builder()
      .id("choice1")
      .type(Constants.LIST)
      .valueSetId("vs1")
      .label(Map.of("en", "Choose One"))
      .build();

    Form form = new Form.Builder()
      .metadata(new Form.Metadata.Builder().label("Test Form").build())
      .putData("choice1", choiceItem)
      .addValueSets(valueSet)
      .build();

    Questionnaire questionnaire = createQuestionnaire("q1", "en",
      createAnswer("choice1", "TEXT", "choice2")
    );

    when(questionnaireDatabase.findOne("test-tenant", "q1")).thenReturn(questionnaire);

    String result = csvSerializer.serializeQuestionnaires(new String[]{"q1"}, form, Locale.ENGLISH);

    assertTrue(result.contains("Choose One"));
    assertTrue(result.contains("Choice 2 Label"));
    assertTrue(result.contains("choice2"));
  }

  @Test
  void testSerializeQuestionnairesWithMultichoiceAnswers() throws IOException {
    FormValueSet valueSet = new FormValueSet.Builder()
      .id("vs1")
      .addEntries(
        new FormValueSetEntry.Builder()
          .id("choice1")
          .label(Map.of("en", "Choice 1 Label"))
          .build(),
        new FormValueSetEntry.Builder()
          .id("choice2")
          .label(Map.of("en", "Choice 2 Label"))
          .build(),
        new FormValueSetEntry.Builder()
          .id("choice3")
          .label(Map.of("en", "Choice 3 Label"))
          .build()
      )
      .build();

    FormItem multichoiceItem = new FormItem.Builder()
      .id("mc1")
      .type(Constants.MULTICHOICE)
      .valueSetId("vs1")
      .label(Map.of("en", "Choose Multiple"))
      .build();

    Form form = new Form.Builder()
      .metadata(new Form.Metadata.Builder().label("Test Form").build())
      .putData("mc1", multichoiceItem)
      .addValueSets(valueSet)
      .build();

    Questionnaire questionnaire = createQuestionnaire("q1", "en",
      createAnswer("mc1", "TEXT", Arrays.asList("choice1", "choice3"))
    );

    when(questionnaireDatabase.findOne("test-tenant", "q1")).thenReturn(questionnaire);

    String result = csvSerializer.serializeQuestionnaires(new String[]{"q1"}, form, Locale.ENGLISH);

    assertTrue(result.contains("Choose Multiple"));
    assertTrue(result.contains("Choice 1 Label, Choice 3 Label"));
    assertTrue(result.contains("choice1, choice3"));
    // Check individual columns for multichoice
    assertTrue(result.contains("1")); // choice1 selected
    assertTrue(result.contains("0")); // choice2 not selected
  }

  @Test
  void testSerializeQuestionnairesWithEmptyAnswer() throws IOException {
    Form form = createFormWithItems(
      createTextItem("text1", "Text Question")
    );

    Questionnaire questionnaire = createQuestionnaire("q1", "en");

    when(questionnaireDatabase.findOne("test-tenant", "q1")).thenReturn(questionnaire);

    String result = csvSerializer.serializeQuestionnaires(new String[]{"q1"}, form, Locale.ENGLISH);

    assertTrue(result.contains("Text Question"));
    // Should have empty values for unanswered questions
    assertTrue(result.contains(",,\r\n") || result.contains(",\r\n"));
  }

  @Test
  void testSerializeQuestionnairesWithContextVariables() throws IOException {
    Form form = new Form.Builder()
      .metadata(new Form.Metadata.Builder().label("Test Form").build())
      .putData("text1", createTextItem("text1", "Question 1"))
      .addVariables(
        new Variable.Builder()
          .name("sessionId")
          .context(true)
          .build(),
        new Variable.Builder()
          .name("userId")
          .context(true)
          .build()
      )
      .build();

    Questionnaire questionnaire = new Questionnaire.Builder()
      .id("q1")
      .metadata(
        new Questionnaire.Metadata.Builder()
          .language("en")
          .formId("form1")
          .build()
      )
      .addAnswers(createAnswer("text1", "TEXT", "Answer 1"))
      .addContext(
        new ContextValue.Builder()
          .id("sessionId")
          .value("session-123")
          .build(),
        new ContextValue.Builder()
          .id("userId")
          .value("user-456")
          .build()
      )
      .build();

    when(questionnaireDatabase.findOne("test-tenant", "q1")).thenReturn(questionnaire);

    String result = csvSerializer.serializeQuestionnaires(new String[]{"q1"}, form, Locale.ENGLISH);

    assertTrue(result.contains("session-123"));
    assertTrue(result.contains("user-456"));
  }

  @Test
  void testSerializeQuestionnairesWithMissingContextVariable() throws IOException {
    Form form = new Form.Builder()
      .metadata(new Form.Metadata.Builder().label("Test Form").build())
      .putData("text1", createTextItem("text1", "Question 1"))
      .addVariables(
        new Variable.Builder()
          .name("sessionId")
          .context(true)
          .build()
      )
      .build();

    Questionnaire questionnaire = createQuestionnaire("q1", "en",
      createAnswer("text1", "TEXT", "Answer 1")
    );

    when(questionnaireDatabase.findOne("test-tenant", "q1")).thenReturn(questionnaire);

    String result = csvSerializer.serializeQuestionnaires(new String[]{"q1"}, form, Locale.ENGLISH);

    // Should handle missing context variable gracefully
    assertNotNull(result);
    assertTrue(result.contains("Question 1"));
  }

  @Test
  void testSerializeQuestionnairesWithMultipleQuestionnaires() throws IOException {
    Form form = createFormWithItems(
      createTextItem("text1", "Question 1")
    );

    Questionnaire q1 = createQuestionnaire("q1", "en",
      createAnswer("text1", "TEXT", "Answer 1")
    );
    Questionnaire q2 = createQuestionnaire("q2", "en",
      createAnswer("text1", "TEXT", "Answer 2")
    );

    when(questionnaireDatabase.findOne("test-tenant", "q1")).thenReturn(q1);
    when(questionnaireDatabase.findOne("test-tenant", "q2")).thenReturn(q2);

    String result = csvSerializer.serializeQuestionnaires(new String[]{"q1", "q2"}, form, Locale.ENGLISH);

    assertTrue(result.contains("Answer 1"));
    assertTrue(result.contains("Answer 2"));
    // Should have header + 2 data rows
    assertEquals(3, result.split("\r\n").length);
  }

  @Test
  void testSerializeQuestionnairesWithNullAnswer() throws IOException {
    Form form = createFormWithItems(
      createTextItem("text1", "Question 1")
    );

    Questionnaire questionnaire = createQuestionnaire("q1", "en",
      new Answer.Builder()
        .id("text1")
        .type("TEXT")
        .build()
    );

    when(questionnaireDatabase.findOne("test-tenant", "q1")).thenReturn(questionnaire);

    String result = csvSerializer.serializeQuestionnaires(new String[]{"q1"}, form, Locale.ENGLISH);

    assertNotNull(result);
    assertTrue(result.contains("Question 1"));
  }

  @Test
  void testSerializeQuestionnairesWithUnknownChoiceValue() throws IOException {
    FormValueSet valueSet = new FormValueSet.Builder()
      .id("vs1")
      .addEntries(
        new FormValueSetEntry.Builder()
          .id("choice1")
          .label(Map.of("en", "Choice 1"))
          .build()
      )
      .build();

    FormItem choiceItem = new FormItem.Builder()
      .id("choice1")
      .type(Constants.LIST)
      .valueSetId("vs1")
      .label(Map.of("en", "Choose One"))
      .build();

    Form form = new Form.Builder()
      .metadata(new Form.Metadata.Builder().label("Test Form").build())
      .putData("choice1", choiceItem)
      .addValueSets(valueSet)
      .build();

    // Answer with unknown choice key
    Questionnaire questionnaire = createQuestionnaire("q1", "en",
      createAnswer("choice1", "TEXT", "unknown_choice")
    );

    when(questionnaireDatabase.findOne("test-tenant", "q1")).thenReturn(questionnaire);

    String result = csvSerializer.serializeQuestionnaires(new String[]{"q1"}, form, Locale.ENGLISH);

    // Should fall back to the key value when label not found
    assertTrue(result.contains("unknown_choice"));
  }

  // Helper methods

  private Form createFormWithItems(FormItem... items) {
    Form.Builder builder = new Form.Builder()
      .metadata(new Form.Metadata.Builder().label("Test Form").build());
    for (FormItem item : items) {
      builder.putData(item.getId(), item);
    }
    return builder.build();
  }

  private FormItem createTextItem(String id, String label) {
    return new FormItem.Builder()
      .id(id)
      .type(Constants.TEXT)
      .label(Map.of("en", label))
      .build();
  }

  private FormItem createBooleanItem(String id, String label) {
    return new FormItem.Builder()
      .id(id)
      .type(Constants.BOOLEAN)
      .label(Map.of("en", label))
      .build();
  }

  private FormItem createItemWithType(String id, String type, String label) {
    return new FormItem.Builder()
      .id(id)
      .type(type)
      .label(Map.of("en", label))
      .build();
  }

  private Questionnaire createQuestionnaire(String id, String language, Answer... answers) {
    Questionnaire.Builder builder = new Questionnaire.Builder()
      .id(id)
      .metadata(
        new Questionnaire.Metadata.Builder()
          .language(language)
          .formId("test-form")
          .build()
      );

    for (Answer answer : answers) {
      builder.addAnswers(answer);
    }

    return builder.build();
  }

  private Answer createAnswer(String id, String type, Object value) {
    return new Answer.Builder()
      .id(id)
      .type(type)
      .value(value)
      .build();
  }
}
