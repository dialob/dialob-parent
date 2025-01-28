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
import io.dialob.rule.parser.api.ValueType;

public class ParserUtil {

  public static boolean isReducerOperator(@NonNull String reducer) {
    return "sumOf".equals(reducer)
      || "minOf".equals(reducer)
      || "maxOf".equals(reducer)
      || "allOf".equals(reducer)
      || "anyOf".equals(reducer);
  }

  public static ValueType itemTypeToValueType(@NonNull String itemType) {
    return switch (itemType) {
      case "text", "list", "note", "survey" -> ValueType.STRING;
      case "boolean" -> ValueType.BOOLEAN;
      case "date" -> ValueType.DATE;
      case "time" -> ValueType.TIME;
      case "number" -> ValueType.INTEGER;
      case "decimal" -> ValueType.DECIMAL;
      case "multichoice" -> ValueType.arrayOf(ValueType.STRING);
      case "rowgroup" -> ValueType.arrayOf(ValueType.INTEGER);
      case "questionnaire", "context", "variable", "group", "surveygroup", "row" -> null;
      default -> throw new RuntimeException(String.format("Unsupported item type %s", itemType));
    };
  }

}
