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
import io.dialob.session.engine.program.EvalContext;

public interface AbstractRelationOperator<T> extends AbstractLogicalOperator {

  @Nullable
  @Override
  default Boolean eval(@NonNull EvalContext evalContext) {
    T lhsResult = (T) getLhs().eval(evalContext);
    T rhsResult = (T) getRhs().eval(evalContext);
    if (lhsResult == null || rhsResult == null) {
      return null;
    }
    return apply(compare(lhsResult, rhsResult));
  }

  boolean apply(int comp);

  int compare(T lhsResult, T rhsResult);

  String getOperator();
}
