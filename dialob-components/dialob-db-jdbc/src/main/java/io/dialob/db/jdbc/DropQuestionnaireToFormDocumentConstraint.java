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

import edu.umd.cs.findbugs.annotations.Nullable;
import lombok.extern.slf4j.Slf4j;
import org.flywaydb.core.api.callback.BaseCallback;
import org.flywaydb.core.api.callback.Context;
import org.flywaydb.core.api.callback.Event;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;

@Slf4j
public class DropQuestionnaireToFormDocumentConstraint extends BaseCallback {

  private final String tableName;

  private final String constraintName;

  public DropQuestionnaireToFormDocumentConstraint(
    DatabaseHelper databaseHelper,
    @Nullable String schema,
    @Nullable String constraintName
  ) {
    this.tableName = databaseHelper.tableName(schema, "questionnaire");
    this.constraintName = Objects.toString(constraintName, "questionnaire_form_document_id_fkey");
  }

  @Override
  public void handle(Event event, Context context) {
    if (event == Event.AFTER_MIGRATE) {
      Connection connection = context.getConnection();
      try {
        // Works only for postgresql
        var stmt = connection.prepareStatement("alter table %s drop constraint %s".formatted(this.tableName, this.constraintName));
        stmt.execute();
        LOGGER.info("constraint {} removed", this.constraintName);
      } catch (SQLException e) {
        LOGGER.warn("Could not remove constraint {} (this is ok if doesn't exist anymore): {}", this.constraintName, e.getMessage());
      }
    }
  }
}
