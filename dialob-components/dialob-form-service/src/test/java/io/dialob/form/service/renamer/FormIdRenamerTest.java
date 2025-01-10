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
package io.dialob.form.service.renamer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.dialob.api.form.*;
import io.dialob.form.service.DialobFormIdRenamer;
import io.dialob.form.service.api.validation.FormIdRenamer;
import io.dialob.rule.parser.api.RuleExpressionCompiler;
import io.dialob.session.engine.program.DialobRuleExpressionCompiler;
import jakarta.inject.Inject;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {FormIdRenamerTest.TestConfiguration.class})
public class FormIdRenamerTest {

  ObjectMapper objectMapper = new ObjectMapper().registerModules(new JavaTimeModule());

  public static class TestConfiguration {
    @Bean
    public FormIdRenamer formIdRenamer(RuleExpressionCompiler compiler) {
      return new DialobFormIdRenamer(compiler);
    }

    @Bean
    public RuleExpressionCompiler ruleExpressionCompiler() {
      return new DialobRuleExpressionCompiler();
    }
  }

  @Inject
  private FormIdRenamer formIdRenamer;

  private Form loadForm() {
    InputStream formInput = this.getClass().getResourceAsStream("/renamerTestForm.json");

    try {
      return objectMapper.readValue(formInput, Form.class);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Test
  public void testQuestionRename() {
    Form form = loadForm();
    Pair<Form, List<FormValidationError>> resultPair = formIdRenamer.renameIdentifiers(form, "question1", "test");
    assertEquals(0, resultPair.getRight().size());
    form = resultPair.getLeft();
    assertFalse(form.getData().containsKey("question1"));
    assertTrue(form.getData().containsKey("test"));

    FormItem q2 = form.getData().get("question2");
    assertEquals("test is answered", q2.getActiveWhen());
    assertEquals("test = 'test'", q2.getRequired());
    assertEquals("test = 'xx'", q2.getCanAddRowWhen());
    assertEquals("test = 'yy'", q2.getCanRemoveRowWhen());

    Validation validationBean = q2.getValidations().get(0);
    assertEquals("question2 is answered and test = 'test2'", validationBean.getRule());

    FormItem q3 = form.getData().get("question3");
    assertEquals("Test Question3 {test} and {question2} and {var}", q3.getLabel().get("en"));

    FormItem group1 = form.getData().get("group1");
    assertTrue(group1.getItems().contains("test"));
    assertFalse(group1.getItems().contains("question1"));

    Optional<Variable> var = form.getVariables().stream().filter(v -> v.getName().equals("var")).findFirst();
    assertTrue(var.isPresent());
    Variable varBean = var.get();
    assertEquals("'one' + test + 'two'", varBean.getExpression());

    FormValueSet valueSet1 = form.getValueSets().stream().filter(formValueSet -> formValueSet.getId().equals("valueSet1")).findFirst().get();
    FormValueSetEntry entry2 = valueSet1.getEntries().stream().filter(formValueSetEntry -> formValueSetEntry.getId().equals("entry2")).findFirst().get();
    assertEquals("test = 'zz'", entry2.getWhen());
  }

  @Test
  public void testVariableRename() {
    Form form = loadForm();
    Pair<Form, List<FormValidationError>> resultPair = formIdRenamer.renameIdentifiers(form, "var", "test");
    form = resultPair.getLeft();
    assertEquals(0, resultPair.getRight().size());

    Optional<Variable> var = form.getVariables().stream().filter(v -> v.getName().equals("var")).findFirst();
    assertFalse(var.isPresent());

    var = form.getVariables().stream().filter(v -> v.getName().equals("test")).findFirst();
    assertTrue(var.isPresent());

    Variable varBean = var.get();
    assertEquals("'one' + question1 + 'two'", varBean.getExpression());

    FormItem q3 = form.getData().get("question3");
    assertEquals("Test Question3 {question1} and {question2} and {test}", q3.getLabel().get("en"));
  }

  @Test
  public void testVarFormatError() {
    Form form = loadForm();
    Pair<Form, List<FormValidationError>> resultPair = formIdRenamer.renameIdentifiers(form, "question1", "123 abc");
    assertEquals(1, resultPair.getRight().size());
    assertEquals("FORM_NEW_VAR_FORMAT", resultPair.getRight().get(0).getMessage());
    assertTrue(form.getData().containsKey("question1"));
  }

  @Test
  public void shouldAcceptUnderscore() {
    Form form = loadForm();
    Pair<Form, List<FormValidationError>> resultPair = formIdRenamer.renameIdentifiers(form, "question1", "question_1");
    assertEquals(0, resultPair.getRight().size());
    form = loadForm();
    resultPair = formIdRenamer.renameIdentifiers(form, "question1", "_question1");
    assertEquals(0, resultPair.getRight().size());
  }

  @Test
  public void testItemClash() {
    Form form = loadForm();
    Pair<Form, List<FormValidationError>> resultPair = formIdRenamer.renameIdentifiers(form, "question1", "question2");
    assertEquals(1, resultPair.getRight().size());
    assertEquals("FORM_NEW_VAR_CLASH", resultPair.getRight().get(0).getMessage());
    assertTrue(form.getData().containsKey("question1"));
  }

  @Test
  public void variableClash() {
    Form form = loadForm();
    Pair<Form, List<FormValidationError>> resultPair = formIdRenamer.renameIdentifiers(form, "question1", "var");
    assertEquals(1, resultPair.getRight().size());
    assertEquals("FORM_NEW_VAR_CLASH", resultPair.getRight().get(0).getMessage());
    assertTrue(form.getData().containsKey("question1"));
  }

  @Test
  public void reservedWordClash() {
    Form form = loadForm();
    Pair<Form, List<FormValidationError>> resultPair = formIdRenamer.renameIdentifiers(form, "question1", "matches");
    assertEquals("FORM_NEW_VAR_CLASH", resultPair.getRight().get(0).getMessage());
    assertTrue(form.getData().containsKey("question1"));
  }

  @Test
  public void testQuestionRenameInOperator() {
    Form form = loadForm();
    Pair<Form, List<FormValidationError>> resultPair = formIdRenamer.renameIdentifiers(form, "question1", "test");
    form = resultPair.getLeft();
    assertEquals(0, resultPair.getRight().size());

    FormItem q4 = form.getData().get("question4");
    assertEquals("\"entry1\" in test", q4.getActiveWhen());
  }

  @Test
  public void testRenamerOnInvalidSyntax() {
    Form form = loadForm();
    form = ImmutableForm.builder().from(form).putData("question4", ImmutableFormItem.builder().from(form.getData().get("question4")).activeWhen("\"entry1\" inn question1").build()).build();
    Pair<Form, List<FormValidationError>> resultPair = formIdRenamer.renameIdentifiers(form, "question1", "test");
    assertEquals(0, resultPair.getRight().size());
    assertEquals("\"entry1\" inn question1", resultPair.getLeft().getData().get("question4").getActiveWhen());
  }

  @Test
  public void issue174() throws IOException {
    String formData = "{\"_id\":\"7b2a87b0d7f60e78ec3b5e2164b76c3f\",\"_rev\":\"382\",\"name\":\"testi2\",\"data\":{\"questionnaire\":{\"id\":\"questionnaire\",\"type\":\"questionnaire\",\"items\":[\"group1\"]},\"text2\":{\"id\":\"text2\",\"type\":\"text\",\"label\":{\"en\":\"Kuinka paljon neliöitä asunnossasi on?\"},\"required\":\"boolean1\",\"activeWhen\":\"boolean1\",\"validations\":[{\"message\":{\"en\":\"\"}}]},\"text3\":{\"id\":\"text3\",\"type\":\"text\",\"label\":{\"en\":\"Kerro kissasi rotu\"},\"className\":[\"textbox\"],\"activeWhen\":\"list1\",\"defaultValue\":\"Key1\"},\"boolean1\":{\"id\":\"boolean1\",\"type\":\"boolean\",\"label\":{\"en\":\"Onko sinulla kotivakutuus?\"},\"validations\":[{\"message\":{\"en\":\"\"},\"rule\":\"boolean1 = 'True'\"}]},\"boolean2\":{\"id\":\"boolean2\",\"type\":\"boolean\",\"label\":{\"en\":\"Onko sinulla kissa?\"}},\"surveygroup1\":{\"id\":\"surveygroup1\",\"type\":\"surveygroup\",\"label\":{\"en\":\"Tiedätkö minkä rotuinen kissasi on?\"},\"items\":[\"list1\",\"text3\"],\"activeWhen\":\"boolean2\"},\"group1\":{\"id\":\"group1\",\"type\":\"group\",\"items\":[\"group2\",\"group3\"]},\"group2\":{\"id\":\"group2\",\"type\":\"group\",\"label\":{\"en\":\"Onko sinulla kotivakutuus?\"},\"items\":[\"boolean1\",\"text2\",\"list2\"]},\"list1\":{\"id\":\"list1\",\"type\":\"list\",\"label\":{\"en\":\"\"},\"valueSetId\":\"vs1\"},\"group3\":{\"id\":\"group3\",\"type\":\"group\",\"label\":{\"en\":\"\"},\"items\":[\"surveygroup1\",\"boolean2\"]},\"list2\":{\"id\":\"list2\",\"type\":\"list\",\"label\":{\"en\":\"Onko kyseessä omakotitalo?\"},\"required\":\"text2\",\"activeWhen\":\"text2\",\"validations\":[{\"message\":{\"en\":\"\"},\"rule\":\"text2 > '100'\"}]}},\"metadata\":{\"composer\":{\"transient\":{\"lastItem\":{\"id\":\"boolean2\",\"type\":\"boolean\"}}},\"tenantId\":\"10d66fc4-3da6-4474-9bb0-2aa21b34b29c\",\"created\":\"2019-03-08T10:06:41.454+0000\",\"creator\":\"490c78d9-80b6-4085-bf77-148f8ab79901\",\"lastSaved\":\"2019-03-11T11:36:46.376+0000\",\"label\":\"New Form\",\"languages\":[\"en\"],\"valid\":true,\"savedBy\":\"3a09a7cc-a3b6-49fc-96dc-76d0c242d8d7\"},\"valueSets\":[{\"id\":\"vs1\",\"entries\":[{\"id\":\"Key1\",\"label\":{\"en\":\"Kyllä\"}},{\"id\":\"Key2\",\"label\":{\"en\":\"En\"}}]}]}";
    Form form = objectMapper.readValue(formData, Form.class);
    Pair<Form, List<FormValidationError>> resultPair = formIdRenamer.renameIdentifiers(form, "text2", "textii");
    assertEquals(0, resultPair.getRight().size());
    assertEquals("textii", resultPair.getLeft().getData().get("list2").getActiveWhen());
    assertEquals("textii", resultPair.getLeft().getData().get("list2").getRequired());
  }
}
