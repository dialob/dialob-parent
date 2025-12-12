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
package io.dialob.session.engine.program.model;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public record Label(
  @NonNull Map<String, String> labels
) implements ProgramNode {

  public static final Label EMPTY_LABEL = new Label(Map.of());

  public Label {
    labels = Map.copyOf(labels);
  }

  @NonNull
  public static Label of(@NonNull Map<String,String> map) {
    return new Label(map);
  }

  @NonNull
  public Label with(@NonNull String label, @NonNull String value) {
    var map = new HashMap<>(labels);
    map.put(label, value);
    return Label.of(map);
  }

  @Nullable
  public String getLabel(@NonNull String language) {
    return labels().get(language);
  }
}
