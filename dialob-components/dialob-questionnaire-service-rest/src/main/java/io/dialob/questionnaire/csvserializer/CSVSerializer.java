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
package io.dialob.questionnaire.csvserializer;

import io.dialob.api.form.*;
import io.dialob.api.questionnaire.Answer;
import io.dialob.api.questionnaire.ContextValue;
import io.dialob.api.questionnaire.Questionnaire;
import io.dialob.common.Constants;
import io.dialob.questionnaire.service.api.QuestionnaireDatabase;
import io.dialob.security.tenant.CurrentTenant;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.IOException;
import java.io.StringWriter;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class CSVSerializer {
  private static final String LABEL_LANGUAGE = "en";
  private static final Map<String, Map<Boolean, String>> BOOLEAN_TRANSLATION = Map.of("en", Map.of(Boolean.TRUE, "Yes", Boolean.FALSE, "No"),
                                                                                      "fi", Map.of(Boolean.TRUE, "Kyll\u00E4", Boolean.FALSE, "Ei"),
                                                                                      "sv", Map.of(Boolean.TRUE, "Ja", Boolean.FALSE, "Nej")
                                                                                      );
  private static final String MULTICHOICE_DELIMITER = ", ";

  private static final Set<String> IGNORED_TYPES = Set.of(
    Constants.QUESTIONNAIRE,
    Constants.PAGE,
    Constants.GROUP,
    Constants.NOTE,
    Constants.ROWGROUP,
    Constants.SURVEYGROUP
  );
  private final QuestionnaireDatabase questionnaireDatabase;
  private final CurrentTenant currentTenant;

  public CSVSerializer(QuestionnaireDatabase questionnaireDatabase, CurrentTenant currentTenant) {
    this.questionnaireDatabase = questionnaireDatabase;
    this.currentTenant = currentTenant;
  }

  private Optional<FormValueSetEntry> getChoice(Form form, String valueSetId, Object answer) {
    Optional<FormValueSet> valueSet = form.getValueSets().stream().filter(vs -> vs.getId().equals(valueSetId)).findFirst();
    return valueSet.flatMap(formValueSet -> formValueSet.getEntries().stream().filter(vse -> vse.getId().equals(answer)).findFirst());
  }

  private String getValuesetAnswer(Form form, Questionnaire questionnaire, String valueSetId, String valueKey) {
    Optional<FormValueSetEntry> value = getChoice(form, valueSetId, valueKey);
    if (value.isPresent()) {
      return value.get().getLabel().get(questionnaire.getMetadata().getLanguage());
    } else {
      return valueKey;
    }
  }

  private Optional<Answer> getAnswer(Questionnaire questionnaire, String itemId) {
    return questionnaire.getAnswers().stream().filter(answer -> answer.getId().equals(itemId)).findFirst();
  }

  private String serializeBoolean(Boolean value, String language) {
    Map<Boolean, String> translation = BOOLEAN_TRANSLATION.get(language);
    if (translation == null) {
      translation = BOOLEAN_TRANSLATION.get(LABEL_LANGUAGE);
    }
    return translation.get(value);
  }

  @SuppressWarnings("unchecked")
  private void serializeAnswer(Form form, Questionnaire questionnaire, FormItem formItem,
                               String answerId, List<String> records, String language, String effectiveValueSetId) throws IOException {
    Optional<Answer> a = getAnswer(questionnaire, answerId != null ? answerId : formItem.getId());

    String realValue = "";
    String valueKey = "";

    String valueSetId = formItem.getType().equals("survey") ? effectiveValueSetId : formItem.getValueSetId();

    if (null != valueSetId) {
      Optional<FormValueSet> valueSet = form.getValueSets().stream().filter(vs -> vs.getId().equals(valueSetId)).findFirst();
      if (valueSet.isPresent() && formItem.getType().equals("multichoice")) {
        // for multichoices export each possible selection in separate column as 0 or 1 for text value and then key value.
        // this allows easy creation of charts by using count aggregation.
        // comma-separated multichoice values in one column are exported as next step in this method.
        valueSet.get().getEntries().forEach(entry  -> {
          String val = "0";
          String key = null;
          if (a.isPresent() && a.get().getValue() != null) {
            Optional<String> matchingValue = ((Collection<String>) a.get().getValue())
              .stream().filter(value -> value.equals(entry.getId())).findFirst();
            if (matchingValue.isPresent()) {
              val = "1";
              key = entry.getId();
            }
          }
          records.add(val);
          records.add(key);
        });
      }
    }
    if (a.isPresent() && a.get().getValue() != null) {
      if (null != valueSetId) {
        // Multichoice
        if (a.get().getValue() instanceof Collection<?> collection) {
          realValue = collection
            .stream()
            .map(String.class::cast)
            .map(key -> getValuesetAnswer(form, questionnaire, valueSetId, key))
            .collect(Collectors.joining(MULTICHOICE_DELIMITER));
          valueKey = String.join(MULTICHOICE_DELIMITER, ((Collection<String>) a.get().getValue()));
        } else {
          valueKey = a.get().getValue().toString();
          realValue = getValuesetAnswer(form, questionnaire, valueSetId, valueKey);
        }
      } else {
        // Booleans translated
        if ("BOOLEAN".equals(a.get().getType())) {
          realValue = serializeBoolean(Boolean.valueOf(a.get().getValue().toString()), language);
          valueKey = a.get().getValue().toString();
        } else {
          // "Normal" values
          realValue = a.get().getValue().toString();
        }
      }
    }

    records.add(realValue);
    records.add(valueKey);
  }


  private void serializeItem(Form form, Questionnaire questionnaire,
                             FormItem formItem, String answerId, List<String> records, String language, String effectiveValueSetId) {
    try {
      // Skip items with export=false prop set
      Object exportFlag = formItem.getProps() != null ? formItem.getProps().get("export") : null;
      if (exportFlag != null && "false".equals(exportFlag.toString())) {
        return;
      }

      // Special handling for surveyGroup
      if ("surveygroup".equals(formItem.getType())) {
        String surveyGroupValueSet =  formItem.getValueSetId();
        formItem.getItems().stream().map(itemId -> form.getData().get(itemId)).forEach(
          item -> serializeItem(form, questionnaire, item, null, records, language, surveyGroupValueSet));
        return;
      }

      if (IGNORED_TYPES.contains(formItem.getType())) {
        return;
      }
      // Container items
      if (null != formItem.getItems() && !formItem.getItems().isEmpty()) {
        // Rowgroup handling
        if ("rowgroup".equals(formItem.getType())) {
          Optional<Answer> a = getAnswer(questionnaire, formItem.getId());
          if (a.isPresent()) {
            List<Integer> rows = (List<Integer>) a.get().getValue();
            if (rows != null) {
              if (!rows.isEmpty()) {
                // Rowgroup label row
                records.add(formItem.getLabel().get(LABEL_LANGUAGE));
                records.add(formItem.getId());
              }
              // Rowgroup items
              rows.forEach(row -> {
                formItem.getItems().stream().map(itemId -> form.getData().get(itemId))
                  .forEach(item -> serializeItem(form, questionnaire, item, "%s.%d.%s".formatted(formItem.getId(), row, item.getId()), records, language, null));
              });
            }
          }
        } else {
          // Container items
          formItem.getItems().stream().map(itemId -> form.getData().get(itemId)).forEach(
            item -> serializeItem(form, questionnaire, item, null, records, language, null));
        }
      } else {
        // "Normal" items
        if ("survey".equals(formItem.getType()) && effectiveValueSetId == null) {
          return;
        }
        serializeAnswer(form, questionnaire, formItem, answerId, records, language, effectiveValueSetId);
      }
    } catch (IOException e) {
      LOGGER.error("CSV Serialization error", e);
    }
  }

  private void serializeContextValue(ContextValue cv, CSVPrinter printer) {
    try {
      printer.printRecord(
        cv.getId(),
        "",
        cv.getValue()
      );
    } catch (IOException e) {
      LOGGER.error("CSV Serialization Error", e);
    }
  }

  private String dedupLabel(String label, Map<String, Integer> labelDedups) {
    // GDS does not accept headers with same value, if there are questions with same text then add distinguishing index
    if (labelDedups.containsKey(label)) {
      Integer index = labelDedups.get(label) + 1;
      labelDedups.put(label, index);
      label = index + ". " + label;
    }
    else {
      labelDedups.put(label, 0);
    }
    return label;
  }

  private void addHeaderFormItem(Form form, FormItem formItem, Map<String, Integer> labelDedups, List<String> result, String language) {
    Object exportFlag = formItem.getProps() != null ? formItem.getProps().get("export") : null;
    if (!(exportFlag != null && "false".equals(exportFlag.toString())) && !IGNORED_TYPES.contains(formItem.getType())) {
      String label = formItem.getLabel().get(language);
      if (formItem.getType().equals("multichoice")) {
        // for multichoice add column for each possible value
        String selectionId = formItem.getValueSetId();
        Optional<FormValueSet> values = form.getValueSets().stream().filter(valueSet -> valueSet.getId().equals(selectionId)).findFirst();
        values.ifPresent(formValueSet -> formValueSet.getEntries().forEach(entry -> {
          result.add(dedupLabel(entry.getLabel().get(language), labelDedups));
          result.add(dedupLabel(entry.getId(), labelDedups));
        }));
      }
      result.add(dedupLabel(label, labelDedups));
      result.add(formItem.getId());
    }
  }

  public String[] serializeHeader(Form form, String language) {
    List<String> result = new ArrayList<>();
    Map<String, Integer> labelDedups = new HashMap<>();
    form.getData().values().forEach(formItem -> {
      addHeaderFormItem(form, formItem, labelDedups, result, language);
    });
    form.getVariables().forEach(variable -> {
      if (Boolean.TRUE.equals(variable.getContext())) {
        result.add(variable.getName());
      }
    });
    return result.toArray(String[]::new);
  }


  private void serializeVariable(Variable variable, Questionnaire questionnaire, List<String> records) {
    if (Boolean.TRUE.equals(variable.getContext())) {
      Optional<ContextValue> var = questionnaire.getContext().stream().filter(varValue -> variable.getName().equals(varValue.getId())).findFirst();
      if (var.isPresent()) {
        records.add(var.get().getValue().toString());
      }
      else {
        records.add("");
      }
    }

  }

  private void serialize(Questionnaire questionnaire, Form form, CSVPrinter printer, String language) throws IOException {
    List<String> records = new ArrayList<>();
    form.getData().values().forEach(item -> serializeItem(form, questionnaire, item, null, records, language, null));
    for (Variable variable : form.getVariables()) {
      serializeVariable(variable, questionnaire, records);
    }
    printer.printRecord(records);
  }

  public String serializeQuestionnaires(String[] sessionIds, Form form, Locale locale) throws IOException {
    var language = locale.getLanguage();
    CSVPrinter printer = null;
    StringWriter out = new StringWriter();
    try {
      printer = new CSVPrinter(out, CSVFormat.DEFAULT.withHeader(serializeHeader(form, language)));
      for (String sessionId : sessionIds) {
        Questionnaire questionnaire = questionnaireDatabase.findOne(currentTenant.getId(), sessionId);
        serialize(questionnaire, form, printer, language);
      }
    }
    finally {
      if (printer != null) {
        printer.close();
      }
    }
    return out.toString();
  }
}
