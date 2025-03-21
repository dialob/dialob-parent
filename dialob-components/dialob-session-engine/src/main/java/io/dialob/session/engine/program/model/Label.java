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
import org.immutables.value.Value;

import java.util.Map;

@Value.Immutable
public interface Label extends ProgramNode {

  @NonNull
  static Label createLabel(@NonNull Map<String,String> map) {
    return ImmutableLabel.builder().putAllLabels(map).build();
  }

  @NonNull
  Map<String, String> getLabels();

  @Nullable
  default String getLabel(@NonNull String language) {
    return getLabels().get(language);
  }
}
