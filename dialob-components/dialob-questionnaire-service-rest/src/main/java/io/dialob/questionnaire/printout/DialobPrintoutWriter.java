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

import java.io.StringWriter;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONWriter;

import io.dialob.api.form.Form;
import io.dialob.api.form.FormItem;
import io.dialob.api.form.FormValueSet;
import io.dialob.api.form.FormValueSetEntry;
import io.dialob.api.questionnaire.Answer;
import io.dialob.api.questionnaire.ContextValue;
import io.dialob.api.questionnaire.Questionnaire;
import io.dialob.questionnaire.printout.ActiveWhenEvaluator.NameResolver;

/**
 * Serializes a completed questionnaire into the generic printout body JSON
 * (id, metadata, formMetadata, contextValues, form, pages, items, groups) consumed
 * downstream (e.g. a Tagomi template) to render a PDF.
 *
 * <p>The body is built from the static {@link Form} (structure, labels, value sets) and the
 * stored {@link Questionnaire} answers — not from the live session — because a completed Dialob
 * session is frozen and no longer exposes the computed hierarchy, labels or visibility. Item
 * visibility ({@code activeWhen}) and {@code {variable}} label substitution are therefore
 * re-derived here from the stored answers.
 */
public class DialobPrintoutWriter {

  private static final String FALLBACK_LANG = "en";
  private static final String PRINTOUT_DISABLE_PROPERTY = "noPrint";
  private static final DateTimeFormatter ISO_DATE_TIME = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
  private final Pattern substitutePattern = Pattern.compile("\\{([\\w:]+?)\\}");

  private final ActiveWhenEvaluator evaluator = new ActiveWhenEvaluator();

  public String writePrintout(Form form, Questionnaire questionnaire, String language, ZoneId timezone) {
    Ctx ctx = new Ctx(form, questionnaire, language == null ? FALLBACK_LANG : language);
    StringWriter buffer = new StringWriter();
    JSONWriter w = new JSONWriter(buffer);

    w.object();
    w.key("id").value(questionnaire.getId());
    writeMetadata(questionnaire, w, timezone);
    writeFormMetadata(form, w, timezone);
    writeContextValues(questionnaire, w);

    FormItem root = findRoot(form);
    writeForm(root, w);

    Set<String> groupList = new HashSet<>();
    writePages(ctx, w, root);
    collectAllGroups(ctx, root.getId(), groupList);
    Set<String> itemList = collectSubitems(ctx, groupList);
    writeSubitems(ctx, w, itemList);
    writeGroups(ctx, w, groupList, itemList);

    w.endObject();
    return buffer.toString();
  }

  private void writePages(Ctx ctx, JSONWriter w, FormItem root) {
    w.key("pages").object().key("byId").object();
    for (String pageId : root.getItems()) {
      FormItem page = findItem(ctx.form, pageId);
      w.key(page.getId()).object();
      w.key("type").value(page.getType());
      w.key("label").value(getLabel(page, ctx.lang));
      w.key("hiddenPrint").value(isHiddenPrint(page, ctx));
      w.key("groupIds").value(page.getItems());
      w.endObject();
    }
    w.endObject();
    w.key("pageIds").value(root.getItems());
    w.endObject();
  }

  private void writeForm(FormItem root, JSONWriter w) {
    w.key("form").object().key("pages").value(root.getItems()).endObject();
  }

  private void collectAllGroups(Ctx ctx, String groupName, Set<String> groupList) {
    FormItem item = findItem(ctx.form, groupName);
    for (String childName : item.getItems()) {
      FormItem child = findItem(ctx.form, childName);
      if (child != null && "group".equals(child.getType())) {
        if (!isInactive(child, ctx)) {
          groupList.add(childName);
          collectAllGroups(ctx, childName, groupList);
        }
      }
    }
  }

