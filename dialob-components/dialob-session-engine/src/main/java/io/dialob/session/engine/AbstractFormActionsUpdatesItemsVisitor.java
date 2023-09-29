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
package io.dialob.session.engine;

import edu.umd.cs.findbugs.annotations.NonNull;
import io.dialob.questionnaire.service.api.FormActions;
import io.dialob.session.engine.program.EvalContext;
import io.dialob.session.engine.session.model.ErrorState;
import io.dialob.session.engine.session.model.ItemState;
import io.dialob.session.engine.session.model.SessionObject;
import io.dialob.session.engine.session.model.ValueSetState;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

public abstract class AbstractFormActionsUpdatesItemsVisitor extends AbstractFormActionsVisitor implements EvalContext.UpdatedItemsVisitor {

  private final Predicate<SessionObject> isVisiblePredicate;

  AbstractFormActionsUpdatesItemsVisitor(@NonNull FormActions formActions, @NonNull Predicate<SessionObject> isVisiblePredicate) {
    super(formActions);
    this.isVisiblePredicate = isVisiblePredicate;
  }

  @Override
  public Optional<UpdatedSessionStateVisitor> visitSession() {
    return Optional.of((original, updated) -> languageChanged(updated));
  }

  @Override
  public Optional<UpdatedErrorStateVisitor> visitUpdatedErrorStates() {
    return Optional.of((original, updated) -> {
      final boolean updatedVisible = updated != null && isVisiblePredicate.test(updated);
      final boolean originalVisible = original != null && isVisiblePredicate.test(original);
      if (originalVisible && !updatedVisible) {
        inactivated(original);
        return;
      }
      if (!originalVisible && updatedVisible) {
        activated(updated);
        return;
      }
      if (updatedVisible) {
        updated(updated);
      }
    });
  }

  @Override
  public Optional<UpdatedItemStateVisitor> visitUpdatedItems() {
    return Optional.of((original, updated) -> {
      final boolean updatedVisible = updated != null && isVisiblePredicate.test(updated);
      final boolean originalVisible = original != null && isVisiblePredicate.test(original);
      if (!originalVisible && !updatedVisible) {
        return;
      }
      if (originalVisible && !updatedVisible) {
        inactivated(updated != null ? updated : original);
        return;
      }
      if (!originalVisible) {
        activated(updated);
        return;
      }
      // here updatedVisible == true and originalVisible == true, so need to check change of disabled
      if (original.isDisabled() && !updated.isDisabled()) {
        enabled(updated);
        return;
      }
      if (!original.isDisabled() && (updated.isDisabled())) {
        disabled(updated);
        return;
      }
      if (!Objects.equals(original, updated)) {
        updated(updated);
      }
    });
  }

  @Override
  public Optional<UpdatedValueSetVisitor> visitUpdatedValueSets() {
    return Optional.of((original, updated) -> {
      if (updated != null) {
        updated(updated);
      }
    });
  }

  protected abstract void updated(@NonNull ErrorState updated);

  protected abstract void updated(@NonNull ValueSetState updated);

  protected abstract void activated(@NonNull ErrorState updated);

  protected abstract void inactivated(@NonNull ErrorState updated);

  protected abstract void disabled(@NonNull ItemState updated);

  protected abstract void enabled(@NonNull ItemState updated);

  protected abstract void activated(@NonNull ItemState updated);

  protected abstract void inactivated(@NonNull ItemState updated);

  protected abstract void updated(@NonNull ItemState updated);

  protected abstract void languageChanged(@NonNull String language);

}
