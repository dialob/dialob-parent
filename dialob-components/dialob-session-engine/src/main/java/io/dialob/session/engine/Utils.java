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
package io.dialob.session.engine;

import com.google.common.collect.ImmutableList;
import com.google.protobuf.CodedInputStream;
import com.google.protobuf.CodedOutputStream;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import io.dialob.api.form.FormValidationError;
import io.dialob.api.form.ImmutableFormValidationError;
import io.dialob.api.proto.*;
import io.dialob.api.questionnaire.Error;
import io.dialob.api.questionnaire.ImmutableError;
import io.dialob.common.Constants;
import io.dialob.rule.parser.ParserUtil;
import io.dialob.rule.parser.api.ValueType;
import io.dialob.session.engine.session.model.ErrorState;
import io.dialob.session.engine.session.model.IdUtils;
import io.dialob.session.engine.session.model.ItemState;
import io.dialob.session.engine.session.model.ValueSetState;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Period;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

public class Utils {

  public static Optional<ValueType> mapQuestionTypeToValueType(String type) {
    if (type != null) {
      switch (type) {
        case "text":
          return Optional.of(ValueType.STRING);
        case "boolean":
          return Optional.of(ValueType.BOOLEAN);
        case "list":
          return Optional.of(ValueType.STRING);
        case "date":
          return Optional.of(ValueType.DATE);
        case "time":
          return Optional.of(ValueType.TIME);
        case "number":
          return Optional.of(ValueType.INTEGER);
        case "decimal":
          return Optional.of(ValueType.DECIMAL);
        case "multichoice":
          return Optional.of(ValueType.arrayOf(ValueType.STRING));
        case "rowgroup":
          return Optional.of(ValueType.arrayOf(ValueType.INTEGER));
        case "survey":
          return Optional.of(ValueType.STRING);
        case "row":
        case "note":
        case "group":
        case "questionnaire":
        case "surveygroup":
          return Optional.empty();
      }
    }
    return Optional.empty();
  }

  public static boolean isVariable(@NonNull String type) {
    return isProgramVariable(type) || isContextVariable(type);
  }

  public static boolean isContextVariable(@NonNull String type) {
    return "context".equals(type);
  }

  public static boolean isProgramVariable(@NonNull String type) {
    return "variable".equals(type);
  }

  public static boolean isNote(@NonNull String type) {
    return "note".equals(type);
  }

  public static boolean isRowgroup(@NonNull String type) {
    return "rowgroup".equals(type);
  }


  @NonNull
  public static String mapValueTypeToType(@NonNull ValueType valueType) {
    if (valueType == ValueType.STRING) {
      return "text";
    }
    if (valueType == ValueType.BOOLEAN) {
      return "boolean";
    }
    if (valueType.isArray()) {
      return "list";
    }
    if (valueType == ValueType.DATE) {
      return "date";
    }
    if (valueType == ValueType.TIME) {
      return "time";
    }
    if (valueType == ValueType.INTEGER) {
      return "number";
    }
    if (valueType == ValueType.DECIMAL) {
      return "decimal";
    }
    throw new RuntimeException("Unknown question type " + valueType);
  }

  public static boolean isGroupType(@NonNull ItemState itemState) {
    return !isQuestionType(itemState);
  }

  public static boolean isQuestionType(@NonNull ItemState itemState) {
    switch (itemState.getType()) {
      case Constants.QUESTIONNAIRE:
      case "group":
      case "note":
      case "variable":
      case "context":
      case "surveygroup":
        return false;
      case "rowgroup":
        // rows are not questions, but row containers answer holds row order on answer.
        return itemState.getPrototypeId() == null;
      default:
        return true;
    }
  }

  @Nullable
  public static Object parse(@NonNull String type, Object answer) {
    if (isVariable(type)) {
      return answer;
    }
    ValueType valueType = ParserUtil.itemTypeToValueType(type);
    if (valueType != null) {
      return parse(valueType, answer);
    }
    return null;
  }

