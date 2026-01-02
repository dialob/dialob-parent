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
import edu.umd.cs.findbugs.annotations.Nullable;
import io.dialob.db.jdbi.model.Form;
import io.dialob.db.jdbi.model.FormDocument;
import org.jdbi.v3.core.mapper.Nested;
import org.jdbi.v3.core.mapper.reflect.ColumnName;
import org.jdbi.v3.spring.JdbiRepository;
import org.jdbi.v3.sqlobject.config.RegisterConstructorMapper;
import org.jdbi.v3.sqlobject.customizer.BindMethods;
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

@JdbiRepository
public interface FormJdbiRepository {

  static Form createFromResultSet(
    @Nested @NonNull Form.Id id,
    @ColumnName("label") String label,
    @ColumnName("latest_form_id") @NonNull UUID latestFormId,
    @ColumnName("created") @NonNull Instant created,
    @ColumnName("updated") @NonNull Instant updated
  ) {
    return new Form(id, label, new FormDocument.Id(id.tenantId(), latestFormId), created, updated);
  }

  @SqlQuery("""
    SELECT
      name, tenant_id, label, latest_form_id, created, updated
    FROM
      <form>
    WHERE
      tenant_id = :tenantId
      AND name = :name
    """)
  @RegisterConstructorMapper(value = Form.class, usingStaticMethodIn = FormJdbiRepository.class)
  Optional<Form> findOne(
    @BindMethods @NonNull Form.Id id
  );

  record Query(@Nullable String tenantId,
               @Nullable String label
  ) {
  }

  @SqlQuery("""
    SELECT
      name, tenant_id, label, latest_form_id, created, updated
    FROM
      <form>
    WHERE
      (:tenantId IS NULL OR tenant_id = :tenantId)
      AND (:name IS NULL OR name = :name)
    """)
  @RegisterConstructorMapper(value = Form.class, usingStaticMethodIn = FormJdbiRepository.class)
  Stream<Form> query(@BindMethods @NonNull Query query);

  @SqlUpdate("""
    INSERT INTO <form>
      (tenant_id, name, label, latest_form_id, created, updated)
    VALUES
      (:id.tenantId, :id.name, :label, :latestForm.id, :created, :updated)
    """)
  @GetGeneratedKeys
  @RegisterConstructorMapper(value = Form.class, usingStaticMethodIn = FormJdbiRepository.class)
  Optional<Form> insert(@BindMethods Form form);

  @SqlUpdate("""
    UPDATE <form> SET
      updated = :updated,
      label = :label,
      latest_form_id = :latestForm.id
    WHERE
      tenant_id = :id.tenantId
      AND name = :id.name
    """)
  @GetGeneratedKeys
  @RegisterConstructorMapper(value = Form.class, usingStaticMethodIn = FormJdbiRepository.class)
  Optional<Form> update(@BindMethods Form form);

  @SqlUpdate("""
    DELETE FROM
      <form>
    WHERE
      tenant_id = :tenantId
      AND name = :name
    """)
  boolean delete(@BindMethods @NonNull Form.Id id);

}
