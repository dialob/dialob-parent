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

import com.google.protobuf.CodedInputStream;
import edu.umd.cs.findbugs.annotations.Nullable;
import io.dialob.rule.parser.api.PrimitiveValueType;
import io.dialob.rule.parser.api.ValueType;
import io.dialob.session.engine.session.model.ItemId;
import io.dialob.session.engine.session.model.ItemIdPartial;
import io.dialob.session.engine.session.model.ItemIndex;
import io.dialob.session.engine.session.model.ItemRef;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.time.Instant;
import java.util.List;

public class StateReader {

  private final CodedInputStream input;

  public StateReader(CodedInputStream input) {
    this.input = input;
  }

  public static StateReader newInstance(byte[] bytes) {
    return new StateReader(CodedInputStream.newInstance(new ByteArrayInputStream(bytes)));
  }

  public static StateReader newInstance(ByteArrayInputStream byteArrayInputStream) {
    return new StateReader(CodedInputStream.newInstance(byteArrayInputStream));
  }

  public String readString() throws IOException {
    return input.readString();
  }

  public int readInt() throws IOException {
    return input.readInt32();
  }

  public byte readRawByte() throws IOException {
    return input.readRawByte();
  }

  public boolean readBool() throws IOException {
    return input.readBool();
  }

  public long readLong() throws IOException {
    return input.readInt64();
  }

  public byte[] readRawBytes(int size) throws IOException {
    return input.readRawBytes(size);
  }

  public double readDouble() throws IOException {
    return input.readDouble();
  }

  public Object readValueType(ValueType valueType) throws IOException {
    return valueType.readFrom(input);
  }

  @Nullable
  public String readNullableString() throws IOException {
    if (readBool()) {
      return readString();
    }
    return null;
  }

  public Instant readDate() throws IOException {
    long epoch = readLong();
    long nano = readInt();
    return Instant.ofEpochSecond(epoch, nano);
  }

  @Nullable
  public Instant readNullableDate() throws IOException {
    if (readBool()) {
      return readDate();
    }
    return null;
  }

  public Object readNullableObjectValue() throws IOException {
    if (readBool()) {
      byte answerType = readRawByte();
      int count;
      switch(answerType) {
        case 1:
          return readString();
        case 2:
          return readBigInteger();
        case 3:
          return readBool();
        case 4:
          return readDouble();
        case (byte) 0x80:
          return List.of();
        case (byte) 0x81:
          count = readInt();
          String[] strings = new String[count];
          for (int i = 0; i < count; ++i) {
            strings[i] = readString();
          }
          return List.of(strings);
        case (byte) 0x82:
          count = readInt();
          BigInteger[] integers = new BigInteger[count];
          for (int i = 0; i < count; ++i) {
            integers[i] = readBigInteger();
          }
          return List.of(integers);
      }
    }
    return null;
  }

  public BigInteger readBigInteger() throws IOException {
    var size = readInt();
    var bytes = readRawBytes(size);
    return new BigInteger(bytes);
  }

  @Nullable
  public ItemId readNullableId() throws IOException {
    if (readBool()) {
      var type = readRawByte();
      return switch (type) {
        case 1 -> new ItemRef(readString(), readNullableId());
        case 2 -> new ItemIdPartial(readNullableId());
        case 3 -> new ItemIndex(readInt(), readNullableId());
        default -> throw new RuntimeException("unknown id type " + type);
      };
    }
    return null;
  }


  public List<ItemId> readIdList() throws IOException {
    int count = readInt();
    if (count > 0) {
      ItemId[] ids = new ItemId[count];
      for (int i = 0; i < count; i++) {
        ids[i] = readNullableId();
      }
      return List.of(ids);
    }
    return List.of();
  }

  public List<String> readStringList() throws IOException {
    int count = readInt();
    if (count > 0) {
      String[] ids = new String[count];
      for (int i = 0; i < count; i++) {
        ids[i] = readString();
      }
      return List.of(ids);
    }
    return List.of();
  }

  public Object readNullableValue() throws IOException {
    if (readBool()) {
      byte typeCode = readRawByte();
      ValueType valueType;
      if ((0x80 & typeCode) != 0) {
        typeCode = (byte) (typeCode & 0x7f);
        valueType = ValueType.arrayOf(PrimitiveValueType.values()[typeCode]);
      } else {
        valueType = PrimitiveValueType.values()[typeCode];
      }
      return readValueType(valueType);
    }
    return null;
  }


}
