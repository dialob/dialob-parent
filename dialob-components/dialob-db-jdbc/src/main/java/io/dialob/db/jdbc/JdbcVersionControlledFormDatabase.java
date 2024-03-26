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
package io.dialob.db.jdbc;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.umd.cs.findbugs.annotations.NonNull;
import io.dialob.api.form.*;
import io.dialob.db.spi.exceptions.*;
import io.dialob.form.service.api.FormDatabase;
import io.dialob.form.service.api.FormVersionControlDatabase;
import io.dialob.form.service.api.ImmutableFormMetadataRow;
import org.apache.commons.lang3.StringUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.lang.Nullable;
import org.springframework.transaction.support.TransactionTemplate;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

public class JdbcVersionControlledFormDatabase implements FormDatabase, FormVersionControlDatabase, JdbcDatabase {

  public static final String LATEST = "LATEST";

  protected final JdbcTemplate jdbcTemplate;

  protected final TransactionTemplate transactionTemplate;

  protected final FormDatabase formDatabase;

  protected String formTableName;

  protected String formRevTableName;

  protected final DatabaseHelper databaseHelper;

  protected final Predicate<String> isAnyTenantPredicate;

  protected final ObjectMapper objectMapper;

  private final RowMapper<FormTag> formTagRowMapper = (rs, rowNum) -> (FormTag) ImmutableFormTag.builder()
    .formName(rs.getString(1))
    .name(rs.getString(2))
    .description(rs.getString(3))
    .created(rs.getTimestamp(4))
    .formId(Utils.toString(getDatabaseHelper().fromJdbcId(rs.getBytes(5))))
    .type(FormTag.Type.valueOf(rs.getString(6).trim()))
    .refName(rs.getString(7))
    .build();

  public JdbcVersionControlledFormDatabase(JdbcTemplate jdbcTemplate,
                                           String schema,
                                           DatabaseHelper databaseHelper,
                                           TransactionTemplate transactionTemplate,
                                           FormDatabase formDatabase,
                                           Predicate<String> isAnyTenantPredicate, ObjectMapper objectMapper)
  {
    this.jdbcTemplate = requireNonNull(jdbcTemplate);
    this.databaseHelper = requireNonNull(databaseHelper);
    this.transactionTemplate = requireNonNull(transactionTemplate);
    this.formDatabase = requireNonNull(formDatabase);
    this.formTableName = this.databaseHelper.tableName(schema, "form");
    this.formRevTableName = this.databaseHelper.tableName(schema, "form_rev");
    this.isAnyTenantPredicate = requireNonNull(isAnyTenantPredicate);
    this.objectMapper = objectMapper;
  }

  private boolean notAnyTenant(String tenantId) {
    return !isAnyTenantPredicate.test(tenantId);
  }

  private void assertTenantContextDefined(String tenantId) {
    if (tenantId == null) {
      throw new TenantContextRequiredException("Service requires tenant context.");
    }
  }

  public boolean formNameExists(String tenantId, String formName) {
    assertTenantContextDefined(tenantId);
    return doTransaction(template -> {
      Integer count = template.queryForObject("select count(*) from " + formTableName + " where name = ? and tenant_id = ?", Integer.class, formName, tenantId);
      return count != null && count > 0;
    });
  }

  public boolean isFormDocumentTagged(String tenantId, String formDocumentId) {
    assertTenantContextDefined(tenantId);
    return doTransaction(template -> {
      Integer count = template.queryForObject("select count(*) from " + formRevTableName + " where form_document_id = ? and tenant_id = ?", Integer.class, toJdbcId(Utils.toOID(formDocumentId)), tenantId);
      return count != null && count > 0;
    });
  }

