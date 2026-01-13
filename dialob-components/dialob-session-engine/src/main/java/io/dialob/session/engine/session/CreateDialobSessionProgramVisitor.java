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
import io.dialob.security.tenant.ResysSecurityConstants;
import io.dialob.session.engine.Utils;
import io.dialob.session.engine.program.expr.arith.RowItemsExpression;
import io.dialob.session.engine.program.model.*;
import io.dialob.session.engine.session.command.Command;
import io.dialob.session.engine.session.command.CommandFactory;
import io.dialob.session.engine.session.command.UpdateCommand;
import io.dialob.session.engine.session.model.*;
import lombok.Getter;

import java.math.BigInteger;
import java.time.Instant;
import java.util.*;
import java.util.stream.Stream;

import static io.dialob.session.engine.Utils.*;

public class CreateDialobSessionProgramVisitor implements ProgramVisitor {

  private final String tenantId;

  private final String sessionId;

  private final String language;

  private final Instant completed;

  private final Instant opened;

  private final Instant lastAnswer;

  @Getter
  private DialobSession dialobSession;

  private Program program;

  private final List<ItemState> items = new ArrayList<>();

  private final List<ItemState> prototypeItems = new ArrayList<>();

  private final List<ValueSetState> valueSets = new ArrayList<>();

  private final List<ErrorState> errors = new ArrayList<>();

  private final List<ErrorState> errorPrototypes = new ArrayList<>();

  @Getter
  private final List<Command<?>> updates = new ArrayList<>();

  private final InitialValueResolver initialValueResolver;

  private final Map<ItemId, List<Command<?>>> itemCommands;

  private final List<ItemState> rowGroups = new ArrayList<>();

  private final ItemId activePage;

  private final ProvidedValueSetEntriesResolver findProvidedValueSetEntries;

  @FunctionalInterface
  public interface InitialValueResolver {
    Optional<Object> apply(ItemId itemId, Item item);
  }

  @FunctionalInterface
  public interface ProvidedValueSetEntriesResolver {
    List<ValueSetState.Entry> apply(ValueSetId valueSetId);
  }

  public CreateDialobSessionProgramVisitor(
    String tenantId,
    String sessionId,
    String language,
    String activePage,
    InitialValueResolver initialValueResolver,
    ProvidedValueSetEntriesResolver findProvidedValueSetEntries,
    Map<ItemId, List<Command<?>>> itemCommands,
    Instant completed,
    Instant opened,
    Instant lastAnswer) {
    this.tenantId = tenantId;
    this.sessionId = sessionId;
    this.language = language;
    this.initialValueResolver = initialValueResolver;
    this.findProvidedValueSetEntries = findProvidedValueSetEntries;
    this.itemCommands = itemCommands == null ? Map.of() : itemCommands;
    this.activePage = activePage != null ? IdUtils.toId(activePage) : null;
    this.completed = completed;
    this.opened = opened;
    this.lastAnswer = lastAnswer;
  }

  @Override
  public void startProgram(@NonNull Program program) {
    this.program = program;
  }


  @Override
  public Optional<ItemVisitor> visitItems() {
    return Optional.of(new AbstractItemVisitor() {


      @Override
      public void visitVariableItem(@NonNull VariableItem item) {
        items.add(createItemState(item.id(), item, item.isPublished()));
      }

      @Override
      public void visitDisplayItem(@NonNull DisplayItem displayItem) {
        ItemState itemState = createItemState(displayItem.id(), displayItem, true);
        if (displayItem.isPrototype()) {
          prototypeItems.add(itemState);
        } else {
          items.add(itemState);
        }
      }

      @Override
      public void visitGroup(@NonNull Group group) {
        ItemState itemState = createItemState(group.id(), group, true);
        if (group.isPrototype()) {
          prototypeItems.add(itemState);
        } else {
          if (isRowgroup(group.type())) {
            rowGroups.add(itemState);
          } else {
            items.add(itemState);
          }
        }
      }
    });
  }


