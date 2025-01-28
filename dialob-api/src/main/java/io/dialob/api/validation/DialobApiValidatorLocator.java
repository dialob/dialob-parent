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
package io.dialob.api.validation;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;
import jakarta.validation.Validator;

import java.util.ServiceLoader;
import java.util.Set;

class DialobApiValidatorLocator {

  private DialobApiValidatorLocator() {}

  private static DialobApiValidator validator;

  private static DialobApiValidatorFactory validationApiDialobApiValidatorFactory = () -> instance -> {
    Validator v = Validation.buildDefaultValidatorFactory().getValidator();
    Set<ConstraintViolation<Object>> constraintViolations = v.validate(instance);
    if (!constraintViolations.isEmpty()) {
      throw new ConstraintViolationException(constraintViolations);
    }
    return instance;
  };

  public static synchronized DialobApiValidator locate() {
    if (validator != null) {
      return validator;
    }
    ServiceLoader<DialobApiValidatorFactory> loader = ServiceLoader.load(DialobApiValidatorFactory.class);
    DialobApiValidatorFactory factory = validationApiDialobApiValidatorFactory;
    for (DialobApiValidatorFactory dialobApiValidatorFactory : loader) {
      factory = dialobApiValidatorFactory;
    }
    return factory.create();
  }

  public static DialobApiValidator getValidator() {
    if (validator == null) {
      validator = locate();
    }
    return validator;
  }
}
