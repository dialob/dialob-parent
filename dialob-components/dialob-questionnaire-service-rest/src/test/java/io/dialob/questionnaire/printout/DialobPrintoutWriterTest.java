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
import io.dialob.api.proto.ActionItem;
import io.dialob.api.questionnaire.Answer;
import io.dialob.api.questionnaire.ContextValue;
import io.dialob.api.questionnaire.Questionnaire;
import io.dialob.api.questionnaire.VariableValue;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import tools.jackson.databind.node.ObjectNode;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Verifies the session-state shape (pages / groups / items, byId / allIds, key / value / hiddenPrint),
 * value-set resolution and rowgroup expansion — from the form + answers — that visibility comes from the
 * engine's computed {@code inactive} flag (no hand-rolled {@code activeWhen} evaluation), that the displayed
 * {@code label} prefers the engine's computed label, and that the in-house fallback substitution formats
 * numeric variables with the locale while leaving text/ids alone.
 */
class DialobPrintoutWriterTest {

  private final DialobPrintoutWriter writer = new DialobPrintoutWriter();
  private final ZoneId tz = ZoneId.of("Europe/Helsinki");

  private final Map<String, FormItem> data = new LinkedHashMap<>();
  private final List<Answer> answers = new ArrayList<>();
  private final List<ContextValue> context = new ArrayList<>();
  private final List<VariableValue> variables = new ArrayList<>();
  private final Map<String, ActionItem> engineItems = new HashMap<>();

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

  /** A value calculated during the session (no form item), exposed to {@code {variable}} substitution. */
  private void variable(String id, Object value) {
    variables.add(new VariableValue.Builder().id(id).value(value).build());
  }

  /** Provide an engine-computed label for an item (otherwise the engine lookup returns nothing → fallback). */
  private void engineLabel(String id, String label) {
    engineItems.put(id, new ActionItem.Builder().id(id).type("note").label(label).build());
  }

  /** Mark an item inactive in the engine's computed view, so the writer hides it from the printout. */
  private void engineInactive(String id) {
    engineItems.put(id, new ActionItem.Builder().id(id).type("text").inactive(true).build());
  }

  /** Engine lookup; returns {@code null} for ids without an explicit engine label, so the writer falls back. */
  private Function<String, ActionItem> engine() {
    return engineItems::get;
  }

  private Form form(List<FormValueSet> valueSets) {
    return new Form.Builder()
      .metadata(new Form.Metadata.Builder().label("My Form").tenantId("t-1").build())
      .putAllData(data)
      .valueSets(valueSets)
      .build();
  }

  private Questionnaire questionnaire(String language) {
    return new Questionnaire.Builder()
      .id("q-123")
      .metadata(new Questionnaire.Metadata.Builder()
        .formId("f-1").status(Questionnaire.Metadata.Status.COMPLETED).language(language).creator("bob").build())
      .answers(answers)
      .context(context)
      .variableValues(variables)
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
    item("q3", "text", "Hidden Q", "showHidden = 'yes'", null);
    engineInactive("q3"); // engine computed it inactive -> excluded from the printout
    answer("q1", "hello");
    answer("q2", "a");

    String json = writer.writePrintout(form(List.of(vs1())), questionnaire("en"), engine(), "en", tz);

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
    assertThat(json).doesNotContain("q3").doesNotContain("Hidden Q");
  }

  @Test
  void prefersEngineComputedLabelOverFormTemplate() throws Exception {
    item("questionnaire", "questionnaire", null, null, null, "page1");
    item("page1", "group", "Page 1", null, null, "g1");
    item("g1", "group", "Group 1", null, null, "n1");
    item("n1", "note", "Saldo: {popSavings} eur", null, null); // form template
    engineLabel("n1", "Saldo: 45 646 eur");                    // engine-computed (locale-grouped)

    String json = writer.writePrintout(form(List.of(vs1())), questionnaire("fi"), engine(), "fi", tz);

    tools.jackson.databind.JsonNode note =
      new tools.jackson.databind.ObjectMapper().readTree(json).path("items").path("byId").path("n1");
    assertThat(note.path("key").asText()).isEqualTo("Saldo: {popSavings} eur"); // raw template
    assertThat(note.path("label").asText()).isEqualTo("Saldo: 45 646 eur");      // engine label
  }

