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
import io.dialob.api.form.FormTag;
import io.dialob.db.jdbi.model.FormDocument;
import io.dialob.db.jdbi.model.FormRev;
import org.jdbi.v3.core.enums.EnumByName;
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

@JdbiRepository
public interface FormRevJdbiRepository {

  static FormRev createFromResultSet(
    @Nested @NonNull FormRev.Id id,
    @ColumnName("ref_name") String refName,
    @ColumnName("description") String description,
    @ColumnName("form_document_id") @NonNull UUID formDocumentId,
    @ColumnName("type") @EnumByName FormTag.Type type,
    @ColumnName("creator") String creator,
    @ColumnName("created") @NonNull Instant created,
    @ColumnName("updated") @NonNull Instant updated
  ) {
    FormRev.Id ref = new FormRev.Id(id.tenantId(), id.formName(), refName);
    return new FormRev(id, ref, created, updated, FormDocument.id(id.tenantId(), formDocumentId), description, type, creator);
  }

  @SqlQuery("""
    SELECT
      tenant_id, form_name, name, ref_name, form_document_id, description, created, updated, type, creator
    FROM
      <form_rev>
    WHERE
      tenant_id = :tenantId
      AND form_name = :formName
      AND name = :name
    """)
  @RegisterConstructorMapper(value = FormRev.class, usingStaticMethodIn = FormRevJdbiRepository.class)
  Optional<FormRev> findOne(
    @BindMethods @NonNull FormRev.Id id
  );

  @SqlUpdate("""
    INSERT INTO <form_rev>
      (tenant_id, form_name, name, ref_name, form_document_id, description, created, updated, type, creator)
    VALUES
      (:id.tenantId, :id.formName, :id.name, :ref?.name, :formDocument.id, :description, :created, :updated, :type, :creator)
    """)
  @RegisterConstructorMapper(value = FormRev.class, usingStaticMethodIn = FormRevJdbiRepository.class)
  @GetGeneratedKeys
  Optional<FormRev> insert(@BindMethods FormRev form);

  @SqlUpdate("""
    UPDATE <form_rev> SET
      updated = :updated,
      label = :label,
      latest_form_id = :latestForm.id
    WHERE
      tenant_id = :tenantId
      AND form_name = :formName
      AND name = :name
    """)
  @RegisterConstructorMapper(value = FormRev.class, usingStaticMethodIn = FormRevJdbiRepository.class)
  @GetGeneratedKeys
  Optional<FormRev> update(@BindMethods FormRev form);

  @SqlUpdate("""
    DELETE FROM
      <form_rev>
    WHERE
      tenant_id = :tenantId
      AND form_name = :formName
      AND name = :name
    """)
  boolean delete(@BindMethods @NonNull FormRev.Id id);


}
