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
package io.dialob.boot.controller;

import io.dialob.boot.settings.QuestionnaireApplicationSettings;
import lombok.Data;
import org.springframework.http.MediaType;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
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
  public String fill(@RequestHeader(value = "X-Forwarded-For", required = false) String forwardedFor,
                     @RequestHeader(value = "Host", required = false) String host,
                     @RequestHeader(value = "X-Real-IP", required = false) String realIp,
                     @RequestHeader(value = "X-Forwarded-Proto", required = false) String forwardedProto,
                     @PathVariable("questionnaireId") String questionnaireId,
                     CsrfToken cfrsToken,
                     Model model,
                     HttpServletRequest request) {
    FormConnectionOptions formConnectionOptions = new FormConnectionOptions();
    formConnectionOptions.setQuestionnaireId(questionnaireId);
    formConnectionOptions.setUrl(settings.getSocketUrl() + "/" + questionnaireId);
    formConnectionOptions.setReviewUrl(settings.getReviewUrl() + "/" + questionnaireId);
    formConnectionOptions.setCsrf(cfrsToken);
    formConnectionOptions.setRestUrl(settings.getRestUrl() + "/" + questionnaireId);
    formConnectionOptions.setRestUrlBase(settings.getRestUrl());
    formConnectionOptions.setConnectionMode(settings.getConnectionMode());
    formConnectionOptions.setBackendApiUrl(settings.getBackendApiUrl());
    model.addAttribute("formConnectionOptions", formConnectionOptions);
    final PageAttributes pageAttributes = pageSettingsProvider.findPageSettingsByQuestionnaireId("fill", questionnaireId);
    model.addAllAttributes(pageAttributes.getAttributes());
    index(model, request);
    return pageAttributes.getTemplate();
  }

  @Data
  public static class FormConnectionOptions {

    private String url;

    private String reviewUrl;

    private String questionnaireId;

    private List<String> transports;

    private CsrfToken csrf;

    private String restUrl;

    private String restUrlBase;

    private String connectionMode;

    private String backendApiUrl;


  }
}
