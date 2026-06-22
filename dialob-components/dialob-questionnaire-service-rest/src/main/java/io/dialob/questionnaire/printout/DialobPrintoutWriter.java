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

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.dialob.api.form.Form;
import io.dialob.api.form.FormItem;
import io.dialob.api.form.FormValueSet;
import io.dialob.api.form.FormValueSetEntry;
import io.dialob.api.proto.ActionItem;
import io.dialob.api.questionnaire.Answer;
import io.dialob.api.questionnaire.ContextValue;
import io.dialob.api.questionnaire.Questionnaire;
import io.dialob.api.questionnaire.VariableValue;

/**
 * Serializes a completed questionnaire into the {@link PrintoutBody} JSON consumed downstream
 * (e.g. a Tagomi template) to render a PDF.
 *
 * <p>Structure, {@code activeWhen} visibility and value-set resolution come from the static
 * {@link Form} and the stored {@link Questionnaire} answers. The displayed {@code label} is taken
 * from the Dialob engine's computed {@link ActionItem} (so notes get the engine's locale-aware
 * variable interpolation) when available, falling back to in-house {@code {variable}} substitution —
 * which itself formats numeric variables with the questionnaire's locale. Serialized with Jackson.
 */
public class DialobPrintoutWriter {

  private static final String FALLBACK_LANG = "en";
  private static final String PRINTOUT_DISABLE_PROPERTY = "noPrint";
  private static final DateTimeFormatter ISO_DATE_TIME = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
  private static final ObjectMapper MAPPER = new ObjectMapper();
  private static final Pattern NUMERIC = Pattern.compile("-?\\d+(\\.\\d+)?");

  private final Pattern substitutePattern = Pattern.compile("\\{([\\w:]+?)\\}");

  public String writePrintout(Form form, Questionnaire questionnaire,
                              Function<String, ActionItem> engineItem, String language, ZoneId timezone) {
    Ctx ctx = new Ctx(form, questionnaire, language == null ? FALLBACK_LANG : language, engineItem);
    FormItem root = findRoot(form);

    Set<String> groupList = new LinkedHashSet<>();
    collectAllGroups(ctx, root.getId(), groupList);
    Set<String> itemList = collectSubitems(ctx, groupList);

    Map<String, Object> itemsById = new LinkedHashMap<>();
    List<String> itemAllIds = buildSubitems(ctx, itemsById, itemList);

    PrintoutBody body = new PrintoutBody(
      questionnaire.getId(),
      buildMetadata(questionnaire, timezone),
      buildFormMetadata(form, timezone),
      buildContextValues(questionnaire),
      new PrintoutBody.FormRef(root.getItems()),
      buildPages(ctx, root),
      new PrintoutBody.Items(itemsById, itemAllIds),
      buildGroups(ctx, groupList, new LinkedHashSet<>(itemAllIds)));

    try {
      return MAPPER.writeValueAsString(body);
    } catch (JsonProcessingException e) {
      throw new IllegalStateException("Failed to serialize printout for questionnaire " + questionnaire.getId(), e);
    }
  }

  private PrintoutBody.Pages buildPages(Ctx ctx, FormItem root) {
    Map<String, PrintoutBody.Page> byId = new LinkedHashMap<>();
    for (String pageId : root.getItems()) {
      FormItem page = findItem(ctx.form, pageId);
      byId.put(page.getId(), new PrintoutBody.Page(
        page.getType(), getLabel(page, ctx.lang), isHiddenPrint(page, ctx), page.getItems()));
    }
    return new PrintoutBody.Pages(byId, root.getItems());
  }

  private void collectAllGroups(Ctx ctx, String groupName, Set<String> groupList) {
    FormItem item = findItem(ctx.form, groupName);
    for (String childName : item.getItems()) {
      FormItem child = findItem(ctx.form, childName);
      if (child != null && "group".equals(child.getType()) && !isInactive(child, ctx)) {
        groupList.add(childName);
        collectAllGroups(ctx, childName, groupList);
      }
    }
  }

