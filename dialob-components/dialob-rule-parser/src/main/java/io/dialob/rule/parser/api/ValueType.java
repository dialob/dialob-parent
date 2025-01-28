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
import edu.umd.cs.findbugs.annotations.Nullable;

import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Period;
import java.util.Objects;
import java.util.function.BinaryOperator;

/**
 * ValueType is an interface representing different types of values
 * that can be handled within a system. It provides methods for value
 * parsing, operations such as addition and multiplication, and type-related
 * metadata.
 * <p>
 * ValueType instances may represent types such as integer, decimal, boolean,
 * string, date, time, and others. It supports operations such as negation,
 * logical NOT, parsing from string or string with units, and coercion.
 * Additionally, it includes type compatibility checks and serialization/deserialization
 * methods.
 */
public interface ValueType extends Serializable, BaseValueType {

  @NonNull
  static ValueType initPrimitive(PrimitiveValueType primitiveValueType) {
    return Objects.requireNonNull(primitiveValueType, "primitiveValueType may not be null");
  }

  /**
   * Represents a value type that corresponds to a time value. This can be used
   * to denote and operate on time-based data in the associated system.
   * <p>
   * The TIME value type encapsulates operations and behaviors specific to
   * time values, including parsing, serialization, and mathematical operations
   * where applicable.
   */
  ValueType TIME = initPrimitive(PrimitiveValueType.TIME);

  /**
   * Represents the `DATE` type within the {@code ValueType} system.
   * This type is used to denote and operate on values that semantically
   * represent dates. The specific operations, conversions, and behaviors
   * associated with this type are defined by the methods in the
   * {@code ValueType} class.
   */
  ValueType DATE = initPrimitive(PrimitiveValueType.DATE);

  /**
   * A constant representing the STRING value type.
   * This value type is associated with textual or string data.
   * It allows for operations and parsing related to string-based values in a system.
   */
  ValueType STRING = initPrimitive(PrimitiveValueType.STRING);

  /**
   * Represents a value type for periods, used to define temporal durations or intervals.
   * This type is part of the {@code ValueType} enumeration, which describes
   * various primitive and non-primitive data types.
   * <p>
   * The {@code PERIOD} value type typically corresponds to a concept of time-based
   * spans or intervals and may be associated with operations or parsing mechanisms
   * specific to such representations.
   */
  ValueType PERIOD = initPrimitive(PrimitiveValueType.PERIOD);

  /**
   * A predefined constant representing the value type for integer numbers.
   * <p>
   * It is used to indicate that a particular value, field, or parameter
   * is expected to have an integer type. This value type supports arithmetic
   * operations such as addition, subtraction, multiplication, and division,
   * along with other operations appropriate for numeric types.
   * <p>
   * The integer type typically corresponds to a whole number representation
   * in Java (e.g., int or Integer).
   */
  ValueType INTEGER = initPrimitive(PrimitiveValueType.INTEGER);

  /**
   * Represents the value type for decimal numbers.
   * DECIMAL is used to denote values that are expected to have decimal precision.
   * This type is typically associated with numerical data that includes fractional parts
   * and supports arithmetic operations like addition, subtraction, multiplication, and division.
   */
  ValueType DECIMAL = initPrimitive(PrimitiveValueType.DECIMAL);

  /**
   * The `BOOLEAN` value type represents a primitive boolean data type.
   * It encapsulates the concept of binary true/false logic.
   * This type is used to define values and operations applicable to boolean logic.
   */
  ValueType BOOLEAN = initPrimitive(PrimitiveValueType.BOOLEAN);

  /**
   * Represents the value type for durations.
   * This value type is used to denote time durations and supports
   * operations and parsing specific to duration-based values.
   * <p>
   * Key features of the `DURATION` value type include:
   * - Parsing and interpreting strings into duration objects.
   * - Defining operations such as addition and subtraction with other value types.
   * - Supporting negation operations where applicable.
   * <p>
   * The `DURATION` value type may interact with other value types such as `TIME`, `PERIOD`,
   * and `DATE` to produce composite values or transformations.
   */
  ValueType DURATION = initPrimitive(PrimitiveValueType.DURATION);

