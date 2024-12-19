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
package io.dialob.security.spring;

import io.dialob.security.spring.tenant.ImmutableGroupGrantedAuthority;
import io.dialob.security.spring.tenant.TenantAccessEvaluator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class DialobSecuritySpringAutoConfigurationTest {

  @Test
  public void test() {
    new ApplicationContextRunner()
      .withPropertyValues(
        "spring.profiles.active=")
      .withUserConfiguration(
        DialobSecuritySpringAutoConfiguration.class)
      .run(context -> {
        assertThat(context)
          .hasSingleBean(FilterRegistrationBean.class)
          .doesNotHaveBean(GrantedAuthoritiesMapper.class)
          .doesNotHaveBean(TenantAccessEvaluator.class);
      });
  }

  @Test
  public void shouldCreateBeansWhenAws() {
    new ApplicationContextRunner()
      .withPropertyValues(
        "spring.profiles.active=aws")
      .withUserConfiguration(
        DialobSecuritySpringAutoConfiguration.class)
      .run(context -> {
        assertThat(context)
          .hasSingleBean(FilterRegistrationBean.class)
          .hasSingleBean(GrantedAuthoritiesMapper.class)
          .hasSingleBean(TenantAccessEvaluator.class);
        var mapper = context.getBean(GrantedAuthoritiesMapper.class);
        Assertions.assertTrue(mapper.mapAuthorities(Collections.emptySet()).isEmpty());
        Assertions.assertTrue(mapper.mapAuthorities(List.of(ImmutableGroupGrantedAuthority.of("g1","a"))).isEmpty());
      });
  }

  @Test
  public void shouldMapGroupToTenant() {
    new ApplicationContextRunner()
      .withPropertyValues(
        "spring.profiles.active=aws",
        "dialob.tenant.env=junit",
        "dialob.tenant.group-to-tenants.g1=t,t2",
        "dialob.tenant.tenants.t.name=Tenant 1",
        "dialob.tenant.tenants.t.name=Tenant 2"
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
  public void shouldCreateBeansForUaa() {
    new ApplicationContextRunner()
      .withPropertyValues(
        "spring.profiles.active=uaa")
      .withUserConfiguration(
        DialobSecuritySpringAutoConfiguration.class)
      .run(context -> {
        assertThat(context)
          .hasSingleBean(FilterRegistrationBean.class)
          .hasSingleBean(GrantedAuthoritiesMapper.class)
          .hasSingleBean(TenantAccessEvaluator.class);
      });
  }

}