  public boolean createControlledForm(String tenantId, String formName, String formDocumentId, String label) {
    assertTenantContextDefined(tenantId);
    return doTransaction(template -> {
      byte[] oid = Utils.toOID(formDocumentId);
      int updated;
      updated = template.update("insert into " + formTableName + " (tenant_id,name,latest_form_id,label) values (?,?,?,?)", tenantId, formName, toJdbcId(oid), label);
      return updated > 0;
    });
  }

  @Override
  public Optional<FormTag> createTag(@NonNull String tenantId, @NonNull String formName, String newTag, String description, String formDocumentIdOrRefName, @NonNull FormTag.Type type) {
    assertTenantContextDefined(tenantId);
    try {
      String resolvedFormDocumentId = null;
      String resolvedRefName = null;

      if (type == FormTag.Type.NORMAL) {
        resolvedFormDocumentId = formDocumentIdOrRefName;
        resolvedRefName = null;
        if (!exists(tenantId, resolvedFormDocumentId)){
          return Optional.empty();
        }
      } else {
        resolvedRefName = formDocumentIdOrRefName;
        resolvedFormDocumentId = findTag(tenantId, formName, resolvedRefName).map(FormTag::getFormId).orElse(null);
        if (resolvedFormDocumentId == null) {
          return Optional.empty();
        }
      }
      String formDocumentId = resolvedFormDocumentId;
      String refName = resolvedRefName;
      return doTransaction(template -> {
        List<Object> sqlParameters  = new ArrayList<>();
        sqlParameters.add(newTag);
        sqlParameters.add(description);
        sqlParameters.add(toJdbcId(Utils.toOID(formDocumentId)));
        sqlParameters.add(type.name());
        sqlParameters.add(refName);

        StringBuilder where = new StringBuilder(" where name = ?");
        sqlParameters.add(formName);
        where.append(" and tenant_id = ?");
        sqlParameters.add(tenantId);


        int updated = template.update("insert into " + formRevTableName + " (tenant_id,form_name, name, description, form_document_id, type, ref_name) select tenant_id, name, ?, ?, ?, ?, ? from " + formTableName + where.toString(), sqlParameters.toArray());
        if (updated > 0) {
          return findTag(tenantId, formName, newTag);
        }
        return Optional.empty();
      });
    } catch (DuplicateKeyException exception) {
      throw new DocumentConflictException("Form \"" + formName + "\" tag \"" + newTag + "\" exists already.");
    }
  }

  @Override
  public Optional<FormTag> createTagOnLatest(String tenantId, @NonNull String formName, String tag, String description, boolean snapshot) {
    assertTenantContextDefined(tenantId);
    if (snapshot) {
      String latestFormId = findTag(tenantId, formName, LATEST).map(FormTag::getFormId).orElse(null);
      if (latestFormId == null) {
        return Optional.empty();
      }
      latestFormId = createSnapshot(tenantId, latestFormId);
      return createTag(tenantId, formName, tag, description, latestFormId, FormTag.Type.NORMAL);
    }
    try {
      return doTransaction(template -> {
        List<Object> sqlParameters  = new ArrayList<>();
        sqlParameters.add(tag);
        sqlParameters.add(description);
        sqlParameters.add(FormTag.Type.NORMAL.name());

        StringBuilder where = new StringBuilder(" where name = ?");
        sqlParameters.add(formName);
        where.append(" and tenant_id = ?");
        sqlParameters.add(tenantId);
        int updated = template.update("insert into " + formRevTableName + " (tenant_id, form_name, name, description, type, form_document_id) select tenant_id, name, ?, ?, ?, latest_form_id from " + formTableName + where, sqlParameters.toArray());
        if (updated > 0) {
          return findTag(tenantId, formName, tag);
        }
        return Optional.empty();
      });
    } catch (DataIntegrityViolationException exception) {
      throw new DocumentConflictException("Form \"" + formName + "\" tag \"" + tag + "\" exists already.");
    }
  }


