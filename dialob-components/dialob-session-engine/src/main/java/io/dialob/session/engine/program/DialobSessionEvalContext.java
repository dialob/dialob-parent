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
import io.dialob.rule.parser.function.FunctionRegistry;
import io.dialob.session.engine.DialobSessionUpdateHook;
import io.dialob.session.engine.program.expr.OutputFormatter;
import io.dialob.session.engine.session.AsyncFunctionCall;
import io.dialob.session.engine.session.ImmutableAsyncFunctionCall;
import io.dialob.session.engine.session.command.Command;
import io.dialob.session.engine.session.command.event.Event;
import io.dialob.session.engine.session.model.*;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static java.util.Objects.requireNonNull;

public class DialobSessionEvalContext implements EvalContext {

  private static final DialobSessionUpdateHook DEFAULT_DIALOB_SESSION_EVAL_HOOKS = (dialobSession, update, delegate) -> delegate.accept(update);

  private final DialobSessionEvalContext parent;

  private final Scope scope;

  private final FunctionRegistry functionRegistry;

  private final DialobSession dialobSession;

  private final Consumer<Event> updatesConsumer;

  private final ItemStates originalStates;

  private final Set<ItemId> updatedItemIds;

  private final Set<ErrorId> updatedErrorIds;

  private final Set<ValueSetId> updatedValueSetIds;

  private final Map<ItemId, AsyncFunctionCall> pendingUpdates;

  private final boolean activating;

  private boolean didComplete;

  private DialobSessionUpdateHook dialobSessionUpdateHook;

  private String originalLanguage;

  DialobSessionEvalContext(
    @NonNull FunctionRegistry functionRegistry,
    @NonNull DialobSession dialobSession,
    @NonNull Consumer<Event> updatesConsumer,
    boolean activating,
    DialobSessionUpdateHook dialobSessionUpdateHook)
  {
    this.parent = null;
    this.scope = null;
    this.functionRegistry = functionRegistry;
    this.dialobSession = dialobSession;
    this.updatesConsumer = updatesConsumer;
    this.activating = activating;
    this.originalStates = ImmutableItemStates.builder().from(dialobSession).build();
    this.pendingUpdates = new HashMap<>();
    this.updatedItemIds = new HashSet<>();
    this.updatedErrorIds = new HashSet<>();
    this.updatedValueSetIds = new HashSet<>();
    this.dialobSessionUpdateHook = dialobSessionUpdateHook != null ? dialobSessionUpdateHook : DEFAULT_DIALOB_SESSION_EVAL_HOOKS;

  }


  private DialobSessionEvalContext(@NonNull DialobSessionEvalContext parent, @NonNull Scope scope)
  {
    this.parent = parent;
    this.scope = scope;
    this.functionRegistry = parent.functionRegistry;
    this.dialobSession = parent.dialobSession;
    this.updatesConsumer = parent.updatesConsumer;
    this.activating = parent.activating;
    this.originalStates = parent.originalStates;
    this.pendingUpdates = parent.pendingUpdates;
    this.updatedItemIds = parent.updatedItemIds;
    this.updatedErrorIds = parent.updatedErrorIds;
    this.updatedValueSetIds = parent.updatedValueSetIds;
    this.dialobSessionUpdateHook = parent.dialobSessionUpdateHook;
  }


  public void applyAction(@NonNull Command<?> action) {
    dialobSessionUpdateHook.hookAction(dialobSession, action, a -> this.dialobSession.applyUpdate(this, a));
  }

  @Override
  public EvalContext withScope(Scope scope) {
    return new DialobSessionEvalContext(this, scope);
  }

  @Override
  public EvalContext getParent() {
    return parent;
  }

  @NonNull
  @Override
  public Optional<ItemState> getItemState(@NonNull ItemId itemId) {
    if (IdUtils.QUESTIONNAIRE_ID.equals(itemId)) {
      return Optional.of(this.dialobSession.getRootItem());
    }
    return this.dialobSession.getItemState(scope(itemId, false));
  }

  @NonNull
  @Override
  public Optional<ItemState> getOriginalItemState(@NonNull ItemId itemId) {
    final ItemId scopedId = scope(itemId, false);
    ItemState originalState = this.originalStates.getItemStates().get(scopedId);
    if (originalState != null) {
      return Optional.of(originalState);
    }
    return Optional.empty();
  }

  @NonNull
  @Override
  public Optional<ItemState> findPrototype(@NonNull ItemId itemId) {
    return dialobSession.findPrototype(itemId);
  }

  @NonNull
  @Override
  public Stream<ErrorState> findErrorPrototypes(@NonNull ItemId itemId) {
    return dialobSession.findErrorPrototypes(itemId);
  }

  @Override
  @NonNull
  public Optional<ValueSetState> getValueSetState(@NonNull ValueSetId valueSetId) {
    return this.dialobSession.getValueSetState(valueSetId);
  }

  public Object getItemValue(ItemId itemId) {
    return getItemState(scope(itemId, false)).map(ItemState::getValue).orElse(null);
  }

  @NonNull
  private ItemId scope(@NonNull ItemId itemId, boolean ignoreScopeItems) {
    if (scope != null) {
      return scope.mapTo(itemId, ignoreScopeItems);
    }
    return itemId;
  }

