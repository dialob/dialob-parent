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
package io.dialob.form.service.api.validation;

import edu.umd.cs.findbugs.annotations.NonNull;
import io.dialob.api.form.Form;
import io.dialob.api.form.FormValidationError;

import java.util.List;

/**
 * Interface for validating forms which ensures the provided form adheres
 * to required structure and rules. Implementations define specific validation
 * logic to detect errors, warnings, or inconsistencies in the form's contents.
 */
public interface FormValidator {

  /**
   * Validates the specified form and returns a list of validation errors found within it.
   * The validation process ensures that the form follows the expected structure, rules,
   * and requirements. Each validation error provides details about the nature of the issue.
   *
   * @param form the form to be validated; must not be null
   * @return a list of validation errors identified in the form; never null but may be empty if no errors are found
   */
  @NonNull
  List<FormValidationError> validate(@NonNull Form form);

}
