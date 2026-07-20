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
package io.dialob.session.engine.program.expr;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import io.dialob.rule.parser.ParserUtil;
import io.dialob.rule.parser.api.CompilerErrorCode;
import io.dialob.rule.parser.api.ValueType;
import io.dialob.session.engine.program.ProgramBuilder;
import io.dialob.session.engine.program.expr.arith.*;
import io.dialob.session.engine.program.model.Expression;
import io.dialob.session.engine.session.model.ItemId;

import java.util.List;
import java.util.function.BinaryOperator;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class DDRLOperatorFactory implements OperatorFactory {

  @Nullable
  private final ProgramBuilder programBuilder;

  public DDRLOperatorFactory() {
    this(null);
  }

  public DDRLOperatorFactory(@Nullable ProgramBuilder programBuilder) {
    this.programBuilder = programBuilder;
  }

  private static final DecimalOperators DECIMAL_OPERATORS = new DecimalOperators();
  private static final NumberOperators NUMBER_OPERATORS = new NumberOperators();
  private static final DateOperators DATE_OPERATORS = new DateOperators();
  private static final TimeOperators TIME_OPERATORS = new TimeOperators();
  private static final StringOperators STRING_OPERATORS = new StringOperators();
  private static final PeriodOperators PERIOD_OPERATORS = new PeriodOperators();
  private static final DurationOperators DURATION_OPERATORS = new DurationOperators();
  private static final BooleanOperators BOOLEAN_OPERATORS = new BooleanOperators();

  @NonNull
  private Operators operatorsOf(ValueType valueType) {
    if (valueType == ValueType.STRING) {
      return STRING_OPERATORS;
    }
    if (valueType == ValueType.DECIMAL) {
      return DECIMAL_OPERATORS;
    }
    if (valueType == ValueType.INTEGER) {
      return NUMBER_OPERATORS;
    }
    if (valueType == ValueType.TIME) {
      return TIME_OPERATORS;
    }
    if (valueType == ValueType.DATE) {
      return DATE_OPERATORS;
    }
    if (valueType == ValueType.PERIOD) {
      return PERIOD_OPERATORS;
    }
    if (valueType == ValueType.DURATION) {
      return DURATION_OPERATORS;
    }
    if (valueType == ValueType.BOOLEAN) {
      return BOOLEAN_OPERATORS;
    }
    throw new RuntimeException("Unknown type " + valueType);
  }

  @Override
  @NonNull
  public Expression createOperator(@NonNull ValueType nodeValueType, @NonNull String operator, @NonNull List<Expression> arguments) {
    Expression expr;
    if (ParserUtil.isFormatFunction(operator)) {
      return createFormatOperator(arguments);
    }
    final OperatorSymbol operatorSymbol = OperatorSymbol.mapOp(operator);
    if (operatorSymbol == null) {
      return createFunctionInvocation(nodeValueType, operator, arguments);
    }
    if (arguments.size() == 2) {
      Expression lhs = lhs(arguments);
      Expression rhs = rhs(arguments);
      Expression op = TimeOperators.createOperator(operatorSymbol, lhs, rhs);
      if (op != null) {
        return op;
      }
    }
    switch(operatorSymbol) {
      case PLUS:
        return new io.dialob.session.engine.program.expr.arith.BinaryOperator.Builder<>().addAllNodes(coerceToType(nodeValueType, arguments)).reducer((Reducer<Object>) Reducers.ofType(nodeValueType).add()).build();
      case MINUS:
        return new io.dialob.session.engine.program.expr.arith.BinaryOperator.Builder<>().addAllNodes(coerceToType(nodeValueType, arguments)).reducer((Reducer<Object>) Reducers.ofType(nodeValueType).sub()).build();
      case MULT:
        return new io.dialob.session.engine.program.expr.arith.BinaryOperator.Builder<>().addAllNodes(coerceToType(nodeValueType, arguments)).reducer((Reducer<Object>) Reducers.ofType(nodeValueType).mult()).build();
      case DIV:
        return new io.dialob.session.engine.program.expr.arith.BinaryOperator.Builder<>().addAllNodes(coerceToType(nodeValueType, arguments)).reducer((Reducer<Object>) Reducers.ofType(nodeValueType).div()).build();
      case NEG:
        if (nodeValueType == ValueType.DECIMAL) {
          return new NegOperatorDecimal.Builder().expression(unaryArg(arguments)).build();
        }
        if (nodeValueType == ValueType.INTEGER) {
          return new NegOperatorNumber.Builder().expression(unaryArg(arguments)).build();
        }
        throw new CannotNegateTypeException(nodeValueType);
      case NOT:
        assert arguments.size() == 1;
        assert nodeValueType == ValueType.BOOLEAN;
        expr = unaryArg(arguments);
        break;
      case AND:
        return new io.dialob.session.engine.program.expr.arith.BinaryOperator.Builder<Boolean>().addAllNodes(coerceToType(ValueType.BOOLEAN, arguments)).reducer(Reducers.Bool.AND).build();
      case OR:
        return new io.dialob.session.engine.program.expr.arith.BinaryOperator.Builder<Boolean>().addAllNodes(coerceToType(ValueType.BOOLEAN, arguments)).reducer(Reducers.Bool.OR).build();
      case NE, EQ, LT, LE, GE, GT:
        return relationOf(operatorSymbol, lhs(arguments), rhs(arguments));

      case NOT_IN, IN:
        expr = new InOperator.Builder().lhs(first(arguments)).rhs(new ExpressionList.Builder().addAllExpressions(rest(arguments)).build()).build();
        break;

      case NOT_MATCHES, MATCHES:
        Expression patternExpr = rhs(arguments);
        validateRegexExpression(patternExpr);
        expr = new MatchesOperator.Builder().lhs(lhs(arguments)).rhs(patternExpr).build();
        break;

      case NOT_ANSWERED, ANSWERED:
        expr = Operators.isAnswered(varRef(arguments));
        break;

      case NOT_BLANK, BLANK:
        expr = Operators.isBlank(varRef(arguments));
        break;

      case NOT_NULL, NULL:
        expr = Operators.isNull(varRef(arguments));
        break;

      case COUNT:
        expr = new CountArrayLengthOperator.Builder().itemId(varRef(arguments)).build();
        break;

      case NOT_VALID, VALID:
        expr = IsValidOperator.of(varRef(arguments));
        break;
      case SUM, MIN, MAX, ALL, ANY:
        expr = createArrayReducingOperator(operatorSymbol, nodeValueType, varRef(arguments));
        break;
      default:
        throw new IllegalStateException("Cannot handle operator " + operatorSymbol);
    }
    if (operatorSymbol.isNot()) {
      return new NotOperator.Builder().expression(expr).build();
    }
    return expr;

  }

  private Expression createArrayReducingOperator(OperatorSymbol operatorSymbol, ValueType itemValueType, ItemId varRef) {
    BinaryOperator<?> reducer = switch (operatorSymbol) {
      case SUM -> ArrayReducerOperator.sumOp(itemValueType);
      case MIN -> ArrayReducerOperator.minOp(itemValueType);
      case MAX -> ArrayReducerOperator.maxOp(itemValueType);
      case ALL -> ArrayReducerOperator.allOp(itemValueType);
      case ANY -> ArrayReducerOperator.anyOp(itemValueType);
      default -> null;
    };

    if (reducer == null) {
      throw new CannotReduceTypeWithOperatorException(operatorSymbol.name(), itemValueType);
    }

    return ArrayReducerOperator.of(
      reducer,
      CollectRowFieldsOperator.of(varRef, itemValueType));
  }

  protected Expression validateRegexExpression(Expression patternExpr) {
    if (patternExpr instanceof Constant) {
      Constant<String> constant = (Constant<String>) patternExpr;
      try {
        Pattern.compile(constant.value());
      } catch (PatternSyntaxException pse) {
        throw new MatcherRegexErrorException(CompilerErrorCode.MATCHER_REGEX_SYNTAX_ERROR, constant.value());
      }
    } else {
      // TODO Reject dynamic regex for now.
      throw new MatcherRegexErrorException(CompilerErrorCode.MATCHER_DYNAMIC_REGEX, null);
    }
    return patternExpr;
  }

  @NonNull
  private Expression createFormatOperator(@NonNull List<Expression> arguments) {
    if (arguments.size() != 1 || !(arguments.getFirst() instanceof Constant<?> constant) || constant.getValueType() != ValueType.STRING) {
      throw new FormatExpressionException(CompilerErrorCode.FORMAT_ARGUMENT_MUST_BE_CONSTANT);
    }
    if (programBuilder == null) {
      throw new FormatExpressionException(CompilerErrorCode.COMPILER_ERROR);
    }
    String template = (String) constant.value();
    if (template == null) {
      return Constant.builder().value("").valueType(ValueType.STRING).build();
    }
    return LocalizedLabelOperator.createFormatExpression(programBuilder, template);
  }

  @NonNull
  private Expression createFunctionInvocation(@NonNull ValueType nodeValueType, @NonNull String operator, @NonNull List<Expression> arguments) {
    return new FunctionCallOperator.Builder()
      .valueType(nodeValueType)
      .addAllArgs(arguments)
      .functionName(operator)
      .build();
  }

  private Iterable<? extends Expression> coerceToType(ValueType nodeValueType, List<Expression> arguments) {
    return arguments
      .stream()
      .map(argument -> coerceToType(nodeValueType, argument))
      .toList();
  }

  private Expression coerceToType(ValueType nodeValueType, Expression argument) {
    if (nodeValueType == argument.getValueType()) {
      return argument;
    }
    if (nodeValueType == ValueType.DECIMAL) {
      return new CoerceToDecimalOperator.Builder().expression(argument).build();
    }
    throw new CannotCoerceTypeException(argument.getValueType(), nodeValueType);
  }

  private Expression first(List<Expression> expressions) {
    assert !expressions.isEmpty();
    return expressions.getFirst();
  }

  private List<Expression> rest(List<Expression> expressions) {
    return expressions.stream().skip(1).toList();
  }

  private Expression relationOf(OperatorSymbol operator, Expression lhs, Expression rhs) {
    ValueType leftValueType = lhs.getValueType();
    ValueType rightValueType = rhs.getValueType();
    ValueType coercedType = leftValueType;
    if (leftValueType != rightValueType) {
      coercedType = resolveCoersionTarget(leftValueType, rightValueType);
      lhs = coerceToType(coercedType, lhs);
      rhs = coerceToType(coercedType, rhs);
    }
    final Operators operators = operatorsOf(coercedType);
    return switch (operator) {
      case NE -> operators.ne(lhs, rhs);
      case EQ -> operators.eq(lhs, rhs);
      case LT -> operators.lt(lhs, rhs);
      case LE -> operators.le(lhs, rhs);
      case GE -> operators.ge(lhs, rhs);
      case GT -> operators.gt(lhs, rhs);
      default -> throw new RuntimeException("Unknown operator " + operator);
    };
  }

  @NonNull
  private ValueType resolveCoersionTarget(ValueType leftValueType, ValueType rightValueType) {
    if (leftValueType.canOrderWith(rightValueType)) {
      return leftValueType.minusType(rightValueType);
    }
    if (leftValueType.canEqualWith(rightValueType)) {
      return leftValueType;
    }
    if (rightValueType.canEqualWith(leftValueType)) {
      return rightValueType;
    }
    throw new TypesDoNotHaveRelationException("NO_RELATION", leftValueType , rightValueType);
  }

  private Expression unaryArg(List<Expression> arguments) {
    assert arguments.size() == 1;
    return arguments.getFirst();
  }

  private ItemId varRef(List<Expression> arguments) {
    assert arguments.size() == 1;
    Expression expression = arguments.getFirst();
    assert expression instanceof VariableReference;
    VariableReference variableReference = (VariableReference) expression;
    return variableReference.itemId();
  }

  private Expression rhs(List<Expression> arguments) {
    assert arguments.size() == 2;
    return arguments.get(1);
  }

  private Expression lhs(List<Expression> arguments) {
    assert arguments.size() == 2;
    return arguments.getFirst();
  }

}
