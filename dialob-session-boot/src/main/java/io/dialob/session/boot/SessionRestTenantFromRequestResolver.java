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
package io.dialob.session.boot;

import edu.umd.cs.findbugs.annotations.NonNull;
import io.dialob.db.spi.exceptions.DocumentNotFoundException;
import io.dialob.questionnaire.service.api.session.QuestionnaireSession;
import io.dialob.questionnaire.service.api.session.QuestionnaireSessionService;
import io.dialob.security.tenant.ImmutableTenant;
import io.dialob.security.tenant.Tenant;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;

import java.util.Optional;

public class SessionRestTenantFromRequestResolver implements TenantFromRequestResolver {

  private final QuestionnaireSessionService questionnaireSessionService;

  public SessionRestTenantFromRequestResolver(@NonNull QuestionnaireSessionService questionnaireSessionService) {
    this.questionnaireSessionService = questionnaireSessionService;
  }

  @Override
  public Optional<Tenant> resolveTenantFromRequest(HttpServletRequest request) {
    String sessionId = getSessionId(request);
    if (StringUtils.isNotBlank(sessionId)) {
      try {
        return getQuestionnaireSession(sessionId)
          .map(QuestionnaireSession::getTenantId).map(tId -> ImmutableTenant.of(tId, Optional.empty()));
      } catch (DocumentNotFoundException dnfe) {
        /* fall throught */;
      }
    }
    return Optional.empty();
  }

  protected String getSessionId(HttpServletRequest request) {
    var sessionId = request.getParameter("sessionId");
    if (StringUtils.isBlank(sessionId)) {
      sessionId = StringUtils.substringAfterLast(request.getPathInfo(), "/");
    }
    if (!StringUtils.containsOnly(sessionId, "0123456789abcdefABCDEF")) {
      return null;
    }
    return sessionId;
  }

  @NonNull
  protected Optional<QuestionnaireSession> getQuestionnaireSession(String sessionId) {
    return Optional.ofNullable(questionnaireSessionService.findOne(sessionId));
  }

}
