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
import edu.umd.cs.findbugs.annotations.Nullable;
import io.dialob.common.Constants;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;
import java.util.Optional;

public class IdUtils {

  public static final ItemId QUESTIONNAIRE_ID = new ItemRef(Constants.QUESTIONNAIRE, null);

  public static String toString(ValueSetId valueSetId) {
    if (valueSetId == null) {
      return null;
    }
    return valueSetId.getValueSetId();
  }

  public static String toString(ItemId itemId) {
    if (itemId == null) {
      return null;
    }
    var idChain = new ArrayList<String>();
    while (itemId != null) {
      switch (itemId) {
        case ErrorId(ItemId itemId1, String code) -> {
          return toString(itemId1) + ":" + code;
        }
        case ItemRef(String ref, ItemId ignored2) -> idChain.add(ref);
        case ItemIndex(Integer index, ItemId ignored1) -> idChain.add(index.toString());
        case ItemIdPartial ignored -> idChain.add("*");
        default -> {
          // should not happen
          throw  new IllegalStateException("Unknown item id " + itemId);
        }
      }
      itemId = itemId.parent();
    }
    Collections.reverse(idChain);
    return StringUtils.join(idChain,".");
  }

  @Nullable
  public static ItemId toIdNullable(@Nullable String itemId) {
    if (itemId == null) {
      return null;
    }
    return toId(itemId);
  }



  @NonNull
  public static ItemId toId(@Nullable String scopeId, @NonNull String itemId) {
    if (StringUtils.isNotBlank(scopeId)) {
      return toId(scopeId + "." + itemId);
    }
    return toId(itemId);
  }

  @NonNull
  public static ItemId toId(@NonNull String itemId) {
    if (Constants.QUESTIONNAIRE.equals(itemId)) {
      return QUESTIONNAIRE_ID;
    }
    String[] strings = itemId.split("\\.");
    ItemId id = null;
    for (var s : strings) {
      if (StringUtils.isNumeric(s)) {
        id = new ItemIndex(Integer.parseInt(s), id);
      } else if ("*".equals(s)) {
        id = new ItemIdPartial(id);
      } else if (!s.isEmpty()) {
        id = new ItemRef(s, id);
      }
    }
    if (id == null) {
      throw new IllegalArgumentException(itemId + " is not valid id");
    }
    return id;
  }

  public static ItemId withIndex(ItemId itemId, int index) {
    // TODO Check
    return itemId.withParent(new ItemIndex(index, itemId.getParent().flatMap(ItemId::getParent).orElse(null)));
  }

  public static boolean matches(@NonNull Optional<ItemId> itemIdLh, @NonNull Optional<ItemId> itemIdRh) {
    if (itemIdLh.isPresent() == itemIdRh.isPresent()) {
      return itemIdLh.map(itemId -> matches(itemId, itemIdRh.get())).orElse(true);
    }
    return false;
  }


  public static boolean matches(@NonNull ItemId itemIdLh, @NonNull ItemId itemIdRh) {
    if (itemIdLh.equals(itemIdRh)) {
      return true;
    }
    if (Objects.equals(itemIdLh.getValue(), itemIdRh.getValue())
      || itemIdLh instanceof ItemIdPartial || itemIdRh instanceof ItemIdPartial) {
      return matches(itemIdLh.getParent(), itemIdRh.getParent());
    }
    return false;
  }

}
