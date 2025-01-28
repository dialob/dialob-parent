package io.dialob.rule.parser.node;

import io.dialob.rule.parser.api.PrimitiveValueType;
import io.dialob.rule.parser.api.ValueType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Period;
import java.util.stream.Stream;

import static java.math.BigInteger.TWO;
import static java.math.BigInteger.ZERO;
import static org.junit.jupiter.api.Assertions.*;

public class ValueTypeTest {

  public static final BigInteger ONE = BigInteger.ONE;


  @Test
  void dateIsParseable() {
    assertNotNull(PrimitiveValueType.DATE, "PrimitiveValueType.DATE should not be null");
    assertNotNull(ValueType.DATE, "ValueType.DATE should not be null");
    assertDoesNotThrow(() -> ValueType.DATE.parseFromString("2025-01-01"));
  }
  @Test
  void booleanIsParseAble() {
    assertNotNull(PrimitiveValueType.BOOLEAN, "PrimitiveValueType.BOOLEAN should not be null");
    assertNotNull(ValueType.BOOLEAN, "ValueType.BOOLEAN should not be null");
    assertNull(ValueType.BOOLEAN.parseFromString(null));
    assertTrue((Boolean) ValueType.BOOLEAN.parseFromString("true"));
    assertFalse((Boolean) ValueType.BOOLEAN.parseFromString("false"));
  }

  @Test
  void booleanReduction() {
    assertTrue(Stream.of(true, true, true).reduce(ValueType.BOOLEAN.multOp()).get());
    assertFalse(Stream.of(true, false, true).reduce(ValueType.BOOLEAN.multOp()).get());
    assertTrue(Stream.of(true, true, true).reduce(ValueType.BOOLEAN.sumOp()).get());
    assertFalse(Stream.of(false, false, false).reduce(ValueType.BOOLEAN.multOp()).get());
  }


  @Test
  void integerIsParseable() {
    assertNotNull(PrimitiveValueType.INTEGER, "PrimitiveValueType.INTEGER should not be null");
    assertNotNull(ValueType.INTEGER, "ValueType.INTEGER should not be null");
    assertNull(ValueType.INTEGER.parseFromString(null));
    Assertions.assertEquals(ZERO, ValueType.INTEGER.parseFromString("0"));
  }

  @Test
  void integerReduction() {
    assertEquals(BigInteger.valueOf(6), Stream.of(ONE, TWO, BigInteger.valueOf(3)).reduce(ValueType.INTEGER.multOp()).get());
    assertEquals(BigInteger.valueOf(6), Stream.of(ONE, TWO, BigInteger.valueOf(3)).reduce(ValueType.INTEGER.sumOp()).get());
    assertEquals(BigInteger.valueOf(24), Stream.of(ONE, TWO, BigInteger.valueOf(3), BigInteger.valueOf(4)).reduce(ValueType.INTEGER.multOp()).get());
    assertEquals(BigInteger.valueOf(10), Stream.of(ONE, TWO, BigInteger.valueOf(3), BigInteger.valueOf(4)).reduce(ValueType.INTEGER.sumOp()).get());
    assertEquals(BigInteger.valueOf(0), Stream.of(ONE, TWO, BigInteger.valueOf(3), BigInteger.valueOf(4), ZERO).reduce(ValueType.INTEGER.multOp()).get());
    assertEquals(BigInteger.valueOf(10), Stream.of(ONE, TWO, BigInteger.valueOf(3), BigInteger.valueOf(4), ZERO).reduce(ValueType.INTEGER.sumOp()).get());
  }


  @Test
  void decimalIsParseAble() {
    assertNotNull(PrimitiveValueType.DECIMAL, "PrimitiveValueType.DECIMAL should not be null");
    assertNotNull(ValueType.DECIMAL, "ValueType.DECIMAL should not be null");
    assertNull(ValueType.DECIMAL.parseFromString(null));
    Assertions.assertEquals(BigDecimal.valueOf(0.1), ValueType.DECIMAL.parseFromString("0.1"));
  }

