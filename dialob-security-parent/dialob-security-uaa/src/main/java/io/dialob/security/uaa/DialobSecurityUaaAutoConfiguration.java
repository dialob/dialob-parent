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
package io.dialob.security.uaa;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import feign.Feign;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import feign.slf4j.Slf4jLogger;
import io.dialob.security.spring.oauth2.UsersAndGroupsService;
import io.dialob.security.uaa.spi.UaaClient;
import io.dialob.security.uaa.spi.UaaUsersAndGroupsService;
import io.dialob.settings.DialobSettings;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration(proxyBeanMethods = false)
@Profile("uaa")
public class DialobSecurityUaaAutoConfiguration {

  @Bean
  public UaaClient uaaClient(DialobSettings settings) {
    ObjectMapper objectMapper = new ObjectMapper().registerModules(new JavaTimeModule(), new Jdk8Module());
    return Feign.builder()
      .encoder(new JacksonEncoder(objectMapper))
      .decoder(new JacksonDecoder(objectMapper))
      .requestInterceptor(new OAuth2AuthenticationInterceptor(
        settings.getUaa().getUrl(),
        settings.getUaa().getClientId(),
        settings.getUaa().getClientSecret()))
      .logger(new Slf4jLogger(DialobSecurityUaaAutoConfiguration.class))
      .target(UaaClient.class, settings.getUaa().getUrl());
  }

  @Bean
  public UsersAndGroupsService usersAndGroupsService(UaaClient uaaClient) {
    return new UaaUsersAndGroupsService(uaaClient);
  }

}
