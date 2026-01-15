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
import io.dialob.rule.parser.api.ValueType;
import io.dialob.session.engine.program.EvalContext;
import io.dialob.session.engine.program.expr.OutputFormatter;
import io.dialob.session.engine.program.model.Expression;
import io.dialob.session.engine.session.command.EventMatcher;
import org.immutables.value.Value;

import java.util.Set;

@Value.Builder
@Value.Style(
  jakarta = true,
  jdkOnly = true,
  jdk9Collections = true,
  overshadowImplementation = true,
  visibility = Value.Style.ImplementationVisibility.PACKAGE
)
public record FormatOperator(
  Expression expression,
  String format
) implements Expression {

  public static class Builder extends FormatOperatorBuilder {}

  public static FormatOperator of(@NonNull Expression expression, String format) {
    return new FormatOperator.Builder().expression(expression).format(format).build();
  }

  @Override
  public String eval(@NonNull EvalContext context) {
    Object eval = expression().eval(context);
    if (eval == null) {
      return null;
    }
    var outputFormatter = new OutputFormatter(context.getLanguage());
    return outputFormatter.format(eval, format());
  }

  @NonNull
  @Override
  public ValueType getValueType() {
    return ValueType.STRING;
  }

  @NonNull
  @Override
  public Set<EventMatcher> getEvalRequiredConditions() {
    return expression().getEvalRequiredConditions();
  }

}
