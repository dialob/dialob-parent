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
import io.dialob.rule.parser.Expression;
import io.dialob.rule.parser.analyze.ValidateExpressionVisitor;
import io.dialob.rule.parser.api.RuleExpressionCompiler;
import io.dialob.rule.parser.api.RuleExpressionCompilerCallback;
import io.dialob.rule.parser.api.VariableFinder;

import java.util.HashMap;
import java.util.function.UnaryOperator;

import static java.util.Objects.requireNonNull;
import static org.apache.commons.lang3.StringUtils.isBlank;

public class DialobRuleExpressionCompiler implements RuleExpressionCompiler {

  @Override
  public boolean compile(@NonNull String expression, @NonNull VariableFinder variableFinder, @NonNull RuleExpressionCompilerCallback compilationResultListener) {
    requireNonNull(expression, "expression may not be null");
    requireNonNull(variableFinder, "variableFinder may not be null");
    requireNonNull(compilationResultListener, "compilationResultListener may not be null");
    Expression expr = Expression.createExpression(variableFinder, new HashMap<>(), expression);
    if (!expr.getErrors().isEmpty()) {
      compilationResultListener.failed(expr.getErrors());
      return false;
    }

    final ValidateExpressionVisitor validateExpressionVisitor = new ValidateExpressionVisitor();
    expr.getAst().accept(validateExpressionVisitor);
    if (validateExpressionVisitor.hasErrors()) {
      compilationResultListener.failed(validateExpressionVisitor.getErrors());
      return false;
    }

    return true;
  }

  @Override
  @NonNull
  public UnaryOperator<String> createIdRenamer(final String oldId, final String newId) {
    if (isBlank(oldId) || isBlank(newId)) {
      throw new IllegalArgumentException("old or newid may not be empty");
    }
    if (oldId.equals(newId)) {
      return expression -> expression;
    }
    return expression -> {
      if (expression == null) {
        return null;
      }
      Expression parsedExpression = Expression.createExpression(expression);
      if (parsedExpression.getAst() == null) {
        // If expression is unparseable or blank, we'll return original expression.
        return expression;
      }
      return parsedExpression.renameId(oldId, newId).toString();
    };
  }
}
