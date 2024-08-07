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
package io.dialob.session.spi;

import io.dialob.session.model.ItemId;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;
import java.util.Map;

public interface SessionReader {

  int getRevision();

  ItemId readId() throws IOException;

  String readString() throws IOException;

  String readNullableString() throws IOException;

  int readRawByte() throws IOException;

  int readInt32() throws IOException;

  BigInteger readBigInteger() throws IOException;

  Object readObjectValue() throws IOException;

  List<String> readStringList() throws IOException;

  List<ItemId> readIdList() throws IOException;

  Map<String, Object> readStringMap() throws IOException;

  long readInt64() throws IOException;

  Date readNullableDate() throws IOException;

  boolean readBool() throws IOException;

  Object readValue() throws IOException;

}
