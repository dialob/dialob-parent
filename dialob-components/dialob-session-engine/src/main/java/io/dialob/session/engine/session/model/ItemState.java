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
import io.dialob.api.proto.Action;
import io.dialob.session.engine.Utils;
import io.dialob.session.engine.session.protobuf.StateReader;
import io.dialob.session.engine.session.protobuf.StateWriter;
import lombok.With;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.Serial;
import java.util.*;

@lombok.Builder(toBuilder = true)
public record ItemState(
  @With @NonNull ItemId id,
  ItemId prototypeId,
  @NonNull String type,
  String view,
  String valueSetId,
  @NonNull Status status,
  Object answer,
  Object value,
  Object defaultValue,
  int bits,
  String label,
  String description,
  List<String> classNames,
  List<ItemId> items,
  List<ItemId> availableItems,
  Map<String, Object> props,
  Set<Action.Type> allowedActions,
  ItemId activePage
) implements SessionObject<ItemId> {

  public ItemState {
    Objects.requireNonNull(id, "id is null");
    Objects.requireNonNull(type, "type is null");
    Objects.requireNonNull(status, "status is null");

    classNames = List.copyOf(Objects.requireNonNullElseGet(classNames, List::of));
    items = List.copyOf(Objects.requireNonNullElseGet(items, List::of));
    availableItems = List.copyOf(Objects.requireNonNullElseGet(availableItems, List::of));
    props = Map.copyOf(Objects.requireNonNullElseGet(props, Map::of));
    allowedActions = Set.copyOf(Objects.requireNonNullElseGet(allowedActions, Set::of));
  }

  @Serial
  private static final long serialVersionUID = -3974128908954128671L;


  public enum Status {
    /**
     * Item state instance is just created, but have not been evaluated
     */
    NEW,

    /**
     * Normal state
     */
    OK,

    /**
     * Update failed
     */
    ERROR,

    /**
     * expecting value from asynchronous evaluation
     */
    PENDING
  }

  public static final int DISPLAY_ITEM_BIT = 1;
  public static final int ACTIVE_BIT = 1 << 1;
  public static final int DISABLED_BIT = 1 << 2;
  public static final int REQUIRED_BIT = 1 << 3;
  public static final int ROWS_CAN_BE_ADDED_BIT = 1 << 4;
  public static final int ROW_CAN_BE_REMOVED_BIT = 1 << 5;
  public static final int INVALID_ANSWERS_BIT = 1 << 6;
  public static final int HAS_CUSTOM_PROPS_BIT = 1 << 7;
  public static final int READ_ONLY_BIT = 1 << 8;

  private boolean testBit(int bit) {
    return (bits & bit) != 0;
  }

  public static ItemState readFrom(StateReader input) throws IOException {
    final ItemId id = input.readNullableId();
    final ItemId prototypeId = input.readNullableId();
    final String type = input.readString();
    final String view = input.readNullableString();
    final String valueSetId = input.readNullableString();

    var activePage = input.readNullableId();
    var status = Status.values()[input.readRawByte()];
    var bits = input.readInt();
    var label = input.readNullableString();
    var description = input.readNullableString();

    var answer = input.readNullableObjectValue();
    var value = input.readNullableValue();
    var defaultValue = input.readNullableValue();

    var classNames = input.readStringList();
    var items = input.readIdList();
    var availableItems = input.readIdList();

    Map<String, Object> props = Map.of();

    var allowedActions = Set.<Action.Type>of();
    int count = input.readInt();
    if ( count >  0) {
      Action.Type[] types = new Action.Type[count];
      for (int i = 0; i < count; i++ ){
        types[i] = Action.Type.values()[input.readInt()];
      }
      allowedActions = Set.of(types);
    } else {
      allowedActions = EnumSet.noneOf(Action.Type.class);
    }
    return new ItemState(
      id,
      prototypeId,
      type,
      view,
      valueSetId,
      status,
      answer,
      value,
      defaultValue,
      bits,
      label,
      description,
      classNames,
      items,
      availableItems,
      props,
      allowedActions,
      activePage);
  }


  @Override
  public void writeTo(StateWriter output) throws IOException {
    output.writeNullableId(id);
    output.writeNullableId(prototypeId);
    output.writeString(type);
    output.writeNullableString(view);
    output.writeNullableString(valueSetId);

    output.writeNullableId(activePage);
    output.writeRawByte(status.ordinal());
    output.writeInt(bits);
    output.writeNullableString(label);
    output.writeNullableString(description);

    output.writeNullableObjectValue(answer);
    output.writeNullableValue(Utils.mapQuestionTypeToValueType(type).orElse(null), value);
    output.writeNullableValue(Utils.mapQuestionTypeToValueType(type).orElse(null), defaultValue);

    output.writeStringList(classNames);
    output.writeIdList(items);
    output.writeIdList(availableItems);

    output.writeInt(allowedActions.size());
    for (Action.Type actionType: allowedActions) {
      output.writeInt(actionType.ordinal());
    }
  }


  // Used only in tests

  // Used only in tests

  @Override
  public boolean isDisplayItem() {
    return (bits & DISPLAY_ITEM_BIT) != 0;
  }

  public Optional<String> valueSetIdOptional() {
    return Optional.ofNullable(valueSetId);
  }

  public Object getValue() {
    return isActive() && value != null ? value : defaultValue;
  }

  @Override
  public boolean isActive() {
    return testBit(ACTIVE_BIT);
  }

  public boolean isAnswered() {
    return !isNull() && !isBlank();
  }

  public boolean isBlank() {
    return isNull() || value instanceof CharSequence cs && StringUtils.isBlank(cs);
  }

  public boolean isNull() {
    return value == null;
  }

  public boolean isInvalidAnswers() {
    return testBit(INVALID_ANSWERS_BIT);
  }

  public boolean isInvalid() {
    if (answer instanceof String) {
      return value == null && StringUtils.isNotEmpty((CharSequence) answer);
    }
    return value == null && answer != null;
  }

  @Override
  public boolean isDisabled() {
    return (bits & DISABLED_BIT) != 0;
  }

  public boolean isRequired() {
    return (bits & REQUIRED_BIT) != 0;
  }

  public boolean isReadOnly() {
    return (bits & READ_ONLY_BIT) != 0;
  }

  public boolean isRowsCanBeAdded() {
    return (bits & ROWS_CAN_BE_ADDED_BIT) != 0;
  }

  public boolean isRowCanBeRemoved() {
    return (bits & ROW_CAN_BE_REMOVED_BIT) != 0;
  }

  public boolean hasCustomProps() {
    return (bits & HAS_CUSTOM_PROPS_BIT) != 0;
  }

  public Optional<ItemId> activePageOptional() {
    return Optional.ofNullable(activePage);
  }

  public class UpdateBuilder {

    private ItemState.ItemStateBuilder builder;

    int bits;

    private void updateBit(boolean toValue, int bit) {
      if (toValue) {
        setBit(bit);
      } else {
        resetBits(bit);
      }
    }
    private void setBit(int bit) {
      bits = bits | bit;
    }

    private void resetBits(int bit) {
      bits = bits & (~bit);
    }

    UpdateBuilder() {
      this.bits = ItemState.this.bits();
    }

    private ItemState.ItemStateBuilder state() {
      if (builder == null) {
        this.builder = toBuilder();
      }
      return builder;
    }

    private boolean hasNewState() {
      return this.builder != null;
    }

    private UpdateBuilder updateBits(boolean toValue, int bit) {
      updateBit(toValue, bit);
      return this;
    }

    public UpdateBuilder setStatus(Status newStatus) {
      if (status != newStatus) {
        state().status(newStatus);
      }
      return this;
    }

    public UpdateBuilder setAnswer(Object newAnswer) {
      if (!Objects.equals(answer, newAnswer)) {
        state().answer(newAnswer);
      }
      return this;
    }

    public UpdateBuilder setValue(Object newValue) {
      if (!Objects.equals(value, newValue)) {
        state().value(newValue);
      }
      return this;
    }

    public UpdateBuilder setActive(boolean newActive) {
      return updateBits(newActive, ACTIVE_BIT);
    }

    public UpdateBuilder setDisabled(Boolean newDisabled) {
      if (newDisabled == null) {
        return this;
      }
      return updateBits(newDisabled, DISABLED_BIT);
    }

    public UpdateBuilder setRequired(boolean newRequired) {
      return updateBits(newRequired, REQUIRED_BIT);
    }

    public UpdateBuilder setReadOnly(boolean newReadOnly) {
      return updateBits(newReadOnly, READ_ONLY_BIT);
    }

    public UpdateBuilder setRowsCanBeAdded(boolean newRowsCanBeAdded) {
      return updateBits(newRowsCanBeAdded, ROWS_CAN_BE_ADDED_BIT);
    }

    public UpdateBuilder setRowCanBeRemoved(boolean newRowsCanBeRemoved) {
      return updateBits(newRowsCanBeRemoved, ROW_CAN_BE_REMOVED_BIT);
    }

    public UpdateBuilder setHasCustomProps(boolean newHasCustomProps) {
      return updateBits(newHasCustomProps, HAS_CUSTOM_PROPS_BIT);
    }

    public UpdateBuilder setInvalidAnswers(boolean newIsInvalidAnswers) {
      return updateBits(newIsInvalidAnswers, INVALID_ANSWERS_BIT);
    }

    public UpdateBuilder setLabel(String newLabel) {
      if (!Objects.equals(label, newLabel)) {
        state().label(newLabel);
      }
      return this;
    }

    public UpdateBuilder setDescription(String newDescription) {
      if (!Objects.equals(description, newDescription)) {
        state().description(newDescription);
      }
      return this;
    }

    public UpdateBuilder setProp(String propName, Object newValue) {
      Object previous = props.get(propName);
      if (!Objects.equals(previous, newValue)) {
        state().props.put(propName, newValue);
      }
      return this;
    }

    public UpdateBuilder setClassNames(List<String> newClassNames) {
      if (!Objects.equals(classNames, newClassNames)) {
        state().classNames(List.copyOf(newClassNames));
      }
      return this;
    }

    public UpdateBuilder setItems(List<ItemId> newItems) {
      if (!Objects.equals(items, newItems)) {
        state().items(List.copyOf(newItems));
      }
      return this;
    }

    public UpdateBuilder setAvailableItems(List<ItemId> newAvailableItems) {
      if (!Objects.equals(availableItems, newAvailableItems)) {
        state().availableItems(List.copyOf(newAvailableItems));
      }
      return this;
    }

    public UpdateBuilder setAllowedActions(Set<Action.Type> newAllowedActions) {
      if (!Objects.equals(allowedActions, newAllowedActions)) {
        state().allowedActions(Set.copyOf(newAllowedActions));
      }
      return this;
    }

    public UpdateBuilder setActivePage(ItemId newActivePage) {
      if ((hasNewState() && state().items.contains(newActivePage) || items.contains(newActivePage)) && !Objects.equals(activePage, newActivePage)) {
        // TODO matches is active item "available"
        state().activePage(newActivePage);
      }
      return this;
    }

    public ItemState get() {
      if (ItemState.this.bits() != this.bits) {
        state().bits(this.bits);
      }
      if (builder == null) {
        return ItemState.this;
      }
      return builder.build();
    }
  }

  public UpdateBuilder update() {
    return new UpdateBuilder();
  }

}
