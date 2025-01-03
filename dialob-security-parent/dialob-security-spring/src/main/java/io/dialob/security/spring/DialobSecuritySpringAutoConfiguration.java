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
import io.dialob.security.spring.oauth2.*;
import io.dialob.security.spring.tenant.*;
import io.dialob.settings.DialobSettings;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.condition.AnyNestedCondition;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.*;
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
@Slf4j
@Conditional(DialobSecuritySpringAutoConfiguration.OnSecurityEnabled.class)
public class DialobSecuritySpringAutoConfiguration {


  private String groupsClaim;

  static class OnSecurityEnabled extends AnyNestedCondition {

    OnSecurityEnabled() {
      super(ConfigurationPhase. PARSE_CONFIGURATION);
    }

    @ConditionalOnProperty(prefix = "dialob.security", name = "enabled", havingValue = "true")
    static class OnSecurity {
    }

    @ConditionalOnProperty(prefix = "dialob.session.security", name = "enabled", havingValue = "true")
    static class OnSessionSecurity {
    }

  }
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
      return Stream.of(groupGrantedAuthority);
    };
  }

  public static Function<GroupGrantedAuthority,Stream<? extends GrantedAuthority>> groupNameToTenantMapper(Map<String, Set<String>> groupMapping, Map<String, DialobSettings.TenantSettings.Tenant> tenants) {
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Number of group mappings: {}, Number of tenants: {}", groupMapping.size(), tenants.size());
    }
    return (GroupGrantedAuthority authority) -> {
      Set<String> groups = groupMapping.get(authority.getAuthority());
      if (groups == null) {
        return Stream.of(authority);
      }
      return groups
          .stream().map(tenantId -> ImmutableTenantGrantedAuthority.builder()
            .authority(tenants.getOrDefault(tenantId, UNKNOWN_TENANT).name())
            .tenantId(tenantId)
            .build());
    };
  }

  @Bean
  @Profile({"uaa | aws | oauth2"})
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

    final var groupPermissions = dialobSettings.getSecurity().getGroupPermissions();
    operators.add(new Groups2GrantedAuthorisations(group -> groupPermissions.getOrDefault(group, Collections.emptySet())));
    operators.add(new MapTenantGroupToTenantGrantedAuthority(tenantMapper));
    usersAndGroupsService.ifPresent(service -> operators.add(new Groups2GroupGrantedAuthoritiesMapper(service)));
    operators.add(new MapClaimToGroups(dialobSettings.getSecurity().getGroupsClaim()));
    return new StreamingGrantedAuthoritiesMapper(operators);
  }


  @Bean
  public TenantAccessEvaluator tenantAccessEvaluator(DialobSettings dialobSettings) {
    if (dialobSettings.getTenant().getMode() == DialobSettings.TenantSettings.Mode.FIXED) {
      return tenant -> true;
    }
    return new GrantedTenantAccessEvaluator() {
      @Override
      protected boolean canAccessAnyTenant(AbstractAuthenticationToken authentication) {
        return authentication.getAuthorities().stream().anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals(Permissions.ALL_TENANTS));
      }
    };
  }

}
