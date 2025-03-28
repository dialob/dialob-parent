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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.dialob.api.form.Form;
import io.dialob.api.questionnaire.Questionnaire;
import io.dialob.boot.settings.ReviewApplicationSettings;
import io.dialob.common.Constants;
import io.dialob.form.service.api.FormDatabase;
import io.dialob.questionnaire.service.api.QuestionnaireDatabase;
import io.dialob.security.tenant.CurrentTenant;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("${review.context-path:/review}")
public class ReviewController extends BaseController {

  private final CurrentTenant currentTenant;

  private final ReviewApplicationSettings settings;

  private final QuestionnaireDatabase questionnaireRepository;

  private final FormDatabase formDatabase;

  private final ObjectMapper objectMapper;

  private final PageSettingsProvider pageSettingsProvider;

  public ReviewController(ReviewApplicationSettings settings, QuestionnaireDatabase questionnaireRepository, FormDatabase formDatabase, ObjectMapper objectMapper, CurrentTenant currentTenant, PageSettingsProvider pageSettingsProvider) {
    this.settings = settings;
    this.questionnaireRepository = questionnaireRepository;
    this.formDatabase = formDatabase;
    this.objectMapper = objectMapper;
    this.currentTenant = currentTenant;
    this.pageSettingsProvider = pageSettingsProvider;
  }

  public record GetReview(
    @PathVariable("questionnaireId") @Pattern(regexp = Constants.QUESTIONNAIRE_ID_PATTERN) String questionnaireId
  ) { }

  // CodeQL gives false positive here and from other places where validation api is used.
  // see. https://github.com/github/codeql/issues/8705
  @GetMapping(value = {"/{questionnaireId}"}, produces = MediaType.TEXT_HTML_VALUE)
  public String review(@Valid GetReview getReview,
                       CsrfToken cfrsToken,
                       Model model,
                       HttpServletRequest request) throws JsonProcessingException {
    var reviewOptionsBuilder = ReviewOptions.builder()
      .apiUrl(settings.getApiUrl())
      .questionnaireId(getReview.questionnaireId())
      .csrf(cfrsToken);
    final String tenantId = request.getParameter("tenantId");
    if (!StringUtils.isBlank(tenantId)) {
      reviewOptionsBuilder.tenantId(tenantId);
    }

    if (StringUtils.isBlank(settings.getApiUrl())) {
      Questionnaire questionnaire = questionnaireRepository.findOne(currentTenant.getId(), getReview.questionnaireId());
      Form form;
      var metadata = questionnaire.getMetadata();
      String formRev = "LATEST".equals(metadata.getFormRev()) ? null : metadata.getFormRev();
      form = formDatabase.findOne(currentTenant.getId(), metadata.getFormId(), formRev);
      reviewOptionsBuilder.form(objectMapper.writeValueAsString(form));
      reviewOptionsBuilder.sessionData(objectMapper.writeValueAsString(questionnaire));
    }
    model.addAttribute("reviewOptions", reviewOptionsBuilder.build());
    final PageAttributes pageAttributes = pageSettingsProvider.findPageSettingsByQuestionnaireId("review", getReview.questionnaireId());
    model.addAllAttributes(pageAttributes.getAttributes());
    index(model, request);
    return pageAttributes.getTemplate();
  }


  @Builder
  public record ReviewOptions(
    String apiUrl,
    String questionnaireId,
    CsrfToken csrf,
    String form,
    String sessionData,
    String tenantId
  ) {
  }
}

