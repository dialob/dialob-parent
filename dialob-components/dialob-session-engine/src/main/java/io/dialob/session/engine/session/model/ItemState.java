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
import io.dialob.api.proto.Action;
import io.dialob.session.engine.Utils;
import io.dialob.session.engine.session.protobuf.StateReader;
import io.dialob.session.engine.session.protobuf.StateWriter;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.Serial;
import java.util.*;


@EqualsAndHashCode
@ToString
public final class ItemState implements SessionObject<ItemId> {

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

  private static final int DISPLAY_ITEM_BIT = 1;
  private static final int ACTIVE_BIT = 1 << 1;
  private static final int DISABLED_BIT = 1 << 2;
  private static final int REQUIRED_BIT = 1 << 3;
  private static final int ROWS_CAN_BE_ADDED_BIT = 1 << 4;
  private static final int ROW_CAN_BE_REMOVED_BIT = 1 << 5;
  private static final int INVALID_ANSWERS_BIT = 1 << 6;
  private static final int HAS_CUSTOM_PROPS_BIT = 1 << 7;


  private final ItemId id;

  private final ItemId prototypeId;

  private final String type;

  private final String view;

  private final String valueSetId;

  @Getter
  private Status status = Status.NEW;

  @Getter
  private Object answer;

  private Object value;

  private Object defaultValue;

  private int bits = (ACTIVE_BIT | ROWS_CAN_BE_ADDED_BIT);

  @Getter
  private String label;

  @Getter
  private String description;

  // indicates whether questionnaire is completed

  @Getter
  private List<String> classNames = List.of();

  private List<ItemId> items = List.of();

  @Getter
  private List<ItemId> availableItems = List.of();

  private Map<String, Object> props = new HashMap<>();

  @Getter
  private Set<Action.Type> allowedActions = Set.of();

  private ItemId activePage;

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

  private boolean testBit(int bit) {
    return (bits & bit) != 0;
  }

  public static ItemState readFrom(StateReader input) throws IOException {
    final ItemId id = input.readNullableId();
    final ItemId prototypeId = input.readNullableId();
    final String type = input.readString();
    final String view = input.readNullableString();
    final String valueSetId = input.readNullableString();

    ItemState state = new ItemState(id, prototypeId, type, view, valueSetId);
    state.activePage = input.readNullableId();
    state.status = Status.values()[input.readRawByte()];
    state.bits = input.readInt();
    state.label = input.readNullableString();
    state.description = input.readNullableString();

    state.answer = input.readNullableObjectValue();
    state.value = input.readNullableValue();
    state.defaultValue = input.readNullableValue();

    state.classNames = input.readStringList();
    state.items = input.readIdList();
    state.availableItems = input.readIdList();

    int count = input.readInt();
    if ( count >  0) {
      Action.Type[] types = new Action.Type[count];
      for (int i = 0; i < count; i++ ){
        types[i] = Action.Type.values()[input.readInt()];
      }
      state.allowedActions = Set.of(types);
    } else {
      state.allowedActions = Set.of();
    }
    return state;
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
    output.writeValue(Utils.mapQuestionTypeToValueType(type).orElse(null), value);
    output.writeValue(Utils.mapQuestionTypeToValueType(type).orElse(null), defaultValue);

    output.writeStringList(classNames);
    output.writeIdList(items);
    output.writeIdList(availableItems);

    output.writeInt(allowedActions.size());
    for (Action.Type actionType: allowedActions) {
      output.writeInt(actionType.ordinal());
    }
  }


  public ItemState(@NonNull ItemId id, ItemId prototypeId, @NonNull String type, String view, String valueSetId) {
    this.id = id;
    this.prototypeId = prototypeId;
    this.type = type;
    this.view = view;
    this.valueSetId = valueSetId;
    resetBits(DISPLAY_ITEM_BIT);
  }

  public ItemState(@NonNull ItemId id, ItemId prototypeId, @NonNull String type, String view, boolean displayItem, String valueSetId, Object answer, Object value, Object defaultValue, ItemId activePage) {
    this.valueSetId = valueSetId;
    this.id = id;
    this.prototypeId = prototypeId;
    this.type = type;
    this.view = view;
    this.updateBit(displayItem, DISPLAY_ITEM_BIT);
    this.answer = answer;
    this.value = value;
    this.defaultValue = defaultValue;
    this.activePage = activePage;
  }

