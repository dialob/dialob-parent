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
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


class ArrayValueTypeTest {

  @Test
  void shouldGetArrayClassOfValueType() {
    assertSame(BigInteger[].class, ValueType.arrayOf(ValueType.INTEGER).getTypeClass());
    assertSame(String[].class, ValueType.arrayOf(ValueType.STRING).getTypeClass());
    assertSame(LocalDate[].class, ValueType.arrayOf(ValueType.DATE).getTypeClass());
    assertSame(LocalTime[].class, ValueType.arrayOf(ValueType.TIME).getTypeClass());
    assertSame(BigDecimal[].class, ValueType.arrayOf(ValueType.DECIMAL).getTypeClass());
    assertSame(Duration[].class, ValueType.arrayOf(ValueType.DURATION).getTypeClass());
    assertSame(Period[].class, ValueType.arrayOf(ValueType.PERIOD).getTypeClass());
    assertSame(Boolean[].class, ValueType.arrayOf(ValueType.BOOLEAN).getTypeClass());
  }

  @Test
  void shouldGetArrayClassOfArray() {
    assertSame(BigInteger[][].class, ValueType.arrayOf(ValueType.arrayOf(ValueType.INTEGER)).getTypeClass());
  }

  @Test
  void shouldParseArrayStrings() {
    assertArrayEquals(new BigInteger[] {BigInteger.valueOf(1),BigInteger.valueOf(2),BigInteger.valueOf(3)}, (BigInteger[]) ValueType.arrayOf(ValueType.INTEGER).parseFromString("[1,2,3]"));
  }

  @Test
  void readAndWrite() throws IOException {
    var buffer = new ByteArrayOutputStream();
    var outputStream = CodedOutputStream.newInstance(buffer);
    ValueType.arrayOf(ValueType.STRING).writeTo(outputStream, null);
    ValueType.arrayOf(ValueType.STRING).writeTo(outputStream, List.of());
    ValueType.arrayOf(ValueType.STRING).writeTo(outputStream, List.of("1", "2", "3"));
    ValueType.arrayOf(ValueType.DECIMAL).writeTo(outputStream, List.of(BigDecimal.valueOf(100,2), BigDecimal.valueOf(1043,2), BigDecimal.valueOf(1010,2)));
    ValueType.arrayOf(ValueType.arrayOf(ValueType.STRING)).writeTo(outputStream, List.of(List.of("?"), List.of(), List.of("1", "2")));

    outputStream.flush();

    var inputStream = CodedInputStream.newInstance(buffer.toByteArray());
    assertNull(ValueType.arrayOf(ValueType.STRING).readFrom(inputStream));
    assertEquals(List.of(), ValueType.arrayOf(ValueType.STRING).readFrom(inputStream));
    assertEquals(List.of("1", "2", "3"), ValueType.arrayOf(ValueType.STRING).readFrom(inputStream));
    assertEquals(List.of(BigDecimal.valueOf(100,2), BigDecimal.valueOf(1043,2), BigDecimal.valueOf(1010,2)), ValueType.arrayOf(ValueType.DECIMAL).readFrom(inputStream));
    assertEquals(List.of(List.of("?"), List.of(), List.of("1", "2")), ValueType.arrayOf(ValueType.STRING).readFrom(inputStream));

    Assertions.assertTrue(inputStream.isAtEnd());

  }
}

