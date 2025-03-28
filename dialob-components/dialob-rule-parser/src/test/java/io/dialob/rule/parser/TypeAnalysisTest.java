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
package io.dialob.rule.parser;

import io.dialob.rule.parser.api.ValueType;
import io.dialob.rule.parser.api.VariableFinder;
import io.dialob.rule.parser.node.NodeBase;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.verifyNoMoreInteractions;

class TypeAnalysisTest {

  @Test
  void doubleQuotedStringsAreStrings() throws Exception {
    // given
    VariableFinder variableFinder = Mockito.mock(VariableFinder.class);

    // when
    NodeBase nodeBase = parse(variableFinder, "\"a\"");

    // then
    assertSame(ValueType.STRING, nodeBase.getValueType());
    verifyNoMoreInteractions(variableFinder);
  }

  @Test
  void singleQuotedStringsAreStrings() throws Exception {
    // given
    VariableFinder variableFinder = Mockito.mock(VariableFinder.class);

    // when
    NodeBase nodeBase = parse(variableFinder, "'a'");

    // then
    assertSame(ValueType.STRING, nodeBase.getValueType());
    verifyNoMoreInteractions(variableFinder);
  }

  @Test
  void additionOf2IntegersResultsInteger() throws Exception {
    // given
    VariableFinder variableFinder = Mockito.mock(VariableFinder.class);

    // when
    assertSame(ValueType.INTEGER, parse(variableFinder, "1 + 2").getValueType());
    assertSame(ValueType.INTEGER, parse(variableFinder, "1 + 2 - 3 * 5 / 9").getValueType());
    assertSame(ValueType.STRING, parse(variableFinder, "'1' + '2'").getValueType());

    assertSame(ValueType.INTEGER, parse(variableFinder, "1 * 2").getValueType());

    assertSame(ValueType.INTEGER, parse(variableFinder, "1 - 2").getValueType());

    assertSame(ValueType.INTEGER, parse(variableFinder, "1 / 2").getValueType());

    assertSame(ValueType.DECIMAL, parse(variableFinder, "1.0 + 2.0").getValueType());
    assertSame(ValueType.DECIMAL, parse(variableFinder, "1.0 * 2.0").getValueType());
    assertSame(ValueType.DECIMAL, parse(variableFinder, "1.0 - 2.0").getValueType());
    assertSame(ValueType.DECIMAL, parse(variableFinder, "1.0 / 2.0").getValueType());

    assertSame(ValueType.DECIMAL, parse(variableFinder, "1.0 + 2").getValueType());
    assertSame(ValueType.DECIMAL, parse(variableFinder, "1 + 2.0").getValueType());


    // then
    verifyNoMoreInteractions(variableFinder);
  }

  private NodeBase parse(VariableFinder variableFinder, String expressionString) {
    return Expression
      .createExpression(variableFinder, new HashMap<>(), expressionString)
      .getAst();
  }


}