  @Test
  void fallbackFormatsNumericVariablesButLeavesTextAndIdsAlone() throws Exception {
    item("questionnaire", "questionnaire", null, null, null, "page1");
    item("page1", "group", "Page 1", null, null, "g1");
    item("g1", "group", "Group 1", null, null, "popSavings", "phone", "n1");
    item("popSavings", "number", "Savings", null, null);
    item("phone", "text", "Phone", null, null);
    item("n1", "note", "Savings: {popSavings}, phone: {phone}, hetu: {hetu}", null, null);
    answer("popSavings", "45646");          // INTEGER stored as string -> grouped
    answer("phone", "0401234567");          // text -> left as-is
    context.add(ContextValue.of("hetu", "010168-0066")); // id-like -> not numeric -> left as-is
    // no engine label for n1 -> writer falls back to in-house substitution

    String json = writer.writePrintout(form(List.of(vs1())), questionnaire("en"), engine(), "en", tz);

    tools.jackson.databind.JsonNode note =
      new tools.jackson.databind.ObjectMapper().readTree(json).path("items").path("byId").path("n1");
    assertThat(note.path("label").asText()).isEqualTo("Savings: 45,646, phone: 0401234567, hetu: 010168-0066");
  }

  @Test
  void expandsRowgroupRowsAndCells() throws Exception {
    item("questionnaire", "questionnaire", null, null, null, "page1");
    item("page1", "group", "Page 1", null, null, "rg");
    item("rg", "rowgroup", "Table", null, null, "c1", "c2");
    item("c1", "text", "Col1", null, null);
    item("c2", "list", "Col2", null, "vs1");
    answer("rg", List.of(0, 1));
    answer("rg.0.c1", "r0c1");
    answer("rg.0.c2", "a");
    answer("rg.1.c1", "r1c1");
    answer("rg.1.c2", "b");

    String json = writer.writePrintout(form(List.of(vs1())), questionnaire("en"), engine(), "en", tz);

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
    assertThat(json).contains("\"value\":null");
  }

  @Test
  void notesOmitValueRegularItemsIncludeIt() throws Exception {
    item("questionnaire", "questionnaire", null, null, null, "page1");
    item("page1", "group", "Page 1", null, null, "g1");
    item("g1", "group", "Group 1", null, null, "q1", "n1");
    item("q1", "text", "Name", null, null);
    item("n1", "note", "A note", null, null);
    answer("q1", "hello");

    String json = writer.writePrintout(form(List.of(vs1())), questionnaire("en"), engine(), "en", tz);

    tools.jackson.databind.JsonNode root = new tools.jackson.databind.ObjectMapper().readTree(json);
    tools.jackson.databind.JsonNode note = root.path("items").path("byId").path("n1");
    tools.jackson.databind.JsonNode q1 = root.path("items").path("byId").path("q1");
    assertThat(iterableFieldNames((ObjectNode) note)).containsExactlyInAnyOrder("type", "hiddenPrint", "key", "label");
    assertThat(iterableFieldNames((ObjectNode) q1)).containsExactlyInAnyOrder("type", "label", "hiddenPrint", "key", "value");
  }

  @Test
  void resolvesMultiChoiceValuesAndDropsUnknownEntries() throws Exception {
    item("questionnaire", "questionnaire", null, null, null, "page1");
    item("page1", "group", "Page 1", null, null, "g1");
    item("g1", "group", "Group 1", null, null, "multi");
    item("multi", "multichoice", "Pick", null, "vs1");
    answer("multi", List.of("a", "b", "zzz")); // a,b resolve from vs1; zzz is unknown -> null

    String json = writer.writePrintout(form(List.of(vs1())), questionnaire("en"), engine(), "en", tz);

    tools.jackson.databind.JsonNode value = new tools.jackson.databind.ObjectMapper()
      .readTree(json).path("items").path("byId").path("multi").path("value");
    assertThat(value.isArray()).isTrue();
    assertThat(value.get(0).asText()).isEqualTo("Option A");
    assertThat(value.get(1).asText()).isEqualTo("Option B");
    assertThat(value.get(2).isNull()).isTrue();
  }

