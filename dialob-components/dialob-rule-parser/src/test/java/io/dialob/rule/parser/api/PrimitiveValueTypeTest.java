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
package io.dialob.rule.parser.api;

import com.google.protobuf.CodedInputStream;
import com.google.protobuf.CodedOutputStream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Period;
import java.util.function.BinaryOperator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class PrimitiveValueTypeTest {

  @Test
  void testIntegerSum() {
    var bil10 = PrimitiveValueType.INTEGER.parseFromString("10000000000");
    BinaryOperator<Object> operator = PrimitiveValueType.INTEGER.sumOp();
    Assertions.assertEquals(new BigInteger("20000000000"), operator.apply(bil10, bil10));
    Assertions.assertEquals(new BigInteger("10000000001"), operator.apply(bil10, BigInteger.valueOf(1)));
    Assertions.assertEquals(new BigInteger("10000000001"), operator.apply(BigInteger.valueOf(1), bil10));
    Assertions.assertEquals(BigInteger.valueOf(2), operator.apply(BigInteger.valueOf(1), BigInteger.valueOf(1)));
  }

  @Test
  void testIntegerMult() {
    var bil10 = PrimitiveValueType.INTEGER.parseFromString("10000000000");
    BinaryOperator<Object> operator = PrimitiveValueType.INTEGER.multOp();
    Assertions.assertEquals(new BigInteger("100000000000000000000"), operator.apply(bil10, bil10));
    Assertions.assertEquals(new BigInteger("20000000000"), operator.apply(bil10, BigInteger.valueOf(2)));
    Assertions.assertEquals(new BigInteger("20000000000"), operator.apply(BigInteger.valueOf(2), bil10));
    Assertions.assertEquals(BigInteger.valueOf(4), operator.apply(BigInteger.valueOf(2), BigInteger.valueOf(2)));
    Assertions.assertEquals(new BigInteger("4611686014132420609"), operator.apply(BigInteger.valueOf(Integer.MAX_VALUE), BigInteger.valueOf(Integer.MAX_VALUE)));
  }

  @Test
  void testIntegerNegate() {
    var bil10 = PrimitiveValueType.INTEGER.parseFromString("10000000000");
    Assertions.assertEquals(new BigInteger("-10000000000"), PrimitiveValueType.INTEGER.negate(bil10));
    Assertions.assertEquals(BigInteger.valueOf(-1), PrimitiveValueType.INTEGER.negate(BigInteger.valueOf(1)));
    Assertions.assertEquals(BigInteger.valueOf(0), PrimitiveValueType.INTEGER.negate(BigInteger.valueOf(0)));
    assertNull(PrimitiveValueType.INTEGER.negate(null));
  }

  @Test
  void testParsePeriodFromStringWithUnit() {
    Assertions.assertEquals(Period.ofDays(1), PrimitiveValueType.PERIOD.parseFromStringWithUnit("1", "days"));
    Assertions.assertEquals(Period.ofDays(1), PrimitiveValueType.PERIOD.parseFromStringWithUnit("1", "day"));
    Assertions.assertEquals(Period.ofMonths(1), PrimitiveValueType.PERIOD.parseFromStringWithUnit("1", "months"));
    Assertions.assertEquals(Period.ofMonths(1), PrimitiveValueType.PERIOD.parseFromStringWithUnit("1", "month"));
    Assertions.assertEquals(Period.ofWeeks(1), PrimitiveValueType.PERIOD.parseFromStringWithUnit("1", "weeks"));
    Assertions.assertEquals(Period.ofWeeks(1), PrimitiveValueType.PERIOD.parseFromStringWithUnit("1", "week"));
    Assertions.assertEquals(Period.ofYears(1), PrimitiveValueType.PERIOD.parseFromStringWithUnit("1", "years"));
    Assertions.assertEquals(Period.ofYears(1), PrimitiveValueType.PERIOD.parseFromStringWithUnit("1", "year"));
    Assertions.assertEquals(Period.ofYears(1), PrimitiveValueType.PERIOD.parseFromStringWithUnit("P1Y", ""));
  }

  @Test
  void testParseDurationFromStringWithUnit() {
    Assertions.assertEquals(Duration.ofDays(1), PrimitiveValueType.DURATION.parseFromStringWithUnit("1", "days"));
    Assertions.assertEquals(Duration.ofDays(1), PrimitiveValueType.DURATION.parseFromStringWithUnit("1", "day"));
    Assertions.assertEquals(Duration.ofHours(1), PrimitiveValueType.DURATION.parseFromStringWithUnit("1", "hours"));
    Assertions.assertEquals(Duration.ofHours(1), PrimitiveValueType.DURATION.parseFromStringWithUnit("1", "hour"));
    Assertions.assertEquals(Duration.ofDays(7), PrimitiveValueType.DURATION.parseFromStringWithUnit("1", "weeks"));
    Assertions.assertEquals(Duration.ofDays(7), PrimitiveValueType.DURATION.parseFromStringWithUnit("1", "week"));
    Assertions.assertEquals(Duration.ofMinutes(1), PrimitiveValueType.DURATION.parseFromStringWithUnit("1", "minutes"));
    Assertions.assertEquals(Duration.ofMinutes(1), PrimitiveValueType.DURATION.parseFromStringWithUnit("1", "minute"));
    Assertions.assertEquals(Duration.ofSeconds(1), PrimitiveValueType.DURATION.parseFromStringWithUnit("1", "seconds"));
    Assertions.assertEquals(Duration.ofSeconds(1), PrimitiveValueType.DURATION.parseFromStringWithUnit("1", "second"));
  }

  @Test
  void readAndWrite() throws IOException {
    var buffer = new ByteArrayOutputStream();
    var outputStream = CodedOutputStream.newInstance(buffer);
    PrimitiveValueType.DURATION.writeTo(outputStream, Duration.ofDays(1));
    PrimitiveValueType.DURATION.writeTo(outputStream, null);
    PrimitiveValueType.PERIOD.writeTo(outputStream, Period.ofDays(1));
    PrimitiveValueType.PERIOD.writeTo(outputStream, null);
    PrimitiveValueType.DATE.writeTo(outputStream, LocalDate.of(2025,1,30));
    PrimitiveValueType.DATE.writeTo(outputStream, null);
    PrimitiveValueType.DECIMAL.writeTo(outputStream, BigDecimal.valueOf(100,2));
    PrimitiveValueType.DECIMAL.writeTo(outputStream, null);
    PrimitiveValueType.TIME.writeTo(outputStream, LocalTime.of(13,42,2));
    PrimitiveValueType.TIME.writeTo(outputStream, null);
    PrimitiveValueType.STRING.writeTo(outputStream, "hello");
    PrimitiveValueType.STRING.writeTo(outputStream, null);
    PrimitiveValueType.INTEGER.writeTo(outputStream, BigInteger.valueOf(123));
    PrimitiveValueType.INTEGER.writeTo(outputStream, null);
    PrimitiveValueType.BOOLEAN.writeTo(outputStream, Boolean.TRUE);
    PrimitiveValueType.BOOLEAN.writeTo(outputStream, null);
    PrimitiveValueType.PERCENT.writeTo(outputStream, BigDecimal.valueOf(32,2));
    PrimitiveValueType.PERCENT.writeTo(outputStream, null);
    outputStream.flush();

    var inputStream = CodedInputStream.newInstance(buffer.toByteArray());
    assertEquals(Duration.ofDays(1), PrimitiveValueType.DURATION.readFrom(inputStream));
    assertNull(PrimitiveValueType.DURATION.readFrom(inputStream));
    assertEquals(Period.ofDays(1), PrimitiveValueType.PERIOD.readFrom(inputStream));
    assertNull(PrimitiveValueType.PERIOD.readFrom(inputStream));
    assertEquals(LocalDate.of(2025,1,30),PrimitiveValueType.DATE.readFrom(inputStream));
    assertNull(PrimitiveValueType.DATE.readFrom(inputStream));
    assertEquals(BigDecimal.valueOf(100,2),PrimitiveValueType.DECIMAL.readFrom(inputStream));
    assertNull(PrimitiveValueType.DECIMAL.readFrom(inputStream));
    assertEquals(LocalTime.of(13,42,2),PrimitiveValueType.TIME.readFrom(inputStream));
    assertNull(PrimitiveValueType.TIME.readFrom(inputStream));
    assertEquals("hello",PrimitiveValueType.STRING.readFrom(inputStream));
    assertNull(PrimitiveValueType.STRING.readFrom(inputStream));
    assertEquals(BigInteger.valueOf(123),PrimitiveValueType.INTEGER.readFrom(inputStream));
    assertNull(PrimitiveValueType.INTEGER.readFrom(inputStream));
    assertEquals(Boolean.TRUE,PrimitiveValueType.BOOLEAN.readFrom(inputStream));
    assertNull(PrimitiveValueType.BOOLEAN.readFrom(inputStream));
    assertEquals(BigDecimal.valueOf(32,2),PrimitiveValueType.PERCENT.readFrom(inputStream));
    assertNull(PrimitiveValueType.PERCENT.readFrom(inputStream));

    Assertions.assertTrue(inputStream.isAtEnd());
  }


}
