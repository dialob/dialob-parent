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

import java.util.List;

@org.immutables.value.Value.Builder
@org.immutables.value.Value.Style(jdkOnly = true, jdk9Collections = true, overshadowImplementation = true, visibility = org.immutables.value.Value.Style.ImplementationVisibility.PACKAGE)
public record ValueSet(

  @NonNull
  String id,

  @NonNull
  List<Value<Entry>> entries

) implements ProgramNode {

  public static class Builder extends ValueSetBuilder { }

  public record Entry(
    String key,
    @NonNull Expression label
  ) implements ProgramNode {

  }
}
