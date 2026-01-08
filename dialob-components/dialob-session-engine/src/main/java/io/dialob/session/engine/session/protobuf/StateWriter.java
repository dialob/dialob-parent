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

import com.google.protobuf.CodedOutputStream;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import io.dialob.rule.parser.api.ValueType;
import io.dialob.session.engine.session.model.ItemId;
import io.dialob.session.engine.session.model.ItemIdPartial;
import io.dialob.session.engine.session.model.ItemIndex;
import io.dialob.session.engine.session.model.ItemRef;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.time.Instant;
import java.util.List;

public class StateWriter {

  private final CodedOutputStream output;

  public StateWriter(CodedOutputStream output) {
    this.output = output;
  }

  public static StateWriter newInstance(ByteArrayOutputStream bos) {
    return new StateWriter(CodedOutputStream.newInstance(bos));
  }

  public static StateWriter newInstance(ByteBuffer bb) {
    return new StateWriter(CodedOutputStream.newInstance(bb));
  }

  interface Writer<T> {
    void write(@NonNull T value) throws IOException;
  }

  <T> StateWriter nullable(@Nullable T value, Writer<T> writer) throws IOException {
    if (value == null) {
      writeBool(false);
    } else {
      writeBool(true);
      writer.write(value);
    }
    return this;
  }


  public void writeString(String string) throws IOException {
    output.writeStringNoTag(string);
  }

  public void writeInt(int integer) throws IOException {
    output.writeInt32NoTag(integer);
  }

  public void writeBool(boolean b) throws IOException {
    output.writeBoolNoTag(b);

  }

  public void writeRawByte(int b) throws IOException {
    output.writeRawByte(b);
  }

  public void write(byte b) throws IOException {
    output.write(b);

  }

  public void flush() throws IOException {
    output.flush();
  }

  public void writeLong(long l) throws IOException {
    output.writeInt64NoTag(l);
  }

  public void writeRawBytes(byte[] bytes) throws IOException {
    output.writeRawBytes(bytes);
  }

  public void writeDouble(Double double1) throws IOException {
    output.writeDoubleNoTag(double1);
  }

  public void writeValueType(ValueType type, Object value) throws IOException {
    type.writeTo(output, value);
  }

  public void writeNullableString(@Nullable String string) throws IOException {
    nullable(string, this::writeString);
  }

  public void writeNullableDate(Instant date) throws IOException {
    nullable(date, this::writeDate);
  }

  public void writeDate(Instant date) throws IOException {
    writeLong(date.getEpochSecond());
    writeInt(date.getNano());
  }

  public void writeNullableObjectValue(@Nullable Object value) throws IOException {
    nullable(value, this::writeObjectValue);
  }

  public void writeObjectValue(Object value) throws IOException {
    switch (value) {
      case String string -> {
        write((byte) 1);
        writeString(string);
      }
      case BigInteger bigInteger -> {
        write((byte) 2);
        writeBigInteger(bigInteger);
      }
      case Boolean boolean1 -> {
        write((byte) 3);
        writeBool(boolean1);
      }
      case Double double1 -> {
        write((byte) 4);
        writeDouble(double1);
      }
      case List listValue -> {
        final int size = listValue.size();
        if (size == 0) {
          write((byte) 0x80); // empty list
          return;
        }
        if (listValue.getFirst() instanceof String) {
          write((byte) 0x81);
          writeInt(size);
          for (String s : (List<String>) listValue) {
            writeString(s);
          }
        } else if (listValue.getFirst() instanceof BigInteger) {
          write((byte) 0x82);
          writeInt(size);
          for (BigInteger i : (List<BigInteger>) listValue) {
            writeBigInteger(i);
          }
        }
      }
      default -> throw new RuntimeException(String.format("Unknown answer value: %s", value.getClass()));
    }
  }

  public void writeBigInteger(@NonNull BigInteger value) throws IOException {
    var bytes = value.toByteArray();
    writeInt(bytes.length);
    writeRawBytes(bytes);
  }

  public void writeNullableId(@Nullable ItemId id) throws IOException {
    nullable(id, this::writeId);
  }

  public void writeId(ItemId id) throws IOException {
    switch (id) {
      case ItemRef itemRef -> {
        write((byte) 1);
        writeString(itemRef.getValue());
      }
      case ItemIdPartial ignored -> write((byte) 2);
      case ItemIndex itemRef -> {
        write((byte) 3);
        writeInt(itemRef.getIndex());
      }
      default -> throw new RuntimeException("unknown id type " + id);
    }
    writeNullableId(id.getParent().orElse(null));
  }

  public void writeIdList(List<ItemId> itemIds) throws IOException {
    writeInt(itemIds.size());
    for (ItemId s : itemIds) {
      writeNullableId(s);
    }
  }

  public void writeStringList(List<String> stringList) throws IOException {
    writeInt(stringList.size());
    for (String s : stringList) {
      writeString(s);
    }
  }

  public void writeValue(ValueType type, Object value) throws IOException {
    final boolean present = value != null && type != null;
    writeBool(present);
    if (present) {
      writeRawByte(type.getTypeCode());
      writeValueType(type, value);
    }
  }



  public int getTotalBytesWritten() {
    return output.getTotalBytesWritten();
  }

}
