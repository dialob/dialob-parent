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
package io.dialob.rule.parser.api;

interface BaseValueType {
  /**
   * Returns the Java class type associated with the specific value type.
   *
   * @return the {@link Class} object that represents the type corresponding to this value type
   */
  Class<?> getTypeClass();

  /**
   * Determines whether the current value type is an array.
   *
   * @return true if the value type is an array; false otherwise
   */
  default boolean isArray() {
    return false;
  }

  /**
   * Attempts to coerce the given value to the type represented by the current value type.
   * If the provided value's class is assignable to the type class of this value type,
   * it is returned as-is. Otherwise, null is returned.
   *
   * @param value the object to be coerced; must not be null
   * @return the coerced object if the type is compatible, or null if the coercion fails
   */
  default Object coerceFrom(Object value) {
    if (getTypeClass().isAssignableFrom(value.getClass())) {
      return value;
    }
    return null;
  }

  /**
   * Retrieves the {@link ValueType} of an individual item within an array type.
   * This method is applicable when the current value type represents an array.
   *
   * @return the {@link ValueType} of an individual item within the array
   * @throws IllegalStateException if the current value type is not an array
   */
  default ValueType getItemValueType() {
    throw new IllegalStateException("Not an array value type");
  }

}
