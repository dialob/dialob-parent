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
package io.dialob.form.service;

import io.dialob.api.form.*;
import io.dialob.form.service.api.validation.CsvToFormParser;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.IOException;
import java.io.StringReader;
import java.util.*;
import java.util.stream.Collectors;

public class DialobCsvToFormParser implements CsvToFormParser {

  private static final List<String> EXPECTED_HEADERS = List.of("id", "type");
  private static final Map<String, String> ITEM_TYPE_MAP = Map.of(
    "Text", "text",
    "Boolean", "boolean",
    "Date", "date",
    "Time", "time",
    "Choice", "list",
    "Note", "note",
    "Integer", "number"
  );

  @Override
  public Form parseCsv(String formCsv) {
    if (formCsv == null || formCsv.isBlank()) {
      throw new CsvParsingException("CSV data is empty or null.");
    }

    try (StringReader reader = new StringReader(formCsv);
      CSVParser preParser = new CSVParser(reader, CSVFormat.DEFAULT)) {

      // Validating first row and header row
      List<CSVRecord> preRecords = preParser.getRecords();
      if (preRecords.size() < 2) {
        throw new CsvParsingException("CSV does not contain enough rows for headers and data.");
      }

      String formName = preRecords.get(0).get(0);
      if (formName == null || formName.isBlank()) {
        throw new CsvParsingException("Technical name of the dialog is missing in the first row.");
      }
      if (formName.contains(" ")) {
        throw new CsvParsingException("Technical name of the dialog must be a single word without spaces.");
      }

      CSVRecord headerRow = preRecords.get(1);

      if (headerRow.size() < EXPECTED_HEADERS.size() ||
        !headerRow.get(0).equalsIgnoreCase(EXPECTED_HEADERS.get(0)) ||
        !headerRow.get(1).equalsIgnoreCase(EXPECTED_HEADERS.get(1))) {
        throw new CsvParsingException("Incorrect header row. Expected the first two columns to be 'id' and 'type'.");
      }

      List<String> headers = new ArrayList<>(EXPECTED_HEADERS);

      Set<String> languages = new HashSet<>();
      for (int i = 2; i < headerRow.size(); i++) {
        String language = headerRow.get(i);
        if (language != null && !language.isBlank() && language.matches("^[a-z]{2}$")) {
          headers.add(language);
          languages.add(language);
        }
      }

      if (languages.isEmpty()) {
        throw new CsvParsingException("CSV headers must contain at least one valid language.");
      }

      CSVFormat csvFormat = CSVFormat.DEFAULT
        .withHeader(headers.toArray(String[]::new))
        .withSkipHeaderRecord(true);

      try (StringReader fullReader = new StringReader(formCsv);
           CSVParser csvParser = new CSVParser(fullReader, csvFormat)) {
        List<CSVRecord> records = csvParser.getRecords();
        return processRecords(records, languages, formName);
      }catch (IOException e) {
        throw new CsvParsingException("Error processing CSV records: " + e.getMessage());
      }
    } catch (IOException e) {
      throw new CsvParsingException("Error reading the CSV data. Error message: " + e.getMessage());
    }
  }

  private Form processRecords(List<CSVRecord> records, Set<String> languages, String formName) {
      // Create metadata and form
      ImmutableForm.Builder formBuilder = ImmutableForm.builder();
      formBuilder.name(formName);

      ImmutableFormMetadata metadata = ImmutableFormMetadata.builder()
        .label(formName)
        .languages(languages)
        .build();
      formBuilder.metadata(metadata);

      Map<String, FormItem> formItems = new LinkedHashMap<>();

      // Create questionnaire item
      formItems.put("questionnaire", createFormItem("questionnaire", "questionnaire", null, List.of("page1"), languages).build());

      // Create Page 1 item
      formItems.put("page1", createFormItem("page1", "group", "page", List.of("group1"), languages).build());

      // Create Group 1 item
      List<String> groupItemIds = new ArrayList<>();
      ImmutableFormItem.Builder groupBuilder = createFormItem("group1", "group", null, groupItemIds, languages);
      formItems.put("group1", groupBuilder.build());

      // Process remaining rows into FormItems
      Map<String, Integer> typeCounters = new HashMap<>();
      for (int i = 1; i < records.size(); i++) {
        CSVRecord record = records.get(i);
        if (isEmptyRow(record)) {
          continue;
        }

        // Extract and validate type
        String type = validateAndMapType(record.get("type"));

        // Extract or generate ID
        String id = validateAndGenerateId(record.get("id"), type, typeCounters, formItems);

        // Extract translations for each language
        Map<String, String> labels = extractLabels(record, languages);

        // Create FormItem
        ImmutableFormItem.Builder itemBuilder = ImmutableFormItem.builder();
        itemBuilder.id(id).type(type).label(labels);
        if (type.equals("text")) {
          itemBuilder.view("text");
        }

        groupItemIds.add(id);
        formItems.put(id, itemBuilder.build());
      }

      // Build form
      formItems.put("group1", groupBuilder.items(groupItemIds).build());
      formBuilder.data(formItems);
      return formBuilder.build();
  }

    private boolean isEmptyRow(CSVRecord record) {
      return record.stream().allMatch(value -> value == null || value.isBlank());
    }

  private ImmutableFormItem.Builder createFormItem(String id, String type, String view, List<String> items, Set<String> languages) {
    ImmutableFormItem.Builder builder = ImmutableFormItem.builder()
      .id(id)
      .type(type)
      .label(createEmptyLabels(languages));
    if (view != null) {
      builder.view(view);
    }
    if (items != null) {
      builder.items(items);
    }
    return builder;
  }

  private Map<String, String> createEmptyLabels(Set<String> languages) {
    return languages.stream().collect(Collectors.toMap(lang -> lang, lang -> ""));
  }

  private String validateAndMapType(String type) {
    if (type == null || type.isBlank()) {
      throw new CsvParsingException("Item type is missing or empty.");
    }

    String trimmedType = type.trim();
    String mappedType = ITEM_TYPE_MAP.entrySet().stream()
      .filter(entry -> entry.getKey().equalsIgnoreCase(trimmedType))
      .map(Map.Entry::getValue)
      .findFirst()
      .orElse(null);

    if (mappedType == null) {
      throw new CsvParsingException("Invalid item type: " + type);
    }

    return mappedType;
  }

  private String validateAndGenerateId(String id, String type, Map<String, Integer> typeCounters, Map<String, FormItem> formItems) {
    if (id == null || id.isBlank()) {
      int count = typeCounters.getOrDefault(type, 0) + 1;
      typeCounters.put(type, count);
      return type + count;
    }
    if (formItems.containsKey(id)) {
      throw new CsvParsingException("Duplicate form item ID found: " + id + ". Item ID's must be unique.");
    }
    return id;
  }

  private Map<String, String> extractLabels(CSVRecord record, Set<String> languages) {
    return languages.stream()
      .collect(Collectors.toMap(
        lang -> lang,
        lang -> record.isMapped(lang) ? record.toMap().getOrDefault(lang, "") : ""
      ));
  }
}
