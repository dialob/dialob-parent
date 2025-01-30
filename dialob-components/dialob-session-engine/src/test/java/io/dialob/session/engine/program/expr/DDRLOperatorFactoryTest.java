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
import io.dialob.session.engine.program.expr.arith.ImmutableVariableReference;
import io.dialob.session.engine.program.expr.arith.InfixOperator;
import io.dialob.session.engine.program.expr.arith.NotOperator;
import io.dialob.session.engine.session.model.IdUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

class DDRLOperatorFactoryTest {

  @Test
  void shouldCreateIsValidOperator() {
    DDRLOperatorFactory factory = new DDRLOperatorFactory();
    var op1 = factory.createOperator(ValueType.STRING, "isValid", List.of(ImmutableVariableReference.of(IdUtils.toId("var1"), ValueType.STRING)));
    var op2 = factory.createOperator(ValueType.STRING, "isNotValid", List.of(ImmutableVariableReference.of(IdUtils.toId("var1"), ValueType.STRING)));
    Assertions.assertNotNull(op1);
    Assertions.assertNotNull(op2);
    Assertions.assertEquals(ValueType.BOOLEAN, op1.getValueType());
    Assertions.assertEquals(ValueType.BOOLEAN, op2.getValueType());
  }

  @Test
  void shouldCreateInOperator() {
    DDRLOperatorFactory factory = new DDRLOperatorFactory();
    var op1 = factory.createOperator(ValueType.STRING, "in", List.of(ImmutableVariableReference.of(IdUtils.toId("var1"), ValueType.STRING)));
    var op2 = factory.createOperator(ValueType.STRING, "notIn", List.of(ImmutableVariableReference.of(IdUtils.toId("var1"), ValueType.STRING)));
    Assertions.assertNotNull(op1);
    Assertions.assertNotNull(op2);
    Assertions.assertEquals(ValueType.BOOLEAN, op1.getValueType());
    Assertions.assertEquals(ValueType.BOOLEAN, op2.getValueType());
    InfixOperator ifop1 = (InfixOperator) op1;
    NotOperator not = (NotOperator) op2;
    InfixOperator ifop2 = (InfixOperator) not.getExpression();

    Assertions.assertEquals(ValueType.STRING, ifop1.getLhs().getValueType());
    Assertions.assertEquals(ValueType.arrayOf(ValueType.STRING), ifop1.getRhs().getValueType());
    Assertions.assertEquals(ValueType.STRING, ifop2.getLhs().getValueType());
    Assertions.assertEquals(ValueType.arrayOf(ValueType.STRING), ifop2.getRhs().getValueType());

  }


}
