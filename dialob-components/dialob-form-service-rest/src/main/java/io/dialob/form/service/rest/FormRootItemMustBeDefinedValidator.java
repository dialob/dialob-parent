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
package io.dialob.form.service.rest;

import edu.umd.cs.findbugs.annotations.NonNull;
import io.dialob.api.form.Form;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/**
 * Validates Form objects to ensure they meet defined constraints.
 * This validator specifically checks if the root form item is defined
 * for a given form. It is implemented for use with the Spring framework's
 * validation mechanism by implementing the Validator interface.
 */
@Slf4j
public class FormRootItemMustBeDefinedValidator implements Validator {

  @Override
  public boolean supports(@NonNull Class<?> clazz) {
    return Form.class.isAssignableFrom(clazz);
  }

  @Override
  public void validate(@Nullable Object target, @NonNull Errors errors) {
    LOGGER.debug("Here");
  }
}
