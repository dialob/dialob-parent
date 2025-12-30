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
import edu.umd.cs.findbugs.annotations.Nullable;

public record ErrorId(
  @NonNull ItemId itemId,
  @Nullable String code
) implements ItemId {

  public static ErrorId of(@NonNull ItemId itemId, @Nullable String code) {
    return new ErrorId(itemId, code);
  }

  @Override
  public String getValue() {
    // TODO
    return this.code;
  }

  public ErrorId withItemId(ItemId itemId) {
    return new ErrorId(itemId, this.code);
  }

  @Override
  public ItemId parent() {
    return this.itemId;
  }

  @Override
  public boolean isPartial() {
    return this.itemId.isPartial();
  }
}

