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
package io.dialob.session.engine.session.model;

import com.google.common.collect.MapDifference;
import com.google.common.collect.Maps;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import io.dialob.common.Constants;
import io.dialob.session.engine.DebugUtil;
import io.dialob.session.engine.program.EvalContext;
import io.dialob.session.engine.session.command.*;
import io.dialob.session.engine.session.protobuf.StateReader;
import io.dialob.session.engine.session.protobuf.StateWriter;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

@EqualsAndHashCode
@ToString
@Slf4j
@AllArgsConstructor
//@Value.Builder
//@Value.Style(
//  isSetOnBuilder = true,
//  jakarta = true,
//  jdkOnly = true,
//  overshadowImplementation = true,
//  visibility = Value.Style.ImplementationVisibility.PACKAGE
//)
public class DialobSession implements Serializable {

//  public static final class Builder extends DialobSessionBuilder {}

  @Serial
  private static final long serialVersionUID = 1180110179877247767L;

  public static final ItemRef QUESTIONNAIRE_REF = (ItemRef) IdUtils.toId(Constants.QUESTIONNAIRE);

  @Getter
  @NonNull
  private final String tenantId;

  @Getter
  private final String id;

  private int asyncUpdateCount;

  @Getter
  private String revision;

//  @NonNull
  private Instant lastUpdate;

  private Instant completed;

  private Instant opened;

  @Getter
  @Setter
  private String language;

  @NonNull
  private final Map<ItemId,ItemState> itemStates;

  // TODO move this to DialobProgram
  @NonNull
  private final Map<ItemId,ItemState> itemPrototypes;

  @NonNull
  private final Map<ValueSetId,ValueSetState> valueSetStates;

  @NonNull
  private final Map<ErrorId,ErrorState> errorStates;

  // TODO move this to DialobProgram
  @NonNull
  private final Map<ErrorId,ErrorState> errorPrototypes;

  public void writeTo(StateWriter output) throws IOException {
    output.writeString(tenantId);
    output.writeNullableString(id);
    output.writeNullableString(revision);
    output.writeString(language);
    output.writeDate(lastUpdate);
    output.writeNullableDate(completed);
    output.writeNullableDate(opened);
    output.writeInt(asyncUpdateCount);

    output.writeInt(itemStates.size());
    for (var state : itemStates.values()) {
      state.writeTo(output);
    }

    output.writeInt(itemPrototypes.size());
    for (var state : itemPrototypes.values()) {
      state.writeTo(output);
    }

    output.writeInt(valueSetStates.size());
    for (var state : valueSetStates.values()) {
      state.writeTo(output);
    }

    output.writeInt(errorStates.size());
    for (var state : errorStates.values()) {
      state.writeTo(output);
    }

    output.writeInt(errorPrototypes.size());
    for (var state : errorPrototypes.values()) {
      state.writeTo(output);
    }
  }

  public static DialobSession readFrom(StateReader input) throws IOException {
    var tenantId = input.readString();
    var id = input.readNullableString();
    var revision = input.readNullableString();
    var language = input.readString();
    var lastUpdate = input.readDate();
    var completed = input.readNullableDate();
    var opened = input.readNullableDate();
    var asyncUpdateCount = input.readInt();
    var itemStates = new HashMap<ItemId,ItemState>();
    var itemPrototypes = new HashMap<ItemId,ItemState>();
    var valueSetStates = new HashMap<ValueSetId,ValueSetState>();
    var errorStates = new HashMap<ErrorId,ErrorState>();
    var errorPrototypes = new HashMap<ErrorId,ErrorState>();


    int count = input.readInt();
    for (int i = 0; i < count; ++i) {
      final var state = ItemState.readFrom(input);
      itemStates.put(state.id(), state);
    }
    count = input.readInt();
    for (int i = 0; i < count; ++i) {
      final var state = ItemState.readFrom(input);
      itemPrototypes.put(state.id(), state);
    }
    count = input.readInt();
    for (int i = 0; i < count; ++i) {
      final var state = ValueSetState.readFrom(input);
      valueSetStates.put(state.id(), state);
    }
    count = input.readInt();
    for (int i = 0; i < count; ++i) {
      final var state = ErrorState.readFrom(input);
      errorStates.put(state.id(), state);
    }
    count = input.readInt();
    for (int i = 0; i < count; ++i) {
      final var state = ErrorState.readFrom(input);
      errorPrototypes.put(state.id(), state);
    }
    return new DialobSession(
      tenantId,
      id,
      asyncUpdateCount,
      revision,
      lastUpdate,
      completed,
      opened,
      language,
      itemStates,
      itemPrototypes,
      valueSetStates,
      errorStates,
      errorPrototypes
    );
  }

