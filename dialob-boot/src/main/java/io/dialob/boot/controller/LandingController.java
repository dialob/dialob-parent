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

import io.dialob.boot.settings.LandingApplicationSettings;
import io.dialob.security.tenant.CurrentTenant;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Builder;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("${landing.context-path:/landing}")
public class LandingController extends BaseController {

  private final LandingApplicationSettings settings;

  private final CurrentTenant currentTenant;

  public LandingController(CurrentTenant currentTenant, LandingApplicationSettings settings) {
    this.currentTenant = currentTenant;
    this.settings = settings;
  }

  @GetMapping(produces = MediaType.TEXT_HTML_VALUE)
  public String fill(@RequestHeader(value = "X-Forwarded-For", required = false) String forwardedFor,
                     @RequestHeader(value = "Host", required = false) String host,
                     @RequestHeader(value = "X-Real-IP", required = false) String realIp,
                     @RequestHeader(value = "X-Forwarded-Proto", required = false) String forwardedProto,
                     CsrfToken cfrsToken,
                     Model model,
                     HttpServletRequest request) {

    var optionsBuilder = LandingOptions.builder()
      .backendApiUrl(settings.getApiUrl())
      .composerUrl(settings.getComposerAppUrl())
      .fillingUrl(settings.getFillingAppUrl())
      .tenantId(currentTenant.getId())
      .adminUrl(settings.getAdminAppUrl())
      .csrf(cfrsToken);
    final String tenantId = request.getParameter("tenantId");
    if (!StringUtils.isBlank(tenantId)) {
      optionsBuilder.tenantId(tenantId);
    }
    model.addAttribute("landingConnectionOptions", optionsBuilder.build());
    final PageAttributes pageAttributes = settings.getTenants().get("default");
    model.addAllAttributes(pageAttributes.getAttributes());
    index(model, request);
    return pageAttributes.getTemplate();
  }

  @Data
  @Builder
  public static class LandingOptions {

    private final String backendApiUrl;

    private final String composerUrl;

    private final String fillingUrl;

    private final CsrfToken csrf;

    private final String adminUrl;

    private final String tenantId;

  }
}
