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
package io.dialob.boot.controller;

import io.dialob.boot.settings.QuestionnaireApplicationSettings;
import io.dialob.common.Constants;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import org.springframework.http.MediaType;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("${fill.context-path:/fill}")
public class FillController extends BaseController {

  private final QuestionnaireApplicationSettings settings;

  private final PageSettingsProvider pageSettingsProvider;

  public FillController(QuestionnaireApplicationSettings settings, PageSettingsProvider pageSettingsProvider) {
    this.settings = settings;
    this.pageSettingsProvider = pageSettingsProvider;
  }

  @GetMapping(value = {"/{questionnaireId}"}, produces = MediaType.TEXT_HTML_VALUE)
  public String fill(@PathVariable("questionnaireId") @Pattern(regexp = Constants.QUESTIONNAIRE_ID_PATTERN) String questionnaireId,
                     CsrfToken cfrsToken,
                     Model model,
                     HttpServletRequest request) {
    model.addAttribute("formConnectionOptions", FormConnectionOptions.builder()
      .questionnaireId(questionnaireId)
      .url(settings.getSocketUrl() + "/" + questionnaireId)
      .reviewUrl(settings.getReviewUrl() + "/" + questionnaireId)
      .csrf(cfrsToken)
      .restUrl(settings.getRestUrl() + "/" + questionnaireId)
      .restUrlBase(settings.getRestUrl())
      .connectionMode(settings.getConnectionMode())
      .backendApiUrl(settings.getBackendApiUrl())
      .build());
    final PageAttributes pageAttributes = pageSettingsProvider.findPageSettingsByQuestionnaireId("fill", questionnaireId);
    model.addAllAttributes(pageAttributes.getAttributes());
    index(model, request);
    return pageAttributes.getTemplate();
  }

  @Builder
  public record FormConnectionOptions(
    String url,
    String reviewUrl,
    String questionnaireId,
    List<String> transports,
    CsrfToken csrf,
    String restUrl,
    String restUrlBase,
    String connectionMode,
    String backendApiUrl
  ) {
  }

}
