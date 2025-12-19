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

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

import java.util.Optional;

public interface VariableFinder {

  @Nullable
  String getScope();

  @Nullable
  ValueType typeOf(String variableName) throws VariableNotDefinedException;

  @Nullable
  ValueType returnTypeOf(String functionName, ValueType... argTypes) throws VariableNotDefinedException;

  boolean isAsync(String functionName);

  /**
   * Maps context specific alias id to real id
   *
   * @param alias
   * @return id of actual variable
   */
  @NonNull
  default String mapAlias(String alias) {
    return alias;
  }

  /**
   * @param variableName
   * @return contextId of variable, and null if global
   */
  default Optional<String> findVariableScope(String variableName) {
    return Optional.empty();
  }

}
