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
package io.dialob.session.engine.session;

import io.dialob.session.engine.program.DialobProgram;
import io.dialob.session.engine.program.EvalContext;
import io.dialob.session.engine.session.command.Command;
import io.dialob.session.engine.session.command.Trigger;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ActiveDialobSessionUpdaterQueueTest {

  private ActiveDialobSessionUpdater updater;
  private DialobProgram dialobProgram;
  private ActiveDialobSessionUpdater.ContextProvider contextProvider;

  @ToString
  @EqualsAndHashCode
  public static class TestCommand implements Command<Object> {
    private final String id;

    public TestCommand(String id) {
      this.id = id;
    }

    @Override
    public List<Trigger<Object>> triggers() {
      return Collections.emptyList();
    }

    @Override
    public Object update(EvalContext context, Object target) {
      return target;
    }
  }

  @BeforeEach
  void setUp() {
    dialobProgram = mock(DialobProgram.class);
    contextProvider = mock(ActiveDialobSessionUpdater.ContextProvider.class);
    updater = new ActiveDialobSessionUpdater(contextProvider, dialobProgram);
  }

  @Test
  void shouldAddCommandToEmptyQueue() {
    Command<?> command = new TestCommand("c1");
    when(dialobProgram.getCommandsToCommands(command)).thenReturn(Collections.emptySet());

    updater.queueCommand(command);

    assertEquals(1, updater.evalQueue.size());
    assertEquals(command, updater.evalQueue.get(0));
  }

  @Test
  void shouldAddCommandToEndOfQueueWhenNoDependencies() {
    Command<?> c1 = new TestCommand("c1");
    Command<?> c2 = new TestCommand("c2");
    when(dialobProgram.getCommandsToCommands(c1)).thenReturn(Collections.emptySet());
    when(dialobProgram.getCommandsToCommands(c2)).thenReturn(Collections.emptySet());

    updater.queueCommand(c1);
    updater.queueCommand(c2);

    assertEquals(2, updater.evalQueue.size());
    assertEquals(c1, updater.evalQueue.get(0));
    assertEquals(c2, updater.evalQueue.get(1));
  }

  @Test
  void shouldNotAddDuplicateCommandWhenDependenciesExist() {
    Command<?> c1 = new TestCommand("c1");
    Command<?> c1_dup = new TestCommand("c1");
    Command<?> dummy = new TestCommand("dummy");

    // Note: Duplication check is only performed if mustBeBefore is NOT empty
    when(dialobProgram.getCommandsToCommands(c1)).thenReturn(Set.of(dummy));
    when(dialobProgram.getCommandsToCommands(c1_dup)).thenReturn(Set.of(dummy));

    updater.queueCommand(c1);
    updater.queueCommand(c1_dup);

    assertEquals(1, updater.evalQueue.size());
  }

  @Test
  void shouldInsertCommandBeforeExistingCommandIfItDependsOnIt() {
    Command<?> c1 = new TestCommand("c1");
    Command<?> c2 = new TestCommand("c2");

    // c2 MUST BE BEFORE c1
    when(dialobProgram.getCommandsToCommands(c2)).thenReturn(Set.of(c1));
    when(dialobProgram.getCommandsToCommands(c1)).thenReturn(Collections.emptySet());

    updater.queueCommand(c1);
    updater.queueCommand(c2);

    assertEquals(2, updater.evalQueue.size());
    assertEquals(c2, updater.evalQueue.get(0));
    assertEquals(c1, updater.evalQueue.get(1));
  }

  @Test
  void shouldHandleMultipleOrderingDependencies() {
    Command<?> c1 = new TestCommand("c1");
    Command<?> c2 = new TestCommand("c2");
    Command<?> c3 = new TestCommand("c3");

    // c3 MUST BE BEFORE c2
    when(dialobProgram.getCommandsToCommands(c1)).thenReturn(Collections.emptySet());
    when(dialobProgram.getCommandsToCommands(c2)).thenReturn(Collections.emptySet());
    when(dialobProgram.getCommandsToCommands(c3)).thenReturn(Set.of(c2));

    updater.queueCommand(c1);
    updater.queueCommand(c2);
    // Queue: [c1, c2]

    updater.queueCommand(c3);
    // Queue: [c1, c3, c2]

    assertEquals(3, updater.evalQueue.size());
    assertEquals(c1, updater.evalQueue.get(0));
    assertEquals(c3, updater.evalQueue.get(1));
    assertEquals(c2, updater.evalQueue.get(2));
  }

  @Test
  void shouldInsertAtTheBeginningIfDependsOnFirst() {
    Command<?> c1 = new TestCommand("c1");
    Command<?> c2 = new TestCommand("c2");
    Command<?> c3 = new TestCommand("c3");

    when(dialobProgram.getCommandsToCommands(c1)).thenReturn(Collections.emptySet());
    when(dialobProgram.getCommandsToCommands(c2)).thenReturn(Collections.emptySet());
    when(dialobProgram.getCommandsToCommands(c3)).thenReturn(Set.of(c1));

    updater.queueCommand(c1);
    updater.queueCommand(c2);
    // Queue: [c1, c2]

    updater.queueCommand(c3);
    // c3 must be before c1
    // Queue: [c3, c1, c2]

    assertEquals(3, updater.evalQueue.size());
    assertEquals(c3, updater.evalQueue.get(0));
    assertEquals(c1, updater.evalQueue.get(1));
    assertEquals(c2, updater.evalQueue.get(2));
  }
}
