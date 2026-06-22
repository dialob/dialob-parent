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

import java.util.List;
import java.util.Map;

/**
 * Typed model of a completed questionnaire's serialized session state (id, metadata, formMetadata,
 * contextValues, form, pages, groups, items). Its primary consumer is a printout pipeline (e.g. a
 * Tagomi template rendering a PDF), but the data is a plain Dialob session view. Serialized with
 * Jackson by {@link DialobPrintoutWriter}. Field declaration order matches the emitted JSON key
 * order; {@code null}s are emitted (the consumer relies on the keys being present).
 */
public record SessionState(
  String id,
  Metadata metadata,
  FormMetadata formMetadata,
  Map<String, Object> contextValues,
  FormRef form,
  Pages pages,
  Items items,
  Groups groups
) {

  public record Metadata(
    String created,
    String creator,
    String formId,
    String formRev,
    String label,
    String language,
    String lastAnswer,
    String owner,
    String status,
    String tenantId
  ) {}

  public record FormMetadata(
    String created,
    String creator,
    String label,
    String lastSaved,
    String savedBy,
    String tenantId
  ) {}

  public record FormRef(List<String> pages) {}

  public record Pages(Map<String, Page> byId, List<String> pageIds) {}

  public record Page(String type, String label, boolean hiddenPrint, List<String> groupIds) {}

  /** byId values are either {@link Item} (questions, rowgroups, rows, cells) or {@link Note}. */
  public record Items(Map<String, Object> byId, List<String> allIds) {}

  public record Item(String type, String label, boolean hiddenPrint, Object key, Object value) {}

  public record Note(String type, boolean hiddenPrint, Object key, String label) {}

  public record Groups(Map<String, Group> byId, List<String> allIds) {}

  public record Group(String type, String label, boolean hiddenPrint, List<String> itemIds) {}
}