  public DialobSession withId(@NonNull String id) {
    if (Objects.equals(this.id, id)) {
      return this;
    }
    return new DialobSession(
      this.tenantId,
      id,
      this.asyncUpdateCount,
      this.revision,
      this.lastUpdate,
      this.completed,
      this.opened,
      this.language,
      new HashMap<>(this.itemStates),
      new HashMap<>(this.itemPrototypes),
      new HashMap<>(this.valueSetStates),
      new HashMap<>(this.errorStates),
      new HashMap<>(this.errorPrototypes)
    );
  }

  @NonNull
  public ItemState getRootItem() {
    return getItemState(QUESTIONNAIRE_REF)
      .orElseThrow(() -> new IllegalStateException("Could not find questionnaire from " + getId()));
  }

  public Optional<ItemState> getItemState(@NonNull ItemId id) {
    return Optional.ofNullable(itemStates.get(id));
  }

  public void accept(DialobSessionVisitor visitor) {
    visitor.start();

    // --
    visitor.visitItemStates().ifPresent(itemVisitor -> {
      itemStates.values().forEach(itemVisitor::visitItemState);
      itemVisitor.end();
    });

    // --
    visitor.visitValueSetStates().ifPresent(valueSetVisitor -> {
      valueSetStates.values().forEach(valueSetVisitor::visitValueSetState);
      valueSetVisitor.end();

    });

    // --
    visitor.visitErrorStates().ifPresent(errorVisitor -> {
      errorStates().values().forEach(errorVisitor::visitErrorState);
      errorVisitor.end();
    });

    visitor.end();
  }

