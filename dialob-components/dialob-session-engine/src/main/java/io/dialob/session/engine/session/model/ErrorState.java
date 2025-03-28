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
import io.dialob.session.engine.program.EvalContext;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.io.IOException;
import java.io.Serial;
import java.util.Objects;

@EqualsAndHashCode
@ToString
public class ErrorState implements SessionObject {

  @Serial
  private static final long serialVersionUID = -6652593868401573582L;

  private final ErrorId targetId;

  @Getter
  private String label;

  private boolean active;

  private boolean disabled;

  public class UpdateBuilder {

    private ErrorState itemState;

    private ErrorState state() {
      if (itemState == null) {
        this.itemState = new ErrorState(ErrorState.this);
      }
      return itemState;
    }

    public ErrorState.UpdateBuilder setActive(boolean newActive) {
      if (active != newActive) {
        state().active = newActive;
      }
      return this;
    }

    public ErrorState.UpdateBuilder setDisabled(boolean newDisabled) {
      if (disabled != newDisabled) {
        state().disabled = newDisabled;
      }
      return this;
    }

    public ErrorState.UpdateBuilder setLabel(String newLabel) {
      if (!Objects.equals(newLabel, label)) {
        state().label = newLabel;
      }
      return this;
    }

    public ErrorState get() {
      if (itemState == null) {
        return ErrorState.this;
      }
      return itemState;
    }

  }

  public ErrorState.UpdateBuilder update(EvalContext context) {
    return new ErrorState.UpdateBuilder();
  }

  public ErrorState(@NonNull ItemId itemId, String code, String label) {
    this(ImmutableErrorId.of(itemId, code), label);
  }

  public ErrorState(@NonNull ErrorId targetId, String label) {
    this.targetId = targetId;
    this.label = label;
  }

  public ErrorState(@NonNull ErrorState errorState) {
    this(errorState.targetId, errorState);
  }

  public ErrorState(@NonNull ErrorId targetId, @NonNull ErrorState errorState) {
    this.targetId = targetId;
    this.label = errorState.label;
    this.active = errorState.active;
    this.disabled = errorState.disabled;
  }

  public ErrorState withErrorId(@NonNull ErrorId targetId) {
    return new ErrorState(targetId, this);
  }

  @Override
  public ErrorId getId() {
    return targetId;
  }

  public ItemId getItemId() {
    return targetId.getItemId();
  }

  public String getCode() {
    return targetId.getCode();
  }

  @Override
  public boolean isActive() {
    return active;
  }

  @Override
  public boolean isDisabled() {
    return disabled;
  }

  @Override
  public boolean isDisplayItem() {
    return true;
  }

  public void writeTo(CodedOutputStream output) throws IOException {
    IdUtils.writeIdTo(targetId.getItemId(), output);
    if (targetId.getCode() == null) {
      output.writeBoolNoTag(false);
    } else {
      output.writeBoolNoTag(true);
      output.writeStringNoTag(targetId.getCode());
    }
    output.writeStringNoTag(label);
    output.writeBoolNoTag(active);
    output.writeBoolNoTag(disabled);

  }

  public static ErrorState readFrom(CodedInputStream input) throws IOException {
    ItemId itemId  = Objects.requireNonNull(IdUtils.readIdFrom(input));
    String code = null;
    if (input.readBool()) {
      code = input.readString();
    }
    String label = input.readString();
    ErrorState state = new ErrorState(itemId, code, label);
    state.active = input.readBool();
    state.disabled = input.readBool();
    return state;
  }
}
