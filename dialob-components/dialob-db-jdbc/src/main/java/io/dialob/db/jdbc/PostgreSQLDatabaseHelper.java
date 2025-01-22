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
import org.postgresql.util.PGobject;
import org.springframework.lang.Nullable;

import java.nio.ByteBuffer;
import java.sql.SQLException;
import java.util.UUID;

/**
 * A helper class for interacting with a PostgreSQL database, extending the functionality
 * provided by {@code AbstractDatabaseHelper}. This class contains methods specific to
 * PostgreSQL, such as handling UUID conversion, JSONB objects, and schema-specific
 * table name generation.
 */
public class PostgreSQLDatabaseHelper extends AbstractDatabaseHelper {

  public PostgreSQLDatabaseHelper(String schema) {
    super(schema);
  }

  @Override
  public Object toJdbcId(byte[] oid) {
    // return oid; //  mysql
    if (oid == null) {
      return null;
    }
    ByteBuffer bb = ByteBuffer.wrap(oid);
    long firstLong = bb.getLong();
    long secondLong = bb.getLong();
    return new UUID(firstLong, secondLong);
  }

  @Override
  public Object jsonObject(ObjectMapper objectMapper, Object document) {
    try {
      PGobject jsonObject = new PGobject();
      jsonObject.setType("jsonb");
      jsonObject.setValue(serializeJson(objectMapper, document));
      return jsonObject;
    } catch (JsonProcessingException | SQLException e) {
      throw new DocumentCorruptedException("Could not write questionnaire");
    }
  }

  @Override
  public byte[] fromJdbcId(Object oid) {
    UUID uuid;
    if (oid instanceof UUID) {
      uuid = (UUID) oid;
    } else if (oid instanceof byte[] bytes) {
      if (bytes.length == 16) {
        return bytes;
      }
      uuid = UUID.fromString(new String(bytes));
    } else {
      throw new IllegalArgumentException("Cannot convert " + oid + " UUID bytes");
    }
    ByteBuffer bb = ByteBuffer.wrap(new byte[16]);
    bb.putLong(uuid.getMostSignificantBits());
    bb.putLong(uuid.getLeastSignificantBits());
    return bb.array();
  }


  @NonNull
  public String tableName(@Nullable String schema, @NonNull String tableName) {
    return "\"" + (StringUtils.isNotBlank(schema) ? schema + "\".\"" + tableName : tableName) + "\"";
  }

  @Override
  public String jsonContains(String path) {
    return "data->'" + path + "' @> ?";
  }

  @Override
  public String extractMetadataJsonArray(String columnName) {
    return String.format (" data->'metadata'->'%s'#>>'{}' ", columnName);
  }
}
