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
import io.dialob.api.form.Form;
import io.dialob.api.form.ImmutableFormMetadata;
import io.dialob.db.spi.spring.AbstractDocumentDatabase;
import io.dialob.form.service.api.FormDatabase;
import io.dialob.questionnaire.service.api.QuestionnaireDatabase;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Hex;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.lang.NonNull;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Nonnull;
import java.sql.Timestamp;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import static java.util.Objects.requireNonNull;

@Slf4j
public abstract class JdbcBackendDatabase<T,M> extends AbstractDocumentDatabase<T> implements JdbcDatabase {

  protected final String tableName;

  protected final JdbcTemplate jdbcTemplate;

  protected final TransactionTemplate transactionTemplate;

  protected final ObjectMapper objectMapper;

  protected final DatabaseHelper databaseHelper;

  protected final Predicate<String> isAnyTenantPredicate;

  public JdbcBackendDatabase(TransactionTemplate transactionTemplate,
                             JdbcTemplate jdbcTemplate,
                             DatabaseHelper databaseHelper,
                             ObjectMapper objectMapper,
                             String schema,
                             String tableName,
                             Class<? extends T> documentClass,
                             Predicate<String> isAnyTenantPredicate)
  {
    super(documentClass);
    this.transactionTemplate = transactionTemplate;
    this.databaseHelper = databaseHelper;
    this.objectMapper = objectMapper;
    this.jdbcTemplate = jdbcTemplate;
    this.tableName = databaseHelper.tableName(schema, tableName);
    this.isAnyTenantPredicate = requireNonNull(isAnyTenantPredicate);
  }

  @Override
  public DatabaseHelper getDatabaseHelper() {
    return databaseHelper;
  }

  public abstract T findOne(@NonNull String tenantId, @NonNull String id, String rev);

  protected String toId(byte[] oid) {
    return Hex.encodeHexString(oid);
  }

  protected boolean notAnyTenant(String tenantId) {
    return !isAnyTenantPredicate.test(tenantId);
  }

  @NonNull
  public T findOne(String tenantId, @NonNull String id) {
    return findOne(tenantId, id, null);
  }

  public boolean exists(@NonNull String tenantId, @NonNull String id) {
    byte[] oid = Utils.toOID(id);
    return doTransaction(template -> {
      Integer count;
      if (notAnyTenant(tenantId)) {
        count = template.queryForObject("select count(*) from " + tableName + " where id = ? and tenant_id = ?", Integer.class, toJdbcId(oid), tenantId);
      } else {
        count = template.queryForObject("select count(*) from " + tableName + " where id = ?", Integer.class, toJdbcId(oid));
      }
      return count > 0;
    });
  }

  public boolean delete(@NonNull String tenantId, @NonNull String id) {
    byte[] oid = Utils.toOID(id);
    return doTransaction(template -> {
      if (notAnyTenant(tenantId)) {
        return template.update("delete from " + tableName + " where id = ? and tenant_id = ?", toJdbcId(oid), tenantId);
      } else {
        return template.update("delete from " + tableName + " where id = ?", toJdbcId(oid));
      }
    }) > 0;
  }

  @NonNull
  public abstract T save(String tenantId, @NonNull T document);

  protected Integer getRevision(@NonNull T document) {
    String rev = rev(document);
    if (rev == null) {
      return null;
    }
    return Integer.parseInt(rev);
  }

  @NonNull
  protected abstract T updatedDocument(@NonNull T document, @NonNull byte[] oid, @NonNull Integer revision, @NonNull Timestamp timestamp, String tenantId);

  protected <R> R doTransaction(Function<JdbcTemplate,R> operation) {
    return transactionTemplate.execute(status -> operation.apply(this.jdbcTemplate));
  }


}
