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

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.util.function.BinaryOperator;

import static org.apache.commons.lang3.StringUtils.isBlank;

public enum PrimitiveValueType implements ValueType {
  TIME {
    @Override
    public Class<?> getTypeClass() {
      return LocalTime.class;
    }

    @Override
    public LocalTime parseFromString(String string) {
      if (isBlank(string)) {
        return null;
      }
      return LocalTime.parse(string);
    }

    @Override
    public boolean isNegateable() {
      return false;
    }

    @Override
    public ValueType plusType(ValueType rhs) {
      if (rhs == PERIOD || rhs == DATE) {
        return null;
      } else if (rhs == DURATION) {
        return TIME;
      } else if (rhs == TIME) {
        return DURATION;
      }
      return super.plusType(rhs);
    }

    @Override
    public ValueType minusType(ValueType rhs) {
      if (rhs == DURATION) {
        return TIME;
      } else if (rhs == TIME) {
        return DURATION;
      }
      return null;
    }

    @Override
    public void writeTo(CodedOutputStream output, Object value) throws IOException {
      boolean present = value != null;
      output.writeBoolNoTag(present);
      if (present) {
        LocalTime localTime = (LocalTime) value;
        output.writeInt32NoTag(localTime.getHour());
        output.writeInt32NoTag(localTime.getMinute());
        output.writeInt32NoTag(localTime.getSecond());
        output.writeInt32NoTag(localTime.getNano());
      }
    }

    @Override
    public Object readFrom(CodedInputStream input) throws IOException {
      if (input.readBool()) {
        int hour = input.readInt32();
        int minute = input.readInt32();
        int second = input.readInt32();
        int nano = input.readInt32();
        return LocalTime.of(hour, minute, second, nano);
      }
      return null;
    }
  },
  DURATION {
    @Override
    public BinaryOperator<Duration> sumOp() {
      return (identity, element) -> {
        if (identity == null) {
          return element;
        }
        if (element == null) {
          return identity;
        }
        return identity.plus(element);
      };
    }

    @Override
    public Class<?> getTypeClass() {
      return Duration.class;
    }

    @Override
    public Duration parseFromString(String string) {
      if (isBlank(string)) {
        return null;
      }
      return Duration.parse(string);
    }

    @Override
    public Object parseFromStringWithUnit(String value, String unit) {
      return switch (unit) {
        case "seconds", "second" -> Duration.ofSeconds(Long.parseLong(value));
        case "minutes", "minute" -> Duration.ofMinutes(Long.parseLong(value));
        case "hours", "hour" -> Duration.ofHours(Long.parseLong(value));
        case "days", "day" -> Duration.ofDays(Long.parseLong(value));
        case "weeks", "week" -> Duration.ofDays(Long.parseLong(value) * 7L);
        default -> parseFromString(value);
      };
    }

    @Override
    public boolean isNegateable() {
      return true;
    }

    @Override
    public Object negate(Object value) {
      return ((Duration) value).negated();
    }

    @Override
    public ValueType plusType(ValueType rhs) {
      if (rhs == DURATION) {
        return DURATION;
      } else if (rhs == TIME) {
        return TIME;
      }
      return super.plusType(rhs);
    }

    @Override
    public ValueType minusType(ValueType rhs) {
      if (rhs == DURATION) {
        return DURATION;
      }
      return null;
    }
    @Override
    public void writeTo(CodedOutputStream output, Object value) throws IOException {
      boolean present = value != null;
      output.writeBoolNoTag(present);
      if (present) {
        Duration duration = (Duration) value;
        output.writeInt64NoTag(duration.getSeconds());
      }
    }

    @Override
    public Object readFrom(CodedInputStream input) throws IOException {
      if (input.readBool()) {
        long seconds = input.readInt64();
        return Duration.of(seconds, ChronoUnit.SECONDS);
      }
      return null;
    }


  },
  DATE {
    @Override
    public Class<?> getTypeClass() {
      return LocalDate.class;
    }

    @Override
    public LocalDate parseFromString(String string) {
      if (isBlank(string)) {
        return null;
      }
      return LocalDate.parse(string);
    }

    @Override
    public boolean isNegateable() {
      return false;
    }

    @Override
    public ValueType plusType(ValueType rhs) {
      if (rhs == TIME || rhs == DURATION) {
        return null;
      } else if (rhs == DATE) {
        return PERIOD;
      } else if (rhs == PERIOD) {
        return DATE;
      }
      return super.plusType(rhs);
    }

    @Override
    public ValueType minusType(ValueType rhs) {
      if (rhs == DATE) {
        return PERIOD;
      } else if (rhs == PERIOD) {
        return DATE;
      }
      return null;
    }
    @Override
    public void writeTo(CodedOutputStream output, Object value) throws IOException {
      boolean present = value != null;
      output.writeBoolNoTag(present);
      if (present) {
        LocalDate localDate = (LocalDate) value;
        output.writeInt32NoTag(localDate.getYear());
        output.writeInt32NoTag(localDate.getMonthValue());
        output.writeInt32NoTag(localDate.getDayOfMonth());
      }
    }

    @Override
    public Object readFrom(CodedInputStream input) throws IOException {
      if (input.readBool()) {
        int year = input.readInt32();
        int month = input.readInt32();
        int day = input.readInt32();
        return LocalDate.of(year, month, day);
      }
      return null;
    }
  },

