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
package io.dialob.session.engine.session.command;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import io.dialob.session.engine.Utils;
import io.dialob.session.engine.program.EvalContext;
import io.dialob.session.engine.session.model.ItemState;
import io.dialob.session.model.ItemId;
import org.immutables.value.Value;

@Value.Immutable
public interface SetAnswer extends AbstractUpdateCommand<ItemId, ItemState>, ItemUpdateCommand {

  @Value.Parameter(order = 1)
  @Nullable
  Object getAnswer();

  @NonNull
  default ItemState update(@NonNull EvalContext context, @NonNull ItemState itemState) {
    if (canUpdate(context, itemState)) {
      Object answer = getAnswer();
      return itemState.update()
        .setAnswer(answer)
        .setValue(Utils.parse(itemState.getType(), answer)).get();
    }
    return itemState;
  }

  default boolean canUpdate(@NonNull EvalContext context, @NonNull ItemState itemState) {
    return ( context.isActivating() || !itemState.isDisabled() && itemState.isActive() ) && Utils.isQuestionType(itemState);
  }

}