  @Override
  public boolean deleteTag(String tenantId, @NonNull String formName, String tag) {
    assertTenantContextDefined(tenantId);
    return doTransaction(template -> {
      final int updated = template.update("delete from " + formRevTableName + " where name = ? and form_name = ? and tenant_id = ? and type = 'MUTABLE'", tag, formName, tenantId);
      return updated > 0;
    });
  }

  @Override
  public boolean updateLabel(String tenantId, @NonNull String formName, String label) {
    assertTenantContextDefined(tenantId);
    return doTransaction(template -> {
      int updated = template.update("update " + formTableName + " set label = ?, updated = current_timestamp where name = ? and tenant_id = ?", label, formName, tenantId);
      return updated > 0;
    });
  }

  @Override
  public String createSnapshot(String tenantId, @NonNull String formId) {
    assertTenantContextDefined(tenantId);
    Form form = getFormDatabase().findOne(tenantId, formId);
    // bypass save validation
    form = formDatabase.save(tenantId, ImmutableForm.builder().from(form).id(null).rev(null).build());
    return form.getId();
  }

  @Override
  public boolean updateLatest(String tenantId, @NonNull String formId, @NonNull FormTag tag) {
    assertTenantContextDefined(tenantId);
    if (!LATEST.equalsIgnoreCase(tag.getName()) || tag.getFormId() == null) {
      return false;
    }
    if (!getFormDatabase().exists(tenantId, tag.getFormId())) {
      return false;
    }
    return doTransaction(template -> {
      byte[] oid = Utils.toOID(tag.getFormId());
      int updated = template.update("update " + formTableName + " set latest_form_id = ?, updated = current_timestamp where name = ? and tenant_id = ?", toJdbcId(oid), formId, tenantId);
      return updated > 0;
    });
  }

  @Override
  public boolean isName(String tenantId, @NonNull String formId) {
    assertTenantContextDefined(tenantId);
    return doTransaction(template -> {
      Integer count = template.queryForObject("select count(*) from " + formTableName + " where name = ? and tenant_id = ?", Integer.class, formId, tenantId);
      return count != null && count> 0;
    });
  }

  @NonNull
  @Override
  public List<FormTag> findTags(String tenantId, @NonNull String formId, FormTag.Type type) {
    assertTenantContextDefined(tenantId);
    return doTransaction(template -> {
      byte[] oid = null;
      try {
        oid = Utils.toOID(formId);
      } catch (Exception e) {
        // no oids here
      }
      List<Object> sqlParameters  = new ArrayList<>();
      String where = "form_name = ?";
      sqlParameters.add(formId);
      if (oid != null) {
        where = where + " or form_document_id = ?";
        sqlParameters.add(toJdbcId(oid));
      }
      where = "(" + where + ")";
      if (type != null) {
        where = where + " and type = ?";
        sqlParameters.add(type.name());
      }
      where = where + " and tenant_id = ?";
      sqlParameters.add(tenantId);

      return template.query("select form_name, name, description, created, form_document_id, type, ref_name from " + formRevTableName + " where " + where, formTagRowMapper, sqlParameters.toArray());
    });
  }

  @Override
  public Optional<FormTag> findTag(String tenantId, @NonNull String formName, @Nullable String name) {
    assertTenantContextDefined(tenantId);
    return doTransaction(template -> {
      try {
        // tags are unique within tenant, not globally
        if (name == null || name.equalsIgnoreCase(LATEST)) {
          return Optional.ofNullable(template.queryForObject("select name, 'LATEST', null, created, latest_form_id, 'NORMAL', null from " + formTableName + " where name = ? and tenant_id = ?", formTagRowMapper, formName, tenantId));
        } else {
          return Optional.ofNullable(template.queryForObject("select form_name, name, description, created, form_document_id, type, ref_name from " + formRevTableName + " where form_name = ? and name = ? and tenant_id = ?", formTagRowMapper, formName, name, tenantId));
        }
      } catch (EmptyResultDataAccessException e) {
        return Optional.empty();
      }
    });
  }


