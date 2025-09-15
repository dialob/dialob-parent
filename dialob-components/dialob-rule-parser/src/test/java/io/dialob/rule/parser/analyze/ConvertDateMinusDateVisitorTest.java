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
package io.dialob.rule.parser.analyze;

import io.dialob.rule.parser.Expression;
import io.dialob.rule.parser.api.ValueType;
import io.dialob.rule.parser.api.VariableFinder;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;

class ConvertDateMinusDateVisitorTest {

  @Test
  void shouldConvertDateMinusDateToPeriodBetweenCall() throws Exception {
    VariableFinder variableFinder = Mockito.mock(VariableFinder.class);
    Mockito.when(variableFinder.typeOf("a")).thenReturn(ValueType.DATE);
    Mockito.when(variableFinder.typeOf("b")).thenReturn(ValueType.DATE);
    Mockito.when(variableFinder.mapAlias(any(String.class))).thenAnswer(invocation -> invocation.getArguments()[0]);

    Expression expression = Expression.createExpression(variableFinder, new HashMap<>(), "a - b");
    expression.accept(new ConvertDateMinusDateVisitor());
    assertEquals("(java.time.Period.between b a)",expression.getAst().toString());
  }

  @Test
  void shouldConvertDateMinusDateToPeriodBetweenCallConst() throws Exception {
    VariableFinder variableFinder = Mockito.mock(VariableFinder.class);
    Mockito.when(variableFinder.typeOf("a")).thenReturn(ValueType.DATE);
    Mockito.when(variableFinder.mapAlias(any(String.class))).thenAnswer(invocation -> invocation.getArguments()[0]);

    Expression expression = Expression.createExpression(variableFinder, new HashMap<>(), "a - \"2025-09-15\"");
    expression.accept(new ConvertDateMinusDateVisitor());
    assertEquals("(java.time.Period.between 2025-09-15 a)",expression.getAst().toString());
  }

  @Test
  void shouldConvertDateMinusDateToPeriodBetweenCallConst2() throws Exception {
    VariableFinder variableFinder = Mockito.mock(VariableFinder.class);
    Mockito.when(variableFinder.typeOf("a")).thenReturn(ValueType.DATE);
    Mockito.when(variableFinder.typeOf("b")).thenReturn(ValueType.PERIOD);
    Mockito.when(variableFinder.typeOf("c")).thenReturn(ValueType.DATE);

    Mockito.when(variableFinder.mapAlias(any(String.class))).thenAnswer(invocation -> invocation.getArguments()[0]);

    Expression expression = Expression.createExpression(variableFinder, new HashMap<>(), "a + 1 years");
    expression.accept(new ConvertDateMinusDateVisitor());
    assertEquals("(io.dialob.rule.parser.PeriodUtil.datePlusPeriod a \"1 years\")",expression.getAst().toString());
  }

}
