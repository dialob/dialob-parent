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

import java.util.Objects;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2UserAuthority;

import io.dialob.security.spring.oauth2.model.Group;
import io.dialob.security.spring.tenant.ImmutableGroupGrantedAuthority;

public class UaaGroups2GroupGrantedAuthoritiesMapper implements UnaryOperator<Stream<? extends GrantedAuthority>> {

  private static final Logger LOGGER = LoggerFactory.getLogger(UaaGroups2GroupGrantedAuthoritiesMapper.class);

  private final UsersAndGroupsService usersAndGroupsService;

  private final Predicate<Group> groupFilter;

  public UaaGroups2GroupGrantedAuthoritiesMapper(UsersAndGroupsService usersAndGroupsService) {
    this(usersAndGroupsService, group -> true);
  }

  public UaaGroups2GroupGrantedAuthoritiesMapper(UsersAndGroupsService usersAndGroupsService, Predicate<Group> groupFilter) {
    this.usersAndGroupsService = Objects.requireNonNull(usersAndGroupsService);
    this.groupFilter = Objects.requireNonNull(groupFilter);
  }

  public Stream<Group> loadUserGroups(String userId) {
    return usersAndGroupsService.findUser(userId)
      .map(user -> {
        LOGGER.debug("Loaded user {}, {} groups", user.getId(), user.getGroups().size());
        return user.getGroups().stream()
          .filter(this.groupFilter);
      }).orElseGet(() -> {
        LOGGER.warn("User {} not found", userId);
        return Stream.empty();
      });
  }

  @Override
  public Stream<? extends GrantedAuthority> apply(Stream<? extends GrantedAuthority> stream) {
    return stream.flatMap(grantedAuthority -> {
      if (grantedAuthority instanceof OAuth2UserAuthority) {
        final OAuth2UserAuthority oAuth2UserAuthority = (OAuth2UserAuthority) grantedAuthority;
        String sub = (String) oAuth2UserAuthority.getAttributes().get("sub");
        return Stream.concat(Stream.of(grantedAuthority), loadUserGroups(sub).map(group -> ImmutableGroupGrantedAuthority.of(group.getId(), group.getName())));
      }
      return Stream.of(grantedAuthority);
    });
  }
}
