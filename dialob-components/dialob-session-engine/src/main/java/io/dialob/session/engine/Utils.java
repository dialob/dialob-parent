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

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import io.dialob.api.form.FormValidationError;
import io.dialob.api.proto.ActionItem;
import io.dialob.api.proto.ValueSet;
import io.dialob.api.proto.ValueSetEntry;
import io.dialob.api.questionnaire.Error;
import io.dialob.common.Constants;
import io.dialob.rule.parser.ParserUtil;
import io.dialob.rule.parser.api.ValueType;
import io.dialob.session.engine.session.model.ErrorState;
import io.dialob.session.engine.session.model.IdUtils;
import io.dialob.session.engine.session.model.ItemState;
import io.dialob.session.engine.session.model.ValueSetState;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Period;
import java.util.Collection;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;

public final class Utils {

  private Utils() {}

  public static Optional<ValueType> mapQuestionTypeToValueType(String type) {
    if (type != null) {
      return Optional.ofNullable(switch (type) {
        case Constants.TEXT, Constants.LIST, Constants.SURVEY -> ValueType.STRING;
        case Constants.BOOLEAN -> ValueType.BOOLEAN;
        case Constants.DATE -> ValueType.DATE;
        case Constants.TIME -> ValueType.TIME;
        case Constants.NUMBER -> ValueType.INTEGER;
        case Constants.DECIMAL -> ValueType.DECIMAL;
        case Constants.MULTICHOICE -> ValueType.arrayOf(ValueType.STRING);
        case Constants.ROWGROUP -> ValueType.arrayOf(ValueType.INTEGER);
        // ROW, NOTE, GROUP, QUESTIONNAIRE, SURVEYGROUP
        default -> null;
      });
    }
    return Optional.empty();
  }

  public static boolean isVariable(@NonNull String type) {
    return isProgramVariable(type) || isContextVariable(type);
  }

  public static boolean isContextVariable(@NonNull String type) {
    return Constants.CONTEXT.equals(type);
  }

  public static boolean isProgramVariable(@NonNull String type) {
    return Constants.VARIABLE.equals(type);
  }

  public static boolean isNote(@NonNull String type) {
    return Constants.NOTE.equals(type);
  }

  public static boolean isRowgroup(@NonNull String type) {
    return Constants.ROWGROUP.equals(type);
  }


  public static boolean isQuestionType(@NonNull ItemState itemState) {
    return switch (itemState.getType()) {
      case Constants.QUESTIONNAIRE, Constants.GROUP, Constants.NOTE, Constants.VARIABLE, Constants.CONTEXT, Constants.SURVEYGROUP -> false;
        // rows are not questions, but row containers answer holds row order on answer.
      case Constants.ROWGROUP -> itemState.getPrototypeId() == null;
      default -> true;
    };
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
      if (value instanceof String string) {
        try {
          return valueType.parseFromString(string);
        } catch (Exception ignored) {
        }
      }
      return valueType.coerceFrom(value);
    }
    if (value instanceof Collection collection) {
      return collection.stream().map(item -> item instanceof Integer i ? BigInteger.valueOf(i) : item).toList();
    }
    // TODO handle array answers
    return null;
  }

  @NonNull
  public static ValueSet toValueSet(@NonNull ValueSetState valueSetState) {
    return new ValueSet.Builder()
      .id(IdUtils.toString(valueSetState.id()))
      .entries(valueSetState.entries().stream().map(entry -> new ValueSetEntry.Builder().key(entry.id()).value(entry.label()).build()).toList()).build();
  }

  @NonNull
  public static Error toError(@NonNull ErrorState updated) {
    return new Error.Builder()
      .id(IdUtils.toString(updated.itemId()))
      .code(updated.code())
      .description(updated.label()).build();
  }

  @NonNull
  public static ActionItem toActionItem(@NonNull ItemState itemState, UnaryOperator<ActionItem.Builder> post) {
    Object value;
    if (isVariable(itemState.getType())) {
      value = itemState.getValue();
    } else {
      value = itemState.getAnswer();
    }
    final ActionItem.Builder actionItemBuilder = new ActionItem.Builder()
      .disabled(itemState.isDisabled() ? true : null)
      .inactive(!itemState.isActive() ? true : null)
      .activeItem(itemState.getActivePage().map(IdUtils::toString).orElse(null))
      .answered(itemState.isAnswered())
      .view(itemState.getView())
      .id(IdUtils.toString(itemState.id()))
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
      actionItemBuilder.items(itemState.getItems().stream().map(IdUtils::toString).toList());
    }
    actionItemBuilder.label(itemState.getLabel());
    actionItemBuilder.description(itemState.getDescription());
    if (!itemState.getAllowedActions().isEmpty()) {
      actionItemBuilder.allowedActions(itemState.getAllowedActions());
    }
    if (!itemState.getAvailableItems().isEmpty()) {
      actionItemBuilder.availableItems(itemState.getAvailableItems().stream().map(IdUtils::toString).toList());
    }
    itemState.getValueSetId().ifPresent(actionItemBuilder::valueSetId);
    if (post != null) {
      post.apply(actionItemBuilder);
    }
    return actionItemBuilder.build();
  }


  public static Object validateDefaultValue(String id, ValueType valueType, Object value, Consumer<FormValidationError> errorListener) {
    if (value == null) {
      return null;
    }
    if (value instanceof String s) {
      try {
        return valueType.parseFromString(s);
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
    if (value instanceof Integer i && valueType == ValueType.INTEGER) {
      return BigInteger.valueOf(i);
    }
    if (value instanceof Long l && valueType == ValueType.INTEGER) {
      return BigInteger.valueOf(l);
    }
    if (value instanceof Double aDouble && valueType == ValueType.DECIMAL) {
      return BigDecimal.valueOf(aDouble);
    }
    errorListener.accept(createError(id, "INVALID_DEFAULT_VALUE"));
    return null;
  }

  public static FormValidationError createError(String itemId, String message) {
    return new FormValidationError.Builder()
      .type(FormValidationError.Type.GENERAL)
      .level(FormValidationError.Level.ERROR)
      .message(message)
      .itemId(itemId)
      .build();
  }

}
