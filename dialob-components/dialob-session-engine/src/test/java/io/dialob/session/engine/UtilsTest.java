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
import io.dialob.rule.parser.api.ArrayValueType;
import io.dialob.rule.parser.api.ValueType;
import io.dialob.session.engine.session.model.IdUtils;
import io.dialob.session.engine.session.model.ItemState;
import org.checkerframework.checker.units.qual.C;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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
    Assertions.assertEquals(Collections.singletonList(BigInteger.ONE), Utils.parse(ValueType.arrayOf(ValueType.INTEGER), Collections.singletonList(1)));
  }

  @Test
  void shouldWriteAndRadBigIntegers() throws IOException {
    var buffer = new ByteArrayOutputStream();
    CodedOutputStream outputStream = CodedOutputStream.newInstance(buffer);
    Utils.writeObjectValue(outputStream, null);
    Assertions.assertEquals(1, outputStream.getTotalBytesWritten());

    buffer = new ByteArrayOutputStream();
    outputStream = CodedOutputStream.newInstance(buffer);
    Utils.writeObjectValue(outputStream, BigInteger.ZERO);
    Utils.writeObjectValue(outputStream, BigInteger.ONE);
    Utils.writeObjectValue(outputStream, new BigInteger("98765432109876543210"));
    Assertions.assertEquals(20, outputStream.getTotalBytesWritten());
    outputStream.flush();

    var inputStream = CodedInputStream.newInstance(buffer.toByteArray());
    Assertions.assertEquals(BigInteger.ZERO, Utils.readObjectValue(inputStream));
    Assertions.assertEquals(BigInteger.ONE, Utils.readObjectValue(inputStream));
    Assertions.assertEquals(new BigInteger("98765432109876543210"), Utils.readObjectValue(inputStream));

  }

}
