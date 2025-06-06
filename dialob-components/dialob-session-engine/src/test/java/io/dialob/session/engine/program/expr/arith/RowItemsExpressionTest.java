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

import io.dialob.session.engine.program.EvalContext;
import io.dialob.session.engine.session.model.IdUtils;
import io.dialob.session.engine.session.model.ItemId;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.AdditionalAnswers;
import org.mockito.Mockito;

import java.util.Collection;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

class RowItemsExpressionTest {

  @Test
  void test() {
    EvalContext context = Mockito.mock(EvalContext.class);
    when(context.mapTo(any(), eq(true))).then(AdditionalAnswers.returnsFirstArg());
    RowItemsExpression rowItemsExpression = ImmutableRowItemsExpression.builder().addItemIds(IdUtils.toId("q1")).build();
    Collection<ItemId> ids = rowItemsExpression.eval(context);
    Assertions.assertIterableEquals(List.of(IdUtils.toId("q1")), ids);


  }

}
