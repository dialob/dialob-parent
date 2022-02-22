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

import static io.dialob.api.questionnaire.QuestionnaireFactory.questionnaire;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Timestamp;

import javax.sql.DataSource;

import org.apache.commons.codec.binary.Hex;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.lang.NonNull;
import org.springframework.transaction.support.TransactionTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.dialob.api.questionnaire.ImmutableQuestionnaire;
import io.dialob.api.questionnaire.ImmutableQuestionnaireMetadata;
import io.dialob.api.questionnaire.Questionnaire;
import io.dialob.db.spi.exceptions.DocumentConflictException;
import io.dialob.db.spi.exceptions.DocumentNotFoundException;
import io.dialob.security.tenant.CurrentTenant;
import io.dialob.security.tenant.FixedCurrentTenant;

public abstract class JdbcBackendDatabaseTest {

  ObjectMapper objectMapper = new ObjectMapper();
  String tenantId = "1230";

  JdbcBackendDatabase jdbcBackendDatabase(TransactionTemplate transactionTemplate, JdbcTemplate jdbcTemplate, DatabaseHelper databaseHelper, ObjectMapper objectMapper, String schema) {
    return jdbcBackendDatabase(transactionTemplate, new FixedCurrentTenant(""), jdbcTemplate, databaseHelper, objectMapper, schema);
  }

  abstract JdbcBackendDatabase jdbcBackendDatabase(TransactionTemplate transactionTemplate, @NonNull CurrentTenant currentTenant, JdbcTemplate jdbcTemplate, DatabaseHelper databaseHelper, ObjectMapper objectMapper, String schema);

  @Test
  public void shouldThrowDocumentNotFoundExceptionWhenDocumentIsNotFound() throws Exception {
    JdbcTemplate jdbcTemplate = Mockito.mock(JdbcTemplate.class);
    DataSource dataSource = Mockito.mock(DataSource.class);
    Connection connection = Mockito.mock(Connection.class);
    when(jdbcTemplate.getDataSource()).thenReturn(dataSource);
    when(dataSource.getConnection()).thenReturn(connection);
    when(jdbcTemplate.queryForObject(eq("select rev, tenant_id, form_document_id, status, created, updated, data from dialob.questionnaire where id = ? and tenant_id = ?"), eq(new Object[] {new byte[] {0x12, 0x30, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00}, ""}), any(RowMapper.class)))
      .thenThrow(EmptyResultDataAccessException.class);

    JdbcBackendDatabase jdbcBackendDatabase = jdbcBackendDatabase(new TransactionTemplate(new DataSourceTransactionManager(dataSource)), jdbcTemplate, databaseHandler(), objectMapper, "dialob");


    //
    Assertions.assertThatThrownBy(() -> jdbcBackendDatabase.findOne("", "1230", null)).isInstanceOf(DocumentNotFoundException.class);

    //
    verify(jdbcTemplate).queryForObject(eq("select rev, tenant_id, form_document_id, status, created, updated, data from dialob.questionnaire where id = ? and tenant_id = ?"), eq(new Object[] {new byte[] {0x12, 0x30, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00}, ""}), any(RowMapper.class));
    verify(connection).rollback();
    verifyNoMoreInteractions(jdbcTemplate);
  }

  private DatabaseHelper databaseHandler() {
    return new DatabaseHelper() {
      @Override
      public String getSchema() {
        return null;
      }

      @Override
      public String jsonContains(String path) {
        return "data->'" + path + "' @> ?";
      }
    };
  }

