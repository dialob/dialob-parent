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
package io.dialob.form.service.copy;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.dialob.api.form.Form;
import io.dialob.api.form.FormValidationError;
import io.dialob.api.form.ImmutableForm;
import io.dialob.form.service.DialobFormIdRenamer;
import io.dialob.form.service.DialobFormItemCopier;
import io.dialob.form.service.api.validation.FormIdRenamer;
import io.dialob.form.service.api.validation.FormItemCopier;
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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {FormItemCopierTest.TestConfiguration.class})
public class FormItemCopierTest {


  public static class TestConfiguration {
    @Bean
    public FormItemCopier formItemCopier(RuleExpressionCompiler compiler, FormIdRenamer renamerService) {
      return new DialobFormItemCopier(compiler, renamerService);
    }

    @Bean
    public RuleExpressionCompiler ruleExpressionCompiler() {
      return new DialobRuleExpressionCompiler();
    }

    @Bean
    public FormIdRenamer formIdRenamer(RuleExpressionCompiler compiler) {
      return new DialobFormIdRenamer(compiler);
    }

    @Bean
    public ObjectMapper objectMapper() {
      return new ObjectMapper()
        .registerModules(new JavaTimeModule())
        .enable(SerializationFeature.INDENT_OUTPUT);
    }
  }

  @Inject
  private FormItemCopier formItemCopier;

  @Inject
  private ObjectMapper objectMapper;

  private Form loadForm() {
    InputStream formInput = this.getClass().getResourceAsStream("/renamerTestForm.json");

    try {
      return objectMapper.readValue(formInput, Form.class);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Test
  void testQuestionCopy() {
    Form form = loadForm();
    Pair<Form, List<FormValidationError>> resultPair = formItemCopier.copyFormItem(form, "question1");
    form = resultPair.getLeft();
    assertEquals(0, resultPair.getRight().size());
    assertTrue(form.getData().containsKey("question1"));
    assertTrue(form.getData().containsKey("question11"));
    assertEquals(form.getData().get("group1").getItems().indexOf("question1") + 1, form.getData().get("group1").getItems().indexOf("question11"));
    assertEquals(3, form.getValueSets().size());
    assertEquals("valueSet11", form.getData().get("question11").getValueSetId());
  }

  @Test
  void testQuestionCopyGvs() {
    Form form = loadForm();
    Pair<Form, List<FormValidationError>> resultPair = formItemCopier.copyFormItem(form, "question4");
    form = resultPair.getLeft();
    assertEquals(0, resultPair.getRight().size());
    assertTrue(form.getData().containsKey("question4"));
    assertTrue(form.getData().containsKey("question41"));
    assertEquals(form.getData().get("group1").getItems().indexOf("question4") + 1, form.getData().get("group1").getItems().indexOf("question41"));
    assertEquals(2, form.getValueSets().size());
    assertEquals("valueSet2", form.getData().get("question41").getValueSetId());
  }

  @Test
  void testGroupCopy() {
    Form form = loadForm();
    Pair<Form, List<FormValidationError>> resultPair = formItemCopier.copyFormItem(form, "group1");
    form = resultPair.getLeft();
    assertEquals(0, resultPair.getRight().size());
    assertTrue(form.getData().containsKey("group1"));
    assertTrue(form.getData().containsKey("group11"));
    assertEquals(form.getData().get("page1").getItems().indexOf("group1") + 1, form.getData().get("page1").getItems().indexOf("group11"));
    assertEquals(3, form.getValueSets().size());
    assertEquals("valueSet11", form.getData().get("question11").getValueSetId());
    assertThat(form.getData().get("group11").getItems(), contains("question11", "question21", "question31", "question41"));
    assertEquals("question11 = 'test'", form.getData().get("question21").getRequired());
    assertEquals("question11 is answered", form.getData().get("question21").getActiveWhen());
    assertEquals("question21 is answered and question11 = 'test2'", form.getData().get("question21").getValidations().get(0).getRule());
    assertEquals("Test Question3 {question11} and {question21} and {var}", form.getData().get("question31").getLabel().get("en"));

    // Verify original still intact
    assertEquals("valueSet1", form.getData().get("question1").getValueSetId());
    assertThat(form.getData().get("group1").getItems(), contains("question1", "question2", "question3", "question4"));
    assertEquals("question1 = 'test'", form.getData().get("question2").getRequired());
    assertEquals("question1 is answered", form.getData().get("question2").getActiveWhen());
    assertEquals("question2 is answered and question1 = 'test2'", form.getData().get("question2").getValidations().get(0).getRule());
    assertEquals("Test Question3 {question1} and {question2} and {var}", form.getData().get("question3").getLabel().get("en"));
  }

  @Test
  void testUnknownItem() {
    Form form = loadForm();
    ImmutableForm.Builder builder = ImmutableForm.builder().from(form);
    Pair<Form, List<FormValidationError>> resultPair = formItemCopier.copyFormItem(form, "IDontExist");
    assertEquals(1, resultPair.getRight().size());
  }

}
