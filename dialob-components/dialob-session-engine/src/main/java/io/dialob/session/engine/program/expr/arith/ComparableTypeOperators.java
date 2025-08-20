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

import io.dialob.session.engine.program.model.Expression;

public class ComparableTypeOperators<T extends Comparable<T>> implements Operators {

  public InfixOperator eq(Expression lhs, Expression rhs) {
    return ImmutableEqOperator.<T>builder().lhs(lhs).rhs(rhs).build();
  }

  public InfixOperator le(Expression lhs, Expression rhs) {
    return ImmutableLeOperator.<T>builder().lhs(lhs).rhs(rhs).build();
  }

  public InfixOperator ne(Expression lhs, Expression rhs) {
    return ImmutableNeOperator.<T>builder().lhs(lhs).rhs(rhs).build();
  }

  public InfixOperator lt(Expression lhs, Expression rhs) {
    return ImmutableLtOperator.<T>builder().lhs(lhs).rhs(rhs).build();
  }

  public InfixOperator ge(Expression lhs, Expression rhs) {
    return ImmutableGeOperator.<T>builder().lhs(lhs).rhs(rhs).build();
  }

  public InfixOperator gt(Expression lhs, Expression rhs) {
    return ImmutableGtOperator.<T>builder().lhs(lhs).rhs(rhs).build();
  }


}
