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
import io.dialob.security.spring.tenant.GrantedTenantAccessEvaluator;
import io.dialob.security.spring.tenant.MapTenantGroupToTenantGrantedAuthority;
import io.dialob.security.spring.tenant.TenantAccessEvaluator;
import io.dialob.security.user.CurrentUserProvider;
import io.dialob.security.user.UnauthenticatedCurrentUserProvider;
import io.dialob.settings.DialobSettings;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;

import java.util.*;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

@Configuration(proxyBeanMethods = false)
@Import(AuditConfiguration.class)
public class DialobSecuritySpringAutoConfiguration {

  @Bean
  public FilterRegistrationBean<MDCRequestIdFilter> requestIdFilter() {
    var filterRegBean = new FilterRegistrationBean<>(new MDCRequestIdFilter());
    filterRegBean.setOrder(Integer.MIN_VALUE);
    return filterRegBean;
  }


  @Bean
  @Profile({"uaa","aws"})
  public GrantedAuthoritiesMapper grantedAuthoritiesMapper(DialobSettings dialobSettings,
                                                           Optional<UsersAndGroupsService> usersAndGroupsService) {
    var operators = new ArrayList<UnaryOperator<Stream<? extends GrantedAuthority>>>();

    final Map<String, Set<String>> groupPermissions = dialobSettings.getSecurity().getGroupPermissions();
    operators.add(new Groups2GrantedAuthorisations(group -> groupPermissions.getOrDefault(group, Collections.emptySet())));
    operators.add(new MapTenantGroupToTenantGrantedAuthority(dialobSettings.getTenant().getEnv()));
    usersAndGroupsService.ifPresent(service -> operators.add(new Groups2GroupGrantedAuthoritiesMapper(service)));
    return new StreamingGrantedAuthoritiesMapper(operators);
  }

  @Bean
  @Profile({"uaa","aws"})
  public TenantAccessEvaluator tenantAccessEvaluator() {
    return new GrantedTenantAccessEvaluator() {
      @Override
      protected boolean canAccessAnyTenant(AbstractAuthenticationToken authentication) {
        return authentication.getAuthorities().stream().anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals(Permissions.ALL_TENANTS));
      }
    };
  }

}
