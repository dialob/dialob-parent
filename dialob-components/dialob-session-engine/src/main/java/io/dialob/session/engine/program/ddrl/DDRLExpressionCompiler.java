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
package io.dialob.session.engine.program.ddrl;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import io.dialob.rule.parser.api.RuleExpressionCompilerError;
import io.dialob.rule.parser.api.VariableFinder;
import io.dialob.rule.parser.node.*;
import io.dialob.session.engine.program.ProgramBuilderException;
import io.dialob.session.engine.program.expr.OperatorFactory;
import io.dialob.session.engine.program.expr.arith.*;
import io.dialob.session.engine.program.model.Expression;
import io.dialob.session.engine.session.model.IdUtils;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static io.dialob.rule.parser.Expression.createExpression;
import static java.util.Objects.requireNonNull;

public class DDRLExpressionCompiler {

  private final OperatorFactory operatorFactory;

  private final Map<NodeBase,String> asyncFunctionVariables;

  public DDRLExpressionCompiler(@NonNull OperatorFactory operatorFactory) {
    this.operatorFactory = requireNonNull(operatorFactory);
    this.asyncFunctionVariables = new HashMap<>();
  }

  @NonNull
  public <T> Optional<Expression> compile(
    @NonNull VariableFinder variableFinder,
    @NonNull String expressionString,
    @NonNull Consumer<RuleExpressionCompilerError> errorConsumer)
  {
    final io.dialob.rule.parser.Expression expression = createExpression(variableFinder, asyncFunctionVariables, expressionString);
    expression.getErrors().forEach(errorConsumer);
    final NodeBase ast = expression.getAst();
    if (ast == null || !expression.getErrors().isEmpty()) {
      return Optional.empty();
    }
    try {
      return Optional.of(convertToImmutableExpression(ast));
    } catch (ProgramBuilderException e) {
      errorConsumer.accept(
        RuleExpressionCompilerError.of(e.getMessage(),
          e.getNode().getSpan(),
          e.getArgs()
        )
      );
    }
    return Optional.empty();
  }

  @NonNull
  private Expression convertToImmutableExpression(@NonNull NodeBase ast) {
    ASTVisitorBuilder visitorBuilder = new ASTVisitorBuilder();
    ast.accept(visitorBuilder);
    final List<Expression> expressions = visitorBuilder.getExpressions();
    assert expressions.size() == 1;
    return expressions.getFirst();
  }

  public Map<String, Expression> getAsyncFunctionVariableExpressions() {
    return asyncFunctionVariables.entrySet().stream().collect(Collectors.toMap(Map.Entry::getValue,
      nodeBaseStringEntry -> convertToImmutableExpression(nodeBaseStringEntry.getKey())));
  }

  private class ASTVisitorBuilder implements ASTVisitor {

    private final List<Expression> expressions = new ArrayList<>();

    private ASTVisitorBuilder subScopeBuilder;

    @NonNull
    public List<Expression> getExpressions() {
      return expressions;
    }

    private ASTVisitorBuilder enterSubScope() {
      this.subScopeBuilder = new ASTVisitorBuilder();
      return this.subScopeBuilder;
    }

    @Override
    public ASTVisitor visitCallExpr(@NonNull CallExprNode node) {
      return enterSubScope();
    }

    @Override
    @NonNull
    public NodeBase endCallExpr(@NonNull CallExprNode node) {
      try {
        final Expression operator = operatorFactory.createOperator(
          requireNonNull(node.getValueType()),
          node.getNodeOperator().operator(),
          this.subScopeBuilder.getExpressions());
        this.expressions.add(operator);
      } catch (ProgramBuilderException e) {
        e.setNode(node);
        throw e;
      }
      this.subScopeBuilder = null;
      return node;
    }

    @Override
    @NonNull
    public NodeBase visitConstExpr(@NonNull ConstExprNode node) {
      expressions.add(Constant.builder().valueType(requireNonNull(node.getValueType())).value(node.getAsValueType()).build());
      return node;
    }

    @NonNull
    @Override
    public NodeBase visitIdExpr(@NonNull IdExprNode node) {
      switch (node.getId()) {
        case "today":
          expressions.add(TimeOperators.today());
          break;
        case "now":
          expressions.add(TimeOperators.now());
          break;
        case "language":
          expressions.add(StringOperators.languageOperator());
          break;
        default:
          if (node.getValueType() == null) {
            throw new UnknownValueTypeException(node.getId());
          }
          expressions.add(Operators.var(IdUtils.toId(node.getScopeId(), node.getId()), node.getValueType()));
      }
      return node;
    }

    @Nullable
    @Override
    public ASTVisitor visitObjectExpr(@NonNull ObjectExprNode node) {
      return enterSubScope();
    }

    @NonNull
    @Override
    public ASTVisitor visitKeyValueExpr(@NonNull KeyValueExprNode node) {
      return enterSubScope();
    }

    @NonNull
    @Override
    public KeyValueExprNode endKeyValueExpr(@NonNull KeyValueExprNode node) {
      expressions.add(new KeyValueOperator(node.getKey(), this.subScopeBuilder.getExpressions().getFirst()));
      return node;
    }

    @NonNull
    @Override
    public NodeBase endObjectExpr(@NonNull ObjectExprNode node) {
      expressions.add(new ObjectOperator(subScopeBuilder.getExpressions()));
      return node;
    }
  }
}
