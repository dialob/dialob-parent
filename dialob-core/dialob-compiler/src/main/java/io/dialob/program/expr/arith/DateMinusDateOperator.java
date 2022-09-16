/*
 * Copyright © 2015 - 2021 ReSys (info@dialob.io)
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
package io.dialob.program.expr.arith;

import io.dialob.program.EvalContext;
import io.dialob.rule.parser.api.ValueType;

import org.immutables.value.Value;

import javax.annotation.Nonnull;
import java.time.LocalDate;
import java.time.Period;

@Value.Immutable
public interface DateMinusDateOperator extends InfixOperator<Period> {

  @Override
  default Object eval(@Nonnull EvalContext evalContext) {
    LocalDate localDate = (LocalDate) getLhs().eval(evalContext);
    LocalDate localDate2 = (LocalDate) getRhs().eval(evalContext);
    if (localDate == null || localDate2 == null) {
      return null;
    }
    return Period.between(localDate2, localDate);
  }

  @Nonnull
  @Override
  default ValueType getValueType() {
    return ValueType.PERIOD;
  }
}
