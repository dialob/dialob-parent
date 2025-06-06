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
import io.dialob.api.proto.Action;
import io.dialob.session.engine.program.EvalContext;
import io.dialob.session.engine.session.model.IdUtils;
import io.dialob.session.engine.session.model.ItemId;
import io.dialob.session.engine.session.model.ItemState;
import org.immutables.value.Value;

import java.util.List;

@Value.Immutable
public interface NextPage extends AbstractPageCommand {

  @Override
  @NonNull
  default ItemState update(@NonNull EvalContext context, @NonNull ItemState itemState) {
    ItemId page = null;
    if (!context.getItemState(IdUtils.QUESTIONNAIRE_ID).map(questionnaire -> questionnaire.getAllowedActions().contains(Action.Type.NEXT)).orElse(false)) {
      return itemState;
    }
    List<ItemId> items = itemState.getItems();
    if (!items.isEmpty()) {
      int i = itemState.getActivePage().map(items::indexOf).orElse(-1);
      while (i >= -1 && i < items.size() - 1 && page == null) {
        page = items.get(++i);
        if (pageIsInactive(context, page)) {
          page = null;
        }
      }
    }
    return gotoPage(context, itemState, page);
  }

}