  /**
   *
   * @param evalContext execution context
   * @param command object to execute within context
   */
  public DialobSession applyUpdate(@NonNull EvalContext evalContext, @NonNull Command<?> command) {
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("applyUpdate({})", DebugUtil.commandToString(command));
    }
    return switch (command) {
      case ItemUpdateCommand itemUpdateCommand -> {
        EvalContext context = createScopedEvalContext(evalContext, itemUpdateCommand.targetId());
        yield applyItemUpdateCommand(context, itemUpdateCommand).updated();
      }
      case ErrorUpdateCommand errorUpdateCommand -> {
        EvalContext context = createScopedEvalContext(evalContext, errorUpdateCommand.targetId().itemId());
        yield applyErrorUpdateCommand(context, errorUpdateCommand).updated();
      }
      case ValueSetUpdateCommand valueSetCommand -> applyUpdateValueSetCommand(evalContext, valueSetCommand).updated();
      case SessionUpdateCommand updateCommand -> applySessionUpdateCommand(evalContext, updateCommand).updated();
      default -> {
        LOGGER.warn("Do not know how to apply command: {}", command);
        yield this;
      }
    };
  }

  public EvalContext createScopedEvalContext(@NonNull EvalContext evalContext, ItemId itemId) {
    return itemId instanceof ItemIndex ?
      createScope(evalContext, itemId) :
      itemId.getParent().map(parentId -> {
        if (parentId instanceof ItemIndex) {
          return createScope(evalContext, parentId);
        }
        return evalContext;
      }).orElse(evalContext);
  }

  public EvalContext createScope(@NonNull EvalContext evalContext, ItemId itemId) {
    var scopeItems = new ArrayList<>(evalContext
      .getItemState(itemId)
      .map(ItemState::items)
      .orElseGet(Collections::emptyList));
    scopeItems.add(itemId);
    return evalContext.withScope(Scope.of(itemId, scopeItems));
  }

  private DialobSession applySessionUpdateCommand(EvalContext evalContext, SessionUpdateCommand command) {
    final var oldStates = new ItemStates.Builder()
      .putAllItemStates(itemStates())
      .putAllErrorStates(errorStates())
      .putAllValueSetStates(valueSetStates())
      .build();
    final var newStates = command.update(evalContext, oldStates);
    command.triggers().stream()
      .flatMap(trigger -> trigger.apply(oldStates, newStates))
      .forEach(event -> evalContext.getEventsConsumer().accept(event));

    MapDifference<ErrorId,ErrorState> errorDiffs = Maps.difference(newStates.errorStates(), oldStates.errorStates());
    MapDifference<ItemId,ItemState> itemStatesDiffs = Maps.difference(newStates.itemStates(), oldStates.itemStates());

    // Removed
    itemStatesDiffs.entriesOnlyOnRight().forEach((itemId, itemState) -> {
      evalContext.registerUpdate(null, itemState);
      itemStates.remove(itemId);
    });
    errorDiffs.entriesOnlyOnRight().forEach(((errorId, errorState) -> {
      evalContext.registerUpdate(null, errorState);
      errorStates.remove(errorId);
    }));
    // New ones
    itemStatesDiffs.entriesOnlyOnLeft().forEach((itemId, itemState) -> {
      evalContext.registerUpdate(itemState, null);
      itemStates.put(itemId, itemState);
    });
    errorDiffs.entriesOnlyOnLeft().forEach(((errorId, errorState) -> {
      evalContext.registerUpdate(errorState, null);
      errorStates.put(errorId, errorState);
    }));
    // Updated
    itemStatesDiffs.entriesDiffering().forEach((itemId, itemStateDiff) -> {
      evalContext.registerUpdate(itemStateDiff.leftValue(), itemStateDiff.rightValue());
      itemStates.put(itemId, itemStateDiff.leftValue());
    });
    errorDiffs.entriesDiffering().forEach(((errorId, errorState) -> {
      evalContext.registerUpdate(errorState.leftValue(), errorState.rightValue());
      errorStates.put(errorId, errorState.leftValue());
    }));
    return this;
  }

  private DialobSession applyUpdateValueSetCommand(EvalContext evalContext, ValueSetUpdateCommand updateCommand) {
    // alias 'answer' to error's target item.
    // TODO should be bound to command in more generic way
    valueSetStates.computeIfPresent(updateCommand.targetId(), (key, state) -> {
      ValueSetState updatedState = updateCommand.update(evalContext, state);
      updateCommand.triggers().stream()
        .flatMap(trigger -> trigger.apply(state, updatedState))
        .forEach(event -> evalContext.getEventsConsumer().accept(event));
      evalContext.registerUpdate(updatedState, state);
      return updatedState;
    });
    return this;
  }

  private DialobSession applyErrorUpdateCommand(EvalContext evalContext, ErrorUpdateCommand updateCommand) {
    // alias 'answer' to error's target item.
    // TODO should be bound to command in more generic way
    errorStates.computeIfPresent(updateCommand.targetId(), (key, state) -> {
      ErrorState updatedState = updateCommand.update(evalContext, state);
      updateCommand.triggers().stream()
        .flatMap(trigger -> trigger.apply(state, updatedState))
        .forEach(event -> evalContext.getEventsConsumer().accept(event));
      evalContext.registerUpdate(updatedState, state);
      return updatedState;
    });
    return this;
  }

  private DialobSession applyItemUpdateCommand(EvalContext evalContext, ItemUpdateCommand updateCommand) {
    itemStates.computeIfPresent(updateCommand.targetId(), (key, state) -> {
      final ItemState updatedState = updateCommand.update(evalContext, state);
      updateCommand.triggers().stream()
        .flatMap(trigger -> trigger.apply(state, updatedState))
        .forEach(event -> evalContext.getEventsConsumer().accept(event));

      if (state.isDisplayItem()) {
        // If update command is SetAnswer, skip update feedback to ui.
        if (!(updateCommand instanceof SetAnswer)) {
          evalContext.registerUpdate(updatedState, state);
        }
      }
      return updatedState;
    });
    return this;
  }

  protected DialobSession updated() {
    lastUpdate = Instant.now();
    if (opened == null) {
      opened = lastUpdate;
    }
    revision = Integer.toString(ThreadLocalRandom.current().nextInt());
    LOGGER.trace("{} updated to rev {}", getId(), revision);
    return this;
  }

  public Optional<ErrorState> getErrorState(ItemId itemId, String code) {
    return Optional.ofNullable(errorStates.get(new ErrorId(itemId, code)));
  }

  @NonNull
  public Instant getLastUpdate() {
    return lastUpdate;
  }

  @Nullable
  public Instant getCompleted() {
    return completed;
  }

  @Nullable
  public Instant getOpened() {
    return opened;
  }


  @Nullable
  public Instant getLastAnswer() {
    return lastUpdate;
  }

  public boolean isCompleted() {
    return completed != null;
  }

  public boolean complete() {
    if (this.completed == null) {
      this.completed = Instant.now();
    }
    return isCompleted();
  }

  @NonNull
  public Map<ItemId, ItemState> itemStates() {
    return Collections.unmodifiableMap(itemStates);
  }

  @NonNull
  public Map<ValueSetId, ValueSetState> valueSetStates() {
    return Collections.unmodifiableMap(valueSetStates);
  }

  @NonNull
  public Map<ErrorId, ErrorState> errorStates() {
    return Collections.unmodifiableMap(errorStates);
  }

  @NonNull
  public Optional<ValueSetState> getValueSetState(ValueSetId id) {
    return Optional.of(valueSetStates.get(id));
  }

  public Optional<ItemState> findPrototype(ItemId itemId) {
    if (itemId.isPartial()) {
      return Optional.ofNullable(itemPrototypes.get(itemId));
    }
    return itemPrototypes.values().stream()
      .filter(itemState -> IdUtils.matches(itemState.id(), itemId))
      .findFirst();
  }

  @NonNull
  private Stream<Map.Entry<ItemId,ItemState>> findMatchingItemsEntries(ItemId partialItemId) {
    return itemStates
      .entrySet()
      .stream()
      .filter(item -> IdUtils.matches(partialItemId, item.getKey()));
  }

  @NonNull
  private Stream<Map.Entry<ErrorId,ErrorState>> findMatchingErrorEntries(ErrorId partialErrorId) {
    return errorStates
      .entrySet()
      .stream()
      .filter(item -> IdUtils.matches(partialErrorId, item.getKey()));
  }

  @NonNull
  public Stream<ItemId> findMatchingItemIds(ItemId partialItemId) {
    final UnaryOperator<Map.Entry<? extends ItemId, ?>> logger = LOGGER.isDebugEnabled() ?
      itemEntry -> {
        LOGGER.debug("Matched {} -> {}", partialItemId, itemEntry.getKey());
        return itemEntry;
      } :
      UnaryOperator.identity();

    if (partialItemId instanceof ErrorId errorId) {
      return findMatchingErrorEntries(errorId)
        .map(logger)
        .map(Map.Entry::getKey);
    }
    return findMatchingItemsEntries(partialItemId)
      .map(logger)
      .map(Map.Entry::getKey);
  }

  @NonNull
  public Stream<ErrorState> findErrorPrototypes(ItemId itemId) {
    if (itemId.isPartial()) {
      return errorPrototypes.values().stream().filter(errorPrototype -> errorPrototype.itemId().equals(itemId));
    }
    return errorPrototypes.values().stream().filter(errorPrototype -> IdUtils.matches(errorPrototype.itemId(), itemId));
  }

  public String generateUpdateId() {
    return Integer.toString(asyncUpdateCount++);
  }
}
