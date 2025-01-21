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
import io.dialob.api.form.FormTag;
import io.dialob.api.questionnaire.ImmutableQuestionnaire;
import io.dialob.api.questionnaire.ImmutableQuestionnaireMetadata;
import io.dialob.api.questionnaire.Questionnaire;
import io.dialob.common.Constants;
import io.dialob.db.spi.exceptions.DocumentConflictException;
import io.dialob.db.spi.exceptions.DocumentCorruptedException;
import io.dialob.db.spi.exceptions.DocumentNotFoundException;
import io.dialob.form.service.api.FormVersionControlDatabase;
import io.dialob.questionnaire.service.api.ImmutableMetadataRow;
import io.dialob.questionnaire.service.api.QuestionnaireDatabase;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.mutable.MutableObject;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.lang.Nullable;
import org.springframework.transaction.support.TransactionTemplate;

import java.io.IOException;
import java.io.Reader;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;

@Slf4j
public class JdbcQuestionnaireDatabase extends JdbcBackendDatabase<Questionnaire> implements QuestionnaireDatabase {

  private final FormVersionControlDatabase versionControlDatabase;
  private final String formIdToNameView;

  public JdbcQuestionnaireDatabase(JdbcTemplate jdbcTemplate,
                                   DatabaseHelper databaseHelper,
                                   TransactionTemplate transactionTemplate,
                                   ObjectMapper objectMapper, String schema,
                                   Optional<FormVersionControlDatabase> versionControlDatabase,
                                   Predicate<String> isAnyTenantPredicate)
  {
    super(transactionTemplate, jdbcTemplate, databaseHelper, objectMapper, schema, Constants.QUESTIONNAIRE, Questionnaire.class, isAnyTenantPredicate);
    this.versionControlDatabase = versionControlDatabase.orElse(null);
    this.formIdToNameView = databaseHelper.viewName(schema, "form_id_to_name");
  }

  protected Questionnaire toObject(byte[] oid, int objectRev, String tenantId, byte[] formId, @NonNull String status, Timestamp created, Timestamp updated, Reader reader) {
    try {
      final Questionnaire questionnaire = objectMapper.readValue(reader, Questionnaire.class);
      ImmutableQuestionnaire.Builder builder = ImmutableQuestionnaire.builder()
        .from(questionnaire)
        .id(toId(oid))
        .rev(Integer.toString(objectRev));
      Questionnaire.Metadata metadata = questionnaire.getMetadata();
      builder.metadata(ImmutableQuestionnaireMetadata.builder().from(metadata)
        .created(new Date(created.getTime()))
        .lastAnswer(new Date(updated.getTime()))
        .formId(toId(formId))
        .tenantId(tenantId)
        .status(Questionnaire.Metadata.Status.valueOf(status.trim()))
        .build());
      return builder.build();
    } catch (IOException e) {
      throw new DocumentCorruptedException("Could not read document " + Utils.toString(oid) + ":" + e.getMessage());
    }
  }

