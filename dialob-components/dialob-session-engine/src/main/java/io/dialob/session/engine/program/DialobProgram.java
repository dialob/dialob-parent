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

import edu.umd.cs.findbugs.annotations.NonNull;
import io.dialob.session.engine.program.model.Item;
import io.dialob.session.engine.program.model.Program;
import io.dialob.session.engine.session.CreateDialobSessionProgramVisitor;
import io.dialob.session.engine.session.command.*;
import io.dialob.session.engine.session.command.event.*;
import io.dialob.session.engine.session.model.DialobSession;
import io.dialob.session.engine.session.model.ErrorId;
import io.dialob.session.engine.session.model.ItemId;
import lombok.extern.slf4j.Slf4j;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.*;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;

@Slf4j
public record DialobProgram(
  Program program,
  Map<EventMatcher, List<Command<?>>> inputUpdates,
  Map<ItemId, List<Command<?>>> itemCommands,
  Map<Command<?>, Set<Command<?>>> commandsToCommands
) implements Serializable {

  @Serial
  private static final long serialVersionUID = 2922819825920407874L;

  public DialobProgram {
    // Make sure nested collections are immutable
    inputUpdates = inputUpdates.entrySet().stream().map(
      entry -> Map.entry(entry.getKey(), List.copyOf(entry.getValue()))
    ).collect(toMap(Map.Entry::getKey, Map.Entry::getValue));

    itemCommands = itemCommands.entrySet().stream().map(
      entry -> Map.entry(entry.getKey(), List.copyOf(entry.getValue()))
    ).collect(toMap(Map.Entry::getKey, Map.Entry::getValue));

    commandsToCommands = commandsToCommands.entrySet().stream().map(
      entry -> Map.entry(entry.getKey(), Set.copyOf(entry.getValue()))
    ).collect(toMap(Map.Entry::getKey, Map.Entry::getValue));
  }

  @NonNull
  public static DialobProgram createDialobProgram(@NonNull Program program) {
    var visitor = new DependencyResolverVisitor();
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
      final ErrorId errorId = updateCommand.targetId();
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
        return Stream.of((C) updateCommand.withTargetId(errorId.withItemId(errorId.itemId().withParent(targetEvent.targetId().getParent()))));
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
      .map(Command::triggers)
      .flatMap(List::stream)
      .map(Trigger::allEvents)
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
                                     Instant completed,
                                     Instant opened,
                                     Instant lastAnswer) {
    final var createDialobSessionProgramVisitor = new CreateDialobSessionProgramVisitor(tenantId, sessionId, language, activePage, initialValueResolver, findProvidedValueSetEntries, this.itemCommands, completed, opened, lastAnswer);
    program.accept(createDialobSessionProgramVisitor);
    var dialobSession = createDialobSessionProgramVisitor.getDialobSession();
    var updater = sessionContextFactory.createSessionUpdater(this, dialobSession, true);
    updater.applyCommands(createDialobSessionProgramVisitor.getUpdates());
    return dialobSession;
  }

  public Optional<Item> getItem(ItemId id) {
    return program.getItem(id);
  }

  public Set<Command<?>> getCommandsToCommands(Command<?> updateCommand) {
    return commandsToCommands.getOrDefault(updateCommand, Collections.emptySet());
  }

}
