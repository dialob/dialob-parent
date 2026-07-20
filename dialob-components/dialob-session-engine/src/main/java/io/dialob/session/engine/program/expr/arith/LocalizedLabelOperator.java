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
package io.dialob.session.engine.program.expr.arith;

import edu.umd.cs.findbugs.annotations.NonNull;
import io.dialob.rule.parser.api.CompilerErrorCode;
import io.dialob.rule.parser.api.ValueType;
import io.dialob.session.engine.program.EvalContext;
import io.dialob.session.engine.program.ProgramBuilder;
import io.dialob.session.engine.program.expr.FormatExpressionException;
import io.dialob.session.engine.program.model.Expression;
import io.dialob.session.engine.program.model.Label;
import io.dialob.session.engine.session.command.EventMatcher;
import io.dialob.session.engine.session.model.ItemId;
import io.dialob.session.engine.session.model.ValueSetId;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Strings;
import org.immutables.value.Value;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static io.dialob.session.engine.program.expr.arith.ToLowerCaseOperator.lowerCaseOf;
import static io.dialob.session.engine.program.expr.arith.ToUpperCaseOperator.upperCaseOf;

@Value.Builder
@Value.Style(
  jakarta = true,
  jdkOnly = true,
  jdk9Collections = true,
  overshadowImplementation = true,
  visibility = Value.Style.ImplementationVisibility.PACKAGE
)
public record LocalizedLabelOperator(
  Map<String, Expression> value
) implements Expression {

  static final Pattern EXPRESSION_PATTERN = Pattern.compile("\\{([\\w]*?)(:.*?)?}");

  public static LocalizedLabelOperator createLocalizedLabelOperator(@NonNull ProgramBuilder programBuilder, @NonNull Label label) {
    Map<String, Expression> value = new HashMap<>();
    label.labels().forEach((key, labelString) -> {
      int i = 0;
      final Matcher matcher = EXPRESSION_PATTERN.matcher(labelString);
      final List<Expression> expressions = new ArrayList<>();
      while (matcher.find()) {
        expressions.add(Constant.builder().value(labelString.substring(i, matcher.start())).valueType(ValueType.STRING).build());
        if (matcher.group(1) == null) {
          expressions.add(Constant.builder().value(matcher.group(0)).valueType(ValueType.STRING).build());
        } else {
          ItemId itemId = programBuilder.findItemBuilder(matcher.group(1))
            .get().getId();
          var variableReference = Operators.var(itemId, ValueType.STRING);
          String format = matcher.group(2);
          if (StringUtils.isNotBlank(format)) {
            // Drop leading ':'
            format = Strings.CS.removeStart(format, ":");
            switch (format) {
              case "key":
                expressions.add(ToStringOperator.of(variableReference));
                break;
              case "lowercase":
                expressions.add(lowerCaseOf(toStringExpression(programBuilder, itemId, variableReference)));
                break;
              case "uppercase":
                expressions.add(upperCaseOf(toStringExpression(programBuilder, itemId, variableReference)));
                break;
              default:
                expressions.add(FormatOperator.of(variableReference, format));
            }
          } else {
            expressions.add(toStringExpression(programBuilder, itemId, variableReference));
          }
        }
        i = matcher.end();
      }
      String ending = labelString.substring(i);
      if (StringUtils.isNotEmpty(ending)) {
        expressions.add(Constant.builder().value(ending).valueType(ValueType.STRING).build());
      }
      if (!expressions.isEmpty()) {
        value.put(key, expressions.size() > 1 ? new ConcatOperator.Builder().addAllExpressions(expressions).build() : expressions.getFirst());
      }
    });

    return LocalizedLabelOperator.of(value);
  }

  public static Expression createFormatExpression(@NonNull ProgramBuilder programBuilder, @NonNull String template) {
    final String formatKey = "__dialob_format__";
    final LocalizedLabelOperator operator;
    try {
      operator = createLocalizedLabelOperator(programBuilder, Label.of(Collections.singletonMap(formatKey, template)));
    } catch (NoSuchElementException e) {
      throw new FormatExpressionException(CompilerErrorCode.UNKNOWN_FORMAT_ITEM);
    }
    Expression expression = operator.value().get(formatKey);
    if (expression == null) {
      return Constant.builder().value("").valueType(ValueType.STRING).build();
    }
    return expression;
  }

  public static LocalizedLabelOperator of(Map<String, Expression> value) {
    return new LocalizedLabelOperator(value);
  }

  static Expression toStringExpression(@NonNull ProgramBuilder programBuilder, ItemId itemId, VariableReference variableReference) {
    Optional<String> valueSetId = programBuilder.findValueSetIdForItem(itemId);
    if (valueSetId.isPresent()) {
      return ValueSetEntryToStringOperator.of(new ValueSetId(valueSetId.get()), variableReference);
    }
    if (programBuilder.isBooleanItem(itemId)) {
      return LocalizedBooleanToStringOperator.of(variableReference);
    }
    return ToStringOperator.of(variableReference);
  }


  @Override
  public String eval(@NonNull EvalContext evalContext) {
    Expression expression = value().get(evalContext.getLanguage());
    if (expression == null) {
      return null;
    }
    return (String) expression.eval(evalContext);
  }

  @NonNull
  @Override
  public Set<EventMatcher> getEvalRequiredConditions() {
    return value()
      .values()
      .stream()
      .map(Expression::getEvalRequiredConditions)
      .flatMap(Collection::stream)
      .collect(Collectors.toUnmodifiableSet());
  }

  @NonNull
  @Override
  public ValueType getValueType() {
    return ValueType.STRING;
  }

}
