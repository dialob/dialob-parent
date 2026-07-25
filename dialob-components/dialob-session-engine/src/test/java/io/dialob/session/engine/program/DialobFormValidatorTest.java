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
package io.dialob.session.engine.program;

import tools.jackson.databind.ObjectMapper;
import io.dialob.api.form.Form;
import io.dialob.api.form.FormItem;
import io.dialob.api.form.FormValidationError;
import io.dialob.api.form.Variable;
import io.dialob.rule.parser.api.ValueType;
import io.dialob.rule.parser.api.VariableNotDefinedException;
import io.dialob.rule.parser.function.FunctionRegistry;
import io.dialob.session.engine.DialobProgramFromFormCompiler;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import static org.assertj.core.groups.Tuple.tuple;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

class DialobFormValidatorTest {

  ObjectMapper objectMapper = new ObjectMapper();

  private Form loadForm(String formFile) {
    try {
      return objectMapper.readValue(Thread.currentThread().getContextClassLoader().getResourceAsStream(formFile), Form.class);
    } catch (Throwable e) {
      Assertions.fail("Could not load " + formFile,e);
    }
    return null;
  }

  @Test
  void shouldReportMissingExpressionOnVariable() {
    FunctionRegistry functionRegistry = Mockito.mock(FunctionRegistry.class);
    DialobProgramFromFormCompiler compiler = new DialobProgramFromFormCompiler(functionRegistry);
    DialobFormValidator validator = new DialobFormValidator(compiler);

    List<FormValidationError> errors = validator.validate(new Form.Builder()
      .id("123")
      .metadata(new Form.Metadata.Builder().label("").build())
      .putData("questionnaire", new FormItem.Builder().id("questionnaire").type("questionnaire").build())
      .addVariables(new Variable.Builder().name("var1").build())
      .build());

    Assertions.assertThat(errors).contains(new FormValidationError.Builder().itemId("var1").type(FormValidationError.Type.VARIABLE).message("RB_VARIABLE_NEEDS_EXPRESSION").build());

    Mockito.verifyNoMoreInteractions(functionRegistry);
  }

  @Test
  void shouldAcceptNegativeNumberVariable() {
    FunctionRegistry functionRegistry = Mockito.mock(FunctionRegistry.class);
    DialobProgramFromFormCompiler compiler = new DialobProgramFromFormCompiler(functionRegistry);
    DialobFormValidator validator = new DialobFormValidator(compiler);

    List<FormValidationError> errors = validator.validate(new Form.Builder()
      .id("123")
      .metadata(new Form.Metadata.Builder().label("").build())
      .putData("questionnaire", new FormItem.Builder().id("questionnaire").type("questionnaire").build())
      .addVariables(new Variable.Builder().name("var1").expression("-1").build())
      .build());

    Assertions.assertThat(errors).isEmpty();

    Mockito.verifyNoMoreInteractions(functionRegistry);
  }

  @Test
  void shouldReportIncompatibleComparison() {
    FunctionRegistry functionRegistry = Mockito.mock(FunctionRegistry.class);
    DialobProgramFromFormCompiler compiler = new DialobProgramFromFormCompiler(functionRegistry);
    DialobFormValidator validator = new DialobFormValidator(compiler);

    Form form = loadForm("io/dialob/session/engine/issue-171.json");

    List<FormValidationError> errors = validator.validate(form);

    Assertions.assertThat(errors).contains(new FormValidationError.Builder()
      .itemId("text1")
      .type(FormValidationError.Type.VISIBILITY)
      .message("NO_ORDER_RELATION_BETWEEN_TYPES")
      .startIndex(0)
      .endIndex(18)
      .build());

    Mockito.verifyNoMoreInteractions(functionRegistry);
  }

  @Test
  void issue233() {
    FunctionRegistry functionRegistry = Mockito.mock(FunctionRegistry.class);
    DialobProgramFromFormCompiler compiler = new DialobProgramFromFormCompiler(functionRegistry);
    DialobFormValidator validator = new DialobFormValidator(compiler);

    Form form = loadForm("io/dialob/session/engine/issue-233.json");

    List<FormValidationError> errors = validator.validate(form);

    Assertions.assertThat(errors).contains(new FormValidationError.Builder()
      .itemId("text1")
      .type(FormValidationError.Type.VALIDATION)
      .message("MATCHER_REGEX_SYNTAX_ERROR")
      .startIndex(0)
      .endIndex(38)
      .build());

    Mockito.verifyNoMoreInteractions(functionRegistry);
  }

