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
package io.dialob.db.jdbi.model;

import edu.umd.cs.findbugs.annotations.NonNull;
import org.apache.commons.lang3.StringUtils;

import java.time.Instant;
import java.util.Objects;

public record Form(
  @NonNull Form.Id id,
  String label,
  @NonNull FormDocument.Id latestForm,
  @NonNull Instant created,
  @NonNull Instant updated
) {

  public Form {
    if (!Objects.equals(StringUtils.trim(id.tenantId()), StringUtils.trim(latestForm.tenantId()))) {
      throw new IllegalArgumentException("Form and latest form document must belong to same tenant");
    }
  }

  public static Form.Id id(@NonNull String tenantId,
                           @NonNull String name
  ) {
    return new Form.Id(tenantId, name);
  }

  public record Id(
    @NonNull String tenantId,
    @NonNull String name
  ) {
  }
}
