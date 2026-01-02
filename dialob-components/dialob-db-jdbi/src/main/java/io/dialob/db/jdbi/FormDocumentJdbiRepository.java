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
import org.jdbi.v3.spring.JdbiRepository;
import org.jdbi.v3.sqlobject.config.RegisterConstructorMapper;
import org.jdbi.v3.sqlobject.customizer.BindMethods;
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import java.util.Optional;

@JdbiRepository
public interface FormDocumentJdbiRepository {

  @SqlQuery("""
    SELECT
      id, rev, tenant_id, created, updated, <json_data_out> as data
    FROM
      <form_document>
    WHERE
      tenant_id = :tenantId
      AND id = :id
    """)
  @RegisterConstructorMapper(FormDocument.class)
  Optional<FormDocument> findOne(
    @BindMethods @NonNull FormDocument.Id id
  );

  @SqlUpdate("""
    INSERT INTO <form_document>
      (tenant_id, id, rev, created, updated, <json_data_in>)
    VALUES
      (:id.tenantId, :id.id, :rev, :created, :updated, :data)
    """)
  @GetGeneratedKeys
  @RegisterConstructorMapper(FormDocument.class)
  FormDocument insert(@BindMethods FormDocument formDocument);

  @SqlUpdate("""
    UPDATE <form_document> SET
      rev = :rev, updated = :updated, <json_data_in> = :data
    WHERE
      tenant_id = :id.tenantId
      AND id = :id.id
      AND rev = :rev - 1
    """)
  @GetGeneratedKeys
  @RegisterConstructorMapper(FormDocument.class)
  Optional<FormDocument> update(@BindMethods FormDocument formDocument);

  @SqlUpdate("""
    DELETE FROM
      <form_document>
    WHERE
      tenant_id = :tenantId
      AND id = :id
    """)
  boolean delete(@BindMethods @NonNull FormDocument.Id id);


}
