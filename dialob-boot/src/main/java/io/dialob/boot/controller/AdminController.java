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

import edu.umd.cs.findbugs.annotations.NonNull;
import io.dialob.boot.settings.AdminApplicationSettings;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("${admin.context-path:/}")
public class AdminController extends BaseController {

  private final AdminApplicationSettings adminApplicationSettings;

  private final PageSettingsProvider pageSettingsProvider;

  public AdminController(AdminApplicationSettings adminApplicationSettings, PageSettingsProvider pageSettingsProvider) {
    this.adminApplicationSettings = adminApplicationSettings;
    this.pageSettingsProvider = pageSettingsProvider;
  }

  @GetMapping(path = {"", "/index.html"}, produces = MediaType.TEXT_HTML_VALUE)
  public String index(CsrfToken cfrsToken, Model model, HttpServletRequest request) {
    model.addAttribute("_csrf", cfrsToken);
    model.addAttribute("adminConfig", getAdminConfig(cfrsToken, request));
    final PageAttributes pageAttributes = pageSettingsProvider.findPageSettings("admin");
    model.addAllAttributes(pageAttributes.getAttributes());
    index(model, request);
    return pageAttributes.getTemplate();
  }

  @GetMapping(path = "/config.json", produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseBody
  public AdminConfig config(CsrfToken cfrsToken, Model model, HttpServletRequest request)  {
    return getAdminConfig(cfrsToken, request);
  }

  @NonNull
  public AdminConfig getAdminConfig(CsrfToken cfrsToken, HttpServletRequest request) {
    AdminConfig adminConfig = new AdminConfig();
    adminConfig.setCsrf(cfrsToken);
    adminConfig.setUrl(adminApplicationSettings.getApiUrl());
    adminConfig.setFillUrl(adminApplicationSettings.getFillingAppUrl());
    adminConfig.setReviewUrl(adminApplicationSettings.getReviewAppUrl());
    adminConfig.setDocumentation(adminApplicationSettings.getDocumentation());
    adminConfig.setComposerUrl(adminApplicationSettings.getComposerAppUrl());
    adminConfig.setVersioning(adminApplicationSettings.isVersioning());
    final String tenantId = request.getParameter("tenantId");
    if (!StringUtils.isBlank(tenantId)) {
      adminConfig.setTenantId(tenantId);
    }
    return adminConfig;
  }



  @Data
  public static class AdminConfig {

    private String url;

    private String documentation;

    private String fillUrl;

    private String reviewUrl;

    private CsrfToken csrf;

    private String composerUrl;

    private String tenantId;

    private boolean versioning;

  }
}
