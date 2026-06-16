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
package io.dialob.questionnaire.printout;

import io.dialob.api.form.Form;
import io.dialob.api.form.FormItem;
import io.dialob.api.form.FormValueSet;
import io.dialob.api.form.FormValueSetEntry;
import io.dialob.api.questionnaire.Answer;
import io.dialob.api.questionnaire.Questionnaire;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Verifies that the printout body built from the static {@link Form} + stored answers keeps the
 * exact shape the downstream template consumes (pages / groups / items, byId / allIds,
 * key / value / hiddenPrint), and that visibility ({@code activeWhen}) and value-set label
 * resolution and rowgroup row/cell expansion behave as expected.
 */
class DialobPrintoutWriterTest {

  private final DialobPrintoutWriter writer = new DialobPrintoutWriter();
  private final ZoneId tz = ZoneId.of("Europe/Helsinki");

  private final Map<String, FormItem> data = new LinkedHashMap<>();
  private final List<Answer> answers = new ArrayList<>();

  private void item(String id, String type, String labelEn, String activeWhen, String valueSetId, String... items) {
    FormItem.Builder b = new FormItem.Builder().id(id).type(type);
    if (labelEn != null) b.label(Map.of("en", labelEn));
    if (activeWhen != null) b.activeWhen(activeWhen);
    if (valueSetId != null) b.valueSetId(valueSetId);
    if (items.length > 0) b.items(List.of(items));
    data.put(id, b.build());
  }

  private void answer(String id, Object value) {
    answers.add(new Answer.Builder().id(id).value(value).build());
  }

  private Form form(List<FormValueSet> valueSets) {
    return new Form.Builder()
      .metadata(new Form.Metadata.Builder().label("My Form").tenantId("t-1").build())
      .putAllData(data)
      .valueSets(valueSets)
      .build();
  }

  private Questionnaire questionnaire() {
    return new Questionnaire.Builder()
      .id("q-123")
      .metadata(new Questionnaire.Metadata.Builder()
        .formId("f-1").status(Questionnaire.Metadata.Status.COMPLETED).language("en").creator("bob").build())
      .answers(answers)
      .build();
  }

  private static FormValueSet vs1() {
    return new FormValueSet.Builder().id("vs1").entries(List.of(
      new FormValueSetEntry.Builder().id("a").label(Map.of("en", "Option A")).build(),
      new FormValueSetEntry.Builder().id("b").label(Map.of("en", "Option B")).build())).build();
  }

  @Test
  void mapsGroupsItemsNotesChoicesAndHidesInactive() throws Exception {
    item("questionnaire", "questionnaire", null, null, null, "page1");
    item("page1", "group", "Page 1", null, null, "g1");
    item("g1", "group", "Group 1", null, null, "q1", "q2", "n1", "q3");
    item("q1", "text", "Name", null, null);
    item("q2", "list", "Choice", null, "vs1");
    item("n1", "note", "Some note text", null, null);
    item("q3", "text", "Hidden Q", "showHidden = 'yes'", null); // activeWhen false -> inactive -> excluded
    answer("q1", "hello");
    answer("q2", "a");

    String json = writer.writePrintout(form(List.of(vs1())), questionnaire(), "en", tz);

    String expected = """
      {
        "id": "q-123",
        "metadata": { "formId": "f-1", "status": "COMPLETED", "language": "en", "creator": "bob" },
        "formMetadata": { "label": "My Form", "tenantId": "t-1" },
        "form": { "pages": ["page1"] },
        "pages": {
          "byId": { "page1": { "type": "group", "label": "Page 1", "hiddenPrint": false, "groupIds": ["g1"] } },
          "pageIds": ["page1"]
        },
        "items": {
          "byId": {
            "q1": { "type": "text", "label": "Name", "hiddenPrint": false, "key": "hello", "value": "hello" },
            "q2": { "type": "list", "label": "Choice", "hiddenPrint": false, "key": "a", "value": "Option A" },
            "n1": { "type": "note", "hiddenPrint": false, "key": "Some note text", "label": "Some note text" }
          },
          "allIds": ["q1", "q2", "n1"]
        },
        "groups": {
          "byId": {
            "page1": { "type": "group", "label": "Page 1", "hiddenPrint": false, "itemIds": ["g1"] },
            "g1": { "type": "group", "label": "Group 1", "hiddenPrint": false, "itemIds": ["q1", "q2", "n1"] }
          },
          "allIds": ["page1", "g1"]
        }
      }
      """;

    JSONAssert.assertEquals(expected, json, JSONCompareMode.LENIENT);
    org.assertj.core.api.Assertions.assertThat(json).doesNotContain("q3").doesNotContain("Hidden Q");
  }

  @Test
  void expandsRowgroupRowsAndCells() throws Exception {
    item("questionnaire", "questionnaire", null, null, null, "page1");
    item("page1", "group", "Page 1", null, null, "rg");
    item("rg", "rowgroup", "Table", null, null, "c1", "c2");
    item("c1", "text", "Col1", null, null);
    item("c2", "list", "Col2", null, "vs1");
    answer("rg", List.of(0, 1)); // two rows
    answer("rg.0.c1", "r0c1");
    answer("rg.0.c2", "a");
    answer("rg.1.c1", "r1c1");
    answer("rg.1.c2", "b");

    String json = writer.writePrintout(form(List.of(vs1())), questionnaire(), "en", tz);

    String expected = """
      {
        "items": {
          "byId": {
            "rg":      { "type": "rowgroup", "label": "Table", "hiddenPrint": false, "key": ["rg.0", "rg.1"], "value": null },
            "rg.0":    { "type": "rowgroup", "label": "Table", "hiddenPrint": false, "key": ["rg.0.c1", "rg.0.c2"], "value": null },
            "rg.0.c1": { "type": "text", "label": "Col1", "hiddenPrint": false, "key": "r0c1", "value": "r0c1" },
            "rg.0.c2": { "type": "list", "label": "Col2", "hiddenPrint": false, "key": "a", "value": "Option A" },
            "rg.1":    { "type": "rowgroup", "label": "Table", "hiddenPrint": false, "key": ["rg.1.c1", "rg.1.c2"], "value": null },
            "rg.1.c1": { "type": "text", "label": "Col1", "hiddenPrint": false, "key": "r1c1", "value": "r1c1" },
            "rg.1.c2": { "type": "list", "label": "Col2", "hiddenPrint": false, "key": "b", "value": "Option B" }
          },
          "allIds": ["rg"]
        },
        "groups": {
          "byId": { "page1": { "type": "group", "label": "Page 1", "hiddenPrint": false, "itemIds": ["rg"] } },
          "allIds": ["page1"]
        }
      }
      """;

    JSONAssert.assertEquals(expected, json, JSONCompareMode.LENIENT);
  }
}
