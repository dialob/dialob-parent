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
import io.dialob.session.engine.program.EvalContext;
import io.dialob.session.engine.session.model.ItemId;
import io.dialob.session.engine.session.model.ItemState;

import java.util.List;

record PrevPage(
  List<Trigger<ItemState>> triggers
) implements AbstractPageCommand {

  @NonNull
  @Override
  public ItemState update(@NonNull EvalContext context, @NonNull ItemState itemState) {
    List<ItemId> items = itemState.items();
    return itemState.activePageOptional().map(itemRef -> {
      ItemId page = null;
      int i = items.indexOf(itemRef);
      while (i > 0 && i < items.size() && pageIsInactive(context, page)) {
        page = items.get(--i);
      }
      return gotoPage(context, itemState, page);
    }).orElse(itemState);
  }


  @NonNull
  @Override
  public UpdateCommand<ItemId, ItemState> withTargetId(@NonNull ItemId targetId) {
    return this;
  }
}
