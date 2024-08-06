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

import com.google.protobuf.CodedOutputStream;
import edu.umd.cs.findbugs.annotations.NonNull;
import io.dialob.rule.parser.api.ValueType;
import io.dialob.session.engine.spi.SessionWriter;
import io.dialob.session.model.ItemId;
import io.dialob.session.model.ItemIdPartial;
import io.dialob.session.model.ItemIndex;
import io.dialob.session.model.ItemRef;
import lombok.Getter;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class CodedOutputStreamSessionWriter implements SessionWriter {

  private final CodedOutputStream output;

  @Getter
  private final int revision;

  public CodedOutputStreamSessionWriter(CodedOutputStream output, int revision) throws IOException {
    this.output = output;
    this.revision = revision;
    this.output.writeInt32NoTag(revision);
  }

  @Override
  public SessionWriter writeId(ItemId id) throws IOException  {
    if (id == null) {
      this.output.writeBoolNoTag(false);
    } else {
      this.output.writeBoolNoTag(true);
      if (id instanceof ItemRef) {
        ItemRef itemRef = (ItemRef) id;
        this.output.write((byte) 1);
        this.output.writeStringNoTag(itemRef.getValue());
      } else if (id instanceof ItemIdPartial) {
        this.output.write((byte) 2);
      } else if (id instanceof ItemIndex) {
        this.output.write((byte) 3);
        ItemIndex itemRef = (ItemIndex) id;
        this.output.writeInt32NoTag(itemRef.getIndex());
      } else {
        throw new RuntimeException("unknown id type " + id);
      }
      writeId(id.getParent().orElse(null));
    }
    return this;
  }

  @Override
  public SessionWriter writeString(String string) throws IOException {
    this.output.writeStringNoTag(string);
    return this;
  }

  @Override
  public SessionWriter writeNullableString(String string) throws IOException {
    if (string == null) {
      this.output.writeBoolNoTag(false);
    } else {
      this.output.writeBoolNoTag(true);
      this.output.writeStringNoTag(string);
    }
    return this;
  }

  @Override
  public SessionWriter writeRawByte(int ordinal) throws IOException {
    this.output.writeRawByte(ordinal);
    return this;
  }

  @Override
  public SessionWriter writeInt32(int bits) throws IOException {
    this.output.writeInt32NoTag(bits);
    return this;
  }

  @Override
  public SessionWriter writeObjectValue(Object answer) throws IOException {
    final boolean present = answer != null;
    this.output.writeBoolNoTag(present);
    if (present) {
      if (answer instanceof String) {
        this.output.write((byte) 1);
        this.output.writeStringNoTag((String) answer);
      } else if (answer instanceof BigInteger) {
        this.output.write((byte) 2);
        writeBigInteger((BigInteger) answer);
      } else if (answer instanceof Boolean) {
        this.output.write((byte) 3);
        this.output.writeBoolNoTag((Boolean) answer);
      } else if (answer instanceof Double) {
        this.output.write((byte) 4);
        this.output.writeDoubleNoTag((Double) answer);
      } else if (answer instanceof List<?> listValue) {
        final int size = listValue.size();
        if (size == 0) {
          this.output.write((byte) 0x80); // empty list
        } else {
          if (listValue.get(0) instanceof String) {
            this.output.write((byte) 0x81);
            this.output.writeInt32NoTag(size);
            for (String s : (List<String>) listValue) {
              this.output.writeStringNoTag(s);
            }
          } else if (listValue.get(0) instanceof BigInteger) {
            this.output.write((byte) 0x82);
            this.output.writeInt32NoTag(size);
            for (BigInteger i : (List<BigInteger>) listValue) {
              writeBigInteger(i);
            }
          }
        }
      } else {
        throw new RuntimeException("Unknown answer value: " + answer.getClass());
      }
    }
    return this;
  }

  public SessionWriter writeBigInteger(@NonNull BigInteger value) throws IOException {
    var bytes = value.toByteArray();
    output.writeInt32NoTag(bytes.length);
    output.writeRawBytes(bytes);
    return this;
  }

  @Override
  public SessionWriter writeValue(ValueType type, Object value) throws IOException {
    final boolean present = value != null && type != null;
    output.writeBoolNoTag(present);
    if (present) {
      this.output.writeRawByte(type.getTypeCode());
      type.writeTo(this.output, value);
    }
    return this;
  }

  @Override
  public SessionWriter writeStringMap(Map<String, Object> map) throws IOException {
    writeInt32(map.size());
    for (Map.Entry<String,Object> entry : map.entrySet()) {
      writeString(entry.getKey());
      writeObjectValue(entry.getValue());
    }
    return this;
  }

  @Override
  public SessionWriter writeStringList(List<String> strings) throws IOException {
    writeInt32(strings.size());
    for (String s : strings) {
      writeString(s);
    }
    return this;
  }

  @Override
  public SessionWriter writeIdList(List<ItemId> itemIds) throws IOException {
    writeInt32(itemIds.size());
    for (final ItemId itemId : itemIds) {
      writeId(itemId);
    }
    return this;
  }

  @Override
  public SessionWriter writeInt64(long l) throws IOException {
    this.output.writeInt64NoTag(l);
    return this;
  }

  @Override
  public SessionWriter writeNullableDate(Date date) throws IOException {
    if (date == null) {
      this.output.writeBoolNoTag(false);
    } else {
      this.output.writeBoolNoTag(true);
      this.output.writeInt64NoTag(date.getTime());
    }
    return this;
  }

  @Override
  public SessionWriter writeBool(boolean bool) throws IOException {
    this.output.writeBoolNoTag(bool);
    return null;
  }
}
