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
    return BinaryOperator.<Boolean>builder().addNodes(expressions).reducer(Reducers.Bool.AND).build();
  }

  static Expression or(@NonNull Expression ...expressions) {
    return BinaryOperator.<Boolean>builder().addNodes(expressions).reducer(Reducers.Bool.OR).build();
  }

  static Expression isAnswered(ItemId id) {
    return new IsAnsweredOperator.Builder().questionId(id).build();
  }

  static Expression isBlank(ItemId id) {
    return new IsBlankOperator.Builder().questionId(id).build();
  }

  static Expression isNull(ItemId id) {
    return new IsNullOperator.Builder().itemId(id).build();
  }

  static Expression isActive(ItemId id) {
    return new IsActiveOperator.Builder().itemId(id).build();
  }

  static Expression isRequired(ItemId id) {
    return new IsRequiredOperator.Builder().itemId(id).build();
  }

  static Expression isReadOnly(ItemId id) {
    return new IsReadOnlyOperator.Builder().itemId(id).build();
  }

  static Expression not(Expression expression) {
    return new NotOperator.Builder().expression(expression).build();
  }


  static Expression isDisabled(ItemId id) {
    return new IsDisabledOperator.Builder().itemId(id).build();
  }

  static ItemId ref(String id) {
    return IdUtils.toId(id);
  }

  static VariableReference<?> var(@NonNull ItemId id, @NonNull ValueType valueType) {
    return new VariableReference.Builder<>().itemId(id).valueType(valueType).build();
  }
  static VariableReference<?> var(@NonNull String id, @NonNull ValueType valueType) {
    return var(ref(id), valueType);
  }
}
