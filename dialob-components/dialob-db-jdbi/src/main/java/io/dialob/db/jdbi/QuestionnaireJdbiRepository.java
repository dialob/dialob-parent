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
package io.dialob.db.jdbi;

import edu.umd.cs.findbugs.annotations.NonNull;
import io.dialob.db.jdbi.model.FormDocument;
import io.dialob.db.jdbi.model.Questionnaire;
import org.jdbi.v3.core.mapper.Nested;
import org.jdbi.v3.core.mapper.reflect.ColumnName;
import org.jdbi.v3.json.Json;
import org.jdbi.v3.spring.JdbiRepository;
import org.jdbi.v3.sqlobject.config.RegisterConstructorMapper;
import org.jdbi.v3.sqlobject.customizer.BindMethods;
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@JdbiRepository
public interface QuestionnaireJdbiRepository {

  static Questionnaire createFromResultSet(
    @Nested @NonNull Questionnaire.Id id,
    @ColumnName("rev") @NonNull Integer rev,
    @ColumnName("created") @NonNull Instant created,
    @ColumnName("updated") @NonNull Instant updated,
    @ColumnName("owner") String owner,
    @ColumnName("form_document_id") @NonNull String formDocumentId,
    @ColumnName("status") String status,
    @ColumnName("data") @Json io.dialob.api.questionnaire.Questionnaire data
  ) {
    return new Questionnaire(id, rev, created, updated, owner, FormDocument.id(id.tenantId(), UUID.fromString(formDocumentId)), status, data);
  }

  @SqlQuery("""
    SELECT
      tenant_id, id, rev, created, updated, <json_data_out> as data, status, form_document_id, owner
    FROM
      <questionnaire>
    WHERE
      tenant_id = :tenantId
      AND id = :id
    """)
  @RegisterConstructorMapper(value = Questionnaire.class, usingStaticMethodIn = QuestionnaireJdbiRepository.class)
  Optional<Questionnaire> findOne(
    @BindMethods @NonNull Questionnaire.Id id
  );

  @SqlUpdate("""
    INSERT INTO <questionnaire>
      (tenant_id, id, rev, created, updated, <json_data_in>, status, form_document_id, owner)
    VALUES
      (:id.tenantId, :id.id, :rev, :created, :updated, :data, :status, :formDocument.id, :owner)
    """)
  @GetGeneratedKeys
  @RegisterConstructorMapper(value = Questionnaire.class, usingStaticMethodIn = QuestionnaireJdbiRepository.class)
  Optional<Questionnaire> insert(@BindMethods Questionnaire questionnaire);

  @SqlUpdate("""
    UPDATE <questionnaire> SET
      rev = :rev,
      updated = :updated,
      <json_data_in> = :data,
      status = :status,
      owner = :owner
    WHERE
      tenant_id = :id.tenantId
      AND id = :id.id
      AND rev = :rev - 1
    """)
  @GetGeneratedKeys
  @RegisterConstructorMapper(value = Questionnaire.class, usingStaticMethodIn = QuestionnaireJdbiRepository.class)
  Optional<Questionnaire> update(@BindMethods Questionnaire questionnaire);

  @SqlUpdate("""
    DELETE FROM
      <questionnaire>
    WHERE
      tenant_id = :tenantId
      AND id = :id
    """)
  boolean delete(@BindMethods @NonNull Questionnaire.Id id);


}
