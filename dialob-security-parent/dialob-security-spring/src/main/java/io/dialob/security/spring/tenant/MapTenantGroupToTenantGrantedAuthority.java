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

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;

import java.util.Objects;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

public class MapTenantGroupToTenantGrantedAuthority implements UnaryOperator<Stream<? extends GrantedAuthority>> {

  private static final Logger LOGGER = LoggerFactory.getLogger(MapTenantGroupToTenantGrantedAuthority.class);

  private final String envPrefix;

  public MapTenantGroupToTenantGrantedAuthority(String envPrefix) {
    this.envPrefix = StringUtils.appendIfMissing(Objects.requireNonNull(envPrefix), "/");
  }

  @Override
  public Stream<? extends GrantedAuthority> apply(Stream<? extends GrantedAuthority> stream) {
    return stream.flatMap(grantedAuthority -> {
      if (grantedAuthority instanceof GroupGrantedAuthority) {
        GroupGrantedAuthority groupGrantedAuthority = (GroupGrantedAuthority) grantedAuthority;
        final String authority = groupGrantedAuthority.getAuthority();
        if (isTenantGroup(authority)) {
          if (authority.startsWith(envPrefix)) {
            return Stream.of(ImmutableTenantGrantedAuthority.builder()
              .tenantId(groupGrantedAuthority.getGroupId())
              .authority(authority.substring(envPrefix.length()))
              .build());
          }
          LOGGER.debug("Dropping other env tenant {}", authority);
          return Stream.empty(); // Filter out unknown tenant groups
        }
      }
      return Stream.of(grantedAuthority);
    });
  }

  private boolean isTenantGroup(String authority) {
    return authority.contains("/");
  }
}