  PERIOD {
    @Override
    public BinaryOperator<Period> sumOp() {
      return (identity, element) -> {
        if (identity == null) {
          return element;
        }
        if (element == null) {
          return identity;
        }
        return identity.plus(element);
      };
    }

    @Override
    public Class<?> getTypeClass() {
      return Period.class;
    }

    @Override
    public Object parseFromStringWithUnit(String value, String unit) {
      return switch (unit) {
        case "years", "year" -> Period.ofYears(Integer.parseInt(value));
        case "months", "month" -> Period.ofMonths(Integer.parseInt(value));
        case "days", "day" -> Period.ofDays(Integer.parseInt(value));
        case "weeks", "week" -> Period.ofWeeks(Integer.parseInt(value));
        default -> parseFromString(value);
      };
    }

    @Override
    public Period parseFromString(String string) {
      if (isBlank(string)) {
        return null;
      }
      return Period.parse(string);
    }

    @Override
    public boolean isNegateable() {
      return true;
    }

    @Override
    public Object negate(Object value) {
      return ((Period) value).negated();
    }

    @Override
    public ValueType plusType(ValueType rhs) {
      if (rhs == PERIOD) {
        return PERIOD;
      } else if (rhs == DATE) {
        return DATE;
      }
      return super.plusType(rhs);
    }

    @Override
    public ValueType minusType(ValueType rhs) {
      if (rhs == PERIOD) {
        return PERIOD;
      }
      return null;
    }

    @Override
    public boolean canOrderWith(ValueType rhs) {
      return rhs == PERIOD;
    }

    @Override
    public void writeTo(CodedOutputStream output, Object value) throws IOException {
      boolean present = value != null;
      output.writeBoolNoTag(present);
      if (present) {
        Period period = (Period) value;
        output.writeInt32NoTag(period.getYears());
        output.writeInt32NoTag(period.getMonths());
        output.writeInt32NoTag(period.getDays());
      }
    }

    @Override
    public Object readFrom(CodedInputStream input) throws IOException {
      if (input.readBool()) {
        int years = input.readInt32();
        int months = input.readInt32();
        int days = input.readInt32();
        return Period.of(years, months, days);
      }
      return null;
    }

  },
  INTEGER {
    @Override
    public BinaryOperator<BigInteger> sumOp() {
      return (identity, element) -> {
        if (identity == null) {
          return element;
        }
        if (element == null) {
          return identity;
        }
        return identity.add(element);
      };
    }

    @Override
    public BinaryOperator<BigInteger> multOp() {
      return (identity, element) -> {
        if (identity == null) {
          return element;
        }
        if (element == null) {
          return identity;
        }
        return identity.multiply(element);
      };
    }

    @Override
    public Class<?> getTypeClass() {
      return BigInteger.class;
    }

    @Override
    public BigInteger parseFromString(String string) {
      if (isBlank(string)) {
        return null;
      }
      return new BigInteger(string);
    }

    @Override
    public boolean isNegateable() {
      return true;
    }

    @Override
    public Object negate(Object value) {
      if (value instanceof BigInteger) {
        return ((BigInteger) value).negate();
      }
      return value;
    }

    protected boolean isNumberType(ValueType rhs) {
      return rhs == DECIMAL || rhs == INTEGER;
    }

    @Override
    public ValueType plusType(ValueType rhs) {
      if (isNumberType(rhs)) {
        return rhs;
      }
      return super.plusType(rhs);
    }

    @Override
    public ValueType minusType(ValueType rhs) {
      if (isNumberType(rhs)) {
        return rhs;
      }
      return null;
    }

    @Override
    public ValueType multiplyType(ValueType rhs) {
      if (isNumberType(rhs)) {
        return rhs;
      }
      return null;
    }

    @Override
    public ValueType divideByType(ValueType rhs) {
      if (isNumberType(rhs)) {
        return rhs;
      }
      return null;
    }

    @Override
    public boolean canOrderWith(ValueType rhs) {
      return isNumberType(rhs);
    }

    @Override
    public boolean canEqualWith(ValueType rhs) {
      return isNumberType(rhs);
    }

    @Override
    public Object coerceFrom(Object value) {
      if (value instanceof BigInteger) {
        return value;
      }
      if (value instanceof BigDecimal) {
        return ((BigDecimal) value).toBigInteger();
      }
      if (value instanceof Number) {
        return BigInteger.valueOf(((Number) value).longValue());
      }
      return null;
    }

    @Override
    public void writeTo(CodedOutputStream output, Object value) throws IOException {
      boolean present = value != null;
      output.writeBoolNoTag(present);
      if (present) {
        BigInteger bi = (BigInteger) value;
        byte[] byteArray = bi.toByteArray();
        output.writeInt32NoTag(byteArray.length);
        output.writeRawBytes(byteArray);
      }
    }

    @Override
    public Object readFrom(CodedInputStream input) throws IOException {
      if (input.readBool()) {
        int size = input.readInt32();
        var bytes = input.readRawBytes(size);
        return new BigInteger(bytes);
      }
      return null;
    }

  },
  DECIMAL {
    @Override
    public BinaryOperator<BigDecimal> sumOp() {
      return (identity, element) -> {
        if (identity == null) {
          return element;
        }
        if (element == null) {
          return identity;
        }
        return identity.add(element);
      };
    }

    @Override
    public BinaryOperator<BigDecimal> multOp() {
      return (identity, element) -> {
        if (identity == null) {
          return element;
        }
        if (element == null) {
          return identity;
        }
        return identity.multiply(element);
      };
    }

    @Override
    public Class<?> getTypeClass() {
      return BigDecimal.class;
    }

    @Override
    public BigDecimal parseFromString(String string) {
      if (isBlank(string)) {
        return null;
      }
      return BigDecimal.valueOf(Double.parseDouble(string));
    }

    @Override
    public boolean isNegateable() {
      return true;
    }

    @Override
    public Object negate(Object value) {
      return ((BigDecimal) value).negate();
    }

    @Override
    public ValueType plusType(ValueType rhs) {
      if (rhs == DECIMAL || rhs == INTEGER) {
        return DECIMAL;
      }
      return super.plusType(rhs);
    }

    @Override
    public ValueType minusType(ValueType rhs) {
      if (rhs == DECIMAL || rhs == INTEGER) {
        return DECIMAL;
      }
      return null;
    }

    @Override
    public ValueType multiplyType(ValueType rhs) {
      if (rhs == DECIMAL || rhs == INTEGER) {
        return DECIMAL;
      }
      return null;
    }

    @Override
    public ValueType divideByType(ValueType rhs) {
      if (rhs == DECIMAL || rhs == INTEGER) {
        return DECIMAL;
      }
      return null;
    }

    @Override
    public boolean canOrderWith(ValueType rhs) {
      return rhs == DECIMAL || rhs == INTEGER;
    }

    @Override
    public boolean canEqualWith(ValueType rhs) {
      return rhs == DECIMAL || rhs == INTEGER;
    }

    @Override
    public Object coerceFrom(Object value) {
      if (value instanceof Number) {
        return BigDecimal.valueOf(((Number) value).doubleValue());
      }
      return null;
    }
    @Override
    public void writeTo(CodedOutputStream output, Object value) throws IOException {
      boolean present = value != null;
      output.writeBoolNoTag(present);
      if (present) {
        BigDecimal decimal = (BigDecimal) value;
        output.writeStringNoTag(decimal.toString());
      }
    }

    @Override
    public Object readFrom(CodedInputStream input) throws IOException {
      if (input.readBool()) {
        return new BigDecimal(input.readString());
      }
      return null;
    }

  },
  BOOLEAN {
    @Override
    public BinaryOperator<Boolean> sumOp() {    // === orOp
      return (identity, element) -> {
        if (identity == null) {
          return element;
        }
        if (element == null) {
          return identity;
        }
        return identity || element;
      };
    }

    @Override
    public BinaryOperator<Boolean> multOp() {    // === andOp
      return (identity, element) -> {
        if (identity == null || element == null) {
          return null;
        }
        return identity && element;
      };
    }

    @Override
    public Class<?> getTypeClass() {
      return Boolean.class;
    }

    @Override
    public Boolean parseFromString(String string) {
      if (isBlank(string)) {
        return null;
      }
      return Boolean.valueOf(string);
    }

    @Override
    public boolean isNegateable() {
      return false;
    }

    @Override
    public Object not(Object value) {
      return !(Boolean) value;
    }

    @Override
    public boolean canOrderWith(ValueType rhs) {
      return false;
    }
    @Override
    public void writeTo(CodedOutputStream output, Object value) throws IOException {
      boolean present = value != null;
      output.writeBoolNoTag(present);
      if (present) {
        output.writeBoolNoTag((Boolean) value);
      }
    }

    @Override
    public Object readFrom(CodedInputStream input) throws IOException {
      if (input.readBool()) {
        return input.readBool();
      }
      return null;
    }
  },
  STRING {
    @Override
    public BinaryOperator<String> sumOp() {
      return (identity, element) -> {
        if (identity == null) {
          return element;
        }
        return identity + element;
      };
    }

    @Override
    public Class<?> getTypeClass() {
      return String.class;
    }

    @Override
    public Object parseFromString(String string) {
      return string;
    }

    @Override
    public boolean isNegateable() {
      return false;
    }

    @Override
    public ValueType plusType(ValueType rhs) {
      return STRING;
    }

    @Override
    public void writeTo(CodedOutputStream output, Object value) throws IOException {
      boolean present = value != null;
      output.writeBoolNoTag(present);
      if (present) {
        String string = (String) value;
        output.writeStringNoTag(string);
      }
    }

    @Override
    public Object readFrom(CodedInputStream input) throws IOException {
      if (input.readBool()) {
        return input.readString();
      }
      return null;
    }
  },
  PERCENT {
    @Override
    public Class<?> getTypeClass() {
      return BigDecimal.class;
    }

    @Override
    public Object parseFromString(String string) {
      if (isBlank(string)) {
        return null;
      }
      return BigDecimal.valueOf(Double.parseDouble(string));
    }

    @Override
    public boolean isNegateable() {
      return false;
    }

    @Override
    public void writeTo(CodedOutputStream output, Object value) throws IOException {
      boolean present = value != null;
      output.writeBoolNoTag(present);
      if (present) {
        BigDecimal decimal = (BigDecimal) value;
        output.writeStringNoTag(decimal.toString());
      }
    }

    @Override
    public Object readFrom(CodedInputStream input) throws IOException {
      if (input.readBool()) {
        return new BigDecimal(input.readString());
      }
      return null;
    }

  };

  public Object negate(Object value) {
    throw new UnsupportedOperationException();
  }

  public Object not(Object value) {
    throw new UnsupportedOperationException();
  }

  public <T> BinaryOperator<T> sumOp() {
    throw new UnsupportedOperationException();
  }

  public <T> BinaryOperator<T> multOp() {
    throw new UnsupportedOperationException();
  }

  public ValueType plusType(ValueType rhs) {
    if (rhs == STRING) {
      return STRING;
    }
    return null;
  }

  public ValueType minusType(ValueType rhs) {
    return null;
  }

  public ValueType multiplyType(ValueType rhs) {
    return null;
  }

  public ValueType divideByType(ValueType rhs) {
    return null;
  }

  public boolean canEqualWith(ValueType rhs) {
    return this == rhs;
  }

  public boolean canOrderWith(ValueType rhs) {
    return this == rhs;
  }

  public Object parseFromStringWithUnit(String value, String unit) {
    return parseFromString(value);
  }

  @Override
  public String getName() {
    return name();
  }

  @Override
  public byte getTypeCode() {
    return (byte) ordinal();
  }
}
