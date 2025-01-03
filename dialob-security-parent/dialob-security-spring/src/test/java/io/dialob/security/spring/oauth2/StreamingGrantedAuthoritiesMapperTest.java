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

import io.dialob.security.spring.DialobSecuritySpringAutoConfiguration;
import io.dialob.security.spring.tenant.ImmutableGroupGrantedAuthority;
import io.dialob.security.spring.tenant.ImmutableTenantGrantedAuthority;
import io.dialob.security.spring.tenant.MapTenantGroupToTenantGrantedAuthority;
import io.dialob.settings.DialobSettings;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2UserAuthority;

import java.util.*;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
class StreamingGrantedAuthoritiesMapperTest {

  @Test
  void shouldPassAsIsWhenNoMappersDefined() {
    StreamingGrantedAuthoritiesMapper mapper = new StreamingGrantedAuthoritiesMapper(Collections.emptyList());
    assertTrue(mapper.mapAuthorities(List.of()).isEmpty());
    assertEquals(List.of(ImmutableGroupGrantedAuthority.of("g","g")), mapper.mapAuthorities(List.of(ImmutableGroupGrantedAuthority.of("g","g"))));
  }

  @Test
  void shouldMapGroupsClaimToGroups() {
    StreamingGrantedAuthoritiesMapper mapper = new StreamingGrantedAuthoritiesMapper(List.of(new MapClaimToGroups("groups")));
    assertTrue(mapper.mapAuthorities(List.of()).isEmpty());
    assertEquals(List.of(ImmutableGroupGrantedAuthority.of("g","g")), mapper.mapAuthorities(List.of(ImmutableGroupGrantedAuthority.of("g","g"))));
    OAuth2UserAuthority e1 = new OAuth2UserAuthority(Map.of("groups", List.of("group1")));
    assertEquals(List.of(e1, ImmutableGroupGrantedAuthority.of("group1","group1")), mapper.mapAuthorities(List.of(e1)));
  }

  @Test
  void shouldMapGroupsClaimToGroups2() {
    var groupPermissions = Map.of("g", Set.of("p1","p2"));
    var mapper = new StreamingGrantedAuthoritiesMapper(List.of(new Groups2GrantedAuthorisations(group -> groupPermissions.getOrDefault(group, Collections.emptySet()))));
    assertTrue(mapper.mapAuthorities(List.of()).isEmpty());
    assertEquals(
      Set.of("p1", "p2"),
      mapper.mapAuthorities(List.of(ImmutableGroupGrantedAuthority.of("g","g"), ImmutableGroupGrantedAuthority.of("g2","g2")))
        .stream().map(Objects::toString).collect(Collectors.toSet())
    );
  }

  @Test
  void shouldMapGroupsToTenants() {
    var tenantMapper = DialobSecuritySpringAutoConfiguration.groupNameToTenantMapper(Map.of(
        "g1", Set.of("t1g"),
        "g2", Set.of("t1g", "t2g")
      ),
      Map.of(
        "t1g", new DialobSettings.TenantSettings.Tenant("t1"),
        "t2g", new DialobSettings.TenantSettings.Tenant("t2")
      ));

    UnaryOperator<Stream<? extends GrantedAuthority>> logOp = stream -> {
      var c = stream.collect(Collectors.toSet());
      LOGGER.info("is now {}", c);
      return c.stream();
    };

    var groupPermissions = Map.of("gx", Set.of("px"));
    List<UnaryOperator<Stream<? extends GrantedAuthority>>> operators = new ArrayList<>();
    operators.add(new Groups2GrantedAuthorisations(group -> groupPermissions.getOrDefault(group, Collections.emptySet())));
    operators.add(new MapTenantGroupToTenantGrantedAuthority(tenantMapper));
    operators.add(new MapClaimToGroups("cognito:groups"));
    var mapper = new StreamingGrantedAuthoritiesMapper(operators);

    assertTrue(mapper.mapAuthorities(List.of()).isEmpty());

    var oauth = new OAuth2UserAuthority(Map.of("cognito:groups", List.of("g1", "gx")));
    assertThat((Collection<GrantedAuthority>) mapper.mapAuthorities(List.of(oauth)))
      .containsOnly(ImmutableTenantGrantedAuthority.of("t1g", "t1"), new SimpleGrantedAuthority("px"), oauth);
  }

}
