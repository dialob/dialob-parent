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
package io.dialob.security.spring;

import io.dialob.security.spring.tenant.ImmutableGroupGrantedAuthority;
import io.dialob.security.spring.tenant.ImmutableTenantGrantedAuthority;
import io.dialob.security.spring.tenant.TenantAccessEvaluator;
import io.dialob.settings.DialobSettings;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;

import java.util.*;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class DialobSecuritySpringAutoConfigurationTest {

  @Test
  void test() {
    new ApplicationContextRunner()
      .withPropertyValues(
        "spring.profiles.active=",
        "dialob.security.enabled=true"
      ).withUserConfiguration(
        DialobSecuritySpringAutoConfiguration.class)
      .run(context -> {
        assertThat(context)
          .hasSingleBean(FilterRegistrationBean.class)
          .doesNotHaveBean(GrantedAuthoritiesMapper.class)
          .hasSingleBean(TenantAccessEvaluator.class);
      });
  }

  @Test
  void shouldCreateBeansWhenAws() {
    new ApplicationContextRunner()
      .withPropertyValues(
        "spring.profiles.active=aws",
        "dialob.security.enabled=true",
        "dialob.security.groups-claim=true")
      .withUserConfiguration(
        DialobSecuritySpringAutoConfiguration.class)
      .run(context -> {
        assertThat(context)
          .hasSingleBean(FilterRegistrationBean.class)
          .hasSingleBean(GrantedAuthoritiesMapper.class)
          .hasSingleBean(TenantAccessEvaluator.class);
        var mapper = context.getBean(GrantedAuthoritiesMapper.class);
        Assertions.assertTrue(mapper.mapAuthorities(Collections.emptySet()).isEmpty());
        Assertions.assertTrue(mapper.mapAuthorities(List.of(ImmutableGroupGrantedAuthority.of("g1", "a"))).isEmpty());
      });
  }

  @Test
  void shouldMapGroupToTenant() {
    new ApplicationContextRunner()
      .withPropertyValues(
        "spring.profiles.active=aws",
        "dialob.tenant.env=junit",
        "dialob.tenant.group-to-tenants.g1=t,t2",
        "dialob.tenant.tenants.t.name=Tenant 1",
        "dialob.tenant.tenants.t.name=Tenant 2",
        "dialob.security.enabled=true",
        "dialob.security.groups-claim=true"
      )
      .withUserConfiguration(
        DialobSecuritySpringAutoConfiguration.class)
      .run(context -> {
        assertThat(context)
          .hasSingleBean(FilterRegistrationBean.class)
          .hasSingleBean(GrantedAuthoritiesMapper.class)
          .hasSingleBean(TenantAccessEvaluator.class);
        var mapper = context.getBean(GrantedAuthoritiesMapper.class);
        Assertions.assertTrue(mapper.mapAuthorities(Collections.emptySet()).isEmpty());
        Collection<? extends GrantedAuthority> grantedAuthorities = mapper.mapAuthorities(List.of(ImmutableGroupGrantedAuthority.of("g1", "g1")));
        Assertions.assertEquals(2, grantedAuthorities.size());
      });
  }


  @Test
  void testGroupNameToTenantMapper() {
    var mapper = DialobSecuritySpringAutoConfiguration.groupNameToTenantMapper(Map.of(
        "g1", Set.of("t1"),
        "g2", Set.of("t1", "t2")
      ),
      Map.of(
        "t1", new DialobSettings.TenantSettings.Tenant("t1"),
        "t2", new DialobSettings.TenantSettings.Tenant("t2")
      ));

    Assertions.assertEquals(Set.of(
      ImmutableGroupGrantedAuthority.of("none", "none")
    ), mapper.apply(ImmutableGroupGrantedAuthority.of("none", "none")).collect(Collectors.toSet()));

    Assertions.assertEquals(Set.of(
      ImmutableTenantGrantedAuthority.of("t1", "t1")
    ), mapper.apply(ImmutableGroupGrantedAuthority.of("g1", "g1")).collect(Collectors.toSet()));

    Assertions.assertEquals(Set.of(
      ImmutableTenantGrantedAuthority.of("t1", "t1"),
      ImmutableTenantGrantedAuthority.of("t2", "t2")
    ), mapper.apply(ImmutableGroupGrantedAuthority.of("g2", "g2")).collect(Collectors.toSet()));

    Assertions.assertEquals(Set.of(
      ImmutableTenantGrantedAuthority.of("t1", "t1"),
      ImmutableTenantGrantedAuthority.of("t2", "t2")
    ), mapper.apply(ImmutableGroupGrantedAuthority.of("g2", "g2")).collect(Collectors.toSet()));

  }

}
