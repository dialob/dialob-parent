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
package io.dialob.boot.settings;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

/**
 * Represents the settings for the Admin application.
 *
 * @param apiUrl         the URL of the API
 * @param fillingAppUrl  the URL of the filling application
 * @param reviewAppUrl   the URL of the review application
 * @param composerAppUrl the URL of the composer application
 * @param documentation  the documentation URL
 * @param contextPath    the context path, must not be null
 * @param versioning     flag indicating if form versioning is enabled
 * @param tenants        a map of tenant settings
 */
@Configuration(proxyBeanMethods = false)
@ConfigurationProperties("admin")
public record AdminApplicationSettings(
  @Getter
  String apiUrl,
  @Getter
  String fillingAppUrl,
  @Getter
  String reviewAppUrl,
  @Getter
  String composerAppUrl,
  @Getter
  String documentation,
  @Getter
  @NotNull String contextPath,
  @Getter
  boolean versioning,
  Map<String, SettingsPageAttributes> tenants
) {
  public AdminApplicationSettings {
    contextPath = StringUtils.defaultIfBlank(contextPath, "/");
    tenants = tenants == null ? Map.of() : Map.copyOf(tenants);
  }
}