  @NonNull
  public Questionnaire findOne(@NonNull String tenantId, @NonNull String id, String rev) {
    LOGGER.debug("{} - findOne questionnaire {} rev {}", tenantId, id, rev);
    Integer revision = Utils.validateRevValue(rev);
    byte[] oid = Utils.toOID(id);
    return doTransaction(template -> {
      RowMapper<Questionnaire> rowMapper = (resultSet, i) -> {
        int objectRev = resultSet.getInt(1);
        String rsTenantId = StringUtils.trim(resultSet.getString(2));
        byte[] formId = databaseHelper.fromJdbcId(resultSet.getBytes(3));
        String status = resultSet.getString(4);
        Timestamp created = resultSet.getTimestamp(5);
        Timestamp updated = resultSet.getTimestamp(6);
        Reader reader = getDatabaseHelper().extractStream(resultSet, 7);
        return toObject(oid, objectRev, rsTenantId, formId, status.trim(), created, updated, reader);
      };
      final StringBuilder sql = new StringBuilder("select rev, tenant_id, form_document_id, status, created, updated, " + getDatabaseHelper().bsonToJson("data") + " from " + tableName + " where id = ?");
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
        return template.queryForObject(sql.toString(), rowMapper, sqlParameters.toArray());
      } catch (EmptyResultDataAccessException e) {
        throw new DocumentNotFoundException(id + " not found");
      }
    });
  }

  @NonNull
  public Questionnaire save(String tenantId, @NonNull Questionnaire document) {
    return doTransaction(template -> {
      final Timestamp timestamp = new Timestamp(System.currentTimeMillis());
      String dId = id(document);
      byte[] oid = Utils.toOID(dId);
      Integer revision = getRevision(document);

      final Questionnaire.Metadata metadata = document.getMetadata();
      byte[] formId = mapToFormDocumentId(tenantId, metadata.getFormId(), metadata.getFormRev());
      String status = metadata.getStatus().toString();
      String owner = metadata.getOwner();
      Questionnaire documentNew = ImmutableQuestionnaire.builder().from(document)
        .metadata(ImmutableQuestionnaireMetadata.builder()
          .from(metadata)
          .formId(Utils.toString(formId))
          .formRev(null)
          .build())
        .build();
      int updated;
      if (isExistingQuestionnaire(oid, revision)) {
        int prevRevision = revision++;
        LOGGER.debug("{} - persisting existing document {} to rev {} -> {}", tenantId, dId, prevRevision, revision);
        documentNew = updatedDocument(documentNew, oid, revision, timestamp, null);
        List<Object> sqlParameters = new ArrayList<>();
        sqlParameters.add(revision);
        sqlParameters.add(status);
        sqlParameters.add(timestamp);
        sqlParameters.add(getDatabaseHelper().jsonObject(objectMapper, documentNew));
        sqlParameters.add(owner);
        sqlParameters.add(toJdbcId(oid));
        sqlParameters.add(prevRevision);
        String where = "";
        if (notAnyTenant(tenantId)) {
          where = " and tenant_id = ?";
          sqlParameters.add(tenantId);
        }
        updated = template.update("update " + tableName + " set rev = ?, status = ?, updated = ?, data = " + getDatabaseHelper().jsonToBson("?") + ", owner = ? where id = ? and rev = ?" + where, sqlParameters.toArray());
      } else {
        revision = 1;
        if (oid == null) {
          oid = Utils.generateOID();
        }
        LOGGER.debug("{} - persisting a new document {} to rev {}", tenantId, dId, revision);
        documentNew = updatedDocument(documentNew, oid, revision, timestamp, tenantId);
        updated = template.update("insert into " + tableName + " (id,rev,tenant_id,form_document_id,status,created,updated,owner,data) values (?,?,?,?,?,?,?,?," + getDatabaseHelper().jsonToBson("?") + ")", toJdbcId(oid), revision, tenantId, toJdbcId(formId), status, timestamp, timestamp, owner, getDatabaseHelper().jsonObject(objectMapper, documentNew));
      }
      if (updated == 0) {
        LOGGER.debug("{} - persisting document {} to rev {} failed (CONFLICT)", tenantId, dId, revision);
        throw new DocumentConflictException(String.format("Conflict during questionnaire document %s rev %d update.", dId, revision));
      }
      return documentNew;
    });
  }

  private boolean isExistingQuestionnaire(byte[] oid, Integer revision) {
    return revision != null && oid != null;
  }

  private byte[] mapToFormDocumentId(String tenantId, @NonNull String formId, @Nullable String formRev) {
    if (versionControlDatabase != null) {
      String id = versionControlDatabase
        .findTag(tenantId, formId, formRev)
        .map(FormTag::getFormId)
        .orElse(formId);
      if (!versionControlDatabase.getFormDatabase().exists(tenantId, formId)) {
        throw new DocumentNotFoundException("Form " + formId + " dot not exists");
      }
      return Utils.toOID(id);
    }
    return Utils.toOID(formId);
  }

  @NonNull
  @Override
  protected Questionnaire updatedDocument(@NonNull Questionnaire document,
                                          @NonNull byte[] oid,
                                          @NonNull Integer revision,
                                          @NonNull Timestamp timestamp,
                                          @Nullable String tenantId)
  {
    ImmutableQuestionnaire.Builder builder = ImmutableQuestionnaire.builder()
      .from(document)
      .id(toId(oid))
      .rev(Integer.toString(revision));
    ImmutableQuestionnaireMetadata.Builder metadataBuilder = ImmutableQuestionnaireMetadata.builder()
      .from(document.getMetadata());
    if (document.getMetadata().getCreated() == null) {
      metadataBuilder.created(new Date(timestamp.getTime()));
    }
    if (tenantId != null) {
      metadataBuilder.tenantId(tenantId);
    }
    builder.metadata(metadataBuilder.build());
    return builder.build();
  }

  @Override
  public void findAllMetadata(String tenantId,
                              String ownerId,
                              String formId,
                              String formName,
                              String formTag,
                              Questionnaire.Metadata.Status status,
                              @NonNull Consumer<MetadataRow> consumer)
  {
    LOGGER.debug("{} - findAllMetadata ownerId = {}, formId = {}, formName = {}, formTag = {}, status = {}", tenantId, ownerId, formId, formName, formTag, status);
    transactionTemplate.execute(transactionStatus -> {
      boolean distinct = false;
      List<String> sqlConditions = new ArrayList<>();
      List<Object> sqlParameters = new ArrayList<>();
      String from = tableName;
      String where = "";
      if (tenantId != null && notAnyTenant(tenantId)) {
        sqlConditions.add(tableName+".tenant_id = ?");
        sqlParameters.add(tenantId);
      }
      if (StringUtils.isNotBlank(formId)) {
          Object id = toJdbcId(Utils.toOID(formId));
          sqlConditions.add("form_document_id = ?");
          sqlParameters.add(id);
      }
      if (StringUtils.isNotBlank(formName)) {
        from = from + " inner join " + formIdToNameView + " fid on form_document_id = fid.id and fid.tenant_id = " + tableName + ".tenant_id";
        sqlConditions.add("fid.name = ? and fid.deleted is null");
        sqlParameters.add(formName);
        if (StringUtils.isNotBlank(formTag)) {
          sqlConditions.add("fid.label = ?");
          sqlParameters.add(formTag);
        } else {
          // Or we'll get row per tag
          distinct = true;
        }
      }

      if (StringUtils.isNotBlank(ownerId)) {
        sqlConditions.add("owner = ?");
        sqlParameters.add(ownerId);
      }
      if (status != null) {
        sqlConditions.add("status = ?");
        sqlParameters.add(status.name());
      }
      if (!sqlConditions.isEmpty()) {
        where = " where " + StringUtils.join(sqlConditions, " and ");
      }
      metadataQuery(distinct, from, where, sqlParameters, consumer);
      return null;
    });
  }

  @Override
  public MetadataRow findMetadata(String tenantId, String questionnaireId) {
    return doTransaction(template -> {
      List<Object> sqlParameters = new ArrayList<>();
      String where = " where id = ?";
      sqlParameters.add(toJdbcId(Utils.toOID(questionnaireId)));
      if (tenantId != null && notAnyTenant(tenantId)) {
        where += " and tenant_id = ?";
        sqlParameters.add(tenantId);
      }
      MutableObject<MetadataRow> metadata = new MutableObject<>();
      metadataQuery(false, tableName, where, sqlParameters, metadata::setValue);
      if (metadata.getValue() == null) {
        throw new DocumentNotFoundException(questionnaireId + " not found");
      }
      return metadata.getValue();
    });
  }


  private void metadataQuery(boolean distinct, String from, String where, List<Object> sqlParameters, @NonNull Consumer<MetadataRow> consumer) {
    String select = distinct ? "select distinct " : "select ";
    jdbcTemplate.query(select + tableName+".tenant_id, "+tableName+".id, "+tableName+".form_document_id, "+tableName+".status, "+tableName+".created, "+tableName+".updated, "+tableName+".owner from " + from + where, resultSet -> {
      String tId = StringUtils.trim(resultSet.getString(1));
      byte[] idBytes = databaseHelper.fromJdbcId(resultSet.getBytes(2));
      byte[] formIdBytes = databaseHelper.fromJdbcId(resultSet.getBytes(3));
      Questionnaire.Metadata.Status status = Questionnaire.Metadata.Status.valueOf(resultSet.getString(4).trim());
      Timestamp created = resultSet.getTimestamp(5);
      Timestamp updated = resultSet.getTimestamp(6);
      String owner = resultSet.getString(7);
      consumer.accept(ImmutableMetadataRow.of(toId(idBytes), ImmutableQuestionnaireMetadata.builder()
        .created(new Date(created.getTime()))
        .lastAnswer(updated)
        .formId(toId(formIdBytes))
        .status(status)
        .owner(owner)
        .tenantId(tId)
        .build()));
    }, sqlParameters.toArray());
  }

  @NonNull
  @Override
  protected Questionnaire updateDocumentId(@NonNull Questionnaire document, String id) {
    return ImmutableQuestionnaire.builder().from(document).id(id).build();
  }

  @NonNull
  @Override
  protected Questionnaire updateDocumentRev(@NonNull Questionnaire document, String rev) {
    return ImmutableQuestionnaire.builder().from(document).rev(rev).build();
  }

}
