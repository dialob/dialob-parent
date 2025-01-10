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

import org.immutables.value.Value;

@Value.Style(validationMethod = Value.Style.ValidationMethod.NONE)
public interface WithValidation<T extends WithValidation<T>> {

  static <T extends WithValidation> T validate(T instance) {
    return (T) DialobApiValidatorLocator.getValidator().validate(instance);
  }

  @Value.Check
  default T validate() {
    return (T) validate(this);
  }
}
