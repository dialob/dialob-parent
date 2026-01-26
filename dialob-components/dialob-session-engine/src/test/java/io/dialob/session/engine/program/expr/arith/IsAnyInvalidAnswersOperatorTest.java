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
import io.dialob.session.engine.program.EvalContext;
import io.dialob.session.engine.session.command.EventMatcher;
import io.dialob.session.engine.session.model.ErrorState;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static io.dialob.session.engine.session.command.EventMatchers.anyError;
import static io.dialob.session.engine.session.command.EventMatchers.errorActivity;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class IsAnyInvalidAnswersOperatorTest {

  @Test
  void shouldReturnBooleanType() {
    IsAnyInvalidAnswersOperator operator = (IsAnyInvalidAnswersOperator) IsAnyInvalidAnswersOperator.instance();
    assertEquals(ValueType.BOOLEAN, operator.getValueType());
  }

  @Test
  void shouldReturnEvalRequiredConditions() {
    IsAnyInvalidAnswersOperator operator = (IsAnyInvalidAnswersOperator) IsAnyInvalidAnswersOperator.instance();
    Set<EventMatcher> conditions = operator.getEvalRequiredConditions();
    assertEquals(Set.of(errorActivity(anyError())), conditions);
  }

  @Test
  void shouldEvalToTrueWhenAnyErrorIsActive() {
    IsAnyInvalidAnswersOperator operator = (IsAnyInvalidAnswersOperator) IsAnyInvalidAnswersOperator.instance();
    EvalContext context = Mockito.mock(EvalContext.class);
    ErrorState activeError = Mockito.mock(ErrorState.class);
    ErrorState inactiveError = Mockito.mock(ErrorState.class);

    when(activeError.isActive()).thenReturn(true);
    when(inactiveError.isActive()).thenReturn(false);
    when(context.getErrorStates()).thenReturn(List.of(inactiveError, activeError));

    assertTrue(operator.eval(context));
  }

  @Test
  void shouldEvalToFalseWhenNoErrorsAreActive() {
    IsAnyInvalidAnswersOperator operator = (IsAnyInvalidAnswersOperator) IsAnyInvalidAnswersOperator.instance();
    EvalContext context = Mockito.mock(EvalContext.class);
    ErrorState inactiveError1 = Mockito.mock(ErrorState.class);
    ErrorState inactiveError2 = Mockito.mock(ErrorState.class);

    when(inactiveError1.isActive()).thenReturn(false);
    when(inactiveError2.isActive()).thenReturn(false);
    when(context.getErrorStates()).thenReturn(List.of(inactiveError1, inactiveError2));

    assertFalse(operator.eval(context));
  }

  @Test
  void shouldEvalToFalseWhenNoErrorsExist() {
    IsAnyInvalidAnswersOperator operator = (IsAnyInvalidAnswersOperator) IsAnyInvalidAnswersOperator.instance();
    EvalContext context = Mockito.mock(EvalContext.class);

    when(context.getErrorStates()).thenReturn(List.of());

    assertFalse(operator.eval(context));
  }
}
