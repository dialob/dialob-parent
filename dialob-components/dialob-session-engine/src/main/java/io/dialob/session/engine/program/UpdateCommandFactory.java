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
import io.dialob.session.engine.DebugUtil;
import io.dialob.session.engine.program.model.Expression;
import io.dialob.session.engine.program.model.Value;
import io.dialob.session.engine.program.model.ValueSet;
import io.dialob.session.engine.session.command.*;
import io.dialob.session.engine.session.model.ErrorId;
import io.dialob.session.engine.session.model.ItemId;
import io.dialob.session.engine.session.model.ValueSetId;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static io.dialob.session.engine.session.command.CommandFactory.*;

@Slf4j
class UpdateCommandFactory {

  private final Set<Command<?>> allCommands = new HashSet<>();
  private final Set<ErrorId> targetIds = new HashSet<>();

  private <C extends Command<?>> C add(C command) {
    assert !allCommands.contains(command);
    allCommands.add(command);
    if(LOGGER.isDebugEnabled()) {
      LOGGER.debug("Command '{}' created", DebugUtil.commandToString(command));
    }
    return command;
  }

  public Set<Command<?>> getAllCommands() {
    return Collections.unmodifiableSet(allCommands);
  }

  public ItemUpdateCommand createUpdateVariable(@NonNull ItemId id, @NonNull Expression expression) {
    return add(variableUpdateCommand(id, expression));
  }

  public ItemUpdateCommand createUpdateClass(@NonNull ItemId id, @NonNull Expression expression) {
    return add(updateClassNames(id, expression));
  }

  public ItemUpdateCommand createUpdateLabel(@NonNull ItemId id, @NonNull Expression expression) {
    return add(labelUpdate(id, expression));
  }

  public ItemUpdateCommand createUpdateDescription(@NonNull ItemId id, @NonNull Expression expression) {
    return add(descriptionUpdate(id, expression));
  }

  public ItemUpdateCommand createUpdateDisabled(@NonNull ItemId id, @NonNull Expression expression) {
    return add(updateDisabled(id, expression));
  }

  public ItemUpdateCommand createUpdateActivity(@NonNull ItemId id, @NonNull Expression expression) {
    return add(activityUpdate(id, expression));
  }

  public ItemUpdateCommand createUpdateRowsCanBeAddedCommand(@NonNull ItemId id, @NonNull Expression expression) {
    return add(rowsCanBeAddedUpdate(id, expression));
  }

  public ItemUpdateCommand createUpdateRowCanBeRemovedCommand(@NonNull ItemId id, @NonNull Expression expression) {
    return add(rowCanBeRemovedUpdate(id, expression));
  }

  public ItemUpdateCommand createUpdateRequired(@NonNull ItemId id, @NonNull Expression expression) {
    return add(requiredUpdate(id, expression));
  }

  public ItemUpdateCommand createUpdateAllowedActions(@NonNull ItemId id, @NonNull Expression expression) {
    return add(allowedActionsUpdate(id, expression));
  }

  public ItemUpdateCommand createUpdateIsInvalidAnswersCommand(@NonNull ItemId id, @NonNull Expression expression) {
    return add(updateIsInvalidAnswers(id, expression));
  }

  public ItemUpdateCommand createUpdateAvailableItems(@NonNull ItemId id, @NonNull Expression expression) {
    return add(availableItemsUpdate(id, expression));
  }

  public ItemUpdateCommand createUpdateGroupItems(@NonNull ItemId groupId, @NonNull Expression expression) {
    return add(updateGroupItems(groupId, expression));
  }

  public ErrorUpdateCommand createUpdateValidationDisabled(@NonNull ErrorId targetId, @NonNull Expression expression) {
    return add(validationDisabledUpdate(targetId, expression));
  }

  public ErrorUpdateCommand createUpdateValidationCommand(@NonNull ErrorId targetId, @NonNull Expression expression) {
    assert !targetIds.contains(targetId);
    targetIds.add(targetId);
    return add(updateValidationCommand(targetId, expression));
  }

  public ErrorUpdateCommand createErrorLabelUpdateCommand(@NonNull ErrorId targetId, @NonNull Expression expression) {
    return add(errorLabelUpdateCommand(targetId, expression));
  }

  public SessionUpdateCommand createRowGroupFromPrototype(ItemId rowGroupPrototypeId) {
    return add(createRowGroupFromPrototypeCommand(rowGroupPrototypeId));
  }

  public SessionUpdateCommand createRowGroupItemsFromPrototype(ItemId rowGroupPrototypeId, List<ItemId> itemIds) {
    return add(createRowGroupItemsFromPrototypeCommand(rowGroupPrototypeId, itemIds));
  }

  public ItemUpdateCommand initRowGroupItems(ItemId groupId) {
    return add(initRowGroupItemsCommand(groupId));
  }

  public ValueSetUpdateCommand createUpdateValueSetCommand(ValueSetId valueSetId, List<Value<ValueSet.Entry>> entries) {
    return add(updateValueSet(valueSetId, entries));
  }

}
