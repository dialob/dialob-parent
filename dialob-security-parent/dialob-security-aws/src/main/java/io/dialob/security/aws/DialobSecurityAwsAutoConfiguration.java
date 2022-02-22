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
package io.dialob.security.aws;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.JWSVerificationKeySelector;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jose.util.DefaultResourceRetriever;
import com.nimbusds.jwt.proc.ConfigurableJWTProcessor;
import com.nimbusds.jwt.proc.DefaultJWTProcessor;
import com.nimbusds.jwt.proc.JWTProcessor;

import io.dialob.common.Permissions;
import io.dialob.security.aws.elb.ElbJWKSource;
import io.dialob.security.spring.oauth2.Groups2GrantedAuthorisations;
import io.dialob.security.spring.oauth2.StreamingGrantedAuthoritiesMapper;
import io.dialob.security.spring.oauth2.UaaGroups2GroupGrantedAuthoritiesMapper;
import io.dialob.security.spring.oauth2.UsersAndGroupsService;
import io.dialob.security.spring.tenant.GrantedTenantAccessEvaluator;
import io.dialob.security.spring.tenant.MapTenantGroupToTenantGrantedAuthority;
import io.dialob.security.spring.tenant.TenantAccessEvaluator;
import io.dialob.settings.DialobSettings;


@Configuration(proxyBeanMethods = false)
@Profile("aws")
public class DialobSecurityAwsAutoConfiguration {

  @Bean
  public GrantedAuthoritiesMapper grantedAuthoritiesMapper(DialobSettings dialobSettings,
                                                           Optional<UsersAndGroupsService> usersAndGroupsService) {
    List<UnaryOperator<Stream<? extends GrantedAuthority>>> operators = new ArrayList<>();

    final Map<String, Set<String>> groupPermissions = dialobSettings.getSecurity().getGroupPermissions();
    operators.add(new Groups2GrantedAuthorisations(group -> groupPermissions.getOrDefault(group, Collections.emptySet())));
    operators.add(new MapTenantGroupToTenantGrantedAuthority(dialobSettings.getTenant().getEnv()));
    usersAndGroupsService.ifPresent(service -> operators.add(new UaaGroups2GroupGrantedAuthoritiesMapper(service)));
    return new StreamingGrantedAuthoritiesMapper(operators);
  }

  @Bean
  public TenantAccessEvaluator tenantAccessEvaluator() {
    return new GrantedTenantAccessEvaluator() {
      @Override
      protected boolean canAccessAnyTenant(AbstractAuthenticationToken authentication) {
        return authentication.getAuthorities().stream().anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals(Permissions.ALL_TENANTS));
      }
    };
  }

  @Bean
  public <C extends SecurityContext> JWTProcessor<C> awsElbJwtProcessor(DialobSettings settings, Optional<JWKSource<C>> jwkSource) {
    JWKSource<C> keySource = jwkSource.orElseGet(() -> {
      String url = "https://public-keys.auth.elb." + settings.getAws().getRegion() + ".amazonaws.com/{kid}";
      var resourceRetriever = new DefaultResourceRetriever(5000, 5000);
      return new ElbJWKSource<>(url, resourceRetriever);
    });
    var keySelector = new JWSVerificationKeySelector<C>(
      settings.getAws().getElb().getAlgorithms().stream().map(JWSAlgorithm::parse).collect(Collectors.toSet()),
      keySource);
    ConfigurableJWTProcessor<C> jwtProcessor = new DefaultJWTProcessor<>();
    jwtProcessor.setJWSKeySelector(keySelector);
    return jwtProcessor;
  }

}
