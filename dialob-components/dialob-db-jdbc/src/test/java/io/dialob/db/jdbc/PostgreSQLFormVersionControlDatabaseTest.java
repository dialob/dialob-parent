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

import io.dialob.api.form.Form;
import io.dialob.api.form.FormItem;
import io.dialob.form.service.api.FormDatabase;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
@Tag("postgresql")
@Tag("container")
class PostgreSQLFormVersionControlDatabaseTest extends AbstractFormVersionControlDatabaseTest implements AbstractPostgreSQLTest {


  @Test
  void saveAndQueryFormsByMetadata() {

    setActiveTenant("12341234-1234-1234-1234-123412341236");

    Form form = new Form.Builder().metadata(new Form.Metadata.Builder().label("test form").labels(Set.of("label1", "label2")).build()).build();
    form = getJdbcFormDatabase().save(getCurrentTenant().getId(), form);

    Form form2 = new Form.Builder().from(form).putData("questionnaire", new FormItem.Builder()
      .id("questionnaire")
      .type("questionnaire")
      .build())
      .metadata(new Form.Metadata.Builder().label("test form 2").putAdditionalProperties("extra",1).build())
      .id(null)
      .rev(null)
      .build();

    form2 = getJdbcFormDatabase().save(getCurrentTenant().getId(), form2);

    List<FormDatabase.FormMetadataRow> rows = new ArrayList<>();
    getJdbcFormDatabase().findAllMetadata("12341234-1234-1234-1234-123412341236", new Form.Metadata.Builder().label("test form 2").build(), rows::add);
    assertEquals(1, rows.size());
    assertEquals(form2.getId(), rows.getFirst().getId());
    rows.clear();

    getJdbcFormDatabase().findAllMetadata("12341234-1234-1234-1234-123412341236", new Form.Metadata.Builder().label("test form").build(), rows::add);
    assertEquals(1, rows.size());
    assertEquals(form.getId(), rows.getFirst().getId());
    assertEquals(Set.of("label1", "label2"), rows.getFirst().getValue().getLabels());
    rows.clear();


    getJdbcFormDatabase().findAllMetadata("12341234-1234-1234-1234-123412341236", new Form.Metadata.Builder().label("test form 2").putAdditionalProperties("extra",1).build(), rows::add);
    assertEquals(1, rows.size());
    assertEquals(form2.getId(), rows.getFirst().getId());
    rows.clear();
  }

}