  @Nullable
  public static Object parse(@NonNull ValueType valueType, Object value) {
    if (value == null) {
      return null;
    }
    if (!valueType.isArray()) {
      if (value instanceof String) {
        try {
          return valueType.parseFromString((String) value);
        } catch (Exception ignored) {
        }
      }
      return valueType.coerceFrom(value);
    }
    if (value instanceof Collection) {
      return ((Collection)value).stream().map(i -> i instanceof Integer ? BigInteger.valueOf((Integer)i) : i).collect(Collectors.toList());
    }
    // TODO handle array answers
    return null;
  }

  @NonNull
  public static ValueSet toValueSet(@NonNull ValueSetState valueSetState) {
    return ImmutableValueSet.builder()
      .id(IdUtils.toString(valueSetState.getId()))
      .entries(valueSetState.getEntries().stream().map(entry -> ImmutableValueSetEntry.builder().key(entry.getId()).value(entry.getLabel()).build()).collect(Collectors.toList())).build();
  }

  @NonNull
  public static Error toError(@NonNull ErrorState updated) {
    return ImmutableError.builder()
      .id(IdUtils.toString(updated.getItemId()))
      .code(updated.getCode())
      .description(updated.getLabel()).build();
  }

  @NonNull
  public static ActionItem toActionItem(@NonNull ItemState itemState, UnaryOperator<ImmutableActionItem.Builder> post) {
    Object value;
    if (isVariable(itemState.getType())) {
      value = itemState.getValue();
    } else {
      value = itemState.getAnswer();
    }
    final ImmutableActionItem.Builder actionItemBuilder = ImmutableActionItem.builder()
      .disabled(itemState.isDisabled() ? true : null)
      .inactive(!itemState.isActive() ? true : null)
      .activeItem(itemState.getActivePage().map(IdUtils::toString).orElse(null))
      .answered(itemState.isAnswered())
      .view(itemState.getView())
      .id(IdUtils.toString(itemState.getId()))
      .type(itemState.getType())
      .value(value);
    if (itemState.isRequired()) {
      actionItemBuilder.required(true);
    }
    if (!itemState.getClassNames().isEmpty()) {
      actionItemBuilder.className(itemState.getClassNames());
    }
    if (!itemState.getItems().isEmpty()) {
      // TODO handled indexed references
      actionItemBuilder.items(itemState.getItems().stream().map(IdUtils::toString).collect(Collectors.toList()));
    }
    actionItemBuilder.label(itemState.getLabel());
    actionItemBuilder.description(itemState.getDescription());
    if (!itemState.getAllowedActions().isEmpty()) {
      actionItemBuilder.allowedActions(itemState.getAllowedActions());
    }
    if (!itemState.getAvailableItems().isEmpty()) {
      actionItemBuilder.availableItems(itemState.getAvailableItems().stream().map(IdUtils::toString).collect(Collectors.toList()));
    }
    itemState.getValueSetId().ifPresent(actionItemBuilder::valueSetId);
    if (post != null) {
      post.apply(actionItemBuilder);
    }
    return actionItemBuilder.build();
  }

  public static void writeNullableString(@NonNull CodedOutputStream output, String string) throws IOException {
    if (string == null) {
      output.writeBoolNoTag(false);
    } else {
      output.writeBoolNoTag(true);
      output.writeStringNoTag(string);
    }
  }

  @Nullable
  public static String readNullableString(@NonNull CodedInputStream input) throws IOException {
    if (input.readBool()) {
      return input.readString();
    }
    return null;
  }


  public static void writeNullableDate(@NonNull CodedOutputStream output, Date date) throws IOException {
    if (date == null) {
      output.writeBoolNoTag(false);
    } else {
      output.writeBoolNoTag(true);
      output.writeInt64NoTag(date.getTime());
    }
  }

  public static Date readNullableDate(@NonNull CodedInputStream input) throws IOException {
    if (input.readBool()) {
      return new Date(input.readInt64());
    }
    return null;
  }


