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
package io.dialob.session.engine;

import com.google.common.collect.Maps;
import edu.umd.cs.findbugs.annotations.NonNull;
import io.dialob.api.form.*;
import io.dialob.common.Constants;
import io.dialob.form.service.api.repository.*;
import io.dialob.rule.parser.function.FunctionRegistry;
import io.dialob.session.engine.program.DialobProgram;
import io.dialob.session.engine.program.ProgramBuilder;
import io.dialob.session.engine.program.QuestionBuilder;
import io.dialob.session.engine.program.ValueSetBuilder;
import io.dialob.session.engine.program.model.Program;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Slf4j
public class DialobProgramFromFormCompiler {

  private final FunctionRegistry functionRegistry;

  public DialobProgramFromFormCompiler(FunctionRegistry functionRegistry) {
    this.functionRegistry = functionRegistry;
  }


  /**
   * Converts FormDocument to DialobProgram
   *
   * @param formDocument
   * @throws DialobProgramErrorsException if any compilation errors found
   * @return compiled DialobProgram
   */
  public DialobProgram compileForm(@NonNull Form formDocument) {
    ProgramBuilder builder = new ProgramBuilder(functionRegistry);
    VisitableForm.makeVisitableForm(formDocument).accept(new FormVisitor() {

      private boolean answersRequiredByDefault = false;

      Set<String> languages;

      private String formLabel;

      @Override
      public void start() {
        builder.startProgram();
      }

      @Override
      public void visitForm(@NonNull Form formDocument) {
        builder.setId(formDocument.getId());
        formLabel = formDocument.getMetadata().getLabel();
        languages = formDocument.getMetadata().getLanguages();
        Object allRequiredByDefault = formDocument.getMetadata().getAdditionalProperties().get("answersRequiredByDefault");
        if (Boolean.TRUE.equals(allRequiredByDefault) ||
          allRequiredByDefault instanceof String string && BooleanUtils.toBoolean(string) ) {
          this.answersRequiredByDefault = true;
        }
        if (languages.isEmpty()) {
          languages = Set.of("en");
        }
      }

      @Override
      public Optional<FormItemVisitor> startFormItems() {
        return Optional.of(new FormItemVisitor() {


          @Override
          public void visitQuestionnaireItem(@NonNull FormItem formItem) {
            builder
              .addRoot()
              .setView(formItem.getView())
              .setLabel(Maps.asMap(languages, input -> formLabel))
              .addItems(formItem.getItems())
              .addClassnames(formItem.getClassName())
              .setProps(formItem.getProps())
              .build();
          }

          @Override
          public void visitGroup(@NonNull FormItem formItem) {
            builder.addGroup(formItem.getId())
              .setView(formItem.getView())
              .setLabel(formItem.getLabel())
              .setDescription(formItem.getDescription())
              .setActiveWhen(formItem.getActiveWhen())
              .addItems(formItem.getItems())
              .addClassnames(formItem.getClassName())
              .setProps(formItem.getProps())
              .build();
          }

          @Override
          public void visitSurveyGroup(@NonNull FormItem formItem) {
            builder.addSurveyGroup(formItem.getId())
              .setView(formItem.getView())
              .setLabel(formItem.getLabel())
              .setDescription(formItem.getDescription())
              .setActiveWhen(formItem.getActiveWhen())
              .addItems(formItem.getItems())
              .addClassnames(formItem.getClassName())
              .setValueSet(formItem.getValueSetId())
              .setProps(formItem.getProps())
              .build();
          }

          @Override
          public void visitRowGroup(@NonNull FormItem formItem) {
            builder.addRowGroup(formItem.getId())
              .setView(formItem.getView())
              .setLabel(formItem.getLabel())
              .setDescription(formItem.getDescription())
              .setActiveWhen(formItem.getActiveWhen())
              .setCanAddRowWhen(formItem.getCanAddRowWhen())
              .setCanRemoveRowWhen(formItem.getCanRemoveRowWhen())
              .addItems(formItem.getItems())
              .addClassnames(formItem.getClassName())
              .setValueSet(formItem.getValueSetId())
              .setProps(formItem.getProps())
              .build();
          }

          @Override
          public void visitPage(@NonNull FormItem formItem) {
            builder.addPage(formItem.getId())
              .setView(formItem.getView())
              .setLabel(formItem.getLabel())
              .setDescription(formItem.getDescription())
              .setActiveWhen(formItem.getActiveWhen())
              .addItems(formItem.getItems())
              .addClassnames(formItem.getClassName())
              .setProps(formItem.getProps())
              .build();
          }

          @Override
          public void visitQuestion(@NonNull FormItem formItem) {
            QuestionBuilder questionBuilder = builder.addQuestion(formItem.getId())
              .setLabel(formItem.getLabel())
              .setDescription(formItem.getDescription())
              .setActiveWhen(formItem.getActiveWhen())
              .setType(formItem.getType())
              .setView(formItem.getView())
              .setDefaultValue(formItem.getDefaultValue())
              .setValueSet(formItem.getValueSetId())
              .setProps(formItem.getProps())
              .addClassnames(formItem.getClassName());

            if (StringUtils.isNotBlank(formItem.getRequired())) {
              questionBuilder.setRequiredWhen(formItem.getRequired());
            } else if (answersRequiredByDefault && !Constants.NOTE.equals(formItem.getType())) {
              questionBuilder.setRequired(true);
            }

            int i = 1;
            // TODO not good idea to use hardcoded filtering here
            if (!Constants.NOTE.equals(formItem.getType())) {
              for (Validation validationBean : formItem.getValidations()) {
                questionBuilder.addValidation(formItem.getId() + "_error" + (i++))
                  .setActiveWhen(validationBean.getRule())
                  .setLabel(validationBean.getMessage())
                  .build();
              }
            }
            questionBuilder.build();
          }

          @Override
          public void visitNote(@NonNull FormItem formItem) {
            this.visitQuestion(formItem);
          }
        });
      }

      @NonNull
      @Override
      public Optional<FormValueSetVisitor> startValueSets() {
        return Optional.of(valueSet -> {
          ValueSetBuilder valueSetBuilder = builder.addValueSet(valueSet.getId());
          for (FormValueSetEntry entry : valueSet.getEntries()) {
            valueSetBuilder.addValue(entry.getId())
              .setActiveWhen(entry.getWhen())
              .setLabel(entry.getLabel())
              .build();
          }
          valueSetBuilder.build();
        });
      }

      @NonNull
      @Override
      public Optional<FormVariableVisitor> startFormVariables() {
        return Optional.of(formVariable -> builder.addVariable(formVariable.getName())
          .setContext(formVariable.getContext())
          .setPublished(formVariable.getPublished())
          .setValueExpression(formVariable.getExpression())
          .setDefaultValue(formVariable.getDefaultValue())
          .setType(formVariable.getContextType())
          .build());
      }
    });

    final Program program = builder.build();
    if (program != null && LOGGER.isDebugEnabled()) {
      LOGGER.debug(program.toString());
    }
    final List<FormValidationError> builderErrors = builder.getErrors();
    if (!builderErrors.isEmpty() || program == null) {
      throw new DialobProgramErrorsException("Could not compile program due errors.", builderErrors);
    }
    return DialobProgram.createDialobProgram(program);
  }
}