  @NonNull
  @Override
  public List<FormTag> queryTags(String tenantId, String formName, String formId, String name, FormTag.Type type) {
    assertTenantContextDefined(tenantId);
    return doTransaction(template -> {
      List<Object> params = new ArrayList<>();
      List<String> terms = new ArrayList<>();
      if (isNotBlank(formName)) {
        params.add(formName);
        terms.add("form_name = ?");
      }
      if (isNotBlank(formId)) {
        params.add(toJdbcId(Utils.toOID(formId)));
        terms.add("form_document_id = ?");
      }
      if (isNotBlank(name)) {
        params.add(name);
        terms.add("name = ?");
      }
      if (type != null) {
        params.add(type.name());
        terms.add("type = ?");
      }
      terms.add("tenant_id = ?");
      params.add(tenantId);
      String where = "";
      if (!terms.isEmpty()) {
        where = " where " + String.join(" and ", terms);
      }
      return template.query("select form_name, name, description, created, form_document_id, type, ref_name from " + formRevTableName + where, formTagRowMapper, params.toArray(new Object[0]));
    });
  }

  @Override
  public Optional<FormTag> moveTag(String tenantId, FormTag updateTag) {
    assertTenantContextDefined(tenantId);
    if (StringUtils.isBlank(updateTag.getRefName())) {
      return Optional.empty();
    }
    return doTransaction(template -> findTag(tenantId, updateTag.getFormName(), updateTag.getRefName()).map(tag -> {
      // Can make ref only to normal tag
      if (tag.getType() != FormTag.Type.NORMAL) {
        throw new DocumentCorruptedException(String.format("Referred tag must be immutable", updateTag.getFormName(), updateTag.getName()));
      }
      int count = template.update("update " + formRevTableName + " set updated = current_timestamp, form_document_id = ?, ref_name = ?, description = ? where type = 'MUTABLE' and form_name = ? and name = ? and tenant_id = ?",
        toJdbcId(Utils.toOID(tag.getFormId())),
        tag.getName(),
        updateTag.getDescription(),
        updateTag.getFormName(),
        updateTag.getName(),
        tenantId);
      if (count == 0) {
        // Insert??
        throw new DocumentNotFoundException(String.format("Form %s mutable tag %s not found", updateTag.getFormName(), updateTag.getName()));
      }
      if (count > 1) {
        throw new DocumentConflictException(String.format("Form %s tag %s is not unique", updateTag.getFormName(), updateTag.getName()));
      }
      return ImmutableFormTag.builder().from(updateTag).formId(tag.getFormId()).refName(tag.getName()).build();
    }));

  }
  public String findFormDocumentId(String tenantId, String formName, String tag) {
    assertTenantContextDefined(tenantId);
    if (tag != null && !LATEST.equals(tag)) {
      return doTransaction(template -> {
        try {
          Object oid;
          oid = template.queryForObject("select form_document_id from " + formRevTableName + " where form_name = ? and name = ? and tenant_id = ?", byte[].class, formName, tag, tenantId);
          return Utils.toString(getDatabaseHelper().fromJdbcId(oid));
        } catch (EmptyResultDataAccessException e) {
          return formName;
        }
      });
    } else {
      return doTransaction(template -> {
        try {
          Object oid;
          oid = template.queryForObject("select latest_form_id from " + formTableName + " where name = ? and tenant_id = ?", byte[].class, formName, tenantId);
          return Utils.toString(getDatabaseHelper().fromJdbcId(oid));
        } catch (EmptyResultDataAccessException e) {
          return formName;
        }
      });
    }
  }

  @NonNull
  @Override
  public FormDatabase getFormDatabase() {
    return this;
  }

  @NonNull
  @Override
  public Form findOne(@NonNull String tenantId, @NonNull String id, String rev) {
    if (StringUtils.isBlank(rev)) {
      return findOne(tenantId, id);
    }
    return formDatabase.findOne(tenantId, findFormDocumentId(tenantId, id, rev));
  }

