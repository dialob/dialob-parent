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
package io.dialob.session.engine.program;

import edu.umd.cs.findbugs.annotations.NonNull;
import io.dialob.api.proto.Action;
import io.dialob.session.engine.program.model.Item;
import io.dialob.session.engine.program.model.Program;
import io.dialob.session.engine.session.ActiveDialobSessionUpdater;
import io.dialob.session.engine.session.CreateDialobSessionProgramVisitor;
import io.dialob.session.engine.session.command.*;
import io.dialob.session.engine.session.command.event.*;
import io.dialob.session.engine.session.model.DialobSession;
import io.dialob.session.engine.session.model.ErrorId;
import io.dialob.session.engine.session.model.ItemId;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.Serial;
import java.io.Serializable;
import java.util.*;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toSet;

@EqualsAndHashCode()
@Slf4j
public class DialobProgram implements Serializable {

  @Serial
  private static final long serialVersionUID = 2922819825920407874L;

  @Getter
  private final Program program;

  private final Map<EventMatcher,List<Command<?>>> inputUpdates;

  private final Map<ItemId,List<Command<?>>> itemCommands;

  private final Map<Command<?>, Set<Command<?>>> commandsToCommands;

  private DialobProgram(@NonNull Program program,
                        @NonNull Map<EventMatcher, List<Command<?>>> inputUpdates,
                        @NonNull Map<ItemId, List<Command<?>>> itemCommands,
                        @NonNull Map<Command<?>, Set<Command<?>>> commandsToCommands) {
    this.program = Objects.requireNonNull(program);
    this.inputUpdates = Objects.requireNonNull(inputUpdates);
    this.itemCommands = Objects.requireNonNull(itemCommands);
    this.commandsToCommands = Objects.requireNonNull(commandsToCommands);
  }

  @NonNull
  public static DialobProgram createDialobProgram(@NonNull Program program) {
    DependencyResolverVisitor visitor = new DependencyResolverVisitor();
    program.accept(visitor);
    return new DialobProgram(program,
      visitor.getInputUpdates(),
      visitor.getItemCommands(),
      visitor.getCommandsToCommands());
  }

  public Stream<Command<?>> findDependencies(@NonNull Event event) {
    return inputUpdates
      .entrySet()
      .stream()
      .filter(entry -> entry.getKey().matches(event))
      .flatMap(entry -> entry.getValue().stream())
      .filter(Objects::nonNull)
      .flatMap(command -> mapTo(event, command));
  }

  private <T, C extends Command<T>> Stream<C> mapTo(Event event, C command) {
    if (command instanceof ErrorUpdateCommand updateCommand) {
      // TODO remove instanceof checks
      final ErrorId errorId = updateCommand.getTargetId();
      if (event instanceof ItemAddedEvent itemAddedEvent) {
        return Stream.of((C) updateCommand.withTargetId(errorId.withItemId(itemAddedEvent.getAddItemId())));
      }
      if (event instanceof ItemRemovedEvent itemRemovedEvent) {
        return Stream.of((C) updateCommand.withTargetId(errorId.withItemId(itemRemovedEvent.getRemoveItemId())));
      }
      if (event instanceof RowGroupItemsInitEvent rowGroupItemsInitEvent) {
        return Stream.of((C) updateCommand.withTargetId(errorId.withItemId(rowGroupItemsInitEvent.getGroupId())));
      }
      if (errorId.isPartial() && event instanceof TargetEvent targetEvent) {
        return Stream.of((C) updateCommand.withTargetId(errorId.withItemId(errorId.getItemId().withParent(targetEvent.getTargetId().getParent()))));
      }
    } else if (command instanceof UpdateCommand updateCommand) {
      if (event instanceof ItemAddedEvent itemAddedEvent) {
        return Stream.of((C) updateCommand.withTargetId(itemAddedEvent.getAddItemId()));
      }
      if (event instanceof ItemRemovedEvent itemRemovedEvent) {
        return Stream.of((C) updateCommand.withTargetId(itemRemovedEvent.getRemoveItemId()));
      }
      if (event instanceof RowGroupItemsInitEvent rowGroupItemsInitEvent) {
        return Stream.of((C) updateCommand.withTargetId(rowGroupItemsInitEvent.getGroupId()));
      }
    }
    return Stream.of(command);
  }

  public Set<Event> allUpdates() {
    return this.inputUpdates.values()
      .stream()
      .flatMap(List::stream)
      .map(Command::getTriggers)
      .flatMap(List::stream)
      .map(Trigger::getAllEvents)
      .flatMap(List::stream)
      .collect(toSet());
  }


  /**
   * @deprecated Only used in unit tests
   */
  @Deprecated
  public DialobSession createSession(@NonNull DialobSessionEvalContextFactory sessionContextFactory, String tenantId, final String sessionId, final String language, String activePage) {
    return this.createSession(sessionContextFactory, tenantId, sessionId, language, activePage, (itemId, item) -> Optional.empty(), valueSetId -> Collections.emptyList(), null, null, null);
  }

  public DialobSession createSession(@NonNull DialobSessionEvalContextFactory sessionContextFactory,
                                     final String tenantId,
                                     final String sessionId,
                                     final String language,
                                     final String activePage,
                                     @NonNull CreateDialobSessionProgramVisitor.InitialValueResolver initialValueResolver,
                                     CreateDialobSessionProgramVisitor.ProvidedValueSetEntriesResolver findProvidedValueSetEntries,
                                     Date completed,
                                     Date opened,
                                     Date lastAnswer)
  {
    final CreateDialobSessionProgramVisitor createDialobSessionProgramVisitor = new CreateDialobSessionProgramVisitor(tenantId, sessionId, language, activePage, initialValueResolver, findProvidedValueSetEntries, this.itemCommands, completed, opened, lastAnswer);
    program.accept(createDialobSessionProgramVisitor);
    DialobSession dialobSession = createDialobSessionProgramVisitor.getDialobSession();
    new ActiveDialobSessionUpdater(sessionContextFactory, this, dialobSession, true) {
      @Override
      protected void applyUpdates(@NonNull Iterable<Action> actions) {
        createDialobSessionProgramVisitor.getUpdates().forEach(this::queueCommand);
        // find first whenActiveUpdated page, if activePage is unset
        if (activePage == null) {
          evalQueue.add(CommandFactory.nextPage());
        }
      }
    }.dispatchActions(Collections.emptyList());

    return dialobSession;
  }

  @Override
  public String toString() {
    return program.toString();
  }

  public Optional<Item> getItem(ItemId id) {
    return program.getItem(id);
  }

  public Set<Command<?>> getCommandsToCommands(Command<?> updateCommand) {
    return commandsToCommands.getOrDefault(updateCommand, Collections.emptySet());
  }

}
