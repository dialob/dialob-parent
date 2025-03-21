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
package io.dialob.security.aws;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.JWSVerificationKeySelector;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jose.util.DefaultResourceRetriever;
import com.nimbusds.jwt.proc.ConfigurableJWTProcessor;
import com.nimbusds.jwt.proc.DefaultJWTProcessor;
import com.nimbusds.jwt.proc.JWTProcessor;
import io.dialob.security.aws.elb.ElbJWKSource;
import io.dialob.settings.DialobSettings;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.Optional;
import java.util.stream.Collectors;


@Configuration(proxyBeanMethods = false)
@Profile("aws")
public class DialobSecurityAwsAutoConfiguration {

  @Bean
  public <C extends SecurityContext> JWTProcessor<C> awsElbJwtProcessor(DialobSettings settings, Optional<JWKSource<C>> jwkSource) {
    JWKSource<C> keySource = jwkSource.orElseGet(() -> {
      String url = "https://public-keys.auth.elb." + settings.getAws().getRegion() + ".amazonaws.com/{kid}";
      var resourceRetriever = new DefaultResourceRetriever(5000, 5000);
      return new ElbJWKSource<>(url, resourceRetriever);
    });
    var keySelector = new JWSVerificationKeySelector<>(
      settings.getAws().getElb().getAlgorithms().stream().map(JWSAlgorithm::parse).collect(Collectors.toSet()),
      keySource);
    ConfigurableJWTProcessor<C> jwtProcessor = new DefaultJWTProcessor<>();
    jwtProcessor.setJWSKeySelector(keySelector);
    return jwtProcessor;
  }

}
