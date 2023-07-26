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
import org.springframework.jdbc.core.SqlParameterValue;
import org.springframework.jdbc.core.support.SqlLobValue;

import java.io.InputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

public class DB2DatabaseHelper implements DatabaseHelper {
  private final String schema;

  public DB2DatabaseHelper(String schema) {
    this.schema = schema;
  }

  @Override
  public String getSchema() {
    return schema;
  }

  @Override
  public String jsonContains(String path) {
    return "JSON_VAL(data, '" + path + "', 'u') is not null ?";
  }

  @Override
  public String jsonToBson(String attr) {
    return "SYSTOOLS.JSON2BSON(" + attr +")";
  }

  @Override
  public String bsonToJson(String attr) {
    return "SYSTOOLS.BSON2JSON(" + attr +")";
  }

  @Override
  public Object jsonObject(ObjectMapper objectMapper, Object document) {
    try {
      return new SqlParameterValue(Types.CLOB, new SqlLobValue(objectMapper.writeValueAsString(document)));
    } catch (JsonProcessingException e) {
      throw new DocumentCorruptedException("Could not write JSON object");
    }
  }

  @Override
  public InputStream extractStream(ResultSet rs, int i) throws SQLException {
    return rs.getClob(i).getAsciiStream();
  }
}
