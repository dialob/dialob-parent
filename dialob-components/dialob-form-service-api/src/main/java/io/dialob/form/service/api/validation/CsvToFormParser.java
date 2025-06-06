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

/**
 * Interface for parsing CSV input into a `Form` representation.
 * Implementations are expected to read the CSV content, validate the structure,
 * map the entries to a `Form` object, and provide meaningful error handling for
 * invalid or improperly formatted input.
 */
public interface CsvToFormParser {
  /**
   * Parses the provided CSV string and converts it into a `Form` object representation.
   *
   * @param formCsv the input CSV content to be parsed; must not be null
   * @return the resulting `Form` object constructed from the CSV data
   */
  Form parseCsv(@NonNull String formCsv);
}