  @NonNull
  @Override
  public Form findOne(@NonNull String tenantId, @NonNull String id) {
    return formDatabase.findOne(tenantId, findFormDocumentId(tenantId, id, null));
  }

  @Override
  public boolean exists(@NonNull String tenantId, @NonNull String id) {
    assertTenantContextDefined(tenantId);
    boolean exists = doTransaction(template -> {
      Integer count = template.queryForObject("select count(*) from " + formTableName + " where name = ? and tenant_id = ?", Integer.class, id, tenantId);
      return count != null && count > 0;
    });
    if (!exists) {
      return formDatabase.exists(tenantId, id);
    }
    return true;
  }

  @Override
  public boolean delete(String tenantId, @NonNull String id) {
    assertTenantContextDefined(tenantId);
    return doTransaction(template -> {
      int count = template.update("delete from " + formTableName + " where name = ? and tenant_id = ?", id, tenantId);
      return count > 0;
    });
  }

  @NonNull
  @Override
  public Form save(String tenantId, @NonNull Form document) {
    if (isNotBlank(document.getId())) {
      if (!isFormDocumentTagged(tenantId, document.getId())) {
        document = formDatabase.save(tenantId, document);
        updateLabel(tenantId, document.getName(), document.getMetadata().getLabel());
        return document;
      }
      throw new DocumentLockedException("form " + document.getId() + " is not editable");
    } else {
      validateNewForm(tenantId, document);
      document = formDatabase.save(tenantId, document);
      createControlledForm(tenantId, document.getName(), document.getId(), document.getMetadata().getLabel());
      return document;
    }
  }

  @Override
  public void findAllMetadata(String tenantId, Form.Metadata metadata, @NonNull Consumer<FormMetadataRow> consumer) {
    doTransaction(template -> {
      List<Object> params = new ArrayList<>();
      List<String> conditions = new ArrayList<>();
      if (notAnyTenant(tenantId)) {
        conditions.add("tenant_id = ?");
        params.add(tenantId);
      }
      if (metadata != null) {
        conditions.add("data->'metadata' @> ?");
        params.add(getDatabaseHelper().jsonObject(objectMapper, metadata));
      }
      String where = conditions.stream().collect(Collectors.joining(" and "));
      if (StringUtils.isNotBlank(where)) {
        where = " where " + where;
      }
      template.query("select tenant_id, name, created, updated, label from " + formTableName + where, rs -> {
        while(rs.next()) {
          String tId = rs.getString(1);
          String name = rs.getString(2);
          Timestamp created = rs.getTimestamp(3);
          Timestamp updated = rs.getTimestamp(4);
          String label = rs.getString(5);
          consumer.accept(ImmutableFormMetadataRow.of(name, ImmutableFormMetadata.builder()
            .created(new Date(created.getTime()))
            .lastSaved(new Date(updated.getTime()))
            .label(label)
            .tenantId(tId)
            .build()));
        }
        return null;
      }, params.toArray());
      return null;
    });
  }

  protected <R> R doTransaction(Function<JdbcTemplate, R> operation) {
    return transactionTemplate.execute(status -> operation.apply(this.jdbcTemplate));
  }

  @Override
  public DatabaseHelper getDatabaseHelper() {
    return databaseHelper;
  }

  private void validateNewForm(String tenantId, Form document) {
    if (isBlank(document.getName())) {
      throw new DocumentCorruptedException("form.name is required field.");
    }
    if (document.getName().length() > 128) {
      throw new DocumentCorruptedException("form.name is too long.");
    }
    if (!document.getName().matches("^[_\\-a-zA-Z\\d]+$")) {
      throw new DocumentCorruptedException("form.name is not valid.");
    }
    if (formNameExists(tenantId, document.getName())) {
      throw new DocumentConflictException("form with name " + document.getName() + " exists already.");
    }
  }
}
