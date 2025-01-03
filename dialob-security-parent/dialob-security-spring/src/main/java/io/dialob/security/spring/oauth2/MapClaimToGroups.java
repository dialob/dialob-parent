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

import io.dialob.security.spring.tenant.ImmutableGroupGrantedAuthority;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2UserAuthority;

import java.util.Collection;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

public class MapClaimToGroups implements UnaryOperator<Stream<? extends GrantedAuthority>> {

  private final String groupAttributeName;

  public MapClaimToGroups(String groupAttributeName) {
    this.groupAttributeName = groupAttributeName;
  }

  @Override
  public Stream<? extends GrantedAuthority> apply(Stream<? extends GrantedAuthority> stream) {
    return stream.flatMap(authority -> {
      if (authority instanceof OAuth2UserAuthority oAuth2UserAuthority) {
        var groups = getGroups(oAuth2UserAuthority);
        if (groups != null) {
          return Stream.concat(Stream.of(authority), groups.stream()
            .map((String groupName) -> ImmutableGroupGrantedAuthority.of(groupName, groupName)));
        }
      }
      return Stream.of(authority);
    });
  }

  private Collection<String> getGroups(OAuth2UserAuthority oAuth2UserAuthority) {
    return (Collection<String>) oAuth2UserAuthority.getAttributes().get(groupAttributeName);
  }
}
