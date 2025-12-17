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
package io.dialob.form.service;

import io.dialob.api.form.Form;
import io.dialob.api.form.FormItem;
import io.dialob.common.Constants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class DialobCsvToFormParserTest {

  private DialobCsvToFormParser parser;

  @BeforeEach
  void setUp() {
    parser = new DialobCsvToFormParser();
  }

  @Test
  void shouldParseValidCsvWithMultipleLanguages() {
    String csv = """
      myForm
      id,type,en,fi
      q1,Text,What is your name?,Mikä on nimesi?
      q2,Boolean,Do you agree?,Hyväksytkö?
      """;

    Form form = parser.parseCsv(csv);

    assertNotNull(form);
    assertEquals("myForm", form.getName());
    assertEquals("myForm", form.getMetadata().getLabel());
    assertEquals(2, form.getMetadata().getLanguages().size());
    assertTrue(form.getMetadata().getLanguages().contains("en"));
    assertTrue(form.getMetadata().getLanguages().contains("fi"));

    Map<String, FormItem> data = form.getData();
    assertNotNull(data.get("q1"));
    assertEquals(Constants.TEXT, data.get("q1").getType());
    assertEquals("What is your name?", data.get("q1").getLabel().get("en"));
    assertEquals("Mikä on nimesi?", data.get("q1").getLabel().get("fi"));

    assertNotNull(data.get("q2"));
    assertEquals(Constants.BOOLEAN, data.get("q2").getType());
  }

  @Test
  void shouldParseValidCsvWithSingleLanguage() {
    String csv = """
      testForm
      id,type,en
      question1,Text,Enter your name
      """;

    Form form = parser.parseCsv(csv);

    assertNotNull(form);
    assertEquals("testForm", form.getName());
    assertEquals(1, form.getMetadata().getLanguages().size());
    assertTrue(form.getMetadata().getLanguages().contains("en"));

    FormItem item = form.getData().get("question1");
    assertNotNull(item);
    assertEquals("Enter your name", item.getLabel().get("en"));
  }

  @Test
  void shouldGenerateIdWhenNotProvided() {
    String csv = """
      myForm
      id,type,en
      ,Text,First question
      ,Text,Second question
      ,Boolean,Third question
      """;

    Form form = parser.parseCsv(csv);

    Map<String, FormItem> data = form.getData();
    assertNotNull(data.get("text1"));
    assertNotNull(data.get("text2"));
    assertNotNull(data.get("boolean1"));
  }

  @Test
  void shouldMapAllItemTypes() {
    String csv = """
      myForm
      id,type,en
      txt,Text,Text question
      bool,Boolean,Boolean question
      dt,Date,Date question
      tm,Time,Time question
      lst,Choice,Choice question
      nt,Note,Note text
      num,Integer,Number question
      """;

    Form form = parser.parseCsv(csv);

    Map<String, FormItem> data = form.getData();
    assertEquals(Constants.TEXT, data.get("txt").getType());
    assertEquals(Constants.BOOLEAN, data.get("bool").getType());
    assertEquals(Constants.DATE, data.get("dt").getType());
    assertEquals(Constants.TIME, data.get("tm").getType());
    assertEquals(Constants.LIST, data.get("lst").getType());
    assertEquals(Constants.NOTE, data.get("nt").getType());
    assertEquals(Constants.NUMBER, data.get("num").getType());
  }

  @Test
  void shouldSetTextViewForTextItems() {
    String csv = """
      myForm
      id,type,en
      q1,Text,Question
      """;

    Form form = parser.parseCsv(csv);

    FormItem item = form.getData().get("q1");
    assertEquals("text", item.getView());
  }

  @Test
  void shouldCreateQuestionnairePageAndGroupStructure() {
    String csv = """
      myForm
      id,type,en
      q1,Text,Question
      """;

    Form form = parser.parseCsv(csv);

    Map<String, FormItem> data = form.getData();

    // Verify questionnaire
    FormItem questionnaire = data.get("questionnaire");
    assertNotNull(questionnaire);
    assertEquals("questionnaire", questionnaire.getType());
    assertEquals(1, questionnaire.getItems().size());
    assertEquals("page1", questionnaire.getItems().get(0));

    // Verify page1
    FormItem page1 = data.get("page1");
    assertNotNull(page1);
    assertEquals("group", page1.getType());
    assertEquals("page", page1.getView());
    assertEquals(1, page1.getItems().size());
    assertEquals("group1", page1.getItems().get(0));

    // Verify group1
    FormItem group1 = data.get("group1");
    assertNotNull(group1);
    assertEquals("group", group1.getType());
    assertEquals(1, group1.getItems().size());
    assertEquals("q1", group1.getItems().get(0));
  }

  @Test
  void shouldSkipEmptyRows() {
    String csv = """
      myForm
      id,type,en
      q1,Text,Question 1
      ,,

      q2,Text,Question 2
      """;

    Form form = parser.parseCsv(csv);

    Map<String, FormItem> data = form.getData();
    FormItem group1 = data.get("group1");
    assertEquals(2, group1.getItems().size());
    assertTrue(group1.getItems().contains("q1"));
    assertTrue(group1.getItems().contains("q2"));
  }

  @Test
  void shouldHandleCaseInsensitiveTypes() {
    String csv = """
      myForm
      id,type,en
      q1,text,Question 1
      q2,TEXT,Question 2
      q3,Text,Question 3
      """;

    Form form = parser.parseCsv(csv);

    Map<String, FormItem> data = form.getData();
    assertEquals(Constants.TEXT, data.get("q1").getType());
    assertEquals(Constants.TEXT, data.get("q2").getType());
    assertEquals(Constants.TEXT, data.get("q3").getType());
  }

  // ========== Error Cases ==========

  private static Stream<Arguments> errorCases() {
    return Stream.of(
      Arguments.of("Null CSV", null),
      Arguments.of("Empty CSV", ""),
      Arguments.of("Blank CSV", "   ")
    );
  }

  @ParameterizedTest
  @MethodSource("errorCases")
  void shouldThrowExceptionForErrorCase(String description, String csv) {
    CsvParsingException exception = assertThrows(CsvParsingException.class, () -> {
      parser.parseCsv(csv);
    });
    assertEquals("CSV data is empty or null.", exception.getMessage());
  }

  @Test
  void shouldThrowExceptionForInsufficientRows() {
    String csv = "myForm";

    CsvParsingException exception = assertThrows(CsvParsingException.class, () -> {
      parser.parseCsv(csv);
    });
    assertEquals("CSV does not contain enough rows for headers and data.", exception.getMessage());
  }

  @Test
  void shouldThrowExceptionForMissingFormName() {
    String csv = """

      id,type,en
      q1,Text,Question
      """;

    CsvParsingException exception = assertThrows(CsvParsingException.class, () -> {
      parser.parseCsv(csv);
    });
    // When form name is blank, it hits the header row validation error first
    assertTrue(exception.getMessage().contains("Incorrect header row") ||
              exception.getMessage().contains("Technical name of the dialog is missing"));
  }

  @Test
  void shouldThrowExceptionForFormNameWithSpaces() {
    String csv = """
      my Form
      id,type,en
      q1,Text,Question
      """;

    CsvParsingException exception = assertThrows(CsvParsingException.class, () -> {
      parser.parseCsv(csv);
    });
    assertEquals("Technical name of the dialog must be a single word without spaces.", exception.getMessage());
  }

  @Test
  void shouldThrowExceptionForMissingIdHeader() {
    String csv = """
      myForm
      type,en
      Text,Question
      """;

    CsvParsingException exception = assertThrows(CsvParsingException.class, () -> {
      parser.parseCsv(csv);
    });
    assertTrue(exception.getMessage().contains("Incorrect header row"));
  }

  @Test
  void shouldThrowExceptionForMissingTypeHeader() {
    String csv = """
      myForm
      id,en
      q1,Question
      """;

    CsvParsingException exception = assertThrows(CsvParsingException.class, () -> {
      parser.parseCsv(csv);
    });
    assertTrue(exception.getMessage().contains("Incorrect header row"));
  }

  @Test
  void shouldThrowExceptionForNoValidLanguages() {
    String csv = """
      myForm
      id,type
      q1,Text
      """;

    CsvParsingException exception = assertThrows(CsvParsingException.class, () -> {
      parser.parseCsv(csv);
    });
    assertEquals("CSV headers must contain at least one valid language.", exception.getMessage());
  }

  @Test
  void shouldThrowExceptionForInvalidLanguageCode() {
    String csv = """
      myForm
      id,type,english
      q1,Text,Question
      """;

    CsvParsingException exception = assertThrows(CsvParsingException.class, () -> {
      parser.parseCsv(csv);
    });
    assertEquals("CSV headers must contain at least one valid language.", exception.getMessage());
  }

  @Test
  void shouldThrowExceptionForMissingItemType() {
    String csv = """
      myForm
      id,type,en
      q1,,Question
      """;

    CsvParsingException exception = assertThrows(CsvParsingException.class, () -> {
      parser.parseCsv(csv);
    });
    assertEquals("Item type is missing or empty.", exception.getMessage());
  }

  @Test
  void shouldThrowExceptionForInvalidItemType() {
    String csv = """
      myForm
      id,type,en
      q1,InvalidType,Question
      """;

    CsvParsingException exception = assertThrows(CsvParsingException.class, () -> {
      parser.parseCsv(csv);
    });
    assertTrue(exception.getMessage().contains("Invalid item type"));
  }

  @Test
  void shouldThrowExceptionForDuplicateIds() {
    String csv = """
      myForm
      id,type,en
      q1,Text,Question 1
      q1,Text,Question 2
      """;

    CsvParsingException exception = assertThrows(CsvParsingException.class, () -> {
      parser.parseCsv(csv);
    });
    assertTrue(exception.getMessage().contains("Duplicate form item ID found: q1"));
  }

  @Test
  void shouldAcceptValidTwoLetterLanguageCodes() {
    String csv = """
      myForm
      id,type,en,fi,sv,de,fr
      q1,Text,English,Finnish,Swedish,German,French
      """;

    Form form = parser.parseCsv(csv);

    assertEquals(5, form.getMetadata().getLanguages().size());
    assertTrue(form.getMetadata().getLanguages().containsAll(java.util.List.of("en", "fi", "sv", "de", "fr")));
  }

  @Test
  void shouldIgnoreInvalidLanguageColumnsButKeepValidOnes() {
    String csv = """
      myForm
      id,type,en,ENG,fi,123
      q1,Text,Question,Invalid,Kysymys,Invalid2
      """;

    Form form = parser.parseCsv(csv);

    // Only "en" and "fi" should be valid (lowercase two-letter codes)
    assertEquals(2, form.getMetadata().getLanguages().size());
    assertTrue(form.getMetadata().getLanguages().contains("en"));
    assertTrue(form.getMetadata().getLanguages().contains("fi"));
  }

  @Test
  void shouldHandleEmptyLabels() {
    String csv = """
      myForm
      id,type,en,fi
      q1,Text,Question,
      q2,Text,,Finnish question
      """;

    Form form = parser.parseCsv(csv);

    FormItem q1 = form.getData().get("q1");
    assertEquals("Question", q1.getLabel().get("en"));
    assertEquals("", q1.getLabel().get("fi"));

    FormItem q2 = form.getData().get("q2");
    assertEquals("", q2.getLabel().get("en"));
    assertEquals("Finnish question", q2.getLabel().get("fi"));
  }

  @Test
  void shouldTrimTypeBeforeMapping() {
    String csv = """
      myForm
      id,type,en
      q1,  Text  ,Question
      """;

    Form form = parser.parseCsv(csv);

    FormItem item = form.getData().get("q1");
    assertEquals(Constants.TEXT, item.getType());
  }
}
