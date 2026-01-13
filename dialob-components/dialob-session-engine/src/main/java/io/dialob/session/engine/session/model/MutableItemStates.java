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
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@EqualsAndHashCode
public class MutableItemStates implements Serializable {

  @Serial
  private static final long serialVersionUID = 3278516873252L;

  @NonNull
  private final Map<ItemId, ItemState> itemState;

  @NonNull
  private final Map<ValueSetId, ValueSetState> valueSetStates;

  @NonNull
  private final Map<ErrorId, ErrorState> errorStates;

  public MutableItemStates(@NonNull Map<ItemId, ItemState> itemState, @NonNull Map<ValueSetId, ValueSetState> valueSetStates, @NonNull Map<ErrorId, ErrorState> errorStates) {
    this.itemState = new HashMap<>(itemState);
    this.valueSetStates = new HashMap<>(valueSetStates);
    this.errorStates = new HashMap<>(errorStates);
  }
  public  MutableItemStates(ItemStates itemStates) {
    this(itemStates.itemStates(), itemStates.valueSetStates(), itemStates.errorStates());
  }

  public ItemStates toItemStates() {
    return new ItemStates(
      Map.copyOf(itemState),
      Map.copyOf(valueSetStates),
      Map.copyOf(errorStates)
    );
  }

  @NonNull
  public Map<ItemId, ItemState> itemStates() {
    return itemState;
  }

  @NonNull
  public Map<ValueSetId, ValueSetState> valueSetStates() {
    return valueSetStates;
  }

  @NonNull
  public Map<ErrorId, ErrorState> errorStates() {
    return errorStates;
  }
}
