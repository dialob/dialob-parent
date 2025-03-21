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
package io.dialob.settings;

import lombok.Data;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.boot.convert.DurationUnit;
import org.springframework.util.CollectionUtils;
import org.springframework.web.cors.CorsConfiguration;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Data
public class CorsSettings {

  private List<String> allowedOrigins = new ArrayList<>();

  private List<String> allowedMethods = new ArrayList<>();

  private List<String> allowedHeaders = new ArrayList<>();

  private List<String> exposedHeaders = new ArrayList<>();

  private Boolean allowCredentials;

  @DurationUnit(ChronoUnit.SECONDS)
  private Duration maxAge = Duration.ofSeconds(1800);

  public Optional<CorsConfiguration> toCorsConfiguration() {
    if (CollectionUtils.isEmpty(this.allowedOrigins)) {
      return Optional.empty();
    }
    final PropertyMapper map = PropertyMapper.get();
    final CorsConfiguration configuration = new CorsConfiguration();
    map.from(this::getAllowedOrigins).to(configuration::setAllowedOrigins);
    map.from(this::getAllowedHeaders).whenNot(CollectionUtils::isEmpty)
      .to(configuration::setAllowedHeaders);
    map.from(this::getAllowedMethods).whenNot(CollectionUtils::isEmpty)
      .to(configuration::setAllowedMethods);
    map.from(this::getExposedHeaders).whenNot(CollectionUtils::isEmpty)
      .to(configuration::setExposedHeaders);
    map.from(this::getMaxAge).whenNonNull().as(Duration::getSeconds)
      .to(configuration::setMaxAge);
    map.from(this::getAllowCredentials).whenNonNull()
      .to(configuration::setAllowCredentials);
    return Optional.of(configuration);
  }
}
