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
package io.dialob.integration.api.event;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import edu.umd.cs.findbugs.annotations.NonNull;
import io.dialob.security.tenant.Tenant;
import org.immutables.value.Value;

@Value.Builder
@JsonSerialize(as = FormDeletedEvent.class)
@JsonDeserialize(builder = FormDeletedEventBuilder.class)
@JsonInclude(content = JsonInclude.Include.NON_NULL, value = JsonInclude.Include.NON_EMPTY)
public record FormDeletedEvent(
  @NonNull Tenant tenant,
  @NonNull String source,
  @NonNull String formId
) implements FormEvent, DistributedEvent {

  @Override
  public String getSource() {
    return source;
  }

  @NonNull
  @Override
  public String getFormId() {
    return formId;
  }

  @NonNull
  @Override
  public Tenant getTenant() {
    return tenant;
  }
}
