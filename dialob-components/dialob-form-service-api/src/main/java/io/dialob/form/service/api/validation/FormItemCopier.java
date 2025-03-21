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

import io.dialob.api.form.Form;
import io.dialob.api.form.FormValidationError;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

/**
 * The FormItemCopier interface provides functionality to copy a form item from a specified form.
 * This process involves duplicating the selected form item while maintaining the integrity of the
 * form structure and producing validation errors, if any, associated with the copying process.
 */
public interface FormItemCopier {
  /**
   * Copies a form item identified by the specified ID from the given form. The method
   * duplicates the form item while maintaining the overall structure and integrity of the form.
   * Additionally, it detects and returns any validation errors related to the copying process.
   *
   * @param form the original form from which the item is to be copied
   * @param idToCopy the ID of the form item that should be copied
   * @return a pair consisting of the updated form containing the copied item and
   *         a list of any validation errors encountered during the copying process
   */
  Pair<Form, List<FormValidationError>> copyFormItem(Form form, String idToCopy);
}