  ItemState(@NonNull ItemState itemState) {
    this(itemState.id(), itemState);
  }

  ItemState(@NonNull ItemId id, @NonNull ItemState itemState) {
    this.id = id;
    this.prototypeId = itemState.prototypeId;
    this.type = itemState.type;
    this.view = itemState.view;
    this.valueSetId = itemState.valueSetId;
    this.status = itemState.status;
    this.answer = itemState.answer;
    this.value = itemState.value;
    this.defaultValue = itemState.defaultValue;
    this.bits = itemState.bits;
    this.label = itemState.label;
    this.description = itemState.description;
    this.classNames = itemState.classNames;
    this.items = itemState.items;
    this.availableItems = itemState.availableItems;
    this.props = itemState.props;
    this.allowedActions = itemState.allowedActions;
    this.activePage = itemState.activePage;
  }

  @NonNull
  public ItemId id() {
    return id;
  }

  @Nullable
  public ItemId getPrototypeId() {
    return prototypeId;
  }

  @NonNull
  public String getType() {
    return type;
  }

  @Nullable
  public String getView() {
    return view;
  }

  @Override
  public boolean isDisplayItem() {
    return (bits & DISPLAY_ITEM_BIT) != 0;
  }

  public Optional<String> getValueSetId() {
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

  public boolean isRowsCanBeAdded() {
    return (bits & ROWS_CAN_BE_ADDED_BIT) != 0;
  }

  public boolean isRowCanBeRemoved() {
    return (bits & ROW_CAN_BE_REMOVED_BIT) != 0;
  }

  public boolean hasCustomProps() {
    return (bits & HAS_CUSTOM_PROPS_BIT) != 0;
  }

  @NonNull
  public List<ItemId> getItems() {
    return items;
  }

  public Optional<ItemId> getActivePage() {
    return Optional.ofNullable(activePage);
  }

  @NonNull
  public ItemState withId(@NonNull ItemId newId) {
    return new ItemState(newId, this);
  }

  public class UpdateBuilder {

    private ItemState itemState;

    UpdateBuilder() {
    }

    private ItemState state() {
      if (itemState == null) {
        this.itemState = new ItemState(ItemState.this);
      }
      return itemState;
    }

    private boolean hasNewState() {
      return this.itemState != null;
    }

    private UpdateBuilder updateBits(boolean toValue, int bit) {
      if (testBit(bit) != toValue) {
        state().updateBit(toValue, bit);
      }
      return this;
    }

    public UpdateBuilder setStatus(Status newStatus) {
      if (status != newStatus) {
        state().status = newStatus;
      }
      return this;
    }

    public UpdateBuilder setAnswer(Object newAnswer) {
      if (!Objects.equals(answer, newAnswer)) {
        state().answer = newAnswer;
      }
      return this;
    }

    public UpdateBuilder setValue(Object newValue) {
      if (!Objects.equals(value, newValue)) {
        state().value = newValue;
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
        state().label = newLabel;
      }
      return this;
    }

    public UpdateBuilder setDescription(String newDescription) {
      if (!Objects.equals(description, newDescription)) {
        state().description = newDescription;
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
        state().classNames = List.copyOf(newClassNames);
      }
      return this;
    }

    public UpdateBuilder setItems(List<ItemId> newItems) {
      if (!Objects.equals(items, newItems)) {
        state().items = List.copyOf(newItems);
      }
      return this;
    }

    public UpdateBuilder setAvailableItems(List<ItemId> newAvailableItems) {
      if (!Objects.equals(availableItems, newAvailableItems)) {
        state().availableItems = List.copyOf(newAvailableItems);
      }
      return this;
    }

    public UpdateBuilder setAllowedActions(Set<Action.Type> newAllowedActions) {
      if (!Objects.equals(allowedActions, newAllowedActions)) {
        state().allowedActions = Set.copyOf(newAllowedActions);
      }
      return this;
    }

    public UpdateBuilder setActivePage(ItemId newActivePage) {
      if ((hasNewState() && state().items.contains(newActivePage) || items.contains(newActivePage)) && !Objects.equals(activePage, newActivePage)) {
        // TODO matches is active item "available"
        state().activePage = newActivePage;
      }
      return this;
    }

    public ItemState get() {
      if (itemState == null) {
        return ItemState.this;
      }
      return itemState;
    }
  }

  public UpdateBuilder update() {
    return new UpdateBuilder();
  }

}
