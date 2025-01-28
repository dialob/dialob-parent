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
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ParserUtilTest {

  @Test
  void testIsReducerOperator() {
    assertTrue(ParserUtil.isReducerOperator("sumOf"));
    assertTrue(ParserUtil.isReducerOperator("minOf"));
    assertTrue(ParserUtil.isReducerOperator("maxOf"));
    assertTrue(ParserUtil.isReducerOperator("allOf"));
    assertTrue(ParserUtil.isReducerOperator("anyOf"));
    assertFalse(ParserUtil.isReducerOperator(null));
    assertFalse(ParserUtil.isReducerOperator(""));
  }

  @Test
  void testItemTypeToValueType() {
    Assertions.assertSame(ValueType.STRING, ParserUtil.itemTypeToValueType("text"));
    Assertions.assertSame(ValueType.STRING, ParserUtil.itemTypeToValueType("list"));
    Assertions.assertSame(ValueType.STRING, ParserUtil.itemTypeToValueType("note"));
    Assertions.assertSame(ValueType.STRING, ParserUtil.itemTypeToValueType("survey"));
    Assertions.assertSame(ValueType.BOOLEAN, ParserUtil.itemTypeToValueType("boolean"));
    Assertions.assertSame(ValueType.DATE, ParserUtil.itemTypeToValueType("date"));
    Assertions.assertSame(ValueType.TIME, ParserUtil.itemTypeToValueType("time"));
    Assertions.assertSame(ValueType.INTEGER, ParserUtil.itemTypeToValueType("number"));
    Assertions.assertSame(ValueType.DECIMAL, ParserUtil.itemTypeToValueType("decimal"));
    Assertions.assertSame(ValueType.arrayOf(ValueType.STRING), ParserUtil.itemTypeToValueType("multichoice"));
    Assertions.assertSame(ValueType.arrayOf(ValueType.INTEGER), ParserUtil.itemTypeToValueType("rowgroup"));
    Assertions.assertNull(ParserUtil.itemTypeToValueType("questionnaire"));
    Assertions.assertNull(ParserUtil.itemTypeToValueType("context"));
    Assertions.assertNull(ParserUtil.itemTypeToValueType("variable"));
    Assertions.assertNull(ParserUtil.itemTypeToValueType("group"));
    Assertions.assertNull(ParserUtil.itemTypeToValueType("surveygroup"));
    Assertions.assertNull(ParserUtil.itemTypeToValueType("row"));
    Assertions.assertThrows(RuntimeException.class, () -> ParserUtil.itemTypeToValueType("x"));
    Assertions.assertThrows(RuntimeException.class, () -> ParserUtil.itemTypeToValueType(null));

  }

}
