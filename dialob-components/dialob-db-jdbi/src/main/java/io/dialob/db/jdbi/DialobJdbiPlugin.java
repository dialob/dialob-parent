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
package io.dialob.db.jdbi;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.umd.cs.findbugs.annotations.NonNull;
import io.dialob.db.jdbc.DatabaseHelper;
import io.dialob.db.jdbc.JdbcDatabase;
import lombok.Setter;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.argument.Argument;
import org.jdbi.v3.core.spi.JdbiPlugin;
import org.jdbi.v3.jackson2.Jackson2Config;
import org.jdbi.v3.jackson2.Jackson2Plugin;
import org.jdbi.v3.json.JsonPlugin;

import java.util.Map;
import java.util.stream.Stream;

public class DialobJdbiPlugin implements JdbiPlugin {

  private final DatabaseHelper databaseHelper;

  private final ObjectMapper objectMapper;

  @Setter
  private Map<String, String> remaps = Map.of();

  @Setter
  private String schema;

  public DialobJdbiPlugin(DatabaseHelper databaseHelper, ObjectMapper objectMapper) {
    this.databaseHelper = databaseHelper;
    this.objectMapper = objectMapper;
  }

  @Override
  public void customizeJdbi(Jdbi jdbi) {
    remapTableNames(jdbi);
    jdbi.define("json_data_in", this.databaseHelper.jsonToBson("data"));
    jdbi.define("json_data_out", this.databaseHelper.bsonToJson("data"));
    jdbi.installPlugin(new JsonPlugin());
    jdbi.installPlugin(new Jackson2Plugin());
    jdbi.configure(Jackson2Config.class, jsonConfig -> {
      jsonConfig.setMapper(objectMapper);
    });
  }

  private void remapTableNames(Jdbi jdbi) {
    Stream.concat(
      JdbcDatabase.TABLES.stream().map(table ->
        Map.entry(table, databaseHelper.tableName(schema, table))),
      remaps.entrySet().stream().map(
        entry -> Map.entry(
          entry.getKey(),
          databaseHelper.tableName(schema, entry.getValue())
        ))).forEach(entry -> jdbi.define(entry.getKey(), entry.getValue()));
  }


  @NonNull
  private Argument jsonbArgument(Object value) {
    return (position, statement, ctx) -> {
      statement.setObject(position, databaseHelper.jsonObject(objectMapper, value));
    };
  }


}
