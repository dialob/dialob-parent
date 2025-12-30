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
import org.jspecify.annotations.Nullable;

import java.io.IOException;
import java.io.Serial;
import java.util.Objects;
import java.util.function.Consumer;

@lombok.Builder(toBuilder = true)
public record ErrorState(
  ErrorId targetId,
  @Nullable String label,
  @Value.Default.Boolean(false)
  boolean active,
  @Value.Default.Boolean(false)
  boolean disabled
) implements SessionObject<ErrorId> {

  @Serial
  private static final long serialVersionUID = -6652593868401573582L;

  public ErrorState(@NonNull ErrorId targetId, String label) {
    this(targetId, label, false, false);
  }

  public ErrorState withErrorId(@NonNull ErrorId targetId) {
    return new ErrorState(targetId, this.label, this.active, this.disabled);
  }

  public class UpdateBuilder {

    private Consumer<ErrorState.ErrorStateBuilder> updateLabel = null;

    private Consumer<ErrorState.ErrorStateBuilder> updateActive = null;

    private Consumer<ErrorState.ErrorStateBuilder> updateDisabled = null;

    public ErrorState.UpdateBuilder setActive(boolean newActive) {
      if (ErrorState.this.active() != newActive) {
        this.updateActive = builder -> builder.active(newActive);
      } else {
        this.updateActive = null;
      }
      return this;
    }

    public ErrorState.UpdateBuilder setDisabled(boolean newDisabled) {
      if (ErrorState.this.disabled() != newDisabled) {
        this.updateDisabled = builder -> builder.disabled(newDisabled);
      } else {
        this.updateDisabled = null;
      }
      return this;
    }

    public ErrorState.UpdateBuilder setLabel(String newLabel) {
      if (!Objects.equals(ErrorState.this.label(), newLabel)) {
        this.updateLabel = builder -> builder.label(newLabel);
      } else {
        this.updateLabel = null;
      }
      return this;
    }

    public ErrorState get() {
      boolean updated = false;
      var builder = toBuilder();
      if (updateLabel != null) {
        this.updateLabel.accept(builder);
        updated = true;
      }
      if (updateActive != null) {
        this.updateActive.accept(builder);
        updated = true;
      }
      if (updateDisabled != null) {
        this.updateDisabled.accept(builder);
        updated = true;
      }
      if (updated) {
        return builder.build();
      }
      return ErrorState.this;
    }
  }

  public ErrorState.UpdateBuilder update() {
    return new ErrorState.UpdateBuilder();
  }

  @Override
  public ErrorId id() {
    return targetId;
  }

  public ItemId itemId() {
    return targetId.itemId();
  }

  public String code() {
    return targetId.code();
  }

  public String label() {
    return label;
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

  @Override
  public void writeTo(StateWriter output) throws IOException {
    output.writeNullableId(targetId.itemId());
    output.writeNullableString(targetId.code());
    output.writeString(label);
    output.writeBool(active);
    output.writeBool(disabled);
  }

  public static ErrorState readFrom(StateReader input) throws IOException {
    var itemId  = Objects.requireNonNull(input.readNullableId());
    var code = input.readNullableString();
    var label = input.readString();
    var active = input.readBool();
    var disabled = input.readBool();
    return new ErrorState(new ErrorId(itemId, code), label, active, disabled);
  }
}