  @Override
  public void registerUpdate(ItemState newState, ItemState oldState) {
    if (newState != oldState) {
      ItemId id;
      if (oldState != null) {
        id = oldState.getId();
      } else {
        id = newState.getId();
      }
      updatedItemIds.add(requireNonNull(id));
    }
  }

  @Override
  public void registerUpdate(ErrorState newState, ErrorState oldState) {
    if (newState != oldState) {
      ErrorId id;
      if (oldState != null) {
        id = oldState.getId();
      } else {
        id = newState.getId();
      }
      updatedErrorIds.add(requireNonNull(id));
    }
  }

  @Override
  public void registerUpdate(@NonNull ValueSetState newState, ValueSetState oldState) {
    if (newState != oldState) {
      ValueSetId id;
      if (oldState != null) {
        id = oldState.getId();
      } else {
        id = newState.getId();
      }
      updatedValueSetIds.add(requireNonNull(id));
    }
  }

  public void accept(@NonNull UpdatedItemsVisitor visitor) {
    visitor.start();
    if (originalLanguage != null) {
      visitor.visitSession().ifPresent(sessionUpdatesVisitor -> {
        sessionUpdatesVisitor.visitLanguageChange(originalLanguage, dialobSession.getLanguage());
        sessionUpdatesVisitor.end();
      });
    }
    visitor.visitUpdatedItems().ifPresent(updatedItemStateVisitor -> {
      for (ItemId updateItemId : this.updatedItemIds) {
        ItemState originalState = originalStates.getItemStates().get(updateItemId);
        Optional<ItemState> itemState1 = dialobSession.getItemState(updateItemId);
        if (itemState1.map(itemState -> itemState != originalState).orElse(originalState != null)) {
          updatedItemStateVisitor.visitUpdatedItemState(originalState, itemState1.orElse(null));
        }
      }
      updatedItemStateVisitor.end();
    });

    visitor.visitUpdatedErrorStates().ifPresent(updatedErrorStateVisitor -> {
      for (final ErrorId errorId : this.updatedErrorIds) {
        ErrorState originalState = originalStates.getErrorStates().get(errorId);
        Optional<ErrorState> itemState1 = dialobSession.getErrorState(errorId.getItemId(), errorId.getCode());
        if (itemState1.map(itemState -> itemState != originalState).orElse(originalState != null)) {
          updatedErrorStateVisitor.visitUpdatedErrorState(originalState, itemState1.orElse(null));
        }
      }
      updatedErrorStateVisitor.end();
    });

    visitor.visitUpdatedValueSets().ifPresent(updatedValueSetVisitor -> {
      for (final ValueSetId valueSetId : this.updatedValueSetIds) {
        ValueSetState originalState = originalStates.getValueSetStates().get(valueSetId);
        Optional<ValueSetState> valueSetState = dialobSession.getValueSetState(valueSetId);
        if (valueSetState.map(itemState -> itemState != originalState).orElse(originalState != null)) {
          updatedValueSetVisitor.visitUpdatedValueSet(originalState, valueSetState.orElse(null));
        }
      }
      updatedValueSetVisitor.end();
    });

    visitor.visitAsyncFunctionCalls().ifPresent(asyncFunctionCallVisitor -> {
      pendingUpdates.values().forEach(asyncFunctionCallVisitor::visitAsyncFunctionCall);
      asyncFunctionCallVisitor.end();
    });

    if (didComplete) {
      visitor.visitCompleted();
    }
    visitor.end();
  }

  @Override
  public String getLanguage() {
    return this.dialobSession.getLanguage();
  }

  @Override
  public void setLanguage(String language) {
    if (this.originalLanguage == null) {
      this.originalLanguage = this.dialobSession.getLanguage();
    }
    this.dialobSession.setLanguage(language);
  }

  @NonNull
  @Override
  public Consumer<Event> getEventsConsumer() {
    return this.updatesConsumer;
  }

  @Override
  public Collection<ErrorState> getErrorStates() {
    return this.dialobSession.getErrorStates().values();
  }

  @NonNull
  @Override
  public FunctionRegistry getFunctionRegistry() {
    return this.functionRegistry;
  }

  @Override
  @NonNull
  public OutputFormatter getOutputFormatter() {
    return new OutputFormatter(dialobSession.getLanguage());
  }

  @Override
  public boolean isActivating() {
    return activating;
  }

  @Override
  public Optional<ItemState> findHoistingGroup(ItemId id) {
    return this.dialobSession.findHoistingGroup(id);
  }

  @Override
  public ItemId mapTo(ItemId itemId, boolean ignoreScopeItems) {
    return scope(itemId, ignoreScopeItems);
  }

  @Override
  public boolean complete() {
    if (!this.dialobSession.isCompleted()) {
      if (this.dialobSession.complete()) {
        this.didComplete = true;
        return true;
      }
    }
    return false;
  }

  @Override
  public String queueAsyncFunctionCall(AsyncFunctionCall asyncFunctionCall) {
    return asyncFunctionCall.getTargetId().map(itemId -> {
      pendingUpdates.put(itemId, ((ImmutableAsyncFunctionCall) asyncFunctionCall)
        .withId(dialobSession.generateUpdateId()));
      return IdUtils.toString(itemId);
    }).orElse(null);
  }
}
