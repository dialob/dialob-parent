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
package io.dialob.session.engine.session.model;

import com.google.common.collect.ImmutableList;
import edu.umd.cs.findbugs.annotations.NonNull;
import io.dialob.session.spi.SessionReader;
import io.dialob.session.engine.spi.SessionWriter;
import io.dialob.session.model.ImmutableValueSetId;
import io.dialob.session.model.ValueSetId;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@EqualsAndHashCode
@ToString
@AllArgsConstructor
public class ValueSetState implements SessionObject {

  private static final long serialVersionUID = 6040009682715910439L;

  private final ValueSetId id;

  private List<ValueSetState.Entry> entries;


  @EqualsAndHashCode
  @ToString
  public static class Entry implements Serializable {

    private static final long serialVersionUID = -4632044242844529912L;

    private final String id;

    private final String label;

    private final boolean provided;

    public static Entry of(String id, String label) {
      return of(id, label, false);
    }

    public static Entry of(String id, String label, boolean provided) {
      return new Entry(id, label, provided);
    }

    public Entry(String id, String label, boolean provided) {
      this.id = id;
      this.label = label;
      this.provided = provided;
    }

    public String getId() {
      return id;
    }

    public String getLabel() {
      return label;
    }

    /**
     * Is label provided by external service or defined on form.
     *
     * @return true when label is from external source
     */
    public boolean isProvided() {
      return provided;
    }
  }

  public class UpdateBuilder {

    private ValueSetState itemState;

    private ValueSetState state() {
      if (itemState == null) {
        this.itemState = new ValueSetState(ValueSetState.this);
      }
      return itemState;
    }

    public ValueSetState.UpdateBuilder setEntries(List<ValueSetState.Entry> newEntries) {
      if (!Objects.equals(entries, newEntries)) {
        if (newEntries != null) {
          state().entries = ImmutableList.copyOf(newEntries);
        } else {
          state().entries = null;
        }
      }
      return this;
    }

    public ValueSetState get() {
      if (itemState == null) {
        return ValueSetState.this;
      }
      return itemState;
    }
  }

  public ValueSetState.UpdateBuilder update() {
    return new ValueSetState.UpdateBuilder();
  }

  public ValueSetState(@NonNull String id) {
    this.id = ImmutableValueSetId.of(id);
  }

  public ValueSetState(@NonNull ValueSetState valueSetState) {
    this.id = valueSetState.id;
    if (valueSetState.entries != null) {
      this.entries = new ArrayList<>(valueSetState.entries);
    }
  }

  @NonNull
  public ValueSetId getId() {
    return id;
  }

  @NonNull
  public List<Entry> getEntries() {
    if (entries == null) {
      return Collections.emptyList();
    }
    return entries;
  }

  public void writeTo(SessionWriter writer) throws IOException {
    writer.writeString(getId().getValueSetId());
    writer.writeInt32(entries.size());
    for (Entry entry :  entries) {
      writer.writeString(entry.getId());
      writer.writeString(entry.getLabel());
      writer.writeBool(entry.isProvided());
    }
  }

  public static ValueSetState readFrom(SessionReader reader) throws IOException {
    String id = reader.readString();
    int count = reader.readInt32();
    Entry[] entries = new Entry[count];
    for ( int i = 0; i < count; i++) {
      String key = reader.readString();
      String label = reader.readString();
      boolean provided = reader.readBool();
      entries[i] = new Entry(key, label, provided);
    }
    return new ValueSetState(ImmutableValueSetId.of(id), ImmutableList.copyOf(entries));
  }


}
