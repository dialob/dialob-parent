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

import io.dialob.api.form.Form;
import io.dialob.api.form.FormValidationError;
import io.dialob.api.form.FormValueSet;
import io.dialob.api.form.FormValueSetEntry;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

class ValueSetValidatorTest {
  private final ValueSetValidator valueSetValidator = new ValueSetValidator();
  private final Form baseForm = new Form.Builder()
      .name("test")
      .metadata(new Form.Metadata.Builder()
        .addLanguages("en")
        .label("TestForm")
        .build()
      ).build();

  @Test
  void shouldWarnOnEmptySet() {
    Form testForm = new Form.Builder()
      .from(baseForm)
      .addValueSets(
        new FormValueSet.Builder()
          .id("emptySetId")
          .build()
      ).build();

    List<FormValidationError> errors = valueSetValidator.validate(testForm);

    assertThat(errors).extracting("type", "level", "message", "itemId").containsOnly(
      tuple(FormValidationError.Type.VALUESET, FormValidationError.Level.WARNING, "VALUESET_EMPTY", "emptySetId")
    );

  }

  @Test
  void shouldErrorOnDuplicateKeys() {
    Form testForm = new Form.Builder()
      .from(baseForm)
      .addValueSets(
        new FormValueSet.Builder()
          .id("setId")
          .addEntries(
            new FormValueSetEntry.Builder()
              .id("a")
              .putLabel("en", "One")
              .build(),
            new FormValueSetEntry.Builder()
              .id("a")
              .putLabel("en", "Two")
              .build(),
            new FormValueSetEntry.Builder()
              .id("b")
              .putLabel("en", "Three")
              .build()
          ).build()
      ).build();

    List<FormValidationError> errors = valueSetValidator.validate(testForm);

    assertThat(errors).extracting("type", "level", "message", "itemId", "expression", "index").containsOnly(
      tuple(FormValidationError.Type.VALUESET, FormValidationError.Level.ERROR, "VALUESET_DUPLICATE_KEY", "setId", Optional.of("a"), Optional.of(0)),
      tuple(FormValidationError.Type.VALUESET, FormValidationError.Level.ERROR, "VALUESET_DUPLICATE_KEY", "setId", Optional.of("a"), Optional.of(1))
    );

  }

  @Test
  void shouldPassValidSet() {
    Form testForm = new Form.Builder()
      .from(baseForm)
      .addValueSets(
        new FormValueSet.Builder()
          .id("setId")
          .addEntries(
            new FormValueSetEntry.Builder()
              .id("a")
              .putLabel("en", "One")
              .build(),
            new FormValueSetEntry.Builder()
              .id("b")
              .putLabel("en", "Two")
              .build(),
            new FormValueSetEntry.Builder()
              .id("c")
              .putLabel("en", "Three")
              .build()
          ).build()
      ).build();

    List<FormValidationError> errors = valueSetValidator.validate(testForm);

    Assertions.assertEquals(0, errors.size());
  }

  @Test
  void shouldErrorOnEmptyKey() {
    Form testForm = new Form.Builder()
      .from(baseForm)
      .addValueSets(
        new FormValueSet.Builder()
          .id("setId")
          .addEntries(
            new FormValueSetEntry.Builder()
              .id("a")
              .putLabel("en", "One")
              .build(),
            new FormValueSetEntry.Builder()
              .id("")
              .putLabel("en", "Two")
              .build(),
            new FormValueSetEntry.Builder()
              .id("c")
              .putLabel("en", "Three")
              .build()
          ).build()
      ).build();

    List<FormValidationError> errors = valueSetValidator.validate(testForm);

    assertThat(errors).extracting("type", "level", "message", "itemId", "index").containsOnly(
      tuple(FormValidationError.Type.VALUESET, FormValidationError.Level.ERROR, "VALUESET_EMPTY_KEY", "setId", Optional.of(1))
    );
  }
}
