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
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.util.Map;

@ConfigurationProperties("review")
@Validated
public record ReviewApplicationSettings(
  @Getter
  String apiUrl,
  @Getter
  @NotNull String contextPath,
  Map<String, SettingsPageAttributes> tenants
) {
  public ReviewApplicationSettings {
    contextPath = contextPath == null ? "/review" : contextPath;
    tenants = tenants == null ? Map.of() : Map.copyOf(tenants);
  }
}
