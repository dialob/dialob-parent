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

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

import io.dialob.boot.settings.LandingApplicationSettings;
import io.dialob.security.tenant.CurrentTenant;
import lombok.Data;

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

    LandingOptions landingOptions = new LandingOptions();
    landingOptions.setBackendApiUrl(settings.getApiUrl());
    landingOptions.setComposerUrl(settings.getComposerAppUrl());
    landingOptions.setFillingUrl(settings.getFillingAppUrl());
    landingOptions.setTenantId(currentTenant.getId());
    landingOptions.setAdminUrl(settings.getAdminAppUrl());
    landingOptions.setCsrf(cfrsToken);
    final String tenantId = request.getParameter("tenantId");
    if (!StringUtils.isBlank(tenantId)) {
      landingOptions.setTenantId(tenantId);
    }
    model.addAttribute("landingConnectionOptions", landingOptions);
    final PageAttributes pageAttributes = settings.getTenants().get("default");
    model.addAllAttributes(pageAttributes.getAttributes());
    index(model, request);
    return pageAttributes.getTemplate();
  }

  @Data
  public static class LandingOptions {

    private String backendApiUrl;

    private String composerUrl;

    private String fillingUrl;

    private CsrfToken csrf;

    private String adminUrl;

    private String tenantId;

  }
}
