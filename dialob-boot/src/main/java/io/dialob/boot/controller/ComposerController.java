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

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dialob.boot.settings.ComposerApplicationSettings;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

import static org.apache.commons.lang3.StringUtils.isBlank;

@Controller
@RequestMapping("${composer.context-path:/composer}")
@Slf4j
public class ComposerController extends BaseController {

  private ComposerApplicationSettings settings;

  private final PageSettingsProvider pageSettingsProvider;

  public ComposerController(ComposerApplicationSettings settings, PageSettingsProvider pageSettingsProvider) {
    this.settings = settings;
    this.pageSettingsProvider = pageSettingsProvider;
  }

  @GetMapping(value = {"/"}, produces = MediaType.TEXT_HTML_VALUE)
  public String composer(@RequestHeader(value = "X-Forwarded-For", required = false) String forwardedFor,
                         @RequestHeader(value = "Host", required = false) String host,
                         @RequestHeader(value = "X-Real-IP", required = false) String realIp,
                         @RequestHeader(value = "X-Forwarded-Proto", required = false) String forwardedProto,
                         CsrfToken cfrsToken,
                         Model model,
                         HttpServletRequest request) {
                           return composer(forwardedFor,host,realIp,forwardedProto,null,cfrsToken,model,request);

  }


  @GetMapping(value = {"/{id}"}, produces = MediaType.TEXT_HTML_VALUE)
  public String composer(@RequestHeader(value = "X-Forwarded-For", required = false) String forwardedFor,
                         @RequestHeader(value = "Host", required = false) String host,
                         @RequestHeader(value = "X-Real-IP", required = false) String realIp,
                         @RequestHeader(value = "X-Forwarded-Proto", required = false) String forwardedProto,
                         @PathVariable("id") String formId,
                         CsrfToken cfrsToken,
                         Model model,
                         HttpServletRequest request) {
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Host: {}", host);
      LOGGER.debug("X-Real-IP: {}", realIp);
      LOGGER.debug("X-Forwarded-For: {}", forwardedFor);
      LOGGER.debug("X-Forwarded-Proto: {}", forwardedProto);
    }
    if ("index.html".equals(formId)) {
      formId = null;
    }
    index(model,request);
    if (isBlank(forwardedProto)) {
      forwardedProto = request.getScheme();
    }

    AppConfig appConfig = getJavascriptAppConfig(host, forwardedProto, formId, cfrsToken, request);
    model.addAttribute("contextPath", settings.getUrl());
    model.addAttribute("subApplicationName", settings.getSubApplicationName());
    model.addAttribute("appConfig", appConfig);
    final PageAttributes pageAttributes = pageSettingsProvider.findPageSettings("composer");
    model.addAllAttributes(pageAttributes.getAttributes());
    index(model, request);
    return pageAttributes.getTemplate();
  }

  @GetMapping(path = "/{id}/config.json", produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseBody
  public AppConfig config(@RequestHeader(value = "Host", required = false) String host,
                          @RequestHeader(value = "X-Forwarded-Proto", required = false) String forwardedProto,
                          @PathVariable("id") String formId,
                          CsrfToken cfrsToken, HttpServletRequest request) {
    return getJavascriptAppConfig(host, forwardedProto, formId, cfrsToken, request);
  }

  protected AppConfig getJavascriptAppConfig(String host,
                                             String forwardedProto,
                                             String formId,
                                             CsrfToken cfrsToken,
                                             HttpServletRequest request) {
    String fillingAppUrl = settings.getFillingAppUrl();
    if (isBlank(fillingAppUrl)) {
      fillingAppUrl = forwardedProto + "://" + host;
    }
    AppConfig appConfig = new AppConfig();
    appConfig.setFormId(formId);
    appConfig.setBackendApiUrl(settings.getBackendApiUrl());
    appConfig.setDocumentationUrl(settings.getDocumentationUrl());
    appConfig.setFillingAppUrl(fillingAppUrl);
    appConfig.setAdminAppUrl(settings.getAdminAppUrl());
    if (cfrsToken != null) {
      appConfig.setCsrf(cfrsToken.getToken());
      appConfig.setCsrfHeader(cfrsToken.getHeaderName());
    }
    final String tenantId = request.getParameter("tenantId");
    if (!StringUtils.isBlank(tenantId)) {
      appConfig.setTenantId(tenantId);
    }
    return appConfig;
  }


  @Data
  public static class AppConfig {

    @JsonProperty("backend_api_url")
    private String backendApiUrl;

    @JsonProperty("documentation_url")
    private String documentationUrl;

    @JsonProperty("filling_app_url")
    private String fillingAppUrl;

    private String csrf;

    private String csrfHeader;

    private String formId;

    private String adminAppUrl;

    private String tenantId;

  }

}
