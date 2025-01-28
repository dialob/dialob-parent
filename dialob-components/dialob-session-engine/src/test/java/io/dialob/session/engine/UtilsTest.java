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
package io.dialob.session.engine;

import com.google.protobuf.CodedInputStream;
import com.google.protobuf.CodedOutputStream;
import io.dialob.api.form.FormValidationError;
import io.dialob.rule.parser.api.ValueType;
import io.dialob.session.engine.session.model.IdUtils;
import io.dialob.session.engine.session.model.ItemState;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Period;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

class UtilsTest {

  @Test
  void shouldNotSerializeFalseInactiveState() {
    ItemState itemState = new ItemState(
      IdUtils.toId("id"),
      null,
      "text",
      null,
      null
    );
    Assertions.assertNull(Utils.toActionItem(itemState.update()
      .setActive(true).get(), null).getInactive());
    Assertions.assertTrue(Utils.toActionItem(itemState.update()
      .setActive(false).get(), null).getInactive());
  }

  @Test
  void shouldConvertIntegerArraysToBigInteger() {
    assertEquals(Collections.singletonList(BigInteger.ONE), Utils.parse(ValueType.arrayOf(ValueType.INTEGER), Collections.singletonList(1)));
  }

  @Test
  void shouldWriteAndRadBigIntegers() throws IOException {
    var buffer = new ByteArrayOutputStream();
    CodedOutputStream outputStream = CodedOutputStream.newInstance(buffer);
    Utils.writeObjectValue(outputStream, null);
    assertEquals(1, outputStream.getTotalBytesWritten());

    buffer = new ByteArrayOutputStream();
    outputStream = CodedOutputStream.newInstance(buffer);
    Utils.writeObjectValue(outputStream, BigInteger.ZERO);
    Utils.writeObjectValue(outputStream, BigInteger.ONE);
    Utils.writeObjectValue(outputStream, new BigInteger("98765432109876543210"));
    assertEquals(20, outputStream.getTotalBytesWritten());
    outputStream.flush();

    var inputStream = CodedInputStream.newInstance(buffer.toByteArray());
    assertEquals(BigInteger.ZERO, Utils.readObjectValue(inputStream));
    assertEquals(BigInteger.ONE, Utils.readObjectValue(inputStream));
    assertEquals(new BigInteger("98765432109876543210"), Utils.readObjectValue(inputStream));
  }


  @Test
  void testValidateDefaultValue() {
    Consumer<FormValidationError> listener = mock();
    assertNull(Utils.validateDefaultValue("x", ValueType.INTEGER, null, listener));
    assertEquals(BigInteger.ONE, Utils.validateDefaultValue("x", ValueType.INTEGER, "1", listener));
    assertEquals(BigDecimal.valueOf(10,1), Utils.validateDefaultValue("x", ValueType.DECIMAL, "1.0", listener));
    assertEquals(Boolean.TRUE, Utils.validateDefaultValue("x", ValueType.BOOLEAN, "true", listener));
    assertEquals(LocalDate.of(2025, 1, 28), Utils.validateDefaultValue("x", ValueType.DATE, "2025-01-28", listener));
    assertEquals(LocalTime.of(14, 14, 55), Utils.validateDefaultValue("x", ValueType.TIME, "14:14:55", listener));
    assertEquals(Period.ofDays(1), Utils.validateDefaultValue("x", ValueType.PERIOD, "P1D", listener));
    assertEquals(Duration.ofHours(1), Utils.validateDefaultValue("x", ValueType.DURATION, "PT1H", listener));

    assertEquals(BigInteger.ONE, Utils.validateDefaultValue("x", ValueType.INTEGER, 1, listener));
    assertEquals(BigInteger.ONE, Utils.validateDefaultValue("x", ValueType.INTEGER, 1L, listener));
    assertEquals(BigDecimal.valueOf(10,1), Utils.validateDefaultValue("x", ValueType.DECIMAL, 1.0, listener));
    assertEquals(BigDecimal.ONE, Utils.validateDefaultValue("x", ValueType.DECIMAL, BigDecimal.ONE, listener));
    assertEquals(Boolean.TRUE, Utils.validateDefaultValue("x", ValueType.BOOLEAN, Boolean.TRUE, listener));
    assertEquals(LocalDate.of(2025, 1, 28), Utils.validateDefaultValue("x", ValueType.DATE, LocalDate.of(2025, 1, 28), listener));
    assertEquals(LocalTime.of(14, 14, 55), Utils.validateDefaultValue("x", ValueType.TIME, LocalTime.of(14, 14, 55), listener));
    assertEquals(Period.ofDays(1), Utils.validateDefaultValue("x", ValueType.PERIOD, Period.ofDays(1), listener));
    assertEquals(Duration.ofHours(1), Utils.validateDefaultValue("x", ValueType.DURATION, Duration.ofHours(1), listener));

    verifyNoInteractions(listener);
  }

  @Test
  void testInvalidValidateDefaultValue() {
    Consumer<FormValidationError> listener = mock();
    assertNull(Utils.validateDefaultValue("x", ValueType.INTEGER, "x", listener));
    verify(listener).accept(any());
    verifyNoMoreInteractions(listener);
  }

  @Test
  void testInvalidValidateDefaultValue2() {
    Consumer<FormValidationError> listener = mock();
    assertNull(Utils.validateDefaultValue("x", ValueType.INTEGER, List.of(), listener));
    verify(listener).accept(any());
    verifyNoMoreInteractions(listener);
    Mockito.reset(listener);

    assertNull(Utils.validateDefaultValue("x", ValueType.INTEGER, 1.0, listener));
    verify(listener).accept(any());
    verifyNoMoreInteractions(listener);
    Mockito.reset(listener);

    assertNull(Utils.validateDefaultValue("x", ValueType.BOOLEAN, 1, listener));
    verify(listener).accept(any());
    verifyNoMoreInteractions(listener);
    Mockito.reset(listener);

    assertNull(Utils.validateDefaultValue("x", ValueType.DATE, 1, listener));
    verify(listener).accept(any());
    verifyNoMoreInteractions(listener);
    Mockito.reset(listener);

    assertNull(Utils.validateDefaultValue("x", ValueType.TIME, 1, listener));
    verify(listener).accept(any());
    verifyNoMoreInteractions(listener);
    Mockito.reset(listener);

    assertNull(Utils.validateDefaultValue("x", ValueType.DURATION, 1, listener));
    verify(listener).accept(any());
    verifyNoMoreInteractions(listener);
    Mockito.reset(listener);

    assertNull(Utils.validateDefaultValue("x", ValueType.PERIOD, 1, listener));
    verify(listener).accept(any());
    verifyNoMoreInteractions(listener);
    Mockito.reset(listener);

    assertNull(Utils.validateDefaultValue("x", ValueType.DECIMAL, 1, listener));
    verify(listener).accept(any());
    verifyNoMoreInteractions(listener);
    Mockito.reset(listener);


  }

}
