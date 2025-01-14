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
import edu.umd.cs.findbugs.annotations.Nullable;
import io.dialob.rule.parser.api.ValueType;
import io.dialob.session.engine.program.expr.OperatorSymbol;
import io.dialob.session.engine.program.model.Expression;

import java.time.LocalTime;

public class TimeOperators  extends ComparableTypeOperators<LocalTime> {

  private static final Expression TODAY = ImmutableTodayExpression.builder().build();

  private static final Expression NOW = ImmutableNowExpression.builder().build();

  @NonNull
  public static Expression today() {
    return TODAY;
  }

  @NonNull
  public static Expression now() {
    return NOW;
  }

  @Nullable
  public static Expression createOperator(@NonNull OperatorSymbol operator, @NonNull Expression lhs, @NonNull Expression rhs) {
    ValueType lhsValueType = lhs.getValueType();
    ValueType rhsValueType = rhs.getValueType();
    if (operator == OperatorSymbol.MINUS) {
      if (lhsValueType == ValueType.DATE) {
        if (rhsValueType == ValueType.DATE) {
          return ImmutableDateMinusDateOperator.builder().lhs(lhs).rhs(rhs).build();
        }
        if (rhsValueType == ValueType.PERIOD) {
          return ImmutableDateMinusPeriodOperator.builder().lhs(lhs).rhs(rhs).build();
        }
      } else if (lhsValueType == ValueType.TIME) {
        if (rhsValueType == ValueType.TIME) {
          return ImmutableTimeMinusTimeOperator.builder().lhs(lhs).rhs(rhs).build();
        }
        if (rhsValueType == ValueType.DURATION) {
          return ImmutableTimeMinusDurationOperator.builder().lhs(lhs).rhs(rhs).build();
        }
      }
    } else if (operator == OperatorSymbol.PLUS) {
      if (lhsValueType == ValueType.DATE && rhsValueType == ValueType.PERIOD) {
        return ImmutableDatePlusPeriodOperator.builder().lhs(lhs).rhs(rhs).build();
      }
      if (lhsValueType == ValueType.TIME && rhsValueType == ValueType.DURATION) {
        return ImmutableTimePlusDurationOperator.builder().lhs(lhs).rhs(rhs).build();
      }
    }
    return null;
  }
}