  @Test
  void keepsLabellessGroupWithVisibleDescendantAndDropsEmptyGroup() throws Exception {
    item("questionnaire", "questionnaire", null, null, null, "page1");
    item("page1", "group", "Page 1", null, null, "wrap", "empty");
    item("wrap", "group", null, null, null, "inner"); // no label, nested group has a visible leaf -> kept
    item("inner", "group", null, null, null, "q1");
    item("q1", "text", "Name", null, null);
    item("empty", "group", null, null, null, "q2"); // no label, child has no answer -> nothing visible -> dropped
    item("q2", "text", "Unanswered", null, null);
    answer("q1", "hi");

    String json = writer.writePrintout(form(List.of(vs1())), questionnaire("en"), engine(), "en", tz);

    tools.jackson.databind.JsonNode groups = new tools.jackson.databind.ObjectMapper()
      .readTree(json).path("groups").path("byId");
    assertThat(groups.has("wrap")).isTrue();
    assertThat(groups.has("inner")).isTrue();
    assertThat(groups.has("empty")).isFalse();
  }

  @Test
  void fallbackStripsFormatModifierAndRendersPlaceholdersForUnansweredVars() throws Exception {
    item("questionnaire", "questionnaire", null, null, null, "page1");
    item("page1", "group", "Page 1", null, null, "g1");
    item("g1", "group", "Group 1", null, null, "amount", "n1");
    item("amount", "number", "Amount", null, null); // unanswered number var -> "0"
    item("n1", "note", "When: {when:format}, amount: {amount}, who: {who}", null, null);
    answer("when", "2020-01-02"); // referenced with a :modifier that gets stripped, value rendered verbatim
    // amount unanswered number -> "0"; who unknown -> "-"; no engine label for n1 -> fallback substitution

    String json = writer.writePrintout(form(List.of(vs1())), questionnaire("en"), engine(), "en", tz);

    tools.jackson.databind.JsonNode note = new tools.jackson.databind.ObjectMapper()
      .readTree(json).path("items").path("byId").path("n1").path("label");
    assertThat(note.asText()).isEqualTo("When: 2020-01-02, amount: 0, who: -");
  }

  @Test
  void marksItemHiddenWhenNoPrintPropSet() throws Exception {
    item("questionnaire", "questionnaire", null, null, null, "page1");
    item("page1", "group", "Page 1", null, null, "g1");
    item("g1", "group", "Group 1", null, null, "secret");
    data.put("secret", new FormItem.Builder().id("secret").type("text")
      .label(Map.of("en", "Secret")).props(Map.of("noPrint", true)).build());
    answer("secret", "x");

    String json = writer.writePrintout(form(List.of(vs1())), questionnaire("en"), engine(), "en", tz);

    tools.jackson.databind.JsonNode secret = new tools.jackson.databind.ObjectMapper()
      .readTree(json).path("items").path("byId").path("secret");
    assertThat(secret.path("hiddenPrint").asBoolean()).isTrue();
  }

  @Test
  void usesCalculatedVariableValuesInSubstitution() throws Exception {
    item("questionnaire", "questionnaire", null, null, null, "page1");
    item("page1", "group", "Page 1", null, null, "g1");
    item("g1", "group", "Group 1", null, null, "n1");
    item("n1", "note", "Owner: {ownerName}", null, null);
    variable("ownerName", "Acme"); // session-calculated variable (no form item) -> rendered verbatim
    // no engine label for n1 -> fallback substitution reads the calculated variable value

    String json = writer.writePrintout(form(List.of(vs1())), questionnaire("en"), engine(), "en", tz);

    tools.jackson.databind.JsonNode note = new tools.jackson.databind.ObjectMapper()
      .readTree(json).path("items").path("byId").path("n1").path("label");
    assertThat(note.asText()).isEqualTo("Owner: Acme");
  }

  @Test
  void throwsWhenFormHasNoQuestionnaireRoot() {
    item("page1", "group", "Page 1", null, null); // no item of type "questionnaire"
    Form formWithoutRoot = form(List.of());
    Questionnaire q = questionnaire("en");
    Function<String, ActionItem> engineLookup = engine();

    assertThatThrownBy(() -> writer.writePrintout(formWithoutRoot, q, engineLookup, "en", tz))
      .isInstanceOf(IllegalStateException.class)
      .hasMessageContaining("no questionnaire root");
  }

  private static List<String> iterableFieldNames(ObjectNode node) {
    return new ArrayList<>(node.propertyNames());
  }
}
