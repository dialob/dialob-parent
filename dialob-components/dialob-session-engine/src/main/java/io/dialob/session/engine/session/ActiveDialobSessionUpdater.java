/*
 * Copyright © 2015 - 2021 ReSys (info@dialob.io)
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
import io.dialob.api.proto.Action;
import io.dialob.session.engine.DebugUtil;
import io.dialob.session.engine.program.DialobProgram;
import io.dialob.session.engine.program.DialobSessionEvalContext;
import io.dialob.session.engine.program.DialobSessionEvalContextFactory;
import io.dialob.session.engine.program.EvalContext;
import io.dialob.session.engine.session.command.Command;
import io.dialob.session.engine.session.command.UpdateCommand;
import io.dialob.session.engine.session.command.event.Event;
import io.dialob.session.engine.session.model.DialobSession;
import io.dialob.session.engine.session.model.IdUtils;
import io.dialob.session.engine.session.model.ItemId;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

import static io.dialob.session.engine.session.command.CommandFactory.*;
import static java.util.Objects.requireNonNull;

@Slf4j
public class ActiveDialobSessionUpdater implements DialobSessionUpdater {

  private final DialobSessionEvalContextFactory sessionContextFactory;

  private final DialobSession dialobSession;

  private final DialobProgram dialobProgram;

  private final Set<Command<?>> updated = new HashSet<>();

  private final boolean activating;

  protected final List<Command<?>> evalQueue = new LinkedList<>();

  public ActiveDialobSessionUpdater(@NonNull DialobSessionEvalContextFactory sessionContextFactory,
                                    @NonNull DialobProgram dialobProgram,
                                    @NonNull DialobSession dialobSession,
                                    boolean activating) {
    this.sessionContextFactory = requireNonNull(sessionContextFactory);
    this.dialobProgram = requireNonNull(dialobProgram);
    this.dialobSession = requireNonNull(dialobSession);
    this.activating = activating;
  }

  @Override
  public Consumer<EvalContext.UpdatedItemsVisitor> dispatchActions(@NonNull Iterable<Action> actions) {
    DialobSessionEvalContext evalContext = sessionContextFactory.createDialobSessionEvalContext(this.dialobSession, this::queueUpdate, this.activating);
    applyUpdates(actions);
    while(!evalQueue.isEmpty()) {
      ListIterator<Command<?>> iterator = evalQueue.listIterator();
      Command<?> command = iterator.next();
      iterator.remove();
      matchPartialCommands(command).forEach(action -> {
        updated.add(action);
        evalContext.applyAction(action);
      });
    }
    updated.clear();
    LOGGER.debug("Update completed.");
    return evalContext::accept;
  }

  protected void applyUpdates(@NonNull Iterable<Action> actions) {
    actions.forEach(action -> {
      ItemId itemId = IdUtils.toIdNullable(action.getId());
      switch(action.getType()) {
        case ANSWER:
          queueCommand(setAnswer(requireNonNull(itemId), action.getAnswer()));
          break;
        case SET_VALUE:
          queueCommand(setVariableValue(requireNonNull(itemId), action.getValue()));
          break;
        case SET_FAILED:
          queueCommand(setVariableFailed(requireNonNull(itemId)));
          break;
        case NEXT:
          queueCommand(nextPage());
          break;
        case PREVIOUS:
          queueCommand(prevPage());
          break;
        case GOTO:
          queueCommand(gotoPage(requireNonNull(itemId)));
          break;
        case COMPLETE:
          queueCommand(complete());
          break;
        case ADD_ROW:
          queueCommand(addRow(requireNonNull(itemId)));
          break;
        case DELETE_ROW:
          queueCommand(deleteRow(requireNonNull(itemId)));
          break;
        case SET_LOCALE:
          if (action.getValue() instanceof String) {
            queueCommand(setLocale((String) action.getValue()));
          }
          break;
        default:
          LOGGER.debug("Action \"{}\" ignored.", action);
      }
    });
  }

  private void queueUpdate(@NonNull Event event) {
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug(" -> event({})", event);
    }
    dialobProgram
      .findDependencies(event)
      .forEach(this::queueCommand);
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("  = {}", StringUtils.join(evalQueue.stream().map(DebugUtil::commandToString).toArray(), " ,"));
    }
  }

  private Stream<Command<?>> matchPartialCommands(Command<?> command) {
    if (command instanceof UpdateCommand) {
      UpdateCommand updateCommand = (UpdateCommand) command;
      final ItemId targetId = updateCommand.getTargetId();
      if (targetId.isPartial()) {
        return dialobSession.findMatchingItemIds(targetId).map((Function<ItemId, Command<?>>) updateCommand::withTargetId);
      }
    }
    return Stream.of(command);
  }

  protected void queueCommand(@NonNull final Command<?> updateCommand) {
    Collection<Command<?>> mustBeBefore = dialobProgram.getCommandsToCommands(updateCommand);
    if (mustBeBefore.isEmpty()) {
      evalQueue.add(updateCommand);
      return;
    }
    // push command to be queue to correct location in queue.
    ListIterator<Command<?>> i = evalQueue.listIterator();
    while (i.hasNext()) {
      Command<?> command = i.next();
      if (updateCommand.equals(command)) {
        if (LOGGER.isDebugEnabled()) {
          LOGGER.debug("  - skip {} (on queue already)", DebugUtil.commandToString(updateCommand));
        }
        return;
      }
      if (mustBeBefore.contains(command)) {
        i.previous();
        break;
      }
    }
    i.add(updateCommand);
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("  + queued {}", DebugUtil.commandToString(updateCommand));
    }
    if (LOGGER.isDebugEnabled() && updated.contains(updateCommand)) {
      LOGGER.debug("Target {} already executed. Cyclic dependency?", DebugUtil.commandToString(updateCommand));
    }
  }
}
