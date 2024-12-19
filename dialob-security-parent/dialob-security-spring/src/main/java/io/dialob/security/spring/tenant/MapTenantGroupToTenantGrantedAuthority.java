/*
 * Copyright © 2015 - 2021 ReSys (info@dialob.io)
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

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

@Slf4j
public class MapTenantGroupToTenantGrantedAuthority implements UnaryOperator<Stream<? extends GrantedAuthority>> {

  private final Function<GroupGrantedAuthority,Stream<? extends GrantedAuthority>> tenantMapper;

  public MapTenantGroupToTenantGrantedAuthority(Function<GroupGrantedAuthority,Stream<? extends GrantedAuthority>> tenantMapper) {
    this.tenantMapper = Objects.requireNonNull(tenantMapper, "tenantMapper may not be null.");
  }

  @Override
  public Stream<? extends GrantedAuthority> apply(Stream<? extends GrantedAuthority> stream) {
    return stream.flatMap(grantedAuthority -> {
      if (grantedAuthority instanceof GroupGrantedAuthority groupGrantedAuthority) {
        return tenantMapper.apply(groupGrantedAuthority);
      }
      return Stream.of(grantedAuthority);
    });
  }

}
