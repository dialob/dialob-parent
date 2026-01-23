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
import io.dialob.api.form.FormValidationError;
import io.dialob.session.engine.DebugUtil;
import io.dialob.session.engine.DependencyLoopException;
import io.dialob.session.engine.Utils;
import io.dialob.session.engine.program.expr.arith.RowItemsExpression;
import io.dialob.session.engine.program.model.*;
import io.dialob.session.engine.session.command.Command;
import io.dialob.session.engine.session.command.EventMatcher;
import io.dialob.session.engine.session.command.Trigger;
import io.dialob.session.engine.session.command.UpdateCommand;
import io.dialob.session.engine.session.model.ErrorId;
import io.dialob.session.engine.session.model.IdUtils;
import io.dialob.session.engine.session.model.ItemId;
import io.dialob.session.engine.session.model.ValueSetId;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Objects.requireNonNull;

@Slf4j
class DependencyResolverVisitor implements ProgramVisitor {

  private final Map<EventMatcher, List<Command<?>>> inputUpdates;

  private final Map<ItemId, List<Command<?>>> itemCommands;

  private final UpdateCommandFactory updateCommandFactory;

  private Map<Command<?>, Set<Command<?>>> commandsToCommands;

  DependencyResolverVisitor() {
    this.inputUpdates = new HashMap<>();
    this.itemCommands = new HashMap<>();
    this.updateCommandFactory = new UpdateCommandFactory();
  }

  @Override
  public Optional<ItemVisitor> visitItems() {
    return Optional.of(new AbstractItemVisitor() {

      @Override
      public void visitGroup(@NonNull Group group) {
        visitDisplayItem(group);
        ItemId groupId;
        if (Utils.isRowgroup(group.type()) || Utils.isRow(group.type())) {
          groupId = group.id();
          if (group.isPrototype()) {
            final Expression itemsExpression = group.itemsExpression();
            if (itemsExpression instanceof RowItemsExpression rowItemsExpression) {
              updateCommandFactory.createRowGroupItemsFromPrototype(groupId, rowItemsExpression.itemIds());

            }
            updateCommandFactory.createUpdateGroupItems(groupId, itemsExpression);
            group.canRemoveRowWhenExpressionOptional().ifPresent(expression -> updateCommandFactory.createUpdateRowCanBeRemovedCommand(groupId, expression));
          } else {
            updateCommandFactory.initRowGroupItems(groupId);
            group.canAddRowWhenExpressionOptional().ifPresent(expression -> updateCommandFactory.createUpdateRowsCanBeAddedCommand(groupId, expression));
          }
        } else {
          groupId = group.id();
          updateCommandFactory.createUpdateGroupItems(groupId, group.itemsExpression());
        }
        group.availableItemsExpressionOptional().ifPresent(expression -> updateCommandFactory.createUpdateAvailableItems(groupId, expression));
        group.isInvalidAnswersExpressionOptional().ifPresent(expression -> updateCommandFactory.createUpdateIsInvalidAnswersCommand(groupId, expression));
        group.allowedActionsExpressionOptional().ifPresent(expression -> updateCommandFactory.createUpdateAllowedActions(groupId, expression));
      }

      @Override
      public void visitDisplayItem(@NonNull DisplayItem displayItem) {
        final ItemId itemId = displayItem.id();
        displayItem.activeExpressionOptional().ifPresent(expression -> updateCommandFactory.createUpdateActivity(itemId, expression));
        displayItem.requiredExpressionOptional().ifPresent(expression -> updateCommandFactory.createUpdateRequired(itemId, expression));
        displayItem.readOnlyExpressionOptional().ifPresent(expression -> updateCommandFactory.createUpdateReadOnly(itemId, expression));
        displayItem.disabledExpressionOptional().ifPresent(expression -> updateCommandFactory.createUpdateDisabled(itemId, expression));
        displayItem.labelExpressionOptional().ifPresent(expression -> updateCommandFactory.createUpdateLabel(itemId, expression));
        displayItem.descriptionExpressionOptional().ifPresent(expression -> updateCommandFactory.createUpdateDescription(itemId, expression));
        displayItem.classNameOptional().ifPresent(expression -> updateCommandFactory.createUpdateClass(itemId, expression));
        if (displayItem.isPrototype() && Utils.isRow(displayItem.type())) {
          updateCommandFactory.createRowGroupFromPrototype(itemId);
        }
      }

      @Override
      public void visitVariableItem(@NonNull VariableItem variableItem) {
        updateCommandFactory.createUpdateVariable(variableItem.id(), variableItem.valueExpression());
      }

    });
  }

