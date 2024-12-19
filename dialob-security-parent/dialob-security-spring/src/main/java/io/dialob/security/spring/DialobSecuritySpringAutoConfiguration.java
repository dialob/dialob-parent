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

import io.dialob.common.Permissions;
import io.dialob.security.spring.audit.AuditConfiguration;
import io.dialob.security.spring.filter.MDCRequestIdFilter;
import io.dialob.security.spring.oauth2.Groups2GrantedAuthorisations;
import io.dialob.security.spring.oauth2.Groups2GroupGrantedAuthoritiesMapper;
import io.dialob.security.spring.oauth2.StreamingGrantedAuthoritiesMapper;
import io.dialob.security.spring.oauth2.UsersAndGroupsService;
import io.dialob.security.spring.tenant.*;
import io.dialob.settings.DialobSettings;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;

import java.util.*;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

@Configuration(proxyBeanMethods = false)
@Import(AuditConfiguration.class)
@EnableConfigurationProperties(DialobSettings.class)
public class DialobSecuritySpringAutoConfiguration {

  public static final DialobSettings.TenantSettings.Tenant UNKNOWN_TENANT = new DialobSettings.TenantSettings.Tenant("unknown");

  @Bean
  public FilterRegistrationBean<MDCRequestIdFilter> requestIdFilter() {
    var filterRegBean = new FilterRegistrationBean<>(new MDCRequestIdFilter());
    filterRegBean.setOrder(Integer.MIN_VALUE);
    return filterRegBean;
  }

  @Deprecated // uaa support should be removed
  static Function<GroupGrantedAuthority,Stream<? extends GrantedAuthority>> uaaGroupNameToTenantMapper(String prefix) {
    var envPrefix = StringUtils.appendIfMissing(Objects.requireNonNull(prefix), "/");
    return (GroupGrantedAuthority groupGrantedAuthority) -> {
      var authority = groupGrantedAuthority.getAuthority();
      if (authority.contains("/") && authority.startsWith(envPrefix)) {
        return Stream.of(ImmutableTenantGrantedAuthority.builder()
            .authority(authority.substring(envPrefix.length()))
            .tenantId(groupGrantedAuthority.getGroupId())
          .build());
      }
      return Stream.empty();
    };
  }

  static Function<GroupGrantedAuthority,Stream<? extends GrantedAuthority>> groupNameToTenantMapper(Map<String,Set<String>> groupMapping, Map<String, DialobSettings.TenantSettings.Tenant> tenants) {
    return (GroupGrantedAuthority authority) -> groupMapping.getOrDefault(authority.getAuthority(), Collections.emptySet())
        .stream().map(tenantId -> ImmutableTenantGrantedAuthority.builder()
          .authority(tenants.getOrDefault(tenantId, UNKNOWN_TENANT).name())
          .tenantId(tenantId)
          .build());
  }

  @Bean
  @Profile({"uaa | aws"})
  public GrantedAuthoritiesMapper grantedAuthoritiesMapper(Environment environment,
                                                           DialobSettings dialobSettings,
                                                           Optional<UsersAndGroupsService> usersAndGroupsService) {
    var operators = new ArrayList<UnaryOperator<Stream<? extends GrantedAuthority>>>();

    Function<GroupGrantedAuthority,Stream<? extends GrantedAuthority>> tenantMapper;
    if (environment.matchesProfiles("uaa")) {
      tenantMapper = uaaGroupNameToTenantMapper(dialobSettings.getTenant().getEnv());
    } else {
      tenantMapper = groupNameToTenantMapper(dialobSettings.getTenant().getGroupToTenants(), dialobSettings.getTenant().getTenants());
    }

    final Map<String, Set<String>> groupPermissions = dialobSettings.getSecurity().getGroupPermissions();
    operators.add(new Groups2GrantedAuthorisations(group -> groupPermissions.getOrDefault(group, Collections.emptySet())));
    operators.add(new MapTenantGroupToTenantGrantedAuthority(tenantMapper));
    usersAndGroupsService.ifPresent(service -> operators.add(new Groups2GroupGrantedAuthoritiesMapper(service)));
    return new StreamingGrantedAuthoritiesMapper(operators);
  }


  @Bean
  @Profile({"uaa | aws"})
  public TenantAccessEvaluator tenantAccessEvaluator() {
    return new GrantedTenantAccessEvaluator() {
      @Override
      protected boolean canAccessAnyTenant(AbstractAuthenticationToken authentication) {
        return authentication.getAuthorities().stream().anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals(Permissions.ALL_TENANTS));
      }
    };
  }

}
