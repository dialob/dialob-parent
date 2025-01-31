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

import edu.umd.cs.findbugs.annotations.NonNull;
import io.dialob.boot.settings.AdminApplicationSettings;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Builder;
import org.springframework.http.MediaType;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("${admin.context-path:/}")
public class AdminController extends BaseController {

  private final AdminApplicationSettings settings;

  private final PageSettingsProvider pageSettingsProvider;

  public AdminController(AdminApplicationSettings settings, PageSettingsProvider pageSettingsProvider) {
    this.settings = settings;
    this.pageSettingsProvider = pageSettingsProvider;
  }

  @GetMapping(path = {"", "/index.html"}, produces = MediaType.TEXT_HTML_VALUE)
  public String index(CsrfToken cfrsToken, Model model, @RequestParam(name = "tenantId", required = false) String tenantId, HttpServletRequest request) {
    model.addAttribute("_csrf", cfrsToken);
    model.addAttribute("adminConfig", getAdminConfig(cfrsToken, tenantId));
    final PageAttributes pageAttributes = pageSettingsProvider.findPageSettings("admin");
    model.addAllAttributes(pageAttributes.getAttributes());
    index(model, request);
    return pageAttributes.getTemplate();
  }

  @GetMapping(path = "/config.json", produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseBody
  public AdminConfig config(CsrfToken cfrsToken, @RequestParam(name = "tenantId", required = false) String tenantId) {
    return getAdminConfig(cfrsToken, tenantId);
  }

  @NonNull
  public AdminConfig getAdminConfig(CsrfToken csrf, String tenantId) {
    var config = AdminConfig.builder()
      .csrf(csrf)
      .url(settings.apiUrl())
      .fillUrl(settings.fillingAppUrl())
      .reviewUrl(settings.reviewAppUrl())
      .documentation(settings.documentation())
      .composerUrl(settings.composerAppUrl())
      .versioning(settings.versioning());
    if (isValidTenantId(tenantId)) {
      config.tenantId(tenantId);
    }
    return config.build();
  }

  @Builder
  public record AdminConfig(String url,
                            String documentation,
                            String fillUrl,
                            String reviewUrl,
                            CsrfToken csrf,
                            String composerUrl,
                            String tenantId,
                            boolean versioning) {
  }
}
