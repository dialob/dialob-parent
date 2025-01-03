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

import com.nimbusds.jwt.proc.JWTProcessor;
import io.dialob.security.spring.DialobSecuritySpringAutoConfiguration;
import io.dialob.security.spring.tenant.TenantAccessEvaluator;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;

import static org.assertj.core.api.Assertions.assertThat;

class DialobSecurityAwsAutoConfigurationTest {

  @Test
  public void shouldNotSetupTypeIfProfileAWSNoSet() {
    new ApplicationContextRunner()
      .withPropertyValues(
        "spring.profiles.active=",
        "dialob.security.enabled=true")
      .withUserConfiguration(
        DialobSecurityAwsAutoConfiguration.class,
        DialobSecuritySpringAutoConfiguration.class)
      .run(context -> {
        assertThat(context)
          .doesNotHaveBean(GrantedAuthoritiesMapper.class)
          .hasSingleBean(TenantAccessEvaluator.class)
          .doesNotHaveBean(JWTProcessor.class);
      });
  }

  @Test
  public void shouldSetupTypesIfProfileAWSIsSet() {
    new ApplicationContextRunner()
      .withPropertyValues(
        "spring.profiles.active=aws",
        "dialob.security.enabled=true",
        "dialob.security.groups-claim=true"
      )
      .withUserConfiguration(
        DialobSecurityAwsAutoConfiguration.class,
        DialobSecuritySpringAutoConfiguration.class)
      .run(context -> {
        assertThat(context)
          .hasSingleBean(GrantedAuthoritiesMapper.class)
          .hasSingleBean(TenantAccessEvaluator.class)
          .hasSingleBean(JWTProcessor.class);
      });
  }


}
