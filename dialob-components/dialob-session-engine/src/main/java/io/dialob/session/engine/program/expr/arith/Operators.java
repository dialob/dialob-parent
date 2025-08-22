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
import io.dialob.session.engine.program.model.Expression;
import io.dialob.session.engine.session.model.IdUtils;
import io.dialob.session.engine.session.model.ItemId;

public interface Operators {

  InfixOperator eq(Expression lhs, Expression rhs);

  InfixOperator ne(Expression lhs, Expression rhs);

  InfixOperator le(Expression lhs, Expression rhs);

  InfixOperator lt(Expression lhs, Expression rhs);

  InfixOperator ge(Expression lhs, Expression rhs);

  InfixOperator gt(Expression lhs, Expression rhs);

  static Expression and(@NonNull Expression ...expressions) {
    return ImmutableBinaryOperator.<Boolean>builder().addNodes(expressions).reducer(Reducers.Bool.AND).build();
  }

  static Expression or(@NonNull Expression ...expressions) {
    return ImmutableBinaryOperator.<Boolean>builder().addNodes(expressions).reducer(Reducers.Bool.OR).build();
  }

  static Expression isAnswered(ItemId id) {
    return ImmutableIsAnsweredOperator.builder().questionId(id).build();
  }

  static Expression isBlank(ItemId id) {
    return ImmutableIsBlankOperator.builder().questionId(id).build();
  }

  static Expression isNull(ItemId id) {
    return ImmutableIsNullOperator.builder().itemId(id).build();
  }

  static Expression isActive(ItemId id) {
    return ImmutableIsActiveOperator.builder().itemId(id).build();
  }

  static Expression isRequired(ItemId id) {
    return ImmutableIsRequiredOperator.builder().itemId(id).build();
  }

  static Expression not(Expression expression) {
    return ImmutableNotOperator.builder().expression(expression).build();
  }


  static Expression isDisabled(ItemId id) {
    return ImmutableIsDisabledOperator.builder().itemId(id).build();
  }

  static ItemId ref(String id) {
    return IdUtils.toId(id);
  }

  static VariableReference<?> var(@NonNull ItemId id, @NonNull ValueType valueType) {
    return ImmutableVariableReference.builder().itemId(id).valueType(valueType).build();
  }
  static VariableReference<?> var(@NonNull String id, @NonNull ValueType valueType) {
    return var(ref(id), valueType);
  }
}
