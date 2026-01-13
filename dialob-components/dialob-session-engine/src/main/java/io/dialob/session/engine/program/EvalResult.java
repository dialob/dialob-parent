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
import io.dialob.session.engine.session.AsyncFunctionCall;
import io.dialob.session.engine.session.model.*;
import org.immutables.value.Value;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

@Value.Builder
@Value.Style(
  jakarta = true,
  jdkOnly = true,
  jdk9Collections = true,
  overshadowImplementation = true,
  visibility = Value.Style.ImplementationVisibility.PACKAGE
)
public record EvalResult(
  @Nullable
  String language,
  @Nullable
  String originalLanguage,
  @NonNull
  ItemStates originalStates,
  @NonNull
  ItemStates updatedStates,
  @NonNull
  List<ItemId> updatedItemIds,
  @NonNull
  List<ErrorId> updatedErrorIds,
  @NonNull
  List<ValueSetId> updatedValueSetIds,
  @NonNull
  Map<ItemId, AsyncFunctionCall> pendingUpdates,
  boolean didComplete
) implements Consumer<EvalResult.UpdatedItemsVisitor> {

  public static final EvalResult NO_UPDATES = new EvalResult.Builder()
    .language(null)
    .originalLanguage(null)
    .originalStates(ItemStates.EMPTY)
    .updatedStates(ItemStates.EMPTY)
    .updatedItemIds(List.of())
    .updatedErrorIds(List.of())
    .updatedValueSetIds(List.of())
    .pendingUpdates(Map.of())
    .didComplete(false)
    .build();

  public static final class Builder extends EvalResultBuilder {}

  @Override
  public void accept(@NonNull UpdatedItemsVisitor visitor) {
    visitor.start();
    if (originalLanguage != null) {  // !Objects.equals(originalLanguage(), language())
      visitor.visitSession().ifPresent(sessionUpdatesVisitor -> {
        sessionUpdatesVisitor.visitLanguageChange(originalLanguage(), language());
        sessionUpdatesVisitor.end();
      });
    }
    visitor.visitUpdatedItems().ifPresent(itemVisitor -> {
      for (final var id : this.updatedItemIds) {
        var original = originalStates().itemStates().get(id);
        var updated = updatedStates().itemStates().get(id);
        if (updated != original) {
          itemVisitor.visitUpdatedItemState(original, updated);
        }
      }
      itemVisitor.end();
    });

    visitor.visitUpdatedErrorStates().ifPresent(itemVisitor -> {
      for (final var id : this.updatedErrorIds) {
        var original = originalStates().errorStates().get(id);
        var updated = updatedStates().errorStates().get(ErrorId.of(id.itemId(), id.code()));
        if (updated != original) {
          itemVisitor.visitUpdatedErrorState(original, updated);
        }
      }
      itemVisitor.end();
    });

    visitor.visitUpdatedValueSets().ifPresent(itemVisitor -> {
      for (final var id : this.updatedValueSetIds) {
        var original = originalStates().valueSetStates().get(id);
        var updated = updatedStates().valueSetStates().get(id);
        if (updated != original) {
          itemVisitor.visitUpdatedValueSet(original, updated);
        }
      }
      itemVisitor.end();
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


  public interface UpdatedItemsVisitor {

    @FunctionalInterface
    interface UpdatedSessionStateVisitor {
      void visitLanguageChange(@Nullable String original, @NonNull String updated);
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


  public abstract static class AbstractDelegateUpdatedItemsVisitor implements UpdatedItemsVisitor {

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
