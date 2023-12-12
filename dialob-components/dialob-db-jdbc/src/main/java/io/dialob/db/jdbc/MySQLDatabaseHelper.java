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

import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MySQLDatabaseHelper extends AbstractDatabaseHelper {
  public MySQLDatabaseHelper(String schema) {
    super(schema);
  }

  @Override
  public String jsonContains(String path) {
    return "JSON_CONTAINS(data, ?, '$." + path + "')";
  }

  @Override
  public Reader extractStream(ResultSet rs, int i) throws SQLException {
    return new InputStreamReader(rs.getBinaryStream(i), StandardCharsets.UTF_8);
  }

}