  private Set<String> collectSubitems(Ctx ctx, Set<String> groupList) {
    Set<String> itemList = new LinkedHashSet<>();
    for (String groupName : groupList) {
      FormItem group = findItem(ctx.form, groupName);
      for (String childName : group.getItems()) {
        if (!groupList.contains(childName)) {
          FormItem child = findItem(ctx.form, childName);
          if (child != null && !isInactive(child, ctx)) {
            itemList.add(childName);
          }
        } else {
          itemList.add(childName);
        }
      }
    }
    return itemList;
  }

  private PrintoutBody.Groups buildGroups(Ctx ctx, Set<String> groupItems, Set<String> groupSubitems) {
    Map<String, PrintoutBody.Group> byId = new LinkedHashMap<>();
    for (String name : groupItems) {
      FormItem item = findItem(ctx.form, name);
      if (!filterGroup(item, ctx, groupSubitems)) {
        continue;
      }
      List<String> itemIds = new ArrayList<>();
      for (String sub : item.getItems()) {
        if (groupSubitems.contains(sub) || groupItems.contains(sub)) {
          itemIds.add(sub);
        }
      }
      byId.put(item.getId(), new PrintoutBody.Group(
        item.getType(), getLabel(item, ctx.lang), isHiddenPrint(item, ctx), itemIds));
    }
    return new PrintoutBody.Groups(byId, new ArrayList<>(groupItems));
  }

  private boolean filterGroup(FormItem group, Ctx ctx, Set<String> groupSubitems) {
    if (getLabel(group, ctx.lang) != null) {
      return true;
    }
    for (String childName : group.getItems()) {
      FormItem child = findItem(ctx.form, childName);
      if (child == null) continue;
      if ("group".equals(child.getType())) {
        if (filterGroup(child, ctx, groupSubitems)) return true;
      } else if (groupSubitems.contains(child.getId())) {
        return true;
      }
    }
    return false;
  }

  private List<String> buildSubitems(Ctx ctx, Map<String, Object> byId, Set<String> items) {
    List<String> allIds = new ArrayList<>();
    for (String itemId : items) {
      FormItem item = findItem(ctx.form, itemId);
      if (item == null) {
        continue;
      }
      if ("rowgroup".equals(item.getType())) {
        List<String> rows = buildRowgroupAnswer(ctx, byId, item);
        byId.put(itemId, item(ctx, item, rows, null));
        allIds.add(itemId);
      } else if (buildRegularItem(ctx, byId, itemId, item)) {
        allIds.add(itemId);
      }
    }
    return allIds;
  }

  /** @return false (and the caller drops the item) when it has no answer and is not a note. */
  private boolean buildRegularItem(Ctx ctx, Map<String, Object> byId, String itemId, FormItem item) {
    Object answerValue = ctx.values.get(itemId);
    Object value = answerValue == null ? null : mapAnswerValue(item, answerValue, ctx);
    if (value != null) {
      byId.put(itemId, item(ctx, item, answerValue, value));
      return true;
    }
    if ("note".equals(item.getType())) {
      byId.put(itemId, new PrintoutBody.Note(
        item.getType(), isHiddenPrint(item, ctx), getLabel(item, ctx.lang), resolveLabel(item, ctx)));
      return true;
    }
    return false;
  }

  private List<String> buildRowgroupAnswer(Ctx ctx, Map<String, Object> byId, FormItem item) {
    Object value = ctx.values.get(item.getId());
    List<String> result = new ArrayList<>();
    if (value instanceof List<?> indexes) {
      for (Object idxObj : indexes) {
        String rowId = item.getId() + "." + idxObj;
        result.add(rowId);
        List<String> cellIds = new ArrayList<>();
        for (String columnName : item.getItems()) {
          FormItem columnItem = findItem(ctx.form, columnName);
          String cellId = rowId + "." + columnName;
          buildRegularItem(ctx, byId, cellId, columnItem);
          cellIds.add(cellId);
        }
        byId.put(rowId, item(ctx, item, cellIds, null));
      }
    }
    return result;
  }

