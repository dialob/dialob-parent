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
package io.dialob.integration.redis;

import io.dialob.api.questionnaire.Questionnaire;
import io.dialob.common.Constants;
import io.dialob.questionnaire.service.api.event.QuestionnaireActionsEvent;
import io.dialob.questionnaire.service.api.session.QuestionnaireSession;
import io.dialob.questionnaire.service.api.session.QuestionnaireSessionService;
import io.dialob.security.tenant.TenantContextHolderCurrentTenant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;

@Slf4j
public class UpdateQuestionnaireInCacheOnActionsEventHandler {

  private final Cache questionnaireCache;

  private final QuestionnaireSessionService questionnaireSessionService;

  public UpdateQuestionnaireInCacheOnActionsEventHandler(CacheManager cacheManager, QuestionnaireSessionService questionnaireSessionService) {
    this.questionnaireCache = cacheManager.getCache(Constants.QUESTIONNAIRE_CACHE_NAME);
    this.questionnaireSessionService = questionnaireSessionService;
  }

  @EventListener
  @Async
  public void onQuestionnaireActionsEvent(QuestionnaireActionsEvent event) {
    TenantContextHolderCurrentTenant.runInTenantContext(event.getTenant(), () -> {
      String questionnaireId = event.getQuestionnaireId();
      QuestionnaireSession session = this.questionnaireSessionService.findOne(questionnaireId, false);
      if (session != null) {
        Questionnaire questionnaire = session.getQuestionnaire();
        questionnaireCache.put(questionnaireId, questionnaire);
        LOGGER.debug("Updated questionnaire {} in cache", questionnaireId);
      }
    });
  }

}
