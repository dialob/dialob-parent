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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.dialob.db.spi.exceptions.DocumentCorruptedException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.sql.ResultSet;
import java.sql.SQLException;

public interface DatabaseHelper {

  default Object toJdbcId(byte[] oid) {
    return oid;
  }

  default Object jsonObject(ObjectMapper objectMapper, Object document) {
    try {
      return new ByteArrayInputStream(objectMapper.writeValueAsBytes(document));
    } catch (JsonProcessingException e) {
      throw new DocumentCorruptedException("Could not write questionnaire");
    }
  }

  default byte[] fromJdbcId(Object oid) {
    return (byte[]) oid;
  }

  @NonNull
  default String tableName(@Nullable String schema, @NonNull String tableName) {
    return StringUtils.isNotBlank(schema) ? schema + "." + tableName : tableName;
  }

  default String bsonToJson(String attr) {
    return attr;
  }
  default String jsonToBson(String attr) {
    return attr;
  }

  default InputStream extractStream(ResultSet rs, int i) throws SQLException {
    return rs.getBinaryStream(i);
  }

  String getSchema();

  String jsonContains(String path);
}
