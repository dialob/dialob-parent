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
package io.dialob.db.jdbc;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.umd.cs.findbugs.annotations.NonNull;
import io.dialob.api.form.Form;
import io.dialob.api.form.ImmutableForm;
import io.dialob.api.form.ImmutableFormMetadata;
import io.dialob.db.spi.exceptions.DocumentConflictException;
import io.dialob.db.spi.exceptions.DocumentCorruptedException;
import io.dialob.db.spi.exceptions.DocumentNotFoundException;
import io.dialob.form.service.api.FormDatabase;
import io.dialob.form.service.api.ImmutableFormMetadataRow;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.transaction.support.TransactionTemplate;

import java.io.IOException;
import java.io.Reader;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Slf4j
public class JdbcFormDatabase extends JdbcBackendDatabase<Form> implements FormDatabase {

  public JdbcFormDatabase(JdbcTemplate jdbcTemplate,
                          DatabaseHelper databaseHelper,
                          TransactionTemplate transactionTemplate,
                          ObjectMapper objectMapper,
                          String schema,
                          Predicate<String> isAnyTenantPredicate)
  {
    super(transactionTemplate, jdbcTemplate, databaseHelper, objectMapper, schema, "form_document", Form.class, isAnyTenantPredicate);
  }

  @NonNull
  @Override
  public Form findOne(@NonNull String tenantId, @NonNull String id, String rev) {
    Integer revision = Utils.validateRevValue(rev);
    byte[] oid = Utils.toOID(id);
    return doTransaction(template -> {
      RowMapper<Form> rowMapper = (resultSet, i) -> {
        int objectRev = resultSet.getInt(1);
        String rsTenantId = StringUtils.trim(resultSet.getString(2));
        Timestamp created = resultSet.getTimestamp(3);
        Timestamp updated = resultSet.getTimestamp(4);
        Reader reader = getDatabaseHelper().extractStream(resultSet, 5);
        return toObject(oid, objectRev, rsTenantId, created, updated, reader);
      };

      final StringBuilder sql = new StringBuilder("select rev, tenant_id, created, updated, " + bsonToJson("data") + " from " + tableName + " where id = ?");
      final List<Object> sqlParameters = new ArrayList<>();

      sqlParameters.add(toJdbcId(oid));
      if (revision != null) {
        sqlParameters.add(revision);
        sql.append(" and rev = ?");
      }
      if (notAnyTenant(tenantId)) {
        sqlParameters.add(tenantId);
        sql.append(" and tenant_id = ?");
      }
      try {
        return template.queryForObject(sql.toString(), sqlParameters.toArray(), rowMapper);
      } catch (EmptyResultDataAccessException e) {
        throw new DocumentNotFoundException(id + " not found");
      }
    });
  }

  @NonNull
  @Override
  public Form save(String tenantId, @NonNull Form document) {
    return doTransaction(template -> {
      final Timestamp timestamp = new Timestamp(System.currentTimeMillis());
      var dId = id(document);
      byte[] oid = Utils.toOID(dId);
      Integer revision = getRevision(document);
      // TODO update tenantId to metadata?
      Object data = getDatabaseHelper().jsonObject(objectMapper, document);
      int updated;
      if (revision != null && oid != null) {
        int prevRevision = revision++;
        if (notAnyTenant(tenantId)) {
          updated = template.update("update " + tableName + " set rev = ?, updated = ?, data = " + jsonToBson("?") + " where id = ? and rev = ? and tenant_id = ?", revision, timestamp, data, toJdbcId(oid), prevRevision, tenantId);
        } else {
          updated = template.update("update " + tableName + " set rev = ?, updated = ?, data = " + jsonToBson("?") + "  where id = ? and rev = ?", revision, timestamp, data, toJdbcId(oid), prevRevision);
        }
      } else {
        revision = 1;
        if (oid == null) {
          oid = Utils.generateOID();
        }
        updated = template.update("insert into " + tableName + " (id,rev,tenant_id,created,updated,data) values (?,?,?,?,?," + jsonToBson("?") + ")", toJdbcId(oid), revision, tenantId, timestamp, timestamp, data);
      }
      if (updated == 0) {
        throw new DocumentConflictException(String.format("Conflict during form document %s rev %d update.", dId, revision));
      }
      return updatedDocument(document, oid, revision, timestamp, tenantId);
    });
  }