  private PrintoutBody.Item item(Ctx ctx, FormItem item, Object key, Object value) {
    return new PrintoutBody.Item(item.getType(), resolveLabel(item, ctx), isHiddenPrint(item, ctx), key, value);
  }

  private Object mapAnswerValue(FormItem item, Object answerValue, Ctx ctx) {
    if (answerValue != null && item.getValueSetId() != null) {
      FormValueSet vs = ctx.form.getValueSets() == null ? null : ctx.form.getValueSets().stream()
          .filter(v -> item.getValueSetId().equals(v.getId()))
          .findFirst().orElse(null);
      if (vs != null) {
        if (answerValue instanceof Collection) {
          List<Object> elems = new ArrayList<>();
          for (Object e : (Collection<?>) answerValue) {
            elems.add(resolveFromValueSet(e, vs, ctx.lang));
          }
          return elems;
        }
        return resolveFromValueSet(answerValue, vs, ctx.lang);
      }
    }
    return answerValue;
  }

  private Object resolveFromValueSet(Object answerValue, FormValueSet vs, String lang) {
    if (vs.getEntries() == null) return null;
    for (FormValueSetEntry e : vs.getEntries()) {
      if (answerValue.equals(e.getId())) {
        String label = e.getLabel().get(lang);
        return label != null ? label : e.getLabel().get(FALLBACK_LANG);
      }
    }
    return null;
  }

  /**
   * Displayed label — prefers the Dialob engine's computed label (locale-aware variable interpolation),
   * falling back to in-house {@code {variable}} substitution over the form label.
   */
  private String resolveLabel(FormItem item, Ctx ctx) {
    ActionItem ai = ctx.engineItem.apply(item.getId());
    if (ai != null) {
      String engineLabel = ai.getLabel();
      if (engineLabel != null && !engineLabel.isEmpty()) {
        return engineLabel;
      }
    }
    return substituteVariables(getLabel(item, ctx.lang), ctx);
  }

  private String substituteVariables(String label, Ctx ctx) {
    if (label == null) return "";
    Matcher m = substitutePattern.matcher(label);
    int i = 0;
    StringBuilder sb = new StringBuilder();
    while (m.find()) {
      String expr = m.group(1);
      if (expr.contains(":")) {
        expr = expr.substring(0, expr.indexOf(':'));
      }
      sb.append(label, i, m.start()).append(getExpression(expr, ctx));
      i = m.end();
    }
    sb.append(label.substring(i));
    return sb.toString();
  }

  private String getExpression(String name, Ctx ctx) {
    Object v = ctx.values.get(name);
    FormItem fi = ctx.form.getData().get(name);
    if (v != null) {
      return formatVariable(v, fi, ctx);
    }
    if (fi != null && "number".equals(fi.getType())) return "0";
    return "-";
  }

  /**
   * Renders a substituted variable value. Numeric values are grouped per the questionnaire locale
   * (e.g. {@code 45 646}), matching how the Dialob engine formats numbers; everything else is rendered
   * verbatim. A value is treated as numeric only when the referenced item is a number/decimal field (or
   * a computed/context variable) <em>and</em> the value parses cleanly as a number — so text, ids and
   * phone numbers (e.g. {@code 010168-0066}) are never grouped.
   */
  private String formatVariable(Object v, FormItem fi, Ctx ctx) {
    boolean numericField = fi == null || "number".equals(fi.getType()) || "decimal".equals(fi.getType());
    if (numericField) {
      BigDecimal n = toNumber(v);
      if (n != null) {
        return ctx.numberFormat.format(n);
      }
    }
    return String.valueOf(v);
  }

  private static BigDecimal toNumber(Object v) {
    if (v instanceof BigDecimal bd) return bd;
    if (v instanceof Number num) return new BigDecimal(num.toString());
    String s = String.valueOf(v).trim();
    if (!NUMERIC.matcher(s).matches()) return null;
    try {
      return new BigDecimal(s);
    } catch (NumberFormatException e) {
      return null;
    }
  }

