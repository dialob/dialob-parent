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

import edu.umd.cs.findbugs.annotations.NonNull;
import io.dialob.session.engine.DebugUtil;
import io.dialob.session.engine.program.DialobProgram;
import io.dialob.session.engine.program.DialobSessionEvalContext;
import io.dialob.session.engine.program.EvalContext;
import io.dialob.session.engine.session.command.Command;
import io.dialob.session.engine.session.command.event.Event;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import static java.util.Objects.requireNonNull;

@Slf4j
public class ActiveDialobSessionUpdater implements DialobSessionUpdater {

  public interface ContextProvider {
    DialobSessionEvalContext createDialobSessionEvalContext(@NonNull Consumer<Event> updatesConsumer);
  }

  private final ContextProvider contextProvider;

  private final DialobProgram dialobProgram;

  // State for updates and commands
  private final Set<Command<?>> updatedCommands = new HashSet<>();

  protected final List<Command<?>> evalQueue = new LinkedList<>();

  public ActiveDialobSessionUpdater(@NonNull ContextProvider contextProvider,
                                    @NonNull DialobProgram dialobProgram) {
    this.contextProvider = requireNonNull(contextProvider, "contextProvider may not be null");
    this.dialobProgram = requireNonNull(dialobProgram, "dialobProgram may not be null");
  }

  @Override
  public Consumer<EvalContext.UpdatedItemsVisitor> applyCommands(@NonNull Iterable<Command<?>> commands) {
    var evalContext = createEvalContext();
    commands.forEach(this::queueCommand);

    // Execute commands in the evaluation queue
    while (!evalQueue.isEmpty()) {
      var command = evalQueue.remove(0); // FIFO queue processing
      updatedCommands.add(command);
      evalContext.applyCommand(command);
    }
    updatedCommands.clear();
    LOGGER.debug("Update completed.");
    return evalContext::accept;
  }

  protected DialobSessionEvalContext createEvalContext() {
    return contextProvider.createDialobSessionEvalContext(this::queueUpdate);
  }

  private void queueUpdate(@NonNull Event event) {
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug(" -> event({})", event);
    }
    this.dialobProgram
      .findDependencies(event)
      .forEach(this::queueCommand);
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("  = {}", StringUtils.join(evalQueue.stream().map(DebugUtil::commandToString).toArray(), " ,"));
    }
  }


  protected void queueCommand(@NonNull final Command<?> updateCommand) {
    var mustBeBefore = this.dialobProgram.getCommandsToCommands(updateCommand);
    if (mustBeBefore.isEmpty()) {
      evalQueue.add(updateCommand);
      return;
    }
    // Push command into correct location in queue.
    var iterator = evalQueue.listIterator();
    while (iterator.hasNext()) {
      var command = iterator.next();
      if (updateCommand.equals(command)) {
        if (LOGGER.isDebugEnabled()) {
          LOGGER.debug("  - skip {} (on queue already)", DebugUtil.commandToString(updateCommand));
        }
        return;
      }
      if (mustBeBefore.contains(command)) {
        iterator.previous();
        break;
      }
    }
    iterator.add(updateCommand);
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("  + queued {}", DebugUtil.commandToString(updateCommand));
      if (updatedCommands.contains(updateCommand)) {
        LOGGER.debug("Target {} already executed. Cyclic dependency?", DebugUtil.commandToString(updateCommand));
      }
    }
  }
}
