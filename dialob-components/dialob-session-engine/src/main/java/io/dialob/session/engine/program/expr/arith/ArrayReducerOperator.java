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
import io.dialob.session.engine.program.EvalContext;
import io.dialob.session.engine.program.model.Expression;
import io.dialob.session.engine.session.command.EventMatcher;
import org.immutables.value.Value;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.BinaryOperator;

@Value.Builder
@Value.Style(
  jakarta = true,
  jdkOnly = true,
  overshadowImplementation = true,
  visibility = Value.Style.ImplementationVisibility.PACKAGE
)
public record ArrayReducerOperator<T>(
  BinaryOperator<T> reducer,

  Expression arrayExpression,

  @Nullable
  Object placeholderValue

) implements Expression {

  public static final class Builder<T> extends ArrayReducerOperatorBuilder<T> {}

  public static <OT> Expression of(BinaryOperator<OT> binaryOperator, CollectRowFieldsOperator of) {
    return new ArrayReducerOperator.Builder<OT>()
      .reducer(binaryOperator)
      .arrayExpression(of)
      .build();
  }

  @Nullable
  @Override
  public Object eval(@NonNull EvalContext evalContext) {
    final List<T> values = (List<T>) arrayExpression().eval(evalContext);
    if (values == null) {
      return null;
    }
    return values
      .stream()
      .filter(Objects::nonNull)
      .reduce(reducer())
      .orElse((T) placeholderValue());
  }

  @NonNull
  @Override
  public ValueType getValueType() {
    return arrayExpression().getValueType().getItemValueType();
  }

  @NonNull
  @Override
  public Set<EventMatcher> getEvalRequiredConditions() {
    return arrayExpression().getEvalRequiredConditions();
  }

  public static final BinaryOperator<Object> ANSWER_COUNT = (result, element) -> {
    if (element != null) {
      return result == null ? 1 : ((BigInteger)result).add(BigInteger.ONE);
    }
    return result;
  };
  public static final BinaryOperator<BigInteger> INTEGER_SUM = (result, element) -> {
    if (element != null) {
      return result == null ? element : result.add(element);
    }
    return result;
  };
  public static final BinaryOperator<BigDecimal> DECIMAL_SUM = (result, element) -> {
    if (element != null) {
      return result == null ? element : result.add(element);
    }
    return result;
  };
  public static final BinaryOperator<BigInteger> INTEGER_MIN = (result, element) -> {
    if (element != null) {
      return result == null ? element : result.min(element);
    }
    return result;
  };
  public static final BinaryOperator<BigDecimal> DECIMAL_MIN = (result, element) -> {
    if (element != null) {
      return result == null ? element : result.min(element);
    }
    return result;
  };
  public static final BinaryOperator<BigInteger> INTEGER_MAX = (result, element) -> {
    if (element != null) {
      return result == null ? element : result.max(element);
    }
    return result;
  };
  public static final BinaryOperator<BigDecimal> DECIMAL_MAX = (result, element) -> {
    if (element != null) {
      return result == null ? element : result.max(element);
    }
    return result;
  };

  public static final BinaryOperator<Boolean> ANY = (result, element) -> {
    if (element != null) {
      return result == null ? element : result || element;
    }
    return result;
  };

  public static final BinaryOperator<Boolean> ALL = (result, element) -> {
    if (element != null) {
      return result == null ? element : result && element;
    }
    return result;
  };



  public static BinaryOperator<? extends Number> sumOp(ValueType valueType) {
    if (valueType == ValueType.INTEGER) {
      return INTEGER_SUM;
    }
    if (valueType == ValueType.DECIMAL) {
      return DECIMAL_SUM;
    }
    return null;
  }

  public static BinaryOperator<? extends Number> minOp(ValueType valueType) {
    if (valueType == ValueType.INTEGER) {
      return INTEGER_MIN;
    }
    if (valueType == ValueType.DECIMAL) {
      return DECIMAL_MIN;
    }
    return null;
  }

  public static BinaryOperator<? extends Number> maxOp(ValueType valueType) {
    if (valueType == ValueType.INTEGER) {
      return INTEGER_MAX;
    }
    if (valueType == ValueType.DECIMAL) {
      return DECIMAL_MAX;
    }
    return null;
  }


  public static BinaryOperator<Boolean> allOp(ValueType valueType) {
    if (valueType == ValueType.BOOLEAN) {
      return ALL;
    }
    return null;
  }

  public static BinaryOperator<Boolean> anyOp(ValueType valueType) {
    if (valueType == ValueType.BOOLEAN) {
      return ANY;
    }
    return null;
  }



}