  @Override
  public Optional<ErrorVisitor> visitErrors() {
    return Optional.of(error -> {
      final ErrorId targetId = new ErrorId(error.itemId(), error.code());
      ErrorState errorState = new ErrorState(targetId, (String) null);
      if (error.isPrototype()) {
        errorPrototypes.add(errorState);
      } else {
        errors.add(errorState);
        collectItemUpdateCommands(targetId);
      }
    });
  }



  @Override
  public Optional<ValueSetVisitor> visitValueSets() {
    return Optional.of(valueSet -> {
      updates.add(CommandFactory.updateValueSet(new ValueSetId(valueSet.id()), valueSet.entries()));
      ValueSetState valueSetState = new ValueSetState(valueSet.id(), null);
      valueSetState = valueSetState.update().setEntries(findProvidedValueSetEntries.apply(valueSetState.id())).get();
      valueSets.add(valueSetState);
    });
  }

  @NonNull
  private ItemState createItemState(@NonNull final ItemId itemId, @NonNull final Item item, boolean published) {
    Object defaultValue = null;
    Object answer = null;
    Object value = null;
    ItemId activePage = null;
    if (!isNote(item.type())) {
      defaultValue = item.defaultValueOptional().map(o -> Utils.parse(item.valueType(), o)).orElse(null);
      if (!item.isPrototype()) {
        answer = initialValueResolver.apply(item.id(), item).orElse(null);
      }
      if (item.valueType() != null) {
        value = Utils.parse(item.valueType(), answer);
      }
      if (isVariable(item.type())) {
        answer = null;
      }
      if (DialobSession.QUESTIONNAIRE_REF.equals(item.id())) {
        activePage = this.activePage;
      }
    }


    final boolean displayItem = item instanceof DisplayItem;

    final String view = displayItem ? ((DisplayItem) item).view():null;
    final boolean hasCustomProps = displayItem && ((DisplayItem) item).props() != null;

    final ItemState itemState = new ItemState(
      itemId,
      item.isPrototype() ? itemId : null,
      item.type(),
      view,
      item.valueSetIdOptional().orElse(null),
      ItemState.Status.NEW,
      answer,
      value,
      defaultValue,
      (published ? ItemState.DISPLAY_ITEM_BIT : 0)
        | (hasCustomProps ? ItemState.HAS_CUSTOM_PROPS_BIT : 0)
        | ItemState.ACTIVE_BIT
        | ItemState.ROWS_CAN_BE_ADDED_BIT,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      activePage);
    if (!item.isPrototype()) {
      collectItemUpdateCommands(itemId);
    }
    return itemState;
  }

  private void collectItemUpdateCommands(ItemId targetId) {
    itemCommands.entrySet().stream().filter(entry -> IdUtils.matches(targetId, entry.getKey()))
      .map(Map.Entry::getValue)
      .flatMap(List::stream)
      .map(command -> {
        if (command instanceof UpdateCommand updateCommand && updateCommand.targetId().isPartial()) {
          command = updateCommand.withTargetId(targetId);
        }
        return command;
      }).forEach(updates::add);
  }

