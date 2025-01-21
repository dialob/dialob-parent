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
import io.dialob.api.form.FormItem;
import io.dialob.api.form.FormValidationError;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.function.UnaryOperator;

/**
 * Interface providing methods to handle the renaming of form identifiers.
 * It includes functionality to validate renaming operations, adjust attributes
 * inside form items, and implement the renaming process on a form and its related identifiers.
 */
public interface FormIdRenamer {

  /**
   * Validates the renaming of a form identifier within a given form document.
   * Ensures that the renaming operation does not result in conflicts or invalid references.
   *
   * @param formDocument the form document to be validated
   * @param oldId the current identifier within the form to be renamed
   * @param newId the new identifier that will replace the old one
   * @return a list of validation errors encountered during the renaming process
   */
  List<FormValidationError> validateRename(@NonNull Form formDocument, @NonNull String oldId, @NonNull String newId);

  /**
   * Renames attributes of a given form item by applying an identifier renaming function
   * and updating specific references to old and new identifiers. This operation ensures
   * that the identifiers within the form item are transformed and consistent.
   *
   * @param item the form item whose attributes are to be renamed
   * @param idRenamer a unary operator function responsible for renaming identifiers
   * @param oldId the original identifier to be replaced
   * @param newId the new identifier that replaces the old one
   * @return a new FormItem instance with updated attributes reflecting the renaming operation
   */
  FormItem renameAttributes(@NonNull FormItem item, @NonNull UnaryOperator<String> idRenamer, @NonNull String oldId, @NonNull String newId);

  /**
   * Renames form identifiers within a specific form by replacing occurrences
   * of the old identifier with the new identifier. This process includes updating
   * any references to the identifier and validating the changes to ensure the integrity
   * of the form structure.
   *
   * The method returns a pair where the first element is the updated form with the
   * renamed identifiers, and the second element is a list of validation errors
   * encountered during the renaming process.
   *
   * @param form the original form containing the identifier to be renamed
   * @param oldId the identifier in the form to be replaced
   * @param newId the new identifier that will substitute the old one
   * @return a pair consisting of the updated form and a list of validation errors
   */
  Pair<Form, List<FormValidationError>> renameIdentifiers(@NonNull Form form, @NonNull String oldId, @NonNull String newId);
}
