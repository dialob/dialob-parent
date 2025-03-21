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
import io.dialob.session.engine.session.model.IdUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

class ConcatOperatorTest {

  @Test
  void emptyExpressionsGiveNonConditions() {
    ConcatOperator op = ImmutableConcatOperator.builder().expressions(List.of()).build();
    Assertions.assertTrue(op.getEvalRequiredConditions().isEmpty());
  }

  @Test
  void constantExpressionGiveNonConditions() {
    ConcatOperator op = ImmutableConcatOperator.builder().expressions(List.of(ImmutableConstant.builder().valueType(ValueType.STRING).value("Hello").build())).build();
    Assertions.assertTrue(op.getEvalRequiredConditions().isEmpty());
  }

  @Test
  void twoExpressionsConditionsAreCombined() {
    ConcatOperator op = ImmutableConcatOperator.builder().expressions(List.of(
      ImmutableIsActiveOperator.builder().itemId(IdUtils.toId("q1")).build(),
      ImmutableIsActiveOperator.builder().itemId(IdUtils.toId("q2")).build()
    )).build();
    Assertions.assertFalse(op.getEvalRequiredConditions().isEmpty());
    Assertions.assertEquals(2, op.getEvalRequiredConditions().size());
  }




}
