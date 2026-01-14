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
package io.dialob.session.engine.session.protobuf;

import io.dialob.rule.parser.api.ValueType;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class StateWriterTest {

  @Test
  void shouldWriteAndReadValues() throws IOException {
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    StateWriter writer = StateWriter.newInstance(bos);

    writer.writeNullableValue(ValueType.STRING, "test");
    writer.writeNullableValue(ValueType.INTEGER, BigInteger.valueOf(123));
    writer.writeNullableValue(ValueType.DECIMAL, new BigDecimal("123.45"));
    writer.writeNullableValue(ValueType.BOOLEAN, true);
    writer.writeNullableValue(ValueType.DATE, LocalDate.of(2023, 10, 26));
    writer.writeNullableValue(ValueType.TIME, LocalTime.of(12, 34, 56));
    writer.writeNullableValue(ValueType.DURATION, Duration.ofMinutes(30));
    writer.writeNullableValue(ValueType.PERIOD, Period.ofDays(5));
    writer.writeNullableValue(ValueType.arrayOf(ValueType.STRING), List.of("a", "b"));
    writer.writeNullableValue(ValueType.arrayOf(ValueType.INTEGER), List.of(BigInteger.valueOf(1), BigInteger.valueOf(2)));

    writer.flush();

    StateReader reader = StateReader.newInstance(bos.toByteArray());

    assertEquals("test", reader.readNullableValue());
    assertEquals(BigInteger.valueOf(123), reader.readNullableValue());
    assertEquals(new BigDecimal("123.45"), reader.readNullableValue());
    assertEquals(true, reader.readNullableValue());
    assertEquals(LocalDate.of(2023, 10, 26), reader.readNullableValue());
    assertEquals(LocalTime.of(12, 34, 56), reader.readNullableValue());
    assertEquals(Duration.ofMinutes(30), reader.readNullableValue());
    assertEquals(Period.ofDays(5), reader.readNullableValue());
    assertEquals(List.of("a", "b"), reader.readNullableValue());
    assertEquals(List.of(BigInteger.valueOf(1), BigInteger.valueOf(2)), reader.readNullableValue());
  }

  @Test
  void shouldWriteAndReadNullableValues() throws IOException {
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    StateWriter writer = StateWriter.newInstance(bos);

    writer.writeNullableValue(ValueType.STRING, null);
    writer.writeNullableValue(ValueType.INTEGER, null);

    writer.flush();

    StateReader reader = StateReader.newInstance(bos.toByteArray());

    assertNull(reader.readNullableValue());
    assertNull(reader.readNullableValue());
  }
}