  @Test
  void issue233b() {
    FunctionRegistry functionRegistry = Mockito.mock(FunctionRegistry.class);
    DialobProgramFromFormCompiler compiler = new DialobProgramFromFormCompiler(functionRegistry);
    DialobFormValidator validator = new DialobFormValidator(compiler);

    Form form = loadForm("io/dialob/session/engine/issue-233b.json");

    List<FormValidationError> errors = validator.validate(form);

    Assertions.assertThat(errors).contains(new FormValidationError.Builder()
      .itemId("text1")
      .type(FormValidationError.Type.VALIDATION)
      .message("MATCHER_DYNAMIC_REGEX")
      .startIndex(0)
      .endIndex(25)
      .build());

    Mockito.verifyNoMoreInteractions(functionRegistry);
  }


  @Test
  void issue275() throws VariableNotDefinedException {
    FunctionRegistry functionRegistry = Mockito.mock(FunctionRegistry.class);
    when(functionRegistry.isAsyncFunction("count")).thenReturn(false);

    when(functionRegistry.returnTypeOf("count", ValueType.arrayOf(ValueType.STRING))).thenReturn(ValueType.INTEGER);


    DialobProgramFromFormCompiler compiler = new DialobProgramFromFormCompiler(functionRegistry);
    DialobFormValidator validator = new DialobFormValidator(compiler);

    Form form = loadForm("io/dialob/session/engine/issue-275.json");

    List<FormValidationError> errors = validator.validate(form);

    Assertions.assertThat(errors.isEmpty()).isTrue();

    Mockito.verify(functionRegistry, times(2)).isAsyncFunction("count");
    Mockito.verify(functionRegistry).returnTypeOf("count", ValueType.arrayOf(ValueType.STRING));
    Mockito.verifyNoMoreInteractions(functionRegistry);
  }

  @Test
  void validateOfExpressions() {
    FunctionRegistry functionRegistry = Mockito.mock(FunctionRegistry.class);
    DialobProgramFromFormCompiler compiler = new DialobProgramFromFormCompiler(functionRegistry);
    DialobFormValidator validator = new DialobFormValidator(compiler);

    Form form = new Form.Builder()
      .id("yyy")
      .name("zzz")
      .metadata(new Form.Metadata.Builder()
        .label("xxx")
        .build())
      .putData("questionnaire", new FormItem.Builder().id("questionnaire").type("questionnaire").addItems("page").build())
      .putData("page", new FormItem.Builder().id("page").type("group").addItems("rg","o1","o2","o3","o4").build())
      .putData("rg", new FormItem.Builder().id("rg").type("rowgroup").addItems("q1","q2","q3","q4","q5").build())
      .putData("q1", new FormItem.Builder().id("q1").type("number").build())
      .putData("q2", new FormItem.Builder().id("q2").type("decimal").build())
      .putData("q3", new FormItem.Builder().id("q3").type("boolean").build())
      .putData("q4", new FormItem.Builder().id("q4").type("text").build())
      .putData("q5", new FormItem.Builder().id("q5").type("note").activeWhen("sum of q1 > 0").build())
      .putData("o1", new FormItem.Builder().id("o1").type("note").activeWhen("sum of q1 > 0").build())
      .putData("o2", new FormItem.Builder().id("o2").type("note").activeWhen("xxx of q3").build())
      .putData("o3", new FormItem.Builder().id("o3").type("note").activeWhen("sum of q4 = \"\"").build())
      .putData("o4", new FormItem.Builder().id("o4").type("note").activeWhen("sum of (1)").build())

      .build();

    Assertions.assertThat(validator.validate(form)).extracting("itemId", "message").containsExactlyInAnyOrder(
      tuple("o2", "UNKNOWN_REDUCER_OPERATOR"),
      tuple("o3", "OPERATOR_CANNOT_REDUCE_TYPE"),
      tuple("rg.*.q5", "CANNOT_USE_REDUCER_INSIDE_SCOPE"),
      tuple("rg.*.q5", "COULD_NOT_DEDUCE_TYPE"),
      tuple("o4", "REDUCER_TARGET_MUST_BE_REFERENCE")
    );


    Mockito.verifyNoMoreInteractions(functionRegistry);
  }


}