  @Test
  void dateIsParseAble() {
    assertNotNull(PrimitiveValueType.DATE, "PrimitiveValueType.DATE should not be null");
    assertNotNull(ValueType.DATE, "ValueType.DATE should not be null");
    assertNull(ValueType.DATE.parseFromString(null));
    Assertions.assertEquals(LocalDate.of(2016, 2, 1), ValueType.DATE.parseFromString("2016-02-01"));
  }

  @Test
  void timeIsParseAble() {
    assertNotNull(PrimitiveValueType.TIME, "PrimitiveValueType.TIME should not be null");
    assertNotNull(ValueType.TIME, "ValueType.TIME should not be null");
    assertNull(ValueType.TIME.parseFromString(null));
    Assertions.assertEquals(LocalTime.of(23, 45, 10), ValueType.TIME.parseFromString("23:45:10"));
  }

  @Test
  void periodIsParseAble() {
    assertNotNull(PrimitiveValueType.PERIOD, "PrimitiveValueType.PERIOD should not be null");
    assertNotNull(ValueType.PERIOD, "ValueType.PERIOD should not be null");
    assertNull(ValueType.PERIOD.parseFromString(null));
    Assertions.assertEquals(Period.of(10, 3, 15), ValueType.PERIOD.parseFromString("P10Y3M15D"));
  }

  @Test
  void durationIsParseAble() {
    assertNotNull(PrimitiveValueType.DURATION, "PrimitiveValueType.DURATION should not be null");
    assertNotNull(ValueType.DURATION, "ValueType.DURATION should not be null");
    assertNull(ValueType.DURATION.parseFromString(null));
    Assertions.assertEquals(Duration.ofDays(100).plusHours(2), ValueType.DURATION.parseFromString("P100DT2H"));
  }

  @Test
  void checkReturnTypeMapping() {
    Assertions.assertEquals(ValueType.STRING, ValueType.valueTypeOf(String.class));
    Assertions.assertEquals(ValueType.DECIMAL, ValueType.valueTypeOf(BigDecimal.class));
    Assertions.assertEquals(ValueType.INTEGER, ValueType.valueTypeOf(Integer.class));
    Assertions.assertEquals(ValueType.INTEGER, ValueType.valueTypeOf(int.class));
    Assertions.assertEquals(ValueType.DATE, ValueType.valueTypeOf(LocalDate.class));
    Assertions.assertEquals(ValueType.TIME, ValueType.valueTypeOf(LocalTime.class));
    Assertions.assertEquals(ValueType.DURATION, ValueType.valueTypeOf(Duration.class));
    Assertions.assertEquals(ValueType.PERIOD, ValueType.valueTypeOf(Period.class));
    Assertions.assertEquals(ValueType.BOOLEAN, ValueType.valueTypeOf(Boolean.class));
    Assertions.assertEquals(ValueType.BOOLEAN, ValueType.valueTypeOf(boolean.class));
    assertNull(ValueType.valueTypeOf(Class.class));
  }