  @Test
  public void shouldReturnFoundObject() throws Exception {
    JdbcTemplate jdbcTemplate = Mockito.mock(JdbcTemplate.class);
    DataSource dataSource = Mockito.mock(DataSource.class);
    Connection connection = Mockito.mock(Connection.class);
    when(jdbcTemplate.getDataSource()).thenReturn(dataSource);
    when(dataSource.getConnection()).thenReturn(connection);
    JdbcBackendDatabase jdbcBackendDatabase = jdbcBackendDatabase(new TransactionTemplate(new DataSourceTransactionManager(dataSource)), jdbcTemplate, databaseHandler(), objectMapper, "dialob");

    Timestamp timestamp = new Timestamp(1000);
    final ResultSet resultSet = Mockito.mock(ResultSet.class);
    when(resultSet.getInt(1)).thenReturn(3);
    when(resultSet.getString(2)).thenReturn(null);
    when(resultSet.getBytes(3)).thenReturn(new byte[] {0x12, 0x30, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00});
    when(resultSet.getString(4)).thenReturn("NEW");
    when(resultSet.getTimestamp(5)).thenReturn(timestamp);
    when(resultSet.getTimestamp(6)).thenReturn(timestamp);
    when(resultSet.getBinaryStream(7)).thenReturn(new ByteArrayInputStream("{\"_id\":\"1230\",\"_rev\":\"2\",\"metadata\":{\"formId\":\"shouldReturnFoundObject\"}}".getBytes()));

    doAnswer(invocation -> {
      RowMapper rowMapper = invocation.getArgument(2);
      return rowMapper.mapRow(resultSet, 1);
    }).when(jdbcTemplate).queryForObject(eq("select rev, tenant_id, form_document_id, status, created, updated, data from dialob.questionnaire where id = ? and tenant_id = ?"), eq(new Object[] {new byte[] {0x12, 0x30, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00}, ""}), any(RowMapper.class));
    //
    Object document = jdbcBackendDatabase.findOne("", "1230", null);

    Assertions.assertThat(document).extracting("id","rev").containsExactly(
      "12300000000000000000000000000000", "3");

    //
    verify(jdbcTemplate).queryForObject(eq("select rev, tenant_id, form_document_id, status, created, updated, data from dialob.questionnaire where id = ? and tenant_id = ?"), eq(new Object[] {new byte[] {0x12, 0x30, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00}, ""}), any(RowMapper.class));
    verify(connection).commit();
    verifyNoMoreInteractions(jdbcTemplate);
  }

  @Test
  public void shouldSaveDocumentAndGiveIdAndRevisionOne() throws Exception {
    JdbcTemplate jdbcTemplate = Mockito.mock(JdbcTemplate.class);
    DataSource dataSource = Mockito.mock(DataSource.class);
    Connection connection = Mockito.mock(Connection.class);
    when(jdbcTemplate.getDataSource()).thenReturn(dataSource);
    when(dataSource.getConnection()).thenReturn(connection);
    JdbcBackendDatabase jdbcBackendDatabase = jdbcBackendDatabase(new TransactionTemplate(new DataSourceTransactionManager(dataSource)), jdbcTemplate, databaseHandler(), objectMapper, "dialob");

    Timestamp timestamp = new Timestamp(1000);

    doReturn(1).when(jdbcTemplate).update(
      eq("insert into dialob.questionnaire (id,rev,tenant_id,form_document_id,status,created,updated,owner,data) values (?,?,?,?,?,?,?,?,?)"),
      new Object[] {any(byte[].class), eq(1), eq(""), any(byte[].class), any(String.class), any(Timestamp.class), any(Timestamp.class), isNull(), any(InputStream.class)});

    Questionnaire questionnaire = questionnaire(null, Hex.encodeHexString(new byte[] {0x12, 0x30, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00}));

    Object document = jdbcBackendDatabase.save("", questionnaire);

    Assertions.assertThat(document).extracting("id","rev").matches(objects -> {
        return ((String)objects.get(0)).length() == 32 && "1".equals(objects.get(1));
      });

    //
    verify(jdbcTemplate).update(eq("insert into dialob.questionnaire (id,rev,tenant_id,form_document_id,status,created,updated,owner,data) values (?,?,?,?,?,?,?,?,?)"), any(byte[].class), eq(1), eq(""), any(byte[].class), any(String.class), any(Timestamp.class), any(Timestamp.class), isNull(), any(InputStream.class));
    verify(connection).commit();
    verifyNoMoreInteractions(jdbcTemplate);
  }



  @Test
  public void shouldSaveDocumentAndIncreaseRevision() throws Exception {
    JdbcTemplate jdbcTemplate = Mockito.mock(JdbcTemplate.class);
    DataSource dataSource = Mockito.mock(DataSource.class);
    Connection connection = Mockito.mock(Connection.class);
    when(jdbcTemplate.getDataSource()).thenReturn(dataSource);
    when(dataSource.getConnection()).thenReturn(connection);
    JdbcBackendDatabase jdbcBackendDatabase = jdbcBackendDatabase(new TransactionTemplate(new DataSourceTransactionManager(dataSource)), jdbcTemplate, databaseHandler(), objectMapper, "dialob");

    Timestamp timestamp = new Timestamp(1000);

    doReturn(1).when(jdbcTemplate).update(
      eq("update dialob.questionnaire set rev = ?, status = ?, updated = ?, data = ?, owner = ? where id = ? and rev = ? and tenant_id = ?"),
      new Object[] {eq(13), any(String.class), any(Timestamp.class), any(InputStream.class), isNull(), eq(new byte[] {0x12, 0x30, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00}), eq(12), eq("")});

    Questionnaire questionnaire = ImmutableQuestionnaire.builder()
      .id("12300000000000000000000000000000")
      .rev("12")
      .metadata(ImmutableQuestionnaireMetadata.builder().formId(Hex.encodeHexString(new byte[] {0x12, 0x30, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00})).build())
      .build();

    Object document = jdbcBackendDatabase.save("", questionnaire);
    Assertions.assertThat(document).extracting("id","rev").containsExactly(
      "12300000000000000000000000000000","13");

    verify(jdbcTemplate).update(
      eq("update dialob.questionnaire set rev = ?, status = ?, updated = ?, data = ?, owner = ? where id = ? and rev = ? and tenant_id = ?"),
      new Object[] {eq(13), any(String.class), any(Timestamp.class), any(InputStream.class), isNull(), eq(new byte[] {0x12, 0x30, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00}), eq(12), eq("")});
    verify(connection).commit();
    verifyNoMoreInteractions(jdbcTemplate);
  }

