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
package io.dialob.session.engine.spi;

import edu.umd.cs.findbugs.annotations.NonNull;
import io.dialob.rule.parser.api.ValueType;
import io.dialob.session.engine.session.model.ItemId;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;
import java.util.Map;

public interface SessionWriter {

  int getRevision();

  SessionWriter writeBool(boolean active) throws IOException;

  SessionWriter writeId(ItemId id) throws IOException;

  SessionWriter writeIdList(List<ItemId> itemIds) throws IOException;

  SessionWriter writeInt32(int bits) throws IOException;

  SessionWriter writeInt64(long l) throws IOException;

  SessionWriter writeNullableDate(Date date) throws IOException;

  SessionWriter writeNullableString(String string) throws IOException;

  SessionWriter writeObjectValue(Object answer) throws IOException;

  SessionWriter writeRawByte(int ordinal) throws IOException;

  SessionWriter writeBigInteger(@NonNull BigInteger value) throws IOException;

  SessionWriter writeString(String type) throws IOException;

  SessionWriter writeStringMap(@NonNull Map<String, Object> map) throws IOException;

  SessionWriter writeStringList(List<String> strings) throws IOException;

  SessionWriter writeValue(ValueType valueType, Object value) throws IOException;

}
