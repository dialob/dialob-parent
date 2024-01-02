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
package io.dialob.service.common;

import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.dialob.service.common.rest.ServiceExceptionMapper;
import io.dialob.service.common.security.SecurityDisabledConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.converter.StringHttpMessageConverter;

import java.nio.charset.StandardCharsets;
import java.time.Clock;

@Configuration(proxyBeanMethods = false)
@Import({ServiceExceptionMapper.class, SecurityDisabledConfiguration.class})
@Slf4j
public class DialobServiceCommonAutoConfiguration {

  @Bean
  public StringHttpMessageConverter stringEncodingConverter() {
    LOGGER.debug("Constructing bean stringEncodingConverter");
    return new StringHttpMessageConverter(StandardCharsets.UTF_8);
  }

  @Bean
  public Clock clock() {
    return Clock.systemDefaultZone();
  }

  @Bean
  public JavaTimeModule javaTimeModule() {
    return new JavaTimeModule();
  }

  @Bean
  public Jdk8Module jdk8Module() {
    return new Jdk8Module();
  }

}