  public static void writeObjectValue(@NonNull CodedOutputStream output, Object value) throws IOException {
    final boolean present = value != null;
    output.writeBoolNoTag(present);
    if (present) {
      if (value instanceof String) {
        output.write((byte) 1);
        output.writeStringNoTag((String) value);
      } else if (value instanceof BigInteger) {
        output.write((byte) 2);
        output.writeInt32NoTag((Integer) value);
      } else if (value instanceof Boolean) {
        output.write((byte) 3);
        output.writeBoolNoTag((Boolean) value);
      } else if (value instanceof Double) {
        output.write((byte) 4);
        output.writeDoubleNoTag((Double) value);
      } else if (value instanceof List) {
        List listValue = (List) value;
        final int size = listValue.size();
        if (size == 0) {
          output.write((byte) 0x80); // empty list
          return;
        }
        if (listValue.get(0) instanceof String) {
          output.write((byte) 0x81);
          output.writeInt32NoTag(size);
          for (String s : (List<String>)listValue) {
            output.writeStringNoTag(s);
          }
        } else if (listValue.get(0) instanceof BigInteger) {
          output.write((byte) 0x82);
          output.writeInt32NoTag(size);
          for (BigInteger i : (List<BigInteger>)listValue) {
            writeBigInteger(output, i);
          }
        }
      } else {
        throw new RuntimeException("Unknown answer value: " + value.getClass());
      }
    }
  }

  public static void writeBigInteger(@NonNull CodedOutputStream output, @NonNull BigInteger value) throws IOException {
    var bytes = value.toByteArray();
    output.writeInt32NoTag(bytes.length);
    output.writeRawBytes(bytes);
  }

  public static Object readObjectValue(@NonNull CodedInputStream input) throws IOException {
    if (input.readBool()) {
      byte answerType = input.readRawByte();
      int count;
      switch(answerType) {
        case 1:
          return input.readString();
        case 2:
          return input.readInt32();
        case 3:
          return input.readBool();
        case 4:
          return input.readDouble();
        case (byte) 0x80:
          return ImmutableList.of();
        case (byte) 0x81:
          count = input.readInt32();
          String[] strings = new String[count];
          for (int i = 0; i < count; ++i) {
            strings[i] = input.readString();
          }
          return ImmutableList.copyOf(strings);
        case (byte) 0x82:
          count = input.readInt32();
          BigInteger[] integers = new BigInteger[count];
          for (int i = 0; i < count; ++i) {
            integers[i] = readBigInteger(input);
          }
          return ImmutableList.copyOf(integers);
      }
    }
    return null;
  }

  public static BigInteger readBigInteger(@NonNull CodedInputStream input) throws IOException {
    var size = input.readInt32();
    var bytes = input.readRawBytes(size);
    return new BigInteger(bytes);
  }

  public static Object validateDefaultValue(String id, ValueType valueType, Object value, Consumer<FormValidationError> errorListener) {
    if (value == null) {
      return null;
    }
    if (value instanceof String) {
      try {
        return valueType.parseFromString((String) value);
      } catch (Exception e) {
        errorListener.accept(createError(id, "INVALID_DEFAULT_VALUE"));
        return null;
      }
    }
    if (value instanceof Boolean && valueType == ValueType.BOOLEAN) {
      return value;
    }
    if (value instanceof LocalDate && valueType == ValueType.DATE) {
      return value;
    }
    if (value instanceof LocalTime && valueType == ValueType.TIME) {
      return value;
    }
    if (value instanceof Period && valueType == ValueType.PERIOD) {
      return value;
    }
    if (value instanceof Duration && valueType == ValueType.DURATION) {
      return value;
    }
    if (value instanceof BigDecimal && valueType == ValueType.DECIMAL) {
      return value;
    }
    if (value instanceof Integer && valueType == ValueType.INTEGER) {
      return value;
    }
    if (value instanceof Double && valueType == ValueType.DECIMAL) {
      return BigDecimal.valueOf((Double) value);
    }
    errorListener.accept(createError(id, "INVALID_DEFAULT_VALUE"));
    return null;
  }

  public static FormValidationError createError(String itemId, String message) {
    return ImmutableFormValidationError.builder()
      .type(FormValidationError.Type.GENERAL)
      .level(FormValidationError.Level.ERROR)
      .message(message)
      .itemId(itemId)
      .build();
  }

}
