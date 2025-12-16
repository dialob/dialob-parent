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

import com.google.protobuf.CodedInputStream;
import com.google.protobuf.CodedOutputStream;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import io.dialob.common.Constants;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
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
      } else {
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

  public static void writeIdTo(@Nullable ItemId id, CodedOutputStream output) throws IOException {
    if (id == null) {
      output.writeBoolNoTag(false);
      return;
    }
    output.writeBoolNoTag(true);
    switch (id) {
      case ItemRef itemRef -> {
        output.write((byte) 1);
        output.writeStringNoTag(itemRef.getValue());
      }
      case ItemIdPartial ignored -> output.write((byte) 2);
      case ItemIndex itemRef -> {
        output.write((byte) 3);
        output.writeInt32NoTag(itemRef.getIndex());
      }
      default -> throw new RuntimeException("unknown id type " + id);
    }
    writeIdTo(id.getParent().orElse(null), output);
  }

  @Nullable
  public static ItemId readIdFrom(CodedInputStream input) throws IOException {
    if (input.readBool()) {
      var type = input.readRawByte();
      return switch (type) {
        case 1 -> new ItemRef(input.readString(), readIdFrom(input));
        case 2 -> new ItemIdPartial(readIdFrom(input));
        case 3 -> new ItemIndex(input.readInt32(), readIdFrom(input));
        default -> throw new RuntimeException("unknown id type " + type);
      };
    }
    return null;
  }
}
