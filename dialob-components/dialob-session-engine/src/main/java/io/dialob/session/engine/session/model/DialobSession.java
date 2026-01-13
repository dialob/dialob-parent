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
public class DialobSession implements EvalContext.SessionFacade, Serializable {

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
  private final MutableItemStates itemStates;

  // TODO move this to DialobProgram
  @NonNull
  private final ItemStates prototypes;

  public void writeTo(StateWriter output) throws IOException {
    output.writeString(tenantId);
    output.writeNullableString(id);
    output.writeNullableString(revision);
    output.writeString(language);
    output.writeDate(lastUpdate);
    output.writeNullableDate(completed);
    output.writeNullableDate(opened);
    output.writeInt(asyncUpdateCount);

    output.writeInt(itemStates.itemStates().size());
    for (var state : itemStates.itemStates().values()) {
      state.writeTo(output);
    }

    output.writeInt(prototypes.itemStates().size());
    for (var state : prototypes.itemStates().values()) {
      state.writeTo(output);
    }

    output.writeInt(itemStates.valueSetStates().size());
    for (var state : itemStates.valueSetStates().values()) {
      state.writeTo(output);
    }

    output.writeInt(itemStates.errorStates().size());
    for (var state : itemStates.errorStates().values()) {
      state.writeTo(output);
    }

    output.writeInt(prototypes.errorStates().size());
    for (var state : prototypes.errorStates().values()) {
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
      new MutableItemStates(new ItemStates.Builder()
        .itemStates(itemStates)
        .errorStates(errorStates)
        .valueSetStates(valueSetStates)
        .build()),
      new ItemStates.Builder()
        .itemStates(itemPrototypes)
        .errorStates(errorPrototypes)
        .build()
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
      this.itemStates,
      this.prototypes
    );
  }

  @NonNull
  public ItemState getRootItem() {
    return getItemState(QUESTIONNAIRE_REF)
      .orElseThrow(() -> new IllegalStateException("Could not find questionnaire from " + getId()));
  }

  public Optional<ItemState> getItemState(@NonNull ItemId id) {
    return Optional.ofNullable(itemStates().get(id));
  }

  public void accept(DialobSessionVisitor visitor) {
    visitor.start();

    // --
    visitor.visitItemStates().ifPresent(itemVisitor -> {
      itemStates().values().forEach(itemVisitor::visitItemState);
      itemVisitor.end();
    });

    // --
    visitor.visitValueSetStates().ifPresent(valueSetVisitor -> {
      valueSetStates().values().forEach(valueSetVisitor::visitValueSetState);
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
    switch (command) {
      case ItemUpdateCommand itemUpdateCommand -> {
        EvalContext context = createScopedEvalContext(evalContext, itemUpdateCommand.targetId());
        new ItemUpdateCommandExecutor().applyCommand(context, itemUpdateCommand);
      }
      case ErrorUpdateCommand errorUpdateCommand -> {
        EvalContext context = createScopedEvalContext(evalContext, errorUpdateCommand.targetId().itemId());
        new ErrorUpdateCommandExecutor().applyCommand(context, errorUpdateCommand);
      }
      case ValueSetUpdateCommand valueSetCommand ->
        new ValueSetUpdateCommandExecutor().applyCommand(evalContext, valueSetCommand);
      case SessionUpdateCommand updateCommand ->
        new SessionUpdateCommandExecutor().applyCommand(evalContext, updateCommand);
      default -> LOGGER.warn("Do not know how to apply command: {}", command);
    }
    updated();
    return this;
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
    return Optional.ofNullable(errorStates().get(new ErrorId(itemId, code)));
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
    return Collections.unmodifiableMap(itemStates.itemStates());
  }

  @Override
  public ItemStates prototypes() {
    return prototypes;
  }

  @NonNull
  public Map<ValueSetId, ValueSetState> valueSetStates() {
    return Collections.unmodifiableMap(itemStates.valueSetStates());
  }

  @NonNull
  public Map<ErrorId, ErrorState> errorStates() {
    return Collections.unmodifiableMap(itemStates.errorStates());
  }

  @NonNull
  public Optional<ValueSetState> getValueSetState(ValueSetId id) {
    return Optional.of(valueSetStates().get(id));
  }

  public Optional<ItemState> findPrototype(ItemId itemId) {
    if (itemId.isPartial()) {
      return Optional.ofNullable(prototypes.itemStates().get(itemId));
    }
    return prototypes.itemStates().values().stream()
      .filter(itemState -> IdUtils.matches(itemState.id(), itemId))
      .findFirst();
  }




  public String generateUpdateId() {
    return Integer.toString(asyncUpdateCount++);
  }

  public MutableItemStates mutableItemStates() {
    return itemStates;
  }
}