  @Test
  public void shouldThrowConflictOnRevisionConflict() throws Exception {
    JdbcTemplate jdbcTemplate = Mockito.mock(JdbcTemplate.class);
    DataSource dataSource = Mockito.mock(DataSource.class);
    Connection connection = Mockito.mock(Connection.class);
    when(jdbcTemplate.getDataSource()).thenReturn(dataSource);
    when(dataSource.getConnection()).thenReturn(connection);
    JdbcBackendDatabase jdbcBackendDatabase = jdbcBackendDatabase(new TransactionTemplate(new DataSourceTransactionManager(dataSource)), jdbcTemplate, databaseHandler(), objectMapper, "dialob");

    Timestamp timestamp = new Timestamp(1000);

    doReturn(0).when(jdbcTemplate).update(
      eq("update dialob.questionnaire set rev = ?, updated = ?, label = ?, data = ? where id = ? and rev = ? and tenant_id = ?"),
      new Object[] {eq(13), any(Timestamp.class), any(String.class), any(InputStream.class), eq(new byte[] {0x12, 0x30, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00}), eq(12)});

    Questionnaire questionnaire = ImmutableQuestionnaire.builder()
      .id("12300000000000000000000000000000")
      .rev("12")
      .metadata(ImmutableQuestionnaireMetadata.builder()
        .formId(Hex.encodeHexString(new byte[] {0x12, 0x30, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00}))
        .owner("me")
        .build())
      .build();

    Assertions.assertThatThrownBy(() -> jdbcBackendDatabase.save("", questionnaire)).isInstanceOf(DocumentConflictException.class);

    verify(jdbcTemplate).update(eq("update dialob.questionnaire set rev = ?, status = ?, updated = ?, data = ?, owner = ? where id = ? and rev = ? and tenant_id = ?"), eq(13), any(String.class), any(Timestamp.class), any(InputStream.class), eq("me"), eq(new byte[] {0x12, 0x30, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00}), eq(12), eq(""));
    verify(connection).rollback();
    verifyNoMoreInteractions(jdbcTemplate);
  }

  @Test
  public void shouldDelete() throws Exception {
    JdbcTemplate jdbcTemplate = Mockito.mock(JdbcTemplate.class);
    DataSource dataSource = Mockito.mock(DataSource.class);
    Connection connection = Mockito.mock(Connection.class);
    when(jdbcTemplate.getDataSource()).thenReturn(dataSource);
    when(dataSource.getConnection()).thenReturn(connection);
    JdbcBackendDatabase jdbcBackendDatabase = jdbcBackendDatabase(new TransactionTemplate(new DataSourceTransactionManager(dataSource)), jdbcTemplate, databaseHandler(), objectMapper, "dialob");

    Timestamp timestamp = new Timestamp(1000);

    doReturn(1).when(jdbcTemplate).update(
      eq("delete from dialob.questionnaire where id = ? and tenant_id = ?"),
      new Object[] {eq(new byte[] {0x12, 0x30, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00})});

    jdbcBackendDatabase.delete("", "12300000000000000000000000000000");

    //
    verify(jdbcTemplate).update(eq("delete from dialob.questionnaire where id = ? and tenant_id = ?"), eq(new byte[] {0x12, 0x30, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00}), eq(""));
    verify(connection).commit();
    verifyNoMoreInteractions(jdbcTemplate);
  }

