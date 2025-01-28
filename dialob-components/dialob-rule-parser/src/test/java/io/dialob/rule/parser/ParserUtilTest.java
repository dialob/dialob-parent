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
