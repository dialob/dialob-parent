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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.umd.cs.findbugs.annotations.NonNull;
import io.dialob.db.spi.exceptions.DocumentCorruptedException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.lang.Nullable;

import java.io.ByteArrayInputStream;
import java.io.Reader;
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

  default String remap(String name) {
    return name;
  }

  @NonNull
  default String tableName(@Nullable String schema, @NonNull String tableName) {
    return StringUtils.isNotBlank(schema) ? schema + "." + remap(tableName) : remap(tableName);
  }

  @NonNull
  default String viewName(@Nullable String schema, @NonNull String viewName) {
    return StringUtils.isNotBlank(schema) ? schema + "." + remap(viewName) : remap(viewName);
  }

  default String bsonToJson(String attr) {
    return attr;
  }

  default String jsonToBson(String attr) {
    return attr;
  }

  default String serializeJson(ObjectMapper objectMapper, Object document) throws JsonProcessingException {
    return objectMapper.writeValueAsString(document);
  }

  default Reader extractStream(ResultSet rs, int i) throws SQLException {
    return rs.getCharacterStream(i);
  }

  String getSchema();

  String jsonContains(String path);

  String extractMetadataJsonArray(String columnName);
}
