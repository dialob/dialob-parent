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
package io.dialob.common;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class BooleanLabelFormatter {

  private static final String RESOURCE = "/io/dialob/common/boolean-labels.json";

  private static final Pattern ENTRY_PATTERN = Pattern.compile(
    "\"([a-z]{2})\"\\s*:\\s*\\{\\s*\"true\"\\s*:\\s*\"([^\"]*)\"\\s*,\\s*\"false\"\\s*:\\s*\"([^\"]*)\"\\s*\\}"
  );

  private static final Map<String, Labels> LABELS = loadLabels();

  private final String languageTag;

  private BooleanLabelFormatter(@Nullable String languageTag) {
    this.languageTag = languageTag;
  }

  @NonNull
  public static BooleanLabelFormatter of(@Nullable String languageTag) {
    return new BooleanLabelFormatter(languageTag);
  }

  @NonNull
  public String labelFor(boolean value) {
    Labels labels = resolveLabels(languageTag);
    if (labels == null) {
      labels = LABELS.get("en");
    }
    if (labels == null) {
      return value ? "true" : "false";
    }
    return value ? labels.trueLabel() : labels.falseLabel();
  }

  @Nullable
  private static Labels resolveLabels(@Nullable String languageTag) {
    if (languageTag == null || languageTag.isBlank()) {
      return LABELS.get("en");
    }
    Labels labels = LABELS.get(languageTag.toLowerCase(Locale.ROOT));
    if (labels != null) {
      return labels;
    }
    int dash = languageTag.indexOf('-');
    if (dash > 0) {
      labels = LABELS.get(languageTag.substring(0, dash).toLowerCase(Locale.ROOT));
      if (labels != null) {
        return labels;
      }
    }
    return LABELS.get("en");
  }

  @NonNull
  static Map<String, Labels> loadLabels() {
    try (InputStream in = BooleanLabelFormatter.class.getResourceAsStream(RESOURCE)) {
      if (in == null) {
        throw new IllegalStateException("Missing boolean label resource: " + RESOURCE);
      }
      String json = new String(in.readAllBytes(), StandardCharsets.UTF_8);
      return parseLabels(json);
    } catch (IOException e) {
      throw new IllegalStateException("Failed to load boolean labels", e);
    }
  }

  @NonNull
  static Map<String, Labels> parseLabels(@NonNull String json) {
    Map<String, Labels> labels = new HashMap<>();
    Matcher matcher = ENTRY_PATTERN.matcher(json);
    while (matcher.find()) {
      labels.put(matcher.group(1), new Labels(matcher.group(2), matcher.group(3)));
    }
    if (labels.isEmpty()) {
      throw new IllegalStateException("No boolean labels parsed from resource");
    }
    return Collections.unmodifiableMap(labels);
  }

  record Labels(@NonNull String trueLabel, @NonNull String falseLabel) {}
}
