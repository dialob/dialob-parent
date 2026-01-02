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
import org.jdbi.v3.core.mapper.Nested;
import org.jdbi.v3.json.Json;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public record Questionnaire(
  @Nested @NonNull Questionnaire.Id id,
  @NonNull Integer rev,
  @NonNull Instant created,
  @NonNull Instant updated,
  String owner,
  @NonNull FormDocument.Id formDocument,
  String status,
  @Json io.dialob.api.questionnaire.Questionnaire data
) implements HasRevision<Questionnaire> {
  public record Id(
    @NonNull String tenantId,
    @NonNull UUID id
  ) {
  }

  public static Questionnaire of(String updatedBy, String tenantId, io.dialob.api.questionnaire.Questionnaire questionnaire) {
    var now = Instant.now();
    var metadata = questionnaire.metadata();
    var id = Objects.requireNonNullElseGet(questionnaire.id(), UUID::randomUUID).toString();
    var rev = Objects.requireNonNullElse(questionnaire.rev(), "1");
    var creator = Objects.requireNonNullElse(metadata.creator(), updatedBy);
    var created = Objects.requireNonNullElse(metadata.created(), now);
    var status = Objects.requireNonNullElse(questionnaire.metadata().status(), io.dialob.api.questionnaire.Questionnaire.Metadata.Status.NEW);
    return new Questionnaire(
      new Questionnaire.Id(tenantId, UUID.fromString(id)),
      Integer.parseInt(rev),
      created,
      now,
      creator,
      FormDocument.id(tenantId, UUID.fromString(questionnaire.metadata().formId())),
      status.toString(),
      new io.dialob.api.questionnaire.Questionnaire.Builder().from(questionnaire)
        .id(id)
        .rev(rev)
        .metadata(new io.dialob.api.questionnaire.Questionnaire.Metadata.Builder()
          .from(metadata)
          .created(created)
          .tenantId(tenantId)
          .creator(creator)
          .build())
        .build()
    );
  }

  public Questionnaire nextRev(String updatedBy) {
    var next = this.rev + 1;
    var now = Instant.now();
    return new Questionnaire(
      this.id,
      next,
      this.created,
      now,
      this.owner,
      this.formDocument,
      this.status,
      new io.dialob.api.questionnaire.Questionnaire.Builder().from(this.data)
        .id(this.id.id().toString())
        .rev(Integer.toString(next))
        .build()
    );
  }

  public static Questionnaire.Id id(@NonNull String tenantId,
                                    @NonNull UUID id
  ) {
    return new Questionnaire.Id(tenantId, id);
  }

}
