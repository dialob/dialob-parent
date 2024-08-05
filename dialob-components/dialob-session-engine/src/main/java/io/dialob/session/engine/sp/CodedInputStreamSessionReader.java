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

import com.google.common.collect.ImmutableList;
import com.google.protobuf.CodedInputStream;
import io.dialob.rule.parser.api.PrimitiveValueType;
import io.dialob.rule.parser.api.ValueType;
import io.dialob.session.engine.session.model.IdUtils;
import io.dialob.session.engine.session.model.ItemId;
import io.dialob.session.engine.spi.SessionReader;
import lombok.Getter;

import java.io.IOException;
import java.math.BigInteger;
import java.util.*;

public class CodedInputStreamSessionReader implements SessionReader {

  private final CodedInputStream input;

  @Getter
  private final int revision;

  public CodedInputStreamSessionReader(CodedInputStream input) throws IOException {
    this.input = input;
    this.revision = input.readInt32();
  }

  @Override
  public ItemId readId() throws IOException {
    return IdUtils.readIdFrom(this.input);
  }

  @Override
  public String readString() throws IOException {
    return this.input.readString();
  }

  @Override
  public String readNullableString() throws IOException {
    if (input.readBool()) {
      return input.readString();
    }
    return null;
  }

  @Override
  public int readRawByte() throws IOException {
    return this.input.readRawByte();
  }

  @Override
  public int readInt32() throws IOException {
    return this.input.readInt32();
  }

  @Override
  public BigInteger readBigInteger() throws IOException {
    var size = readInt32();
    var bytes = this.input.readRawBytes(size);
    return new BigInteger(bytes);
  }

  @Override
  public Object readObjectValue() throws IOException {
    if (this.input.readBool()) {
      byte answerType = this.input.readRawByte();
      int count;
      switch(answerType) {
        case 1:
          return this.input.readString();
        case 2:
          return readBigInteger();
        case 3:
          return this.input.readBool();
        case 4:
          return this.input.readDouble();
        case (byte) 0x80:
          return ImmutableList.of();
        case (byte) 0x81:
          count = this.input.readInt32();
          String[] strings = new String[count];
          for (int i = 0; i < count; ++i) {
            strings[i] = this.input.readString();
          }
          return ImmutableList.copyOf(strings);
        case (byte) 0x82:
          count = this.input.readInt32();
          BigInteger[] integers = new BigInteger[count];
          for (int i = 0; i < count; ++i) {
            integers[i] = readBigInteger();
          }
          return ImmutableList.copyOf(integers);
      }
    }
    return null;
  }



  @Override
  public Object readValue() throws IOException {
    if (input.readBool()) {
      byte typeCode = input.readRawByte();
      ValueType valueType;
      if ((0x80 & typeCode) != 0) {
        typeCode = (byte) (typeCode & 0x7f);
        valueType = ValueType.arrayOf(PrimitiveValueType.values()[typeCode]);
      } else {
        valueType = PrimitiveValueType.values()[typeCode];
      }
      return valueType.readFrom(input);
    }
    return null;
  }

  @Override
  public List<String> readStringList() throws IOException {
    int count = input.readInt32();
    if (count > 0) {
      String[] ids = new String[count];
      for (int i = 0; i < count; i++) {
        ids[i] = input.readString();
      }
      return ImmutableList.copyOf(ids);
    }
    return ImmutableList.of();
  }

  @Override
  public Map<String, Object> readStringMap() throws IOException {
    var map = new HashMap<String,Object>();
    int count = input.readInt32();
    if (count == 0) {
      return Collections.emptyMap();
    }
    while (count-- > 0) {
      String key = readString();
      Object value = readObjectValue();
      map.put(key, value);
    }
    return Collections.unmodifiableMap(map);
  }

  @Override
  public List<ItemId> readIdList() throws IOException {
    int count = input.readInt32();
    if (count > 0) {
      ItemId[] ids = new ItemId[count];
      for (int i = 0; i < count; i++) {
        ids[i] = IdUtils.readIdFrom(input);
      }
      return ImmutableList.copyOf(ids);
    }
    return ImmutableList.of();
  }

  @Override
  public long readInt64() throws IOException {
    return this.input.readInt64();
  }

  @Override
  public Date readNullableDate() throws IOException {
    if (input.readBool()) {
      return new Date(input.readInt64());
    }
    return null;
  }

  @Override
  public boolean readBool() throws IOException {
    return input.readBool();
  }
}
