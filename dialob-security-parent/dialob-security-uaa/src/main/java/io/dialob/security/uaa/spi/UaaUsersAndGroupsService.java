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
package io.dialob.security.uaa.spi;

import io.dialob.security.spring.oauth2.UsersAndGroupsService;
import io.dialob.security.spring.oauth2.model.Group;
import io.dialob.security.spring.oauth2.model.ImmutableGroup;
import io.dialob.security.spring.oauth2.model.ImmutableUser;
import io.dialob.security.spring.oauth2.model.User;
import io.dialob.security.uaa.spi.model.UaaUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;

import java.util.Optional;

@Slf4j
public class UaaUsersAndGroupsService extends UaaServiceBase implements UsersAndGroupsService {

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

}
