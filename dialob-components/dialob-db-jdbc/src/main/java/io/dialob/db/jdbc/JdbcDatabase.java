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

import java.util.Set;

public interface JdbcDatabase {

  String TABLE_FORM_DOCUMENT = "form_document";
  String TABLE_QUESTIONNAIRE = "questionnaire";
  String TABLE_FORM = "form";
  String TABLE_FORM_REV = "form_rev";
  String TABLE_FORM_ARCHIVE = "form_archive";
  String TABLE_FORM_REV_ARCHIVE = "form_rev_archive";

  Set<String> TABLES = Set.of(
    TABLE_FORM_DOCUMENT,
    TABLE_QUESTIONNAIRE,
    TABLE_FORM_REV_ARCHIVE,
    TABLE_FORM,
    TABLE_FORM_REV,
    TABLE_FORM_ARCHIVE
  );

  DatabaseHelper getDatabaseHelper();

  default Object toJdbcId(byte[] oid) {
    return getDatabaseHelper().toJdbcId(oid);
  }

}
