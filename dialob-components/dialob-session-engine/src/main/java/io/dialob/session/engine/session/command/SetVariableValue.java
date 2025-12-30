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
package io.dialob.session.engine.session.command;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import io.dialob.session.engine.program.EvalContext;
import io.dialob.session.engine.session.model.ItemId;
import io.dialob.session.engine.session.model.ItemState;

record SetVariableValue(
  ItemId targetId,
  Object value,
  java.util.List<Trigger<ItemState>> triggers
) implements AbstractUpdateCommand<ItemId, ItemState>, ItemUpdateCommand {

  @NonNull
  @Override
  public UpdateCommand<ItemId, ItemState> withTargetId(@NonNull ItemId targetId) {
    return new SetVariableValue(targetId, value, triggers);
  }

  @Nullable
  public Object getValue() {
    return value;
  }

  @NonNull
  public ItemState update(@NonNull EvalContext context, @NonNull ItemState itemState) {
    // TODO validate matching type??
    return itemState.update()
      .setValue(getValue())
      .setStatus(ItemState.Status.OK).get();
  }
}
