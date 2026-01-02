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
package io.dialob.db.jdbi.model;

import edu.umd.cs.findbugs.annotations.NonNull;
import io.dialob.api.form.Form;
import org.jdbi.v3.core.mapper.Nested;
import org.jdbi.v3.json.Json;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public record FormDocument(
  @Nested @NonNull Id id,
  @NonNull Integer rev,
  Instant created,
  Instant updated,
  @Json Form data
) implements HasRevision<FormDocument> {

  public static FormDocument of(Form form) {
    return of("system", form.metadata().tenantId(), form);
  }

  public static FormDocument of(String updatedBy, String tenantId, Form form) {
    var now = Instant.now();
    var metadata = form.metadata();
    var id = Objects.requireNonNullElseGet(form.id(), UUID::randomUUID).toString();
    var rev = Objects.requireNonNullElse(form.rev(), "1");
    String creator = Objects.requireNonNullElse(metadata.creator(), updatedBy);
    var created = Objects.requireNonNullElse(metadata.created(), now);
    var updated = Objects.requireNonNullElse(metadata.lastSaved(), now);
    return new FormDocument(
      new Id(tenantId, UUID.fromString(id)),
      Integer.parseInt(rev),
      created,
      updated,
      new Form.Builder().from(form)
        .id(id)
        .rev(rev)
        .metadata(new Form.Metadata.Builder()
          .from(metadata)
          .created(created)
          .lastSaved(updated)
          .tenantId(tenantId)
          .savedBy(updatedBy)
          .creator(creator)
          .build())
        .build()
    );
  }

  public FormDocument nextRev(String updatedBy) {
    var next = this.rev + 1;
    var now = Instant.now();
    return new FormDocument(
      this.id,
      next,
      this.created,
      now,
      new Form.Builder().from(this.data)
        .id(this.id.id().toString())
        .rev(Integer.toString(next))
        .metadata(new Form.Metadata.Builder()
          .from(this.data.metadata())
          .created(this.created)
          .lastSaved(now)
          .savedBy(updatedBy)
          .build())
        .build()
    );
  }

  public static Id id(@NonNull String tenantId,
                      @NonNull UUID id
  ) {
    return new Id(tenantId, id);
  }

  public record Id(
    @NonNull String tenantId,
    @NonNull UUID id
  ) {
  }

}