  protected Form toObject(@NonNull byte[] oid, int objectRev, String tenantId, Timestamp created, Timestamp updated, Reader reader) {
    try {
      final Form form = objectMapper.readValue(reader, Form.class);
      return ImmutableForm.builder().from(form)
        .id(toId(oid))
        .rev(Integer.toString(objectRev))
        .metadata(ImmutableFormMetadata.builder().from(form.getMetadata()).created(new Date(created.getTime())).tenantId(tenantId).build())
        .build();
    } catch (IOException e) {
      throw new DocumentCorruptedException("Could not read document " + toId(oid) + ":" + e.getMessage());
    }
  }

  @NonNull
  @Override
  protected Form updatedDocument(@NonNull Form form, @NonNull byte[] oid, @NonNull Integer revision, @NonNull Timestamp timestamp, String tenantId) {
    ImmutableFormMetadata.Builder builder = ImmutableFormMetadata.builder();
    if (form.getMetadata() != null) {
      builder = builder.from(form.getMetadata());
      builder = builder.created(new Date(timestamp.getTime()));
    }
    if (tenantId != null) {
      builder = builder.tenantId(tenantId);
    }
    return ImmutableForm.builder().from(form)
      .id(toId(oid))
      .rev(Integer.toString(revision))
      .metadata(builder.build())
      .build();
  }

  @Override
  public void findAllMetadata(String tenantId, Form.Metadata metadata, @NonNull Consumer<FormMetadataRow> consumer) {
    transactionTemplate.execute(transactionStatus -> {
      List<Object> params = new ArrayList<>();
      List<String> conditions = new ArrayList<>();
      if (notAnyTenant(tenantId)) {
        conditions.add("tenant_id = ?");
        params.add(tenantId);
      }
      if (metadata != null) {
        conditions.add(getDatabaseHelper().jsonContains("metadata"));
        params.add(getDatabaseHelper().jsonObject(objectMapper, metadata));
      }
      String where = conditions.stream().collect(Collectors.joining(" and "));
      if (StringUtils.isNotBlank(where)) {
        where = " where " + where;
      }
      jdbcTemplate.query("select tenant_id, id, created, updated, " + databaseHelper.extractMetadataJsonArray("labels") + " from " + tableName + where, resultSet -> {
        String tId = StringUtils.trim(resultSet.getString(1));
        byte[] id = getDatabaseHelper().fromJdbcId(resultSet.getBytes(2));
        Timestamp created = resultSet.getTimestamp(3);
        Timestamp updated = resultSet.getTimestamp(4);
        String labels = resultSet.getString(5);

        ImmutableFormMetadata.Builder metadataBuilder = ImmutableFormMetadata.builder()
          .tenantId(tId)
          .created(new Date(created.getTime()))
          .lastSaved(new Date(updated.getTime()));

        if (labels != null) {
          try {
            String[] labelArray = objectMapper.readValue(labels, String[].class);
            metadataBuilder.addAllLabels(Arrays.stream(labelArray).toList());
          } catch (Exception e) {
            throw new RuntimeException("Unable to parse label array", e);
          }
        }
        consumer.accept(ImmutableFormMetadataRow.of(toId(id), metadataBuilder.build()));
      }, params.toArray());
      return null;
    });
  }

  @NonNull
  @Override
  protected Form updateDocumentId(@NonNull Form document, String id) {
    return ImmutableForm.builder().from(document).id(id).build();
  }

  @NonNull
  @Override
  protected Form updateDocumentRev(@NonNull Form document, String rev) {
    return ImmutableForm.builder().from(document).rev(rev).build();
  }

}
