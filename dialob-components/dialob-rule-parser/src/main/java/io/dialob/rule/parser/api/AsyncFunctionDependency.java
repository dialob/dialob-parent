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
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.List;

public record AsyncFunctionDependency(
  String functionRefId,
  List<String> argumentExpressions,
  String functionName,
  String canonicalFunctionName, ValueType valueType
) implements Serializable {

  public AsyncFunctionDependency {
    if (StringUtils.isBlank(functionRefId)) {
      throw new IllegalArgumentException("functionRefId may not be blank.");
    }
    argumentExpressions = List.copyOf(argumentExpressions);
  }

  @Override
  public int hashCode() {
    return functionRefId.hashCode();
  }

  @NonNull
  @Override
  public String toString() {
    return functionRefId;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (obj instanceof AsyncFunctionDependency dependency) {
      return functionRefId.equals(dependency.functionRefId);
    }
    return false;
  }

}