  @Test
  public void existsReturnsTrueWhenDocumentExists() throws Exception {
    JdbcTemplate jdbcTemplate = Mockito.mock(JdbcTemplate.class);
    DataSource dataSource = Mockito.mock(DataSource.class);
    Connection connection = Mockito.mock(Connection.class);
    when(jdbcTemplate.getDataSource()).thenReturn(dataSource);
    when(dataSource.getConnection()).thenReturn(connection);
    JdbcBackendDatabase jdbcBackendDatabase = jdbcBackendDatabase(new TransactionTemplate(new DataSourceTransactionManager(dataSource)), jdbcTemplate, databaseHandler(), objectMapper, "dialob");

    Timestamp timestamp = new Timestamp(1000);

    doReturn(1).when(jdbcTemplate).queryForObject(
      eq("select count(*) from dialob.questionnaire where id = ? and tenant_id = ?"),
      eq(Integer.class),
      eq(new byte[] {0x12, 0x30, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00}),
      eq("")
      );

    Assertions.assertThat(jdbcBackendDatabase.exists("", "12300000000000000000000000000000")).isTrue();

    //
    verify(jdbcTemplate).queryForObject(
      eq("select count(*) from dialob.questionnaire where id = ? and tenant_id = ?"),
      eq(Integer.class),
      eq(new byte[] {0x12, 0x30, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00}),
      eq("")
    );
    verify(connection).commit();
    verifyNoMoreInteractions(jdbcTemplate);
  }
  @Test
  public void existsReturnsFalseWhenDocumentExists() throws Exception {
    JdbcTemplate jdbcTemplate = Mockito.mock(JdbcTemplate.class);
    DataSource dataSource = Mockito.mock(DataSource.class);
    Connection connection = Mockito.mock(Connection.class);
    when(jdbcTemplate.getDataSource()).thenReturn(dataSource);
    when(dataSource.getConnection()).thenReturn(connection);
    JdbcBackendDatabase jdbcBackendDatabase = jdbcBackendDatabase(new TransactionTemplate(new DataSourceTransactionManager(dataSource)), jdbcTemplate, databaseHandler(), objectMapper, "dialob");
    Timestamp timestamp = new Timestamp(1000);

    doReturn(0).when(jdbcTemplate).queryForObject(
      eq("select count(*) from dialob.questionnaire where id = ? and tenant_id = ?"),
      eq(Integer.class),
      eq(new byte[] {0x12, 0x30, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00}),
      eq("")
    );

    boolean exists = jdbcBackendDatabase.exists("", "12300000000000000000000000000000");
    Assertions.assertThat(exists).isFalse();

    //
    verify(jdbcTemplate).queryForObject(
      eq("select count(*) from dialob.questionnaire where id = ? and tenant_id = ?"),
      eq(Integer.class),
      eq(new byte[] {0x12, 0x30, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00}),
      eq("")
    );
    verify(connection).commit();
    verifyNoMoreInteractions(jdbcTemplate);
  }

  @Test
  public void castStringRevToIntegerImplicitly() throws Exception {
    JdbcTemplate jdbcTemplate = Mockito.mock(JdbcTemplate.class);
    DataSource dataSource = Mockito.mock(DataSource.class);
    Connection connection = Mockito.mock(Connection.class);
    when(jdbcTemplate.getDataSource()).thenReturn(dataSource);
    when(dataSource.getConnection()).thenReturn(connection);
    JdbcBackendDatabase jdbcBackendDatabase = jdbcBackendDatabase(new TransactionTemplate(new DataSourceTransactionManager(dataSource)), jdbcTemplate, databaseHandler(), objectMapper, "dialob");
    Timestamp timestamp = new Timestamp(1000);

    doReturn(ImmutableQuestionnaire.builder().metadata(ImmutableQuestionnaireMetadata.builder().formId("123").build()).build()).when(jdbcTemplate).queryForObject(
      eq("select rev, tenant_id, form_document_id, status, created, updated, data from dialob.questionnaire where id = ? and rev = ? and tenant_id = ?"),
      eq(new Object[] {new byte[] {0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00}, Integer.valueOf(1), ""}),
      any(RowMapper.class)
    );

    Object found = jdbcBackendDatabase.findOne("", "00000000000000000000000000000000", "1");
    Assertions.assertThat(found).isNotNull();
    verify(jdbcTemplate).queryForObject(
      eq("select rev, tenant_id, form_document_id, status, created, updated, data from dialob.questionnaire where id = ? and rev = ? and tenant_id = ?"),
      eq(new Object[] {new byte[] {0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00}, Integer.valueOf(1), ""}),
      any(RowMapper.class)
    );
    verify(connection).commit();
    verifyNoMoreInteractions(jdbcTemplate);
  }

}
