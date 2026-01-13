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
import io.dialob.session.engine.program.EvalContext;
import io.dialob.session.engine.session.command.UpdateCommand;

import java.util.Map;

abstract class AbstractCommandExecutor<I extends ItemId, T, C extends UpdateCommand<I, T>> implements CommandExecutor<T, C> {

  protected void triggerEvents(EvalContext context, C command, T state, T updatedState) {
    command.triggers().stream()
      .flatMap(trigger -> trigger.apply(state, updatedState))
      .forEach(event -> context.getEventsConsumer().accept(event));
  }

  @Override
  public void applyCommand(@NonNull EvalContext context, @NonNull C command) {
    mutableItems(context).computeIfPresent(command.targetId(), (key, state) -> {
      var updatedState = command.update(context, state);
      // TODO SetLocale does not update item's state. Locale attribute should be moved to item state.
//      if (state != updatedState) {
        triggerEvents(context, command, state, updatedState);
        registerUpdate(context, command, state, updatedState);
//      }
      return updatedState;
    });
  }

  protected abstract Map<I,T> mutableItems(@NonNull EvalContext context);

  protected abstract void registerUpdate(EvalContext context, C command, T state, T updatedState);


}
