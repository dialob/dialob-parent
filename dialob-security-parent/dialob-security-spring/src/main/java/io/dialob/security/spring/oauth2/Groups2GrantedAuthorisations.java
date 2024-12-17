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
package io.dialob.security.spring.oauth2;

import io.dialob.security.spring.tenant.GroupGrantedAuthority;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;
import java.util.Objects;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

public class Groups2GrantedAuthorisations implements UnaryOperator<Stream<? extends GrantedAuthority>> {

  private final GroupToAuthorityMapper groupToAuthorityMapper;

  public Groups2GrantedAuthorisations(GroupToAuthorityMapper groupToAuthorityMapper) {
    this.groupToAuthorityMapper = Objects.requireNonNull(groupToAuthorityMapper);
  }

  @Override
  public Stream<? extends GrantedAuthority> apply(Stream<? extends GrantedAuthority> stream) {
    final List<? extends GrantedAuthority> grantedAuthorities = stream.collect(toList());

    List<GroupGrantedAuthority> groups = grantedAuthorities.stream().filter(a -> a instanceof GroupGrantedAuthority)
      .map(a -> (GroupGrantedAuthority) a).toList();

    return Stream.concat(
      grantedAuthorities.stream().filter(a -> !(a instanceof GroupGrantedAuthority)),
      groups.stream().flatMap(this::mapGroupToAuthorities)
    );
  }

  private Stream<? extends GrantedAuthority> mapGroupToAuthorities(GroupGrantedAuthority group) {
    return groupToAuthorityMapper.apply(group.getAuthority()).stream().map(SimpleGrantedAuthority::new);
  }
}
