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
package io.dialob.form.service;

import io.dialob.api.form.*;
import io.dialob.form.service.api.validation.FormIdRenamer;
import io.dialob.rule.parser.DialobRuleLexer;
import io.dialob.rule.parser.api.RuleExpressionCompiler;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.Vocabulary;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.lang.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.UnaryOperator;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

public class DialobFormIdRenamer implements FormIdRenamer {

  private final RuleExpressionCompiler compiler;

  private final List<String> reservedWords;

  public DialobFormIdRenamer(@NonNull RuleExpressionCompiler compiler) {
    this.compiler = compiler;
    this.reservedWords = new ArrayList<>();
    DialobRuleLexer lexer = new DialobRuleLexer(CharStreams.fromString(""));
    Vocabulary vocabulary = lexer.getVocabulary();
    int i = 0;
    String w;
    while ((w = vocabulary.getLiteralName(++i)) != null) {
      reservedWords.add(w.substring(1,w.length()-1));
    }
  }

  @Override
  public List<FormValidationError> validateRename(@NonNull Form formDocument, @NonNull String oldId, @NonNull String newId) {
    List<FormValidationError> errors = new ArrayList<>();

    // Check name format
    if (!newId.matches("^[a-zA-Z_][a-zA-Z\\d_]*$")) {
      errors.add(createRenamerExpressionCompilerError(oldId, "FORM_NEW_VAR_FORMAT"));
    }

    // Check reserved word clash
    reservedWords.forEach(t -> {
      if (t.equals(newId)) {
        errors.add(createRenamerExpressionCompilerError(oldId, "FORM_NEW_VAR_CLASH"));
      }
    });

    // Check item ID and variable name clash
    Map<String, FormItem> formData = formDocument.getData();
    if (formData.containsKey(newId) ||
      formDocument.getVariables().stream().anyMatch(v -> v.getName().equals(newId))) {
      errors.add(createRenamerExpressionCompilerError(oldId, "FORM_NEW_VAR_CLASH"));
    }
    return errors;
  }

  private FormItem renameItemAndAttributes(FormItem item, UnaryOperator<String> idRenamer, String oldId, String newId) {
    ImmutableFormItem.Builder builder = ImmutableFormItem.builder().from(renameAttributes(item, idRenamer, oldId, newId));
    if (oldId.equals(item.getId())) {
      builder.id(newId);
    }
    return builder.build();
  }



  @Override
  public FormItem renameAttributes(@NonNull FormItem item, @NonNull UnaryOperator<String> idRenamer, @NonNull String oldId, @NonNull String newId) {
    ImmutableFormItem.Builder builder = ImmutableFormItem.builder().from(item);
    if (item.getActiveWhen() != null) {
      builder.activeWhen(idRenamer.apply(item.getActiveWhen()));
    }
    if (item.getRequired() != null) {
      builder.required(idRenamer.apply(item.getRequired()));
    }
    List<Validation> validations = item.getValidations().stream().map(validation -> ImmutableValidation.builder().from(validation).rule(idRenamer.apply(validation.getRule())).build()).collect(toList());
    builder.validations(validations);

    // Child refs
    int index = item.getItems().indexOf(oldId);
    if (index > -1) {
      List<String> newItems = new ArrayList<>();
      newItems.addAll(item.getItems());
      newItems.set(index, newId);
      builder.items(newItems);
    }

    // Item label refs
    if (!item.getLabel().isEmpty()) {
      item.getLabel().forEach((l, m) -> {
        String newLabel = m.replaceAll("\\{(" + oldId + ")}", "{" + newId + "}");
        builder.putLabel(l, newLabel);
      });
    }
    return builder.build();
  }

  @Override
  public Pair<Form, List<FormValidationError>> renameIdentifiers(@NonNull Form form, @NonNull String oldId, @NonNull String newId) {
    List<FormValidationError> errors = validateRename(form, oldId, newId);
    if (!errors.isEmpty()) {
      return Pair.of(form, errors);
    }
    UnaryOperator<String> idRenamer = compiler.createIdRenamer(oldId, newId);
    ImmutableForm.Builder formBuilder = ImmutableForm.builder().from(form);
    formBuilder.data(form.getData().entrySet().stream()
      .map(itemEntry -> renameItemAndAttributes(itemEntry.getValue(), idRenamer, oldId, newId))
      .collect(toMap(FormItem::getId, item -> item)));
    List<io.dialob.api.form.Variable> updatedVariables = new ArrayList<>();
    // Handle variable expressions
    form.getVariables().forEach(v -> {
        ImmutableVariable.Builder builder = ImmutableVariable.builder().from(v);
        if (!Boolean.TRUE.equals(v.getContext())) {
          builder.expression(idRenamer.apply(v.getExpression()));
        }
        if (v.getName().equals(oldId)) {
          builder.name(newId);
        }
        updatedVariables.add(builder.build());
      }
    );
    formBuilder.variables(updatedVariables);
    return Pair.of(formBuilder.build(), errors);
  }

  private FormValidationError createRenamerExpressionCompilerError(String oldId, String message) {
    return ImmutableFormValidationError.builder()
      .type(FormValidationError.Type.GENERAL)
      .itemId(oldId)
      .message(message)
      .build();
  }

}