  private String getLabel(FormItem item, String language) {
    if (item.getLabel() == null) return null;
    String result = item.getLabel().get(language);
    return result != null ? result : item.getLabel().get(FALLBACK_LANG);
  }

  private boolean isHiddenPrint(FormItem item, Ctx ctx) {
    Map<String, Object> props = item.getProps();
    if (props != null) {
      Object np = props.get(PRINTOUT_DISABLE_PROPERTY);
      if ("true".equals(np) || Boolean.TRUE.equals(np)) return true;
    }
    return isInactive(item, ctx);
  }

  /**
   * Visibility comes straight from the engine: an item is hidden when its computed {@code ActionItem}
   * is marked inactive. The engine evaluates the form's full {@code activeWhen} expression language —
   * so this no longer reimplements a subset of it. Items the engine never produced (id not in the
   * computed view) default to visible.
   */
  private boolean isInactive(FormItem item, Ctx ctx) {
    ActionItem ai = ctx.engineItem.apply(item.getId());
    return ai != null && Boolean.TRUE.equals(ai.getInactive());
  }

  private PrintoutBody.Metadata buildMetadata(Questionnaire q, ZoneId tz) {
    var m = q.getMetadata();
    return new PrintoutBody.Metadata(
      writeDateTime(m.getCreated(), tz), m.getCreator(), m.getFormId(), m.getFormRev(),
      m.getLabel(), m.getLanguage(), writeDateTime(m.getLastAnswer(), tz), m.getOwner(),
      String.valueOf(m.getStatus()), m.getTenantId());
  }

  private PrintoutBody.FormMetadata buildFormMetadata(Form form, ZoneId tz) {
    var m = form.getMetadata();
    return new PrintoutBody.FormMetadata(
      writeDateTime(m.getCreated(), tz), m.getCreator(), m.getLabel(),
      writeDateTime(m.getLastSaved(), tz), m.getSavedBy(), m.getTenantId());
  }

  private Map<String, Object> buildContextValues(Questionnaire q) {
    Map<String, Object> contextValues = new LinkedHashMap<>();
    if (q.getContext() != null) {
      for (ContextValue cv : q.getContext()) {
        contextValues.put(cv.getId(), cv.getValue());
      }
    }
    return contextValues;
  }

  private String writeDateTime(Instant instant, ZoneId tz) {
    return instant == null ? null : ISO_DATE_TIME.format(ZonedDateTime.ofInstant(instant, tz));
  }

  private FormItem findItem(Form form, String itemId) {
    return form.getData().get(itemId);
  }

  private FormItem findRoot(Form form) {
    for (Entry<String, FormItem> e : form.getData().entrySet()) {
      if ("questionnaire".equals(e.getValue().getType())) {
        return e.getValue();
      }
    }
    throw new IllegalStateException("Incorrect form, no questionnaire root found");
  }

  private final class Ctx {
    final Form form;
    final String lang;
    final Map<String, Object> values = new LinkedHashMap<>();
    final NumberFormat numberFormat;
    final Function<String, ActionItem> engineItem;

    Ctx(Form form, Questionnaire q, String lang, Function<String, ActionItem> engineItem) {
      this.form = form;
      this.lang = lang;
      this.engineItem = engineItem;
      this.numberFormat = NumberFormat.getInstance(Locale.forLanguageTag(lang));

      index(q.getAnswers(), Answer::getId, Answer::getValue);
      index(q.getVariableValues(), VariableValue::getId, VariableValue::getValue);
      index(q.getContext(), ContextValue::getId, ContextValue::getValue);
    }

    /** Indexes a list of id/value holders into {@link #values}, keeping the first non-null value per id. */
    private <T> void index(List<T> items, Function<T, String> id, Function<T, Object> value) {
      if (items == null) return;
      for (T item : items) {
        Object v = value.apply(item);
        if (v != null) values.putIfAbsent(id.apply(item), v);
      }
    }
  }
}