  /**
   * A predefined constant representing a percentage value type.
   * This value type is typically used to handle percentages in various
   * contexts, such as calculations, formatting, and data interpretation.
   * <p>
   * The {@code PERCENT} value type supports operations specific to percentages,
   * including arithmetic operations like addition and multiplication,
   * as well as parsing and negation. The exact behavior of these operations
   * is determined by the methods defined in the containing class.
   */
  ValueType PERCENT = initPrimitive(PrimitiveValueType.PERCENT);

  /**
   * Parses the provided string and returns its corresponding interpreted object.
   * The specific type of object returned depends on the implementation logic and the
   * value of the provided string.
   *
   * @param string the input string to be parsed; must not be null
   * @return the object corresponding to the parsed string
   * @throws IllegalArgumentException if the input string cannot be parsed
   */
  Object parseFromString(String string);

  /**
   * Determines whether the value type supports negation operations.
   * This method checks if applying a negation operation is valid for the specific type.
   *
   * @return true if the value type supports negation operations; false otherwise.
   */
  boolean isNegateable();

  /**
   * Negates the given value by applying the unary negation operation.
   * The specific behavior of the negation operation depends on the type
   * of the input value. For numerical values, this typically involves
   * reversing the sign, while for other types, the operation may not be applicable.
   *
   * @param value the input value to be negated
   * @return the negated result, or throws an UnsupportedOperationException if the negation is not supported for the provided value
   */
  Object negate(Object value);

  /**
   * Applies a logical NOT operation to the given value.
   * This operation typically reverses the boolean representation of
   * the value if applicable.
   *
   * @param value the input value on which the logical NOT operation is to be applied
   * @return the result of the logical NOT operation or an UnsupportedOperationException
   *         if the operation is not applicable to the provided value
   */
  Object not(Object value);

  /**
   * Provides a binary operator that represents the summation operation
   * applicable to the specific type designated by the implementation.
   *
   * @param <T> the type of the operands and the result of the operation
   * @return a {@link BinaryOperator} that performs the summation operation
   */
  <T> BinaryOperator<T> sumOp();

  /**
   * Provides a binary operator that represents the multiplication operation
   * applicable to the specific type designated by the implementation.
   *
   * @param <T> the type of the operands and the result of the operation
   * @return a {@link BinaryOperator} that performs the multiplication operation
   */
  <T> BinaryOperator<T> multOp();

  /**
   * Determines and returns the resulting {@link ValueType} that corresponds to
   * the operation of addition between the current type and the provided type.
   *
   * @param rhs the {@link ValueType} to be added to the current type
   * @return the resulting {@link ValueType} after the addition operation
   */
  ValueType plusType(ValueType rhs);

  /**
   * Determines and returns the resulting {@link ValueType} that corresponds to
   * the subtraction operation between the current type and the provided type.
   *
   * @param rhs the {@link ValueType} to be subtracted from the current type
   * @return the resulting {@link ValueType} after the subtraction operation
   */
  ValueType minusType(ValueType rhs);

  /**
   * Determines and returns the resulting {@link ValueType} that corresponds to
   * the multiplication operation between the current type and the provided type.
   *
   * @param rhs the {@link ValueType} to be multiplied with the current type
   * @return the resulting {@link ValueType} after the multiplication operation
   */
  ValueType multiplyType(ValueType rhs);

  /**
   * Determines and returns the resulting {@link ValueType} that corresponds to
   * the division operation between the current type and the provided type.
   *
   * @param rhs the {@link ValueType} to be divided from the current type
   * @return the resulting {@link ValueType} after the division operation
   */
  ValueType divideByType(ValueType rhs);

  /**
   * Determines if the current {@link ValueType} can be compared for equality
   * with the specified {@link ValueType}.
   *
   * @param rhs the {@link ValueType} to check for equality compatibility
   * @return true if the current {@link ValueType} can be compared for equality
   *         with the specified {@link ValueType}, false otherwise
   */
  boolean canEqualWith(ValueType rhs);

