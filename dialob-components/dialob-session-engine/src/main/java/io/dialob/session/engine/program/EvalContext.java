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
import edu.umd.cs.findbugs.annotations.Nullable;
import io.dialob.rule.parser.function.FunctionRegistry;
import io.dialob.session.engine.program.expr.OutputFormatter;
import io.dialob.session.engine.session.AsyncFunctionCall;
import io.dialob.session.engine.session.command.event.Event;
import io.dialob.session.engine.session.model.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collection;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Stream;

public interface EvalContext {

  @NonNull
  Optional<ItemState> getItemState(@NonNull ItemId itemId);

  @NonNull
  Optional<ItemState> getOriginalItemState(@NonNull ItemId itemId);

  @NonNull
  Optional<ItemState> findPrototype(@NonNull ItemId itemId);

  @NonNull
  Stream<ErrorState> findErrorPrototypes(@NonNull ItemId itemId);

  @NonNull
  Optional<ValueSetState> getValueSetState(@NonNull ValueSetId valueSetId);

  @Nullable
  Object getItemValue(ItemId itemId);

  EvalContext withScope(Scope scope);

  EvalContext getParent();

  void registerUpdate(ItemState newState, ItemState oldState);

  void registerUpdate(ErrorState newState, ErrorState oldState);

  void registerUpdate(@NonNull ValueSetState newState, ValueSetState oldState);

  void accept(@NonNull UpdatedItemsVisitor visitor);

  String getLanguage();

  void setLanguage(String language);

  @NonNull
  Consumer<Event> getEventsConsumer();

  Collection<ErrorState> getErrorStates();

  @NonNull
  FunctionRegistry getFunctionRegistry();

  @NonNull
  default LocalDate today() {
    return LocalDate.now();
  }

  @NonNull
  default LocalTime now() {
    return LocalTime.now();
  }

  @NonNull
  OutputFormatter getOutputFormatter();

  boolean isActivating();

  ItemId mapTo(ItemId itemId, boolean ignoreScopeItems);

  boolean complete();

  /**
   *
   * @param asyncFunctionCall
   * @return id of update
   */
  String queueAsyncFunctionCall(AsyncFunctionCall asyncFunctionCall);

  interface UpdatedItemsVisitor {

    @FunctionalInterface
    interface UpdatedSessionStateVisitor {
      void visitLanguageChange(@NonNull String original, @NonNull String updated);
      default void end() {}
    }

    Optional<UpdatedSessionStateVisitor> visitSession();

    @FunctionalInterface
    interface UpdatedItemStateVisitor {
      void visitUpdatedItemState(@Nullable ItemState original, @Nullable ItemState updated);
      default void end() {}
    }

    @FunctionalInterface
    interface UpdatedErrorStateVisitor {
      void visitUpdatedErrorState(@Nullable ErrorState original, @Nullable ErrorState updated);
      default void end() {}
    }

    @FunctionalInterface
    interface UpdatedValueSetVisitor {
      void visitUpdatedValueSet(@Nullable ValueSetState original, @Nullable ValueSetState updated);
      default void end() {}
    }

    @FunctionalInterface
    interface AsyncFunctionCallVisitor {
      void visitAsyncFunctionCall(@NonNull AsyncFunctionCall asyncFunctionCall);
      default void end() {}
    }

    default void start() {}

    default Optional<UpdatedItemStateVisitor> visitUpdatedItems() {
      return Optional.empty();
    }

    default Optional<UpdatedErrorStateVisitor> visitUpdatedErrorStates() {
      return Optional.empty();
    }

    default Optional<UpdatedValueSetVisitor> visitUpdatedValueSets() {
      return Optional.empty();
    }

    default Optional<AsyncFunctionCallVisitor> visitAsyncFunctionCalls() {
      return Optional.empty();
    }

    default void visitCompleted() {}

    default void end() {}
  }

  abstract class AbstractDelegateUpdatedItemsVisitor implements UpdatedItemsVisitor {

    private final UpdatedItemsVisitor delegate;

    public AbstractDelegateUpdatedItemsVisitor(UpdatedItemsVisitor delegate) {
      this.delegate = delegate;
    }

    @Override
    public void start() {
      delegate.start();
    }

    @Override
    public Optional<UpdatedItemStateVisitor> visitUpdatedItems() {
      return delegate.visitUpdatedItems();
    }

    @Override
    public Optional<UpdatedErrorStateVisitor> visitUpdatedErrorStates() {
      return delegate.visitUpdatedErrorStates();
    }

    @Override
    public Optional<UpdatedValueSetVisitor> visitUpdatedValueSets() {
      return delegate.visitUpdatedValueSets();
    }

    @Override
    public Optional<AsyncFunctionCallVisitor> visitAsyncFunctionCalls() {
      return delegate.visitAsyncFunctionCalls();
    }

    @Override
    public void visitCompleted() {
      delegate.visitCompleted();
    }

    @Override
    public Optional<UpdatedSessionStateVisitor> visitSession() {
      return delegate.visitSession();
    }

    @Override
    public void end() {
      delegate.end();
    }


  }

}
