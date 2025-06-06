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
import org.immutables.value.Value;

import java.time.LocalDate;
import java.time.Period;

@Value.Immutable
public interface DateMinusPeriodOperator extends InfixOperator<LocalDate> {

  @Override
  default Object eval(@NonNull EvalContext evalContext) {
    LocalDate localDate = (LocalDate) getLhs().eval(evalContext);
    Period period = (Period) getRhs().eval(evalContext);
    if (period == null || localDate == null) {
      return null;
    }
    return PeriodUtil.dateMinusPeriod(localDate, period);
  }

  @NonNull
  @Override
  default ValueType getValueType() {
    return ValueType.DATE;
  }
}
