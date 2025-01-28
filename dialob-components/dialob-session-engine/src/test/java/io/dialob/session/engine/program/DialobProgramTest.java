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
package io.dialob.session.engine.program;

import io.dialob.rule.parser.function.FunctionRegistry;
import io.dialob.session.engine.DependencyLoopException;
import io.dialob.session.engine.program.model.Item;
import io.dialob.session.engine.program.model.Program;
import io.dialob.session.engine.session.command.Command;
import io.dialob.session.engine.session.command.event.Event;
import io.dialob.session.engine.session.command.event.ImmutableActiveUpdatedEvent;
import io.dialob.session.engine.session.command.event.ImmutableTargetEvent;
import io.dialob.session.engine.session.model.IdUtils;
import io.dialob.session.engine.session.model.ImmutableItemIdPartial;
import io.dialob.session.engine.session.model.ImmutableItemRef;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

public class DialobProgramTest {

  @Test
  void emptyProgramShouldNotProduceAnyDependencies() {
    // given
    Program program = mock(Program.class);
    DialobSessionEvalContextFactory sessionContextFactory = mock(DialobSessionEvalContextFactory.class);
    EvalContext evalContext = mock(EvalContext.class);
    DialobProgram dialobProgram = DialobProgram.createDialobProgram(program);

    // when
    List<Command<?>> updates = dialobProgram.findDependencies(ImmutableTargetEvent.of(IdUtils.toId("question"))).collect(Collectors.toList());

    // expect
    assertNotNull(updates);
    assertTrue(updates.isEmpty());
    verify(program).accept(any());
    verifyNoMoreInteractions(program, evalContext, sessionContextFactory);
  }

  @Test
  void partialMatchersShouldGenerateExactUpdates() {
    // given
    FunctionRegistry functionRegistry = mock(FunctionRegistry.class);
    DialobSessionEvalContextFactory sessionContextFactory = mock(DialobSessionEvalContextFactory.class);
    EvalContext evalContext = mock(EvalContext.class);

    ProgramBuilder programBuilder = new ProgramBuilder(functionRegistry);
    // @formatter:off
    Program program = programBuilder
      .setId("prog1")
      .addRoot()
        .addItem("page1")
        .build()
      .addPage("page1")
        .addItem("rgroup")
        .addItem("question1")
        .build()
      .addRowGroup("rgroup")
        .addItem("question2")
        .build()
      .addQuestion("question1")
        .setType("boolean")
        .build()
      .addQuestion("question2")
        .setActiveWhen("question1 = true")
        .setType("text")
        .build()
      .build();
    // @formatter:on
    DialobProgram dialobProgram = DialobProgram.createDialobProgram(program);

    // when
    Optional<Item> rgroup = dialobProgram.getItem(IdUtils.toId("rgroup"));
    Assertions.assertTrue(rgroup.isPresent());
    rgroup = dialobProgram.getItem(ImmutableItemIdPartial.of(Optional.of(IdUtils.toId("rgroup"))));
    Assertions.assertTrue(rgroup.isPresent());

    Set<Event> allUpdates = dialobProgram.allUpdates();

    List<Command<?>> updates = dialobProgram.findDependencies(ImmutableActiveUpdatedEvent.of(ImmutableTargetEvent.of(IdUtils.toId("rgroup.2")))).collect(Collectors.toList());

    // expect
    assertNotNull(updates);
    assertTrue(updates.isEmpty());
    verifyNoMoreInteractions(evalContext, sessionContextFactory, functionRegistry);
  }

  @Test
  void variablesShouldFindReferencesFromInternalScope() {
    // given
    FunctionRegistry functionRegistry = mock(FunctionRegistry.class);
    DialobSessionEvalContextFactory sessionContextFactory = mock(DialobSessionEvalContextFactory.class);
    EvalContext evalContext = mock(EvalContext.class);

    ProgramBuilder programBuilder = new ProgramBuilder(functionRegistry);
    // @formatter:off
    Program program = programBuilder
      .setId("prog1")
      .addRoot()
        .addItem("page1")
        .build()
      .addPage("page1")
        .addItem("rgroup")
        .build()
      .addRowGroup("rgroup")
        .addItem("question1")
        .addItem("question2")
        .build()
      .addQuestion("question1")
        .setType("boolean")
        .build()
      .addQuestion("question2")
        .setActiveWhen("question1 = true")
        .setType("text")
        .build()
      .build();
    // @formatter:on
    DialobProgram dialobProgram = DialobProgram.createDialobProgram(program);

    // when
    Optional<Item> rgroup = dialobProgram.getItem(IdUtils.toId("rgroup"));
    Assertions.assertTrue(rgroup.isPresent());
    rgroup = dialobProgram.getItem(ImmutableItemIdPartial.of(Optional.of(IdUtils.toId("rgroup"))));
    Assertions.assertTrue(rgroup.isPresent());

    Optional<Item> question2 = dialobProgram.getItem(ImmutableItemRef.of("question2", Optional.of(ImmutableItemIdPartial.of(Optional.of(IdUtils.toId("rgroup"))))));
    Assertions.assertTrue(question2.isPresent());

    Set<Event> allUpdates = dialobProgram.allUpdates();

    List<Command<?>> updates = dialobProgram.findDependencies(ImmutableActiveUpdatedEvent.of(ImmutableTargetEvent.of(IdUtils.toId("rgroup.2")))).collect(Collectors.toList());

    // expect
    assertNotNull(updates);
    assertTrue(updates.isEmpty());
    verifyNoMoreInteractions(evalContext, sessionContextFactory, functionRegistry);
  }

  @Test
  void shouldDetectDependencyLoop() {
    // given
    FunctionRegistry functionRegistry = mock(FunctionRegistry.class);
    DialobSessionEvalContextFactory sessionContextFactory = mock(DialobSessionEvalContextFactory.class);
    EvalContext evalContext = mock(EvalContext.class);

    ProgramBuilder programBuilder = new ProgramBuilder(functionRegistry);
    // @formatter:off
    Program program = programBuilder
      .setId("prog1")
      .addRoot()
        .addItem("question1")
        .build()
      .addQuestion("question1")
        .setType("boolean")
        .setActiveWhen("question1 = true")
        .build()
      .build();
    // @formatter:on
    Assertions.assertThrows(DependencyLoopException.class, () -> DialobProgram.createDialobProgram(program));

  }

}