  /**
   * Determines if the current {@link ValueType} can be ordered in relation to the specified {@link ValueType}.
   * This method checks if the two types allow for comparison based on ordering (e.g., less than, greater than).
   *
   * @param rhs the {@link ValueType} to check for order compatibility with the current {@link ValueType}
   * @return true if the current {@link ValueType} can be ordered with the specified {@link ValueType}, false otherwise
   */
  boolean canOrderWith(ValueType rhs);

  /**
   * Parses the provided string value along with its associated unit and returns the corresponding interpreted object.
   * The behavior of this method depends on the specific value type and unit combination.
   *
   * @param value the string representation of the value to be parsed; must not be null
   * @param unit the unit associated with the value; must not be null
   * @return the object corresponding to the parsed value and unit combination
   */
  Object parseFromStringWithUnit(String value, String unit);

  /**
   * Retrieves the name associated with the current value type.
   *
   * @return the name of the value type as a {@link String}
   */
  String getName();

  /**
   * Creates and returns a {@link ValueType} that represents an array of the specified ValueType.
   *
   * @param valueType the {@link ValueType} of the elements in the array; must not be null
   * @return a new {@link ValueType} representing an array of the specified ValueType
   */
  static ValueType arrayOf(@NonNull ValueType valueType) {
    return ArrayValueType.arrayOf(valueType);
  }

  /**
   * Determines the {@link ValueType} corresponding to the specified Java class type.
   * This method recognizes various standard types and returns their respective {@link ValueType}.
   * If the provided type is an array, it recursively determines the {@link ValueType} of its component type.
   * If the type is not recognized, the method returns null.
   *
   * @param returnType the {@link Class} object representing the type for which the {@link ValueType} is to be determined; must not be null
   * @return the corresponding {@link ValueType}, or null if the type is not recognized
   * @throws RuntimeException if the provided type is an array and the {@link ValueType} for its component type cannot be determined
   */
  @Nullable
  static ValueType valueTypeOf(final @NonNull Class<?> returnType) {
    if (returnType == String.class) {
      return STRING;
    }
    if (returnType == LocalDate.class) {
      return DATE;
    }
    if (returnType == LocalTime.class) {
      return TIME;
    }
    if (returnType == Period.class) {
      return PERIOD;
    }
    if (returnType == Integer.class || returnType == int.class || returnType == BigInteger.class ) {
      return INTEGER;
    }
    if (returnType == BigDecimal.class) {
      return DECIMAL;
    }
    if (returnType == Boolean.class || returnType == boolean.class) {
      return BOOLEAN;
    }
    if (returnType == Duration.class) {
      return DURATION;
    }
    if (returnType.isArray()) {
      ValueType valueType = valueTypeOf(returnType.getComponentType());
      if (valueType == null) {
        throw new RuntimeException("Cannot find ValueType of type " + returnType.getComponentType());
      }
      return arrayOf(valueType);
    }
    return null;
  }

  /**
   * Returns the type code representing the specific value type.
   * Each value type is associated with a unique byte code.
   *
   * @return the byte code representing the type.
   */
  byte getTypeCode();


  /**
   * Writes the provided value into the given {@link CodedOutputStream}.
   * The behavior of this method depends on the specific implementation
   * and the type of the passed value.
   *
   * @param output the {@link CodedOutputStream} to which the value will be written
   * @param value the value to be serialized and written into the output
   * @throws IOException if an input or output exception occurs during writing
   */
  void writeTo(CodedOutputStream output, Object value) throws IOException;

  /**
   * Reads and deserializes an object from the provided {@link CodedInputStream}.
   * The specific object type and deserialization logic are determined by the implementation.
   *
   * @param input the {@link CodedInputStream} to read data from; must not be null
   * @return the deserialized object read from the input stream
   * @throws IOException if an I/O error occurs during reading
   */
  Object readFrom(CodedInputStream input) throws IOException;
}
