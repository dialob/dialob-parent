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
package io.dialob.session.rest;

import io.dialob.questionnaire.service.api.session.QuestionnaireSession;
import io.dialob.questionnaire.service.api.session.QuestionnaireSessionService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

@Slf4j
public class OnlyOwnerCanAccessSessionPermissionEvaluator implements SessionPermissionEvaluator {

  private final QuestionnaireSessionService questionnaireSessionService;

  public OnlyOwnerCanAccessSessionPermissionEvaluator(QuestionnaireSessionService questionnaireSessionService) {
    this.questionnaireSessionService = questionnaireSessionService;
  }

  @Override
  public boolean hasAccess(String sessionId, String userId) {
    if (StringUtils.isBlank(userId) || StringUtils.isBlank(sessionId)) {
      LOGGER.trace("{} access to {} denied", userId, sessionId);
      return false;
    }
    final QuestionnaireSession questionnaireSession = questionnaireSessionService.findOne(sessionId);
    boolean access = userId.equals(questionnaireSession.getOwner());
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("{} access to {} {}. Onwer id {}", userId, sessionId, access ? "granted" : "denied", questionnaireSession.getOwner());
    }
    return access;
  }

}
