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

import edu.umd.cs.findbugs.annotations.NonNull;
import io.dialob.api.form.Form;
import io.dialob.api.form.FormValidationError;
import io.dialob.form.service.api.validation.FormValidator;
import io.dialob.session.engine.DialobProgramErrorsException;
import io.dialob.session.engine.DialobProgramFromFormCompiler;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class DialobFormValidator implements FormValidator {

  private final DialobProgramFromFormCompiler programFromFormCompiler;

  public DialobFormValidator(@NonNull DialobProgramFromFormCompiler programFromFormCompiler) {
    this.programFromFormCompiler = programFromFormCompiler;
  }

  @Override
  @NonNull
  public List<FormValidationError> validate(@NonNull Form form) {
    final List<FormValidationError> result = new ArrayList<>();
    try {
      programFromFormCompiler.compileForm(form);
    } catch (DialobProgramErrorsException e) {
      final List<FormValidationError> errors = e.getErrors();
      result.addAll(errors);
      if (LOGGER.isDebugEnabled()) {
        String sb = "Form validation errors for: " + form.getId() + "\n  " +
          errors.stream().map(error -> error.getItemId() + ": " + error.getMessage()).collect(Collectors.joining("\n  "));
        LOGGER.debug(sb);
      }
    }
    return result;
  }

}
