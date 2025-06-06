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

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

public class AsyncFunctionDependency implements Serializable {

  @Getter
  private final String functionRefId;
  private final List<String> argumentExpressions;
  @Getter
  private final String functionName;
  @Getter
  private final String canonicalFunctionName;
  @Getter
  private ValueType valueType;

  public AsyncFunctionDependency(String functionRefId, String canonicalFunctionName, ValueType valueType, String functionName, List<String> argumentExpressions) {
    if (StringUtils.isBlank(functionRefId)) {
      throw new IllegalArgumentException("functionRefId may not be blank.");
    }
    this.functionRefId = functionRefId;
    this.functionName = functionName;
    this.valueType = valueType;
    this.argumentExpressions = argumentExpressions;
    this.canonicalFunctionName = canonicalFunctionName;
  }

  public List<String> getArgumentExpressions() {
    return Collections.unmodifiableList(argumentExpressions);
  }

  @Override
  public int hashCode() {
    return functionRefId.hashCode();
  }

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
