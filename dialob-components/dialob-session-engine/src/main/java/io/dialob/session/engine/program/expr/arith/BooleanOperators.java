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

import io.dialob.rule.parser.api.ValueType;
import io.dialob.session.engine.program.model.Expression;

public class BooleanOperators implements Operators {

  public static final Constant<Boolean> FALSE = ImmutableConstant.<Boolean>builder().valueType(ValueType.BOOLEAN).value(Boolean.FALSE).build();

  public static final Constant<Boolean> TRUE = ImmutableConstant.<Boolean>builder().valueType(ValueType.BOOLEAN).value(Boolean.TRUE).build();

  public InfixOperator eq(Expression lhs, Expression rhs) {
    return ImmutableEqOperator.<Boolean>builder().lhs(lhs).rhs(rhs).build();
  }

  public InfixOperator ne(Expression lhs, Expression rhs) {
    return ImmutableNeOperator.<Boolean>builder().lhs(lhs).rhs(rhs).build();
  }

  public InfixOperator le(Expression lhs, Expression rhs) {
    return noRelationError();
  }

  public InfixOperator lt(Expression lhs, Expression rhs) {
    return noRelationError();
  }

  public InfixOperator ge(Expression lhs, Expression rhs) {
    return noRelationError();
  }

  public InfixOperator gt(Expression lhs, Expression rhs) {
    return noRelationError();
  }

  private static InfixOperator noRelationError() {
    throw new RuntimeException("No relation operator between boolean types");
  }

}
