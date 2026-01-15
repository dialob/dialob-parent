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

import java.io.IOException;
import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;

public record ValueSetState(
  ValueSetId id,
  List<Entry> entries
) implements SessionObject<ValueSetId> {

  @Serial
  private static final long serialVersionUID = 6040009682715910439L;

  public ValueSetState {
    Objects.requireNonNull(id, "id is null");
    entries = List.copyOf(Objects.requireNonNullElseGet(entries, List::of));
  }

  public ValueSetState(@NonNull String id, List<Entry> entries) {
    this(new ValueSetId(id), entries);
  }


  /**
   * @param id
   * @param label
   * @param provided Is label provided by external service or defined on form.
   */
  public record Entry(
    String id,
    String label,
    boolean provided
  ) implements Serializable {

    @Serial
    private static final long serialVersionUID = -4632044242844529912L;

    public static Entry of(String id, String label) {
      return of(id, label, false);
    }

    public static Entry of(String id, String label, boolean provided) {
      return new Entry(id, label, provided);
    }

  }

  public class UpdateBuilder {

    private List<Entry> entries = List.of();

    private boolean updated = false;

    public UpdateBuilder setEntries(List<Entry> newEntries) {
      if (!Objects.equals(entries, newEntries)) {
        updated = true;
        if (newEntries != null) {
          entries = List.copyOf(newEntries);
        } else {
          entries = List.of();
        }
      }
      return this;
    }

    public ValueSetState get() {
      if (!updated) {
        return ValueSetState.this;
      }
      return new ValueSetState(ValueSetState.this.id, this.entries);
    }
  }

  public UpdateBuilder update() {
    return new UpdateBuilder();
  }

  @Override
  public void writeTo(StateWriter output) throws IOException {
    output.writeString(id().getValueSetId());
    output.writeInt(entries.size());
    for (Entry entry : entries) {
      output.writeString(entry.id());
      output.writeString(entry.label());
      output.writeBool(entry.provided());
    }
  }

  public static ValueSetState readFrom(StateReader input) throws IOException {
    String id = input.readString();
    int count = input.readInt();
    Entry[] entries = new Entry[count];
    for (int i = 0; i < count; i++) {
      String key = input.readString();
      String label = input.readString();
      boolean provided = input.readBool();
      entries[i] = new Entry(key, label, provided);
    }
    return new ValueSetState(new ValueSetId(id), List.of(entries));
  }


}
