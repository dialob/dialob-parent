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
import io.dialob.api.form.*;
import io.dialob.form.service.api.validation.FormValidator;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class ValueSetValidator implements FormValidator {

  private List<FormValidationError> checkValueSet(FormValueSet valueSet) {
    List<FormValidationError> result = new ArrayList<>();

    // Warn about empty valueset
    if (valueSet.getEntries().isEmpty()) {
      result.add(
        ImmutableFormValidationError.builder()
          .type(FormValidationError.Type.VALUESET)
          .level(FormValidationError.Level.WARNING)
          .message("VALUESET_EMPTY")
          .itemId(valueSet.getId())
          .build()
      );
      return result;
    }

    // Check duplicates
    valueSet.getEntries().stream()
      .collect(Collectors.groupingBy(FormValueSetEntry::getId, Collectors.toList()))
      .values().stream().filter(l -> l.size() > 1)
      .flatMap(List::stream)
      .map(e -> ImmutableFormValidationError.builder()
                .type(FormValidationError.Type.VALUESET)
                .level(FormValidationError.Level.ERROR)
                .message("VALUESET_DUPLICATE_KEY")
                .itemId(valueSet.getId())
                .expression(e.getId())
                .index(valueSet.getEntries().indexOf(e))
                .build())
      .forEach(result::add);

    // Check empties
    valueSet.getEntries().stream()
      .filter(e -> StringUtils.isBlank(e.getId()))
      .map(e -> ImmutableFormValidationError.builder()
        .type(FormValidationError.Type.VALUESET)
        .level(FormValidationError.Level.ERROR)
        .message("VALUESET_EMPTY_KEY")
        .itemId(valueSet.getId())
        .index(valueSet.getEntries().indexOf(e))
        .build())
      .forEach(result::add);

    return result;
  }

  @NonNull
  @Override
  public List<FormValidationError> validate(Form form) {
    return form.getValueSets().stream().map(this::checkValueSet).flatMap(List::stream).collect(Collectors.toList());
  }
}
