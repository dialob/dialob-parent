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

import java.util.Collection;
import java.util.List;

public interface VariableExpressionCompilerCallback {

  void failed(List<RuleExpressionCompilerError> errors);

  void start(String variableRuleName);

  void whenConstraints(String constraints) throws VariableNotDefinedException;

  void thenExpression(String expression) throws VariableNotDefinedException;

  void expressionResultType(ValueType valueType);

  void asyncFunctionExpressionDependencies(Collection<AsyncFunctionDependency> asyncFunctionDependencySet);

}