  @Override
  public Optional<ErrorVisitor> visitErrors() {
    return Optional.of(error -> {
      final ErrorId targetId = new ErrorId(error.itemId(), error.code());
      updateCommandFactory.createUpdateValidationCommand(targetId, error.validationExpression());
      error.disabledExpressionOptional().ifPresent(disabledExpression -> updateCommandFactory.createUpdateValidationDisabled(targetId, disabledExpression));
      if (error.label() != null) {
        updateCommandFactory.createErrorLabelUpdateCommand(targetId, error.label());
      }
    });
  }

  @Override
  public Optional<ValueSetVisitor> visitValueSets() {
    return Optional.of(valueSet -> updateCommandFactory.createUpdateValueSetCommand(new ValueSetId(valueSet.id()), valueSet.entries()));
  }

  @Override
  public void end() {
    // collect direct command dependencies
    updateCommandFactory.getAllCommands().stream()
      .filter(command -> command instanceof UpdateCommand)
      .map(command -> (UpdateCommand<?,?>) command)
      .forEach(updateCommand ->
        itemCommands.computeIfAbsent(updateCommand.targetId(),
        targetId -> new ArrayList<>()).add(updateCommand));
    updateCommandFactory.getAllCommands().forEach(updateCommand -> updateCommand.eventMatchers().forEach(
      eventMatcher -> inputUpdates.computeIfAbsent(requireNonNull(eventMatcher),
        key -> new ArrayList<>()).add(updateCommand)));


    commandsToCommands = updateCommandFactory.getAllCommands().stream()
      .collect(Collectors.toMap(
        command -> command,
        command -> findTriggers(command)
          .map(Trigger::allEvents)
          .flatMap(List::stream)
          .flatMap(event -> inputUpdates.entrySet().stream()
            .filter(entry -> entry.getKey().matches(event))
            .flatMap(entry -> entry.getValue().stream()))
          .collect(Collectors.toSet())
      ));

    loopScan();

    // Scan deeper command dependencies

    commandsToCommands.entrySet().forEach((Map.Entry<Command<?>, Set<Command<?>>> entry) -> {
      Set<Command<?>> prevSet;
      Set<Command<?>> set;
      boolean same;
      do {
        prevSet = entry.getValue();
        set = prevSet.stream()
          .flatMap(updateCommand -> Stream.concat(Stream.of(updateCommand), commandsToCommands.get(updateCommand).stream()))
          .collect(Collectors.toSet());
        entry.setValue(set);
        if (set.contains(entry.getKey())) {
          // TODO Add custom exception
          throw new RuntimeException("Command loop! " + entry.getKey());
        }
        same = prevSet.equals(set);
      } while (!same);
    });

    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug(">>> Command dependencies >>>");
      commandsToCommands.forEach((key, value) -> {
        LOGGER.debug("Command : {}", DebugUtil.commandToString(key));
        value.forEach(command -> LOGGER.debug("  <= {}", DebugUtil.commandToString(command)));
      });
      LOGGER.debug("<<< Command dependencies <<<");
    }
    // Uncomment this to get dot dump of form.
    // DebugUtil.dumpDotFile(getItemCommands());
  }

  private <T> Stream<Trigger<T>> findTriggers(Command<T> command) {
    return command.triggers().stream();
  }

  private void loopScan() {
    for (final Map.Entry<Command<?>, Set<Command<?>>> entry : commandsToCommands.entrySet()) {
      final Command<?> key = entry.getKey();
      final ArrayList<Command<?>> path = new ArrayList<>();
      path.add(key);
      loopScan(path, key);
    }
  }

  private void loopScan(List<Command<?>> path, Command<?> next) {
    for (Command<?> command : commandsToCommands.get(next)) {
      final boolean contains = path.contains(command);
      path.add(command);
      if (contains) {
        ItemId itemId = IdUtils.QUESTIONNAIRE_ID;
        if (command instanceof UpdateCommand updateCommand) {
          itemId = updateCommand.targetId();
        }
        throw new DependencyLoopException("dependency loop", List.of(new FormValidationError.Builder()
          .type(FormValidationError.Type.GENERAL)
          .level(FormValidationError.Level.ERROR)
          .message("dependency loop")
          .itemId(IdUtils.toString(itemId))
          .build()));
      }
      loopScan(path, command);
      path.remove(command);
    }
  }

  @NonNull
  public Map<EventMatcher, List<Command<?>>> getInputUpdates() {
    return Collections.unmodifiableMap(inputUpdates);
  }

  @NonNull
  public Map<ItemId, List<Command<?>>> getItemCommands() {
    return Collections.unmodifiableMap(itemCommands);
  }

  @NonNull
  public Map<Command<?>, Set<Command<?>>> getCommandsToCommands() {
    return commandsToCommands == null ?
      Collections.emptyMap() :
      Collections.unmodifiableMap(commandsToCommands);
  }
}
