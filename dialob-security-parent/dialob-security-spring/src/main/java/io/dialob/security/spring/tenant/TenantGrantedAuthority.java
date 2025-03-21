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
package io.dialob.security.spring.tenant;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;
import org.springframework.security.core.GrantedAuthority;

@Value.Immutable
@JsonSerialize(as = ImmutableTenantGrantedAuthority.class)
@JsonDeserialize(as = ImmutableTenantGrantedAuthority.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public interface TenantGrantedAuthority extends GrantedAuthority {

  @Value.Parameter
  String getTenantId();

  /**
   * Tenant's display name as authority
   *
   * @return tenant display name
   */
  @Override
  @Value.Parameter
  String getAuthority();

}
