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

import io.dialob.common.Constants;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.util.Map;

@ConfigurationProperties(Constants.QUESTIONNAIRE)
@Validated
public record QuestionnaireApplicationSettings(
  @Getter
  String socketUrl,
  @Getter
  String reviewUrl,
  @Getter
  String restUrl,
  @Getter
  String connectionMode,
  @Getter
  String backendApiUrl,
  @Getter
  @NotNull String contextPath,
  @Getter
  Map<String, SettingsPageAttributes> tenants
) {
  public QuestionnaireApplicationSettings {
    contextPath = contextPath == null ? "/fill" : contextPath;
    tenants = tenants == null ? Map.of() : Map.copyOf(tenants);
  }
}