  @Test
  void plusTypeReturnTypes() {
    Assertions.assertEquals(ValueType.DATE, ValueType.DATE.plusType(ValueType.PERIOD));
    Assertions.assertEquals(ValueType.TIME, ValueType.TIME.plusType(ValueType.DURATION));
    Assertions.assertEquals(ValueType.INTEGER, ValueType.INTEGER.plusType(ValueType.INTEGER));
    Assertions.assertEquals(ValueType.DECIMAL, ValueType.INTEGER.plusType(ValueType.DECIMAL));
    Assertions.assertEquals(ValueType.DECIMAL, ValueType.DECIMAL.plusType(ValueType.INTEGER));
    Assertions.assertEquals(ValueType.DECIMAL, ValueType.DECIMAL.plusType(ValueType.DECIMAL));
    Assertions.assertEquals(ValueType.STRING, ValueType.STRING.plusType(ValueType.DECIMAL));
    Assertions.assertEquals(ValueType.STRING, ValueType.STRING.plusType(ValueType.PERIOD));
    Assertions.assertEquals(ValueType.STRING, ValueType.STRING.plusType(ValueType.DURATION));
    Assertions.assertEquals(ValueType.STRING, ValueType.STRING.plusType(ValueType.BOOLEAN));
    Assertions.assertEquals(ValueType.STRING, ValueType.TIME.plusType(ValueType.STRING));
    Assertions.assertEquals(ValueType.STRING, ValueType.DECIMAL.plusType(ValueType.STRING));
    Assertions.assertEquals(ValueType.STRING, ValueType.PERIOD.plusType(ValueType.STRING));
    Assertions.assertEquals(ValueType.STRING, ValueType.DURATION.plusType(ValueType.STRING));
    Assertions.assertEquals(ValueType.STRING, ValueType.BOOLEAN.plusType(ValueType.STRING));
    Assertions.assertEquals(ValueType.STRING, ValueType.TIME.plusType(ValueType.STRING));
  }
  @Test
  void minusTypeReturnTypes() {
    Assertions.assertEquals(ValueType.PERIOD, ValueType.DATE.minusType(ValueType.DATE));
    Assertions.assertEquals(ValueType.DURATION, ValueType.TIME.minusType(ValueType.TIME));
    Assertions.assertEquals(ValueType.INTEGER, ValueType.INTEGER.minusType(ValueType.INTEGER));
    Assertions.assertEquals(ValueType.DECIMAL, ValueType.INTEGER.minusType(ValueType.DECIMAL));
    Assertions.assertEquals(ValueType.DECIMAL, ValueType.DECIMAL.minusType(ValueType.INTEGER));
    Assertions.assertEquals(ValueType.DECIMAL, ValueType.DECIMAL.minusType(ValueType.DECIMAL));
    Assertions.assertEquals(ValueType.TIME, ValueType.TIME.minusType(ValueType.DURATION));
    Assertions.assertEquals(ValueType.DATE, ValueType.DATE.minusType(ValueType.PERIOD));
  }
  @Test
  void multipleTypeReturnTypes() {
    Assertions.assertEquals(ValueType.INTEGER, ValueType.INTEGER.multiplyType(ValueType.INTEGER));
    Assertions.assertEquals(ValueType.DECIMAL, ValueType.INTEGER.multiplyType(ValueType.DECIMAL));
    Assertions.assertEquals(ValueType.DECIMAL, ValueType.DECIMAL.multiplyType(ValueType.INTEGER));
    Assertions.assertEquals(ValueType.DECIMAL, ValueType.DECIMAL.multiplyType(ValueType.DECIMAL));
  }
  @Test
  void divideTypeReturnTypes() {
    Assertions.assertEquals(ValueType.INTEGER, ValueType.INTEGER.divideByType(ValueType.INTEGER));
    Assertions.assertEquals(ValueType.DECIMAL, ValueType.INTEGER.divideByType(ValueType.DECIMAL));
    Assertions.assertEquals(ValueType.DECIMAL, ValueType.DECIMAL.divideByType(ValueType.INTEGER));
    Assertions.assertEquals(ValueType.DECIMAL, ValueType.DECIMAL.divideByType(ValueType.DECIMAL));
  }

  @Test
  void valueTypeNames() {
    Assertions.assertEquals("INTEGER", ValueType.INTEGER.getName());
    Assertions.assertEquals("DATE", ValueType.DATE.getName());
    Assertions.assertEquals("DECIMAL", ValueType.DECIMAL.getName());
    Assertions.assertEquals("TIME", ValueType.TIME.getName());
    Assertions.assertEquals("PERIOD", ValueType.PERIOD.getName());
    Assertions.assertEquals("DURATION", ValueType.DURATION.getName());
    Assertions.assertEquals("DECIMAL", ValueType.DECIMAL.getName());
    Assertions.assertEquals("BOOLEAN", ValueType.BOOLEAN.getName());
    Assertions.assertEquals("[STRING]", ValueType.arrayOf(ValueType.STRING).getName());
  }
}
