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
package io.dialob.boot.settings;

import io.dialob.common.Constants;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;

@Configuration(proxyBeanMethods = false)
@ConfigurationProperties(Constants.QUESTIONNAIRE)
@Validated
@Data
public class QuestionnaireApplicationSettings {

  private String socketUrl;

  private String reviewUrl;

  private String restUrl;

  private String connectionMode;

  private String backendApiUrl;

  @NotNull
  private String contextPath = "/fill";

  private Map<String, SettingsPageAttributes> tenants = new HashMap<>();

}
