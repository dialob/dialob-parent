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
package io.dialob.security.uaa.spi;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;

import io.dialob.security.spring.oauth2.UsersAndGroupsService;
import io.dialob.security.spring.oauth2.model.Group;
import io.dialob.security.spring.oauth2.model.ImmutableGroup;
import io.dialob.security.spring.oauth2.model.ImmutableUser;
import io.dialob.security.spring.oauth2.model.User;
import io.dialob.security.uaa.spi.model.UaaGroup;
import io.dialob.security.uaa.spi.model.UaaUser;

public class UaaUsersAndGroupsService extends UaaServiceBase implements UsersAndGroupsService {

  private static final Logger LOGGER = LoggerFactory.getLogger(UaaUsersAndGroupsService.class);

  public UaaUsersAndGroupsService(final @NonNull UaaClient uaaClient) {
    super(uaaClient);
  }

  @Override
  public Optional<User> findUser(final String userId) {
    LOGGER.debug("findUser('{}')", userId);
    if (userId == null) {
      return Optional.empty();
    }
    return get(client -> client.getUser(userId)).map(uaaUser -> {
      final UaaUser.Name name = uaaUser.getName();
      String firstName = null;
      String lastName = null;
      if (name != null) {
        firstName = name.getGivenName();
        lastName = name.getFamilyName();
      }
      return ImmutableUser.builder()
        .id(uaaUser.getId())
        .isAnonymous(false)
        .userName(uaaUser.getUserName())
        .firstName(firstName)
        .lastName(lastName)
        .addAllGroups(uaaUser.getGroups().stream()
          .map(group -> (Group) ImmutableGroup.builder().id(group.getValue()).name(group.getDisplay()).build())::iterator)
        .build();
    });
  }

  @Override
  public Optional<Group> findGroup(final String groupId) {
    LOGGER.debug("findGroup('{}')", groupId);
    if (groupId == null) {
      return Optional.empty();
    }
    return get(client -> client.getGroup(groupId))
      .map(this::mapToGroup);
  }

  @Override
  public List<Group> findGroupByName(String groupName) {
    LOGGER.debug("findGroupByName('{}')", groupName);
    if (groupName == null) {
      return Collections.emptyList();
    }
    return list(client -> client.findGroupsByDisplayName(groupName).getResources()).stream()
      .map(this::mapToGroup)
      .collect(Collectors.toList());
  }

  private Group mapToGroup(UaaGroup group) {
    return ImmutableGroup
      .builder()
      .id(group.getId())
      .name(group.getDisplayName())
      .addAllMembers(group.getMembers().stream()
        .map(UaaGroup.Member::getValue)::iterator) // map id to userName?
      .build();
  }
}
