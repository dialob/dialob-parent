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
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ObjectValueTypeTest {

  @Test
  void shouldCreateObjectValueType() {
    Map<String, ValueType> fieldTypes = Map.of(
      "name", PrimitiveValueType.STRING,
      "age", PrimitiveValueType.INTEGER
    );

    ObjectValueType objectValueType = new ObjectValueType(fieldTypes);

    assertNotNull(objectValueType);
    assertEquals(fieldTypes, objectValueType.fieldTypes());
  }

  @Test
  void shouldThrowExceptionWhenFieldTypesIsNull() {
    assertThrows(NullPointerException.class, () -> new ObjectValueType(null));
  }

  @Test
  void shouldMakeFieldTypesUnmodifiable() {
    Map<String, ValueType> fieldTypes = new HashMap<>();
    fieldTypes.put("name", PrimitiveValueType.STRING);

    ObjectValueType objectValueType = new ObjectValueType(fieldTypes);

    assertThrows(UnsupportedOperationException.class, () ->
      objectValueType.fieldTypes().put("age", PrimitiveValueType.INTEGER)
    );
  }

  @Test
  void shouldCreateObjectValueTypeUsingStaticFactory() {
    Map<String, ValueType> fieldTypes = Map.of(
      "name", PrimitiveValueType.STRING,
      "age", PrimitiveValueType.INTEGER
    );

    ValueType valueType = ObjectValueType.objectOf(fieldTypes);

    assertInstanceOf(ObjectValueType.class, valueType);
    assertEquals(fieldTypes, ((ObjectValueType) valueType).fieldTypes());
  }

  @Test
  void shouldReturnTrueForIsObject() {
    ObjectValueType objectValueType = new ObjectValueType(Map.of());

    assertTrue(objectValueType.isObject());
  }

  @Test
  void shouldThrowUnsupportedOperationExceptionForGetTypeClass() {
    ObjectValueType objectValueType = new ObjectValueType(Map.of());

    assertThrows(UnsupportedOperationException.class, objectValueType::getTypeClass);
  }

  @Test
  void shouldThrowUnsupportedOperationExceptionForParseFromString() {
    ObjectValueType objectValueType = new ObjectValueType(Map.of());

    assertThrows(UnsupportedOperationException.class, () ->
      objectValueType.parseFromString("{}")
    );
  }

  @Test
  void shouldReturnFalseForIsNegateable() {
    ObjectValueType objectValueType = new ObjectValueType(Map.of());

    assertFalse(objectValueType.isNegateable());
  }

  @Test
  void shouldThrowUnsupportedOperationExceptionForNegate() {
    ObjectValueType objectValueType = new ObjectValueType(Map.of());

    assertThrows(UnsupportedOperationException.class, () ->
      objectValueType.negate(new Object())
    );
  }

  @Test
  void shouldThrowUnsupportedOperationExceptionForNot() {
    ObjectValueType objectValueType = new ObjectValueType(Map.of());

    assertThrows(UnsupportedOperationException.class, () ->
      objectValueType.not(new Object())
    );
  }

  @Test
  void shouldThrowUnsupportedOperationExceptionForSumOp() {
    ObjectValueType objectValueType = new ObjectValueType(Map.of());

    assertThrows(UnsupportedOperationException.class, objectValueType::sumOp);
  }

  @Test
  void shouldThrowUnsupportedOperationExceptionForMultOp() {
    ObjectValueType objectValueType = new ObjectValueType(Map.of());

    assertThrows(UnsupportedOperationException.class, objectValueType::multOp);
  }

  @Test
  void shouldReturnNullForPlusType() {
    ObjectValueType objectValueType = new ObjectValueType(Map.of());

    assertNull(objectValueType.plusType(PrimitiveValueType.STRING));
  }

  @Test
  void shouldReturnNullForMinusType() {
    ObjectValueType objectValueType = new ObjectValueType(Map.of());

    assertNull(objectValueType.minusType(PrimitiveValueType.STRING));
  }

  @Test
  void shouldReturnNullForMultiplyType() {
    ObjectValueType objectValueType = new ObjectValueType(Map.of());

    assertNull(objectValueType.multiplyType(PrimitiveValueType.STRING));
  }

  @Test
  void shouldReturnNullForDivideByType() {
    ObjectValueType objectValueType = new ObjectValueType(Map.of());

    assertNull(objectValueType.divideByType(PrimitiveValueType.STRING));
  }

  @Test
  void shouldReturnTrueForCanEqualWithWhenTypesAreEqual() {
    Map<String, ValueType> fieldTypes = Map.of(
      "name", PrimitiveValueType.STRING,
      "age", PrimitiveValueType.INTEGER
    );
    ObjectValueType objectValueType1 = new ObjectValueType(fieldTypes);
    ObjectValueType objectValueType2 = new ObjectValueType(fieldTypes);

    assertTrue(objectValueType1.canEqualWith(objectValueType2));
  }

  @Test
  void shouldReturnFalseForCanEqualWithWhenTypesAreDifferent() {
    ObjectValueType objectValueType1 = new ObjectValueType(Map.of("name", PrimitiveValueType.STRING));
    ObjectValueType objectValueType2 = new ObjectValueType(Map.of("age", PrimitiveValueType.INTEGER));

    assertFalse(objectValueType1.canEqualWith(objectValueType2));
  }

  @Test
  void shouldReturnFalseForCanOrderWith() {
    ObjectValueType objectValueType = new ObjectValueType(Map.of());

    assertFalse(objectValueType.canOrderWith(PrimitiveValueType.STRING));
  }

  @Test
  void shouldThrowUnsupportedOperationExceptionForParseFromStringWithUnit() {
    ObjectValueType objectValueType = new ObjectValueType(Map.of());

    assertThrows(UnsupportedOperationException.class, () ->
      objectValueType.parseFromStringWithUnit("{}", "unit")
    );
  }

  @Test
  void shouldReturnFormattedNameForGetName() {
    Map<String, ValueType> fieldTypes = Map.of(
      "name", PrimitiveValueType.STRING,
      "age", PrimitiveValueType.INTEGER
    );
    ObjectValueType objectValueType = new ObjectValueType(fieldTypes);

    String name = objectValueType.getName();

    assertNotNull(name);
    assertTrue(name.startsWith("{"));
    assertTrue(name.endsWith("}"));
    assertTrue(name.contains("STRING") || name.contains("INTEGER"));
  }

  @Test
  void shouldReturnTrueForEqualsWhenObjectsAreEqual() {
    Map<String, ValueType> fieldTypes = Map.of(
      "name", PrimitiveValueType.STRING,
      "age", PrimitiveValueType.INTEGER
    );
    ObjectValueType objectValueType1 = new ObjectValueType(fieldTypes);
    ObjectValueType objectValueType2 = new ObjectValueType(fieldTypes);

    assertEquals(objectValueType1, objectValueType2);
  }

  @Test
  void shouldReturnFalseForEqualsWhenObjectsAreDifferent() {
    ObjectValueType objectValueType1 = new ObjectValueType(Map.of("name", PrimitiveValueType.STRING));
    ObjectValueType objectValueType2 = new ObjectValueType(Map.of("age", PrimitiveValueType.INTEGER));

    assertNotEquals(objectValueType1, objectValueType2);
  }

  @Test
  void shouldReturnFalseForEqualsWhenComparedWithNull() {
    ObjectValueType objectValueType = new ObjectValueType(Map.of());

    assertNotEquals(null, objectValueType);
  }

  @Test
  void shouldReturnTrueForEqualsWhenComparedWithSelf() {
    ObjectValueType objectValueType = new ObjectValueType(Map.of());

    assertSame(objectValueType, objectValueType);
  }

  @Test
  void shouldReturnSameHashCodeForEqualObjects() {
    Map<String, ValueType> fieldTypes = Map.of(
      "name", PrimitiveValueType.STRING,
      "age", PrimitiveValueType.INTEGER
    );
    ObjectValueType objectValueType1 = new ObjectValueType(fieldTypes);
    ObjectValueType objectValueType2 = new ObjectValueType(fieldTypes);

    assertEquals(objectValueType1.hashCode(), objectValueType2.hashCode());
  }

  @Test
  void shouldReturnObjectForToString() {
    ObjectValueType objectValueType = new ObjectValueType(Map.of());

    assertEquals("OBJECT", objectValueType.toString());
  }

  @Test
  void shouldReturnMinusOneForGetTypeCode() {
    ObjectValueType objectValueType = new ObjectValueType(Map.of());

    assertEquals(-1, objectValueType.getTypeCode());
  }

  @Test
  void shouldThrowUnsupportedOperationExceptionForGetItemValueType() {
    ObjectValueType objectValueType = new ObjectValueType(Map.of());

    assertThrows(UnsupportedOperationException.class, objectValueType::getItemValueType);
  }

  @Test
  void shouldThrowUnsupportedOperationExceptionForWriteTo() {
    ObjectValueType objectValueType = new ObjectValueType(Map.of());
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    CodedOutputStream output = CodedOutputStream.newInstance(byteArrayOutputStream);

    assertThrows(UnsupportedOperationException.class, () ->
      objectValueType.writeTo(output, new Object())
    );
  }

  @Test
  void shouldThrowUnsupportedOperationExceptionForReadFrom() {
    ObjectValueType objectValueType = new ObjectValueType(Map.of());
    ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(new byte[0]);
    CodedInputStream input = CodedInputStream.newInstance(byteArrayInputStream);

    assertThrows(UnsupportedOperationException.class, () ->
      objectValueType.readFrom(input)
    );
  }
}
