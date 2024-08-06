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
package io.dialob.session.model;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import io.dialob.common.Constants;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

public class IdUtils {

  public static final ItemId QUESTIONNAIRE_ID = ImmutableItemRef.of(Constants.QUESTIONNAIRE, Optional.empty());

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
    List<String> idChain = new ArrayList<>();
    Optional<ItemId> id = Optional.of(itemId);
    while (id.isPresent()) {
      itemId = id.get();
      if (itemId instanceof ErrorId) {
        return toString(((ErrorId) itemId).getItemId()) + ":" + ((ErrorId) itemId).getCode();
      } else if (itemId instanceof ItemRef) {
        idChain.add(((ItemRef) itemId).getId());
      } else if (itemId instanceof ItemIndex) {
        idChain.add(((ItemIndex) itemId).getIndex().toString());
      } else if (itemId instanceof ItemIdPartial) {
        idChain.add("*");
      }
      id = itemId.getParent();
    }
    Collections.reverse(idChain);
    return StringUtils.join(idChain, ".");
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
    for (String s : strings) {
      if (StringUtils.isNumeric(s)) {
        id = ImmutableItemIndex.of(Integer.parseInt(s), Optional.ofNullable(id));
      } else if ("*".equals(s)) {
        id = ImmutableItemIdPartial.of(Optional.ofNullable(id));
      } else {
        id = ImmutableItemRef.of(s, Optional.ofNullable(id));
      }
    }
    if (id == null) {
      throw new IllegalArgumentException(itemId + " is not valid id");
    }
    return id;
  }

  public static ItemId withIndex(ItemId itemId, int index) {
    // TODO Check
    return itemId.withParent(ImmutableItemIndex.of(index, itemId.getParent().flatMap(ItemId::getParent)));
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
