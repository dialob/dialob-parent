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
package io.dialob.rule.parser;

import edu.umd.cs.findbugs.annotations.NonNull;
import io.dialob.common.Constants;
import io.dialob.rule.parser.api.ValueType;

public final class ParserUtil {

  private ParserUtil() {}

  public static boolean isReducerOperator(@NonNull String reducer) {
    return "sumOf".equals(reducer)
      || "minOf".equals(reducer)
      || "maxOf".equals(reducer)
      || "allOf".equals(reducer)
      || "anyOf".equals(reducer);
  }

  public static ValueType itemTypeToValueType(@NonNull String itemType) {
    return switch (itemType) {
      case Constants.TEXT, Constants.LIST, Constants.NOTE, Constants.SURVEY -> ValueType.STRING;
      case Constants.BOOLEAN -> ValueType.BOOLEAN;
      case Constants.DATE -> ValueType.DATE;
      case Constants.TIME -> ValueType.TIME;
      case Constants.NUMBER -> ValueType.INTEGER;
      case Constants.DECIMAL -> ValueType.DECIMAL;
      case Constants.MULTICHOICE -> ValueType.arrayOf(ValueType.STRING);
      case Constants.ROWGROUP -> ValueType.arrayOf(ValueType.INTEGER);
      case Constants.QUESTIONNAIRE, Constants.CONTEXT, Constants.VARIABLE, Constants.GROUP, Constants.SURVEYGROUP, Constants.ROW -> null;
      default -> throw new RuntimeException("Unsupported item type %s".formatted(itemType));
    };
  }

}
