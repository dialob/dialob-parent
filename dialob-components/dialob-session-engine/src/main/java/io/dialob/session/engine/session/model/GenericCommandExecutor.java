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
import io.dialob.session.engine.DebugUtil;
import io.dialob.session.engine.program.EvalContext;
import io.dialob.session.engine.session.command.*;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Collections;

@Slf4j
public record GenericCommandExecutor(
  DialobSession session
) implements CommandExecutor {

  /**
   * @param evalContext execution context
   * @param command object to execute within context
   */
  public DialobSession applyCommand(@NonNull EvalContext evalContext, @NonNull Command command) {
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
    return session()
      .updateItemStatesTo(evalContext.mutableItemStates().toItemStates());
  }

  private EvalContext createScopedEvalContext(@NonNull EvalContext evalContext, ItemId itemId) {
    return itemId instanceof ItemIndex ?
      createScope(evalContext, itemId) :
      itemId.getParent().map(parentId -> {
        if (parentId instanceof ItemIndex) {
          return createScope(evalContext, parentId);
        }
        return evalContext;
      }).orElse(evalContext);
  }

  private EvalContext createScope(@NonNull EvalContext evalContext, ItemId itemId) {
    var scopeItems = new ArrayList<>(evalContext
      .getItemState(itemId)
      .map(ItemState::items)
      .orElseGet(Collections::emptyList));
    scopeItems.add(itemId);
    return evalContext.withScope(Scope.of(itemId, scopeItems));
  }
}