  private Set<String> collectSubitems(Ctx ctx, Set<String> groupList) {
    Set<String> itemList = new HashSet<>();
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

  private void writeGroups(Ctx ctx, JSONWriter w, Set<String> groupItems, Set<String> groupSubitems) {
    Set<String> filtered = new HashSet<>();
    for (String name : groupItems) {
      if (filterGroup(findItem(ctx.form, name), ctx, groupSubitems)) {
        filtered.add(name);
      }
    }
    w.key("groups").object().key("byId").object();
    for (String name : filtered) {
      FormItem item = findItem(ctx.form, name);
      w.key(item.getId()).object();
      w.key("type").value(item.getType());
      w.key("label").value(getLabel(item, ctx.lang));
      w.key("hiddenPrint").value(isHiddenPrint(item, ctx));
      w.key("itemIds").array();
      for (String sub : item.getItems()) {
        if (groupSubitems.contains(sub) || groupItems.contains(sub)) {
          w.value(sub);
        }
      }
      w.endArray();
      w.endObject();
    }
    w.endObject();
    w.key("allIds").value(groupItems);
    w.endObject();
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

  private void writeSubitems(Ctx ctx, JSONWriter w, Set<String> items) {
    w.key("items").object().key("byId").object();
    for (Iterator<String> it = items.iterator(); it.hasNext();) {
      String itemId = it.next();
      FormItem item = findItem(ctx.form, itemId);
      if (item == null) { it.remove(); continue; }
      if ("rowgroup".equals(item.getType())) {
        List<String> rows = writeRowgroupAnswer(ctx, w, item);
        writeItem(w, ctx, item, null, rows, itemId);
      } else if (!writeRegularItem(ctx, w, itemId, item)) {
        it.remove();
      }
    }
    w.endObject();
    w.key("allIds").value(items);
    w.endObject();
  }

  /** @return false (and the caller drops the item) when it has no answer and is not a note. */
  private boolean writeRegularItem(Ctx ctx, JSONWriter w, String itemId, FormItem item) {
    Object answerValue = ctx.values.get(itemId);
    Object value = answerValue == null ? null : mapAnswerValue(item, answerValue, ctx);
    if (value != null) {
      writeItem(w, ctx, item, value, answerValue, itemId);
      return true;
    }
    if ("note".equals(item.getType())) {
      writeNote(ctx, w, item, itemId);
      return true;
    }
    return false;
  }

  private List<String> writeRowgroupAnswer(Ctx ctx, JSONWriter w, FormItem item) {
    Object value = ctx.values.get(item.getId());
    List<String> result = new ArrayList<>();
    if (value instanceof List) {
      List<?> indexes = (List<?>) value;
      for (Object idxObj : indexes) {
        String rowId = item.getId() + "." + idxObj;
        result.add(rowId);
        List<String> cellIds = new ArrayList<>();
        for (String columnName : item.getItems()) {
          FormItem columnItem = findItem(ctx.form, columnName);
          String cellId = rowId + "." + columnName;
          writeRegularItem(ctx, w, cellId, columnItem);
          cellIds.add(cellId);
        }
        writeItem(w, ctx, item, null, cellIds, rowId);
      }
    }
    return result;
  }

  private void writeNote(Ctx ctx, JSONWriter w, FormItem item, String itemId) {
    w.key(itemId).object();
    w.key("type").value(item.getType());
    w.key("hiddenPrint").value(isHiddenPrint(item, ctx));
    w.key("key").value(getLabel(item, ctx.lang));
    w.key("label").value(resolveLabel(item, ctx));
    w.endObject();
  }

  private void writeItem(JSONWriter w, Ctx ctx, FormItem item, Object value, Object answerValue, String itemId) {
    w.key(itemId).object();
    w.key("type").value(item.getType());
    w.key("label").value(resolveLabel(item, ctx));
    w.key("hiddenPrint").value(isHiddenPrint(item, ctx));
    w.key("key").value(answerValue);
    w.key("value").value(value);
    w.endObject();
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

  private String resolveLabel(FormItem item, Ctx ctx) {
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
    if (v != null) return String.valueOf(v);
    FormItem fi = ctx.form.getData().get(name);
    if (fi != null && "number".equals(fi.getType())) return "0";
    return "-";
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

  private boolean isInactive(FormItem item, Ctx ctx) {
    String aw = item.getActiveWhen();
    if (aw == null || aw.isBlank()) return false;
    return !evaluator.isActive(aw, ctx.resolver);
  }

  private void writeMetadata(Questionnaire q, JSONWriter w, ZoneId tz) {
    w.key("metadata").object();
    var m = q.getMetadata();
    w.key("created").value(writeDateTime(m.getCreated(), tz));
    w.key("creator").value(m.getCreator());
    w.key("formId").value(m.getFormId());
    w.key("formRev").value(m.getFormRev());
    w.key("label").value(m.getLabel());
    w.key("language").value(m.getLanguage());
    w.key("lastAnswer").value(writeDateTime(m.getLastAnswer(), tz));
    w.key("owner").value(m.getOwner());
    w.key("status").value(String.valueOf(m.getStatus()));
    w.key("tenantId").value(m.getTenantId());
    w.endObject();
  }

  private void writeFormMetadata(Form form, JSONWriter w, ZoneId tz) {
    w.key("formMetadata").object();
    var m = form.getMetadata();
    w.key("created").value(writeDateTime(m.getCreated(), tz));
    w.key("creator").value(m.getCreator());
    w.key("label").value(m.getLabel());
    w.key("lastSaved").value(writeDateTime(m.getLastSaved(), tz));
    w.key("savedBy").value(m.getSavedBy());
    w.key("tenantId").value(m.getTenantId());
    w.endObject();
  }

  private void writeContextValues(Questionnaire q, JSONWriter w) {
    w.key("contextValues").object();
    if (q.getContext() != null) {
      for (ContextValue cv : q.getContext()) {
        w.key(cv.getId()).value(cv.getValue());
      }
    }
    w.endObject();
  }

  private Object writeDateTime(Instant instant, ZoneId tz) {
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
    final Map<String, Object> values = new HashMap<>();
    final NameResolver resolver;

    Ctx(Form form, Questionnaire q, String lang) {
      this.form = form;
      this.lang = lang;

      if (q.getAnswers() != null) {
        for (Answer a : q.getAnswers()) {
          if (a.getValue() != null) values.putIfAbsent(a.getId(), a.getValue());
        }
      }
      if (q.getVariableValues() != null) {
        for (var v : q.getVariableValues()) {
          if (v.getValue() != null) values.putIfAbsent(v.getId(), v.getValue());
        }
      }
      if (q.getContext() != null) {
        for (ContextValue c : q.getContext()) {
          if (c.getValue() != null) values.putIfAbsent(c.getId(), c.getValue());
        }
      }
      this.resolver = new NameResolver() {
        @Override public Object value(String name) { return values.get(name); }
        @Override public boolean isAnswered(String name) {
          Object v = values.get(name);
          if (v == null) return false;
          if (v instanceof Collection<?> c) return !c.isEmpty();
          return !String.valueOf(v).isEmpty();
        }
      };
    }
  }
}
