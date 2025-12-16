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
package io.dialob.session.engine.session.model;

import edu.umd.cs.findbugs.annotations.NonNull;
import io.dialob.api.annotation.Nullable;

import java.util.Optional;

public record ItemRef(
  @NonNull String id,
  @Nullable ItemId parent
) implements ItemId {

  public static ItemRef of(String id, Optional<? extends ItemId> parent) {
    return new ItemRef(id, parent.orElse(null));
  }

  @NonNull
  public String getId() {
    return id;
  }

  public String getValue() {
    return id;
  }

  @Override
  public <I extends ItemId> ItemId withParent(I parent) {
    return new ItemRef(this.id, parent);
  }

}
