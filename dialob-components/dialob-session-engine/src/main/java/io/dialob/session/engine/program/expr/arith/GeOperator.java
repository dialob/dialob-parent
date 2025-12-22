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
import org.immutables.value.Value;

@Value.Builder
@Value.Style(
  jakarta = true,
  jdkOnly = true,
  overshadowImplementation = true,
  visibility = Value.Style.ImplementationVisibility.PACKAGE
)
public record GeOperator<T extends Comparable<T>>(
  Expression lhs,
  Expression rhs
) implements AbstractComparableRelationOperator<T> {

  public static <T extends Comparable<T>> GeOperator.Builder<T> builder() {
    return new GeOperator.Builder<T>();
  }

  public static final class Builder<T extends Comparable<T>> extends GeOperatorBuilder<T> {}

  @Override
  public boolean apply(int comp) {
    return comp >= 0;
  }

  @Override
  public String getOperator() {
    return ">=";
  }

}
