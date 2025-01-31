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
package io.dialob.boot.settings;

import edu.umd.cs.findbugs.annotations.NonNull;
import io.dialob.boot.controller.PageAttributes;
import io.dialob.boot.controller.PageSettingsProvider;
import io.dialob.db.spi.exceptions.DocumentNotFoundException;
import io.dialob.questionnaire.service.api.QuestionnaireDatabase;
import io.dialob.security.tenant.CurrentTenant;
import io.dialob.security.tenant.NoTenantInScopeException;

import java.util.Map;
import java.util.Optional;

public class SettingsPageSettingsProvider implements PageSettingsProvider {

  private final QuestionnaireDatabase questionnaireDatabase;

  private final QuestionnaireApplicationSettings settings;

  private final SettingsPageAttributes defaultPageSettings;

  private final ReviewApplicationSettings reviewSettings;

  private final AdminApplicationSettings adminApplicationSettings;

  private final ComposerApplicationSettings composerApplicationSettings;

  private final SettingsPageAttributes defaultReviewPageSettings;

  private final SettingsPageAttributes defaultAdminPageSettings;

  private final SettingsPageAttributes defaultComposerPageSettings;

  private final CurrentTenant currentTenant;

  public SettingsPageSettingsProvider(CurrentTenant currentTenant, QuestionnaireDatabase questionnaireDatabase,
                                      QuestionnaireApplicationSettings settings,
                                      ReviewApplicationSettings reviewSettings,
                                      ComposerApplicationSettings composerApplicationSettings,
                                      Optional<AdminApplicationSettings> adminApplicationSettings) {
    this.currentTenant = currentTenant;
    this.questionnaireDatabase = questionnaireDatabase;
    this.settings = settings;
    this.defaultPageSettings = settings.getTenants().get("default");
    this.reviewSettings = reviewSettings;
    this.adminApplicationSettings = adminApplicationSettings.orElse(null);
    this.composerApplicationSettings = composerApplicationSettings;
    this.defaultReviewPageSettings = reviewSettings.tenants().get("default");
    this.defaultComposerPageSettings = composerApplicationSettings.tenants().get("default");
    this.defaultAdminPageSettings = this.adminApplicationSettings != null ? this.adminApplicationSettings.tenants().get("default") : null;
  }

  @NonNull
  @Override
  public PageAttributes findPageSettingsByQuestionnaireId(String page, String questionnaireId) {
    return findTenantFor(questionnaireId)
      .map(tenantId -> findPageSettingsByTenantId(page,tenantId))
      .orElseGet(() -> findDefaultSettings(page));
  }

  @NonNull
  @Override
  public PageAttributes findPageSettingsByTenantId(String page, @NonNull String tenantId) {
    SettingsPageAttributes settingsPageAttributes = findSettings(page).get(tenantId);
    if (settingsPageAttributes == null) {
      settingsPageAttributes = findDefaultSettings(page);
    }
    return settingsPageAttributes;
  }

  @NonNull
  @Override
  public PageAttributes findPageSettings(String page) {
    String tenantId;
    try {
      tenantId = currentTenant.getId();
    } catch (NoTenantInScopeException e) {
      tenantId = "default";
    }
    return findPageSettingsByTenantId(page, tenantId);
  }

  Map<String, SettingsPageAttributes> findSettings(String page) {
    return switch (page) {
      case "fill" -> settings.tenants();
      case "review" -> reviewSettings.tenants();
      case "admin" -> adminApplicationSettings.tenants();
      case "composer" -> composerApplicationSettings.tenants();
      default -> throw new IllegalStateException("unknown page " + page);
    };
  }

  SettingsPageAttributes findDefaultSettings(String page) {
    return switch (page) {
      case "fill" -> defaultPageSettings;
      case "review" -> defaultReviewPageSettings;
      case "admin" -> defaultAdminPageSettings;
      case "composer" -> defaultComposerPageSettings;
      default -> throw new IllegalStateException("unknown page " + page);
    };
  }

  private Optional<String> findTenantFor(String questionnaireId) {
    try {
      QuestionnaireDatabase.MetadataRow metadataRow = questionnaireDatabase.findMetadata(null, questionnaireId);
      return Optional.of(metadataRow.getValue().getTenantId());
    } catch (DocumentNotFoundException e) {
      return Optional.empty();
    }
  }
}
