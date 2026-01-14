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
import io.dialob.session.engine.session.protobuf.StateReader;
import io.dialob.session.engine.session.protobuf.StateWriter;
import org.immutables.value.Value;

import java.io.IOException;
import java.io.Serial;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Value.Builder
@Value.Style(
  jakarta = true,
  jdkOnly = true,
  jdk9Collections = true,
  overshadowImplementation = true,
  visibility = Value.Style.ImplementationVisibility.PACKAGE
)
public record ItemStates(
  @NonNull
  Map<ItemId, ItemState> itemStates,

  @NonNull
  Map<ValueSetId, ValueSetState> valueSetStates,

  @NonNull
  Map<ErrorId, ErrorState> errorStates
) implements Serializable {

  @Serial
  private static final long serialVersionUID = 7344194323565473252L;

  public static final class Builder extends ItemStatesBuilder {
  }

  public static final ItemStates EMPTY = new ItemStates(Map.of(), Map.of(), Map.of());


  public void writeTo(StateWriter output) throws IOException {
    output.writeInt(itemStates.size());
    for (var state : itemStates.values()) {
      state.writeTo(output);
    }
    output.writeInt(valueSetStates.size());
    for (var state : valueSetStates.values()) {
      state.writeTo(output);
    }
    output.writeInt(errorStates.size());
    for (var state : errorStates.values()) {
      state.writeTo(output);
    }
  }

  public static ItemStates readFrom(StateReader input) throws IOException {
    var itemStates = new HashMap<ItemId,ItemState>();
    var valueSetStates = new HashMap<ValueSetId,ValueSetState>();
    var errorStates = new HashMap<ErrorId,ErrorState>();

    int count = input.readInt();
    for (int i = 0; i < count; ++i) {
      final var state = ItemState.readFrom(input);
      itemStates.put(state.id(), state);
    }
    count = input.readInt();
    for (int i = 0; i < count; ++i) {
      final var state = ValueSetState.readFrom(input);
      valueSetStates.put(state.id(), state);
    }
    count = input.readInt();
    for (int i = 0; i < count; ++i) {
      final var state = ErrorState.readFrom(input);
      errorStates.put(state.id(), state);
    }
    return new ItemStates(
        itemStates,
        valueSetStates,
        errorStates
    );
  }
}
