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

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BooleanLabelFormatterTest {

  @Test
  void shouldLocalizeEnglishAndFinnish() {
    assertEquals("Yes", BooleanLabelFormatter.of("en").labelFor(true));
    assertEquals("No", BooleanLabelFormatter.of("en").labelFor(false));
    assertEquals("Kyllä", BooleanLabelFormatter.of("fi").labelFor(true));
    assertEquals("Ei", BooleanLabelFormatter.of("fi").labelFor(false));
  }

  @Test
  void shouldLocalizeGerman() {
    assertEquals("Ja", BooleanLabelFormatter.of("de").labelFor(true));
    assertEquals("Nein", BooleanLabelFormatter.of("de").labelFor(false));
  }

  @Test
  void shouldFallbackFromRegionTagToLanguage() {
    assertEquals("Ja", BooleanLabelFormatter.of("de-AT").labelFor(true));
    assertEquals("Nein", BooleanLabelFormatter.of("de-AT").labelFor(false));
  }

  @Test
  void shouldFallbackToEnglishForUnknownLanguage() {
    assertEquals("Yes", BooleanLabelFormatter.of("zz").labelFor(true));
    assertEquals("No", BooleanLabelFormatter.of("zz").labelFor(false));
  }

  @Test
  void shouldIncludeComposerIsoLanguageCodes() {
    Map<String, BooleanLabelFormatter.Labels> labels = BooleanLabelFormatter.loadLabels();
    assertTrue(labels.size() >= 180);
    for (String code : new String[] {"en", "fi", "sv", "et", "ms", "de", "fr", "pl", "zh", "ar"}) {
      assertTrue(labels.containsKey(code), "Missing boolean labels for " + code);
    }
  }
}
