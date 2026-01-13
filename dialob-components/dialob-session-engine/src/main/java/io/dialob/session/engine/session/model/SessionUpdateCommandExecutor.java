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

import com.google.common.collect.Maps;
import edu.umd.cs.findbugs.annotations.NonNull;
import io.dialob.session.engine.program.EvalContext;
import io.dialob.session.engine.session.command.SessionUpdateCommand;

public class SessionUpdateCommandExecutor implements CommandExecutor<ItemStates, SessionUpdateCommand> {

  @Override
  public ItemStates applyCommand(@NonNull EvalContext context, @NonNull SessionUpdateCommand command) {
    var itemStates = context.mutableItemStates().toItemStates();

    final var newStates = command.update(context, itemStates);

    triggerEvents(context, command, itemStates, newStates);

    var errorDiffs = Maps.difference(newStates.errorStates(), itemStates.errorStates());
    var itemStatesDiffs = Maps.difference(newStates.itemStates(), itemStates.itemStates());

    // Removed
    itemStatesDiffs.entriesOnlyOnRight().forEach((itemId, itemState) -> {
      context.registerUpdate(null, itemState);
      context.mutableItemStates().itemStates().remove(itemId);
    });
    errorDiffs.entriesOnlyOnRight().forEach(((errorId, errorState) -> {
      context.registerUpdate(null, errorState);
      context.mutableItemStates().errorStates().remove(errorId);
    }));
    // New ones
    itemStatesDiffs.entriesOnlyOnLeft().forEach((itemId, itemState) -> {
      context.registerUpdate(itemState, null);
      context.mutableItemStates().itemStates().put(itemId, itemState);
    });
    errorDiffs.entriesOnlyOnLeft().forEach(((errorId, errorState) -> {
      context.registerUpdate(errorState, null);
      context.mutableItemStates().errorStates().put(errorId, errorState);
    }));
    // Updated
    itemStatesDiffs.entriesDiffering().forEach((itemId, itemStateDiff) -> {
      context.registerUpdate(itemStateDiff.leftValue(), itemStateDiff.rightValue());
      context.mutableItemStates().itemStates().put(itemId, itemStateDiff.leftValue());
    });
    errorDiffs.entriesDiffering().forEach(((errorId, errorState) -> {
      context.registerUpdate(errorState.leftValue(), errorState.rightValue());
      context.mutableItemStates().errorStates().put(errorId, errorState.leftValue());
    }));
    return context.mutableItemStates().toItemStates();
  }

  protected void triggerEvents(EvalContext context, SessionUpdateCommand command, ItemStates state, ItemStates updatedState) {
    command.triggers().stream()
      .flatMap(trigger -> trigger.apply(state, updatedState))
      .forEach(event -> context.getEventsConsumer().accept(event));
  }

}
