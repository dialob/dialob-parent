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
import io.dialob.session.engine.program.model.Expression;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoSession;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

class MatchesOperatorTest {

  public MockitoSession mockitoSession;

  @BeforeEach
  public void beforeEach() {
    mockitoSession = Mockito.mockitoSession().initMocks(this).startMocking();
  }

  @AfterEach
  public void afterEach() {
    mockitoSession.finishMocking();
  }

  @Mock
  public Expression rhs;

  @Mock
  public Expression lhs;

  @Mock
  public EvalContext context;

  @Test
  void nullValueRreturnsNull() {
    MatchesOperator op = ImmutableMatchesOperator.builder().lhs(lhs).rhs(rhs).build();
    when(lhs.eval(context)).thenReturn(null);
    when(rhs.eval(context)).thenReturn(".*");
    assertNull(op.eval(context));
  }
  @Test
  void nullPatternRreturnsNull() {
    MatchesOperator op = ImmutableMatchesOperator.builder().lhs(lhs).rhs(rhs).build();
    when(lhs.eval(context)).thenReturn("a");
    when(rhs.eval(context)).thenReturn(null);
    assertNull(op.eval(context));
  }


  @Test
  void bothNullReturnsNull() {
    MatchesOperator op = ImmutableMatchesOperator.builder().lhs(lhs).rhs(rhs).build();
    when(lhs.eval(context)).thenReturn("a");
    when(rhs.eval(context)).thenReturn(null);
    assertNull(op.eval(context));
  }

  @Test
  void shouldMatchAnything() {
    lenient().when(lhs.eval(context)).thenReturn("a");
    lenient().when(rhs.eval(context)).thenReturn(".*");
    MatchesOperator op = ImmutableMatchesOperator.builder().lhs(lhs).rhs(rhs).build();
    assertTrue(op.eval(context));
    lenient().when(lhs.eval(context)).thenReturn("123");
    lenient().when(rhs.eval(context)).thenReturn(".*");
    assertTrue(op.eval(context));
    when(lhs.eval(context)).thenReturn("gfds*-fdsa");
    when(rhs.eval(context)).thenReturn(".*");
    assertTrue(op.eval(context));
  }

  @Test
  void shouldMatchJustNumbers() {
    MatchesOperator op = ImmutableMatchesOperator.builder().lhs(lhs).rhs(rhs).build();
    lenient().when(lhs.eval(context)).thenReturn("123");
    lenient().when(rhs.eval(context)).thenReturn("\\d+");
    assertTrue(op.eval(context));
    when(lhs.eval(context)).thenReturn("asd");
    when(rhs.eval(context)).thenReturn("\\d+");
    assertFalse(op.eval(context));
  }

  @Test
  void shouldMatchJustAlphabets() {
    MatchesOperator op = ImmutableMatchesOperator.builder().lhs(lhs).rhs(rhs).build();
    lenient().when(lhs.eval(context)).thenReturn("123");
    lenient().when(rhs.eval(context)).thenReturn("\\a+");
    assertFalse(op.eval(context));
    when(lhs.eval(context)).thenReturn("asd");
    when(rhs.eval(context)).thenReturn("[a-z]+");
    assertTrue(op.eval(context));
  }


}
