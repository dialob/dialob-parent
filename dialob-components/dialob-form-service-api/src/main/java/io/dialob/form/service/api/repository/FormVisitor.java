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
package io.dialob.form.service.api.repository;

import edu.umd.cs.findbugs.annotations.NonNull;
import io.dialob.api.form.Form;

import java.util.Optional;

public interface FormVisitor extends Visitor {

  default void visitForm(@NonNull Form formDocument) { }

  default void visitFormMetadata(@NonNull Form.Metadata metadata) { }

  default Optional<FormItemVisitor> startFormItems() {
    return Optional.empty();
  }

  default void endFormItems() {
  }

  default Optional<FormVariableVisitor> startFormVariables() {
    return Optional.empty();
  }

  default void endFormVariables() {
  }

  default Optional<FormValueSetVisitor> startValueSets() {
    return Optional.empty();
  }

  default void endValueSets() {}

}
