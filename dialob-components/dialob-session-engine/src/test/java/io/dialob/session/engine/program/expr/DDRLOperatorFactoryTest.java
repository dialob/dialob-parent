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

import io.dialob.rule.parser.api.ValueType;
import io.dialob.session.engine.program.expr.arith.*;
import io.dialob.session.engine.program.model.Expression;
import io.dialob.session.engine.session.model.IdUtils;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DDRLOperatorFactoryTest {

  @Test
  void shouldCreateIsValidOperator() {
    DDRLOperatorFactory factory = new DDRLOperatorFactory();
    var op1 = factory.createOperator(ValueType.STRING, "isValid", List.of(VariableReference.of(IdUtils.toId("var1"), ValueType.STRING)));
    var op2 = factory.createOperator(ValueType.STRING, "isNotValid", List.of(VariableReference.of(IdUtils.toId("var1"), ValueType.STRING)));
    assertNotNull(op1);
    assertNotNull(op2);
    assertEquals(ValueType.BOOLEAN, op1.getValueType());
    assertEquals(ValueType.BOOLEAN, op2.getValueType());
  }

  @Test
  void shouldCreateInOperator() {
    DDRLOperatorFactory factory = new DDRLOperatorFactory();
    var op1 = factory.createOperator(ValueType.STRING, "in", List.of(VariableReference.of(IdUtils.toId("var1"), ValueType.STRING)));
    var op2 = factory.createOperator(ValueType.STRING, "notIn", List.of(VariableReference.of(IdUtils.toId("var1"), ValueType.STRING)));
    assertNotNull(op1);
    assertNotNull(op2);
    assertEquals(ValueType.BOOLEAN, op1.getValueType());
    assertEquals(ValueType.BOOLEAN, op2.getValueType());
    InfixOperator ifop1 = (InfixOperator) op1;
    NotOperator not = (NotOperator) op2;
    InfixOperator ifop2 = (InfixOperator) not.expression();

    assertEquals(ValueType.STRING, ifop1.getLhs().getValueType());
    assertEquals(ValueType.arrayOf(ValueType.STRING), ifop1.getRhs().getValueType());
    assertEquals(ValueType.STRING, ifop2.getLhs().getValueType());
    assertEquals(ValueType.arrayOf(ValueType.STRING), ifop2.getRhs().getValueType());

  }

  @Test
  void shouldCreateArithmeticOperators() {
    DDRLOperatorFactory factory = new DDRLOperatorFactory();
    Expression lhs = new Constant<>(1, ValueType.INTEGER);
    Expression rhs = new Constant<>(2, ValueType.INTEGER);
    List<Expression> args = List.of(lhs, rhs);

    Expression plus = factory.createOperator(ValueType.INTEGER, "+", args);
    assertInstanceOf(BinaryOperator.class, plus);
    assertEquals(ValueType.INTEGER, plus.getValueType());

    Expression minus = factory.createOperator(ValueType.INTEGER, "-", args);
    assertInstanceOf(BinaryOperator.class, minus);
    assertEquals(ValueType.INTEGER, minus.getValueType());

    Expression mult = factory.createOperator(ValueType.INTEGER, "*", args);
    assertInstanceOf(BinaryOperator.class, mult);
    assertEquals(ValueType.INTEGER, mult.getValueType());

    Expression div = factory.createOperator(ValueType.INTEGER, "/", args);
    assertInstanceOf(BinaryOperator.class, div);
    assertEquals(ValueType.INTEGER, div.getValueType());
  }

  @Test
  void shouldCreateNegationOperator() {
    DDRLOperatorFactory factory = new DDRLOperatorFactory();
    Expression arg = new Constant<>(1, ValueType.INTEGER);
    List<Expression> args = List.of(arg);

    Expression neg = factory.createOperator(ValueType.INTEGER, "neg", args);
    assertInstanceOf(NegOperatorNumber.class, neg);
    assertEquals(ValueType.INTEGER, neg.getValueType());

    Expression decimalArg = new Constant<>(1.0, ValueType.DECIMAL);
    Expression decimalNeg = factory.createOperator(ValueType.DECIMAL, "neg", List.of(decimalArg));
    assertInstanceOf(NegOperatorDecimal.class, decimalNeg);
    assertEquals(ValueType.DECIMAL, decimalNeg.getValueType());
  }

  @Test
  void shouldCreateBooleanOperators() {
    DDRLOperatorFactory factory = new DDRLOperatorFactory();
    Expression lhs = new Constant<>(true, ValueType.BOOLEAN);
    Expression rhs = new Constant<>(false, ValueType.BOOLEAN);
    List<Expression> args = List.of(lhs, rhs);

    Expression and = factory.createOperator(ValueType.BOOLEAN, "and", args);
    assertInstanceOf(BinaryOperator.class, and);
    assertEquals(ValueType.BOOLEAN, and.getValueType());

    Expression or = factory.createOperator(ValueType.BOOLEAN, "or", args);
    assertInstanceOf(BinaryOperator.class, or);
    assertEquals(ValueType.BOOLEAN, or.getValueType());

    Expression not = factory.createOperator(ValueType.BOOLEAN, "not", List.of(lhs));
    // Assuming unaryArg returns the expression itself for NOT if it's already boolean,
    // but DDRLOperatorFactory implementation for NOT seems to just return unaryArg(arguments)
    // which is the expression itself. Wait, DDRLOperatorFactory:
    // case NOT:
    //   assert arguments.size() == 1;
    //   assert nodeValueType == ValueType.BOOLEAN;
    //   expr = unaryArg(arguments);
    //   break;
    // And then:
    // if (operatorSymbol.isNot()) { return new NotOperator.Builder().expression(expr).build(); }
    // So it wraps it in NotOperator.
    assertInstanceOf(NotOperator.class, not);
    assertEquals(ValueType.BOOLEAN, not.getValueType());
  }

  @Test
  void shouldCreateRelationOperators() {
    DDRLOperatorFactory factory = new DDRLOperatorFactory();
    Expression lhs = new Constant<>(1, ValueType.INTEGER);
    Expression rhs = new Constant<>(2, ValueType.INTEGER);
    List<Expression> args = List.of(lhs, rhs);

    Expression eq = factory.createOperator(ValueType.BOOLEAN, "=", args);
    assertInstanceOf(EqOperator.class, eq);

    Expression ne = factory.createOperator(ValueType.BOOLEAN, "!=", args);
    assertInstanceOf(NeOperator.class, ne);

    Expression lt = factory.createOperator(ValueType.BOOLEAN, "<", args);
    assertInstanceOf(LtOperator.class, lt);

    Expression le = factory.createOperator(ValueType.BOOLEAN, "<=", args);
    assertInstanceOf(LeOperator.class, le);

    Expression gt = factory.createOperator(ValueType.BOOLEAN, ">", args);
    assertInstanceOf(GtOperator.class, gt);

    Expression ge = factory.createOperator(ValueType.BOOLEAN, ">=", args);
    assertInstanceOf(GeOperator.class, ge);
  }

  @Test
  void shouldCreateMatchesOperator() {
    DDRLOperatorFactory factory = new DDRLOperatorFactory();
    Expression lhs = new Constant<>("test", ValueType.STRING);
    Expression rhs = new Constant<>(".*", ValueType.STRING);
    List<Expression> args = List.of(lhs, rhs);

    Expression matches = factory.createOperator(ValueType.BOOLEAN, "matches", args);
    assertInstanceOf(MatchesOperator.class, matches);

    Expression notMatches = factory.createOperator(ValueType.BOOLEAN, "notMatches", args);
    assertInstanceOf(NotOperator.class, notMatches);
    assertInstanceOf(MatchesOperator.class, ((NotOperator) notMatches).expression());
  }

  @Test
  void shouldCreateIsAnsweredOperator() {
    DDRLOperatorFactory factory = new DDRLOperatorFactory();
    Expression arg = VariableReference.of(IdUtils.toId("var1"), ValueType.STRING);
    List<Expression> args = List.of(arg);

    Expression isAnswered = factory.createOperator(ValueType.BOOLEAN, "isAnswered", args);
    assertInstanceOf(IsAnsweredOperator.class, isAnswered);

    Expression isNotAnswered = factory.createOperator(ValueType.BOOLEAN, "isNotAnswered", args);
    assertInstanceOf(NotOperator.class, isNotAnswered);
    assertInstanceOf(IsAnsweredOperator.class, ((NotOperator) isNotAnswered).expression());
  }

  @Test
  void shouldCreateIsBlankOperator() {
    DDRLOperatorFactory factory = new DDRLOperatorFactory();
    Expression arg = VariableReference.of(IdUtils.toId("var1"), ValueType.STRING);
    List<Expression> args = List.of(arg);

    Expression isBlank = factory.createOperator(ValueType.BOOLEAN, "isBlank", args);
    assertInstanceOf(IsBlankOperator.class, isBlank);

    Expression isNotBlank = factory.createOperator(ValueType.BOOLEAN, "isNotBlank", args);
    assertInstanceOf(NotOperator.class, isNotBlank);
    assertInstanceOf(IsBlankOperator.class, ((NotOperator) isNotBlank).expression());
  }

  @Test
  void shouldCreateIsNullOperator() {
    DDRLOperatorFactory factory = new DDRLOperatorFactory();
    Expression arg = VariableReference.of(IdUtils.toId("var1"), ValueType.STRING);
    List<Expression> args = List.of(arg);

    Expression isNull = factory.createOperator(ValueType.BOOLEAN, "isNull", args);
    assertInstanceOf(IsNullOperator.class, isNull);

    Expression isNotNull = factory.createOperator(ValueType.BOOLEAN, "isNotNull", args);
    assertInstanceOf(NotOperator.class, isNotNull);
    assertInstanceOf(IsNullOperator.class, ((NotOperator) isNotNull).expression());
  }

  @Test
  void shouldCreateCountOperator() {
    DDRLOperatorFactory factory = new DDRLOperatorFactory();
    Expression arg = VariableReference.of(IdUtils.toId("var1"), ValueType.arrayOf(ValueType.STRING));
    List<Expression> args = List.of(arg);

    Expression count = factory.createOperator(ValueType.INTEGER, "count", args);
    assertInstanceOf(CountArrayLengthOperator.class, count);
  }

  @Test
  void shouldCreateArrayReducingOperators() {
    DDRLOperatorFactory factory = new DDRLOperatorFactory();
    Expression arg = VariableReference.of(IdUtils.toId("var1"), ValueType.arrayOf(ValueType.INTEGER));
    List<Expression> args = List.of(arg);

    Expression sum = factory.createOperator(ValueType.INTEGER, "sumOf", args);
    assertInstanceOf(ArrayReducerOperator.class, sum);

    Expression min = factory.createOperator(ValueType.INTEGER, "minOf", args);
    assertInstanceOf(ArrayReducerOperator.class, min);

    Expression max = factory.createOperator(ValueType.INTEGER, "maxOf", args);
    assertInstanceOf(ArrayReducerOperator.class, max);

    Expression argBool = VariableReference.of(IdUtils.toId("varBool"), ValueType.arrayOf(ValueType.BOOLEAN));
    List<Expression> argsBool = List.of(argBool);

    Expression all = factory.createOperator(ValueType.BOOLEAN, "allOf", argsBool);
    assertInstanceOf(ArrayReducerOperator.class, all);

    Expression any = factory.createOperator(ValueType.BOOLEAN, "anyOf", argsBool);
    assertInstanceOf(ArrayReducerOperator.class, any);
  }

  @Test
  void shouldCreateFunctionCallOperator() {
    DDRLOperatorFactory factory = new DDRLOperatorFactory();
    Expression arg = new Constant<>("test", ValueType.STRING);
    List<Expression> args = List.of(arg);

    Expression func = factory.createOperator(ValueType.STRING, "customFunc", args);
    assertInstanceOf(FunctionCallOperator.class, func);
  }

  @Test
  void shouldRejectNonConstantFormatArgument() {
    DDRLOperatorFactory factory = new DDRLOperatorFactory();
    Expression arg = VariableReference.of(IdUtils.toId("var1"), ValueType.STRING);

    assertThrows(FormatExpressionException.class,
      () -> factory.createOperator(ValueType.STRING, "format", List.of(arg)));
  }

  @Test
  void shouldRejectNonStringConstantFormatArgument() {
    DDRLOperatorFactory factory = new DDRLOperatorFactory();
    Expression arg = new Constant<>(1, ValueType.INTEGER);

    assertThrows(FormatExpressionException.class,
      () -> factory.createOperator(ValueType.STRING, "format", List.of(arg)));
  }

  @Test
  void shouldRejectFormatWithWrongArgumentCount() {
    DDRLOperatorFactory factory = new DDRLOperatorFactory();
    Expression arg1 = new Constant<>("a", ValueType.STRING);
    Expression arg2 = new Constant<>("b", ValueType.STRING);

    assertThrows(FormatExpressionException.class,
      () -> factory.createOperator(ValueType.STRING, "format", List.of(arg1, arg2)));
  }

}
