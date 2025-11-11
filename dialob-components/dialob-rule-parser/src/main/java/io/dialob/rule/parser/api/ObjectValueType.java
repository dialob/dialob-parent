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
package io.dialob.rule.parser.api;

import com.google.protobuf.CodedInputStream;
import com.google.protobuf.CodedOutputStream;
import edu.umd.cs.findbugs.annotations.NonNull;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public record ObjectValueType(
  Map<String, ValueType> fieldTypes
) implements ValueType {

  public static ValueType EMPTY_OBJECT = new ObjectValueType(Collections.emptyMap());

  public ObjectValueType {
    fieldTypes = Collections.unmodifiableMap(Objects.requireNonNull(fieldTypes, "fieldTypes may not be null"));
  }

  public static ValueType objectOf(@NonNull Map<String, ValueType> fieldTypes) {
    if (fieldTypes.isEmpty()) {
      return EMPTY_OBJECT;
    }
    return new ObjectValueType(fieldTypes);
  }

  @Override
  public boolean isObject() {
    return true;
  }

  @Override
  public String getName() {
    return "{" + fieldTypes.entrySet().stream().map(entry -> "\"" + entry.getValue().getName() + "\": " + entry.getKey()).collect(Collectors.joining(",")) + "}";
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (obj == this) {
      return true;
    }
    if (obj instanceof ObjectValueType(Map<String, ValueType> types)) {
      return types.equals(this.fieldTypes);
    }
    return false;
  }

  @Override
  public int hashCode() {
    return fieldTypes.hashCode() * 7;
  }

  @NonNull
  @Override
  public String toString() {
    return "OBJECT("+ fieldTypes.entrySet().stream().map(entry -> entry.getKey() + ":" + entry.getValue()).collect(Collectors.joining(",")) + ")";
  }

  @Override
  public byte getTypeCode() {
    return -1;
  }

  @Override
  public ValueType getItemValueType() {
    throw new UnsupportedOperationException();
  }

  @Override
  public void writeTo(CodedOutputStream output, Object value) throws IOException {
    // Not stored into session state yet.
    throw new UnsupportedOperationException();
  }

  @Override
  public Object readFrom(CodedInputStream input) throws IOException {
    // Not stored into session state yet.
    throw new UnsupportedOperationException();
  }
}
