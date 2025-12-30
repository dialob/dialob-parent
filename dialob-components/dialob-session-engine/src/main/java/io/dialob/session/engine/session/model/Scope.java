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

import java.io.Serializable;
import java.util.Collection;
import java.util.Set;

public record Scope(
  @NonNull
  ItemId scopeId,

  Set<ItemId> scopeItems

) implements Serializable {

  public static Scope of(@NonNull ItemId scopeId, Collection<ItemId> scopeItems) {
    return new Scope(scopeId, Set.copyOf(scopeItems));
  }

  /**
   * Maps item id to current evaluation scope. This is used to map plain row group item to current row.
   *
   * @param itemId plain item id
   * @param ignoreScopeItems if mapped item id is not found from scope,
   * @return item id from this scope
   */
  public ItemId mapTo(final ItemId itemId, final boolean ignoreScopeItems) {
    return this.scopeId().getParent().map(scopeParent -> {
      ItemId scopedId = itemId;
      if (itemId.getParent().isEmpty()){
        String id = itemId.getValue();
        scopedId = new ItemRef(id, this.scopeId());
      } else {
        if (IdUtils.matches(itemId, this.scopeId())) {
          scopedId = this.scopeId();
        } else if (itemId.isPartial() && itemId.getParent().isPresent()) {
          ItemId parentId = itemId.getParent().get();
          if (IdUtils.matches(parentId, this.scopeId())) {
            scopedId = itemId.withParent(this.scopeId());
          }
        }
      }
      if (!ignoreScopeItems && !scopeItems().contains(scopedId)) {
        scopedId = itemId;
      }
      return scopedId;
    }).orElse(itemId);
  }

}
