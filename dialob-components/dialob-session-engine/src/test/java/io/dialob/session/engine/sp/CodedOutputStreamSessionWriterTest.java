/*
 * Copyright © 2015 - 2021 ReSys (info@dialob.io)
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
package io.dialob.session.engine.sp;

import com.google.protobuf.CodedInputStream;
import com.google.protobuf.CodedOutputStream;
import io.dialob.session.model.IdUtils;
import io.dialob.session.engine.spi.SessionReader;
import io.dialob.session.engine.spi.SessionWriter;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class CodedOutputStreamSessionWriterTest {

  interface AssertConsumer<T> {
    void apply(T in) throws IOException;
  }

  @Test
  void test() throws IOException {
    assertWriteRead(writer -> writer.writeBigInteger(BigInteger.ZERO), reader -> assertEquals(BigInteger.ZERO, reader.readBigInteger()));
    assertWriteRead(writer -> writer.writeBigInteger(BigInteger.ONE), reader -> assertEquals(BigInteger.ONE, reader.readBigInteger()));
    assertWriteRead(writer -> writer.writeBool(true), reader -> assertTrue(reader.readBool()));
    assertWriteRead(writer -> writer.writeBool(false), reader -> assertFalse(reader.readBool()));
    assertWriteRead(writer -> writer.writeId(IdUtils.toId("1")), reader -> assertEquals(IdUtils.toId("1"), reader.readId()));
    assertWriteRead(writer -> writer.writeId(IdUtils.toId("1.3")), reader -> assertEquals(IdUtils.toId("1.3"), reader.readId()));
    assertWriteRead(writer -> writer.writeId(IdUtils.toId("1.*")), reader -> assertEquals(IdUtils.toId("1.*"), reader.readId()));
    assertWriteRead(writer -> writer.writeId(IdUtils.toId("a.b")), reader -> assertEquals(IdUtils.toId("a.b"), reader.readId()));

    assertWriteRead(writer -> writer.writeIdList(Collections.emptyList()), reader -> assertEquals(Collections.emptyList(), reader.readIdList()));
    assertWriteRead(writer -> writer.writeIdList(Arrays.asList(IdUtils.toId("a.b"))), reader -> assertEquals(Arrays.asList(IdUtils.toId("a.b")), reader.readIdList()));
    assertWriteRead(writer -> writer.writeIdList(Arrays.asList(IdUtils.toId("1"), IdUtils.toId("2"))), reader -> assertEquals(Arrays.asList(IdUtils.toId("1"), IdUtils.toId("2")), reader.readIdList()));


    assertWriteRead(writer -> writer.writeInt32(0), reader -> assertEquals(0, reader.readInt32()));
    assertWriteRead(writer -> writer.writeInt32(1), reader -> assertEquals(1, reader.readInt32()));
    assertWriteRead(writer -> writer.writeInt32(Integer.MIN_VALUE), reader -> assertEquals(Integer.MIN_VALUE, reader.readInt32()));
    assertWriteRead(writer -> writer.writeInt32(Integer.MAX_VALUE), reader -> assertEquals(Integer.MAX_VALUE, reader.readInt32()));

    assertWriteRead(writer -> writer.writeInt64(0), reader -> assertEquals(0, reader.readInt64()));
    assertWriteRead(writer -> writer.writeInt64(1L), reader -> assertEquals(1L, reader.readInt64()));
    assertWriteRead(writer -> writer.writeInt64(Long.MIN_VALUE), reader -> assertEquals(Long.MIN_VALUE, reader.readInt64()));
    assertWriteRead(writer -> writer.writeInt64(Long.MAX_VALUE), reader -> assertEquals(Long.MAX_VALUE, reader.readInt64()));


    assertWriteRead(writer -> writer.writeNullableDate(null), reader -> assertNull(reader.readNullableDate()));
    assertWriteRead(writer -> writer.writeNullableDate(new Date(0L)), reader -> assertEquals(new Date(0L), reader.readNullableDate()));

    assertWriteRead(writer -> writer.writeNullableString(null), reader -> assertNull(reader.readNullableString()));
    assertWriteRead(writer -> writer.writeNullableString("abc"), reader -> assertEquals("abc", reader.readNullableString()));

    assertWriteRead(writer -> writer.writeObjectValue(null), reader -> assertNull(reader.readObjectValue()));
    assertWriteRead(writer -> writer.writeObjectValue("abc"), reader -> assertEquals("abc", reader.readObjectValue()));
    assertWriteRead(writer -> writer.writeObjectValue(BigInteger.ONE), reader -> assertEquals(BigInteger.ONE, reader.readObjectValue()));
    assertWriteRead(writer -> writer.writeObjectValue(1.0), reader -> assertEquals(1.0, reader.readObjectValue()));
    assertWriteRead(writer -> writer.writeObjectValue(true), reader -> assertTrue((Boolean) reader.readObjectValue()));
    assertWriteRead(writer -> writer.writeObjectValue(Collections.emptyList()), reader -> assertEquals(Collections.emptyList(), reader.readObjectValue()));
    assertWriteRead(writer -> writer.writeObjectValue(Arrays.asList("abc")), reader -> assertEquals(Arrays.asList("abc"), reader.readObjectValue()));
    assertWriteRead(writer -> writer.writeObjectValue(Arrays.asList(BigInteger.ONE)), reader -> assertEquals(Arrays.asList(BigInteger.ONE), reader.readObjectValue()));

    assertWriteRead(writer -> writer.writeStringMap(Collections.emptyMap()), reader -> assertEquals(Collections.emptyMap(), reader.readStringMap()));
    assertWriteRead(writer -> writer.writeStringMap(Map.of("a", BigInteger.ONE)), reader -> assertEquals(Map.of("a", BigInteger.ONE), reader.readStringMap()));
    assertWriteRead(writer -> writer.writeStringMap(Map.of("a", BigInteger.ONE, "b", "a")), reader -> assertEquals(Map.of("a", BigInteger.ONE, "b", "a"), reader.readStringMap()));
  }

  private <T> void assertWriteRead(AssertConsumer<SessionWriter> outwriter, AssertConsumer<SessionReader> assertion) throws IOException {
    var buffer = ByteBuffer.allocate(10000);
    var writer = new CodedOutputStreamSessionWriter(CodedOutputStream.newInstance(buffer), 1);
    outwriter.apply(writer);
    var reader = new CodedInputStreamSessionReader(CodedInputStream.newInstance(buffer));
    assertion.apply(reader);
  }
}
