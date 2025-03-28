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

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dialob.boot.settings.ComposerApplicationSettings;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import static org.apache.commons.lang3.StringUtils.isBlank;

@Controller
@RequestMapping("${composer.context-path:/composer}")
@Slf4j
public class ComposerController extends BaseController {

  @Value("${info.build.version}")
  private String version;

  private final ComposerApplicationSettings settings;

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
                         CsrfToken csrfToken,
                         @RequestParam(name = "tenantId", required = false) String tenantId,
                         Model model,
                         HttpServletRequest request) {
    return composer(forwardedFor, host, realIp, forwardedProto, null, csrfToken, tenantId, model, request);
  }


  @GetMapping(value = {"/{id}"}, produces = MediaType.TEXT_HTML_VALUE)
  public String composer(@RequestHeader(value = "X-Forwarded-For", required = false) String forwardedFor,
                         @RequestHeader(value = "Host", required = false) String host,
                         @RequestHeader(value = "X-Real-IP", required = false) String realIp,
                         @RequestHeader(value = "X-Forwarded-Proto", required = false) String forwardedProto,
                         @PathVariable("id") String formId,
                         CsrfToken cfrsToken,
                         @RequestParam(name = "tenantId", required = false) String tenantId,
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
    index(model, request);
    if (isBlank(forwardedProto)) {
      forwardedProto = request.getScheme();
    }

    ComposerConfig appConfig = getJavascriptAppConfig(host, forwardedProto, formId, cfrsToken, tenantId);
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
  public ComposerConfig config(@RequestHeader(value = "Host", required = false) String host,
                               @RequestHeader(value = "X-Forwarded-Proto", required = false) String forwardedProto,
                               @PathVariable("id") String formId,
                               CsrfToken cfrsToken,
                               @RequestParam(name = "tenantId", required = false) String tenantId) {
    return getJavascriptAppConfig(host, forwardedProto, formId, cfrsToken, tenantId);
  }

  protected ComposerConfig getJavascriptAppConfig(String host,
                                                  String forwardedProto,
                                                  String formId,
                                                  CsrfToken cfrsToken,
                                                  @RequestParam(name = "tenantId", required = false) String tenantId) {
    String fillingAppUrl = settings.getFillingAppUrl();
    if (isBlank(fillingAppUrl)) {
      fillingAppUrl = forwardedProto + "://" + host;
    }
    return ComposerConfig.builder()
      .formId(formId)
      .backendApiUrl(settings.getBackendApiUrl())
      .documentationUrl(settings.getDocumentationUrl())
      .fillingAppUrl(fillingAppUrl)
      .adminAppUrl(settings.getAdminAppUrl())
      .csrf(cfrsToken != null ? cfrsToken.getToken() : null)
      .csrfHeader(cfrsToken != null ? cfrsToken.getHeaderName() : null)
      .tenantId(isValidTenantId(tenantId) ? tenantId : null)
      .version(version)
      .build();
  }

  @Builder
  public record ComposerConfig(
    @JsonProperty("backend_api_url") String backendApiUrl,
    @JsonProperty("documentation_url") String documentationUrl,
    @JsonProperty("filling_app_url") String fillingAppUrl,
    String csrf,
    String csrfHeader,
    String formId,
    String adminAppUrl,
    String tenantId,
    String version
  ) {
  }

}