  @Override
  public void end() {
    // TODO this is way too complex. This restores row states from Questionnaire. This decouples DialobSession object from
    // Questionnaire type. Problem is that this duplicates logic build into commands. Expression evaluation context needs to be simplified.
    rowGroups
      .stream()
      .flatMap(rowGroup -> {
        List<BigInteger> rowNumbers = ((List<BigInteger>)rowGroup.getValue());
        if (rowNumbers == null) {
          return Stream.empty();
        }
        return rowNumbers.stream().flatMap(rowNumber -> {
          final ItemId rowId = new ItemIndex(rowNumber.intValue(), rowGroup.id());
          // Create stream of all new item ids
          return Stream.concat(
            Stream.of(rowId),
            program.items().stream()
              .filter(itemPrototype -> itemPrototype.isPrototype() &&
                IdUtils.matches(rowId, itemPrototype.id()))
              .map(itemPrototype -> (Group) itemPrototype)
              .map(groupPrototype -> (RowItemsExpression) groupPrototype.itemsExpression())
              .flatMap(rowItemsExpression -> rowItemsExpression.itemIds().stream())
              .map(ItemId::getValue)
              .map(name -> new ItemRef(name, rowId)));
        });
      }).flatMap(itemIdToCreate -> prototypeItems
      .stream()
      .filter(prototype -> IdUtils.matches(prototype.id(), itemIdToCreate))
      .map(prototype -> {
        // Find prototype for each id and instantiate itemstate from it
        ItemState newItem = prototype.withId(itemIdToCreate);
        newItem = program.findItemsBy(id -> IdUtils.matches(id, itemIdToCreate)).findFirst().map(item -> {
          List<ItemId> rowItems = List.of();
          if (item instanceof Group group) {
            Expression expression = group.itemsExpression();
            if (expression instanceof RowItemsExpression rowItemsExpression) {
              final Scope scope = Scope.of(itemIdToCreate, Set.of());
              rowItems = rowItemsExpression.itemIds().stream().map(itemId -> scope.mapTo(itemId, true)).toList();
            }
          }
          final Object newAnswer = initialValueResolver.apply(itemIdToCreate, item).orElse(null);
          return prototype.withId(itemIdToCreate).update().setAnswer(newAnswer).setValue(Utils.parse(prototype.type(), newAnswer)).setItems(rowItems).get();
        }).orElse(newItem);
        collectItemUpdateCommands(itemIdToCreate);
        // create error states for each created state
        errorPrototypes.stream()
          .filter(errorPrototype -> IdUtils.matches(itemIdToCreate, errorPrototype.itemId())).map(errorPrototype -> errorPrototype.withErrorId(errorPrototype.id().withItemId(itemIdToCreate)))
          .peek(errorPrototype -> collectItemUpdateCommands(errorPrototype.id()))
          .forEach(errors::add);
        return newItem;
      }))
      .forEach(items::add); // Collect all new states
    // setup items list of reach row group
    rowGroups.stream()
      .map(rowGroup -> {
        if (rowGroup.getValue() != null) {
          return rowGroup
            .update().setItems(
              ((List<BigInteger>) rowGroup.getValue())
                .stream()
                .map(rowNumber -> (ItemId) new ItemIndex(rowNumber.intValue(), rowGroup.id())).toList()
            ).get();
        }
        return rowGroup;
      })
      .forEach(items::add);
    // find first whenActiveUpdated page, if activePage is unset
    if (activePage == null) {
      updates.add(CommandFactory.nextPage());
    }
    var itemStatesMap = new HashMap<ItemId, ItemState>();
    var itemPrototypesMap = new HashMap<ItemId, ItemState>();
    var valueSetStatesMap = new HashMap<ValueSetId, ValueSetState>();
    var errorStatesMap = new HashMap<ErrorId, ErrorState>();
    var errorPrototypesMap = new HashMap<ErrorId, ErrorState>();
    items.forEach(item -> itemStatesMap.put(item.id(), item));
    valueSets.forEach(item -> valueSetStatesMap.put(item.id(), item));
    errors.forEach(item -> errorStatesMap.put(new ErrorId(item.itemId(), item.code()), item));
    errorPrototypes.forEach(item -> errorPrototypesMap.put(new ErrorId(item.itemId(), item.code()), item));
    prototypeItems.forEach(prototype -> itemPrototypesMap.put(prototype.id(), prototype));
    this.dialobSession = new DialobSession(
      Objects.requireNonNullElseGet(tenantId, ResysSecurityConstants.DEFAULT_TENANT::id),
      sessionId,
      0,
      null,
      lastAnswer,
      completed,
      opened,
      language,
      new MutableItemStates(new ItemStates.Builder()
        .itemStates(itemStatesMap)
        .errorStates(errorStatesMap)
        .valueSetStates(valueSetStatesMap)
        .build()),
      new ItemStates.Builder()
        .itemStates(itemPrototypesMap)
        .errorStates(errorPrototypesMap)
        .build()
    );
  }

}
