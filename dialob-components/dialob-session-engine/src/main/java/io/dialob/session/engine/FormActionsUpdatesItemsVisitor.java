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
package io.dialob.session.engine;

import edu.umd.cs.findbugs.annotations.NonNull;
import io.dialob.api.proto.ActionItem;
import io.dialob.api.questionnaire.ImmutableError;
import io.dialob.questionnaire.service.api.FormActions;
import io.dialob.session.engine.session.model.*;

import java.util.Locale;
import java.util.function.Function;
import java.util.function.Predicate;

public class FormActionsUpdatesItemsVisitor extends AbstractFormActionsUpdatesItemsVisitor {

  private final Function<ItemState, ActionItem> toActionItemFunction;

  public FormActionsUpdatesItemsVisitor(@NonNull FormActions formActions,
                                        @NonNull Predicate<SessionObject> isVisiblePredicate,
                                        @NonNull Function<ItemState, ActionItem> toActionItemFunction) {
    super(formActions, isVisiblePredicate);
    this.toActionItemFunction = toActionItemFunction;
  }

  protected void updated(@NonNull ErrorState updated) {
    // triggered when error label is updated
    formActions.addError(Utils.toError(updated));
  }

  @Override
  protected void updated(@NonNull ValueSetState updated) {
    formActions.newValueSet(Utils.toValueSet(updated));
  }

  protected void activated(@NonNull ErrorState updated) {
    formActions.addError(Utils.toError(updated));
  }

  protected void inactivated(@NonNull ErrorState updated) {
    formActions.removeError(ImmutableError.builder().id(IdUtils.toString(updated.getItemId())).code(updated.getCode()).build());
  }

  protected void disabled(@NonNull ItemState updated) {
    updated(updated);
  }

  protected void enabled(@NonNull ItemState updated) {
    updated(updated);
  }

  protected void activated(@NonNull ItemState updated) {
    formActions.newQuestion(toActionItemFunction.apply(updated));
  }

  protected void inactivated(@NonNull ItemState updated) {
    formActions.removeQuestion(IdUtils.toString(updated.getId()));
  }

  protected void updated(@NonNull ItemState updated) {
    formActions.updateQuestion(toActionItemFunction.apply(updated));
  }

  protected void languageChanged(@NonNull String language) {
    formActions.locale(new Locale(language));
  }

  @Override
  public void visitCompleted() {
    formActions.complete();
  }


}
