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
import io.dialob.rule.parser.PeriodUtil;
import io.dialob.rule.parser.api.ValueType;
import io.dialob.session.engine.program.EvalContext;
import io.dialob.session.engine.program.model.Expression;
import org.immutables.value.Value;

import java.time.Duration;
import java.time.LocalTime;

@Value.Builder
@Value.Style(
  jakarta = true,
  jdkOnly = true,
  jdk9Collections = true,
  overshadowImplementation = true,
  visibility = Value.Style.ImplementationVisibility.PACKAGE
)
public record TimeMinusDurationOperator(
  Expression lhs,
  Expression rhs
) implements InfixOperator {

  public static final class Builder extends TimeMinusDurationOperatorBuilder {}

  @Override
  public Object eval(@NonNull EvalContext evalContext) {
    LocalTime localTime = (LocalTime) getLhs().eval(evalContext);
    Duration duration = (Duration) getRhs().eval(evalContext);
    if (duration == null || localTime == null) {
      return null;
    }
    return PeriodUtil.timeMinusDuration(localTime, duration);
  }

  @NonNull
  @Override
  public ValueType getValueType() {
    return ValueType.TIME;
  }
}
